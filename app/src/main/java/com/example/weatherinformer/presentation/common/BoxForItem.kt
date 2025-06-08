package com.example.weatherinformer.presentation.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


@Composable
fun BoxForItem(
    time: String,
    temp : Double,
    hum: Int,
    wind : Double,
    image: String,
    feelsLike : Double,
    pressure: Int,
    windDirection: Int) {

    Box(
        modifier = Modifier
            .padding(end = 8.dp, top = 8.dp)
            .height(130.dp)
            .width(240.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(width = 0.3.dp, color = Color.LightGray, RoundedCornerShape(5.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 4.dp)
        ) {
            Text(
                text = time,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Row {
                Text(
                    text = "${temp}C"
                )
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Ощу.: ${feelsLike}C"
                )
            }
            Row {
                Text(
                    text = "Влаж.: ${hum}%"
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Давл.: ${pressure}"
                )
            }
            Row {
                Text(
                    text = "Ветер: ${wind} м/c"
                )
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Напрв.: ${windDirection}"
                )
            }
            Image(
                painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/${image}@2x.png"),
                contentDescription = null,
                modifier = Modifier.size(48.dp).align(Alignment.CenterHorizontally)
            )
        }
    }

}