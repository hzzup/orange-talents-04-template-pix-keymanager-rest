package br.com.zup.keymanager

import br.com.zup.KeyManagerRegistraGrpcServiceGrpc
import br.com.zup.keymanager.dto.NovaChavePixRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import java.util.*
import javax.validation.Valid

@Validated
@Controller("/api/v1/registra/{clienteId}")
class RegistraChavePixController(private val registraChavePixClient : KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub) {

    @Post("/pix")
    fun registra(
        @PathVariable("clienteId") clienteId: UUID,
        @Valid @Body request: NovaChavePixRequest) : HttpResponse<Any>{

        val grpcResponse = registraChavePixClient.registra(request.paraModeloGrpc(clienteId))
        val uri = HttpResponse.uri("/api/v1/registra/$clienteId/pix/${grpcResponse.pixId}")

        return HttpResponse.created(uri)
    }
}