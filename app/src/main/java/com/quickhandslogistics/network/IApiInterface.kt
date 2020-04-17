package com.quickhandslogistics.network

import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.modified.data.buildingOperations.BuildingOperationAPIResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordRequest
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordResponse
import com.quickhandslogistics.modified.data.login.LoginRequest
import com.quickhandslogistics.modified.data.login.LoginResponse
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.schedule.*
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
        @Query("buildingId") buildingId: String
    ): Call<ScheduleListAPIResponse>

    @GET("schedule/unscheduled")
    fun getUnSchedulesList(
        @Header("Authorization") auth: String,
        @Query("buildingId") buildingId: String
    ): Call<UnScheduleListAPIResponse>

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
    ): Call<BaseResponse>
    /////////////////////////////////////////////////////////////

    // Building Operations /////////////////////////////////////////////////
    @GET("schedule/{workItemId}/operations")
    fun getBuildingOperationsDetail(
        @Header("Authorization") auth: String,
        @Path("workItemId") workItemId: String
    ): Call<BuildingOperationAPIResponse>

    @POST("schedule/{workItemId}/operations")
    fun saveBuildingOperationsDetail(
        @Header("Authorization") auth: String,
        @Path("workItemId") workItemId: String,
        @Body request: HashMap<String, String>
    ): Call<BaseResponse>
    /////////////////////////////////////////////////////////////

    // Attendance /////////////////////////////////////////////////
    @GET("employees/lead/attendance")
    fun getAttendanceList(
        @Header("Authorization") auth: String
    ): Call<GetAttendanceAPIResponse>

    @POST("employees/lead/attendance")
    fun saveAttendanceDetails(
        @Header("Authorization") auth: String,
        @Body request: List<AttendanceDetail>
    ): Call<BaseResponse>
    /////////////////////////////////////////////////////////////
}