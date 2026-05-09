package com.danish.noorservice.data.repository

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class ImageRepository @Inject constructor(
    private val context: Context
) {
    suspend fun uploadProfileImage(uri: Uri, userId: String): String =
        uploadToCloudinary(uri, tag = "profile_$userId")

    suspend fun uploadVendorLogo(uri: Uri, userId: String): String =
        uploadToCloudinary(uri, tag = "logo_$userId")

    private suspend fun uploadToCloudinary(
        uri: Uri,
        tag: String
    ): String = suspendCancellableCoroutine { continuation ->

        val requestId = MediaManager.get()
            .upload(uri)
            .option("upload_preset", "ml_default")
            .option("tags", tag)           // tag instead of public_id/folder
            .unsigned("ml_default")        // explicitly mark as unsigned
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as? String
                    if (url != null) {
                        continuation.resume(url)
                    } else {
                        continuation.resumeWithException(
                            Exception("Upload succeeded but no URL returned")
                        )
                    }
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    continuation.resumeWithException(
                        Exception("Cloudinary upload failed: ${error.description}")
                    )
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    continuation.resumeWithException(
                        Exception("Upload rescheduled: ${error.description}")
                    )
                }
            })
            .dispatch()

        continuation.invokeOnCancellation {
            MediaManager.get().cancelRequest(requestId)
        }
    }
}