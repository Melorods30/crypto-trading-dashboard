package com.cryptotrading.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptotrading.dashboard.data.model.Crypto
import com.cryptotrading.dashboard.data.repository.CryptoRepository
import com.cryptotrading.dashboard.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela principal do Dashboard
 * Gerencia o estado da lista de criptomoedas e filtros de ordenação
 */
class DashboardViewModel(
    private val repository: CryptoRepository = CryptoRepository()
) : ViewModel() {

    /**
     * Estados possíveis da UI
     */
    sealed class UiState {
        object Loading : UiState()
        data class Success(val cryptos: List<Crypto>) : UiState()
        data class Error(val message: String) : UiState()
    }

    /**
     * Tipos de ordenação disponíveis
     */
    enum class SortType(val apiOrder: String, val displayName: String) {
        MARKET_CAP_DESC("market_cap_desc", "Cap. Mercado"),
        VOLUME_DESC("volume_desc", "Volume 24h"),
        PRICE_DESC("price_desc", "Maior Preço"),
        PRICE_ASC("price_asc", "Menor Preço"),
        PERCENT_CHANGE_DESC("percent_change_desc", "Maiores Ganhos"),
        PERCENT_CHANGE_ASC("percent_change_asc", "Maiores Perdas")
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _currentSortType = MutableStateFlow(SortType.MARKET_CAP_DESC)
    val currentSortType: StateFlow<SortType> = _currentSortType.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadCryptos()
    }

    /**
     * Carrega a lista de criptomoedas
     */
    fun loadCryptos() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.getTopCryptos(
                orderBy = _currentSortType.value.apiOrder,
                limit = 20
            ).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Manter estado de loading
                    }
                    is Result.Success -> {
                        _uiState.value = UiState.Success(result.data)
                        _isRefreshing.value = false
                    }
                    is Result.Error -> {
                        _uiState.value = UiState.Error(result.message)
                        _isRefreshing.value = false
                    }
                }
            }
        }
    }

    /**
     * Altera o tipo de ordenação e recarrega os dados
     */
    fun changeSortType(sortType: SortType) {
        if (_currentSortType.value != sortType) {
            _currentSortType.value = sortType
            loadCryptos()
        }
    }

    /**
     * Atualiza os dados (pull to refresh)
     */
    fun refresh() {
        _isRefreshing.value = true
        loadCryptos()
    }

    /**
     * Retorna o tipo de ordenação baseado na aba selecionada
     */
    fun getSortTypeForTab(tabIndex: Int): SortType {
        return when (tabIndex) {
            0 -> SortType.VOLUME_DESC // Top 20 por Volume
            1 -> SortType.PERCENT_CHANGE_DESC // Maiores Ganhos
            2 -> SortType.PERCENT_CHANGE_ASC // Maiores Perdas
            else -> SortType.MARKET_CAP_DESC
        }
    }
}
