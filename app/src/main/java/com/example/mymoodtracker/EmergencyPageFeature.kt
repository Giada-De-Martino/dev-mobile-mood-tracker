package com.example.mymoodtracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

@Composable
fun EmergencyPage() {

    var imageUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var imageCount by remember { mutableIntStateOf(0) }
    var showAd by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Emergency Comfort",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // This Box takes all available space between the header and the footer
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                showAd -> {
                    AdPlaceholder()
                }

                imageUrl != null -> {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Cute animal",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (showAd) "Check out this offer!" else "Take a breath. You're safe.\nHere’s something cute.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    
                    if (showAd) {
                        imageUrl = fetchCuteAnimal()
                        if (imageUrl != null) {
                            showAd = false
                            imageCount++
                        }
                    } else {
                        val nextImage = fetchCuteAnimal()
                        if (nextImage != null) {
                            imageUrl = nextImage
                            imageCount++
                            if (imageCount % 10 == 0) {
                                showAd = true
                            }
                        }
                    }
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (showAd && imageCount > 0) "Dismiss Ad & See More" else "Show me cute animals",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AdPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ADVERTISEMENT",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Get Premium for no ads!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Support the Mood Tracker app",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

suspend fun fetchCuteAnimal(): String? {
    return withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            
            // Randomly pick between Dog API and Cat API
            val apiUrl = if (Random.nextBoolean()) {
                "https://api.thedogapi.com/v1/images/search"
            } else {
                "https://api.thecatapi.com/v1/images/search"
            }

            val request = Request.Builder()
                .url(apiUrl)
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) return@withContext null

            val body = response.body?.string() ?: return@withContext null
            val jsonArray = JSONArray(body)

            jsonArray.getJSONObject(0).getString("url")

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
