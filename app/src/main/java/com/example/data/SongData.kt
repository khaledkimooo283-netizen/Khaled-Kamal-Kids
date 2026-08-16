package com.example.data

import android.util.Log

data class SongLearningItem(
    val id: Int,
    val displayText: String,
    val spokenText: String,
    val visualEmoji: String,
    val subtitle: String = "",
    val chipLabel: String = ""
)

data class SongItem(
    val id: String,
    val title: String,
    val categoryEmoji: String,
    val themeColor: Long,
    val items: List<SongLearningItem>,
    val actionChallenge: MovementAction,
    val lyricsLines: List<SongLyricLine> = emptyList()
)

data class SongLyricLine(
    val lineText: String,
    val spokenText: String,
    val highlightEmoji: String,
    val tokens: List<String> = emptyList()
)

data class MovementAction(
    val promptText: String,
    val actionEmoji: String,
    val targetType: String // "CLAP", "JUMP", "RAISE_HANDS", "WAVE", "SPIN"
)

object SongDataValidator {
    fun validateSong(song: SongItem): List<String> {
        val errors = mutableListOf<String>()
        if (song.items.isEmpty()) {
            errors.add("Song '${song.title}' (${song.id}) has an empty learning items list!")
        }

        // Validate uniqueness of IDs
        val ids = song.items.map { it.id }
        if (ids.distinct().size != ids.size) {
            errors.add("Song '${song.title}' has duplicate item IDs: $ids")
        }

        // Validate non-blank text
        song.items.forEachIndexed { index, item ->
            if (item.displayText.isBlank()) {
                errors.add("Song '${song.title}' item at index $index has blank displayText!")
            }
            if (item.spokenText.isBlank()) {
                errors.add("Song '${song.title}' item at index $index has blank spokenText!")
            }
        }

        // Specific category requirements
        when (song.id) {
            "s_num" -> {
                if (song.items.size != 21) {
                    errors.add("Numbers song 's_num' MUST contain exactly 21 items (0 to 20), but found ${song.items.size}")
                }
                val expectedNumbers = listOf(
                    "Zero", "One", "Two", "Three", "Four", "Five",
                    "Six", "Seven", "Eight", "Nine", "Ten",
                    "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
                    "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty"
                )
                expectedNumbers.forEachIndexed { idx, expected ->
                    if (idx < song.items.size) {
                        val actual = song.items[idx].spokenText
                        if (!actual.equals(expected, ignoreCase = true)) {
                            errors.add("Numbers song index $idx expected '$expected' but found '$actual'")
                        }
                    }
                }
            }
            "s_days" -> {
                if (song.items.size != 7) {
                    errors.add("Days song 's_days' MUST contain exactly 7 items, but found ${song.items.size}")
                }
                val expectedDays = listOf("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
                expectedDays.forEachIndexed { idx, expected ->
                    if (idx < song.items.size) {
                        val actual = song.items[idx].spokenText
                        if (!actual.equals(expected, ignoreCase = true)) {
                            errors.add("Days song index $idx expected '$expected' but found '$actual'")
                        }
                    }
                }
            }
            "s_months" -> {
                if (song.items.size != 12) {
                    errors.add("Months song 's_months' MUST contain exactly 12 items, but found ${song.items.size}")
                }
                val expectedMonths = listOf(
                    "January", "February", "March", "April",
                    "May", "June", "July", "August",
                    "September", "October", "November", "December"
                )
                expectedMonths.forEachIndexed { idx, expected ->
                    if (idx < song.items.size) {
                        val actual = song.items[idx].spokenText
                        if (!actual.equals(expected, ignoreCase = true)) {
                            errors.add("Months song index $idx expected '$expected' but found '$actual'")
                        }
                    }
                }
            }
            "s_abc" -> {
                if (song.items.size != 26) {
                    errors.add("Alphabet song 's_abc' MUST contain exactly 26 items (A to Z), but found ${song.items.size}")
                }
                val expectedLetters = ('A'..'Z').map { it.toString() }
                expectedLetters.forEachIndexed { idx, expected ->
                    if (idx < song.items.size) {
                        val actual = song.items[idx].spokenText
                        if (!actual.equals(expected, ignoreCase = true)) {
                            errors.add("Alphabet song index $idx expected '$expected' but found '$actual'")
                        }
                    }
                }
            }
            "s_vocab" -> {
                if (song.items.size != 26) {
                    errors.add("Phonics song 's_vocab' MUST contain exactly 26 items (A to Z), but found ${song.items.size}")
                }
            }
        }

        return errors
    }

    fun validateAll(): Boolean {
        var allValid = true
        SongDataRepository.songsList.forEach { song ->
            val errors = validateSong(song)
            if (errors.isNotEmpty()) {
                allValid = false
                errors.forEach { err ->
                    Log.e("SongDataValidator", "VALIDATION ERROR: $err")
                }
            }
        }
        return allValid
    }
}

object SongDataRepository {

