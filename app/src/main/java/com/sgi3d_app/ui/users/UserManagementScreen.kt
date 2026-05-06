package com.sgi3d_app.ui.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    token: String,
    onBack: () -> Unit,
    onNavigateToCreateUser: () -> Unit
) {

    val viewModel: UserViewModel = viewModel()

    var userToDelete by remember {
        mutableStateOf<Int?>(null)
    }

    // 🔄 Charger utilisateurs au démarrage
    LaunchedEffect(Unit) {
        viewModel.fetchUsers(token)
    }

    Scaffold(

        // 🎯 Barre identique à "Mon compte"
        topBar = {

            TopAppBar(

                title = {
                    Text("Gestion des comptes")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )

                    }

                }

            )

        }

    ) { padding ->

        Column(

            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)

        ) {

            // 🔄 Actualiser utilisateurs
            Button(

                onClick = {
                    viewModel.fetchUsers(token)
                },

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(12.dp)

            ) {

                Text("🔄 Actualiser utilisateurs")

            }

            Spacer(modifier = Modifier.height(14.dp))

            // ➕ Ajouter utilisateur
            Button(

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007311)
                ),

                onClick = {
                    onNavigateToCreateUser()
                },

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(12.dp)

            ) {

                Text("➕ Ajouter utilisateur")

            }

            Spacer(modifier = Modifier.height(16.dp))

            // 📄 Liste utilisateurs
            LazyColumn {

                items(viewModel.users) { user ->

                    val statutText =
                        if (user.actif == 1)
                            "🟢 Actif"
                        else
                            "🔴 Inactif"

                    val statutColor =
                        if (user.actif == 1)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error

                    Card(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),

                        shape = RoundedCornerShape(12.dp)

                    ) {

                        Column(

                            modifier = Modifier.padding(12.dp)

                        ) {

                            Text(
                                text = "👤 ${user.nom}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "📧 ${user.email}"
                            )

                            Text(
                                text = "🎭 Rôle : ${user.role}"
                            )

                            Text(
                                text = statutText,
                                color = statutColor
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 🔄 Activer / Désactiver
                            Button(

                                onClick = {

                                    println(
                                        "CLICK toggle user ${user.id}"
                                    )

                                    val newStatus =
                                        if (user.actif == 1)
                                            0
                                        else
                                            1

                                    viewModel.toggleUserStatus(
                                        token,
                                        user.id,
                                        newStatus
                                    )

                                },

                                modifier = Modifier.fillMaxWidth(),

                                colors =
                                    if (user.actif == 1)
                                        ButtonDefaults.buttonColors(
                                            containerColor =
                                                MaterialTheme.colorScheme.error
                                        )
                                    else
                                        ButtonDefaults.buttonColors(
                                            containerColor =
                                                MaterialTheme.colorScheme.primary
                                        )

                            ) {

                                if (user.actif == 1) {

                                    Text("⛔ Désactiver compte")

                                } else {

                                    Text("✅ Activer compte")

                                }

                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // 🗑 Supprimer utilisateur
                            Button(

                                onClick = {
                                    userToDelete = user.id
                                },

                                modifier = Modifier.fillMaxWidth(),

                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8B0000)
                                )

                            ) {

                                Text("🗑 Supprimer utilisateur")

                            }

                        }

                    }

                }

            }

        }

    }

    // 🧾 Confirmation suppression
    if (userToDelete != null) {

        AlertDialog(

            onDismissRequest = {
                userToDelete = null
            },

            title = {
                Text("Confirmation")
            },

            text = {

                Text(
                    "Voulez-vous vraiment supprimer cet utilisateur ?"
                )

            },

            confirmButton = {

                Button(

                    onClick = {

                        viewModel.deleteUser(
                            token,
                            userToDelete!!
                        )

                        userToDelete = null

                    }

                ) {

                    Text("Supprimer")

                }

            },

            dismissButton = {

                Button(

                    onClick = {
                        userToDelete = null
                    }

                ) {

                    Text("Annuler")

                }

            }

        )

    }

}