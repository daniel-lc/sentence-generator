package com.gooddata.app.api.routing

import com.gooddata.app.api.RequestPrinter
import com.gooddata.app.api.adapter.ResponseApiAdapter
import com.gooddata.app.api.handler.SentenceApiHandler
import com.gooddata.app.api.handler.WordsApiHandler
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

@Configuration
class ApiRouterConfiguration(
        val wordsApiHandler: WordsApiHandler,
        val sentenceApiHandler: SentenceApiHandler,
        val responseApiAdapter: ResponseApiAdapter
) {

    @Bean
    fun router(): RouterFunction<ServerResponse> {
        return router {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("/words", wordsApiHandler::getWords.adapt(WordsApiHandler::class, RequestPrinter::print))
                PUT("/words/{word}", wordsApiHandler::putWord.adapt(WordsApiHandler::class, RequestPrinter::print))
                GET("/words/{word}", wordsApiHandler::getWord.adapt(WordsApiHandler::class, RequestPrinter::print))
                GET("/sentences", sentenceApiHandler::getSentences.adapt(SentenceApiHandler::class, RequestPrinter::print))
                POST("/sentences/generate", sentenceApiHandler::generateSentence.adapt(SentenceApiHandler::class, RequestPrinter::print))
                GET("/sentences/{sentenceID}", sentenceApiHandler::getSentence.adapt(SentenceApiHandler::class, RequestPrinter::print))
                GET("/sentences/{sentenceID}/yodaTalk", sentenceApiHandler::getYodaSentence.adapt(SentenceApiHandler::class, RequestPrinter::print))
            }
        }
    }

    private inline fun <reified T> ((ServerRequest, T) -> Mono<ResponseApiAdapter.Response>).adapt(handlerClass: KClass<*>, noinline reqPrinter: (T) -> String) =
            responseApiAdapter.adapt(this, T::class.java, handlerClass, reqPrinter, LoggerFactory.getLogger(handlerClass.java))
}