    // Helper to generate backwards-compatible lyricsLines from items
    private fun createLegacyLinesFromItems(items: List<SongLearningItem>, chunkSize: Int = 5): List<SongLyricLine> {
        return items.chunked(chunkSize).map { chunk ->
            val lineText = chunk.joinToString(", ") { it.displayText }
            val spokenText = chunk.joinToString(", ") { it.spokenText }
            val highlightEmoji = chunk.firstOrNull()?.visualEmoji ?: "🎵"
            val tokens = chunk.map { it.spokenText }
            SongLyricLine(
                lineText = lineText,
                spokenText = spokenText,
                highlightEmoji = highlightEmoji,
                tokens = tokens
            )
        }
    }

    // 1. Alphabet Song (Complete A-Z, exactly 26 learning items)
    private val abcItems = listOf(
        SongLearningItem(0, "A", "A", "🅰️", "Letter A", "A"),
        SongLearningItem(1, "B", "B", "🅱️", "Letter B", "B"),
        SongLearningItem(2, "C", "C", "🔤", "Letter C", "C"),
        SongLearningItem(3, "D", "D", "🔤", "Letter D", "D"),
        SongLearningItem(4, "E", "E", "🔤", "Letter E", "E"),
        SongLearningItem(5, "F", "F", "🔤", "Letter F", "F"),
        SongLearningItem(6, "G", "G", "🔤", "Letter G", "G"),
        SongLearningItem(7, "H", "H", "🔤", "Letter H", "H"),
        SongLearningItem(8, "I", "I", "🔤", "Letter I", "I"),
        SongLearningItem(9, "J", "J", "🔤", "Letter J", "J"),
        SongLearningItem(10, "K", "K", "🔤", "Letter K", "K"),
        SongLearningItem(11, "L", "L", "🔤", "Letter L", "L"),
        SongLearningItem(12, "M", "M", "🔤", "Letter M", "M"),
        SongLearningItem(13, "N", "N", "🔤", "Letter N", "N"),
        SongLearningItem(14, "O", "O", "🍎", "Letter O", "O"),
        SongLearningItem(15, "P", "P", "🔤", "Letter P", "P"),
        SongLearningItem(16, "Q", "Q", "🔤", "Letter Q", "Q"),
        SongLearningItem(17, "R", "R", "🔤", "Letter R", "R"),
        SongLearningItem(18, "S", "S", "🔤", "Letter S", "S"),
        SongLearningItem(19, "T", "T", "🔤", "Letter T", "T"),
        SongLearningItem(20, "U", "U", "🔤", "Letter U", "U"),
        SongLearningItem(21, "V", "V", "🔤", "Letter V", "V"),
        SongLearningItem(22, "W", "W", "🔤", "Letter W", "W"),
        SongLearningItem(23, "X", "X", "🔤", "Letter X", "X"),
        SongLearningItem(24, "Y", "Y", "🔤", "Letter Y", "Y"),
        SongLearningItem(25, "Z", "Z", "🌟", "Letter Z", "Z")
    )

