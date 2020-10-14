package com.gooddata.app.core.data

import com.gooddata.app.core.SentenceGenerator
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "sentences")
data class Sentence(
        @Id @Column(name = "sentence_id") val sentenceID: UUID = UUID.randomUUID(),
        val text: String = "",
        @Column(name = "display_count") val displayCount: Int = 0,
        val created: Instant = Instant.now()
)

data class SentenceDto(
        val sentenceID: UUID,
        val text: String,
        val displayCount: Int,
        val created: String
) {

    object ModelMapper {
        fun from(sentence: Sentence, increase: Boolean = false, yodaSentence: Boolean = false): SentenceDto = SentenceDto(
                sentence.sentenceID,
                if (yodaSentence) SentenceGenerator.composeYodaSentence(sentence.text) else sentence.text,
                if (increase) sentence.displayCount + 1 else sentence.displayCount,         // todo: I would like to have displayCount updated and returned at same time with over SQL query, but that solution didn't work or I need more time to investigate.
                convertToDate(sentence.created)
        )

        private fun convertToDate(created: Instant): String = LocalDateTime.ofInstant(created, ZoneOffset.UTC).toString()
    }

}
