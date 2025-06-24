package com.example.photoeditordip.presentation.editing

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.photoeditordip.R
import com.example.photoeditordip.core.data.dto.bitmapToFile
import com.example.photoeditordip.editordip.presentation.editing.PresetHolderViewModel
import com.example.photoeditordip.editordip.presentation.editing.components.QuarterCircleButton
import com.example.photoeditordip.navigation.Screen
import com.example.photoeditordip.utils.createTempFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.opencv.android.OpenCVLoader
import java.io.File


data class Effect(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit,
    val onClickOK: (() -> Unit)? = null // nullable и необязательный
)

const val SHOW_NONE = 0
const val SHOW_STANDART_SLIDER = 1
const val SHOW_AI_SLIDER = 2
const val SHOW_AI_TRANSFER = 3
const val SHOW_CROP_PANEL = 4
const val SHOW_DRAW_PANEL = 5


enum class CornerType { TOP_LEFT, BOTTOM_RIGHT }

sealed class DrawShape {
    data class Line(val points: List<Offset>) : DrawShape()
    data class Rectangle(val start: Offset, val end: Offset) : DrawShape()
    data class Circle(val center: Offset, val radius: Float) : DrawShape()
}

enum class DrawMode { LINE, RECTANGLE, CIRCLE , NONE}


