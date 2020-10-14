package com.gooddata.app.core.repository

import com.gooddata.app.core.data.Sentence
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface SentenceRepository : CrudRepository<Sentence, UUID> {

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Sentence s set s.displayCount=s.displayCount+1 where s.sentenceID = :sentenceID")
    fun increment(@Param("sentenceID") sentenceID: UUID)

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Sentence s set s.displayCount=s.displayCount+1 where s.sentenceID in :sentenceIDs")
    fun incrementAll(@Param("sentenceIDs") sentenceIDs: List<UUID>)

    @Query("SELECT s from Sentence s where s.text = :text")
    fun findByText(@Param("text") text: String): Optional<Sentence>
}
