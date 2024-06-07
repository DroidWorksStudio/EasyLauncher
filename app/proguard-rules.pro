# Keep the WeatherResponse and its nested classes
-keep class com.github.droidworksstudio.launcher.helper.weather.WeatherResponse { *; }
-keep class com.github.droidworksstudio.launcher.helper.weather.Wind { *; }
-keep class com.github.droidworksstudio.launcher.helper.weather.Main { *; }
-keep class com.github.droidworksstudio.launcher.helper.weather.Weather { *; }

# Keep all model classes and their fields that Gson might use for serialization/deserialization
-keep class com.github.droidworksstudio.launcher.helper.weather.** { *; }

# Keep Gson specific annotations
-keepattributes Signature
-keepattributes *Annotation*

# Keep the default constructor of the data classes
-keepclassmembers class com.github.droidworksstudio.launcher.helper.weather.** {
    public <init>(...);
}
