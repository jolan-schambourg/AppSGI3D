package com.sgi3d_app.ui.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(
    token: String,
    onBack: () -> Unit
) {

    val viewModel: UserViewModel = viewModel()

    var nom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("operateur") }

    var expanded by remember {
        mutableStateOf(false)
    }

    val success by viewModel.createSuccess

    // 🔙 Retour automatique si succès
    LaunchedEffect(success) {

        if (success) {

            viewModel.resetCreateSuccess()

            onBack()

        }

    }

    Scaffold(

        // 🎯 Même barre que les autres écrans
        topBar = {

            TopAppBar(

                title = {
                    Text("Créer un utilisateur")
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

            // 📝 Nom
            OutlinedTextField(

                value = nom,

                onValueChange = {
                    nom = it
                },

                label = {
                    Text("Nom")
                },

                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(12.dp))

            // 📧 Email
            OutlinedTextField(

                value = email,

                onValueChange = {
                    email = it
                },

                label = {
                    Text("Email")
                },

                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔑 Mot de passe
            OutlinedTextField(

                value = password,

                onValueChange = {
                    password = it
                },

                label = {
                    Text("Mot de passe")
                },

                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🎭 Rôle
            ExposedDropdownMenuBox(

                expanded = expanded,

                onExpandedChange = {
                    expanded = !expanded
                }

            ) {

                OutlinedTextField(

                    value = role,

                    onValueChange = {},

                    readOnly = true,

                    label = {
                        Text("Rôle")
                    },

                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()

                )

                ExposedDropdownMenu(

                    expanded = expanded,

                    onDismissRequest = {
                        expanded = false
                    }

                ) {

                    DropdownMenuItem(

                        text = {
                            Text("operateur")
                        },

                        onClick = {

                            role = "operateur"

                            expanded = false

                        }

                    )

                    DropdownMenuItem(

                        text = {
                            Text("admin")
                        },

                        onClick = {

                            role = "admin"

                            expanded = false

                        }

                    )

                }

            }

            Spacer(modifier = Modifier.height(24.dp))

            // ➕ Créer utilisateur
            Button(

                onClick = {

                    viewModel.createUser(
                        token,
                        nom,
                        email,
                        password,
                        role
                    )

                },

                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007311)
                ),
                shape = RoundedCornerShape(12.dp)

            ) {

                Text("➕ Créer utilisateur")

            }

        }

    }

}