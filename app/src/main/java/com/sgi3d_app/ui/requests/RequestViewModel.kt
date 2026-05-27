package com.sgi3d_app.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.*
import com.sgi3d_app.data.remote.ApiRetrofitInstance
import com.sgi3d_app.data.remote.DemandeItem
import kotlinx.coroutines.launch
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import android.content.Context
import com.sgi3d_app.ui.utils.showSimpleNotification

class RequestViewModel : ViewModel() {

    var demandes by mutableStateOf<List<DemandeItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Etat succès création
    private val _successState =
        MutableStateFlow<Boolean?>(null)

    val successState: StateFlow<Boolean?> =
        _successState

    private var lastDemandeIds: Set<Int> = emptySet()

    // ===============================
    // 📥 Charger demandes
    // ===============================

    fun fetchDemandes(context: Context, userEmail: String, role: String) {
        viewModelScope.launch {
            isLoading = true

            try {
                val response = ApiRetrofitInstance.requestApi.getDemandes()

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null) {
                        val newDemandes = body.data ?: emptyList()

                        // 🔥 DETECTION NOUVELLES DEMANDES
                        val newIds = newDemandes.map { it.id }.toSet()
                        val addedIds = newIds - lastDemandeIds

                        val nouvellesDemandes = newDemandes.filter { it.id in addedIds }

                        // 🔥 FILTRAGE PAR ROLE
                        val demandesPourMoi = when (role) {
                            "admin", "operateur" -> nouvellesDemandes
                            "etudiant" -> nouvellesDemandes.filter {
                                it.etudiant_email == userEmail
                            }
                            else -> emptyList()
                        }

                        if (lastDemandeIds.isNotEmpty() && demandesPourMoi.isNotEmpty()) {
                            showSimpleNotification(
                                context,
                                "📄 Nouvelle demande",
                                "${demandesPourMoi.size} nouvelle(s) demande(s)",
                                2001
                            )
                        }

                        // ✅ update
                        lastDemandeIds = newIds
                        demandes = newDemandes
                    }

                } else {
                    Log.e("REQUEST_DEBUG", "Erreur fetchDemandes: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("REQUEST_DEBUG", "Exception fetchDemandes", e)
            }

            isLoading = false
        }
    }

    // ===============================
    // ✅ ACCEPTER DEMANDE
    // ===============================

    fun accepterDemande(
        id: Int,
        adminNom: String,
        adminEmail: String,
        commentaire: String,
        context: Context,
        role: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    ApiRetrofitInstance
                        .requestApi
                        .accepterDemande(
                            id,
                            adminNom,
                            adminEmail,
                            commentaire
                        )

                if (
                    response.isSuccessful &&
                    response.body()?.success == true
                ) {

                    Log.d(
                        "REQUEST_DEBUG",
                        "Demande acceptée"
                    )



                    fetchDemandes(context, adminEmail, role)

                } else {

                    Log.e(
                        "REQUEST_DEBUG",
                        "Erreur accepter"
                    )

                }

            } catch (e: Exception) {

                Log.e(
                    "REQUEST_DEBUG",
                    "Exception accepter",
                    e
                )

            }

        }

    }

    // ===============================
    // ❌ REFUSER DEMANDE
    // ===============================

    fun refuserDemande(
        id: Int,
        adminNom: String,
        adminEmail: String,
        commentaire: String,
        context: Context,
        role: String
    ) {

        viewModelScope.launch {

            try {

                val response =
                    ApiRetrofitInstance
                        .requestApi
                        .refuserDemande(
                            id,
                            adminNom,
                            adminEmail,
                            commentaire
                        )

                if (
                    response.isSuccessful &&
                    response.body()?.success == true
                ) {

                    Log.d(
                        "REQUEST_DEBUG",
                        "Demande refusée"
                    )

                    fetchDemandes(context, adminEmail, role)

                } else {

                    Log.e(
                        "REQUEST_DEBUG",
                        "Erreur refuser"
                    )

                }

            } catch (e: Exception) {

                Log.e(
                    "REQUEST_DEBUG",
                    "Exception refuser",
                    e
                )

            }

        }

    }

    // ===============================
    // 📤 CREER DEMANDE
    // ===============================

    fun createDemande(
        filePath: String,
        nom: String,
        email: String,
        commentaire: String,
        context: Context,
        role: String
    ) {

        viewModelScope.launch {

            try {

                val file = File(filePath)

                if (!file.exists()) {

                    Log.e(
                        "REQUEST_DEBUG",
                        "Fichier inexistant"
                    )

                    _successState.value = false
                    return@launch

                }

                val requestFile =
                    file.asRequestBody(
                        "application/octet-stream"
                            .toMediaTypeOrNull()
                    )

                val filePart =
                    MultipartBody.Part.createFormData(
                        "file",
                        file.name,
                        requestFile
                    )

                val nomBody =
                    nom.toRequestBody(
                        "text/plain"
                            .toMediaTypeOrNull()
                    )

                val emailBody =
                    email.toRequestBody(
                        "text/plain"
                            .toMediaTypeOrNull()
                    )

                val commentaireBody =
                    commentaire.toRequestBody(
                        "text/plain"
                            .toMediaTypeOrNull()
                    )

                val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val userToken = sharedPref.getString("token", null)

                if (userToken.isNullOrEmpty()) {
                    Log.e("REQUEST_DEBUG", "Token manquant")
                    _successState.value = false
                    return@launch
                }


                Log.d("REQUEST_DEBUG", "Token envoyé: $userToken")

                val tokenBody = userToken.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = ApiRetrofitInstance.requestApi.createDemande(
                    tokenBody,
                    filePart,
                    nomBody,
                    emailBody,
                    commentaireBody
                )

                Log.d("REQUEST_DEBUG", "Code: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("REQUEST_DEBUG", "Body: $body")
                    Log.d("REQUEST_DEBUG", "Response: ${response.body()}")

                    if (body?.success == true) {
                        Log.d("REQUEST_DEBUG","Demande créée")
                        _successState.value = true
                        fetchDemandes(context, email, role)
                    } else {
                        Log.e("REQUEST_DEBUG","Erreur logique API")
                        _successState.value = false
                    }

                } else {
                    Log.e("REQUEST_DEBUG", "Erreur createDemande")
                    Log.e("REQUEST_DEBUG", "Code: ${response.code()}")
                    Log.e("REQUEST_DEBUG", "Body: ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {

                Log.e(
                    "REQUEST_DEBUG",
                    "Exception createDemande",
                    e
                )

                _successState.value = false

            }

        }

    }

}