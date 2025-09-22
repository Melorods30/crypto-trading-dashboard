package com.cryptotrading.dashboard.data.repository

import com.cryptotrading.dashboard.data.api.RetrofitClient
import com.cryptotrading.dashboard.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

/**
 * Repository para gerenciar dados de criptomoedas
 * Centraliza o acesso aos dados da API e implementa lógica de cache e tratamento de erros
 */
class CryptoRepository {

    private val api = RetrofitClient.api

    /**
     * Busca as principais criptomoedas por volume de mercado
     */
    fun getTopCryptos(
        orderBy: String = "market_cap_desc",
        limit: Int = 20
    ): Flow<Result<List<Crypto>>> = flow {
        try {
            emit(Result.Loading)

            val response = api.getCoinsMarkets(
                order = orderBy,
                perPage = limit,
                sparkline = true,
                priceChangePercentage = "1h,24h,7d"
            )

            if (response.isSuccessful) {
                response.body()?.let { cryptos ->
                    emit(Result.Success(cryptos))
                } ?: emit(Result.Error("Resposta vazia da API"))
            } else {
                emit(Result.Error("Erro na API: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Erro de rede: ${e.localizedMessage ?: "Erro desconhecido"}"))
        }
    }

    /**
     * Busca dados detalhados de uma criptomoeda específica
     */
    fun getCryptoDetails(cryptoId: String): Flow<Result<MarketData>> = flow {
        try {
            emit(Result.Loading)

            val response = api.getCoinById(
                id = cryptoId,
                marketData = true,
                sparkline = true
            )

            if (response.isSuccessful) {
                response.body()?.let { marketData ->
                    emit(Result.Success(marketData))
                } ?: emit(Result.Error("Resposta vazia da API"))
            } else {
                emit(Result.Error("Erro na API: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Erro de rede: ${e.localizedMessage ?: "Erro desconhecido"}"))
        }
    }

    /**
     * Busca histórico de preços de uma criptomoeda
     */
    fun getPriceHistory(
        cryptoId: String,
        days: String = "7"
    ): Flow<Result<PriceHistory>> = flow {
        try {
            emit(Result.Loading)

            val response = api.getCoinMarketChart(
                id = cryptoId,
                days = days
            )

            if (response.isSuccessful) {
                response.body()?.let { priceHistory ->
                    emit(Result.Success(priceHistory))
                } ?: emit(Result.Error("Resposta vazia da API"))
            } else {
                emit(Result.Error("Erro na API: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Erro de rede: ${e.localizedMessage ?: "Erro desconhecido"}"))
        }
    }

    /**
     * Busca tickers/pares de negociação de uma criptomoeda
     */
    fun getTradingPairs(cryptoId: String): Flow<Result<List<TradingPair>>> = flow {
        try {
            emit(Result.Loading)

            val response = api.getCoinTickers(id = cryptoId)

            if (response.isSuccessful) {
                response.body()?.let { tickerData ->
                    // Processar dados dos tickers para criar TradingPairs
                    val tradingPairs = parseTradingPairs(tickerData)
                    emit(Result.Success(tradingPairs))
                } ?: emit(Result.Error("Resposta vazia da API"))
            } else {
                emit(Result.Error("Erro na API: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Erro de rede: ${e.localizedMessage ?: "Erro desconhecido"}"))
        }
    }

    /**
     * Converte dados brutos dos tickers em objetos TradingPair
     */
    private fun parseTradingPairs(tickerData: Map<String, Any>): List<TradingPair> {
        val pairs = mutableListOf<TradingPair>()

        try {
            @Suppress("UNCHECKED_CAST")
            val tickers = tickerData["tickers"] as? List<Map<String, Any>> ?: return emptyList()

            // Agrupar por par de moedas e pegar o mais líquido
            val groupedPairs = tickers.groupBy { ticker ->
                val base = ticker["base"] as? String ?: ""
                val target = ticker["target"] as? String ?: ""
                "$base/$target"
            }

            groupedPairs.values.forEach { tickerGroup ->
                // Pegar o ticker com maior volume
                val bestTicker = tickerGroup.maxByOrNull { ticker ->
                    (ticker["volume"] as? Double) ?: 0.0
                } ?: return@forEach

                val base = bestTicker["base"] as? String ?: ""
                val target = bestTicker["target"] as? String ?: ""
                val last = bestTicker["last"] as? Double ?: 0.0
                val volume = bestTicker["volume"] as? Double ?: 0.0
                val change = bestTicker["bid_ask_spread_percentage"] as? Double ?: 0.0

                pairs.add(
                    TradingPair(
                        base = base,
                        quote = target,
                        symbol = "$base/$target",
                        price = last,
                        volume24h = volume,
                        priceChange24h = change
                    )
                )
            }

            // Limitar a 10 pares mais líquidos
            return pairs.sortedByDescending { it.volume24h }.take(10)

        } catch (e: Exception) {
            return emptyList()
        }
    }
}

/**
 * Classe selada para representar estados de resultado das operações
 */
sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}