    // 2. Numbers 0 to 20 Song (Complete 0-20, exactly 21 learning items)
    private val numberItems = listOf(
        SongLearningItem(0, "0  Zero", "Zero", "0️⃣", "Number Zero", "0"),
        SongLearningItem(1, "1  One", "One", "1️⃣", "Number One", "1"),
        SongLearningItem(2, "2  Two", "Two", "2️⃣", "Number Two", "2"),
        SongLearningItem(3, "3  Three", "Three", "3️⃣", "Number Three", "3"),
        SongLearningItem(4, "4  Four", "Four", "4️⃣", "Number Four", "4"),
        SongLearningItem(5, "5  Five", "Five", "5️⃣", "Number Five", "5"),
        SongLearningItem(6, "6  Six", "Six", "6️⃣", "Number Six", "6"),
        SongLearningItem(7, "7  Seven", "Seven", "7️⃣", "Number Seven", "7"),
        SongLearningItem(8, "8  Eight", "Eight", "8️⃣", "Number Eight", "8"),
        SongLearningItem(9, "9  Nine", "Nine", "9️⃣", "Number Nine", "9"),
        SongLearningItem(10, "10  Ten", "Ten", "🔟", "Number Ten", "10"),
        SongLearningItem(11, "11  Eleven", "Eleven", "🔢", "Number Eleven", "11"),
        SongLearningItem(12, "12  Twelve", "Twelve", "🔢", "Number Twelve", "12"),
        SongLearningItem(13, "13  Thirteen", "Thirteen", "🔢", "Number Thirteen", "13"),
        SongLearningItem(14, "14  Fourteen", "Fourteen", "🔢", "Number Fourteen", "14"),
        SongLearningItem(15, "15  Fifteen", "Fifteen", "🔢", "Number Fifteen", "15"),
        SongLearningItem(16, "16  Sixteen", "Sixteen", "🔢", "Number Sixteen", "16"),
        SongLearningItem(17, "17  Seventeen", "Seventeen", "🔢", "Number Seventeen", "17"),
        SongLearningItem(18, "18  Eighteen", "Eighteen", "🔢", "Number Eighteen", "18"),
        SongLearningItem(19, "19  Nineteen", "Nineteen", "🔢", "Number Nineteen", "19"),
        SongLearningItem(20, "20  Twenty", "Twenty", "2️⃣0️⃣", "Number Twenty", "20")
    )

    // 3. Rainbow Colors Song (7 learning items)
    private val colorItems = listOf(
        SongLearningItem(0, "Red  Apple", "Red", "🍎", "Sweet and round apple", "Red"),
        SongLearningItem(1, "Orange  Pumpkin", "Orange", "🎃", "Pumpkin on the ground", "Orange"),
        SongLearningItem(2, "Yellow  Sun", "Yellow", "☀️", "Bright and warm sun", "Yellow"),
        SongLearningItem(3, "Green  Leaf", "Green", "🍃", "Leaf growing in the farm", "Green"),
        SongLearningItem(4, "Blue  Sky", "Blue", "☁️", "Sky flying up high", "Blue"),
        SongLearningItem(5, "Purple  Grape", "Purple", "🍇", "Sweet purple grapes", "Purple"),
        SongLearningItem(6, "Pink  Flower", "Pink", "🌸", "Flower blooming everywhere", "Pink")
    )

    // 4. Animal Sounds Song (6 learning items)
    private val animalItems = listOf(
        SongLearningItem(0, "Lion  Roar!", "The Lion roars! Roar, Roar!", "🦁", "King of the jungle", "Lion"),
        SongLearningItem(1, "Elephant  Toot!", "The Elephant trumpets! Toot, Toot!", "🐘", "Big elephant with a trunk", "Elephant"),
        SongLearningItem(2, "Dog  Woof!", "The Dog barks! Woof, Woof!", "🐶", "Friendly puppy", "Dog"),
        SongLearningItem(3, "Cat  Meow!", "The Cat meows! Meow, Meow!", "🐱", "Playful kitten", "Cat"),
        SongLearningItem(4, "Duck  Quack!", "The Duck quacks! Quack, Quack!", "🦆", "Swimming duck", "Duck"),
        SongLearningItem(5, "Monkey  Ooh Aah!", "The Monkey chitters! Ooh ooh, Aah aah!", "🐒", "Climbing monkey", "Monkey")
    )

    // 5. Days of the Week Song (Starting strictly on Saturday, through to Friday, exactly 7 items)
    private val daysItems = listOf(
        SongLearningItem(0, "Saturday", "Saturday", "🌅", "Day 1 of the week", "Sat"),
        SongLearningItem(1, "Sunday", "Sunday", "☀️", "Day 2 of the week", "Sun"),
        SongLearningItem(2, "Monday", "Monday", "📚", "Day 3 of the week", "Mon"),
        SongLearningItem(3, "Tuesday", "Tuesday", "🎨", "Day 4 of the week", "Tue"),
        SongLearningItem(4, "Wednesday", "Wednesday", "⚽", "Day 5 of the week", "Wed"),
        SongLearningItem(5, "Thursday", "Thursday", "🎵", "Day 6 of the week", "Thu"),
        SongLearningItem(6, "Friday", "Friday", "🎉", "Day 7 of the week", "Fri")
    )

