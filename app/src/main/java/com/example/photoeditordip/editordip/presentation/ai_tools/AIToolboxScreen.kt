package com.example.photoeditordip.editordip.presentation.ai_tools

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.photoeditordip.R
import com.example.photoeditordip.editordip.presentation.components.ImagePickerButton
import com.example.photoeditordip.editordip.presentation.editing.PresetHolderViewModel
import com.example.photoeditordip.navigation.Screen

data class AITool(
    val title: String,
    val description: String,
    val color: Color,
    val originalPic: Painter,
    val editedPic: Painter,
    val aiCode: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIToolboxScreen(
    navController: NavController
) {
    val tools = listOf(
        AITool(
            title = "AI Style Transfer",
            description = "Apply artistic styles to your photo using neural networks",
            color = Color(0xFF6A4C93),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyStyleTransfer"
        ),
        AITool(
            title = "AI Pixelate Effect",
            description = "Pixelate your image to create a retro or mosaic style",
            color = Color(0xFF4ECDC4),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyPixelateEffect"
        ),
        AITool(
            title = "AI Background Remover",
            description = "Erase the background from your photo instantly",
            color = Color(0xFFFFBE0B),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyRemoveBackground"
        ),
        AITool(
            title = "AI Cartoonify",
            description = "Transform your photo into a cartoon-style image",
            color = Color(0xFF6A4C93),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyCartoonify"
        ),
        AITool(
            title = "AI Enhance Details",
            description = "Improve image clarity by enhancing fine details",
            color = Color(0xFF1A535C),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyEnhanceDetailsEffect"
        ),
        AITool(
            title = "AI CLAHE Filter",
            description = "Enhance contrast using adaptive histogram equalization",
            color = Color(0xFFFF6B6B),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyClaheEffect"
        ),
        AITool(
            title = "AI HDR Filter",
            description = "Simulate high dynamic range for vivid images",
            color = Color(0xFF4ECDC4),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyHDR"
        ),
        AITool(
            title = "AI Sharpen Effect",
            description = "Sharpen blurry areas to improve edge definition",
            color = Color(0xFFA8DADC),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applySharpenEffect"
        ),
        AITool(
            title = "AI Reduce Noise",
            description = "Remove visual noise while preserving details",
            color = Color(0xFF1A535C),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyReduceNoiseEffect"
        ),
        AITool(
            title = "AI Vignette Effect",
            description = "Darken edges to create a vignette focus effect",
            color = Color(0xFFFF6B6B),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyVignetteEffect"
        ),
        AITool(
            title = "AI Solarize Effect",
            description = "Apply surreal lighting with solarization",
            color = Color(0xFF6A4C93),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applySolarizeEffect"
        ),
        AITool(
            title = "AI Sobel Edge Detection",
            description = "Highlight edges using Sobel operator",
            color = Color(0xFF1A535C),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applySobelEffect"
        ),
        AITool(
            title = "AI Laplacian Filter",
            description = "Detect edges using Laplacian operator",
            color = Color(0xFFA8DADC),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyLaplacianEffect"
        ),
        AITool(
            title = "AI Film Grain",
            description = "Add realistic film grain effect to your image",
            color = Color(0xFF6A4C93),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyFilmGrainEffect"
        ),
        AITool(
            title = "AI Equalize Exposure",
            description = "Automatically balance brightness across the photo",
            color = Color(0xFFFFBE0B),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyEqualizeExposureEffect"
        ),
        AITool(
            title = "AI Negative Effect",
            description = "Invert colors to create a photographic negative",
            color = Color(0xFFFF6B6B),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyNegativeEffect"
        ),
        AITool(
            title = "AI Pencil Sketch",
            description = "Convert your photo into a pencil sketch drawing",
            color = Color(0xFF4ECDC4),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyPencil"
        ),
        AITool(
            title = "AI Colored Pencil",
            description = "Draw photo using colored pencil style",
            color = Color(0xFFA8DADC),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyPencilColor"
        ),
        AITool(
            title = "AI Watercolor",
            description = "Give your photo a painted watercolor effect",
            color = Color(0xFF1A535C),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyWaterColor"
        ),
        AITool(
            title = "AI Oil Painting",
            description = "Convert photo into an oil painting style",
            color = Color(0xFF6A4C93),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyOilPainting"
        ),
        AITool(
            title = "AI Background Blur",
            description = "Blur the background while keeping the subject clear",
            color = Color(0xFFFFBE0B),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyBackgroundBlur"
        ),
        AITool(
            title = "AI General Blur",
            description = "Apply overall blur effect to the entire image",
            color = Color(0xFFA8DADC),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01),
            aiCode = "applyBlur"
        )
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AI Toolbox",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
//        bottomBar = {
//            GlassmorphicBottomNavigation(hazeState, navController = navController)
//        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            items(tools.size) { index ->
                AIToolCard(tool = tools[index], navController)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun AIToolCard(tool: AITool, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clickable {

            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Edited Image (фон карточки)
            Image(
                painter = tool.editedPic,
                contentDescription = "${tool.title} edited image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )

            // Title & Description (в верхней части)
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = tool.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }

            // Original image (в нижнем углу)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                Image(
                    painter = tool.originalPic,
                    contentDescription = "${tool.title} original image",
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.End)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                ImagePickerButton(
                    modifier = Modifier.fillMaxWidth(),
                    onImagePicked = { uri: Uri? ->
                        uri?.let {
                            val encodedUri = Uri.encode(it.toString())
                            val encodedTool = Uri.encode(tool.aiCode)
                            navController.navigate(Screen.EditParam(imageUri = encodedUri, aiTool = encodedTool, origin = "ai_toolbox").route, )
                        }
                    },
                    buttonText = "Try"
                )

            }
        }
    }
}
