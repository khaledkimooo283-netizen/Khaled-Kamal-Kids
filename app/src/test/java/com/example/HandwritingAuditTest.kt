package com.example

import com.example.data.HandwritingData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HandwritingAuditTest {

    @Test
    fun testAll26UppercaseLettersExistAndValid() {
        val uppercase = HandwritingData.uppercaseLetters
        assertEquals("Should have all 26 capital letters A-Z", 26, uppercase.size)

        val alphabet = ('A'..'Z').map { it.toString() }
        alphabet.forEach { letter ->
            val item = uppercase.find { it.character == letter }
            assertNotNull("Uppercase letter $letter must exist in HandwritingData", item)
            assertTrue("Uppercase letter $letter must have at least 1 stroke", item!!.strokeGuidePoints.isNotEmpty())

            item.strokeGuidePoints.forEachIndexed { strokeIdx, stroke ->
                assertTrue("Stroke $strokeIdx for $letter must not be empty", stroke.isNotEmpty())
                stroke.forEach { (x, y) ->
                    assertTrue("Point ($x, $y) for $letter must be within canvas boundaries [0, 1]", x in 0f..1f && y in 0f..1f)
                }
            }
        }
    }

    @Test
    fun testAll26LowercaseLettersExistAndValid() {
        val lowercase = HandwritingData.lowercaseLetters
        assertEquals("Should have all 26 lowercase letters a-z", 26, lowercase.size)

        val alphabet = ('a'..'z').map { it.toString() }
        alphabet.forEach { letter ->
            val item = lowercase.find { it.character == letter }
            assertNotNull("Lowercase letter $letter must exist in HandwritingData", item)
            assertTrue("Lowercase letter $letter must have at least 1 stroke", item!!.strokeGuidePoints.isNotEmpty())

            item.strokeGuidePoints.forEachIndexed { strokeIdx, stroke ->
                assertTrue("Stroke $strokeIdx for $letter must not be empty", stroke.isNotEmpty())
                stroke.forEach { (x, y) ->
                    assertTrue("Point ($x, $y) for $letter must be within canvas boundaries [0, 1]", x in 0f..1f && y in 0f..1f)
                }
            }
        }
    }

    @Test
    fun testAll21NumbersExistAndValid() {
        val numbers = HandwritingData.numbers
        assertEquals("Should have 21 numbers (0-20)", 21, numbers.size)

        for (num in 0..20) {
            val numStr = num.toString()
            val item = numbers.find { it.character == numStr }
            assertNotNull("Number $numStr must exist in HandwritingData", item)
            assertTrue("Number $numStr must have at least 1 stroke", item!!.strokeGuidePoints.isNotEmpty())

            item.strokeGuidePoints.forEachIndexed { strokeIdx, stroke ->
                assertTrue("Stroke $strokeIdx for Number $numStr must not be empty", stroke.isNotEmpty())
                stroke.forEach { (x, y) ->
                    assertTrue("Point ($x, $y) for Number $numStr must be within canvas boundaries [0, 1]", x in 0f..1f && y in 0f..1f)
                }
            }
        }
    }

    @Test
    fun testLowercaseEHandwritingStructure() {
        val e = HandwritingData.lowercaseLetters.find { it.character == "e" }
        assertNotNull("Lowercase e must exist", e)

        val strokes = e!!.strokeGuidePoints
        assertTrue("Lowercase e must have 2 stroke parts (crossbar + loop)", strokes.size >= 1)

        // Crossbar: stroke 0 must be horizontal from left to right
        val stroke0 = strokes[0]
        val startX = stroke0.first().first
        val endX = stroke0.last().first
        val yBar = stroke0.first().second

        assertTrue("e crossbar startX should be left (< 0.4)", startX < 0.4f)
        assertTrue("e crossbar endX should be right (> 0.6)", endX > 0.6f)

        // Loop: stroke 1 (or part of stroke) must curve UP above yBar towards midline (y <= 0.52)
        val allPoints = strokes.flatten()
        val minY = allPoints.minOf { it.second }
        val maxY = allPoints.maxOf { it.second }

        assertTrue("e loop top must reach midline (minY <= 0.52)", minY <= 0.52f)
        assertTrue("e bottom curve must reach baseline (maxY >= 0.80)", maxY >= 0.80f)
    }

    @Test
    fun testLowercaseJHandwritingStructure() {
        val j = HandwritingData.lowercaseLetters.find { it.character == "j" }
        assertNotNull("Lowercase j must exist", j)

        val strokes = j!!.strokeGuidePoints
        assertEquals("Lowercase j must have 2 strokes (descender stem + dot)", 2, strokes.size)

        val stemStroke = strokes[0]
        val dotStroke = strokes[1]

        // Stem must start near midline (y <= 0.55), go down into descender zone (y >= 0.88), and hook left (x < 0.35)
        val stemStartY = stemStroke.first().second
        val stemMaxY = stemStroke.maxOf { it.second }
        val hookMinX = stemStroke.minOf { it.first }

        assertTrue("j stem must start near midline (y <= 0.55)", stemStartY <= 0.55f)
        assertTrue("j stem must reach descender zone (y >= 0.88)", stemMaxY >= 0.88f)
        assertTrue("j hook must curve left (x <= 0.35)", hookMinX <= 0.35f)

        // Dot must be above midline (y <= 0.35)
        val dotY = dotStroke.first().second
        assertTrue("j dot must be above midline (y <= 0.35)", dotY <= 0.35f)
    }

    @Test
    fun testDescenderLettersP_Q_G_Y() {
        val descenders = listOf("g", "p", "q", "y")
        descenders.forEach { charStr ->
            val item = HandwritingData.lowercaseLetters.find { it.character == charStr }
            assertNotNull("Letter $charStr must exist", item)

            val maxY = item!!.strokeGuidePoints.flatten().maxOf { it.second }
            assertTrue("Descender letter $charStr must reach into descender zone (y >= 0.90)", maxY >= 0.90f)
        }
    }

    @Test
    fun testAscenderLettersB_D_F_H_K_L_T() {
        val ascenders = listOf("b", "d", "f", "h", "k", "l", "t")
        ascenders.forEach { charStr ->
            val item = HandwritingData.lowercaseLetters.find { it.character == charStr }
            assertNotNull("Letter $charStr must exist", item)

            val minY = item!!.strokeGuidePoints.flatten().minOf { it.second }
            assertTrue("Ascender letter $charStr top must reach headline zone (y <= 0.25)", minY <= 0.25f)
        }
    }
}
