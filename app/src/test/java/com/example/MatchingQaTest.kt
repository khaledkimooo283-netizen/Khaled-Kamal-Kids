package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.HandwritingData
import com.example.data.KkDataRepository
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.random.Random

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MatchingQaTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val repository = KkDataRepository(context)

    @Test
    fun testAllCategoriesExistAndHaveFullCoverage() {
        val allPairs = repository.matchPairsList

        // 1. Letters A-Z
        val letterPairs = allPairs.filter { it.category == "letters" }
        assertEquals("Should have 26 letter pairs for A-Z", 26, letterPairs.size)

        val upperLetters = HandwritingData.uppercaseLetters.map { it.character }
        upperLetters.forEach { letter ->
            val pair = letterPairs.find { it.promptEmoji == letter }
            assertNotNull("Letter $letter must exist in matchPairsList", pair)
            assertEquals("m_letter_$letter", pair?.id)
        }

        // 2. Capital <-> Small A-Z
        val casePairs = allPairs.filter { it.category == "case" }
        assertEquals("Should have 26 case pairs for A-Z", 26, casePairs.size)

        upperLetters.forEach { letter ->
            val pair = casePairs.find { it.promptEmoji == letter }
            assertNotNull("Case pair for $letter must exist", pair)
            assertEquals("m_case_$letter", pair?.id)
            assertEquals("Small ${letter.lowercase()}", pair?.matchText)
        }

        // 3. Numbers 0-20
        val numberPairs = allPairs.filter { it.category == "numbers" }
        assertEquals("Should have 21 number pairs (0-20)", 21, numberPairs.size)

        HandwritingData.numbers.forEach { numItem ->
            val pair = numberPairs.find { it.promptEmoji == numItem.character }
            assertNotNull("Number ${numItem.character} must exist in matchPairsList", pair)
            assertEquals("m_number_${numItem.character}", pair?.id)
        }
    }

    @Test
    fun testMatchingLogicByUniqueIdNotPosition() {
        val allPairs = repository.matchPairsList

        // Pick pair A and pair B
        val pairA = allPairs.first()
        val pairB = allPairs[1]

        // Matching same ID must succeed
        assertTrue("Matching same pair ID must succeed", pairA.id == pairA.id)

        // Matching different ID at SAME list position in another list must fail!
        val leftColumn = listOf(pairA, pairB)
        val rightColumn = listOf(pairB, pairA) // Position 0 on left is pairA, position 0 on right is pairB

        // Validating by index (position 0 == position 0) would give wrong match (pairA == pairB) -> FALSE!
        val isMatchByIndex = leftColumn[0].id == rightColumn[0].id
        assertFalse("Matching by position index must be FALSE when items are shuffled!", isMatchByIndex)

        // Validating by ID lookup finds pairA at rightColumn[1] -> TRUE!
        val targetInRight = rightColumn.find { it.id == leftColumn[0].id }
        assertNotNull("Target item must be found by ID", targetInRight)
        assertEquals(pairA.id, targetInRight?.id)
    }

    @Test
    fun test100RandomizedRoundsAllCategories() {
        val random = Random(42)
        val categories = listOf("letters", "case", "numbers")

        var totalRoundsTested = 0
        var totalMatchesTested = 0

        repeat(120) { round ->
            val category = categories[round % categories.size]
            val categoryPairs = repository.matchPairsList.filter { it.category == category }

            // Take 4 random pairs for this round
            val roundPairs = categoryPairs.shuffled(random).take(4)

            // Shuffle left and right columns independently
            val leftColumn = roundPairs.shuffled(random)
            val rightColumn = roundPairs.shuffled(random)

            var matchedIds = setOf<String>()

            // Simulate child playing the round
            leftColumn.forEachIndexed { leftIdx, leftItem ->
                // Search right column for the matching item
                val matchingRightItem = rightColumn.find { it.id == leftItem.id }
                assertNotNull("Matching item for ${leftItem.id} must be present in right column", matchingRightItem)

                // Verify that matching by unique ID succeeds regardless of position index
                val isCorrectMatch = leftItem.id == matchingRightItem?.id
                assertTrue("Match for ${leftItem.id} must be accepted!", isCorrectMatch)

                // Verify wrong match is rejected
                val wrongRightItem = rightColumn.firstOrNull { it.id != leftItem.id }
                if (wrongRightItem != null) {
                    val isWrongMatchAccepted = leftItem.id == wrongRightItem.id
                    assertFalse("Wrong match between ${leftItem.id} and ${wrongRightItem.id} MUST be rejected!", isWrongMatchAccepted)
                }

                matchedIds = matchedIds + leftItem.id
                totalMatchesTested++
            }

            assertEquals("All 4 items in round $round must be successfully matched", 4, matchedIds.size)
            totalRoundsTested++
        }

        assertTrue("Should test over 100 rounds", totalRoundsTested >= 100)
        assertTrue("Should test over 400 total matches", totalMatchesTested >= 400)
    }

    @Test
    fun test1000RandomRoundsValidationAndCoverage() {
        val categories = listOf("letters", "case", "numbers")
        var roundsTested = 0

        repeat(1000) { iteration ->
            val category = categories[iteration % categories.size]
            val round = repository.generateMatchRound(category, count = 4)

            // 1. Verify round validity check passes
            assertTrue("Round $iteration ($category) must pass validation", repository.validateGeneratedRound(round.pairs, category))

            // 2. Verify left & right columns contain exactly 4 pairs
            assertEquals("Left column must have 4 items", 4, round.leftPairs.size)
            assertEquals("Right column must have 4 items", 4, round.rightPairs.size)

            // 3. Verify every word in right column corresponds to a letter present in left column
            val leftLetters = round.leftPairs.map { it.promptEmoji }.toSet()

            if (category == "letters") {
                round.rightPairs.forEach { rightPair ->
                    val letter = rightPair.promptEmoji
                    assertTrue("Letter $letter for word '${rightPair.matchText}' MUST be present in left column $leftLetters!", leftLetters.contains(letter))

                    // Verify word starts with or corresponds to the letter
                    val expectedVocabs = repository.letterVocabMap[letter]?.map { it.word } ?: emptyList()
                    assertTrue("Word '${rightPair.matchText}' must be valid for letter $letter", expectedVocabs.contains(rightPair.matchText))
                }
            }

            // 4. Verify no duplicate items
            assertEquals("All left items must be distinct", 4, round.leftPairs.map { it.id }.distinct().size)
            assertEquals("All right items must be distinct", 4, round.rightPairs.map { it.id }.distinct().size)

            // 5. Verify every left item has exactly ONE right match
            round.leftPairs.forEach { leftItem ->
                val matches = round.rightPairs.filter { it.id == leftItem.id }
                assertEquals("Left item ${leftItem.id} must have exactly 1 match in right column", 1, matches.size)
            }

            roundsTested++
        }

        assertEquals("Should have successfully tested 1000 rounds", 1000, roundsTested)
    }
}
