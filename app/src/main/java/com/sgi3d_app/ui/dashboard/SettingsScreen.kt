package com.sgi3d_app.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// 🔥 import écran gestion users
import com.sgi3d_app.ui.users.UserManagementScreen

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onOpenProfile: () -> Unit,
    onNavigateToCreateUser: () -> Unit,
    onChangePassword: () -> Unit,

    // 🔥 nouveau
    role: String,
    token: String
) {



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Paramètres",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Mon compte ---
        Button(
            onClick = { onOpenProfile() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),

            shape = RoundedCornerShape(12.dp)
        ) {

            Text("Mon compte")

        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Modifier mot de passe ---
        Button(
            onClick = {
                onChangePassword()
                /* futur écran */
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),

            shape = RoundedCornerShape(12.dp)
        ) {

            Text("Modifier le mot de passe")

        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔐 Visible seulement pour admin
        if (role == "admin") {

            Button(
                onClick = {
                        onNavigateToCreateUser()
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),

                shape = RoundedCornerShape(12.dp)
            ) {

                Text("👥 Gérer les comptes")

            }

            Spacer(modifier = Modifier.height(32.dp))

        }

        // --- Mode sombre ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),

            horizontalArrangement = Arrangement.SpaceBetween,

            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Mode sombre",
                style = MaterialTheme.typography.titleMedium
            )

            Switch(
                checked = isDarkMode,
                onCheckedChange = {
                    onToggleDarkMode(it)
                }
            )

        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- Déconnexion ---
        Button(
            onClick = {
                onLogout()
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),

            shape = RoundedCornerShape(12.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor =
                    MaterialTheme.colorScheme.error
            )
        ) {

            Text("Se déconnecter")

        }

    }

}