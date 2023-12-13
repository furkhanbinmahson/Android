package com.example.tabletalk.presentation.chat

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tabletalk.MyViewModel
import com.example.tabletalk.R

@Composable
fun ChatScreen(navController: NavController, viewModel: MyViewModel) {

    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {

        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Chat Window", fontSize = 18.sp)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = if (viewModel.profileDetails.value?.profileImageUrl == null)
                            painterResource(id = R.drawable.tabletalk)
                        else rememberAsyncImagePainter(model = viewModel.profileDetails.value?.profileImageUrl),
                        contentDescription = "Profile Pic",
                        modifier = Modifier
                            .height(60.dp)
                            .width(60.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = viewModel.profileDetails.value?.name ?: "",
                        Modifier.padding(start = 8.dp),
                        fontSize = 20.sp
                    )
                }
            }
        }



        Column(
            modifier = Modifier
                .fillMaxSize()

                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(text = "Your recent chat appears here", fontSize = 16.sp)

            FloatingActionButton(modifier = Modifier.padding(top = 16.dp), onClick = {
                Toast.makeText(context, "This feature will be available soon", Toast.LENGTH_LONG)
                    .show()
            }) {
                Image(painter = painterResource(id = R.drawable.add), contentDescription = "Add")
            }

        }


    }
}