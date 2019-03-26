package me.apon.vochat.service

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/8.
 */
abstract class BaseManager {
    abstract fun start()
    abstract fun reset()
    abstract fun release()
}