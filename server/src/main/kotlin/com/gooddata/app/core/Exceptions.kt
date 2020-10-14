package com.gooddata.app.core

import java.util.*

open class WordNotExistsException(val word: String, message: String?): RuntimeException(message)
open class SentenceNotExistsException(val sentenceID: UUID, message: String?): RuntimeException(message)
open class NotEnoughWordCategoriesException(message: String?): RuntimeException(message)
open class ForbiddenWordException(val word: String, message: String?): RuntimeException(message)
