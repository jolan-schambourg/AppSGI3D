package com.sgi3d_app.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.sgi3d_app.network.ApiClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    token: String,
    onBack: () -> Unit
) {

    /////////////////////////////////////////////////////////
    // 🔐 États
    /////////////////////////////////////////////////////////

    var showDialog by remember { mutableStateOf(false) }

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var message by remember { mutableStateOf("") }

    var oldPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    /////////////////////////////////////////////////////////
    // UI
    /////////////////////////////////////////////////////////

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Modifier mot de passe")
                },

                navigationIcon = {

                    IconButton(onClick = onBack) {

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

            Spacer(modifier = Modifier.height(16.dp))

            /////////////////////////////////////////////////////////
            // 🔐 Ancien mot de passe
            /////////////////////////////////////////////////////////

            OutlinedTextField(

                value = oldPassword,

                onValueChange = {
                    oldPassword = it
                },

                label = {
                    Text("Ancien mot de passe")
                },

                visualTransformation =
                    if (oldPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),

                trailingIcon = {

                    val icon =
                        if (oldPasswordVisible)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility

                    IconButton(
                        onClick = {
                            oldPasswordVisible =
                                !oldPasswordVisible
                        }
                    ) {

                        Icon(
                            imageVector = icon,
                            contentDescription = "Afficher/Masquer"
                        )

                    }

                },

                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(12.dp))

            /////////////////////////////////////////////////////////
            // 🔐 Nouveau mot de passe
            /////////////////////////////////////////////////////////

            OutlinedTextField(

                value = newPassword,

                onValueChange = {
                    newPassword = it
                },

                label = {
                    Text("Nouveau mot de passe")
                },

                visualTransformation =
                    if (newPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),

                trailingIcon = {

                    val icon =
                        if (newPasswordVisible)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility

                    IconButton(
                        onClick = {
                            newPasswordVisible =
                                !newPasswordVisible
                        }
                    ) {

                        Icon(
                            imageVector = icon,
                            contentDescription = "Afficher/Masquer"
                        )

                    }

                },

                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(12.dp))

            /////////////////////////////////////////////////////////
            // 🔐 Confirmation mot de passe
            /////////////////////////////////////////////////////////

            OutlinedTextField(

                value = confirmPassword,

                onValueChange = {
                    confirmPassword = it
                },

                label = {
                    Text("Confirmer mot de passe")
                },

                visualTransformation =
                    if (confirmPasswordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),

                trailingIcon = {

                    val icon =
                        if (confirmPasswordVisible)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility

                    IconButton(
                        onClick = {
                            confirmPasswordVisible =
                                !confirmPasswordVisible
                        }
                    ) {

                        Icon(
                            imageVector = icon,
                            contentDescription = "Afficher/Masquer"
                        )

                    }

                },

                modifier = Modifier.fillMaxWidth()

            )

            Spacer(modifier = Modifier.height(24.dp))

            /////////////////////////////////////////////////////////
            // 🚨 Bouton Modifier
            /////////////////////////////////////////////////////////

            Button(

                onClick = {

                    /////////////////////////////////////////////////////////
                    // Vérifications
                    /////////////////////////////////////////////////////////

                    if (newPassword != confirmPassword) {

                        message =
                            "❌ Les mots de passe ne correspondent pas"

                        return@Button
                    }

                    if (newPassword.length < 6) {

                        message =
                            "❌ Mot de passe trop court (min 6 caractères)"

                        return@Button
                    }

                    /////////////////////////////////////////////////////////
                    // 🔐 Ouvrir popup confirmation
                    /////////////////////////////////////////////////////////

                    showDialog = true

                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)

            ) {

                Text("Modifier mot de passe")

            }

            Spacer(modifier = Modifier.height(16.dp))

            /////////////////////////////////////////////////////////
            // 🧾 Message retour
            /////////////////////////////////////////////////////////

            if (message.isNotEmpty()) {

                Text(

                    text = message,

                    color =
                        if (message.contains("❌"))
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary

                )

            }

            /////////////////////////////////////////////////////////
            // 🔐 Popup confirmation
            /////////////////////////////////////////////////////////

            if (showDialog) {

                AlertDialog(

                    onDismissRequest = {
                        showDialog = false
                    },

                    title = {
                        Text("Confirmation")
                    },

                    text = {
                        Text(
                            "Voulez-vous vraiment changer le mot de passe ?"
                        )
                    },

                    confirmButton = {

                        TextButton(

                            onClick = {

                                showDialog = false

                                /////////////////////////////////////////////////////////
                                // 🔥 Appel API
                                /////////////////////////////////////////////////////////

                                ApiClient.changePassword(
                                    token,
                                    oldPassword,
                                    newPassword
                                ) { success, msg ->

                                    /////////////////////////////////////////////////////////
                                    // 🔥 Revenir sur le thread principal
                                    /////////////////////////////////////////////////////////



                                        message = msg

                                        /////////////////////////////////////////////////////////
                                        // 🚪 Fermer écran si succès
                                        /////////////////////////////////////////////////////////

                                        if (success) {

                                            oldPassword = ""
                                            newPassword = ""
                                            confirmPassword = ""

                                            onBack()

                                        }

                                    }



                            }

                        ) {

                            Text("Oui")

                        }

                    },

                    dismissButton = {

                        TextButton(

                            onClick = {
                                showDialog = false
                            }

                        ) {

                            Text("Annuler")

                        }

                    }

                )

            }

        }

    }

}