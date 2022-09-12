package com.dev.notespace.screen

import android.graphics.Insets.add
import android.icu.text.CaseMap
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Message
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.example.newproject.ui.theme.NewProjectTheme
import java.time.format.TextStyle

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.textInputServiceFactory
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.notespace.R
import com.dev.notespace.viewModel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
){
    val painter2 = painterResource(id = R.drawable.oxfordblue)
    val description2 = "Profile"
    val title2 = "Profile"
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            LongCard(
                painter = painter2,
                contentDescription = description2,
                title = title2,
                name = viewModel.user.value?.name ?: "null",
                total_star = viewModel.user.value?.totalStar ?: 0,
                status = "${viewModel.user.value?.education} - ${viewModel.user.value?.major}"
            )

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Column() {
                val email = painterResource(R.drawable.mail)
                IconCard(app= "Email", msg = "emailAsal123@gmail.com", painter = email)

                Spacer(modifier = Modifier.height(13.dp))
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Color.LightGray)
                )
                Spacer(modifier = Modifier.height(13.dp))

                val mobile = painterResource(R.drawable.phonenum)
                IconCard(app= "Mobile", msg = viewModel.user.value?.mobile ?: "null", painter = mobile)

                Spacer(modifier = Modifier.height(13.dp))
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Color.LightGray)
                )

                Spacer(modifier = Modifier.height(13.dp))

                val twitter = painterResource(R.drawable._60194)
                IconCard(app= "Twitter", msg = "@dummyTwitter", painter = twitter)

                Spacer(modifier = Modifier.height(13.dp))
                Divider(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Color.LightGray)
                )
                Spacer(modifier = Modifier.height(13.dp))

                val facebook = painterResource(R.drawable.facebook)
                IconCard(app= "Facebook", msg = "www.facebook.com/dummy321", painter = facebook)
            }
        }

    }
}

@Composable
fun IconCard(app: String, msg: String, painter: Painter) {
    // Add padding around our message
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painter,
            contentDescription = "Contact profile picture",
            modifier = Modifier
                // Set image size to 40 dp
                .size(25.dp)
            // Clip image to be shaped as a circle
        )

        // Add a horizontal space between the image and the column
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(
                text = app,
                style = androidx.compose.ui.text.TextStyle(
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal

                ),
                textAlign = TextAlign.Center
            )
            // Add a vertical space between the author and message texts
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = msg,
                style = androidx.compose.ui.text.TextStyle(
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun LongCard(
    painter: Painter,
    contentDescription: String,
    title: String,
    name: String,
    status: String,
    total_star: Int,
    modifier: Modifier = Modifier
){
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp, 0.dp, 30.dp, 30.dp )
    ) {
        Box(modifier = Modifier.height(400.dp))
        {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painter,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(25.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = title,
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFFE5FAFC),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                MessageCard(
                    name = name,
                    status = status
                )
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "⭐ $total_star      |     Level 100\n\n",
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color.LightGray,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    textAlign = TextAlign.Center
                )
            }

        }



    }
}

@Composable
fun MessageCard(
    name: String,
    status: String
) {
    // Add padding around our message
    Column(
        modifier = Modifier
            .padding(all = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                // Set image size to 40 dp
                .size(110.dp)
                // Clip image to be shaped as a circle
                .clip(CircleShape)
                .border(4.dp, Color.White, CircleShape)
        )

        // Add a horizontal space between the image and the column
        Spacer(modifier = Modifier.width(10.dp))
        Spacer(modifier = Modifier.height(15.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = name,
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFFE5FAFC),
                        fontSize = 20.sp
                    ),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = status,
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color.LightGray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

            }
        }

        // Add a vertical space between the author and message texts
        Spacer(modifier = Modifier.height(4.dp))

    }
}