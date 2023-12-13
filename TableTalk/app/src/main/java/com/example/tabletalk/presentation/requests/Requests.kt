package com.example.tabletalk.presentation.requests

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tabletalk.MyViewModel
import com.example.tabletalk.R
import com.example.tabletalk.presentation.data.OpenRequestModel
import com.example.tabletalk.presentation.util.CommonCompose.EmptyCard
import com.example.tabletalk.presentation.util.Screen
import com.example.tabletalk.presentation.util.UtilFunctions
import com.google.android.gms.maps.model.LatLng

@Composable
fun RequestsScreen(navController: NavController, viewModel: MyViewModel) {


    val context = LocalContext.current

    var isOpenRequestExpanded by remember {
        mutableStateOf(true)
    }

    var isMyRequestExpanded by remember {
        mutableStateOf(false)
    }

    var stateUpdate by remember {
        mutableStateOf(false)
    }

    var emptyOpenRequest by remember {
        mutableStateOf(true)
    }

    var emptyMyRequest by remember {
        mutableStateOf(true)
    }

    val openRequests = viewModel.allRequests

    Column {


        Card(
            modifier = Modifier.padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(249, 229, 188))
        ) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp)
            ) {

                Row(
                    Modifier
                        .padding(6.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Open Requests", modifier = Modifier.clickable {
                        isOpenRequestExpanded = !isOpenRequestExpanded
                        isMyRequestExpanded = !isOpenRequestExpanded
                    }, textAlign = TextAlign.Center, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Image(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        painter =
                        if (isOpenRequestExpanded)
                            painterResource(id = R.drawable.baseline_keyboard_arrow_up_24)
                        else
                            painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                        contentDescription = "Up Arrow"
                    )
                }
                if (isOpenRequestExpanded) {
                    LazyColumn() {
                        items(openRequests.size) { index ->
                            if (openRequests[index].userId != viewModel.currentUser.value?.uid &&
                                openRequests[index].isWaitingForConfirmation != true
                            ) {
                                OpenRequest(openRequests[index], onJoinClicked = {
                                    openRequests[index].isWaitingForConfirmation = true
                                    openRequests[index].waiterId = viewModel.currentUser.value?.uid
                                    openRequests[index].waiterName = viewModel.profileDetails.value?.name
                                    openRequests[index].waiterProfileUrl = viewModel.profileDetails.value?.profileImageUrl
                                    viewModel.updateMyRequest(openRequests[index], context)
                                    stateUpdate = true
                                })
                                emptyOpenRequest = false
                            }
                        }
                    }
                    if(openRequests.size == 0 ){
                        emptyOpenRequest = true
                    }
                    if(emptyOpenRequest) {
                        EmptyCard(text = "No Request Found")
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f, false),
            colors = CardDefaults.cardColors(containerColor = Color(249, 229, 188)),
        ) {

            Row(
                Modifier
                    .padding(6.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "My Requests", modifier = Modifier.clickable {
                    isMyRequestExpanded = !isMyRequestExpanded
                    isOpenRequestExpanded = !isMyRequestExpanded
                }, textAlign = TextAlign.Center, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Image(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    painter =
                    if (isMyRequestExpanded)
                        painterResource(id = R.drawable.baseline_keyboard_arrow_up_24)
                    else
                        painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                    contentDescription = "Up Arrow"
                )
            }

            if (isMyRequestExpanded) {
                LazyColumn() {
                    items(openRequests.size) { index ->
                        if (openRequests[index].userId == viewModel.currentUser.value?.uid) {
                            emptyMyRequest = false
                            MyRequest(openRequests[index], navController, viewModel)
                        }
                    }
                }

                if(openRequests.size == 0){
                    emptyMyRequest = true
                }
                if(emptyMyRequest) {
                    EmptyCard(text = "No Request Found")
                }
            }
        }
    }
}

