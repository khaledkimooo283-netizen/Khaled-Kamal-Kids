package com.example.data

data class MemoryPairItem(
    val pairId: String,
    val letter: Char,
    val word: String,
    val arabicWord: String,
    val emoji: String,
    val phonetic: String
)

enum class MemoryGameMode {
    PICTURE_PICTURE, // Picture ↔ Picture
    PICTURE_WORD     // Picture ↔ Word
}

enum class MemoryDifficulty(val cardCount: Int, val pairCount: Int, val columns: Int) {
    EASY(cardCount = 6, pairCount = 3, columns = 3),
    MEDIUM(cardCount = 12, pairCount = 6, columns = 3),
    HARD(cardCount = 20, pairCount = 10, columns = 4)
}

object MemoryCardsData {

    val allAlphabetPairs: List<MemoryPairItem> = listOf(
        // A
        MemoryPairItem("p_apple", 'A', "Apple", "تفاحة", "🍎", "A is for Apple"),
        MemoryPairItem("p_ant", 'A', "Ant", "نملة", "🐜", "A is for Ant"),
        MemoryPairItem("p_airplane", 'A', "Airplane", "طائرة", "✈️", "A is for Airplane"),
        MemoryPairItem("p_alligator", 'A', "Alligator", "تمساح", "🐊", "A is for Alligator"),
        MemoryPairItem("p_anchor", 'A', "Anchor", "مرساة", "⚓", "A is for Anchor"),

        // B
        MemoryPairItem("p_ball", 'B', "Ball", "كرة", "⚽", "B is for Ball"),
        MemoryPairItem("p_bee", 'B', "Bee", "نحلة", "🐝", "B is for Bee"),
        MemoryPairItem("p_banana", 'B', "Banana", "موزة", "🍌", "B is for Banana"),
        MemoryPairItem("p_boat", 'B', "Boat", "قارب", "⛵", "B is for Boat"),
        MemoryPairItem("p_bear", 'B', "Bear", "دب", "🐻", "B is for Bear"),

        // C
        MemoryPairItem("p_cat", 'C', "Cat", "قطة", "🐱", "C is for Cat"),
        MemoryPairItem("p_car", 'C', "Car", "سيارة", "🚗", "C is for Car"),
        MemoryPairItem("p_cake", 'C', "Cake", "كعكة", "🎂", "C is for Cake"),
        MemoryPairItem("p_cow", 'C', "Cow", "بقرة", "🐮", "C is for Cow"),
        MemoryPairItem("p_cookie", 'C', "Cookie", "بسكويت", "🍪", "C is for Cookie"),

        // D
        MemoryPairItem("p_dog", 'D', "Dog", "كلب", "🐶", "D is for Dog"),
        MemoryPairItem("p_duck", 'D', "Duck", "بطة", "🦆", "D is for Duck"),
        MemoryPairItem("p_dinosaur", 'D', "Dinosaur", "ديناصور", "🦕", "D is for Dinosaur"),
        MemoryPairItem("p_dolphin", 'D', "Dolphin", "دلفين", "🐬", "D is for Dolphin"),
        MemoryPairItem("p_drum", 'D', "Drum", "طبلة", "🥁", "D is for Drum"),

        // E
        MemoryPairItem("p_elephant", 'E', "Elephant", "فيل", "🐘", "E is for Elephant"),
        MemoryPairItem("p_egg", 'E', "Egg", "بيضة", "🥚", "E is for Egg"),
        MemoryPairItem("p_eagle", 'E', "Eagle", "نسر", "🦅", "E is for Eagle"),
        MemoryPairItem("p_engine", 'E', "Engine", "محرك", "🚂", "E is for Engine"),
        MemoryPairItem("p_earth", 'E', "Earth", "الأرض", "🌍", "E is for Earth"),

        // F
        MemoryPairItem("p_fish", 'F', "Fish", "سمكة", "🐟", "F is for Fish"),
        MemoryPairItem("p_frog", 'F', "Frog", "ضفدع", "🐸", "F is for Frog"),
        MemoryPairItem("p_flower", 'F', "Flower", "زهرة", "🌸", "F is for Flower"),
        MemoryPairItem("p_fox", 'F', "Fox", "ثعلب", "🦊", "F is for Fox"),
        MemoryPairItem("p_fire", 'F', "Fire", "نار", "🔥", "F is for Fire"),

        // G
        MemoryPairItem("p_giraffe", 'G', "Giraffe", "زرافة", "🦒", "G is for Giraffe"),
        MemoryPairItem("p_grape", 'G', "Grape", "عنب", "🍇", "G is for Grape"),
        MemoryPairItem("p_guitar", 'G', "Guitar", "قيثارة", "🎸", "G is for Guitar"),
        MemoryPairItem("p_gift", 'G', "Gift", "هدية", "🎁", "G is for Gift"),
        MemoryPairItem("p_gorilla", 'G', "Gorilla", "غوريلا", "🦍", "G is for Gorilla"),

        // H
        MemoryPairItem("p_house", 'H', "House", "منزل", "🏠", "H is for House"),
        MemoryPairItem("p_horse", 'H', "Horse", "حصان", "🐴", "H is for Horse"),
        MemoryPairItem("p_hat", 'H', "Hat", "قبعة", "🎩", "H is for Hat"),
        MemoryPairItem("p_heart", 'H', "Heart", "قلب", "💖", "H is for Heart"),
        MemoryPairItem("p_helicopter", 'H', "Helicopter", "مروحية", "🚁", "H is for Helicopter"),

        // I
        MemoryPairItem("p_icecream", 'I', "Ice Cream", "بوظة", "🍦", "I is for Ice Cream"),
        MemoryPairItem("p_igloo", 'I', "Igloo", "كوخ جليدي", "🧊", "I is for Igloo"),
        MemoryPairItem("p_island", 'I', "Island", "جزيرة", "🏝️", "I is for Island"),
        MemoryPairItem("p_insect", 'I', "Insect", "حشرة", "🐛", "I is for Insect"),
        MemoryPairItem("p_ink", 'I', "Ink", "حبر", "🖋️", "I is for Ink"),

        // J
        MemoryPairItem("p_juice", 'J', "Juice", "عصير", "🧃", "J is for Juice"),
        MemoryPairItem("p_jellyfish", 'J', "Jellyfish", "قنديل البحر", "🪼", "J is for Jellyfish"),
        MemoryPairItem("p_jet", 'J', "Jet", "طائرة نفاثة", "✈️", "J is for Jet"),
        MemoryPairItem("p_jam", 'J', "Jam", "مربى", "🏺", "J is for Jam"),

        // K
        MemoryPairItem("p_kangaroo", 'K', "Kangaroo", "كانغر", "🦘", "K is for Kangaroo"),
        MemoryPairItem("p_kite", 'K', "Kite", "طائرة ورقية", "🪁", "K is for Kite"),
        MemoryPairItem("p_key", 'K', "Key", "مفتاح", "🔑", "K is for Key"),
        MemoryPairItem("p_koala", 'K', "Koala", "كوالا", "🐨", "K is for Koala"),

        // L
        MemoryPairItem("p_lion", 'L', "Lion", "أسد", "🦁", "L is for Lion"),
        MemoryPairItem("p_lemon", 'L', "Lemon", "ليمون", "🍋", "L is for Lemon"),
        MemoryPairItem("p_leaf", 'L', "Leaf", "ورقة شجر", "🍃", "L is for Leaf"),
        MemoryPairItem("p_lock", 'L', "Lock", "قفل", "🔒", "L is for Lock"),

        // M
        MemoryPairItem("p_monkey", 'M', "Monkey", "قرد", "🐒", "M is for Monkey"),
        MemoryPairItem("p_moon", 'M', "Moon", "قمر", "🌙", "M is for Moon"),
        MemoryPairItem("p_mango", 'M', "Mango", "مانجو", "🥭", "M is for Mango"),
        MemoryPairItem("p_milk", 'M', "Milk", "حليب", "🥛", "M is for Milk"),

        // N
        MemoryPairItem("p_nest", 'N', "Nest", "عش", "🪹", "N is for Nest"),
        MemoryPairItem("p_nut", 'N', "Nut", "بندقة", "🥜", "N is for Nut"),
        MemoryPairItem("p_net", 'N', "Net", "شبكة", "🕸️", "N is for Net"),

        // O
        MemoryPairItem("p_owl", 'O', "Owl", "بومة", "🦉", "O is for Owl"),
        MemoryPairItem("p_orange", 'O', "Orange", "برتقالة", "🍊", "O is for Orange"),
        MemoryPairItem("p_octopus", 'O', "Octopus", "أخطبوط", "🐙", "O is for Octopus"),

        // P
        MemoryPairItem("p_penguin", 'P', "Penguin", "بطريق", "🐧", "P is for Penguin"),
        MemoryPairItem("p_pizza", 'P', "Pizza", "بيتزا", "🍕", "P is for Pizza"),
        MemoryPairItem("p_panda", 'P', "Panda", "باندا", "🐼", "P is for Panda"),
        MemoryPairItem("p_pear", 'P', "Pear", "إجاص", "🍐", "P is for Pear"),

        // Q
        MemoryPairItem("p_queen", 'Q', "Queen", "ملكة", "👸", "Q is for Queen"),
        MemoryPairItem("p_quail", 'Q', "Quail", "سمان", "🐦", "Q is for Quail"),
        MemoryPairItem("p_question", 'Q', "Question", "سؤال", "❓", "Q is for Question"),

        // R
        MemoryPairItem("p_rabbit", 'R', "Rabbit", "أرنب", "🐰", "R is for Rabbit"),
        MemoryPairItem("p_rocket", 'R', "Rocket", "صاروخ", "🚀", "R is for Rocket"),
        MemoryPairItem("p_rainbow", 'R', "Rainbow", "قوس قزح", "🌈", "R is for Rainbow"),
        MemoryPairItem("p_robot", 'R', "Robot", "روبوت", "🤖", "R is for Robot"),

        // S
        MemoryPairItem("p_sun", 'S', "Sun", "شمس", "☀️", "S is for Sun"),
        MemoryPairItem("p_star", 'S', "Star", "نجمة", "⭐️", "S is for Star"),
        MemoryPairItem("p_snake", 'S', "Snake", "ثعبان", "🐍", "S is for Snake"),
        MemoryPairItem("p_strawberry", 'S', "Strawberry", "فراولة", "🍓", "S is for Strawberry"),

        // T
        MemoryPairItem("p_tiger", 'T', "Tiger", "نمر", "🐯", "T is for Tiger"),
        MemoryPairItem("p_tree", 'T', "Tree", "شجرة", "🌳", "T is for Tree"),
        MemoryPairItem("p_tomato", 'T', "Tomato", "طماطم", "🍅", "T is for Tomato"),
        MemoryPairItem("p_train", 'T', "Train", "قطار", "🚂", "T is for Train"),

        // U
        MemoryPairItem("p_umbrella", 'U', "Umbrella", "مظلة", "☂️", "U is for Umbrella"),
        MemoryPairItem("p_unicorn", 'U', "Unicorn", "وحيد القرن", "🦄", "U is for Unicorn"),
        MemoryPairItem("p_ufo", 'U', "UFO", "صحن طائر", "🛸", "U is for UFO"),

        // V
        MemoryPairItem("p_violin", 'V', "Violin", "كمان", "🎻", "V is for Violin"),
        MemoryPairItem("p_volcano", 'V', "Volcano", "بركان", "🌋", "V is for Volcano"),
        MemoryPairItem("p_van", 'V', "Van", "شاحنة", "🚐", "V is for Van"),

        // W
        MemoryPairItem("p_watermelon", 'W', "Watermelon", "بطيخ", "🍉", "W is for Watermelon"),
        MemoryPairItem("p_whale", 'W', "Whale", "حوت", "🐋", "W is for Whale"),
        MemoryPairItem("p_watch", 'W', "Watch", "ساعة يد", "⌚", "W is for Watch"),

        // X
        MemoryPairItem("p_xylophone", 'X', "Xylophone", "كسيلوفون", "🎼", "X is for Xylophone"),
        MemoryPairItem("p_xray", 'X', "X-Ray", "أشعة", "🦴", "X is for X-Ray"),

        // Y
        MemoryPairItem("p_yoyo", 'Y', "Yo-Yo", "يويو", "🪀", "Y is for Yo-Yo"),
        MemoryPairItem("p_yacht", 'Y', "Yacht", "يخت", "🛥️", "Y is for Yacht"),
        MemoryPairItem("p_yak", 'Y', "Yak", "ثور التبت", "🐂", "Y is for Yak"),

        // Z
        MemoryPairItem("p_zebra", 'Z', "Zebra", "حمار وحشي", "🦓", "Z is for Zebra"),
        MemoryPairItem("p_zip", 'Z', "Zip", "سحاب", "🤐", "Z is for Zip"),
        MemoryPairItem("p_zoo", 'Z', "Zoo", "حديقة حيوان", "🦁", "Z is for Zoo")
    )
}
