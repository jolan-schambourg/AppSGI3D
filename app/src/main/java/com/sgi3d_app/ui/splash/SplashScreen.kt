package com.sgi3d_app.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.sgi3d_app.R

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit
) {

    LaunchedEffect(true) {
        delay(2500) // 2,5 secondes
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F0)), // blanc cassé
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo_lycee),
                contentDescription = "Logo Lycée",
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Projet SGI3D",
                fontSize = 22.sp,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "App Mobile",
                fontSize = 18.sp
            )
        }
    }
}