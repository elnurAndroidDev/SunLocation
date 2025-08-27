import com.isaevapps.data.network.ConditionDTO
import com.isaevapps.data.network.CurrentDTO
import com.isaevapps.data.network.LocationDTO
import com.isaevapps.data.network.WeatherApi
import com.isaevapps.data.network.WeatherDTO
import com.isaevapps.data.repository.WeatherRepositoryImpl
import com.isaevapps.data.toDomain
import com.isaevapps.domain.model.Weather
import com.isaevapps.domain.result.NetworkError
import com.isaevapps.domain.result.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherRepositoryImplTest {

    private lateinit var weatherApi: WeatherApi
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setUp() {
        weatherApi = mockk()
        repository = WeatherRepositoryImpl(weatherApi)
    }

    @Test
    fun `getWeather success returns domain model`() = runTest {
        // given
        val lat = 10.0
        val lon = 20.0
        val apiResponse = WeatherDTO(
            location = LocationDTO(
                name = "City",
                country = "Country",
                localtime = "2023-08-01 12:00"
            ),
            current = CurrentDTO(
                last_updated = "2023-08-01 12:00",
                temp_c = 25.0,
                condition = ConditionDTO("Sunny")
            )
        )
        val domainModel = apiResponse.toDomain()
        val successResult: Result<Weather, NetworkError> = Result.Success(domainModel)

        coEvery { weatherApi.getWeather("$lat,$lon") } returns apiResponse

        // when
        val result = repository.getWeather(lat, lon)

        // then
        assertEquals(successResult, result)
        assertEquals(domainModel, (result as Result.Success).data)

        coVerify(exactly = 1) { weatherApi.getWeather("$lat,$lon") }
    }

    @Test
    fun `getWeather error returns No Internet`() = runTest {
        // given
        val lat = 10.0
        val lon = 20.0

        coEvery { weatherApi.getWeather(any()) } throws IOException("No Internet")

        // when
        val result = repository.getWeather(lat, lon)

        // then
        assertTrue(result is Result.Error)
        assertEquals(NetworkError.NO_INTERNET, (result as Result.Error).error)
        coVerify(exactly = 1) { weatherApi.getWeather("$lat,$lon") }
    }

    @Test
    fun `getWeather error returns Server Error`() = runTest {
        // given
        val lat = 10.0
        val lon = 20.0

        coEvery { weatherApi.getWeather("$lat,$lon") } throws HttpException(
            Response.error<Any>(500, "Server error".toResponseBody())
        )

        // when
        val result = repository.getWeather(lat, lon)

        // then
        assertTrue(result is Result.Error)
        assertEquals(NetworkError.SERVER_ERROR, (result as Result.Error).error)
        coVerify(exactly = 1) { weatherApi.getWeather("$lat,$lon") }
    }

    @Test
    fun `getWeather error returns Timeout Error`() = runTest {
        // given
        val lat = 10.0
        val lon = 20.0

        coEvery { weatherApi.getWeather("$lat,$lon") } throws SocketTimeoutException()

        // when
        val result = repository.getWeather(lat, lon)

        // then
        assertTrue(result is Result.Error)
        assertEquals(NetworkError.TIMEOUT, (result as Result.Error).error)
        coVerify(exactly = 1) { weatherApi.getWeather("$lat,$lon") }
    }

    @Test
    fun `getWeather error returns Unknown Error`() = runTest {
        // given
        val lat = 10.0
        val lon = 20.0

        coEvery { weatherApi.getWeather("$lat,$lon") } throws Exception("Unknown error")

        // when
        val result = repository.getWeather(lat, lon)

        // then
        assertTrue(result is Result.Error)
        assertEquals(NetworkError.UNKNOWN, (result as Result.Error).error)
        coVerify(exactly = 1) { weatherApi.getWeather("$lat,$lon") }
    }
}
