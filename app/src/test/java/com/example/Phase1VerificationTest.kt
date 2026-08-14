package com.example

import com.example.data.HandwritingData
import com.example.data.SongDataRepository
import com.example.util.PronunciationEvaluator
import org.junit.Assert.*
import org.junit.Test

class Phase1VerificationTest {

    @Test
    fun testNumbersSongSequence() {
        val numbersSong = SongDataRepository.songsList.first { it.id == "s_num" }
        assertEquals("Numbers 0 to 20 Song", numbersSong.title)

        val allTokens = numbersSong.lyricsLines.flatMap { it.tokens }
        val expectedNumbers = listOf(
            "Zero", "One", "Two", "Three", "Four", "Five",
            "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
            "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty"
        )

        for (num in expectedNumbers) {
            assertTrue("Numbers song must contain $num", allTokens.contains(num))
        }

        // Verify sequence order
        val sequenceStart = allTokens.take(21)
        assertEquals(expectedNumbers, sequenceStart)
    }

    @Test
    fun testDaysOfWeekSongSequence() {
        val daysSong = SongDataRepository.songsList.first { it.id == "s_days" }
        val allTokens = daysSong.lyricsLines.flatMap { it.tokens }

        val expectedDays = listOf(
            "Saturday", "Sunday", "Monday",
            "Tuesday", "Wednesday", "Thursday",
            "Friday"
        )

        val actualDays = allTokens.filter { it in expectedDays }
        assertEquals("Days song must start with Saturday and end with Friday in exact order", expectedDays, actualDays)
    }

    @Test
    fun testMonthsSongSequence() {
        val monthsSong = SongDataRepository.songsList.first { it.id == "s_months" }
        val allTokens = monthsSong.lyricsLines.flatMap { it.tokens }

        val expectedMonths = listOf(
            "January", "February", "March", "April",
            "May", "June", "July", "August",
            "September", "October", "November", "December"
        )

        val actualMonths = allTokens.filter { it in expectedMonths }
        assertEquals("Months song must include all 12 months in exact order through December", expectedMonths, actualMonths)
        assertTrue("December must be present in audio tokens", allTokens.contains("December"))
    }

    @Test
    fun testPhonicsSongSequence() {
        val phonicsSong = SongDataRepository.songsList.first { it.id == "s_vocab" }
        val allTokens = phonicsSong.lyricsLines.flatMap { it.tokens }

        val expectedLetters = ('A'..'Z').map { it.toString() }
        for (letter in expectedLetters) {
            assertTrue("Phonics song must contain letter $letter", allTokens.contains(letter))
        }
    }

    @Test
    fun testHandwritingDataCoverage() {
        val upperLetters = HandwritingData.uppercaseLetters.map { it.character }
        val lowerLetters = HandwritingData.lowercaseLetters.map { it.character }
        val numbers = HandwritingData.numbers.map { it.character }

        val expectedUpper = ('A'..'Z').map { it.toString() }
        val expectedLower = ('a'..'z').map { it.toString() }

        assertEquals("Must cover all Capital A-Z", expectedUpper, upperLetters)
        assertEquals("Must cover all Small a-z", expectedLower, lowerLetters)
        assertTrue("Must cover numbers starting with 0", numbers.contains("0"))
        assertTrue("Must cover number 9", numbers.contains("9"))

        // Check stroke paths for Capital E
        val capitalE = HandwritingData.uppercaseLetters.first { it.character == "E" }
        assertEquals("Capital E must have 4 distinct sequential strokes", 4, capitalE.strokeGuidePoints.size)
    }

    @Test
    fun testLeoSpeechEvaluator() {
        // Correct pronunciation
        assertTrue(PronunciationEvaluator.evaluatePronunciationCandidates("A", listOf("a")).isAccepted)
        assertTrue(PronunciationEvaluator.evaluatePronunciationCandidates("B", listOf("bee")).isAccepted)
        assertTrue(PronunciationEvaluator.evaluatePronunciationCandidates("C", listOf("see")).isAccepted)
        assertTrue(PronunciationEvaluator.evaluatePronunciationCandidates("E", listOf("ee")).isAccepted)
        assertTrue(PronunciationEvaluator.evaluatePronunciationCandidates("apple", listOf("apple")).isAccepted)
        assertTrue(PronunciationEvaluator.evaluatePronunciationCandidates("ball", listOf("ball")).isAccepted)
        assertTrue(PronunciationEvaluator.evaluatePronunciationCandidates("cat", listOf("cat")).isAccepted)
        assertTrue(PronunciationEvaluator.evaluatePronunciationCandidates("dog", listOf("dog")).isAccepted)
        assertTrue(PronunciationEvaluator.evaluatePronunciationCandidates("before", listOf("before")).isAccepted)

        // Wrong pronunciation
        assertFalse(PronunciationEvaluator.evaluatePronunciationCandidates("B", listOf("banana")).isAccepted)
        assertFalse(PronunciationEvaluator.evaluatePronunciationCandidates("B", listOf("book")).isAccepted)
        assertFalse(PronunciationEvaluator.evaluatePronunciationCandidates("before", listOf("banana")).isAccepted)

        // Unclear speech
        val silenceResult = PronunciationEvaluator.evaluatePronunciationCandidates("cat", emptyList())
        assertFalse(silenceResult.isAccepted)
        assertEquals("I couldn't hear you. Try again.", silenceResult.feedbackMessage)
    }
}
