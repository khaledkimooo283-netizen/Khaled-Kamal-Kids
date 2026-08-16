package com.example.data

data class LearningVocabItem(
    val id: String,
    val word: String,
    val emoji: String,
    val category: String,
    val worldId: String,
    val phrase: String,
    val audioPrompt: String,
    val difficulty: String = "beginner",
    val colorHex: Long = 0xFF4F46E5
)

data class ActionVerbItem(
    val id: String,
    val verb: String,
    val emoji: String,
    val phrase: String,
    val actionSound: String = "fun"
)

data class WorldPhraseItem(
    val phrase: String,
    val emoji: String,
    val context: String
)

enum class LearningActivityType(val title: String, val emoji: String) {
    EXPLORE("Explore & Tap", "🔍"),
    FIND_IT("Find It!", "🎯"),
    DRAG_INTO_TARGET("Drag & Put", "🧺"),
    CHOOSE_ONE("Which One?", "❓"),
    LISTEN_CHOOSE("Listen & Choose", "🎧"),
    ACTION_FUN("Action Play", "⚡"),
    SORTING("Sort It!", "📦"),
    MEMORY_MATCH("Memory Cards", "🧠")
}

data class WorldEnvironment(
    val id: String,
    val shortName: String,
    val title: String,
    val emoji: String,
    val themeColorHex: Long,
    val accentColorHex: Long,
    val description: String,
    val leoGreeting: String,
    val vocabList: List<LearningVocabItem>,
    val verbs: List<ActionVerbItem>,
    val phrases: List<WorldPhraseItem>,
    val targetContainerName: String,
    val targetContainerEmoji: String,
    val sortCategoryA: String,
    val sortCategoryB: String
)

object LearningWorldData {

