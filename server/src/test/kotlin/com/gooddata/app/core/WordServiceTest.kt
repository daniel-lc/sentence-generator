package com.gooddata.app.core

import com.gooddata.app.api.WordCategoryRequest
import com.gooddata.app.core.data.Word
import com.gooddata.app.core.data.WordCategory
import com.gooddata.app.core.data.WordDto
import com.gooddata.app.core.repository.WordRepository
import com.gooddata.app.core.service.WordService
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import java.util.*

@WebFluxTest
internal class WordServiceTest {

    private val words = listOf(Word("home", WordCategory.NOUN), Word("stand", WordCategory.ADJECTIVE), Word("straight", WordCategory.VERB))
    private val wordRepository = mockk<WordRepository>()

    private val wordService = WordService(wordRepository, LoggerFactory.getLogger(javaClass))

    @Test
    fun getWords() {

        every { wordRepository.findAll() } returns words

        val wordsResult = wordService.getWords().collectList().block()!!

        assertEquals(wordsResult.size, 3)
        assertThat(wordsResult[0], instanceOf(WordDto::class.java))
        assertEquals(wordsResult[0].word, "home")
        assertEquals(wordsResult[0].wordCategory, WordCategory.NOUN)
    }

    @Test
    fun putWord() {

        every { wordRepository.save<Word>(any()) } returns words[0]

        val wordResult = wordService.putWord(words[0].word, WordCategoryRequest(WordCategory.NOUN)).block()!!

        assertThat(wordResult, instanceOf(WordDto::class.java))
        assertEquals(wordResult.word, "home")
        assertEquals(wordResult.wordCategory, WordCategory.NOUN)
    }

    @Test
    fun getWord() {

        every { wordRepository.findById(any()) } returns Optional.of(words[0])

        val wordResult = wordService.getWord(words[0].word).block()!!

        assertThat(wordResult, instanceOf(WordDto::class.java))
        assertEquals(wordResult.word, "home")
        assertEquals(wordResult.wordCategory, WordCategory.NOUN)
    }
}
