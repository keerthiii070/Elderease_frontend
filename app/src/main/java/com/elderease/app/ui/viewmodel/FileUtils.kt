package com.elderease.app.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object FileUtil {

    /**
     * Convert a content Uri (from gallery / camera) into a File
     * used for Multipart uploads.
     */
    fun from(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Unable to open URI: $uri")

        val file = File(
            context.cacheDir,
            "upload_${System.currentTimeMillis()}.jpg"
        )

        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }

        inputStream.close()
        return file
    }
}
