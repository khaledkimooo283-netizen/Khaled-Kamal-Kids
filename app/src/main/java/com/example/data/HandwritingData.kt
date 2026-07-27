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
    // -------------------------------------------------------------
    val uppercaseLetters: List<TracingGuideItem> = listOf(
        // A
        TracingGuideItem("upper_A", "Capital A", "A", "uppercase", "Apple", "🍎", listOf(
            generateLine(0.22f, 0.88f, 0.50f, 0.12f, 15), // 1: Left slant up
            generateLine(0.50f, 0.12f, 0.78f, 0.88f, 15), // 2: Right slant down
            generateLine(0.32f, 0.58f, 0.68f, 0.58f, 12)  // 3: Crossbar
        )),
        // B
        TracingGuideItem("upper_B", "Capital B", "B", "uppercase", "Ball", "⚽", listOf(
            generateLine(0.25f, 0.12f, 0.25f, 0.88f, 15), // 1: Vertical down
            generateArc(0.25f, 0.12f, 0.25f, 0.50f, 0.75f, 0.31f, 16), // 2: Top loop
            generateArc(0.25f, 0.50f, 0.25f, 0.88f, 0.80f, 0.69f, 16)  // 3: Bottom loop
        )),
        // C
        TracingGuideItem("upper_C", "Capital C", "C", "uppercase", "Cat", "🐱", listOf(
            generateArc(0.75f, 0.25f, 0.75f, 0.75f, 0.18f, 0.50f, 26) // 1: C-curve
        )),
        // D
        TracingGuideItem("upper_D", "Capital D", "D", "uppercase", "Dog", "🐶", listOf(
            generateLine(0.25f, 0.12f, 0.25f, 0.88f, 15), // 1: Vertical down
            generateArc(0.25f, 0.12f, 0.25f, 0.88f, 0.82f, 0.50f, 24) // 2: Big curve
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
            generateArc(0.75f, 0.25f, 0.75f, 0.75f, 0.18f, 0.50f, 24), // 1: Main curve
            generateLine(0.75f, 0.75f, 0.75f, 0.52f, 10), // 2: Upward stem
            generateLine(0.75f, 0.52f, 0.52f, 0.52f, 10)  // 3: Inward bar
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
            generateArc(0.50f, 0.12f, 0.28f, 0.75f, 0.50f, 0.88f, 20) // 2: Stem with bottom hook
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
            generateCircle(0.50f, 0.50f, 0.32f, 0.38f, -90.0, 360.0, 28) // 1: Circle
        )),
        // P
        TracingGuideItem("upper_P", "Capital P", "P", "uppercase", "Penguin", "🐧", listOf(
            generateLine(0.25f, 0.12f, 0.25f, 0.88f, 15), // 1: Vertical stem
            generateArc(0.25f, 0.12f, 0.25f, 0.52f, 0.78f, 0.32f, 18) // 2: Loop
        )),
        // Q
        TracingGuideItem("upper_Q", "Capital Q", "Q", "uppercase", "Queen", "👑", listOf(
            generateCircle(0.50f, 0.48f, 0.30f, 0.36f, -90.0, 360.0, 28), // 1: Circle
            generateLine(0.55f, 0.65f, 0.80f, 0.90f, 10)  // 2: Tail
        )),
        // R
        TracingGuideItem("upper_R", "Capital R", "R", "uppercase", "Rabbit", "🐰", listOf(
            generateLine(0.25f, 0.12f, 0.25f, 0.88f, 15), // 1: Vertical stem
            generateArc(0.25f, 0.12f, 0.25f, 0.50f, 0.78f, 0.31f, 16), // 2: Loop
            generateLine(0.25f, 0.50f, 0.75f, 0.88f, 14)  // 3: Leg
        )),
        // S
        TracingGuideItem("upper_S", "Capital S", "S", "uppercase", "Sun", "☀️", listOf(
            listOf(
                Pair(0.72f, 0.22f), Pair(0.50f, 0.12f), Pair(0.28f, 0.25f),
                Pair(0.35f, 0.45f), Pair(0.50f, 0.50f), Pair(0.68f, 0.58f),
                Pair(0.72f, 0.75f), Pair(0.50f, 0.88f), Pair(0.25f, 0.80f)
            )
        )),
        // T
        TracingGuideItem("upper_T", "Capital T", "T", "uppercase", "Train", "🚂", listOf(
            generateLine(0.20f, 0.12f, 0.80f, 0.12f, 14), // 1: Top bar
            generateLine(0.50f, 0.12f, 0.50f, 0.88f, 15)  // 2: Center stem
        )),
        // U
        TracingGuideItem("upper_U", "Capital U", "U", "uppercase", "Umbrella", "☂️", listOf(
            generateArc(0.25f, 0.12f, 0.75f, 0.12f, 0.50f, 0.92f, 24) // 1: U-curve
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
    // -------------------------------------------------------------
    val lowercaseLetters: List<TracingGuideItem> = listOf(
        // a
        TracingGuideItem("lower_a", "lowercase a", "a", "lowercase", "apple", "🍎", listOf(
            generateCircle(0.52f, 0.65f, 0.22f, 0.22f, 0.0, -360.0, 22), // 1: c-loop
            generateLine(0.74f, 0.43f, 0.74f, 0.88f, 12) // 2: Right stem
        )),
        // b
        TracingGuideItem("lower_b", "lowercase b", "b", "lowercase", "ball", "⚽", listOf(
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 16), // 1: Tall stem
            generateArc(0.28f, 0.50f, 0.28f, 0.88f, 0.75f, 0.69f, 18) // 2: Lower loop
        )),
        // c
        TracingGuideItem("lower_c", "lowercase c", "c", "lowercase", "cat", "🐱", listOf(
            generateArc(0.72f, 0.50f, 0.72f, 0.82f, 0.25f, 0.66f, 20) // 1: Small c-curve
        )),
        // d
        TracingGuideItem("lower_d", "lowercase d", "d", "lowercase", "dog", "🐶", listOf(
            generateCircle(0.48f, 0.65f, 0.22f, 0.22f, 0.0, -360.0, 20), // 1: c-loop
            generateLine(0.70f, 0.12f, 0.70f, 0.88f, 16) // 2: Tall stem
        )),
        // e
        TracingGuideItem("lower_e", "lowercase e", "e", "lowercase", "elephant", "🐘", listOf(
            generateLine(0.30f, 0.64f, 0.70f, 0.64f, 10), // 1: Mid horizontal bar
            generateArc(0.70f, 0.64f, 0.70f, 0.84f, 0.22f, 0.42f, 22) // 2: Loop around
        )),
        // f
        TracingGuideItem("lower_f", "lowercase f", "f", "lowercase", "fish", "🐟", listOf(
            generateArc(0.68f, 0.18f, 0.45f, 0.88f, 0.45f, 0.12f, 18), // 1: Top hook & stem
            generateLine(0.28f, 0.45f, 0.68f, 0.45f, 10)  // 2: Crossbar
        )),
        // g
        TracingGuideItem("lower_g", "lowercase g", "g", "lowercase", "giraffe", "🦒", listOf(
            generateCircle(0.50f, 0.52f, 0.22f, 0.20f, 0.0, -360.0, 20), // 1: Upper c-loop
            generateArc(0.72f, 0.38f, 0.35f, 0.94f, 0.72f, 0.88f, 20)  // 2: Descender tail with hook
        )),
        // h
        TracingGuideItem("lower_h", "lowercase h", "h", "lowercase", "hat", "🎩", listOf(
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 16), // 1: Tall stem
            generateArc(0.28f, 0.55f, 0.72f, 0.88f, 0.50f, 0.42f, 18) // 2: Hump
        )),
        // i
        TracingGuideItem("lower_i", "lowercase i", "i", "lowercase", "ice cream", "🍦", listOf(
            generateLine(0.50f, 0.42f, 0.50f, 0.88f, 12), // 1: Short stem
            generateLine(0.50f, 0.22f, 0.50f, 0.25f, 2)   // 2: Dot
        )),
        // j
        TracingGuideItem("lower_j", "lowercase j", "j", "lowercase", "juice", "🧃", listOf(
            generateArc(0.55f, 0.42f, 0.28f, 0.94f, 0.55f, 0.88f, 18), // 1: Descender stem hook
            generateLine(0.55f, 0.22f, 0.55f, 0.25f, 2)   // 2: Dot
        )),
        // k
        TracingGuideItem("lower_k", "lowercase k", "k", "lowercase", "kite", "🪁", listOf(
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 16), // 1: Tall stem
            generateLine(0.70f, 0.42f, 0.28f, 0.65f, 10), // 2: Slant in
            generateLine(0.28f, 0.65f, 0.70f, 0.88f, 10)  // 3: Slant out
        )),
        // l
        TracingGuideItem("lower_l", "lowercase l", "l", "lowercase", "lion", "🦁", listOf(
            generateLine(0.50f, 0.12f, 0.50f, 0.88f, 16) // 1: Tall stem
        )),
        // m
        TracingGuideItem("lower_m", "lowercase m", "m", "lowercase", "monkey", "🐒", listOf(
            generateLine(0.20f, 0.42f, 0.20f, 0.88f, 12), // 1: Short stem
            generateArc(0.20f, 0.52f, 0.50f, 0.88f, 0.35f, 0.42f, 14), // 2: First hump
            generateArc(0.50f, 0.52f, 0.80f, 0.88f, 0.65f, 0.42f, 14)  // 3: Second hump
        )),
        // n
        TracingGuideItem("lower_n", "lowercase n", "n", "lowercase", "nest", "🪹", listOf(
            generateLine(0.28f, 0.42f, 0.28f, 0.88f, 12), // 1: Short stem
            generateArc(0.28f, 0.52f, 0.72f, 0.88f, 0.50f, 0.42f, 16)  // 2: Hump
        )),
        // o
        TracingGuideItem("lower_o", "lowercase o", "o", "lowercase", "owl", "🦉", listOf(
            generateCircle(0.50f, 0.65f, 0.22f, 0.22f, -90.0, 360.0, 22) // 1: Small circle
        )),
        // p
        TracingGuideItem("lower_p", "lowercase p", "p", "lowercase", "penguin", "🐧", listOf(
            generateLine(0.28f, 0.42f, 0.28f, 0.94f, 15), // 1: Descender stem
            generateArc(0.28f, 0.42f, 0.28f, 0.78f, 0.75f, 0.60f, 18) // 2: Loop
        )),
        // q
        TracingGuideItem("lower_q", "lowercase q", "q", "lowercase", "queen", "👑", listOf(
            generateCircle(0.50f, 0.60f, 0.22f, 0.20f, 0.0, -360.0, 20), // 1: c-loop
            generateLine(0.72f, 0.42f, 0.72f, 0.94f, 15) // 2: Descender stem
        )),
        // r
        TracingGuideItem("lower_r", "lowercase r", "r", "lowercase", "rabbit", "🐰", listOf(
            generateLine(0.32f, 0.42f, 0.32f, 0.88f, 12), // 1: Short stem
            generateArc(0.32f, 0.55f, 0.70f, 0.48f, 0.52f, 0.42f, 12) // 2: Hook
        )),
        // s
        TracingGuideItem("lower_s", "lowercase s", "s", "lowercase", "sun", "☀️", listOf(
            listOf(
                Pair(0.68f, 0.48f), Pair(0.50f, 0.42f), Pair(0.32f, 0.52f),
                Pair(0.50f, 0.65f), Pair(0.68f, 0.78f), Pair(0.50f, 0.88f), Pair(0.32f, 0.82f)
            )
        )),
        // t
        TracingGuideItem("lower_t", "lowercase t", "t", "lowercase", "train", "🚂", listOf(
            generateArc(0.50f, 0.18f, 0.65f, 0.88f, 0.50f, 0.88f, 16), // 1: Stem with bottom flick
            generateLine(0.30f, 0.40f, 0.70f, 0.40f, 10)  // 2: Crossbar
        )),
        // u
        TracingGuideItem("lower_u", "lowercase u", "u", "lowercase", "umbrella", "☂️", listOf(
            generateArc(0.28f, 0.42f, 0.72f, 0.42f, 0.50f, 0.88f, 20), // 1: U-curve
            generateLine(0.72f, 0.42f, 0.72f, 0.88f, 10)  // 2: Right tail
        )),
        // v
        TracingGuideItem("lower_v", "lowercase v", "v", "lowercase", "violin", "🎻", listOf(
            generateLine(0.24f, 0.42f, 0.50f, 0.88f, 12), // 1: Slant down
            generateLine(0.50f, 0.88f, 0.76f, 0.42f, 12)  // 2: Slant up
        )),
        // w
        TracingGuideItem("lower_w", "lowercase w", "w", "lowercase", "watermelon", "🍉", listOf(
            generateLine(0.18f, 0.42f, 0.34f, 0.88f, 10), // 1: Slant 1
            generateLine(0.34f, 0.88f, 0.50f, 0.60f, 10), // 2: Slant 2
            generateLine(0.50f, 0.60f, 0.66f, 0.88f, 10), // 3: Slant 3
            generateLine(0.66f, 0.88f, 0.82f, 0.42f, 10)  // 4: Slant 4
        )),
        // x
        TracingGuideItem("lower_x", "lowercase x", "x", "lowercase", "xylophone", "🎼", listOf(
            generateLine(0.25f, 0.42f, 0.75f, 0.88f, 14), // 1: Slant 1
            generateLine(0.75f, 0.42f, 0.25f, 0.88f, 14)  // 2: Slant 2
        )),
        // y
        TracingGuideItem("lower_y", "lowercase y", "y", "lowercase", "yo-yo", "🪀", listOf(
            generateLine(0.25f, 0.42f, 0.50f, 0.70f, 10), // 1: Slant left
            generateLine(0.75f, 0.42f, 0.25f, 0.94f, 15)  // 2: Descender slant
        )),
        // z
        TracingGuideItem("lower_z", "lowercase z", "z", "lowercase", "zebra", "🦓", listOf(
            generateLine(0.25f, 0.42f, 0.75f, 0.42f, 10), // 1: Top bar
            generateLine(0.75f, 0.42f, 0.25f, 0.88f, 14), // 2: Diagonal
            generateLine(0.25f, 0.88f, 0.75f, 0.88f, 10)  // 3: Bottom bar
        ))
    )

    // -------------------------------------------------------------
    // NUMBERS (0-20) - Official Kindergarten Writing Style
    // -------------------------------------------------------------
    val numbers: List<TracingGuideItem> = listOf(
        // 0
        TracingGuideItem("num_0", "Number 0", "0", "number", "Zero", "⭕", listOf(
            generateCircle(0.50f, 0.50f, 0.28f, 0.38f, -90.0, 360.0, 28) // Oval zero
        )),
        // 1
        TracingGuideItem("num_1", "Number 1", "1", "number", "One", "🍎", listOf(
            generateLine(0.35f, 0.25f, 0.50f, 0.12f, 8),  // Slant top
            generateLine(0.50f, 0.12f, 0.50f, 0.88f, 16), // Center stem
            generateLine(0.30f, 0.88f, 0.70f, 0.88f, 10)  // Base bar
        )),
        // 2
        TracingGuideItem("num_2", "Number 2", "2", "number", "Two", "🍌", listOf(
            generateArc(0.25f, 0.28f, 0.75f, 0.52f, 0.50f, 0.12f, 18), // Top arch
            generateLine(0.75f, 0.52f, 0.25f, 0.88f, 14), // Slant down
            generateLine(0.25f, 0.88f, 0.80f, 0.88f, 12)  // Base line
        )),
        // 3
        TracingGuideItem("num_3", "Number 3", "3", "number", "Three", "🍓", listOf(
            generateArc(0.30f, 0.15f, 0.30f, 0.50f, 0.78f, 0.32f, 16), // Top loop
            generateArc(0.30f, 0.50f, 0.30f, 0.88f, 0.82f, 0.69f, 16)  // Bottom loop
        )),
        // 4
        TracingGuideItem("num_4", "Number 4", "4", "number", "Four", "🍊", listOf(
            generateLine(0.70f, 0.12f, 0.22f, 0.60f, 15), // Left slant down
            generateLine(0.22f, 0.60f, 0.82f, 0.60f, 12), // Horizontal crossbar
            generateLine(0.70f, 0.35f, 0.70f, 0.88f, 12)  // Vertical stem
        )),
        // 5
        TracingGuideItem("num_5", "Number 5", "5", "number", "Five", "🧁", listOf(
            generateLine(0.75f, 0.12f, 0.35f, 0.12f, 10), // Top horizontal
            generateLine(0.35f, 0.12f, 0.35f, 0.45f, 10), // Neck down
            generateArc(0.35f, 0.45f, 0.30f, 0.88f, 0.80f, 0.66f, 18) // Bottom belly curve
        )),
        // 6
        TracingGuideItem("num_6", "Number 6", "6", "number", "Six", "⭐", listOf(
            generateArc(0.70f, 0.18f, 0.28f, 0.60f, 0.28f, 0.12f, 18), // Slant curve down
            generateCircle(0.50f, 0.64f, 0.22f, 0.24f, 0.0, 360.0, 22) // Bottom loop
        )),
        // 7
        TracingGuideItem("num_7", "Number 7", "7", "number", "Seven", "🎈", listOf(
            generateLine(0.22f, 0.12f, 0.80f, 0.12f, 12), // Top horizontal
            generateLine(0.80f, 0.12f, 0.35f, 0.88f, 16)  // Slant down
        )),
        // 8
        TracingGuideItem("num_8", "Number 8", "8", "number", "Eight", "🌸", listOf(
            generateCircle(0.50f, 0.32f, 0.20f, 0.20f, -90.0, 360.0, 20), // Top loop
            generateCircle(0.50f, 0.68f, 0.24f, 0.22f, -90.0, 360.0, 22)  // Bottom loop
        )),
        // 9
        TracingGuideItem("num_9", "Number 9", "9", "number", "Nine", "🍪", listOf(
            generateCircle(0.50f, 0.35f, 0.22f, 0.22f, -90.0, 360.0, 22), // Top loop
            generateLine(0.72f, 0.12f, 0.72f, 0.88f, 16)  // Vertical right stem
        )),
        // 10
        TracingGuideItem("num_10", "Number 10", "10", "number", "Ten", "🦆", listOf(
            // Digit 1
            generateLine(0.20f, 0.25f, 0.32f, 0.12f, 8),
            generateLine(0.32f, 0.12f, 0.32f, 0.88f, 15),
            generateLine(0.18f, 0.88f, 0.46f, 0.88f, 10),
            // Digit 0
            generateCircle(0.70f, 0.50f, 0.18f, 0.38f, -90.0, 360.0, 24)
        )),
        // 11
        TracingGuideItem("num_11", "Number 11", "11", "number", "Eleven", "🌟", listOf(
            // Digit 1
            generateLine(0.18f, 0.25f, 0.30f, 0.12f, 8),
            generateLine(0.30f, 0.12f, 0.30f, 0.88f, 15),
            generateLine(0.16f, 0.88f, 0.44f, 0.88f, 10),
            // Digit 1
            generateLine(0.58f, 0.25f, 0.70f, 0.12f, 8),
            generateLine(0.70f, 0.12f, 0.70f, 0.88f, 15),
            generateLine(0.56f, 0.88f, 0.84f, 0.88f, 10)
        )),
        // 12
        TracingGuideItem("num_12", "Number 12", "12", "number", "Twelve", "🚀", listOf(
            // Digit 1
            generateLine(0.16f, 0.25f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 2
            generateArc(0.48f, 0.28f, 0.85f, 0.52f, 0.66f, 0.12f, 16),
            generateLine(0.85f, 0.52f, 0.48f, 0.88f, 14),
            generateLine(0.48f, 0.88f, 0.88f, 0.88f, 12)
        )),
        // 13
        TracingGuideItem("num_13", "Number 13", "13", "number", "Thirteen", "🦋", listOf(
            // Digit 1
            generateLine(0.16f, 0.25f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 3
            generateArc(0.50f, 0.15f, 0.50f, 0.50f, 0.88f, 0.32f, 14),
            generateArc(0.50f, 0.50f, 0.50f, 0.88f, 0.90f, 0.69f, 14)
        )),
        // 14
        TracingGuideItem("num_14", "Number 14", "14", "number", "Fourteen", "🍧", listOf(
            // Digit 1
            generateLine(0.16f, 0.25f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 4
            generateLine(0.80f, 0.12f, 0.46f, 0.60f, 14),
            generateLine(0.46f, 0.60f, 0.88f, 0.60f, 12),
            generateLine(0.80f, 0.35f, 0.80f, 0.88f, 12)
        )),
        // 15
        TracingGuideItem("num_15", "Number 15", "15", "number", "Fifteen", "🐝", listOf(
            // Digit 1
            generateLine(0.16f, 0.25f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 5
            generateLine(0.85f, 0.12f, 0.52f, 0.12f, 10),
            generateLine(0.52f, 0.12f, 0.52f, 0.45f, 10),
            generateArc(0.52f, 0.45f, 0.48f, 0.88f, 0.88f, 0.66f, 16)
        )),
        // 16
        TracingGuideItem("num_16", "Number 16", "16", "number", "Sixteen", "🐼", listOf(
            // Digit 1
            generateLine(0.16f, 0.25f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 6
            generateArc(0.84f, 0.18f, 0.50f, 0.60f, 0.50f, 0.12f, 16),
            generateCircle(0.68f, 0.64f, 0.18f, 0.24f, 0.0, 360.0, 20)
        )),
        // 17
        TracingGuideItem("num_17", "Number 17", "17", "number", "Seventeen", "🎨", listOf(
            // Digit 1
            generateLine(0.16f, 0.25f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 7
            generateLine(0.46f, 0.12f, 0.88f, 0.12f, 12),
            generateLine(0.88f, 0.12f, 0.54f, 0.88f, 16)
        )),
        // 18
        TracingGuideItem("num_18", "Number 18", "18", "number", "Eighteen", "🍕", listOf(
            // Digit 1
            generateLine(0.16f, 0.25f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 8
            generateCircle(0.68f, 0.32f, 0.18f, 0.20f, -90.0, 360.0, 18),
            generateCircle(0.68f, 0.68f, 0.20f, 0.22f, -90.0, 360.0, 20)
        )),
        // 19
        TracingGuideItem("num_19", "Number 19", "19", "number", "Nineteen", "🍔", listOf(
            // Digit 1
            generateLine(0.16f, 0.25f, 0.28f, 0.12f, 8),
            generateLine(0.28f, 0.12f, 0.28f, 0.88f, 15),
            generateLine(0.14f, 0.88f, 0.42f, 0.88f, 10),
            // Digit 9
            generateCircle(0.66f, 0.35f, 0.18f, 0.22f, -90.0, 360.0, 20),
            generateLine(0.84f, 0.12f, 0.84f, 0.88f, 16)
        )),
        // 20
        TracingGuideItem("num_20", "Number 20", "20", "number", "Twenty", "👑", listOf(
            // Digit 2
            generateArc(0.14f, 0.28f, 0.46f, 0.52f, 0.30f, 0.12f, 16),
            generateLine(0.46f, 0.52f, 0.14f, 0.88f, 14),
            generateLine(0.14f, 0.88f, 0.48f, 0.88f, 12),
            // Digit 0
            generateCircle(0.72f, 0.50f, 0.18f, 0.38f, -90.0, 360.0, 24)
        ))
    )

    fun getAllItems(): List<TracingGuideItem> {
        return uppercaseLetters + lowercaseLetters + numbers
    }

    /**
     * Evaluates user tracing input against guide stroke points using shape recognition.
     * Accepts tracing from any starting point or direction as long as the overall shape matches.
     */
    fun validateHandwritingTracing(
        drawnPoints: List<Pair<Float, Float>>,
        strokes: List<List<Pair<Float, Float>>>,
        canvasSize: Float
    ): TracingValidationResult {
        if (drawnPoints.size < 5) {
            return TracingValidationResult(
                isValid = false,
                coverage = 0f,
                accuracy = 0f,
                directionCorrect = true,
                failedStrokeIndex = 0,
                message = "Trace along the letter shape! ✏️"
            )
        }

        // Child-friendly tolerance radius (~15% of canvas size)
        val tolerance = canvasSize * 0.15f

        var totalGuidePoints = 0
        var coveredGuidePoints = 0

        strokes.forEach { stroke ->
            if (stroke.isEmpty()) return@forEach

            totalGuidePoints += stroke.size

            stroke.forEach { guideP ->
                val gx = guideP.first * canvasSize
                val gy = guideP.second * canvasSize
                val covered = drawnPoints.any { (ux, uy) -> hypot(ux - gx, uy - gy) <= tolerance }
                if (covered) {
                    coveredGuidePoints++
                }
            }
        }

        val coverageRatio = if (totalGuidePoints > 0) coveredGuidePoints.toFloat() / totalGuidePoints.toFloat() else 1f

        // Check user point accuracy (points near any stroke to prevent wild scribbles)
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

        // Flexible shape recognition: child can start ANYWHERE and draw in ANY order!
        val isPassed = coverageRatio >= 0.58f && accuracyRatio >= 0.58f

        val msg = when {
            coverageRatio < 0.58f -> "Trace a bit more of the letter shape! ✏️"
            accuracyRatio < 0.58f -> "Try to stay near the letter shape! 🎯"
            else -> "Superb Handwriting! ⭐"
        }

        return TracingValidationResult(
            isValid = isPassed,
            coverage = coverageRatio,
            accuracy = accuracyRatio,
            directionCorrect = true,
            failedStrokeIndex = -1,
            message = msg
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
