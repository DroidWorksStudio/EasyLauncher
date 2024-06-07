package com.github.droidworksstudio.launcher.utils

import com.github.droidworksstudio.launcher.helper.weather.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather")
    fun getWeather(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("units") units: String,
        @Query("apiKey") apiKey: String
    ): Call<WeatherResponse>
}
