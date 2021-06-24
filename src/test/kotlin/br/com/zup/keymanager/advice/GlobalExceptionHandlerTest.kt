package br.com.zup.keymanager.advice

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.hateoas.JsonError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GlobalExceptionHandlerTest {

    val request = HttpRequest.GET<Any>("/")

    @Test
    internal fun `deve retornar 404 quando statusException for not found`() {

        val mensagem = "nao encontrado"
        val notFoundException = StatusRuntimeException(
            Status.NOT_FOUND
            .withDescription(mensagem))

        val resposta = GlobalExceptionHandler().handle(request, notFoundException)

        assertEquals(HttpStatus.NOT_FOUND, resposta.status)
        assertNotNull(resposta.body())
        assertEquals(mensagem, (resposta.body() as JsonError).message)
    }

    @Test
    internal fun `deve retornar 422 quando statusException der erro externo`() {

        val mensagem = "erro externo"
        val notFoundException = StatusRuntimeException(
            Status.FAILED_PRECONDITION
                .withDescription(mensagem))

        val resposta = GlobalExceptionHandler().handle(request, notFoundException)

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resposta.status)
        assertNotNull(resposta.body())
        assertEquals(mensagem, (resposta.body() as JsonError).message)
    }

    @Test
    internal fun `deve retornar 422 quando statusException for already exists`() {

        val mensagem = "chave ja existente"
        val alreadyExistsException = StatusRuntimeException(Status.ALREADY_EXISTS
            .withDescription(mensagem))

        val resposta = GlobalExceptionHandler().handle(request, alreadyExistsException)

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resposta.status)
        assertNotNull(resposta.body())
        assertEquals(mensagem, (resposta.body() as JsonError).message)
    }

    @Test
    internal fun `deve retornar 400 quando statusException for invalid argument`() {

        val mensagem = "Dados da requisição estão inválidos"
        val invalidArgumentException = StatusRuntimeException(Status.INVALID_ARGUMENT)

        val resposta = GlobalExceptionHandler().handle(request, invalidArgumentException)

        assertEquals(HttpStatus.BAD_REQUEST, resposta.status)
        assertNotNull(resposta.body())
        assertEquals(mensagem, (resposta.body() as JsonError).message)
    }

    @Test
    internal fun `deve retornar 500 quando qualquer outro erro for lancado`() {

        val internalException = StatusRuntimeException(Status.INTERNAL)

        val resposta = GlobalExceptionHandler().handle(request, internalException)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.status)
        assertNotNull(resposta.body())
        assertTrue((resposta.body() as JsonError).message.contains("INTERNAL"))
    }
}