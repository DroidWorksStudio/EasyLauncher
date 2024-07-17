# Keep Gson specific annotations
-keepattributes Signature
-keepattributes *Annotation*

# Keep the default constructor of the data classes
-keepclassmembers class com.github.droidworksstudio.launcher.helper.weather.** {
    public <init>(...);
}

# Preserve generic type information used in Gson TypeToken
-keep class com.google.gson.reflect.TypeToken {*;}
-keepattributes Signature

# Preserve classes used by Gson
-keep class com.google.gson.** { *; }

# Preserve your package classes, replace 'my.package.name' with your actual package name
-keep class com.github.droidworksstudio.** { *; }

# Keep PreferenceHelper class if it uses Gson serialization/deserialization
-keep class com.github.droidworksstudio.launcher.helper.PreferenceHelper { *; }

# Keep the WeatherResponse and its nested classes
-keep class com.github.droidworksstudio.launcher.helper.weather.WeatherResponse { *; }
-keep class com.github.droidworksstudio.launcher.helper.weather.Wind { *; }
-keep class com.github.droidworksstudio.launcher.helper.weather.Main { *; }
-keep class com.github.droidworksstudio.launcher.helper.weather.Weather { *; }

# Keep all model classes and their fields that Gson might use for serialization/deserialization
-keep class com.github.droidworksstudio.launcher.helper.weather.** { *; }
