package com.github.droidworksstudio.launcher.helper

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.github.droidworksstudio.common.showLongToast
import com.github.droidworksstudio.launcher.BuildConfig
import com.github.droidworksstudio.launcher.utils.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException

class UpdateManagerHelper(private val fragment: Fragment) {
    private val sharedPreferences by lazy {
        fragment.requireContext().getSharedPreferences("update_prefs", Context.MODE_PRIVATE)
    }

    private val context = fragment.requireContext()
    private val activity = fragment.requireActivity()

    fun checkForUpdates() {
        val currentVersion = BuildConfig.VERSION_NAME
        val url = "https://api.github.com/repos/DroidWorksStudio/EasyLauncher/releases/latest"

        val request = Request.Builder().url(url).build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle the error
                Log.e("UpdateManager", "Failed to check for updates", e)
            }

            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val json = response.body()?.string()
                    val jsonObject = JSONObject(json.toString())
                    val tagName = jsonObject.getString("tag_name")
                    val latestVersion = tagName.replace("v", "")
                    val assets = jsonObject.getJSONArray("assets")

                    // Check if assets array has elements
                    if (assets.length() > 0) {
                        val apkUrl = (assets.get(1) as JSONObject).getString("browser_download_url")
                        Log.d("UpdateManager", "APK URL: $apkUrl | Latest version: $latestVersion | Current version: $currentVersion")

                        if (latestVersion > currentVersion) {
                            val declinedVersion = sharedPreferences.getString("declined_version", "")

                            Log.d("UpdateManager", "Declined version: $declinedVersion")

                            if (latestVersion != declinedVersion) {
                                // Ask the user if they want to update
                                activity.runOnUiThread {
                                    showUpdateDialog(latestVersion, apkUrl)
                                }
                            }
                        }
                    } else {
                        Log.e("UpdateManager", "Assets array is empty")
                    }
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showUpdateDialog(latestVersion: String, apkUrl: String) {
        MaterialAlertDialogBuilder(context).apply {
            setTitle("Update Available")
            setMessage("A new version of the app is available. Do you want to update?")
            setPositiveButton("Update") { _, _ ->
                downloadApk(apkUrl)
            }
            setNegativeButton("Later") { _, _ ->
                // Save the declined version
                with(sharedPreferences.edit()) {
                    putString("declined_version", latestVersion)
                    apply()
                }
            }
            setCancelable(false)
            show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun downloadApk(apkUrl: String) {
        val request = DownloadManager.Request(Uri.parse(apkUrl)).apply {
            setTitle("Downloading update")
            setDescription("Your app is downloading the latest update")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${Constants.PACKAGE_NAME}.apk")
        }

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)

        // Register a BroadcastReceiver to listen for completion of the download
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = manager.query(query)
                    if (cursor != null && cursor.moveToFirst()) {
                        val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if (statusIndex != -1) {
                            val status = cursor.getInt(statusIndex)
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                // Download completed successfully, now call installApk()
                                requestInstallPermission()
                            }
                        }
                    }
                    cursor?.close()
                }
            }
        }

        context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED)
    }

    private fun requestInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                @Suppress("DEPRECATION")
                fragment.startActivityForResult(intent, Constants.REQUEST_INSTALL_PERMISSION)
            } else {
                // Permission already granted, proceed with installation
                installApk()
            }
        } else {
            // For devices below Android Oreo, installation permission is granted by default
            installApk()
        }
    }

    private fun installApk() {
        val apkFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "${Constants.PACKAGE_NAME}.apk")
        val apkUri = FileProvider.getUriForFile(context.applicationContext, "${context.packageName}.provider", apkFile)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)
    }

    @Deprecated("Deprecated in Java")
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_INSTALL_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val canInstallPackages = context.packageManager.canRequestPackageInstalls()
                if (canInstallPackages) {
                    // Permission granted, proceed with installation
                    installApk()
                } else {
                    // Permission still not granted, handle accordingly
                    context.applicationContext.showLongToast("Please allow install permission to install.")
                }
            }
        }
    }
}