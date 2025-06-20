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

    override suspend fun applyStyleTransferEffect(
        contentFile: File,
        styleFile: File
    ): Result<String> {
        Log.d("ImageRepository", "applyStyleTransferEffect called")
        Log.d("ImageRepository", "File details: name=${contentFile.name}, size=${contentFile.length()}, path=${contentFile.absolutePath}")

        return try {

            // Create multipart request for the image file
            val contentFileImage = contentFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val contentFileRequest = MultipartBody.Part.createFormData("content_file", contentFile.name, contentFileImage)

            val styleFileImage = styleFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val styleFileRequest = MultipartBody.Part.createFormData("style_file", styleFile.name, styleFileImage)
            Log.d("ImageRepository", "Image part created successfully")

            Log.d("ImageRepository", "Making API call...")

            // ВАЖНО: Изменяем интерфейс API для получения ResponseBody вместо String
            val response = apiService.applyStyleTransferEffect(
                contentFile = contentFileRequest,
                styleFile = styleFileRequest
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
            Log.e("ImageRepository", "Exception in applyStyleTransferEffect: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun applyBlurBackgroundEffect(imageFile: File, blurIntensity: Int): Result<String> {
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

    override suspend fun applyWarmColdEffect(imageFile: File, warmth: Double, coldness: Double): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val warmthPart = warmth.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val coldnessPart = coldness.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.applyWarmColdEffect(imagePart, warmthPart, coldnessPart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyCartoonifyEffect(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.applyCartoonifyEffect(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyPencilColorEffect(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.applyPencilColorEffect(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyOilPaintingEffect(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.applyOilPaintingEffect(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyWaterColorEffect(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.applyWaterColorEffect(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyPencilEffect(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.applyPencilEffect(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyContrastEffect(imageFile: File, contrast: Int): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val contrastPart = contrast.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.applyContrastEffect(imagePart, contrastPart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyBrightnessEffect(imageFile: File, brightness: Int): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val brightnessPart = brightness.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.applyBrightnessEffect(imagePart, brightnessPart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun invertColors(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.invertColors(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyGrayScaleEffect(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.applyGrayScaleEffect(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyNegativeEffect(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.applyNegativeEffect(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyTintEffect(imageFile: File, r: Int, g: Int, b: Int): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val rPart = r.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val gPart = g.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val bPart = b.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.applyTintEffect(imagePart, rPart, gPart, bPart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applySolarizeEffect(imageFile: File, threshold: Int): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val thresholdPart = threshold.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.applySolarizeEffect(imagePart, thresholdPart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyBWNegativeEffect(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.applyBWNegativeEffect(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyHDREffect(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.applyHDREffect(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyPixelateEffect(imageFile: File, blockSize: Int): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val blockSizePart = blockSize.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val response = apiService.applyPixelateEffect(imagePart, blockSizePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyVignetteEffect(imageFile: File, strength: Int): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val strengthPart = strength.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val response = apiService.applyVignetteEffect(imagePart, strengthPart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyFilmGrainEffect(imageFile: File, intensity: Double): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val intensityPart = intensity.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val response = apiService.applyFilmGrainEffect(imagePart, intensityPart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeBackground(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.removeBackground(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyCannyEffect(
        imageFile: File,
        threshold1: Int,
        threshold2: Int
    ): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val threshold1Part = threshold1.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val threshold2Part = threshold2.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val response = apiService.applyCannyEffect(imagePart, threshold1Part, threshold2Part)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applySobelEffect(imageFile: File, kernelSize: Int): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val kernelSizePart = kernelSize.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val response = apiService.applySobelEffect(imagePart, kernelSizePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyLaplacianEffect(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.applyLaplacianEffect(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyContoursEffect(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.applyContoursEffect(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applySharpenEffect(imageFile: File, strength: Double): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val strengthPart = strength.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val response = apiService.applySharpenEffect(imagePart, strengthPart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyReduceNoiseEffect(
        imageFile: File,
        noiseStrength: Int
    ): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val noiseStrengthPart = noiseStrength.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val response = apiService.applyReduceNoiseEffect(imagePart, noiseStrengthPart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyEnhanceDetailsEffect(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.applyEnhanceDetailsEffect(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyEqualizeExposureEffect(imageFile: File): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.applyEqualizeExposureEffect(imagePart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyClaheEffect(imageFile: File, clipLimit: Int): Result<String> {
        return try {
            val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
            val clipLimitPart = clipLimit.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.applyClaheEffect(imagePart, clipLimitPart)

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val imageBytes = responseBody.bytes()
                    val base64String = "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    Result.success(base64String)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API error: ${response.code()} - ${response.message()}. Details: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}