@Composable
fun EditScreen(
    navController: NavController,
    imageUri: Uri?,
    aiTool: String?,
    origin: String?
) {
    val sharedViewModel: PresetHolderViewModel = when (origin) {
        "home" -> hiltViewModel(navController.getBackStackEntry(Screen.Home.route))
        "ai_toolbox" -> hiltViewModel(navController.getBackStackEntry(Screen.AIToolbox.route))
        else -> hiltViewModel(navController.getBackStackEntry(Screen.Home.route)) // fallback, если не удалось
    }
    val viewModel: EditViewModel = when (origin) {
        "home" -> hiltViewModel(navController.getBackStackEntry(Screen.Home.route))
        "ai_toolbox" -> hiltViewModel(navController.getBackStackEntry(Screen.AIToolbox.route))
        else -> hiltViewModel(navController.getBackStackEntry(Screen.Home.route)) // fallback, если не удалось
    }
    val preset = sharedViewModel.selectedPreset.collectAsState().value

    val context = LocalContext.current
    var displayBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val uiState by viewModel.uiState.collectAsState()
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedFilter by remember { mutableStateOf<Effect?>(null) }
    // Initialize OpenCV
    LaunchedEffect(Unit) {
        OpenCVLoader.initDebug()
    }
// Load image
    LaunchedEffect(imageUri) {
        imageUri?.let {
            try {
                withContext(Dispatchers.IO) {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    originalBitmap = bitmap
                    bitmap?.let { safeBitmap ->
                        val editableBitmap = safeBitmap.copy(
                            safeBitmap.config ?: Bitmap.Config.ARGB_8888,
                            true
                        )
                        displayBitmap = editableBitmap
                        viewModel.updateExternalBitmap(editableBitmap)
                        // ✅ Добавляем в историю при первом показе
                        viewModel.addToHistory(editableBitmap)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        preset?.let {
            viewModel.applyPreset(it) // применяем эффекты к фото
        }
    }
    LaunchedEffect(aiTool, displayBitmap) {
        Log.d("EditScreen", "aiTool = $aiTool") // 🔍 Добавь лог

        if (aiTool != null && displayBitmap != null) {
            val file = bitmapToFile(context, displayBitmap!!, "image_${System.currentTimeMillis()}.png")

            when (aiTool) {
                "applyBlur" -> viewModel.applyBlur(file)
                "applyBackgroundBlur" -> viewModel.applyBackgroundBlur(file, 50) // можно задать дефолт
                "applyStyleTransfer" -> {
                    val styleFile = uriToFile(context, Uri.parse("android.resource://${context.packageName}/${R.drawable.images}"))
                    if (styleFile != null) viewModel.applyStyleTransfer(file, styleFile)
                }
                "applyRemoveBackground" -> viewModel.applyRemoveBackground(file)
                "applyCartoonify" -> viewModel.applyCartoonify(file)
                "applyPencil" -> viewModel.applyPencil(file)
                "applyPencilColor" -> viewModel.applyPencilColor(file)
                "applyOilPainting" -> viewModel.applyOilPainting(file)
                "applyWaterColor" -> viewModel.applyWaterColor(file)
                "applyHDR" -> viewModel.applyHDR(file)
                "applyEnhanceDetailsEffect" -> viewModel.applyEnhanceDetailsEffect(file)
                "applyEqualizeExposureEffect" -> viewModel.applyEqualizeExposureEffect(file)
                "applyClaheEffect" -> viewModel.applyClaheEffect(file, 2)
                "applyNegativeEffect" -> viewModel.applyNegativeEffect(file)
                "applyLaplacianEffect" -> viewModel.applyLaplacianEffect(file)
                "applyReduceNoiseEffect" -> viewModel.applyReduceNoiseEffect(file, 10)
                "applyVignetteEffect" -> viewModel.applyVignetteEffect(file, 2)
                "applySolarizeEffect" -> viewModel.applySolarizeEffect(file, 128)
                "applySobelEffect" -> viewModel.applySobelEffect(file, 3)
                "applySharpenEffect" -> viewModel.applySharpenEffect(file, 2.0)
                "applyPixelateEffect" -> viewModel.applyPixelateEffect(file, 15)
                "applyFilmGrainEffect" -> viewModel.applyFilmGrainEffect(file, 5.0)
            }
        }
    }


    // Handle UI state changes
    when (val state = uiState) {
        is EditScreenState.Result -> {
            // Display edited image from API
            val decodedBitmap = state.editedImage
            decodedBitmap.let {
                displayBitmap = it
            }
        }
        is EditScreenState.Error -> {
            Text(text = "Error: ${state.message}")
        }
        else -> {
            // Default state - do nothing
        }
    }

    // States to store images


    // States for sliders
    var showSliderById by remember { mutableStateOf(SHOW_NONE) }
    var sliderValue by remember { mutableStateOf(0f) }
    var sliderValueRange by remember { mutableStateOf(-100f..100f) }
    var sliderLabel by remember { mutableStateOf("") }

    //States for crop
    var topLeft by remember { mutableStateOf(Offset(100f, 100f)) }
    var bottomRight by remember { mutableStateOf(Offset(400f, 400f)) }
    var rectShow by remember { mutableStateOf(false) }
    var draggingCorner by remember { mutableStateOf<CornerType?>(null) }
    var imageBoxSize by remember { mutableStateOf(Size.Zero) }


    // States for drawing
    var drawMode by remember { mutableStateOf<DrawMode>(DrawMode.NONE) }
    var shapes by remember { mutableStateOf<List<DrawShape>>(emptyList()) }
    var currentLine by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var drawnLines by remember { mutableStateOf<List<List<Offset>>>(emptyList()) }






    // Available filters
    val filters = listOf(
        Effect(Icons.Filled.Tune, "Original", onClick = {
            showSliderById = SHOW_NONE
            originalBitmap?.let {
                val config = it.config ?: Bitmap.Config.ARGB_8888
                displayBitmap = it.copy(config, true)
            }
        }),
        Effect(Icons.Filled.InvertColors, "Crop", onClick = {
            rectShow = true
            showSliderById = SHOW_CROP_PANEL
            displayBitmap?.let { viewModel.updateExternalBitmap(it) }
        },
            onClickOK = {
                val imageWidth = originalBitmap?.width?.toFloat() ?: 1f
                val imageHeight = originalBitmap?.height?.toFloat() ?: 1f

                val scaleX = imageWidth / imageBoxSize.width
                val scaleY = imageHeight / imageBoxSize.height

                val x = (topLeft.x * scaleX).toInt().coerceAtLeast(0)
                val y = (topLeft.y * scaleY).toInt().coerceAtLeast(0)
                val width = ((bottomRight.x - topLeft.x) * scaleX).toInt().coerceAtLeast(1)
                val height = ((bottomRight.y - topLeft.y) * scaleY).toInt().coerceAtLeast(1)

                viewModel.cropBitmap(x, y, width, height)
                rectShow = false
                showSliderById = SHOW_NONE
            }

        ),
//        Effect(Icons.Filled.Draw, "Draw", onClick = {
//            drawMode = DrawMode.LINE
//            showSliderById = SHOW_DRAW_PANEL
//            displayBitmap?.let { viewModel.updateExternalBitmap(it) }
//        },
//            onClickOK = {
//
//            }
//
//        ),
        Effect(Icons.Filled.InvertColors, "Sepia", onClick = {
            showSliderById = SHOW_STANDART_SLIDER
            sliderValue = 51f
            sliderValueRange = 0f..100f
            sliderLabel = "Sepia"
            displayBitmap?.let { viewModel.updateExternalBitmap(it) }
        },
            onClickOK = {
                viewModel.applySepia(sliderValue)
            }),
        Effect(Icons.Filled.Brightness6, "Brightness",
            onClick = {
                showSliderById = SHOW_STANDART_SLIDER
                sliderValue = 0f
                sliderValueRange = -100f..100f
                sliderLabel = "Brightness"

                displayBitmap?.let { viewModel.updateExternalBitmap(it) }
            },
            onClickOK = { viewModel.applyBrightness(sliderValue) }),
        Effect(Icons.Filled.Contrast, "Contrast", onClick = {
            showSliderById = SHOW_STANDART_SLIDER
            sliderValue = 51f
            sliderValueRange = 0f..100f
            sliderLabel = "Contrast"

            displayBitmap?.let { viewModel.updateExternalBitmap(it) }
        },
            onClickOK = { viewModel.applyContrast(sliderValue) }),

        Effect(Icons.Filled.Straighten, "Saturation", onClick = {
            showSliderById = SHOW_STANDART_SLIDER
            sliderValue = 51f
            sliderValueRange = 0f..100f
            sliderLabel = "Saturation"

            displayBitmap?.let { viewModel.updateExternalBitmap(it) }
        },
            onClickOK = { viewModel.applySaturation(sliderValue) }),

        Effect(Icons.Filled.BlurOn, "Blur", onClick = {
            showSliderById = SHOW_STANDART_SLIDER
            sliderValue = 51f
            sliderValueRange = 0f..100f
            sliderLabel = "Intensity"

            displayBitmap?.let { viewModel.updateExternalBitmap(it) }
        },
            onClickOK = { viewModel.applyLocalBlur(sliderValue) }),


        Effect(Icons.Filled.RotateRight, "Rotation", onClick = {
            showSliderById = SHOW_STANDART_SLIDER
            sliderValue = 0f
            sliderValueRange = -180f..180f
            sliderLabel = "Rotation (degrees)"
            displayBitmap?.let { viewModel.updateExternalBitmap(it) }
        },
            onClickOK = { viewModel.applyRotation(sliderValue) })
    )

    val selectedIndex = remember { mutableStateOf<Int?>(null) }


    val defaultImageUris = remember { getDrawableUris(context) }
    val imageUris = remember { mutableStateListOf<Uri>().apply { addAll(defaultImageUris) } }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { imageUris.add(it) }
        }
    )


    val aiFilters = listOf(
        Effect(Icons.Filled.Tune, "Original", onClick = {
            showSliderById = SHOW_NONE
            originalBitmap?.let {
                val config = it.config ?: Bitmap.Config.ARGB_8888
                displayBitmap = it.copy(config, true)
            }
        }),
        Effect(Icons.Filled.BlurCircular, "AI Blur", onClick = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                // Call the API through ViewModel
                viewModel.applyBlur(file)
            } ?: run {
                // Handle case where tempFile is null
                Toast.makeText(context, "Failed to applyBlurFilter image", Toast.LENGTH_SHORT)
                    .show()
            }
        }),
        Effect(Icons.Filled.Brightness6, "Background Blur", onClick = {
            showSliderById = SHOW_AI_SLIDER
            sliderValue = 51f
            sliderValueRange = 3f..100f
            sliderLabel = "Blur intensity"
        },
            onClickOK = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                // Call the API through ViewModel
                viewModel.applyBackgroundBlur(file, sliderValue.toInt())
            } ?: run {
                // Handle case where tempFile is null
                Toast.makeText(context, "Failed to applyBlurFilter image", Toast.LENGTH_SHORT)
                    .show()
            }
        }),
        Effect(Icons.Filled.Style, "Style Transfer", onClick = {
            showSliderById = SHOW_AI_TRANSFER
        },
            onClickOK = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            val styleFile = uriToFile(context, imageUris[selectedIndex.value!!])

            editedFile?.let { file ->
                styleFile?.let { viewModel.applyStyleTransfer(file, it) }
            } ?: run {
                Toast.makeText(context, "Failed to remove background", Toast.LENGTH_SHORT).show()
            }
        }),
        Effect(Icons.Filled.BlurOn, "Remove Background", onClick = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applyRemoveBackground(file)
            } ?: run {
                Toast.makeText(context, "Failed to remove background", Toast.LENGTH_SHORT).show()
            }
        }),
        Effect(Icons.Filled.Face, "Cartoonify", onClick = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applyCartoonify(file)
            } ?: run {
                Toast.makeText(context, "Failed to apply cartoon filter", Toast.LENGTH_SHORT).show()
            }
        }),
        Effect(Icons.Filled.Edit, "Pencil Sketch", onClick = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applyPencil(file)
            } ?: run {
                Toast.makeText(context, "Failed to apply pencil sketch", Toast.LENGTH_SHORT).show()
            }
        }),
        Effect(Icons.Filled.Create, "Colored Pencil", onClick = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applyPencilColor(file)
            } ?: run {
                Toast.makeText(context, "Failed to apply colored pencil", Toast.LENGTH_SHORT).show()
            }
        }),

        Effect(Icons.Filled.Brush, "Oil Painting", onClick = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applyOilPainting(file)
            } ?: run {
                Toast.makeText(context, "Failed to apply oil painting", Toast.LENGTH_SHORT).show()
            }
        }),
        Effect(Icons.Filled.ColorLens, "Watercolor", onClick = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applyWaterColor(file)
            } ?: run {
                Toast.makeText(context, "Failed to apply watercolor", Toast.LENGTH_SHORT).show()
            }
        }),
        Effect(Icons.Filled.HdrStrong, "HDR", onClick = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applyHDR(file)
            } ?: run {
                Toast.makeText(context, "Failed to apply HDR", Toast.LENGTH_SHORT).show()
            }
        }),
        Effect(Icons.Filled.Tune, "Enhance Details", onClick = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applyEnhanceDetailsEffect(file)
            } ?: run {
                Toast.makeText(context, "Failed to enhance details", Toast.LENGTH_SHORT).show()
            }
        }),
        Effect(Icons.Filled.Explore, "Equalize Exposure", onClick = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applyEqualizeExposureEffect(file)
            } ?: run {
                Toast.makeText(context, "Failed to equalize exposure", Toast.LENGTH_SHORT).show()
            }
        }),
        Effect(Icons.Filled.Grain, "Clahe", onClick = {
            showSliderById = SHOW_AI_SLIDER
            sliderValue = 2f
            sliderValueRange = 0.1f..10f
            sliderLabel = "Clip Limit"

        },
            onClickOK = {
                val editedFile = displayBitmap?.let {
                    bitmapToFile(
                        context,
                        it, "image_${System.currentTimeMillis()}.png"
                    )
                }
                editedFile?.let { file ->
                    viewModel.applyClaheEffect(file, sliderValue.toInt())
                } ?: run {
                    Toast.makeText(context, "Failed to applyPixelateEffect", Toast.LENGTH_SHORT).show()
                }
            }),
        Effect(Icons.Filled.Tonality, "Negative", onClick = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applyNegativeEffect(file)
            } ?: run {
                Toast.makeText(context, "Failed to apply Negative", Toast.LENGTH_SHORT).show()
            }
        }),

        Effect(Icons.Filled.Tonality, "Laplacian", onClick = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applyLaplacianEffect(file)
            } ?: run {
                Toast.makeText(context, "Failed to applyLaplacianEffect", Toast.LENGTH_SHORT).show()
            }
        }),
        Effect(Icons.Filled.Grain, "Reduce Noise", onClick = {
            showSliderById = SHOW_AI_SLIDER
            sliderValue = 10f
            sliderValueRange = 0f..50f
            sliderLabel = "Strength"

        },
            onClickOK = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applyReduceNoiseEffect(file, sliderValue.toInt())
            } ?: run {
                Toast.makeText(context, "Failed to apply Negative", Toast.LENGTH_SHORT).show()
            }
        }),

        Effect(Icons.Filled.Grain, "Vignette", onClick = {
            showSliderById = SHOW_AI_SLIDER
            sliderValue = 2f
            sliderValueRange = 0f..5f
            sliderLabel = "Strength"

        },
            onClickOK = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applyVignetteEffect(file, sliderValue.toInt())
            } ?: run {
                Toast.makeText(context, "Failed to apply Negative", Toast.LENGTH_SHORT).show()
            }
        }),

        Effect(Icons.Filled.Grain, "Solarize", onClick = {
            showSliderById = SHOW_AI_SLIDER
            sliderValue = 128f
            sliderValueRange = 0f..255f
            sliderLabel = "Threshold"

        },
            onClickOK = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applySolarizeEffect(file, sliderValue.toInt())
            } ?: run {
                Toast.makeText(context, "Failed to apply Negative", Toast.LENGTH_SHORT).show()
            }
        }),
        Effect(Icons.Filled.Grain, "Sobel", onClick = {
            showSliderById = SHOW_AI_SLIDER
            sliderValue = 3f
            sliderValueRange = 1f..7f
            sliderLabel = "kernelSize"

        },
            onClickOK = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applySobelEffect(file, sliderValue.toInt())
            } ?: run {
                Toast.makeText(context, "Failed to apply Negative", Toast.LENGTH_SHORT).show()
            }
        }),
        Effect(Icons.Filled.Grain, "Sharpen", onClick = {
            showSliderById = SHOW_AI_SLIDER
            sliderValue = 0f
            sliderValueRange = 0f..5f
            sliderLabel = "strength"

        },
            onClickOK = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applySharpenEffect(file, sliderValue.toDouble())
            } ?: run {
                Toast.makeText(context, "Failed to apply Negative", Toast.LENGTH_SHORT).show()
            }
        }),
        Effect(Icons.Filled.Grain, "Pixelate", onClick = {
            showSliderById = SHOW_AI_SLIDER
            sliderValue = 1f
            sliderValueRange = 1f..70f
            sliderLabel = "Block Size"

        },
            onClickOK = {
            val editedFile = displayBitmap?.let {
                bitmapToFile(
                    context,
                    it, "image_${System.currentTimeMillis()}.png"
                )
            }
            editedFile?.let { file ->
                viewModel.applyPixelateEffect(file, sliderValue.toInt())
            } ?: run {
                Toast.makeText(context, "Failed to applyPixelateEffect", Toast.LENGTH_SHORT).show()
            }
        }),
        Effect(Icons.Filled.Grain, "Pixelate", onClick = {
            showSliderById = SHOW_AI_SLIDER
            sliderValue = 1f
            sliderValueRange = 1f..70f
            sliderLabel = "Block Size"

        },
            onClickOK = {
                val editedFile = displayBitmap?.let {
                    bitmapToFile(
                        context,
                        it, "image_${System.currentTimeMillis()}.png"
                    )
                }
                editedFile?.let { file ->
                    viewModel.applyPixelateEffect(file, sliderValue.toInt())
                } ?: run {
                    Toast.makeText(context, "Failed to applyPixelateEffect", Toast.LENGTH_SHORT).show()
                }
            }),

        Effect(Icons.Filled.Grain, "Film Grain", onClick = {
            showSliderById = SHOW_AI_SLIDER
            sliderValue = 1f
            sliderValueRange = 1f..70f
            sliderLabel = "Intensity"

        },
            onClickOK = {
                val editedFile = displayBitmap?.let {
                    bitmapToFile(
                        context,
                        it, "image_${System.currentTimeMillis()}.png"
                    )
                }
                editedFile?.let { file ->
                    viewModel.applyFilmGrainEffect(file, sliderValue.toDouble())
                } ?: run {
                    Toast.makeText(context, "Failed to Film Grain", Toast.LENGTH_SHORT).show()
                }
            }),
    )

    Scaffold{ paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Image view area (70-80% of screen)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
                    .background(Color.Black)
                    .onGloballyPositioned { coordinates ->
                        imageBoxSize = coordinates.size.toSize()
                    }

                    .pointerInput(key1 = rectShow, key2 = drawMode) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                if (rectShow) {
                                    val touchRadius = 56f
                                    if ((offset - topLeft).getDistance() < touchRadius) {
                                        draggingCorner = CornerType.TOP_LEFT
                                    } else if ((offset - bottomRight).getDistance() < touchRadius) {
                                        draggingCorner = CornerType.BOTTOM_RIGHT
                                    }
                                }
                                if (drawMode != DrawMode.NONE) {
                                    currentLine = mutableListOf(offset)
                                }
                            },
                            onDragEnd = {
                                if (rectShow) draggingCorner = null
                                if (drawMode != DrawMode.NONE) {
                                    // например, сохранить фигуру
                                    shapes += DrawShape.Line(currentLine)
                                    currentLine = emptyList()
                                }
                            },
                            onDragCancel = {
                                draggingCorner = null
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()

                                if (rectShow) {
                                    when (draggingCorner) {
                                        CornerType.TOP_LEFT -> {
                                            val newPos = topLeft + dragAmount
                                            topLeft = Offset(
                                                x = newPos.x.coerceIn(0f, bottomRight.x - 50f),
                                                y = newPos.y.coerceIn(0f, bottomRight.y - 50f)
                                            )
                                        }

                                        CornerType.BOTTOM_RIGHT -> {
                                            val newPos = bottomRight + dragAmount
                                            bottomRight = Offset(
                                                x = newPos.x.coerceIn(
                                                    topLeft.x + 50f,
                                                    imageBoxSize.width
                                                ),
                                                y = newPos.y.coerceIn(
                                                    topLeft.y + 50f,
                                                    imageBoxSize.height
                                                )
                                            )
                                        }

                                        null -> {}
                                    }
                                }

                                if (drawMode != DrawMode.NONE) {
                                    val newPos = change.position
                                    currentLine += newPos
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                displayBitmap?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Image being edited",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (rectShow) {
                        // 🔲 Обрезка
                        drawRect(
                            color = Color.Red,
                            topLeft = topLeft,
                            size = Size(
                                bottomRight.x - topLeft.x,
                                bottomRight.y - topLeft.y
                            ),
                            style = Stroke(3f)
                        )
                        drawCircle(Color.Green, 10f, center = topLeft)
                        drawCircle(Color.Green, 10f, center = bottomRight)
                    }

                    if (drawMode != DrawMode.NONE) {
                        // ✍️ Нарисованные линии
                        drawnLines.forEach { line ->
                            line.zipWithNext { start, end ->
                                drawLine(Color.Blue, start, end, strokeWidth = 3f)
                            }
                        }

                        // ✍️ Линия в процессе рисования
                        currentLine.zipWithNext { start, end ->
                            drawLine(Color.Gray, start, end, strokeWidth = 3f)
                        }
                    }
                }


                // Показываем ProgressIndicator поверх картинки, если состояние Loading
                if (uiState is EditScreenState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Кнопка "Назад"
                    QuarterCircleButton(
                        icon = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        onClick = {
                            viewModel.clearFullHistory()
                            navController.popBackStack()
                        },
                        topLeft = true
                    )

                    // Кнопки Undo и Redo по центру
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.undo() }) {
                            Icon(Icons.Default.Undo, contentDescription = "Undo", tint = Color.White)
                        }
                        IconButton(onClick = { viewModel.redo() }) {
                            Icon(Icons.Default.Redo, contentDescription = "Redo", tint = Color.White)
                        }
                    }

                    // Кнопка "Сохранить"
                    QuarterCircleButton(
                        icon = Icons.Default.Done,
                        contentDescription = "Save",
                        onClick = {
                            viewModel.updateExternalBitmap(displayBitmap!!)
                            displayBitmap?.let { bitmap ->
                                val savedUri = saveBitmapToGallery(context, bitmap)
                                savedUri?.let { uri ->
                                    navController.navigate(Screen.PreviewParam(uri.toString()).route)
                                }
                            }
                        },
                        topLeft = false
                    )
                }

            }

            FilterSliderSection(
                showSliderById = showSliderById,
                selectedFilter = selectedFilter,
                sliderLabel = sliderLabel,
                sliderValue = sliderValue,
                onSliderValueChange = { sliderValue = it },
                sliderValueRange = sliderValueRange,
                onClickOk = {
                    selectedFilter?.onClickOK?.invoke()
                    showSliderById = SHOW_NONE
                } ,
                imageUris = imageUris,
                selectedImageIndex = selectedIndex.value,
                onAddImageClick = { imagePickerLauncher.launch("image/*") },
                onImageSelect = { index -> selectedIndex.value = index },
                drawShape = drawMode,
                onDrawShapeSelected = { drawMode = it }
            )

            FilterSelector(
                filters = filters,
                aiFilters = aiFilters
            ) { effect ->
                selectedFilter = effect
                effect.onClick() // вот теперь вызываем прямо отсюда
            }

        }
    }
}
@Composable
fun FilterSelector(
    filters: List<Effect>,
    aiFilters: List<Effect>,
    onEffectSelected: (Effect) -> Unit
) {
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var showingAIFilters by remember { mutableStateOf(false) }

    val currentFilters = if (showingAIFilters) aiFilters else filters

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // LazyRow — 80% ширины
        LazyRow(
            modifier = Modifier
                .weight(0.85f)
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(currentFilters) { effect ->
                FilterItem(
                    effect = effect,
                    isSelected = selectedFilter == effect.title,
                    onClick = {
                        selectedFilter = effect.title
                        onEffectSelected(effect)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Кнопка — 20% ширины
        Button(
            onClick = { showingAIFilters = !showingAIFilters },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(0.15f)
                .height(64.dp)
        ) {
            Text(
                text = if (showingAIFilters) "Стандартные" else "AI",
                style = MaterialTheme.typography.labelSmall,
                )
        }
    }
}

fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun FilterItem(effect: Effect, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(60.dp) // шире, чтобы влезал текст
            .height(64.dp), // выше, чтобы влезла иконка и текст
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else Color.White
//                MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp)
        ) {
            Icon(
                imageVector = effect.icon,
                contentDescription = effect.title,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
            Box(
                modifier = Modifier
                    .height(32.dp) // примерно на 2 строки мелкого текста
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = effect.title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

fun saveBitmapToGallery(
    context: Context,
    bitmap: Bitmap,
    filename: String = "edited_image_${System.currentTimeMillis()}.jpg"
): Uri? {
    val resolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/EditorDip")
        put(MediaStore.Images.Media.IS_PENDING, 1)
    }

    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    if (uri != null) {
        try {
            resolver.openOutputStream(uri)?.use { out ->
                val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                if (success) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)

                    Toast.makeText(context, "Фото успешно сохранено 🎉", Toast.LENGTH_SHORT).show()
                    return uri // Возвращаем Uri сохранённого файла
                } else {
                    Toast.makeText(context, "Ошибка при сохранении изображения", Toast.LENGTH_SHORT).show()
                    return null
                }
            } ?: run {
                Toast.makeText(context, "Не удалось открыть OutputStream", Toast.LENGTH_SHORT).show()
                return null
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
            return null
        }
    } else {
        Toast.makeText(context, "Не удалось создать файл для сохранения", Toast.LENGTH_SHORT).show()
        return null
    }
}



@Composable
fun FilterSliderSection(
    showSliderById: Int,
    selectedFilter: Effect?,
    sliderLabel: String,
    sliderValue: Float,
    onSliderValueChange: (Float) -> Unit,
    sliderValueRange: ClosedFloatingPointRange<Float>,
    onClickOk: () -> Unit,
    imageUris: List<Uri> = emptyList(),
    selectedImageIndex: Int? = null,
    onImageSelect: (Int) -> Unit = {},
    onAddImageClick: () -> Unit = {},
    drawShape: DrawMode = DrawMode.LINE,
    onDrawShapeSelected: (DrawMode) -> Unit = {}

) {
    when (showSliderById) {
        SHOW_STANDART_SLIDER, SHOW_AI_SLIDER -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$sliderLabel: ${sliderValue.toInt()}",
                        modifier = Modifier.weight(1f)
                    )

                    if (showSliderById == SHOW_AI_SLIDER) {
                        Button(onClick = onClickOk) {
                            Text("OK")
                        }
                    }
                }

                Slider(
                    value = sliderValue,
                    onValueChange = { newValue ->
                        onSliderValueChange(newValue)
                        if (showSliderById == SHOW_STANDART_SLIDER) {
                            selectedFilter?.onClickOK?.invoke()
                        }
                    },
                    valueRange = sliderValueRange
                )
            }
        }

        SHOW_AI_TRANSFER -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Горизонтальный список с изображениями и кнопкой "+"
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // "+" кнопка
                    item {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Gray)
                                .clickable { onAddImageClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Добавить")
                        }
                    }

                    itemsIndexed(imageUris) { index, uri ->
                        val painter = rememberAsyncImagePainter(model = uri)

                        val isSelected = index == selectedImageIndex

                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onImageSelect(index) }
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Кнопка OK
                Button(
                    onClick = onClickOk,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("OK")
                }
            }
        }
        SHOW_CROP_PANEL -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onClickOk) {
                    Text("Crop")
                }
            }
        }
        SHOW_DRAW_PANEL -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Выберите фигуру для рисования:")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { onDrawShapeSelected(DrawMode.LINE) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (drawShape == DrawMode.LINE) Color.Blue else Color.LightGray
                        )
                    ) {
                        Text("Линия", color = Color.White)
                    }

                    Button(
                        onClick = { onDrawShapeSelected(DrawMode.RECTANGLE) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (drawShape == DrawMode.RECTANGLE) Color.Blue else Color.LightGray
                        )
                    ) {
                        Text("Прямоугольник", color = Color.White)
                    }

                    Button(
                        onClick = { onDrawShapeSelected(DrawMode.CIRCLE) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (drawShape == DrawMode.CIRCLE) Color.Blue else Color.LightGray
                        )
                    ) {
                        Text("Круг", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onClickOk,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("OK")
                }
            }
        }

    }
}

fun getDrawableUris(context: Context): List<Uri> {
    val resources = listOf(
        R.drawable.images,
        R.drawable.images__1_,
        R.drawable._10fb113984c0af39a930d4747bc3c1d__2_,
        R.drawable.kartina_krik_1,
        R.drawable.francis_picabia__1913__udnie_young_american_girl__the_dance__oil
    )

    return resources.map { resId ->
        Uri.parse("android.resource://${context.packageName}/$resId")
    }
}

