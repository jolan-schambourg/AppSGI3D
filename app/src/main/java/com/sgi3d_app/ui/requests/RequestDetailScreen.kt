package com.sgi3d_app.ui.requests

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.sgi3d_app.data.remote.DemandeItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailScreen(

    demande: DemandeItem,

    onBack: () -> Unit,

    onAccept: (String) -> Unit,

    onRefuse: (String) -> Unit

) {

    var commentaireAdmin by remember {
        mutableStateOf("")
    }

    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text("Détail demande")
                }
            )

        }

    ) { padding ->

        Column(

            modifier = Modifier
                .padding(padding)
                .padding(16.dp)

        ) {

            Text(
                text = "👤 Étudiant : ${demande.etudiant_nom}"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📧 Email : ${demande.etudiant_email}"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📄 Fichier : ${demande.fichier_nom}"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📝 Commentaire : ${demande.commentaire ?: "Aucun"}"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "📅 Date : ${demande.date_creation}"
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(

                value = commentaireAdmin,

                onValueChange = {
                    commentaireAdmin = it
                },

                label = {
                    Text("Commentaire admin (optionnel)")
                },

                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(24.dp))

            Row {

                Button(

                    onClick = {
                        onAccept(commentaireAdmin)
                    },

                    modifier = Modifier.weight(1f)

                ) {

                    Text("✅ Accepter")

                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(

                    onClick = {
                        onRefuse(commentaireAdmin)
                    },

                    modifier = Modifier.weight(1f),

                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            MaterialTheme.colorScheme.error
                    )

                ) {

                    Text("❌ Refuser")

                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(

                onClick = onBack,

                modifier = Modifier.fillMaxWidth()

            ) {

                Text("Retour")

            }

        }

    }

}