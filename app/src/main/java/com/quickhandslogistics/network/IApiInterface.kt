package com.quickhandslogistics.network

import com.quickhandslogistics.model.login.LoginRequest
import com.quickhandslogistics.model.login.LoginResponse
import com.quickhandslogistics.model.lumper.AllLumpersResponse
import retrofit2.Call
import retrofit2.http.*

interface IApiInterface {
    @POST("employees/login")
    fun doLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("employees/lead/lumpers")
    fun getAllLumpersData(@Header("Authorization") auth: String): Call<AllLumpersResponse>

}