package com.example.data

import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

data class TracingGuideItem(
    val id: String,
    val displayTitle: String,
    val character: String,
    val category: String, // "uppercase", "lowercase", "number"
    val word: String,
    val emoji: String,
    val strokeGuidePoints: List<List<Pair<Float, Float>>>
)

object HandwritingData {

    // Helper functions for generating smooth geometric stroke paths
    fun generateLine(x1: Float, y1: Float, x2: Float, y2: Float, steps: Int = 15): List<Pair<Float, Float>> {
        val list = mutableListOf<Pair<Float, Float>>()
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            list.add(Pair(x1 + (x2 - x1) * t, y1 + (y2 - y1) * t))
        }
        return list
    }

    fun generateArc(
        x1: Float, y1: Float, x2: Float, y2: Float, curveX: Float, curveY: Float, steps: Int = 18
    ): List<Pair<Float, Float>> {
        val list = mutableListOf<Pair<Float, Float>>()
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            val x = (1 - t) * (1 - t) * x1 + 2 * (1 - t) * t * curveX + t * t * x2
            val y = (1 - t) * (1 - t) * y1 + 2 * (1 - t) * t * curveY + t * t * y2
            list.add(Pair(x, y))
        }
        return list
    }

    fun generateCubicBezier(
        x1: Float, y1: Float,
        cx1: Float, cy1: Float,
        cx2: Float, cy2: Float,
        x2: Float, y2: Float,
        steps: Int = 18
    ): List<Pair<Float, Float>> {
        val list = mutableListOf<Pair<Float, Float>>()
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            val u = 1 - t
            val x = u * u * u * x1 + 3 * u * u * t * cx1 + 3 * u * t * t * cx2 + t * t * t * x2
            val y = u * u * u * y1 + 3 * u * u * t * cy1 + 3 * u * t * t * cy2 + t * t * t * y2
            list.add(Pair(x, y))
        }
        return list
    }

    fun generateCircle(
        centerX: Float, centerY: Float, radiusX: Float, radiusY: Float,
        startAngleDeg: Double = -90.0, sweepAngleDeg: Double = 360.0, steps: Int = 24
    ): List<Pair<Float, Float>> {
        val list = mutableListOf<Pair<Float, Float>>()
        for (i in 0..steps) {
            val fraction = i.toDouble() / steps
            val angleRad = Math.toRadians(startAngleDeg + fraction * sweepAngleDeg)
            val x = centerX + radiusX * cos(angleRad).toFloat()
            val y = centerY + radiusY * sin(angleRad).toFloat()
            list.add(Pair(x, y))
        }
        return list
    }

    // -------------------------------------------------------------
    // UPPERCASE LETTERS (A-Z) - Official Educational Handwriting
    // Headline: y = 0.12, Midline: y = 0.50, Baseline: y = 0.88
    // -------------------------------------------------------------
    val uppercaseLetters: List<TracingGuideItem> = listOf(
        // A
        TracingGuideItem("upper_A", "Capital A", "A", "uppercase", "Apple", "🍎", listOf(
            generateLine(0.22f, 0.88f, 0.50f, 0.12f, 15), // 1: Left slant up
            generateLine(0.50f, 0.12f, 0.78f, 0.88f, 15), // 2: Right slant down
            generateLine(0.32f, 0.55f, 0.68f, 0.55f, 12)  // 3: Crossbar
        )),
        // B
        TracingGuideItem("upper_B", "Capital B", "B", "uppercase", "Ball", "⚽", listOf(
            generateLine(0.25f, 0.12f, 0.25f, 0.88f, 15), // 1: Vertical down
            generateCubicBezier(0.25f, 0.12f, 0.75f, 0.12f, 0.75f, 0.50f, 0.25f, 0.50f, 18), // 2: Top loop
            generateCubicBezier(0.25f, 0.50f, 0.80f, 0.50f, 0.80f, 0.88f, 0.25f, 0.88f, 18)  // 3: Bottom loop
        )),
        // C
        TracingGuideItem("upper_C", "Capital C", "C", "uppercase", "Cat", "🐱", listOf(
            generateCubicBezier(0.75f, 0.25f, 0.70f, 0.12f, 0.22f, 0.12f, 0.22f, 0.50f, 15) +
                generateCubicBezier(0.22f, 0.50f, 0.22f, 0.88f, 0.70f, 0.88f, 0.75f, 0.75f, 15)
        )),
        // D
        TracingGuideItem("upper_D", "Capital D", "D", "uppercase", "Dog", "🐶", listOf(
            generateLine(0.25f, 0.12f, 0.25f, 0.88f, 15), // 1: Vertical down
            generateCubicBezier(0.25f, 0.12f, 0.85f, 0.12f, 0.85f, 0.88f, 0.25f, 0.88f, 24) // 2: Big curve
        )),
        // E
        TracingGuideItem("upper_E", "Capital E", "E", "uppercase", "Elephant", "🐘", listOf(
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15), // 1: Vertical stem
            generateLine(0.28f, 0.12f, 0.75f, 0.12f, 12), // 2: Top bar
            generateLine(0.28f, 0.50f, 0.65f, 0.50f, 10), // 3: Mid bar
            generateLine(0.28f, 0.88f, 0.75f, 0.88f, 12)  // 4: Bottom bar
        )),
        // F
        TracingGuideItem("upper_F", "Capital F", "F", "uppercase", "Fish", "🐟", listOf(
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15), // 1: Vertical stem
            generateLine(0.28f, 0.12f, 0.75f, 0.12f, 12), // 2: Top bar
            generateLine(0.28f, 0.50f, 0.65f, 0.50f, 10)  // 3: Mid bar
        )),
        // G
        TracingGuideItem("upper_G", "Capital G", "G", "uppercase", "Giraffe", "🦒", listOf(
            generateCubicBezier(0.75f, 0.25f, 0.70f, 0.12f, 0.22f, 0.12f, 0.22f, 0.50f, 15) +
                generateCubicBezier(0.22f, 0.50f, 0.22f, 0.88f, 0.70f, 0.88f, 0.75f, 0.75f, 15),
            generateLine(0.75f, 0.75f, 0.75f, 0.50f, 10),
            generateLine(0.75f, 0.50f, 0.52f, 0.50f, 8)
        )),
        // H
        TracingGuideItem("upper_H", "Capital H", "H", "uppercase", "Hat", "🎩", listOf(
            generateLine(0.25f, 0.12f, 0.25f, 0.88f, 15), // 1: Left stem
            generateLine(0.75f, 0.12f, 0.75f, 0.88f, 15), // 2: Right stem
            generateLine(0.25f, 0.50f, 0.75f, 0.50f, 12)  // 3: Crossbar
        )),
        // I
        TracingGuideItem("upper_I", "Capital I", "I", "uppercase", "Ice Cream", "🍦", listOf(
            generateLine(0.28f, 0.12f, 0.72f, 0.12f, 12), // 1: Top bar
            generateLine(0.50f, 0.12f, 0.50f, 0.88f, 15), // 2: Center stem
            generateLine(0.28f, 0.88f, 0.72f, 0.88f, 12)  // 3: Bottom bar
        )),
        // J
        TracingGuideItem("upper_J", "Capital J", "J", "uppercase", "Juice", "🧃", listOf(
            generateLine(0.28f, 0.12f, 0.72f, 0.12f, 12), // 1: Top bar
            generateLine(0.50f, 0.12f, 0.50f, 0.76f, 15) + generateCubicBezier(
                0.50f, 0.76f,
                0.50f, 0.88f,
                0.35f, 0.88f,
                0.28f, 0.82f,
                8
            )
        )),
        // K
        TracingGuideItem("upper_K", "Capital K", "K", "uppercase", "Kite", "🪁", listOf(
            generateLine(0.25f, 0.12f, 0.25f, 0.88f, 15), // 1: Vertical stem
            generateLine(0.75f, 0.12f, 0.25f, 0.50f, 14), // 2: Top slant
            generateLine(0.25f, 0.50f, 0.75f, 0.88f, 14)  // 3: Bottom slant
        )),
        // L
        TracingGuideItem("upper_L", "Capital L", "L", "uppercase", "Lion", "🦁", listOf(
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15), // 1: Vertical stem
            generateLine(0.28f, 0.88f, 0.75f, 0.88f, 12)  // 2: Bottom bar
        )),
        // M
        TracingGuideItem("upper_M", "Capital M", "M", "uppercase", "Monkey", "🐒", listOf(
            generateLine(0.20f, 0.88f, 0.20f, 0.12f, 15), // 1: Left stem up
            generateLine(0.20f, 0.12f, 0.50f, 0.62f, 14), // 2: Slant down mid
            generateLine(0.50f, 0.62f, 0.80f, 0.12f, 14), // 3: Slant up right
            generateLine(0.80f, 0.12f, 0.80f, 0.88f, 15)  // 4: Right stem down
        )),
        // N
        TracingGuideItem("upper_N", "Capital N", "N", "uppercase", "Nest", "🪹", listOf(
            generateLine(0.22f, 0.88f, 0.22f, 0.12f, 15), // 1: Left stem up
            generateLine(0.22f, 0.12f, 0.78f, 0.88f, 18), // 2: Diagonal down
            generateLine(0.78f, 0.88f, 0.78f, 0.12f, 15)  // 3: Right stem up
        )),
        // O
        TracingGuideItem("upper_O", "Capital O", "O", "uppercase", "Owl", "🦉", listOf(
            generateCircle(0.50f, 0.50f, 0.30f, 0.38f, -90.0, 360.0, 28) // 1: Circle
        )),
        // P
        TracingGuideItem("upper_P", "Capital P", "P", "uppercase", "Penguin", "🐧", listOf(
            generateLine(0.25f, 0.12f, 0.25f, 0.88f, 15), // 1: Vertical stem
            generateCubicBezier(0.25f, 0.12f, 0.78f, 0.12f, 0.78f, 0.52f, 0.25f, 0.52f, 18) // 2: Loop
        )),
        // Q
        TracingGuideItem("upper_Q", "Capital Q", "Q", "uppercase", "Queen", "👑", listOf(
            generateCircle(0.50f, 0.48f, 0.30f, 0.36f, -90.0, 360.0, 28), // 1: Circle
            generateLine(0.55f, 0.62f, 0.80f, 0.88f, 10)  // 2: Tail
        )),
        // R
        TracingGuideItem("upper_R", "Capital R", "R", "uppercase", "Rabbit", "🐰", listOf(
            generateLine(0.25f, 0.12f, 0.25f, 0.88f, 15), // 1: Vertical stem
            generateCubicBezier(0.25f, 0.12f, 0.78f, 0.12f, 0.78f, 0.50f, 0.25f, 0.50f, 18), // 2: Loop
            generateLine(0.25f, 0.50f, 0.75f, 0.88f, 14)  // 3: Leg
        )),
        // S
        TracingGuideItem("upper_S", "Capital S", "S", "uppercase", "Sun", "☀️", listOf(
            generateCubicBezier(0.72f, 0.22f, 0.70f, 0.12f, 0.30f, 0.12f, 0.28f, 0.32f, 12) +
                generateCubicBezier(0.28f, 0.32f, 0.25f, 0.52f, 0.75f, 0.52f, 0.72f, 0.72f, 12) +
                generateCubicBezier(0.72f, 0.72f, 0.70f, 0.88f, 0.30f, 0.88f, 0.25f, 0.80f, 10)
        )),
        // T
        TracingGuideItem("upper_T", "Capital T", "T", "uppercase", "Train", "🚂", listOf(
            generateLine(0.20f, 0.12f, 0.80f, 0.12f, 14), // 1: Top bar
            generateLine(0.50f, 0.12f, 0.50f, 0.88f, 15)  // 2: Center stem
        )),
        // U
        TracingGuideItem("upper_U", "Capital U", "U", "uppercase", "Umbrella", "☂️", listOf(
            generateCubicBezier(0.25f, 0.12f, 0.25f, 0.88f, 0.75f, 0.88f, 0.75f, 0.12f, 24) // 1: U-curve
        )),
        // V
        TracingGuideItem("upper_V", "Capital V", "V", "uppercase", "Violin", "🎻", listOf(
            generateLine(0.22f, 0.12f, 0.50f, 0.88f, 15), // 1: Slant down
            generateLine(0.50f, 0.88f, 0.78f, 0.12f, 15)  // 2: Slant up
        )),
        // W
        TracingGuideItem("upper_W", "Capital W", "W", "uppercase", "Watermelon", "🍉", listOf(
            generateLine(0.18f, 0.12f, 0.34f, 0.88f, 14), // 1: Slant 1
            generateLine(0.34f, 0.88f, 0.50f, 0.45f, 12), // 2: Slant 2
            generateLine(0.50f, 0.45f, 0.66f, 0.88f, 12), // 3: Slant 3
            generateLine(0.66f, 0.88f, 0.82f, 0.12f, 14)  // 4: Slant 4
        )),
        // X
        TracingGuideItem("upper_X", "Capital X", "X", "uppercase", "Xylophone", "🎼", listOf(
            generateLine(0.22f, 0.12f, 0.78f, 0.88f, 16), // 1: Diagonal 1
            generateLine(0.78f, 0.12f, 0.22f, 0.88f, 16)  // 2: Diagonal 2
        )),
        // Y
        TracingGuideItem("upper_Y", "Capital Y", "Y", "uppercase", "Yo-yo", "🪀", listOf(
            generateLine(0.22f, 0.12f, 0.50f, 0.50f, 12), // 1: Left branch
            generateLine(0.78f, 0.12f, 0.50f, 0.50f, 12), // 2: Right branch
            generateLine(0.50f, 0.50f, 0.50f, 0.88f, 12)  // 3: Center stem
        )),
        // Z
        TracingGuideItem("upper_Z", "Capital Z", "Z", "uppercase", "Zebra", "🦓", listOf(
            generateLine(0.22f, 0.12f, 0.78f, 0.12f, 12), // 1: Top bar
            generateLine(0.78f, 0.12f, 0.22f, 0.88f, 16), // 2: Diagonal
            generateLine(0.22f, 0.88f, 0.78f, 0.88f, 12)  // 3: Bottom bar
        ))
    )

    // -------------------------------------------------------------
    // LOWERCASE LETTERS (a-z) - Kindergarten Handwriting Standards
    // Midline: y = 0.50, Baseline: y = 0.88, Headline: y = 0.12, Descender: y = 0.95
    // -------------------------------------------------------------
    val lowercaseLetters: List<TracingGuideItem> = listOf(
        // a
        TracingGuideItem("lower_a", "lowercase a", "a", "lowercase", "apple", "🍎", listOf(
            generateCircle(0.50f, 0.69f, 0.22f, 0.19f, -90.0, 360.0, 22), // 1: c-loop
            generateLine(0.72f, 0.50f, 0.72f, 0.88f, 12) // 2: Right stem
        )),
        // b
        TracingGuideItem("lower_b", "lowercase b", "b", "lowercase", "ball", "⚽", listOf(
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 16), // 1: Tall stem
            generateCubicBezier(0.28f, 0.50f, 0.75f, 0.50f, 0.75f, 0.88f, 0.28f, 0.88f, 18) // 2: Lower loop
        )),
        // c
        TracingGuideItem("lower_c", "lowercase c", "c", "lowercase", "cat", "🐱", listOf(
            generateCubicBezier(0.72f, 0.55f, 0.68f, 0.50f, 0.28f, 0.50f, 0.28f, 0.69f, 12) +
                generateCubicBezier(0.28f, 0.69f, 0.28f, 0.88f, 0.68f, 0.88f, 0.72f, 0.82f, 12)
        )),
        // d
        TracingGuideItem("lower_d", "lowercase d", "d", "lowercase", "dog", "🐶", listOf(
            generateCircle(0.48f, 0.69f, 0.22f, 0.19f, -90.0, 360.0, 20), // 1: c-loop
            generateLine(0.70f, 0.12f, 0.70f, 0.88f, 16) // 2: Tall stem
        )),
        // e - OFFICIAL MANUSCRIPT LOWERCASE E
        TracingGuideItem("lower_e", "lowercase e", "e", "lowercase", "elephant", "🐘", listOf(
            generateLine(0.30f, 0.68f, 0.70f, 0.68f, 12), // 1: Mid horizontal bar
            generateCubicBezier(
                0.70f, 0.68f,
                0.70f, 0.44f,
                0.28f, 0.44f,
                0.28f, 0.68f,
                12
            ) + generateCubicBezier(
                0.28f, 0.68f,
                0.28f, 0.88f,
                0.55f, 0.88f,
                0.72f, 0.80f,
                12
            ) // 2: Top loop and bottom arc
        )),
        // f
        TracingGuideItem("lower_f", "lowercase f", "f", "lowercase", "fish", "🐟", listOf(
            generateCubicBezier(0.68f, 0.18f, 0.65f, 0.12f, 0.48f, 0.12f, 0.48f, 0.25f, 10) +
                generateLine(0.48f, 0.25f, 0.48f, 0.88f, 15), // 1: Top hook & stem
            generateLine(0.28f, 0.50f, 0.68f, 0.50f, 10)  // 2: Crossbar
        )),
        // g
        TracingGuideItem("lower_g", "lowercase g", "g", "lowercase", "giraffe", "🦒", listOf(
            generateCircle(0.48f, 0.65f, 0.22f, 0.15f, -90.0, 360.0, 20), // 1: Upper c-loop
            generateLine(0.70f, 0.50f, 0.70f, 0.86f, 15) + generateCubicBezier(
                0.70f, 0.86f,
                0.70f, 0.95f,
                0.40f, 0.95f,
                0.30f, 0.88f,
                10
            )  // 2: Descender stem with left hook
        )),
        // h
        TracingGuideItem("lower_h", "lowercase h", "h", "lowercase", "hat", "🎩", listOf(
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 16), // 1: Tall stem
            generateCubicBezier(0.28f, 0.62f, 0.40f, 0.50f, 0.72f, 0.50f, 0.72f, 0.88f, 18) // 2: Hump
        )),
        // i
        TracingGuideItem("lower_i", "lowercase i", "i", "lowercase", "ice cream", "🍦", listOf(
            generateLine(0.50f, 0.50f, 0.50f, 0.88f, 12), // 1: Short stem
            generateCircle(0.50f, 0.32f, 0.02f, 0.02f, 0.0, 360.0, 6)   // 2: Dot
        )),
        // j - OFFICIAL MANUSCRIPT LOWERCASE J
        TracingGuideItem("lower_j", "lowercase j", "j", "lowercase", "juice", "🧃", listOf(
            generateLine(0.55f, 0.50f, 0.55f, 0.86f, 15) + generateCubicBezier(
                0.55f, 0.86f,
                0.55f, 0.95f,
                0.35f, 0.95f,
                0.28f, 0.88f,
                10
            ), // 1: Straight descender stem with left hook
            generateCircle(0.55f, 0.32f, 0.02f, 0.02f, 0.0, 360.0, 6)   // 2: Dot
        )),
        // k
        TracingGuideItem("lower_k", "lowercase k", "k", "lowercase", "kite", "🪁", listOf(
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 16), // 1: Tall stem
            generateLine(0.70f, 0.50f, 0.28f, 0.69f, 10), // 2: Slant in
            generateLine(0.28f, 0.69f, 0.70f, 0.88f, 10)  // 3: Slant out
        )),
        // l
        TracingGuideItem("lower_l", "lowercase l", "l", "lowercase", "lion", "🦁", listOf(
            generateLine(0.50f, 0.12f, 0.50f, 0.88f, 16) // 1: Tall stem
        )),
        // m
        TracingGuideItem("lower_m", "lowercase m", "m", "lowercase", "monkey", "🐒", listOf(
            generateLine(0.20f, 0.50f, 0.20f, 0.88f, 12), // 1: Short stem
            generateCubicBezier(0.20f, 0.60f, 0.30f, 0.50f, 0.50f, 0.50f, 0.50f, 0.88f, 14), // 2: First hump
            generateCubicBezier(0.50f, 0.60f, 0.60f, 0.50f, 0.80f, 0.50f, 0.80f, 0.88f, 14)  // 3: Second hump
        )),
        // n
        TracingGuideItem("lower_n", "lowercase n", "n", "lowercase", "nest", "🪹", listOf(
            generateLine(0.28f, 0.50f, 0.28f, 0.88f, 12), // 1: Short stem
            generateCubicBezier(0.28f, 0.60f, 0.40f, 0.50f, 0.72f, 0.50f, 0.72f, 0.88f, 16)  // 2: Hump
        )),
        // o
        TracingGuideItem("lower_o", "lowercase o", "o", "lowercase", "owl", "🦉", listOf(
            generateCircle(0.50f, 0.69f, 0.22f, 0.19f, -90.0, 360.0, 22) // 1: Small circle
        )),
        // p
        TracingGuideItem("lower_p", "lowercase p", "p", "lowercase", "penguin", "🐧", listOf(
            generateLine(0.28f, 0.50f, 0.28f, 0.95f, 15), // 1: Descender stem
            generateCubicBezier(0.28f, 0.50f, 0.75f, 0.50f, 0.75f, 0.88f, 0.28f, 0.88f, 18) // 2: Loop
        )),
        // q
        TracingGuideItem("lower_q", "lowercase q", "q", "lowercase", "queen", "👑", listOf(
            generateCircle(0.48f, 0.69f, 0.22f, 0.19f, -90.0, 360.0, 20), // 1: c-loop
            generateLine(0.70f, 0.50f, 0.70f, 0.95f, 15) // 2: Descender stem
        )),
        // r
        TracingGuideItem("lower_r", "lowercase r", "r", "lowercase", "rabbit", "🐰", listOf(
            generateLine(0.32f, 0.50f, 0.32f, 0.88f, 12), // 1: Short stem
            generateCubicBezier(0.32f, 0.62f, 0.42f, 0.50f, 0.60f, 0.50f, 0.68f, 0.54f, 12) // 2: Hook
        )),
        // s
        TracingGuideItem("lower_s", "lowercase s", "s", "lowercase", "sun", "☀️", listOf(
            generateCubicBezier(0.68f, 0.54f, 0.65f, 0.50f, 0.35f, 0.50f, 0.32f, 0.62f, 10) +
                generateCubicBezier(0.32f, 0.62f, 0.30f, 0.76f, 0.70f, 0.76f, 0.68f, 0.84f, 10) +
                generateCubicBezier(0.68f, 0.84f, 0.65f, 0.88f, 0.35f, 0.88f, 0.32f, 0.84f, 6)
        )),
        // t
        TracingGuideItem("lower_t", "lowercase t", "t", "lowercase", "train", "🚂", listOf(
            generateLine(0.50f, 0.22f, 0.50f, 0.82f, 15) + generateCubicBezier(
                0.50f, 0.82f,
                0.50f, 0.88f,
                0.58f, 0.88f,
                0.62f, 0.86f,
                6
            ), // 1: Stem with bottom flick
            generateLine(0.30f, 0.50f, 0.70f, 0.50f, 10)  // 2: Crossbar
        )),
        // u
        TracingGuideItem("lower_u", "lowercase u", "u", "lowercase", "umbrella", "☂️", listOf(
            generateCubicBezier(0.28f, 0.50f, 0.28f, 0.88f, 0.72f, 0.88f, 0.72f, 0.50f, 20), // 1: U-curve
            generateLine(0.72f, 0.50f, 0.72f, 0.88f, 10)  // 2: Right tail
        )),
        // v
        TracingGuideItem("lower_v", "lowercase v", "v", "lowercase", "violin", "🎻", listOf(
            generateLine(0.24f, 0.50f, 0.50f, 0.88f, 12), // 1: Slant down
            generateLine(0.50f, 0.88f, 0.76f, 0.50f, 12)  // 2: Slant up
        )),
        // w
        TracingGuideItem("lower_w", "lowercase w", "w", "lowercase", "watermelon", "🍉", listOf(
            generateLine(0.18f, 0.50f, 0.34f, 0.88f, 10), // 1: Slant 1
            generateLine(0.34f, 0.88f, 0.50f, 0.65f, 10), // 2: Slant 2
            generateLine(0.50f, 0.65f, 0.66f, 0.88f, 10), // 3: Slant 3
            generateLine(0.66f, 0.88f, 0.82f, 0.50f, 10)  // 4: Slant 4
        )),
        // x
        TracingGuideItem("lower_x", "lowercase x", "x", "lowercase", "xylophone", "🎼", listOf(
            generateLine(0.25f, 0.50f, 0.75f, 0.88f, 14), // 1: Slant 1
            generateLine(0.75f, 0.50f, 0.25f, 0.88f, 14)  // 2: Slant 2
        )),
        // y
        TracingGuideItem("lower_y", "lowercase y", "y", "lowercase", "yo-yo", "🪀", listOf(
            generateLine(0.25f, 0.50f, 0.50f, 0.75f, 10), // 1: Slant left
            generateLine(0.75f, 0.50f, 0.25f, 0.95f, 15)  // 2: Descender slant
        )),
        // z
        TracingGuideItem("lower_z", "lowercase z", "z", "lowercase", "zebra", "🦓", listOf(
            generateLine(0.25f, 0.50f, 0.75f, 0.50f, 10), // 1: Top bar
            generateLine(0.75f, 0.50f, 0.25f, 0.88f, 14), // 2: Diagonal
            generateLine(0.25f, 0.88f, 0.75f, 0.88f, 10)  // 3: Bottom bar
        ))
    )

    // -------------------------------------------------------------
    // NUMBERS (0-20)
    // -------------------------------------------------------------
    val numbers: List<TracingGuideItem> = listOf(
        // 0
        TracingGuideItem("num_0", "Number 0", "0", "number", "Zero", "📦", listOf(
            generateCircle(0.50f, 0.50f, 0.28f, 0.38f, -90.0, 360.0, 28) // Oval zero
        )),
        // 1
        TracingGuideItem("num_1", "Number 1", "1", "number", "One", "🍎", listOf(
            generateLine(0.38f, 0.24f, 0.50f, 0.12f, 8),  // Slant top
            generateLine(0.50f, 0.12f, 0.50f, 0.88f, 16), // Center stem
            generateLine(0.30f, 0.88f, 0.70f, 0.88f, 10)  // Base bar
        )),
        // 2
        TracingGuideItem("num_2", "Number 2", "2", "number", "Two", "⭐", listOf(
            generateCubicBezier(0.28f, 0.28f, 0.35f, 0.12f, 0.75f, 0.12f, 0.75f, 0.38f, 14) +
                generateLine(0.75f, 0.38f, 0.25f, 0.88f, 15) +
                generateLine(0.25f, 0.88f, 0.78f, 0.88f, 12)
        )),
        // 3
        TracingGuideItem("num_3", "Number 3", "3", "number", "Three", "🌳", listOf(
            generateCubicBezier(0.30f, 0.20f, 0.40f, 0.12f, 0.78f, 0.15f, 0.75f, 0.45f, 12) +
                generateCubicBezier(0.75f, 0.45f, 0.60f, 0.50f, 0.40f, 0.50f, 0.35f, 0.50f, 8),
            generateCubicBezier(0.35f, 0.50f, 0.60f, 0.50f, 0.82f, 0.55f, 0.78f, 0.80f, 12) +
                generateCubicBezier(0.78f, 0.80f, 0.72f, 0.88f, 0.40f, 0.88f, 0.30f, 0.82f, 8)
        )),
        // 4
        TracingGuideItem("num_4", "Number 4", "4", "number", "Four", "⚽", listOf(
            generateLine(0.70f, 0.12f, 0.22f, 0.58f, 15), // Left slant down
            generateLine(0.22f, 0.58f, 0.82f, 0.58f, 12), // Horizontal crossbar
            generateLine(0.70f, 0.35f, 0.70f, 0.88f, 12)  // Vertical stem
        )),
        // 5
        TracingGuideItem("num_5", "Number 5", "5", "number", "Five", "🐟", listOf(
            generateLine(0.75f, 0.12f, 0.35f, 0.12f, 10), // Top horizontal
            generateLine(0.35f, 0.12f, 0.35f, 0.48f, 10), // Neck down
            generateCubicBezier(0.35f, 0.48f, 0.78f, 0.45f, 0.82f, 0.80f, 0.65f, 0.88f, 12) +
                generateCubicBezier(0.65f, 0.88f, 0.45f, 0.88f, 0.30f, 0.82f, 0.28f, 0.78f, 6) // Belly
        )),
        // 6
        TracingGuideItem("num_6", "Number 6", "6", "number", "Six", "🚗", listOf(
            generateCubicBezier(0.70f, 0.18f, 0.50f, 0.12f, 0.28f, 0.40f, 0.28f, 0.65f, 16) +
                generateCircle(0.50f, 0.65f, 0.22f, 0.23f, -180.0, 360.0, 22)
        )),
        // 7
        TracingGuideItem("num_7", "Number 7", "7", "number", "Seven", "🦋", listOf(
            generateLine(0.22f, 0.12f, 0.80f, 0.12f, 12), // Top horizontal
            generateLine(0.80f, 0.12f, 0.35f, 0.88f, 16)  // Slant down
        )),
        // 8
        TracingGuideItem("num_8", "Number 8", "8", "number", "Eight", "🍭", listOf(
            generateCircle(0.50f, 0.32f, 0.20f, 0.20f, -90.0, 360.0, 20), // Top loop
            generateCircle(0.50f, 0.68f, 0.24f, 0.20f, -90.0, 360.0, 22)  // Bottom loop
        )),
        // 9
        TracingGuideItem("num_9", "Number 9", "9", "number", "Nine", "🐻", listOf(
            generateCircle(0.50f, 0.32f, 0.22f, 0.20f, -90.0, 360.0, 22), // Top loop
            generateLine(0.72f, 0.12f, 0.72f, 0.88f, 16)  // Vertical right stem
        )),
        // 10
        TracingGuideItem("num_10", "Number 10", "10", "number", "Ten", "🎈", listOf(
            // Digit 1
            generateLine(0.20f, 0.24f, 0.32f, 0.12f, 8),
            generateLine(0.32f, 0.12f, 0.32f, 0.88f, 15),
            generateLine(0.18f, 0.88f, 0.46f, 0.88f, 10),
            // Digit 0
            generateCircle(0.70f, 0.50f, 0.18f, 0.38f, -90.0, 360.0, 24)
        )),
        // 11
        TracingGuideItem("num_11", "Number 11", "11", "number", "Eleven", "🌸", listOf(
            // Digit 1
            generateLine(0.18f, 0.24f, 0.30f, 0.12f, 8),
            generateLine(0.30f, 0.12f, 0.30f, 0.88f, 15),
            generateLine(0.16f, 0.88f, 0.44f, 0.88f, 10),
            // Digit 1
            generateLine(0.58f, 0.24f, 0.70f, 0.12f, 8),
            generateLine(0.70f, 0.12f, 0.70f, 0.88f, 15),
            generateLine(0.56f, 0.88f, 0.84f, 0.88f, 10)
        )),
        // 12
        TracingGuideItem("num_12", "Number 12", "12", "number", "Twelve", "🧁", listOf(
            // Digit 1
            generateLine(0.16f, 0.24f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 2
            generateCubicBezier(0.48f, 0.28f, 0.55f, 0.12f, 0.88f, 0.12f, 0.85f, 0.38f, 12) +
                generateLine(0.85f, 0.38f, 0.48f, 0.88f, 14) +
                generateLine(0.48f, 0.88f, 0.88f, 0.88f, 10)
        )),
        // 13
        TracingGuideItem("num_13", "Number 13", "13", "number", "Thirteen", "🦆", listOf(
            // Digit 1
            generateLine(0.16f, 0.24f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 3
            generateCubicBezier(0.50f, 0.20f, 0.58f, 0.12f, 0.88f, 0.15f, 0.85f, 0.45f, 12) +
                generateCubicBezier(0.85f, 0.45f, 0.75f, 0.50f, 0.60f, 0.50f, 0.55f, 0.50f, 8),
            generateCubicBezier(0.55f, 0.50f, 0.75f, 0.50f, 0.90f, 0.55f, 0.88f, 0.80f, 12) +
                generateCubicBezier(0.88f, 0.80f, 0.82f, 0.88f, 0.60f, 0.88f, 0.50f, 0.82f, 8)
        )),
        // 14
        TracingGuideItem("num_14", "Number 14", "14", "number", "Fourteen", "🍓", listOf(
            // Digit 1
            generateLine(0.16f, 0.24f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 4
            generateLine(0.80f, 0.12f, 0.46f, 0.58f, 14),
            generateLine(0.46f, 0.58f, 0.88f, 0.58f, 12),
            generateLine(0.80f, 0.35f, 0.80f, 0.88f, 12)
        )),
        // 15
        TracingGuideItem("num_15", "Number 15", "15", "number", "Fifteen", "🚀", listOf(
            // Digit 1
            generateLine(0.16f, 0.24f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 5
            generateLine(0.85f, 0.12f, 0.52f, 0.12f, 10),
            generateLine(0.52f, 0.12f, 0.52f, 0.48f, 10),
            generateCubicBezier(0.52f, 0.48f, 0.78f, 0.45f, 0.82f, 0.80f, 0.72f, 0.88f, 12) +
                generateCubicBezier(0.72f, 0.88f, 0.58f, 0.88f, 0.48f, 0.82f, 0.46f, 0.78f, 6)
        )),
        // 16
        TracingGuideItem("num_16", "Number 16", "16", "number", "Sixteen", "🍪", listOf(
            // Digit 1
            generateLine(0.16f, 0.24f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 6
            generateCubicBezier(0.84f, 0.18f, 0.68f, 0.12f, 0.48f, 0.40f, 0.48f, 0.65f, 14) +
                generateCircle(0.68f, 0.65f, 0.20f, 0.23f, -180.0, 360.0, 20)
        )),
        // 17
        TracingGuideItem("num_17", "Number 17", "17", "number", "Seventeen", "🐝", listOf(
            // Digit 1
            generateLine(0.16f, 0.24f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 7
            generateLine(0.46f, 0.12f, 0.88f, 0.12f, 12),
            generateLine(0.88f, 0.12f, 0.54f, 0.88f, 16)
        )),
        // 18
        TracingGuideItem("num_18", "Number 18", "18", "number", "Eighteen", "🍕", listOf(
            // Digit 1
            generateLine(0.16f, 0.24f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 8
            generateCircle(0.68f, 0.32f, 0.18f, 0.20f, -90.0, 360.0, 18),
            generateCircle(0.68f, 0.68f, 0.20f, 0.20f, -90.0, 360.0, 20)
        )),
        // 19
        TracingGuideItem("num_19", "Number 19", "19", "number", "Nineteen", "🍔", listOf(
            // Digit 1
            generateLine(0.16f, 0.24f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 9
            generateCircle(0.66f, 0.32f, 0.18f, 0.20f, -90.0, 360.0, 20),
            generateLine(0.84f, 0.12f, 0.84f, 0.88f, 16)
        )),
        // 20
        TracingGuideItem("num_20", "Number 20", "20", "number", "Twenty", "👑", listOf(
            // Digit 2
            generateCubicBezier(0.14f, 0.28f, 0.20f, 0.12f, 0.48f, 0.12f, 0.46f, 0.38f, 12) +
                generateLine(0.46f, 0.38f, 0.14f, 0.88f, 14) +
                generateLine(0.14f, 0.88f, 0.48f, 0.88f, 10),
            // Digit 0
            generateCircle(0.72f, 0.50f, 0.18f, 0.38f, -90.0, 360.0, 24)
        ))
    )

    // -------------------------------------------------------------
    // SHAPE PREPARATION & PRE-WRITING LINES
    // -------------------------------------------------------------
    val shapePrepItems: List<TracingGuideItem> = listOf(
        TracingGuideItem("shape_line_v", "Vertical Line", "|", "shape", "Straight Line", "📏", listOf(
            generateLine(0.50f, 0.15f, 0.50f, 0.85f, 20)
        )),
        TracingGuideItem("shape_line_h", "Horizontal Line", "—", "shape", "Flat Line", "➖", listOf(
            generateLine(0.15f, 0.50f, 0.85f, 0.50f, 20)
        )),
        TracingGuideItem("shape_line_d", "Diagonal Line", "/", "shape", "Slant Line", "📐", listOf(
            generateLine(0.15f, 0.15f, 0.85f, 0.85f, 20)
        )),
        TracingGuideItem("shape_curve", "Curved Wave", "~", "shape", "Wave Line", "🌊", listOf(
            generateCubicBezier(0.15f, 0.50f, 0.35f, 0.15f, 0.65f, 0.85f, 0.85f, 0.50f, 25)
        )),
        TracingGuideItem("shape_circle", "Circle", "⭕", "shape", "Circle", "⭕", listOf(
            generateCircle(0.50f, 0.50f, 0.35f, 0.35f, -90.0, 360.0, 30)
        )),
        TracingGuideItem("shape_square", "Square", "🟥", "shape", "Square", "🟥", listOf(
            generateLine(0.20f, 0.20f, 0.80f, 0.20f, 12) +
                generateLine(0.80f, 0.20f, 0.80f, 0.80f, 12) +
                generateLine(0.80f, 0.80f, 0.20f, 0.80f, 12) +
                generateLine(0.20f, 0.80f, 0.20f, 0.20f, 12)
        )),
        TracingGuideItem("shape_triangle", "Triangle", "🔺", "shape", "Triangle", "🔺", listOf(
            generateLine(0.50f, 0.15f, 0.85f, 0.85f, 15) +
                generateLine(0.85f, 0.85f, 0.15f, 0.85f, 15) +
                generateLine(0.15f, 0.85f, 0.50f, 0.15f, 15)
        )),
        TracingGuideItem("shape_heart", "Heart", "❤️", "shape", "Heart", "❤️", listOf(
            generateCubicBezier(0.50f, 0.30f, 0.30f, 0.10f, 0.15f, 0.35f, 0.50f, 0.85f, 20) +
                generateCubicBezier(0.50f, 0.85f, 0.85f, 0.35f, 0.70f, 0.10f, 0.50f, 0.30f, 20)
        )),
        TracingGuideItem("shape_star", "Star", "⭐", "shape", "Star", "⭐", listOf(
            generateLine(0.50f, 0.10f, 0.62f, 0.38f, 10) +
                generateLine(0.62f, 0.38f, 0.90f, 0.38f, 10) +
                generateLine(0.90f, 0.38f, 0.68f, 0.58f, 10) +
                generateLine(0.68f, 0.58f, 0.78f, 0.88f, 10) +
                generateLine(0.78f, 0.88f, 0.50f, 0.70f, 10) +
                generateLine(0.50f, 0.70f, 0.22f, 0.88f, 10) +
                generateLine(0.22f, 0.88f, 0.32f, 0.58f, 10) +
                generateLine(0.32f, 0.58f, 0.10f, 0.38f, 10) +
                generateLine(0.10f, 0.38f, 0.38f, 0.38f, 10) +
                generateLine(0.38f, 0.38f, 0.50f, 0.10f, 10)
        ))
    )

    // -------------------------------------------------------------
    // PENCIL CONTROL & ROAD FOLLOWING
    // -------------------------------------------------------------
    val pencilControlRoads: List<TracingGuideItem> = listOf(
        TracingGuideItem("road_straight", "Straight Highway", "🏎️", "road", "Drive Straight", "🏁", listOf(
            generateLine(0.15f, 0.50f, 0.85f, 0.50f, 25)
        )),
        TracingGuideItem("road_zigzag", "Zig-Zag Track", "🚗", "road", "Zig Zag Track", "🏁", listOf(
            generateLine(0.15f, 0.20f, 0.85f, 0.40f, 15) +
                generateLine(0.85f, 0.40f, 0.15f, 0.60f, 15) +
                generateLine(0.15f, 0.60f, 0.85f, 0.80f, 15)
        )),
        TracingGuideItem("road_scurve", "S-Curve Road", "🚙", "road", "S Curve Road", "🏁", listOf(
            generateCubicBezier(0.15f, 0.20f, 0.85f, 0.20f, 0.15f, 0.80f, 0.85f, 0.80f, 30)
        )),
        TracingGuideItem("road_mountain", "Mountain Pass", "🚚", "road", "Mountain Track", "🏁", listOf(
            generateLine(0.10f, 0.80f, 0.30f, 0.20f, 15) +
                generateLine(0.30f, 0.20f, 0.50f, 0.80f, 15) +
                generateLine(0.50f, 0.80f, 0.70f, 0.20f, 15) +
                generateLine(0.70f, 0.20f, 0.90f, 0.80f, 15)
        ))
    )

    fun getAllItems(): List<TracingGuideItem> {
        return uppercaseLetters + lowercaseLetters + numbers
    }

    /**
     * Intelligent evaluation of user handwriting input against letter stroke guide points.
     * Checks:
     * 1. Minimum path points & stroke length
     * 2. Writing inside guidelines (Accuracy Ratio >= 70%)
     * 3. Full letter stroke coverage (Coverage Ratio >= 65% & individual stroke completion)
     * 4. Bounding box & shape similarity
     * 5. Stroke direction & flow trajectory
     */
    fun validateHandwritingTracing(
        drawnPoints: List<Pair<Float, Float>>,
        strokes: List<List<Pair<Float, Float>>>,
        canvasSize: Float
    ): TracingValidationResult {
        if (drawnPoints.size < 12) {
            return TracingValidationResult(
                isValid = false,
                coverage = 0f,
                accuracy = 0f,
                directionCorrect = false,
                failedStrokeIndex = 0,
                message = "Try Again! Trace the complete letter. ❌"
            )
        }

        // Child-friendly guideline tolerance (~12.5% of canvas size)
        val tolerance = canvasSize * 0.125f

        // 1. Calculate total path length to detect random scribbles/zigzags
        var totalDrawnLen = 0f
        for (i in 0 until drawnPoints.size - 1) {
            val (x1, y1) = drawnPoints[i]
            val (x2, y2) = drawnPoints[i + 1]
            totalDrawnLen += hypot(x2 - x1, y2 - y1)
        }

        var totalGuideLen = 0f
        strokes.forEach { stroke ->
            for (i in 0 until stroke.size - 1) {
                val gx1 = stroke[i].first * canvasSize
                val gy1 = stroke[i].second * canvasSize
                val gx2 = stroke[i + 1].first * canvasSize
                val gy2 = stroke[i + 1].second * canvasSize
                totalGuideLen += hypot(gx2 - gx1, gy2 - gy1)
            }
        }

        if (totalGuideLen > 0f && totalDrawnLen > 2.3f * totalGuideLen) {
            return TracingValidationResult(
                isValid = false,
                coverage = 0f,
                accuracy = 0f,
                directionCorrect = false,
                failedStrokeIndex = 0,
                message = "Try Again! Do not scribble. Trace neatly. ❌"
            )
        }

        // 2. Accuracy Check: Percentage of drawn points that lie strictly inside letter guidelines
        var pointsOnTrack = 0
        drawnPoints.forEach { (ux, uy) ->
            var onTrack = false
            for (stroke in strokes) {
                for (guideP in stroke) {
                    val gx = guideP.first * canvasSize
                    val gy = guideP.second * canvasSize
                    if (hypot(ux - gx, uy - gy) <= tolerance) {
                        onTrack = true
                        break
                    }
                }
                if (onTrack) break
            }
            if (onTrack) pointsOnTrack++
        }

        val accuracyRatio = pointsOnTrack.toFloat() / drawnPoints.size.toFloat()
        if (accuracyRatio < 0.72f) {
            return TracingValidationResult(
                isValid = false,
                coverage = 0f,
                accuracy = accuracyRatio,
                directionCorrect = false,
                failedStrokeIndex = 0,
                message = "Try Again! Follow the letter lines carefully. ❌"
            )
        }

        // 3. Stroke Coverage Check: Overall letter completion and individual stroke completion
        var totalGuidePoints = 0
        var totalCoveredGuidePoints = 0
        var allStrokesSatisfied = true

        strokes.forEach { stroke ->
            if (stroke.isEmpty()) return@forEach
            totalGuidePoints += stroke.size

            var strokeCoveredCount = 0
            stroke.forEach { guideP ->
                val gx = guideP.first * canvasSize
                val gy = guideP.second * canvasSize
                val isCovered = drawnPoints.any { (ux, uy) -> hypot(ux - gx, uy - gy) <= tolerance }
                if (isCovered) {
                    strokeCoveredCount++
                }
            }
            totalCoveredGuidePoints += strokeCoveredCount

            val singleStrokeRatio = strokeCoveredCount.toFloat() / stroke.size.toFloat()
            if (singleStrokeRatio < 0.50f) {
                allStrokesSatisfied = false
            }
        }

        val coverageRatio = if (totalGuidePoints > 0) totalCoveredGuidePoints.toFloat() / totalGuidePoints.toFloat() else 1f
        if (coverageRatio < 0.68f || !allStrokesSatisfied) {
            return TracingValidationResult(
                isValid = false,
                coverage = coverageRatio,
                accuracy = accuracyRatio,
                directionCorrect = false,
                failedStrokeIndex = 0,
                message = "Try Again! Trace all parts of the letter. ❌"
            )
        }

        // 4. Shape & Bounding Box Similarity Check
        val allGuidePts = strokes.flatten()
        if (allGuidePts.isNotEmpty()) {
            val gMinX = allGuidePts.minOf { it.first } * canvasSize
            val gMaxX = allGuidePts.maxOf { it.first } * canvasSize
            val gMinY = allGuidePts.minOf { it.second } * canvasSize
            val gMaxY = allGuidePts.maxOf { it.second } * canvasSize
            val gWidth = (gMaxX - gMinX).coerceAtLeast(10f)
            val gHeight = (gMaxY - gMinY).coerceAtLeast(10f)

            val uMinX = drawnPoints.minOf { it.first }
            val uMaxX = drawnPoints.maxOf { it.first }
            val uMinY = drawnPoints.minOf { it.second }
            val uMaxY = drawnPoints.maxOf { it.second }
            val uWidth = uMaxX - uMinX
            val uHeight = uMaxY - uMinY

            val widthRatio = uWidth / gWidth
            val heightRatio = uHeight / gHeight

            if (widthRatio < 0.58f || heightRatio < 0.58f || widthRatio > 1.50f || heightRatio > 1.50f) {
                return TracingValidationResult(
                    isValid = false,
                    coverage = coverageRatio,
                    accuracy = accuracyRatio,
                    directionCorrect = false,
                    failedStrokeIndex = 0,
                    message = "Try Again! Match the letter size and shape. ❌"
                )
            }
        }

        // 5. Direction & Trajectory Flow Check
        var directionScore = 0
        var totalSegmentChecks = 0
        val flatStrokes = strokes.flatten()
        if (flatStrokes.size > 5) {
            for (i in 0 until drawnPoints.size - 3 step 3) {
                val p1 = drawnPoints[i]
                val p2 = drawnPoints[i + 3]
                val dx = p2.first - p1.first
                val dy = p2.second - p1.second
                if (hypot(dx, dy) > 4f) {
                    var bestGuideIdx = -1
                    var bestDist = Float.MAX_VALUE
                    flatStrokes.forEachIndexed { gIdx, gPt ->
                        val d = hypot(p1.first - gPt.first * canvasSize, p1.second - gPt.second * canvasSize)
                        if (d < bestDist) {
                            bestDist = d
                            bestGuideIdx = gIdx
                        }
                    }

                    if (bestGuideIdx >= 0 && bestGuideIdx + 2 < flatStrokes.size) {
                        val gPt1 = flatStrokes[bestGuideIdx]
                        val gPt2 = flatStrokes[bestGuideIdx + 2]
                        val gdx = gPt2.first * canvasSize - gPt1.first * canvasSize
                        val gdy = gPt2.second * canvasSize - gPt1.second * canvasSize
                        val dot = dx * gdx + dy * gdy
                        totalSegmentChecks++
                        if (dot >= -100f) {
                            directionScore++
                        }
                    }
                }
            }
        }

        val directionRatio = if (totalSegmentChecks > 0) directionScore.toFloat() / totalSegmentChecks.toFloat() else 1f
        if (directionRatio < 0.50f) {
            return TracingValidationResult(
                isValid = false,
                coverage = coverageRatio,
                accuracy = accuracyRatio,
                directionCorrect = false,
                failedStrokeIndex = 0,
                message = "Try Again! Follow stroke direction arrows. ❌"
            )
        }

        return TracingValidationResult(
            isValid = true,
            coverage = coverageRatio,
            accuracy = accuracyRatio,
            directionCorrect = true,
            failedStrokeIndex = -1,
            message = "Superb Handwriting! ⭐"
        )
    }
}

data class TracingValidationResult(
    val isValid: Boolean,
    val coverage: Float,
    val accuracy: Float,
    val directionCorrect: Boolean,
    val failedStrokeIndex: Int,
    val message: String
)
