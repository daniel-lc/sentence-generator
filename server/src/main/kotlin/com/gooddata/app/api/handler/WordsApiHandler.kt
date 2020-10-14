package com.gooddata.app.api.handler

import com.gooddata.app.api.EmptyRequest
import com.gooddata.app.api.Word
import com.gooddata.app.api.WordCategoryRequest
import com.gooddata.app.api.adapter.ErrorResponse
import com.gooddata.app.api.adapter.ResponseApiAdapter.Response
import com.gooddata.app.api.adapter.ResponseApiAdapter.Response.ClientFailure
import com.gooddata.app.api.adapter.ResponseApiAdapter.Response.Companion.successResponse
import com.gooddata.app.core.ForbiddenWordException
import com.gooddata.app.core.WordNotExistsException
import com.gooddata.app.core.data.WordDto
import com.gooddata.app.core.logOnError
import com.gooddata.app.core.service.WordService
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class WordsApiHandler(val wordService: WordService, val logger: Logger, @Qualifier("forbiddenWords") val forbiddenWords: List<String>) {

    fun getWords(serverRequest: ServerRequest, request: EmptyRequest): Mono<Response> = wordService.getWords()
            .collectList()
            .map { buildSuccess(it) }

    fun putWord(serverRequest: ServerRequest, wordCategoryRequest: WordCategoryRequest): Mono<Response> = serverRequest
            .pathVariable("word").toMono()
            .flatMap { wordService.putWord(it, wordCategoryRequest) }
            .map { buildSuccess(it) }

    fun getWord(serverRequest: ServerRequest, request: EmptyRequest): Mono<Response> = serverRequest
            .pathVariable("word").toMono()
            .flatMap { checkForbiddenWords(it) }
            .flatMap { wordService.getWord(it) }
            .map { buildSuccess(it) }
            .logOnError(ForbiddenWordException::class.java) { logger.debug(it.message) }
            .onErrorResume(ForbiddenWordException::class.java) { ClientFailure(ErrorResponse("forbidden_word")).toMono() }
            .logOnError(WordNotExistsException::class.java) { logger.debug(it.message) }
            .onErrorResume(WordNotExistsException::class.java) {
                ClientFailure(ErrorResponse("Word: ${it.word} not exists")).toMono()
            }

    fun checkForbiddenWords(word: String): Mono<String> =
            if (forbiddenWords.contains(word)) ForbiddenWordException(word, "Word: $word belongs between forbidden words.").toMono()
            else word.toMono()

    private fun buildSuccess(wordDto: WordDto): Response = successResponse(Word.DtoMapper.from(wordDto))

    private fun buildSuccess(wordDtos: List<WordDto>): Response = successResponse(wordDtos.map { Word.DtoMapper.from(it) })
}
