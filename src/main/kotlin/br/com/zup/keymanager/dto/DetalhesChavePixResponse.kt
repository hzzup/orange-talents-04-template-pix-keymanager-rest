package br.com.zup.keymanager.dto

import br.com.zup.BuscaChavePixResponse
import br.com.zup.TipoDeConta
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Introspected
class DetalhesChavePixResponse(chaveResponse: BuscaChavePixResponse) {

    //informações referente a chave
    val pixId = chaveResponse.pixId
    val clienteId = chaveResponse.clienteId
    val valorChave = chaveResponse.chave.chave
    val criadaEm = chaveResponse.chave.criadaEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }

    //informacões referente a conta
    val tipoConta = when (chaveResponse.chave.conta.tipo) {
        TipoDeConta.CONTA_CORRENTE -> "CONTA_CORRENTE"
        TipoDeConta.CONTA_POUPANCA -> "CONTA_POUPANCA"
        else -> "DESCONHECIDA"
    }
    val instituicao = chaveResponse.chave.conta.instituicao
    val nomeTitular = chaveResponse.chave.conta.nomeDoTitular
    val cpfTitular = chaveResponse.chave.conta.cpfDoTitular
    val agencia = chaveResponse.chave.conta.agencia
    val numeroConta = chaveResponse.chave.conta.numeroDaConta

}
