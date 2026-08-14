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
    fun testNumbersSongIncludesAll21NumbersZeroThroughTwentyInExactSequence() {
        val numSong = SongDataRepository.songsList.find { it.id == "s_num" }
        assertNotNull("Numbers song must exist", numSong)

        val expectedSequence = listOf(
            "Zero", "One", "Two", "Three", "Four", "Five",
            "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
            "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty"
        )

        val allTokens = numSong!!.lyricsLines.flatMap { it.tokens }
        val numberTokens = allTokens.filter { expectedSequence.contains(it) }

        assertEquals(
            "Numbers song MUST contain all 21 numbers from Zero to Twenty in exact sequence",
            expectedSequence,
            numberTokens
        )
    }

    @Test
    fun testDaysOfWeekSongStartsWithSaturdayAndEndsWithFridayInExactSequence() {
        val daysSong = SongDataRepository.songsList.find { it.id == "s_days" }
        assertNotNull("Days of the week song must exist", daysSong)

        val expectedDays = listOf("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

        val allTokens = daysSong!!.lyricsLines.flatMap { it.tokens }
        val dayTokens = allTokens.filter { expectedDays.contains(it) }

        assertEquals(
            "Days of week song MUST start with Saturday and end with Friday in exact sequence",
            expectedDays,
            dayTokens
        )
    }

    @Test
    fun testMonthsOfYearSongIncludesAllTwelveMonthsIncludingDecember() {
        val monthsSong = SongDataRepository.songsList.find { it.id == "s_months" }
        assertNotNull("Months of year song must exist", monthsSong)

        val expectedMonths = listOf(
            "January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"
        )

        val allTokens = monthsSong!!.lyricsLines.flatMap { it.tokens }
        val monthTokens = allTokens.filter { expectedMonths.contains(it) }

        assertEquals(
            "Months of year song MUST include all 12 months including December in exact sequence",
            expectedMonths,
            monthTokens
        )
    }

    @Test
    fun testPhonicsAndVocabularySongIncludesFullAToZSequence() {
        val vocabSong = SongDataRepository.songsList.find { it.id == "s_vocab" }
        assertNotNull("Phonics & Vocabulary song must exist", vocabSong)

        val expectedLetters = ('A'..'Z').map { it.toString() }
        val expectedWords = listOf(
            "Apple", "Ball", "Cat", "Dog", "Elephant", "Fish", "Giraffe", "Hat",
            "Ice cream", "Juice", "Kite", "Lion", "Monkey", "Nose", "Orange",
            "Pizza", "Queen", "Rabbit", "Sun", "Tiger", "Umbrella", "Van",
            "Watermelon", "Xylophone", "Yo-yo", "Zebra"
        )

        val allTokens = vocabSong!!.lyricsLines.flatMap { it.tokens }

        expectedLetters.forEach { letter ->
            assertTrue("Phonics song MUST contain letter '$letter'", allTokens.contains(letter))
        }

        expectedWords.forEach { word ->
            assertTrue("Phonics song MUST contain word '$word'", allTokens.contains(word))
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
