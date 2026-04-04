package com.example.know_it_all.data.remote

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
    private const val BASE_URL = "http://10.0.2.2:8080/api/v1/" 
    // Change to "https://your-backend-domain.com/api/v1/" for production

    private val httpClient: OkHttpClient
        get() {
            val clientBuilder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)

            // Enable logging in all builds (remove BuildConfig dependency)
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            clientBuilder.addInterceptor(loggingInterceptor)

            return clientBuilder.build()
        }

    private val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun createUserService(): UserService = retrofit.create(UserService::class.java)
    fun createSkillService(): SkillService = retrofit.create(SkillService::class.java)
    fun createSwapService(): SwapService = retrofit.create(SwapService::class.java)
    fun createLedgerService(): LedgerService = retrofit.create(LedgerService::class.java)
}
