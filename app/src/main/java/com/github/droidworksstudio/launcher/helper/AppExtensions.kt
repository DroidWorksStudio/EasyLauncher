package com.github.droidworksstudio.launcher.helper

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.github.droidworksstudio.ktx.openUrl
import com.github.droidworksstudio.launcher.Constants
import java.io.File
import java.io.IOException

fun Context.searchOnPlayStore(query: String? = null): Boolean {
    return try {
        val playStoreIntent = Intent(Intent.ACTION_VIEW)
        playStoreIntent.data = Uri.parse("${Constants.APP_GOOGLE_PLAY_STORE}=$query")

        // Check if the Play Store app is installed
        if (playStoreIntent.resolveActivity(packageManager) != null) {
            startActivity(playStoreIntent)
        } else {
            // If Play Store app is not installed, open Play Store website in browser
            playStoreIntent.data = Uri.parse("${Constants.URL_GOOGLE_PLAY_STORE}=$query")
            startActivity(playStoreIntent)
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun Context.searchCustomSearchEngine(searchQuery: String? = null): Boolean {
    val searchUrl = Constants.URL_GOOGLE_SEARCH
    val encodedQuery = Uri.encode(searchQuery)
    val fullUrl = "$searchUrl$encodedQuery"
    Log.d("fullUrl", fullUrl)
    openUrl(fullUrl)
    return true
}

fun Context.backupSharedPreferences(backupFileName: String) {
    val sharedPreferences: SharedPreferences =
        this.getSharedPreferences(Constants.PREFS_FILENAME, 0)
    val allPrefs = sharedPreferences.all
    val backupFile = File(filesDir, backupFileName)

    println("Backup SharedPreferences to: ${backupFile.absolutePath}")

    try {
        backupFile.bufferedWriter().use { writer ->
            for ((key, value) in allPrefs) {
                if (value != null) {
                    val line = when (value) {
                        is Boolean -> "$key=${value}\n"
                        is Int -> "$key=${value}\n"
                        is Float -> "$key=${value}\n"
                        is Long -> "$key=${value}\n"
                        is String -> "$key=${value}\n"
                        is Set<*> -> "$key=${value.joinToString(",")}\n"
                        else -> null
                    }
                    if (line != null) {
                        writer.write(line)
                        println("Writing: $line")
                    } else {
                        println("Skipping unsupported type for key: $key")
                    }
                } else {
                    println("Null value for key: $key")
                }
            }
        }
        println("Backup completed successfully.")
    } catch (e: IOException) {
        e.printStackTrace()
        println("Failed to backup SharedPreferences: ${e.message}")
    }
}

fun Context.restoreSharedPreferences(backupFileName: String) {
    val sharedPreferences: SharedPreferences =
        this.getSharedPreferences(Constants.PREFS_FILENAME, 0)
    val editor = sharedPreferences.edit()
    val backupFile = File(filesDir, backupFileName)

    println("Restoring SharedPreferences from: ${backupFile.absolutePath}")

    if (backupFile.exists()) {
        try {
            backupFile.forEachLine { line ->
                val (key, value) = line.split("=", limit = 2)
                when {
                    value.toBooleanStrictOrNull() != null -> editor.putBoolean(
                        key,
                        value.toBoolean()
                    )

                    value.toIntOrNull() != null -> editor.putInt(key, value.toInt())
                    value.toFloatOrNull() != null -> editor.putFloat(key, value.toFloat())
                    value.toLongOrNull() != null -> editor.putLong(key, value.toLong())
                    value.contains(",") -> editor.putStringSet(key, value.split(",").toSet())
                    else -> editor.putString(key, value)
                }
                println("Restoring: $key=$value")
            }
            editor.apply()
            println("Restore completed successfully.")
        } catch (e: IOException) {
            e.printStackTrace()
            println("Failed to restore SharedPreferences: ${e.message}")
        }
    } else {
        println("Backup file does not exist.")
    }
}