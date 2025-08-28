package com.isaevapps.data.repository

import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

class ResourceTimeZoneRepositoryTest {

    private lateinit var repository: ResourceTimeZoneRepository

    @Before
    fun setUp() {
        repository = ResourceTimeZoneRepository()
    }

    @Test
    fun `utcList is not empty`() {
        assertTrue(ResourceTimeZoneRepository.utcList.isNotEmpty())
    }

    @Test
    fun `getSystemUtc returns first timezone if no match found`() {
        // given
        mockkStatic(TimeZone::class)
        val fakeTimeZone = TimeZone.getTimeZone("GMT+13:37")
        every { TimeZone.getDefault() } returns fakeTimeZone

        // when
        val result = repository.getSystemUtc()

        // then
        assertEquals(repository.timeZones[0], result)
    }

    @Test
    fun `getSystemUtc returns matching timezone`() {
        // given
        val default = TimeZone.getDefault()
        val offsetInMillis = default.rawOffset
        val hours = offsetInMillis / (1000 * 60 * 60)
        val minutes = (offsetInMillis / (1000 * 60)) % 60
        val expectedPrefix =
            String.format(Locale.getDefault(), "UTC%+03d:%02d", hours, Math.abs(minutes))
                .take(9)

        // when
        val result = repository.getSystemUtc()

        // then
        assertTrue(result.name.startsWith(expectedPrefix))
    }

    @Test
    fun `getTimeZoneByDouble returns correct timezone`() {
        // given
        val tz = repository.timeZones.first()
        val offset = tz.getUTCDouble()

        // when
        val result = repository.getTimeZoneByDouble(offset)

        // then
        assertEquals(tz, result)
    }

    @Test
    fun `getTimeZoneByDouble returns first if not found`() {
        // given
        val invalidOffset = 99.99

        // when
        val result = repository.getTimeZoneByDouble(invalidOffset)

        // then
        assertEquals(repository.timeZones[0], result)
    }
}