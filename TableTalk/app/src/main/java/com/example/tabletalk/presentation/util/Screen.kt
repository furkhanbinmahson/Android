package com.example.tabletalk.presentation.util

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")
    object SplashScreen: Screen("splash_screen")
    object LoginScreen: Screen("login_screen")
    object ProfileScreen: Screen("profile_screen")
    object RequestsScreen: Screen("requests_screen")
    object NewMyRequestScreen: Screen("new_my_request_screen")
    object PeopleAroundMe: Screen("people_around_me")
    object RestaurantsScreen: Screen("restaurant_screen")
}