package com.quickhandslogistics.resources

import androidx.test.espresso.IdlingResource
import okhttp3.Dispatcher

class FetchingIdlingResource(private val okHttpClientDispatcher: Dispatcher?) : IdlingResource {
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    init {
        okHttpClientDispatcher?.setIdleCallback {
            resourceCallback?.let { callback ->
                callback.onTransitionToIdle()
            }
        }
    }

    override fun getName(): String {
        return FetchingIdlingResource::class.java.simpleName
    }

    override fun isIdleNow(): Boolean {
        return okHttpClientDispatcher?.runningCallsCount() == 0
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
    }
}