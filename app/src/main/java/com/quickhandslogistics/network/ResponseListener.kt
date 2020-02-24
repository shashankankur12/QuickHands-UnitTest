package com.quickhandslogistics.network

interface ResponseListener<T> {
    fun onSuccess(response: T)
    fun onError(error: Any)
}