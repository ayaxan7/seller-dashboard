package com.ayaan.sellerdashboard.data.repository

import android.net.Uri
import com.ayaan.sellerdashboard.data.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {
    private val productsCollection = firestore.collection("products")
    private val storageRef = storage.reference.child("product_images")

    suspend fun uploadImage(imageUri: Uri): Result<String> {
        return try {
            val fileName = "${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child(fileName)
            imageRef.putFile(imageUri).await()
            val downloadUrl = imageRef.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addProduct(product: Product): Result<String> {
        return try {
            val vendorId = auth.currentUser?.uid ?: throw Exception("User not logged in")
            val email=auth.currentUser?.email.toString()
            val productWithVendor = product.copy(vendorId = vendorId, email = email)
            val documentRef = productsCollection.add(productWithVendor).await()
            // Set the document ID in the product object
            // Update with the document ID
            productsCollection.document(documentRef.id)
                .update("id", documentRef.id).await()

            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            productsCollection.document(product.id)
                .set(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String, imageUrl: String): Result<Unit> {
        return try {
            // Delete from Firestore
            productsCollection.document(productId).delete().await()

            // Delete image from Storage if exists
            if (imageUrl.isNotEmpty()) {
                try {
                    val imageRef = storage.getReferenceFromUrl(imageUrl)
                    imageRef.delete().await()
                } catch (e: Exception) {
                    // Image deletion failed, but continue
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getVendorProducts(): Flow<List<Product>> = callbackFlow {
        val vendorId = auth.currentUser?.uid ?: ""

        val listener = productsCollection
            .whereEqualTo("vendorId", vendorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val products = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Product::class.java)
                }?.sortedByDescending { it.createdAt } ?: emptyList()

                trySend(products)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getProductById(productId: String): Result<Product> {
        return try {
            val document = productsCollection.document(productId).get().await()
            val product = document.toObject(Product::class.java)
            if (product != null) {
                Result.success(product)
            } else {
                Result.failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

