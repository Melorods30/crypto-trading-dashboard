package com.cryptotrading.dashboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.cryptotrading.dashboard.data.model.PricePoint
import com.cryptotrading.dashboard.ui.theme.PriceDownColor
import com.cryptotrading.dashboard.ui.theme.PriceUpColor
import com.cryptotrading.dashboard.ui.theme.SparklineColor

/**
 * Componente para exibir gráfico de preços histórico
 */
@Composable
fun PriceChart(
    pricePoints: List<PricePoint>,
    modifier: Modifier = Modifier,
    showFill: Boolean = true,
    lineColor: Color = SparklineColor,
    fillColor: Color = SparklineColor.copy(alpha = 0.1f),
    strokeWidth: Float = 3f
) {
    if (pricePoints.size < 2) {
        // Mostrar mensagem quando não há dados suficientes
        Box(modifier = modifier) {
            // Pode adicionar uma mensagem ou placeholder aqui
        }
        return
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val padding = 16f

        // Encontrar valores mínimo e máximo
        val prices = pricePoints.map { it.price }
        val minPrice = prices.minOrNull() ?: 0.0
        val maxPrice = prices.maxOrNull() ?: 0.0
        val priceRange = maxPrice - minPrice

        if (priceRange <= 0) return@Canvas

        // Calcular pontos normalizados
        val points = pricePoints.mapIndexed { index, pricePoint ->
            val x = padding + (index.toFloat() / (pricePoints.size - 1)) * (width - 2 * padding)
            val normalizedPrice = (pricePoint.price - minPrice) / priceRange
            val y = height - padding - (normalizedPrice.toFloat() * (height - 2 * padding))
            Offset(x, y)
        }

        // Desenhar área preenchida se solicitado
        if (showFill && points.size >= 2) {
            val fillPath = Path().apply {
                moveTo(points.first().x, height - padding)
                points.forEach { point ->
                    lineTo(point.x, point.y)
                }
                lineTo(points.last().x, height - padding)
                close()
            }

            drawPath(
                path = fillPath,
                color = fillColor,
                style = Fill
            )
        }

        // Desenhar linha do gráfico
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { point ->
                lineTo(point.x, point.y)
            }
        }

        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Desenhar pontos nos extremos se houver poucos pontos
        if (points.size <= 50) {
            points.forEach { point ->
                drawCircle(
                    color = lineColor,
                    radius = 4f,
                    center = point
                )
            }
        }
    }
}

/**
 * Componente para mostrar estatísticas do gráfico
 */
@Composable
fun PriceChartStats(
    pricePoints: List<PricePoint>,
    modifier: Modifier = Modifier
) {
    if (pricePoints.isEmpty()) return

    val prices = pricePoints.map { it.price }
    val currentPrice = prices.last()
    val startPrice = prices.first()
    val change = currentPrice - startPrice
    val changePercent = if (startPrice != 0.0) (change / startPrice) * 100 else 0.0

    val minPrice = prices.minOrNull() ?: 0.0
    val maxPrice = prices.maxOrNull() ?: 0.0

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PriceStatItem(
            label = "Preço Atual",
            value = String.format("%.2f", currentPrice),
            modifier = Modifier.weight(1f)
        )
        PriceStatItem(
            label = "Variação",
            value = String.format("%.2f%%", changePercent),
            color = if (changePercent >= 0) PriceUpColor else PriceDownColor,
            modifier = Modifier.weight(1f)
        )
        PriceStatItem(
            label = "Máx/Mín",
            value = String.format("%.2f / %.2f", maxPrice, minPrice),
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Item individual de estatística de preço
 */
@Composable
private fun PriceStatItem(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        androidx.compose.material3.Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
        )
    }
}
