package com.ayaan.sellerdashboard.ui.screens.editproduct

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.sellerdashboard.data.model.Product
import com.ayaan.sellerdashboard.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProductUiState())
    val uiState: StateFlow<EditProductUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            productRepository.getProductById(productId)
                .onSuccess { product ->
                    _uiState.value = _uiState.value.copy(
                        productId = product.id,
                        name = product.name,
                        price = product.price.toString(),
                        description = product.description,
                        currentImageUrl = product.imageUrl,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to load product"
                    )
                }
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name, error = null)
    }

    fun onPriceChange(price: String) {
        _uiState.value = _uiState.value.copy(price = price, error = null)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description, error = null)
    }

    fun onImageSelected(uri: Uri) {
        _uiState.value = _uiState.value.copy(newImageUri = uri, error = null)
    }

    fun updateProduct(onSuccess: () -> Unit) {
        if (!validateInput()) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // If new image selected, upload it first
            val imageUrl = if (_uiState.value.newImageUri != null) {
                val uploadResult = productRepository.uploadImage(_uiState.value.newImageUri!!)
                if (uploadResult.isSuccess) {
                    uploadResult.getOrNull()!!
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to upload image"
                    )
                    return@launch
                }
            } else {
                _uiState.value.currentImageUrl
            }

            // Update product
            val product = Product(
                id = _uiState.value.productId,
                name = _uiState.value.name,
                price = _uiState.value.price.toDouble(),
                description = _uiState.value.description,
                imageUrl = imageUrl
            )

            productRepository.updateProduct(product)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to update product"
                    )
                }
        }
    }

    private fun validateInput(): Boolean {
        val name = _uiState.value.name
        val price = _uiState.value.price

        return when {
            name.isBlank() -> {
                _uiState.value = _uiState.value.copy(error = "Product name cannot be empty")
                false
            }
            price.isBlank() -> {
                _uiState.value = _uiState.value.copy(error = "Price cannot be empty")
                false
            }
            price.toDoubleOrNull() == null || price.toDouble() <= 0 -> {
                _uiState.value = _uiState.value.copy(error = "Please enter a valid price")
                false
            }
            else -> true
        }
    }
}

data class EditProductUiState(
    val productId: String = "",
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val currentImageUrl: String = "",
    val newImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

