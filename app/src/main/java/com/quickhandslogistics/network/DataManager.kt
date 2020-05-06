package com.quickhandslogistics.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.ErrorResponse
import com.quickhandslogistics.modified.data.attendance.AttendanceDetail
import com.quickhandslogistics.modified.data.attendance.GetAttendanceAPIResponse
import com.quickhandslogistics.modified.data.buildingOperations.BuildingOperationAPIResponse
import com.quickhandslogistics.modified.data.customerSheet.CustomerSheetListAPIResponse
import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordRequest
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordResponse
import com.quickhandslogistics.modified.data.login.LoginRequest
import com.quickhandslogistics.modified.data.login.LoginResponse
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.schedule.*
import com.quickhandslogistics.modified.data.scheduleTime.GetScheduleTimeAPIResponse
import com.quickhandslogistics.modified.data.scheduleTime.ScheduleTimeRequest
import com.quickhandslogistics.modified.data.workSheet.*
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


object DataManager : AppConstant {
    private var retrofitStandard: Retrofit? = null
    private val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.MINUTES)
        .readTimeout(2, TimeUnit.MINUTES)
        .writeTimeout(2, TimeUnit.MINUTES)
        .addInterceptor(NetworkConnectionInterceptor())
        .addInterceptor(interceptor)
        .build()

    private fun getDataManager(): Retrofit? {
        if (retrofitStandard == null) {
            retrofitStandard = Retrofit.Builder()
                .baseUrl(AppConfiguration.API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }
        return retrofitStandard
    }

    private fun getService(): IApiInterface {
        return getDataManager()!!.create(IApiInterface::class.java)
    }

    fun doLogin(loginRequest: LoginRequest, listener: ResponseListener<LoginResponse>) {
        val call = getService().doLogin(loginRequest)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun doPasswordReset(
        forgotPasswordRequest: ForgotPasswordRequest,
        listener: ResponseListener<ForgotPasswordResponse>
    ) {
        val call = getService().doResetPassword(forgotPasswordRequest)
        call.enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(
                call: Call<ForgotPasswordResponse>,
                response: Response<ForgotPasswordResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun getAllLumpersData(listener: ResponseListener<AllLumpersResponse>) {
        val call = getService().getAllLumpersData(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN)
        )
        call.enqueue(object : Callback<AllLumpersResponse> {
            override fun onResponse(
                call: Call<AllLumpersResponse>,
                response: Response<AllLumpersResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<AllLumpersResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun getLeadProfile(listener: ResponseListener<LeadProfileAPIResponse>) {
        val call = getService().getLeadProfile(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN)
        )
        call.enqueue(object : Callback<LeadProfileAPIResponse> {
            override fun onResponse(
                call: Call<LeadProfileAPIResponse>,
                response: Response<LeadProfileAPIResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<LeadProfileAPIResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun getSchedulesList(
        date: String, listener: ResponseListener<ScheduleListAPIResponse>
    ) {
        val call = getService().getSchedulesList(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN),
            date
        )
        call.enqueue(object : Callback<ScheduleListAPIResponse> {
            override fun onResponse(
                call: Call<ScheduleListAPIResponse>,
                response: Response<ScheduleListAPIResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<ScheduleListAPIResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun getUnSchedulesList(listener: ResponseListener<UnScheduleListAPIResponse>) {
        val call = getService().getUnSchedulesList(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN)
        )
        call.enqueue(object : Callback<UnScheduleListAPIResponse> {
            override fun onResponse(
                call: Call<UnScheduleListAPIResponse>,
                response: Response<UnScheduleListAPIResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<UnScheduleListAPIResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun getScheduleDetail(
        scheduleIdentityId: String,
        date: String,
        listener: ResponseListener<ScheduleDetailAPIResponse>
    ) {
        val call = getService().getScheduleDetail(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN),
            scheduleIdentityId, date
        )
        call.enqueue(object : Callback<ScheduleDetailAPIResponse> {
            override fun onResponse(
                call: Call<ScheduleDetailAPIResponse>,
                response: Response<ScheduleDetailAPIResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<ScheduleDetailAPIResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun getWorkItemDetail(
        workItemId: String,
        listener: ResponseListener<WorkItemDetailAPIResponse>
    ) {
        val call = getService().getWorkItemDetail(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN),
            workItemId
        )
        call.enqueue(object : Callback<WorkItemDetailAPIResponse> {
            override fun onResponse(
                call: Call<WorkItemDetailAPIResponse>,
                response: Response<WorkItemDetailAPIResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<WorkItemDetailAPIResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun assignLumpers(
        workItemId: String, assignLumpersRequest: AssignLumpersRequest,
        listener: ResponseListener<BaseResponse>
    ) {
        val call = getService().assignLumpers(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN),
            workItemId, assignLumpersRequest
        )
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun getLumpersAttendanceList(listener: ResponseListener<GetAttendanceAPIResponse>) {
        val call = getService().getAttendanceList(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN)
        )
        call.enqueue(object : Callback<GetAttendanceAPIResponse> {
            override fun onResponse(
                call: Call<GetAttendanceAPIResponse>,
                response: Response<GetAttendanceAPIResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<GetAttendanceAPIResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun saveLumpersAttendanceList(
        attendanceDetailList: List<AttendanceDetail>,
        listener: ResponseListener<BaseResponse>
    ) {
        val call = getService().saveAttendanceDetails(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN),
            attendanceDetailList
        )
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun getBuildingOperationsDetail(
        workItemId: String,
        listener: ResponseListener<BuildingOperationAPIResponse>
    ) {
        val call = getService().getBuildingOperationsDetail(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN),
            workItemId
        )
        call.enqueue(object : Callback<BuildingOperationAPIResponse> {
            override fun onResponse(
                call: Call<BuildingOperationAPIResponse>,
                response: Response<BuildingOperationAPIResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<BuildingOperationAPIResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun saveBuildingOperationsDetail(
        workItemId: String,
        request: HashMap<String, String>,
        listener: ResponseListener<BaseResponse>
    ) {
        val call = getService().saveBuildingOperationsDetail(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN),
            workItemId, request
        )
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun getScheduleTimeList(day: String, listener: ResponseListener<GetScheduleTimeAPIResponse>) {
        val call = getService().getScheduleTimeList(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN), day
        )
        call.enqueue(object : Callback<GetScheduleTimeAPIResponse> {
            override fun onResponse(
                call: Call<GetScheduleTimeAPIResponse>,
                response: Response<GetScheduleTimeAPIResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<GetScheduleTimeAPIResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun saveScheduleTimeDetail(
        request: ScheduleTimeRequest, listener: ResponseListener<BaseResponse>
    ) {
        val call = getService().saveScheduleTimeDetails(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN),
            request
        )
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>, response: Response<BaseResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun getWorkSheetList(
        day: String, listener: ResponseListener<WorkSheetListAPIResponse>
    ) {
        val call = getService().getWorkSheetList(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN), day
        )
        call.enqueue(object : Callback<WorkSheetListAPIResponse> {
            override fun onResponse(
                call: Call<WorkSheetListAPIResponse>,
                response: Response<WorkSheetListAPIResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<WorkSheetListAPIResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun changeWorkItemStatus(
        workItemId: String, request: ChangeStatusRequest,
        listener: ResponseListener<BaseResponse>
    ) {
        val call = getService().changeWorkItemStatus(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN),
            workItemId, request
        )
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage =
                        getErrorMessage(
                            response.errorBody()
                        )
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun updateWorkItemNotes(
        workItemId: String, request: UpdateNotesRequest, listener: ResponseListener<BaseResponse>
    ) {
        val call = getService().updateWorkItemNotes(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN),
            workItemId, request
        )
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage = getErrorMessage(response.errorBody())
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun updateLumperTimeInWorkItem(
        request: UpdateLumperTimeRequest, listener: ResponseListener<BaseResponse>
    ) {
        val call = getService().updateLumperTimeInWorkItem(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN),
            request
        )
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>, response: Response<BaseResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage = getErrorMessage(response.errorBody())
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun cancelAllSchedules(
        day: String, request: CancelAllSchedulesRequest, listener: ResponseListener<BaseResponse>
    ) {
        val call = getService().cancelAllSchedules(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN),
            day, request
        )
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>, response: Response<BaseResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage = getErrorMessage(response.errorBody())
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun getCustomerSheetList(
        day: String, listener: ResponseListener<CustomerSheetListAPIResponse>
    ) {
        val call = getService().getCustomerSheetList(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN), day
        )
        call.enqueue(object : Callback<CustomerSheetListAPIResponse> {
            override fun onResponse(
                call: Call<CustomerSheetListAPIResponse>,
                response: Response<CustomerSheetListAPIResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage = getErrorMessage(response.errorBody())
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<CustomerSheetListAPIResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun saveCustomerSheetList(
        name: String, notes: String, file: File, listener: ResponseListener<BaseResponse>
    ) {
        val nameRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), name)
        val notesRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), notes)

        val signatureRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val signatureMultiPartBody =
            MultipartBody.Part.createFormData("signature", file.name, signatureRequestBody)

        val call = getService().saveCustomerSheet(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN),
            nameRequestBody, notesRequestBody, signatureMultiPartBody
        )
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                var errorMessage = ""
                if (!response.isSuccessful) {
                    errorMessage = getErrorMessage(response.errorBody())
                    listener.onError(errorMessage)
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    private fun getErrorMessage(errorBody: ResponseBody?): String {
        var errorMessage = ""
        errorBody?.let {
            val errorBodyString = String(it.bytes())
            errorMessage = try {
                val errorResponse: ErrorResponse =
                    Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                errorResponse.message!!
            } catch (e: JsonSyntaxException) {
                errorBodyString
            }
        }
        return errorMessage
    }
}