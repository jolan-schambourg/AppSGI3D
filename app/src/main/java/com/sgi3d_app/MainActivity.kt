package com.sgi3d_app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.sgi3d_app.ui.navigation.AppNavigation
import com.sgi3d_app.ui.utils.requestNotificationPermission

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔔 Permission notif
        requestNotificationPermission(this)




        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }



}