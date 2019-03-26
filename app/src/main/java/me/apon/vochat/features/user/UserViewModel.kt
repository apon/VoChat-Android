package me.apon.vochat.features.user

import android.util.Log
import com.airbnb.mvrx.*
import com.orhanobut.hawk.Hawk
import me.apon.vochat.app.BaseActivity
import me.apon.vochat.db.AppRoomDatabase
import me.apon.vochat.model.*
import me.apon.vochat.service.callback.MessageListener

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/16.
 */
data class UserState(
    val login: Async<String> = Uninitialized,
    val register: Async<String> = Uninitialized,
    val loginUser: LoginUser? = null,
    val contacts: Async<List<User>> = Uninitialized,
    val searchUser: Async<List<User>> = Uninitialized,
    val addContact: Async<String> = Uninitialized,
    val userInfo: Async<User> = Uninitialized,
    val updateName: Async<String> = Uninitialized
) : MvRxState

class UserViewModel(
    initState: UserState,
    private val roomDatabase: AppRoomDatabase,
    private val baseActivity: BaseActivity?
) : BaseMvRxViewModel<UserState>(initState, true) {

    companion object : MvRxViewModelFactory<UserViewModel, UserState> {

        override fun create(viewModelContext: ViewModelContext, state: UserState): UserViewModel {
            val db = AppRoomDatabase.getDatabase(viewModelContext.activity.applicationContext)
            val activity = viewModelContext.activity as BaseActivity
            return UserViewModel(state, db, activity)
        }
    }


    fun login(phone: String, pass: String) {
        setState { copy(login = Loading()) }
        val req = LoginReq(phone, pass)
        baseActivity?.chatNetService?.sendRequest(req, object : MessageListener() {
            override fun onReceive(msg: String) {
                val loginRes = Json.M.moshi.adapter<LoginRes>(LoginRes::class.java).fromJson(msg)
                if (loginRes?.code == 200) {
                    val loginUser: LoginUser? = loginRes.data
                    Hawk.put("loginUser", loginUser)
                    setState { copy(login = Success("登录成功！")) }
                } else {
                    setState { copy(login = Fail(Throwable(loginRes?.msg))) }
                }
            }

            override fun onTimeout() {
                setState { copy(login = Fail(Throwable("请求超时"))) }
            }
        })
    }

    fun register(phone: String, pass: String) {
        setState { copy(register = Loading()) }
        val req = RegisterReq(phone, pass)
        baseActivity?.chatNetService?.sendRequest(req, object : MessageListener() {
            override fun onReceive(msg: String) {
                val resp = Json.M.moshi.adapter<RegisterRes>(RegisterRes::class.java).fromJson(msg)
                if (resp?.code == 200) {
                    val loginUser: LoginUser? = resp.data
                    Hawk.put("loginUser", loginUser)
                    setState { copy(register = Success("注册成功！")) }
                } else {
                    setState { copy(register = Fail(Throwable(resp?.msg))) }
                }
            }

            override fun onTimeout() {
                setState { copy(register = Fail(Throwable("请求超时"))) }
            }
        })
    }

    fun getUser() {
        val loginUser = Hawk.get<LoginUser>("loginUser")
        setState {
            copy(loginUser = loginUser)
        }
    }

    fun getUserById(id: String) {
        val userDao = roomDatabase.localUserDao()
        val user = userDao.getUserById(id)
        setState { copy(userInfo = Success(user)) }
    }


    fun getContacts() {
        setState { copy(contacts = Loading()) }
        val userDao = roomDatabase.localUserDao()
//        var userList = userDao.getUsers()
//        setState { copy(contacts = Success(userList)) }
        val req = ContactsReq()
        baseActivity?.chatNetService?.sendRequest(req, object : MessageListener() {
            override fun onReceive(msg: String) {
                val resp = Json.M.moshi.adapter<ContactsResp>(ContactsResp::class.java).fromJson(msg)
                if (resp?.code == 200) {
                    val userList = resp.data
                    userDao.addUsers(userList)
                    setState { copy(contacts = Success(userList)) }
                } else {
                    setState { copy(contacts = Fail(Throwable(resp?.msg))) }
                }
            }

            override fun onTimeout() {
                setState { copy(contacts = Fail(Throwable("超时"))) }
            }
        })

    }

    fun searchUser(phone: String) {
        setState { copy(searchUser = Loading()) }
        val req = SearchUserReq(phone)
        baseActivity?.chatNetService?.sendRequest(req, object : MessageListener() {
            override fun onReceive(msg: String) {
                val resp = Json.M.moshi.adapter<SearchUserResp>(SearchUserResp::class.java).fromJson(msg)
                if (resp?.code == 200) {
                    val contacts = resp.data
                    setState { copy(searchUser = Success(contacts)) }
                } else {
                    setState { copy(searchUser = Fail(Throwable(resp?.msg))) }
                }
            }

            override fun onTimeout() {
                setState { copy(searchUser = Fail(Throwable("超时"))) }
            }
        })
    }

    fun addContact(friendId: String) {
        setState { copy(addContact = Loading()) }
        val req = AddContactReq(friendId)
        baseActivity?.chatNetService?.sendRequest(req, object : MessageListener() {
            override fun onReceive(msg: String) {
                val resp = Json.M.moshi.adapter<BaseResp>(BaseResp::class.java).fromJson(msg)
                if (resp?.code == 200) {
                    setState { copy(addContact = Success(resp.msg)) }
                } else {
                    setState { copy(addContact = Fail(Throwable(resp?.msg))) }
                }
            }

            override fun onTimeout() {
                setState { copy(addContact = Fail(Throwable("超时"))) }
            }
        })
    }

    fun updateName(name: String) {
        setState { copy(updateName = Loading()) }
        val req = ResetNameReq(name)
        baseActivity?.chatNetService?.sendRequest(req, object : MessageListener() {
            override fun onReceive(msg: String) {
                val resp = Json.M.moshi.adapter<BaseResp>(BaseResp::class.java).fromJson(msg)
                if (resp?.code == 200) {
                    setState { copy(updateName = Success(resp.msg)) }
                    val loginUser = Hawk.get<LoginUser>("loginUser")
                    loginUser.name = name
                    Hawk.put("loginUser", loginUser)
                    setState { copy(loginUser = loginUser) }
                } else {
                    setState { copy(updateName = Fail(Throwable(resp?.msg))) }
                }
            }

            override fun onTimeout() {
                setState { copy(updateName = Fail(Throwable("超时"))) }
            }
        })
    }

    fun unBind(){
        val req = UnBindReq()
        baseActivity?.chatNetService?.sendBroadcast(req)
    }
}