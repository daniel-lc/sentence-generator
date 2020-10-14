package com.gooddata.app.core.data

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "sentence_track")
data class SentenceTrack(
        @Id val text: String,
        @Column(name = "sentence_id") val sentenceID: UUID,
        val count: Int = 0
)
