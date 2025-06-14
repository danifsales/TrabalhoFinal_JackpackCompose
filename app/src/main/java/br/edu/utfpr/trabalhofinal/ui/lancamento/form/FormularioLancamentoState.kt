package br.edu.utfpr.trabalhofinal.ui.lancamento.form

import br.edu.utfpr.trabalhofinal.data.Lancamento
import br.edu.utfpr.trabalhofinal.data.TipoLancamentoEnum
import br.edu.utfpr.trabalhofinal.utils.formatar
import java.time.LocalDate

data class CampoFormulario(
    val valor: String = "",
    val codigoMensagemErro: Int = 0
) {
    val contemErro get(): Boolean = codigoMensagemErro > 0
    val valido get(): Boolean = !contemErro
}

data class FormularioLancamentoState(
    val idLancamento: Int = 0,
    val carregando: Boolean = false,
    val lancamento: Lancamento = Lancamento(),
    val erroAoCarregar: Boolean = false,
    val salvando: Boolean = false,
    val mostrarDialogConfirmacao: Boolean = false,
    val excluindo: Boolean = false,
    val lancamentoPersistidaOuRemovida: Boolean = false,
    val codigoMensagem: Int = 0,
    val descricao: CampoFormulario = CampoFormulario(),
    //    val data: CampoFormulario = CampoFormulario(),
    val data: CampoFormulario = CampoFormulario(valor = LocalDate.now().formatar()),
    val valor: CampoFormulario = CampoFormulario(),
    val paga: CampoFormulario = CampoFormulario(valor = "false"),
    val tipo: CampoFormulario = CampoFormulario(TipoLancamentoEnum.DESPESA.name)
) {
    val lancamentoNovo get(): Boolean = idLancamento <= 0
    val formularioValido get(): Boolean = descricao.valido &&
            data.valido &&
            valor.valido &&
            paga.valido &&
            tipo.valido
}