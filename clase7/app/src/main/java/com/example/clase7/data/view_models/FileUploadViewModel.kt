package com.example.clase7.data.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

data class UploadedFile(
    val id: String,
    val name: String,
    val base64Data: String,
    val size: Long,
    val mimeType: String,
    val uploadDate: Date
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "base64Data" to base64Data,
            "size" to size,
            "mimeType" to mimeType,
            "uploadDate" to uploadDate
        )
    }

    // Función para obtener el tipo de archivo (para mostrar iconos)
    fun getFileType(): String {
        return when {
            mimeType.startsWith("image/") -> "image"
            mimeType.startsWith("video/") -> "video"
            mimeType.startsWith("audio/") -> "audio"
            mimeType == "application/pdf" -> "pdf"
            mimeType.startsWith("text/") -> "text"
            else -> "file"
        }
    }

    // Función para obtener tamaño formateado
    fun getFormattedSize(): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${String.format("%.1f", size / 1024.0)} KB"
            else -> "${String.format("%.1f", size / (1024.0 * 1024.0))} MB"
        }
    }
}

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    data class Success(val file: UploadedFile) : UploadState()
    data class Error(val message: String) : UploadState()
}

class FileUploadViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    private val _uploadedFiles = MutableStateFlow<List<UploadedFile>>(emptyList())
    val uploadedFiles: StateFlow<List<UploadedFile>> = _uploadedFiles.asStateFlow()

    fun uploadFile(context: Context, fileUri: Uri, fileName: String? = null) {
        viewModelScope.launch {
            try {
                _uploadState.value = UploadState.Loading


                // Convertir archivo a Base64
                val fileData = convertUriToBase64(context, fileUri)
                val fileSize = getFileSize(context, fileUri)
                val mimeType = getMimeType(context, fileUri)

                // Crear objeto de archivo
                val uploadedFile = UploadedFile(
                    id = UUID.randomUUID().toString(),
                    name = fileName ?: generateFileName(),
                    base64Data = fileData,
                    size = fileSize,
                    mimeType = mimeType ?: "application/octet-stream",
                    uploadDate = Date()
                )

                // Subir a Firestore
                db.collection("files")
                    .document(uploadedFile.id)
                    .set(uploadedFile.toMap())
                    .await()

                // Actualizar lista local
                _uploadedFiles.value = _uploadedFiles.value + uploadedFile
                _uploadState.value = UploadState.Success(uploadedFile)

            } catch (e: Exception) {
                _uploadState.value = UploadState.Error(e.message ?: "Error desconocido al subir el archivo")
            }
        }
    }

    fun getFilesFromFirestore() {
        viewModelScope.launch {
            try {
                _uploadState.value = UploadState.Loading

                val result = db.collection("uploadedFiles")
                    .orderBy("uploadDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()

                val files = result.documents.map { document ->
                    UploadedFile(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        base64Data = document.getString("base64Data") ?: "",
                        size = document.getLong("size") ?: 0,
                        mimeType = document.getString("mimeType") ?: "",
                        uploadDate = document.getDate("uploadDate") ?: Date()
                    )
                }

                _uploadedFiles.value = files
                _uploadState.value = UploadState.Idle

            } catch (e: Exception) {
                _uploadState.value = UploadState.Error("Error al cargar archivos: ${e.message}")
            }
        }
    }

    fun deleteFile(fileId: String) {
        viewModelScope.launch {
            try {
                db.collection("uploadedFiles")
                    .document(fileId)
                    .delete()
                    .await()

                // Actualizar lista local
                _uploadedFiles.value = _uploadedFiles.value.filter { it.id != fileId }

            } catch (e: Exception) {
                _uploadState.value = UploadState.Error("Error al eliminar archivo: ${e.message}")
            }
        }
    }

    private fun getFileSize(context: Context, uri: Uri): Long {
        return try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { parcelFileDescriptor ->
                parcelFileDescriptor.statSize
            } ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun getMimeType(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)
    }

    private fun generateFileName(): String {
        return "file_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}"
    }

    fun resetState() {
        _uploadState.value = UploadState.Idle
    }
    // Comprime una imagen desde un Uri y devuelve los bytes resultantes.
    // - maxWidth / maxHeight: tamaño máximo deseado (mantiene aspect ratio).
    // - quality: 0..100 para la compresión JPEG.
    // - format: cambiar a Bitmap.CompressFormat.PNG si se necesita transparencia.
    private suspend fun compressImage(
        context: Context,
        uri: Uri,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024,
        quality: Int = 80,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): ByteArray {
        val resolver = context.contentResolver

        // 1) Leer dimensiones sin cargar el bitmap completo
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, boundsOptions) }
        val (origWidth, origHeight) = boundsOptions.outWidth to boundsOptions.outHeight

        // 2) Calcular inSampleSize (potencias de 2)
        fun calculateInSampleSize(reqW: Int, reqH: Int): Int {
            var inSampleSize = 1
            if (origHeight > reqH || origWidth > reqW) {
                var halfH = origHeight / 2
                var halfW = origWidth / 2
                while (halfH / inSampleSize >= reqH && halfW / inSampleSize >= reqW) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }

        val inSampleSize = calculateInSampleSize(maxWidth, maxHeight)

        // 3) Decodificar con sample para evitar OOM
        val decodeOptions = BitmapFactory.Options().apply {
            this.inSampleSize = inSampleSize
            inPreferredConfig = Bitmap.Config.RGB_565 // menos memoria que ARGB_8888
        }

        val bitmap = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, decodeOptions) }
            ?: throw Exception("No se pudo decodificar la imagen")

        // 4) Escalar si aún es mayor que el máximo
        val (bw, bh) = bitmap.width to bitmap.height
        val scale = minOf(maxWidth.toFloat() / bw, maxHeight.toFloat() / bh).coerceAtMost(1.0f)
        val finalBitmap = if (scale < 1.0f) {
            val newW = (bw * scale).toInt()
            val newH = (bh * scale).toInt()
            Bitmap.createScaledBitmap(bitmap, newW, newH, true)
        } else {
            bitmap
        }

        // 5) Comprimir a bytes
        val baos = ByteArrayOutputStream()
        finalBitmap.compress(format, quality, baos)
        val bytes = baos.toByteArray()

        // Liberar bitmaps si fue creado uno nuevo
        if (finalBitmap !== bitmap) bitmap.recycle()
        finalBitmap.recycle()

        return bytes
    }

    // Reemplazo sugerido de convertUriToBase64 para usar compresión en imágenes.
    private suspend fun convertUriToBase64(context: Context, uri: Uri): String {
        val mime = getMimeType(context, uri)
        return if (mime?.startsWith("image/") == true) {
            val compressed = compressImage(context, uri, maxWidth = 1024, maxHeight = 1024, quality = 75)
            android.util.Base64.encodeToString(compressed, android.util.Base64.DEFAULT)
        } else {
            // lectura directa para otros tipos
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }?.let {
                android.util.Base64.encodeToString(it, android.util.Base64.DEFAULT)
            } ?: throw Exception("No se pudo leer el archivo")
        }
    }



}
