package com.example.photoeditordip.di


import android.content.Context
import android.content.SharedPreferences
import com.example.photoeditordip.core.data.network.ApiConstants
import com.example.photoeditordip.data.api.ApiService
import com.example.photoeditordip.data.repository.ImageRepositoryImpl
import com.example.photoeditordip.domain.usecase.ApplyBlurUseCase
import com.example.photoeditordip.editordip.data.repository.PresetRepositoryImpl
import com.example.photoeditordip.editordip.data.repository.SharedPrefsPresetStorage
import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import com.example.photoeditordip.editordip.domain.repository.PresetRepository
import com.example.photoeditordip.editordip.domain.repository.PresetStorage
import com.example.photoeditordip.editordip.domain.usecases.ApplyBlurBackgroundUseCase
import com.example.photoeditordip.editordip.domain.usecases.ApplyStyleTransferUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("photo_editor_presets", Context.MODE_PRIVATE)
    }

    @Provides
    fun providePresetStorage(sharedPreferences: SharedPreferences): PresetStorage {
        return SharedPrefsPresetStorage(sharedPreferences)
    }

    @Provides
    fun providePresetRepository(storage: PresetStorage): PresetRepository {
        return PresetRepositoryImpl(storage)
    }
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideImageRepository(apiService: ApiService): ImageRepository {
        return ImageRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideApplyBlurUseCase(repository: ImageRepository): ApplyBlurUseCase {
        return ApplyBlurUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideApplyBackgroundBlurUseCase(repository: ImageRepository): ApplyBlurBackgroundUseCase {
        return ApplyBlurBackgroundUseCase(repository)
    }
    @Provides
    @Singleton
    fun provideApplyStyleTransferEffectUseCase(repository: ImageRepository): ApplyStyleTransferUseCase {
        return ApplyStyleTransferUseCase(repository)
    }
}