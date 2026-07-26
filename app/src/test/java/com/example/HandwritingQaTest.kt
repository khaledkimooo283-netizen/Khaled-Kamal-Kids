package com.example

import com.example.data.HandwritingData
import org.junit.Assert.*
import org.junit.Test

class HandwritingQaTest {

    @Test
    fun testAllHandwritingModelsAndTracingEngine() {
        var uppercasePassed = 0
        var lowercasePassed = 0
        var numbersPassed = 0

        val canvasSize = 300f

        // 1. Test Uppercase Letters (26)
        val upperList = HandwritingData.uppercaseLetters
        assertEquals("Should have 26 uppercase letters", 26, upperList.size)

        upperList.forEach { item ->
            val strokes = item.strokeGuidePoints
            assertTrue("Item ${item.character} must have at least 1 stroke", strokes.isNotEmpty())

            // Verify bounds & shape validity
            strokes.forEach { stroke ->
                assertTrue("Stroke in ${item.character} cannot be empty", stroke.isNotEmpty())
                stroke.forEach { (x, y) ->
                    assertTrue("X coordinate ($x) in ${item.character} must be within bounds", x in 0.05f..0.95f)
                    assertTrue("Y coordinate ($y) in ${item.character} must be within bounds", y in 0.05f..0.95f)
                }
            }

            // Generate ideal tracing path (tracing along guide points in pixel space)
            val idealDrawnPoints = strokes.flatten().map { Pair(it.first * canvasSize, it.second * canvasSize) }
            val validation = HandwritingData.validateHandwritingTracing(
                drawnPoints = idealDrawnPoints,
                strokes = strokes,
                canvasSize = canvasSize
            )

            assertTrue("Ideal tracing for Uppercase ${item.character} must be valid", validation.isValid)
            assertTrue("Coverage for ${item.character} should be >= 0.88", validation.coverage >= 0.88f)
            assertTrue("Accuracy for ${item.character} should be >= 0.85", validation.accuracy >= 0.85f)

            // Test rejection of incorrect tracing (tracing backwards or far off)
            val badTracing = listOf(Pair(0.01f, 0.01f), Pair(0.02f, 0.02f), Pair(0.03f, 0.03f))
            val badValidation = HandwritingData.validateHandwritingTracing(
                drawnPoints = badTracing,
                strokes = strokes,
                canvasSize = canvasSize
            )
            assertFalse("Bad tracing for ${item.character} must be rejected", badValidation.isValid)

            uppercasePassed++
        }

        // 2. Test Lowercase Letters (26)
        val lowerList = HandwritingData.lowercaseLetters
        assertEquals("Should have 26 lowercase letters", 26, lowerList.size)

        lowerList.forEach { item ->
            val strokes = item.strokeGuidePoints
            assertTrue("Item ${item.character} must have at least 1 stroke", strokes.isNotEmpty())

            strokes.forEach { stroke ->
                assertTrue("Stroke in ${item.character} cannot be empty", stroke.isNotEmpty())
                stroke.forEach { (x, y) ->
                    assertTrue("X coordinate ($x) in ${item.character} must be within bounds", x in 0.05f..0.95f)
                    assertTrue("Y coordinate ($y) in ${item.character} must be within bounds", y in 0.05f..0.95f)
                }
            }

            val idealDrawnPoints = strokes.flatten().map { Pair(it.first * canvasSize, it.second * canvasSize) }
            val validation = HandwritingData.validateHandwritingTracing(
                drawnPoints = idealDrawnPoints,
                strokes = strokes,
                canvasSize = canvasSize
            )

            assertTrue("Ideal tracing for Lowercase ${item.character} must be valid", validation.isValid)
            assertTrue("Coverage for ${item.character} should be >= 0.88", validation.coverage >= 0.88f)
            assertTrue("Accuracy for ${item.character} should be >= 0.85", validation.accuracy >= 0.85f)

            lowercasePassed++
        }

        // 3. Test Numbers 0-20 (21)
        val numList = HandwritingData.numbers
        assertEquals("Should have 21 numbers (0-20)", 21, numList.size)

        numList.forEach { item ->
            val strokes = item.strokeGuidePoints
            assertTrue("Item ${item.character} must have at least 1 stroke", strokes.isNotEmpty())

            strokes.forEach { stroke ->
                assertTrue("Stroke in Number ${item.character} cannot be empty", stroke.isNotEmpty())
                stroke.forEach { (x, y) ->
                    assertTrue("X coordinate ($x) in Number ${item.character} must be within bounds", x in 0.05f..0.95f)
                    assertTrue("Y coordinate ($y) in Number ${item.character} must be within bounds", y in 0.05f..0.95f)
                }
            }

            val idealDrawnPoints = strokes.flatten().map { Pair(it.first * canvasSize, it.second * canvasSize) }
            val validation = HandwritingData.validateHandwritingTracing(
                drawnPoints = idealDrawnPoints,
                strokes = strokes,
                canvasSize = canvasSize
            )

            assertTrue("Ideal tracing for Number ${item.character} must be valid", validation.isValid)
            assertTrue("Coverage for Number ${item.character} should be >= 0.88", validation.coverage >= 0.88f)
            assertTrue("Accuracy for Number ${item.character} should be >= 0.85", validation.accuracy >= 0.85f)

            numbersPassed++
        }

        // Print QA summary report to build console output
        println("=== HANDWRITING QA AUTOMATED TEST REPORT ===")
        println("Uppercase Letters: ✅ $uppercasePassed / 26 Passed")
        println("Lowercase Letters: ✅ $lowercasePassed / 26 Passed")
        println("Numbers: ✅ $numbersPassed / 21 Passed")
        println("Overall Result: ✅ 100% Passed")
        println("===========================================")

        assertEquals(26, uppercasePassed)
        assertEquals(26, lowercasePassed)
        assertEquals(21, numbersPassed)
    }
}
