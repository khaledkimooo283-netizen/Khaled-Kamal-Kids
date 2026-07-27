package com.example

import com.example.data.SongDataRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SongsAndMusicQaTest {

    @Test
    fun testAllSongsExistAndHaveLines() {
        val songs = SongDataRepository.songsList
        assertTrue("Should have at least 8 educational song categories", songs.size >= 8)

        songs.forEach { song ->
            assertNotNull("Song title must not be empty", song.title)
            assertTrue("Song ${song.title} must have lyrics lines", song.lyricsLines.isNotEmpty())
            assertNotNull("Song ${song.title} must have an action challenge prompt", song.actionChallenge.promptText)

            song.lyricsLines.forEachIndexed { lineIdx, line ->
                assertTrue("Line $lineIdx in ${song.title} must have lineText", line.lineText.isNotBlank())
                assertTrue("Line $lineIdx in ${song.title} must have spokenText", line.spokenText.isNotBlank())
            }
        }
    }

    @Test
    fun testAlphabetSongIncludesAll26Letters() {
        val abcSong = SongDataRepository.songsList.find { it.id == "s_abc" }
        assertNotNull("Alphabet song must exist", abcSong)

        val allTokens = abcSong!!.lyricsLines.flatMap { it.tokens }
        val alphabetLetters = ('A'..'Z').map { it.toString() }

        alphabetLetters.forEach { letter ->
            assertTrue(
                "Alphabet song MUST include letter '$letter' explicitly",
                allTokens.contains(letter)
            )
        }
    }

    @Test
    fun testNumbersSongIncludesAll20Numbers() {
        val numSong = SongDataRepository.songsList.find { it.id == "s_num" }
        assertNotNull("Numbers song must exist", numSong)

        val allTokens = numSong!!.lyricsLines.flatMap { it.tokens }
        for (num in 1..20) {
            val numStr = num.toString()
            assertTrue(
                "Numbers song MUST include number '$numStr' explicitly",
                allTokens.contains(numStr)
            )
        }
    }

    @Test
    fun testDaysOfWeekSongIncludesAllSevenDays() {
        val daysSong = SongDataRepository.songsList.find { it.id == "s_days" }
        assertNotNull("Days of the week song must exist", daysSong)

        val allTokens = daysSong!!.lyricsLines.flatMap { it.tokens }
        val expectedDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        expectedDays.forEach { day ->
            assertTrue(
                "Days of week song MUST include '$day'",
                allTokens.contains(day)
            )
        }
    }

    @Test
    fun testMonthsOfYearSongIncludesAllTwelveMonths() {
        val monthsSong = SongDataRepository.songsList.find { it.id == "s_months" }
        assertNotNull("Months of year song must exist", monthsSong)

        val allTokens = monthsSong!!.lyricsLines.flatMap { it.tokens }
        val expectedMonths = listOf(
            "January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"
        )

        expectedMonths.forEach { month ->
            assertTrue(
                "Months of year song MUST include '$month'",
                allTokens.contains(month)
            )
        }
    }

    @Test
    fun testGreetingsAndPolitenessSong() {
        val greetSong = SongDataRepository.songsList.find { it.id == "s_greet" }
        assertNotNull("Greetings song must exist", greetSong)

        val allTokens = greetSong!!.lyricsLines.flatMap { it.tokens }
        assertTrue("Greetings song must contain 'Good Morning'", allTokens.contains("Good Morning"))
        assertTrue("Greetings song must contain 'Please'", allTokens.contains("Please"))
        assertTrue("Greetings song must contain 'Thank You'", allTokens.contains("Thank You"))
    }
}
