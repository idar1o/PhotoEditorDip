package com.example.photoeditordip.data.repository

import android.util.Log
import com.example.photoeditordip.data.api.ApiService
import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

import android.util.Base64
import okhttp3.ResponseBody

class ImageRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ImageRepository {

    override suspend fun applyBlurEffect(imageFile: File): Result<String> {
        Log.d("ImageRepository", "applyBlurEffect called")
        Log.d("ImageRepository", "File details: name=${imageFile.name}, size=${imageFile.length()}, path=${imageFile.absolutePath}")

        return try {

            // Create multipart request for the image file
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
            Log.d("ImageRepository", "Image part created successfully")

            // Create parts for additional parameters
            val blurTypePart = "gaussian".toRequestBody("text/plain".toMediaTypeOrNull())
            val kernelSizePart = 15.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val diameterPart = 9.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val sigmaColorPart = 75.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val sigmaSpacePart = 75.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            Log.d("ImageRepository", "All request parts created")

            Log.d("ImageRepository", "Making API call...")

            // ВАЖНО: Изменяем интерфейс API для получения ResponseBody вместо String
            val response = apiService.applyBlurEffect(
                file = imagePart,
                blurType = blurTypePart,
                kernelSize = kernelSizePart,
                diameter = diameterPart,
                sigmaColor = sigmaColorPart,
                sigmaSpace = sigmaSpacePart
            )

            Log.d("ImageRepository", "API call completed")
            Log.d("ImageRepository", "Response code: ${response.code()}")
            Log.d("ImageRepository", "Response successful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("ImageRepository", "Response body is null: ${responseBody == null}")

                if (responseBody != null) {
                    // Конвертируем бинарные данные в Base64
                    val imageBytes = responseBody.bytes()
                    Log.d("ImageRepository", "Image bytes length: ${imageBytes.size}")

                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Log.d("ImageRepository", "Base64 string length: ${base64String.length}")
                    Log.d("ImageRepository", "Base64 preview: ${base64String.take(100)}...")
                    Log.d("ImageRepository", "Returning success result")

                    Result.success(base64String)
                } else {
                    Log.e("ImageRepository", "Response body is null")
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ImageRepository", "API error: ${response.code()} - ${response.message()}")
                Log.e("ImageRepository", "Error body: $errorBody")
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("ImageRepository", "Exception in applyBlurEffect: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun applyBlurBackgroundEffect(imageFile: File): Result<String> {
        Log.d("ImageRepository", "applyBlurBackgroundEffect called")
        Log.d("ImageRepository", "File details: name=${imageFile.name}, size=${imageFile.length()}, path=${imageFile.absolutePath}")

        return try {

            // Create multipart request for the image file
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
            Log.d("ImageRepository", "Image part created successfully")

//            // Create parts for additional parameters
//            val blurTypePart = "gaussian".toRequestBody("text/plain".toMediaTypeOrNull())
//            val kernelSizePart = 15.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//            val diameterPart = 9.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//            val sigmaColorPart = 75.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//            val sigmaSpacePart = 75.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//            Log.d("ImageRepository", "All request parts created")

            Log.d("ImageRepository", "Making API call...")

            // ВАЖНО: Изменяем интерфейс API для получения ResponseBody вместо String
            val response = apiService.applyBlurBackgroundEffect(
                file = imagePart,
                blurIntensity = 51.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            )

            Log.d("ImageRepository", "API call completed")
            Log.d("ImageRepository", "Response code: ${response.code()}")
            Log.d("ImageRepository", "Response successful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("ImageRepository", "Response body is null: ${responseBody == null}")

                if (responseBody != null) {
                    // Конвертируем бинарные данные в Base64
                    val imageBytes = responseBody.bytes()
                    Log.d("ImageRepository", "Image bytes length: ${imageBytes.size}")

                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Log.d("ImageRepository", "Base64 string length: ${base64String.length}")
                    Log.d("ImageRepository", "Base64 preview: ${base64String.take(100)}...")
                    Log.d("ImageRepository", "Returning success result")

                    Result.success(base64String)
                } else {
                    Log.e("ImageRepository", "Response body is null")
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ImageRepository", "API error: ${response.code()} - ${response.message()}")
                Log.e("ImageRepository", "Error body: $errorBody")
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("ImageRepository", "Exception in applyBlurEffect: ${e.message}", e)
            Result.failure(e)
        }
    }
}