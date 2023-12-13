package com.example.tabletalk.presentation.home

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tabletalk.MyViewModel
import com.example.tabletalk.R
import com.example.tabletalk.presentation.data.DoneRequests
import com.example.tabletalk.presentation.data.OpenRequestModel
import com.example.tabletalk.presentation.data.TrendingFoodItemModel
import com.example.tabletalk.presentation.util.CommonCompose
import com.example.tabletalk.presentation.util.CommonCompose.EmptyCard
import com.example.tabletalk.presentation.util.Screen
import com.example.tabletalk.presentation.util.UtilFunctions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.io.IOException
import java.net.URL
import java.util.UUID


@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
@Composable
fun Home(navController:NavController,viewModel: MyViewModel) {

    var myLatLng by rememberSaveable {
        mutableStateOf(LatLng(0.0, 0.0))
    }

    val context = LocalContext.current

    var isMapLoaded by remember {
        mutableStateOf(true)
    }


    LaunchedEffect(Unit) {
        viewModel.getProfileDetails()
        viewModel.getTrendingFoods()
        viewModel.getAllRequests()

    }

    LaunchedEffect(viewModel.profileDetails.value,viewModel.shouldShowLoader.value) {
        if(!viewModel.shouldShowLoader.value && viewModel.profileDetails.value == null) {
            navController.navigate(Screen.ProfileScreen.route)
        }
    }

    LaunchedEffect(viewModel.allRequestLoading.value) {
        if(!viewModel.allRequestLoading.value) {
            viewModel.getRequestsInRange(10.0)
            viewModel.getRequestPendingForMe()
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(myLatLng, 10f)
    }


    val scrollState = rememberScrollState()

    val requestsInRange = viewModel.requestsInRange



    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )


    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if(event == Lifecycle.Event.ON_START) {
                    locationPermissions.launchMultiplePermissionRequest()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )



    var fusedLocationClient:FusedLocationProviderClient
    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                myLatLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                viewModel.myLatLng = myLatLng
                cameraPositionState.position = CameraPosition.fromLatLngZoom(myLatLng,10f)
            }
    }




    val trendingFoodList = viewModel.trendingFood

    val pendingRequestList = viewModel.allPendingRequestForMe


    val trendingFoodPageState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        trendingFoodList.size
    }

    val pendingRequestPageState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        pendingRequestList.size
    }




    CommonCompose.OpaqueLoaderScreen(disableInteraction = viewModel.shouldShowLoader.value) {

        Column(
            Modifier
                .padding(8.dp)
                .verticalScroll(scrollState)
        ) {


            Column(Modifier) {
                if (locationPermissions.allPermissionsGranted) {
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            compassEnabled = false, zoomControlsEnabled = false,
                            myLocationButtonEnabled = false, zoomGesturesEnabled = false,
                            scrollGesturesEnabled = false, tiltGesturesEnabled = false,
                            mapToolbarEnabled = false, indoorLevelPickerEnabled = false
                        ),

                        properties = MapProperties(isMyLocationEnabled = true, mapType = MapType.TERRAIN),
                        onMapLoaded = {
                            isMapLoaded = true
                        }
                    ) {
                        requestsInRange.forEach {

                            if(it.userId != viewModel.profileDetails.value?.userId) {
                                var bitmap:Bitmap? = null

                                try {
                                    val url = URL(it.profileUrl)
                                    bitmap = BitmapFactory.decodeStream(
                                        url.openStream()
                                    )
                                    bitmap = Bitmap.createScaledBitmap(bitmap,100,100,false)
                                } catch (e: IOException) {
                                    println(e)
                                }

                                Marker(state = MarkerState(LatLng(it.latitude?:0.0,it.longitude?:0.0)),
                                    title = it.name,
                                    icon = bitmap?.let {
                                        BitmapDescriptorFactory.fromBitmap(bitmap)
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Text(text = "Doesn't have permission")
                }
            }

            if(isMapLoaded) {

            Column(Modifier.padding(top = 8.dp)) {


                Text(text = "Your Requests", fontWeight = FontWeight.Bold, fontSize = 16.sp)


                HorizontalPager(
                    state = pendingRequestPageState,
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxHeight(),
                    pageSize = object : PageSize {
                        override fun Density.calculateMainAxisPageSize(
                            availableSpace: Int,
                            pageSpacing: Int
                        ): Int {
                            return ((availableSpace - 2 * pageSpacing) * 0.3f).toInt()
                        }
                    }
                ) { index ->
                    val pendingRequest = pendingRequestList[index]
                    if(pendingRequest.userId == viewModel.profileDetails.value?.userId) {
                        PendingRequestItem(pendingRequest,viewModel)
                    }
                }
                if(pendingRequestList.size == 0) {
                    EmptyCard("No Requests Pending")
                }
            }



            Column(Modifier.padding(top = 8.dp)) {


                Text(text = "Trending This Week", fontWeight = FontWeight.Bold, fontSize = 14.sp)



                HorizontalPager(
                    state = trendingFoodPageState,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxHeight(),
                    pageSize = object : PageSize {
                        override fun Density.calculateMainAxisPageSize(
                            availableSpace: Int,
                            pageSpacing: Int
                        ): Int {
                            return ((availableSpace - 2 * pageSpacing) * 0.33f).toInt()
                        }
                    }
                ) { index ->
                    val foodItem = trendingFoodList[index]

                    Card(
                        modifier = Modifier.padding(4.dp),
                        shape = RoundedCornerShape(9.2.dp),
                        border = BorderStroke(0.9.dp, color = colorResource(id = R.color.black))
                    ) {

                        foodItem?.let {
                            TrendingItem(it)
                        }

                    }

                }
            }

            }
        }

        }
    }


@Composable
fun TrendingItem(foodItem: TrendingFoodItemModel) {


    Column(modifier = Modifier) {
        Image(
            painter = if (foodItem.foodImageUrl.isNullOrEmpty())
                painterResource(id = R.drawable.baseline_photo_24) else
                rememberAsyncImagePainter(foodItem.foodImageUrl),
            contentDescription = "Food Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .size(150.dp)
        )

        Row(
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = if (foodItem.restaurantImageUrl.isNullOrEmpty())
                    painterResource(id = R.drawable.baseline_photo_24) else
                    rememberAsyncImagePainter(foodItem.restaurantImageUrl),
                contentDescription = "Profile Image",

                modifier = Modifier
                    .height(35.dp)
                    .width(35.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Black, CircleShape)
            )
            Column(Modifier.padding(4.dp)) {
                Text(text = foodItem.foodName ?: "", fontSize = 12.sp, style = TextStyle(lineHeight =12.sp))
                Text(text = "${foodItem.foodRating} ★", fontSize = 10.sp)
            }
        }

    }
}


@Composable
fun PendingRequestItem(pendingRequest: OpenRequestModel,viewModel: MyViewModel) {
    val context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = if (pendingRequest.profileUrl.isNullOrEmpty())
                painterResource(id = R.drawable.baseline_photo_24) else
                rememberAsyncImagePainter(pendingRequest.waiterProfileUrl),
            contentDescription = "Profile Image",
            modifier = Modifier
                .padding(8.dp)
                .height(75.dp)
                .width(75.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape)

        )
        Text(text = pendingRequest.waiterName ?: "", style = TextStyle(lineHeight = 4.sp), fontSize = 14.sp, modifier = Modifier.padding(top = 2.dp))
        Text(text = UtilFunctions.convertMillisToDate(pendingRequest.date?:0),
            modifier = Modifier.padding(top = 2.dp),fontSize = 10.sp, lineHeight = 10.sp)
        Row(Modifier.padding(top = 4.dp)) {
            Image(painter = painterResource(id = R.drawable.yes), contentDescription = "Yes",
                modifier = Modifier.padding(end = 4.dp).clickable {
                    val reqId = UUID.randomUUID().toString()
                    val doneModel = DoneRequests(
                        reqId = reqId,
                        user1Id = pendingRequest.waiterId,
                        user1ImageUrl = pendingRequest.waiterProfileUrl,
                        user1Name = pendingRequest.waiterName,
                        user2Id =  viewModel.currentUser.value?.uid,
                        user2ImageUrl = viewModel.profileDetails.value?.profileImageUrl,
                        user2Name = viewModel.profileDetails.value?.name,
                        date = pendingRequest.date,
                        time = pendingRequest.time,
                        isDone = false,
                        latitude = pendingRequest.latitude,
                        longitude = pendingRequest.longitude,
                    )
                    viewModel.addAcceptedRequests(doneModel,context,reqId,pendingRequest.reqId)
                })
            Image(painter = painterResource(id = R.drawable.baseline_cancel_24), contentDescription = "Cancel",
                modifier = Modifier.clickable {
                    viewModel.removeMyRequest(pendingRequest, context)
                })
        }
    }
}


