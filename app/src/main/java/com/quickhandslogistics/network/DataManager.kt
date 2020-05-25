package com.quickhandslogistics.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
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

    fun createRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value)
    }

    fun createMultiPartBody(file: File, variableName: String): MultipartBody.Part {
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        return MultipartBody.Part.createFormData(variableName, file.name, requestBody)
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