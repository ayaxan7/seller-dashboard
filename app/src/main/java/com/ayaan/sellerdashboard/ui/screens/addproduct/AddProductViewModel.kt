package com.ayaan.sellerdashboard.ui.screens.addproduct

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
class AddProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddProductUiState())
    val uiState: StateFlow<AddProductUiState> = _uiState.asStateFlow()

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
        _uiState.value = _uiState.value.copy(selectedImageUri = uri, error = null)
    }

    fun addProduct(onSuccess: () -> Unit) {
        if (!validateInput()) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // First upload image
            val imageUri = _uiState.value.selectedImageUri!!
            productRepository.uploadImage(imageUri)
                .onSuccess { imageUrl ->
                    // Then add product with image URL
                    val product = Product(
                        name = _uiState.value.name,
                        price = _uiState.value.price.toDouble(),
                        description = _uiState.value.description,
                        imageUrl = imageUrl
                    )

                    productRepository.addProduct(product)
                        .onSuccess {
                            _uiState.value = _uiState.value.copy(isLoading = false)
                            onSuccess()
                        }
                        .onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = exception.message ?: "Failed to add product"
                            )
                        }
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Failed to upload image"
                    )
                }
        }
    }

    private fun validateInput(): Boolean {
        val name = _uiState.value.name
        val price = _uiState.value.price
        val imageUri = _uiState.value.selectedImageUri


        return when {
            imageUri == null -> {
                _uiState.value = _uiState.value.copy(error = "Please select an image")
                false
            }
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

data class AddProductUiState(
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val selectedImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

