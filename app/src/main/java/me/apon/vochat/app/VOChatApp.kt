package me.apon.vochat.app

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import com.orhanobut.hawk.Hawk
import com.squareup.leakcanary.LeakCanary
import me.apon.vochat.service.MainService

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/8.
 */
class VOChatApp : MultiDexApplication() {
    companion object {
        lateinit var context: Context
    }


    override fun onCreate() {
        super.onCreate()
        Log.d("VOChatApp", "-----onCreate----")
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
        Hawk.init(this).build()
        MainService.start(this)
        Stetho.initializeWithDefaults(this)
        context = this.applicationContext
    }

}