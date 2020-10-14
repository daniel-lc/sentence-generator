package com.gooddata.app.api.handler

import com.gooddata.app.api.EmptyRequest
import com.gooddata.app.api.Sentence
import com.gooddata.app.api.ShortSentence
import com.gooddata.app.api.YodaSentence
import com.gooddata.app.api.adapter.ErrorResponse
import com.gooddata.app.api.adapter.ResponseApiAdapter.Response
import com.gooddata.app.api.adapter.ResponseApiAdapter.Response.ClientFailure
import com.gooddata.app.api.adapter.ResponseApiAdapter.Response.Companion.successResponse
import com.gooddata.app.core.NotEnoughWordCategoriesException
import com.gooddata.app.core.SentenceNotExistsException
import com.gooddata.app.core.data.SentenceDto
import com.gooddata.app.core.data.WordCategory
import com.gooddata.app.core.data.WordDto
import com.gooddata.app.core.logOnError
import com.gooddata.app.core.service.SentenceService
import com.gooddata.app.core.service.WordService
import org.slf4j.Logger
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.*

@Component
class SentenceApiHandler(val sentenceService: SentenceService, val wordService: WordService, val logger: Logger) {

    fun getSentences(serverRequest: ServerRequest, emptyRequest: EmptyRequest): Mono<Response> = sentenceService.getSentences()
            .map { buildSuccess(it) }

    fun getSentence(serverRequest: ServerRequest, emptyRequest: EmptyRequest): Mono<Response> = serverRequest
            .pathVariable("sentenceID").toMono()
            .flatMap { sentenceService.getSentence(UUID.fromString(it)) }
            .flatMap { buildSuccess(it, ShortSentence::class.java) }
            .logOnError(IllegalArgumentException::class.java) { logger.debug(it.message) }
            .onErrorResume(IllegalArgumentException::class.java) {
                ClientFailure(ErrorResponse(it.message)).toMono()
            }
            .logOnError(SentenceNotExistsException::class.java) { logger.debug(it.message) }
            .onErrorResume(SentenceNotExistsException::class.java) {
                ClientFailure(ErrorResponse("Sentence: ${it.sentenceID} not exists")).toMono()
            }

    fun generateSentence(serverRequest: ServerRequest, emptyRequest: EmptyRequest): Mono<Response> = wordService.getCategorizedWords()
            .flatMap { validateWordCategories(it) }
            .flatMap { sentenceService.generateSentence(it) }
            .flatMap { buildSuccess(it, Sentence::class.java) }
            .logOnError(NotEnoughWordCategoriesException::class.java) { logger.debug(it.message) }
            .onErrorResume(NotEnoughWordCategoriesException::class.java) { ClientFailure(ErrorResponse("not_enough_words_categories")).toMono() }

    fun getYodaSentence(serverRequest: ServerRequest, emptyRequest: EmptyRequest): Mono<Response> = serverRequest
            .pathVariable("sentenceID").toMono()
            .flatMap { sentenceService.getYodaSentence(UUID.fromString(it)) }
            .flatMap { buildSuccess(it, YodaSentence::class.java) }

    private fun validateWordCategories(validatedMap: MutableMap<WordCategory, MutableCollection<WordDto>>): Mono<MutableMap<WordCategory, MutableCollection<WordDto>>> =
            if (validatedMap.keys.size == WordCategory.values().size) validatedMap.toMono()
            else NotEnoughWordCategoriesException("Cannot generate sentence. Not enough words with needed categories.").toMono()

    private fun buildSuccess(sentenceDtos: List<SentenceDto>): Response = successResponse(sentenceDtos.map { Sentence.DtoMapper.from(it) })

    private inline fun <reified T> buildSuccess(sentenceDto: SentenceDto, responseType: Class<T>): Mono<Response> = when(responseType) {
        ShortSentence::class.java -> successResponse(ShortSentence.DtoMapper.from(sentenceDto)).toMono()
        Sentence::class.java -> successResponse(Sentence.DtoMapper.from(sentenceDto)).toMono()
        YodaSentence::class.java -> successResponse(YodaSentence.DtoMapper.from(sentenceDto)).toMono()
        else -> IllegalStateException("Unknown type: $responseType").toMono()
    }

}
