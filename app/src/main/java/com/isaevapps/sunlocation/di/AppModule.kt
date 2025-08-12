package com.isaevapps.sunlocation.di

import android.content.Context
import com.isaevapps.data.algorithm.SunCalculator
import com.isaevapps.data.network.ApiKeyQueryInterceptor
import com.isaevapps.data.network.WeatherApi
import com.isaevapps.data.repository.ResourceTimeZoneRepository
import com.isaevapps.data.repository.SunRepositoryImpl
import com.isaevapps.data.repository.WeatherRepositoryImpl
import com.isaevapps.domain.repository.SunRepository
import com.isaevapps.domain.repository.TimeZoneRepository
import com.isaevapps.domain.repository.WeatherRepository
import com.isaevapps.domain.usecase.CalculateSunPositionUseCase
import com.isaevapps.domain.usecase.ExtractCoordinatesUseCase
import com.isaevapps.domain.usecase.GetCurrentWeatherUseCase
import com.isaevapps.domain.utils.CoordinatesParser
import com.isaevapps.domain.utils.DefaultCoordinatesParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(ApiKeyQueryInterceptor()).build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl("https://api.weatherapi.com/").client(okHttpClient).build()

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi = retrofit.create(WeatherApi::class.java)

    @Provides
    @Singleton
    fun provideWeatherRepository(weatherApi: WeatherApi): WeatherRepository {
        return WeatherRepositoryImpl(weatherApi)
    }

    @Provides
    @Singleton
    fun provideGetCurrentWeatherUseCase(repository: WeatherRepository): GetCurrentWeatherUseCase {
        return GetCurrentWeatherUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCoordinatesParser(): CoordinatesParser {
        return DefaultCoordinatesParser()
    }

    @Provides
    @Singleton
    fun provideExtractCoordinatesUseCase(coordinatesParser: CoordinatesParser): ExtractCoordinatesUseCase {
        return ExtractCoordinatesUseCase(coordinatesParser)
    }

    @Provides
    fun provideSunCalculator(): SunCalculator = SunCalculator

    @Provides
    @Singleton
    fun provideSunRepository(sunCalculator: SunCalculator): SunRepository {
        return SunRepositoryImpl(sunCalculator)
    }

    @Provides
    @Singleton
    fun provideTimeZonesRepository(@ApplicationContext context: Context): TimeZoneRepository {
        return ResourceTimeZoneRepository()
    }

    @Provides
    @Singleton
    fun provideCalculateSunPositionUseCase(
        repository: SunRepository, dispatchers: AppDispatchers
    ): CalculateSunPositionUseCase {
        return CalculateSunPositionUseCase(repository, dispatchers.default)
    }


}