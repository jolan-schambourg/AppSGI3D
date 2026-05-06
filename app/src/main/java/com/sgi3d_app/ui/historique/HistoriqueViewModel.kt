package com.sgi3d_app.ui.historique

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.*
import com.sgi3d_app.data.remote.ApiRetrofitInstance
import com.sgi3d_app.data.remote.HistoriqueItem
import kotlinx.coroutines.launch
import android.util.Log

class HistoriqueViewModel : ViewModel() {

    var historique by mutableStateOf<List<HistoriqueItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set


    fun fetchHistorique() {

        viewModelScope.launch {

            isLoading = true

            try {

                val response =
                    ApiRetrofitInstance
                        .historiqueApi
                        .getHistorique()

                if (response.isSuccessful) {

                    val body = response.body()

                    if (body != null) {

                        historique = body.data

                        Log.d(
                            "HISTORIQUE",
                            "Items: ${historique.size}"
                        )

                    }

                }

            }

            catch (e: Exception) {

                Log.e(
                    "HISTORIQUE",
                    "Erreur chargement",
                    e
                )

            }

            isLoading = false

        }

    }

}