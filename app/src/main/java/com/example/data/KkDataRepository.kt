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

    // --- Profile & Settings Data ---
    fun getChildName(): String = prefs.getString("child_name", "Ahmed") ?: "Ahmed"
    fun setChildName(name: String) = prefs.edit().putString("child_name", name).apply()

    fun getChildAge(): Int = prefs.getInt("child_age", 4)
    fun setChildAge(age: Int) = prefs.edit().putInt("child_age", age).apply()

    fun getAvatarEmoji(): String = prefs.getString("avatar_emoji", "🦁") ?: "🦁"
    fun setAvatarEmoji(emoji: String) = prefs.edit().putString("avatar_emoji", emoji).apply()

    fun getCoins(): Int = prefs.getInt("user_coins", 120)
    fun addCoins(count: Int) {
        val current = getCoins()
        prefs.edit().putInt("user_coins", current + count).apply()
    }

    fun getParentPin(): String = prefs.getString("parent_pin", "1234") ?: "1234"
    fun setParentPin(pin: String) = prefs.edit().putString("parent_pin", pin).apply()

    fun getVoiceVolume(): Float = prefs.getFloat("voice_volume", 1.0f)
    fun setVoiceVolume(vol: Float) = prefs.edit().putFloat("voice_volume", vol).apply()

    fun getMusicVolume(): Float = prefs.getFloat("music_volume", 0.8f)
    fun setMusicVolume(vol: Float) = prefs.edit().putFloat("music_volume", vol).apply()

    fun isSoundFxEnabled(): Boolean = prefs.getBoolean("sound_fx_enabled", true)
    fun setSoundFxEnabled(enabled: Boolean) = prefs.edit().putBoolean("sound_fx_enabled", enabled).apply()

    fun isMusicEnabled(): Boolean = prefs.getBoolean("bg_music_enabled", true)
    fun setMusicEnabled(enabled: Boolean) = prefs.edit().putBoolean("bg_music_enabled", enabled).apply()

    fun getVoiceType(): String = prefs.getString("voice_type", "Female Teacher") ?: "Female Teacher"
    fun setVoiceType(type: String) = prefs.edit().putString("voice_type", type).apply()

    fun getGameDifficulty(): String = prefs.getString("game_difficulty", "Easy") ?: "Easy"
    fun setGameDifficulty(diff: String) = prefs.edit().putString("game_difficulty", diff).apply()

    fun getTracingSensitivity(): String = prefs.getString("tracing_sensitivity", "Medium") ?: "Medium"
    fun setTracingSensitivity(sens: String) = prefs.edit().putString("tracing_sensitivity", sens).apply()

    fun isLargeTextMode(): Boolean = prefs.getBoolean("large_text_mode", false)
    fun setLargeTextMode(enabled: Boolean) = prefs.edit().putBoolean("large_text_mode", enabled).apply()

    fun getLanguage(): String = prefs.getString("app_language", "English") ?: "English"
    fun setLanguage(lang: String) = prefs.edit().putString("app_language", lang).apply()

    fun getReadingAccuracy(): Int = prefs.getInt("reading_accuracy", 88)
    fun getMatchingAccuracy(): Int = prefs.getInt("matching_accuracy", 95)

    fun resetAllProgress() {
        prefs.edit().clear().apply()
    }

    // --- Adventure Mode & Parent Progress ---
    fun getAdventureUnlockedWorld(): Int {
        return prefs.getInt("adv_unlocked_world", 0)
    }

    fun unlockNextWorld(currentWorld: Int) {
        val next = (currentWorld + 1).coerceAtMost(6)
        if (next > getAdventureUnlockedWorld()) {
            prefs.edit().putInt("adv_unlocked_world", next).apply()
        }
    }

    fun getAdventureWorldProgress(worldIdx: Int): Int {
        return prefs.getInt("adv_world_progress_$worldIdx", 0)
    }

    fun setAdventureWorldProgress(worldIdx: Int, progress: Int) {
        prefs.edit().putInt("adv_world_progress_$worldIdx", progress).apply()
    }

    fun getLearnedWords(): Set<String> {
        return prefs.getStringSet("learned_words_set", setOf("Apple", "Ant", "Animal", "Ball", "Banana", "Cat", "Dog", "Elephant")) ?: emptySet()
    }

    fun addLearnedWords(words: List<String>) {
        val current = getLearnedWords().toMutableSet()
        current.addAll(words)
        prefs.edit().putStringSet("learned_words_set", current).apply()
    }

    fun getTracingAccuracy(): Int = prefs.getInt("tracing_accuracy", 94)
    fun getListeningAccuracy(): Int = prefs.getInt("listening_accuracy", 96)
    fun getTypingAccuracy(): Int = prefs.getInt("typing_accuracy", 91)
    fun getLearningTimeMinutes(): Int = prefs.getInt("learning_time_mins", 65)

    fun addLearningTimeMinutes(mins: Int) {
        val current = getLearningTimeMinutes()
        prefs.edit().putInt("learning_time_mins", current + mins).apply()
    }

    // List of Alphabet items (Uppercase)
    val alphabetList = HandwritingData.uppercaseLetters.map { item ->
        LetterItem(
            letter = item.character[0],
            word = item.word,
            emoji = item.emoji,
            phonetic = "${item.character} for ${item.word}",
            colorHex = 0xFFFF5252,
            strokeGuidePoints = item.strokeGuidePoints
        )
    }

    // List of Lowercase items
    val lowercaseList = HandwritingData.lowercaseLetters.map { item ->
        LetterItem(
            letter = item.character[0],
            word = item.word,
            emoji = item.emoji,
            phonetic = "lowercase ${item.character}",
            colorHex = 0xFF42A5F5,
            strokeGuidePoints = item.strokeGuidePoints
        )
    }

    // List of Number items (0 to 20)
    val numberList = HandwritingData.numbers.mapIndexed { idx, item ->
        NumberItem(
            number = idx, // 0..20
            word = item.word,
            emoji = item.emoji,
            colorHex = 0xFFAB47BC,
            strokeGuidePoints = item.strokeGuidePoints
        )
    }

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

