package com.chargemap.android.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chargemap.android.sample.compose.GymsharkBlackA
import com.chargemap.android.sample.compose.GymsharkBlueA
import com.chargemap.android.sample.compose.GymsharkWhite
import com.chargemap.compose.numberpicker.*

@Composable
fun MainActivityUI() {

    val scrollState = rememberScrollState()

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "GS Number Picker",
                            color = GymsharkWhite
                        )
                    },
                    backgroundColor = GymsharkBlackA
                )
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
//                        .clip(shape = RoundedCornerShape(8.dp))
//                        .background(Color(0xFFE7E7E7))
//                        .border(
//                            width = 1.dp,
//                            shape = RoundedCornerShape(8.dp),
//                            brush = SolidColor(GymsharkBlackA)
//                        )
                ) {
                    NumberPicker()
                    FruitPicker()
                }
                HoursNumberPicker1()
                HoursNumberPicker2()
                HoursNumberPicker3()
                HoursNumberPicker4()
                DoublesPicker()
                IntRangePicker()
            }
        }
    }
}

@Composable
private fun NumberPicker() {
    var state by remember { mutableStateOf(0) }
    NumberPicker(
        value = state,
        range = 0..5,
        onValueChange = {
            state = it
        },
        dividersColor = GymsharkBlueA
    )
}

@Composable
private fun HoursNumberPicker1() {
    var state by remember { mutableStateOf<Hours>(FullHours(12, 43)) }
    HoursNumberPicker(
        modifier = Modifier
            .padding(vertical = 16.dp), leadingZero = true,

        dividersColor = MaterialTheme.colors.error,
        value = state,
        onValueChange = {
            state = it
        },
        hoursDivider = {
            Text(
                modifier = Modifier.size(24.dp),
                textAlign = TextAlign.Center,
                text = ":"
            )
        }
    )
}

@Composable
private fun HoursNumberPicker2() {
    var state by remember { mutableStateOf<Hours>(AMPMHours(9, 43, AMPMHours.DayTime.PM)) }
    HoursNumberPicker(
        modifier = Modifier
            .padding(vertical = 16.dp), leadingZero = true,

        dividersColor = MaterialTheme.colors.secondary,
        value = state,
        onValueChange = {
            state = it
        },
        hoursDivider = {
            Text(
                modifier = Modifier.size(24.dp),
                textAlign = TextAlign.Center,
                text = ":"
            )
        },
        minutesDivider = {
            Spacer(
                modifier = Modifier.size(24.dp),
            )
        }
    )
}

@Composable
private fun HoursNumberPicker3() {
    var state by remember { mutableStateOf<Hours>(FullHours(9, 20)) }

    HoursNumberPicker(
        modifier = Modifier
            .padding(vertical = 16.dp), leadingZero = true,

        value = state,
        onValueChange = {
            state = it
        },
        minutesRange = IntProgression.fromClosedRange(0, 50, 10),
        hoursDivider = {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center,
                text = "h"
            )
        },
        minutesDivider = {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center,
                text = "m"
            )
        },
        dividersColor = GymsharkBlueA
    )
}

@Composable
private fun HoursNumberPicker4() {
    var state by remember { mutableStateOf<Hours>(FullHours(11, 36)) }
    HoursNumberPicker(
        modifier = Modifier
            .padding(vertical = 16.dp), leadingZero = true,

        value = state,
        onValueChange = {
            state = it
        },
        hoursDivider = {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center,
                text = "hours"
            )
        },
        minutesDivider = {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center,
                text = "minutes"
            )
        },
        dividersColor = GymsharkBlueA
    )
}

@Composable
private fun DoublesPicker() {
    val possibleValues = generateSequence(0.5f) { it + 0.25f }
        .takeWhile { it <= 5f }
        .toList()
    var state by remember { mutableStateOf(possibleValues[0]) }
    ListItemPicker(
        label = { it.toString() },
        value = state,
        onValueChange = { state = it },
        list = possibleValues,
        dividersColor = GymsharkBlueA
    )
}

@Composable
private fun FruitPicker() {
    val possibleValues = listOf("🍎", "🍊", "🍉", "🥭", "🍈", "🍇", "🍍")
    var state by remember { mutableStateOf(possibleValues[0]) }
    ListItemPicker(
        label = { it },
        value = state,
        onValueChange = { state = it },
        list = possibleValues,
        dividersColor = GymsharkBlueA
    )
}

@Composable
private fun IntRangePicker() {
    val possibleValues = (-5..10).toList()
    var value by remember { mutableStateOf(possibleValues[0]) }
    ListItemPicker(
        label = { it.toString() },
        value = value,
        onValueChange = { value = it },
        list = possibleValues,
        dividersColor = GymsharkBlueA
    )
}