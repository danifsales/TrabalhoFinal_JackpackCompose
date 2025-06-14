package br.edu.utfpr.trabalhofinal.ui.lancamento.form.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.utfpr.trabalhofinal.R
import br.edu.utfpr.trabalhofinal.ui.theme.TrabalhoFinalTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.*

@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    label: String,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    errorMessageCode: Int = 0,
    keyboardCapitalization: KeyboardCapitalization = KeyboardCapitalization.Unspecified,
    keyboardImeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
    leadingIcon: @Composable (() -> Unit)? = null //inserido

) {
    val hasError = errorMessageCode > 0
    Column(modifier = modifier) {
        IconFieldWrapper(leadingIcon = leadingIcon) { //inserido
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = onValueChanged,
                label = { Text(label) },
                maxLines = 1,
                enabled = enabled,
                readOnly = readOnly,
                isError = hasError,
                keyboardOptions = KeyboardOptions(
                    capitalization = keyboardCapitalization,
                    imeAction = keyboardImeAction,
                    keyboardType = keyboardType
                ),
                visualTransformation = visualTransformation,
                trailingIcon = trailingIcon
            )
        }
        if (hasError) {
            Text(
                text = stringResource(errorMessageCode),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FormTextFieldPreview() {
    TrabalhoFinalTheme {
        var value by remember { mutableStateOf("Teste") }
        var errorMessageCode = if (value.isBlank()) {
            R.string.descricao_obrigatoria
        } else {
            0
        }
        FormTextField(
            modifier = Modifier.padding(start = 1.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            value = value,
            onValueChanged = { newValue ->
                value = newValue
            },
            label = "Descrição",
            errorMessageCode = errorMessageCode,
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Notes,
                    contentDescription = "Ícone de descrição",
                    modifier = Modifier
                        .padding(start = 2.dp, end = 8.dp)
        )
    }
}