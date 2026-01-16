package com.example.mymoodtracker

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun DiaryPageFeature(db: AppDatabase) {

    val scope = rememberCoroutineScope()
    val today = getTodayInt()

    var diaryText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedDate by remember { mutableLongStateOf(today) }
    var entries by remember { mutableStateOf<List<DiaryEntry>>(emptyList()) }

    LaunchedEffect(Unit) {
        entries = db.diaryEntryDao().getAll()
    }

    LaunchedEffect(selectedDate) {
        val entry = db.diaryEntryDao().getByDate(selectedDate)
        diaryText = TextFieldValue(entry?.content ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        /** ---------- HEADER ---------- */

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Diary for ${getDayString(selectedDate)}",
                style = MaterialTheme.typography.headlineSmall
            )

            TextButton(
                onClick = {
                    selectedDate = today
                }
            ) {
                Text("New Entry")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        /** ---------- EDITOR ---------- */

        BasicTextField(
            value = diaryText,
            onValueChange = { diaryText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    MaterialTheme.shapes.medium
                )
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                scope.launch {
                    db.diaryEntryDao().insert(
                        DiaryEntry(
                            date = selectedDate,
                            content = diaryText.text
                        )
                    )
                    entries = db.diaryEntryDao().getAll()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(24.dp))

        /** ---------- ENTRY LIST ---------- */

        Text(
            text = "All Entries",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(entries) { entry ->
                DiaryEntryItem(
                    entry = entry,
                    isSelected = entry.date == selectedDate,
                    onClick = { selectedDate = entry.date }
                )
            }
        }
    }
}



@Composable
fun DiaryEntryItem(
    entry: DiaryEntry,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        tonalElevation = if (isSelected) 4.dp else 0.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = getDayString(entry.date),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = entry.content.take(80),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
        }
    }
}
