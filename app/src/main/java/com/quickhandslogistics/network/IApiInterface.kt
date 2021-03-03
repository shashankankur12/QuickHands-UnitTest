package com.quickhandslogistics.network

import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.addContainer.AddContainerRequest
import com.quickhandslogistics.data.attendance.AttendanceDetail
import com.quickhandslogistics.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.data.buildingOperations.BuildingOperationAPIResponse
import com.quickhandslogistics.data.common.AllLumpersResponse
import com.quickhandslogistics.data.customerSheet.CustomerSheetListAPIResponse
import com.quickhandslogistics.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.data.forgotPassword.ForgotPasswordRequest
import com.quickhandslogistics.data.forgotPassword.ForgotPasswordResponse
import com.quickhandslogistics.data.login.LoginRequest
import com.quickhandslogistics.data.login.LoginResponse
import com.quickhandslogistics.data.lumperSheet.LumperSheetListAPIResponse
import com.quickhandslogistics.data.lumperSheet.LumperWorkDetailAPIResponse
import com.quickhandslogistics.data.lumperSheet.SubmitLumperSheetRequest
import com.quickhandslogistics.data.lumpers.LumperListAPIResponse
import com.quickhandslogistics.data.qhlContact.QhlContactListResponse
import com.quickhandslogistics.data.qhlContact.QhlOfficeInfoResponse
import com.quickhandslogistics.data.reports.ReportRequest
import com.quickhandslogistics.data.reports.ReportResponse
import com.quickhandslogistics.data.schedule.*
import com.quickhandslogistics.data.scheduleTime.*
import com.quickhandslogistics.data.workSheet.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface IApiInterface {
    @POST("employees/lead/login")
    fun doLogin(@Body request: LoginRequest): Call<LoginResponse>

    @GET("employees/lead/lumpers")
    fun getAllLumpersData(@Header("Authorization") auth: String, @Query("day") day: String): Call<LumperListAPIResponse>

    @GET("employees/me")
    fun getLeadProfile(@Header("Authorization") auth: String): Call<LeadProfileAPIResponse>

    @POST("employees/logout")
    fun logout(@Header("Authorization") auth: String): Call<BaseResponse>

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

    @POST("customer/schedules/add")
    fun addSchedulesWorkItem(@Header("Authorization") auth: String, @Body request: AddContainerRequest
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
    fun getAttendanceList(@Header("Authorization") auth: String, @Query("day") day: String): Call<GetAttendanceAPIResponse>

    @POST("employees/lead/attendance")
    fun saveAttendanceDetails(@Header("Authorization") auth: String, @Query("day") day: String, @Body request: List<AttendanceDetail>): Call<BaseResponse>

    @GET("employees/lumpers/present")
    fun getPresentLumpersList(@Header("Authorization") auth: String, @Query("day") day: String): Call<AllLumpersResponse>
    /////////////////////////////////////////////////////////////

    // Schedule Lumper Time /////////////////////////////////////////////////
    @GET("employees/scheduled/lumpers")
    fun getScheduleTimeList(@Header("Authorization") auth: String, @Query("day") day: String): Call<GetScheduleTimeAPIResponse>

    @POST("employees/schedule/lumpers")
    fun saveScheduleTimeDetails(@Header("Authorization") auth: String, @Body request: ScheduleTimeRequest): Call<BaseResponse>

    @GET("employees/lead/lumpers/requests")
    fun getRequestLumpersList(@Header("Authorization") auth: String, @Query("day") day: String): Call<RequestLumpersListAPIResponse>

    @POST("employees/lead/lumpers/request")
    fun createRequestLumpers(@Header("Authorization") auth: String, @Body request: RequestLumpersRequest): Call<BaseResponse>

    @PUT("employees/lead/lumpers/request/{requestId}")
    fun updateRequestLumpers(
        @Header("Authorization") auth: String, @Path("requestId") requestId: String, @Body request: RequestLumpersRequest
    ): Call<BaseResponse>

    @POST("employees/requests/cancel")
    fun cancelRequestLumpers(@Header("Authorization") auth: String, @Body request: CancelRequestLumpersRequest): Call<BaseResponse>

    @DELETE("employees/scheduled/lumpers/{lumperId}")
    fun cancelScheduleLumper(@Header("Authorization") auth: String, @Path("lumperId") lumperId: String, @Query("day") day: String): Call<BaseResponse>

    @PUT("employees/scheduled/lumpers/{lumperId}")
    fun editScheduleLumper(@Header("Authorization") auth: String, @Path("lumperId") lumperId: String, @Query("day") day: String, @Query("reportingTime") reportingTime: Long, @Body request: ScheduleTimeNoteRequest): Call<BaseResponse>

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
        @Header("Authorization") auth: String,
        @Part("customerRepresentativeName") customerRepresentativeName: RequestBody,
        @Part("note") note: RequestBody,
        @Part signature: MultipartBody.Part? = null,
        @Part("customerSheetId") customerIdBody: RequestBody
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

    // Reports /////////////////////////////////////////////////
    @POST("employees/worksheet/reports/lumpers")
    fun createLumperJobReport(
        @Header("Authorization") auth: String, @Query("start") startDate: String,
        @Query("end") endDate: String, @Query("type") type: String, @Body request: ReportRequest
    ): Call<ReportResponse>

    @POST("employees/timeclock/reports/lumpers")
    fun createTimeClockReport(
        @Header("Authorization") auth: String, @Query("start") startDate: String,
        @Query("end") endDate: String, @Query("type") type: String, @Body request: ReportRequest
    ): Call<ReportResponse>

    @POST("customers/reports")
    fun createCustomerReport(
        @Header("Authorization") auth: String, @Query("start") startDate: String,
        @Query("end") endDate: String, @Query("type") type: String
    ): Call<ReportResponse>

    @GET("employees/lead/lumpers/date-range")
    fun getAllLumpersSelectedDates(@Header("Authorization") auth: String, @Query("dayStart") dayStart: String, @Query("dayEnd") dayEnd: String): Call<LumperListAPIResponse>
    /////////////////////////////////////////////////////////////

    //QHL Contact////////////////////////////////////////////////
    @GET("employees/lead/qhl-contacts")
    fun getQhlContactList(@Header("Authorization") auth: String): Call<QhlContactListResponse>

    @GET("employees/admin/office")
    fun getQhlOfficeInfo(@Header("Authorization") auth: String): Call<QhlOfficeInfoResponse>
    ////////////////////////////////////////////////////////////

    //Customer Contact/////////////////////////////////////////
    @GET("employees/lead/customer-contacts")
    fun getCustomerContactList(@Header("Authorization") auth: String): Call<QhlContactListResponse>
    ////////////////////////////////////////////////////////////


}