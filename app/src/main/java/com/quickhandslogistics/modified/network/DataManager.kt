package com.quickhandslogistics.modified.network

import com.google.gson.Gson
import com.quickhandslogistics.modified.data.dashboard.LeadProfileAPIResponse
import com.quickhandslogistics.modified.data.ErrorResponse
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordRequest
import com.quickhandslogistics.modified.data.forgotPassword.ForgotPasswordResponse
import com.quickhandslogistics.modified.data.login.LoginRequest
import com.quickhandslogistics.modified.data.login.LoginResponse
import com.quickhandslogistics.modified.data.lumpers.AllLumpersResponse
import com.quickhandslogistics.modified.data.schedule.ScheduleAPIResponse
import com.quickhandslogistics.network.AppConfiguration
import com.quickhandslogistics.network.IApiInterface
import com.quickhandslogistics.network.NetworkConnectionInterceptor
import com.quickhandslogistics.network.ResponseListener
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DataManager : AppConstant {
    private var retrofit: Retrofit? = null
    private var retrofitStandard: Retrofit? = null
    private val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

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
                    errorMessage = getErrorMessage(response.errorBody())
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
                    errorMessage = getErrorMessage(response.errorBody())
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
                    errorMessage = getErrorMessage(response.errorBody())
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
                    errorMessage = getErrorMessage(response.errorBody())
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
        date: String,
        buildingId: String,
        scheduled: Boolean,
        listener: ResponseListener<ScheduleAPIResponse>
    ) {
        val call = getService().getSchedulesList(
            "Bearer " + SharedPref.getInstance().getString(AppConstant.PREFERENCE_AUTH_TOKEN),
            date,
            buildingId,
            scheduled
        )
        call.enqueue(object : Callback<ScheduleAPIResponse> {
            override fun onResponse(
                call: Call<ScheduleAPIResponse>,
                response: Response<ScheduleAPIResponse>
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

            override fun onFailure(call: Call<ScheduleAPIResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }

    private fun getErrorMessage(errorBody: ResponseBody?): String {
        var errorMessage = ""
        errorBody?.let {
            val errorResponse: ErrorResponse? =
                Gson().fromJson(String(it.bytes()), ErrorResponse::class.java)
            errorResponse?.let {
                errorMessage = errorResponse.message!!
            }
        }
        return errorMessage
    }
}