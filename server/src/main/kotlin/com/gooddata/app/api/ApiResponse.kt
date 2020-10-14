package com.gooddata.app.api

import com.gooddata.app.core.data.SentenceDto
import com.gooddata.app.core.data.WordCategory
import com.gooddata.app.core.data.WordDto
import java.util.*

data class Word(val word: String, val wordCategory: WordCategory) {

    object DtoMapper {
        fun from(wordDto: WordDto): Word = Word(wordDto.word, wordDto.wordCategory)
    }
}

data class Sentence(val sentenceID: UUID, val text: String, val displayCount: Int, val created: String) {

    object DtoMapper {
        fun from(sentenceDto: SentenceDto): Sentence = Sentence(sentenceDto.sentenceID, sentenceDto.text, sentenceDto.displayCount, sentenceDto.created)
    }
}

data class ShortSentence(val text: String, val displayCount: Int) {

    object DtoMapper {
        fun from(sentenceDto: SentenceDto): ShortSentence = ShortSentence(sentenceDto.text, sentenceDto.displayCount)
    }
}

data class YodaSentence(val text: String) {

    object DtoMapper {
        fun from(sentenceDto: SentenceDto): YodaSentence = YodaSentence(sentenceDto.text)
    }
}
