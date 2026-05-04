package com.example.know_it_all.data.remote

import com.example.know_it_all.data.remote.api.LedgerService
import com.example.know_it_all.data.remote.api.SkillService
import com.example.know_it_all.data.remote.api.SwapService
import com.example.know_it_all.data.remote.api.UserService
import com.example.know_it_all.data.local.prefs.PreferenceManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Safe version that doesn't require BuildConfig.
 * 
 * To use environment-specific URLs later, add to build.gradle.kts:
 *   android {
 *     buildFeatures { buildConfig = true }
 *     defaultConfig {
 *       buildConfigField("String", "BASE_URL", "\"http://192.168.0.107:8080/api/v1/\"")
 *     }
 *     buildTypes {
 *       release {
 *         buildConfigField("String", "BASE_URL", "\"https://api.knowitall.app/api/v1/\"")
 *       }
 *     }
 *   }
 * Then replace BASE_URL below with BuildConfig.BASE_URL
 * and add: import com.example.know_it_all.BuildConfig
 */
object RetrofitClient {
    private const val BASE_URL = BuildConfig.BASE_URL

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY   // change to NONE for release
        }
    }

private val httpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val token = PreferenceManager.getToken(context) 

            val requestBuilder = original.newBuilder()
            
            if (!token.isNullOrEmpty()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
            
            chain.proceed(requestBuilder.build())
        }
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