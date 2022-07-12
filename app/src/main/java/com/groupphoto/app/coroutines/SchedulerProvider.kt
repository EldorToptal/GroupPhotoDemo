package com.groupphoto.app.coroutines

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Rx Scheduler Provider
 */
interface SchedulerProvider {
    fun io(): Scheduler
    fun ui(): Scheduler
    fun computation(): Scheduler
}

class AppSchedulerProvider :
    SchedulerProvider {
    override fun io(): Scheduler = Schedulers.io()
    override fun ui(): Scheduler = AndroidSchedulers.mainThread()
    override fun computation(): Scheduler = Schedulers.computation()
}