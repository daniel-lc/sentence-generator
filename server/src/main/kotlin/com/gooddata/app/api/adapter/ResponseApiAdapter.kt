package com.gooddata.app.api.adapter

import com.gooddata.app.core.logOnError
import com.gooddata.app.core.logOnNext
import com.gooddata.app.core.logOnSubscribe
import com.gooddata.app.core.logOnSuccess
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.InvalidMediaTypeException
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import javax.validation.ValidationException
import javax.validation.Validator
import kotlin.reflect.KClass

typealias HandleApiFunction<T> = (ServerRequest, T) -> Mono<ResponseApiAdapter.Response>

@Component
class ResponseApiAdapter(private val validator: Validator) {

    fun <T> adapt(handle: HandleApiFunction<T>,
                  clazz: Class<T>,
                  handlerClass: KClass<*>,
                  reqPrinter: (T) -> String,
                  logger: Logger
    ): (ServerRequest) -> Mono<ServerResponse> {
        return { request ->
            Mono.defer { request.bodyToMono(clazz) }
                    .logOnSubscribe { logger.debug("ServerRequest: $request") }
                    .flatMap { validateBody(it) }
                    .logOnNext { logger.debug("Request body: ${reqPrinter(it)}") }
                    .flatMap { handle(request, it) }
                    .defaultIfEmpty(Response.ClientFailure(ErrorResponse("invalid_input")))
                    .logOnError(ValidationException::class.java) { logger.debug("Validation failed: ${it.message}") }
                    .onErrorResume(ValidationException::class.java) { Response.ClientFailure(ErrorResponse("invalid_input")).toMono() }
                    .logOnError(InvalidMediaTypeException::class.java) { logger.debug("Invalid media type: ${it.message}") }
                    .onErrorResume(InvalidMediaTypeException::class.java) { Response.ClientFailure(ErrorResponse("invalid_input")).toMono() }
                    .logOnError(ResponseStatusException::class.java) { logger.debug("Cannot parse request: ${it.message}") }
                    .onErrorResume(ResponseStatusException::class.java) { Response.ClientFailure(ErrorResponse("invalid_input")).toMono() }
                    .logOnError { logger.error("Unexpected handler error for request ($request)", it) }
                    .onErrorResume { Response.ServerFailure(ErrorResponse("unexpected error")).toMono() }
                    .logOnSuccess { logger.debug("Response: $it") }
                    .flatMap { handleResponse(it) }
        }
    }

    private fun handleResponse(response: Response): Mono<ServerResponse> = when(response) {
        is Response.Body -> ServerResponse.ok().body(response.payload.toMono())
        is Response.ClientFailure -> ServerResponse.badRequest().body<ErrorResponse>(response.payload.toMono())
        is Response.ServerFailure -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body<ErrorResponse>(response.payload.toMono())
    }

    private fun <T> validateBody(body: T): Mono<T> {
        val errorMessage = validator.validate(body).joinToString("; ") { "${it.propertyPath} ${it.message}" }
        return if (errorMessage.isEmpty()) {
            Mono.just(body)
        } else {
            ValidationException(errorMessage).toMono()
        }
    }

    sealed class Response {

        data class Body(val payload: Any = emptyMap<String, String>()) : Response()

        data class ClientFailure(val payload: ErrorResponse) : Response()
        data class ServerFailure(val payload: ErrorResponse) : Response()

        companion object {
            val emptySuccess: Response = Body()
            fun successResponse(payload: Any): Response = Body(payload = payload)
        }

    }
}

data class ErrorResponse(val error: String?)
