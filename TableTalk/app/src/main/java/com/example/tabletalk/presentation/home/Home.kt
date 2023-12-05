package com.example.tabletalk.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tabletalk.MyViewModel
import com.example.tabletalk.R
import com.example.tabletalk.presentation.data.PendingRequestItemModel
import com.example.tabletalk.presentation.data.TrendingFoodItemModel
import com.example.tabletalk.presentation.util.CommonCompose
import com.example.tabletalk.presentation.util.Screen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(navController:NavController,viewModel: MyViewModel) {

    val myLatLng by rememberSaveable {
        mutableStateOf(LatLng(0.0, 0.0))
    }

    var isMapLoaded by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        viewModel.getProfileDetails()
        viewModel.getTrendingFoods()
    }

    LaunchedEffect(viewModel.profileDetails.value,viewModel.shouldShowLoader.value) {
        if(!viewModel.shouldShowLoader.value && viewModel.profileDetails.value == null) {
            navController.navigate(Screen.ProfileScreen.route)
        }
    }

    val hasLocationPermission = remember {
        mutableStateOf(false)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(myLatLng, 10f)
    }

    val scrollState = rememberScrollState()

    val context = LocalContext.current


    val locationPermissionsAlreadyGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val permissionsGranted = permissions.values.reduce { acc, isPermissionGranted ->
                acc && isPermissionGranted
            }

            hasLocationPermission.value = permissionsGranted

        })

    val trendingFoodList = viewModel.trendingFood

    val pendingRequestList = listOf(
        PendingRequestItemModel(
            "https://images.pexels.com/photos/376464/pexels-photo-376464.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Ram", "Amritha Hotel", "Ondipudur, Dec 4th 2023 4:00PM"
        ),
        PendingRequestItemModel(
            "https://images.pexels.com/photos/376464/pexels-photo-376464.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Kishore", "Amritha Hotel", "Ondipudur, Dec 4th 2023 4:00PM"
        ),
        PendingRequestItemModel(
            "https://images.pexels.com/photos/376464/pexels-photo-376464.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Rahim", "Amritha Hotel", "Ondipudur, Dec 4th 2023 4:00PM"
        ),
        PendingRequestItemModel(
            "https://images.pexels.com/photos/376464/pexels-photo-376464.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Issac", "Amritha Hotel", "Ondipudur, Dec 4th 2023 4:00PM"
        )
    )




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


    LaunchedEffect(Unit) {
        if (locationPermissionsAlreadyGranted) {
            hasLocationPermission.value = true
        } else {
            locationPermissionLauncher.launch(locationPermissions)
        }
    }


    CommonCompose.OpaqueLoaderScreen(disableInteraction = viewModel.shouldShowLoader.value) {

        Column(
            Modifier
                .padding(8.dp)
                .verticalScroll(scrollState)
        ) {


            Column(Modifier) {
                if (hasLocationPermission.value) {
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            compassEnabled = false, zoomControlsEnabled = false,
                            myLocationButtonEnabled = true
                        ),
                        properties = MapProperties(isMyLocationEnabled = true),
                        onMapLoaded = {
                            isMapLoaded = true
                        }
                    )
                } else {
                    Text(text = "Doesn't have permission")
                }
            }

            if(isMapLoaded) {


            Column(Modifier.padding(top = 8.dp)) {


                Text(text = "Your Requests", fontWeight = FontWeight.Bold, fontSize = 14.sp)



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

                    PendingRequestItem(pendingRequest)

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
                Text(text = foodItem.foodName ?: "", fontSize = 12.sp)
                Text(text = "${foodItem.foodRating} ★", fontSize = 10.sp)
            }
        }

    }
}

@Composable
@Preview(showBackground = true)
fun PreviewTrendingItem() {
    TrendingItem(
        foodItem = TrendingFoodItemModel(
            "1", "Idly", "1,",
            "https://images.pexels.com/photos/376464/pexels-photo-376464.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            restaurantImageUrl = "https://images.pexels.com/photos/376464/pexels-photo-376464.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "4.5"
        )
    )
}


@Composable
fun PendingRequestItem(pendingRequest: PendingRequestItemModel) {
    Column() {
        Image(
            painter = if (pendingRequest.profileImageUrl.isNullOrEmpty())
                painterResource(id = R.drawable.baseline_photo_24) else
                rememberAsyncImagePainter(pendingRequest.profileImageUrl),
            contentDescription = "Profile Image",
            modifier = Modifier
                .padding(start = 16.dp)
                .height(55.dp)
                .width(55.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape)

        )
        Text(text = pendingRequest.name ?: "", fontSize = 12.sp)
        Text(text = pendingRequest.restaurantName ?: "", fontSize = 8.sp)
        Text(text = pendingRequest.locationTime ?: "", fontSize = 8.sp, lineHeight = 10.sp)
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPendingRequestItem() {
    PendingRequestItem(
        pendingRequest = PendingRequestItemModel(
            "https://images.pexels.com/photos/376464/pexels-photo-376464.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2",
            "Padhmanathan", "Amritha Hotel", "Ondipudur, Dec 4th 2023: 4:00PM"
        )
    )
}


@OptIn(ExperimentalFoundationApi::class)
fun PagerState.calculateCurrentOffsetForPage(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
}
