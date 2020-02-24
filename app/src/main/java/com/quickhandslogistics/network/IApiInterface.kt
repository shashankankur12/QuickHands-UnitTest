package com.quickhandslogistics.network

import com.quickhandslogistics.model.login.LoginRequest
import com.quickhandslogistics.model.login.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IApiInterface {

    @POST("employees/lead/login")
    fun doLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>
}