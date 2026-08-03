package com.example

import com.example.util.PronunciationEvaluator
import org.junit.Assert.*
import org.junit.Test

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
    fun testNegativeAndArabicSpeechRejection() {
        // Wrong word target
        val wrongResult = PronunciationEvaluator.evaluatePronunciationCandidates("Apple", listOf("banana"))
        assertFalse("Apple should NOT be accepted when child spoke banana", wrongResult.isAccepted)

        // Arabic speech
        val arabicResult = PronunciationEvaluator.evaluatePronunciationCandidates("Cat", listOf("قطة"))
        assertFalse("Cat should NOT be accepted when child spoke Arabic", arabicResult.isAccepted)
        assertTrue("Feedback should mention Arabic speech", arabicResult.feedbackMessage.contains("Arabic"))

        // Silence / Empty input
        val silenceResult = PronunciationEvaluator.evaluatePronunciationCandidates("Dog", emptyList())
        assertFalse("Dog should NOT be accepted when there is silence", silenceResult.isAccepted)
    }
}
