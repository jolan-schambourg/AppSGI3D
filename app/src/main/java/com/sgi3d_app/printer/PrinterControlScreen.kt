package com.sgi3d_app.ui.printer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.sgi3d_app.data.remote.sendMoveCommand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterControlScreen(

    printerIp: String,
    apiKey: String,

    onBack: () -> Unit

) {

    var stepSize by remember {
        mutableStateOf(5)
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Contrôle imprimante")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )

                    }

                }

            )

        }

    ) { padding ->

        Column(

            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Text("Pas de déplacement")

            Row {

                listOf(1, 5, 10).forEach {

                    FilterChip(

                        selected =
                            stepSize == it,

                        onClick = {

                            stepSize = it

                        },

                        label = {

                            Text("${it} mm")

                        },

                        modifier =
                            Modifier.padding(4.dp)

                    )

                }

            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            Text("Déplacements")

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Button(
                onClick = {

                    sendMoveCommand(
                        printerIp,
                        apiKey,
                        "Y",
                        stepSize
                    )

                }
            ) {

                Text("Y +")

            }

            Row {

                Button(
                    onClick = {

                        sendMoveCommand(
                            printerIp,
                            apiKey,
                            "X",
                            -stepSize
                        )

                    }
                ) {

                    Text("X -")

                }

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Button(
                    onClick = {

                        sendMoveCommand(
                            printerIp,
                            apiKey,
                            "X",
                            stepSize
                        )

                    }
                ) {

                    Text("X +")

                }

            }

            Button(
                onClick = {

                    sendMoveCommand(
                        printerIp,
                        apiKey,
                        "Y",
                        -stepSize
                    )

                }
            ) {

                Text("Y -")

            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            Text("Axe Z")

            Row {

                Button(
                    onClick = {

                        sendMoveCommand(
                            printerIp,
                            apiKey,
                            "Z",
                            stepSize
                        )

                    }
                ) {

                    Text("Z +")

                }

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Button(
                    onClick = {

                        sendMoveCommand(
                            printerIp,
                            apiKey,
                            "Z",
                            -stepSize
                        )

                    }
                ) {

                    Text("Z -")

                }

            }

        }

    }

}