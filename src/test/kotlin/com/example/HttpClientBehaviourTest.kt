package com.example

import io.micronaut.core.type.Argument
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpMethod
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.simple.SimpleHttpRequest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject

@MicronautTest
class HttpClientBehaviourTest {

    @Inject
    lateinit var httpClient: HttpClient

    @Test
    fun readPlainTextBodyOfSuccessfulGet() {
        val request = SimpleHttpRequest<String>(HttpMethod.GET, "https://httpstat.us/200", null)
                .header(HttpHeaders.ACCEPT, "text/plain")
        val response = httpClient.toBlocking().exchange(request, String::class.java)

        assertEquals("200 OK", response.body.get())
    }

    @Test
    fun readPlainTextBodyOfFailingGet() {
        val request = SimpleHttpRequest<String>(HttpMethod.GET, "https://httpstat.us/400", null)
                .header(HttpHeaders.ACCEPT, "text/plain")

        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, String::class.java)
        }
        assertEquals("400 Bad Request", exception.response.body.get())
    }

    @Test
    fun readJsonBodyOfSuccessfulGet() {
        val request = SimpleHttpRequest<String>(HttpMethod.GET, "https://httpstat.us/200", null)
                .header(HttpHeaders.ACCEPT, "application/json")
        val response = httpClient.toBlocking().exchange(request, HttpStatUsResponse::class.java)

        assertEquals(HttpStatUsResponse(200, "OK"), response.body.get())
    }

    @Test
    fun readJsonBodyOfFailingGet() {
        val request = SimpleHttpRequest<String>(HttpMethod.GET, "https://httpstat.us/400", null)
                .header(HttpHeaders.ACCEPT, "application/json")

        val exception = assertThrows<HttpClientResponseException> {
            httpClient.toBlocking().exchange(request, Argument.of(HttpStatUsResponse::class.java), Argument.of(HttpStatUsResponse::class.java))
        }
        assertEquals(HttpStatUsResponse(400, "Bad Request"), exception.response.getBody(Argument.of(HttpStatUsResponse::class.java)).get())
    }

    data class HttpStatUsResponse(val code: Int, val description: String)
}