@Composable
fun OpenRequest(
    openRequestModel: OpenRequestModel, onJoinClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {

        Row(Modifier.padding(8.dp)) {
            Column(Modifier.weight(2f)) {
                Image(
                    painter = if (openRequestModel.profileUrl == null)
                        painterResource(id = R.drawable.tabletalk)
                    else
                        rememberAsyncImagePainter(model = openRequestModel.profileUrl),
                    contentDescription = "Requester Image",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .height(80.dp)
                        .width(80.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally),

                    )
                Text(
                    text = openRequestModel.name ?: "", fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                )
            }

            Column(
                Modifier
                    .padding(start = 8.dp)
                    .weight(5f)
            ) {

                Text(
                    text =
                    "${UtilFunctions.convertMillisToDate(openRequestModel.date ?: 0L)} - " +
                            "${openRequestModel.time} ", fontSize = 14.sp
                )
                Text(text = openRequestModel.description ?: "", fontSize = 12.sp)
                Text(
                    text = "Preferred Foods : ${openRequestModel.foodPreferences.toString()}",
                    fontSize = 10.sp
                )
                Text(text = "Location  : ${UtilFunctions.getAddressFromLatLng(LatLng(openRequestModel.latitude?:0.0,openRequestModel.longitude?:0.0), context )}", fontSize = 10.sp)

            }

            Column(
                Modifier
                    .weight(1f)
                    .clickable {
                        onJoinClicked.invoke()
                    }
                    .align(Alignment.CenterVertically)) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_local_pizza_24),
                    contentDescription = "Pizza Icon"
                )
                Text(
                    text = "Request to Join",
                    fontSize = 10.sp,
                    style = TextStyle(lineHeight = 12.sp)
                )
            }

        }

    }
}


@Composable
fun MyRequest(
    openRequestModel: OpenRequestModel,
    navController: NavController,
    viewModel: MyViewModel
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {

        Row(Modifier.padding(8.dp)) {
            Column(Modifier.weight(2f)) {
                Image(
                    painter = if (openRequestModel.profileUrl == null)
                        painterResource(id = R.drawable.tabletalk)
                    else
                        rememberAsyncImagePainter(model = openRequestModel.profileUrl),
                    contentDescription = "Requester Image",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .height(80.dp)
                        .width(80.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally),

                    )
                Text(
                    text = openRequestModel.name ?: "", fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
                )
            }

            Column(
                Modifier
                    .padding(start = 8.dp)
                    .weight(5f)
            ) {

                Text(
                    text =
                    "${UtilFunctions.convertMillisToDate(openRequestModel.date ?: 0L)} - " +
                            "${openRequestModel.time} ", fontSize = 14.sp
                )
                Text(text = openRequestModel.description ?: "", fontSize = 12.sp)
                Text(
                    text = "Preferred Foods : ${openRequestModel.foodPreferences.toString()}",
                    fontSize = 10.sp
                )
                Text(
                    text = "Location  : ${
                        UtilFunctions.getAddressFromLatLng(
                            LatLng(
                                openRequestModel.latitude ?: 0.0,
                                openRequestModel.longitude ?: 0.0
                            ), context
                        )
                    }", fontSize = 10.sp
                )

            }

            Column(
                Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
            ) {
                Image(painter = painterResource(id = R.drawable.baseline_mode_edit_outline_24),
                    contentDescription = "edit Icon",
                    modifier = Modifier.clickable {
                        viewModel.toBeEditRequest = openRequestModel
                        navController.navigate(Screen.NewMyRequestScreen.route)
                    }
                )
                Text(
                    text = "Edit", fontSize = 10.sp, modifier = Modifier
                        .fillMaxWidth()
                        .align(
                            Alignment.CenterHorizontally
                        )
                        .padding(bottom = 4.dp)
                )

                Image(painter = painterResource(id = R.drawable.baseline_delete_24),
                    contentDescription = "Delete Icon",
                    Modifier
                        .padding(top = 4.dp)
                        .clickable {
                            viewModel.deleteMyRequest(openRequestModel.reqId ?: "", context) {
                                viewModel.getAllRequests()
                            }
                        }
                )
                Text(
                    text = "Delete", fontSize = 10.sp, modifier = Modifier
                        .fillMaxWidth()
                        .align(
                            Alignment.CenterHorizontally
                        )
                )
            }


        }

    }
}

