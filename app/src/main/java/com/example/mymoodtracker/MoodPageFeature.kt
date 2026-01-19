package com.example.mymoodtracker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class MoodOption(val label: String, val color: Color)

val moodOptions = listOf(
    MoodOption("No data", Color(0x00000000)),       // Transparent
    MoodOption("Sad", Color(0xFF1E81B0)),           // Red-ish
    MoodOption("A bit down", Color(0xFF4DA8DA)),    // Orange
    MoodOption("Quiet day", Color(0xFF90C3D4)),     // Yellow
    MoodOption("Feeling good", Color(0xFFFFB347)),  // Light green
    MoodOption("At my best", Color(0xFFFDE74C))     // Dark green
)

@Composable
fun FirstPage(db: AppDatabase){
    val context = LocalContext.current
    var moods by remember { mutableStateOf<List<DailyMood>>(emptyList()) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        /** TO TEST */
        db.dailyMoodDao().insertAll(SampleData.moodList)

        /** WORKER NOTIFICATION CALL */
        val testRequest = OneTimeWorkRequestBuilder<NotificationWorker>().build()
        WorkManager.getInstance(context).enqueue(testRequest)

        setUpDailyMood(db)
        moods = db.dailyMoodDao().getAll()
        scrollState.scrollTo(scrollState.maxValue)


    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        GraphScreen(moods, {moods = it}, db)
        Spacer(Modifier.height(10.dp))
        MoodList(moods, db, {moods = it})
    }
}

@Composable
fun GraphScreen(
    moods: List<DailyMood>,
    onMoodsUpdated: (List<DailyMood>) -> Unit,
    db: AppDatabase
) {
    var showDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Scroll to end whenever moods change
    LaunchedEffect(moods.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            "MY MOOD TRACKER",
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.headlineMedium
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(40.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .horizontalScroll(scrollState)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GraphLine(moods) // directly use parent state
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = { showDialog = true }
        ) {
            Text("ADD MOOD")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("How are you feeling today?") },
                text = {
                    Column {
                        moodOptions.drop(1).forEach { mood ->
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = mood.color),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                onClick = {
                                    showDialog = false

                                    scope.launch(Dispatchers.IO) {
                                        db.dailyMoodDao().updateMood(getDayInt(Date()), moodOptions.indexOf(mood))

                                        val updated = db.dailyMoodDao().getAll()
                                        withContext(Dispatchers.Main) {
                                            onMoodsUpdated(updated)
                                            scope.launch {
                                                scrollState.animateScrollTo(scrollState.maxValue)
                                            }
                                        }
                                    }
                                }
                            ) {
                                Text(mood.label)
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {}
            )
        }
    }
}


@Composable
fun GraphLine(data: List<DailyMood>) {

    val lineColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onPrimaryContainer
    val pointSpacing = 80.dp
    val scrollWidth = if (data.isEmpty()) 300.dp else pointSpacing * data.size
    val height = 200.dp
    val sdf = remember { SimpleDateFormat("MM/dd", Locale.getDefault()) }

    Canvas(
        modifier = Modifier
            .width(scrollWidth)
            .height(height)
            .padding(8.dp)
    ) {
        if (data.isEmpty()) return@Canvas

        val maxValue = 5f
        val minValue = 1f
        val stepX = size.width / data.size

        // Horizontal grid
        for (i in 0..4) {
            val y = size.height - (i / 4f * size.height)
            drawLine(lineColor, Offset(0f, y), Offset(size.maxDimension, y), 1f)
        }

        // Background rounded bars
        data.forEachIndexed { i, mood ->
            if (mood.value > 0) {
                val x = i * stepX + stepX / 2
                val barHeight = ((mood.value - minValue) / (maxValue - minValue)) * size.height
                val barColor = moodOptions[mood.value].color

                drawRect(
                    color = barColor,
                    topLeft = Offset(x - stepX / 4, size.height - barHeight),
                    size = androidx.compose.ui.geometry.Size(stepX / 2, barHeight)
                )

                drawArc(
                    color = barColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(x - stepX / 4, size.height - barHeight - stepX / 4),
                    size = androidx.compose.ui.geometry.Size(stepX / 2, stepX / 2)
                )
            }
        }

        // Lines: connect only non-zero points
        var previousValidIndex: Int? = null
        data.forEachIndexed { i, mood ->
            if (mood.value > 0) {
                if (previousValidIndex != null) {
                    val prev = previousValidIndex
                    val x1 = prev * stepX + stepX / 2
                    val x2 = i * stepX + stepX / 2
                    val y1 = size.height - ((data[prev].value - minValue) / (maxValue - minValue)) * size.height
                    val y2 = size.height - ((mood.value - minValue) / (maxValue - minValue)) * size.height

                    drawLine(lineColor, Offset(x1, y1), Offset(x2, y2), 6f)
                }
                previousValidIndex = i
            }
        }

        // Points + dates: draw only non-zero moods
        data.forEachIndexed { i, mood ->
            val x = i * stepX + stepX / 2

            val calendar = Calendar.getInstance().apply {
                set(
                    (mood.date / 10000).toInt(),
                    ((mood.date % 10000) / 100 - 1).toInt(),
                    (mood.date % 100).toInt()
                )
            }

            if (mood.value > 0) {
                val y = size.height - ((mood.value - minValue) / (maxValue - minValue)) * size.height
                drawCircle(lineColor, 10f, Offset(x, y))
            }

            drawContext.canvas.nativeCanvas.drawText(
                sdf.format(calendar.time),
                x,
                size.height + 30f,
                android.graphics.Paint().apply {
                    color = textColor.hashCode()
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}
@Composable
fun MoodList(
    moods: List<DailyMood>,
    db: AppDatabase,
    onMoodsUpdated: (List<DailyMood>) -> Unit
) {
    if (moods.isEmpty()) {
        Text(
            text = "No moods recorded yet.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )
        return
    }

    var selectedMood by remember { mutableStateOf<DailyMood?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(15.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(vertical = 5.dp, horizontal = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            moods
                .sortedByDescending { it.date } // latest first
                .forEach { mood ->
                    val moodOption = moodOptions[mood.value]

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable {
                                selectedMood = mood
                                showDialog = true
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .fillMaxWidth()
                                .background(
                                    color = moodOption.color,
                                    shape = MaterialTheme.shapes.small
                                )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${getDayString(mood.date)} - ${moodOption.label}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
        }
    }

    // Dialog to change mood for selected day
    if (showDialog && selectedMood != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Change mood for ${getDayString(selectedMood!!.date)}") },
            text = {
                Column {
                    moodOptions.drop(1).forEach { moodOption ->
                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = moodOption.color),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = {
                                showDialog = false
                                val newValue = moodOptions.indexOf(moodOption)
                                scope.launch(Dispatchers.IO) {
                                    db.dailyMoodDao().updateMood(selectedMood!!.date, newValue)
                                    val updated = db.dailyMoodDao().getAll()
                                    withContext(Dispatchers.Main) {
                                        onMoodsUpdated(updated)
                                    }
                                }
                            }
                        ) {
                            Text(moodOption.label)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}