package com.quickhandslogistics.network

import com.quickhandslogistics.modified.data.login.LoginRequest
import com.quickhandslogistics.modified.data.login.LoginResponse
import com.quickhandslogistics.model.lumper.AllLumpersResponse
import com.quickhandslogistics.utils.AppConstant
import com.quickhandslogistics.utils.SharedPref

import okhttp3.OkHttpClient
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
                .baseUrl(AppConfiguration.MOCK_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }
        return retrofitStandard
    }

    private fun getMockDataManager(): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(AppConfiguration.MOCK_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }

    fun getService(): IApiInterface {
        return getDataManager()!!.create(IApiInterface::class.java)
    }

    fun getMockService(): IApiInterface {
        return getMockDataManager()!!.create(IApiInterface::class.java)
    }

     fun doLogin(loginRequest: LoginRequest, listener: ResponseListener<LoginResponse>) {
           val call = getService().doLogin(loginRequest)
               call.enqueue(object : Callback<LoginResponse> {
                   override fun onResponse(
                       call: Call<LoginResponse>,
                       response: Response<LoginResponse>
                   ) {
                       if (!response.isSuccessful) {
                           response.errorBody()?.let { listener.onError(it) }
                           return
                       }

                       if (response.body()?.success != null && response.body()?.success == true)
                           listener.onSuccess(response.body()!!)
                       else
                           listener.onError(response.body()!!.message)
                   }

                   override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                       listener.onError(t)
                   }
               })
           }

    fun getAllLumpersData(listener: ResponseListener<AllLumpersResponse>) {
        val call = getService().getAllLumpersData("Bearer " + SharedPref.getInstance().getString(AppConstant.PREF_AUTH_TOKEN))
        call.enqueue(object : Callback<AllLumpersResponse> {
            override fun onResponse(call: Call<AllLumpersResponse>, response: Response<AllLumpersResponse>) {
                if (!response.isSuccessful) {
                    response.errorBody()?.let { listener.onError(it) }
                    return
                }

                if (response.body()?.success != null && response.body()?.success == true)
                    listener.onSuccess(response.body()!!)
                else
                    listener.onError(response.body()!!.message)
            }

            override fun onFailure(call: Call<AllLumpersResponse>, t: Throwable) {
                listener.onError(t)
            }
        })
    }
       }

