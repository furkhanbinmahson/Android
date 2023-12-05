package com.example.tabletalk.presentation.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.tabletalk.R

object CommonCompose {
    @Composable
    fun OpaqueLoaderScreen(
        disableInteraction: Boolean = false,
        content: @Composable () -> Unit,
    ) {
        Box(Modifier.fillMaxSize()) {
            content.invoke()
            if (disableInteraction) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(0.3f))
                    .pointerInput(Unit) {}) {
                    CircularProgressIndicator(
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(35.dp)
                            .align(Alignment.Center),
                        colorResource(id = R.color.black)
                    )
                }
            }
        }
    }
}