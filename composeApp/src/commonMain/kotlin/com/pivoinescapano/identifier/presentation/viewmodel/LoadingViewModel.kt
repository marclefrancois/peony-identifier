package com.pivoinescapano.identifier.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pivoinescapano.identifier.data.cache.DataCacheManager
import com.pivoinescapano.identifier.data.model.LoadingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoadingViewModel(
    private val dataCacheManager: DataCacheManager,
) : ViewModel() {
    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            val success = dataCacheManager.preloadAllData()
            _loadingState.value =
                if (success) {
                    LoadingState.Success
                } else {
                    LoadingState.Error("Impossible de charger les données. Veuillez vérifier votre connexion.")
                }
        }
    }

    fun retry() {
        loadData()
    }
}
