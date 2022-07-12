package com.groupphoto.app.coroutines

import io.reactivex.Completable
import io.reactivex.Single

/**
 * Use
 * @param schedulerProvider configuration for Observable
 */
fun Completable.with(schedulerProvider: SchedulerProvider): Completable =
    observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())

/**
 * Use
 * @param schedulerProvider configuration for Single
 */
fun <T> Single<T>.with(schedulerProvider: SchedulerProvider): Single<T> =
    observeOn(schedulerProvider.ui()).subscribeOn(schedulerProvider.io())