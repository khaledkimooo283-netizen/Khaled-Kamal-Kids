package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.ui.graphics.Color
import java.util.Locale

object NetworkUtils {
    fun isInternetAvailable(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
            val activeNetwork = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            false
        }
    }
}

data class SpeakingPromptItem(
    val category: String, // "Letters", "Numbers", "Days", "Months", "Words", "Sentences"
    val targetText: String,
    val phoneticHint: String,
    val emoji: String,
    val difficultyColor: Color
)

data class PronunciationResult(
    val score: Int,
    val scoreRange: String,
    val ratingTitle: String,
    val isAccepted: Boolean,
    val feedbackMessage: String,
    val recognizedSpeech: String
)

object PronunciationEvaluator {

    private val LETTER_HOMOPHONES = mapOf(
        "a" to setOf("a", "ay", "ei", "eh", "ey", "hey", "a."),
        "b" to setOf("b", "be", "bee", "b."),
        "c" to setOf("c", "see", "sea", "c."),
        "d" to setOf("d", "dee", "d."),
        "e" to setOf("e", "ee", "ea", "e."),
        "f" to setOf("f", "eff", "ef", "f."),
        "g" to setOf("g", "gee", "ji", "g."),
        "h" to setOf("h", "aitch", "eitch", "ache", "h."),
        "i" to setOf("i", "eye", "ai", "ay", "i."),
        "j" to setOf("j", "jay", "j."),
        "k" to setOf("k", "kay", "k."),
        "l" to setOf("l", "el", "ell", "l."),
        "m" to setOf("m", "em", "m."),
        "n" to setOf("n", "en", "n."),
        "o" to setOf("o", "oh", "owe", "o."),
        "p" to setOf("p", "pee", "pea", "p."),
        "q" to setOf("q", "cue", "queue", "q."),
        "r" to setOf("r", "are", "ar", "r."),
        "s" to setOf("s", "ess", "es", "s."),
        "t" to setOf("t", "tea", "tee", "t."),
        "u" to setOf("u", "you", "yew", "u."),
        "v" to setOf("v", "vee", "v."),
        "w" to setOf("w", "double u", "doubleyou", "doubleu", "w."),
        "x" to setOf("x", "ex", "x."),
        "y" to setOf("y", "why", "y."),
        "z" to setOf("z", "zee", "zed", "z.")
    )

    private val NUMBER_EQUIVALENTS = mapOf(
        "zero" to setOf("zero", "0", "oh", "o", "xero", "hero"),
        "0" to setOf("zero", "0", "oh", "o", "xero", "hero"),
        "one" to setOf("one", "1", "won", "wan"),
        "1" to setOf("one", "1", "won", "wan"),
        "two" to setOf("two", "2", "to", "too", "tu"),
        "2" to setOf("two", "2", "to", "too", "tu"),
        "three" to setOf("three", "3", "tree", "free"),
        "3" to setOf("three", "3", "tree", "free"),
        "four" to setOf("four", "4", "for", "fore", "fur"),
        "4" to setOf("four", "4", "for", "fore", "fur"),
        "five" to setOf("five", "5", "fiv", "fyve"),
        "5" to setOf("five", "5", "fiv", "fyve"),
        "six" to setOf("six", "6", "siks", "seks"),
        "6" to setOf("six", "6", "siks", "seks"),
        "seven" to setOf("seven", "7", "sevin"),
        "7" to setOf("seven", "7", "sevin"),
        "eight" to setOf("eight", "8", "ate", "ait"),
        "8" to setOf("eight", "8", "ate", "ait"),
        "nine" to setOf("nine", "9", "nein"),
        "9" to setOf("nine", "9", "nein"),
        "ten" to setOf("ten", "10", "tin"),
        "10" to setOf("ten", "10", "tin"),
        "eleven" to setOf("eleven", "11", "aleven"),
        "11" to setOf("eleven", "11", "aleven"),
        "twelve" to setOf("twelve", "12", "twelv"),
        "12" to setOf("twelve", "12", "twelv"),
        "thirteen" to setOf("thirteen", "13", "thirdteen"),
        "13" to setOf("thirteen", "13", "thirdteen"),
        "fourteen" to setOf("fourteen", "14"),
        "14" to setOf("fourteen", "14"),
        "fifteen" to setOf("fifteen", "15"),
        "15" to setOf("fifteen", "15"),
        "sixteen" to setOf("sixteen", "16"),
        "16" to setOf("sixteen", "16"),
        "seventeen" to setOf("seventeen", "17"),
        "17" to setOf("seventeen", "17"),
        "eighteen" to setOf("eighteen", "18"),
        "18" to setOf("eighteen", "18"),
        "nineteen" to setOf("nineteen", "19"),
        "19" to setOf("nineteen", "19"),
        "twenty" to setOf("twenty", "20", "twentie"),
        "20" to setOf("twenty", "20", "twentie"),
        "thirty" to setOf("thirty", "30"),
        "30" to setOf("thirty", "30"),
        "forty" to setOf("forty", "40"),
        "40" to setOf("forty", "40"),
        "fifty" to setOf("fifty", "50"),
        "50" to setOf("fifty", "50"),
        "hundred" to setOf("hundred", "100", "one hundred"),
        "100" to setOf("hundred", "100", "one hundred")
    )

