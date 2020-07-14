package com.quickhandslogistics.network

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.quickhandslogistics.BuildConfig
import com.quickhandslogistics.contracts.BaseContract
import com.quickhandslogistics.data.BaseResponse
import com.quickhandslogistics.data.ErrorResponse
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
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
            retrofitStandard = Retrofit.Builder().baseUrl(BuildConfig.API_BASE_URL).client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create()).build()
        }
        return retrofitStandard
    }

    fun getOkHttpClient(): OkHttpClient? {
        return okHttpClient
    }

    fun getService(): IApiInterface {
        return getDataManager()!!.create(IApiInterface::class.java)
    }

    fun getAuthToken(): String {
        return "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN)
    }

    fun createRequestBody(value: String): RequestBody {
        return value.toRequestBody("multipart/form-data".toMediaTypeOrNull())
    }

    fun createMultiPartBody(file: File, variableName: String): MultipartBody.Part {
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
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
            var errorResponse=getErrorCode(errorBody)
            if (errorResponse.errCode == 777) {
                onFinishedListener.onErrorCode(errorResponse)
            } else {
                onFinishedListener.onFailure(errorResponse.message)
            }
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

    private fun getErrorCode(errorBody: ResponseBody?): ErrorResponse {
        var errorCode :ErrorResponse= ErrorResponse()
        errorBody?.let {
            val errorBodyString = String(it.bytes())
            errorCode = try {
                 Gson().fromJson(errorBodyString, ErrorResponse::class.java)
            } catch (e: JsonSyntaxException) {
                errorBodyString
            } as ErrorResponse
        }
        return errorCode
    }

    /*private fun logout() {
         var sharedPref = SharedPref.getInstance()
        sharedPref.performLogout()
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        activity.finish()
        Toast.makeText(activity, "Logout", Toast.LENGTH_SHORT).show()
    }*/
}