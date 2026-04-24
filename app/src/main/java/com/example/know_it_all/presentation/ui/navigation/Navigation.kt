package com.example.know_it_all.data.remote

import com.example.know_it_all.BuildConfig
import com.example.know_it_all.data.remote.api.LedgerService
import com.example.know_it_all.data.remote.api.SkillService
import com.example.know_it_all.data.remote.api.SwapService
import com.example.know_it_all.data.remote.api.UserService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }
    }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userService: UserService by lazy { retrofit.create(UserService::class.java) }
    val skillService: SkillService by lazy { retrofit.create(SkillService::class.java) }
    val swapService: SwapService by lazy { retrofit.create(SwapService::class.java) }
    val ledgerService: LedgerService by lazy { retrofit.create(LedgerService::class.java) }
}