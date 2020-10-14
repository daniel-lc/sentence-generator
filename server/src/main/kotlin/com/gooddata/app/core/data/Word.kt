package com.gooddata.app.core.data

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "words")
class Word(@Id val word: String, @Column(name = "word_category") val wordCategory: WordCategory)

enum class WordCategory {

    NOUN,
    VERB,
    ADJECTIVE
}

data class WordDto(val word: String, val wordCategory: WordCategory) {
    object ModelMapper {
        fun from(word: Word) = WordDto(word.word, word.wordCategory)
    }
}
