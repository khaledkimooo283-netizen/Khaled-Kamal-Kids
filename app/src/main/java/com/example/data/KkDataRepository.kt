package com.example.data

import android.content.Context
import android.content.SharedPreferences

data class LetterItem(
    val letter: Char,
    val word: String,
    val emoji: String,
    val phonetic: String,
    val colorHex: Long,
    val strokeGuidePoints: List<List<Pair<Float, Float>>> = emptyList()
)

data class NumberItem(
    val number: Int,
    val word: String,
    val emoji: String,
    val colorHex: Long,
    val strokeGuidePoints: List<List<Pair<Float, Float>>> = emptyList()
)

data class AnimalItem(
    val id: String,
    val name: String,
    val emoji: String,
    val soundText: String,
    val description: String
)

data class MatchPair(
    val id: String,
    val promptText: String,
    val promptEmoji: String,
    val matchText: String,
    val matchEmoji: String,
    val category: String
)

class KkDataRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("kk_kids_prefs", Context.MODE_PRIVATE)

    fun getStars(): Int {
        return prefs.getInt("user_stars", 15)
    }

    fun addStars(count: Int) {
        val current = getStars()
        prefs.edit().putInt("user_stars", current + count).apply()
    }

    fun getStreak(): Int {
        return prefs.getInt("user_streak", 3)
    }

    // List of Alphabet items
    val alphabetList = listOf(
        LetterItem('A', "Apple", "🍎", "Ah ah Apple", 0xFFFF5252, getNormalizedLetterStroke('A')),
        LetterItem('B', "Ball", "⚽", "Buh buh Ball", 0xFF448AFF, getNormalizedLetterStroke('B')),
        LetterItem('C', "Cat", "🐱", "Cuh cuh Cat", 0xFFFFB74D, getNormalizedLetterStroke('C')),
        LetterItem('D', "Dog", "🐶", "Duh duh Dog", 0xFF66BB6A, getNormalizedLetterStroke('D')),
        LetterItem('E', "Elephant", "🐘", "Eh eh Elephant", 0xFFAB47BC, getNormalizedLetterStroke('E')),
        LetterItem('F', "Fish", "🐟", "Fuh fuh Fish", 0xFF26C6DA, getNormalizedLetterStroke('F')),
        LetterItem('G', "Giraffe", "🦒", "Guh guh Giraffe", 0xFFFFA726, getNormalizedLetterStroke('G')),
        LetterItem('H', "Hat", "🎩", "Huh huh Hat", 0xFFEC407A, getNormalizedLetterStroke('H')),
        LetterItem('I', "Ice Cream", "🍦", "Ih ih Ice Cream", 0xFFFF7043, getNormalizedLetterStroke('I')),
        LetterItem('J', "Juice", "🧃", "Juh juh Juice", 0xFF26A69A, getNormalizedLetterStroke('J')),
        LetterItem('K', "Kite", "🪁", "Kuh kuh Kite", 0xFF7E57C2, getNormalizedLetterStroke('K')),
        LetterItem('L', "Lion", "🦁", "Luh luh Lion", 0xFFFFCA28, getNormalizedLetterStroke('L')),
        LetterItem('M', "Monkey", "🐒", "Muh muh Monkey", 0xFF8D6E63, getNormalizedLetterStroke('M')),
        LetterItem('N', "Nest", "🪹", "Nuh nuh Nest", 0xFF78909C, getNormalizedLetterStroke('N')),
        LetterItem('O', "Owl", "🦉", "Ah ah Owl", 0xFF5C6BC0, getNormalizedLetterStroke('O')),
        LetterItem('P', "Penguin", "🐧", "Puh puh Penguin", 0xFF42A5F5, getNormalizedLetterStroke('P')),
        LetterItem('Q', "Queen", "👑", "Quah quah Queen", 0xFFD4E157, getNormalizedLetterStroke('Q')),
        LetterItem('R', "Rabbit", "🐰", "Ruh ruh Rabbit", 0xFFFF8A65, getNormalizedLetterStroke('R')),
        LetterItem('S', "Sun", "☀️", "Suh suh Sun", 0xFFFFD54F, getNormalizedLetterStroke('S')),
        LetterItem('T', "Train", "🚂", "Tuh tuh Train", 0xFF26C6DA, getNormalizedLetterStroke('T')),
        LetterItem('U', "Umbrella", "☂️", "Uh uh Umbrella", 0xFFBA68C8, getNormalizedLetterStroke('U')),
        LetterItem('V', "Violin", "🎻", "Vuh vuh Violin", 0xFF81C784, getNormalizedLetterStroke('V')),
        LetterItem('W', "Watermelon", "🍉", "Wuh wuh Watermelon", 0xFFFF5252, getNormalizedLetterStroke('W')),
        LetterItem('X', "Xylophone", "🎼", "Ks ks Xylophone", 0xFF4DD0E1, getNormalizedLetterStroke('X')),
        LetterItem('Y', "Yo-yo", "🪀", "Yuh yuh Yo-yo", 0xFFFFB74D, getNormalizedLetterStroke('Y')),
        LetterItem('Z', "Zebra", "🦓", "Zuh zuh Zebra", 0xFFA1887F, getNormalizedLetterStroke('Z'))
    )

    // List of Number items
    val numberList = listOf(
        NumberItem(1, "One", "🍎", 0xFFFF5252, getNormalizedNumberStroke(1)),
        NumberItem(2, "Two", "🍌", 0xFFFFB74D, getNormalizedNumberStroke(2)),
        NumberItem(3, "Three", "🍓", 0xFFFF4081, getNormalizedNumberStroke(3)),
        NumberItem(4, "Four", "🍊", 0xFFFFA726, getNormalizedNumberStroke(4)),
        NumberItem(5, "Five", "🧁", 0xFFAB47BC, getNormalizedNumberStroke(5)),
        NumberItem(6, "Six", "⭐", 0xFFFFCA28, getNormalizedNumberStroke(6)),
        NumberItem(7, "Seven", "🎈", 0xFF42A5F5, getNormalizedNumberStroke(7)),
        NumberItem(8, "Eight", "🌸", 0xFFEC407A, getNormalizedNumberStroke(8)),
        NumberItem(9, "Nine", "🍪", 0xFF8D6E63, getNormalizedNumberStroke(9)),
        NumberItem(10, "Ten", "🦆", 0xFF26C6DA, getNormalizedNumberStroke(10))
    )

    // Animals list
    val animalsList = listOf(
        AnimalItem("lion", "Lion", "🦁", "Roar!", "King of the Jungle"),
        AnimalItem("dog", "Dog", "🐶", "Woof Woof!", "Friendly pet"),
        AnimalItem("cat", "Cat", "🐱", "Meow!", "Playful kitten"),
        AnimalItem("cow", "Cow", "🐮", "Moo!", "Gives yummy milk"),
        AnimalItem("duck", "Duck", "🦆", "Quack Quack!", "Swims in the pond"),
        AnimalItem("frog", "Frog", "🐸", "Ribbit Ribbit!", "Jumps super high"),
        AnimalItem("sheep", "Sheep", "🐑", "Baa Baa!", "Soft and fluffy"),
        AnimalItem("monkey", "Monkey", "🐒", "Ooh Ooh Aah Aah!", "Loves sweet bananas")
    )

    // Logical Matching Pairs for Drag & Match Games
    val matchPairsList = listOf(
        MatchPair("m1", "Letter A", "🅰️", "Apple", "🍎", "letters"),
        MatchPair("m2", "Letter B", "🅱️", "Ball", "⚽", "letters"),
        MatchPair("m3", "Letter C", "🔤", "Cat", "🐱", "letters"),
        MatchPair("m4", "Letter D", "🔤", "Dog", "🐶", "letters"),
        MatchPair("m5", "Letter L", "🔤", "Lion", "🦁", "letters"),
        MatchPair("m6", "Number 3", "3️⃣", "3 Apples", "🍎🍎🍎", "numbers"),
        MatchPair("m7", "Number 2", "2️⃣", "2 Bananas", "🍌🍌", "numbers"),
        MatchPair("m8", "Number 1", "1️⃣", "1 Star", "⭐", "numbers"),
        MatchPair("m9", "Number 4", "4️⃣", "4 Cupcakes", "🧁🧁🧁🧁", "numbers"),
        MatchPair("m10", "Number 5", "5️⃣", "5 Balloons", "🎈🎈🎈🎈🎈", "numbers")
    )

    /**
     * Normalized stroke guide paths for letters in a standard 0..1 bounding box.
     * Each stroke is a list of sequential normalized (x,y) coordinates.
     */
    private fun getNormalizedLetterStroke(char: Char): List<List<Pair<Float, Float>>> {
        return when (char) {
            'A' -> listOf(
                generateLine(0.2f, 0.9f, 0.5f, 0.1f, 15), // Left diagonal going up
                generateLine(0.5f, 0.1f, 0.8f, 0.9f, 15), // Right diagonal going down
                generateLine(0.35f, 0.55f, 0.65f, 0.55f, 10) // Horizontal crossbar
            )
            'B' -> listOf(
                generateLine(0.25f, 0.1f, 0.25f, 0.9f, 15), // Vertical spine down
                generateArc(0.25f, 0.1f, 0.25f, 0.5f, 0.7f, 15), // Top loop
                generateArc(0.25f, 0.5f, 0.25f, 0.9f, 0.75f, 15) // Bottom loop
            )
            'C' -> listOf(
                generateArc(0.75f, 0.2f, 0.75f, 0.8f, 0.2f, 25) // Curved C
            )
            'D' -> listOf(
                generateLine(0.25f, 0.1f, 0.25f, 0.9f, 15), // Vertical spine
                generateArc(0.25f, 0.1f, 0.25f, 0.9f, 0.8f, 20) // Big curve
            )
            'E' -> listOf(
                generateLine(0.3f, 0.1f, 0.3f, 0.9f, 15),
                generateLine(0.3f, 0.1f, 0.8f, 0.1f, 10),
                generateLine(0.3f, 0.5f, 0.7f, 0.5f, 10),
                generateLine(0.3f, 0.9f, 0.8f, 0.9f, 10)
            )
            'F' -> listOf(
                generateLine(0.3f, 0.1f, 0.3f, 0.9f, 15),
                generateLine(0.3f, 0.1f, 0.8f, 0.1f, 10),
                generateLine(0.3f, 0.5f, 0.7f, 0.5f, 10)
            )
            'L' -> listOf(
                generateLine(0.3f, 0.1f, 0.3f, 0.9f, 15),
                generateLine(0.3f, 0.9f, 0.8f, 0.9f, 10)
            )
            'O' -> listOf(
                generateCircle(0.5f, 0.5f, 0.35f, 30)
            )
            'S' -> listOf(
                generateSPath(25)
            )
            'T' -> listOf(
                generateLine(0.2f, 0.1f, 0.8f, 0.1f, 12),
                generateLine(0.5f, 0.1f, 0.5f, 0.9f, 15)
            )
            else -> listOf(
                generateLine(0.2f, 0.1f, 0.5f, 0.9f, 15),
                generateLine(0.5f, 0.9f, 0.8f, 0.1f, 15)
            )
        }
    }

    private fun getNormalizedNumberStroke(number: Int): List<List<Pair<Float, Float>>> {
        return when (number) {
            1 -> listOf(
                generateLine(0.35f, 0.25f, 0.5f, 0.1f, 8),
                generateLine(0.5f, 0.1f, 0.5f, 0.9f, 15),
                generateLine(0.3f, 0.9f, 0.7f, 0.9f, 10)
            )
            2 -> listOf(
                generateArc(0.25f, 0.3f, 0.75f, 0.5f, 0.5f, 15),
                generateLine(0.75f, 0.5f, 0.25f, 0.9f, 12),
                generateLine(0.25f, 0.9f, 0.8f, 0.9f, 10)
            )
            3 -> listOf(
                generateArc(0.3f, 0.1f, 0.3f, 0.5f, 0.75f, 15),
                generateArc(0.3f, 0.5f, 0.3f, 0.9f, 0.8f, 15)
            )
            4 -> listOf(
                generateLine(0.7f, 0.1f, 0.2f, 0.6f, 15),
                generateLine(0.2f, 0.6f, 0.85f, 0.6f, 12),
                generateLine(0.7f, 0.3f, 0.7f, 0.9f, 12)
            )
            5 -> listOf(
                generateLine(0.75f, 0.1f, 0.35f, 0.1f, 10),
                generateLine(0.35f, 0.1f, 0.35f, 0.45f, 10),
                generateArc(0.35f, 0.45f, 0.3f, 0.9f, 0.75f, 15)
            )
            else -> listOf(
                generateLine(0.5f, 0.1f, 0.5f, 0.9f, 15)
            )
        }
    }

    private fun generateLine(x1: Float, y1: Float, x2: Float, y2: Float, steps: Int): List<Pair<Float, Float>> {
        val list = mutableListOf<Pair<Float, Float>>()
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            list.add(Pair(x1 + (x2 - x1) * t, y1 + (y2 - y1) * t))
        }
        return list
    }

    private fun generateArc(
        x1: Float, y1: Float, x2: Float, y2: Float, curveX: Float, steps: Int
    ): List<Pair<Float, Float>> {
        val list = mutableListOf<Pair<Float, Float>>()
        val midY = (y1 + y2) / 2f
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            // Quadratic Bezier curve
            val x = (1 - t) * (1 - t) * x1 + 2 * (1 - t) * t * curveX + t * t * x2
            val y = (1 - t) * (1 - t) * y1 + 2 * (1 - t) * t * midY + t * t * y2
            list.add(Pair(x, y))
        }
        return list
    }

    private fun generateCircle(centerX: Float, centerY: Float, radius: Float, steps: Int): List<Pair<Float, Float>> {
        val list = mutableListOf<Pair<Float, Float>>()
        for (i in 0..steps) {
            val angle = 2.0 * Math.PI * i / steps
            val x = centerX + radius * Math.cos(angle).toFloat()
            val y = centerY + radius * Math.sin(angle).toFloat()
            list.add(Pair(x, y))
        }
        return list
    }

    private fun generateSPath(steps: Int): List<Pair<Float, Float>> {
        val list = mutableListOf<Pair<Float, Float>>()
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            val y = 0.1f + t * 0.8f
            val x = (0.5f + 0.3f * Math.sin(t * Math.PI * 2.0)).toFloat()
            list.add(Pair(x, y))
        }
        return list
    }
}
