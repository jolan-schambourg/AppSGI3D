package com.sgi3d_app.ui.requests

import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestScreen(
    studentName: String,
    studentEmail: String,
    role: String,
    onSend: (fileUri: Uri?, comment: String) -> Unit,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    val viewModel: RequestViewModel = viewModel()

    var comment by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var tempFilePath by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    // Observer succès depuis ViewModel
    val successState by viewModel
        .successState
        .collectAsStateWithLifecycle()

    // Gestion retour automatique
    LaunchedEffect(successState) {

        if (successState == true) {

            isLoading = false

            successMessage =
                "✅ Demande envoyée avec succès"

            delay(1500)

            onBack()

        }

        if (successState == false) {

            isLoading = false

            errorMessage =
                "❌ Erreur lors de l'envoi"

        }

    }

    // ===============================
    // 🔹 Lire nom fichier
    // ===============================

    fun getFileName(uri: Uri): String? {

        var name: String? = null

        val cursor =
            context.contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )

        cursor?.use {

            if (it.moveToFirst()) {

                val index =
                    it.getColumnIndex(
                        OpenableColumns.DISPLAY_NAME
                    )

                if (index >= 0) {

                    name = it.getString(index)

                }

            }

        }

        return name
    }

    // ===============================
    // 🔹 Copier fichier temporaire
    // ===============================

    fun copyToTemp(
        uri: Uri,
        fileName: String
    ): String? {

        return try {

            val inputStream =
                context.contentResolver
                    .openInputStream(uri)

            val tempFile =
                File(context.cacheDir, fileName)

            val outputStream =
                FileOutputStream(tempFile)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            tempFile.absolutePath

        } catch (e: Exception) {

            e.printStackTrace()
            null

        }

    }

    // ===============================
    // 📁 FILE PICKER
    // ===============================

    val filePickerLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            uri?.let {

                val fileName = getFileName(it) ?: "model.stl"

                val isStl = fileName.endsWith(".stl", true)
                val isGcode = fileName.endsWith(".gcode", true)

                if (isStl || isGcode) {

                    selectedFileUri = uri
                    selectedFileName = fileName
                    tempFilePath = copyToTemp(uri, fileName)

                    errorMessage = null

                } else {

                    errorMessage = "Seuls les fichiers STL ou GCODE sont acceptés"

                    selectedFileUri = null
                    selectedFileName = null
                }

            }

        }

    // ===============================
    // UI
    // ===============================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Nouvelle demande")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector =
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
                    .verticalScroll(scrollState)
                    .padding(16.dp)

        ) {

            // ===============================
            // 👤 Infos utilisateur
            // ===============================

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(12.dp)
                ) {

                    Text(
                        "👤 $studentName",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text("📧 $studentEmail")

                }

            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // ===============================
            // ℹ️ Info STL
            // ===============================

            Card(
                modifier =
                    Modifier.fillMaxWidth(),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .surfaceVariant
                    )
            ) {

                Text(
                    text =
                        "📌 Information :\n" +
                                "Les fichiers STL envoyés devront être convertis " +
                                "en GCODE par un collaborateur avant l'impression.",

                    modifier =
                        Modifier.padding(12.dp)
                )

            }

            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )

            // ===============================
            // 📁 Choix fichier
            // ===============================

            Button(

                onClick = {
                    filePickerLauncher.launch("*/*")
                },

                shape =
                    RoundedCornerShape(12.dp),

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text("📁 Choisir fichier STL")

            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            selectedFileName?.let {

                Text(
                    "Fichier sélectionné : $it"
                )

            }

            errorMessage?.let {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text = it,
                    color =
                        MaterialTheme
                            .colorScheme
                            .error
                )

            }

            // ===============================
            // 🧊 APERÇU STL
            // ===============================

            tempFilePath?.let { path ->

                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )

                Text(
                    "🧊 Aperçu 3D",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Card(
                    shape =
                        RoundedCornerShape(12.dp),
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    AndroidView(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(300.dp),

                        factory = { ctx ->

                            WebView(ctx).apply {

                                settings.javaScriptEnabled = true
                                settings.allowFileAccess = true
                                settings.domStorageEnabled = true

                                webViewClient =
                                    object : WebViewClient() {

                                        override fun onPageFinished(
                                            view: WebView?,
                                            url: String?
                                        ) {

                                            view?.post {

                                                view.loadUrl(
                                                    "javascript:loadSTL('file://$path')"
                                                )

                                            }

                                        }

                                    }

                                loadUrl(
                                    "file:///android_asset/stl_viewer.html"
                                )

                            }

                        }

                    )

                }

            }

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            // ===============================
            // 💬 Commentaire
            // ===============================

            OutlinedTextField(

                value = comment,

                onValueChange = {
                    comment = it
                },

                label = {
                    Text("Commentaire (optionnel)")
                },

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(12.dp)

            )

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            // ===============================
            // 📤 ENVOI
            // ===============================

            Button(

                onClick = {

                    if (selectedFileUri == null) {

                        errorMessage =
                            "Veuillez sélectionner un fichier"

                        return@Button
                    }

                    isLoading = true

                    onSend(
                        tempFilePath?.let {
                            Uri.fromFile(File(it))
                        },
                        comment
                    )

                },

                enabled = !isLoading,

                shape =
                    RoundedCornerShape(12.dp),

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)

            ) {

                if (isLoading) {

                    CircularProgressIndicator(
                        color =
                            MaterialTheme
                                .colorScheme
                                .onPrimary,

                        modifier =
                            Modifier.size(24.dp),

                        strokeWidth = 2.dp
                    )

                } else {

                    Text("📤 Envoyer la demande")

                }

            }
            Spacer(modifier = Modifier.height(12.dp))

            successMessage?.let {

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary
                )

            }

            Spacer(modifier = Modifier.height(40.dp))

        }
    }
}