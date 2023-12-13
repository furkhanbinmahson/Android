package com.example.tabletalk.presentation.restaurants

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tabletalk.MyViewModel
import com.example.tabletalk.R
import com.example.tabletalk.presentation.data.Food
import com.example.tabletalk.presentation.data.Restaurants
import com.example.tabletalk.presentation.util.UtilFunctions
import com.google.android.gms.maps.model.LatLng

@Composable
fun RestaurantsScreen(navController: NavController, viewModel: MyViewModel) {


    val context = LocalContext.current
    val listOfRestaurants = viewModel.allRestaurants
    remember {
        mutableStateOf(false)
    }
    val expandedResId = remember {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(Unit) {
        viewModel.getRestaurants(context )
    }


    LazyColumn() {
        items(listOfRestaurants.size) { index ->

                HotelCard(listOfRestaurants[index],onExpandClicked = {
                    expandedResId.intValue = index
                })
                if (index == expandedResId.intValue) {
                    listOfRestaurants[index].food?.forEach {
                        if (it != null)
                            Foods(it)
                    }
                }

        }
    }

}


@Composable
fun HotelCard(restaurants: Restaurants,onExpandClicked:()->Unit) {

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {

        Row(Modifier.padding(8.dp)) {

            Image(
                painter =
                if (restaurants.imageUrl == null)
                    painterResource(id = R.drawable.tabletalk)
                else
                    rememberAsyncImagePainter(model = restaurants.imageUrl),
                contentDescription = "Requester Image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .height(80.dp)
                    .width(80.dp)
                    .clip(CircleShape)
            )


            Column(
                Modifier
                    .padding(start = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Show Menu", fontSize = 12.sp, modifier = Modifier.fillMaxWidth().clickable {
                            onExpandClicked.invoke()
                    },
                    textAlign = TextAlign.End
                )
                Text(
                    text =
                    restaurants.name ?: "", fontSize = 12.sp
                )

                Text(text = "Location  :${restaurants.location}", fontSize = 10.sp)
                Text(text = "4.0 ★")

            }
        }
    }
}

@Composable
fun Foods(food: Food) {

    Card(
        modifier = Modifier
            .padding(start = 24.dp, top = 8.dp, end = 16.dp)
            .fillMaxWidth()
    ) {

        Row(Modifier.padding(8.dp)) {

            Image(
                painter = if (food.foodImageUrl == null)
                    painterResource(id = R.drawable.tabletalk)
                else
                    rememberAsyncImagePainter(model = food.foodImageUrl),
                contentDescription = "food Image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .height(50.dp)
                    .width(50.dp)
                    .clip(CircleShape)
            )



            Column(Modifier.padding(start = 8.dp)) {
                Text(
                    text = food.name ?:"", fontSize = 12.sp
                )

                Text(text = "${food.rating} ★")

            }
        }
    }


}