    // 6. Months of the Year Song (All 12 months in sequence through December)
    private val monthsItems = listOf(
        SongLearningItem(0, "January", "January", "❄️", "Month 1 of 12", "Jan"),
        SongLearningItem(1, "February", "February", "❤️", "Month 2 of 12", "Feb"),
        SongLearningItem(2, "March", "March", "🌱", "Month 3 of 12", "Mar"),
        SongLearningItem(3, "April", "April", "🌧️", "Month 4 of 12", "Apr"),
        SongLearningItem(4, "May", "May", "🌸", "Month 5 of 12", "May"),
        SongLearningItem(5, "June", "June", "☀️", "Month 6 of 12", "Jun"),
        SongLearningItem(6, "July", "July", "🏖️", "Month 7 of 12", "Jul"),
        SongLearningItem(7, "August", "August", "🌻", "Month 8 of 12", "Aug"),
        SongLearningItem(8, "September", "September", "🎒", "Month 9 of 12", "Sep"),
        SongLearningItem(9, "October", "October", "🎃", "Month 10 of 12", "Oct"),
        SongLearningItem(10, "November", "November", "🍂", "Month 11 of 12", "Nov"),
        SongLearningItem(11, "December", "December", "🎄", "Month 12 of 12", "Dec")
    )

    // 7. Greetings & Magic Words Song (5 learning items)
    private val greetItems = listOf(
        SongLearningItem(0, "Good Morning", "Good Morning", "🌅", "To the sunny sky", "Morning"),
        SongLearningItem(1, "Good Afternoon", "Good Afternoon", "☀️", "As birds fly by", "Afternoon"),
        SongLearningItem(2, "Good Night", "Good Night", "🌙", "To stars so bright", "Night"),
        SongLearningItem(3, "Please & Thank You", "Say Please and Thank You, always polite!", "💖", "Magic polite words", "Polite"),
        SongLearningItem(4, "You're Welcome", "You're Welcome with a big bright smile!", "😊", "With a big bright smile", "Welcome")
    )

    // 8. Phonics & Vocabulary Song (A-Z complete, exactly 26 items)
    private val vocabItems = listOf(
        SongLearningItem(0, "A  Apple", "A is for Apple", "🍎", "A is for Apple", "A"),
        SongLearningItem(1, "B  Ball", "B is for Ball", "⚽", "B is for Ball", "B"),
        SongLearningItem(2, "C  Cat", "C is for Cat", "🐱", "C is for Cat", "C"),
        SongLearningItem(3, "D  Dog", "D is for Dog", "🐶", "D is for Dog", "D"),
        SongLearningItem(4, "E  Elephant", "E is for Elephant", "🐘", "E is for Elephant", "E"),
        SongLearningItem(5, "F  Fish", "F is for Fish", "🐟", "F is for Fish", "F"),
        SongLearningItem(6, "G  Giraffe", "G is for Giraffe", "🦒", "G is for Giraffe", "G"),
        SongLearningItem(7, "H  Hat", "H is for Hat", "🎩", "H is for Hat", "H"),
        SongLearningItem(8, "I  Ice Cream", "I is for Ice Cream", "🍦", "I is for Ice Cream", "I"),
        SongLearningItem(9, "J  Juice", "J is for Juice", "🧃", "J is for Juice", "J"),
        SongLearningItem(10, "K  Kite", "K is for Kite", "🪁", "K is for Kite", "K"),
        SongLearningItem(11, "L  Lion", "L is for Lion", "🦁", "L is for Lion", "L"),
        SongLearningItem(12, "M  Monkey", "M is for Monkey", "🐒", "M is for Monkey", "M"),
        SongLearningItem(13, "N  Nose", "N is for Nose", "👃", "N is for Nose", "N"),
        SongLearningItem(14, "O  Orange", "O is for Orange", "🍊", "O is for Orange", "O"),
        SongLearningItem(15, "P  Pizza", "P is for Pizza", "🍕", "P is for Pizza", "P"),
        SongLearningItem(16, "Q  Queen", "Q is for Queen", "👑", "Q is for Queen", "Q"),
        SongLearningItem(17, "R  Rabbit", "R is for Rabbit", "🐰", "R is for Rabbit", "R"),
        SongLearningItem(18, "S  Sun", "S is for Sun", "☀️", "S is for Sun", "S"),
        SongLearningItem(19, "T  Tiger", "T is for Tiger", "🐯", "T is for Tiger", "T"),
        SongLearningItem(20, "U  Umbrella", "U is for Umbrella", "☂️", "U is for Umbrella", "U"),
        SongLearningItem(21, "V  Van", "V is for Van", "🚐", "V is for Van", "V"),
        SongLearningItem(22, "W  Watermelon", "W is for Watermelon", "🍉", "W is for Watermelon", "W"),
        SongLearningItem(23, "X  Xylophone", "X is for Xylophone", "🎹", "X is for Xylophone", "X"),
        SongLearningItem(24, "Y  Yo-yo", "Y is for Yo-yo", "🪀", "Y is for Yo-yo", "Y"),
        SongLearningItem(25, "Z  Zebra", "Z is for Zebra", "🦓", "Z is for Zebra", "Z")
    )

