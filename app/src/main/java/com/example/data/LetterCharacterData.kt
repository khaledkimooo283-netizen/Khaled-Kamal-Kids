package com.example.data

data class CharacterVocabItem(
    val word: String,
    val emoji: String,
    val sentence: String,
    val arabicWord: String = "",
    val difficulty: String = "easy",
    val phoneticSpelling: String = ""
)

enum class MissionGameplayType {
    ROPE_RESCUE,        // 1. Pick up rope -> Move to hazard -> Throw rope -> Pull friend to safety
    ROLLING_BASKET,     // 2. Chase/catch rolling item -> Carry item -> Deliver to Basket/Goal
    INTERACT_ACTIVATE,  // 3. Approach machine/object -> Activate/Launch/Help
    BRIDGE_ENCOUNTER    // 4. Navigate across terrain/bridge -> Greet/Share/Meet friend
}

data class CharacterMission(
    val id: String,
    val title: String,
    val description: String,
    val targetWord: String,
    val targetEmoji: String,
    val options: List<String> = emptyList(),
    val optionEmojis: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val gameplayType: MissionGameplayType = MissionGameplayType.ROLLING_BASKET,
    val step1Prompt: String = "",
    val step2Prompt: String = "",
    val step3Prompt: String = "",
    val storyPrompt: String = "",
    val rescueActionPrompt: String = "",
    val goalLocationName: String = "Picnic Basket",
    val goalEmoji: String = "🧺",
    val ropeItemEmoji: String = "🪢",
    val obstacleEmoji: String = "🪵",
    val rewardCoins: Int = 10,
    val rewardStars: Int = 1
)

enum class CharacterActionState {
    IDLE,
    WALK,
    RUN,
    JUMP,
    FALL,
    LAND,
    WAVE,
    CELEBRATE,
    REACT,
    SPEAK,
    PICK_UP,
    THROW,
    PULL,
    CARRY,
    RESCUE,
    PLACE
}

data class LetterCharacter(
    val letter: Char,
    val name: String,
    val characterEmoji: String,
    val themeColorHex: Long,
    val secondaryColorHex: Long,
    val phonicsSound: String,
    val phonicsExample: String,
    val personality: String,
    val greetingSpeech: String,
    val storyIntro: String,
    val vocabulary: List<CharacterVocabItem>,
    val missions: List<CharacterMission>,
    val unlockBadgeName: String
)

object LetterCharacterData {

    private fun makeMissionsForLetter(
        letter: Char,
        w1: Pair<String, String>, // Rescue word (ROPE_RESCUE)
        w2: Pair<String, String>, // Catch word (ROLLING_BASKET)
        w3: Pair<String, String>, // Activate word (INTERACT_ACTIVATE)
        w4: Pair<String, String>  // Bridge word (BRIDGE_ENCOUNTER)
    ): List<CharacterMission> {
        val letLower = letter.lowercaseChar()
        return listOf(
            CharacterMission(
                id = "m_${letLower}1",
                title = "Rescue the ${w1.first}!",
                description = "Rescue the ${w1.first} trapped by the water with the rescue rope!",
                targetWord = w1.first,
                targetEmoji = w1.second,
                gameplayType = MissionGameplayType.ROPE_RESCUE,
                storyPrompt = "Oh no! The little ${w1.first} is trapped near the water! Pick up the rope to rescue ${w1.first}!",
                step1Prompt = "Walk to the rescue rope and pick it up!",
                step2Prompt = "Go to the water edge and throw the rope to ${w1.first}!",
                step3Prompt = "Pull the rope to bring ${w1.first} safely onto the grass!",
                ropeItemEmoji = "🪢",
                goalLocationName = "Safe Meadow",
                goalEmoji = "🌱"
            ),
            CharacterMission(
                id = "m_${letLower}2",
                title = "Catch the ${w2.first}!",
                description = "The ${w2.first} is rolling away! Catch it and deliver it to the basket!",
                targetWord = w2.first,
                targetEmoji = w2.second,
                gameplayType = MissionGameplayType.ROLLING_BASKET,
                storyPrompt = "Look! The ${w2.first} is on the loose! Chase and catch the ${w2.first}, then place it safely in the basket!",
                step1Prompt = "Run after the ${w2.first} and catch it!",
                step2Prompt = "Carry the ${w2.first} and place it in the Picnic Basket!",
                goalLocationName = "Picnic Basket",
                goalEmoji = "🧺"
            ),
            CharacterMission(
                id = "m_${letLower}3",
                title = "Help the ${w3.first}!",
                description = "Navigate across the path and launch or help the ${w3.first}!",
                targetWord = w3.first,
                targetEmoji = w3.second,
                gameplayType = MissionGameplayType.INTERACT_ACTIVATE,
                storyPrompt = "The ${w3.first} is waiting on the adventure path! Walk over and activate or help ${w3.first}!",
                step1Prompt = "Walk toward the ${w3.first} on the runway path!",
                step2Prompt = "Press the action button to activate ${w3.first}!",
                goalLocationName = "Adventure Runway",
                goalEmoji = "🏁"
            ),
            CharacterMission(
                id = "m_${letLower}4",
                title = "Meet the ${w4.first}!",
                description = "Cross the wooden bridge and become best friends with the ${w4.first}!",
                targetWord = w4.first,
                targetEmoji = w4.second,
                gameplayType = MissionGameplayType.BRIDGE_ENCOUNTER,
                storyPrompt = "A friendly ${w4.first} is waiting across the bridge! Cross the river bridge and say hello!",
                step1Prompt = "Walk across the wooden cobblestone bridge!",
                step2Prompt = "Wave hello and share a high-five with ${w4.first}!",
                goalLocationName = "Friendship Hill",
                goalEmoji = "💚"
            )
        )
    }

