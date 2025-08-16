package com.example.audiobookshelfwatch.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import com.example.audiobookshelfwatch.presentation.theme.AudiobookshelfWatchTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("WearAppUI", "MainActivity onCreate started.")
        setContent {
            WearApp(context = applicationContext)
        }
        Log.d("WearAppUI", "MainActivity onCreate finished.")
    }
}

@Composable
fun WearApp(context: Context) {
    Log.d("WearAppUI", "WearApp Composable started.")
    val audiobooks = remember {
        val filesDir = context.filesDir
        filesDir.listFiles { file -> file.extension.equals("m4a", ignoreCase = true) } ?: emptyArray()
    }

    AudiobookshelfWatchTheme {
        // We don't need a Box here. ScalingLazyColumn will handle the layout.
        BookList(books = audiobooks.toList())
    }
    Log.d("WearAppUI", "WearApp Composable finished.")
}

@Composable
fun BookList(books: List<File>) {
    Log.d("WearAppUI", "BookList Composable started with ${books.size} books.")
    // ScalingLazyColumn is the correct component for scrollable lists on Wear OS.
    // It automatically handles circular screen layouts.
    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        items(books) { bookFile ->
            // Use a Column to center the text within each list item.
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = bookFile.nameWithoutExtension,
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
    }
    Log.d("WearAppUI", "BookList Composable finished.")
}
