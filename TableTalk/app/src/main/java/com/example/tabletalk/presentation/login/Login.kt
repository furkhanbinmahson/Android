package com.example.tabletalk.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tabletalk.R
import com.example.tabletalk.MyViewModel
import com.example.tabletalk.presentation.util.CommonCompose
import com.example.tabletalk.presentation.util.CommonCompose.OpaqueLoaderScreen
import com.example.tabletalk.presentation.util.Screen
import com.example.tabletalk.presentation.util.UtilFunctions.isValidEmail


@Composable

fun LoginScreen(navController: NavController, viewModel: MyViewModel) {


    if (viewModel.isLoggedIn.value) {
        navController.navigate(Screen.HomeScreen.route)
    } else {
        Login(navController)
    }


}

@Composable
fun Login(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoginScreen by remember { mutableStateOf(true) }
    val viewModel: MyViewModel = viewModel()

    val context = LocalContext.current
    var isEmailError by remember {
        mutableStateOf(false)
    }
    var isPasswordError by remember {
        mutableStateOf(false)
    }

    val focusManager = LocalFocusManager.current



    if (viewModel.loginSuccess.value == true) {

        navController.navigate(Screen.HomeScreen.route) {
            popUpTo(Screen.LoginScreen.route) {
                inclusive = true
            }
        }
    }

    OpaqueLoaderScreen(
        disableInteraction = viewModel.shouldShowLoader.value
    ) {


        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {


            Image(
                painter = painterResource(id = R.drawable.tabletalk),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(5f)
            ) {


                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                if (isPasswordVisible) Icons.Default.Search else Icons.Default.Lock,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    )
                )

                if (isLoginScreen) {
                    Text(
                        text = "Forgot Password?",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {

                        if (isValidEmail(email) && password.length >= 6) {
                            if (isLoginScreen) {
                                viewModel.login(email, password, context)
                            } else {
                                viewModel.register(email, password, context)
                            }
                        } else if (!isValidEmail(email)) {
                            isEmailError = true
                        } else {
                            isPasswordError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = if (isLoginScreen) "Login" else "Register")
                }

                Text(
                    text =
                    if (isLoginScreen) "Doesn't have account? SignUp"
                    else "Already Registered? Login",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clickable {
                            isLoginScreen = !isLoginScreen
                        },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

