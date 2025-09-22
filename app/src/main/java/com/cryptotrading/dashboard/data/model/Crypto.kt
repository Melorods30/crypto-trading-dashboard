package com.cryptotrading.dashboard.data.model

/**
 * Modelo de dados para representar uma criptomoeda
 */
data class Crypto(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val currentPrice: Double,
    val marketCap: Double,
    val marketCapRank: Int,
    val fullyDilutedValuation: Double?,
    val totalVolume: Double,
    val high24h: Double,
    val low24h: Double,
    val priceChange24h: Double,
    val priceChangePercentage24h: Double,
    val marketCapChange24h: Double,
    val marketCapChangePercentage24h: Double,
    val circulatingSupply: Double,
    val totalSupply: Double?,
    val maxSupply: Double?,
    val ath: Double,
    val athChangePercentage: Double,
    val athDate: String,
    val atl: Double,
    val atlChangePercentage: Double,
    val atlDate: String,
    val lastUpdated: String,
    val sparklineIn7d: SparklineData? = null,
    val priceChangePercentage7dInCurrency: Double? = null,
    val priceChangePercentage14dInCurrency: Double? = null,
    val priceChangePercentage30dInCurrency: Double? = null,
    val priceChangePercentage200dInCurrency: Double? = null,
    val priceChangePercentage1yInCurrency: Double? = null
)

/**
 * Dados do sparkline (gráfico miniatura de 7 dias)
 */
data class SparklineData(
    val price: List<Double>
)

/**
 * Resposta da API para lista de criptomoedas
 */
data class CryptoListResponse(
    val data: List<Crypto>
)
