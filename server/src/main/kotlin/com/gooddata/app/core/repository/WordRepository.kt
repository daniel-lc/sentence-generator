package com.gooddata.app.core.repository

import com.gooddata.app.core.data.Word
import org.springframework.data.repository.CrudRepository

interface WordRepository : CrudRepository<Word, String>
