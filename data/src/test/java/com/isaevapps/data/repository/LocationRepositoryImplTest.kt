package com.isaevapps.data.repository

import app.cash.turbine.test
import com.isaevapps.data.location.LocationDataSource
import com.isaevapps.domain.model.Location
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocationRepositoryImplTest {

    private lateinit var locationDataSource: LocationDataSource
    private lateinit var repository: LocationRepositoryImpl

    @Before
    fun setUp() {
        locationDataSource = mockk()
        repository = LocationRepositoryImpl(locationDataSource)
    }

    @Test
    fun `getCurrentLocation returns flow from datasource`() = runTest {
        // given
        val location = Location(lat = 55.75, lon = 37.62)
        every { locationDataSource.flow() } returns flowOf(location)

        // when
        val flow = repository.getCurrentLocation()

        // then
        flow.test {
            assertEquals(location, awaitItem())
            awaitComplete()
        }

        verify(exactly = 1) { locationDataSource.flow() }
    }
}