package com.example.data

data class SongItem(
    val id: String,
    val title: String,
    val categoryEmoji: String,
    val themeColor: Long,
    val lyricsLines: List<SongLyricLine>,
    val actionChallenge: MovementAction
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

object SongDataRepository {
    val songsList: List<SongItem> = listOf(
        // 1. Alphabet Song (Complete A-Z, no letters skipped)
        SongItem(
            id = "s_abc",
            title = "Alphabet Song (A to Z)",
            categoryEmoji = "🔤",
            themeColor = 0xFFEC4899,
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
}
