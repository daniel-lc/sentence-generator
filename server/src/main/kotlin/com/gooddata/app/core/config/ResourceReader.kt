package com.gooddata.app.core.config

import org.springframework.core.io.DefaultResourceLoader
import java.io.InputStreamReader

object ResourceReader {

    fun resourceToList(path: String): List<String> = InputStreamReader(
            DefaultResourceLoader().getResource(path).inputStream,
            "UTF-8"
    ).readLines()
}

