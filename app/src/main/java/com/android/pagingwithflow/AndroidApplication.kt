package com.android.pagingwithflow

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.pagingwithflow.integration.dynatrace.DynatraceOpenKitManager

@HiltAndroidApp
class AndroidApplication : Application(), DefaultLifecycleObserver {
    override fun onCreate() {
        super<Application>.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        DynatraceOpenKitManager.initialize(this)

        Log.d("OPENKIT", "onCreate: iniciado")
    }

    /**
     * Chamado quando o app inteiro entra em primeiro plano (foreground).
     */
    override fun onStart(owner: LifecycleOwner) {

        DynatraceOpenKitManager.startSession()

        Log.d("OPENKIT", "onStart: iniciado")
    }

    /**
     * Chamado quando o app inteiro entra em segundo plano (background).
     */
    override fun onStop(owner: LifecycleOwner) {

        DynatraceOpenKitManager.endSession()

        Log.d("OPENKIT", "onStop: finalizado dados enviados")
    }

    /**
     * Chamado em emuladores ou situações raras. É a última chance de enviar dados.
     */
    override fun onTerminate() {

        DynatraceOpenKitManager.shutdown()

        Log.d("OPENKIT", "onTerminate: finalizado dados enviados")

        super.onTerminate()
    }
}