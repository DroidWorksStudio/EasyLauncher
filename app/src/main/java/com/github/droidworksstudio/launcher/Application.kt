package com.github.droidworksstudio.launcher

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.acra.ACRA
import org.acra.ReportField
import org.acra.config.dialog
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra


@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        val pkgName = getString(R.string.app_name)
        val pkgVersion = this.packageManager.getPackageInfo(
            this.packageName,
            0
        ).versionName

        ACRA.DEV_LOGGING = true

        initAcra {
            //core configuration:
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.KEY_VALUE_LIST
            reportContent = listOf(
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL,
                ReportField.CUSTOM_DATA,
                ReportField.STACK_TRACE,
                ReportField.LOGCAT
            )
            //each plugin you chose above can be configured in a block like this:
            dialog {
                //required
                text = getString(R.string.acra_dialog_text).format(pkgName)
                //optional, enables the dialog title
                title = getString(R.string.acra_crash)
                //defaults to android.R.string.ok
                positiveButtonText = getString(R.string.acra_send_report)
                //defaults to android.R.string.cancel
                negativeButtonText = getString(R.string.acra_dont_send)
                //optional, defaults to @android:style/Theme.Dialog
                resTheme = R.style.MaterialDialogTheme
            }

            mailSender {
                //required
                mailTo = getString(R.string.acra_email)
                //defaults to true
                reportAsFile = true
                //defaults to ACRA-report.stacktrace
                reportFileName = "$pkgName-$pkgVersion-crash-report.ini"
                //defaults to "<applicationId> Crash Report"
                subject = "$pkgName $pkgVersion Crash Report"
                //defaults to empty
                body = getString(R.string.acra_mail_body)
            }
        }
    }
}