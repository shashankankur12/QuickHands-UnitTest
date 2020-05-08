package com.quickhandslogistics.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.quickhandslogistics.modified.contracts.BaseContract
import com.quickhandslogistics.modified.data.BaseResponse
import com.quickhandslogistics.modified.data.ErrorResponse
import com.quickhandslogistics.modified.data.customerSheet.CustomerSheetListAPIResponse
import com.quickhandslogistics.modified.data.lumperSheet.LumperSheetListAPIResponse
import com.quickhandslogistics.modified.data.lumperSheet.LumperWorkDetailAPIResponse
import com.quickhandslogistics.modified.data.lumperSheet.SubmitLumperSheetRequest
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
            retrofitStandard = Retrofit.Builder().baseUrl(AppConfiguration.API_BASE_URL).client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create()).build()
        }
        return retrofitStandard
    }

    fun getService(): IApiInterface {
        return getDataManager()!!.create(IApiInterface::class.java)
    }

    fun getAuthToken(): String {
        return "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN)
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

    private fun getErrorMessage(errorBody: ResponseBody?): String {
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