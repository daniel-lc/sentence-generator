package com.gooddata.integration.tests.api

import com.gooddata.app.api.adapter.ResponseApiAdapter
import com.gooddata.app.api.handler.SentenceApiHandler
import com.gooddata.app.api.handler.WordsApiHandler
import com.gooddata.app.api.routing.ApiRouterConfiguration
import com.gooddata.app.core.data.WordCategory
import com.gooddata.app.core.service.WordService
import io.mockk.junit5.MockKExtension
import io.mockk.mockkObject
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.RouterFunctionDsl

@RunWith(SpringRunner::class)
@ExtendWith(MockKExtension::class)
@ContextConfiguration(classes = [ApiRouterConfiguration::class, WordsApiHandler::class])
@WebFluxTest
//@SpringBootTest
//@SpringJUnitConfig(WebFluxAutoConfiguration.WebFluxConfig::class)
class TestWordsHandler {

    // TODO: I would fix this by removing all these mock, only run backend and do just calls by webTestClient

    @MockBean lateinit var wordService: WordService
    @MockBean lateinit var logger: Logger
    @MockBean lateinit var sentenceApiHandler: SentenceApiHandler
    @MockBean lateinit var responseApiHandler: ResponseApiAdapter


    @MockBean @Qualifier("forbiddenWords") lateinit var forbiddenWords: List<String>
    @Autowired lateinit var context: ApplicationContext

    private lateinit var webTestClient: WebTestClient


    private val apiRequestSpec: RequestSpecification = RequestSpecBuilder()
            .setBaseUri("http://localhost:7070")
            .setContentType(ContentType.JSON)
            .build()

    @Before
    fun setup(): Unit {
        mockkObject(RouterFunctionDsl::class)
        webTestClient = WebTestClient.bindToApplicationContext(context).build()
    }

    @Test
    fun testPutWords(): Unit {

        Given {
            spec(apiRequestSpec)
            body("""{"wordCategory": "NOUN"}""")
        } When {
            put("/words/home")
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
            body("word", Matchers.equalTo("home"))
            body("wordCategory", Matchers.equalTo(WordCategory.NOUN))
        }
    }


}
