package com.cryptotrading.dashboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.cryptotrading.dashboard.ui.theme.SparklineColor

/**
 * Componente para exibir um gráfico sparkline (miniatura) do preço
 */
@Composable
fun SparklineChart(
    data: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = SparklineColor,
    strokeWidth: Float = 2f
) {
    if (data.isEmpty()) return

    Canvas(modifier = modifier.fillMaxSize()) {
        if (data.size < 2) return@Canvas

        val width = size.width
        val height = size.height

        // Encontrar valores mínimo e máximo para normalizar
        val minValue = data.minOrNull() ?: 0.0
        val maxValue = data.maxOrNull() ?: 0.0
        val range = maxValue - minValue

        if (range == 0.0) return@Canvas

        val path = Path()

        // Calcular pontos normalizados
        val points = data.mapIndexed { index, value ->
            val x = (index.toFloat() / (data.size - 1)) * width
            val normalizedValue = if (range > 0) (value - minValue) / range else 0.5
            val y = height - (normalizedValue.toFloat() * height)
            Offset(x, y)
        }

        // Desenhar linha conectando os pontos
        path.moveTo(points.first().x, points.first().y)
        points.drop(1).forEach { point ->
            path.lineTo(point.x, point.y)
        }

        // Desenhar o caminho
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = strokeWidth)
        )
    }
}
