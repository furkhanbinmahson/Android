package com.example.tabletalk.presentation.newrequest

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.tabletalk.MyViewModel
import com.example.tabletalk.presentation.data.OpenRequestModel
import com.example.tabletalk.presentation.util.CommonCompose
import com.example.tabletalk.presentation.util.Screen
import com.example.tabletalk.presentation.util.UtilFunctions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import io.grpc.okhttp.internal.Util
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable

fun NewRequest(navController: NavController, viewModel: MyViewModel) {


    var isEdit = remember {
        mutableStateOf(false)
    }
    var editableData = remember {
        mutableStateOf<OpenRequestModel?>(null)
    }

    LaunchedEffect(Unit) {
        viewModel.getProfileDetails()
    }


    val profileDetails = viewModel.profileDetails

    val focusManager = LocalFocusManager.current

    val foodPreferenceTime = arrayOf("Breakfast", "Brunch", "Lunch", "Eve Snacks", "Dinner")
    val context = LocalContext.current.applicationContext

    var expanded by remember {
        mutableStateOf(false)
    }

    var shouldShowMap by remember {
        mutableStateOf(false)
    }


    var shouldShowDatePicker by remember {
        mutableStateOf(false)
    }


    var foodPreferenceSelectedTime by remember {
        mutableStateOf(
            foodPreferenceTime[0]
        )
    }

    val datePickerState = rememberDatePickerState()

    var selectedDateInMills = datePickerState.selectedDateMillis


    var selectedDate = datePickerState.selectedDateMillis?.let {
            UtilFunctions.convertMillisToDate(it)
    }

    val scrollState = rememberScrollState()

    var selectedLatLng by rememberSaveable {
        mutableStateOf(LatLng(0.0, 0.0))
    }

    val locationPermissionsAlreadyGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    var fusedLocationClient: FusedLocationProviderClient
    LaunchedEffect(Unit) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                selectedLatLng = LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
            }
    }


    val chipState = remember {
        mutableStateListOf<String>().also {
            if (!profileDetails.value?.foodPreferences.isNullOrEmpty()) {
                it.addAll(profileDetails.value?.foodPreferences?.toList()!!)
            }
        }
    }


    val chips by remember {
        mutableStateOf(
            listOf(
                "Vegetarian",
                "Pescetarian", "Vegan", "Dairy-free", "Gluten-free",
                "Paleo", "Raw Food", "Keto"
            )
        )
    }


    var address by remember {
        mutableStateOf("")
    }


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLatLng, 10f)
    }


    var description by remember {
        mutableStateOf("")
    }


    LaunchedEffect(Unit) {
        viewModel.toBeEditRequest?.let { openModel ->
            isEdit.value = true
            editableData.value = openModel


            if (isEdit.value) {
                chipState.clear()
                editableData.value?.foodPreferences?.forEach { pref ->
                    chipState.add(pref)
                }
                selectedDate = UtilFunctions.convertMillisToDate(editableData.value?.date ?: 0)
                address = UtilFunctions.getAddressFromLatLng(
                    LatLng(
                        editableData.value?.latitude ?: 0.0,
                        editableData.value?.longitude ?: 0.0
                    ), context
                )
                foodPreferenceSelectedTime = editableData.value?.time ?: ""
                description = editableData.value?.description ?: ""
                selectedLatLng = LatLng(
                    editableData.value?.latitude ?: 0.0,
                    editableData.value?.longitude ?: 0.0
                )
                selectedDateInMills = editableData.value?.date
                selectedDate = UtilFunctions.convertMillisToDate(editableData.value?.date?:0)
            }

            viewModel.toBeEditRequest = null
        }
    }


    if (shouldShowMap) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = {
                selectedLatLng = it
                shouldShowMap = false

                address =
                    UtilFunctions.getAddressFromLatLng(selectedLatLng, context.applicationContext)

            },
            onMapLoaded = {

            },

            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(myLocationButtonEnabled = true)

        ) {
            Marker(
                state = MarkerState(
                    position = selectedLatLng
                )
            )
        }

    } else if (shouldShowDatePicker) {

        DatePickerDialog(
            onDismissRequest = { shouldShowDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        shouldShowDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            }) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }


    } else {

        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(12.dp)
        ) {


            OutlinedTextField(
                value = selectedDate ?: "",
                readOnly = true,
                enabled = false,
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        shouldShowDatePicker = true
                    },
                label = {
                    Text(text = "Select Date")
                },
            )

            Text(
                text = "Food Preference",
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 12.sp
            )
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
                        CommonCompose.SuggestionChipEachRow(
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



            OutlinedTextField(
                value = address,
                readOnly = true,
                minLines = 3,
                maxLines = 3,
                enabled = false,
                onValueChange = { description = it },
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
                    .clickable {
                        if (locationPermissionsAlreadyGranted) {
                            shouldShowMap = true
                        }
                    },
                label = {
                    Text(text = "Select Location")
                },
            )


            ExposedDropdownMenuBox(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                // text field
                TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    value = foodPreferenceSelectedTime ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = "Preferred Time") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )


                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    foodPreferenceTime.forEach { selectedOption ->
                        DropdownMenuItem(onClick = {
                            foodPreferenceSelectedTime = selectedOption
                            expanded = false
                        },
                            text = {
                                Text(text = selectedOption)
                            }
                        )
                    }
                }
            }


            OutlinedTextField(
                value = description ?: "",
                onValueChange = { description = it },
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                minLines = 3,
                maxLines = 3,
                label = { Text(text = "Description") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = false,
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )

            Button(
                onClick = {

                    if(chipState.isNotEmpty() && selectedDateInMills != null) {
                        viewModel.addNewRequest(
                            OpenRequestModel(
                                if (isEdit.value) editableData.value?.reqId else UUID.randomUUID()
                                    .toString(),
                                viewModel.currentUser.value?.uid ?: "",
                                profileDetails.value?.name,
                                profileDetails.value?.profileImageUrl,
                                foodPreferenceSelectedTime,
                                selectedDateInMills,
                                description,
                                selectedLatLng.latitude,
                                selectedLatLng.longitude,
                                chipState.toList()
                            ), context
                        ) {
                            navController.navigate(Screen.HomeScreen.route) {
                                popUpTo(Screen.NewMyRequestScreen.route) {
                                    inclusive = true
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context,"Enter Valid Details",Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = "Save")
            }
        }

    }


}