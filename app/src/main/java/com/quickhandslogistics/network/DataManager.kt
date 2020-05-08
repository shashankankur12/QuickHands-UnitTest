package com.quickhandslogistics.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.quickhandslogistics.modified.contracts.BaseContract
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
import com.quickhandslogistics.modified.data.lumperSheet.LumperSheetListAPIResponse
import com.quickhandslogistics.modified.data.lumperSheet.LumperWorkDetailAPIResponse
import com.quickhandslogistics.modified.data.lumperSheet.SubmitLumperSheetRequest
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

    fun getService(): IApiInterface {
        return getDataManager()!!.create(IApiInterface::class.java)
    }

    private fun getAuthToken(): String {
        return "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN)
    }

    fun doPasswordReset(forgotPasswordRequest: ForgotPasswordRequest, listener: ResponseListener<ForgotPasswordResponse>) {
        val call = getService().doResetPassword(forgotPasswordRequest)
        call.enqueue(object : Callback<ForgotPasswordResponse> {
            override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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
        val call = getService().getAllLumpersData(getAuthToken())
        call.enqueue(object : Callback<AllLumpersResponse> {
            override fun onResponse(call: Call<AllLumpersResponse>, response: Response<AllLumpersResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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
        val call = getService().getLeadProfile(getAuthToken())
        call.enqueue(object : Callback<LeadProfileAPIResponse> {
            override fun onResponse(call: Call<LeadProfileAPIResponse>, response: Response<LeadProfileAPIResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun getSchedulesList(date: String, listener: ResponseListener<ScheduleListAPIResponse>) {
        val call = getService().getSchedulesList(getAuthToken(), date)
        call.enqueue(object : Callback<ScheduleListAPIResponse> {
            override fun onResponse(call: Call<ScheduleListAPIResponse>, response: Response<ScheduleListAPIResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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
        val call = getService().getUnSchedulesList(getAuthToken())
        call.enqueue(object : Callback<UnScheduleListAPIResponse> {
            override fun onResponse(call: Call<UnScheduleListAPIResponse>, response: Response<UnScheduleListAPIResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun getScheduleDetail(scheduleIdentityId: String, date: String, listener: ResponseListener<ScheduleDetailAPIResponse>) {
        val call = getService().getScheduleDetail(getAuthToken(), scheduleIdentityId, date)
        call.enqueue(object : Callback<ScheduleDetailAPIResponse> {
            override fun onResponse(call: Call<ScheduleDetailAPIResponse>, response: Response<ScheduleDetailAPIResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun getWorkItemDetail(workItemId: String, listener: ResponseListener<WorkItemDetailAPIResponse>) {
        val call = getService().getWorkItemDetail(getAuthToken(), workItemId)
        call.enqueue(object : Callback<WorkItemDetailAPIResponse> {
            override fun onResponse(call: Call<WorkItemDetailAPIResponse>, response: Response<WorkItemDetailAPIResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun assignLumpers(workItemId: String, request: AssignLumpersRequest, listener: ResponseListener<BaseResponse>) {
        val call = getService().assignLumpers(getAuthToken(), workItemId, request)
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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
        val call = getService().getAttendanceList(getAuthToken())
        call.enqueue(object : Callback<GetAttendanceAPIResponse> {
            override fun onResponse(call: Call<GetAttendanceAPIResponse>, response: Response<GetAttendanceAPIResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun saveLumpersAttendanceList(attendanceDetailList: List<AttendanceDetail>, listener: ResponseListener<BaseResponse>) {
        val call = getService().saveAttendanceDetails(getAuthToken(), attendanceDetailList)
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun getBuildingOperationsDetail(workItemId: String, listener: ResponseListener<BuildingOperationAPIResponse>) {
        val call = getService().getBuildingOperationsDetail(getAuthToken(), workItemId)
        call.enqueue(object : Callback<BuildingOperationAPIResponse> {
            override fun onResponse(call: Call<BuildingOperationAPIResponse>, response: Response<BuildingOperationAPIResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun saveBuildingOperationsDetail(workItemId: String, request: HashMap<String, String>, listener: ResponseListener<BaseResponse>) {
        val call = getService().saveBuildingOperationsDetail(getAuthToken(), workItemId, request)
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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
        val call = getService().getScheduleTimeList(getAuthToken(), day)
        call.enqueue(object : Callback<GetScheduleTimeAPIResponse> {
            override fun onResponse(call: Call<GetScheduleTimeAPIResponse>, response: Response<GetScheduleTimeAPIResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun saveScheduleTimeDetail(request: ScheduleTimeRequest, listener: ResponseListener<BaseResponse>) {
        val call = getService().saveScheduleTimeDetails(getAuthToken(), request)
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun getWorkSheetList(day: String, listener: ResponseListener<WorkSheetListAPIResponse>) {
        val call = getService().getWorkSheetList(getAuthToken(), day)
        call.enqueue(object : Callback<WorkSheetListAPIResponse> {
            override fun onResponse(call: Call<WorkSheetListAPIResponse>, response: Response<WorkSheetListAPIResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun changeWorkItemStatus(workItemId: String, request: ChangeStatusRequest, listener: ResponseListener<BaseResponse>) {
        val call = getService().changeWorkItemStatus(getAuthToken(), workItemId, request)
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun updateWorkItemNotes(workItemId: String, request: UpdateNotesRequest, listener: ResponseListener<BaseResponse>) {
        val call = getService().updateWorkItemNotes(getAuthToken(), workItemId, request)
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun updateLumperTimeInWorkItem(request: UpdateLumperTimeRequest, listener: ResponseListener<BaseResponse>) {
        val call = getService().updateLumperTimeInWorkItem(getAuthToken(), request)
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun cancelAllSchedules(day: String, request: CancelAllSchedulesRequest, listener: ResponseListener<BaseResponse>) {
        val call = getService().cancelAllSchedules(getAuthToken(), day, request)
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun getCustomerSheetList(day: String, listener: ResponseListener<CustomerSheetListAPIResponse>) {
        val call = getService().getCustomerSheetList(getAuthToken(), day)
        call.enqueue(object : Callback<CustomerSheetListAPIResponse> {
            override fun onResponse(call: Call<CustomerSheetListAPIResponse>, response: Response<CustomerSheetListAPIResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun saveCustomerSheetList(name: String, notes: String, file: File, listener: ResponseListener<BaseResponse>) {
        val nameRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), name)
        val notesRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), notes)

        val signatureRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val signatureMultiPartBody = MultipartBody.Part.createFormData("signature", file.name, signatureRequestBody)

        val call = getService().saveCustomerSheet(getAuthToken(), nameRequestBody, notesRequestBody, signatureMultiPartBody)
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun getLumperSheetList(day: String, listener: ResponseListener<LumperSheetListAPIResponse>) {
        val call = getService().getLumperSheetList(getAuthToken(), day)
        call.enqueue(object : Callback<LumperSheetListAPIResponse> {
            override fun onResponse(call: Call<LumperSheetListAPIResponse>, response: Response<LumperSheetListAPIResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<LumperSheetListAPIResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun getLumperWorkDetails(lumperId: String, day: String, listener: ResponseListener<LumperWorkDetailAPIResponse>) {
        val call = getService().getLumperWorkDetail(getAuthToken(), day, lumperId)
        call.enqueue(object : Callback<LumperWorkDetailAPIResponse> {
            override fun onResponse(call: Call<LumperWorkDetailAPIResponse>, response: Response<LumperWorkDetailAPIResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
                } else {
                    response.body()?.let { it ->
                        listener.onSuccess(it)
                    }
                }
            }

            override fun onFailure(call: Call<LumperWorkDetailAPIResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    fun saveLumperSigntaure(lumperId: String, day: String, file: File, listener: ResponseListener<BaseResponse>) {
        val lumperIdRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), lumperId)
        val dayRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), day)

        val signatureRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val signatureMultiPartBody = MultipartBody.Part.createFormData("signature", file.name, signatureRequestBody)

        val call = getService().saveLumperSignature(getAuthToken(), dayRequestBody, lumperIdRequestBody, signatureMultiPartBody)
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun submitLumperSheet(request: SubmitLumperSheetRequest, listener: ResponseListener<BaseResponse>) {
        val call = getService().submitLumperSheet(getAuthToken(), request)
        call.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (!response.isSuccessful) {
                    listener.onError(getErrorMessage(response.errorBody()))
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

    fun isSuccessResponse(isSuccessful: Boolean, body: BaseResponse?, errorBody: ResponseBody?, onFinishedListener: BaseContract.Model.OnFinishedListener): Boolean {
        var isSuccessResponse = false

        if (isSuccessful) {
            body?.also { it ->
                if (it.success) {
                    isSuccessResponse = true
                } else {
                    onFinishedListener.onFailure(it.message)
                }
            } ?: run {
                onFinishedListener.onFailure()
            }
        } else {
            onFinishedListener.onFailure(getErrorMessage(errorBody))
        }
        return isSuccessResponse
    }

    fun getErrorMessage(errorBody: ResponseBody?): String {
        var errorMessage = ""
        errorBody?.let {
            val errorBodyString = String(it.bytes())
            errorMessage = try {
                val errorResponse: ErrorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
                errorResponse.message
            } catch (e: JsonSyntaxException) {
                errorBodyString
            }
        }
        return errorMessage
    }
}