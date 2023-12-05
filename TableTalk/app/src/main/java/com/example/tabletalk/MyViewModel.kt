package com.example.tabletalk

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tabletalk.presentation.data.Food
import com.example.tabletalk.presentation.data.Restaurants
import com.example.tabletalk.presentation.data.TrendingFoodItemModel
import com.example.tabletalk.presentation.data.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyViewModel : ViewModel() {


    val firebaseAuth = Firebase.auth
    val loginSuccess = mutableStateOf<Boolean?>(null)
    var isLoggedIn = mutableStateOf(false)
    var currentUser= mutableStateOf<FirebaseUser?>(null)
    var shouldShowLoader = mutableStateOf(false)
    var firebaseFirestore = FirebaseFirestore.getInstance()
    var firebaseStorage = FirebaseStorage.getInstance()
    val storageRef = firebaseStorage.reference
    val profilePics = storageRef.child("profilePictures")
    val profileDetails = mutableStateOf<User?>(null)
    val restaurants = mutableStateListOf<Restaurants>()
    val trendingFood = mutableStateListOf<TrendingFoodItemModel?>()



    init {
        getProfileDetails()
        isLoggedIn.value = firebaseAuth.currentUser != null
    }

    fun getRestaurants() {

    }

    fun getTrendingFoods() {
        viewModelScope.launch {
            shouldShowLoader.value = true
            firebaseFirestore.collection("trendingFood").get().addOnCompleteListener {
                if(it.isSuccessful) {
                    shouldShowLoader.value = false
                    trendingFood.clear()
                    it.result.documents.forEach { resDoc->
                        trendingFood.add(resDoc.toObject<TrendingFoodItemModel>())
                    }
                } else {
                    shouldShowLoader.value = false
                }
            }
        }

    }


    fun login(email:String,password:String,context: Context) {
        shouldShowLoader.value = true
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful) {
                loginSuccess.value = true
                shouldShowLoader.value = false
                currentUser.value = firebaseAuth.currentUser
            } else {
                loginSuccess.value = false
                shouldShowLoader.value = false
                Toast.makeText(context,it.exception?.message?: "Error occurred", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun register(email:String,password:String,context: Context) {
        shouldShowLoader.value = true
        firebaseAuth.createUserWithEmailAndPassword (email,password).addOnCompleteListener {
            if(it.isSuccessful) {
                currentUser.value = firebaseAuth.currentUser
                loginSuccess.value = true
                shouldShowLoader.value = false
            } else {
                loginSuccess.value = false
                shouldShowLoader.value = false
                Toast.makeText(context,it.exception?.message?: "Error occurred", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun logout() {
        firebaseAuth.signOut()
        isLoggedIn.value = false
    }

    fun getProfileDetails() {
        shouldShowLoader.value = true
        firebaseFirestore.collection("profile").document(currentUser.value?.uid?:"doc").get().addOnCompleteListener {
            if(it.isSuccessful) {
                shouldShowLoader.value = false
                if(it.result.exists()) {
                    profileDetails.value =  it.result.toObject<User>()
                } else {
                    profileDetails.value = null
                }
            } else {
                shouldShowLoader.value = false
                profileDetails.value = null
            }
        }
    }

    fun updateProfile(context: Context,profileImageUri: Uri, name:String, bio:String, foodPreference:List<String>, isPhotoChanged:Boolean) {
        shouldShowLoader.value = true

        if(isPhotoChanged) {
            profilePics.child(currentUser.value?.uid?:"Doc").putFile(profileImageUri).addOnCompleteListener {photoTask ->
                if(photoTask.isSuccessful) {
                    profilePics.child(currentUser.value?.uid?:"Doc").downloadUrl.addOnSuccessListener { downloadUri ->
                        val user  = User(currentUser.value?.uid?:"doc",name,downloadUri.toString(),bio,foodPreference)
                        firebaseFirestore.collection("profile").document(currentUser.value?.uid?:"doc").set(user).addOnCompleteListener { profileTask ->
                            if(profileTask.isSuccessful) {
                                shouldShowLoader.value = false
                                getProfileDetails()
                                Toast.makeText(context,"Profile Updated successfully",Toast.LENGTH_LONG).show()
                            } else {
                                shouldShowLoader.value = false
                                Toast.makeText(context,"Profile Update Failed" + profileTask.exception?.message,Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    shouldShowLoader.value = false
                    Toast.makeText(context,"Profile Update Failed" + photoTask.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
        } else {
            val user  = User(currentUser.value?.uid?:"doc",name, profileImageUri.toString(),bio,foodPreference)
            firebaseFirestore.collection("profile").document(currentUser.value?.uid?:"doc").set(user).addOnCompleteListener { profileTask ->
                if(profileTask.isSuccessful) {
                    getProfileDetails()
                    shouldShowLoader.value = false
                    Toast.makeText(context,"Profile Updated successfully",Toast.LENGTH_LONG).show()
                } else {
                    shouldShowLoader.value = false
                    Toast.makeText(context,"Profile Update Failed" + profileTask.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
        }

    }

}