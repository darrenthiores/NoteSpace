package com.dev.notespace

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch


@Composable
fun BaseApp() {
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
        BaseNavHost(
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

@Composable
fun BaseNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    showSnackBar: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "Splash", // change to your screen first destinaton
        modifier = modifier
    ) {
        composable("Base") { // just a placeholder -> change to your screen route

        }
    }
}