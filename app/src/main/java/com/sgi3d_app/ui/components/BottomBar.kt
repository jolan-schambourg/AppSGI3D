package com.sgi3d_app.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.History

@Composable
fun BottomBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {

    NavigationBar {

        // 🏠 Accueil
        NavigationBarItem(
            selected = selectedItem == "printers",
            onClick = { onItemSelected("printers") },
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Accueil"
                )
            },
            label = { Text("Accueil") }
        )

        // 📄 Demandes
        NavigationBarItem(
            selected = selectedItem == "requests",
            onClick = { onItemSelected("requests") },
            icon = {
                Icon(
                    Icons.Default.List,
                    contentDescription = "Demandes"
                )
            },
            label = { Text("Demandes") }
        )

        // 📜 Historique
        NavigationBarItem(
            selected = selectedItem == "historique",
            onClick = { onItemSelected("historique") },
            icon = {
                Icon(
                    Icons.Default.History,
                    contentDescription = "Historique"
                )
            },
            label = { Text("Historique") }
        )

        // ⚙️ Paramètres
        NavigationBarItem(
            selected = selectedItem == "settings",
            onClick = { onItemSelected("settings") },
            icon = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Paramètres"
                )
            },
            label = { Text("Paramètres") }
        )

    }

}