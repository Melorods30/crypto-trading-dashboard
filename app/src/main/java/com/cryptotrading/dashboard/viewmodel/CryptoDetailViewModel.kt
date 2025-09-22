package com.cryptotrading.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptotrading.dashboard.data.model.*
import com.cryptotrading.dashboard.data.repository.CryptoRepository
import com.cryptotrading.dashboard.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela de detalhes da criptomoeda
 * Gerencia dados detalhados, histórico de preços e pares de negociação
 */
class CryptoDetailViewModel(
    private val repository: CryptoRepository = CryptoRepository()
) : ViewModel() {

    /**
     * Estados possíveis para dados detalhados
     */
    sealed class DetailState {
        object Loading : DetailState()
        data class Success(val data: MarketData) : DetailState()
        data class Error(val message: String) : DetailState()
    }

    /**
     * Estados possíveis para histórico de preços
     */
    sealed class PriceHistoryState {
        object Loading : PriceHistoryState()
        data class Success(val data: List<PricePoint>, val period: TimePeriod) : PriceHistoryState()
        data class Error(val message: String) : PriceHistoryState()
    }

    /**
     * Estados possíveis para pares de negociação
     */
    sealed class TradingPairsState {
        object Loading : TradingPairsState()
        data class Success(val pairs: List<TradingPair>) : TradingPairsState()
        data class Error(val message: String) : TradingPairsState()
    }

    private val _detailState = MutableStateFlow<DetailState>(DetailState.Loading)
    val detailState: StateFlow<DetailState> = _detailState.asStateFlow()

    private val _priceHistoryState = MutableStateFlow<PriceHistoryState>(PriceHistoryState.Loading)
    val priceHistoryState: StateFlow<PriceHistoryState> = _priceHistoryState.asStateFlow()

    private val _tradingPairsState = MutableStateFlow<TradingPairsState>(TradingPairsState.Loading)
    val tradingPairsState: StateFlow<TradingPairsState> = _tradingPairsState.asStateFlow()

    private val _selectedTimePeriod = MutableStateFlow(TimePeriod.DAY_7)
    val selectedTimePeriod: StateFlow<TimePeriod> = _selectedTimePeriod.asStateFlow()

    private var currentCryptoId: String? = null

    /**
     * Carrega dados detalhados de uma criptomoeda
     */
    fun loadCryptoDetails(cryptoId: String) {
        if (currentCryptoId == cryptoId) return

        currentCryptoId = cryptoId

        viewModelScope.launch {
            repository.getCryptoDetails(cryptoId).collect { result ->
                when (result) {
                    is Result.Loading -> _detailState.value = DetailState.Loading
                    is Result.Success -> _detailState.value = DetailState.Success(result.data)
                    is Result.Error -> _detailState.value = DetailState.Error(result.message)
                }
            }
        }

        // Carregar histórico de preços e pares de negociação
        loadPriceHistory(cryptoId)
        loadTradingPairs(cryptoId)
    }

    /**
     * Carrega histórico de preços para o período selecionado
     */
    fun loadPriceHistory(cryptoId: String, period: TimePeriod = _selectedTimePeriod.value) {
        _selectedTimePeriod.value = period

        viewModelScope.launch {
            repository.getPriceHistory(cryptoId, period.days).collect { result ->
                when (result) {
                    is Result.Loading -> _priceHistoryState.value = PriceHistoryState.Loading
                    is Result.Success -> {
                        val pricePoints = result.data.prices.map { priceData ->
                            PricePoint(
                                timestamp = priceData[0].toLong(),
                                price = priceData[1]
                            )
                        }
                        _priceHistoryState.value = PriceHistoryState.Success(pricePoints, period)
                    }
                    is Result.Error -> _priceHistoryState.value = PriceHistoryState.Error(result.message)
                }
            }
        }
    }

    /**
     * Carrega pares de negociação da criptomoeda
     */
    fun loadTradingPairs(cryptoId: String) {
        viewModelScope.launch {
            repository.getTradingPairs(cryptoId).collect { result ->
                when (result) {
                    is Result.Loading -> _tradingPairsState.value = TradingPairsState.Loading
                    is Result.Success -> _tradingPairsState.value = TradingPairsState.Success(result.data)
                    is Result.Error -> _tradingPairsState.value = TradingPairsState.Error(result.message)
                }
            }
        }
    }

    /**
     * Altera o período do gráfico de preços
     */
    fun changeTimePeriod(period: TimePeriod) {
        currentCryptoId?.let { cryptoId ->
            loadPriceHistory(cryptoId, period)
        }
    }

    /**
     * Atualiza todos os dados da criptomoeda
     */
    fun refreshData() {
        currentCryptoId?.let { cryptoId ->
            loadCryptoDetails(cryptoId)
        }
    }
}
