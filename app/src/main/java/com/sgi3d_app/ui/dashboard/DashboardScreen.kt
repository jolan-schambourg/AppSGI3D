package com.sgi3d_app.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

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
import com.sgi3d_app.ui.historique.HistoriqueScreen

import com.sgi3d_app.ui.requests.NewRequestScreen
import com.sgi3d_app.ui.requests.RequestDetailScreen
import com.sgi3d_app.ui.requests.RequestViewModel

import com.sgi3d_app.ui.printer.PrinterControlScreen

import android.content.Intent
import androidx.core.content.ContextCompat
import com.sgi3d_app.service.PrintForegroundService

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

    var selectedTab by remember {
        mutableStateOf("printers")
    }

    var showDetails by remember {
        mutableStateOf(false)
    }

    var showNewRequest by remember {
        mutableStateOf(false)
    }

    // 🔥 demande sélectionnée
    var selectedRequest by remember {
        mutableStateOf<com.sgi3d_app.data.remote.DemandeItem?>(null)
    }

    var demandeFilter by remember {
        mutableStateOf("EN_ATTENTE")
    }

    val apiKey =
        "GN2-MsGMr05YG0vUw-98MLiRZKFkXcYZrkvfeztDh-8"

    val printerIp =
        "192.168.0.32"

    // 🔥 ADMIN récupéré depuis backend



    val context = LocalContext.current
    var serviceStarted by remember { mutableStateOf(false) }

    // 🚀 LANCER LE SERVICE DE NOTIF
    fun startPrintService() {
        val intent = Intent(context, PrintForegroundService::class.java).apply {
            putExtra("apiKey", apiKey)
            putExtra("printerName", printerViewModel.printerName)
        }
        context.startForegroundService(intent)
    }

    val userNom =
        profileViewModel.profile?.nom ?: ""

    val userEmail =
        profileViewModel.profile?.email ?: ""

    LaunchedEffect(printerViewModel.printerName) {
        if (!serviceStarted && printerViewModel.printerName != "Chargement...") {
            startPrintService()
            serviceStarted = true
        }
    }

    LaunchedEffect(Unit) {

        if (token.isNotBlank()) {
            profileViewModel.loadProfile(token)
        }

        printerViewModel.fetchPrinterName(apiKey)
        printerViewModel.fetchFiles(apiKey)
        printerViewModel.fetchPrinterDetails(token)


        while (true) {
            printerViewModel.fetchPrinterData(apiKey)
            printerViewModel.fetchJobProgress(apiKey)
            delay(5000)
        }
    }

    LaunchedEffect(userEmail) {
        if (userEmail.isNotBlank()) {

            requestViewModel.fetchDemandes(context, userEmail, role, token)

            while (true) {
                requestViewModel.fetchDemandes(context, userEmail, role, token)
                delay(5000)
            }
        }
    }





    val isAdmin = role == "admin"
    val isOperateur = role == "operateur"
    val isEtudiant = role == "etudiant"

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        "Imprimantes 3D - $role"
                    )

                }

            )

        },

        bottomBar = {

            BottomBar(

                selectedItem = selectedTab,

                onItemSelected = {

                    selectedTab = it

                }

            )

        }

    ) { paddingValues ->

        Column(

            modifier =
                Modifier
                    .padding(paddingValues)
                    .padding(16.dp)

        ) {

            when (selectedTab) {

                // ==================================
                // 🖨 PRINTERS
                // ==================================

                "printers" -> {

                    if (showDetails) {

                        PrinterDetailsScreen(

                            name =
                                printerViewModel.printerName,

                            volume =
                                "220x220x250 mm",

                            materials =
                                printerViewModel.printerMaterial,

                            location =
                                printerViewModel.printerLocation,

                            description =
                                printerViewModel.printerDescription,

                            onBack = {

                                showDetails = false

                            }

                        )

                    }

                    else {

                        PrinterCard(

                            printerName =
                                printerViewModel.printerName,

                            status =
                                printerViewModel.status,

                            temperature =
                                "Buse : ${
                                    printerViewModel.nozzleTemp
                                } | Plateau : ${
                                    printerViewModel.bedTemp
                                }",

                            timeRemaining = "-",

                            progress =
                                printerViewModel.progress,

                            printerIp =
                                printerIp,

                            apiKey =
                                apiKey,

                            onPause = {

                                printerViewModel
                                    .pausePrint(apiKey)

                            },

                            onCancel = {

                                printerViewModel
                                    .cancelPrint(apiKey)

                            },

                            onMoreInfo = {

                                printerViewModel
                                    .fetchPrinterDetails(token)

                                showDetails = true

                            },

                            onControlClick = { _, _ ->

                                selectedTab = "control"

                            },

                            canControl = isAdmin || isOperateur

                        )

                    }

                }

                // ==================================
                // 🎮 CONTROL
                // ==================================

                "control" -> {

                    PrinterControlScreen(

                        printerIp =
                            printerIp,

                        apiKey =
                            apiKey,

                        onBack = {

                            selectedTab = "printers"

                        }

                    )

                }

                // ==================================
                // 📄 REQUESTS
                // ==================================

                "requests" -> {

                    // 🔎 Détails demande

                    if (selectedRequest != null) {

                        RequestDetailScreen(

                            demande =
                                selectedRequest!!,

                            onBack = {

                                selectedRequest = null

                            },

                            // ✅ ACCEPTER DETAILS

                            onAccept = { commentaire ->
                                requestViewModel.accepterDemande(
                                    selectedRequest!!.id,
                                    userNom,
                                    userEmail,
                                    commentaire,
                                    context,
                                    role,
                                    token
                                )

                                selectedRequest = null
                            },

                            // ❌ REFUSER DETAILS

                            onRefuse = { commentaire ->

                                requestViewModel
                                    .refuserDemande(

                                        selectedRequest!!.id,

                                        userNom,
                                        userEmail,

                                        commentaire,

                                        context,

                                        role,

                                        token

                                    )

                                selectedRequest = null

                            }

                        )

                    }

                    // 📄 Liste demandes

                    else {

                        Column {

                            if (isAdmin || isOperateur || isEtudiant) {
                                Button(
                                    onClick = {
                                        navController.navigate("new_request/$userNom/$userEmail/$role/$token")
                                    },
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.fillMaxWidth().height(50.dp)
                                ) {
                                    Text("+ Nouvelle demande")
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(12.dp)
                            )

                            val countEnAttente =
                                requestViewModel.demandes.count {

                                    val cleanStatus =
                                        it.status
                                            ?.trim()
                                            ?.uppercase()
                                            ?: "EN_ATTENTE"

                                    cleanStatus == "EN_ATTENTE"
                                }

                            val countASlicer =
                                requestViewModel.demandes.count {

                                    val cleanStatus =
                                        it.status
                                            ?.trim()
                                            ?.uppercase()
                                            ?: ""

                                    cleanStatus == "A_SLICER"
                                }

                            if (!isEtudiant){
                                Row {

                                    Button(
                                        onClick = {
                                            demandeFilter = "EN_ATTENTE"
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(50),

                                        colors = ButtonDefaults.buttonColors(
                                            containerColor =
                                                if (demandeFilter == "EN_ATTENTE")
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.surfaceVariant
                                        )

                                    ) {

                                        Row {
                                            Text("En attente ")

                                            Badge {
                                                Text("$countEnAttente")
                                            }
                                        }

                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = {
                                            demandeFilter = "A_SLICER"
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(50),

                                        colors = ButtonDefaults.buttonColors(
                                            containerColor =
                                                if (demandeFilter == "A_SLICER")
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.surfaceVariant
                                        )

                                    ) {

                                        Row {
                                            Text("🧊 À slicer ")

                                            Badge {
                                                Text("$countASlicer")
                                            }
                                        }

                                    }

                                }


                            }

                            Spacer(
                                modifier =
                                    Modifier.height(24.dp)
                            )

                            val demandesBase = if (isEtudiant) {
                                requestViewModel.demandes.filter {
                                    it.etudiant_email == userEmail
                                }
                            } else {
                                requestViewModel.demandes
                            }

                            val filteredDemandes = demandesBase.filter {
                                val cleanStatus = it.status?.trim()?.uppercase() ?: "EN_ATTENTE"
                                if (demandeFilter == "EN_ATTENTE") {
                                    cleanStatus == "EN_ATTENTE"
                                } else {
                                    cleanStatus == "A_SLICER"
                                }
                            }

                            LazyColumn {

                                items(filteredDemandes) { demande ->

                                    Column {
                                        DemandeCardSGI3D(
                                            demande = demande,

                                            onValider = if (isEtudiant) {
                                                {} // ❌ étudiant ne fait rien
                                            } else {
                                                {
                                                    requestViewModel.accepterDemande(
                                                        demande.id,
                                                        userNom,
                                                        userEmail,
                                                        "Acceptée rapide",
                                                        context,
                                                        role,
                                                        token
                                                    )
                                                }
                                            },

                                            onRefuser = if (isEtudiant) {
                                                {}
                                            } else {
                                                {
                                                    requestViewModel.refuserDemande(
                                                        demande.id,
                                                        userNom,
                                                        userEmail,
                                                        "Refus rapide",
                                                        context,
                                                        role,
                                                        token
                                                    )
                                                }
                                            },

                                            onDetail = { selectedRequest = demande },

                                            isAdmin = isAdmin || isOperateur // 🔥 CRUCIAL
                                        )

                                    }

                                }

                            }

                        }

                    }
                }


                "historique" -> {

                    HistoriqueScreen(email = userEmail,
                        isEtudiant = isEtudiant)

                }
                // ==================================
                // ⚙️ SETTINGS
                // ==================================

                "settings" -> {

                    SettingsScreen(

                        isDarkMode =
                            isDarkMode,

                        onToggleDarkMode =
                            onToggleDarkMode,

                        onLogout =
                            onLogout,

                        onOpenProfile =
                            onOpenProfile,

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