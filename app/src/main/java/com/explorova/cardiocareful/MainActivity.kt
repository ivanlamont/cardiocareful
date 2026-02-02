package com.explorova.cardiocareful

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.explorova.cardiocareful.engine.MonitorData
import com.explorova.cardiocareful.presentation.cardioCarefulApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val healthServicesRepository = (application as MainApplication).healthServicesRepository
        MonitorData(healthServicesRepository, applicationContext)

        setContent {
            cardioCarefulApp(healthServicesRepository = healthServicesRepository)
        }
    }
}
