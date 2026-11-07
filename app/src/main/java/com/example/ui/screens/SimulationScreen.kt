package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.MirrorType
import com.example.model.OpticsCalculator
import com.example.model.OpticsResult
import com.example.model.PRESET_POSITIONS
import com.example.model.PresetPosition
import com.example.model.SimulationState
import com.example.ui.components.RayDiagramCanvas
import kotlin.math.abs

@Composable
fun SimulationScreen(
    state: SimulationState,
    onStateChange: (SimulationState) -> Unit,
    modifier: Modifier = Modifier
) {
    val result = OpticsCalculator.calculate(state)
    val scrollState = rememberScrollState()
    var isFullscreen by remember { mutableStateOf(false) }

    if (isFullscreen) {
        FullScreenSimulationDialog(
            state = state,
            result = result,
            onStateChange = onStateChange,
            onDismiss = { isFullscreen = false }
        )
    }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header with Language Toggle & Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (state.isEnglish) "Spherical Mirror Ray Simulation" else "গোলীয় দর্পণে প্রতিবিম্বের রশ্মিচিত্র",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (state.isEnglish) "Optics Lab" else "উত্তল ও অবতল দর্পণ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1877F2).copy(alpha = 0.15f),
                        modifier = Modifier
                            .clickable {
                                try {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.facebook.com/mybe.tanvir")
                                    )
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            .testTag("dev_header_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Tanvir Facebook Profile",
                                tint = Color(0xFF1877F2),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Dev: Tanvir",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1877F2)
                            )
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Language switch chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable { onStateChange(state.copy(isEnglish = !state.isEnglish)) }
                        .testTag("lang_toggle_btn")
                ) {
                    Text(
                        text = if (state.isEnglish) "🌐 EN" else "🌐 বাং",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        onStateChange(
                            SimulationState(
                                mirrorType = state.mirrorType,
                                isEnglish = state.isEnglish
                            )
                        )
                    },
                    modifier = Modifier.testTag("reset_simulation_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Simulation",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 2. Mirror Type Selector Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val concaveSelected = state.mirrorType == MirrorType.CONCAVE
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (concaveSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable {
                        onStateChange(
                            state.copy(
                                mirrorType = MirrorType.CONCAVE,
                                objectDistanceU = 1.40f
                            )
                        )
                    }
                    .padding(vertical = 10.dp)
                    .testTag("mirror_concave_tab"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (state.isEnglish) "Concave Mirror (অবতল)" else "অবতল দর্পণ (Concave)",
                    color = if (concaveSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (concaveSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                )
            }

            val convexSelected = state.mirrorType == MirrorType.CONVEX
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (convexSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable {
                        onStateChange(
                            state.copy(
                                mirrorType = MirrorType.CONVEX,
                                objectDistanceU = 1.40f
                            )
                        )
                    }
                    .padding(vertical = 10.dp)
                    .testTag("mirror_convex_tab"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (state.isEnglish) "Convex Mirror (উত্তল)" else "উত্তল দর্পণ (Convex)",
                    color = if (convexSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (convexSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }

        // 3. Ray Diagram Canvas with Fullscreen Button
        RayDiagramCanvas(
            state = state,
            result = result,
            onObjectDistanceChange = { newU ->
                onStateChange(state.copy(objectDistanceU = newU))
            },
            isFullscreen = false,
            onToggleFullscreen = { isFullscreen = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        // 4. Control Panel (⚙️ নিয়ন্ত্রণ প্যানেল / Control Panel)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().testTag("sliders_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (state.isEnglish) "➿ Control Panel ⏳" else "➿ নিয়ন্ত্রণ প্যানেল (Control Panel) ⏳",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                // Object Distance Slider (u)
                val minU = if (state.mirrorType == MirrorType.CONCAVE) 0.30f else 0.40f
                val maxU = if (state.mirrorType == MirrorType.CONCAVE) 3.50f else 2.80f
                SliderControl(
                    label = if (state.isEnglish) "Object Distance (u, m):" else "বস্তুর দূরত্ব (u, m):",
                    value = state.objectDistanceU,
                    range = minU..maxU,
                    step = 0.05f,
                    unit = "m",
                    onValueChange = { onStateChange(state.copy(objectDistanceU = it)) },
                    testTag = "slider_object_distance"
                )

                // Object Height Slider (h)
                SliderControl(
                    label = if (state.isEnglish) "Object Height (h, m):" else "বস্তুর উচ্চতা (h, m):",
                    value = state.objectHeightH,
                    range = 0.40f..1.30f,
                    step = 0.05f,
                    unit = "m",
                    onValueChange = { onStateChange(state.copy(objectHeightH = it)) },
                    testTag = "slider_object_height"
                )

                // Focal Length Slider (f)
                SliderControl(
                    label = if (state.isEnglish) "Focal Length (f, m):" else "ফোকাস দূরত্ব (f, m):",
                    value = state.focalLengthF,
                    range = 0.50f..1.50f,
                    step = 0.05f,
                    unit = "m",
                    onValueChange = { onStateChange(state.copy(focalLengthF = it)) },
                    testTag = "slider_focal_length"
                )

                // Ray Toggles inside Control Panel
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                Text(
                    text = if (state.isEnglish) "Ray Toggles" else "রশ্মির প্রদর্শন (Ray Toggles):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RayFilterChip(
                        label = if (state.isEnglish) "Parallel Ray (Yellow)" else "সমান্তরাল রশ্মি (হলুদ)",
                        color = Color(0xFFF59E0B),
                        selected = state.showRay1Parallel,
                        onSelectedChange = { onStateChange(state.copy(showRay1Parallel = it)) },
                        modifier = Modifier.weight(1f)
                    )
                    RayFilterChip(
                        label = if (state.isEnglish) "Center Ray (Blue)" else "বক্রতা কেন্দ্র / ফোকাসগামী (নীল)",
                        color = Color(0xFF0284C7),
                        selected = state.showRay2Curvature,
                        onSelectedChange = { onStateChange(state.copy(showRay2Curvature = it)) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RayFilterChip(
                        label = if (state.isEnglish) "Pole Ray (Pink)" else "মেরুগামী রশ্মি (গোলাপি)",
                        color = Color(0xFFEC4899),
                        selected = state.showRay3Pole,
                        onSelectedChange = { onStateChange(state.copy(showRay3Pole = it)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 5. Image Characteristics & Metrics Card (প্রতিবিম্বের বৈশিষ্ট্য)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().testTag("stats_card")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.isEnglish) "Characteristics of Image" else "প্রতিবিম্বের বৈশিষ্ট্য",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = if (state.isEnglish) result.caseNameEn else result.caseNameBn,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricBadge(
                        label = if (state.isEnglish) "Nature of Image:" else "প্রতিবিম্বের প্রকৃতি:",
                        value = if (state.isEnglish) result.natureTextEn else result.natureTextBn,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBadge(
                        label = if (state.isEnglish) "Size of Image:" else "প্রতিবিম্বের আকার:",
                        value = if (state.isEnglish) result.sizeTextEn else result.sizeTextBn,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val vDisplay = if (result.isInfinity) {
                        if (state.isEnglish) "Infinity (∞)" else "অসীম (∞)"
                    } else {
                        "${String.format("%.2f", abs(result.v))} m"
                    }
                    MetricBadge(
                        label = if (state.isEnglish) "Image Distance (v, m):" else "প্রতিবিম্বের দূরত্ব (v, m):",
                        value = vDisplay,
                        modifier = Modifier.weight(1f)
                    )

                    val mDisplay = if (result.isInfinity) {
                        "∞"
                    } else {
                        String.format("%.2f", result.magnification)
                    }
                    MetricBadge(
                        label = if (state.isEnglish) "Magnification (m):" else "বিবর্ধন (m):",
                        value = mDisplay,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Dynamic Physical Explanation Tip & Cardinal Points Note
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.isEnglish) "P: Pole, F: Focus, C: Centre of Curvature" else "P: দর্পণের মেরু, F: ফোকাস, C: বক্রতার কেন্দ্র।",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = if (state.isEnglish) result.explanationTipEn else result.explanationTipBn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 6. Quick Case Presets Row (দ্রুত অবস্থান নির্বাচন)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = if (state.isEnglish) "⚡ Quick Presets (Textbook Cases):" else "⚡ দ্রুত অবস্থান নির্বাচন (পাঠ্যবইয়ের বিশেষ কেস):",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            val presetsScroll = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(presetsScroll),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PRESET_POSITIONS.forEachIndexed { index, preset ->
                    val isSelected = state.mirrorType == preset.mirrorType &&
                            abs(state.objectDistanceU - preset.u) < 0.04f &&
                            abs(state.focalLengthF - preset.f) < 0.04f

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isSelected) borderBorder(MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .clickable {
                                onStateChange(
                                    state.copy(
                                        mirrorType = preset.mirrorType,
                                        objectDistanceU = preset.u,
                                        objectHeightH = preset.h,
                                        focalLengthF = preset.f
                                    )
                                )
                            }
                            .testTag("preset_btn_$index")
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = if (state.isEnglish) preset.titleEn else preset.titleBn,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = preset.descriptionBn,
                                fontSize = 10.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        // 7. Developer & Contact on Facebook Card (কে বানালো অ্যাপটি / ফেসবুকে যোগাযোগ)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("developer_contact_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Spherical Mirrors Physics Simulation © 2026 | Developed by Tanvir",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = {
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.facebook.com/mybe.tanvir")
                            )
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1877F2) // Official Facebook Brand Blue
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .testTag("contact_facebook_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Contact on Facebook",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isEnglish) "Contact on Facebook" else "Contact on Facebook (ফেসবুকে যোগাযোগ করুন)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MetricBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SliderControl(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    step: Float,
    unit: String,
    onValueChange: (Float) -> Unit,
    testTag: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        val next = (value - step).coerceIn(range.start, range.endInclusive)
                        onValueChange(next)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "${String.format("%.2f", value)} $unit",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                IconButton(
                    onClick = {
                        val next = (value + step).coerceIn(range.start, range.endInclusive)
                        onValueChange(next)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

@Composable
private fun RayFilterChip(
    label: String,
    color: Color,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = { onSelectedChange(!selected) },
        label = {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color, CircleShape)
            )
        },
        modifier = modifier
    )
}

private fun borderBorder(color: Color) = androidx.compose.foundation.BorderStroke(1.5.dp, color)

@Composable
fun FullScreenSimulationDialog(
    state: SimulationState,
    result: OpticsResult,
    onStateChange: (SimulationState) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        BackHandler { onDismiss() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF070B14))
                .statusBarsPadding()
                .navigationBarsPadding()
                .testTag("fullscreen_simulation_dialog")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. Top Bar inside Fullscreen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0x2EFFFFFF), CircleShape)
                                .testTag("fullscreen_close_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (state.isEnglish) "Full Screen Ray Diagram" else "পূর্ণাঙ্গ রশ্মিচিত্র সিমুলেশন",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (state.mirrorType == MirrorType.CONCAVE) {
                                    if (state.isEnglish) "Concave Mirror (Converging)" else "অবতল দর্পণ (অভিসারী)"
                                } else {
                                    if (state.isEnglish) "Convex Mirror (Diverging)" else "উত্তল দর্পণ (অপসারী)"
                                },
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Quick Switch Mirror Type & Reset
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (state.mirrorType == MirrorType.CONCAVE) MaterialTheme.colorScheme.primary else Color(0x33FFFFFF),
                            modifier = Modifier
                                .clickable { onStateChange(state.copy(mirrorType = MirrorType.CONCAVE)) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("fullscreen_concave_chip")
                        ) {
                            Text(
                                text = if (state.isEnglish) "Concave" else "অবতল",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = if (state.mirrorType == MirrorType.CONCAVE) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (state.mirrorType == MirrorType.CONVEX) MaterialTheme.colorScheme.primary else Color(0x33FFFFFF),
                            modifier = Modifier
                                .clickable { onStateChange(state.copy(mirrorType = MirrorType.CONVEX)) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("fullscreen_convex_chip")
                        ) {
                            Text(
                                text = if (state.isEnglish) "Convex" else "উত্তল",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = if (state.mirrorType == MirrorType.CONVEX) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        IconButton(
                            onClick = {
                                onStateChange(
                                    SimulationState(
                                        mirrorType = state.mirrorType,
                                        isEnglish = state.isEnglish
                                    )
                                )
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // 2. Fullscreen Expansive Canvas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    RayDiagramCanvas(
                        state = state,
                        result = result,
                        onObjectDistanceChange = { newU ->
                            onStateChange(state.copy(objectDistanceU = newU))
                        },
                        isFullscreen = true,
                        onToggleFullscreen = onDismiss,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // 3. Immersive Bottom HUD
                Surface(
                    color = Color(0xF00A0F1D),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Stats summary pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val vText = if (result.isInfinity) "∞" else "${String.format("%.2f", abs(result.v))}m"
                            val mText = if (result.isInfinity) "∞" else String.format("%.2f", result.magnification)

                            MiniStatPill(
                                label = if (state.isEnglish) "Nature" else "প্রকৃতি",
                                value = if (state.isEnglish) result.natureTextEn else result.natureTextBn,
                                modifier = Modifier.weight(1.4f)
                            )
                            MiniStatPill(
                                label = if (state.isEnglish) "Size" else "আকার",
                                value = if (state.isEnglish) result.sizeTextEn else result.sizeTextBn,
                                modifier = Modifier.weight(1.1f)
                            )
                            MiniStatPill(
                                label = "v",
                                value = vText,
                                modifier = Modifier.weight(0.7f)
                            )
                            MiniStatPill(
                                label = "m",
                                value = mText,
                                modifier = Modifier.weight(0.7f)
                            )
                        }

                        // Compact Object Distance (u) Slider
                        val minU = if (state.mirrorType == MirrorType.CONCAVE) 0.30f else 0.40f
                        val maxU = if (state.mirrorType == MirrorType.CONCAVE) 3.50f else 2.80f
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "u: ${String.format("%.2f", state.objectDistanceU)}m",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(66.dp)
                            )
                            IconButton(
                                onClick = {
                                    val next = (state.objectDistanceU - 0.05f).coerceIn(minU, maxU)
                                    onStateChange(state.copy(objectDistanceU = next))
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Slider(
                                value = state.objectDistanceU,
                                onValueChange = { onStateChange(state.copy(objectDistanceU = it)) },
                                valueRange = minU..maxU,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    val next = (state.objectDistanceU + 0.05f).coerceIn(minU, maxU)
                                    onStateChange(state.copy(objectDistanceU = next))
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Presets row inside fullscreen
                        val presetsScroll = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(presetsScroll),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PRESET_POSITIONS.forEach { preset ->
                                val isSelected = state.mirrorType == preset.mirrorType &&
                                        abs(state.objectDistanceU - preset.u) < 0.04f &&
                                        abs(state.focalLengthF - preset.f) < 0.04f
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0x22FFFFFF),
                                    modifier = Modifier
                                        .clickable {
                                            onStateChange(
                                                state.copy(
                                                    mirrorType = preset.mirrorType,
                                                    objectDistanceU = preset.u,
                                                    objectHeightH = preset.h,
                                                    focalLengthF = preset.f
                                                )
                                            )
                                        }
                                ) {
                                    Text(
                                        text = if (state.isEnglish) preset.titleEn else preset.titleBn,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Ray Toggles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CompactRayChip(
                                label = if (state.isEnglish) "Ray 1: Parallel" else "১. সমান্তরাল (হলুদ)",
                                color = Color(0xFFF59E0B),
                                selected = state.showRay1Parallel,
                                onToggle = { onStateChange(state.copy(showRay1Parallel = it)) },
                                modifier = Modifier.weight(1f)
                            )
                            CompactRayChip(
                                label = if (state.isEnglish) "Ray 2: Center" else "২. কেন্দ্র (নীল)",
                                color = Color(0xFF0284C7),
                                selected = state.showRay2Curvature,
                                onToggle = { onStateChange(state.copy(showRay2Curvature = it)) },
                                modifier = Modifier.weight(1f)
                            )
                            CompactRayChip(
                                label = if (state.isEnglish) "Ray 3: Pole" else "৩. মেরু (গোলাপি)",
                                color = Color(0xFFEC4899),
                                selected = state.showRay3Pole,
                                onToggle = { onStateChange(state.copy(showRay3Pole = it)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniStatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = Color(0x33FFFFFF)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                fontSize = 9.sp,
                color = Color(0xFF94A3B8)
            )
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun CompactRayChip(
    label: String,
    color: Color,
    selected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { onToggle(!selected) },
        shape = RoundedCornerShape(6.dp),
        color = if (selected) color.copy(alpha = 0.25f) else Color(0x1AFFFFFF),
        border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, color) else null,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (selected) Color.White else Color(0xFF94A3B8),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

