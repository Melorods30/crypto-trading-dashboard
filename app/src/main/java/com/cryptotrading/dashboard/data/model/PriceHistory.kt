package com.cryptotrading.dashboard.data.model

/**
 * Dados históricos de preço para uma criptomoeda
 */
data class PriceHistory(
    val prices: List<List<Double>>, // [timestamp, price]
    val marketCaps: List<List<Double>>, // [timestamp, marketCap]
    val totalVolumes: List<List<Double>> // [timestamp, volume]
)

/**
 * Ponto de dados do gráfico de preços
 */
data class PricePoint(
    val timestamp: Long,
    val price: Double
)

/**
 * Período de tempo para histórico de preços
 */
enum class TimePeriod(val days: String) {
    DAY_1("1"),
    DAY_7("7"),
    DAY_30("30"),
    DAY_90("90"),
    YEAR_1("365");

    companion object {
        fun fromString(value: String): TimePeriod {
            return when (value) {
                "1" -> DAY_1
                "7" -> DAY_7
                "30" -> DAY_30
                "90" -> DAY_90
                "365" -> YEAR_1
                else -> DAY_7
            }
        }
    }
}

/**
 * Par de negociação
 */
data class TradingPair(
    val base: String, // Ex: "BTC"
    val quote: String, // Ex: "USDT"
    val symbol: String, // Ex: "BTC/USDT"
    val price: Double,
    val volume24h: Double,
    val priceChange24h: Double
)
