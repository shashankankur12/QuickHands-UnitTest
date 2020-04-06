package com.quickhandslogistics.network

import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordRequest
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordResponse
import com.quickhandslogistics.modified.data.login.LoginRequest
import com.quickhandslogistics.modified.data.login.LoginResponse
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.schedule.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface IApiInterface {
    @POST("employees/lead/login")
    fun doLogin(@Body request: LoginRequest): Call<LoginResponse>

    @GET("employees/lead/lumpers")
    fun getAllLumpersData(@Header("Authorization") auth: String): Call<AllLumpersResponse>

    @GET("employees/me")
    fun getLeadProfile(@Header("Authorization") auth: String): Call<LeadProfileAPIResponse>

    @POST("emails/forgot-password/lead")
    fun doResetPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    // Schedule /////////////////////////////////////////////////
    @GET("schedule/lookup/date")
    fun getSchedulesList(
        @Header("Authorization") auth: String,
        @Query("date") date: String,
        @Query("buildingId") buildingId: String,
        @Query("scheduled") scheduled: Boolean
    ): Call<ScheduleListAPIResponse>

    @GET("schedule/identity/{scheduleIdentityId}")
    fun getScheduleDetail(
        @Header("Authorization") auth: String,
        @Path("scheduleIdentityId") scheduleIdentityId: String
    ): Call<ScheduleDetailAPIResponse>

    @GET("schedule/{workItemId}")
    fun getWorkItemDetail(
        @Header("Authorization") auth: String,
        @Path("workItemId") workItemId: String
    ): Call<WorkItemDetailAPIResponse>

    @PUT("schedule/lumper/{workItemId}")
    fun assignLumpers(
        @Header("Authorization") auth: String,
        @Path("workItemId") workItemId: String,
        @Body request: AssignLumpersRequest
    ): Call<ResponseBody>

    @PUT("schedule/{buildingId}/{workItemId}")
    fun changeWorkItemScheduleStatus(
        @Header("Authorization") auth: String,
        @Path("buildingId") buildingId: String,
        @Path("workItemId") workItemId: String,
        @Body request: ChangeWorkItemScheduleStatusRequest
    ): Call<BaseResponse>
    /////////////////////////////////////////////////////////////
}