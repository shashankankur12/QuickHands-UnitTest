package com.quickhandslogistics.network

import com.quickhandslogistics.model.login.LoginRequest
import com.quickhandslogistics.model.login.LoginResponse
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IApiInterface {
    @POST("employees/login")
    fun doLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>
}