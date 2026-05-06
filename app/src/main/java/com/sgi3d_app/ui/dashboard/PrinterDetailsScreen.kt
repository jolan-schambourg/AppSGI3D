package com.sgi3d_app.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PrinterDetailsScreen(
    name: String,
    volume: String,
    materials: String,
    location: String,
    description: String,
    onBack: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            // 🔙 Bouton retour
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("← Retour")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Informations imprimante",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Nom : $name")
            Spacer(modifier = Modifier.height(8.dp))

            Text("Volume : $volume")
            Spacer(modifier = Modifier.height(8.dp))

            Text("Matériaux compatibles : $materials")
            Spacer(modifier = Modifier.height(8.dp))

            Text("Localisation : $location")
            Spacer(modifier = Modifier.height(8.dp))

            Text("Description :")
            Spacer(modifier = Modifier.height(4.dp))

            Text(description)

        }

    }

}