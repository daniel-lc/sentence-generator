package com.gooddata.app.core

import reactor.core.publisher.Mono
import java.util.Optional

fun <T> Optional<T>.toMonoOrEmpty(): Mono<T> = Mono.justOrEmpty(this)
