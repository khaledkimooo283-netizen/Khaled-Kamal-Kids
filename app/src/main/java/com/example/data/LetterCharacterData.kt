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
    ROPE_RESCUE,        // 1. Pick up rope -> Move to water -> Throw rope -> Pull friend to safety
    ROLLING_BASKET,     // 2. Chase/catch rolling item -> Carry item -> Deliver to Basket
    INTERACT_ACTIVATE,  // 3. Approach machine/vehicle -> Activate/Launch/Play
    BRIDGE_ENCOUNTER    // 4. Navigate across terrain/bridge -> Feed or greet friend
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
    val goalLocationName: String = "Basket",
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

    val characters: List<LetterCharacter> = listOf(
        // A -> Apple (Red glossy 3D apple character with leaf & friendly smile)
        LetterCharacter(
            letter = 'A',
            name = "Apple",
            characterEmoji = "🍎",
            themeColorHex = 0xFFDC2626, // Crimson Red
            secondaryColorHex = 0xFFEF4444,
            phonicsSound = "/æ/",
            phonicsExample = "A says /æ/ as in Apple!",
            personality = "Happy, sweet, and full of energy!",
            greetingSpeech = "Hi! I'm Apple! 'A' is for Apple! /æ/ /æ/ Apple! Let's explore together!",
            storyIntro = "Uh-oh! Apple wants to gather all tasty red snacks and find little friends in the garden!",
            vocabulary = listOf(
                CharacterVocabItem("Apple", "🍎", "The sweet red Apple is juicy and yummy.", "تفاحة", phoneticSpelling = "AP-uhl"),
                CharacterVocabItem("Ant", "🐜", "The tiny Ant carries a big green leaf.", "نملة", phoneticSpelling = "ANT"),
                CharacterVocabItem("Airplane", "✈️", "The fast Airplane flies above the white clouds.", "طائرة", phoneticSpelling = "AIR-playn"),
                CharacterVocabItem("Alligator", "🐊", "The friendly green Alligator smiles by the river.", "تمساح", phoneticSpelling = "AL-uh-gay-ter"),
                CharacterVocabItem("Animal", "🐾", "The cute friendly Animal plays in the grass.", "حيوان", phoneticSpelling = "AN-ih-muhl")
            ),
            missions = listOf(
                CharacterMission(
                    id = "m_a1",
                    title = "Help the Ant!",
                    description = "Rescue the little Ant trapped near the water using the rope!",
                    targetWord = "Ant",
                    targetEmoji = "🐜",
                    gameplayType = MissionGameplayType.ROPE_RESCUE,
                    storyPrompt = "Oh no! The tiny Ant is trapped near the water! Find the rope to rescue Ant!",
                    step1Prompt = "Walk to the rope and pick it up!",
                    step2Prompt = "Go to the water edge and throw the rope to Ant!",
                    step3Prompt = "Pull the rope to bring Ant safely to the grass!",
                    ropeItemEmoji = "🪢",
                    goalLocationName = "Green Meadow",
                    goalEmoji = "🌱"
                ),
                CharacterMission(
                    id = "m_a2",
                    title = "Find the Apple!",
                    description = "OH NO! THE APPLE FELL! Catch the rolling Apple and put it in the Basket!",
                    targetWord = "Apple",
                    targetEmoji = "🍎",
                    gameplayType = MissionGameplayType.ROLLING_BASKET,
                    storyPrompt = "OH NO! THE APPLE FELL! Run and catch the rolling Apple, then place it in the Picnic Basket!",
                    step1Prompt = "Run after the rolling Apple and catch it!",
                    step2Prompt = "Carry the Apple and place it in the Picnic Basket!",
                    goalLocationName = "Picnic Basket",
                    goalEmoji = "🧺"
                ),
                CharacterMission(
                    id = "m_a3",
                    title = "Launch the Airplane!",
                    description = "Walk to the Airplane on the runway and start the propeller!",
                    targetWord = "Airplane",
                    targetEmoji = "✈️",
                    gameplayType = MissionGameplayType.INTERACT_ACTIVATE,
                    storyPrompt = "The Airplane is ready on the runway! Guide Letter A to the Airplane and start the flight!",
                    step1Prompt = "Walk over to the Airplane runway!",
                    step2Prompt = "Press Start to launch the Airplane!",
                    goalLocationName = "Sky Runway",
                    goalEmoji = "🏁"
                ),
                CharacterMission(
                    id = "m_a4",
                    title = "Meet the Alligator!",
                    description = "Cross the river bridge and become best friends with the friendly Alligator!",
                    targetWord = "Alligator",
                    targetEmoji = "🐊",
                    gameplayType = MissionGameplayType.BRIDGE_ENCOUNTER,
                    storyPrompt = "Friendly Alligator is smiling across the bridge! Cross over and greet Alligator!",
                    step1Prompt = "Walk across the cobblestone bridge!",
                    step2Prompt = "Wave hello and share a treat with Alligator!",
                    goalLocationName = "River Bridge",
                    goalEmoji = "🌉"
                )
            ),
            unlockBadgeName = "Apple Star Explorer"
        ),

        // B -> Bear (Warm honey-brown cuddly 3D teddy bear figurine)
        LetterCharacter(
            letter = 'B',
            name = "Bear",
            characterEmoji = "🐻",
            themeColorHex = 0xFF854D0E, // Honey Warm Brown
            secondaryColorHex = 0xFFB45309,
            phonicsSound = "/b/",
            phonicsExample = "B says /b/ as in Bear!",
            personality = "Friendly, gentle, and loves warm hugs!",
            greetingSpeech = "Hello friend! I'm Bear! 'B' is for Bear! /b/ /b/ Bear! Give me a high-five!",
            storyIntro = "Bear is playing in the meadow and needs help finding his favorite toys and snacks!",
            vocabulary = listOf(
                CharacterVocabItem("Bear", "🐻", "The brown Bear gives the best warm hugs.", "دب", phoneticSpelling = "BAIR"),
                CharacterVocabItem("Ball", "⚽", "The bouncy Ball rolls across the meadow.", "كرة", phoneticSpelling = "BAWL"),
                CharacterVocabItem("Book", "📖", "I love to read my colorful story Book.", "كتاب", phoneticSpelling = "BUUK"),
                CharacterVocabItem("Banana", "🍌", "The yellow Banana is sweet and yummy.", "موزة", phoneticSpelling = "buh-NAN-uh"),
                CharacterVocabItem("Bird", "🐦", "The blue Bird sings a cheerful morning song.", "طائر", phoneticSpelling = "BURD")
            ),
            missions = listOf(
                CharacterMission("m_b1", "Find Bear's Ball!", "Help Bear catch the rolling bouncy Ball!", "Ball", "⚽", listOf("Ball", "Cup", "Sun"), listOf("⚽", "🥛", "☀️"), 0),
                CharacterMission("m_b2", "Find the Sweet Banana!", "Bear is hungry for a yummy yellow Banana!", "Banana", "🍌", listOf("Egg", "Banana", "Hat"), listOf("🥚", "🍌", "🎩"), 1),
                CharacterMission("m_b3", "Beginning Sound /b/", "Which friend starts with letter B?", "Bird", "🐦", listOf("Snake", "Cat", "Bird"), listOf("🐍", "🐱", "🐦"), 2)
            ),
            unlockBadgeName = "Brave Bear Champion"
        ),

        // C -> Cat (Orange striped ginger 3D cat curved into C with playful wink)
        LetterCharacter(
            letter = 'C',
            name = "Cat",
            characterEmoji = "🐱",
            themeColorHex = 0xFFEA580C, // Vibrant Warm Orange
            secondaryColorHex = 0xFFFB923C,
            phonicsSound = "/k/",
            phonicsExample = "C says /k/ as in Cat!",
            personality = "Curious, playful, and loves purring!",
            greetingSpeech = "Meow! I'm Cat! 'C' is for Cat! /k/ /k/ Cat! Let's play together!",
            storyIntro = "Cat lost her red toy car and delicious birthday cake in the castle!",
            vocabulary = listOf(
                CharacterVocabItem("Cat", "🐱", "The soft Cat purrs when you pet its ears.", "قطة", phoneticSpelling = "KAT"),
                CharacterVocabItem("Car", "🚗", "The red Car goes beep beep down the street!", "سيارة", phoneticSpelling = "KAHR"),
                CharacterVocabItem("Cup", "🥛", "Drink fresh clean milk from the Cup.", "كوب", phoneticSpelling = "KUHP"),
                CharacterVocabItem("Cake", "🎂", "The sweet birthday Cake has glowing candles.", "كعكة", phoneticSpelling = "KAYK"),
                CharacterVocabItem("Cow", "🐮", "The friendly spotted Cow says moo moo!", "بقرة", phoneticSpelling = "KOW")
            ),
            missions = listOf(
                CharacterMission("m_c1", "Find the Red Car!", "Help Cat drive the fast red Car!", "Car", "🚗", listOf("Car", "Door", "Fish"), listOf("🚗", "🚪", "🐟"), 0),
                CharacterMission("m_c2", "Find the Birthday Cake!", "Cat wants a slice of delicious sweet Cake!", "Cake", "🎂", listOf("Tree", "Cake", "Shoe"), listOf("🌳", "🎂", "👟"), 1),
                CharacterMission("m_c3", "Beginning Sound /k/", "Which word starts with letter C?", "Cup", "🥛", listOf("Ant", "Egg", "Cup"), listOf("🐜", "🥚", "🥛"), 2)
            ),
            unlockBadgeName = "Curious Cat Detective"
        ),

        // D -> Duck (Sunny yellow 3D rubber duckling formed into D)
        LetterCharacter(
            letter = 'D',
            name = "Duck",
            characterEmoji = "🦆",
            themeColorHex = 0xFFEAB308, // Sunshine Yellow
            secondaryColorHex = 0xFFFACC15,
            phonicsSound = "/d/",
            phonicsExample = "D says /d/ as in Duck!",
            personality = "Playful, funny, and loves splashing in water!",
            greetingSpeech = "Quack quack! I'm Duck! 'D' is for Duck! /d/ /d/ Duck! Splish splash!",
            storyIntro = "Duck is swimming in the sunny pond and wants to find all his musical friends!",
            vocabulary = listOf(
                CharacterVocabItem("Duck", "🦆", "The yellow Duck swims happily in the pond.", "بطة", phoneticSpelling = "DUHK"),
                CharacterVocabItem("Dog", "🐶", "The happy Dog wags its tail and barks woof!", "كلب", phoneticSpelling = "DAWG"),
                CharacterVocabItem("Door", "🚪", "Open the wooden Door to welcome your friends.", "باب", phoneticSpelling = "DOR"),
                CharacterVocabItem("Doll", "🪆", "The cute Doll wears a pretty pink dress.", "دمية", phoneticSpelling = "DAHL"),
                CharacterVocabItem("Drum", "🥁", "Tap the loud Drum! Boom boom boom!", "طبلة", phoneticSpelling = "DRUHM")
            ),
            missions = listOf(
                CharacterMission("m_d1", "Find the Friendly Dog!", "Help Duck find his best puppy friend Dog!", "Dog", "🐶", listOf("Dog", "Bear", "Cat"), listOf("🐶", "🐻", "🐱"), 0),
                CharacterMission("m_d2", "Find the Musical Drum!", "Duck wants to make music on the loud Drum!", "Drum", "🥁", listOf("Ball", "Drum", "Leaf"), listOf("⚽", "🥁", "🍃"), 1),
                CharacterMission("m_d3", "Beginning Sound /d/", "Which item starts with letter D?", "Door", "🚪", listOf("Apple", "Sun", "Door"), listOf("🍎", "☀️", "🚪"), 2)
            ),
            unlockBadgeName = "Daring Duck Sailor"
        ),

        // E -> Elephant (Soft baby-blue 3D elephant with floppy ear & trunk as E)
        LetterCharacter(
            letter = 'E',
            name = "Elephant",
            characterEmoji = "🐘",
            themeColorHex = 0xFF0284C7, // Sky Blue
            secondaryColorHex = 0xFF38BDF8,
            phonicsSound = "/e/",
            phonicsExample = "E says /e/ as in Elephant!",
            personality = "Kind, helpful, and very strong!",
            greetingSpeech = "Pawoo! I'm Elephant! 'E' is for Elephant! /e/ /e/ Elephant! I have a long trunk!",
            storyIntro = "Elephant wants to help all the safari animals find healthy breakfast eggs and protect planet Earth!",
            vocabulary = listOf(
                CharacterVocabItem("Elephant", "🐘", "The gentle Elephant sprays water with its trunk.", "فيل", phoneticSpelling = "EL-uh-fuhnt"),
                CharacterVocabItem("Egg", "🥚", "The little white Egg hatches into a chick.", "بيضة", phoneticSpelling = "EG"),
                CharacterVocabItem("Eye", "👁️", "I can see the beautiful rainbow with my Eye.", "عين", phoneticSpelling = "EYE"),
                CharacterVocabItem("Ear", "👂", "The big floppy Ear listens to cheerful music.", "أذن", phoneticSpelling = "EER"),
                CharacterVocabItem("Earth", "🌍", "Our beautiful green and blue planet Earth.", "الأرض", phoneticSpelling = "URTH")
            ),
            missions = listOf(
                CharacterMission("m_e1", "Find the Breakfast Egg!", "Help Elephant find the fresh round Egg!", "Egg", "🥚", listOf("Egg", "Hat", "Car"), listOf("🥚", "🎩", "🚗"), 0),
                CharacterMission("m_e2", "Find Planet Earth!", "Elephant loves our beautiful planet Earth!", "Earth", "🌍", listOf("Moon", "Earth", "Star"), listOf("🌙", "🌍", "⭐"), 1),
                CharacterMission("m_e3", "Beginning Sound /e/", "Which part starts with letter E?", "Ear", "👂", listOf("Nose", "Hand", "Ear"), listOf("👃", "✋", "👂"), 2)
            ),
            unlockBadgeName = "Gentle Elephant Hero"
        ),

        // F -> Fish (Orange and white striped clownfish 3D swimmer formed into F)
        LetterCharacter(
            letter = 'F',
            name = "Fish",
            characterEmoji = "🐟",
            themeColorHex = 0xFFF97316, // Clownfish Orange
            secondaryColorHex = 0xFFFB923C,
            phonicsSound = "/f/",
            phonicsExample = "F says /f/ as in Fish!",
            personality = "Fast, cheerful, and loves swimming in waves!",
            greetingSpeech = "Blub blub! I'm Fish! 'F' is for Fish! /f/ /f/ Fish! Let's swim in the ocean!",
            storyIntro = "Fish is swimming along the coral reef looking for blooming flowers and jumpy frogs!",
            vocabulary = listOf(
                CharacterVocabItem("Fish", "🐟", "The orange clown Fish swims through the sea.", "سمكة", phoneticSpelling = "FISH"),
                CharacterVocabItem("Flower", "🌸", "The sweet pink Flower smells wonderful.", "زهرة", phoneticSpelling = "FLOW-er"),
                CharacterVocabItem("Frog", "🐸", "The green Frog jumps ribbit ribbit on the lily pad.", "ضفدع", phoneticSpelling = "FRAWG"),
                CharacterVocabItem("Fork", "🍴", "Use the clean Fork to eat your tasty food.", "شوكة", phoneticSpelling = "FORK"),
                CharacterVocabItem("Foot", "🦶", "Stomp your Foot happily on the ground!", "قدم", phoneticSpelling = "FUUT")
            ),
            missions = listOf(
                CharacterMission("m_f1", "Find the Pink Flower!", "Help Fish find the sweet blooming Flower!", "Flower", "🌸", listOf("Flower", "Tree", "Cloud"), listOf("🌸", "🌳", "☁️"), 0),
                CharacterMission("m_f2", "Find the Jumping Frog!", "Fish wants to say hello to the jumpy Frog!", "Frog", "🐸", listOf("Duck", "Frog", "Cat"), listOf("🦆", "🐸", "🐱"), 1),
                CharacterMission("m_f3", "Beginning Sound /f/", "Which dining item starts with letter F?", "Fork", "🍴", listOf("Spoon", "Plate", "Fork"), listOf("🥄", "🍽️", "🍴"), 2)
            ),
            unlockBadgeName = "Speedy Fish Swimmer"
        ),

        // G -> Giraffe (Golden yellow spotted 3D giraffe formed into G)
        LetterCharacter(
            letter = 'G',
            name = "Giraffe",
            characterEmoji = "🦒",
            themeColorHex = 0xFFD97706, // Safari Amber
            secondaryColorHex = 0xFFF59E0B,
            phonicsSound = "/g/",
            phonicsExample = "G says /g/ as in Giraffe & Garden!",
            personality = "Tall, friendly, and sees everything from up high!",
            greetingSpeech = "Hello down there! I'm Giraffe! 'G' is for Giraffe! /g/ /g/ Giraffe! Look how tall I am!",
            storyIntro = "Giraffe has a super long neck and can spot green grass and sweet grapes from high above!",
            vocabulary = listOf(
                CharacterVocabItem("Giraffe", "🦒", "The tall spotted Giraffe reaches high leaves.", "زرافة", phoneticSpelling = "juh-RAF"),
                CharacterVocabItem("Grass", "🌱", "Green fresh Grass grows in the meadow.", "عشب", phoneticSpelling = "GRAS"),
                CharacterVocabItem("Girl", "👧", "The happy Girl plays with her friends.", "بنت", phoneticSpelling = "GURL"),
                CharacterVocabItem("Gift", "🎁", "Unwrap the shiny surprise Gift box.", "هدية", phoneticSpelling = "GIFT"),
                CharacterVocabItem("Grape", "🍇", "Purple Grapes are juicy and sweet.", "عنب", phoneticSpelling = "GRAYP")
            ),
            missions = listOf(
                CharacterMission("m_g1", "Find the Green Grass!", "Help Giraffe find fresh green Grass to munch on!", "Grass", "🌱", listOf("Grass", "Sun", "Water"), listOf("🌱", "☀️", "💧"), 0),
                CharacterMission("m_g2", "Find the Surprise Gift!", "Giraffe brought a special surprise Gift for you!", "Gift", "🎁", listOf("Book", "Gift", "Hat"), listOf("📖", "🎁", "🎩"), 1),
                CharacterMission("m_g3", "Beginning Sound /g/", "Which yummy fruit starts with letter G?", "Grape", "🍇", listOf("Apple", "Banana", "Grape"), listOf("🍎", "🍌", "🍇"), 2)
            ),
            unlockBadgeName = "High-Flying Giraffe Scout"
        ),

        // H -> Horse (Chestnut brown 3D horse figurine formed into H)
        LetterCharacter(
            letter = 'H',
            name = "Horse",
            characterEmoji = "🐴",
            themeColorHex = 0xFF92400E, // Chestnut Brown
            secondaryColorHex = 0xFFB45309,
            phonicsSound = "/h/",
            phonicsExample = "H says /h/ as in Horse!",
            personality = "Brave, speedy, and loyal companion!",
            greetingSpeech = "Neigh! I'm Horse! 'H' is for Horse! /h/ /h/ Horse! Let's gallop across the field!",
            storyIntro = "Horse loves galloping through the farm and finding cozy houses and warm hats!",
            vocabulary = listOf(
                CharacterVocabItem("Horse", "🐴", "The brown Horse gallops fast across the field.", "حصان", phoneticSpelling = "HORS"),
                CharacterVocabItem("House", "🏡", "A cozy warm House for the whole family.", "منزل", phoneticSpelling = "HOWS"),
                CharacterVocabItem("Hat", "🎩", "Wear the cool Hat to shield your eyes.", "قبعة", phoneticSpelling = "HAT"),
                CharacterVocabItem("Hand", "✋", "Wave your Hand high to say hello!", "يد", phoneticSpelling = "HAND"),
                CharacterVocabItem("Heart", "❤️", "My Heart is full of love and happiness.", "قلب", phoneticSpelling = "HAHRT")
            ),
            missions = listOf(
                CharacterMission("m_h1", "Find the Cozy House!", "Help Horse gallop home to the cozy House!", "House", "🏡", listOf("House", "Car", "Tree"), listOf("🏡", "🚗", "🌳"), 0),
                CharacterMission("m_h2", "Find the Cool Hat!", "Horse wants to wear a fancy top Hat!", "Hat", "🎩", listOf("Shoe", "Hat", "Bag"), listOf("👟", "🎩", "🎒"), 1),
                CharacterMission("m_h3", "Beginning Sound /h/", "Which body part starts with letter H?", "Hand", "✋", listOf("Foot", "Eye", "Hand"), listOf("🦶", "👁️", "✋"), 2)
            ),
            unlockBadgeName = "Galloping Horse Champion"
        ),

        // I -> Ice Cream (Waffle cone with pink strawberry scoop & sprinkles formed into I)
        LetterCharacter(
            letter = 'I',
            name = "Ice Cream",
            characterEmoji = "🍦",
            themeColorHex = 0xFFEC4899, // Strawberry Pink
            secondaryColorHex = 0xFFF472B6,
            phonicsSound = "/aɪ/",
            phonicsExample = "I says /aɪ/ & /ɪ/ as in Ice Cream & Igloo!",
            personality = "Cool, sweet, and always brings smiles!",
            greetingSpeech = "Yum! I'm Ice Cream! 'I' is for Ice Cream! /aɪ/ /aɪ/ Ice Cream! Cool and sweet!",
            storyIntro = "Ice Cream is on a sunny island adventure looking for frozen igloos and tiny insects!",
            vocabulary = listOf(
                CharacterVocabItem("Ice Cream", "🍦", "Cold strawberry Ice Cream is delicious!", "مثلجات", phoneticSpelling = "EYES-kreem"),
                CharacterVocabItem("Island", "🏝️", "A tropical Island surrounded by blue sea.", "جزيرة", phoneticSpelling = "EYE-luhnd"),
                CharacterVocabItem("Igloo", "🛖", "The snowy white Igloo is cozy inside.", "كوخ جليدي", phoneticSpelling = "IG-loo"),
                CharacterVocabItem("Insect", "🐞", "The spotted little ladybug Insect crawls.", "حشرة", phoneticSpelling = "IN-sekt"),
                CharacterVocabItem("Iron", "🧲", "The strong magnet Iron attracts little pins.", "مغناطيس", phoneticSpelling = "EYE-urn")
            ),
            missions = listOf(
                CharacterMission("m_i1", "Find the Tropical Island!", "Fly with Ice Cream to the sunny Island!", "Island", "🏝️", listOf("Island", "House", "City"), listOf("🏝️", "🏡", "🏙️"), 0),
                CharacterMission("m_i2", "Find the Snowy Igloo!", "Ice Cream wants to visit the cold white Igloo!", "Igloo", "🛖", listOf("Tent", "Igloo", "Castle"), listOf("⛺", "🛖", "🏰"), 1),
                CharacterMission("m_i3", "Beginning Sound /ɪ/", "Which tiny creature starts with letter I?", "Insect", "🐞", listOf("Bird", "Frog", "Insect"), listOf("🐦", "🐸", "🐞"), 2)
            ),
            unlockBadgeName = "Sweet Ice Cream Master"
        ),

        // J -> Jellyfish (Lavender pastel 3D jellyfish with wavy tentacles formed into J)
        LetterCharacter(
            letter = 'J',
            name = "Jellyfish",
            characterEmoji = "🪼",
            themeColorHex = 0xFF8B5CF6, // Lavender Purple
            secondaryColorHex = 0xFFA78BFA,
            phonicsSound = "/dʒ/",
            phonicsExample = "J says /dʒ/ as in Jellyfish!",
            personality = "Gentle, calm, and loves floating gracefully!",
            greetingSpeech = "Float float! I'm Jellyfish! 'J' is for Jellyfish! /dʒ/ /dʒ/ Jellyfish! Look at my tentacles!",
            storyIntro = "Jellyfish is floating through ocean currents finding sweet juice and warm winter jackets!",
            vocabulary = listOf(
                CharacterVocabItem("Jellyfish", "🪼", "The purple Jellyfish floats in calm waves.", "قنديل البحر", phoneticSpelling = "JEL-ee-fish"),
                CharacterVocabItem("Juice", "🧃", "Fresh orange Juice is cold and fruity.", "عصير", phoneticSpelling = "JOOS"),
                CharacterVocabItem("Jump", "🦘", "Jump high up into the air with joy!", "يقفز", phoneticSpelling = "JUHMP"),
                CharacterVocabItem("Jam", "🍯", "Sweet strawberry Jam on morning toast.", "مربى", phoneticSpelling = "JAM"),
                CharacterVocabItem("Jacket", "🧥", "Wear your warm blue Jacket when it is cold.", "سترة", phoneticSpelling = "JAK-it")
            ),
            missions = listOf(
                CharacterMission("m_j1", "Find the Fruity Juice!", "Help Jellyfish sip a box of orange Juice!", "Juice", "🧃", listOf("Juice", "Water", "Milk"), listOf("🧃", "💧", "🥛"), 0),
                CharacterMission("m_j2", "Find the Warm Jacket!", "Jellyfish wants to bundle up in a warm Jacket!", "Jacket", "🧥", listOf("Hat", "Jacket", "Shoe"), listOf("🎩", "🧥", "👟"), 1),
                CharacterMission("m_j3", "Beginning Sound /dʒ/", "Which breakfast spread starts with letter J?", "Jam", "🍯", listOf("Bread", "Cake", "Jam"), listOf("🍞", "🎂", "🍯"), 2)
            ),
            unlockBadgeName = "Floating Jellyfish Star"
        ),

        // K -> Koala (Soft fuzzy grey 3D koala hugging letter K)
        LetterCharacter(
            letter = 'K',
            name = "Koala",
            characterEmoji = "🐨",
            themeColorHex = 0xFF64748B, // Slate Grey
            secondaryColorHex = 0xFF94A3B8,
            phonicsSound = "/k/",
            phonicsExample = "K says /k/ as in Koala!",
            personality = "Cuddly, peaceful, and loves tree climbing!",
            greetingSpeech = "G'day! I'm Koala! 'K' is for Koala! /k/ /k/ Koala! I love eucalyptus leaves!",
            storyIntro = "Koala is high up in the eucalyptus tree looking for colorful kites and golden keys!",
            vocabulary = listOf(
                CharacterVocabItem("Koala", "🐨", "The fuzzy grey Koala hugs the tree trunk.", "كوالا", phoneticSpelling = "koh-AH-luh"),
                CharacterVocabItem("Kite", "🪁", "The diamond Kite flies high in the breezy sky.", "طائرة ورقية", phoneticSpelling = "KYTE"),
                CharacterVocabItem("Key", "🔑", "The golden Key unlocks the treasure chest.", "مفتاح", phoneticSpelling = "KEE"),
                CharacterVocabItem("Kangaroo", "🦘", "The brown Kangaroo leaps across the field.", "كنغر", phoneticSpelling = "kang-guh-ROO"),
                CharacterVocabItem("King", "👑", "The noble King wears a shining crown.", "ملك", phoneticSpelling = "KING")
            ),
            missions = listOf(
                CharacterMission("m_k1", "Find the Flying Kite!", "Help Koala fly the colorful diamond Kite!", "Kite", "🪁", listOf("Kite", "Ball", "Bird"), listOf("🪁", "⚽", "🐦"), 0),
                CharacterMission("m_k2", "Find the Golden Key!", "Koala found a secret lock and needs the Key!", "Key", "🔑", listOf("Book", "Key", "Door"), listOf("📖", "🔑", "🚪"), 1),
                CharacterMission("m_k3", "Beginning Sound /k/", "Which hopping animal starts with letter K?", "Kangaroo", "🦘", listOf("Dog", "Frog", "Kangaroo"), listOf("🐶", "🐸", "🦘"), 2)
            ),
            unlockBadgeName = "Kind Koala Climber"
        ),

        // L -> Lion (Majestic golden 3D lion with fluffy mane formed into L)
        LetterCharacter(
            letter = 'L',
            name = "Lion",
            characterEmoji = "🦁",
            themeColorHex = 0xFFF59E0B, // Golden Yellow
            secondaryColorHex = 0xFFFBBF24,
            phonicsSound = "/l/",
            phonicsExample = "L says /l/ as in Lion!",
            personality = "Brave, royal, and has a mighty roar!",
            greetingSpeech = "Roar! I'm Lion, your learning king! 'L' is for Lion! /l/ /l/ Lion! Let's be brave!",
            storyIntro = "Lion is leading the savanna expedition to discover green leaves, bright lamps, and sour lemons!",
            vocabulary = listOf(
                CharacterVocabItem("Lion", "🦁", "The brave Lion has a magnificent golden mane.", "أسد", phoneticSpelling = "LY-uhn"),
                CharacterVocabItem("Leaf", "🍃", "A fluttering green Leaf dances in the wind.", "ورقة شجر", phoneticSpelling = "LEEF"),
                CharacterVocabItem("Lamp", "💡", "Turn on the bright Lamp to read your book.", "مصباح", phoneticSpelling = "LAMP"),
                CharacterVocabItem("Lemon", "🍋", "The yellow Lemon is sour and refreshing.", "ليمون", phoneticSpelling = "LEM-uhn"),
                CharacterVocabItem("Leg", "🦵", "I use my strong Leg to run and jump!", "ساق", phoneticSpelling = "LEG")
            ),
            missions = listOf(
                CharacterMission("m_l1", "Find the Green Leaf!", "Help Lion catch the falling green Leaf!", "Leaf", "🍃", listOf("Leaf", "Flower", "Grass"), listOf("🍃", "🌸", "🌱"), 0),
                CharacterMission("m_l2", "Find the Bright Lamp!", "Lion wants to light up the cozy room with a Lamp!", "Lamp", "💡", listOf("Sun", "Lamp", "Star"), listOf("☀️", "💡", "⭐"), 1),
                CharacterMission("m_l3", "Beginning Sound /l/", "Which yellow citrus starts with letter L?", "Lemon", "🍋", listOf("Apple", "Grape", "Lemon"), listOf("🍎", "🍇", "🍋"), 2)
            ),
            unlockBadgeName = "Mighty Lion King"
        ),

        // M -> Monkey (Brown 3D monkey with cute ears and curling tail formed into M)
        LetterCharacter(
            letter = 'M',
            name = "Monkey",
            characterEmoji = "🐵",
            themeColorHex = 0xFFB45309, // Monkey Brown
            secondaryColorHex = 0xFFD97706,
            phonicsSound = "/m/",
            phonicsExample = "M says /m/ as in Monkey!",
            personality = "Playful, funny, and loves swinging from vine to vine!",
            greetingSpeech = "Ooh ooh aah aah! I'm Monkey! 'M' is for Monkey! /m/ /m/ Monkey! Let's swing!",
            storyIntro = "Monkey is swinging through the jungle canopy to gaze at the glowing moon and drink fresh milk!",
            vocabulary = listOf(
                CharacterVocabItem("Monkey", "🐵", "The clever Monkey swings happily from vines.", "قرد", phoneticSpelling = "MUHNG-kee"),
                CharacterVocabItem("Moon", "🌙", "The crescent Moon glows softly in the night sky.", "قمر", phoneticSpelling = "MOON"),
                CharacterVocabItem("Milk", "🥛", "Drink healthy white Milk to grow strong.", "حليب", phoneticSpelling = "MILK"),
                CharacterVocabItem("Mouse", "🐭", "The little Mouse nibbles a piece of cheese.", "فأر", phoneticSpelling = "MOWS"),
                CharacterVocabItem("Mango", "🥭", "The sweet juicy Mango is tropical and delicious.", "مانجو", phoneticSpelling = "MANG-goh")
            ),
            missions = listOf(
                CharacterMission("m_m1", "Find the Glowing Moon!", "Help Monkey point to the beautiful Moon!", "Moon", "🌙", listOf("Moon", "Sun", "Cloud"), listOf("🌙", "☀️", "☁️"), 0),
                CharacterMission("m_m2", "Find the Fresh Milk!", "Monkey wants a cup of healthy white Milk!", "Milk", "🥛", listOf("Juice", "Milk", "Water"), listOf("🧃", "🥛", "💧"), 1),
                CharacterMission("m_m3", "Beginning Sound /m/", "Which tropical fruit starts with letter M?", "Mango", "🥭", listOf("Banana", "Apple", "Mango"), listOf("🍌", "🍎", "🥭"), 2)
            ),
            unlockBadgeName = "Jumping Jungle Monkey"
        ),

        // N -> Nest (Textured twig nest with shiny blue eggs formed into N)
        LetterCharacter(
            letter = 'N',
            name = "Nest",
            characterEmoji = "🪺",
            themeColorHex = 0xFF78350F, // Twig Brown
            secondaryColorHex = 0xFF92400E,
            phonicsSound = "/n/",
            phonicsExample = "N says /n/ as in Nest!",
            personality = "Warm, cozy, and nurturing!",
            greetingSpeech = "Tweet tweet! I'm Nest! 'N' is for Nest! /n/ /n/ Nest! Home for little baby birds!",
            storyIntro = "Nest is sitting safely high in the oak tree keeping eggs warm and looking at starry night skies!",
            vocabulary = listOf(
                CharacterVocabItem("Nest", "🪺", "The cozy bird Nest protects tiny blue eggs.", "عش", phoneticSpelling = "NEST"),
                CharacterVocabItem("Nose", "👃", "I use my Nose to smell sweet flowers.", "أنف", phoneticSpelling = "NOHZ"),
                CharacterVocabItem("Nut", "🥜", "The crunchy Nut is a squirrel's favorite snack.", "بندقة", phoneticSpelling = "NUHT"),
                CharacterVocabItem("Net", "🥅", "The soccer Net catches the winning goal!", "شبكة", phoneticSpelling = "NET"),
                CharacterVocabItem("Night", "🌌", "Twinkling stars shine brightly at Night.", "ليل", phoneticSpelling = "NYTE")
            ),
            missions = listOf(
                CharacterMission("m_n1", "Find the Crunchy Nut!", "Help Nest find the crunchy snack Nut!", "Nut", "🥜", listOf("Nut", "Apple", "Cake"), listOf("🥜", "🍎", "🎂"), 0),
                CharacterMission("m_n2", "Find the Soccer Net!", "Nest wants to score a goal into the Net!", "Net", "🥅", listOf("Ball", "Net", "Shoe"), listOf("⚽", "🥅", "👟"), 1),
                CharacterMission("m_n3", "Beginning Sound /n/", "Which face part starts with letter N?", "Nose", "👃", listOf("Eye", "Ear", "Nose"), listOf("👁️", "👂", "👃"), 2)
            ),
            unlockBadgeName = "Cozy Nest Guardian"
        ),

        // O -> Owl (Sky blue feathered 3D owl with big bright intelligent eyes formed into O)
        LetterCharacter(
            letter = 'O',
            name = "Owl",
            characterEmoji = "🦉",
            themeColorHex = 0xFF0284C7, // Wise Owl Blue
            secondaryColorHex = 0xFF38BDF8,
            phonicsSound = "/ɒ/",
            phonicsExample = "O says /ɒ/ as in Owl & Orange!",
            personality = "Smart, calm, and loves night wisdom!",
            greetingSpeech = "Hoo hoo! I'm Owl! 'O' is for Owl! /ɒ/ /ɒ/ Owl! I have big wise eyes!",
            storyIntro = "Owl sits on the moonlit branch looking for juicy oranges and ocean octopuses!",
            vocabulary = listOf(
                CharacterVocabItem("Owl", "🦉", "The wise Owl hoots gently in the night.", "بومة", phoneticSpelling = "OWL"),
                CharacterVocabItem("Orange", "🍊", "The round Orange is citrusy and full of vitamin C.", "برتقالة", phoneticSpelling = "OR-inj"),
                CharacterVocabItem("Octopus", "🐙", "The smart Octopus has eight flexible arms.", "أخطبوط", phoneticSpelling = "AHK-tuh-puhs"),
                CharacterVocabItem("Onion", "🧅", "The purple Onion adds flavor to cooking.", "بصل", phoneticSpelling = "UHN-yuhn"),
                CharacterVocabItem("Ocean", "🌊", "The vast blue Ocean is filled with sea life.", "محيط", phoneticSpelling = "OH-shuhn")
            ),
            missions = listOf(
                CharacterMission("m_o1", "Find the Juicy Orange!", "Help Owl find the round sweet Orange!", "Orange", "🍊", listOf("Orange", "Grape", "Lemon"), listOf("🍊", "🍇", "🍋"), 0),
                CharacterMission("m_o2", "Find the Eight-Armed Octopus!", "Owl wants to spot the swimming Octopus!", "Octopus", "🐙", listOf("Fish", "Octopus", "Duck"), listOf("🐟", "🐙", "🦆"), 1),
                CharacterMission("m_o3", "Beginning Sound /ɒ/", "Which vast blue water starts with letter O?", "Ocean", "🌊", listOf("River", "Rain", "Ocean"), listOf("🏞️", "🌧️", "🌊"), 2)
            ),
            unlockBadgeName = "Wise Owl Scholar"
        ),

        // P -> Penguin (Tuxedo black and white 3D penguin with orange beak formed into P)
        LetterCharacter(
            letter = 'P',
            name = "Penguin",
            characterEmoji = "🐧",
            themeColorHex = 0xFF1E293B, // Tuxedo Slate
            secondaryColorHex = 0xFF475569,
            phonicsSound = "/p/",
            phonicsExample = "P says /p/ as in Penguin!",
            personality = "Playful, polite, and loves sliding on ice!",
            greetingSpeech = "Waddle waddle! I'm Penguin! 'P' is for Penguin! /p/ /p/ Penguin! Let's slide on ice!",
            storyIntro = "Penguin is sliding across snowy glaciers to deliver warm pizza and write with colorful pencils!",
            vocabulary = listOf(
                CharacterVocabItem("Penguin", "🐧", "The cute Penguin waddles across the white snow.", "بطريق", phoneticSpelling = "PENG-gwin"),
                CharacterVocabItem("Pizza", "🍕", "Hot cheesy Pizza with yummy tomato toppings.", "بيتزا", phoneticSpelling = "PEET-suh"),
                CharacterVocabItem("Pencil", "✏️", "Use the sharp Pencil to draw and write letters.", "قلم رصاص", phoneticSpelling = "PEN-suhl"),
                CharacterVocabItem("Panda", "🐼", "The black and white Panda munches green bamboo.", "باندا", phoneticSpelling = "PAN-duh"),
                CharacterVocabItem("Pear", "🍐", "The juicy green Pear is sweet and crunchy.", "كمثرى", phoneticSpelling = "PAIR")
            ),
            missions = listOf(
                CharacterMission("m_p1", "Find the Cheesy Pizza!", "Help Penguin deliver the hot slice of Pizza!", "Pizza", "🍕", listOf("Pizza", "Bread", "Cake"), listOf("🍕", "🍞", "🎂"), 0),
                CharacterMission("m_p2", "Find the Writing Pencil!", "Penguin needs a Pencil to practice writing letters!", "Pencil", "✏️", listOf("Book", "Pencil", "Bag"), listOf("📖", "✏️", "🎒"), 1),
                CharacterMission("m_p3", "Beginning Sound /p/", "Which bamboo-eating animal starts with letter P?", "Panda", "🐼", listOf("Koala", "Bear", "Panda"), listOf("🐨", "🐻", "🐼"), 2)
            ),
            unlockBadgeName = "Polite Penguin Explorer"
        ),

        // Q -> Queen (Royal purple 3D crowned queen with pearl necklace formed into Q)
        LetterCharacter(
            letter = 'Q',
            name = "Queen",
            characterEmoji = "👸",
            themeColorHex = 0xFF7E22CE, // Royal Purple
            secondaryColorHex = 0xFFA855F7,
            phonicsSound = "/kw/",
            phonicsExample = "Q says /kw/ as in Queen!",
            personality = "Graceful, kind, and royal leader!",
            greetingSpeech = "Greetings! I am Queen! 'Q' is for Queen! /kw/ /kw/ Queen! Welcome to my castle!",
            storyIntro = "Queen rules the kingdom with kindness and is writing royal decrees with her feathered quill!",
            vocabulary = listOf(
                CharacterVocabItem("Queen", "👸", "The gracious Queen wears a sparkling gold crown.", "ملكة", phoneticSpelling = "KWEEN"),
                CharacterVocabItem("Quiet", "🤫", "Shh! Be Quiet while the baby sleeps peacefully.", "هادئ", phoneticSpelling = "KWY-uht"),
                CharacterVocabItem("Quill", "🪶", "The feather Quill writes fancy cursive letters.", "ريشة كتابة", phoneticSpelling = "KWIL"),
                CharacterVocabItem("Quick", "⚡", "The cheetah is Quick like a lightning flash!", "سريع", phoneticSpelling = "KWIK"),
                CharacterVocabItem("Quilt", "🧵", "The colorful patchwork Quilt keeps us warm.", "لحاف", phoneticSpelling = "KWILT")
            ),
            missions = listOf(
                CharacterMission("m_q1", "Find the Feather Quill!", "Help Queen find her writing Quill pen!", "Quill", "🪶", listOf("Quill", "Pencil", "Book"), listOf("🪶", "✏️", "📖"), 0),
                CharacterMission("m_q2", "Find the Warm Quilt!", "Queen wants to tuck in with a cozy patchwork Quilt!", "Quilt", "🧵", listOf("Blanket", "Quilt", "Shirt"), listOf("🧶", "🧵", "👕"), 1),
                CharacterMission("m_q3", "Beginning Sound /kw/", "Which sign means 'Shh' starting with Q?", "Quiet", "🤫", listOf("Stop", "Go", "Quiet"), listOf("🛑", "🟢", "🤫"), 2)
            ),
            unlockBadgeName = "Royal Queen Crown"
        ),

        // R -> Rabbit (Snow white fluffy 3D bunny with pink long ears formed into R)
        LetterCharacter(
            letter = 'R',
            name = "Rabbit",
            characterEmoji = "🐰",
            themeColorHex = 0xFFDB2777, // Rosy Pink
            secondaryColorHex = 0xFFF472B6,
            phonicsSound = "/r/",
            phonicsExample = "R says /r/ as in Rabbit!",
            personality = "Fast, cheerful, and loves hopping around!",
            greetingSpeech = "Hop hop! I'm Rabbit! 'R' is for Rabbit! /r/ /r/ Rabbit! Let's hop together!",
            storyIntro = "Rabbit is hopping through the flower garden to chase beautiful rainbows and launch rockets!",
            vocabulary = listOf(
                CharacterVocabItem("Rabbit", "🐰", "The white Rabbit hops fast with long pink ears.", "أرنب", phoneticSpelling = "RAB-it"),
                CharacterVocabItem("Rainbow", "🌈", "A vibrant Rainbow arches across the blue sky.", "قوس قزح", phoneticSpelling = "RAYN-boh"),
                CharacterVocabItem("Rocket", "🚀", "3.. 2.. 1.. Blast off! The fast space Rocket.", "صاروخ", phoneticSpelling = "RAHK-it"),
                CharacterVocabItem("Ring", "💍", "The shining gold Ring sparkles in the light.", "خاتم", phoneticSpelling = "RING"),
                CharacterVocabItem("Rose", "🌹", "The fragrant red Rose blooms in the garden.", "وردة", phoneticSpelling = "ROHZ")
            ),
            missions = listOf(
                CharacterMission("m_r1", "Find the Colorful Rainbow!", "Help Rabbit look up and find the shining Rainbow!", "Rainbow", "🌈", listOf("Rainbow", "Sun", "Cloud"), listOf("🌈", "☀️", "☁️"), 0),
                CharacterMission("m_r2", "Find the Fast Rocket!", "Rabbit wants to count down and launch the Rocket!", "Rocket", "🚀", listOf("Car", "Rocket", "Airplane"), listOf("🚗", "🚀", "✈️"), 1),
                CharacterMission("m_r3", "Beginning Sound /r/", "Which garden flower starts with letter R?", "Rose", "🌹", listOf("Tree", "Grass", "Rose"), listOf("🌳", "🌱", "🌹"), 2)
            ),
            unlockBadgeName = "Joyful Hopping Rabbit"
        ),

        // S -> Snake (Emerald green spotted 3D friendly snake curved into S)
        LetterCharacter(
            letter = 'S',
            name = "Snake",
            characterEmoji = "🐍",
            themeColorHex = 0xFF16A34A, // Emerald Green
            secondaryColorHex = 0xFF22C55E,
            phonicsSound = "/s/",
            phonicsExample = "S says /s/ as in Snake & Sun!",
            personality = "Silly, friendly, and loves sliding smoothly!",
            greetingSpeech = "Sssss! I'm Snake! 'S' is for Snake! /s/ /s/ Snake! Slither and smile!",
            storyIntro = "Snake is sliding along the sunny sandy beach looking for the bright sun and twinkling stars!",
            vocabulary = listOf(
                CharacterVocabItem("Snake", "🐍", "The green spotted Snake slithers smoothly.", "ثعبان", phoneticSpelling = "SNAYK"),
                CharacterVocabItem("Sun", "☀️", "The bright yellow Sun warms our beautiful day.", "شمس", phoneticSpelling = "SUHN"),
                CharacterVocabItem("Star", "⭐", "A glittering gold Star shines in the evening sky.", "نجمة", phoneticSpelling = "STAHR"),
                CharacterVocabItem("Shoe", "👟", "Tie the blue running Shoe on your foot.", "حذاء", phoneticSpelling = "SHOO"),
                CharacterVocabItem("Ship", "🚢", "The grand Ship sails across the deep ocean.", "سفينة", phoneticSpelling = "SHIP")
            ),
            missions = listOf(
                CharacterMission("m_s1", "Find the Bright Sun!", "Help Snake bask under the warm glowing Sun!", "Sun", "☀️", listOf("Sun", "Moon", "Cloud"), listOf("☀️", "🌙", "☁️"), 0),
                CharacterMission("m_s2", "Find the Twinkling Star!", "Snake wants to wish upon a twinkling gold Star!", "Star", "⭐", listOf("Heart", "Star", "Circle"), listOf("❤️", "⭐", "🔵"), 1),
                CharacterMission("m_s3", "Beginning Sound /s/", "Which footwear starts with letter S?", "Shoe", "👟", listOf("Hat", "Shirt", "Shoe"), listOf("🎩", "👕", "👟"), 2)
            ),
            unlockBadgeName = "Smooth Slithering Snake"
        ),

        // T -> Tiger (Vibrant orange striped 3D tiger cub face formed into T)
        LetterCharacter(
            letter = 'T',
            name = "Tiger",
            characterEmoji = "🐯",
            themeColorHex = 0xFFEA580C, // Tiger Orange
            secondaryColorHex = 0xFFF97316,
            phonicsSound = "/t/",
            phonicsExample = "T says /t/ as in Tiger!",
            personality = "Energetic, adventurous, and loves running fast!",
            greetingSpeech = "Grrr-eat! I'm Tiger! 'T' is for Tiger! /t/ /t/ Tiger! Let's explore the jungle!",
            storyIntro = "Tiger is exploring the jungle trails discovering tall green trees and riding the toy train!",
            vocabulary = listOf(
                CharacterVocabItem("Tiger", "🐯", "The playful orange Tiger has black stripes.", "نمر", phoneticSpelling = "TY-gur"),
                CharacterVocabItem("Tree", "🌳", "The tall green Tree gives shade on sunny days.", "شجرة", phoneticSpelling = "TREE"),
                CharacterVocabItem("Train", "🚂", "Choo choo! The steam Train chugs along tracks.", "قطار", phoneticSpelling = "TRAYN"),
                CharacterVocabItem("Table", "🪵", "We set our plates and cups on the wooden Table.", "طاولة", phoneticSpelling = "TAY-buhl"),
                CharacterVocabItem("Tomato", "🍅", "The juicy red Tomato is fresh from the garden.", "طماطم", phoneticSpelling = "tuh-MAY-toh")
            ),
            missions = listOf(
                CharacterMission("m_t1", "Find the Tall Tree!", "Help Tiger climb up to the top of the Tree!", "Tree", "🌳", listOf("Tree", "Flower", "Grass"), listOf("🌳", "🌸", "🌱"), 0),
                CharacterMission("m_t2", "Find the Choo-Choo Train!", "Tiger wants to ride the fast steam Train!", "Train", "🚂", listOf("Car", "Train", "Rocket"), listOf("🚗", "🚂", "🚀"), 1),
                CharacterMission("m_t3", "Beginning Sound /t/", "Which garden veggie starts with letter T?", "Tomato", "🍅", listOf("Apple", "Banana", "Tomato"), listOf("🍎", "🍌", "🍅"), 2)
            ),
            unlockBadgeName = "Tiger Adventure Champion"
        ),

        // U -> Umbrella (Rainbow multi-color curved 3D umbrella formed into U)
        LetterCharacter(
            letter = 'U',
            name = "Umbrella",
            characterEmoji = "☂️",
            themeColorHex = 0xFF0284C7, // Ocean Cyan
            secondaryColorHex = 0xFF06B6D4,
            phonicsSound = "/ʌ/",
            phonicsExample = "U says /ʌ/ as in Umbrella & Up!",
            personality = "Helpful, colorful, and shields everyone from rain!",
            greetingSpeech = "Open up! I'm Umbrella! 'U' is for Umbrella! /ʌ/ /ʌ/ Umbrella! Rain or shine, I'm here!",
            storyIntro = "Umbrella is opening its colorful canopy to help friends stay dry and find magical unicorns!",
            vocabulary = listOf(
                CharacterVocabItem("Umbrella", "☂️", "The colorful Umbrella keeps us dry in rain.", "مظلة", phoneticSpelling = "uhm-BREL-uh"),
                CharacterVocabItem("Uniform", "🥋", "Wear the clean school Uniform with pride.", "زي موحد", phoneticSpelling = "YOO-nuh-form"),
                CharacterVocabItem("Unicorn", "🦄", "The magical white Unicorn has a glowing horn.", "وحيد القرن", phoneticSpelling = "YOO-nuh-korn"),
                CharacterVocabItem("Up", "⬆️", "Look Up high at the clouds and birds.", "فوق", phoneticSpelling = "UHP"),
                CharacterVocabItem("Uncle", "👨", "My kind Uncle tells the funniest jokes.", "عم / خال", phoneticSpelling = "UHNG-kuhl")
            ),
            missions = listOf(
                CharacterMission("m_u1", "Find the Magical Unicorn!", "Help Umbrella fly with the magical Unicorn!", "Unicorn", "🦄", listOf("Unicorn", "Horse", "Bear"), listOf("🦄", "🐴", "🐻"), 0),
                CharacterMission("m_u2", "Point Arrow Up!", "Umbrella wants to point Up to the sky!", "Up", "⬆️", listOf("Down", "Up", "Left"), listOf("⬇️", "⬆️", "⬅️"), 1),
                CharacterMission("m_u3", "Beginning Sound /juː/", "Which outfit starts with letter U?", "Uniform", "🥋", listOf("Shirt", "Shoe", "Uniform"), listOf("👕", "👟", "🥋"), 2)
            ),
            unlockBadgeName = "Rainbow Umbrella Protector"
        ),

        // V -> Violin (Wooden classical 3D violin with strings formed into V)
        LetterCharacter(
            letter = 'V',
            name = "Violin",
            characterEmoji = "🎻",
            themeColorHex = 0xFF9A3412, // Polished Wood Rust
            secondaryColorHex = 0xFFC2410C,
            phonicsSound = "/v/",
            phonicsExample = "V says /v/ as in Violin!",
            personality = "Musical, elegant, and makes sweet melodies!",
            greetingSpeech = "La la la! I'm Violin! 'V' is for Violin! /v/ /v/ Violin! Let's make sweet music!",
            storyIntro = "Violin is performing in the grand concert hall driving the touring van and eating healthy vegetables!",
            vocabulary = listOf(
                CharacterVocabItem("Violin", "🎻", "The wooden Violin plays a sweet classical tune.", "كمان", phoneticSpelling = "vy-uh-LIN"),
                CharacterVocabItem("Van", "🚐", "The blue family Van carries everyone on trips.", "شاحنة صغيرة", phoneticSpelling = "VAN"),
                CharacterVocabItem("Vase", "🏺", "Put fresh blooming flowers in the ceramic Vase.", "مزهرية", phoneticSpelling = "VAYZ"),
                CharacterVocabItem("Vest", "🦺", "Wear the bright safety Vest when crossing streets.", "سترة نجاة", phoneticSpelling = "VEST"),
                CharacterVocabItem("Vegetable", "🥦", "Crunchy green Vegetables make you healthy!", "خضار", phoneticSpelling = "VEJ-tuh-buhl")
            ),
            missions = listOf(
                CharacterMission("m_v1", "Find the Family Van!", "Help Violin load instruments into the Van!", "Van", "🚐", listOf("Van", "Car", "Train"), listOf("🚐", "🚗", "🚂"), 0),
                CharacterMission("m_v2", "Find the Flower Vase!", "Violin wants to place roses in the pretty Vase!", "Vase", "🏺", listOf("Cup", "Vase", "Plate"), listOf("🥛", "🏺", "🍽️"), 1),
                CharacterMission("m_v3", "Beginning Sound /v/", "Which healthy food starts with letter V?", "Vegetable", "🥦", listOf("Cake", "Pizza", "Vegetable"), listOf("🎂", "🍕", "🥦"), 2)
            ),
            unlockBadgeName = "Musical Violin Virtuoso"
        ),

        // W -> Whale (Ocean blue 3D whale spouting water fountain formed into W)
        LetterCharacter(
            letter = 'W',
            name = "Whale",
            characterEmoji = "🐳",
            themeColorHex = 0xFF0284C7, // Deep Ocean Blue
            secondaryColorHex = 0xFF38BDF8,
            phonicsSound = "/w/",
            phonicsExample = "W says /w/ as in Whale & Water!",
            personality = "Grand, friendly, and spouts water joyfully!",
            greetingSpeech = "Splash splash! I'm Whale! 'W' is for Whale! /w/ /w/ Whale! Welcome to the ocean!",
            storyIntro = "Whale is swimming through deep blue waters spraying water fountains and looking through windows!",
            vocabulary = listOf(
                CharacterVocabItem("Whale", "🐳", "The gentle blue Whale spouts water in the sea.", "حوت", phoneticSpelling = "WAYL"),
                CharacterVocabItem("Water", "💧", "Drink clean fresh Water to stay healthy.", "ماء", phoneticSpelling = "WAH-ter"),
                CharacterVocabItem("Watch", "⌚", "Look at the ticking Watch to check the time.", "ساعة يد", phoneticSpelling = "WAHCH"),
                CharacterVocabItem("Window", "🪟", "Look out the clean glass Window at the birds.", "نافذة", phoneticSpelling = "WIN-doh"),
                CharacterVocabItem("Wind", "💨", "The cool gentle Wind makes the leaves dance.", "رياح", phoneticSpelling = "WIND")
            ),
            missions = listOf(
                CharacterMission("m_w1", "Find the Fresh Water!", "Help Whale spout fresh clean Water!", "Water", "💧", listOf("Water", "Milk", "Juice"), listOf("💧", "🥛", "🧃"), 0),
                CharacterMission("m_w2", "Find the Ticking Watch!", "Whale wants to know what time it is on the Watch!", "Watch", "⌚", listOf("Lamp", "Watch", "Key"), listOf("💡", "⌚", "🔑"), 1),
                CharacterMission("m_w3", "Beginning Sound /w/", "Which glass frame starts with letter W?", "Window", "🪟", listOf("Door", "Wall", "Window"), listOf("🚪", "🧱", "🪟"), 2)
            ),
            unlockBadgeName = "Gentle Whale Navigator"
        ),

        // X -> Xylophone (Rainbow colored chime bars with wooden mallets formed into X)
        LetterCharacter(
            letter = 'X',
            name = "Xylophone",
            characterEmoji = "🎼",
            themeColorHex = 0xFF7C3AED, // Violet Melody
            secondaryColorHex = 0xFFA78BFA,
            phonicsSound = "/ks/",
            phonicsExample = "X says /ks/ as in Xylophone, Box & Fox!",
            personality = "Musical, vibrant, and loves cheerful tunes!",
            greetingSpeech = "Ding dong! I'm Xylophone! 'X' is for Xylophone! /ks/ /ks/ Xylophone! Let's play chimes!",
            storyIntro = "Xylophone is tapping rainbow chimes and packing mystery boxes with clever foxes!",
            vocabulary = listOf(
                CharacterVocabItem("Xylophone", "🎼", "Tap the colorful bars on the musical Xylophone.", "إكسيلوفون", phoneticSpelling = "ZY-luh-fohn"),
                CharacterVocabItem("X-ray", "🩻", "The medical X-ray picture sees our strong bones.", "أشعة سينية", phoneticSpelling = "EKS-ray"),
                CharacterVocabItem("Box", "📦", "Open the cardboard Box to find your toy.", "صندوق", phoneticSpelling = "BAHKS"),
                CharacterVocabItem("Fox", "🦊", "The clever orange Fox has a bushy tail.", "ثعلب", phoneticSpelling = "FAHKS"),
                CharacterVocabItem("Six", "6️⃣", "Count your fingers: one, two, three, four, five, Six!", "ستة", phoneticSpelling = "SIKS")
            ),
            missions = listOf(
                CharacterMission("m_x1", "Find the Medical X-ray!", "Help Xylophone view the shiny bone X-ray!", "X-ray", "🩻", listOf("X-ray", "Book", "Photo"), listOf("🩻", "📖", "🖼️"), 0),
                CharacterMission("m_x2", "Find the Cardboard Box!", "Xylophone packed instruments inside the Box!", "Box", "📦", listOf("Bag", "Box", "Cup"), listOf("🎒", "📦", "🥛"), 1),
                CharacterMission("m_x3", "Ending Sound /ks/", "Which clever animal ends with letter X sound?", "Fox", "🦊", listOf("Dog", "Cat", "Fox"), listOf("🐶", "🐱", "🦊"), 2)
            ),
            unlockBadgeName = "Rainbow Xylophone Maestro"
        ),

        // Y -> Yak (Fuzzy shaggy brown 3D yak with curved horns formed into Y)
        LetterCharacter(
            letter = 'Y',
            name = "Yak",
            characterEmoji = "🐂",
            themeColorHex = 0xFF78350F, // Shaggy Yak Brown
            secondaryColorHex = 0xFF92400E,
            phonicsSound = "/j/",
            phonicsExample = "Y says /j/ as in Yak & Yellow!",
            personality = "Strong, cozy, and loves mountain walks!",
            greetingSpeech = "Warm snort! I'm Yak! 'Y' is for Yak! /j/ /j/ Yak! I have warm shaggy fur!",
            storyIntro = "Yak is trekking across snowy mountain peaks playing with yellow yo-yos and sailing yachts!",
            vocabulary = listOf(
                CharacterVocabItem("Yak", "🐂", "The shaggy brown Yak lives in high mountains.", "ثور الياك", phoneticSpelling = "YAK"),
                CharacterVocabItem("Yellow", "🟡", "The bright Yellow lemon shines like the sun.", "أصفر", phoneticSpelling = "YEL-oh"),
                CharacterVocabItem("Yo-yo", "🪀", "Spin the spinning Yo-yo up and down on a string.", "يويو", phoneticSpelling = "YOH-yoh"),
                CharacterVocabItem("Yacht", "⛵", "The white sailing Yacht glides across the lake.", "يخت", phoneticSpelling = "YAHT"),
                CharacterVocabItem("Yarn", "🧶", "Knit a warm winter scarf from soft wool Yarn.", "خيط صوف", phoneticSpelling = "YAHRN")
            ),
            missions = listOf(
                CharacterMission("m_y1", "Find the Bright Yellow!", "Help Yak find the glowing Yellow color!", "Yellow", "🟡", listOf("Yellow", "Blue", "Red"), listOf("🟡", "🔵", "🔴"), 0),
                CharacterMission("m_y2", "Find the Spinning Yo-yo!", "Yak loves performing fun tricks with the Yo-yo!", "Yo-yo", "🪀", listOf("Ball", "Yo-yo", "Kite"), listOf("⚽", "🪀", "🪁"), 1),
                CharacterMission("m_y3", "Beginning Sound /j/", "Which cozy knitting thread starts with letter Y?", "Yarn", "🧶", listOf("Net", "Quilt", "Yarn"), listOf("🥅", "🧵", "🧶"), 2)
            ),
            unlockBadgeName = "Mountain Yak Champion"
        ),

        // Z -> Zebra (Crisp black & white striped 3D zebra formed into Z)
        LetterCharacter(
            letter = 'Z',
            name = "Zebra",
            characterEmoji = "🦓",
            themeColorHex = 0xFF1E293B, // Striped Dark Slate
            secondaryColorHex = 0xFF475569,
            phonicsSound = "/z/",
            phonicsExample = "Z says /z/ as in Zebra & Zoo!",
            personality = "Energetic, stylish, and loves zigzag racing!",
            greetingSpeech = "Whee! I'm Zebra! 'Z' is for Zebra! /z/ /z/ Zebra! Look at my cool stripes!",
            storyIntro = "Zebra is galloping through the safari zoo zip-zipping zippers and running in fun zigzags!",
            vocabulary = listOf(
                CharacterVocabItem("Zebra", "🦓", "The swift Zebra has beautiful black and white stripes.", "حمار وحشي", phoneticSpelling = "ZEE-bruh"),
                CharacterVocabItem("Zoo", "🦁", "Visit all friendly animals at the animal Zoo.", "حديقة حيوان", phoneticSpelling = "ZOO"),
                CharacterVocabItem("Zero", "0️⃣", "The number Zero looks like an empty circle.", "صفر", phoneticSpelling = "ZEER-oh"),
                CharacterVocabItem("Zipper", "🤐", "Zip up your warm jacket with the metal Zipper.", "سحاب", phoneticSpelling = "ZIP-er"),
                CharacterVocabItem("Zigzag", "⚡", "The lightning bolt zigzags across the sky.", "متعرج", phoneticSpelling = "ZIG-zag")
            ),
            missions = listOf(
                CharacterMission("m_z1", "Find the Animal Zoo!", "Help Zebra guide all safari friends to the Zoo!", "Zoo", "🦁", listOf("Zoo", "Park", "School"), listOf("🦁", "🏞️", "🏫"), 0),
                CharacterMission("m_z2", "Find the Jacket Zipper!", "Zebra wants to zip up the jacket with the Zipper!", "Zipper", "🤐", listOf("Button", "Zipper", "Key"), listOf("🔘", "🤐", "🔑"), 1),
                CharacterMission("m_z3", "Beginning Sound /z/", "Which starting number word means none / 0?", "Zero", "0️⃣", listOf("One", "Ten", "Zero"), listOf("1️⃣", "🔟", "0️⃣"), 2)
            ),
            unlockBadgeName = "Zippy Zebra Explorer"
        )
    )

    fun getCharacterByLetter(letter: Char): LetterCharacter {
        return characters.find { it.letter.equals(letter, ignoreCase = true) } ?: characters.first()
    }
}
