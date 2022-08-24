package com.dev.notespace

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dev.notespace.navigation.NoteSpaceNavigation
import com.dev.notespace.navigation.NoteSpaceRegis
import com.dev.notespace.navigation.NoteSpaceScreen
import com.dev.notespace.screen.LandingScreen
import com.dev.notespace.screen.LoginScreen
import com.dev.notespace.screen.MobileOtpScreen
import com.dev.notespace.screen.RegisterScreen
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun NoteSpaceApp() {
    val allScreens = null
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentScreen = null
    
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    navController.addOnDestinationChangedListener{ _, destination, _ ->
        // if you need to hide either top bar or bottom bar in some navigation screen
        when(destination.route) {
            
        }
    }

    Scaffold(
        topBar = {
            
        },
        bottomBar = {
            
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
                    navController.navigate("${NoteSpaceRegis.Otp.name}/$number/$verificationId")
                },
                navigateToRegister =  { navController.navigate(NoteSpaceRegis.Register.name) }
            )
        }
        composable(
            route = "${NoteSpaceRegis.Otp.name}/{number}/{verificationId}",
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
            MobileOtpScreen(
                number = number ?: "",
                verification_id = verificationId ?: "",
                showSnackBar = showSnackBar,
                navigateToHome = { navController.navigate(NoteSpaceNavigation.Home.name) }
            )
        }
        composable(NoteSpaceRegis.Register.name) {
            RegisterScreen(
                navigateToOtp = { number, verificationId ->
                    navController.navigate("${NoteSpaceRegis.Otp.name}/$number/$verificationId")
                }
            )
        }
    }
}