package me.apon.vochat.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/8.
 */
abstract class MainServiceConnector {
    abstract fun onServiceConnected(service: MainService)
    abstract fun onServiceDisconnected()


    private var serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

            onServiceDisconnected()
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            var binder = service as MainService.CallServiceBinder
            var mainService = binder.getService()
            onServiceConnected(mainService)
        }

    }

    fun connect(context: Context) {
        var intent = Intent(context, MainService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun disConnect(context: Context) {
        context.unbindService(serviceConnection)
    }
}