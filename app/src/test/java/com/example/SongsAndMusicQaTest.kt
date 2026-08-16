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

    @Test
    fun testSongDataValidatorValidatesAllSongsSuccessfully() {
        val isValid = com.example.data.SongDataValidator.validateAll()
        assertTrue("SongDataValidator.validateAll() must pass for all songs without error", isValid)
    }

    @Test
    fun testSingleSourceOfTruthLearningItemsIntegrity() {
        val songs = SongDataRepository.songsList

        songs.forEach { song ->
            assertTrue("Song ${song.title} must have items", song.items.isNotEmpty())
            song.items.forEachIndexed { idx, item ->
                assertEquals("Item id must match its sequence index", idx, item.id)
                assertTrue("Item displayText in ${song.title} must not be blank", item.displayText.isNotBlank())
                assertTrue("Item spokenText in ${song.title} must not be blank", item.spokenText.isNotBlank())
                assertTrue("Item visualEmoji in ${song.title} must not be blank", item.visualEmoji.isNotBlank())
            }
        }
    }

    @Test
    fun testNumbersSongItemsExactZeroToTwenty() {
        val numSong = SongDataRepository.songsList.first { it.id == "s_num" }
        assertEquals("Numbers song must have exactly 21 items", 21, numSong.items.size)

        val expectedSpoken = listOf(
            "Zero", "One", "Two", "Three", "Four", "Five",
            "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
            "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty"
        )

        expectedSpoken.forEachIndexed { idx, expectedWord ->
            val item = numSong.items[idx]
            assertEquals("Spoken text at $idx must be $expectedWord", expectedWord, item.spokenText)
            assertTrue("Display text at $idx must contain $idx", item.displayText.contains(idx.toString()) || item.displayText.contains(expectedWord))
        }
    }

    @Test
    fun testDaysOfWeekSongItemsExactSaturdayToFriday() {
        val daysSong = SongDataRepository.songsList.first { it.id == "s_days" }
        assertEquals("Days song must have exactly 7 items", 7, daysSong.items.size)

        val expectedDays = listOf("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
        expectedDays.forEachIndexed { idx, expectedDay ->
            val item = daysSong.items[idx]
            assertEquals("Day at $idx must be $expectedDay", expectedDay, item.spokenText)
            assertEquals("Day displayText at $idx must be $expectedDay", expectedDay, item.displayText)
        }
    }

    @Test
    fun testMonthsOfYearSongItemsExactJanuaryToDecember() {
        val monthsSong = SongDataRepository.songsList.first { it.id == "s_months" }
        assertEquals("Months song must have exactly 12 items", 12, monthsSong.items.size)

        val expectedMonths = listOf(
            "January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"
        )
        expectedMonths.forEachIndexed { idx, expectedMonth ->
            val item = monthsSong.items[idx]
            assertEquals("Month at $idx must be $expectedMonth", expectedMonth, item.spokenText)
            assertEquals("Month displayText at $idx must be $expectedMonth", expectedMonth, item.displayText)
        }
    }

    @Test
    fun testPhonicsSongItemsExactAToZ() {
        val vocabSong = SongDataRepository.songsList.first { it.id == "s_vocab" }
        assertEquals("Phonics song must have exactly 26 items", 26, vocabSong.items.size)

        val letters = ('A'..'Z').toList()
        letters.forEachIndexed { idx, letterChar ->
            val item = vocabSong.items[idx]
            assertTrue("Phonics item at $idx must start with letter $letterChar", item.displayText.startsWith(letterChar.toString()))
            assertTrue("Spoken text at $idx must mention letter $letterChar", item.spokenText.startsWith("$letterChar is for"))
        }
    }
}
