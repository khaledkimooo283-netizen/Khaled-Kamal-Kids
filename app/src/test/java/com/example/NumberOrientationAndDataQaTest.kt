package com.example

import com.example.data.HandwritingData
import com.example.data.SongDataRepository
import com.example.ui.games.NumberQuantityData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberOrientationAndDataQaTest {

    private val expectedNumberWords = listOf(
        "Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty"
    )

    @Test
    fun testHandwritingNumbers0To20() {
        val numbers = HandwritingData.numbers
        assertEquals(21, numbers.size)

        numbers.forEachIndexed { index, item ->
            assertEquals(index.toString(), item.character)
            assertEquals(expectedNumberWords[index], item.word)
            assertFalse(item.word.contains("\n"))
            assertFalse(item.word.contains("\r"))
            assertTrue(item.displayTitle.startsWith("Number "))
        }
    }

    @Test
    fun testNumberQuantityData0To20() {
        val items = NumberQuantityData.items
        assertEquals(21, items.size)

        items.forEachIndexed { index, item ->
            assertEquals(index, item.number)
            assertEquals(expectedNumberWords[index].uppercase(), item.word)
            assertFalse(item.word.contains("\n"))
            assertFalse(item.word.contains("\r"))
        }
    }

    @Test
    fun testSongDataNumbers0To20() {
        val numberSong = SongDataRepository.songsList.find { it.id == "s_num" }
        assertNotNull(numberSong)

        val allTokens = numberSong!!.lyricsLines.flatMap { it.tokens }
        val numberTokens = allTokens.filter { expectedNumberWords.contains(it) }

        assertEquals(
            "Numbers song MUST contain all 21 numbers from Zero to Twenty in exact sequence",
            expectedNumberWords,
            numberTokens
        )
    }

    @Test
    fun testLetterCharacterDataAtoZ() {
        val characters = com.example.data.LetterCharacterData.characters
        assertEquals("There must be exactly 26 Letter Characters", 26, characters.size)

        ('A'..'Z').forEachIndexed { index, char ->
            val charData = characters[index]
            assertEquals(char, charData.letter)
            assertTrue(charData.name.isNotBlank())
            assertTrue(charData.characterEmoji.isNotBlank())
            assertTrue(charData.phonicsSound.isNotBlank())
            assertTrue(charData.vocabulary.size >= 4)
            assertTrue(charData.missions.size >= 3)
            assertTrue(charData.unlockBadgeName.isNotBlank())

            charData.missions.forEach { mission ->
                assertTrue(mission.options.isNotEmpty())
                assertTrue(mission.correctIndex in 0 until mission.options.size)
            }
        }
    }
}
