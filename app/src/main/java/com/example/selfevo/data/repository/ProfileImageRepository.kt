package com.example.selfevo.data.repository

import android.content.Context
import android.net.Uri
import com.example.selfevo.data.remote.SelfEvoApiService
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
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

class ProfileImageException(
    val reason: Reason,
    cause: Throwable? = null
) : Exception(cause) {
    enum class Reason {
        SIGN_IN_REQUIRED,
        UNSUPPORTED_IMAGE,
        IMAGE_TOO_LARGE,
        IMAGE_UNAVAILABLE,
        SERVICE_NOT_READY,
        UPLOAD_FAILED,
        PROFILE_ACCESS_DENIED,
        PROFILE_SAVE_DENIED,
        PROFILE_SAVE_FAILED,
        PROFILE_LOAD_FAILED
    }
}

class ProfileImageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: SelfEvoApiService
) {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getProfileImageUrl(userId: String): String? {
        if (userId.isBlank()) {
            throw ProfileImageException(ProfileImageException.Reason.SIGN_IN_REQUIRED)
        }

        val hasProfileImage = try {
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()
                .let { snapshot ->
                    snapshot.getString(PROFILE_IMAGE_PATH_FIELD)?.isNotBlank() == true
                }
        } catch (error: FirebaseFirestoreException) {
            val reason = if (error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                ProfileImageException.Reason.PROFILE_ACCESS_DENIED
            } else {
                ProfileImageException.Reason.PROFILE_LOAD_FAILED
            }
            throw ProfileImageException(reason, error)
        }
        if (!hasProfileImage) return null

        val response = apiService.getProfileImageUrl()
        if (!response.isSuccessful) {
            throw exceptionForServiceResponse(response.code(), isLoading = true)
        }
        return response.body()?.url
            ?: throw ProfileImageException(ProfileImageException.Reason.PROFILE_LOAD_FAILED)
    }

    suspend fun uploadProfileImage(userId: String, imageUri: Uri): String {
        if (userId.isBlank()) {
            throw ProfileImageException(ProfileImageException.Reason.SIGN_IN_REQUIRED)
        }

        val contentType = context.contentResolver.getType(imageUri)
            ?.substringBefore(';')
            ?.lowercase()
            ?: throw ProfileImageException(ProfileImageException.Reason.UNSUPPORTED_IMAGE)
        if (contentType !in SUPPORTED_CONTENT_TYPES) {
            throw ProfileImageException(ProfileImageException.Reason.UNSUPPORTED_IMAGE)
        }

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
            throw exceptionForServiceResponse(uploadResponse.code())
        }
        val imagePath = uploadResponse.body()?.path
            ?: throw ProfileImageException(ProfileImageException.Reason.UPLOAD_FAILED)
        try {
            // Store only the private object path in Firestore; image bytes stay in Supabase Storage.
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
        } catch (error: FirebaseFirestoreException) {
            val reason = if (error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                ProfileImageException.Reason.PROFILE_SAVE_DENIED
            } else {
                ProfileImageException.Reason.PROFILE_SAVE_FAILED
            }
            throw ProfileImageException(reason, error)
        }

        val signedUrlResponse = apiService.getProfileImageUrl()
        if (!signedUrlResponse.isSuccessful) {
            throw exceptionForServiceResponse(signedUrlResponse.code(), isLoading = true)
        }
        return signedUrlResponse.body()?.url
            ?: throw ProfileImageException(ProfileImageException.Reason.PROFILE_LOAD_FAILED)
    }

    private fun readImageBytes(imageUri: Uri): ByteArray {
        try {
            val input = context.contentResolver.openInputStream(imageUri)
                ?: throw ProfileImageException(ProfileImageException.Reason.IMAGE_UNAVAILABLE)
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
                    if (totalBytesRead > MAX_IMAGE_SIZE_BYTES) {
                        throw ProfileImageException(ProfileImageException.Reason.IMAGE_TOO_LARGE)
                    }
                    output.write(buffer, 0, bytesRead)
                }
                return output.toByteArray()
            }
        } catch (error: ProfileImageException) {
            throw error
        } catch (error: IOException) {
            throw ProfileImageException(ProfileImageException.Reason.IMAGE_UNAVAILABLE, error)
        }
    }

    private fun exceptionForServiceResponse(
        statusCode: Int,
        isLoading: Boolean = false
    ): ProfileImageException {
        val reason = when (statusCode) {
            401, 403 -> ProfileImageException.Reason.SIGN_IN_REQUIRED
            404, 503 -> ProfileImageException.Reason.SERVICE_NOT_READY
            413 -> ProfileImageException.Reason.IMAGE_TOO_LARGE
            415 -> ProfileImageException.Reason.UNSUPPORTED_IMAGE
            else -> if (isLoading) {
                ProfileImageException.Reason.PROFILE_LOAD_FAILED
            } else {
                ProfileImageException.Reason.UPLOAD_FAILED
            }
        }
        return ProfileImageException(reason)
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val PROFILE_IMAGE_PATH_FIELD = "profileImagePath"
        const val UPDATED_AT_FIELD = "updatedAt"
        const val MAX_IMAGE_SIZE_BYTES = 5L * 1024 * 1024
        val SUPPORTED_CONTENT_TYPES = setOf("image/jpeg", "image/png", "image/webp")
    }
}
