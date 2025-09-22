package com.cryptotrading.dashboard.data.model

/**
 * Dados de mercado para uma criptomoeda específica
 */
data class MarketData(
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
    val priceChangePercentage7dInCurrency: Double?,
    val priceChangePercentage14dInCurrency: Double?,
    val priceChangePercentage30dInCurrency: Double?,
    val priceChangePercentage200dInCurrency: Double?,
    val priceChangePercentage1yInCurrency: Double?
)
