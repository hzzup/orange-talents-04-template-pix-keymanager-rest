package br.com.zup.keymanager

import br.com.zup.*
import br.com.zup.keymanager.grpc.KeyManagerGrpcFactory
import com.google.protobuf.Timestamp
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

//testes devem ser rodados separadamente, já que estamos utilizando toBlocking

@MicronautTest
class BuscaChavePixControllerTest {

    //injetaremos um mock do serviço grpc
    @field:Inject
    lateinit var buscaStub: KeyManagerBuscaChaveGrpcServiceGrpc.KeyManagerBuscaChaveGrpcServiceBlockingStub

    //injetaremos tambem um client http para fazermos requisições para o nosso proprio servico (controller)
    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    companion object {
        val clienteId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
    }

    @Test
    fun `deve buscar uma chave pix com seus detalhes` () {
        //como o grpc é mockado, nós criamos uma resposta pronta
        val respostaGrpc = BuscaChavePixResponse.newBuilder()
            .setChave(BuscaChavePixResponse.ChavePix.newBuilder()
                .setConta(BuscaChavePixResponse.ChavePix.ContaInfo.newBuilder()
                    .setTipo(TipoDeConta.CONTA_POUPANCA)
                    .setAgencia("00")
                    .setNumeroDaConta("1234")
                    .setCpfDoTitular("24158269016")
                    .setNomeDoTitular("Teste 1234")
                    .setInstituicao("ITAU UNIBANCO SA.")
                    .build())
                .setTipo(TipoDeChave.CPF)
                .setChave("24158269016")
                .setCriadaEm(Timestamp.newBuilder()
                    .setNanos(LocalDateTime.now().nano)
                    .setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                    .build())
                .build())
            .setClienteId(clienteId)
            .setPixId(pixId)
            .build()

        given(buscaStub.buscaChave(Mockito.any())).willReturn(respostaGrpc)

        val request = HttpRequest.GET<Any>("/api/v1/busca/$clienteId/pix/$pixId")
        val response = client.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
    }

    @Test //testando apenas um cenario que nao funcionaria, o restante será testado no handler
    fun `nao deve buscar chave pix que nao existe` () {

        given(buscaStub.buscaChave(Mockito.any())).willThrow(StatusRuntimeException(Status.NOT_FOUND))

        val request = HttpRequest.GET<Any>("/api/v1/busca/$clienteId/pix/$pixId")
        val response = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, Any::class.java)
        }

        assertEquals(HttpStatus.NOT_FOUND, response.status)
    }


    //na nossa factory que geraria nossos stubs do grpc
    //substituimos por uma fabrica do mockito para mockar o grpc
    @Factory
    @Replaces(factory = KeyManagerGrpcFactory::class)
    internal class BuscaStubFactory  {

        @Singleton
        fun buscaChave() = Mockito.mock(KeyManagerBuscaChaveGrpcServiceGrpc.KeyManagerBuscaChaveGrpcServiceBlockingStub::class.java)
    }
}