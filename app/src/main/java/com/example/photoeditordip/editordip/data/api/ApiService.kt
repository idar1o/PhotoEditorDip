package com.example.photoeditordip.data.api

import com.example.photoeditordip.core.data.network.ApiConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST(ApiConstants.BLUR)
    suspend fun applyBlurEffect(
        @Part file: MultipartBody.Part,
        @Part("blur_type") blurType: RequestBody,
        @Part("kernel_size") kernelSize: RequestBody,
        @Part("diameter") diameter: RequestBody,
        @Part("sigma_color") sigmaColor: RequestBody,
        @Part("sigma_space") sigmaSpace: RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.BLUR_BACKGROUND)
    suspend fun applyBlurBackgroundEffect(
        @Part file: MultipartBody.Part,
        @Part("blur_intensity") blurIntensity: RequestBody,
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.STYLE_TRANSFER)
    suspend fun applyStyleTransferEffect(
        @Part contentFile: MultipartBody.Part,
        @Part styleFile: MultipartBody.Part,
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.WARM_COLD)
    suspend fun applyWarmColdEffect(
        @Part file: MultipartBody.Part,
        @Part("warmth") warmth: RequestBody,
        @Part("coldness") coldness: RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.CARTOONIFY)
    suspend fun applyCartoonifyEffect(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.PENCIL_COLOR)
    suspend fun applyPencilColorEffect(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>


    @Multipart
    @POST(ApiConstants.OIL_PAINTING)
    suspend fun applyOilPaintingEffect(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.WATER_COLOR)
    suspend fun applyWaterColorEffect(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.PENCIL)
    suspend fun applyPencilEffect(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.CONTRAST)
    suspend fun applyContrastEffect(
        @Part file: MultipartBody.Part,
        @Part("contrast") contrast: RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.BRIGHTNESS)
    suspend fun applyBrightnessEffect(
        @Part file: MultipartBody.Part,
        @Part("brightness") brightness: RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.INVERT_COLORS)
    suspend fun invertColors(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.GRAY_SCALE)
    suspend fun applyGrayScaleEffect(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.NEGATIVE)
    suspend fun applyNegativeEffect(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.TINT)
    suspend fun applyTintEffect(
        @Part file: MultipartBody.Part,
        @Part("r") r: RequestBody,
        @Part("g") g: RequestBody,
        @Part("b") b: RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.SOLARIZE)
    suspend fun applySolarizeEffect(
        @Part file: MultipartBody.Part,
        @Part("threshold") threshold: RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.BW_NEGATIVE)
    suspend fun applyBWNegativeEffect(
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.HDR)
    suspend fun applyHDREffect(@Part file: MultipartBody.Part): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.PIXELATE)
    suspend fun applyPixelateEffect(
        @Part file: MultipartBody.Part,
        @Part("block_size") blockSize:RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.VIGNETTE)
    suspend fun applyVignetteEffect(
        @Part file: MultipartBody.Part,
        @Part("strength") strength:RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.FILM_GRAIN)
    suspend fun applyFilmGrainEffect(
        @Part file: MultipartBody.Part,
        @Part("intensity") intensity:RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.REMOVE_BACKGROUND)
    suspend fun removeBackground(@Part file: MultipartBody.Part): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.CANNY)
    suspend fun applyCannyEffect(
        @Part file: MultipartBody.Part,
        @Part("threshold1") threshold1:RequestBody,
        @Part("threshold2") threshold2:RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.SOBEL)
    suspend fun applySobelEffect(
        @Part file: MultipartBody.Part,
        @Part("kernel_size") kernelSize:RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.LAPLACIAN)
    suspend fun applyLaplacianEffect(@Part file: MultipartBody.Part): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.CONTOURS)
    suspend fun applyContoursEffect(@Part file: MultipartBody.Part): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.SHARPEN)
    suspend fun applySharpenEffect(
        @Part file: MultipartBody.Part,
        @Part("strength") strength:RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.REDUCE_NOISE)
    suspend fun applyReduceNoiseEffect(
        @Part file: MultipartBody.Part,
        @Part("noise_strength") noiseStrength:RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.ENHANCE_DETAILS)
    suspend fun applyEnhanceDetailsEffect(@Part file: MultipartBody.Part): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.EQUALIZE_EXPOSURE)
    suspend fun applyEqualizeExposureEffect(@Part file: MultipartBody.Part): Response<ResponseBody>

    @Multipart
    @POST(ApiConstants.CLAHE)
    suspend fun applyClaheEffect(
        @Part file: MultipartBody.Part,
        @Part("clip_limit") clipLimit:RequestBody
    ): Response<ResponseBody>
}