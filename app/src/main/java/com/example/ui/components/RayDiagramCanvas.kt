package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MirrorType
import com.example.model.OpticsCalculator
import com.example.model.OpticsResult
import com.example.model.SimulationState
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
fun RayDiagramCanvas(
    state: SimulationState,
    result: OpticsResult,
    onObjectDistanceChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    isFullscreen: Boolean = false,
    onToggleFullscreen: (() -> Unit)? = null
) {
    val darkBg = Color(0xFF070B14)
    val principalAxisColor = Color(0xFF64748B)
    val mirrorGlassColor = Color(0xFF38BDF8)
    val mirrorHatchColor = Color(0xFF475569)
    val objectColor = Color(0xFFA855F7)
    val imageRealColor = Color(0xFF10B981)
    val imageVirtualColor = Color(0xFF34D399)
    val ray1Color = Color(0xFFF59E0B) // Amber
    val ray2Color = Color(0xFF0284C7) // Sky / Cyan
    val ray3Color = Color(0xFFEC4899) // Rose / Pink
    val pointLabelColor = Color(0xFFCBD5E1)
    val pointMarkerColor = Color(0xFFFB7185)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(darkBg)
            .testTag("ray_diagram_canvas_box")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("ray_diagram_canvas")
                .pointerInput(state.mirrorType, state.focalLengthF) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val w = size.width.toFloat()
                        val mirrorX = if (state.mirrorType == MirrorType.CONCAVE) w * 0.54f else w * 0.58f
                        val maxSpan = max(
                            state.objectDistanceU * 100f,
                            if (state.mirrorType == MirrorType.CONCAVE) 2f * state.focalLengthF * 100f else state.focalLengthF * 100f
                        ).coerceAtLeast(200f)
                        val scale = min(
                            ((mirrorX - 40f) / maxSpan).coerceIn(0.4f, 1.3f),
                            ((size.height * 0.40f) / 130f).coerceIn(0.4f, 1.3f)
                        )
                        // Delta u = -dx / (100 * scale)
                        val deltaU = -dragAmount.x / (100f * scale)
                        val minU = if (state.mirrorType == MirrorType.CONCAVE) 0.30f else 0.40f
                        val maxU = if (state.mirrorType == MirrorType.CONCAVE) 3.50f else 2.80f
                        val newU = (state.objectDistanceU + deltaU).coerceIn(minU, maxU)
                        onObjectDistanceChange(newU)
                    }
                }
        ) {
            val w = size.width
            val h = size.height
            if (w <= 0 || h <= 0) return@Canvas

            val centerY = h / 2f
            val isConcave = state.mirrorType == MirrorType.CONCAVE
            val mirrorX = if (isConcave) w * 0.54f else w * 0.58f

            val u = state.objectDistanceU
            val objH = state.objectHeightH
            val fDist = state.focalLengthF

            // Dynamic coordinate scaling
            val maxSpan = max(
                u * 100f,
                if (isConcave) 2f * fDist * 100f else fDist * 100f
            ).coerceAtLeast(200f)
            val scale = min(
                ((mirrorX - 45f) / maxSpan).coerceIn(0.4f, 1.3f),
                ((h * 0.40f) / 130f).coerceIn(0.4f, 1.3f)
            )

            val fDraw = fDist * 100f * scale
            val uDraw = u * 100f * scale
            val objHDraw = objH * 100f * scale

            val fx = if (isConcave) mirrorX - fDraw else mirrorX + fDraw
            val cx = if (isConcave) mirrorX - 2f * fDraw else mirrorX + 2f * fDraw

            val objX = mirrorX - uDraw
            val objY = centerY - objHDraw

            // 1. Draw Subtle Background Grid
            val gridStep = 40f * scale
            var gx = mirrorX % gridStep
            while (gx < w) {
                drawLine(
                    color = Color(0x0CFFFFFF),
                    start = Offset(gx, 0f),
                    end = Offset(gx, h),
                    strokeWidth = 1f
                )
                gx += gridStep
            }
            var gy = centerY % gridStep
            while (gy < h) {
                drawLine(
                    color = Color(0x0CFFFFFF),
                    start = Offset(0f, gy),
                    end = Offset(w, gy),
                    strokeWidth = 1f
                )
                gy += gridStep
            }

            // 2. Draw Principal Axis
            drawDashedLine(
                start = Offset(20f, centerY),
                end = Offset(w - 20f, centerY),
                color = principalAxisColor,
                strokeWidth = 2f,
                dashLength = 8f,
                spaceLength = 6f
            )

            // 3. Draw Spherical Mirror Arc & Silvered Hatchings
            val radius = max(2f * fDraw, 120f * scale)
            val arcAngle = 0.52f // radians

            if (isConcave) {
                // Concave: Center of circle is at (mirrorX - radius, centerY)
                // Reflective surface faces Left (towards object)
                val circleCenterX = mirrorX - radius
                val path = Path()
                val steps = 30
                for (i in 0..steps) {
                    val theta = -arcAngle + (2f * arcAngle * i / steps)
                    val px = circleCenterX + radius * cos(theta)
                    val py = centerY + radius * sin(theta)
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                drawPath(path, color = mirrorGlassColor, style = Stroke(width = 6f, cap = StrokeCap.Round))

                // Silvered / non-reflecting hatch lines on the right (back)
                var angle = -arcAngle
                while (angle <= arcAngle) {
                    val hx = circleCenterX + radius * cos(angle)
                    val hy = centerY + radius * sin(angle)
                    drawLine(
                        color = mirrorHatchColor,
                        start = Offset(hx, hy),
                        end = Offset(hx + 10f * scale, hy + 6f * scale),
                        strokeWidth = 2f
                    )
                    angle += 0.07f
                }
            } else {
                // Convex: Center of circle is at (mirrorX + radius, centerY)
                // Reflective surface faces Left (bulges outwards towards object)
                val circleCenterX = mirrorX + radius
                val path = Path()
                val steps = 30
                for (i in 0..steps) {
                    val theta = (PI.toFloat() - arcAngle) + (2f * arcAngle * i / steps)
                    val px = circleCenterX + radius * cos(theta)
                    val py = centerY + radius * sin(theta)
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                drawPath(path, color = mirrorGlassColor, style = Stroke(width = 6f, cap = StrokeCap.Round))

                // Hatch lines on the inside / right (back)
                var angle = -arcAngle
                while (angle <= arcAngle) {
                    val theta = PI.toFloat() + angle
                    val hx = circleCenterX + radius * cos(theta)
                    val hy = centerY + radius * sin(theta)
                    drawLine(
                        color = mirrorHatchColor,
                        start = Offset(hx, hy),
                        end = Offset(hx + 10f * scale, hy + 6f * scale),
                        strokeWidth = 2f
                    )
                    angle += 0.07f
                }
            }

            // 4. Draw Cardinal Points: P (Pole), F (Focus), C (Center of Curvature)
            val points = listOf(
                Triple(mirrorX, "P (মেরু)", "Pole"),
                Triple(fx, "F (ফোকাস)", "Focus"),
                Triple(cx, "C (বক্রতার কেন্দ্র)", "Centre C")
            )
            points.forEach { (px, bnLabel, enLabel) ->
                drawCircle(color = pointMarkerColor, radius = 5f, center = Offset(px, centerY))
                drawCircle(color = Color(0x40FB7185), radius = 9f, center = Offset(px, centerY))
                val label = if (state.isEnglish) enLabel else bnLabel
                drawTextNative(
                    text = label,
                    x = px,
                    y = centerY + 24f,
                    color = android.graphics.Color.parseColor("#CBD5E1"),
                    textSize = 28f,
                    alignCenter = true
                )
            }

            // 5. Draw Object Arrow (বস্তু)
            drawVectorArrow(
                from = Offset(objX, centerY),
                to = Offset(objX, objY),
                color = objectColor,
                strokeWidth = 5f,
                headSize = 18f
            )
            // Object draggable hint pulse ring at top of object
            drawCircle(color = Color(0x60A855F7), radius = 14f, center = Offset(objX, objY))
            drawCircle(color = objectColor, radius = 6f, center = Offset(objX, objY))

            val objText = if (state.isEnglish) "Object (u=${String.format("%.2f", u)}m)" else "বস্তু (u=${String.format("%.2f", u)}m)"
            drawTextNative(
                text = objText,
                x = objX,
                y = objY - 14f,
                color = android.graphics.Color.parseColor("#D8B4FE"),
                textSize = 30f,
                isBold = true,
                alignCenter = true
            )

            // 6. Calculate & Draw Image Arrow (প্রতিবিম্ব)
            val v = result.v
            val isInfinity = result.isInfinity
            val m = result.magnification

            val imgX = if (isConcave) {
                if (isInfinity) -9999f else mirrorX - v * 100f * scale
            } else {
                mirrorX + abs(v) * 100f * scale
            }
            val imgY = if (isInfinity) centerY else centerY - (objHDraw * m)

            val canDrawImage = !isInfinity && abs(v) <= 40f && imgX >= -100f && imgX <= w + 100f
            if (canDrawImage) {
                val isVirtual = !result.isReal
                val imgColor = if (isVirtual) imageVirtualColor else imageRealColor

                drawVectorArrow(
                    from = Offset(imgX, centerY),
                    to = Offset(imgX, imgY),
                    color = imgColor,
                    strokeWidth = 4.5f,
                    headSize = 16f,
                    dashed = isVirtual
                )

                val imgLabel = if (state.isEnglish) {
                    if (isVirtual) "Virtual Image" else "Real Image"
                } else {
                    if (isVirtual) "অবাস্তব প্রতিবিম্ব" else "বাস্তব প্রতিবিম্ব"
                }
                val imgDistLabel = "v = ${String.format("%.2f", abs(v))}m"
                val labelY = if (imgY < centerY) imgY - 14f else imgY + 34f
                drawTextNative(
                    text = "$imgLabel ($imgDistLabel)",
                    x = imgX,
                    y = labelY,
                    color = if (isVirtual) android.graphics.Color.parseColor("#6EE7B7") else android.graphics.Color.parseColor("#34D399"),
                    textSize = 28f,
                    isBold = true,
                    alignCenter = true
                )
            } else if (isInfinity) {
                // Visual label at top indicating image at infinity
                drawTextNative(
                    text = if (state.isEnglish) "Parallel Reflected Rays -> Image at Infinity (∞)" else "প্রতিফলিত রশ্মিগুলো সমান্তরাল -> প্রতিবিম্ব অসীম দূরত্বে (∞)",
                    x = w / 2f,
                    y = 36f,
                    color = android.graphics.Color.parseColor("#FBBF24"),
                    textSize = 32f,
                    isBold = true,
                    alignCenter = true
                )
            }

            // 7. Ray Tracing (Rays 1, 2, 3)

            // RAY 1: Parallel to Principal Axis
            if (state.showRay1Parallel) {
                // Incident ray: Horizontal from object tip to mirror
                drawVectorArrow(
                    from = Offset(objX, objY),
                    to = Offset(mirrorX, objY),
                    color = ray1Color,
                    strokeWidth = 3f,
                    headSize = 14f,
                    drawMidArrow = true
                )

                if (isConcave) {
                    if (u < fDist) {
                        // Concave virtual case (u < f): reflected ray goes down-left through Focus F
                        // Reflected line goes from (mirrorX, objY) through (fx, centerY)
                        val slope = (centerY - objY) / (fx - mirrorX)
                        val endX = max(20f, fx - 160f * scale)
                        val endY = centerY + slope * (endX - fx)
                        drawVectorArrow(
                            from = Offset(mirrorX, objY),
                            to = Offset(endX, endY),
                            color = ray1Color,
                            strokeWidth = 3f,
                            headSize = 14f,
                            drawMidArrow = true
                        )

                        // Virtual backward extension behind mirror to image point
                        if (canDrawImage) {
                            drawDashedLine(
                                start = Offset(mirrorX, objY),
                                end = Offset(imgX, imgY),
                                color = ray1Color,
                                strokeWidth = 2.5f
                            )
                        } else {
                            drawDashedLine(
                                start = Offset(mirrorX, objY),
                                end = Offset(mirrorX + 160f * scale, objY - slope * (160f * scale)),
                                color = ray1Color,
                                strokeWidth = 2.5f
                            )
                        }
                    } else if (isInfinity) {
                        // At focus: reflects through fx and continues parallel
                        val slope = (centerY - objY) / (fx - mirrorX)
                        val endX = 20f
                        val endY = centerY + slope * (endX - fx)
                        drawVectorArrow(
                            from = Offset(mirrorX, objY),
                            to = Offset(endX, endY),
                            color = ray1Color,
                            strokeWidth = 3f,
                            headSize = 14f,
                            drawMidArrow = true
                        )
                    } else {
                        // Standard real image: passes through focus F and converges at image
                        val slope = (centerY - objY) / (fx - mirrorX)
                        val targetX = if (imgX < 40f) 20f else min(imgX - 60f * scale, fx - 60f * scale)
                        val endX = targetX.coerceAtLeast(15f)
                        val endY = centerY + slope * (endX - fx)
                        drawVectorArrow(
                            from = Offset(mirrorX, objY),
                            to = Offset(endX, endY),
                            color = ray1Color,
                            strokeWidth = 3f,
                            headSize = 14f,
                            drawMidArrow = true
                        )
                    }
                } else {
                    // Convex mirror: reflects diverging as if from virtual focus F behind mirror
                    val slope = (objY - centerY) / (mirrorX - fx)
                    val reflectedEndX = max(20f, mirrorX - 220f * scale)
                    val reflectedEndY = objY + slope * (reflectedEndX - mirrorX)
                    drawVectorArrow(
                        from = Offset(mirrorX, objY),
                        to = Offset(reflectedEndX, reflectedEndY),
                        color = ray1Color,
                        strokeWidth = 3f,
                        headSize = 14f,
                        drawMidArrow = true
                    )
                    // Virtual dashed extension back to F (passes through image tip!)
                    drawDashedLine(
                        start = Offset(mirrorX, objY),
                        end = Offset(fx, centerY),
                        color = ray1Color,
                        strokeWidth = 2.5f
                    )
                }
            }

            // RAY 2: Directed through Center of Curvature (C)
            if (state.showRay2Curvature) {
                if (isConcave) {
                    if (u < fDist) {
                        // Virtual case: line from C through object tip hits mirror at hitY
                        val hitY = centerY - (objHDraw * (cx - mirrorX)) / (cx - objX)
                        drawVectorArrow(
                            from = Offset(objX, objY),
                            to = Offset(mirrorX, hitY),
                            color = ray2Color,
                            strokeWidth = 3f,
                            headSize = 14f,
                            drawMidArrow = true
                        )
                        // Reflected ray goes back towards C
                        drawVectorArrow(
                            from = Offset(mirrorX, hitY),
                            to = Offset(cx - 50f * scale, centerY - (centerY - hitY) * 1.3f),
                            color = ray2Color,
                            strokeWidth = 3f,
                            headSize = 14f,
                            drawMidArrow = true
                        )
                        // Dashed virtual extension behind mirror to image
                        if (canDrawImage) {
                            drawDashedLine(
                                start = Offset(mirrorX, hitY),
                                end = Offset(imgX, imgY),
                                color = ray2Color,
                                strokeWidth = 2.5f
                            )
                        }
                    } else if (isInfinity) {
                        // Line through C
                        val hitY = centerY - (objHDraw * (cx - mirrorX)) / (cx - objX)
                        drawVectorArrow(
                            from = Offset(objX, objY),
                            to = Offset(mirrorX, hitY),
                            color = ray2Color,
                            strokeWidth = 3f,
                            headSize = 14f,
                            drawMidArrow = true
                        )
                        drawVectorArrow(
                            from = Offset(mirrorX, hitY),
                            to = Offset(cx - 80f * scale, centerY - (centerY - hitY) * 1.5f),
                            color = ray2Color,
                            strokeWidth = 3f,
                            headSize = 14f,
                            drawMidArrow = true
                        )
                    } else {
                        // Passing through or from C to mirror
                        val hitY = centerY - (objHDraw * (cx - mirrorX)) / (cx - objX)
                        drawVectorArrow(
                            from = Offset(objX, objY),
                            to = Offset(mirrorX, hitY),
                            color = ray2Color,
                            strokeWidth = 3f,
                            headSize = 14f,
                            drawMidArrow = true
                        )
                        // Reflected straight back through C to image
                        val targetX = min(imgX - 50f * scale, 20f)
                        val endX = targetX.coerceAtLeast(15f)
                        val endY = centerY - (objHDraw * (cx - endX)) / (cx - objX)
                        drawVectorArrow(
                            from = Offset(mirrorX, hitY),
                            to = Offset(endX, endY),
                            color = ray2Color,
                            strokeWidth = 3f,
                            headSize = 14f,
                            drawMidArrow = true
                        )
                    }
                } else {
                    // Convex mirror: directed toward virtual center C behind mirror
                    val hitY = centerY - (objHDraw * (cx - mirrorX)) / (cx - objX)
                    drawVectorArrow(
                        from = Offset(objX, objY),
                        to = Offset(mirrorX, hitY),
                        color = ray2Color,
                        strokeWidth = 3f,
                        headSize = 14f,
                        drawMidArrow = true
                    )
                    // Reflected back along same path
                    drawVectorArrow(
                        from = Offset(mirrorX, hitY),
                        to = Offset(objX - 60f * scale, centerY - (centerY - hitY) * 1.4f),
                        color = ray2Color,
                        strokeWidth = 3f,
                        headSize = 14f,
                        drawMidArrow = true
                    )
                    // Dashed extension to virtual C
                    drawDashedLine(
                        start = Offset(mirrorX, hitY),
                        end = Offset(cx, centerY),
                        color = ray2Color,
                        strokeWidth = 2.5f
                    )
                }
            }

            // RAY 3: Directed to Pole P
            if (state.showRay3Pole) {
                // Incident to Pole (mirrorX, centerY)
                drawVectorArrow(
                    from = Offset(objX, objY),
                    to = Offset(mirrorX, centerY),
                    color = ray3Color,
                    strokeWidth = 2.5f,
                    headSize = 13f,
                    drawMidArrow = true
                )

                // Reflected ray at equal angle below axis: theta_r = theta_i
                val angleSlope = (centerY - objY) / (mirrorX - objX)
                val reflectedEndX = max(20f, mirrorX - 220f * scale)
                val reflectedEndY = centerY + angleSlope * (mirrorX - reflectedEndX)
                drawVectorArrow(
                    from = Offset(mirrorX, centerY),
                    to = Offset(reflectedEndX, reflectedEndY),
                    color = ray3Color,
                    strokeWidth = 2.5f,
                    headSize = 13f,
                    drawMidArrow = true
                )

                // Virtual dashed extension behind mirror
                if (canDrawImage) {
                    drawDashedLine(
                        start = Offset(mirrorX, centerY),
                        end = Offset(imgX, imgY),
                        color = ray3Color,
                        strokeWidth = 2f
                    )
                } else if (!isConcave) {
                    drawDashedLine(
                        start = Offset(mirrorX, centerY),
                        end = Offset(mirrorX + 160f * scale, centerY - angleSlope * (160f * scale)),
                        color = ray3Color,
                        strokeWidth = 2f
                    )
                }
            }

            // 8. Legend pill at top right of canvas
            val legendX = w - 16f
            val legendY = 24f
            // (Canvas already has rich legend chips in UI)
        }

        // Overlay badge at top-left
        Box(
            modifier = Modifier
                .padding(12.dp)
                .background(Color(0xCC1E293B), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (state.isEnglish) {
                    if (state.mirrorType == MirrorType.CONCAVE) "Concave Mirror (Converging)" else "Convex Mirror (Diverging)"
                } else {
                    if (state.mirrorType == MirrorType.CONCAVE) "অবতল দর্পণ (অভিসারী)" else "উত্তল দর্পণ (অপসারী)"
                },
                color = Color.White,
                fontSize = 12.sp,
                style = MaterialTheme.typography.labelMedium
            )
        }

        // Expand to Fullscreen button at top-right
        if (onToggleFullscreen != null) {
            Surface(
                onClick = onToggleFullscreen,
                shape = RoundedCornerShape(8.dp),
                color = Color(0xCC1E293B),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .testTag(if (isFullscreen) "exit_fullscreen_btn" else "expand_fullscreen_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = if (isFullscreen) "Exit Full Screen" else "Expand Full Screen",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isFullscreen) {
                            if (state.isEnglish) "Exit" else "প্রস্থান"
                        } else {
                            if (state.isEnglish) "Full Screen" else "পূর্ণস্ক্রিন"
                        },
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Overlay drag hint badge at bottom-left
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .background(Color(0x990F172A), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (state.isEnglish) "👆 Drag on canvas to move object" else "👆 ক্যানভাসে ড্র্যাগ করে বস্তু সরান",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
        }
    }
}

private fun DrawScope.drawVectorArrow(
    from: Offset,
    to: Offset,
    color: Color,
    strokeWidth: Float,
    headSize: Float = 16f,
    dashed: Boolean = false,
    drawMidArrow: Boolean = false
) {
    if (dashed) {
        drawDashedLine(
            start = from,
            end = to,
            color = color,
            strokeWidth = strokeWidth
        )
    } else {
        drawLine(
            color = color,
            start = from,
            end = to,
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }

    val angle = atan2(to.y - from.y, to.x - from.x)
    val headAngle = PI.toFloat() / 6f

    // Draw arrowhead at 'to'
    val path = Path().apply {
        moveTo(to.x, to.y)
        lineTo(
            to.x - headSize * cos(angle - headAngle),
            to.y - headSize * sin(angle - headAngle)
        )
        lineTo(
            to.x - headSize * cos(angle + headAngle),
            to.y - headSize * sin(angle + headAngle)
        )
        close()
    }
    drawPath(path, color = color)

    // Mid arrow showing light direction
    if (drawMidArrow) {
        val midX = (from.x + to.x) / 2f
        val midY = (from.y + to.y) / 2f
        val midHeadSize = headSize * 0.85f
        val midPath = Path().apply {
            moveTo(midX, midY)
            lineTo(
                midX - midHeadSize * cos(angle - headAngle),
                midY - midHeadSize * sin(angle - headAngle)
            )
            lineTo(
                midX - midHeadSize * cos(angle + headAngle),
                midY - midHeadSize * sin(angle + headAngle)
            )
            close()
        }
        drawPath(midPath, color = color)
    }
}

private fun DrawScope.drawDashedLine(
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float,
    dashLength: Float = 8f,
    spaceLength: Float = 6f
) {
    val path = Path().apply {
        moveTo(start.x, start.y)
        lineTo(end.x, end.y)
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, spaceLength), 0f),
            cap = StrokeCap.Round
        )
    )
}

private fun DrawScope.drawTextNative(
    text: String,
    x: Float,
    y: Float,
    color: Int,
    textSize: Float = 24f,
    isBold: Boolean = false,
    alignCenter: Boolean = false
) {
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            this.color = color
            this.textSize = textSize
            this.isAntiAlias = true
            this.isFakeBoldText = isBold
            if (alignCenter) {
                this.textAlign = android.graphics.Paint.Align.CENTER
            }
        }
        drawText(text, x, y, paint)
    }
}
