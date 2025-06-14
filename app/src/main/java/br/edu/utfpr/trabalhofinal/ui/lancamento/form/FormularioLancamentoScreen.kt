package br.edu.utfpr.trabalhofinal.ui.lancamento.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.trabalhofinal.R
import br.edu.utfpr.trabalhofinal.data.TipoLancamentoEnum
import br.edu.utfpr.trabalhofinal.ui.lancamento.form.composables.FormCheckbox
import br.edu.utfpr.trabalhofinal.ui.lancamento.form.composables.FormRadioButton
import br.edu.utfpr.trabalhofinal.ui.lancamento.form.composables.FormTextField
import br.edu.utfpr.trabalhofinal.ui.theme.TrabalhoFinalTheme
import br.edu.utfpr.trabalhofinal.ui.utils.composables.Carregando
import br.edu.utfpr.trabalhofinal.ui.utils.composables.ErroAoCarregar

@Composable
fun FormularioLancamentoScreen(
    modifier: Modifier = Modifier,
    onVoltarPressed: () -> Unit,
    viewModel: FormularioLancamentoViewModel = viewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {

    var mostrarConfirmacao by remember { mutableStateOf(false) } // confirmação para exclusão

    LaunchedEffect(viewModel.state.lancamentoPersistidaOuRemovida) {
        if (viewModel.state.lancamentoPersistidaOuRemovida) {
            onVoltarPressed()
        }
    }
    val context = LocalContext.current
    LaunchedEffect(snackbarHostState, viewModel.state.codigoMensagem) {
        viewModel.state.codigoMensagem
            .takeIf { it > 0 }
            ?.let {
                snackbarHostState.showSnackbar(context.getString(it))
                viewModel.onMensagemExibida()
            }
    }

    val contentModifier: Modifier = modifier.fillMaxSize()
    if (viewModel.state.carregando) {
        Carregando(modifier = contentModifier)
    } else if (viewModel.state.erroAoCarregar) {
        ErroAoCarregar(
            modifier = contentModifier,
            onTryAgainPressed = viewModel::carregarLancamento
        )
    } else {
        Scaffold(
            modifier = contentModifier,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                AppBar(
                    lancamentoNovo = viewModel.state.lancamentoNovo,
                    processando = viewModel.state.salvando || viewModel.state.excluindo,
                    onVoltarPressed = onVoltarPressed,
                    onSalvarPressed = viewModel::salvarLancamento,
                    onExcluirPressed = viewModel::removerLancamento
                )
            }
        ) { paddingValues ->
            FormContent(
                modifier = Modifier.padding(paddingValues),
                processando = viewModel.state.salvando || viewModel.state.excluindo,
                descricao = viewModel.state.descricao,
                data = viewModel.state.data,
                valor = viewModel.state.valor,
                paga = viewModel.state.paga,
                tipo = viewModel.state.tipo,
                onDescricaoAlterada = viewModel::onDescricaoAlterada,
                onDataAlterada = viewModel::onDataAlterada,
                onValorAlterado = viewModel::onValorAlterado,
                onStatusPagamentoAlterado = viewModel::onStatusPagamentoAlterado,
                onTipoAlterado = viewModel::onTipoAlterado
            )

            if (showConfirmationDialog) {
                ConfirmationDialog(
                    title = stringResource(R.string.confirmar_exclusao),
                    text = stringResource(R.string.texto_confirmacao_exclusao),
                    onDismiss = { showConfirmationDialog = false },
                    onConfirm = {
                        showConfirmationDialog = false
                        viewModel.removerLancamento()
                    },
                    confirmButtonText = stringResource(R.string.sim),
                    dismissButtonText = stringResource(R.string.nao)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    lancamentoNovo: Boolean,
    processando: Boolean,
    onVoltarPressed: () -> Unit,
    onSalvarPressed: () -> Unit,
    onExcluirPressed: () -> Unit
) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            Text(if (lancamentoNovo) {
                stringResource(R.string.novo_lancamento)
            } else {
                stringResource(R.string.editar_lancamento)
            })
        },
        navigationIcon = {
            IconButton(onClick = onVoltarPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.voltar)
                )
            }
        },
        actions = {
            if (processando) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(all = 16.dp),
                    strokeWidth = 2.dp
                )
            } else {
                if (!lancamentoNovo) {
                    IconButton(onClick = onExcluirPressed) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.excluir)
                        )
                    }
                }
                IconButton(onClick = onSalvarPressed) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = stringResource(R.string.salvar)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun AppBarPreview() {
    TrabalhoFinalTheme {
        AppBar(
            lancamentoNovo = true,
            processando = false,
            onVoltarPressed = {},
            onSalvarPressed = {},
            onExcluirPressed = {}
        )
    }
}

