package com.isaevapps.domain.usecase

import com.isaevapps.domain.model.Location
import com.isaevapps.domain.repository.LocationRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetCurrentLocationUseCaseTest {

    private lateinit var repository: LocationRepository
    private lateinit var useCase: GetCurrentLocationUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetCurrentLocationUseCase(repository)
    }

    @Test
    fun `invoke returns flow from repository`() = runTest {
        // given
        val expectedLocation = Location(lat = 55.75, lon = 37.62)
        every { repository.getCurrentLocation() } returns flowOf(expectedLocation)

        // when
        val result = useCase().first() // берём первый элемент из Flow

        // then
        assertEquals(expectedLocation, result)
        verify(exactly = 1) { repository.getCurrentLocation() }
    }
}