    val songsList: List<SongItem> = listOf(
        // 1. Alphabet Song (Complete A-Z, no letters skipped)
        SongItem(
            id = "s_abc",
            title = "Alphabet Song (A to Z)",
            categoryEmoji = "🔤",
            themeColor = 0xFFEC4899,
            items = abcItems,
            lyricsLines = listOf(
                SongLyricLine("A - B - C - D - E - F - G", "A, B, C, D, E, F, G", "🅰️", listOf("A", "B", "C", "D", "E", "F", "G")),
                SongLyricLine("H - I - J - K - L - M - N", "H, I, J, K, L, M, N", "🔤", listOf("H", "I", "J", "K", "L", "M", "N")),
                SongLyricLine("O - P - Q - R - S - T - U", "O, P, Q, R, S, T, U", "🍎", listOf("O", "P", "Q", "R", "S", "T", "U")),
                SongLyricLine("V - W - X - Y and Z!", "V, W, X, Y, and Z", "🌟", listOf("V", "W", "X", "Y", "Z")),
                SongLyricLine("Now I know my ABCs!", "Now I know my A B C s", "🎉", listOf("Now I know my ABCs")),
                SongLyricLine("Next time won't you sing with me!", "Next time won't you sing with me", "🎵", listOf("Sing with me"))
            ),
            actionChallenge = MovementAction("👏 Clap your hands 3 times!", "👏", "CLAP")
        ),

        // 2. Numbers 0 to 20 Song (Complete 0-20, no numbers skipped, exact sequence Zero to Twenty)
        SongItem(
            id = "s_num",
            title = "Numbers 0 to 20 Song",
            categoryEmoji = "🔢",
            themeColor = 0xFF3B82F6,
            items = numberItems,
            lyricsLines = listOf(
                SongLyricLine("Zero, One, Two, Three, Four, Five", "Zero, One, Two, Three, Four, Five", "0️⃣", listOf("Zero", "One", "Two", "Three", "Four", "Five")),
                SongLyricLine("Six, Seven, Eight, Nine, Ten", "Six, Seven, Eight, Nine, Ten", "5️⃣", listOf("Six", "Seven", "Eight", "Nine", "Ten")),
                SongLyricLine("Eleven, Twelve, Thirteen, Fourteen, Fifteen", "Eleven, Twelve, Thirteen, Fourteen, Fifteen", "🔟", listOf("Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen")),
                SongLyricLine("Sixteen, Seventeen, Eighteen, Nineteen, Twenty", "Sixteen, Seventeen, Eighteen, Nineteen, Twenty", "2️⃣0️⃣", listOf("Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty")),
                SongLyricLine("We counted from Zero all the way to Twenty!", "We counted from Zero all the way to Twenty!", "🌟", listOf("Zero to Twenty"))
            ),
            actionChallenge = MovementAction("👣 Jump up and down 2 times!", "👣", "JUMP")
        ),

        // 3. Rainbow Colors Song
        SongItem(
            id = "s_col",
            title = "Rainbow Colors Song",
            categoryEmoji = "🎨",
            themeColor = 0xFF10B981,
            items = colorItems,
            lyricsLines = listOf(
                SongLyricLine("Red is Apple, sweet and round!", "Red is Apple, sweet and round!", "🍎", listOf("Red", "Apple")),
                SongLyricLine("Orange Pumpkin on the ground!", "Orange Pumpkin on the ground!", "🎃", listOf("Orange", "Pumpkin")),
                SongLyricLine("Yellow Sun so bright and warm!", "Yellow Sun so bright and warm!", "☀️", listOf("Yellow", "Sun")),
                SongLyricLine("Green Leaf growing in the farm!", "Green Leaf growing in the farm!", "🍃", listOf("Green", "Leaf")),
                SongLyricLine("Blue Sky flying up so high!", "Blue Sky flying up so high!", "☁️", listOf("Blue", "Sky")),
                SongLyricLine("Purple Grape up in the sky!", "Purple Grape up in the sky!", "🍇", listOf("Purple", "Grape")),
                SongLyricLine("Pink Flower blooming everywhere!", "Pink Flower blooming everywhere!", "🌸", listOf("Pink", "Flower"))
            ),
            actionChallenge = MovementAction("🙌 Raise your hands high in the air!", "🙌", "RAISE_HANDS")
        ),

        // 4. Animal Friends Song
        SongItem(
            id = "s_ani",
            title = "Animal Sounds Song",
            categoryEmoji = "🦁",
            themeColor = 0xFFF59E0B,
            items = animalItems,
            lyricsLines = listOf(
                SongLyricLine("The Lion roars... ROAR ROAR!", "The Lion roars! Roar, Roar!", "🦁", listOf("Lion", "Roar")),
                SongLyricLine("The Elephant trumpets... TOOT TOOT!", "The Elephant trumpets! Toot, Toot!", "🐘", listOf("Elephant", "Toot")),
                SongLyricLine("The Dog barks... WOOF WOOF!", "The Dog barks! Woof, Woof!", "🐶", listOf("Dog", "Woof")),
                SongLyricLine("The Cat meows... MEOW MEOW!", "The Cat meows! Meow, Meow!", "🐱", listOf("Cat", "Meow")),
                SongLyricLine("The Duck quacks... QUACK QUACK!", "The Duck quacks! Quack, Quack!", "🦆", listOf("Duck", "Quack")),
                SongLyricLine("The Monkey chitters... OOH OOH AAH AAH!", "The Monkey chitters! Ooh ooh, Aah aah!", "🐒", listOf("Monkey", "Ooh Aah"))
            ),
            actionChallenge = MovementAction("👋 Wave hello to your animal friends!", "👋", "WAVE")
        ),

        // 5. Days of the Week Song (Starting strictly on Saturday, through to Friday)
        SongItem(
            id = "s_days",
            title = "Days of the Week Song",
            categoryEmoji = "📅",
            themeColor = 0xFF8B5CF6,
            items = daysItems,
            lyricsLines = listOf(
                SongLyricLine("Saturday, Sunday, Monday!", "Saturday, Sunday, Monday!", "🌅", listOf("Saturday", "Sunday", "Monday")),
                SongLyricLine("Tuesday, Wednesday, Thursday!", "Tuesday, Wednesday, Thursday!", "☀️", listOf("Tuesday", "Wednesday", "Thursday")),
                SongLyricLine("And Friday makes seven days!", "And Friday makes seven days!", "🌈", listOf("Friday")),
                SongLyricLine("Saturday to Friday, 7 days in a week!", "Saturday to Friday, seven days in a week!", "🎉", listOf("Seven Days"))
            ),
            actionChallenge = MovementAction("🔄 Spin around in a circle!", "🔄", "SPIN")
        ),

        // 6. Months of the Year Song (All 12 months including December)
        SongItem(
            id = "s_months",
            title = "Months of the Year Song",
            categoryEmoji = "🗓️",
            themeColor = 0xFF06B6D4,
            items = monthsItems,
            lyricsLines = listOf(
                SongLyricLine("January, February, March, April", "January, February, March, April", "❄️", listOf("January", "February", "March", "April")),
                SongLyricLine("May, June, July, August", "May, June, July, August", "🌻", listOf("May", "June", "July", "August")),
                SongLyricLine("September, October, November, December", "September, October, November, December", "🍂", listOf("September", "October", "November", "December")),
                SongLyricLine("Twelve months from January to December!", "Twelve months from January to December!", "🎄", listOf("January to December"))
            ),
            actionChallenge = MovementAction("👏 Clap your hands for twelve months!", "👏", "CLAP")
        ),

        // 7. Greetings & Magic Words Song
        SongItem(
            id = "s_greet",
            title = "Greetings & Magic Words Song",
            categoryEmoji = "🤝",
            themeColor = 0xFFEC4899,
            items = greetItems,
            lyricsLines = listOf(
                SongLyricLine("Good Morning to the sunny sky!", "Good Morning to the sunny sky!", "🌅", listOf("Good Morning")),
                SongLyricLine("Good Afternoon as birds fly by!", "Good Afternoon as birds fly by!", "☀️", listOf("Good Afternoon")),
                SongLyricLine("Good Night to stars so clean and bright!", "Good Night to stars so clean and bright!", "🌙", listOf("Good Night")),
                SongLyricLine("Say Please and Thank You, always polite!", "Say Please and Thank You, always polite!", "💖", listOf("Please", "Thank You")),
                SongLyricLine("You're Welcome with a big bright smile!", "You're Welcome with a big bright smile!", "😊", listOf("You're Welcome"))
            ),
            actionChallenge = MovementAction("👋 Wave hello and smile!", "👋", "WAVE")
        ),

        // 8. Vocabulary & Phonics Song (Complete A to Z Phonics)
        SongItem(
            id = "s_vocab",
            title = "Phonics & Vocabulary Song (A-Z)",
            categoryEmoji = "🔤",
            themeColor = 0xFF14B8A6,
            items = vocabItems,
            lyricsLines = listOf(
                SongLyricLine("A is for Apple, B is for Ball", "A is for Apple! B is for Ball!", "🍎", listOf("A", "Apple", "B", "Ball")),
                SongLyricLine("C is for Cat, D is for Dog", "C is for Cat! D is for Dog!", "🐱", listOf("C", "Cat", "D", "Dog")),
                SongLyricLine("E is for Elephant, F is for Fish", "E is for Elephant! F is for Fish!", "🐘", listOf("E", "Elephant", "F", "Fish")),
                SongLyricLine("G is for Giraffe, H is for Hat", "G is for Giraffe! H is for Hat!", "🦒", listOf("G", "Giraffe", "H", "Hat")),
                SongLyricLine("I is for Ice cream, J is for Juice", "I is for Ice cream! J is for Juice!", "🍦", listOf("I", "Ice cream", "J", "Juice")),
                SongLyricLine("K is for Kite, L is for Lion", "K is for Kite! L is for Lion!", "🪁", listOf("K", "Kite", "L", "Lion")),
                SongLyricLine("M is for Monkey, N is for Nose", "M is for Monkey! N is for Nose!", "🐒", listOf("M", "Monkey", "N", "Nose")),
                SongLyricLine("O is for Orange, P is for Pizza", "O is for Orange! P is for Pizza!", "🍊", listOf("O", "Orange", "P", "Pizza")),
                SongLyricLine("Q is for Queen, R is for Rabbit", "Q is for Queen! R is for Rabbit!", "👑", listOf("Q", "Queen", "R", "Rabbit")),
                SongLyricLine("S is for Sun, T is for Tiger", "S is for Sun! T is for Tiger!", "☀️", listOf("S", "Sun", "T", "Tiger")),
                SongLyricLine("U is for Umbrella, V is for Van", "U is for Umbrella! V is for Van!", "☂️", listOf("U", "Umbrella", "V", "Van")),
                SongLyricLine("W is for Watermelon, X is for Xylophone", "W is for Watermelon! X is for Xylophone!", "🍉", listOf("W", "Watermelon", "X", "Xylophone")),
                SongLyricLine("Y is for Yo-yo, Z is for Zebra", "Y is for Yo-yo! Z is for Zebra!", "🦓", listOf("Y", "Yo-yo", "Z", "Zebra")),
                SongLyricLine("Phonics fun from A to Z!", "Phonics fun from A to Z!", "🌟", listOf("Phonics A to Z"))
            ),
            actionChallenge = MovementAction("👣 Jump like an elephant!", "🐘", "JUMP")
        )
    )

    init {
        SongDataValidator.validateAll()
    }
}