data class LetterVocab(
    val letter: String,
    val word: String,
    val emoji: String
)

data class MatchRound(
    val pairs: List<MatchPair>,
    val leftPairs: List<MatchPair>,
    val rightPairs: List<MatchPair>
)

    val letterVocabMap: Map<String, List<LetterVocab>> = mapOf(
        "A" to listOf(LetterVocab("A", "Apple", "🍎"), LetterVocab("A", "Ant", "🐜"), LetterVocab("A", "Animal", "🦁")),
        "B" to listOf(LetterVocab("B", "Ball", "⚽"), LetterVocab("B", "Banana", "🍌"), LetterVocab("B", "Bear", "🐻")),
        "C" to listOf(LetterVocab("C", "Cat", "🐱"), LetterVocab("C", "Car", "🚗"), LetterVocab("C", "Cake", "🎂")),
        "D" to listOf(LetterVocab("D", "Dog", "🐶"), LetterVocab("D", "Duck", "🦆"), LetterVocab("D", "Dolphin", "🐬")),
        "E" to listOf(LetterVocab("E", "Elephant", "🐘"), LetterVocab("E", "Egg", "🥚"), LetterVocab("E", "Eagle", "🦅")),
        "F" to listOf(LetterVocab("F", "Fish", "🐟"), LetterVocab("F", "Frog", "🐸"), LetterVocab("F", "Flower", "🌸")),
        "G" to listOf(LetterVocab("G", "Giraffe", "🦒"), LetterVocab("G", "Grapes", "🍇"), LetterVocab("G", "Guitar", "🎸")),
        "H" to listOf(LetterVocab("H", "House", "🏠"), LetterVocab("H", "Horse", "🐴"), LetterVocab("H", "Hat", "🎩")),
        "I" to listOf(LetterVocab("I", "Ice Cream", "🍦"), LetterVocab("I", "Iguana", "🦎"), LetterVocab("I", "Island", "🏝️")),
        "J" to listOf(LetterVocab("J", "Juice", "🧃"), LetterVocab("J", "Jellyfish", "🪼"), LetterVocab("J", "Jet", "✈️")),
        "K" to listOf(LetterVocab("K", "Kite", "🪁"), LetterVocab("K", "Key", "🔑"), LetterVocab("K", "Kangaroo", "🦘")),
        "L" to listOf(LetterVocab("L", "Lion", "🦁"), LetterVocab("L", "Lemon", "🍋"), LetterVocab("L", "Leaf", "🍃")),
        "M" to listOf(LetterVocab("M", "Monkey", "🐒"), LetterVocab("M", "Moon", "🌙"), LetterVocab("M", "Milk", "🥛")),
        "N" to listOf(LetterVocab("N", "Nest", "🪹"), LetterVocab("N", "Nut", "🥜"), LetterVocab("N", "Net", "🥅")),
        "O" to listOf(LetterVocab("O", "Owl", "🦉"), LetterVocab("O", "Orange", "🍊"), LetterVocab("O", "Octopus", "🐙")),
        "P" to listOf(LetterVocab("P", "Penguin", "🐧"), LetterVocab("P", "Pizza", "🍕"), LetterVocab("P", "Pencil", "✏️")),
        "Q" to listOf(LetterVocab("Q", "Queen", "👸"), LetterVocab("Q", "Quail", "🐦"), LetterVocab("Q", "Question", "❓")),
        "R" to listOf(LetterVocab("R", "Rabbit", "🐇"), LetterVocab("R", "Ring", "💍"), LetterVocab("R", "Robot", "🤖")),
        "S" to listOf(LetterVocab("S", "Sun", "☀️"), LetterVocab("S", "Star", "⭐"), LetterVocab("S", "Strawberry", "🍓")),
        "T" to listOf(LetterVocab("T", "Tiger", "🐯"), LetterVocab("T", "Tree", "🌳"), LetterVocab("T", "Turtle", "🐢")),
        "U" to listOf(LetterVocab("U", "Umbrella", "☂️"), LetterVocab("U", "Unicorn", "🦄"), LetterVocab("U", "UFO", "🛸")),
        "V" to listOf(LetterVocab("V", "Violin", "🎻"), LetterVocab("V", "Van", "🚐"), LetterVocab("V", "Volcano", "🌋")),
        "W" to listOf(LetterVocab("W", "Watermelon", "🍉"), LetterVocab("W", "Watch", "⌚"), LetterVocab("W", "Whale", "🐋")),
        "X" to listOf(LetterVocab("X", "Xylophone", "🎼"), LetterVocab("X", "X-ray", "🩻"), LetterVocab("X", "Fox", "🦊")),
        "Y" to listOf(LetterVocab("Y", "Yo-Yo", "🪀"), LetterVocab("Y", "Yak", "🐂"), LetterVocab("Y", "Yacht", "🛥️")),
        "Z" to listOf(LetterVocab("Z", "Zebra", "🦓"), LetterVocab("Z", "Zipper", "🤐"), LetterVocab("Z", "Zoo", "🦁"))
    )

    fun generateMatchRound(category: String, count: Int = 4): MatchRound {
        val roundPairs = when (category) {
            "letters" -> {
                val selectedLetters = letterVocabMap.keys.shuffled().take(count)
                selectedLetters.map { letter ->
                    val vocabs = letterVocabMap[letter] ?: error("No vocab for $letter")
                    val chosenVocab = vocabs.random()
                    MatchPair(
                        id = "m_letter_$letter",
                        promptText = "Letter $letter",
                        promptEmoji = letter,
                        matchText = chosenVocab.word,
                        matchEmoji = chosenVocab.emoji,
                        category = "letters"
                    )
                }
            }
            "case" -> {
                val selectedLetters = letterVocabMap.keys.shuffled().take(count)
                selectedLetters.map { letter ->
                    val lowerChar = letter.lowercase()
                    MatchPair(
                        id = "m_case_$letter",
                        promptText = "Big $letter",
                        promptEmoji = letter,
                        matchText = "Small $lowerChar",
                        matchEmoji = lowerChar,
                        category = "case"
                    )
                }
            }
            "numbers" -> {
                val selectedNumbers = HandwritingData.numbers.shuffled().take(count)
                selectedNumbers.map { numItem ->
                    MatchPair(
                        id = "m_number_${numItem.character}",
                        promptText = "Number ${numItem.character}",
                        promptEmoji = numItem.character,
                        matchText = numItem.word,
                        matchEmoji = numItem.emoji,
                        category = "numbers"
                    )
                }
            }
            else -> emptyList()
        }

        require(validateGeneratedRound(roundPairs, category)) {
            "Generated invalid match round for category $category"
        }

        return MatchRound(
            pairs = roundPairs,
            leftPairs = roundPairs.shuffled(),
            rightPairs = roundPairs.shuffled()
        )
    }

    fun validateGeneratedRound(pairs: List<MatchPair>, category: String): Boolean {
        if (pairs.isEmpty()) return false

        // 1. Check unique IDs
        val ids = pairs.map { it.id }
        if (ids.distinct().size != pairs.size) return false

        // 2. Check no duplicate prompt items
        if (pairs.map { it.promptEmoji }.distinct().size != pairs.size) return false
        if (pairs.map { it.promptText }.distinct().size != pairs.size) return false

        // 3. Check no duplicate match items
        if (pairs.map { it.matchText }.distinct().size != pairs.size) return false

        // 4. Category specific rules
        if (category == "letters") {
            val displayedLetters = pairs.map { it.promptEmoji }.toSet()
            pairs.forEach { pair ->
                val letter = pair.promptEmoji
                val word = pair.matchText
                // Word MUST correspond to a letter displayed on the left
                if (!displayedLetters.contains(letter)) return false
                val expectedWords = letterVocabMap[letter]?.map { it.word } ?: emptyList()
                if (!expectedWords.contains(word)) return false
            }
        } else if (category == "case") {
            pairs.forEach { pair ->
                val letter = pair.promptEmoji
                val lowerChar = letter.lowercase()
                if (pair.matchEmoji != lowerChar) return false
            }
        } else if (category == "numbers") {
            pairs.forEach { pair ->
                val numChar = pair.promptEmoji
                val numItem = HandwritingData.numbers.find { it.character == numChar }
                if (numItem == null || pair.matchText != numItem.word) return false
            }
        }

        return true
    }

    // Logical Matching Pairs for Drag & Match Games (Full A-Z & 0-20)
    val matchPairsList: List<MatchPair> by lazy {
        val letterPairs = HandwritingData.uppercaseLetters.map { item ->
            MatchPair(
                id = "m_letter_${item.character}",
                promptText = "Letter ${item.character}",
                promptEmoji = item.character,
                matchText = item.word,
                matchEmoji = item.emoji,
                category = "letters"
            )
        }

        val numberPairs = HandwritingData.numbers.map { numItem ->
            MatchPair(
                id = "m_number_${numItem.character}",
                promptText = "Number ${numItem.character}",
                promptEmoji = numItem.character,
                matchText = numItem.word,
                matchEmoji = numItem.emoji,
                category = "numbers"
            )
        }

        val casePairs = HandwritingData.uppercaseLetters.map { upperItem ->
            val lowerChar = upperItem.character.lowercase()
            MatchPair(
                id = "m_case_${upperItem.character}",
                promptText = "Big ${upperItem.character}",
                promptEmoji = upperItem.character,
                matchText = "Small $lowerChar",
                matchEmoji = lowerChar,
                category = "case"
            )
        }

        letterPairs + numberPairs + casePairs
    }

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
