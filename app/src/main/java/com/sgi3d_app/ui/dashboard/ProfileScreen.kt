package com.sgi3d_app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    role: String,
    token: String,
    onBack: () -> Unit
) {

    val viewModel: ProfileViewModel = viewModel()

    LaunchedEffect(token) {

        if (token.isNotBlank()) {

            viewModel.loadProfile(token)

        }

    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Mon compte")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,
                            contentDescription =
                                "Retour"
                        )

                    }

                }

            )

        }

    ) { padding ->

        Box(

            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)

        ) {

            when {

                viewModel.isLoading -> {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.align(
                                Alignment.Center
                            )
                    )

                }

                viewModel.errorMessage != null -> {

                    Text(
                        "Erreur : ${viewModel.errorMessage}",
                        color =
                            MaterialTheme
                                .colorScheme.error
                    )

                }

                viewModel.profile != null -> {

                    val p =
                        viewModel.profile!!

                    Column(

                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        modifier =
                            Modifier.fillMaxWidth()

                    ) {

                        // Avatar

                        Box(

                            modifier =
                                Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme
                                            .colorScheme.primary
                                    ),

                            contentAlignment =
                                Alignment.Center

                        ) {

                            Text(

                                text =
                                    p.avatar ?: "U",

                                color =
                                    Color.White,

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleLarge

                            )

                        }

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        Text(

                            text =
                                p.nom,

                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge

                        )

                        Spacer(
                            modifier =
                                Modifier.height(24.dp)
                        )

                        Card(

                            shape =
                                RoundedCornerShape(16.dp),

                            modifier =
                                Modifier.fillMaxWidth()

                        ) {

                            Column(

                                modifier =
                                    Modifier.padding(16.dp)

                            ) {

                                Text(
                                    "Email : ${p.email}"
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(8.dp)
                                )

                                Text(
                                    "Rôle : ${p.role}"
                                )

                                Spacer(
                                    modifier =
                                        Modifier.height(8.dp)
                                )

                                Text(
                                    "Créé le : ${p.cree_le}"
                                )

                            }

                        }

                    }

                }

            }

        }

    }

}