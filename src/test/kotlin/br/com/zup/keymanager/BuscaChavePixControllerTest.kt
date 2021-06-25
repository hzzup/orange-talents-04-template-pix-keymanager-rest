package br.com.zup.keymanager

import br.com.zup.*
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.StatusRuntimeException
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

    @field:Inject
    lateinit var listaStub: KeyManagerListaChavesGrpcServiceGrpc.KeyManagerListaChavesGrpcServiceBlockingStub


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

    @Test
    fun `deve listar todas as chaves de um cliente` () {

        val respostaGrpc = listaChavePixResponse(clienteId)

        given(listaStub.listaChaves(Mockito.any())).willReturn(respostaGrpc)

        val request = HttpRequest.GET<Any>("/api/v1/busca/$clienteId/pix/")
        val response = client.toBlocking().exchange(request, List::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        assertEquals(response.body().size, 4)
    }

    @Test //testando apenas um cenario que nao funcionaria, o restante será testado no handler
    fun `nao deve listar chave se cliente nao possui chaves ou cliente invalido` () {

        val respostaGrpc = ListaChavesPixResponse.newBuilder().build()

        given(listaStub.listaChaves(Mockito.any())).willReturn(respostaGrpc)

        val request = HttpRequest.GET<Any>("/api/v1/busca/$clienteId/pix/")
        val response = client.toBlocking().exchange(request, List::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        assertEquals(response.body().size, 0)
    }

    private fun listaChavePixResponse(clienteId: String): ListaChavesPixResponse {
        val chaveEmail = ListaChavesPixResponse.ChavePix.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipoDeChave(TipoDeChave.EMAIL)
            .setChave("teste@teste.com")
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .setCriadaEm(Timestamp.newBuilder()
                .setNanos(LocalDateTime.now().nano)
                .setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .build())
            .build()

        val chaveCelular = ListaChavesPixResponse.ChavePix.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipoDeChave(TipoDeChave.CELULAR)
            .setChave("+1122333334444")
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .setCriadaEm(Timestamp.newBuilder()
                .setNanos(LocalDateTime.now().nano)
                .setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .build())
            .build()

        val chaveCpf = ListaChavesPixResponse.ChavePix.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipoDeChave(TipoDeChave.CPF)
            .setChave("78185151008")
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .setCriadaEm(Timestamp.newBuilder()
                .setNanos(LocalDateTime.now().nano)
                .setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .build())
            .build()

        val chaveAleatoria = ListaChavesPixResponse.ChavePix.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setTipoDeChave(TipoDeChave.ALEATORIA)
            .setChave("")
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .setCriadaEm(Timestamp.newBuilder()
                .setNanos(LocalDateTime.now().nano)
                .setSeconds(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .build())
            .build()


        return ListaChavesPixResponse.newBuilder()
            .setClienteId(clienteId)
            .addAllChaves(listOf(chaveEmail, chaveCelular, chaveCpf, chaveAleatoria))
            .build()

    }


    //Não é necessario mockar a factory inteira mais, apenas o metodo
    //@Factory
    //@Replaces(factory = KeyManagerGrpcFactory::class)
    //internal class BuscaStubFactory  {
        @Singleton
        @Replaces(bean = KeyManagerBuscaChaveGrpcServiceGrpc.KeyManagerBuscaChaveGrpcServiceBlockingStub::class)
        fun buscaChave() = Mockito.mock(KeyManagerBuscaChaveGrpcServiceGrpc.KeyManagerBuscaChaveGrpcServiceBlockingStub::class.java)

        @Singleton
        @Replaces(bean = KeyManagerListaChavesGrpcServiceGrpc.KeyManagerListaChavesGrpcServiceBlockingStub::class)
        fun listaChave() = Mockito.mock(KeyManagerListaChavesGrpcServiceGrpc.KeyManagerListaChavesGrpcServiceBlockingStub::class.java)

    //}
}