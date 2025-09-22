package com.cryptotrading.dashboard.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cryptotrading.dashboard.R
import com.cryptotrading.dashboard.data.model.Crypto
import com.cryptotrading.dashboard.ui.theme.PriceDownColor
import com.cryptotrading.dashboard.ui.theme.PriceUpColor
import java.text.NumberFormat
import java.util.*

/**
 * Card para exibir informações de uma criptomoeda na lista
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoCard(
    crypto: Crypto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone da criptomoeda
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(crypto.image)
                    .crossfade(true)
                    .build(),
                contentDescription = "${crypto.name} icon",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Informações principais
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nome e símbolo
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = crypto.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = crypto.symbol.uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Preço atual
                Text(
                    text = formatPrice(crypto.currentPrice),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Sparkline e variação
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Variação percentual
                val changePercent = crypto.priceChangePercentage24h ?: 0.0
                val changeColor = if (changePercent >= 0) PriceUpColor else PriceDownColor

                Text(
                    text = formatPercentage(changePercent),
                    style = MaterialTheme.typography.bodyMedium,
                    color = changeColor,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Sparkline chart
                crypto.sparklineIn7d?.price?.let { sparklineData ->
                    SparklineChart(
                        data = sparklineData,
                        modifier = Modifier
                            .width(80.dp)
                            .height(32.dp)
                    )
                }
            }
        }
    }
}

/**
 * Formata preço para exibição
 */
private fun formatPrice(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(price)
}

/**
 * Formata porcentagem para exibição
 */
private fun formatPercentage(percentage: Double): String {
    val sign = if (percentage >= 0) "+" else ""
    return String.format(Locale.US, "%s%.2f%%", sign, percentage)
}
