package com.project.wirebarley_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.wirebarley_android.ui.FormatUtils
import com.project.wirebarley_android.ui.MainUiState
import com.project.wirebarley_android.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: MainViewModel = viewModel()
            val uiState by vm.uiState.collectAsState()
            MainScreen(
                uiState = uiState,
                onRefresh = { vm.fetchRates() },
                getRate = { vm.getRateFor(it) }
            )
        }
    }
}

private val TextBlack = Color.Black

@Composable
fun SlimAmountField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .width(140.dp)
            .height(28.dp)
            .border(
                BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 1.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                color = TextBlack
            ),
            cursorBrush = SolidColor(TextBlack),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text("0", color = Color.Gray, fontSize = 16.sp)
                }
                inner()
            }
        )
    }
}

@Composable
fun SlimDropdownField(
    label: String,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    items: List<Pair<String, String>>,
    onSelect: (String, String) -> Unit
) {
    Box(
        modifier = Modifier
            .width(140.dp)
            .height(28.dp)
            .border(
                BorderStroke(1.dp, Color.Black),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onExpandToggle() }
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(label, fontSize = 16.sp, color = TextBlack)

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandToggle() },
            modifier = Modifier.width(140.dp)
        ) {
            items.forEach { (code, text) ->
                DropdownMenuItem(onClick = {
                    onSelect(code, text)
                    onExpandToggle()
                }) {
                    Text(text, color = TextBlack)
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    uiState: MainUiState,
    onRefresh: () -> Unit,
    getRate: (String) -> Double?
) {
    val countries = listOf(
        "KRW" to "대한민국 (KRW)",
        "JPY" to "일본 (JPY)",
        "PHP" to "필리핀 (PHP)"
    )

    var receivingCurrency by remember { mutableStateOf("KRW") }
    var receivingLabel by remember { mutableStateOf("대한민국 (KRW)") }

    var sendingAmountText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf<String?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val rate = getRate(receivingCurrency)

    val timestamp = uiState.ratesResponse?.timestamp
    val formattedTime = remember(timestamp) {
        timestamp?.let {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            df.timeZone = TimeZone.getDefault()
            df.format(Date(it * 1000L))
        } ?: ""
    }

    var expanded by remember { mutableStateOf(false) }

    fun computeIfPossible() {
        val amount = sendingAmountText.toDoubleOrNull()
        if (amount == null) {
            resultText = null
            errorText = if (sendingAmountText.isBlank()) null else "송금액이 바르지 않습니다"
            return
        }
        if (amount <= 0 || amount > 10000) {
            resultText = null
            errorText = "송금액이 바르지 않습니다"
            return
        }
        if (rate == null) {
            resultText = null
            errorText = "환율 정보가 없습니다"
            return
        }

        val received = amount * rate
        resultText = FormatUtils.formatAmount(received)
        errorText = null
    }

    LaunchedEffect(sendingAmountText, rate, receivingCurrency) {
        computeIfPossible()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "환율 계산",
            fontSize = 36.sp,
            color = TextBlack,
            modifier = Modifier.padding(vertical = 20.dp)
        )

        Column(modifier = Modifier.fillMaxWidth(0.95f)) {

            Text("송금국가 : 미국 (USD)", color = TextBlack)
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("수취국가 :", color = TextBlack)
                Spacer(modifier = Modifier.width(8.dp))

                SlimDropdownField(
                    label = receivingLabel,
                    expanded = expanded,
                    onExpandToggle = { expanded = !expanded },
                    items = countries,
                    onSelect = { code, text ->
                        receivingCurrency = code
                        receivingLabel = text
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (rate != null) "환율 : ${FormatUtils.formatAmount(rate)} $receivingLabel / USD"
                else "환율 정보가 없습니다",
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (formattedTime.isNotEmpty()) "조회시간 : $formattedTime" else "",
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text("송금액 :", color = TextBlack)
                Spacer(modifier = Modifier.width(8.dp))

                SlimAmountField(
                    value = sendingAmountText,
                    onValueChange = {
                        sendingAmountText = it.filter { ch -> ch.isDigit() || ch == '.' }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text("USD", color = TextBlack)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (resultText != null) {
            Text(
                text = "수취금액은 $resultText $receivingCurrency 입니다",
                fontSize = 18.sp,
                color = TextBlack
            )
        } else if (errorText != null) {
            Text(errorText!!, color = Color.Red)
        }

        Spacer(modifier = Modifier.weight(1f))

        if (uiState.errorMessage != null) {
            Text("네트워크 오류: ${uiState.errorMessage}", color = Color.Red)
        }
    }
}