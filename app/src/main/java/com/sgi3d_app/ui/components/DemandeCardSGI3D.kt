package com.sgi3d_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 🔥 IMPORTANT
import com.sgi3d_app.data.remote.DemandeItem

@Composable
fun DemandeCardSGI3D(

    demande: DemandeItem,

    onValider: () -> Unit,

    onRefuser: () -> Unit,

    onDetail: () -> Unit,

    isAdmin: Boolean = true



) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),

        shape = RoundedCornerShape(16.dp),

        elevation =
            CardDefaults.cardElevation(4.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            )

    ) {

        Column(

            modifier =
                Modifier.padding(16.dp)

        ) {

            Text(

                text =
                    "Demande n°${demande.id}",

                style =
                    MaterialTheme.typography.titleMedium,

                fontWeight =
                    FontWeight.SemiBold

            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            // 🔥 Champs API

            Text(
                "Étudiant : ${demande.etudiant_nom}"
            )

            Text(
                "Email : ${demande.etudiant_email}"
            )

            Text(
                "Fichier : ${demande.fichier_nom}"
            )

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            if (isAdmin) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onValider,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Valider")
                    }

                    Button(
                        onClick = onRefuser,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) {
                        Text("Refuser")
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                TextButton(

                    onClick = onDetail,

                    modifier =
                        Modifier.align(Alignment.End)

                ) {

                    Text("Voir détails")

                }
            }


        }

    }

}