package com.example.tabletalk.presentation.oldRequests

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.tabletalk.presentation.data.DoneRequests
import com.example.tabletalk.presentation.util.CommonCompose
import com.example.tabletalk.presentation.util.CommonCompose.EmptyCard
import com.example.tabletalk.presentation.util.UtilFunctions
import com.google.android.gms.maps.model.LatLng

@Composable
fun OldRequestScreen(navController: NavController,viewModel: MyViewModel) {

    val oldRequests = viewModel.acceptedRequests

    LaunchedEffect(Unit) {
        viewModel.getAcceptedRequests()
    }

    CommonCompose.OpaqueLoaderScreen(viewModel.shouldShowLoader.value) {
        LazyColumn {
            items(oldRequests.size) { index ->
                SinglePast(oldRequests[index], viewModel = viewModel)
            }
        }

        if(oldRequests.size == 0) {
            EmptyCard(text = "No Past Requests Found")
        }
    }

}


@Composable
fun SinglePast(doneRequests: DoneRequests,viewModel: MyViewModel) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(249, 229, 188)),
    ) {

        Row(horizontalArrangement = Arrangement.Center) {
            Column(
                Modifier
                    .padding(start = 4.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Image(
                    painter = if (doneRequests.user1ImageUrl == null)
                        painterResource(id = R.drawable.tabletalk)
                    else rememberAsyncImagePainter(model = doneRequests.user1ImageUrl),
                    contentDescription = "Person 1",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(60.dp)
                        .width(60.dp)
                        .clip(CircleShape),
                )

                Text(text = doneRequests.user1Name ?: "", fontSize = 14.sp)
            }


            Column(
                Modifier
                    .padding(start = 4.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Image(
                    painter = if (doneRequests.user2ImageUrl == null)
                        painterResource(id = R.drawable.tabletalk)
                    else rememberAsyncImagePainter(model = doneRequests.user2ImageUrl),
                    contentDescription = "Person 1",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(60.dp)
                        .width(60.dp)
                        .clip(CircleShape),
                )

                Text(text = doneRequests.user2Name ?: "", fontSize = 14.sp)
            }


            Column(Modifier.padding(start = 8.dp)) {
                Text(text = UtilFunctions.getAddressFromLatLng(LatLng (doneRequests.latitude?:0.0,doneRequests.longitude?:0.0),context), fontSize = 14.sp)
                Text(
                    text = "${UtilFunctions.convertMillisToDate(doneRequests.date ?: 0)} - ${doneRequests.time}",
                    fontSize = 14.sp
                )

                if (doneRequests.isDone == false) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)
                        .clickable {
                            viewModel.markDone(doneRequests,context)
                        }) {
                        Text(
                            text = "Mark Done", fontSize = 14.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.yes),
                            contentDescription = "Yes"
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                    viewModel.deleteAcceptedRequests(doneRequests.reqId?:"", context)
                }) {
                    Text(
                        text = "Delete", fontSize = 14.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.baseline_delete_24),
                        contentDescription = "Yes"
                    )
                }

            }
        }

    }


}
