package com.gooddata.app.core

import org.reactivestreams.Subscription
import reactor.core.publisher.Mono

fun <T> Mono<T>.logOnNext(f: (T) -> Unit): Mono<T> = this.doOnNext(f)
fun <T> Mono<T>.logOnSuccess(f: (T) -> Unit): Mono<T> = this.doOnSuccess(f)
fun <T> Mono<T>.logOnSubscribe(f: (Subscription) -> Unit): Mono<T> = this.doOnSubscribe(f)
fun <T> Mono<T>.logOnError(f: (Throwable) -> Unit): Mono<T> = this.doOnError(f)
fun <E : Throwable, T>Mono<T>.logOnError(exceptionType: Class<E>, f: (E) -> Unit): Mono<T> = this.doOnError(exceptionType, f)
