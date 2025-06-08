package com.example.photoeditordip.editordip.presentation.editing.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import androidx.compose.ui.geometry.Rect

@Composable
fun QuarterCircleButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    topLeft: Boolean
) {
    Box(
        modifier = Modifier.size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawOval(
                color = Color.Black.copy(alpha = 0.3f),
                size = size
            )
        }

        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.Center)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.White
            )
        }
    }
}



fun applyBrightnessContrast(bitmap: Bitmap, brightness: Double, contrast: Double): Bitmap {
    val src = Mat()
    Utils.bitmapToMat(bitmap, src)
    val dst = Mat(src.size(), src.type())
    // Формула: new_image = image * contrast + brightness
    src.convertTo(dst, -1, contrast, brightness)
    val resultBitmap = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(dst, resultBitmap)
    return resultBitmap
}
fun rotateImage(bitmap: Bitmap, angle: Double): Bitmap {
    val src = Mat()
    Utils.bitmapToMat(bitmap, src)
    val center = Point(src.width() / 2.0, src.height() / 2.0)
    val rotMatrix = Imgproc.getRotationMatrix2D(center, angle, 1.0)
    val dst = Mat()
    Imgproc.warpAffine(src, dst, rotMatrix, src.size())
    val resultBitmap = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(dst, resultBitmap)
    return resultBitmap
}


// Изменение размера изображения с помощью OpenCV
fun resizeImage(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
    val src = Mat()
    Utils.bitmapToMat(bitmap, src)
    val dst = Mat()
    Imgproc.resize(src, dst, Size(newWidth.toDouble(), newHeight.toDouble()))
    val resultBitmap = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(dst, resultBitmap)
    return resultBitmap
}
//
//// Обрезка изображения. Здесь используется прямоугольная область (cropRect)
//fun cropImage(bitmap: Bitmap, cropRect: Rect): Bitmap {
//    val src = Mat()
//    Utils.bitmapToMat(bitmap, src)
//    val cropped = Mat(src, cropRect)
//    val resultBitmap = Bitmap.createBitmap(cropped.cols(), cropped.rows(), Bitmap.Config.ARGB_8888)
//    Utils.matToBitmap(cropped, resultBitmap)
//    return resultBitmap
//}

// Функция для "выравнивания" изображения (пример: коррекция наклона).
// Здесь реализовано как поворот на фиксированный угол (-10°) для демонстрации.
fun alignImage(bitmap: Bitmap): Bitmap {
    return rotateImage(bitmap, -10.0)
}

fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return context.contentResolver.openInputStream(uri)?.use { inputStream ->
        BitmapFactory.decodeStream(inputStream)
    }
}

