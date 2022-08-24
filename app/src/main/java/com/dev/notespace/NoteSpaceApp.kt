package com.dev.notespace

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dev.core.model.presenter.User
import com.dev.notespace.component.BottomBar
import com.dev.notespace.navigation.NoteSpaceNavigation
import com.dev.notespace.navigation.NoteSpaceRegis
import com.dev.notespace.navigation.NoteSpaceScreen
import com.dev.notespace.screen.LandingScreen
import com.dev.notespace.screen.HomeScreen
import com.dev.notespace.screen.LoginScreen
import com.dev.notespace.screen.MobileOtpScreen
import com.dev.notespace.screen.RegisterScreen
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun NoteSpaceApp() {
    val allScreens = NoteSpaceNavigation.values().toList()
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentScreen = NoteSpaceNavigation.fromRoute(
        backStackEntry.value?.destination?.route
    )

    var showBottomBar by remember {
        mutableStateOf(false)
    }
    
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    navController.addOnDestinationChangedListener{ _, destination, _ ->
        // if you need to hide either top bar or bottom bar in some navigation screen
        when(destination.route) {
            NoteSpaceNavigation.Home.name -> {
                showBottomBar = true
            }
            NoteSpaceNavigation.Search.name -> {
                showBottomBar = true
            }
            NoteSpaceNavigation.Notification.name -> {
                showBottomBar = true
            }
            NoteSpaceNavigation.Profile.name -> {
                showBottomBar = true
            }
            else -> {
                showBottomBar = false
            }
        }
    }

    Scaffold(
        topBar = {
            
        },
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
                    allScreens = allScreens,
                    onTabSelected = { screen -> navController.navigate(screen.name) },
                    currentScreen = currentScreen,
                    onAddPostClicked = {

                    }
                )
            }
        },
        floatingActionButton = {
                               
        },
        scaffoldState = scaffoldState
    ) { paddingValues ->
        NoteSpaceNavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            showSnackBar = { message ->
                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message
                    )
                }
            }
        )
    }
}

@ExperimentalComposeUiApi
@Composable
private fun NoteSpaceNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    showSnackBar: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = NoteSpaceScreen.Landing.name,
        modifier = modifier
    ) {

        composable(NoteSpaceScreen.Landing.name) {
            LandingScreen(navigateToLogin =  { navController.navigate(NoteSpaceRegis.Login.name) })
        }

        composable(NoteSpaceRegis.Login.name) {
            LoginScreen(
                navigateToOtp = { number, verificationId ->
                    navigateToOtp(navController, number, verificationId, null)
                },
                navigateToRegister =  { navController.navigate(NoteSpaceRegis.Register.name) }
            )
        }
        composable(
            route = "${NoteSpaceRegis.Otp.name}/{number}/{verificationId}/{user}",
            arguments = listOf(
                navArgument("number") {
                    type = NavType.StringType
                },
                navArgument("verificationId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val number = backStackEntry.arguments?.getString("number")
            val verificationId = backStackEntry.arguments?.getString("verificationId")
            val userObject = navController.previousBackStackEntry?.arguments?.getParcelable<User>("user")
            MobileOtpScreen(
                number = number ?: "",
                verification_id = verificationId ?: "",
                user = userObject,
                showSnackBar = showSnackBar,
                navigateToHome = { navController.navigate(NoteSpaceNavigation.Home.name) }
            )
        }
        composable(NoteSpaceRegis.Register.name) {
            RegisterScreen(
                navigateToOtp = { number, verificationId, user ->
                    navigateToOtp(navController, number, verificationId, user)
                }
            )
        }
        composable(NoteSpaceNavigation.Home.name) {
            HomeScreen()
        }
    }
}

private fun navigateToOtp(
    navController: NavController,
    number: String,
    verificationId: String,
    user: User?
) {
    navController.currentBackStackEntry?.arguments?.putParcelable("user", user)
    navController.navigate("${NoteSpaceRegis.Otp.name}/$number/$verificationId/{user}")
}