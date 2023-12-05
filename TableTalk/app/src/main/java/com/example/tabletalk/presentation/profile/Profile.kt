package com.example.tabletalk.presentation.profile


import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tabletalk.R
import com.example.tabletalk.MyViewModel
import com.example.tabletalk.presentation.login.Login
import com.example.tabletalk.presentation.util.CommonCompose.OpaqueLoaderScreen
import com.example.tabletalk.presentation.util.Screen
import com.example.tabletalk.ui.theme.Purple80
import com.example.tabletalk.ui.theme.PurpleGrey40
import okhttp3.internal.toImmutableList

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: MyViewModel) {


    LaunchedEffect(Unit ) {
        viewModel.getProfileDetails()

    }
    val profileDetails = viewModel.profileDetails

    var name by remember { mutableStateOf(profileDetails.value?.name ?: "") }
    var photoUri: Uri? by remember {
        mutableStateOf(
            if (profileDetails.value?.profileImageUrl != null) Uri.parse(profileDetails.value?.profileImageUrl) else null
        )
    }
    var bio by remember { mutableStateOf(profileDetails.value?.bio ?: "") }
    val chips by remember {
        mutableStateOf(
            listOf(
                "Vegetarian",
                "Pescetarian", "Vegan", "Dairy-free", "Gluten-free",
                "Paleo", "Raw Food", "Keto"
            )
        )
    }
    val focusManager = LocalFocusManager.current

    val chipState = remember {
        mutableStateListOf<String>().also {
            if (!profileDetails.value?.foodPreferences.isNullOrEmpty()) {
                it.addAll(profileDetails.value?.foodPreferences?.toList()!!)
            }
        }
    }


    val scrollState = rememberScrollState()

    val isPhotoChanged = remember {
        mutableStateOf(false)
    }


    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uriData ->
            photoUri = uriData
            isPhotoChanged.value = true
        }

    val context = LocalContext.current

    OpaqueLoaderScreen(disableInteraction = viewModel.shouldShowLoader.value) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Image(painter = if (photoUri == null) painterResource(
                id = R.drawable.baseline_person_outline_24
            ) else rememberAsyncImagePainter(model = photoUri),
                contentDescription = "Profile Image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .border(2.dp, Color.Black, CircleShape)
                    .clickable {
                        launcher.launch(
                            PickVisualMediaRequest(
                                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text(text = "Name") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )


            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                modifier = Modifier
                    .fillMaxWidth(),
                label = { Text(text = "Bio") },
            )


            Text(text = "Food Preference", modifier = Modifier.padding(top = 16.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    chips.forEach {
                        SuggestionChipEachRow(
                            chip = it,
                            chipState.contains(it)
                        ) { chip ->
                            if (!chipState.contains(chip)) {
                                chipState.add(chip)
                            } else {
                                chipState.remove(chip)
                            }
                        }
                    }
                }
            }


            Button(
                onClick = {

                    if(photoUri == null) {
                        Toast.makeText(context,"Please Select Profile Picture",Toast.LENGTH_LONG).show()
                    } else if(name.isEmpty()) {
                        Toast.makeText(context,"Name Cannot be empty",Toast.LENGTH_LONG).show()
                    } else if(bio.isEmpty()) {
                        Toast.makeText(context,"Bio Cannot be empty",Toast.LENGTH_LONG).show()
                    } else if(chipState.isEmpty()) {
                        Toast.makeText(context,"Food Preference Cannot be empty",Toast.LENGTH_LONG).show()
                    } else {
                        viewModel.updateProfile(
                            context, profileImageUri = photoUri!!, name, bio,
                            chipState.toList(), isPhotoChanged.value
                        )
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = "Save")
            }


            Button(
                onClick = {
                    viewModel.logout()
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.ProfileScreen.route) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = "Logout")
            }

        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionChipEachRow(
    chip: String,
    selected: Boolean,
    onChipState: (String) -> Unit
) {

    SuggestionChip(onClick = {
        onChipState(chip)
    }, label = {
        Text(text = chip)
    },
        border = SuggestionChipDefaults.suggestionChipBorder(
            borderWidth = 1.dp,
            borderColor = if (selected) Color.Transparent else PurpleGrey40
        ),
        modifier = Modifier.padding(horizontal = 2.dp),
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = if (selected) Purple80 else Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp)
    )

}
