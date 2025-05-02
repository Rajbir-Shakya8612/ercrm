package com.example.ercrm.di

import android.util.Log
import com.example.ercrm.data.api.ApiService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TAG = "NetworkModule"
    private var authToken: String? = null

    fun setAuthToken(token: String?) {
        Log.d(TAG, "Setting auth token: ${token?.take(10)}...")
        authToken = token
    }

    fun getAuthToken(): String? = authToken

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                
                // Add auth token if available
                val request = if (authToken != null) {
                    Log.d(TAG, "Adding auth token to request: ${originalRequest.url}")
                    originalRequest.newBuilder()
                        .header("Authorization", "Bearer $authToken")
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .build()
                } else {
                    Log.d(TAG, "No auth token available for request: ${originalRequest.url}")
                    originalRequest.newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .build()
                }

                try {
                    val response = chain.proceed(request)
                    if (!response.isSuccessful) {
                        Log.e(TAG, "Request failed: ${response.code} - ${response.message}")
                        val errorBody = response.body?.string()
                        Log.e(TAG, "Error response body: $errorBody")
                        
                        if (response.code == 401) {
                            Log.e(TAG, "Authentication failed. Token may be invalid or expired.")
                            // Clear the token on authentication failure
                            setAuthToken(null)
                        }
                    }
                    response
                } catch (e: Exception) {
                    Log.e(TAG, "Error during request", e)
                    throw e
                }
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl("https://plyvista.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
} 