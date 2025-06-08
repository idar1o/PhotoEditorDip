package com.example.photoeditordip.editordip.presentation.ai_tools

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.photoeditordip.R

data class AITool(
    val title: String,
    val description: String,
    val color: Color,
    val originalPic: Painter,
    val editedPic: Painter,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIToolboxScreen() {
    val tools = listOf(
        AITool(
            "AI Avatar Generator",
            "Turn your photo or selfie into an AI avatar",
            Color(0xFFFF6B6B),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01)
        ),
        AITool(
            "AI Photo Generator",
            "Create multiple variations from a single photo",
            Color(0xFF4ECDC4),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01)
        ),
        AITool(
            "AI Magic Eraser Photo",
            "Remove unwanted objects from a photo in just one tap",
            Color(0xFFA8DADC),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01)
        ),
        AITool(
            "AI Background Remover",
            "Remove background from a photo in just one click",
            Color(0xFFFFBE0B),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01)
        ),
        AITool(
            "AI Photo Recoloring",
            "Change the color of the image with a variety of styles",
            Color(0xFF6A4C93),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01)
        ),
        AITool(
            "AI Old Photo Restoration",
            "Enhance the quality of old photos so they look great",
            Color(0xFF1A535C),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01)
        ),
        AITool(
            "AI Old Photo Colorization",
            "Colorize old photos so it looks more alive",
            Color(0xFFFF6B6B),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01)
        ),
        AITool(
            "Extend Images",
            "Change the aspect ratio of your image with one click",
            Color(0xFF4ECDC4),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01)
        ),
        AITool(
            "Batch Enhance Photos",
            "Enhance a batch of photos at once",
            Color(0xFFA8DADC),
            originalPic = painterResource(id = R.drawable.beautiful_girl_2829997_1280),
            editedPic = painterResource(id = R.drawable.photo_2025_06_05_15_18_01))
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
                AIToolCard(tool = tools[index])
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun AIToolCard(tool: AITool) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clickable { /* Handle tool click */ },
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
            Image(
                painter = tool.originalPic,
                contentDescription = "${tool.title} original image",
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.White, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