    private val DAY_EQUIVALENTS = mapOf(
        "sunday" to setOf("sunday", "sun day", "sun"),
        "monday" to setOf("monday", "mon day", "mon"),
        "tuesday" to setOf("tuesday", "tues day", "tue"),
        "wednesday" to setOf("wednesday", "wednes day", "wed day", "wed"),
        "thursday" to setOf("thursday", "thurs day", "thu"),
        "friday" to setOf("friday", "fri day", "fri"),
        "saturday" to setOf("saturday", "sat day", "sat")
    )

    private val MONTH_EQUIVALENTS = mapOf(
        "january" to setOf("january", "jan"),
        "february" to setOf("february", "febuary", "feb"),
        "march" to setOf("march", "mar"),
        "april" to setOf("april", "apr"),
        "may" to setOf("may"),
        "june" to setOf("june", "jun"),
        "july" to setOf("july", "jul"),
        "august" to setOf("august", "aug"),
        "september" to setOf("september", "sep", "sept"),
        "october" to setOf("october", "oct"),
        "november" to setOf("november", "nov"),
        "december" to setOf("december", "dec")
    )

    fun levenshteinDistance(lhs: CharSequence, rhs: CharSequence): Int {
        val lhsLength = lhs.length
        val rhsLength = rhs.length
        var cost = IntArray(lhsLength + 1) { it }
        var newCost = IntArray(lhsLength + 1)

        for (i in 1..rhsLength) {
            newCost[0] = i
            for (j in 1..lhsLength) {
                val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1
                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1
                newCost[j] = minOf(costReplace, minOf(costInsert, costDelete))
            }
            val swap = cost
            cost = newCost
            newCost = swap
        }
        return cost[lhsLength]
    }

