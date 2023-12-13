package com.example.tabletalk.presentation.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tabletalk.R
import com.example.tabletalk.ui.theme.Purple80
import com.example.tabletalk.ui.theme.PurpleGrey40

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


    @Composable
    fun EmptyCard(text:String) {

        Column(modifier = Modifier.fillMaxWidth()) {
            Image(painter = painterResource(id = R.drawable.empty), contentDescription = "Empty Image",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(70.dp)
                    .width(70.dp).fillMaxWidth())

            Text(text = text, fontSize = 16.sp, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), textAlign = TextAlign.Center)
        }
    }


}