package com.gooddata.app.core

import com.gooddata.app.core.data.WordDto

object SentenceGenerator {

    fun composeSentence(noun: MutableCollection<WordDto>?, verb: MutableCollection<WordDto>?, adjective: MutableCollection<WordDto>?): String =
            "${noun?.random()?.word} ${verb?.random()?.word} ${adjective?.random()?.word}"

    fun composeYodaSentence(text: String): String = text.split(' ').reversed().joinToString(separator = " ")
}