    fun evaluateSingleCandidate(targetText: String, speechInput: String): PronunciationResult {
        val hasNonEnglish = speechInput.any { it in '\u0600'..'\u06FF' }
        if (hasNonEnglish) {
            return PronunciationResult(
                score = 10,
                scoreRange = "Below 70%",
                ratingTitle = "Try Again ❌",
                isAccepted = false,
                feedbackMessage = "Please speak in English! Try again.",
                recognizedSpeech = speechInput
            )
        }

        val cleanTarget = targetText.trim().lowercase(Locale.ROOT).replace(Regex("[^a-z0-9 ]"), "")
        val cleanInput = speechInput.trim().lowercase(Locale.ROOT).replace(Regex("[^a-z0-9 ]"), "")

        if (cleanInput.isEmpty() || cleanInput == "silent" || cleanInput == "silence") {
            return PronunciationResult(
                score = 0,
                scoreRange = "Below 70%",
                ratingTitle = "Try Again ❌",
                isAccepted = false,
                feedbackMessage = "I couldn't hear you. Try again.",
                recognizedSpeech = "[Silence]"
            )
        }

        val inputWords = cleanInput.split(" ").filter { it.isNotEmpty() }
        val targetWords = cleanTarget.split(" ").filter { it.isNotEmpty() }

        var isAccepted = false
        var finalScore = 0

        // 1. Single Letter evaluation (e.g., target = "a", "b", "c", "e")
        if (cleanTarget.length == 1 && cleanTarget in "a".."z") {
            val homophones = LETTER_HOMOPHONES[cleanTarget] ?: setOf(cleanTarget)
            val isMatch = cleanInput == cleanTarget ||
                    cleanInput in homophones ||
                    inputWords.any { it == cleanTarget || it in homophones }

            if (isMatch) {
                isAccepted = true
                finalScore = 100
            }
        }
        // 2. Number evaluation (e.g., target = "zero", "one", "1", "20")
        else if (NUMBER_EQUIVALENTS.containsKey(cleanTarget)) {
            val equivalents = NUMBER_EQUIVALENTS[cleanTarget] ?: setOf(cleanTarget)
            val isMatch = cleanInput in equivalents ||
                    inputWords.any { it in equivalents } ||
                    cleanInput == cleanTarget

            if (isMatch) {
                isAccepted = true
                finalScore = 100
            }
        }
        // 3. Day of Week evaluation (e.g., "sunday")
        else if (DAY_EQUIVALENTS.containsKey(cleanTarget)) {
            val equivalents = DAY_EQUIVALENTS[cleanTarget] ?: setOf(cleanTarget)
            val isMatch = cleanInput in equivalents ||
                    inputWords.any { it in equivalents }

            if (isMatch) {
                isAccepted = true
                finalScore = 100
            }
        }
        // 4. Month evaluation (e.g., "january")
        else if (MONTH_EQUIVALENTS.containsKey(cleanTarget)) {
            val equivalents = MONTH_EQUIVALENTS[cleanTarget] ?: setOf(cleanTarget)
            val isMatch = cleanInput in equivalents ||
                    inputWords.any { it in equivalents }

            if (isMatch) {
                isAccepted = true
                finalScore = 100
            }
        }
        // 5. Single Word evaluation (e.g., "apple", "cat", "dog", "before", "watermelon")
        else if (targetWords.size == 1) {
            val tWord = targetWords[0]
            val cleanNoSpaceInput = cleanInput.replace(" ", "")
            val cleanNoSpaceTarget = cleanTarget.replace(" ", "")

            if (cleanInput == cleanTarget ||
                cleanNoSpaceInput == cleanNoSpaceTarget ||
                inputWords.contains(tWord) ||
                (inputWords.size == 2 && (inputWords.contains("a") || inputWords.contains("an") || inputWords.contains("the")) && inputWords.contains(tWord))
            ) {
                isAccepted = true
                finalScore = 100
            } else {
                // Check closest word in spoken phrase using Levenshtein distance
                var bestScore = 0
                for (inW in inputWords) {
                    val dist = levenshteinDistance(tWord, inW)
                    if (tWord.length <= 4) {
                        if (dist == 0) {
                            bestScore = 100
                            break
                        } else if (dist == 1 && tWord.length >= 3) {
                            bestScore = maxOf(bestScore, 85)
                        }
                    } else if (tWord.length in 5..7) {
                        if (dist == 0) {
                            bestScore = 100
                            break
                        } else if (dist == 1) {
                            bestScore = maxOf(bestScore, 90)
                        } else if (dist == 2) {
                            bestScore = maxOf(bestScore, 78)
                        }
                    } else { // length >= 8
                        if (dist <= 1) {
                            bestScore = maxOf(bestScore, 92)
                        } else if (dist <= 2) {
                            bestScore = maxOf(bestScore, 82)
                        } else if (dist <= 3) {
                            bestScore = maxOf(bestScore, 75)
                        }
                    }
                }

                if (bestScore >= 75) {
                    isAccepted = true
                    finalScore = bestScore
                } else {
                    isAccepted = false
                    finalScore = maxOf(bestScore, 30)
                }
            }
        }
        // 6. Sentence / Phrase evaluation (e.g. "This is a cat", "I like apples")
        else {
            var matchedCount = 0
            for (tWord in targetWords) {
                val foundMatch = inputWords.any { inW ->
                    inW == tWord || (tWord.length >= 4 && levenshteinDistance(tWord, inW) <= 1)
                }
                if (foundMatch) {
                    matchedCount++
                }
            }

            val matchRatio = matchedCount.toFloat() / targetWords.size.toFloat()
            if (matchRatio >= 0.55f) { // 55% or more words matched in sentence
                isAccepted = true
                finalScore = maxOf(75, (matchRatio * 100).toInt())
            } else {
                isAccepted = false
                finalScore = (matchRatio * 100).toInt()
            }
        }

        return if (isAccepted) {
            PronunciationResult(
                score = finalScore,
                scoreRange = if (finalScore >= 90) "95–100%" else "85–94%",
                ratingTitle = "Great Job! 🎉",
                isAccepted = true,
                feedbackMessage = "Great job!",
                recognizedSpeech = speechInput
            )
        } else {
            PronunciationResult(
                score = finalScore,
                scoreRange = "Below 70%",
                ratingTitle = "Try Again ❌",
                isAccepted = false,
                feedbackMessage = "Try again.",
                recognizedSpeech = speechInput
            )
        }
    }

    fun evaluatePronunciationCandidates(targetText: String, candidates: List<String>): PronunciationResult {
        if (candidates.isEmpty()) {
            return PronunciationResult(
                score = 0,
                scoreRange = "Below 70%",
                ratingTitle = "Try Again ❌",
                isAccepted = false,
                feedbackMessage = "I couldn't hear you. Try again.",
                recognizedSpeech = "[Silence]"
            )
        }

        val evaluated = candidates.map { evaluateSingleCandidate(targetText, it) }
        // Pick the result with highest score or accepted result
        return evaluated.maxByOrNull { it.score } ?: evaluated.first()
    }
}
