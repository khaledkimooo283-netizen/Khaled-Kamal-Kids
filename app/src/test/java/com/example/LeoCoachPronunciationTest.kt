package com.example

import com.example.util.AssessmentErrorType
import com.example.util.LocalPronunciationAssessmentEngine
import com.example.util.PRONUNCIATION_PASS_THRESHOLD
import com.example.util.PhonemeDictionary
import com.example.util.PronunciationEvaluator
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class LeoCoachPronunciationTest {

    @Test
    fun testFiftyPlusEnglishWordsPronunciation() {
        val testCases = listOf(
            // Letters (A-Z)
            "A" to listOf("a", "ay", "ei"),
            "B" to listOf("b", "bee"),
            "C" to listOf("c", "see"),
            "D" to listOf("d", "dee"),
            "E" to listOf("e", "ee"),
            "F" to listOf("f", "eff"),
            "G" to listOf("g", "gee"),
            "H" to listOf("h", "aitch"),
            "I" to listOf("i", "eye"),
            "J" to listOf("j", "jay"),
            "K" to listOf("k", "kay"),
            "L" to listOf("l", "el"),
            "M" to listOf("m", "em"),
            "N" to listOf("n", "en"),
            "O" to listOf("o", "oh"),
            "P" to listOf("p", "pee"),
            "Q" to listOf("q", "cue"),
            "R" to listOf("r", "are"),
            "S" to listOf("s", "ess"),
            "T" to listOf("t", "tea"),
            "U" to listOf("u", "you"),
            "V" to listOf("v", "vee"),
            "W" to listOf("w", "double u"),
            "X" to listOf("x", "ex"),
            "Y" to listOf("y", "why"),
            "Z" to listOf("z", "zee"),

            // Numbers (0-20)
            "Zero" to listOf("0", "zero"),
            "One" to listOf("1", "one"),
            "Two" to listOf("2", "two"),
            "Three" to listOf("3", "three"),
            "Four" to listOf("4", "four"),
            "Five" to listOf("5", "five"),
            "Six" to listOf("6", "six"),
            "Seven" to listOf("7", "seven"),
            "Eight" to listOf("8", "eight"),
            "Nine" to listOf("9", "nine"),
            "Ten" to listOf("10", "ten"),
            "Eleven" to listOf("11", "eleven"),
            "Twelve" to listOf("12", "twelve"),
            "Thirteen" to listOf("13", "thirteen"),
            "Fourteen" to listOf("14", "fourteen"),
            "Fifteen" to listOf("15", "fifteen"),
            "Sixteen" to listOf("16", "sixteen"),
            "Seventeen" to listOf("17", "seventeen"),
            "Eighteen" to listOf("18", "eighteen"),
            "Nineteen" to listOf("19", "nineteen"),
            "Twenty" to listOf("20", "twenty"),

            // Days of Week
            "Sunday" to listOf("sun day", "sunday"),
            "Monday" to listOf("mon day", "monday"),
            "Tuesday" to listOf("tues day", "tuesday"),
            "Wednesday" to listOf("wednesday", "wed day"),
            "Thursday" to listOf("thursday", "thurs day"),
            "Friday" to listOf("friday", "fri day"),
            "Saturday" to listOf("saturday", "sat day"),

            // Months
            "January" to listOf("january"),
            "February" to listOf("february", "febuary"),
            "March" to listOf("march"),
            "April" to listOf("april"),
            "May" to listOf("may"),
            "June" to listOf("june"),
            "July" to listOf("july"),
            "August" to listOf("august"),
            "September" to listOf("september"),
            "October" to listOf("october"),
            "November" to listOf("november"),
            "December" to listOf("december"),

            // Vocabulary Words
            "Apple" to listOf("an apple", "apple"),
            "Banana" to listOf("banana", "a banana"),
            "Cat" to listOf("a cat", "cat"),
            "Dog" to listOf("the dog", "dog"),
            "Elephant" to listOf("an elephant", "elefant"),
            "Fish" to listOf("fish", "a fish"),
            "Giraffe" to listOf("giraffe"),
            "House" to listOf("a house", "house"),
            "Ice Cream" to listOf("ice cream"),
            "Juice" to listOf("juice", "orange juice"),
            "Kite" to listOf("kite", "a kite"),
            "Lion" to listOf("lion", "a lion"),
            "Monkey" to listOf("monkey"),
            "Orange" to listOf("orange", "an orange"),
            "Panda" to listOf("panda"),
            "Rabbit" to listOf("rabbit"),
            "Sun" to listOf("sun", "the sun"),
            "Tiger" to listOf("tiger"),
            "Umbrella" to listOf("an umbrella"),
            "Watermelon" to listOf("watermelon"),
            "Zebra" to listOf("zebra"),

            // Sentences
            "This is a cat" to listOf("this is a cat"),
            "I like apples" to listOf("i like apples"),
            "Good morning Leo" to listOf("good morning leo"),
            "The sun is shining" to listOf("the sun is shining"),
            "I love my family" to listOf("i love my family")
        )

        assertTrue("Should have tested at least 50 English words/phrases", testCases.size >= 50)

        for ((target, spokenCandidates) in testCases) {
            val result = PronunciationEvaluator.evaluatePronunciationCandidates(target, spokenCandidates)
            assertTrue(
                "Target \"$target\" should be accepted for speech candidates $spokenCandidates, but got score=${result.score} feedback=${result.feedbackMessage}",
                result.isAccepted
            )
        }
    }

    @Test
    fun testPromptSpecificLettersAndWords() {
        // Letters A, B, C, E
        val aResult = PronunciationEvaluator.evaluatePronunciationCandidates("A", listOf("a", "ay"))
        assertTrue("Letter A should pass for 'a'", aResult.isAccepted)

        val bResult = PronunciationEvaluator.evaluatePronunciationCandidates("B", listOf("b", "bee"))
        assertTrue("Letter B should pass for 'bee'", bResult.isAccepted)

        val bWrongBanana = PronunciationEvaluator.evaluatePronunciationCandidates("B", listOf("banana"))
        assertFalse("Letter B should FAIL when child says 'banana'", bWrongBanana.isAccepted)

        val bWrongBook = PronunciationEvaluator.evaluatePronunciationCandidates("B", listOf("book"))
        assertFalse("Letter B should FAIL when child says 'book'", bWrongBook.isAccepted)

        val cResult = PronunciationEvaluator.evaluatePronunciationCandidates("C", listOf("see"))
        assertTrue("Letter C should pass for 'see'", cResult.isAccepted)

        val eResult = PronunciationEvaluator.evaluatePronunciationCandidates("E", listOf("ee"))
        assertTrue("Letter E should pass for 'ee'", eResult.isAccepted)

        // Words: apple, ball, cat, dog, before
        val applePass = PronunciationEvaluator.evaluatePronunciationCandidates("apple", listOf("apple"))
        assertTrue("Word 'apple' should pass", applePass.isAccepted)

        val ballPass = PronunciationEvaluator.evaluatePronunciationCandidates("ball", listOf("ball"))
        assertTrue("Word 'ball' should pass", ballPass.isAccepted)

        val catPass = PronunciationEvaluator.evaluatePronunciationCandidates("cat", listOf("cat"))
        assertTrue("Word 'cat' should pass", catPass.isAccepted)

        val dogPass = PronunciationEvaluator.evaluatePronunciationCandidates("dog", listOf("dog"))
        assertTrue("Word 'dog' should pass", dogPass.isAccepted)

        val beforePass = PronunciationEvaluator.evaluatePronunciationCandidates("before", listOf("before"))
        assertTrue("Word 'before' should pass", beforePass.isAccepted)

        val beforeWrongBanana = PronunciationEvaluator.evaluatePronunciationCandidates("before", listOf("banana"))
        assertFalse("Word 'before' should FAIL when child says 'banana'", beforeWrongBanana.isAccepted)

        val beforeWrongBook = PronunciationEvaluator.evaluatePronunciationCandidates("before", listOf("book"))
        assertFalse("Word 'before' should FAIL when child says 'book'", beforeWrongBook.isAccepted)
    }

    @Test
    fun testNegativeAndArabicSpeechRejection() {
        // Wrong word target
        val wrongResult = PronunciationEvaluator.evaluatePronunciationCandidates("Apple", listOf("banana"))
        assertFalse("Apple should NOT be accepted when child spoke banana", wrongResult.isAccepted)

        // Arabic speech
        val arabicResult = PronunciationEvaluator.evaluatePronunciationCandidates("Cat", listOf("قطة"))
        assertFalse("Cat should NOT be accepted when child spoke Arabic", arabicResult.isAccepted)
        assertTrue("Feedback should mention English speech", arabicResult.feedbackMessage.contains("English"))

        // Silence / Empty input
        val silenceResult = PronunciationEvaluator.evaluatePronunciationCandidates("Dog", emptyList())
        assertFalse("Dog should NOT be accepted when there is silence", silenceResult.isAccepted)
        assertEquals("I couldn't hear you. Try again.", silenceResult.feedbackMessage)
    }

    @Test
    fun testPhonemeDictionaryCoverage() {
        val letterB = PhonemeDictionary.getIpaAndPhonemes("B")
        assertEquals("/biː/", letterB.first)
        assertTrue(letterB.second.contains("b"))

        val wordApple = PhonemeDictionary.getIpaAndPhonemes("Apple")
        assertEquals("/ˈæp.əl/", wordApple.first)
        assertTrue(wordApple.second.contains("p"))

        val numberTen = PhonemeDictionary.getIpaAndPhonemes("Ten")
        assertEquals("/tɛn/", numberTen.first)
        assertTrue(numberTen.second.contains("t"))
    }

    @Test
    fun testLocalPronunciationAssessmentAcoustics() = runBlocking {
        val engine = LocalPronunciationAssessmentEngine()

        // 1. Test empty / missing audio file -> NO_SPEECH
        val emptyFile = File.createTempFile("test_empty", ".wav").apply { deleteOnExit() }
        val noSpeechResult = engine.evaluateAudio(emptyFile, "Apple")
        assertFalse(noSpeechResult.passed)
        assertEquals(AssessmentErrorType.NO_SPEECH, noSpeechResult.errorType)

        // 2. Test synthetic valid audio file -> evaluates properly
        val wavFile = File.createTempFile("test_audio", ".wav").apply { deleteOnExit() }
        createSyntheticWav(wavFile, durationSeconds = 1.0, amplitude = 0.5)

        val validResult = engine.evaluateAudio(wavFile, "Apple")
        assertTrue(validResult.isSpeechDetected)
        assertTrue("Score should meet or exceed pass threshold", validResult.pronunciationScore >= PRONUNCIATION_PASS_THRESHOLD)
        assertTrue(validResult.passed)
        assertTrue(validResult.phonemeBreakdown.isNotEmpty())
    }

    private fun createSyntheticWav(file: File, durationSeconds: Double, amplitude: Double) {
        val sampleRate = 16000
        val numSamples = (sampleRate * durationSeconds).toInt()
        val pcmData = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val wave = Math.sin(2.0 * Math.PI * 440.0 * t) * amplitude
            val sample = (wave * 32767).toInt().coerceIn(-32768, 32767).toShort()
            pcmData[i * 2] = (sample.toInt() and 0xFF).toByte()
            pcmData[i * 2 + 1] = ((sample.toInt() shr 8) and 0xFF).toByte()
        }

        val totalDataLen = pcmData.size + 36
        val byteRate = sampleRate * 2
        val header = ByteArray(44)
        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0
        header[20] = 1; header[21] = 0
        header[22] = 1; header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = 2; header[33] = 0
        header[34] = 16; header[35] = 0
        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
        header[40] = (pcmData.size and 0xff).toByte()
        header[41] = ((pcmData.size shr 8) and 0xff).toByte()
        header[42] = ((pcmData.size shr 16) and 0xff).toByte()
        header[43] = ((pcmData.size shr 24) and 0xff).toByte()

        file.outputStream().use { out ->
            out.write(header)
            out.write(pcmData)
        }
    }
}
