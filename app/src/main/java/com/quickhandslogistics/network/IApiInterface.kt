package com.quickhandslogistics.network

import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.modified.data.buildingOperations.BuildingOperationAPIResponse
import com.quickhandslogistics.modified.data.common.AllLumpersResponse
import com.quickhandslogistics.modified.data.customerSheet.CustomerSheetListAPIResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordRequest
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordResponse
import com.quickhandslogistics.modified.data.login.LoginRequest
import com.quickhandslogistics.modified.data.login.LoginResponse
import com.quickhandslogistics.modified.data.lumperSheet.LumperSheetListAPIResponse
import com.quickhandslogistics.modified.data.lumperSheet.LumperWorkDetailAPIResponse
import com.quickhandslogistics.modified.data.lumperSheet.SubmitLumperSheetRequest
import com.quickhandslogistics.modified.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.modified.data.schedule.*
import com.quickhandslogistics.modified.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeRequest
import com.quickhandslogistics.modified.data.workSheet.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface IApiInterface {
    @POST("employees/lead/login")
    fun doLogin(@Body request: LoginRequest): Call<LoginResponse>

    @GET("employees/lead/lumpers")
    fun getAllLumpersData(
        @Header("Authorization") auth: String, @Query("page") page: Int, @Query("pageSize") pageSize: Int
    ): Call<LumperListAPIResponse>

    @GET("employees/me")
    fun getLeadProfile(@Header("Authorization") auth: String): Call<LeadProfileAPIResponse>

    @POST("emails/forgot-password/lead")
    fun doResetPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    // Schedule /////////////////////////////////////////////////
    @GET("schedule/lookup/date")
    fun getSchedulesList(
        @Header("Authorization") auth: String, @Query("date") date: String,
        @Query("page") page: Int, @Query("pageSize") pageSize: Int
    ): Call<ScheduleListAPIResponse>

    @GET("schedule/unscheduled")
    fun getUnSchedulesList(@Header("Authorization") auth: String): Call<UnScheduleListAPIResponse>

    @GET("schedule/identity/{scheduleIdentityId}")
    fun getScheduleDetail(
        @Header("Authorization") auth: String, @Path("scheduleIdentityId") scheduleIdentityId: String, @Query("day") day: String
    ): Call<ScheduleDetailAPIResponse>

    @GET("schedule/{workItemId}")
    fun getWorkItemDetail(@Header("Authorization") auth: String, @Path("workItemId") workItemId: String): Call<WorkItemDetailAPIResponse>

    @PUT("schedule/lumper/{workItemId}")
    fun assignLumpers(
        @Header("Authorization") auth: String, @Path("workItemId") workItemId: String, @Body request: AssignLumpersRequest
    ): Call<BaseResponse>
    /////////////////////////////////////////////////////////////

    // Building Operations /////////////////////////////////////////////////
    @GET("schedule/{workItemId}/operations")
    fun getBuildingOperationsDetail(@Header("Authorization") auth: String, @Path("workItemId") workItemId: String): Call<BuildingOperationAPIResponse>

    @POST("schedule/{workItemId}/operations")
    fun saveBuildingOperationsDetail(
        @Header("Authorization") auth: String, @Path("workItemId") workItemId: String, @Body request: HashMap<String, String>
    ): Call<BaseResponse>
    /////////////////////////////////////////////////////////////

    // Attendance /////////////////////////////////////////////////
    @GET("employees/lead/attendance")
    fun getAttendanceList(@Header("Authorization") auth: String): Call<GetAttendanceAPIResponse>

    @POST("employees/lead/attendance")
    fun saveAttendanceDetails(@Header("Authorization") auth: String, @Body request: List<AttendanceDetail>): Call<BaseResponse>

    @GET("employees/lumpers/present")
    fun getPresentLumpersList(@Header("Authorization") auth: String, @Query("day") day: String): Call<AllLumpersResponse>
    /////////////////////////////////////////////////////////////

    // Schedule Lumper Time /////////////////////////////////////////////////
    @GET("employees/scheduled/lumpers")
    fun getScheduleTimeList(@Header("Authorization") auth: String, @Query("day") day: String): Call<GetScheduleTimeAPIResponse>

    @POST("employees/schedule/lumpers")
    fun saveScheduleTimeDetails(@Header("Authorization") auth: String, @Body request: ScheduleTimeRequest): Call<BaseResponse>
    /////////////////////////////////////////////////////////////

    // Work Sheet /////////////////////////////////////////////////
    @GET("buildings/workitems")
    fun getWorkSheetList(@Header("Authorization") auth: String, @Query("day") day: String): Call<WorkSheetListAPIResponse>

    @PUT("schedule/status/workitem/{workItemId}")
    fun changeWorkItemStatus(
        @Header("Authorization") auth: String, @Path("workItemId") workItemId: String, @Body request: ChangeStatusRequest
    ): Call<BaseResponse>

    @PUT("schedule/update/{workItemId}")
    fun updateWorkItemNotes(
        @Header("Authorization") auth: String, @Path("workItemId") workItemId: String, @Body request: UpdateNotesRequest
    ): Call<BaseResponse>

    @POST("employees/timings")
    fun updateLumperTimeInWorkItem(@Header("Authorization") auth: String, @Body request: UpdateLumperTimeRequest): Call<BaseResponse>

    @POST("schedule/cancel/all/{day}")
    fun cancelAllSchedules(
        @Header("Authorization") auth: String, @Path("day") day: String, @Body request: CancelAllSchedulesRequest
    ): Call<BaseResponse>
    /////////////////////////////////////////////////////////////

    // Customer Sheet /////////////////////////////////////////////////
    @GET("customers/sheet")
    fun getCustomerSheetList(@Header("Authorization") auth: String, @Query("day") day: String): Call<CustomerSheetListAPIResponse>

    @Multipart
    @POST("customers/sheet")
    fun saveCustomerSheet(
        @Header("Authorization") auth: String, @Part("customerRepresentativeName") customerRepresentativeName: RequestBody,
        @Part("note") note: RequestBody, @Part signature: MultipartBody.Part
    ): Call<BaseResponse>
    /////////////////////////////////////////////////////////////

    // Lumper Sheet /////////////////////////////////////////////////
    @GET("employees/siginfo/lumpers")
    fun getLumperSheetList(@Header("Authorization") auth: String, @Query("day") day: String): Call<LumperSheetListAPIResponse>

    @GET("employees/daily/worksheet")
    fun getLumperWorkDetail(
        @Header("Authorization") auth: String, @Query("day") day: String, @Query("lumperId") lumperId: String
    ): Call<LumperWorkDetailAPIResponse>

    @Multipart
    @POST("employees/sig/lumper")
    fun saveLumperSignature(
        @Header("Authorization") auth: String, @Part("day") day: RequestBody,
        @Part("lumperId") lumperId: RequestBody, @Part signature: MultipartBody.Part
    ): Call<BaseResponse>

    @POST("employees/lumpersheet/finalize")
    fun submitLumperSheet(@Header("Authorization") auth: String, @Body request: SubmitLumperSheetRequest): Call<BaseResponse>
    /////////////////////////////////////////////////////////////
}