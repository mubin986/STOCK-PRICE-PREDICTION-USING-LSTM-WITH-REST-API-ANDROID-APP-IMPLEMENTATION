package com.theappbangla.dseprediction.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Created by praka on 12/24/2017.
 */

class RetrofitClientInstance {
    companion object {
        private var retrofit: Retrofit? = null
//        private val BASE_URL = "http://10.0.2.2:8080/"
        private val BASE_URL = "http://192.168.201.4:5000/"
//        private val BASE_URL = "http://localhost:5000/"

        fun getInstance(): Retrofit {
            if (retrofit == null) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS) // connect timeout
                    .readTimeout(30, TimeUnit.SECONDS)    // socket timeout
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
//                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(okHttpClient)
                    .build()
            }
            return retrofit!!
        }
    }
}