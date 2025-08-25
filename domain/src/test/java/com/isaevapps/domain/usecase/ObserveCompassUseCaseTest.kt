package com.isaevapps.domain.usecase

import com.isaevapps.domain.model.CompassData
import com.isaevapps.domain.repository.CompassRepository
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
class ObserveCompassUseCaseTest {

    private lateinit var repository: CompassRepository
    private lateinit var useCase: ObserveCompassUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = ObserveCompassUseCase(repository)
    }

    @Test
    fun `invoke returns flow from repository`() = runTest {
        // given
        val expectedData = CompassData(azimuth = 123)
        every { repository.observeAzimuth() } returns flowOf(expectedData)

        // when
        val result = useCase().first()

        // then
        assertEquals(expectedData, result)
        verify(exactly = 1) { repository.observeAzimuth() }
    }
}
