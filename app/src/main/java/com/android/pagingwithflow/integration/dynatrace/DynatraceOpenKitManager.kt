package com.android.pagingwithflow.integration.dynatrace

import android.annotation.SuppressLint
import android.app.ApplicationErrorReport
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.android.pagingwithflow.network.NetworkingConstants
import com.dynatrace.openkit.DynatraceOpenKitBuilder
import com.dynatrace.openkit.api.*
import com.dynatrace.openkit.CrashReportingLevel
import com.dynatrace.openkit.DataCollectionLevel
import com.dynatrace.openkit.util.json.objects.JSONValue

object DynatraceOpenKitManager {

    private const val ENDPOINT_URL = NetworkingConstants.ENDPOINT_URL
    private const val APP_ID = NetworkingConstants.APP_ID

    @Volatile private var openKit: OpenKit? = null
    @Volatile private var session: Session? = null
    @Volatile private var currentAction: Action? = null

    private val lock = Any()

    @SuppressLint("HardwareIds")
    fun initialize(context: Context) {

        if (openKit != null) {
            return
        }

        synchronized(lock) {
            if (openKit == null) {
                val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                val deviceId = androidId.hashCode().toLong()

                val osVersion = "Android ${Build.VERSION.RELEASE}"
                val manufacturer = Build.MANUFACTURER ?: "Unknown"
                val model = Build.MODEL ?: "Unknown"

                openKit = DynatraceOpenKitBuilder(ENDPOINT_URL, APP_ID, deviceId)
                    .withApplicationVersion("1.0")
                    .withOperatingSystem(osVersion)
                    .withManufacturer(manufacturer)
                    .withModelID(model)
//                    .withLogLevel(LogLevel.DEBUG)
                    .withCrashReportingLevel(CrashReportingLevel.OPT_IN_CRASHES)
                    .withDataCollectionLevel(DataCollectionLevel.USER_BEHAVIOR)
                    .build()
            }
        }
    }

    fun startSession(clientIP: String? = null) {
        if (session == null) {
            // O IP do cliente é opcional, mas útil
            session = if (clientIP != null) {
                openKit?.createSession(clientIP)
            } else {
                openKit?.createSession()
            }
        }
    }

    fun identifyUser(userId: String) {
        session?.identifyUser(userId)
    }

    fun enterAction(actionName: String) {
        // Finaliza ação anterior se uma nova for iniciada sem fechar a antiga
        currentAction?.leaveAction()
        currentAction = session?.enterAction(actionName)
    }


    fun reportEvent(eventName: String) {
        Log.d("OPENKIT", "Reportando evento: $eventName")
        currentAction?.reportEvent(eventName)
    }

    fun reportError(errorName: String, throwable: Throwable) {
        Log.d("OPENKIT", "Reportando reportError: $errorName")
        currentAction?.reportError(errorName, throwable)
        session?.reportCrash(throwable)
    }

    fun leaveAction() {
        currentAction?.leaveAction()
        currentAction = null
    }

    /**
     * Retorna um tracer para uma requisição web.
     * O código que faz a chamada de rede é responsável por chamar start() e stop().
     */
    fun traceWebRequest(url: String): WebRequestTracer? {
        return session?.traceWebRequest(url)
    }

    fun endSession() {
        session?.end()
        session = null
    }

    fun reportCrash(errorName: String, reason: String, stacktrace: String){
        Log.d("OPENKIT", "Reportando reportCrash: $errorName")
        getSession()?.reportCrash(errorName, reason, stacktrace)
        session?.reportCrash(errorName, reason, stacktrace)
    }

    fun getSession(): Session? {
        return session
    }

    fun shutdown() {
        openKit?.shutdown()
        openKit = null
    }

    fun sendBusEvent(data: String, attributes: Map<String?, JSONValue?>) {
        session?.sendBizEvent(data, attributes)
    }
}