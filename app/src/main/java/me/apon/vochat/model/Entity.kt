package me.apon.vochat.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/9.
 */
/////////////cmd 常量//////////////
const val CMD_LOGIN: Long = 1000
const val CMD_REGISTER: Long = 1001
const val CMD_BIND: Long = 1002
const val CMD_RESET_PASSWORD: Long = 1003
const val CMD_RESET_NAME: Long = 1004
const val CMD_SEARCH_USER: Long = 1005
const val CMD_ADD_CONTACT: Long = 1006
const val CMD_GET_CONTACT: Long = 1007
const val CMD_UNBIND:Long = 1008

const val CMD_SEND_MSG: Long = 2000 //发送消息
const val CMD_REC_MSG: Long = 2001 //接收消息

const val CMD_REQ_VOICE:Long = 2002 //请求语音聊天
const val CMD_VOICE_MSG:Long = 2003 //收发语音

const val REQ_VOICE_TYPE_ASK = 100 //请求语音聊天
const val REQ_VOICE_TYPE_CANCEL = 200 //拒绝、取消语音聊天
const val REQ_VOICE_TYPE_ACCEPT = 300 //接受语音聊天


const val PEER_TYPE_C2C = 100 //单聊
const val PEER_TYPE_GROUP = 200 //群聊
/////////////////基类//////////////////////

open class BaseReq(val cmd: Long) {
    open val id: Long = System.currentTimeMillis()
}

open class BaseResp {
    var id: Long = 0
    var cmd: Long = 0
    var code: Int = 0
    var msg: String = ""
}

//data class NormalResp(
//    val code: Int,
//    val msg:String
//):BaseResp()

///////////////请求、返回实体//////////////////

data class LoginUser(val id: String, var name: String, val phone: String, val avatar: String)

@Entity(tableName = "LocalUser")
@Parcelize
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val avatar: String
) : Parcelable

//-------登录------------------
//登录请求 cmd = 1000
data class LoginReq(
    val phone: String,
    val password: String
) : BaseReq(CMD_LOGIN)

//登录返回 cmd ==1000
data class LoginRes(
//    val code: Int = -1,
//    val msg:String,
    val data: LoginUser
) : BaseResp()

//-------注册------------------

//注册账号 cmd = 1001
data class RegisterReq(
    val phone: String,
    val password: String
) : BaseReq(CMD_REGISTER)

//注册账号返回 cmd = 1001
data class RegisterRes(
//    val code: Int = -1,
//    val msg:String,
    val data: LoginUser
) : BaseResp()

//-------绑定------------------

//用户绑定 cmd = 1002
data class BindReq(
    val userid: String
) : BaseReq(CMD_BIND)
//用户绑定返回 cmd = 1002
//BaseResp

//-------解绑------------------

//用户解绑 cmd = 1008
class UnBindReq : BaseReq(CMD_UNBIND)
//用户解绑返回 cmd = 1008
//BaseResp

//-------重置密码------------------

//重置密码 cmd = 1003
data class ResetPassReq(
    val password: String
) : BaseReq(CMD_RESET_PASSWORD)
//重置密码返回 cmd = 1003
//BaseResp

//-------重置名称------------------

//重置名称 cmd = 1004
data class ResetNameReq(
    val name: String
) : BaseReq(CMD_RESET_NAME)
//重置名称返回 cmd = 1004
//BaseResp

//-------搜索用户------------------


//搜索用户 cmd = 1005
data class SearchUserReq(
    val phone: String
) : BaseReq(CMD_SEARCH_USER)

//搜索用户返回 cmd = 1005
data class SearchUserResp(
//    val code: Int = -1,
//    val msg:String,
    val data: List<User> = listOf<User>()
) : BaseResp()

//-------添加联系人------------------

//添加联系人 cmd = 1006
data class AddContactReq(
    val friendid: String
) : BaseReq(CMD_ADD_CONTACT)
//添加联系人返回 cmd = 1006
//BaseResp

//-------获取联系人------------------


//获取联系人 cmd = 1007
class ContactsReq : BaseReq(CMD_GET_CONTACT)

//搜索用户返回 cmd = 1007
data class ContactsResp(
//    val code: Int = -1,
//    val msg:String,
    val data: List<User> = listOf<User>()
) : BaseResp()

//发送消息 cmd 2000
data class SendMessageReq(
    val fromId: String,
    val fromName: String,
    val toId: String,
    val toName: String,
    val created: Long,
    val peerType: Int,
    val content: String
) : BaseReq(CMD_SEND_MSG)
//发送消息返回
//BaseResp cmd 2000

//接收消息 cmd = 2001
data class RecMessageResp(
    val fromId: String,
    val fromName: String,
    val toId: String,
    val toName: String,
    val created: Long,
    val peerType: Int,
    val content: String
) : BaseResp()

//请求语言聊天 cmd = 2002
data class ReqVoiceReq(
    val fromId: String,
    val fromName: String,
    val toId: String,
    val toName: String,
    val reqType:Int
):BaseReq(CMD_REQ_VOICE)
//请求语言聊天返回 cmd = 2002
data class ReqVoiceResp(
    val fromId: String,
    val fromName: String,
    val toId: String,
    val toName: String,
    val reqType:Int
):BaseResp()

//发送语音 cmd = 2003
data class VoiceMsgReq(
    val fromId: String,
    val toId: String,
    val content: String
):BaseReq(CMD_VOICE_MSG)
//接收语音 cmd = 2003
data class VoiceMsgResp(
    val fromId: String,
    val toId: String,
    val content: String
) : BaseResp()
