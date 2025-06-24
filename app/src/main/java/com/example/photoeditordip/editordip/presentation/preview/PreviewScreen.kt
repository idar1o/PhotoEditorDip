package com.example.photoeditordip.editordip.presentation.preview

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.photoeditordip.navigation.Screen
import com.example.photoeditordip.presentation.editing.EditViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
@Composable
fun PreviewScreen(
    navController: NavController,
    imageUri: Uri?,
) {
    val parentEntry = remember { navController.getBackStackEntry(Screen.Home.route) }
    val viewModel: EditViewModel = hiltViewModel(parentEntry)
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var presetName by remember { mutableStateOf("") }

    // Основной UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Preview Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )
            } ?: CircularProgressIndicator()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    viewModel.clearFullHistory()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Preview.route) { inclusive = true }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Home, contentDescription = "Home")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Home",
                    style = TextStyle(fontSize = 12.sp))
            }

            Button(
                onClick = {
                    imageUri?.let { shareImage(context, it) }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share",style = TextStyle(fontSize = 12.sp))
            }

            Button(
                onClick = {
                    showDialog = true
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Preset")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save", style = TextStyle(fontSize = 12.sp))
            }
        }


        // Диалог для ввода имени пресета
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Save Preset") },
                text = {
                    OutlinedTextField(
                        value = presetName,
                        onValueChange = { presetName = it },
                        label = { Text("Preset Name") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (presetName.isNotBlank()) {
                            imageUri?.let { viewModel.savePreset(presetName.trim(), it) }
                            presetName = ""
                            showDialog = false
                        } else {
                            Toast.makeText(context, "Write Name", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = false
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}


fun shareImage(context: Context, it: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, it)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Поделиться изображением через"))
}
