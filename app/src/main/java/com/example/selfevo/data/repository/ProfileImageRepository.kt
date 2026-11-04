package com.example.selfevo.data.repository

import android.content.Context
import android.net.Uri
import com.example.selfevo.data.remote.SelfEvoApiService
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject

class ProfileImageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: SelfEvoApiService
) {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getProfileImageUrl(userId: String): String? {
        require(userId.isNotBlank()) { "A signed-in user is required to load a profile image." }

        val imagePath = firestore.collection(USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .getString(PROFILE_IMAGE_PATH_FIELD)
            ?: return null

        val response = apiService.getProfileImageUrl()
        if (!response.isSuccessful) {
            throw IOException("Profile image URL request failed (HTTP ${response.code()}).")
        }
        return response.body()?.url
            ?: throw IOException("Profile image service returned no signed URL for $imagePath.")
    }

    suspend fun uploadProfileImage(userId: String, imageUri: Uri): String {
        require(userId.isNotBlank()) { "A signed-in user is required to upload a profile image." }

        val contentType = context.contentResolver.getType(imageUri)
            ?.substringBefore(';')
            ?.lowercase()
            ?: throw IllegalArgumentException("Choose a JPEG, PNG, or WebP image.")
        require(contentType in SUPPORTED_CONTENT_TYPES) {
            "Choose a JPEG, PNG, or WebP image."
        }

        context.contentResolver.openFileDescriptor(imageUri, "r")?.use { descriptor ->
            require(descriptor.statSize <= MAX_IMAGE_SIZE_BYTES || descriptor.statSize < 0) {
                "Profile images must be 5 MB or smaller."
            }
        } ?: throw IOException("The selected image could not be opened.")

        val imageBytes = readImageBytes(imageUri)
        val requestBody = object : RequestBody() {
            override fun contentType(): MediaType = contentType.toMediaType()

            override fun contentLength(): Long = imageBytes.size.toLong()

            override fun writeTo(sink: BufferedSink) {
                sink.write(imageBytes)
            }
        }

        val uploadResponse = apiService.uploadProfileImage(requestBody)
        if (!uploadResponse.isSuccessful) {
            throw IOException("Profile image upload failed (HTTP ${uploadResponse.code()}).")
        }
        val imagePath = uploadResponse.body()?.path
            ?: throw IOException("Profile image service returned no stored image path.")
        firestore.collection(USERS_COLLECTION)
            .document(userId)
            .set(
                mapOf(
                    PROFILE_IMAGE_PATH_FIELD to imagePath,
                    UPDATED_AT_FIELD to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            .await()

        val signedUrlResponse = apiService.getProfileImageUrl()
        if (!signedUrlResponse.isSuccessful) {
            throw IOException("Profile image URL request failed (HTTP ${signedUrlResponse.code()}).")
        }
        return signedUrlResponse.body()?.url
            ?: throw IOException("Profile image service returned no signed URL.")
    }

    private fun readImageBytes(imageUri: Uri): ByteArray {
        val input = context.contentResolver.openInputStream(imageUri)
            ?: throw IOException("The selected image could not be opened.")
        input.use { stream ->
            val output = ByteArrayOutputStream()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var totalBytesRead = 0L
            while (true) {
                val remainingWithOverflowCheck =
                    (MAX_IMAGE_SIZE_BYTES - totalBytesRead + 1).coerceAtMost(buffer.size.toLong()).toInt()
                val bytesRead = stream.read(buffer, 0, remainingWithOverflowCheck)
                if (bytesRead == -1) break
                totalBytesRead += bytesRead
                require(totalBytesRead <= MAX_IMAGE_SIZE_BYTES) {
                    "Profile images must be 5 MB or smaller."
                }
                output.write(buffer, 0, bytesRead)
            }
            return output.toByteArray()
        }
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val PROFILE_IMAGE_PATH_FIELD = "profileImagePath"
        const val UPDATED_AT_FIELD = "updatedAt"
        const val MAX_IMAGE_SIZE_BYTES = 5L * 1024 * 1024
        val SUPPORTED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/webp")
    }
}
