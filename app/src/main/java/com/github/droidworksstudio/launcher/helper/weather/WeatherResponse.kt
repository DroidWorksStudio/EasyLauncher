package com.github.droidworksstudio.launcher.helper.weather

data class WeatherResponse(
    val name: String,
    val main: Main,
    val wind: Wind,
    val sys: Sys,
    val weather: List<Weather>
)

data class Wind(
    val speed: Double,
    val deg: Int
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)

data class Weather(
    val description: String,
    val id: Int
)

data class Sys(
    val country: String
)