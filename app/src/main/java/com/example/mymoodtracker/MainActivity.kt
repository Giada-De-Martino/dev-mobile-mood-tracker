package com.example.mymoodtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.mymoodtracker.ui.theme.MyMoodTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "daily_mood_database",
            ).fallbackToDestructiveMigration(false).build()

        setContent {
            MyMoodTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FirstPage(database)
                }
                NavigationBarFeature(database)
            }
        }
    }
}
