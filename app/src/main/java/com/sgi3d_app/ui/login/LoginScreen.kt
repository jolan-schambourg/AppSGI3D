package com.sgi3d_app.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(

    onLoginClick: (
        email: String,
        password: String
    ) -> Unit

) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),

        verticalArrangement =
            Arrangement.Center

    ) {

        Text(

            text = "Connexion",

            style =
                MaterialTheme
                    .typography
                    .headlineMedium

        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        // 🔹 Email

        OutlinedTextField(

            value = email,

            onValueChange = {
                email = it
            },

            label = {
                Text("Email")
            },

            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        KeyboardType.Email
                ),

            modifier =
                Modifier.fillMaxWidth()

        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        // 🔹 Mot de passe

        OutlinedTextField(

            value = password,

            onValueChange = {
                password = it
            },

            label = {
                Text("Mot de passe")
            },

            visualTransformation =

                if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),

            trailingIcon = {

                val icon =

                    if (passwordVisible)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff

                IconButton(
                    onClick = {

                        passwordVisible =
                            !passwordVisible

                    }
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription =
                            "Afficher mot de passe"
                    )

                }

            },

            keyboardOptions =

                KeyboardOptions(
                    imeAction =
                        ImeAction.Done
                ),

            keyboardActions =

                KeyboardActions(

                    onDone = {

                        onLoginClick(
                            email,
                            password
                        )

                    }

                ),

            modifier =
                Modifier.fillMaxWidth()

        )

        Spacer(
            modifier =
                Modifier.height(24.dp)
        )

        // 🔹 Bouton login

        Button(

            onClick = {

                onLoginClick(
                    email,
                    password
                )

            },

            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)

        ) {

            Text("Se connecter")

        }

    }

}