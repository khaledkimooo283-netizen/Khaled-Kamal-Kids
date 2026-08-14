package com.example

import com.example.ui.games.RunChoiceItem
import com.example.ui.games.RunQuestion
import com.example.ui.games.RunQuestionType
import org.junit.Assert.*
import org.junit.Test

class RunAndLearnQaTest {

    @Test
    fun testWorld1QuestionsHave2ChoicesAndCorrectTarget() {
        val questions = getTestWorld1Questions()
        assertEquals("World 1 must have 5 questions", 5, questions.size)

        questions.forEach { q ->
            assertEquals("World 1 questions should have 2 choices", 2, q.choices.size)
            val correctChoices = q.choices.filter { it.isCorrect }
            assertEquals("Each question must have exactly 1 correct choice", 1, correctChoices.size)
        }
    }

    @Test
    fun testWorld2And3QuestionsHave3ChoicesAndValidVocabulary() {
        val w2Questions = getTestWorld2Questions()
        val w3Questions = getTestWorld3Questions()

        w2Questions.forEach { q ->
            assertEquals("World 2 questions should have 3 choices", 3, q.choices.size)
            assertTrue("Question must have 1 correct choice", q.choices.count { it.isCorrect } == 1)
        }

        w3Questions.forEach { q ->
            assertEquals("World 3 questions should have 3 choices", 3, q.choices.size)
            assertTrue("Question must have 1 correct choice", q.choices.count { it.isCorrect } == 1)
        }
    }

    @Test
    fun testQuestionTypesContainPictureAndSentenceTypes() {
        val w4Questions = getTestWorld4Questions()
        val questionTypes = w4Questions.map { it.type }.toSet()

        assertTrue("World 4 must contain SIMPLE_ENGLISH questions", questionTypes.contains(RunQuestionType.SIMPLE_ENGLISH))
        assertTrue("World 4 must contain COUNTING questions", questionTypes.contains(RunQuestionType.COUNTING))
    }

    private fun getTestWorld1Questions(): List<RunQuestion> {
        return listOf(
            RunQuestion(
                id = "w1_q1",
                type = RunQuestionType.PICTURE_QUESTION,
                voicePrompt = "What is this?",
                promptText = "What is this?",
                promptEmoji = "🍎",
                choices = listOf(
                    RunChoiceItem("c1", "Apple", "🍎", true),
                    RunChoiceItem("c2", "Cat", "🐱", false)
                )
            ),
            RunQuestion(
                id = "w1_q2",
                type = RunQuestionType.FIND_THE_WORD,
                voicePrompt = "Find the ball!",
                promptText = "Find the ball",
                choices = listOf(
                    RunChoiceItem("c1", "Ball", "⚽", true),
                    RunChoiceItem("c2", "Dog", "🐶", false)
                )
            ),
            RunQuestion(
                id = "w1_q3",
                type = RunQuestionType.FIND_THE_PICTURE,
                voicePrompt = "Find the cat!",
                promptText = "Find the cat",
                choices = listOf(
                    RunChoiceItem("c1", "Cat", "🐱", true),
                    RunChoiceItem("c2", "Car", "🚗", false)
                )
            ),
            RunQuestion(
                id = "w1_q4",
                type = RunQuestionType.PICTURE_QUESTION,
                voicePrompt = "What is this?",
                promptText = "What is this?",
                promptEmoji = "☀️",
                choices = listOf(
                    RunChoiceItem("c1", "Sun", "☀️", true),
                    RunChoiceItem("c2", "Cup", "☕", false)
                )
            ),
            RunQuestion(
                id = "w1_q5",
                type = RunQuestionType.FIND_THE_WORD,
                voicePrompt = "Find the book!",
                promptText = "Find the book",
                choices = listOf(
                    RunChoiceItem("c1", "Book", "📖", true),
                    RunChoiceItem("c2", "Bag", "🎒", false)
                )
            )
        )
    }

    private fun getTestWorld2Questions(): List<RunQuestion> {
        return listOf(
            RunQuestion(
                id = "w2_q1",
                type = RunQuestionType.FIND_THE_PICTURE,
                voicePrompt = "Find the fish!",
                promptText = "Find the fish",
                choices = listOf(
                    RunChoiceItem("c1", "Fish", "🐟", true),
                    RunChoiceItem("c2", "Bird", "🐦", false),
                    RunChoiceItem("c3", "Tree", "🌳", false)
                )
            )
        )
    }

    private fun getTestWorld3Questions(): List<RunQuestion> {
        return listOf(
            RunQuestion(
                id = "w3_q1",
                type = RunQuestionType.NUMBERS,
                voicePrompt = "Which one is three?",
                promptText = "Which one is 3?",
                choices = listOf(
                    RunChoiceItem("c1", "1", "1️⃣", false),
                    RunChoiceItem("c2", "3", "3️⃣", true),
                    RunChoiceItem("c3", "5", "5️⃣", false)
                )
            )
        )
    }

    private fun getTestWorld4Questions(): List<RunQuestion> {
        return listOf(
            RunQuestion(
                id = "w4_q1",
                type = RunQuestionType.SIMPLE_ENGLISH,
                voicePrompt = "Complete the sentence: The cat is...",
                promptText = "The cat is ___.",
                choices = listOf(
                    RunChoiceItem("c1", "Big", "🐘", true),
                    RunChoiceItem("c2", "Apple", "🍎", false),
                    RunChoiceItem("c3", "Car", "🚗", false)
                )
            ),
            RunQuestion(
                id = "w4_q3",
                type = RunQuestionType.COUNTING,
                voicePrompt = "How many stars?",
                promptText = "How many stars?",
                promptEmoji = "⭐ ⭐ ⭐ ⭐ ⭐",
                choices = listOf(
                    RunChoiceItem("c1", "3", "3️⃣", false),
                    RunChoiceItem("c2", "5", "5️⃣", true),
                    RunChoiceItem("c3", "2", "2️⃣", false)
                )
            )
        )
    }
}
