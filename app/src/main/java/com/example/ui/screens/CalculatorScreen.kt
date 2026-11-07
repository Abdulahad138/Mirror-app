package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MirrorType
import com.example.model.SimulationState
import kotlin.math.abs

@Composable
fun CalculatorScreen(
    state: SimulationState,
    onApplyToSimulation: (SimulationState) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var mirrorType by remember { mutableStateOf(state.mirrorType) }
    var inputU by remember { mutableStateOf(state.objectDistanceU.toString()) }
    var inputF by remember { mutableStateOf(state.focalLengthF.toString()) }
    var inputH by remember { mutableStateOf(state.objectHeightH.toString()) }

    var calculationResult by remember { mutableStateOf<CalcOutput?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun doCalculate() {
        errorMessage = null
        val u = inputU.toFloatOrNull()
        val f = inputF.toFloatOrNull()
        val h = inputH.toFloatOrNull() ?: 1.0f

        if (u == null || u <= 0) {
            errorMessage = if (state.isEnglish) "Please enter a valid positive object distance u" else "দয়া করে বস্তুর দূরত্বের (u) সঠিক ধনাত্মক মান দিন।"
            calculationResult = null
            return
        }
        if (f == null || f <= 0) {
            errorMessage = if (state.isEnglish) "Please enter a valid positive focal length f" else "দয়া করে ফোকাস দূরত্বের (f) সঠিক মান দিন।"
            calculationResult = null
            return
        }

        if (mirrorType == MirrorType.CONCAVE) {
            val diff = u - f
            if (abs(diff) < 0.001f) {
                calculationResult = CalcOutput(
                    v = Float.POSITIVE_INFINITY,
                    isInfinity = true,
                    m = Float.POSITIVE_INFINITY,
                    hPrime = Float.POSITIVE_INFINITY,
                    step1 = "1/f = 1/u + 1/v  =>  1/v = 1/$f - 1/$u = 0",
                    step2 = "v = 1 / 0 = ∞ (অসীম দূরত্বে)",
                    step3 = "m = - v / u = - ∞ / $u = ∞",
                    nature = if (state.isEnglish) "Real & Inverted at Infinity" else "বাস্তব ও উল্টো (অসীম দূরত্বে)",
                    size = if (state.isEnglish) "Extremely Magnified" else "অত্যন্ত বিবর্ধিত"
                )
            } else {
                val v = (f * u) / diff
                val m = -v / u
                val hPrime = m * h
                val isVirtual = u < f
                val natureStr = if (isVirtual) {
                    if (state.isEnglish) "Virtual & Erect (behind mirror)" else "অবাস্তব ও সোজা (দর্পণের পেছনে)"
                } else {
                    if (state.isEnglish) "Real & Inverted (in front of mirror)" else "বাস্তব ও উল্টো (দর্পণের সামনে)"
                }
                val sizeStr = when {
                    abs(abs(m) - 1.0f) < 0.02f -> if (state.isEnglish) "Same Size as Object" else "বস্তুর সমান আকার"
                    abs(m) > 1.0f -> if (state.isEnglish) "Magnified (Larger)" else "বিবর্ধিত (বড়)"
                    else -> if (state.isEnglish) "Diminished (Smaller)" else "খর্বিত (ছোট)"
                }

                calculationResult = CalcOutput(
                    v = v,
                    isInfinity = false,
                    m = m,
                    hPrime = hPrime,
                    step1 = "1/f = 1/u + 1/v  =>  1/v = 1/$f - 1/$u",
                    step2 = "1/v = ${String.format("%.4f", 1f / f)} - ${String.format("%.4f", 1f / u)} = ${String.format("%.4f", (u - f) / (f * u))}",
                    step3 = "v = ($f × $u) / ($u - $f) = ${String.format("%.3f", v)} m",
                    nature = natureStr,
                    size = sizeStr
                )
            }
        } else {
            // Convex Mirror: 1/(-f) = 1/u + 1/v => 1/v = -1/f - 1/u => v = -(f * u) / (f + u)
            val vMagnitude = (f * u) / (f + u)
            val v = -vMagnitude
            val m = vMagnitude / u
            val hPrime = m * h

            calculationResult = CalcOutput(
                v = v,
                isInfinity = false,
                m = m,
                hPrime = hPrime,
                step1 = "1/(-f) = 1/u + 1/v  =>  1/v = - 1/$f - 1/$u",
                step2 = "1/v = -(${String.format("%.4f", 1f / f)} + ${String.format("%.4f", 1f / u)}) = -${String.format("%.4f", (f + u) / (f * u))}",
                step3 = "v = - ($f × $u) / ($f + $u) = ${String.format("%.3f", v)} m (দর্পণের পেছনে)",
                nature = if (state.isEnglish) "Virtual & Erect (behind mirror)" else "অবাস্তব ও সোজা (দর্পণের পেছনে)",
                size = if (state.isEnglish) "Diminished (Smaller)" else "খর্বিত (ছোট)"
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Column {
            Text(
                text = if (state.isEnglish) "Optics Formula Solver" else "দর্পণ সূত্র ও গাণিতিক সমাধান",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (state.isEnglish) "Step-by-step mathematical calculation of image distance and magnification" else "বস্তুর দূরত্ব ও ফোকাস দূরত্ব দিয়ে প্রতিবিম্ব ও বিবর্ধনের নির্ভুল হিসাব",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Inputs Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (state.isEnglish) "Select Mirror Type" else "দর্পণের ধরণ নির্বাচন করুন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = mirrorType == MirrorType.CONCAVE,
                        onClick = { mirrorType = MirrorType.CONCAVE },
                        label = { Text("অবতল দর্পণ (Concave)") },
                        modifier = Modifier.testTag("calc_concave_chip")
                    )
                    FilterChip(
                        selected = mirrorType == MirrorType.CONVEX,
                        onClick = { mirrorType = MirrorType.CONVEX },
                        label = { Text("উত্তল দর্পণ (Convex)") },
                        modifier = Modifier.testTag("calc_convex_chip")
                    )
                }

                OutlinedTextField(
                    value = inputU,
                    onValueChange = { inputU = it },
                    label = { Text(if (state.isEnglish) "Object Distance u (m)" else "বস্তুর দূরত্ব u (মিটার)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("calc_input_u")
                )

                OutlinedTextField(
                    value = inputF,
                    onValueChange = { inputF = it },
                    label = { Text(if (state.isEnglish) "Focal Length f (m)" else "ফোকাস দূরত্ব f (মিটার)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("calc_input_f")
                )

                OutlinedTextField(
                    value = inputH,
                    onValueChange = { inputH = it },
                    label = { Text(if (state.isEnglish) "Object Height h (m)" else "বস্তুর উচ্চতা h (মিটার)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("calc_input_h")
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = { doCalculate() },
                    modifier = Modifier.fillMaxWidth().testTag("calc_submit_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Calculate, contentDescription = null)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = if (state.isEnglish) "Calculate Step-by-Step" else "ধাপ অনুসারে হিসাব করুন",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Calculation Output Card
        calculationResult?.let { res ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().testTag("calc_result_card")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (state.isEnglish) "📝 Step-by-Step Mathematical Derivation" else "📝 ধাপে ধাপে গাণিতিক সমাধান",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    CalculationStepBox(number = "১", formula = res.step1)
                    CalculationStepBox(number = "২", formula = res.step2)
                    CalculationStepBox(number = "৩", formula = res.step3)

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            ResultRow(
                                label = if (state.isEnglish) "Image Distance (v):" else "প্রতিবিম্বের দূরত্ব (v):",
                                value = if (res.isInfinity) "∞ (অসীম)" else "${String.format("%.3f", abs(res.v))} m"
                            )
                            ResultRow(
                                label = if (state.isEnglish) "Magnification (m):" else "বিবর্ধন (m):",
                                value = if (res.isInfinity) "∞" else String.format("%.3f", res.m)
                            )
                            ResultRow(
                                label = if (state.isEnglish) "Image Height (h'):" else "প্রতিবিম্বের উচ্চতা (h'):",
                                value = if (res.isInfinity) "∞" else "${String.format("%.3f", abs(res.hPrime))} m"
                            )
                            ResultRow(
                                label = if (state.isEnglish) "Nature of Image:" else "প্রতিবিম্বের প্রকৃতি:",
                                value = res.nature
                            )
                            ResultRow(
                                label = if (state.isEnglish) "Size:" else "প্রতিবিম্বের আকার:",
                                value = res.size
                            )
                        }
                    }

                    // Button to apply directly to simulation canvas
                    Button(
                        onClick = {
                            val validU = inputU.toFloatOrNull() ?: 1.4f
                            val validF = inputF.toFloatOrNull() ?: 1.0f
                            val validH = inputH.toFloatOrNull() ?: 0.85f
                            onApplyToSimulation(
                                state.copy(
                                    mirrorType = mirrorType,
                                    objectDistanceU = validU.coerceIn(0.3f, 3.5f),
                                    focalLengthF = validF.coerceIn(0.4f, 1.5f),
                                    objectHeightH = validH.coerceIn(0.4f, 1.3f)
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        modifier = Modifier.fillMaxWidth().testTag("apply_to_sim_btn")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = if (state.isEnglish) "Load into Simulation Canvas 🚀" else "সিমুলেশন ক্যানভাসে দেখুন 🚀",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private data class CalcOutput(
    val v: Float,
    val isInfinity: Boolean,
    val m: Float,
    val hPrime: Float,
    val step1: String,
    val step2: String,
    val step3: String,
    val nature: String,
    val size: String
)

@Composable
private fun CalculationStepBox(number: String, formula: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(
                text = "ধাপ $number",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Text(
            text = formula,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}