    val worlds: List<WorldEnvironment> = listOf(
        // 1. HOME
        WorldEnvironment(
            id = "home",
            shortName = "Home",
            title = "Leo's Cozy Home",
            emoji = "🏠",
            themeColorHex = 0xFFF59E0B, // Amber / Warm Orange
            accentColorHex = 0xFFFBBF24,
            description = "Explore Leo's bedroom and living room!",
            leoGreeting = "Welcome to my cozy home! Let's explore together!",
            targetContainerName = "Toy Box",
            targetContainerEmoji = "🧸",
            sortCategoryA = "Bedroom",
            sortCategoryB = "Kitchen",
            vocabList = listOf(
                LearningVocabItem("h_bed", "bed", "🛏️", "Furniture", "home", "Sleep in the warm bed.", "Bed", colorHex = 0xFF3B82F6),
                LearningVocabItem("h_pillow", "pillow", "🛋️", "Bedroom", "home", "Rest on the soft pillow.", "Pillow", colorHex = 0xFF8B5CF6),
                LearningVocabItem("h_blanket", "blanket", "🧶", "Bedroom", "home", "Warm and cozy blanket.", "Blanket", colorHex = 0xFFEC4899),
                LearningVocabItem("h_door", "door", "🚪", "House", "home", "Open the wooden door.", "Door", colorHex = 0xFFF97316),
                LearningVocabItem("h_window", "window", "🪟", "House", "home", "Look through the window.", "Window", colorHex = 0xFF06B6D4),
                LearningVocabItem("h_chair", "chair", "🪑", "Furniture", "home", "Sit on the wooden chair.", "Chair", colorHex = 0xFF10B981),
                LearningVocabItem("h_table", "table", "🪵", "Furniture", "home", "Put food on the table.", "Table", colorHex = 0xFF84CC16),
                LearningVocabItem("h_lamp", "lamp", "💡", "Objects", "home", "Turn on the bright lamp.", "Lamp", colorHex = 0xFFEAB308),
                LearningVocabItem("h_book", "book", "📖", "Objects", "home", "Read the colorful book.", "Book", colorHex = 0xFF6366F1),
                LearningVocabItem("h_toy", "toy", "🧸", "Toys", "home", "Play with the cute toy.", "Toy", colorHex = 0xFFEC4899),
                LearningVocabItem("h_ball", "ball", "⚽", "Toys", "home", "Roll the bouncy ball.", "Ball", colorHex = 0xFFEF4444),
                LearningVocabItem("h_cup", "cup", "🥛", "Kitchen", "home", "Drink from the clean cup.", "Cup", colorHex = 0xFF0EA5E9),
                LearningVocabItem("h_plate", "plate", "🍽️", "Kitchen", "home", "Put yummy food on the plate.", "Plate", colorHex = 0xFF14B8A6),
                LearningVocabItem("h_spoon", "spoon", "🥄", "Kitchen", "home", "Eat with the little spoon.", "Spoon", colorHex = 0xFF64748B),
                LearningVocabItem("h_fork", "fork", "🍴", "Kitchen", "home", "Pick fruit with the fork.", "Fork", colorHex = 0xFF94A3B8),
                LearningVocabItem("h_water", "water", "💧", "Drinks", "home", "Drink fresh clean water.", "Water", colorHex = 0xFF0284C7),
                LearningVocabItem("h_milk", "milk", "🥛", "Drinks", "home", "Drink healthy warm milk.", "Milk", colorHex = 0xFF065F46),
                LearningVocabItem("h_bread", "bread", "🍞", "Food", "home", "Eat the tasty fresh bread.", "Bread", colorHex = 0xFFD97706),
                LearningVocabItem("h_apple", "apple", "🍎", "Food", "home", "Crunch the red apple.", "Apple", colorHex = 0xFFDC2626),
                LearningVocabItem("h_bag", "bag", "🎒", "Objects", "home", "Pack the yellow bag.", "Bag", colorHex = 0xFF7C3AED),
                LearningVocabItem("h_shoe", "shoe", "👟", "Clothes", "home", "Tie the blue shoe.", "Shoe", colorHex = 0xFF2563EB),
                LearningVocabItem("h_shirt", "shirt", "👕", "Clothes", "home", "Wear the clean shirt.", "Shirt", colorHex = 0xFF059669)
            ),
            verbs = listOf(
                ActionVerbItem("hv_eat", "eat", "🍽️", "Let's eat yummy food!"),
                ActionVerbItem("hv_drink", "drink", "🥛", "Drink fresh water."),
                ActionVerbItem("hv_sleep", "sleep", "😴", "Time to sleep peacefully."),
                ActionVerbItem("hv_wake", "wake", "⏰", "Wake up, sunshine!"),
                ActionVerbItem("hv_sit", "sit", "🪑", "Sit on the chair."),
                ActionVerbItem("hv_stand", "stand", "🧍", "Stand up tall!"),
                ActionVerbItem("hv_open", "open", "🚪", "Open the door."),
                ActionVerbItem("hv_close", "close", "🔒", "Close the door gently."),
                ActionVerbItem("hv_take", "take", "🤲", "Take the apple."),
                ActionVerbItem("hv_give", "give", "🎁", "Give a warm hug."),
                ActionVerbItem("hv_look", "look", "👀", "Look around the room."),
                ActionVerbItem("hv_play", "play", "🧸", "Play with toys!")
            ),
            phrases = listOf(
                WorldPhraseItem("Good morning!", "☀️", "Greeting"),
                WorldPhraseItem("Wake up!", "⏰", "Morning"),
                WorldPhraseItem("Let's eat!", "🍽️", "Mealtime"),
                WorldPhraseItem("Drink water.", "💧", "Healthy habit"),
                WorldPhraseItem("Open the door.", "🚪", "Action"),
                WorldPhraseItem("Close the door.", "🚪", "Action"),
                WorldPhraseItem("Where is the ball?", "⚽", "Question")
            )
        ),

        // 2. GARDEN
        WorldEnvironment(
            id = "garden",
            shortName = "Garden",
            title = "Sunshine Garden",
            emoji = "🌳",
            themeColorHex = 0xFF10B981, // Emerald Green
            accentColorHex = 0xFF34D399,
            description = "Explore flowers, trees, bees, and green grass!",
            leoGreeting = "Look at the colorful flowers and buzzing bees in the garden!",
            targetContainerName = "Garden Basket",
            targetContainerEmoji = "🧺",
            sortCategoryA = "Plants",
            sortCategoryB = "Animals",
            vocabList = listOf(
                LearningVocabItem("g_tree", "tree", "🌳", "Nature", "garden", "The tall green tree.", "Tree", colorHex = 0xFF15803D),
                LearningVocabItem("g_flower", "flower", "🌸", "Nature", "garden", "Smell the sweet pink flower.", "Flower", colorHex = 0xFFEC4899),
                LearningVocabItem("g_grass", "grass", "🌱", "Nature", "garden", "Walk on the green grass.", "Grass", colorHex = 0xFF22C55E),
                LearningVocabItem("g_leaf", "leaf", "🍃", "Nature", "garden", "A falling green leaf.", "Leaf", colorHex = 0xFF16A34A),
                LearningVocabItem("g_sun", "sun", "☀️", "Sky", "garden", "The sun is bright and warm.", "Sun", colorHex = 0xFFF59E0B),
                LearningVocabItem("g_cloud", "cloud", "☁️", "Sky", "garden", "Fluffy white cloud in the sky.", "Cloud", colorHex = 0xFF94A3B8),
                LearningVocabItem("g_sky", "sky", "🌤️", "Sky", "garden", "The sky is lovely and blue.", "Sky", colorHex = 0xFF0284C7),
                LearningVocabItem("g_bird", "bird", "🐦", "Animals", "garden", "The little bird sings happily.", "Bird", colorHex = 0xFF06B6D4),
                LearningVocabItem("g_butterfly", "butterfly", "🦋", "Animals", "garden", "A colorful fluttering butterfly.", "Butterfly", colorHex = 0xFFA855F7),
                LearningVocabItem("g_bee", "bee", "🐝", "Animals", "garden", "The busy bee buzzes around.", "Bee", colorHex = 0xFFEAB308),
                LearningVocabItem("g_dog", "dog", "🐶", "Animals", "garden", "The friendly dog wags its tail.", "Dog", colorHex = 0xFFD97706),
                LearningVocabItem("g_cat", "cat", "🐱", "Animals", "garden", "The soft cat says meow.", "Cat", colorHex = 0xFFF97316),
                LearningVocabItem("g_ball", "ball", "⚽", "Toys", "garden", "Kick the soccer ball.", "Ball", colorHex = 0xFFEF4444),
                LearningVocabItem("g_swing", "swing", "🪢", "Playground", "garden", "Swing high in the air.", "Swing", colorHex = 0xFF8B5CF6),
                LearningVocabItem("g_slide", "slide", "🛝", "Playground", "garden", "Slide down the yellow slide.", "Slide", colorHex = 0xFFEAB308),
                LearningVocabItem("g_bench", "bench", "🪵", "Garden", "garden", "Sit on the wooden park bench.", "Bench", colorHex = 0xFF78350F)
            ),
            verbs = listOf(
                ActionVerbItem("gv_run", "run", "🏃", "Run across the green lawn!"),
                ActionVerbItem("gv_walk", "walk", "🚶", "Walk gently on the path."),
                ActionVerbItem("gv_jump", "jump", "🦘", "Jump high into the air!"),
                ActionVerbItem("gv_play", "play", "🎈", "Play together with friends."),
                ActionVerbItem("gv_look", "look", "👀", "Look at the pretty butterfly!"),
                ActionVerbItem("gv_touch", "touch", "🌸", "Touch the soft petal."),
                ActionVerbItem("gv_catch", "catch", "🤲", "Catch the flying ball!"),
                ActionVerbItem("gv_throw", "throw", "🥎", "Throw the ball to Leo!")
            ),
            phrases = listOf(
                WorldPhraseItem("Where is the ball?", "⚽", "Search"),
                WorldPhraseItem("Look at the bird!", "🐦", "Observation"),
                WorldPhraseItem("Touch the flower.", "🌸", "Action"),
                WorldPhraseItem("The sun is warm.", "☀️", "Nature"),
                WorldPhraseItem("Great! Ball!", "🎉", "Praise")
            )
        ),

        // 3. SUPERMARKET
        WorldEnvironment(
            id = "supermarket",
            shortName = "Supermarket",
            title = "Happy Supermarket",
            emoji = "🛒",
            themeColorHex = 0xFF0284C7, // Ocean Blue
            accentColorHex = 0xFF38BDF8,
            description = "Fill your cart with fruits, juices, and groceries!",
            leoGreeting = "Let's go shopping! Help me find healthy food for our cart!",
            targetContainerName = "Shopping Cart",
            targetContainerEmoji = "🛒",
            sortCategoryA = "Fresh Fruit",
            sortCategoryB = "Groceries",
            vocabList = listOf(
                LearningVocabItem("sm_apple", "apple", "🍎", "Fruit", "supermarket", "A crisp sweet red apple.", "Apple", colorHex = 0xFFDC2626),
                LearningVocabItem("sm_banana", "banana", "🍌", "Fruit", "supermarket", "A ripe yellow banana.", "Banana", colorHex = 0xFFF59E0B),
                LearningVocabItem("sm_orange", "orange", "🍊", "Fruit", "supermarket", "A juicy round orange.", "Orange", colorHex = 0xFFEA580C),
                LearningVocabItem("sm_milk", "milk", "🥛", "Dairy", "supermarket", "Put cold milk in the cart.", "Milk", colorHex = 0xFF0284C7),
                LearningVocabItem("sm_juice", "juice", "🧃", "Drinks", "supermarket", "Delicious fruit juice box.", "Juice", colorHex = 0xFFEC4899),
                LearningVocabItem("sm_water", "water", "💧", "Drinks", "supermarket", "A refreshing bottle of water.", "Water", colorHex = 0xFF06B6D4),
                LearningVocabItem("sm_bread", "bread", "🍞", "Bakery", "supermarket", "Warm loaf of wheat bread.", "Bread", colorHex = 0xFFD97706),
                LearningVocabItem("sm_cheese", "cheese", "🧀", "Dairy", "supermarket", "Yellow slice of cheese.", "Cheese", colorHex = 0xFFEAB308),
                LearningVocabItem("sm_egg", "egg", "🥚", "Dairy", "supermarket", "Careful with the white egg.", "Egg", colorHex = 0xFFF59E0B),
                LearningVocabItem("sm_rice", "rice", "🍚", "Grains", "supermarket", "A bag of white rice.", "Rice", colorHex = 0xFF64748B),
                LearningVocabItem("sm_box", "box", "📦", "Packaging", "supermarket", "A cardboard grocery box.", "Box", colorHex = 0xFF854D0E),
                LearningVocabItem("sm_bottle", "bottle", "🍼", "Packaging", "supermarket", "A glass juice bottle.", "Bottle", colorHex = 0xFF0284C7),
                LearningVocabItem("sm_bag", "bag", "🛍️", "Packaging", "supermarket", "A reusable shopping bag.", "Bag", colorHex = 0xFF7C3AED),
                LearningVocabItem("sm_basket", "basket", "🧺", "Tools", "supermarket", "Carry the shopping basket.", "Basket", colorHex = 0xFFD97706),
                LearningVocabItem("sm_cart", "cart", "🛒", "Tools", "supermarket", "Push the shopping cart.", "Cart", colorHex = 0xFF2563EB),
                LearningVocabItem("sm_shelf", "shelf", "🗄️", "Store", "supermarket", "Pick cereal from the shelf.", "Shelf", colorHex = 0xFF475569)
            ),
            verbs = listOf(
                ActionVerbItem("smv_buy", "buy", "💳", "Buy yummy snacks!"),
                ActionVerbItem("smv_take", "take", "🤲", "Take the apple from the shelf."),
                ActionVerbItem("smv_put", "put", "🧺", "Put the milk in the basket."),
                ActionVerbItem("smv_give", "give", "🎁", "Give the apple to Leo."),
                ActionVerbItem("smv_choose", "choose", "👉", "Choose the freshest banana."),
                ActionVerbItem("smv_find", "find", "🔍", "Find the orange!")
            ),
            phrases = listOf(
                WorldPhraseItem("Find the apple.", "🍎", "Instructions"),
                WorldPhraseItem("Put the milk in the basket.", "🧺", "Action"),
                WorldPhraseItem("Great! Apple!", "🎉", "Celebration"),
                WorldPhraseItem("Which one is a banana?", "🍌", "Quiz"),
                WorldPhraseItem("I love fresh juice!", "🧃", "Taste")
            )
        ),

        // 4. SCHOOL
        WorldEnvironment(
            id = "school",
            shortName = "School",
            title = "Fun Classroom",
            emoji = "🏫",
            themeColorHex = 0xFF6366F1, // Indigo
            accentColorHex = 0xFF818CF8,
            description = "Learn letters, books, pencils, and meet friends!",
            leoGreeting = "Ring ring! School is in session! Let's learn together!",
            targetContainerName = "School Bag",
            targetContainerEmoji = "🎒",
            sortCategoryA = "Stationery",
            sortCategoryB = "Classroom",
            vocabList = listOf(
                LearningVocabItem("sc_teacher", "teacher", "👩‍🏫", "People", "school", "The kind smiling teacher.", "Teacher", colorHex = 0xFFEC4899),
                LearningVocabItem("sc_book", "book", "📚", "Supplies", "school", "Read the colorful storybook.", "Book", colorHex = 0xFF3B82F6),
                LearningVocabItem("sc_pencil", "pencil", "✏️", "Supplies", "school", "Write with the sharp pencil.", "Pencil", colorHex = 0xFFF59E0B),
                LearningVocabItem("sc_eraser", "eraser", "🧼", "Supplies", "school", "Erase with the pink eraser.", "Eraser", colorHex = 0xFFF43F5E),
                LearningVocabItem("sc_bag", "bag", "🎒", "Supplies", "school", "Pack the school backpack.", "Bag", colorHex = 0xFF8B5CF6),
                LearningVocabItem("sc_desk", "desk", "🪑", "Furniture", "school", "Sit neatly at the student desk.", "Desk", colorHex = 0xFFD97706),
                LearningVocabItem("sc_chair", "chair", "🪑", "Furniture", "school", "Sit on the classroom chair.", "Chair", colorHex = 0xFF10B981),
                LearningVocabItem("sc_board", "board", "📋", "Classroom", "school", "Look at the blackboard.", "Board", colorHex = 0xFF15803D),
                LearningVocabItem("sc_class", "class", "🏫", "Classroom", "school", "Our happy kindergarten class.", "Class", colorHex = 0xFF6366F1),
                LearningVocabItem("sc_letter", "letter", "🔤", "Learning", "school", "Practice the alphabet letter.", "Letter", colorHex = 0xFFEA580C),
                LearningVocabItem("sc_number", "number", "🔢", "Learning", "school", "Count one, two, three.", "Number", colorHex = 0xFF0284C7),
                LearningVocabItem("sc_friend", "friend", "🤝", "People", "school", "Smile and play with your friend.", "Friend", colorHex = 0xFFE11D48)
            ),
            verbs = listOf(
                ActionVerbItem("scv_write", "write", "✍️", "Write your name nicely."),
                ActionVerbItem("scv_read", "read", "📖", "Read the story together."),
                ActionVerbItem("scv_draw", "draw", "🎨", "Draw a bright yellow sun!"),
                ActionVerbItem("scv_sit", "sit", "🪑", "Sit quietly at your desk."),
                ActionVerbItem("scv_stand", "stand", "🧍", "Stand up to sing the song!"),
                ActionVerbItem("scv_listen", "listen", "👂", "Listen to the teacher."),
                ActionVerbItem("scv_look", "look", "👀", "Look at the blackboard.")
            ),
            phrases = listOf(
                WorldPhraseItem("Good morning, teacher!", "👩‍🏫", "Polite Greeting"),
                WorldPhraseItem("Read the book.", "📖", "Learning"),
                WorldPhraseItem("Draw a star.", "⭐", "Art"),
                WorldPhraseItem("Sit on the chair.", "🪑", "Classroom Rule"),
                WorldPhraseItem("Listen carefully.", "👂", "Attention")
            )
        ),

        // 5. FARM
        WorldEnvironment(
            id = "farm",
            shortName = "Farm",
            title = "Friendly Farm",
            emoji = "🚜",
            themeColorHex = 0xFFD97706, // Amber Brown
            accentColorHex = 0xFFF59E0B,
            description = "Meet cows, horses, pigs, ducks, and tractors!",
            leoGreeting = "Old MacDonald had a farm! Look at all the happy animals!",
            targetContainerName = "Barn Cart",
            targetContainerEmoji = "🌾",
            sortCategoryA = "Animals",
            sortCategoryB = "Farm Produce",
            vocabList = listOf(
                LearningVocabItem("f_cow", "cow", "🐮", "Animals", "farm", "The spotted cow says Moo!", "Cow", colorHex = 0xFF1E293B),
                LearningVocabItem("f_sheep", "sheep", "🐑", "Animals", "farm", "The fluffy sheep says Baa!", "Sheep", colorHex = 0xFF64748B),
                LearningVocabItem("f_horse", "horse", "🐴", "Animals", "farm", "The fast horse gallops happily.", "Horse", colorHex = 0xFFB45309),
                LearningVocabItem("f_chicken", "chicken", "🐔", "Animals", "farm", "The chicken clucks in the yard.", "Chicken", colorHex = 0xFFDC2626),
                LearningVocabItem("f_duck", "duck", "🦆", "Animals", "farm", "The yellow duck says Quack!", "Duck", colorHex = 0xFF059669),
                LearningVocabItem("f_pig", "pig", "🐷", "Animals", "farm", "The pink pig rolls in the mud.", "Pig", colorHex = 0xFFF472B6),
                LearningVocabItem("f_farmer", "farmer", "👨‍🌾", "People", "farm", "The hard-working friendly farmer.", "Farmer", colorHex = 0xFF3B82F6),
                LearningVocabItem("f_barn", "barn", "🛖", "Buildings", "farm", "The big red wooden barn.", "Barn", colorHex = 0xFFB91C1C),
                LearningVocabItem("f_tractor", "tractor", "🚜", "Vehicles", "farm", "The green noisy farm tractor.", "Tractor", colorHex = 0xFF15803D),
                LearningVocabItem("f_apple", "apple", "🍎", "Produce", "farm", "Pick a red apple from the tree.", "Apple", colorHex = 0xFFEF4444),
                LearningVocabItem("f_tree", "tree", "🌳", "Produce", "farm", "An apple tree full of fruit.", "Tree", colorHex = 0xFF16A34A),
                LearningVocabItem("f_egg", "egg", "🥚", "Produce", "farm", "A fresh egg in the nest.", "Egg", colorHex = 0xFFF59E0B),
                LearningVocabItem("f_milk", "milk", "🥛", "Produce", "farm", "Fresh cold farm milk.", "Milk", colorHex = 0xFF0284C7)
            ),
            verbs = listOf(
                ActionVerbItem("fv_feed", "feed", "🌾", "Feed the hungry animals!"),
                ActionVerbItem("fv_walk", "walk", "🚶", "Walk along the dirt path."),
                ActionVerbItem("fv_run", "run", "🏃", "Run across the pasture!"),
                ActionVerbItem("fv_jump", "jump", "🦘", "Jump over the hay bale!"),
                ActionVerbItem("fv_find", "find", "🔍", "Find the hidden egg!"),
                ActionVerbItem("fv_touch", "touch", "🐑", "Touch the soft sheep.")
            ),
            phrases = listOf(
                WorldPhraseItem("Feed the cow.", "🐮", "Care"),
                WorldPhraseItem("Find the egg.", "🥚", "Quest"),
                WorldPhraseItem("The sheep is soft!", "🐑", "Touch"),
                WorldPhraseItem("The horse runs fast.", "🐴", "Speed"),
                WorldPhraseItem("Moo Moo!", "🐮", "Sound")
            )
        ),

        // 6. BEACH
        WorldEnvironment(
            id = "beach",
            shortName = "Beach",
            title = "Sunny Beach",
            emoji = "🏖️",
            themeColorHex = 0xFF06B6D4, // Cyan Sky
            accentColorHex = 0xFF22D3EE,
            description = "Play with sand, shells, waves, fish, and boats!",
            leoGreeting = "Splash splash! The sea is warm and sunny! Let's build a sandcastle!",
            targetContainerName = "Sand Bucket",
            targetContainerEmoji = "🪣",
            sortCategoryA = "Beach Gear",
            sortCategoryB = "Marine Life",
            vocabList = listOf(
                LearningVocabItem("b_sea", "sea", "🌊", "Nature", "beach", "The big blue rolling sea.", "Sea", colorHex = 0xFF0284C7),
                LearningVocabItem("b_water", "water", "💧", "Nature", "beach", "Cool splashing sea water.", "Water", colorHex = 0xFF06B6D4),
                LearningVocabItem("b_sand", "sand", "🏖️", "Nature", "beach", "Warm golden soft beach sand.", "Sand", colorHex = 0xFFF59E0B),
                LearningVocabItem("b_sun", "sun", "☀️", "Sky", "beach", "Bright sun shining down.", "Sun", colorHex = 0xFFFBBF24),
                LearningVocabItem("b_shell", "shell", "🐚", "Sea", "beach", "Find a spiral sea shell.", "Shell", colorHex = 0xFFF472B6),
                LearningVocabItem("b_fish", "fish", "🐟", "Sea", "beach", "A swimming silver fish.", "Fish", colorHex = 0xFF3B82F6),
                LearningVocabItem("b_boat", "boat", "⛵", "Vehicles", "beach", "A sailboat floating on the sea.", "Boat", colorHex = 0xFFDC2626),
                LearningVocabItem("b_ball", "ball", "🏐", "Toys", "beach", "Pass the beach volley ball.", "Ball", colorHex = 0xFFEAB308),
                LearningVocabItem("b_bucket", "bucket", "🪣", "Toys", "beach", "Fill the red bucket with sand.", "Bucket", colorHex = 0xFFEF4444),
                LearningVocabItem("b_shovel", "shovel", "🪴", "Toys", "beach", "Dig deep with the yellow shovel.", "Shovel", colorHex = 0xFF10B981),
                LearningVocabItem("b_hat", "hat", "👒", "Clothes", "beach", "Wear a straw sun hat.", "Hat", colorHex = 0xFFD97706),
                LearningVocabItem("b_towel", "towel", "🧖", "Clothes", "beach", "Dry off with the soft towel.", "Towel", colorHex = 0xFF8B5CF6)
            ),
            verbs = listOf(
                ActionVerbItem("bv_swim", "swim", "🏊", "Swim in the cool water!"),
                ActionVerbItem("bv_jump", "jump", "🌊", "Jump over the small wave!"),
                ActionVerbItem("bv_dig", "dig", "🪣", "Dig a big sand hole."),
                ActionVerbItem("bv_play", "play", "🏐", "Play with the beach ball!"),
                ActionVerbItem("bv_throw", "throw", "🤲", "Throw the ball to Leo!"),
                ActionVerbItem("bv_catch", "catch", "👐", "Catch the flying frisbee!")
            ),
            phrases = listOf(
                WorldPhraseItem("Swim in the sea.", "🌊", "Action"),
                WorldPhraseItem("Find a pretty shell.", "🐚", "Discovery"),
                WorldPhraseItem("Dig in the sand!", "🪣", "Play"),
                WorldPhraseItem("Wear your sun hat.", "👒", "Safety"),
                WorldPhraseItem("Splash splash!", "💦", "Fun")
            )
        ),

        // 7. CITY
        WorldEnvironment(
            id = "city",
            shortName = "City",
            title = "Busy City",
            emoji = "🏙️",
            themeColorHex = 0xFF8B5CF6, // Purple
            accentColorHex = 0xFFA78BFA,
            description = "Cars, buses, fire trucks, traffic lights, and roads!",
            leoGreeting = "Beep beep! Welcome to the city! Remember to look both ways!",
            targetContainerName = "City Garage",
            targetContainerEmoji = "🏢",
            sortCategoryA = "Vehicles",
            sortCategoryB = "Buildings",
            vocabList = listOf(
                LearningVocabItem("c_car", "car", "🚗", "Vehicles", "city", "A fast red city car.", "Car", colorHex = 0xFFDC2626),
                LearningVocabItem("c_bus", "bus", "🚌", "Vehicles", "city", "The big yellow school bus.", "Bus", colorHex = 0xFFF59E0B),
                LearningVocabItem("c_taxi", "taxi", "🚕", "Vehicles", "city", "A quick yellow taxi cab.", "Taxi", colorHex = 0xFFEAB308),
                LearningVocabItem("c_bike", "bike", "🚲", "Vehicles", "city", "Ride the bicycle safely.", "Bike", colorHex = 0xFF10B981),
                LearningVocabItem("c_road", "road", "🛣️", "City", "city", "The long paved road.", "Road", colorHex = 0xFF475569),
                LearningVocabItem("c_street", "street", "🚦", "City", "city", "Walk on the sidewalk street.", "Street", colorHex = 0xFF64748B),
                LearningVocabItem("c_shop", "shop", "🏬", "Buildings", "city", "Buy treats at the shop.", "Shop", colorHex = 0xFFEC4899),
                LearningVocabItem("c_park", "park", "🏞️", "City", "city", "Play on the green city park.", "Park", colorHex = 0xFF16A34A),
                LearningVocabItem("c_house", "house", "🏡", "Buildings", "city", "A cozy neighborhood house.", "House", colorHex = 0xFF0284C7),
                LearningVocabItem("c_hospital", "hospital", "🏥", "Buildings", "city", "Doctors help at the hospital.", "Hospital", colorHex = 0xFFE11D48),
                LearningVocabItem("c_school", "school", "🏫", "Buildings", "city", "Children learn at school.", "School", colorHex = 0xFF6366F1),
                LearningVocabItem("c_fire_truck", "fire truck", "🚒", "Emergency", "city", "Wee-woo! The big red fire truck.", "Fire truck", colorHex = 0xFFB91C1C),
                LearningVocabItem("c_police_car", "police car", "🚓", "Emergency", "city", "The blue police patrol car.", "Police car", colorHex = 0xFF1E40AF)
            ),
            verbs = listOf(
                ActionVerbItem("cv_stop", "stop", "🛑", "Stop at the red light!"),
                ActionVerbItem("cv_go", "go", "🟢", "Go at the green light!"),
                ActionVerbItem("cv_walk", "walk", "🚶", "Walk on the zebra crosswalk."),
                ActionVerbItem("cv_cross", "cross", "🚸", "Look left and right before crossing."),
                ActionVerbItem("cv_look", "look", "👀", "Look both ways carefully!")
            ),
            phrases = listOf(
                WorldPhraseItem("Stop at red!", "🛑", "Safety"),
                WorldPhraseItem("Go at green!", "🟢", "Safety"),
                WorldPhraseItem("Look both ways.", "👀", "Crosswalk"),
                WorldPhraseItem("The bus is big!", "🚌", "Observation"),
                WorldPhraseItem("Beep beep!", "🚗", "Sound")
            )
        ),

        // 8. SPACE
        WorldEnvironment(
            id = "space",
            shortName = "Space",
            title = "Cosmic Space",
            emoji = "🚀",
            themeColorHex = 0xFF1E1B4B, // Deep Cosmic Indigo
            accentColorHex = 0xFF6366F1,
            description = "Fly rockets, explore planets, stars, and the moon!",
            leoGreeting = "3, 2, 1... Blast off! Let's explore stars and planets in deep space!",
            targetContainerName = "Rocket Cargo",
            targetContainerEmoji = "🚀",
            sortCategoryA = "Celestial",
            sortCategoryB = "Exploration",
            vocabList = listOf(
                LearningVocabItem("sp_sun", "sun", "☀️", "Celestial", "space", "The giant blazing sun.", "Sun", colorHex = 0xFFF59E0B),
                LearningVocabItem("sp_moon", "moon", "🌙", "Celestial", "space", "The glowing silver moon.", "Moon", colorHex = 0xFFE2E8F0),
                LearningVocabItem("sp_star", "star", "⭐", "Celestial", "space", "A twinkling bright star.", "Star", colorHex = 0xFFFBBF24),
                LearningVocabItem("sp_planet", "planet", "🪐", "Celestial", "space", "A spinning ringed planet.", "Planet", colorHex = 0xFFA855F7),
                LearningVocabItem("sp_earth", "Earth", "🌍", "Celestial", "space", "Our beautiful blue home planet Earth.", "Earth", colorHex = 0xFF0284C7),
                LearningVocabItem("sp_mars", "Mars", "🔴", "Celestial", "space", "The rusty red planet Mars.", "Mars", colorHex = 0xFFDC2626),
                LearningVocabItem("sp_rocket", "rocket", "🚀", "Vehicles", "space", "Zoom! The fast space rocket.", "Rocket", colorHex = 0xFFE11D48),
                LearningVocabItem("sp_astronaut", "astronaut", "👨‍🚀", "Explorers", "space", "The brave space explorer.", "Astronaut", colorHex = 0xFF38BDF8),
                LearningVocabItem("sp_space", "space", "🌌", "Universe", "space", "The endless starry universe.", "Space", colorHex = 0xFF4338CA),
                LearningVocabItem("sp_alien", "alien", "👽", "Explorers", "space", "A friendly smiling green alien.", "Alien", colorHex = 0xFF10B981),
                LearningVocabItem("sp_comet", "comet", "☄️", "Celestial", "space", "A shooting flaming comet.", "Comet", colorHex = 0xFFF97316)
            ),
            verbs = listOf(
                ActionVerbItem("spv_fly", "fly", "🚀", "Fly high above the clouds!"),
                ActionVerbItem("spv_launch", "launch", "🔥", "3.. 2.. 1.. Launch!"),
                ActionVerbItem("spv_land", "land", "🛬", "Land safely on the moon."),
                ActionVerbItem("spv_look", "look", "🔭", "Look through the space telescope."),
                ActionVerbItem("spv_explore", "explore", "🪐", "Explore mysterious new worlds!")
            ),
            phrases = listOf(
                WorldPhraseItem("3, 2, 1, Blast off!", "🚀", "Launch"),
                WorldPhraseItem("Look at the star.", "⭐", "Wonder"),
                WorldPhraseItem("The Earth is blue!", "🌍", "Discovery"),
                WorldPhraseItem("Rocket is fast!", "⚡", "Concept"),
                WorldPhraseItem("Hello, astronaut!", "👨‍🚀", "Greeting")
            )
        ),

        // 9. PARK
        WorldEnvironment(
            id = "park",
            shortName = "Park",
            title = "Sunny Park",
            emoji = "🏞️",
            themeColorHex = 0xFF059669,
            accentColorHex = 0xFF34D399,
            description = "Enjoy picnics, ponds, ducks, kites, and green lawns!",
            leoGreeting = "What a lovely day at the sunny park! Let's have fun outdoors!",
            targetContainerName = "Picnic Basket",
            targetContainerEmoji = "🧺",
            sortCategoryA = "Picnic",
            sortCategoryB = "Nature",
            vocabList = listOf(
                LearningVocabItem("pk_kite", "kite", "🪁", "Toys", "park", "Fly the diamond kite high.", "Kite", colorHex = 0xFFEC4899),
                LearningVocabItem("pk_pond", "pond", "🌊", "Nature", "park", "Ducks swim in the cool pond.", "Pond", colorHex = 0xFF0284C7),
                LearningVocabItem("pk_duck", "duck", "🦆", "Animals", "park", "The duck quacks happily.", "Duck", colorHex = 0xFFEAB308),
                LearningVocabItem("pk_picnic", "picnic", "🧺", "Picnic", "park", "Delicious picnic on the grass.", "Picnic", colorHex = 0xFFD97706),
                LearningVocabItem("pk_grass", "grass", "🌱", "Nature", "park", "Soft green fresh grass.", "Grass", colorHex = 0xFF16A34A),
                LearningVocabItem("pk_tree", "tree", "🌳", "Nature", "park", "Rest under the shade tree.", "Tree", colorHex = 0xFF15803D),
                LearningVocabItem("pk_bench", "bench", "🪵", "Furniture", "park", "Sit down on the wooden bench.", "Bench", colorHex = 0xFF78350F),
                LearningVocabItem("pk_fountain", "fountain", "⛲", "Park", "park", "The splashing water fountain.", "Fountain", colorHex = 0xFF06B6D4)
            ),
            verbs = listOf(
                ActionVerbItem("pkv_fly", "fly", "🪁", "Fly the kite in the wind!"),
                ActionVerbItem("pkv_walk", "walk", "🚶", "Walk around the peaceful pond."),
                ActionVerbItem("pkv_feed", "feed", "🦆", "Feed bread crumbs to ducks."),
                ActionVerbItem("pkv_relax", "relax", "🧘", "Relax in the warm sunshine.")
            ),
            phrases = listOf(
                WorldPhraseItem("Fly the kite!", "🪁", "Activity"),
                WorldPhraseItem("Feed the ducks.", "🦆", "Care"),
                WorldPhraseItem("The park is green!", "🌱", "Nature"),
                WorldPhraseItem("Have a picnic!", "🧺", "Fun")
            )
        ),

        // 10. PLAYGROUND
        WorldEnvironment(
            id = "playground",
            shortName = "Playground",
            title = "Fun Playground",
            emoji = "🛝",
            themeColorHex = 0xFFF97316,
            accentColorHex = 0xFFFDBA74,
            description = "Swings, slides, merry-go-round, sandbox, and climbing!",
            leoGreeting = "Yippee! Welcome to the playground! Time to swing, slide, and climb!",
            targetContainerName = "Play Locker",
            targetContainerEmoji = "🎒",
            sortCategoryA = "Rides",
            sortCategoryB = "Games",
            vocabList = listOf(
                LearningVocabItem("pg_slide", "slide", "🛝", "Rides", "playground", "Slide down fast and safe.", "Slide", colorHex = 0xFFEF4444),
                LearningVocabItem("pg_swing", "swing", "🪢", "Rides", "playground", "Swing up towards the blue sky.", "Swing", colorHex = 0xFF3B82F6),
                LearningVocabItem("pg_sandbox", "sandbox", "🏖️", "Games", "playground", "Build castles in the sandbox.", "Sandbox", colorHex = 0xFFF59E0B),
                LearningVocabItem("pg_seesaw", "seesaw", "⚖️", "Rides", "playground", "Up and down on the seesaw.", "Seesaw", colorHex = 0xFF8B5CF6),
                LearningVocabItem("pg_ball", "ball", "⚽", "Games", "playground", "Kick the bouncy playground ball.", "Ball", colorHex = 0xFF10B981),
                LearningVocabItem("pg_ladder", "ladder", "🪜", "Rides", "playground", "Climb the colorful ladder.", "Ladder", colorHex = 0xFFEA580C)
            ),
            verbs = listOf(
                ActionVerbItem("pgv_slide", "slide", "🛝", "Slide down with joy!"),
                ActionVerbItem("pgv_swing", "swing", "🪢", "Swing higher and higher!"),
                ActionVerbItem("pgv_climb", "climb", "🧗", "Climb safely to the top."),
                ActionVerbItem("pgv_play", "play", "🎈", "Play together with buddies!")
            ),
            phrases = listOf(
                WorldPhraseItem("Slide down!", "🛝", "Fun"),
                WorldPhraseItem("Swing high in the sky!", "🪢", "Adventure"),
                WorldPhraseItem("Take turns kindly.", "🤝", "Sharing"),
                WorldPhraseItem("Play in the sandbox!", "🏖️", "Creativity")
            )
        ),

        // 11. KITCHEN
        WorldEnvironment(
            id = "kitchen",
            shortName = "Kitchen",
            title = "Chef's Kitchen",
            emoji = "🍳",
            themeColorHex = 0xFFE11D48,
            accentColorHex = 0xFFFB7185,
            description = "Cook, bake, blend juices, and wash up tasty foods!",
            leoGreeting = "Chef Leo here! Let's cook some delicious treats together in the kitchen!",
            targetContainerName = "Chef Pot",
            targetContainerEmoji = "🍲",
            sortCategoryA = "Cookware",
            sortCategoryB = "Ingredients",
            vocabList = listOf(
                LearningVocabItem("kt_pot", "pot", "🍲", "Cookware", "kitchen", "Boil warm soup in the pot.", "Pot", colorHex = 0xFF475569),
                LearningVocabItem("kt_pan", "pan", "🍳", "Cookware", "kitchen", "Sizzle eggs in the frying pan.", "Pan", colorHex = 0xFF0F172A),
                LearningVocabItem("kt_oven", "oven", "♨️", "Appliances", "kitchen", "Bake warm cookies in the oven.", "Oven", colorHex = 0xFFD97706),
                LearningVocabItem("kt_fridge", "fridge", "🧊", "Appliances", "kitchen", "Keep milk cold in the fridge.", "Fridge", colorHex = 0xFF0284C7),
                LearningVocabItem("kt_spoon", "spoon", "🥄", "Cookware", "kitchen", "Stir the yummy sauce with spoon.", "Spoon", colorHex = 0xFF64748B),
                LearningVocabItem("kt_fruit", "fruit", "🍓", "Ingredients", "kitchen", "Sweet juicy red strawberries.", "Fruit", colorHex = 0xFFDC2626),
                LearningVocabItem("kt_vegetable", "vegetable", "🥕", "Ingredients", "kitchen", "Crunchy orange carrot.", "Vegetable", colorHex = 0xFFEA580C)
            ),
            verbs = listOf(
                ActionVerbItem("ktv_cook", "cook", "🍳", "Cook delicious dinner!"),
                ActionVerbItem("ktv_bake", "bake", "🥖", "Bake yummy cookies."),
                ActionVerbItem("ktv_wash", "wash", "🧼", "Wash your hands before eating."),
                ActionVerbItem("ktv_stir", "stir", "🥄", "Stir the pot gently.")
            ),
            phrases = listOf(
                WorldPhraseItem("Cook yummy soup!", "🍲", "Cooking"),
                WorldPhraseItem("Wash your hands.", "🧼", "Hygiene"),
                WorldPhraseItem("Bake tasty cookies.", "🍪", "Baking"),
                WorldPhraseItem("Taste good!", "😋", "Enjoyment")
            )
        ),

        // 12. BEDROOM
        WorldEnvironment(
            id = "bedroom",
            shortName = "Bedroom",
            title = "Cozy Bedroom",
            emoji = "🛏️",
            themeColorHex = 0xFF7C3AED,
            accentColorHex = 0xFFA78BFA,
            description = "Soft beds, storybooks, teddy bears, and sweet dreams!",
            leoGreeting = "Welcome to the cozy bedroom! Let's read a story and get comfortable!",
            targetContainerName = "Nightstand",
            targetContainerEmoji = "🏮",
            sortCategoryA = "Sleep",
            sortCategoryB = "Play",
            vocabList = listOf(
                LearningVocabItem("bd_bed", "bed", "🛏️", "Sleep", "bedroom", "A soft comfy wooden bed.", "Bed", colorHex = 0xFF3B82F6),
                LearningVocabItem("bd_pillow", "pillow", "🛋️", "Sleep", "bedroom", "Rest your head on the pillow.", "Pillow", colorHex = 0xFF8B5CF6),
                LearningVocabItem("bd_blanket", "blanket", "🧶", "Sleep", "bedroom", "Tuck under the warm blanket.", "Blanket", colorHex = 0xFFEC4899),
                LearningVocabItem("bd_teddy", "teddy bear", "🧸", "Play", "bedroom", "Hug the fluffy teddy bear.", "Teddy bear", colorHex = 0xFFD97706),
                LearningVocabItem("bd_clock", "clock", "⏰", "Objects", "bedroom", "The ticking bedtime alarm clock.", "Clock", colorHex = 0xFFF59E0B),
                LearningVocabItem("bd_closet", "closet", "🚪", "Furniture", "bedroom", "Hang clean clothes in closet.", "Closet", colorHex = 0xFF78350F),
                LearningVocabItem("bd_slippers", "slippers", "🥿", "Sleep", "bedroom", "Warm fluffy bedroom slippers.", "Slippers", colorHex = 0xFFF472B6)
            ),
            verbs = listOf(
                ActionVerbItem("bdv_sleep", "sleep", "😴", "Close your eyes and sleep well."),
                ActionVerbItem("bdv_read", "read", "📖", "Read a wonderful bedtime story."),
                ActionVerbItem("bdv_hug", "hug", "🧸", "Hug your teddy bear tight!"),
                ActionVerbItem("bdv_dream", "dream", "✨", "Dream of magical adventures.")
            ),
            phrases = listOf(
                WorldPhraseItem("Good night!", "🌙", "Bedtime"),
                WorldPhraseItem("Read a bedtime story.", "📖", "Story"),
                WorldPhraseItem("Sweet dreams!", "✨", "Wishes"),
                WorldPhraseItem("Sleep tight.", "😴", "Comfort")
            )
        ),

        // 13. ANIMAL AREA / SAFARI
        WorldEnvironment(
            id = "animals",
            shortName = "Animals",
            title = "Safari Wildlife",
            emoji = "🦁",
            themeColorHex = 0xFFD97706,
            accentColorHex = 0xFFFBBF24,
            description = "Lions, elephants, monkeys, giraffes, and zebras in the wild!",
            leoGreeting = "Roar! Welcome to the Safari Wildlife area! Meet amazing animal friends!",
            targetContainerName = "Safari Jeep",
            targetContainerEmoji = "🚙",
            sortCategoryA = "Big Cats",
            sortCategoryB = "Herbivores",
            vocabList = listOf(
                LearningVocabItem("an_lion", "lion", "🦁", "Big Cats", "animals", "The brave king lion roars.", "Lion", colorHex = 0xFFF59E0B),
                LearningVocabItem("an_tiger", "tiger", "🐯", "Big Cats", "animals", "The striped tiger pounces.", "Tiger", colorHex = 0xFFEA580C),
                LearningVocabItem("an_elephant", "elephant", "🐘", "Herbivores", "animals", "The big elephant sprays water.", "Elephant", colorHex = 0xFF0284C7),
                LearningVocabItem("an_giraffe", "giraffe", "🦒", "Herbivores", "animals", "The tall giraffe reaches leaves.", "Giraffe", colorHex = 0xFFEAB308),
                LearningVocabItem("an_zebra", "zebra", "🦓", "Herbivores", "animals", "The striped zebra runs across plains.", "Zebra", colorHex = 0xFF1E293B),
                LearningVocabItem("an_monkey", "monkey", "🐵", "Herbivores", "animals", "The cheeky monkey swings in trees.", "Monkey", colorHex = 0xFF854D0E),
                LearningVocabItem("an_hippo", "hippo", "🦛", "Herbivores", "animals", "The big hippo swims in the river.", "Hippo", colorHex = 0xFF475569)
            ),
            verbs = listOf(
                ActionVerbItem("anv_roar", "roar", "🦁", "Roar loud like a brave lion!"),
                ActionVerbItem("anv_swing", "swing", "🐵", "Swing through the jungle branches."),
                ActionVerbItem("anv_run", "run", "🦓", "Run fast across the savanna!"),
                ActionVerbItem("anv_watch", "watch", "👀", "Watch wild animals respectfully.")
            ),
            phrases = listOf(
                WorldPhraseItem("The lion roars!", "🦁", "Roar"),
                WorldPhraseItem("Giraffe is so tall!", "🦒", "Height"),
                WorldPhraseItem("Elephant is strong!", "🐘", "Strength"),
                WorldPhraseItem("Look at the zebra!", "🦓", "Discovery")
            )
        )
    )

    fun getWorldById(worldId: String): WorldEnvironment {
        return worlds.find { it.id.equals(worldId, ignoreCase = true) } ?: worlds.first()
    }
}
