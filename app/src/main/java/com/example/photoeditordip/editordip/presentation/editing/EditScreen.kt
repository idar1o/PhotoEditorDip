package com.example.photoeditordip.presentation.editing

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.photoeditordip.editordip.presentation.editing.components.QuarterCircleButton
import com.example.photoeditordip.utils.createTempFile
import com.example.photoeditordip.utils.decodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navController: NavController,
    imageUri: Uri?,
    viewModel: EditViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var displayBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val uiState by viewModel.uiState.collectAsState()

    // Store temp file for later use
    var tempFile by remember { mutableStateOf<File?>(null) }

    // Initialize tempFile when screen loads
    LaunchedEffect(key1 = imageUri) {
        imageUri?.let {
            tempFile = createTempFile(context, it)
        }
    }

    // Handle UI state changes
    when (val state = uiState) {
        is EditScreenState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
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
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    // States for sliders
    var showSlider by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableStateOf(0f) }
    var sliderValueRange by remember { mutableStateOf(-100f..100f) }
    var currentSliderFunction by remember { mutableStateOf<((Float) -> Unit)?>(null) }
    var sliderLabel by remember { mutableStateOf("") }

    // Load image
    LaunchedEffect(imageUri) {
        imageUri?.let {
            try {
                withContext(Dispatchers.IO) {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    originalBitmap = bitmap
                    // Initialize display bitmap with original image
                    bitmap?.let { safeBitmap ->
                        displayBitmap = safeBitmap.copy(safeBitmap.config ?: Bitmap.Config.ARGB_8888, true)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Initialize OpenCV
    LaunchedEffect(Unit) {
        OpenCVLoader.initDebug()
    }

    // Function to apply grayscale filter
    fun applyGrayscaleFilter() {
        originalBitmap?.let { original ->
            // Fixed null safety issue with config
            val config = original.config ?: Bitmap.Config.ARGB_8888
            val result = original.copy(config, true)
            val mat = Mat()
            Utils.bitmapToMat(original, mat)
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGBA)
            Utils.matToBitmap(mat, result)
            displayBitmap = result
        }
    }

    // Function to apply sepia
    fun applySepiaFilter() {
        originalBitmap?.let { original ->
            // Fixed null safety issue with config
            val config = original.config ?: Bitmap.Config.ARGB_8888
            val result = original.copy(config, true)
            val mat = Mat()
            Utils.bitmapToMat(original, mat)

            // Convert to grayscale
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)

            // Apply sepia effect
            val sepiaMat = Mat(mat.rows(), mat.cols(), mat.type())
            Core.convertScaleAbs(mat, sepiaMat, 1.0, 20.0)

            val bgrMat = Mat()
            Imgproc.cvtColor(sepiaMat, bgrMat, Imgproc.COLOR_GRAY2BGR)

            Utils.matToBitmap(bgrMat, result)
            displayBitmap = result
        }
    }

    // Function to apply blur with OpenCV
    fun applyLocalBlurFilter() {
        originalBitmap?.let { original ->
            // Fixed null safety issue with config
            val config = original.config ?: Bitmap.Config.ARGB_8888
            val result = original.copy(config, true)
            val mat = Mat()
            Utils.bitmapToMat(original, mat)
            Imgproc.GaussianBlur(mat, mat, org.opencv.core.Size(21.0, 21.0), 0.0)
            Utils.matToBitmap(mat, result)
            displayBitmap = result
        }
    }

    // Function to apply server-side blur using ViewModel
    fun applyServerBlurFilter() {
        tempFile?.let { file ->
            // Call the API through ViewModel
            viewModel.applyBlur(file)
        } ?: run {
            // Handle case where tempFile is null
            Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
        }
    }

    // Function for brightness adjustment
    fun setupBrightnessAdjustment() {
        showSlider = true
        sliderValue = 0f
        sliderValueRange = -100f..100f
        sliderLabel = "Brightness"
        currentSliderFunction = { value ->
            originalBitmap?.let { original ->
                // Fixed null safety issue with config
                val config = original.config ?: Bitmap.Config.ARGB_8888
                val result = original.copy(config, true)
                val mat = Mat()
                Utils.bitmapToMat(original, mat)
                mat.convertTo(mat, -1, 1.0, value.toDouble())
                Utils.matToBitmap(mat, result)
                displayBitmap = result
            }
        }
    }

    // Function for contrast adjustment
    fun setupContrastAdjustment() {
        showSlider = true
        sliderValue = 1f
        sliderValueRange = 0.5f..2f
        sliderLabel = "Contrast"
        currentSliderFunction = { value ->
            originalBitmap?.let { original ->
                // Fixed null safety issue with config
                val config = original.config ?: Bitmap.Config.ARGB_8888
                val result = original.copy(config, true)
                val mat = Mat()
                Utils.bitmapToMat(original, mat)
                mat.convertTo(mat, -1, value.toDouble(), 0.0)
                Utils.matToBitmap(mat, result)
                displayBitmap = result
            }
        }
    }

    // Function for saturation adjustment
    fun setupSaturationAdjustment() {
        showSlider = true
        sliderValue = 1f
        sliderValueRange = 0f..3f
        sliderLabel = "Saturation"
        currentSliderFunction = { value ->
            originalBitmap?.let { original ->
                // Fixed null safety issue with config
                val config = original.config ?: Bitmap.Config.ARGB_8888
                val result = original.copy(config, true)
                val mat = Mat()
                Utils.bitmapToMat(original, mat)

                // Convert to HSV
                val hsvMat = Mat()
                Imgproc.cvtColor(mat, hsvMat, Imgproc.COLOR_BGR2HSV)

                // Split channels
                val channels = ArrayList<Mat>()
                Core.split(hsvMat, channels)

                // Multiply saturation channel by coefficient
                Core.multiply(channels[1], org.opencv.core.Scalar(value.toDouble()), channels[1])

                // Merge back
                Core.merge(channels, hsvMat)

                // Convert back to BGR
                Imgproc.cvtColor(hsvMat, mat, Imgproc.COLOR_HSV2BGR)

                Utils.matToBitmap(mat, result)
                displayBitmap = result
            }
        }
    }

    // Function for sharpening
    fun applySharpenFilter() {
        originalBitmap?.let { original ->
            // Fixed null safety issue with config
            val config = original.config ?: Bitmap.Config.ARGB_8888
            val result = original.copy(config, true)
            val mat = Mat()
            Utils.bitmapToMat(original, mat)

            // Create kernel for sharpening
            val kernel = Mat(3, 3, org.opencv.core.CvType.CV_32F)
            kernel.put(0, 0, 0.0, -1.0, 0.0, -1.0, 5.0, -1.0, 0.0, -1.0, 0.0)

            // Apply filter
            Imgproc.filter2D(mat, mat, -1, kernel)

            Utils.matToBitmap(mat, result)
            displayBitmap = result
        }
    }

    // Function for rotation
    fun setupRotationAdjustment() {
        showSlider = true
        sliderValue = 0f
        sliderValueRange = -180f..180f
        sliderLabel = "Rotation (degrees)"
        currentSliderFunction = { value ->
            originalBitmap?.let { original ->
                val mat = Mat()
                Utils.bitmapToMat(original, mat)

                // Get rotation matrix
                val center = org.opencv.core.Point(mat.cols() / 2.0, mat.rows() / 2.0)
                val rotationMatrix = Imgproc.getRotationMatrix2D(center, value.toDouble(), 1.0)

                // Apply rotation
                val rotated = Mat()
                Imgproc.warpAffine(mat, rotated, rotationMatrix, mat.size())

                // Fixed null safety issue with config
                val config = original.config ?: Bitmap.Config.ARGB_8888
                val result = Bitmap.createBitmap(rotated.cols(), rotated.rows(), config)
                Utils.matToBitmap(rotated, result)
                displayBitmap = result
            }
        }
    }

    // Available filters
    val filters = listOf(
        "Original", "Black & White", "Sepia", "Brightness",
        "Contrast", "Saturation", "Blur", "AI Blur", "Sharpen", "Rotation"
    )

    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Photo Editor") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = {
//                        // Save photo function
//                        displayBitmap?.let { bitmap ->
//                            try {
//                                val filename = "edited_image_${System.currentTimeMillis()}.jpg"
//                                val file = File(context.getExternalFilesDir(null), filename)
//                                FileOutputStream(file).use { outputStream ->
//                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//                                    outputStream.flush()
//                                }
//                                Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show()
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }) {
//                        Icon(Icons.Default.Done, contentDescription = "Save")
//                    }
//                }
//            )
//        }
    ) { paddingValues ->
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
                    .background(Color.Black),
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .padding(16.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Левая иконка (назад)
                    QuarterCircleButton(
                        icon = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        onClick = { navController.popBackStack() },
                        topLeft = true
                    )

                    // Правая иконка (сохранить)
                    QuarterCircleButton(
                        icon = Icons.Default.Done,
                        contentDescription = "Save",
                        onClick = {
                            displayBitmap?.let { bitmap ->
                                try {
                                    val filename = "edited_image_${System.currentTimeMillis()}.jpg"
                                    val file = File(context.getExternalFilesDir(null), filename)
                                    FileOutputStream(file).use { outputStream ->
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                                        outputStream.flush()
                                    }
                                    Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        topLeft = false
                    )
                }


            }

            // Slider area (if needed)
            if (showSlider) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("$sliderLabel: ${sliderValue.toInt()}")
                    Slider(
                        value = sliderValue,
                        onValueChange = { newValue ->
                            sliderValue = newValue
                            currentSliderFunction?.invoke(newValue)
                        },
                        valueRange = sliderValueRange
                    )
                }
            }

            // Tools panel (bottom of screen)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 8.dp)
            ) {
                // List of available filters
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(filters) { filter ->
                        FilterItem(
                            name = filter,
                            isSelected = selectedFilter == filter,
                            onClick = {
                                selectedFilter = filter
                                when (filter) {
                                    "Brightness" -> setupBrightnessAdjustment()
                                    "Contrast" -> setupContrastAdjustment()
                                    "Saturation" -> setupSaturationAdjustment()
                                    "Black & White" -> {
                                        showSlider = false
                                        applyGrayscaleFilter()
                                    }
                                    "Sepia" -> {
                                        showSlider = false
                                        applySepiaFilter()
                                    }
                                    "Blur" -> {
                                        showSlider = false
                                        applyLocalBlurFilter()
                                    }
                                    "AI Blur" -> {
                                        showSlider = false
                                        applyServerBlurFilter()
                                    }
                                    "Sharpen" -> {
                                        showSlider = false
                                        applySharpenFilter()
                                    }
                                    "Rotation" -> setupRotationAdjustment()
                                    "Original" -> {
                                        showSlider = false
                                        originalBitmap?.let {
                                            // Fixed null safety issue with config
                                            val config = it.config ?: Bitmap.Config.ARGB_8888
                                            displayBitmap = it.copy(config, true)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterItem(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .padding(4.dp)
    ) {
        Card(
            modifier = Modifier
                .size(60.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            ),
            onClick = onClick
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Icons for filters
                when (name) {
                    "Sepia" -> Icon(Icons.Default.Add, contentDescription = name)
                    "Sharpen" -> Icon(Icons.Default.Lock, contentDescription = name)
                    "Black & White" -> Icon(Icons.Default.AccountCircle, contentDescription = name)
                    "Brightness" -> Icon(Icons.Default.Star, contentDescription = name)
                    "Blur" -> Icon(Icons.Default.KeyboardArrowDown, contentDescription = name)
                    "AI Blur" -> Icon(Icons.Default.KeyboardArrowDown, contentDescription = name)
                    "Contrast" -> Icon(Icons.Default.KeyboardArrowUp, contentDescription = name)
                    "Crop" -> Icon(Icons.Default.Edit, contentDescription = name)
                    "Rotation" -> Icon(Icons.Default.Refresh, contentDescription = name)
                    else -> Text(name.first().toString())
                }
            }
        }

        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}