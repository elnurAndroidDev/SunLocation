package com.isaevapps.domain.usecase

import com.isaevapps.domain.model.Weather
import com.isaevapps.domain.repository.WeatherRepository
import com.isaevapps.domain.result.NetworkError
import com.isaevapps.domain.result.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class GetCurrentWeatherUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: GetCurrentWeatherUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetCurrentWeatherUseCase(repository)
    }

    @Test
    fun `invoke calls repository getWeather method`() = runTest {
        // given
        val lat = 55.75
        val lon = 37.62
        val expectedWeather =
            Weather("City", "Country", 20, "Clear", LocalDateTime.now(), LocalDateTime.now())
        val expectedResult: Result<Weather, NetworkError> = Result.Success(expectedWeather)
        coEvery { repository.getWeather(lat, lon) } returns expectedResult

        // when
        val result = useCase(lat, lon)

        // then
        assertEquals(expectedResult, result)
        coVerify { repository.getWeather(lat, lon) }
    }

    @Test
    fun `invoke returns error from repository`() = runTest {
        // given
        val lat = 55.75
        val lon = 37.62
        val expectedResult: Result<Weather, NetworkError> = Result.Error(NetworkError.NO_INTERNET)

        coEvery { repository.getWeather(lat, lon) } returns expectedResult

        // when
        val result = useCase(lat, lon)

        // then
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { repository.getWeather(lat, lon) }
    }


}