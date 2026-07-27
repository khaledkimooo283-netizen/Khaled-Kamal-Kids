package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository
import com.example.data.MemoryCardsData
import com.example.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class AdventureWorldData(
    val id: Int,
    val letter: Char,
    val name: String,
    val arabicName: String,
    val guideName: String,
    val guideEmoji: String,
    val storyEn: String,
    val storyAr: String,
    val learnedWords: List<String>,
    val wordEmojis: List<String>,
    val arabicWords: List<String>,
    val bgStartColor: Long,
    val bgEndColor: Long
)

object AdventureData {
    val all26Worlds: List<AdventureWorldData> = listOf(
        AdventureWorldData(
            id = 0, letter = 'A', name = "Apple Forest", arabicName = "غابة التفاح",
            guideName = "Squirrel 🐿️", guideEmoji = "🐿️",
            storyEn = "The dark magician stole Letter A! Little Squirrel lost all magic apples!",
            storyAr = "الساحر المظلم سرق حرف A! السنجاب الصغير فقد تفاحه السحري!",
            learnedWords = listOf("Apple", "Ant", "Airplane"),
            wordEmojis = listOf("🍎", "🐜", "✈️"),
            arabicWords = listOf("تفاحة", "نملة", "طائرة"),
            bgStartColor = 0xFF4ADE80, bgEndColor = 0xFF16A34A
        ),
        AdventureWorldData(
            id = 1, letter = 'B', name = "Bear Meadow", arabicName = "مرج الدب",
            guideName = "Bear 🐻", guideEmoji = "🐻",
            storyEn = "Bear lost all his magic balls and sweet bananas in the meadow!",
            storyAr = "الدب فقد كراته السحرية والموز اللذيذ في المرج!",
            learnedWords = listOf("Ball", "Bear", "Banana"),
            wordEmojis = listOf("⚽", "🐻", "🍌"),
            arabicWords = listOf("كرة", "دب", "موزة"),
            bgStartColor = 0xFF60A5FA, bgEndColor = 0xFF2563EB
        ),
        AdventureWorldData(
            id = 2, letter = 'C', name = "Cat Castle", arabicName = "قلعة القطة",
            guideName = "Cat 🐱", guideEmoji = "🐱",
            storyEn = "Cat lost her birthday cake and magic red car! Help Cat!",
            storyAr = "القطة فقدت كعكة عيد ميلادها وسيارتها الحمراء! ساعد القطة!",
            learnedWords = listOf("Cat", "Car", "Cake"),
            wordEmojis = listOf("🐱", "🚗", "🎂"),
            arabicWords = listOf("قطة", "سيارة", "كعكة"),
            bgStartColor = 0xFFF472B6, bgEndColor = 0xFFDB2777
        ),
        AdventureWorldData(
            id = 3, letter = 'D', name = "Dog Island", arabicName = "جزيرة الكلب",
            guideName = "Dog 🐶", guideEmoji = "🐶",
            storyEn = "Dog cannot find his magic drum and duck friends on the island!",
            storyAr = "الكلب لا يجد طبله السحري وأصدقاءه البط في الجزيرة!",
            learnedWords = listOf("Dog", "Duck", "Drum"),
            wordEmojis = listOf("🐶", "🦆", "🥁"),
            arabicWords = listOf("كلب", "بطة", "طبلة"),
            bgStartColor = 0xFFFBBF24, bgEndColor = 0xFFD97706
        ),
        AdventureWorldData(
            id = 4, letter = 'E', name = "Elephant Jungle", arabicName = "غابة الفيل",
            guideName = "Elephant 🐘", guideEmoji = "🐘",
            storyEn = "Elephant is searching for the Golden Egg guarded by the Eagle!",
            storyAr = "الفيل يبحث عن البيضة الذهبية التي يحرسها النسر!",
            learnedWords = listOf("Elephant", "Egg", "Eagle"),
            wordEmojis = listOf("🐘", "🥚", "🦅"),
            arabicWords = listOf("فيل", "بيضة", "نسر"),
            bgStartColor = 0xFFA78BFA, bgEndColor = 0xFF7C3AED
        ),
        AdventureWorldData(
            id = 5, letter = 'F', name = "Fox River", arabicName = "نهر الثعلب",
            guideName = "Fox 🦊", guideEmoji = "🦊",
            storyEn = "Fox lost his friendly fish and pink flowers along the enchanted river!",
            storyAr = "الثعلب فقد سمكته اللطيفة والزهور الوردية بجانب النهر السحري!",
            learnedWords = listOf("Fish", "Frog", "Flower"),
            wordEmojis = listOf("🐟", "🐸", "🌸"),
            arabicWords = listOf("سمكة", "ضفدع", "زهرة"),
            bgStartColor = 0xFFFB923C, bgEndColor = 0xFFEA580C
        ),
        AdventureWorldData(
            id = 6, letter = 'G', name = "Giraffe Grove", arabicName = "بستان الزرافة",
            guideName = "Giraffe 🦒", guideEmoji = "🦒",
            storyEn = "Giraffe wants to play music with his guitar under sweet grape vines!",
            storyAr = "الزرافة تريد عزف الموسيقى بقلعة القيثارة تحت عناقيد العنب!",
            learnedWords = listOf("Giraffe", "Grape", "Guitar"),
            wordEmojis = listOf("🦒", "🍇", "🎸"),
            arabicWords = listOf("زرافة", "عنب", "قيثارة"),
            bgStartColor = 0xFF34D399, bgEndColor = 0xFF059669
        ),
        AdventureWorldData(
            id = 7, letter = 'H', name = "Horse Haven", arabicName = "مأوى الحصان",
            guideName = "Horse 🐴", guideEmoji = "🐴",
            storyEn = "Horse lost his magic hat near the cozy house!",
            storyAr = "الحصان فقد قبعته السحرية بجوار المنزل الدافئ!",
            learnedWords = listOf("House", "Horse", "Hat"),
            wordEmojis = listOf("🏠", "🐴", "🎩"),
            arabicWords = listOf("منزل", "حصان", "قبعة"),
            bgStartColor = 0xFFF87171, bgEndColor = 0xFFDC2626
        ),
        AdventureWorldData(
            id = 8, letter = 'I', name = "Iguana Island", arabicName = "جزيرة الإغوانا",
            guideName = "Iguana 🦎", guideEmoji = "🦎",
            storyEn = "Iguana is looking for yummy ice cream on the tropical island!",
            storyAr = "الإغوانا تبحث عن البوظة اللذيذة في الجزيرة الاستوائية!",
            learnedWords = listOf("Ice Cream", "Iguana", "Island"),
            wordEmojis = listOf("🍦", "🦎", "🏝️"),
            arabicWords = listOf("بوظة", "إغوانا", "جزيرة"),
            bgStartColor = 0xFF38BDF8, bgEndColor = 0xFF0284C7
        ),
        AdventureWorldData(
            id = 9, letter = 'J', name = "Jellyfish Deep", arabicName = "أعماق قنديل البحر",
            guideName = "Jellyfish 🪼", guideEmoji = "🪼",
            storyEn = "Jellyfish lost his sweet fruit juice in the blue ocean!",
            storyAr = "قنديل البحر فقد عصير الفاكهة اللذيذ في المحيط الأزرق!",
            learnedWords = listOf("Juice", "Jellyfish", "Jet"),
            wordEmojis = listOf("🧃", "🪼", "✈️"),
            arabicWords = listOf("عصير", "قنديل البحر", "طائرة نفاثة"),
            bgStartColor = 0xFF818CF8, bgEndColor = 0xFF4F46E5
        ),
        AdventureWorldData(
            id = 10, letter = 'K', name = "Kangaroo Peak", arabicName = "قمة الكانغر",
            guideName = "Kangaroo 🦘", guideEmoji = "🦘",
            storyEn = "Kangaroo's kite flew into the high clouds! Find the magic key!",
            storyAr = "طائرة الكانغر الورقية طارت إلى السحاب! ابحث عن المفتاح السحري!",
            learnedWords = listOf("Kite", "Key", "Kangaroo"),
            wordEmojis = listOf("🪁", "🔑", "🦘"),
            arabicWords = listOf("طائرة ورقية", "مفتاح", "كانغر"),
            bgStartColor = 0xFFC084FC, bgEndColor = 0xFF9333EA
        ),
        AdventureWorldData(
            id = 11, letter = 'L', name = "Lion Kingdom", arabicName = "مملكة الأسد",
            guideName = "Leo Lion 🦁", guideEmoji = "🦁",
            storyEn = "Leo Lion needs lemons and green leaves to make a magical potion!",
            storyAr = "الأسد ليو يحتاج ليمون وأوراق شجر خضراء لصنع المشروب السحري!",
            learnedWords = listOf("Lion", "Lemon", "Leaf"),
            wordEmojis = listOf("🦁", "🍋", "🍃"),
            arabicWords = listOf("أسد", "ليمون", "ورقة شجر"),
            bgStartColor = 0xFFFBBF24, bgEndColor = 0xFFB45309
        ),
        AdventureWorldData(
            id = 12, letter = 'M', name = "Monkey Mountain", arabicName = "جبل القرد",
            guideName = "Monkey 🐒", guideEmoji = "🐒",
            storyEn = "Monkey lost his moon key under the starry night sky!",
            storyAr = "القرد فقد مفتاح القمر تحت السماء المليئة بالنجوم!",
            learnedWords = listOf("Monkey", "Moon", "Milk"),
            wordEmojis = listOf("🐒", "🌙", "🥛"),
            arabicWords = listOf("قرد", "قمر", "حليب"),
            bgStartColor = 0xFF64748B, bgEndColor = 0xFF334155
        ),
        AdventureWorldData(
            id = 13, letter = 'N', name = "Nest Valley", arabicName = "وادي العش",
            guideName = "Bird 🪹", guideEmoji = "🪹",
            storyEn = "Bird lost her golden nest and nuts in the tall trees!",
            storyAr = "العصفورة فقدت عشها الذهبي والبندق في الأشجار العالية!",
            learnedWords = listOf("Nest", "Nut", "Net"),
            wordEmojis = listOf("🪹", "🥜", "🥅"),
            arabicWords = listOf("عش", "بندقة", "شبكة"),
            bgStartColor = 0xFF10B981, bgEndColor = 0xFF047857
        ),
        AdventureWorldData(
            id = 14, letter = 'O', name = "Owl Observatory", arabicName = "مرصد البومة",
            guideName = "Owl 🦉", guideEmoji = "🦉",
            storyEn = "Wise Owl needs sweet oranges for the starry night party!",
            storyAr = "البومة الحكيمة تحتاج برتقالاً لذيذاً لحفلة الليل النجمية!",
            learnedWords = listOf("Owl", "Orange", "Octopus"),
            wordEmojis = listOf("🦉", "🍊", "🐙"),
            arabicWords = listOf("بومة", "برتقالة", "أخطبوط"),
            bgStartColor = 0xFFF97316, bgEndColor = 0xFFC2410C
        ),
        AdventureWorldData(
            id = 15, letter = 'P', name = "Penguin Polar", arabicName = "قطب البطريق",
            guideName = "Penguin 🐧", guideEmoji = "🐧",
            storyEn = "Penguin lost his pizza slices and pencil on the slippery ice!",
            storyAr = "البطريق فقد شرائح البيتزا وقلمه على الجليد الزلق!",
            learnedWords = listOf("Penguin", "Pizza", "Pencil"),
            wordEmojis = listOf("🐧", "🍕", "✏️"),
            arabicWords = listOf("بطريق", "بيتزا", "قلم رصاص"),
            bgStartColor = 0xFF06B6D4, bgEndColor = 0xFF0E7490
        ),
        AdventureWorldData(
            id = 16, letter = 'Q', name = "Queen Bee Hive", arabicName = "خلية الملكة",
            guideName = "Queen Bee 🐝", guideEmoji = "🐝",
            storyEn = "Queen Bee is looking for her golden crown and quail friends!",
            storyAr = "ملكة النحل تبحث عن تاجها الذهبي وأصدقائها السمان!",
            learnedWords = listOf("Queen", "Quail", "Question"),
            wordEmojis = listOf("👸", "🐦", "❓"),
            arabicWords = listOf("ملكة", "سمان", "سؤال"),
            bgStartColor = 0xFFEAB308, bgEndColor = 0xFFA16207
        ),
        AdventureWorldData(
            id = 17, letter = 'R', name = "Rabbit Rainbow", arabicName = "قوس قزح الأرنب",
            guideName = "Rabbit 🐰", guideEmoji = "🐰",
            storyEn = "Rabbit lost his shiny ring and toy robot under the rainbow!",
            storyAr = "الأرنب فقد خاتمه اللامع وروبوته الألعاب تحت قوس قزح!",
            learnedWords = listOf("Rabbit", "Ring", "Robot"),
            wordEmojis = listOf("🐇", "💍", "🤖"),
            arabicWords = listOf("أرنب", "خاتم", "روبوت"),
            bgStartColor = 0xFFEC4899, bgEndColor = 0xFFBE185D
        ),
        AdventureWorldData(
            id = 18, letter = 'S', name = "Sun Desert", arabicName = "صحراء الشمس",
            guideName = "Snake 🐍", guideEmoji = "🐍",
            storyEn = "Snake wants to catch golden stars under the warm glowing sun!",
            storyAr = "الثعبان يريد التقاط النجوم الذهبية تحت الشمس الدافئة!",
            learnedWords = listOf("Sun", "Star", "Strawberry"),
            wordEmojis = listOf("☀️", "⭐", "🍓"),
            arabicWords = listOf("شمس", "نجمة", "فراولة"),
            bgStartColor = 0xFFF59E0B, bgEndColor = 0xFFB45309
        ),
        AdventureWorldData(
            id = 19, letter = 'T', name = "Tiger Temple", arabicName = "معبد النمر",
            guideName = "Tiger 🐯", guideEmoji = "🐯",
            storyEn = "Tiger lost his path in the magical tree temple!",
            storyAr = "النمر فقد طريقه في معبد الأشجار السحري!",
            learnedWords = listOf("Tiger", "Tree", "Turtle"),
            wordEmojis = listOf("🐯", "🌳", "🐢"),
            arabicWords = listOf("نمر", "شجرة", "سلحفاة"),
            bgStartColor = 0xFF84CC16, bgEndColor = 0xFF4D7C0F
        ),
        AdventureWorldData(
            id = 20, letter = 'U', name = "Unicorn Universe", arabicName = "كون وحيد القرن",
            guideName = "Unicorn 🦄", guideEmoji = "🦄",
            storyEn = "Unicorn needs a rainbow umbrella to fly in the galaxy UFO!",
            storyAr = "وحيد القرن يحتاج مظلة قوس قزح للطيران في الصحن الطائر!",
            learnedWords = listOf("Umbrella", "Unicorn", "UFO"),
            wordEmojis = listOf("☂️", "🦄", "🛸"),
            arabicWords = listOf("مظلة", "وحيد القرن", "صحن طائر"),
            bgStartColor = 0xFFA855F7, bgEndColor = 0xFF6B21A8
        ),
        AdventureWorldData(
            id = 21, letter = 'V', name = "Volcano Valley", arabicName = "وادي البركان",
            guideName = "Volcano Bird 🦜", guideEmoji = "🦜",
            storyEn = "Bird plays the magic violin near the glowing volcano!",
            storyAr = "الطائر يعزف على الكمان السحري بجوار البركان المتوهج!",
            learnedWords = listOf("Violin", "Van", "Volcano"),
            wordEmojis = listOf("🎻", "🚐", "🌋"),
            arabicWords = listOf("كمان", "شاحنة", "بركان"),
            bgStartColor = 0xFFEF4444, bgEndColor = 0xFF991B1B
        ),
        AdventureWorldData(
            id = 22, letter = 'W', name = "Whale Ocean", arabicName = "محيط الحوت",
            guideName = "Whale 🐋", guideEmoji = "🐋",
            storyEn = "Whale lost his watermelon slices and watch in the ocean!",
            storyAr = "الحوت فقد شرائح البطيخ وساعته في أعماق المحيط!",
            learnedWords = listOf("Watermelon", "Watch", "Whale"),
            wordEmojis = listOf("🍉", "⌚", "🐋"),
            arabicWords = listOf("بطيخ", "ساعة يد", "حوت"),
            bgStartColor = 0xFF14B8A6, bgEndColor = 0xFF0F766E
        ),
        AdventureWorldData(
            id = 23, letter = 'X', name = "Xylophone Galaxy", arabicName = "مجرة الكسيلوفون",
            guideName = "X-Ray Fish 🐠", guideEmoji = "🐠",
            storyEn = "X-Ray Fish plays the starry xylophone in space!",
            storyAr = "سمكة الأشعة تعزف على الكسيلوفون النجمي في الفضاء!",
            learnedWords = listOf("Xylophone", "X-ray", "Fox"),
            wordEmojis = listOf("🎼", "🩻", "🦊"),
            arabicWords = listOf("كسيلوفون", "أشعة", "ثعلب"),
            bgStartColor = 0xFF6366F1, bgEndColor = 0xFF3730A3
        ),
        AdventureWorldData(
            id = 24, letter = 'Y', name = "Yak Yacht", arabicName = "يخت ثور التبت",
            guideName = "Yak 🐂", guideEmoji = "🐂",
            storyEn = "Yak lost his magic yo-yo on the sailing yacht!",
            storyAr = "ثور التبت فقد يويو ألعابه السحري على اليخت الشراعي!",
            learnedWords = listOf("Yo-Yo", "Yak", "Yacht"),
            wordEmojis = listOf("🪀", "🐂", "🛥️"),
            arabicWords = listOf("يويو", "ثور التبت", "يخت"),
            bgStartColor = 0xFF0EA5E9, bgEndColor = 0xFF0369A1
        ),
        AdventureWorldData(
            id = 25, letter = 'Z', name = "Zebra Zoo", arabicName = "حديقة الحمار الوحشي",
            guideName = "Zebra 🦓", guideEmoji = "🦓",
            storyEn = "Zebra opens the final Golden Gate to free all letters in the Zoo!",
            storyAr = "الحمار الوحشي يفتح البوابة الذهبية الأخيرة لتحرير جميع الحروف!",
            learnedWords = listOf("Zebra", "Zip", "Zoo"),
            wordEmojis = listOf("🦓", "🤐", "🦁"),
            arabicWords = listOf("حمار وحشي", "سحاب", "حديقة حيوان"),
            bgStartColor = 0xFF8B5CF6, bgEndColor = 0xFF5B21B6
        )
    )
}

