package com.example.tabletalk

import android.os.Bundle
import android.os.StrictMode
import android.transition.Scene
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tabletalk.presentation.chat.ChatScreen
import com.example.tabletalk.presentation.home.Home
import com.example.tabletalk.presentation.login.LoginScreen
import com.example.tabletalk.presentation.newrequest.NewRequest
import com.example.tabletalk.presentation.oldRequests.OldRequestScreen
import com.example.tabletalk.presentation.profile.ProfileScreen
import com.example.tabletalk.presentation.requests.RequestsScreen
import com.example.tabletalk.presentation.restaurants.RestaurantsScreen
import com.example.tabletalk.presentation.splash.Splash
import com.example.tabletalk.presentation.util.Screen
import com.example.tabletalk.ui.theme.TableTalkTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        setContent {


            TableTalkTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val viewModel: MyViewModel = viewModel()


                    val navController = rememberNavController()
                    var showBottomBar by remember { mutableStateOf(true) }
                    var showTopBar by remember { mutableStateOf(true) }
                    var shouldShowPast by remember { mutableStateOf(false) }
                    var shouldShowBack by remember { mutableStateOf(false) }
                    val navBackStackEntry by navController.currentBackStackEntryAsState()


                    shouldShowPast = when (navBackStackEntry?.destination?.route) {
                        Screen.ProfileScreen.route -> true
                        else -> false
                    }


                    shouldShowBack = when (navBackStackEntry?.destination?.route) {
                        Screen.OldRequestScreen.route,Screen.ChatScreen.route -> true
                        else -> false
                    }


                    showBottomBar = when (navBackStackEntry?.destination?.route) {
                        Screen.SplashScreen.route,
                        Screen.LoginScreen.route,Screen.OldRequestScreen.route -> false
                        else -> true
                    }

                    showTopBar = when (navBackStackEntry?.destination?.route) {
                        Screen.SplashScreen.route,
                        Screen.LoginScreen.route,Screen.ChatScreen.route -> false
                        else -> true
                    }

                    Scaffold(
                        topBar = {
                            if (showTopBar) {
                                TopAppBar(
                                    title = {
                                        Text(
                                            "Table Talk",
                                            color = colorResource(id = R.color.black),
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    },
                                    actions = {
                                        if (shouldShowPast) {
                                            Text(
                                                "Past Events",
                                                color = colorResource(id = R.color.black),
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                modifier = Modifier.clickable {
                                                    navController.navigate(Screen.OldRequestScreen.route)
                                                }
                                            )
                                        } else {
                                            Text(
                                                "Chat",
                                                color = colorResource(id = R.color.black),
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                modifier = Modifier.clickable {
                                                    navController.navigate(Screen.ChatScreen.route)
                                                }
                                            )
                                        }

                                    },
                                    navigationIcon = {
                                        if(shouldShowBack) {
                                            Image(painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                                                contentDescription = "Back",
                                                modifier = Modifier.clickable {
                                                    navController.navigate(Screen.HomeScreen.route) {
                                                        popUpTo(Screen.NewMyRequestScreen.route) {
                                                            inclusive = true
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    })
                            }

                        },
                        bottomBar = {
                            if (showBottomBar) {
                                MyBottomNavigation(navController = navController)
                            }
                        }
                    ) {

                        NavHost(
                            modifier = Modifier.padding(
                                top = it.calculateTopPadding(),
                                bottom = it.calculateBottomPadding()
                            ),
                            navController = navController,
                            startDestination = Screen.SplashScreen.route,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None },
                            popEnterTransition = { EnterTransition.None },
                            popExitTransition = { ExitTransition.None }
                        )
                        {
                            composable(route = Screen.HomeScreen.route) {
                                Home(navController, viewModel)
                            }
                            composable(route = Screen.ProfileScreen.route) {
                                ProfileScreen(navController, viewModel)
                            }

                            composable(route = Screen.SplashScreen.route) {
                                Splash(navController)
                            }
                            composable(route = Screen.LoginScreen.route) {
                                LoginScreen(navController, viewModel)
                            }

                            composable(route = Screen.RestaurantsScreen.route) {
                                RestaurantsScreen(navController, viewModel)
                            }

                            composable(route = Screen.NewMyRequestScreen.route) {
                                NewRequest(navController, viewModel)
                            }

                            composable(route = Screen.RequestsScreen.route) {
                                RequestsScreen(navController, viewModel)
                            }

                            composable(route = Screen.OldRequestScreen.route) {
                                OldRequestScreen(navController, viewModel)
                            }

                            composable(route = Screen.ChatScreen.route) {
                                ChatScreen(navController, viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MyBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Restaurants,
        BottomNavItem.Requests,
        BottomNavItem.Home,
        BottomNavItem.AddMyRequest,
        BottomNavItem.Profile
    )
    NavigationBar(
        containerColor = Color.Transparent
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                modifier = Modifier.padding(1.dp),
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 9.sp,
                        color = colorResource(id = R.color.black)
                    )
                },
                alwaysShowLabel = false,
                selected = currentRoute == item.screenRoute,
                onClick = {
                    navController.navigate(item.screenRoute) {
                        navController.graph.startDestinationRoute?.let { screenRoute ->
                            popUpTo(screenRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


sealed class BottomNavItem(var title: String, var icon: Int, var screenRoute: String) {
    object Restaurants :
        BottomNavItem("Restaurants", R.drawable.restaurant, Screen.RestaurantsScreen.route)

    object Requests :
        BottomNavItem("Requests", R.drawable.pending_requests, Screen.RequestsScreen.route)

    object Home : BottomNavItem("Home", R.drawable.home, Screen.HomeScreen.route)
    object AddMyRequest :
        BottomNavItem("New Request", R.drawable.add, Screen.NewMyRequestScreen.route)

    object Profile : BottomNavItem("Profile", R.drawable.person, Screen.ProfileScreen.route)
}
