package com.example.tabletalk.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.tabletalk.R
import com.example.tabletalk.presentation.util.Screen
import kotlinx.coroutines.delay

@Composable
fun Splash(navController: NavController) {


    LaunchedEffect(Unit) {
        delay(10L)
        navController.navigate(Screen.LoginScreen.route){
            popUpTo(Screen.SplashScreen.route) {
                inclusive = true
            }
        }
    }
    Column(Modifier.fillMaxSize()) {
     Image(
         modifier = Modifier.fillMaxSize(),
         painter = painterResource(id = R.drawable.splash),
         contentDescription ="Splash",
         contentScale = ContentScale.FillBounds
     )
    }
}