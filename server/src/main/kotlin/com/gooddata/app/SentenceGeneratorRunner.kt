package com.gooddata.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SentenceGeneratorRunner

fun main(args: Array<String>) {
	runApplication<SentenceGeneratorRunner>(*args)
}
