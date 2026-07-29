package com.example.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SpeechAndSoundEngine
import com.example.data.KkDataRepository

@Composable
fun HomeScreen(
    repository: KkDataRepository,
    audioEngine: SpeechAndSoundEngine,
    onNavigateToGame: (String) -> Unit
) {
    val totalStars by remember { mutableIntStateOf(repository.getStars()) }
    val appLanguage by remember { derivedStateOf { repository.getLanguage() } }

    var activeHubDialog by remember { mutableStateOf<String?>(null) } // "letters", "numbers", "games"

    LaunchedEffect(Unit) {
        val welcomeSpeech = if (appLanguage == "Arabic") {
            "مرحباً بك في تطبيقات خالد كمال للأطفال! اختر أقسام التعلم أو الألعاب! ✨"
        } else {
            "Welcome to Khaled Kamal Kids! Choose a fun section below! ✨"
        }
        audioEngine.speak(welcomeSpeech)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFEDE9FE), // Soft Lavender Top
                        Color(0xFFFCE7F3), // Pastel Pink Middle
                        Color(0xFFFAF5FF)  // Off-white Soft Violet Bottom
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Rainbow Header Arch Graphic
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .width(220.dp)
                        .height(110.dp)
                ) {
                    val strokeWidth = 11.dp.toPx()
                    val colors = listOf(
                        Color(0xFFEC4899), // Pink (outer)
                        Color(0xFFFB923C), // Orange
                        Color(0xFF34D399), // Green
                        Color(0xFF38BDF8), // Cyan
                        Color(0xFFA78BFA)  // Purple (inner)
                    )

                    val center = Offset(size.width / 2f, size.height * 0.98f)
                    val baseRadius = size.width * 0.22f

                    colors.forEachIndexed { index, color ->
                        val r = baseRadius + (4 - index) * (strokeWidth + 2.5.dp.toPx())
                        drawArc(
                            color = color,
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = false,
                            topLeft = Offset(center.x - r, center.y - r),
                            size = Size(r * 2, r * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 2. Title "Khaled Kamal Kids"
            val titleAnnotated = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF4F46E5), fontWeight = FontWeight.Black)) {
                    append("Khaled ")
                }
                withStyle(style = SpanStyle(color = Color(0xFFA855F7), fontWeight = FontWeight.Black)) {
                    append("Kamal ")
                }
                withStyle(style = SpanStyle(color = Color(0xFFEC4899), fontWeight = FontWeight.Black)) {
                    append("Kids")
                }
            }

            Text(
                text = titleAnnotated,
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Arabic / English Subtitle Banner
            Text(
                text = if (appLanguage == "Arabic") "تعلّم اللغة الإنجليزية بمتعة وسحر ✨" else "Learn English with Fun & Magic ✨",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6B21A8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Main 6 Navigation Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                // Card 1: Learn Letters (Purple)
                item {
                    OriginalHomeCard(
                        title = if (appLanguage == "Arabic") "تعلم الحروف" else "Learn Letters",
                        backgroundColor = Color(0xFF8B5CF6),
                        badgeContent = {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFCCFBF1))
                                    .border(2.dp, Color(0xFF0D9488), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "abc",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F766E)
                                )
                            }
                        },
                        onClick = {
                            audioEngine.speak("Learn Letters!")
                            activeHubDialog = "letters"
                        }
                    )
                }

                // Card 2: Learn Numbers (Pink)
                item {
                    OriginalHomeCard(
                        title = if (appLanguage == "Arabic") "تعلم الأرقام" else "Learn Numbers",
                        backgroundColor = Color(0xFFF472B6),
                        badgeContent = {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFCCFBF1))
                                    .border(2.dp, Color(0xFF0D9488), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "1 2\n3 4",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F766E),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 11.sp
                                )
                            }
                        },
                        onClick = {
                            audioEngine.speak("Learn Numbers!")
                            activeHubDialog = "numbers"
                        }
                    )
                }

                // Card 3: Games (Sky Blue)
                item {
                    OriginalHomeCard(
                        title = if (appLanguage == "Arabic") "الألعاب" else "Games",
                        backgroundColor = Color(0xFF38BDF8),
                        badgeContent = {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.9f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🎮", fontSize = 24.sp)
                            }
                        },
                        onClick = {
                            audioEngine.speak("Educational Mini Games!")
                            activeHubDialog = "games"
                        }
                    )
                }

                // Card 4: Rewards (Amber Yellow)
                item {
                    OriginalHomeCard(
                        title = if (appLanguage == "Arabic") "المكافآت" else "Rewards",
                        backgroundColor = Color(0xFFFBBF24),
                        badgeContent = {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.9f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🏆", fontSize = 24.sp)
                            }
                        },
                        onClick = {
                            audioEngine.speak("Trophy & Rewards Room!")
                            onNavigateToGame("rewards")
                        }
                    )
                }

                // Card 5: Settings (Mint Green)
                item {
                    OriginalHomeCard(
                        title = if (appLanguage == "Arabic") "الإعدادات" else "Settings",
                        backgroundColor = Color(0xFF34D399),
                        badgeContent = {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.9f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "Settings",
                                    tint = Color(0xFF047857),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        },
                        onClick = {
                            audioEngine.speak("Settings & Profile!")
                            onNavigateToGame("profile_settings")
                        }
                    )
                }

                // Card 6: Parents Area (Coral Orange)
                item {
                    OriginalHomeCard(
                        title = if (appLanguage == "Arabic") "منطقة الوالدين" else "Parents Area",
                        backgroundColor = Color(0xFFFB923C),
                        badgeContent = {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.9f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Groups,
                                    contentDescription = "Parents Area",
                                    tint = Color(0xFFC2410C),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        },
                        onClick = {
                            audioEngine.speak("Parent Dashboard!")
                            onNavigateToGame("parent_progress")
                        }
                    )
                }
            }
        }

        // 5. Floating Bottom Star Pill
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .shadow(elevation = 6.dp, shape = CircleShape)
                    .clickable {
                        audioEngine.speak("You collected $totalStars stars!")
                        onNavigateToGame("rewards")
                    },
                shape = CircleShape,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Stars",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (appLanguage == "Arabic") "تم جمع $totalStars نجوم ✨" else "$totalStars stars collected",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6D28D9)
                    )
                }
            }
        }

        // 6. Hub Selection Dialogs for Letters, Numbers & Games
        activeHubDialog?.let { category ->
            AlertDialog(
                onDismissRequest = { activeHubDialog = null },
                confirmButton = {
                    TextButton(onClick = { activeHubDialog = null }) {
                        Text(if (appLanguage == "Arabic") "إغلاق" else "Close", fontWeight = FontWeight.Bold)
                    }
                },
                shape = RoundedCornerShape(28.dp),
                containerColor = Color.White,
                title = {
                    Text(
                        text = when (category) {
                            "letters" -> if (appLanguage == "Arabic") "ألعاب أنشطة الحروف 🔤" else "Letter Learning Games 🔤"
                            "numbers" -> if (appLanguage == "Arabic") "ألعاب أنشطة الأرقام 🔢" else "Number Learning Games 🔢"
                            else -> if (appLanguage == "Arabic") "مكتبة الألعاب التعليمية 🎮" else "Educational Games Hub 🎮"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF4C1D95)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        when (category) {
                            "letters" -> {
                                HubOptionRow(
                                    emoji = "🗺️",
                                    title = if (appLanguage == "Arabic") "نمط المغامرة (A-Z)" else "KK Adventure Mode (A-Z)",
                                    subtitle = if (appLanguage == "Arabic") "26 عالم مع قصة وحيوان وكلمات وطبقات!" else "26 unique story worlds & treasure chests!",
                                    badge = "MAIN",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("adventure_mode")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "✏️",
                                    title = if (appLanguage == "Arabic") "الكتابة والتتبع" else "Handwriting Tracing",
                                    subtitle = if (appLanguage == "Arabic") "تتبع الحروف الكبيرة والصغيرة والأرقام" else "Trace uppercase & lowercase letters!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("tracing")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "📖",
                                    title = if (appLanguage == "Arabic") "قاموس المفردات المصور" else "Vocabulary Picture Dictionary",
                                    subtitle = if (appLanguage == "Arabic") "استكشف كلمات الحروف والنطق الصوتي" else "A-Z word dictionary with clear audio!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("dictionary")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "🅰️",
                                    title = if (appLanguage == "Arabic") "مطابقة الكبيرة والصغيرة" else "Capital ↔ Small Match",
                                    subtitle = if (appLanguage == "Arabic") "طابق الحرف الكبير بالحرف الصغير" else "Match uppercase A with lowercase a!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("capital_small")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "🔍",
                                    title = if (appLanguage == "Arabic") "البحث عن الحرف المفقود" else "Find Missing Letter",
                                    subtitle = if (appLanguage == "Arabic") "أكمل الحروف المفقودة في الكلمة" else "Fill in missing letters in words!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("missing_letter")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "⌨️",
                                    title = if (appLanguage == "Arabic") "التهجئة والكتابة" else "Typing & Spelling",
                                    subtitle = if (appLanguage == "Arabic") "اكتب الكلمات الإنجليزية بالكامل" else "Type & spell full English words!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("typing")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "🚂",
                                    title = if (appLanguage == "Arabic") "قطار الحروف" else "Alphabet Train",
                                    subtitle = if (appLanguage == "Arabic") "أكمل تسلسل عربات الحروف" else "Complete train letter sequence!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("train")
                                    }
                                )
                            }
                            "numbers" -> {
                                HubOptionRow(
                                    emoji = "🔢",
                                    title = if (appLanguage == "Arabic") "ترتيب الأرقام (1-20)" else "Sequence Order (1-20)",
                                    subtitle = if (appLanguage == "Arabic") "أكمل تسلسل الأرقام بشكل صحيح" else "Complete number sequence 1 to 20!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("sequence_order")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "✏️",
                                    title = if (appLanguage == "Arabic") "تتبع الأرقام (0-20)" else "Number Tracing (0-20)",
                                    subtitle = if (appLanguage == "Arabic") "تعلم كتابة الأرقام الإنجليزية" else "Learn standard number handwriting!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("tracing")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "🍦",
                                    title = if (appLanguage == "Arabic") "متجر الآيس كريم" else "Ice Cream Counting Shop",
                                    subtitle = if (appLanguage == "Arabic") "عد كرات الآيس كريم اللذيذة" else "Count scoops & serve animals!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("ice_cream")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "🦖",
                                    title = if (appLanguage == "Arabic") "فقس بيض الديناصورات" else "Dino Egg Hatching",
                                    subtitle = if (appLanguage == "Arabic") "عد وافقس بيض الديناصور الصغير" else "Count & hatch cute baby dinosaurs!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("dino_hatch")
                                    }
                                )
                            }
                            else -> {
                                HubOptionRow(
                                    emoji = "🎵",
                                    title = if (appLanguage == "Arabic") "الأغاني والموسيقى" else "Songs & Music Studio",
                                    subtitle = if (appLanguage == "Arabic") "أغاني ABC وكاريوكي رائع" else "Sing along to ABC songs & karaoke!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("songs_music")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "🎣",
                                    title = if (appLanguage == "Arabic") "مغامرة الصيد" else "Fishing Adventure",
                                    subtitle = if (appLanguage == "Arabic") "اصطد الحروف والأرقام السابحة" else "Catch swimming letters & numbers!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("fishing")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "🧠",
                                    title = if (appLanguage == "Arabic") "بطاقات الذاكرة" else "Memory Cards Game",
                                    subtitle = if (appLanguage == "Arabic") "اقلب واكتشف الأزواج المتطابقة" else "Flip & match pair cards!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("memory")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "🎈",
                                    title = if (appLanguage == "Arabic") "فرقعة البالونات" else "Balloon Pop",
                                    subtitle = if (appLanguage == "Arabic") "فرقع البالونات التي تحتوي الحرف المطلوب" else "Pop balloons with target letters!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("balloon_pop")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "🎯",
                                    title = if (appLanguage == "Arabic") "المطابقة والتعلم" else "Match & Learn",
                                    subtitle = if (appLanguage == "Arabic") "طابق الصور بالكلمات والأشكال" else "Match objects & words!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("drag_match")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "🎧",
                                    title = if (appLanguage == "Arabic") "استمع واختر" else "Listen & Choose",
                                    subtitle = if (appLanguage == "Arabic") "استمع للصوت واكتشف الصورة الصحيحة" else "Listen to audio & pick picture!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("listen_choose")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "🐶",
                                    title = if (appLanguage == "Arabic") "أصوات الحيوانات" else "Animal Sounds Kingdom",
                                    subtitle = if (appLanguage == "Arabic") "تعرف على أسماء وأصوات الحيوانات" else "Learn animal names & sounds!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("animals")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "🚀",
                                    title = if (appLanguage == "Arabic") "مغامرة الفضاء" else "Space Adventure",
                                    subtitle = if (appLanguage == "Arabic") "اجمع النجوم الفضائية" else "Fly in space & collect stars!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("space_adv")
                                    }
                                )
                                HubOptionRow(
                                    emoji = "🎨",
                                    title = if (appLanguage == "Arabic") "كتاب التلوين" else "Coloring Book",
                                    subtitle = if (appLanguage == "Arabic") "لون الرسومات بالألوان الممتعة" else "Color fun letter drawings!",
                                    onClick = {
                                        activeHubDialog = null
                                        onNavigateToGame("coloring")
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun OriginalHomeCard(
    title: String,
    backgroundColor: Color,
    badgeContent: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(backgroundColor)
            .clickable { onClick() }
    ) {
        // Bottom-Right Soft Circle Overlay for Aesthetic Depth
        Box(
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 20.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Left Badge Container
            badgeContent()

            // Bottom Title Text
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun HubOptionRow(
    emoji: String,
    title: String,
    subtitle: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    if (badge != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF59E0B))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = badge, fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B)
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Open",
                tint = Color(0xFF94A3B8)
            )
        }
    }
}
