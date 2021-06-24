package br.com.zup.keymanager

import br.com.zup.KeyManagerRemoveGrpcServiceGrpc
import br.com.zup.RemoveChavePixRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import java.util.*


@Validated
@Controller("/api/v1/remove/{clienteId}")
class RemoveChavePixController(private val removeChavePixClient: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub) {

    @Delete("/pix/{pixId}")
    fun remove(@PathVariable("clienteId") clienteId : UUID,@PathVariable("pixId") pixId : UUID) :HttpResponse<Any> {

        removeChavePixClient.remove(RemoveChavePixRequest.newBuilder()
            .setClienteId(clienteId.toString())
            .setPixId(pixId.toString())
            .build())

        return HttpResponse.ok()
    }

}