package ru.sudox.android.time.formatters

import android.app.Activity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.sudox.android.time.dateTimeOf
import ru.sudox.android.time.formatters.FullTimeFormatter.onEventOccurredBetween12And24HoursAgo
import ru.sudox.android.time.formatters.FullTimeFormatter.onEventOccurredBetween1And12HoursAgo
import ru.sudox.android.time.formatters.FullTimeFormatter.onEventOccurredBetween1And60MinutesAgo
import ru.sudox.android.time.formatters.FullTimeFormatter.onEventOccurredBetween2And7DaysAgo
import ru.sudox.android.time.formatters.FullTimeFormatter.onEventOccurredInAnotherYear
import ru.sudox.android.time.formatters.FullTimeFormatter.onEventOccurredInSameYear
import ru.sudox.android.time.formatters.FullTimeFormatter.onEventOccurredNow
import ru.sudox.android.time.formatters.FullTimeFormatter.onEventOccurredYesterday
import java.util.TimeZone

@Config(qualifiers = "en")
@RunWith(RobolectricTestRunner::class)
class FullTimeFormatterTest  {

    private val activity = Robolectric.buildActivity(Activity::class.java).get()

    init {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    }

    @Test
    fun testWhenEventOccurredNow() {
        assertEquals("just now", onEventOccurredNow(activity, dateTimeOf(0L), dateTimeOf(0L), false))
    }

    @Test
    fun testWhenEventOccurredBetween1And60MinutesAgo() {
        assertEquals("1 minute ago", onEventOccurredBetween1And60MinutesAgo(activity, 1, dateTimeOf(0L), dateTimeOf(0L), false))
        assertEquals("2 minutes ago", onEventOccurredBetween1And60MinutesAgo(activity, 2, dateTimeOf(0L), dateTimeOf(0L), false))
    }

    @Test
    fun testWhenEventOccurredBetween1And12HoursAgo() {
        assertEquals("1 hour ago", onEventOccurredBetween1And12HoursAgo(activity, 1, dateTimeOf(0L), dateTimeOf(0L), false))
        assertEquals("2 hours ago", onEventOccurredBetween1And12HoursAgo(activity, 2, dateTimeOf(0L), dateTimeOf(0L), false))
    }

    @Test
    fun testWhenEventOccurredBetween12And24HoursAgo() {
        assertEquals("at 00:00", onEventOccurredBetween12And24HoursAgo(activity, 1, dateTimeOf(0L), dateTimeOf(0L), false))
        assertEquals("at 00:00 AM", onEventOccurredBetween12And24HoursAgo(activity, 1, dateTimeOf(0L), dateTimeOf(0L), true))
    }

    @Test
    fun testWhenEventOccurredYesterday() {
        assertEquals("yesterday at 00:00", onEventOccurredYesterday(activity, dateTimeOf(0L), dateTimeOf(0L), false))
        assertEquals("yesterday at 00:00 AM", onEventOccurredYesterday(activity, dateTimeOf(0L), dateTimeOf(0L), true))
    }

    @Test
    fun testWhenEventOccurredBetween2And7DaysAgo() {
        assertEquals("on Thursday at 00:00", onEventOccurredBetween2And7DaysAgo(activity, 2, dateTimeOf(0L), dateTimeOf(0L), false))
        assertEquals("on Thursday at 00:00 AM", onEventOccurredBetween2And7DaysAgo(activity, 2, dateTimeOf(0L), dateTimeOf(0L), true))
    }

    @Test
    fun testWhenEventOccurredInSameYear() {
        assertEquals("January 1 at 00:00", onEventOccurredInSameYear(activity, dateTimeOf(0L), dateTimeOf(0L), false))
        assertEquals("January 1 at 00:00 AM", onEventOccurredInSameYear(activity, dateTimeOf(0L), dateTimeOf(0L), true))
    }

    @Test
    fun testWhenEventOccurredInAnotherYear() {
        assertEquals("January 1 1970 at 00:00", onEventOccurredInAnotherYear(activity, 1, dateTimeOf(0L), dateTimeOf(31536000000L), false))
        assertEquals("January 1 1970 at 00:00 AM", onEventOccurredInAnotherYear(activity, 1, dateTimeOf(0L), dateTimeOf(31536000000L), true))
    }
}