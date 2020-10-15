package com.gooddata.app.core.service

import com.gooddata.app.core.*
import com.gooddata.app.core.data.*
import com.gooddata.app.core.repository.SentenceRepository
import com.gooddata.app.core.repository.SentenceTrackRepository
import org.slf4j.Logger
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Service
class SentenceService(
        val sentenceRepository: SentenceRepository,
        val sentenceTrackRepository: SentenceTrackRepository,
        val logger: Logger
) {

    fun getSentences(): Mono<MutableList<SentenceDto>> = Flux
            .defer { sentenceRepository.findAll().toFlux() }
            .map { SentenceDto.ModelMapper.from(it, true) }
            .collectList()
            .doOnSuccess { sentenceRepository.incrementAll(it.map(SentenceDto::sentenceID)) }

    fun getSentence(sentenceID: UUID): Mono<SentenceDto> = Mono
            .defer { sentenceRepository.findById(sentenceID).toMonoOrEmpty() }
            .map { SentenceDto.ModelMapper.from(it, true) }
            .doOnSuccess { sentenceRepository.increment(sentenceID) }
            .switchIfEmpty(SentenceNotExistsException(sentenceID, "SentenceID: $sentenceID not found.").toMono())

    fun getYodaSentence(sentenceID: UUID): Mono<SentenceDto> = Mono
            .defer { sentenceRepository.findById(sentenceID).toMonoOrEmpty() }
            .map { SentenceDto.ModelMapper.from(it, yodaSentence = true) }
            .switchIfEmpty(SentenceNotExistsException(sentenceID, "SentenceID: $sentenceID not found.").toMono())

    // todo: tracking is not working correctly, cause it's not updating row
    fun generateSentence(categorizedWords: MutableMap<WordCategory, MutableCollection<WordDto>>): Mono<SentenceDto> = Mono
            .defer { categorizedWords.toMono() }
            .map { SentenceGenerator.composeSentence(it[WordCategory.NOUN], it[WordCategory.VERB], it[WordCategory.ADJECTIVE]) }
            .flatMap { text ->
                sentenceRepository
                        .findByText(text).toMonoOrEmpty()
                        .doOnSuccess { sentenceTrackRepository.increment(text) }
                        .logOnNext { logger.debug("Generated sentence with text: '$text' already existed and were tracked.") }
                        .switchIfEmpty { sentenceRepository.save(Sentence(text = text)).toMono() }
                        .logOnNext { logger.debug("Sentence added - sentenceID: ${it.sentenceID}, text: ${it.text}, created: ${it.created}") }
                        .doOnNext { sentenceTrackRepository.save(SentenceTrack(it.text, it.sentenceID)) }
            }
            .map { SentenceDto.ModelMapper.from(it) }

}
