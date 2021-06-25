package br.com.zup.keymanager

import br.com.zup.BuscaChavePixRequest
import br.com.zup.KeyManagerBuscaChaveGrpcServiceGrpc
import br.com.zup.KeyManagerRemoveGrpcServiceGrpc
import br.com.zup.RemoveChavePixRequest
import br.com.zup.keymanager.dto.DetalhesChavePixResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import java.util.*


@Validated
@Controller("/api/v1/busca/{clienteId}")
class BuscaChavePixController(private val buscaChavePixClient: KeyManagerBuscaChaveGrpcServiceGrpc.KeyManagerBuscaChaveGrpcServiceBlockingStub) {

    @Get("/pix/{pixId}")
    fun remove(@PathVariable("clienteId") clienteId : UUID,@PathVariable("pixId") pixId : UUID) :HttpResponse<Any> {

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

}