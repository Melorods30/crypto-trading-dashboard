package com.cryptotrading.dashboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cryptotrading.dashboard.R
import com.cryptotrading.dashboard.data.model.TimePeriod
import com.cryptotrading.dashboard.data.model.TradingPair
import com.cryptotrading.dashboard.ui.components.PriceChart
import com.cryptotrading.dashboard.ui.components.PriceChartStats
import com.cryptotrading.dashboard.ui.theme.PriceDownColor
import com.cryptotrading.dashboard.ui.theme.PriceUpColor
import com.cryptotrading.dashboard.viewmodel.CryptoDetailViewModel
import java.text.NumberFormat
import java.util.*

/**
 * Tela de detalhes da criptomoeda
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CryptoDetailScreen(
    cryptoId: String,
    onBackClick: () -> Unit,
    viewModel: CryptoDetailViewModel = viewModel()
) {
    val detailState by viewModel.detailState.collectAsState()
    val priceHistoryState by viewModel.priceHistoryState.collectAsState()
    val tradingPairsState by viewModel.tradingPairsState.collectAsState()
    val selectedTimePeriod by viewModel.selectedTimePeriod.collectAsState()

    // Carregar dados quando a tela é criada
    LaunchedEffect(cryptoId) {
        viewModel.loadCryptoDetails(cryptoId)
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = false,
        onRefresh = { viewModel.refreshData() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (detailState) {
                        is CryptoDetailViewModel.DetailState.Success -> {
                            val data = (detailState as CryptoDetailViewModel.DetailState.Success).data
                            Text("${data.name} (${data.symbol.uppercase()})")
                        }
                        else -> {
                            Text(stringResource(R.string.app_name))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            when (detailState) {
                is CryptoDetailViewModel.DetailState.Loading -> {
                    LoadingScreen()
                }
                is CryptoDetailViewModel.DetailState.Success -> {
                    val cryptoData = (detailState as CryptoDetailViewModel.DetailState.Success).data

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        // Cabeçalho com informações principais
                        item {
                            CryptoHeader(cryptoData)
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Seletor de período do gráfico
                        item {
                            TimePeriodSelector(
                                selectedPeriod = selectedTimePeriod,
                                onPeriodSelected = { period ->
                                    viewModel.changeTimePeriod(period)
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Gráfico de preços
                        item {
                            when (priceHistoryState) {
                                is CryptoDetailViewModel.PriceHistoryState.Success -> {
                                    val priceData = priceHistoryState as CryptoDetailViewModel.PriceHistoryState.Success
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp)
                                        ) {
                                            Text(
                                                text = stringResource(R.string.price_history),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            PriceChart(
                                                pricePoints = priceData.data,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            PriceChartStats(pricePoints = priceData.data)
                                        }
                                    }
                                }
                                is CryptoDetailViewModel.PriceHistoryState.Loading -> {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                                is CryptoDetailViewModel.PriceHistoryState.Error -> {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = stringResource(R.string.error_loading_data),
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Pares de negociação
                        item {
                            Text(
                                text = stringResource(R.string.trading_pairs),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        when (tradingPairsState) {
                            is CryptoDetailViewModel.TradingPairsState.Success -> {
                                val pairs = (tradingPairsState as CryptoDetailViewModel.TradingPairsState.Success).pairs
                                items(pairs) { pair ->
                                    TradingPairItem(pair)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                            is CryptoDetailViewModel.TradingPairsState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                            is CryptoDetailViewModel.TradingPairsState.Error -> {
                                item {
                                    Text(
                                        text = stringResource(R.string.error_loading_data),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
                is CryptoDetailViewModel.DetailState.Error -> {
                    val errorMessage = (detailState as CryptoDetailViewModel.DetailState.Error).message
                    ErrorScreen(
                        message = errorMessage,
                        onRetry = { viewModel.refreshData() }
                    )
                }
            }

            PullRefreshIndicator(
                refreshing = false,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

/**
 * Cabeçalho com informações principais da criptomoeda
 */
@Composable
private fun CryptoHeader(cryptoData: com.cryptotrading.dashboard.data.model.MarketData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(cryptoData.image)
                .crossfade(true)
                .build(),
            contentDescription = "${cryptoData.name} icon",
            modifier = Modifier
                .size(64.dp)
                .padding(end = 16.dp),
            contentScale = ContentScale.Crop
        )

        // Informações
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = cryptoData.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = cryptoData.symbol.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Preço e variação
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatPrice(cryptoData.currentPrice),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(12.dp))
                val changePercent = cryptoData.priceChangePercentage24h ?: 0.0
                val changeColor = if (changePercent >= 0) PriceUpColor else PriceDownColor
                Text(
                    text = formatPercentage(changePercent),
                    style = MaterialTheme.typography.titleMedium,
                    color = changeColor,
                    fontWeight = FontWeight.Medium
                )
            }

            // Volume e Market Cap
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Volume 24h: ${formatPrice(cryptoData.totalVolume)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Cap. Mercado: ${formatPrice(cryptoData.marketCap)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Seletor de período de tempo para o gráfico
 */
@Composable
private fun TimePeriodSelector(
    selectedPeriod: TimePeriod,
    onPeriodSelected: (TimePeriod) -> Unit
) {
    val periods = listOf(
        TimePeriod.DAY_1 to "1D",
        TimePeriod.DAY_7 to "7D",
        TimePeriod.DAY_30 to "30D",
        TimePeriod.YEAR_1 to "1Y"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        periods.forEach { (period, label) ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = { Text(label) }
            )
        }
    }
}

/**
 * Item de par de negociação
 */
@Composable
private fun TradingPairItem(pair: TradingPair) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = pair.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Volume: ${formatPrice(pair.volume24h)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatPrice(pair.price),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                val changeColor = if (pair.priceChange24h >= 0) PriceUpColor else PriceDownColor
                Text(
                    text = formatPercentage(pair.priceChange24h),
                    style = MaterialTheme.typography.bodySmall,
                    color = changeColor
                )
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
