package com.gooddata.app.core.service

import com.gooddata.app.api.WordCategoryRequest
import com.gooddata.app.core.WordNotExistsException
import com.gooddata.app.core.data.Word
import com.gooddata.app.core.data.WordCategory
import com.gooddata.app.core.data.WordDto
import com.gooddata.app.core.logOnSuccess
import com.gooddata.app.core.repository.WordRepository
import com.gooddata.app.core.toMonoOrEmpty
import org.slf4j.Logger
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@Service
class WordService(private val wordRepository: WordRepository, val logger: Logger) {

    fun getWords(): Flux<WordDto> = Flux
            .defer { wordRepository.findAll().toFlux() }
            .map { WordDto.ModelMapper.from(it) }

    fun putWord(word: String, wordRequest: WordCategoryRequest): Mono<WordDto> = Mono
            .defer { wordRepository.save(Word(word, wordRequest.wordCategory)).toMono() }
            .logOnSuccess { logger.debug("Word added - word: ${it.word}, wordCategory: ${it.wordCategory.name}") }
            .map { WordDto.ModelMapper.from(it) }

    fun getWord(word: String): Mono<WordDto> = Mono
            .defer { wordRepository.findById(word).toMonoOrEmpty() }
            .map { WordDto.ModelMapper.from(it) }
            .switchIfEmpty(WordNotExistsException(word, "Word: $word not found.").toMono())

    fun getCategorizedWords(): Mono<MutableMap<WordCategory, MutableCollection<WordDto>>> = getWords().collectMultimap { it.wordCategory }

}
