package me.apon.vochat.app

import android.os.Bundle
import android.util.Log
import com.airbnb.mvrx.BaseMvRxActivity
import me.apon.vochat.service.MainService
import me.apon.vochat.service.MainServiceConnector
import me.apon.vochat.service.NetworkManager

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/16.
 */
open class BaseActivity : BaseMvRxActivity() {

    var chatNetService: NetworkManager? = null

    var mainService: MainService? = null

    private val connector = object : MainServiceConnector() {

        override fun onServiceConnected(service: MainService) {
            mainService = service
            chatNetService = service.getNetwork()
            Log.d("BaseActivity:", "----onServiceConnected----")
        }

        override fun onServiceDisconnected() {
            Log.d("BaseActivity:", "----onServiceDisconnected----")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connector.connect(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        connector.disConnect(this)
    }
}