@Composable
private fun FormContent(
    modifier: Modifier = Modifier,
    processando: Boolean,
    descricao: CampoFormulario,
    data: CampoFormulario,
    valor: CampoFormulario,
    paga: CampoFormulario,
    tipo: CampoFormulario,
    onDescricaoAlterada: (String) -> Unit,
    onDataAlterada: (String) -> Unit,
    onValorAlterado: (String) -> Unit,
    onStatusPagamentoAlterado: (String) -> Unit,
    onTipoAlterado: (String) -> Unit
) {
    Column(
        modifier = modifier
            .padding(all = 16.dp)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Notes,
                contentDescription = stringResource(R.string.descricao),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
            )
            FormTextField(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.descricao),
                value = descricao.valor,
                errorMessageCode = descricao.codigoMensagemErro,
                onValueChanged = onDescricaoAlterada,
                keyboardCapitalization = KeyboardCapitalization.Words,
                enabled = !processando
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.AttachMoney,
                contentDescription = stringResource(R.string.valor),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
            )
            FormTextField(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.valor),
                value = valor.valor,
                errorMessageCode = valor.codigoMensagemErro,
                onValueChanged = onValorAlterado,
                keyboardType = KeyboardType.Number,
                enabled = !processando
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(32.dp)) // ocupa o espaço do ícone anterior
            FormDatePicker(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.data),
                value = LocalDate.parse(data.valor, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                onValueChanged = {
                    onDataAlterada(it.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                },
                errorMessageCode = data.codigoMensagemErro,
                enabled = !processando
            )
        }

        val checkOptionsModifier = odifier.padding(horizontal = 17.dp, vertical = 8.dp)
        FormCheckbox(
            modifier = checkOptionsModifier,
            label = stringResource(R.string.paga),
            checked = paga.valor.toBoolean(),
            onCheckChanged = { newValue ->
                onStatusPagamentoAlterado(newValue.toString())
            },
            enabled = !processando
        )
        Row {
            FormRadioButton(
                modifier = checkModifier,
                value = TipoLancamentoEnum.DESPESA,
                groupValue = TipoLancamentoEnum.valueOf(tipo.valor),
                onValueChanged = { onTipoAlterado(it.toString()) },
                label = stringResource(R.string.despesa),
                enabled = !processando
            )
            FormRadioButton(
                modifier = checkModifier,
                value = TipoLancamentoEnum.RECEITA,
                groupValue = TipoLancamentoEnum.valueOf(tipo.valor),
                onValueChanged = { onTipoAlterado(it.toString()) },
                label = stringResource(R.string.receita),
                enabled = !processando
            )
        }
    }
}
@Composable
fun DatePickerField(
    modifier: Modifier = Modifier,
    dataSelecionada: String,
    errorMessageCode: Int,
    enabled: Boolean,
    onDataSelecionada: (String) -> Unit
) {
    val context = LocalContext.current
    val showDatePicker = remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val initialDate = remember(dataSelecionada) {
        kotlin.runCatching {
            LocalDate.parse(dataSelecionada, formatter)
        }.getOrNull() ?: LocalDate.now()
    }

    if (showDatePicker.value) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                onDataSelecionada(selectedDate.format(formatter))
                showDatePicker.value = false
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        ).show()
    }

    OutlinedTextField(
        value = dataSelecionada,
        onValueChange = {},
        readOnly = true,
        isError = errorMessageCode > 0,
        label = { Text(text = stringResource(R.string.data)) },
        trailingIcon = {
            IconButton(onClick = { if (enabled) showDatePicker.value = true }) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Selecione data")
            }
        },
        modifier = modifier
            .clickable(enabled = enabled) { showDatePicker.value = true },
        enabled = enabled,
        singleLine = true
    )
}

@Preview(showSystemUi = true)
@Composable
private fun FormContentPreview() {
    TrabalhoFinalTheme {
        FormContent(
            processando = false,
            descricao = CampoFormulario(),
            data = CampoFormulario(),
            valor = CampoFormulario(),
            paga = CampoFormulario(),
            tipo = CampoFormulario(TipoLancamentoEnum.RECEITA.toString()),
            onDescricaoAlterada = {},
            onDataAlterada = {},
            onValorAlterado = {},
            onStatusPagamentoAlterado = {},
            onTipoAlterado = {}
        )
    }
}
