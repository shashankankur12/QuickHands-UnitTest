package com.quickhandslogistics.network

import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordRequest
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordResponse
import com.quickhandslogistics.modified.data.login.LoginRequest
import com.quickhandslogistics.modified.data.login.LoginResponse
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.schedule.ScheduleAPIResponse
import retrofit2.Call
import retrofit2.http.*

interface IApiInterface {
    @POST("employees/lead/login")
    fun doLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("employees/lead/lumpers")
    fun getAllLumpersData(@Header("Authorization") auth: String): Call<AllLumpersResponse>

    @POST("emails/forgot-password/lead")
    fun doResetPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    @GET("schedule/lookup/date")
    fun getSchedulesList(
        @Header("Authorization") auth: String,
        @Query("date") date: String,
        @Query("buildingId") buildingId: String,
        @Query("scheduled") scheduled: Boolean
    ): Call<ScheduleAPIResponse>
}