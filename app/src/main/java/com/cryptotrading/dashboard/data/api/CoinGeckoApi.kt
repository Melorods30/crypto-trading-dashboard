package com.cryptotrading.dashboard.data.api

import com.cryptotrading.dashboard.data.model.Crypto
import com.cryptotrading.dashboard.data.model.MarketData
import com.cryptotrading.dashboard.data.model.PriceHistory
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface da API CoinGecko para buscar dados de criptomoedas
 */
interface CoinGeckoApi {

    /**
     * Busca as principais criptomoedas ordenadas por volume de mercado
     * @param vsCurrency Moeda de referência (ex: "usd", "brl")
     * @param order Ordenação (market_cap_desc, volume_desc, etc.)
     * @param perPage Número de resultados por página
     * @param page Página atual
     * @param sparklineInclua dados do sparkline (gráfico miniatura)
     * @param priceChangePercentage Períodos para incluir variação de preço
     */
    @GET("coins/markets")
    suspend fun getCoinsMarkets(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = true,
        @Query("price_change_percentage") priceChangePercentage: String = "1h,24h,7d"
    ): Response<List<Crypto>>

    /**
     * Busca dados detalhados de uma criptomoeda específica
     * @param id ID da criptomoeda (ex: "bitcoin")
     * @param localization Incluir nomes localizados
     * @param tickers Incluir dados de tickers
     * @param marketData Incluir dados de mercado
     * @param communityData Incluir dados da comunidade
     * @param developerData Incluir dados do desenvolvedor
     * @param sparkline Incluir dados do sparkline
     */
    @GET("coins/{id}")
    suspend fun getCoinById(
        @Path("id") id: String,
        @Query("localization") localization: Boolean = false,
        @Query("tickers") tickers: Boolean = false,
        @Query("market_data") marketData: Boolean = true,
        @Query("community_data") communityData: Boolean = false,
        @Query("developer_data") developerData: Boolean = false,
        @Query("sparkline") sparkline: Boolean = true
    ): Response<MarketData>

    /**
     * Busca histórico de preços de uma criptomoeda
     * @param id ID da criptomoeda
     * @param vsCurrency Moeda de referência
     * @param days Número de dias de histórico
     * @param interval Intervalo dos dados (daily, hourly, minutely)
     */
    @GET("coins/{id}/market_chart")
    suspend fun getCoinMarketChart(
        @Path("id") id: String,
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("days") days: String = "7",
        @Query("interval") interval: String? = null
    ): Response<PriceHistory>

    /**
     * Busca dados de tickers para pares de negociação
     * @param id ID da criptomoeda
     * @param exchangeIds IDs das exchanges (opcional)
     * @param includeExchangeLogo Incluir logos das exchanges
     * @param page Página
     * @param order Ordenação
     * @param depth Incluir profundidade do livro de ordens
     */
    @GET("coins/{id}/tickers")
    suspend fun getCoinTickers(
        @Path("id") id: String,
        @Query("exchange_ids") exchangeIds: String? = null,
        @Query("include_exchange_logo") includeExchangeLogo: Boolean = false,
        @Query("page") page: Int = 1,
        @Query("order") order: String = "trust_score_desc",
        @Query("depth") depth: Boolean = false
    ): Response<Map<String, Any>>

    companion object {
        const val BASE_URL = "https://api.coingecko.com/api/v3/"
    }
}
