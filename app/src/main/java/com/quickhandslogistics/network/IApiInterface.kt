package com.quickhandslogistics.network

import com.quickhandslogistics.modified.data.login.LoginRequest
import com.quickhandslogistics.modified.data.login.LoginResponse
import com.quickhandslogistics.model.lumper.AllLumpersResponse
import retrofit2.Call
import retrofit2.http.*

interface IApiInterface {
    @POST("employees/lead/login")
    fun doLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("employees/lead/lumpers")
    fun getAllLumpersData(@Header("Authorization") auth: String): Call<AllLumpersResponse>

}