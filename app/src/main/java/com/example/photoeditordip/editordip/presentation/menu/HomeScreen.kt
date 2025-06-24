package com.example.photoeditordip.presentation.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.photoeditordip.editordip.presentation.components.ImagePickerButton
import com.example.photoeditordip.editordip.presentation.editing.PresetHolderViewModel
import com.example.photoeditordip.editordip.presentation.menu.HomeScreenState
import com.example.photoeditordip.editordip.presentation.menu.HomeScreenViewModel
import com.example.photoeditordip.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    holderViewModel: PresetHolderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val presets by viewModel.presets.collectAsState()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(), // либо custom для камеры
        onResult = { imageUri ->
            imageUri?.let {
                val encodedUri = Uri.encode(it.toString())
                navController.navigate(Screen.EditParam(encodedUri, origin = "home").route)
            }
        }
    )
    LaunchedEffect(Unit) {
        viewModel.loadUserImages()
        viewModel.loadPresets()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // AI Toolbox Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFF6347))
                .clickable {
                    navController.navigate(Screen.AIToolbox.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "AI Toolbox",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    text = "Unleash your creativity and try our AI Toolbox now!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Try Now",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // All Photos section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Choose Image",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            ImagePickerButton(onImagePicked = { uri: Uri? ->
                uri?.let {
                    val encodedUri = Uri.encode(it.toString())
                    navController.navigate(Screen.EditParam(imageUri = encodedUri, origin = "home").route)
                }
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "Ваши работы" — горизонтальный список
        when (val state = uiState) {
            is HomeScreenState.Result -> {
                if (state.userImages.isNotEmpty()) {
                    Text(
                        text = "Your pictures",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Box(
                        modifier = Modifier // чуть меньше, чтобы скомпенсировать размер
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE0E0E0)) // светло-серый фон
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                        ) {
                            items(state.userImages) { imagePair ->
                                val uri = imagePair.first
                                val bitmap = imagePair.second

                                Image(
                                    painter = rememberAsyncImagePainter(bitmap),
                                    contentDescription = "User Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(130.dp)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            val encodedUri = Uri.encode(uri.toString())
                                            navController.navigate(Screen.EditParam(imageUri = encodedUri, origin = "home").route)
                                        }
                                )
                            }

                        }
                    }


                }
            }
            is HomeScreenState.Loading -> {
                Text("Loading...", style = MaterialTheme.typography.bodyMedium)
            }
            is HomeScreenState.Error -> {
                Text("Ошибка: ${state.message}", color = Color.Red)
            }
            else -> Unit
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔥 Новый раздел "Presets"
        if (presets.isNotEmpty()) {
            Text(
                text = "Presets",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                LazyRow(
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(4.dp)
                ) {
                    items(presets) { preset ->
                        val uri = remember(preset.imageUri) { Uri.parse(preset.imageUri) }

                        Box(
                            modifier = Modifier
                                .size(width = 150.dp, height = 274.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    holderViewModel.setSelectedPreset(preset)
                                    launcher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = preset.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                                        )
                                    )
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = preset.name,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

            }

        }
    }
}