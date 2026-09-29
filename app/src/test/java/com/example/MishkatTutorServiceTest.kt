package com.example

import com.example.mishkat.guardrails.MishkatGuardrails
import com.example.mishkat.model.MishkatRequest
import com.example.mishkat.model.TutorExplanationMode
import com.example.mishkat.model.TutorLevel
import com.example.mishkat.rag.MishkatKnowledgeBase
import com.example.mishkat.rag.MishkatRagRetriever
import com.example.mishkat.service.MishkatTutorServiceImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MishkatTutorServiceTest {

    @Test
    fun testGuardrailOutOfScope_returnsExactRequiredString() {
        val outOfScopeQuery = "ما هي وصفة إعداد كعكة الشوكولاتة؟"
        assertTrue(MishkatGuardrails.isClearlyOutOfScope(outOfScopeQuery))

        val service = MishkatTutorServiceImpl()
        val response = runBlocking {
            service.askMishkat(MishkatRequest(userQuery = outOfScopeQuery))
        }

        assertTrue(response.isOutOfScope)
        assertEquals(MishkatGuardrails.OUT_OF_SCOPE_EXACT_MESSAGE, response.replyText)
    }

    @Test
    fun testGuardrailSanitize_removesChainOfThoughtTags() {
        val rawAiOutput = """
            <thought>
            Thinking process:
            1. Analyze user question about Hafs.
            2. Hafs reads with Qasr from 5 books.
            </thought>
            طرق قصر المنفصل لحفص وردت من طريق عمرو بن الصباح عن الفيل.
        """.trimIndent()

        val sanitized = MishkatGuardrails.sanitizeOutput(rawAiOutput)
        assertFalse(sanitized.contains("<thought>"))
        assertFalse(sanitized.contains("Thinking process"))
        assertTrue(sanitized.contains("طرق قصر المنفصل لحفص"))
    }

    @Test
    fun testRagRetriever_findsCorrectChaptersAndPages() {
        val retriever = MishkatRagRetriever()
        val chunks = retriever.retrieveRelevantContext("ما هي طرق قصر المنفصل لحفص وما الذي يمتنع عليه؟")

        assertTrue(chunks.isNotEmpty())
        val topChunk = chunks.first()
        assertTrue(topChunk.content.contains("الحمامي") || topChunk.content.contains("عمرو بن الصباح"))
        assertTrue(topChunk.pageStart in 60..75)
    }

    @Test
    fun testTutorService_askInsideBook_citesBookAndReturnsAnswer() = runBlocking {
        val service = MishkatTutorServiceImpl()
        val request = MishkatRequest(
            userQuery = "ما هي كتب قصر المنفصل لحفص؟ وما حكم السكت عليه؟",
            tutorLevel = TutorLevel.INTERMEDIATE,
            explanationMode = TutorExplanationMode.DIRECT
        )

        val response = service.askMishkat(request)
        assertFalse(response.isOutOfScope)
        assertTrue(response.replyText.contains("الروض الناضر"))
        assertTrue(response.citedPages.isNotEmpty())
    }

    @Test
    fun testTutorService_hintsProgression() = runBlocking {
        val service = MishkatTutorServiceImpl()
        val hint1 = service.getNextHint("قصر المنفصل لحفص", 0)
        assertEquals(1, hint1.hintStep)

        val hint2 = service.getNextHint("قصر المنفصل لحفص", 1)
        assertEquals(2, hint2.hintStep)
    }

    @Test
    fun testTutorService_revisionPlan() = runBlocking {
        val service = MishkatTutorServiceImpl()
        val errors = listOf("الخلط بين السكت والقصر لحفص", "الغنة في اللام والراء")
        val plan = service.generateRevisionPlan(errors)

        assertFalse(plan.isOutOfScope)
        assertTrue(plan.replyText.contains("الروض الناضر"))
    }
}
