package br.com.zup.keymanager.grpc

import br.com.zup.KeyManagerBuscaChaveGrpcServiceGrpc
import br.com.zup.KeyManagerListaChavesGrpcServiceGrpc
import br.com.zup.KeyManagerRegistraGrpcServiceGrpc
import br.com.zup.KeyManagerRemoveGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

//classe para gerar as factorys dos meus servicos gRPC
@Factory
class KeyManagerGrpcFactory(@GrpcChannel("keyManager") val channel: ManagedChannel) {

    @Singleton
    fun registraChave() = KeyManagerRegistraGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun deletaChave() = KeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun listaChaves() = KeyManagerListaChavesGrpcServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun buscaChave() = KeyManagerBuscaChaveGrpcServiceGrpc.newBlockingStub(channel)


}