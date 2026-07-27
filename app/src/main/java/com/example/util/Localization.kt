package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.LayoutDirection

val LocalLanguage = compositionLocalOf { "English" }

object Localization {

    private val translations = mapOf(
        // General / Nav
        "app_name" to mapOf("English" to "KK Kids Learning App", "Arabic" to "تطبيق كي كي لتعليم الأطفال"),
        "home" to mapOf("English" to "Home", "Arabic" to "الرئيسية"),
        "profile_settings" to mapOf("English" to "Profile & Settings", "Arabic" to "الملف الشخصي والإعدادات"),
        "stars" to mapOf("English" to "Stars", "Arabic" to "النجوم"),
        "coins" to mapOf("English" to "Coins", "Arabic" to "العملات"),
        "streak" to mapOf("English" to "Streak", "Arabic" to "المتابعة"),
        "days" to mapOf("English" to "Days", "Arabic" to "أيام"),
        "level" to mapOf("English" to "Level 3 Explorer", "Arabic" to "المستكشف المستوى 3"),
        "age" to mapOf("English" to "Age", "Arabic" to "العمر"),
        "back" to mapOf("English" to "Back", "Arabic" to "رجوع"),
        "save" to mapOf("English" to "Save", "Arabic" to "حفظ"),
        "cancel" to mapOf("English" to "Cancel", "Arabic" to "إلغاء"),
        "unlock" to mapOf("English" to "Unlock", "Arabic" to "فتح"),
        "close" to mapOf("English" to "Close", "Arabic" to "إغلاق"),
        "ok" to mapOf("English" to "OK", "Arabic" to "حسناً"),

        // Settings Section
        "settings_header" to mapOf("English" to "Settings ⚙️", "Arabic" to "الإعدادات ⚙️"),
        "audio_voice_settings" to mapOf("English" to "Audio & Voice Settings 🔊", "Arabic" to "إعدادات الصوت والكلام 🔊"),
        "voice_volume" to mapOf("English" to "Voice Volume 🗣️", "Arabic" to "مستوى صوت الكلام 🗣️"),
        "voice_style" to mapOf("English" to "Voice Style 🎙️", "Arabic" to "نمط الصوت 🎙️"),
        "voice_child" to mapOf("English" to "👧 Child Voice", "Arabic" to "👧 صوت طفل"),
        "voice_female" to mapOf("English" to "👩 Female Teacher", "Arabic" to "👩 معلمة"),
        "voice_male" to mapOf("English" to "👨 Male Teacher", "Arabic" to "👨 معلم"),
        "bg_music" to mapOf("English" to "Background Music 🎵", "Arabic" to "الموسيقى الخلفية 🎵"),
        "music_volume" to mapOf("English" to "Music Volume 🎶", "Arabic" to "مستوى الموسيقى 🎶"),
        "sound_fx" to mapOf("English" to "Sound Effects 🔔", "Arabic" to "المؤثرات الصوتية 🔔"),
        "gameplay_difficulty" to mapOf("English" to "Gameplay & Difficulty 🎮", "Arabic" to "أسلوب اللعب والصعوبة 🎮"),
        "difficulty_level" to mapOf("English" to "Difficulty Level", "Arabic" to "مستوى الصعوبة"),
        "easy" to mapOf("English" to "Easy", "Arabic" to "سهل"),
        "medium" to mapOf("English" to "Medium", "Arabic" to "متوسط"),
        "hard" to mapOf("English" to "Hard", "Arabic" to "صعب"),
        "tracing_sensitivity" to mapOf("English" to "Tracing Sensitivity ✏️", "Arabic" to "حساسية الرسم والتتبع ✏️"),
        "low" to mapOf("English" to "Low", "Arabic" to "منخفض"),
        "high" to mapOf("English" to "High", "Arabic" to "عالي"),
        "accessibility_lang" to mapOf("English" to "Accessibility & Language 🌐", "Arabic" to "إمكانية الوصول واللغة 🌐"),
        "large_text_mode" to mapOf("English" to "Large Text Mode 🔤", "Arabic" to "وضع النص الكبير 🔤"),
        "language" to mapOf("English" to "Language 🌐", "Arabic" to "اللغة 🌐"),
        "english" to mapOf("English" to "🇺🇸 English", "Arabic" to "🇺🇸 English"),
        "arabic" to mapOf("English" to "🇪🇬 العربية", "Arabic" to "🇪🇬 العربية"),
        "reset_progress_parent" to mapOf("English" to "Reset Progress (Parents Only 🔒)", "Arabic" to "إعادة ضبط التقدم (لأولياء الأمور 🔒)"),
        "parental_gate" to mapOf("English" to "Parental Gate 🔒", "Arabic" to "بوابة أولياء الأمور 🔒"),
        "enter_parent_pin" to mapOf("English" to "Please enter Parent PIN (Default: 1234):", "Arabic" to "الرجاء إدخال رمز الأمان (الافتراضي: 1234):"),
        "change_pin_btn" to mapOf("English" to "Change Parent PIN 🔑", "Arabic" to "تغيير رمز الأمان 🔑"),
        "new_pin_prompt" to mapOf("English" to "Enter new 4-digit PIN:", "Arabic" to "أدخل رمز جديد من 4 أرقام:"),
        "pin_updated" to mapOf("English" to "Parent PIN updated successfully!", "Arabic" to "تم تحديث رمز الأمان بنجاح!"),
        "pin_error" to mapOf("English" to "Incorrect PIN!", "Arabic" to "رمز غير صحيح!"),
        "edit_profile" to mapOf("English" to "Edit Child Profile 👤", "Arabic" to "تعديل ملف الطفل 👤"),
        "child_name_label" to mapOf("English" to "Child Name", "Arabic" to "اسم الطفل"),
        "choose_avatar" to mapOf("English" to "Choose Avatar:", "Arabic" to "اختر الرمز:"),
        "learning_analytics" to mapOf("English" to "Learning Analytics 📊", "Arabic" to "تحليلات التعلم 📊"),
        "adaptive_recommendation" to mapOf("English" to "Adaptive Recommendation 💡", "Arabic" to "توصية التكيف 💡"),
        "listening" to mapOf("English" to "Listening", "Arabic" to "الاستماع"),
        "writing_tracing" to mapOf("English" to "Writing & Tracing", "Arabic" to "الكتابة والتتبع"),
        "tracing" to mapOf("English" to "Tracing & Writing", "Arabic" to "التتبع والكتابة"),
        "spelling_typing" to mapOf("English" to "Spelling & Typing", "Arabic" to "التهجئة والكتابة"),
        "reading" to mapOf("English" to "Reading", "Arabic" to "القراءة"),
        "matching" to mapOf("English" to "Matching", "Arabic" to "المطابقة"),

        // Categories
        "cat_reading" to mapOf("English" to "Reading & Phonics 📚", "Arabic" to "القراءة واللفظ 📚"),
        "cat_writing" to mapOf("English" to "Writing & Tracing ✏️", "Arabic" to "الكتابة والتتبع ✏️"),
        "cat_math" to mapOf("English" to "Math & Numbers 🔢", "Arabic" to "الرياضيات والأرقام 🔢"),
        "cat_games" to mapOf("English" to "Fun Games 🎮", "Arabic" to "ألعاب ممتعة 🎮"),
        "cat_songs" to mapOf("English" to "Songs & Music 🎵", "Arabic" to "الأغاني والموسيقى 🎵"),
        "cat_adventure" to mapOf("English" to "Adventure Island 🏝️", "Arabic" to "جزيرة المغامرات 🏝️"),
        "cat_parent" to mapOf("English" to "Parent Dashboard 📈", "Arabic" to "لوحة تحكم الوالدين 📈"),
        "cat_dictionary" to mapOf("English" to "Picture Dictionary 📖", "Arabic" to "قاموس الصور 📖"),
        "cat_rewards" to mapOf("English" to "My Rewards 🏆", "Arabic" to "مكافآتي 🏆")
    )

    fun tr(key: String, lang: String = "English"): String {
        return translations[key]?.get(lang) ?: translations[key]?.get("English") ?: key
    }

    fun getLayoutDirection(lang: String): LayoutDirection {
        return if (lang == "Arabic") LayoutDirection.Rtl else LayoutDirection.Ltr
    }
}