@Composable
fun AdventureModeScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val appLanguage by remember { derivedStateOf { repository.getLanguage() } }

    var userStars by remember { mutableIntStateOf(repository.getStars()) }
    var userCoins by remember { mutableIntStateOf(repository.getCoins()) }
    var unlockedWorldIdx by remember { mutableIntStateOf(repository.getAdventureUnlockedWorld()) }

    var activeWorld by remember { mutableStateOf<AdventureWorldData?>(null) }
    var activeStep by remember { mutableIntStateOf(0) } // 0: Briefing, 1: Explore Cards, 2: Mini-Game, 3: Quiz, 4: Treasure Opening

    var selectedQuizChoice by remember { mutableStateOf<String?>(null) }
    var quizError by remember { mutableStateOf(false) }

    var showChestOpenAnimation by remember { mutableStateOf(false) }
    var showCertificateDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val welcomeSpeech = if (appLanguage == "Arabic") {
            "مرحباً بك في نمط المغامرة! ساعد الأسد ليو والحيوانات في إنقاذ الحروف السحرية! 🗺️"
        } else {
            "Welcome to Adventure Mode! Help Leo the Lion save the magical letters! 🗺️"
        }
        audioEngine.speak(welcomeSpeech)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEF3C7))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KkHeader(
                title = if (appLanguage == "Arabic") "مغامرة الحروف 🗺️" else "Alphabet Adventure 🗺️",
                starsCount = userStars,
                onBackClick = onBackClick,
                isMuted = audioEngine.isMuted,
                onMuteToggle = { audioEngine.isMuted = !audioEngine.isMuted }
            )

            // Story Clue Progress Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD97706)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🦁", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (appLanguage == "Arabic") "التقدم في المغامرة: $unlockedWorldIdx / 26 عالم" else "Adventure Progress: $unlockedWorldIdx / 26 Worlds Saved!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = if (appLanguage == "Arabic") "انقر على العالم المفتوح لاسترجاع الحروف والكلمات!" else "Tap any unlocked world to rescue stolen letters!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 26 Worlds Trail List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                itemsIndexed(AdventureData.all26Worlds) { idx, world ->
                    val isUnlocked = idx <= unlockedWorldIdx
                    val isCompleted = idx < unlockedWorldIdx
                    val isCurrentTarget = idx == unlockedWorldIdx

                    val gradientBrush = Brush.linearGradient(
                        colors = if (isUnlocked) listOf(Color(world.bgStartColor), Color(world.bgEndColor))
                        else listOf(Color(0xFF94A3B8), Color(0xFF64748B))
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .border(
                                width = if (isCurrentTarget) 4.dp else 1.dp,
                                color = if (isCurrentTarget) Color(0xFFF59E0B) else Color.Transparent,
                                shape = RoundedCornerShape(22.dp)
                            )
                            .clickable(enabled = isUnlocked) {
                                activeWorld = world
                                activeStep = 0
                                selectedQuizChoice = null
                                quizError = false
                                showChestOpenAnimation = false

                                val introText = if (appLanguage == "Arabic") {
                                    "${world.arabicName}! حرف ${world.letter}. ${world.storyAr}"
                                } else {
                                    "${world.name}! Letter ${world.letter}. ${world.storyEn}"
                                }
                                audioEngine.speak(introText)
                            },
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(brush = gradientBrush)
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Guide Emoji + Letter Badge
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    Text(text = world.guideEmoji, fontSize = 42.sp)
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${world.letter}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(world.bgEndColor)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (appLanguage == "Arabic") world.arabicName else world.name,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                        if (isCompleted) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = "Completed",
                                                tint = Color(0xFF86EFAC),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "${world.guideName} • ${if (appLanguage == "Arabic") world.storyAr else world.storyEn}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.9f),
                                        maxLines = 2
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        world.wordEmojis.forEach { emoji ->
                                            Text(text = emoji, fontSize = 14.sp)
                                        }
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isUnlocked) Icons.Filled.PlayArrow else Icons.Filled.Lock,
                                        contentDescription = "Status",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active World Mission Full Flow Dialog
        activeWorld?.let { world ->
            AlertDialog(
                onDismissRequest = { activeWorld = null },
                confirmButton = {},
                shape = RoundedCornerShape(28.dp),
                containerColor = Color.White,
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = world.guideEmoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (appLanguage == "Arabic") world.arabicName else world.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFB45309)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Letter ${world.letter}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (activeStep) {
                            0 -> {
                                // Step 0: Mission Briefing
                                Text(
                                    text = if (appLanguage == "Arabic") "مهمة البطل 🚀" else "Hero Mission 🚀",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = if (appLanguage == "Arabic") world.storyAr else world.storyEn,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF475569)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = if (appLanguage == "Arabic") "الكلمات السحرية للإنقاذ:" else "Magic Words to Rescue:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    world.learnedWords.forEachIndexed { i, w ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = world.wordEmojis[i], fontSize = 32.sp)
                                            Text(text = w, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        activeStep = 1
                                        audioEngine.speak(if (appLanguage == "Arabic") "اضغط على الكروت لاستكشاف ونطق الكلمات!" else "Tap cards to explore words and pronunciation!")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(if (appLanguage == "Arabic") "بدء المهمة! 🚀" else "Start Mission! 🚀", fontWeight = FontWeight.Bold)
                                }
                            }

                            1 -> {
                                // Step 1: Discover Vocabulary Cards
                                Text(
                                    text = if (appLanguage == "Arabic") "استكشف الكلمات السحرية 📖" else "Explore Magic Vocabulary 📖",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    world.learnedWords.forEachIndexed { i, word ->
                                        val emoji = world.wordEmojis[i]
                                        val arWord = world.arabicWords[i]

                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    val speakMsg = if (appLanguage == "Arabic") "$arWord... $word" else "$word... ${world.letter} is for $word"
                                                    audioEngine.speak(speakMsg)
                                                },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(text = emoji, fontSize = 32.sp)
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Column {
                                                        Text(text = word, fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color(0xFFB45309))
                                                        Text(text = arWord, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                                                    }
                                                }

                                                IconButton(onClick = {
                                                    audioEngine.speak("$word... $arWord")
                                                }) {
                                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Audio", tint = Color(0xFFD97706))
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        activeStep = 2
                                        audioEngine.speak(if (appLanguage == "Arabic") "تحدي الاختبار! أين توجد كلمة ${world.learnedWords[0]}؟" else "Quiz Challenge! Which one is ${world.learnedWords[0]}?")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(if (appLanguage == "Arabic") "التالي: التحدي النهائى 🎯" else "Next: Final Challenge 🎯", fontWeight = FontWeight.Bold)
                                }
                            }

                            2 -> {
                                // Step 2: Final Quiz Challenge
                                val targetWord = world.learnedWords[0]
                                val targetEmoji = world.wordEmojis[0]

                                Text(
                                    text = if (appLanguage == "Arabic") "التحدي النهائي! 🎯" else "Final Challenge Quiz! 🎯",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1E293B)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = if (appLanguage == "Arabic") "اختر الصورة الصحيحة لـ: $targetWord" else "Find and tap: $targetWord ($targetEmoji)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    world.learnedWords.shuffled().forEach { optWord ->
                                        val optIdx = world.learnedWords.indexOf(optWord)
                                        val optEmoji = world.wordEmojis[optIdx]
                                        val isSelected = selectedQuizChoice == optWord

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(if (isSelected) Color(0xFFBBF7D0) else Color(0xFFF1F5F9))
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) Color(0xFF16A34A) else Color.Transparent,
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                                .clickable {
                                                    selectedQuizChoice = optWord
                                                    if (optWord == targetWord) {
                                                        quizError = false
                                                        audioEngine.speakPraise()
                                                        audioEngine.speak(if (appLanguage == "Arabic") "إجابة ممتازة! افتح صندوق الكنز!" else "Perfect answer! Open the Treasure Chest!")
                                                        coroutineScope.launch {
                                                            delay(1000)
                                                            activeStep = 3
                                                        }
                                                    } else {
                                                        quizError = true
                                                        audioEngine.speak(if (appLanguage == "Arabic") "حاول مرة أخرى! 😊" else "Try again! 😊")
                                                    }
                                                }
                                                .padding(12.dp)
                                        ) {
                                            Text(text = optEmoji, fontSize = 42.sp)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = optWord, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (quizError) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (appLanguage == "Arabic") "حاول مرة أخرى! انقر على الكلمة الصحيحة" else "Try again! Tap $targetWord",
                                        color = Color.Red,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            3 -> {
                                // Step 3: Treasure Chest Opening Sequence
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Text(
                                        text = "🎁 ${if (appLanguage == "Arabic") "صندوق الكنز السحري!" else "Treasure Chest!"} 🎁",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFD97706)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = if (showChestOpenAnimation) "👑 🪙 ⭐ 🏅" else "🎁",
                                        fontSize = 64.sp,
                                        modifier = Modifier.clickable {
                                            showChestOpenAnimation = true
                                            audioEngine.speakPraise()
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    if (!showChestOpenAnimation) {
                                        Text(
                                            text = if (appLanguage == "Arabic") "انقر على الصندوق لفتحه!" else "Tap the chest to open!",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB45309)
                                        )
                                    } else {
                                        Text(
                                            text = if (appLanguage == "Arabic") "تم إنقاذ الحرف! حصلت على +15 نجمة و +20 قطعة نقدية!" else "Letter Rescued! Earned +15 Stars & +20 Coins!",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF16A34A),
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            // Award rewards & unlock next world
                                            repository.addStars(15)
                                            repository.addCoins(20)
                                            repository.addLearnedWords(world.learnedWords)

                                            userStars = repository.getStars()
                                            userCoins = repository.getCoins()

                                            if (world.id == unlockedWorldIdx) {
                                                repository.unlockNextWorld(world.id)
                                                unlockedWorldIdx = repository.getAdventureUnlockedWorld()
                                            }

                                            if (world.id == 25) {
                                                showConfetti = true
                                                showCertificateDialog = true
                                            }

                                            activeWorld = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                        shape = RoundedCornerShape(18.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(if (appLanguage == "Arabic") "جمع المكافآت 🌟" else "Collect Rewards 🌟", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }

        // Final Master Explorer Certificate Dialog when finishing World Z (id 25)
        if (showCertificateDialog) {
            AlertDialog(
                onDismissRequest = { showCertificateDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            showCertificateDialog = false
                            showConfetti = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(if (appLanguage == "Arabic") "استلام وسام البطل 🏆" else "Claim Hero Badge 🏆", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("🏆 HERO OF ALPHABET KINGDOM 🏆", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFD97706))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(if (appLanguage == "Arabic") "شهادة التخرج والإتقان A - Z" else "Mastery Certificate A to Z", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                    }
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("🦁", fontSize = 54.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (appLanguage == "Arabic") {
                                "تهانينا يا بطل!\n\nلقد أكملت جميع الـ 26 عالماً بنجاح، وأنقذت كل الحروف من A إلى Z وتعلمت أكثر من 80 كلمة إنجليزية!"
                            } else {
                                "Congratulations Hero!\n\nYou completed all 26 Worlds, saved every letter from A to Z, and learned over 80 English vocabulary words!"
                            },
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF475569)
                        )
                    }
                },
                containerColor = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(28.dp)
            )
        }

        ConfettiOverlay(isVisible = showConfetti)
    }
}
