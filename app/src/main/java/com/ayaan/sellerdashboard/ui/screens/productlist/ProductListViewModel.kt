package com.ayaan.sellerdashboard.ui.screens.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.sellerdashboard.data.model.Product
import com.ayaan.sellerdashboard.data.repository.AuthRepository
import com.ayaan.sellerdashboard.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            productRepository.getVendorProducts().collect { products ->
                _uiState.value = _uiState.value.copy(
                    products = products,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeletingProductId = product.id)

            productRepository.deleteProduct(product.id, product.imageUrl)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isDeletingProductId = null,
                        successMessage = "Product deleted successfully"
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isDeletingProductId = null,
                        error = exception.message ?: "Failed to delete product"
                    )
                }
        }
    }

    fun logout(onLogout: () -> Unit) {
        authRepository.signOut()
        onLogout()
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
}

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isDeletingProductId: String? = null,
    val error: String? = null,
    val successMessage: String? = null
)

