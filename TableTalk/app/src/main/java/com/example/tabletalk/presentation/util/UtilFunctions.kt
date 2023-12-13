package com.example.tabletalk.presentation.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.lang.Math.cos
import java.lang.Math.sin
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.sqrt

object UtilFunctions {

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return email.matches(emailRegex)
    }

    fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(Date(millis))
    }


    fun findLatLngWithinDistance(target: LatLng, fromList: LatLng, distance: Double): Boolean {

        val d = calculateDistance(target.latitude, target.longitude, fromList.latitude, fromList.longitude)
        return d <= distance

    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c
    }

    fun getAddressFromLatLng(latLng: LatLng,context:Context):String {
        var address = ""
        try {
            val geo = Geocoder(
                context.applicationContext,
                Locale.getDefault()
            )
            val addresses: List<Address>? =
                geo.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1
                )
            address = if (addresses!!.isEmpty()) {
                "Waiting for Location"
            } else {
                (addresses[0].featureName ?: "") + ", " + (addresses[0].locality
                    ?: "") + ", " + (addresses[0].adminArea
                    ?: "") + ", " + addresses[0].countryName
            }
            return address
        } catch (e: Exception) {

            e.printStackTrace()
            return ""
        }
    }
}