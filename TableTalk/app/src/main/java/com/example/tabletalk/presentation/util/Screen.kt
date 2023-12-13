package com.example.tabletalk.presentation.util

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")
    object SplashScreen: Screen("splash_screen")
    object LoginScreen: Screen("login_screen")
    object ProfileScreen: Screen("profile_screen")
    object RequestsScreen: Screen("requests_screen")
    object NewMyRequestScreen: Screen("new_my_request_screen")
    object OldRequestScreen: Screen("old_request_screen")
    object RestaurantsScreen: Screen("restaurant_screen")
    object ChatScreen: Screen("chat_screen")
}