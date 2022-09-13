package com.dev.notespace

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.dev.core.domain.model.presenter.User
import com.dev.notespace.component.BottomBar
import com.dev.notespace.navigation.NoteSpaceNavigation
import com.dev.notespace.navigation.NoteSpaceRegis
import com.dev.notespace.navigation.NoteSpaceScreen
import com.dev.notespace.screen.*
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@ExperimentalPagerApi
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

    val result = remember { mutableStateOf<Uri?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            result.value = it
            navController.navigate(NoteSpaceNavigation.Post.name)
        }
    }

    navController.addOnDestinationChangedListener{ _, destination, _ ->
        showBottomBar = when(destination.route) {
            NoteSpaceNavigation.Home.name -> {
                true
            }
            NoteSpaceNavigation.Profile.name -> {
                true
            }
            else -> {
                false
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
                        launcher.launch("application/pdf")
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
            },
            mediaUri = result.value
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalPagerApi
@Composable
private fun NoteSpaceNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    showSnackBar: (String) -> Unit,
    mediaUri: Uri?
) {
    NavHost(
        navController = navController,
        startDestination = NoteSpaceScreen.Splash.name,
        modifier = modifier
    ) {
        composable(NoteSpaceScreen.Splash.name) {
            SplashScreen(
                navigateToLanding = {
                    navController.navigate(NoteSpaceScreen.Landing.name) {
                        popUpTo(NoteSpaceScreen.Splash.name) {
                            inclusive = true
                        }
                    }
                },
                navigateToHome = {
                    navController.navigate(NoteSpaceNavigation.Home.name) {
                        popUpTo(NoteSpaceScreen.Splash.name) {
                            inclusive = true
                        }
                    }
                }
            )
        }

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
                navigateToHome = {
                    navController.navigate(NoteSpaceNavigation.Home.name) {
                        popUpTo(NoteSpaceNavigation.Home.name) {
                            inclusive = true
                        }
                    }
                },
                onBackClicked = {
                    navController.navigateUp()
                }
            )
        }

        composable(NoteSpaceRegis.Register.name) {
            RegisterScreen(
                navigateToOtp = { number, verificationId, user ->
                    navigateToOtp(navController, number, verificationId, user)
                },
                onBackClicked = {
                    navController.navigateUp()
                },
                showSnackBar = showSnackBar
            )
        }

        composable(NoteSpaceNavigation.Home.name) {
            HomeScreen(
                navigateToNoteDetail = { note_id, user_id ->
                    navigateToNoteDetail(navController, note_id, user_id)
                },
                navigateToSearch = { subject ->
                    navigateToSearch(navController, subject)
                },
                navigateToStarred = {
                    navController.navigate(NoteSpaceScreen.Starred.name)
                }
            )
        }

        composable(NoteSpaceNavigation.Profile.name) {
            ProfileScreen()
        }

        composable(
            route = NoteSpaceNavigation.Post.name
        ) {
            AddScreen(
                _mediaUri = mediaUri,
                onBackClicked = {
                    navController.navigateUp()
                },
                onPostSuccess = {
                    navController.navigate(NoteSpaceNavigation.Home.name)
                },
                showSnackBar = showSnackBar
            )
        }

        composable(
            route = "${NoteSpaceScreen.NoteDetail.name}/{note_id}/{user_id}",
            arguments = listOf(
                navArgument("note_id") {
                    type = NavType.StringType
                },
                navArgument("user_id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("note_id")
            val userId = backStackEntry.arguments?.getString("user_id")

            if(noteId!=null && userId!=null) {
                NoteDetailScreen(
                    note_id = noteId,
                    user_id = userId,
                    onBackClicked = {
                        navController.navigateUp()
                    }
                )
            }
        }

        composable(
            route = "${NoteSpaceScreen.Search.name}/{subject}",
            arguments = listOf(
                navArgument("subject") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val subject = backStackEntry.arguments?.getString("subject")

            if(subject!=null) {
                SearchScreen(
                    subject = subject,
                    navigateToNoteDetail = { note_id, user_id ->
                        navigateToNoteDetail(navController, note_id, user_id)
                    },
                    onBackClicked = {
                        navController.navigateUp()
                    }
                )
            }
        }

        composable(NoteSpaceScreen.Starred.name) {
            StarredNoteScreen(
                navigateToNoteDetail = { note_id, user_id ->
                    navigateToNoteDetail(navController, note_id, user_id)
                },
                onBackClicked = {
                    navController.navigateUp()
                }
            )
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

private fun navigateToNoteDetail(
    navController: NavController,
    note_id: String,
    user_id: String
) {
    navController.navigate("${NoteSpaceScreen.NoteDetail.name}/$note_id/$user_id")
}

private fun navigateToSearch(
    navController: NavController,
    subject: String
) {
    navController.navigate("${NoteSpaceScreen.Search.name}/$subject")
}