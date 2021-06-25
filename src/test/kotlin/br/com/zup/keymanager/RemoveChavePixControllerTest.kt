package br.com.zup.keymanager

import br.com.zup.KeyManagerRegistraGrpcServiceGrpc
import br.com.zup.KeyManagerRemoveGrpcServiceGrpc
import br.com.zup.RemoveChavePixResponse
import br.com.zup.keymanager.grpc.KeyManagerGrpcFactory
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

//testes devem ser rodados separadamente, já que estamos utilizando toBlocking

@MicronautTest
class RemoveChavePixControllerTest {

    //injetaremos um mock do serviço grpc
    @field:Inject
    lateinit var removeStub: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub

    //injetaremos tambem um client http para fazermos requisições para o nosso proprio servico (controller)
    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    companion object {
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
    }

    @Test
    fun `deve excluir uma chave pix` () {
        //como o grpc é mockado, nós criamos uma resposta pronta
        val respostaGrpc = RemoveChavePixResponse.newBuilder()
            .setClienteId(clienteId)
            .setPixId(pixId)
            .setMensagem("Excluido com sucesso")
            .build()

        given(removeStub.remove(Mockito.any())).willReturn(respostaGrpc)

        val request = HttpRequest.DELETE<Any>("/api/v1/remove/$clienteId/pix/$pixId")
        val response = client.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.OK, response.status)
    }

    @Test //testando apenas um cenario que nao funcionaria, o restante será testado no handler
    fun `nao deve excluir chave pix que nao existe` () {

        given(removeStub.remove(Mockito.any())).willThrow(StatusRuntimeException(Status.NOT_FOUND))

        val request = HttpRequest.DELETE<Any>("/api/v1/remove/$clienteId/pix/$pixId")
        val response = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        assertEquals(HttpStatus.NOT_FOUND, response.status)
    }


    //Não é necessario mockar a factory inteira mais, apenas o metodo
    //@Factory
    //@Replaces(factory = KeyManagerGrpcFactory::class)
    //internal class RemoveStubFactory  {
    @Singleton
    @Replaces(bean=KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub::class)
    fun deletaChave() = Mockito.mock(KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub::class.java)
    //}
}