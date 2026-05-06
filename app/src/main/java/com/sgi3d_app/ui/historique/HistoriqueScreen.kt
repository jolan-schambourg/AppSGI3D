package com.sgi3d_app.ui.historique

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HistoriqueScreen(email: String,
                     isEtudiant: Boolean) {

    val viewModel: HistoriqueViewModel = viewModel()
    var searchText by remember {
        mutableStateOf("")
    }


    LaunchedEffect(Unit) {

        viewModel.fetchHistorique()

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        TextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            label = {
                if (isEtudiant) {
                    Text("📅 Filtrer par date (YYYY-MM-DD)")
                } else {
                    Text("🔎 Rechercher étudiant")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                viewModel.fetchHistorique()
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("🔄 Actualiser historique")

        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {

            val baseList = if (isEtudiant) {
                viewModel.historique.filter {
                    it.etudiant_email == email
                }
            } else {
                viewModel.historique
            }

            val filteredList = if (isEtudiant) {
                baseList.filter {
                    it.date_traitement.contains(searchText, ignoreCase = true)
                }
            } else {
                baseList.filter {
                    it.etudiant_nom.contains(searchText, ignoreCase = true) ||
                            it.fichier_nom.contains(searchText, ignoreCase = true)
                }
            }

            items(filteredList) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {

                        Text(
                            "👤 ${item.etudiant_nom}"
                        )

                        Text(
                            "📄 ${item.fichier_nom}"
                        )

                        val statutColor =
                            if (item.statut == "acceptee")
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error

                        Text(
                            text = "📌 Statut : ${item.statut}",
                            color = statutColor
                        )

                        Text(
                            "🧑 Admin : ${item.admin_nom}"
                        )

// 💬 Commentaire admin (si présent et pas rapide)

                        if (
                            item.commentaire_admin.isNotBlank()
                            && item.commentaire_admin != "Acceptée rapide"
                            && item.commentaire_admin != "Refus rapide"
                        ) {

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                "💬 Commentaire admin : ${item.commentaire_admin}"
                            )

                        }

                        Text(
                            "📅 ${item.date_traitement}"
                        )

                    }

                }

            }

        }

    }

}