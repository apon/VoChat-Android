package me.apon.vochat.service


import android.util.Log
import com.orhanobut.hawk.Hawk
import me.apon.lemon.Lemon
import me.apon.lemon.core.ConnectHandler
import me.apon.lemon.core.SocketClient
import me.apon.lemon.protocols.FrameProtocols
import me.apon.vochat.model.*
import me.apon.vochat.service.callback.CallbackQueue
import me.apon.vochat.service.callback.MessageReceiver
import me.apon.vochat.service.callback.MessageListener
import java.util.concurrent.Executors

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/8.
 */
class NetworkManager private constructor() :BaseManager() {

    companion object {
        val instance: NetworkManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkManager()
        }
    }

    private var isConnect = false
    private lateinit var   lemon: Lemon
    private val callbackQueue = CallbackQueue.INSTANCE
    private val threadPool = Executors.newFixedThreadPool(2)

    init {
        initLemon()
    }


    override fun start() {
        lemon.connect()
        callbackQueue.onStart()
    }

    override fun reset() {
        if (!isConnect){
            lemon.connect()
        }
    }
    override fun release() {
        lemon.disconnect()
        callbackQueue.onDestory()
    }
    private fun initLemon(){
        val client = SocketClient.Builder()
            .ip("120.78.175.94") //设置ip、端口
            .port(8282)
            .setKeepAlive(true) //设置socket选项
            .build()
        val pingData = "{\"id\":\"-1\",\"cmd\":\"-1\",\"msg\":\"ping!!\"}"
        lemon = Lemon.Builder()
            .client(client)
            .protocols(FrameProtocols.create()) //设置协议，可自定义
            .pingInterval(10) //设置心跳间隔（秒）大于0打开心跳功能
            .pingData(pingData.toByteArray()) //设置心跳包内容
//            .debug(true) //开启调试模式
            .build()

        lemon.onConnect(object : ConnectHandler {

            override fun connectFail() {
                Log.d("NetworkManager","-----connectFail----")
                isConnect = false
            }

            override fun connectSuccess() {
                Log.d("NetworkManager","-----connectSuccess----")
                isConnect = true
                bindUser()
            }

            override fun disconnect() {
                Log.d("NetworkManager","-----disconnect----")
                isConnect = false
            }
        })


        lemon.onMessage {
            val msg = String(it)
            Log.d("NetworkManager", "receive： $msg")
            handleMessage(msg)
        }

    }

    /**
     * 绑定用户
     */
    private fun bindUser(){
        val loginUser = Hawk.get<LoginUser>("loginUser")
        Log.d("NetworkManager", "bindUser： $loginUser")
        if(loginUser!=null){
            val req = BindReq(loginUser.id)
            sendBroadcast(req)
        }
    }

    private fun handleMessage(msg:String){

        try {
            val baseAdapter = Json.M.moshi.adapter<BaseResp>(BaseResp::class.java)
            val baseRes = baseAdapter.fromJson(msg)

            val listener = callbackQueue.popCallBack(baseRes?.id!!)
            if (listener==null){//广播
                emit(baseRes.cmd,msg)
            }else{//回调监听
                listener.onReceive(msg)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun emit(cmd:Long,msg: String){
        val list = callbackQueue.getMessageReceiver(cmd)
        if (list.isNullOrEmpty()){
//            Log.d("NetworkManager", "emit receive：$msg  $cmd")

        }else{
            for (receiver in list){
                if (receiver.runOnMain){
                    receiver.onReceive(msg)
                }else{
                    runOnThreadPools(receiver,msg)
                }
            }
        }
    }

    private fun runOnThreadPools(receiver: MessageReceiver,msg: String){
        val task = Runnable {
            try {
                receiver.onReceive(msg)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        threadPool.submit(task)
    }


    /**
     * 向服务器发送请求并处理返回的数据
     */
    fun<T : BaseReq> sendRequest(req:T,listener:MessageListener){
        callbackQueue.pushCallBack(req.id,listener)

        val adapter = Json.M.moshi.adapter<T>(req::class.java)
        val msg = adapter.toJson(req)
        if (isConnect){
            lemon.send(msg)
            Log.d("NetworkManager", "send request： $msg")
        }else{
            val listener2 = callbackQueue.popCallBack(req.id)
            listener2?.onTimeout()
            lemon.connect()
        }
    }


    /**
     * 发送Broadcast，不需要服务器回响应
     */
    fun<T : BaseReq> sendBroadcast(req:T){
        val adapter = Json.M.moshi.adapter<T>(req::class.java)
        val msg = adapter.toJson(req)
        if (isConnect){
            lemon.send(msg)
            Log.d("NetworkManager", "send broadcast： $msg")
        }else{

        }
    }

    /**
     * 用来对服务端的推送作出响应
     */
    fun addReceiver(receiver:MessageReceiver){
        callbackQueue.addMessageReceiver(receiver)
    }

    fun deleteReceiver(receiver:MessageReceiver){
        callbackQueue.deleteMessageReceiver(receiver)
    }

}