package com.gooddata.app.core.repository

import com.gooddata.app.core.data.SentenceTrack
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface SentenceTrackRepository : CrudRepository<SentenceTrack, String> {

    @Transactional
    @Modifying
    @Query("update SentenceTrack s set s.count=s.count+1 where s.text = :text")
    fun increment(@Param("text") text: String)
}
