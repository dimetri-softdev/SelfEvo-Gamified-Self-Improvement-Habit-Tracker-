package com.example.selfevo.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileImageRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getProfileImageUrl(userId: String): String? {
        require(userId.isNotBlank()) { "A signed-in user is required to load a profile image." }

        return firestore.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .getString(PROFILE_IMAGE_URL_FIELD)
    }

    suspend fun uploadProfileImage(userId: String, imageUri: Uri): String {
        require(userId.isNotBlank()) { "A signed-in user is required to upload a profile image." }

        val contentType = context.contentResolver.getType(imageUri)
        require(contentType?.startsWith("image/") == true) {
            "Please choose a valid image file."
        }

        context.contentResolver.openFileDescriptor(imageUri, "r")?.use { descriptor ->
            require(descriptor.statSize <= MAX_IMAGE_SIZE_BYTES || descriptor.statSize < 0) {
                "Profile images must be 5 MB or smaller."
            }
        }

        val imageReference = storage.reference
            .child("$PROFILE_IMAGES_PATH/$userId/profile-image")
        val metadata = StorageMetadata.Builder()
            .setContentType(contentType)
            .build()

        imageReference.putFile(imageUri, metadata).await()
        val downloadUrl = imageReference.downloadUrl.await().toString()

        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .set(
                mapOf(
                    PROFILE_IMAGE_URL_FIELD to downloadUrl,
                    UPDATED_AT_FIELD to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            .await()

        return downloadUrl
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val PROFILE_IMAGE_URL_FIELD = "profileImageUrl"
        const val UPDATED_AT_FIELD = "updatedAt"
        const val PROFILE_IMAGES_PATH = "profile-images"
        const val MAX_IMAGE_SIZE_BYTES = 5L * 1024 * 1024
    }
}
