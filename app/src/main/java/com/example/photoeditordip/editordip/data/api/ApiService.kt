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
    @POST(ApiConstants.BLUR)
    suspend fun applyBlurBackgroundEffect(
        @Part file: MultipartBody.Part,
        @Part("blur_intensity") blurIntensity: RequestBody,
    ): Response<ResponseBody>
}