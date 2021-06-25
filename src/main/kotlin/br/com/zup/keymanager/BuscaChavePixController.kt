package br.com.zup.keymanager

import br.com.zup.*
import br.com.zup.keymanager.dto.ChavePixUnResponse
import br.com.zup.keymanager.dto.DetalhesChavePixResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import java.util.*


@Validated
@Controller("/api/v1/busca/{clienteId}")
class BuscaChavePixController(private val buscaChavePixClient: KeyManagerBuscaChaveGrpcServiceGrpc.KeyManagerBuscaChaveGrpcServiceBlockingStub,
                              private val listaChavePixClient: KeyManagerListaChavesGrpcServiceGrpc.KeyManagerListaChavesGrpcServiceBlockingStub) {

    @Get("/pix/{pixId}")
    fun busca(@PathVariable("clienteId") clienteId : UUID,@PathVariable("pixId") pixId : UUID) :HttpResponse<Any> {

        val chaveEncontrada = buscaChavePixClient.buscaChave(
            BuscaChavePixRequest.newBuilder()
                .setPixId(
                    BuscaChavePixRequest.FiltroPorPixId.newBuilder()
                        .setPixId(pixId.toString())
                        .setClienteId(clienteId.toString())
                        .build()
                )
                .build()
        )

        return HttpResponse.ok(DetalhesChavePixResponse(chaveEncontrada))
    }

    @Get("/pix/")
    fun lista(@PathVariable("clienteId") clienteId : UUID) :HttpResponse<Any> {

        val chavesEncontradas = listaChavePixClient.listaChaves(
            ListaChavesPixRequest.newBuilder()
                .setClienteId(clienteId.toString())
                .build()
        )
        val chavesDoCliente = chavesEncontradas.chavesList.map { ChavePixUnResponse(it) }

        return HttpResponse.ok(chavesDoCliente)
    }

}