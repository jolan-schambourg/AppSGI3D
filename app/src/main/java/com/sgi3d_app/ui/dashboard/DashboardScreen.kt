package com.sgi3d_app.ui.dashboard

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

import androidx.compose.ui.platform.LocalContext

import kotlinx.coroutines.delay

import com.sgi3d_app.ui.components.BottomBar
import com.sgi3d_app.ui.components.DemandeCardSGI3D

import com.sgi3d_app.ui.requests.RequestDetailScreen
import com.sgi3d_app.ui.requests.RequestViewModel

import com.sgi3d_app.ui.printer.PrinterControlScreen

import com.sgi3d_app.data.model.Printer
import com.sgi3d_app.ui.historique.HistoriqueScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    role: String,
    token: String,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onOpenProfile: () -> Unit,
    navController: NavHostController,
) {

    val profileViewModel: ProfileViewModel = viewModel()
    val printerViewModel: PrinterViewModel = viewModel()
    val requestViewModel: RequestViewModel = viewModel()

    var selectedTab by remember { mutableStateOf("printers") }
    var showDetails by remember { mutableStateOf(false) }
    var selectedPrinter by remember { mutableStateOf<Printer?>(null) }

    var selectedRequest by remember {
        mutableStateOf<com.sgi3d_app.data.remote.DemandeItem?>(null)
    }

    var demandeFilter by remember { mutableStateOf("EN_ATTENTE") }

    val context = LocalContext.current

    val userNom = profileViewModel.profile?.nom ?: ""
    val userEmail = profileViewModel.profile?.email ?: ""

    val isAdmin = role == "admin"
    val isOperateur = role == "operateur"
    val isEtudiant = role == "etudiant"


    // ===============================
    // 🔄 INIT
    // ===============================
    LaunchedEffect(Unit) {
        if (token.isNotBlank()) {
            profileViewModel.loadProfile(token)
            printerViewModel.fetchPrinters(token)
        }

        while (true) {
            printerViewModel.fetchPrinters(token)
            delay(5000)
        }
    }

    LaunchedEffect(userEmail) {
        if (userEmail.isNotBlank()) {
            while (true) {
                requestViewModel.fetchDemandes(context, userEmail, role)
                delay(5000)
            }
        }
    }

    // ===============================
    // UI
    // ===============================
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Imprimantes 3D - $role") })
        },
        bottomBar = {
            BottomBar(
                selectedItem = selectedTab,
                onItemSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            when (selectedTab) {

                // ==================================
                // 🖨 PRINTERS
                // ==================================
                "printers" -> {

                    if (showDetails && selectedPrinter != null) {

                        PrinterDetailsScreen(
                            name = selectedPrinter!!.nom,
                            volume = "220x220x250 mm",
                            materials = selectedPrinter!!.materiau ?: "",
                            location = selectedPrinter!!.localisation ?: "",
                            description = selectedPrinter!!.description?: "",
                            onBack = {
                                showDetails = false
                                selectedPrinter = null
                            }
                        )

                    } else {

                        LazyColumn {
                            items(printerViewModel.printers) { printer ->
                                Log.d("PRINTER_TYPE", "Printer: ${printer.nom} | Type: ${printer.type}")
                                val state = printerViewModel.printerStates[printer.id] ?: PrinterViewModel.PrinterState()
                                // 🔥 IMPORTANT : lancer récupération dynamique
                                LaunchedEffect(printer.id) {
                                    while (true) {
                                        printerViewModel.fetchPrinterDataDynamic(printer)

                                        printerViewModel.fetchJobProgress(printer)

                                        kotlinx.coroutines.delay(5000)
                                    }
                                }

                                PrinterCard(
                                    printerName = printer.nom,
                                    status = state.status,
                                    temperature = "Buse: ${state.nozzleTemp} | Lit: ${state.bedTemp}",
                                    timeRemaining = state.timeRemaining,
                                    progress = state.progress,
                                    printerIp = printer.ip ?: "",
                                    apiKey = printer.api_key ?: "",

                                    onPause = { printerViewModel.pausePrint(printer) },
                                    onCancel = { printerViewModel.cancelPrint(printer) },

                                    onMoreInfo = {
                                        selectedPrinter = printer
                                        showDetails = true
                                    },

                                    onControlClick = { _, _ ->
                                        selectedPrinter = printer
                                        selectedTab = "control"
                                    },

                                    canControl = isAdmin || isOperateur
                                )
                            }
                        }
                    }
                }

                // ==================================
                // 🎮 CONTROL
                // ==================================
                "control" -> {

                    selectedPrinter?.let { printer ->
                        PrinterControlScreen(
                            printerIp = printer.ip,
                            apiKey = printer.api_key?: "",
                            onBack = { selectedTab = "printers" }
                        )
                    }
                }

                // ==================================
                // 📄 REQUESTS (inchangé)
                // ==================================
                "requests" -> {

                    if (selectedRequest != null) {

                        RequestDetailScreen(
                            demande = selectedRequest!!,
                            onBack = { selectedRequest = null },

                            onAccept = { commentaire ->
                                requestViewModel.accepterDemande(
                                    selectedRequest!!.id,
                                    userNom,
                                    userEmail,
                                    commentaire,
                                    context,
                                    role
                                )
                                selectedRequest = null
                            },

                            onRefuse = { commentaire ->
                                requestViewModel.refuserDemande(
                                    selectedRequest!!.id,
                                    userNom,
                                    userEmail,
                                    commentaire,
                                    context,
                                    role
                                )
                                selectedRequest = null
                            }
                        )

                    } else {

                        Column {

                            Button(
                                onClick = {
                                    navController.navigate("new_request/$userNom/$userEmail/$role")
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("+ Nouvelle demande")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            LazyColumn {
                                items(requestViewModel.demandes) { demande ->

                                    DemandeCardSGI3D(
                                        demande = demande,
                                        onValider = {
                                            requestViewModel.accepterDemande(
                                                demande.id,
                                                userNom,
                                                userEmail,
                                                "Acceptée",
                                                context,
                                                role
                                            )
                                        },
                                        onRefuser = {
                                            requestViewModel.refuserDemande(
                                                demande.id,
                                                userNom,
                                                userEmail,
                                                "Refus",
                                                context,
                                                role
                                            )
                                        },
                                        onDetail = {
                                            selectedRequest = demande
                                        },
                                        isAdmin = isAdmin || isOperateur
                                    )
                                }
                            }
                        }
                    }
                }

                // ==================================
                // 📜 HISTORIQUE
                // ==================================
                                "historique" -> {
                                    HistoriqueScreen(
                                        email = userEmail,
                                        isEtudiant = isEtudiant,
                                        token = token
                                    )
                                }
                // ==================================
                // ⚙️ SETTINGS
                // ==================================
                "settings" -> {
                    SettingsScreen(
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = onToggleDarkMode,
                        onLogout = onLogout,
                        onOpenProfile = onOpenProfile,
                        role = role,
                        token = token,
                        onNavigateToCreateUser = {
                            navController.navigate("user_management/$token")
                        },
                        onChangePassword = {
                            navController.navigate("change_password/$token")
                        }
                    )
                }
            }
        }
    }
}