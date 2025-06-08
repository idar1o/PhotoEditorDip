package com.example.photoeditordip.di


import com.example.photoeditordip.core.data.network.ApiConstants
import com.example.photoeditordip.data.api.ApiService
import com.example.photoeditordip.data.repository.ImageRepositoryImpl
import com.example.photoeditordip.domain.usecase.ApplyBlurUseCase
import com.example.photoeditordip.editordip.domain.repository.ImageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
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
}