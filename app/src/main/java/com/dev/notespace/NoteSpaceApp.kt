package com.dev.notespace

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dev.core.domain.model.presenter.User
import com.dev.notespace.component.BottomBar
import com.dev.notespace.component.PostPickerDialog
import com.dev.notespace.helper.MediaType
import com.dev.notespace.navigation.NoteSpaceNavigation
import com.dev.notespace.navigation.NoteSpaceRegis
import com.dev.notespace.navigation.NoteSpaceScreen
import com.dev.notespace.screen.*
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
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

    val pdfResult = remember { mutableStateOf<Uri?>(null) }
    val imgResult = remember { mutableStateOf<List<Uri>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    var showPickerDialog by remember {
        mutableStateOf(false)
    }

    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            showPickerDialog = false
            pdfResult.value = it
            navController.navigate(NoteSpaceScreen.AddByPdf.name)
        }
    }

    val imgLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) {
        if (it.isNotEmpty()) {
            showPickerDialog = false
            imgResult.value = it
            navController.navigate(NoteSpaceScreen.AddByImage.name)
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
                        showPickerDialog = true
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
                    val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = "Close"
                    )

                    when(snackBarResult) {
                        SnackbarResult.Dismissed -> {

                        }
                        SnackbarResult.ActionPerformed -> {
                            scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                        }
                    }
                }
            },
            pdfUri = pdfResult.value,
            imgUri = imgResult.value
        )
    }

    if(showPickerDialog) {
        PostPickerDialog(
            message = "Choose Media Type To Upload",
            onDismiss = { showPickerDialog = false },
            onClicked = {
                when(it) {
                    MediaType.Pdf.name -> {
                        pdfLauncher.launch("application/pdf")
                    }
                    MediaType.Image.name -> {
                        imgLauncher.launch("image/*")
                    }
                }
            }
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
private fun NoteSpaceNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    showSnackBar: (String) -> Unit,
    pdfUri: Uri?,
    imgUri: List<Uri>
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
                        popUpTo(NoteSpaceRegis.Login.name) {
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
                navigateToNoteDetail = { note_id, user_id, type ->
                    navigateToNoteDetail(navController, note_id, user_id, type)
                },
                navigateToSearch = { subject ->
                    navigateToSearch(navController, subject)
                },
                navigateToStarred = {
                    navController.navigate(NoteSpaceScreen.Starred.name)
                },
                navigateToUpdateNote = { note_id, user_id, type ->
                    navigateToEditNote(navController, note_id, user_id, type)
                },
                showSnackBar = showSnackBar
            )
        }

        composable(NoteSpaceNavigation.Profile.name) {
            ProfileScreen(
                onSettingClicked = {
                    navController.navigate(NoteSpaceScreen.ProfileSetting.name)
                }
            )
        }

        composable(
            route = NoteSpaceScreen.AddByPdf.name
        ) {
            AddByPdfScreen(
                _mediaUri = pdfUri,
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
            route = NoteSpaceScreen.AddByImage.name
        ) {
            AddByImageScreen(
                _imgUri = imgUri,
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
            route = "${NoteSpaceScreen.NoteDetail.name}/{note_id}/{user_id}/{media_type}",
            arguments = listOf(
                navArgument("note_id") {
                    type = NavType.StringType
                },
                navArgument("user_id") {
                    type = NavType.StringType
                },
                navArgument("media_type") {
                    type = NavType.StringType
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    action = Intent.ACTION_VIEW
                    uriPattern =
                        "https://www.notespace.com/${NoteSpaceScreen.NoteDetail.name}/{note_id}/{user_id}/{media_type}"
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("note_id")
            val userId = backStackEntry.arguments?.getString("user_id")
            val mediaType = backStackEntry.arguments?.getString("media_type")

            if(noteId!=null && userId!=null && mediaType!=null) {
                NoteDetailScreen(
                    note_id = noteId,
                    user_id = userId,
                    mediaType = mediaType,
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
                    navigateToNoteDetail = { note_id, user_id, type ->
                        navigateToNoteDetail(navController, note_id, user_id, type)
                    },
                    onBackClicked = {
                        navController.navigateUp()
                    }
                )
            }
        }

        composable(NoteSpaceScreen.Starred.name) {
            StarredNoteScreen(
                navigateToNoteDetail = { note_id, user_id, type ->
                    navigateToNoteDetail(navController, note_id, user_id, type)
                },
                onBackClicked = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            route = "${NoteSpaceScreen.EditNote.name}/{note_id}/{user_id}/{media_type}",
            arguments = listOf(
                navArgument("note_id") {
                    type = NavType.StringType
                },
                navArgument("user_id") {
                    type = NavType.StringType
                },
                navArgument("media_type") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("note_id")
            val userId = backStackEntry.arguments?.getString("user_id")
            val mediaType = backStackEntry.arguments?.getString("media_type")

            if(noteId!=null && userId!=null && mediaType!=null) {
                EditNoteScreen(
                    note_id = noteId,
                    user_id = userId,
                    mediaType = mediaType,
                    onBackClicked = {
                        navController.navigateUp()
                    },
                    onUpdateSuccess = {
                        navController.navigate(NoteSpaceNavigation.Home.name) {
                            popUpTo("${NoteSpaceScreen.EditNote.name}/{note_id}/{user_id}/{media_type}") {
                                inclusive = true
                            }
                        }
                    },
                    showSnackBar = showSnackBar
                )
            }
        }

        composable(
            NoteSpaceScreen.ProfileSetting.name
        ) {
            ProfileScreenSettings(
                navigateToUpdateProfile = {
                    navController.navigate(NoteSpaceScreen.EditProfile.name)
                },
                navigateToAbout = {  },
                navigateOnLogOut = {
                    navController.navigate(NoteSpaceRegis.Login.name) {
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

        composable(NoteSpaceScreen.EditProfile.name) {
            EditProfileScreen(
                navigateEditSuccess = {
                    navController.navigate(NoteSpaceNavigation.Profile.name) {
                        popUpTo(NoteSpaceNavigation.Profile.name) {
                            inclusive = true
                        }
                    }
                },
                onBackClicked = { navController.navigateUp() },
                showSnackBar = showSnackBar
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
    user_id: String,
    media_type: String
) {
    navController.navigate("${NoteSpaceScreen.NoteDetail.name}/$note_id/$user_id/$media_type")
}

private fun navigateToSearch(
    navController: NavController,
    subject: String
) {
    navController.navigate("${NoteSpaceScreen.Search.name}/$subject")
}

private fun navigateToEditNote(
    navController: NavController,
    note_id: String,
    user_id: String,
    type: String
) {
    navController.navigate("${NoteSpaceScreen.EditNote.name}/$note_id/$user_id/$type")
}