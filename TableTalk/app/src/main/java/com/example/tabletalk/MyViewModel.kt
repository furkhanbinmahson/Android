package com.example.tabletalk

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.presentation.data.DoneRequests
import com.example.tabletalk.presentation.data.Food
import com.example.tabletalk.presentation.data.OpenRequestModel
import com.example.tabletalk.presentation.data.Restaurants
import com.example.tabletalk.presentation.data.TrendingFoodItemModel
import com.example.tabletalk.presentation.data.User
import com.example.tabletalk.presentation.util.UtilFunctions
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class MyViewModel : ViewModel() {


    val firebaseAuth = Firebase.auth
    val loginSuccess = mutableStateOf<Boolean?>(null)
    var isLoggedIn = mutableStateOf(false)
    var currentUser = mutableStateOf<FirebaseUser?>(null)
    var shouldShowLoader = mutableStateOf(false)
    var firebaseFirestore = FirebaseFirestore.getInstance()
    var firebaseStorage = FirebaseStorage.getInstance()
    val storageRef = firebaseStorage.reference
    val profilePics = storageRef.child("profilePictures")
    val profileDetails = mutableStateOf<User?>(null)
    val allRestaurants = mutableStateListOf<Restaurants>()
    val trendingFood = mutableStateListOf<TrendingFoodItemModel?>()
    val allRequests = mutableStateListOf<OpenRequestModel>()
    val allRequestLoading = mutableStateOf(true)
    val requestsInRange = mutableStateListOf<OpenRequestModel>()
    var myLatLng: LatLng? = null
    val allPendingRequestForMe = mutableStateListOf<OpenRequestModel>()
    var toBeEditRequest : OpenRequestModel? = null
    var acceptedRequests = mutableStateListOf<DoneRequests>()


    init {
        currentUser.value = firebaseAuth.currentUser
        getProfileDetails()
        isLoggedIn.value = firebaseAuth.currentUser != null
    }

    fun getRestaurants(context: Context) {
        shouldShowLoader.value = true
        firebaseFirestore.collection("restaurants").get().addOnCompleteListener {
            if (it.isSuccessful) {
                shouldShowLoader.value = false
                if (it.result.documents.isNotEmpty()) {
                    allRestaurants.clear()
                    it.result.documents.forEach { doc ->

                        val restaurants = Restaurants()
                        doc?.data?.let { docS ->
                            restaurants.id = docS["id"].toString()
                            restaurants.imageUrl = docS["imageUrl"].toString()
                            restaurants.location = docS["location"].toString()
                            restaurants.name = docS["name"].toString()
                            restaurants.rating = docS["rating"].toString()
                            val foodArray = docS["food"] as ArrayList<*>
                            val foodArrayFinal = mutableListOf<Food>()
                            foodArray.forEach { f ->
                                val foodObj = Food()
                                val food = f as Map<*, *>
                                foodObj.rating = food["rating"].toString()
                                foodObj.foodImageUrl = food["foodImageUrl"].toString()
                                foodObj.name = food["name"].toString()
                                foodArrayFinal.add(foodObj)

                            }
                            restaurants.food = foodArrayFinal
                            allRestaurants.add(restaurants)
                        }


                    }
                }
            } else {
                Toast.makeText(context, "Request Failed", Toast.LENGTH_LONG).show()
                shouldShowLoader.value = false
            }
        }
    }

    fun getAcceptedRequests() {
        shouldShowLoader.value = true
        firebaseFirestore.collection("oldRequests").orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapShot , e ->
                acceptedRequests.clear()
                if(snapShot !=null && !snapShot.isEmpty) {
                    snapShot.documents.forEach { doc ->
                        val acc = doc.toObject<DoneRequests>()
                        if (acc != null) {
                            acceptedRequests.add(acc)
                        }
                    }
                    shouldShowLoader.value = false
                }
                shouldShowLoader.value = false
            }
    }

    fun markDone(doneRequests: DoneRequests,context:Context) {
        shouldShowLoader.value = true
        doneRequests.isDone = true
        firebaseFirestore.collection("oldRequests").document(doneRequests.reqId?:"").set(doneRequests).addOnCompleteListener {
            if(it.isSuccessful) {
                shouldShowLoader.value = false
                Toast.makeText(context,"Completed", Toast.LENGTH_LONG).show()
            } else {
                shouldShowLoader.value = false
                Toast.makeText(context,"Failed to Mark", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun deleteAcceptedRequests(id:String,context:Context) {
        shouldShowLoader.value = true
        firebaseFirestore.collection("oldRequests").document(id).delete().addOnCompleteListener {
            if(it.isSuccessful) {
                shouldShowLoader.value = false
                Toast.makeText(context,"Deleted", Toast.LENGTH_LONG).show()
            } else {
                shouldShowLoader.value = false
                Toast.makeText(context,"Failed to Delete", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun removeMyRequest(newRequest: OpenRequestModel,context:Context) {
        shouldShowLoader.value = true
        newRequest.isWaitingForConfirmation = false
        firebaseFirestore.collection("oldRequests").document(newRequest.reqId?:"").set(newRequest).addOnCompleteListener {
            if(it.isSuccessful) {
                getAllRequests()
                shouldShowLoader.value = false
                Toast.makeText(context, "Request Rejected", Toast.LENGTH_LONG).show()
            }
            else {
                shouldShowLoader.value = false
                Toast.makeText(context, "Failed To Reject", Toast.LENGTH_LONG).show()
            }
        }

    }

    fun addAcceptedRequests(
        doneRequests: DoneRequests,
        context: Context,
        reqId: String,
        reqId1: String?
    ) {
        shouldShowLoader.value = true
        firebaseFirestore.collection("oldRequests").document(reqId).set(doneRequests).addOnCompleteListener {
            if(it.isSuccessful) {
                shouldShowLoader.value = false
                firebaseFirestore.collection("newRequests").document(reqId1?:"").delete().addOnCompleteListener{ delete->
                    if(delete.isSuccessful) {
                        getAllRequests()
                    }
                }
                Toast.makeText(context,"Request Accepted", Toast.LENGTH_LONG).show()
            } else {
                shouldShowLoader.value = false
                Toast.makeText(context,"Request Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun deleteMyRequest(id: String, context: Context, onsuccessCallBack: () -> Unit) {
        shouldShowLoader.value   = true
        firebaseFirestore.collection("newRequests").document(id).delete().addOnCompleteListener {
            if(it.isSuccessful) {
                shouldShowLoader.value = false
                onsuccessCallBack.invoke()
                Toast.makeText(context, "Request Deleted", Toast.LENGTH_LONG).show()
            }
            else {
                shouldShowLoader.value = false
                Toast.makeText(context, "Failed To Delete", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun updateMyRequest(openRequestModel: OpenRequestModel, context: Context) {
        shouldShowLoader.value = true
        firebaseFirestore.collection("newRequests").document(openRequestModel.reqId ?: "")
            .set(openRequestModel).addOnCompleteListener {
                if (it.isSuccessful) {
                    shouldShowLoader.value = false
                    Toast.makeText(context, "Request Submitted", Toast.LENGTH_LONG).show()
                } else {
                    shouldShowLoader.value = false
                    Toast.makeText(context, "Failed to Place Request, Try Again", Toast.LENGTH_LONG)
                        .show()

                }
            }
    }

    fun getRequestsInRange(distance: Double) {
        requestsInRange.clear()
        allRequests.forEach { openRequestModel ->
            if (myLatLng != null && openRequestModel.latitude != null && openRequestModel.longitude != null) {
                if (UtilFunctions.findLatLngWithinDistance(
                        myLatLng ?: LatLng(0.0, 0.0),
                        LatLng(openRequestModel.latitude, openRequestModel.longitude),
                        distance
                    )
                ) {
                    requestsInRange.add(openRequestModel)
                }
            }
        }
    }

    fun getRequestPendingForMe() {
        allPendingRequestForMe.clear()
        allRequests.forEach { openRequestModel ->
            if (openRequestModel.isWaitingForConfirmation == true && openRequestModel.userId == currentUser.value?.uid) {
                allPendingRequestForMe.add(openRequestModel)
            }
        }
    }

    fun getAllRequests() {
        allRequestLoading.value = true
        shouldShowLoader.value = true
        firebaseFirestore.collection("newRequests").get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (it.result.documents.isNotEmpty()) {
                    allRequests.clear()
                    it.result.documents.forEachIndexed { index, doc ->
                        val openRequestModel = doc.toObject<OpenRequestModel>()
                        if (openRequestModel != null) {
                            allRequests.add(openRequestModel)
                        }
                    }
                    allRequestLoading.value = false
                    shouldShowLoader.value = false
                } else {
                    shouldShowLoader.value = false
                }
            } else {
                shouldShowLoader.value = false
            }
        }
    }


    fun addNewRequest(newRequest: OpenRequestModel, context: Context, successCallback: () -> Unit) {

        viewModelScope.launch {
            shouldShowLoader.value = true
            firebaseFirestore.collection("newRequests").document(newRequest.reqId ?: "reqId")
                .set(newRequest).addOnCompleteListener {
                    if (it.isSuccessful) {
                        shouldShowLoader.value = false
                        Toast.makeText(
                            context,
                            "New Request Placed Successfully",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        successCallback.invoke()
                    } else {
                        shouldShowLoader.value = false
                        Toast.makeText(
                            context,
                            "Failed to Place Request, Try Again",
                            Toast.LENGTH_LONG
                        )
                            .show()

                    }
                }
        }
    }

    fun getTrendingFoods() {
        viewModelScope.launch {
            shouldShowLoader.value = true
            firebaseFirestore.collection("trendingFood").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    shouldShowLoader.value = false
                    trendingFood.clear()
                    it.result.documents.forEach { resDoc ->
                        trendingFood.add(resDoc.toObject<TrendingFoodItemModel>())
                    }
                } else {
                    shouldShowLoader.value = false
                }
            }
        }

    }


    fun login(email: String, password: String, context: Context) {
        shouldShowLoader.value = true
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                loginSuccess.value = true
                shouldShowLoader.value = false
                currentUser.value = firebaseAuth.currentUser
            } else {
                loginSuccess.value = false
                shouldShowLoader.value = false
                Toast.makeText(
                    context,
                    it.exception?.message ?: "Error occurred",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun register(email: String, password: String, context: Context) {
        shouldShowLoader.value = true
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                currentUser.value = firebaseAuth.currentUser
                loginSuccess.value = true
                shouldShowLoader.value = false
            } else {
                loginSuccess.value = false
                shouldShowLoader.value = false
                Toast.makeText(
                    context,
                    it.exception?.message ?: "Error occurred",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun logout() {
        firebaseAuth.signOut()
        isLoggedIn.value = false
    }

    fun getProfileDetails() {
        currentUser.value = firebaseAuth.currentUser
        shouldShowLoader.value = true
        firebaseFirestore.collection("profile").document(currentUser.value?.uid ?: "doc").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    shouldShowLoader.value = false
                    if (it.result.exists()) {
                        profileDetails.value = it.result.toObject<User>()
                    } else {
                        profileDetails.value = null
                    }
                } else {
                    shouldShowLoader.value = false
                    profileDetails.value = null
                }
            }
    }

    fun updateProfile(
        context: Context,
        profileImageUri: Uri,
        name: String,
        bio: String,
        foodPreference: List<String>,
        isPhotoChanged: Boolean
    ) {
        currentUser.value = firebaseAuth.currentUser
        shouldShowLoader.value = true

        if (isPhotoChanged) {
            profilePics.child(currentUser.value?.uid ?: "Doc").putFile(profileImageUri)
                .addOnCompleteListener { photoTask ->
                    if (photoTask.isSuccessful) {
                        profilePics.child(
                            currentUser.value?.uid ?: "Doc"
                        ).downloadUrl.addOnSuccessListener { downloadUri ->
                            val user = User(
                                currentUser.value?.uid ?: "doc",
                                name,
                                downloadUri.toString(),
                                bio,
                                foodPreference
                            )
                            firebaseFirestore.collection("profile")
                                .document(currentUser.value?.uid ?: "doc").set(user)
                                .addOnCompleteListener { profileTask ->
                                    if (profileTask.isSuccessful) {
                                        shouldShowLoader.value = false
                                        getProfileDetails()
                                        Toast.makeText(
                                            context,
                                            "Profile Updated successfully",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        shouldShowLoader.value = false
                                        Toast.makeText(
                                            context,
                                            "Profile Update Failed" + profileTask.exception?.message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }
                    } else {
                        shouldShowLoader.value = false
                        Toast.makeText(
                            context,
                            "Profile Update Failed" + photoTask.exception?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } else {
            val user = User(
                currentUser.value?.uid ?: "doc",
                name,
                profileImageUri.toString(),
                bio,
                foodPreference
            )
            firebaseFirestore.collection("profile").document(currentUser.value?.uid ?: "doc")
                .set(user).addOnCompleteListener { profileTask ->
                    if (profileTask.isSuccessful) {
                        getProfileDetails()
                        shouldShowLoader.value = false
                        Toast.makeText(context, "Profile Updated successfully", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        shouldShowLoader.value = false
                        Toast.makeText(
                            context,
                            "Profile Update Failed" + profileTask.exception?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

    }

}