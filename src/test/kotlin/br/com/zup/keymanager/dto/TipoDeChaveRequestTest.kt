package br.com.zup.keymanager.dto

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TipoDeChaveRequestTest {

    @Nested
    inner class ChaveAleatoriaTest {

        @Test
        fun `deve ser valida quando for nula ou vazia`() {
            val tipoChave = TipoDeChaveRequest.ALEATORIA

            assertTrue(tipoChave.valida(""))
            assertTrue(tipoChave.valida(null))
        }

        @Test
        fun `nao deve ser valida quando cliente passar algum valor`() {
            val tipoChave = TipoDeChaveRequest.ALEATORIA

            assertFalse((tipoChave.valida("teste chave invalida")))
        }
    }

    @Nested
    inner class ChaveCpfTest{

        @Test
        fun `deve aceitar cpf valido`() {
            val tipoChave = TipoDeChaveRequest.CPF

            assertTrue(tipoChave.valida("95229252090"))
        }

        @Test
        fun `nao deve aceitar cpf nulo ou vazio` () {
            val tipoChave = TipoDeChaveRequest.CPF

            assertFalse(tipoChave.valida(""))
            assertFalse(tipoChave.valida(null))
        }

        @Test
        fun `nao deve aceitar cpf com formato invalido`(){
            val tipoChave = TipoDeChaveRequest.CPF

            assertFalse(tipoChave.valida("cpf incorreto"))
        }

        @Test
        fun `nao deve aceitar cpf com formato valido porem valor invalido`() {
            val tipoChave = TipoDeChaveRequest.CPF

            assertFalse(tipoChave.valida("11122233344"))
        }
    }

    @Nested
    inner class ChaveTelefoneTest {

        @Test
        fun `deve ser valida quando telefone estiver correto`() {
            val tipoChave = TipoDeChaveRequest.CELULAR

            assertTrue(tipoChave.valida("+1122333334444"))
        }

        @Test
        fun `nao deve ser valida quando for nula ou vazia`() {
            val tipoChave = TipoDeChaveRequest.CELULAR

            assertFalse(tipoChave.valida(""))
            assertFalse(tipoChave.valida(null))
        }

        @Test
        fun `nao deve ser valida quando telefone estiver em formato incorreto`() {
            val tipoChave = TipoDeChaveRequest.CELULAR

            assertFalse(tipoChave.valida("valor incorreto"))
        }

    }

    @Nested
    inner class ChaveEmailTest() {

        @Test
        fun `deve ser valido quando email estiver correto`() {
            val tipoChave = TipoDeChaveRequest.EMAIL

            assertTrue(tipoChave.valida("teste@teste.com.br"))
        }

        @Test
        fun `nao deve ser valido quando email for nulo ou vazio`() {
            val tipoChave = TipoDeChaveRequest.EMAIL

            assertFalse(tipoChave.valida(""))
            assertFalse(tipoChave.valida(null))
        }

        @Test
        fun `nao deve ser valido quando email estiver mal formatado`() {
            val tipoChave = TipoDeChaveRequest.EMAIL

            assertFalse(tipoChave.valida("email invalido"))
        }

    }


}