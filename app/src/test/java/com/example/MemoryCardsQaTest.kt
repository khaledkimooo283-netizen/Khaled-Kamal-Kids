package com.example

import androidx.test.core.app.ApplicationProvider
import com.example.data.KkDataRepository
import com.example.data.MemoryCardsData
import com.example.data.MemoryDifficulty
import com.example.data.MemoryGameMode
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MemoryCardsQaTest {

    private lateinit var repository: KkDataRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        repository = KkDataRepository(context)
    }

    @Test
    fun testFullAlphabetCoverageInVocabulary() {
        val pairs = MemoryCardsData.allAlphabetPairs
        assertTrue("Alphabet pairs should have > 100 vocabulary items", pairs.size >= 100)

        ('A'..'Z').forEach { letter ->
            val letterItems = pairs.filter { it.letter == letter }
            assertTrue("Letter '$letter' must have at least 2 items (target 3-5)", letterItems.size >= 2)
            letterItems.forEach { item ->
                assertFalse("Word for letter '$letter' should not be empty", item.word.isBlank())
                assertFalse("Arabic word for letter '$letter' should not be empty", item.arabicWord.isBlank())
                assertFalse("Emoji for letter '$letter' should not be empty", item.emoji.isBlank())
            }
        }
    }

    @Test
    fun testDifficultyCardCounts() {
        assertEquals(6, MemoryDifficulty.EASY.cardCount)
        assertEquals(3, MemoryDifficulty.EASY.pairCount)

        assertEquals(12, MemoryDifficulty.MEDIUM.cardCount)
        assertEquals(6, MemoryDifficulty.MEDIUM.pairCount)

        assertEquals(20, MemoryDifficulty.HARD.cardCount)
        assertEquals(10, MemoryDifficulty.HARD.pairCount)
    }

    @Test
    fun testRecordMemorySessionAndParentDashboardIntegration() {
        val initialAccuracy = repository.getMatchingAccuracy()
        val initialLearnedWords = repository.getLearnedWords()

        repository.recordMemorySession(
            matches = 6,
            attempts = 6,
            timeSpentSecs = 45,
            wordsLearned = listOf("Apple", "Banana", "Cat", "Dog", "Elephant", "Fox")
        )

        val updatedAccuracy = repository.getMatchingAccuracy()
        val updatedLearnedWords = repository.getLearnedWords()

        assertEquals(100, updatedAccuracy)
        assertTrue(updatedLearnedWords.containsAll(listOf("Apple", "Banana", "Cat", "Dog", "Elephant", "Fox")))
    }
}
