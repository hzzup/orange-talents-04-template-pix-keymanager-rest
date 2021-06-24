package br.com.zup.keymanager

import br.com.zup.KeyManagerRegistraGrpcServiceGrpc
import br.com.zup.RegistraChavePixResponse
import br.com.zup.keymanager.dto.NovaChavePixRequest
import br.com.zup.keymanager.dto.TipoDeChaveRequest
import br.com.zup.keymanager.dto.TipoDeContaRequest
import br.com.zup.keymanager.grpc.KeyManagerGrpcFactory
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

//testes devem ser rodados separadamente, já que estamos utilizando toBlocking

@MicronautTest
class RegistraChavePixControllerTest {

    //injetaremos um mock do serviço grpc
    @field:Inject
    lateinit var registraStub: KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub

    //injetaremos tambem um client http para fazermos requisições para o nosso proprio servico (controller)
    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient


    companion object {
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
    }

    @Test
    fun `deve registrar uma nova chave pix` () {
        //como o grpc é mockado, nós criamos uma resposta pronta
        val respostaGrpc = RegistraChavePixResponse.newBuilder()
            .setClienteId(clienteId)
            .setPixId(pixId)
            .build()

        given(registraStub.registra(Mockito.any())).willReturn(respostaGrpc)

        val novaChavePix = NovaChavePixRequest(tipoDeConta = TipoDeContaRequest.CONTA_CORRENTE,
            chave = "teste@teste.com.br",
            tipoDeChave = TipoDeChaveRequest.EMAIL
        )

        val request = HttpRequest.POST("/api/v1/registra/$clienteId/pix", novaChavePix)
        val response = client.toBlocking().exchange(request, NovaChavePixRequest::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
        assertTrue(response.headers.contains("Location"))
        assertTrue(response.header("Location").contains(pixId))
    }

    @Test //testando apenas um cenario que nao funcionaria, o restante será testado no handler
    fun `nao deve registrar uma nova chave pix quando grpc retornar algum erro` () {

        //como o grpc é mockado, nós criamos uma resposta pronta
        val respostaGrpc = RegistraChavePixResponse.newBuilder()
            .setClienteId(clienteId)
            .setPixId(pixId)
            .build()

        given(registraStub.registra(Mockito.any())).willThrow(StatusRuntimeException(Status.NOT_FOUND))

        val novaChavePix = NovaChavePixRequest(tipoDeConta = TipoDeContaRequest.CONTA_CORRENTE,
            chave = "teste@teste.com.br",
            tipoDeChave = TipoDeChaveRequest.EMAIL
        )

        val request = HttpRequest.POST("/api/v1/clientes/$clienteId/pix", novaChavePix)
        val response = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, NovaChavePixRequest::class.java)
        }

        assertEquals(HttpStatus.NOT_FOUND, response.status)
    }


    //na nossa factory que geraria nossos stubs do grpc
    //substituimos por uma fabrica do mockito para mockar o grpc
    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    internal class MockitoStubFactory {

        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub::class.java)
    }
}