    val characters: List<LetterCharacter> = listOf(
        // A -> Apple (Red glossy 3D apple character)
        LetterCharacter(
            letter = 'A',
            name = "Apple",
            characterEmoji = "🍎",
            themeColorHex = 0xFFDC2626,
            secondaryColorHex = 0xFFEF4444,
            phonicsSound = "/æ/",
            phonicsExample = "A says /æ/ as in Apple & Ant!",
            personality = "Happy, sweet, and full of energy!",
            greetingSpeech = "Hi! I'm Apple! 'A' is for Apple! /æ/ /æ/ Apple! Let's explore together!",
            storyIntro = "Apple is on a hero mission to rescue little Ant, collect tasty apples, fly airplanes, and meet Alligator!",
            vocabulary = listOf(
                CharacterVocabItem("Ant", "🐜", "The tiny Ant is strong and carries a leaf.", "نملة", phoneticSpelling = "ANT"),
                CharacterVocabItem("Apple", "🍎", "The sweet red Apple is juicy and delicious.", "تفاحة", phoneticSpelling = "AP-uhl"),
                CharacterVocabItem("Airplane", "✈️", "The fast Airplane flies high in the sky.", "طائرة", phoneticSpelling = "AIR-playn"),
                CharacterVocabItem("Alligator", "🐊", "The friendly green Alligator smiles by the river.", "تمساح", phoneticSpelling = "AL-uh-gay-ter")
            ),
            missions = makeMissionsForLetter('A', "Ant" to "🐜", "Apple" to "🍎", "Airplane" to "✈️", "Alligator" to "🐊"),
            unlockBadgeName = "Super Apple Hero"
        ),

        // B -> Bear (Fuzzy brown 3D bear)
        LetterCharacter(
            letter = 'B',
            name = "Bear",
            characterEmoji = "🐻",
            themeColorHex = 0xFF92400E,
            secondaryColorHex = 0xFFB45309,
            phonicsSound = "/b/",
            phonicsExample = "B says /b/ as in Bear & Ball!",
            personality = "Big, cuddly, and loves honey snacks!",
            greetingSpeech = "Hello! I'm Bear! 'B' is for Bear! /b/ /b/ Bear! Big and cuddly!",
            storyIntro = "Bear is exploring the forest to help friends find balls, delicious bananas, and ride the yellow bus!",
            vocabulary = listOf(
                CharacterVocabItem("Bear", "🐻", "The friendly brown Bear loves sweet honey.", "دب", phoneticSpelling = "BAIR"),
                CharacterVocabItem("Ball", "⚽", "Kick the round soccer Ball across the meadow.", "كرة", phoneticSpelling = "BAWL"),
                CharacterVocabItem("Banana", "🍌", "The sweet yellow Banana is a healthy snack.", "موزة", phoneticSpelling = "buh-NAN-uh"),
                CharacterVocabItem("Bus", "🚌", "The yellow school Bus takes children to school.", "حافلة", phoneticSpelling = "BUHS")
            ),
            missions = makeMissionsForLetter('B', "Bear" to "🐻", "Ball" to "⚽", "Banana" to "🍌", "Bus" to "🚌"),
            unlockBadgeName = "Brave Bear Explorer"
        ),

        // C -> Cat (Playful orange tabby cat)
        LetterCharacter(
            letter = 'C',
            name = "Cat",
            characterEmoji = "🐱",
            themeColorHex = 0xFFF97316,
            secondaryColorHex = 0xFFFB923C,
            phonicsSound = "/k/",
            phonicsExample = "C says /k/ as in Cat & Car!",
            personality = "Curious, agile, and loves sweet milk!",
            greetingSpeech = "Meow! I'm Cat! 'C' is for Cat! /k/ /k/ Cat! Let's play!",
            storyIntro = "Cat is prowling through the town to find speedy cars, warm milk cups, and birthday cakes!",
            vocabulary = listOf(
                CharacterVocabItem("Cat", "🐱", "The cute tabby Cat purrs softly when happy.", "قطة", phoneticSpelling = "KAT"),
                CharacterVocabItem("Car", "🚗", "The red toy Car zooms down the paved road.", "سيارة", phoneticSpelling = "KAHR"),
                CharacterVocabItem("Cup", "🥛", "Drink refreshing water from the clean Cup.", "كوب", phoneticSpelling = "KUHP"),
                CharacterVocabItem("Cake", "🎂", "The birthday Cake has sweet icing and candles.", "كعكة", phoneticSpelling = "KAYK")
            ),
            missions = makeMissionsForLetter('C', "Cat" to "🐱", "Car" to "🚗", "Cup" to "🥛", "Cake" to "🎂"),
            unlockBadgeName = "Clever Cat Champion"
        ),

        // D -> Duck (Bright yellow rubber duck)
        LetterCharacter(
            letter = 'D',
            name = "Duck",
            characterEmoji = "🦆",
            themeColorHex = 0xFFEAB308,
            secondaryColorHex = 0xFFFACC15,
            phonicsSound = "/d/",
            phonicsExample = "D says /d/ as in Duck & Dog!",
            personality = "Cheerful, bubbly, and loves splashing in water!",
            greetingSpeech = "Quack quack! I'm Duck! 'D' is for Duck! /d/ /d/ Duck! Splish splash!",
            storyIntro = "Duck is waddling across the pond to find friendly dogs, magical doors, and musical drums!",
            vocabulary = listOf(
                CharacterVocabItem("Duck", "🦆", "The cheerful yellow Duck swims in the pond.", "بطة", phoneticSpelling = "DUHK"),
                CharacterVocabItem("Dog", "🐶", "The playful puppy Dog wags its tail happily.", "كلب", phoneticSpelling = "DAWG"),
                CharacterVocabItem("Door", "🚪", "Open the wooden Door to enter the cozy room.", "باب", phoneticSpelling = "DOR"),
                CharacterVocabItem("Drum", "🥁", "Tap the Drum to make a loud rhythmic beat.", "طبل", phoneticSpelling = "DRUHM")
            ),
            missions = makeMissionsForLetter('D', "Duck" to "🦆", "Dog" to "🐶", "Door" to "🚪", "Drum" to "🥁"),
            unlockBadgeName = "Daring Duck Scout"
        ),

        // E -> Elephant (Gentle baby elephant)
        LetterCharacter(
            letter = 'E',
            name = "Elephant",
            characterEmoji = "🐘",
            themeColorHex = 0xFF0284C7,
            secondaryColorHex = 0xFF38BDF8,
            phonicsSound = "/e/",
            phonicsExample = "E says /e/ as in Elephant & Egg!",
            personality = "Kind, helpful, and very strong!",
            greetingSpeech = "Pawoo! I'm Elephant! 'E' is for Elephant! /e/ /e/ Elephant! I have a long trunk!",
            storyIntro = "Elephant wants to help all safari friends find fresh eggs, soaring eagles, and listen with big ears!",
            vocabulary = listOf(
                CharacterVocabItem("Elephant", "🐘", "The gentle Elephant sprays water with its trunk.", "فيل", phoneticSpelling = "EL-uh-fuhnt"),
                CharacterVocabItem("Egg", "🥚", "The little white Egg hatches into a chick.", "بيضة", phoneticSpelling = "EG"),
                CharacterVocabItem("Eagle", "🦅", "The majestic Eagle soars high in the blue sky.", "نسر", phoneticSpelling = "EE-guhl"),
                CharacterVocabItem("Ear", "👂", "Use your Ear to listen to joyful music.", "أذن", phoneticSpelling = "EER")
            ),
            missions = makeMissionsForLetter('E', "Elephant" to "🐘", "Egg" to "🥚", "Eagle" to "🦅", "Ear" to "👂"),
            unlockBadgeName = "Gentle Elephant Hero"
        ),

        // F -> Fish (Orange clownfish)
        LetterCharacter(
            letter = 'F',
            name = "Fish",
            characterEmoji = "🐟",
            themeColorHex = 0xFFF97316,
            secondaryColorHex = 0xFFFB923C,
            phonicsSound = "/f/",
            phonicsExample = "F says /f/ as in Fish & Frog!",
            personality = "Fast, cheerful, and loves swimming in waves!",
            greetingSpeech = "Blub blub! I'm Fish! 'F' is for Fish! /f/ /f/ Fish! Let's swim in the ocean!",
            storyIntro = "Fish is swimming along coral reefs to find jumping frogs, warm campfire fires, and pretty flowers!",
            vocabulary = listOf(
                CharacterVocabItem("Fish", "🐟", "The orange clown Fish swims through the sea.", "سمكة", phoneticSpelling = "FISH"),
                CharacterVocabItem("Frog", "🐸", "The green Frog jumps ribbit ribbit on lily pads.", "ضفدع", phoneticSpelling = "FRAWG"),
                CharacterVocabItem("Fire", "🔥", "The warm campfire Fire keeps campers cozy.", "نار", phoneticSpelling = "FY-er"),
                CharacterVocabItem("Flower", "🌸", "The sweet pink Flower smells wonderful.", "زهرة", phoneticSpelling = "FLOW-er")
            ),
            missions = makeMissionsForLetter('F', "Fish" to "🐟", "Frog" to "🐸", "Fire" to "🔥", "Flower" to "🌸"),
            unlockBadgeName = "Speedy Fish Swimmer"
        ),

        // G -> Giraffe (Golden yellow spotted giraffe)
        LetterCharacter(
            letter = 'G',
            name = "Giraffe",
            characterEmoji = "🦒",
            themeColorHex = 0xFFD97706,
            secondaryColorHex = 0xFFF59E0B,
            phonicsSound = "/g/",
            phonicsExample = "G says /g/ as in Giraffe & Goat!",
            personality = "Tall, friendly, and sees everything from up high!",
            greetingSpeech = "Hello! I'm Giraffe! 'G' is for Giraffe! /g/ /g/ Giraffe! Look how tall I am!",
            storyIntro = "Giraffe is looking from high above to help friendly goats, unwrap surprise gifts, and harvest sweet grapes!",
            vocabulary = listOf(
                CharacterVocabItem("Giraffe", "🦒", "The tall spotted Giraffe reaches high tree leaves.", "زرافة", phoneticSpelling = "juh-RAF"),
                CharacterVocabItem("Goat", "🐐", "The playful mountain Goat leaps on the rocks.", "ماعز", phoneticSpelling = "GOHT"),
                CharacterVocabItem("Gift", "🎁", "Unwrap the shiny surprise Gift box with a ribbon.", "هدية", phoneticSpelling = "GIFT"),
                CharacterVocabItem("Grapes", "🍇", "Juicy purple Grapes grow in sweet bunches.", "عنب", phoneticSpelling = "GRAYPS")
            ),
            missions = makeMissionsForLetter('G', "Giraffe" to "🦒", "Goat" to "🐐", "Gift" to "🎁", "Grapes" to "🍇"),
            unlockBadgeName = "High-Flying Giraffe Scout"
        ),

        // H -> Horse (Chestnut brown horse)
        LetterCharacter(
            letter = 'H',
            name = "Horse",
            characterEmoji = "🐴",
            themeColorHex = 0xFF92400E,
            secondaryColorHex = 0xFFB45309,
            phonicsSound = "/h/",
            phonicsExample = "H says /h/ as in Horse & House!",
            personality = "Brave, speedy, and a loyal friend!",
            greetingSpeech = "Neigh! I'm Horse! 'H' is for Horse! /h/ /h/ Horse! Let's gallop!",
            storyIntro = "Horse loves galloping across fields to find cozy houses, stylish hats, and wave friendly hands!",
            vocabulary = listOf(
                CharacterVocabItem("Horse", "🐴", "The brown Horse gallops fast across the grassy field.", "حصان", phoneticSpelling = "HORS"),
                CharacterVocabItem("House", "🏠", "A cozy warm House for the whole family.", "منزل", phoneticSpelling = "HOWS"),
                CharacterVocabItem("Hat", "🎩", "Wear the cool Hat to shield your eyes from sun.", "قبعة", phoneticSpelling = "HAT"),
                CharacterVocabItem("Hand", "✋", "Wave your Hand high up to say hello.", "يد", phoneticSpelling = "HAND")
            ),
            missions = makeMissionsForLetter('H', "Horse" to "🐴", "House" to "🏠", "Hat" to "🎩", "Hand" to "✋"),
            unlockBadgeName = "Galloping Horse Champion"
        ),

        // I -> Ice Cream (Strawberry scoop ice cream)
        LetterCharacter(
            letter = 'I',
            name = "Ice Cream",
            characterEmoji = "🍦",
            themeColorHex = 0xFFEC4899,
            secondaryColorHex = 0xFFF472B6,
            phonicsSound = "/aɪ/",
            phonicsExample = "I says /aɪ/ as in Ice Cream & Igloo!",
            personality = "Cool, sweet, and always brings smiles!",
            greetingSpeech = "Yum! I'm Ice Cream! 'I' is for Ice Cream! /aɪ/ /aɪ/ Ice Cream! Cool and sweet!",
            storyIntro = "Ice Cream is exploring snowy mountains to build frosty igloos, help tiny insects, and press shirts with irons!",
            vocabulary = listOf(
                CharacterVocabItem("Ice Cream", "🍦", "Cold strawberry Ice Cream is delicious on warm days.", "مثلجات", phoneticSpelling = "EYES-kreem"),
                CharacterVocabItem("Igloo", "🧊", "The snowy white Igloo is warm and cozy inside.", "كوخ جليدي", phoneticSpelling = "IG-loo"),
                CharacterVocabItem("Insect", "🪲", "The tiny shiny Insect crawls on the green leaf.", "حشرة", phoneticSpelling = "IN-sekt"),
                CharacterVocabItem("Iron", "👔", "Use the smooth Iron to press clothes neat and clean.", "مكواة", phoneticSpelling = "EYE-urn")
            ),
            missions = makeMissionsForLetter('I', "Ice Cream" to "🍦", "Igloo" to "🧊", "Insect" to "🪲", "Iron" to "👔"),
            unlockBadgeName = "Sweet Ice Cream Master"
        ),

        // J -> Jellyfish (Pastel lavender jellyfish)
        LetterCharacter(
            letter = 'J',
            name = "Jellyfish",
            characterEmoji = "🪼",
            themeColorHex = 0xFF8B5CF6,
            secondaryColorHex = 0xFFA78BFA,
            phonicsSound = "/dʒ/",
            phonicsExample = "J says /dʒ/ as in Jellyfish & Juice!",
            personality = "Gentle, calm, and floats gracefully!",
            greetingSpeech = "Float float! I'm Jellyfish! 'J' is for Jellyfish! /dʒ/ /dʒ/ Jellyfish! Look at my tentacles!",
            storyIntro = "Jellyfish is floating through ocean waves finding fruity juice, warm jackets, and practicing big jumps!",
            vocabulary = listOf(
                CharacterVocabItem("Jellyfish", "🪼", "The purple Jellyfish floats gracefully in ocean waves.", "قنديل البحر", phoneticSpelling = "JEL-ee-fish"),
                CharacterVocabItem("Juice", "🧃", "Fresh orange Juice is cold, sweet, and fruity.", "عصير", phoneticSpelling = "JOOS"),
                CharacterVocabItem("Jacket", "🧥", "Wear your warm cozy Jacket when it is breezy outside.", "سترة", phoneticSpelling = "JAK-it"),
                CharacterVocabItem("Jump", "🦘", "Jump high up into the air with joy and excitement.", "يقفز", phoneticSpelling = "JUHMP")
            ),
            missions = makeMissionsForLetter('J', "Jellyfish" to "🪼", "Juice" to "🧃", "Jacket" to "🧥", "Jump" to "🦘"),
            unlockBadgeName = "Floating Jellyfish Star"
        ),

        // K -> Koala (Soft grey koala)
        LetterCharacter(
            letter = 'K',
            name = "Koala",
            characterEmoji = "🐨",
            themeColorHex = 0xFF64748B,
            secondaryColorHex = 0xFF94A3B8,
            phonicsSound = "/k/",
            phonicsExample = "K says /k/ as in Koala & Kite!",
            personality = "Cuddly, peaceful, and loves climbing trees!",
            greetingSpeech = "G'day! I'm Koala! 'K' is for Koala! /k/ /k/ Koala! I love climbing!",
            storyIntro = "Koala is high up in the eucalyptus trees looking for flying kites, shiny keys, and noble kings!",
            vocabulary = listOf(
                CharacterVocabItem("Koala", "🐨", "The fuzzy grey Koala hugs the tree branch tightly.", "كوالا", phoneticSpelling = "koh-AH-luh"),
                CharacterVocabItem("Kite", "🪁", "The diamond Kite flies high in the breezy blue sky.", "طائرة ورقية", phoneticSpelling = "KYTE"),
                CharacterVocabItem("Key", "🔑", "The golden Key unlocks the treasure chest.", "مفتاح", phoneticSpelling = "KEE"),
                CharacterVocabItem("King", "👑", "The noble King wears a sparkling golden crown.", "ملك", phoneticSpelling = "KING")
            ),
            missions = makeMissionsForLetter('K', "Koala" to "🐨", "Kite" to "🪁", "Key" to "🔑", "King" to "👑"),
            unlockBadgeName = "Kind Koala Climber"
        ),

        // L -> Lion (Golden lion with mane)
        LetterCharacter(
            letter = 'L',
            name = "Lion",
            characterEmoji = "🦁",
            themeColorHex = 0xFFF59E0B,
            secondaryColorHex = 0xFFFBBF24,
            phonicsSound = "/l/",
            phonicsExample = "L says /l/ as in Lion & Lemon!",
            personality = "Brave, royal, and has a mighty roar!",
            greetingSpeech = "Roar! I'm Lion! 'L' is for Lion! /l/ /l/ Lion! Let's be brave!",
            storyIntro = "Lion is leading the savanna team to find sour lemons, bright glowing lamps, and fluttering green leaves!",
            vocabulary = listOf(
                CharacterVocabItem("Lion", "🦁", "The brave Lion has a magnificent golden mane.", "أسد", phoneticSpelling = "LY-uhn"),
                CharacterVocabItem("Lemon", "🍋", "The yellow Lemon is fresh, sour, and full of flavor.", "ليمون", phoneticSpelling = "LEM-uhn"),
                CharacterVocabItem("Lamp", "💡", "Turn on the bright Lamp to illuminate the room.", "مصباح", phoneticSpelling = "LAMP"),
                CharacterVocabItem("Leaf", "🍃", "A fluttering green Leaf dances in the wind.", "ورقة شجر", phoneticSpelling = "LEEF")
            ),
            missions = makeMissionsForLetter('L', "Lion" to "🦁", "Lemon" to "🍋", "Lamp" to "💡", "Leaf" to "🍃"),
            unlockBadgeName = "Mighty Lion King"
        ),

        // M -> Monkey (Playful brown monkey)
        LetterCharacter(
            letter = 'M',
            name = "Monkey",
            characterEmoji = "🐵",
            themeColorHex = 0xFFB45309,
            secondaryColorHex = 0xFFD97706,
            phonicsSound = "/m/",
            phonicsExample = "M says /m/ as in Monkey & Moon!",
            personality = "Playful, funny, and loves swinging on vines!",
            greetingSpeech = "Ooh ooh aah aah! I'm Monkey! 'M' is for Monkey! /m/ /m/ Monkey! Let's swing!",
            storyIntro = "Monkey is swinging through the canopy to gaze at the glowing moon, drink cold milk, and say hi to tiny mouse!",
            vocabulary = listOf(
                CharacterVocabItem("Monkey", "🐵", "The clever Monkey swings happily from jungle vines.", "قرد", phoneticSpelling = "MUHNG-kee"),
                CharacterVocabItem("Moon", "🌙", "The crescent Moon glows softly in the evening sky.", "قمر", phoneticSpelling = "MOON"),
                CharacterVocabItem("Milk", "🥛", "Drink healthy white Milk to grow strong bones.", "حليب", phoneticSpelling = "MILK"),
                CharacterVocabItem("Mouse", "🐭", "The tiny cute Mouse nibbles a piece of yellow cheese.", "فأر", phoneticSpelling = "MOWS")
            ),
            missions = makeMissionsForLetter('M', "Monkey" to "🐵", "Moon" to "🌙", "Milk" to "🥛", "Mouse" to "🐭"),
            unlockBadgeName = "Jumping Jungle Monkey"
        ),

        // N -> Nest (Twig nest with eggs)
        LetterCharacter(
            letter = 'N',
            name = "Nest",
            characterEmoji = "🪺",
            themeColorHex = 0xFF78350F,
            secondaryColorHex = 0xFF92400E,
            phonicsSound = "/n/",
            phonicsExample = "N says /n/ as in Nest & Nose!",
            personality = "Warm, cozy, and nurturing!",
            greetingSpeech = "Tweet tweet! I'm Nest! 'N' is for Nest! /n/ /n/ Nest! A safe cozy home!",
            storyIntro = "Nest is sitting safely high in the tree watching sniffing noses, helpful nurses, and starry night skies!",
            vocabulary = listOf(
                CharacterVocabItem("Nest", "🪺", "The cozy bird Nest protects tiny baby eggs.", "عش", phoneticSpelling = "NEST"),
                CharacterVocabItem("Nose", "👃", "I use my Nose to smell fresh blooming flowers.", "أنف", phoneticSpelling = "NOHZ"),
                CharacterVocabItem("Nurse", "👩‍⚕️", "The kind Nurse helps everyone feel healthy and well.", "ممرضة", phoneticSpelling = "NURS"),
                CharacterVocabItem("Night", "🌌", "Twinkling stars shine brightly across the dark Night sky.", "ليل", phoneticSpelling = "NYTE")
            ),
            missions = makeMissionsForLetter('N', "Nest" to "🪺", "Nose" to "👃", "Nurse" to "👩‍⚕️", "Night" to "🌌"),
            unlockBadgeName = "Cozy Nest Guardian"
        ),

        // O -> Owl (Wise blue owl)
        LetterCharacter(
            letter = 'O',
            name = "Owl",
            characterEmoji = "🦉",
            themeColorHex = 0xFF0284C7,
            secondaryColorHex = 0xFF38BDF8,
            phonicsSound = "/ɒ/",
            phonicsExample = "O says /ɒ/ as in Owl & Orange!",
            personality = "Smart, calm, and loves nighttime wisdom!",
            greetingSpeech = "Hoo hoo! I'm Owl! 'O' is for Owl! /ɒ/ /ɒ/ Owl! I have big wise eyes!",
            storyIntro = "Owl sits on a tree branch looking for juicy round oranges, the deep blue ocean, and eight-armed octopuses!",
            vocabulary = listOf(
                CharacterVocabItem("Owl", "🦉", "The wise Owl hoots gently in the peaceful forest.", "بومة", phoneticSpelling = "OWL"),
                CharacterVocabItem("Orange", "🍊", "The round orange fruit is sweet, juicy, and healthy.", "برتقالة", phoneticSpelling = "OR-inj"),
                CharacterVocabItem("Ocean", "🌊", "The vast blue Ocean is filled with amazing sea creatures.", "محيط", phoneticSpelling = "OH-shuhn"),
                CharacterVocabItem("Octopus", "🐙", "The clever Octopus has eight flexible arms.", "أخطبوط", phoneticSpelling = "AHK-tuh-puhs")
            ),
            missions = makeMissionsForLetter('O', "Owl" to "🦉", "Orange" to "🍊", "Ocean" to "🌊", "Octopus" to "🐙"),
            unlockBadgeName = "Wise Owl Scholar"
        ),

        // P -> Penguin (Tuxedo penguin)
        LetterCharacter(
            letter = 'P',
            name = "Penguin",
            characterEmoji = "🐧",
            themeColorHex = 0xFF1E293B,
            secondaryColorHex = 0xFF475569,
            phonicsSound = "/p/",
            phonicsExample = "P says /p/ as in Penguin & Pizza!",
            personality = "Playful, polite, and loves sliding on ice!",
            greetingSpeech = "Waddle waddle! I'm Penguin! 'P' is for Penguin! /p/ /p/ Penguin! Let's slide on ice!",
            storyIntro = "Penguin is sliding across snowy glaciers to deliver warm pizza, write with sharp pencils, and visit panda!",
            vocabulary = listOf(
                CharacterVocabItem("Penguin", "🐧", "The cute Penguin waddles across the snowy ice.", "بطريق", phoneticSpelling = "PENG-gwin"),
                CharacterVocabItem("Pizza", "🍕", "Hot cheesy Pizza with delicious tomato sauce.", "بيتزا", phoneticSpelling = "PEET-suh"),
                CharacterVocabItem("Pencil", "✏️", "Use the sharp Pencil to draw and write letters.", "قلم رصاص", phoneticSpelling = "PEN-suhl"),
                CharacterVocabItem("Panda", "🐼", "The black and white Panda munches green bamboo leaves.", "باندا", phoneticSpelling = "PAN-duh")
            ),
            missions = makeMissionsForLetter('P', "Penguin" to "🐧", "Pizza" to "🍕", "Pencil" to "✏️", "Panda" to "🐼"),
            unlockBadgeName = "Polite Penguin Explorer"
        ),

        // Q -> Queen (Royal crowned queen)
        LetterCharacter(
            letter = 'Q',
            name = "Queen",
            characterEmoji = "👑",
            themeColorHex = 0xFF7E22CE,
            secondaryColorHex = 0xFFA855F7,
            phonicsSound = "/kw/",
            phonicsExample = "Q says /kw/ as in Queen & Quilt!",
            personality = "Graceful, kind, and royal leader!",
            greetingSpeech = "Greetings! I am Queen! 'Q' is for Queen! /kw/ /kw/ Queen! Welcome to my kingdom!",
            storyIntro = "Queen rules the kingdom with kindness, sewing cozy quilts, asking clever questions, and being super quick!",
            vocabulary = listOf(
                CharacterVocabItem("Queen", "👑", "The gracious Queen wears a sparkling gold crown.", "ملكة", phoneticSpelling = "KWEEN"),
                CharacterVocabItem("Quilt", "🧵", "The colorful patchwork Quilt keeps us warm and cozy.", "لحاف", phoneticSpelling = "KWILT"),
                CharacterVocabItem("Question", "❓", "Raise your hand to ask a smart Question.", "سؤال", phoneticSpelling = "KWES-chuhn"),
                CharacterVocabItem("Quick", "⚡", "The speedy cheetah runs very Quick like lightning!", "سريع", phoneticSpelling = "KWIK")
            ),
            missions = makeMissionsForLetter('Q', "Queen" to "👑", "Quilt" to "🧵", "Question" to "❓", "Quick" to "⚡"),
            unlockBadgeName = "Royal Queen Crown"
        ),

        // R -> Rabbit (White fluffy bunny)
        LetterCharacter(
            letter = 'R',
            name = "Rabbit",
            characterEmoji = "🐰",
            themeColorHex = 0xFFDB2777,
            secondaryColorHex = 0xFFF472B6,
            phonicsSound = "/r/",
            phonicsExample = "R says /r/ as in Rabbit & Robot!",
            personality = "Fast, cheerful, and loves hopping around!",
            greetingSpeech = "Hop hop! I'm Rabbit! 'R' is for Rabbit! /r/ /r/ Rabbit! Let's hop together!",
            storyIntro = "Rabbit is hopping through gardens to find friendly robots, play in the rain, and discover shining rings!",
            vocabulary = listOf(
                CharacterVocabItem("Rabbit", "🐰", "The fluffy white Rabbit hops fast with long ears.", "أرنب", phoneticSpelling = "RAB-it"),
                CharacterVocabItem("Robot", "🤖", "The smart mechanical Robot beeps and flashes lights.", "روبوت", phoneticSpelling = "ROH-baht"),
                CharacterVocabItem("Rain", "🌧️", "Pitter-patter falls the fresh Rain from clouds.", "مطر", phoneticSpelling = "RAYN"),
                CharacterVocabItem("Ring", "💍", "The shining gold Ring sparkles in the sunshine.", "خاتم", phoneticSpelling = "RING")
            ),
            missions = makeMissionsForLetter('R', "Rabbit" to "🐰", "Robot" to "🤖", "Rain" to "🌧️", "Ring" to "💍"),
            unlockBadgeName = "Joyful Hopping Rabbit"
        ),

        // S -> Snake (Emerald green snake)
        LetterCharacter(
            letter = 'S',
            name = "Snake",
            characterEmoji = "🐍",
            themeColorHex = 0xFF16A34A,
            secondaryColorHex = 0xFF22C55E,
            phonicsSound = "/s/",
            phonicsExample = "S says /s/ as in Snake & Sun!",
            personality = "Silly, friendly, and loves sliding smoothly!",
            greetingSpeech = "Sssss! I'm Snake! 'S' is for Snake! /s/ /s/ Snake! Slither and smile!",
            storyIntro = "Snake is sliding across sunny fields to bask under the sun, touch glowing stars, and make yummy sandwiches!",
            vocabulary = listOf(
                CharacterVocabItem("Snake", "🐍", "The green spotted Snake slithers smoothly on grass.", "ثعبان", phoneticSpelling = "SNAYK"),
                CharacterVocabItem("Sun", "☀️", "The bright yellow Sun warms our wonderful day.", "شمس", phoneticSpelling = "SUHN"),
                CharacterVocabItem("Star", "⭐", "A twinkling gold Star shines in the night sky.", "نجمة", phoneticSpelling = "STAHR"),
                CharacterVocabItem("Sandwich", "🥪", "The tasty Sandwich has lettuce, cheese, and tomatoes.", "شطيرة", phoneticSpelling = "SAND-wich")
            ),
            missions = makeMissionsForLetter('S', "Snake" to "🐍", "Sun" to "☀️", "Star" to "⭐", "Sandwich" to "🥪"),
            unlockBadgeName = "Smooth Slithering Snake"
        ),

        // T -> Tiger (Vibrant orange tiger cub)
        LetterCharacter(
            letter = 'T',
            name = "Tiger",
            characterEmoji = "🐯",
            themeColorHex = 0xFFEA580C,
            secondaryColorHex = 0xFFF97316,
            phonicsSound = "/t/",
            phonicsExample = "T says /t/ as in Tiger & Tree!",
            personality = "Energetic, adventurous, and loves running fast!",
            greetingSpeech = "Grrr-eat! I'm Tiger! 'T' is for Tiger! /t/ /t/ Tiger! Let's explore the jungle!",
            storyIntro = "Tiger is exploring the jungle to climb tall trees, play with soft toys, and ride the choo-choo train!",
            vocabulary = listOf(
                CharacterVocabItem("Tiger", "🐯", "The playful orange Tiger has black stripes.", "نمر", phoneticSpelling = "TY-gur"),
                CharacterVocabItem("Tree", "🌳", "The tall green Tree provides cool shade on sunny days.", "شجرة", phoneticSpelling = "TREE"),
                CharacterVocabItem("Toy", "🧸", "The soft cuddly Toy teddy bear is my favorite.", "لعبة", phoneticSpelling = "TOY"),
                CharacterVocabItem("Train", "🚂", "Choo choo! The steam Train chugs down the railway.", "قطار", phoneticSpelling = "TRAYN")
            ),
            missions = makeMissionsForLetter('T', "Tiger" to "🐯", "Tree" to "🌳", "Toy" to "🧸", "Train" to "🚂"),
            unlockBadgeName = "Tiger Adventure Champion"
        ),

        // U -> Umbrella (Colorful rainbow umbrella)
        LetterCharacter(
            letter = 'U',
            name = "Umbrella",
            characterEmoji = "☂️",
            themeColorHex = 0xFF0284C7,
            secondaryColorHex = 0xFF06B6D4,
            phonicsSound = "/ʌ/",
            phonicsExample = "U says /ʌ/ as in Umbrella & Up!",
            personality = "Helpful, colorful, and shields everyone from rain!",
            greetingSpeech = "Open up! I'm Umbrella! 'U' is for Umbrella! /ʌ/ /ʌ/ Umbrella! Rain or shine, I'm here!",
            storyIntro = "Umbrella is opening up to find magical unicorns, float up into the sky, and wear neat martial arts uniforms!",
            vocabulary = listOf(
                CharacterVocabItem("Umbrella", "☂️", "The colorful Umbrella keeps us dry when it rains.", "مظلة", phoneticSpelling = "uhm-BREL-uh"),
                CharacterVocabItem("Unicorn", "🦄", "The magical white Unicorn has a glowing horn.", "وحيد القرن", phoneticSpelling = "YOO-nuh-korn"),
                CharacterVocabItem("Up", "🎈", "Float Up into the sky with a colorful balloon.", "فوق", phoneticSpelling = "UHP"),
                CharacterVocabItem("Uniform", "🥋", "Wear the clean karate Uniform with focus and pride.", "زي موحد", phoneticSpelling = "YOO-nuh-form")
            ),
            missions = makeMissionsForLetter('U', "Umbrella" to "☂️", "Unicorn" to "🦄", "Up" to "🎈", "Uniform" to "🥋"),
            unlockBadgeName = "Rainbow Umbrella Protector"
        ),

        // V -> Violin (Polished wooden violin)
        LetterCharacter(
            letter = 'V',
            name = "Violin",
            characterEmoji = "🎻",
            themeColorHex = 0xFF9A3412,
            secondaryColorHex = 0xFFC2410C,
            phonicsSound = "/v/",
            phonicsExample = "V says /v/ as in Violin & Van!",
            personality = "Musical, elegant, and makes sweet melodies!",
            greetingSpeech = "La la la! I'm Violin! 'V' is for Violin! /v/ /v/ Violin! Let's make sweet music!",
            storyIntro = "Violin is on a musical concert tour riding the family van, seeing glowing volcanoes, and eating fresh vegetables!",
            vocabulary = listOf(
                CharacterVocabItem("Violin", "🎻", "The wooden Violin plays sweet classical melodies.", "كمان", phoneticSpelling = "vy-uh-LIN"),
                CharacterVocabItem("Van", "🚐", "The blue family Van takes everyone on road trips.", "شاحنة صغيرة", phoneticSpelling = "VAN"),
                CharacterVocabItem("Volcano", "🌋", "The mighty mountain Volcano puffs soft smoke.", "بركان", phoneticSpelling = "vohl-KAY-noh"),
                CharacterVocabItem("Vegetable", "🥕", "Crunchy orange carrots are healthy Vegetables.", "خضار", phoneticSpelling = "VEJ-tuh-buhl")
            ),
            missions = makeMissionsForLetter('V', "Violin" to "🎻", "Van" to "🚐", "Volcano" to "🌋", "Vegetable" to "🥕"),
            unlockBadgeName = "Musical Violin Virtuoso"
        ),

        // W -> Whale (Deep blue ocean whale)
        LetterCharacter(
            letter = 'W',
            name = "Whale",
            characterEmoji = "🐋",
            themeColorHex = 0xFF0284C7,
            secondaryColorHex = 0xFF38BDF8,
            phonicsSound = "/w/",
            phonicsExample = "W says /w/ as in Whale & Water!",
            personality = "Grand, friendly, and spouts water joyfully!",
            greetingSpeech = "Splash splash! I'm Whale! 'W' is for Whale! /w/ /w/ Whale! Welcome to the sea!",
            storyIntro = "Whale is swimming through deep blue oceans finding clean water, pulling red wagons, and feeling gentle wind!",
            vocabulary = listOf(
                CharacterVocabItem("Whale", "🐋", "The gentle blue Whale swims peacefully in the sea.", "حوت", phoneticSpelling = "WAYL"),
                CharacterVocabItem("Water", "💧", "Drink clean fresh Water every day to stay healthy.", "ماء", phoneticSpelling = "WAH-ter"),
                CharacterVocabItem("Wagon", "🛒", "Pull the red Wagon loaded with your favorite toys.", "عربة", phoneticSpelling = "WAG-uhn"),
                CharacterVocabItem("Wind", "💨", "The cool gentle Wind makes the trees whisper.", "رياح", phoneticSpelling = "WIND")
            ),
            missions = makeMissionsForLetter('W', "Whale" to "🐋", "Water" to "💧", "Wagon" to "🛒", "Wind" to "💨"),
            unlockBadgeName = "Gentle Whale Navigator"
        ),

        // X -> Xylophone (Rainbow chime xylophone)
        LetterCharacter(
            letter = 'X',
            name = "Xylophone",
            characterEmoji = "🎵",
            themeColorHex = 0xFF7C3AED,
            secondaryColorHex = 0xFFA78BFA,
            phonicsSound = "/ks/",
            phonicsExample = "X says /ks/ as in Xylophone & X-Ray!",
            personality = "Musical, vibrant, and loves cheerful tunes!",
            greetingSpeech = "Ding dong! I'm Xylophone! 'X' is for Xylophone! /ks/ /ks/ Xylophone! Let's play!",
            storyIntro = "Xylophone is tapping rainbow chimes to view bone x-rays, spot the xenops bird, and celebrate Xmas holidays!",
            vocabulary = listOf(
                CharacterVocabItem("Xylophone", "🎵", "Tap colorful bars on the musical Xylophone.", "إكسيلوفون", phoneticSpelling = "ZY-luh-fohn"),
                CharacterVocabItem("X-Ray", "🩻", "The medical X-Ray sees our strong healthy bones.", "أشعة سينية", phoneticSpelling = "EKS-ray"),
                CharacterVocabItem("Xenops", "🐦", "The tiny Xenops bird flutters in rainforest trees.", "طائر الزينوبس", phoneticSpelling = "ZEE-nops"),
                CharacterVocabItem("Xmas", "🎄", "Decorate the sparkling Xmas tree with ornaments.", "عيد الميلاد", phoneticSpelling = "EKS-muhs")
            ),
            missions = makeMissionsForLetter('X', "Xylophone" to "🎵", "X-Ray" to "🩻", "Xenops" to "🐦", "Xmas" to "🎄"),
            unlockBadgeName = "Rainbow Xylophone Maestro"
        ),

        // Y -> Yak (Shaggy brown mountain yak)
        LetterCharacter(
            letter = 'Y',
            name = "Yak",
            characterEmoji = "🐂",
            themeColorHex = 0xFF78350F,
            secondaryColorHex = 0xFF92400E,
            phonicsSound = "/j/",
            phonicsExample = "Y says /j/ as in Yak & Yo-Yo!",
            personality = "Strong, cozy, and loves mountain trails!",
            greetingSpeech = "Warm snort! I'm Yak! 'Y' is for Yak! /j/ /j/ Yak! I have cozy warm fur!",
            storyIntro = "Yak is trekking high across mountain peaks spinning fun yo-yos, finding yellow gems, and eating healthy yogurt!",
            vocabulary = listOf(
                CharacterVocabItem("Yak", "🐂", "The shaggy brown Yak lives in high snowy mountains.", "ثور الياك", phoneticSpelling = "YAK"),
                CharacterVocabItem("Yo-Yo", "🪀", "Spin the spinning Yo-Yo up and down on its string.", "يويو", phoneticSpelling = "YOH-yoh"),
                CharacterVocabItem("Yellow", "💛", "The bright Yellow heart shines like the morning sun.", "أصفر", phoneticSpelling = "YEL-oh"),
                CharacterVocabItem("Yogurt", "🥣", "Creamy sweet strawberry Yogurt is a healthy snack.", "زبادي", phoneticSpelling = "YOH-gurt")
            ),
            missions = makeMissionsForLetter('Y', "Yak" to "🐂", "Yo-Yo" to "🪀", "Yellow" to "💛", "Yogurt" to "🥣"),
            unlockBadgeName = "Mountain Yak Champion"
        ),

        // Z -> Zebra (Black and white striped zebra)
        LetterCharacter(
            letter = 'Z',
            name = "Zebra",
            characterEmoji = "🦓",
            themeColorHex = 0xFF1E293B,
            secondaryColorHex = 0xFF475569,
            phonicsSound = "/z/",
            phonicsExample = "Z says /z/ as in Zebra & Zoo!",
            personality = "Energetic, stylish, and loves zigzag running!",
            greetingSpeech = "Whee! I'm Zebra! 'Z' is for Zebra! /z/ /z/ Zebra! Look at my cool stripes!",
            storyIntro = "Zebra is galloping through the safari to visit animal zoos, pull jackets with zippers, and count down to zero!",
            vocabulary = listOf(
                CharacterVocabItem("Zebra", "🦓", "The swift Zebra has beautiful black and white stripes.", "حمار وحشي", phoneticSpelling = "ZEE-bruh"),
                CharacterVocabItem("Zoo", "🦁", "Visit all the amazing friendly animals at the animal Zoo.", "حديقة حيوان", phoneticSpelling = "ZOO"),
                CharacterVocabItem("Zipper", "🤐", "Pull the metal Zipper to zip up your warm coat.", "سحاب", phoneticSpelling = "ZIP-er"),
                CharacterVocabItem("Zero", "0️⃣", "The number Zero represents a fresh beginning circle.", "صفر", phoneticSpelling = "ZEER-oh")
            ),
            missions = makeMissionsForLetter('Z', "Zebra" to "🦓", "Zoo" to "🦁", "Zipper" to "🤐", "Zero" to "0️⃣"),
            unlockBadgeName = "Zippy Zebra Explorer"
        )
    )

    fun getCharacterByLetter(letter: Char): LetterCharacter {
        return characters.find { it.letter.equals(letter, ignoreCase = true) } ?: characters.first()
    }
}
