package com.example.know_it_all.data.remote

import com.example.know_it_all.data.local.prefs.PreferenceManager
import com.example.know_it_all.data.remote.api.LedgerService
import com.example.know_it_all.data.remote.api.UserService
import com.example.know_it_all.data.remote.api.SkillService
import com.example.know_it_all.data.remote.api.SwapService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://192.168.0.107:8080/api/v1/"
    private var prefManager: PreferenceManager? = null

    fun init(prefManager: PreferenceManager) {
        this.prefManager = prefManager
    }

    private val authInterceptor: Interceptor
        get() = Interceptor { chain ->
            val originalRequest = chain.request()
            val token = prefManager?.getAuthToken()
            
            val requestBuilder = originalRequest.newBuilder()
            if (!token.isNullOrEmpty()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
            
            chain.proceed(requestBuilder.build())
        }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userService: UserService by lazy { retrofit.create(UserService::class.java) }
    val skillService: SkillService by lazy { retrofit.create(SkillService::class.java) }
    val swapService: SwapService by lazy { retrofit.create(SwapService::class.java) }
    val ledgerService: LedgerService by lazy { retrofit.create(LedgerService::class.java) }
}
