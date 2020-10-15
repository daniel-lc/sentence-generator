package com.gooddata.app.core

import com.gooddata.app.core.data.*
import com.gooddata.app.core.repository.SentenceRepository
import com.gooddata.app.core.repository.SentenceTrackRepository
import com.gooddata.app.core.service.SentenceService
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import java.util.*

@WebFluxTest
internal class SentenceServiceTest {

    private val sentences = listOf(Sentence(text = "home stand straight"))
    private val categorizedWords = mutableMapOf(
            Pair(WordCategory.NOUN, mutableListOf(WordDto("home", WordCategory.NOUN)) as MutableCollection<WordDto>),
            Pair(WordCategory.ADJECTIVE, mutableListOf(WordDto("stand", WordCategory.ADJECTIVE)) as MutableCollection<WordDto>),
            Pair(WordCategory.VERB, mutableListOf(WordDto("straight", WordCategory.VERB)) as MutableCollection<WordDto>)
    )
    private val sentenceRepository = mockk<SentenceRepository>()
    private val sentenceTrackRepository = mockk<SentenceTrackRepository>()

    private val sentenceService = SentenceService(sentenceRepository, sentenceTrackRepository, LoggerFactory.getLogger(javaClass))

    @Test
    fun getSentences() {

        every { sentenceRepository.findAll() } returns sentences
        every { sentenceRepository.incrementAll(any()) } returns Unit

        val sentencesResult = sentenceService.getSentences().block()!!

        Assertions.assertEquals(sentencesResult.size, 1)
        MatcherAssert.assertThat(sentencesResult[0], Matchers.instanceOf(SentenceDto::class.java))
        Assertions.assertEquals(sentencesResult[0].text, "home stand straight")
        Assertions.assertEquals(sentencesResult[0].displayCount, 1)
    }

    @Test
    fun generateSentence() {

        every { sentenceRepository.findByText(any()) } returns Optional.of(sentences[0])
        every { sentenceTrackRepository.increment(any()) } returns Unit
        every { sentenceRepository.save<Sentence>(any()) } returns sentences[0]
        every { sentenceTrackRepository.save<SentenceTrack>(any()) } returns SentenceTrack("", UUID.randomUUID())

        val sentenceResult = sentenceService.generateSentence(categorizedWords).block()!!

        MatcherAssert.assertThat(sentenceResult, Matchers.instanceOf(SentenceDto::class.java))
        Assertions.assertEquals(sentenceResult.text, "home stand straight")
    }

}
