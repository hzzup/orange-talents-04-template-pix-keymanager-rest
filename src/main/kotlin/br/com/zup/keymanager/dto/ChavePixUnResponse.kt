package br.com.zup.keymanager.dto

import br.com.zup.ListaChavesPixResponse
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Introspected
class ChavePixUnResponse(chavePix: ListaChavesPixResponse.ChavePix) {

    val id = chavePix.pixId
    val chave = chavePix.chave
    val tipo = chavePix.tipoDeChave
    val tipoDeConta = chavePix.tipoDeConta
    val criadaEm = chavePix.criadaEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }
}
