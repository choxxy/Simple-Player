package com.example.simpleplayer

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.core.database.getIntOrNull
import java.io.File

/**
 * Created by  choxxy on 04 Oct, 2020
 */


object Utils {

    fun getAlbumImage(context: Context, path: String?): Bitmap? {
        if (path.isNullOrEmpty())
            return null
        if (File(path).exists()) {
            try {
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(path)
                val rawArt = mediaMetadataRetriever.embeddedPicture
                if (rawArt != null) {
                    val options = BitmapFactory.Options()
                    val bitmap = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.size, options)
                    if (bitmap != null) {
                        return bitmap
                    }
                }
                val songParentDirectory = File(path).parent.trimEnd('/')
                val albumArtFiles = arrayListOf("folder.jpg", "albumart.jpg", "cover.jpg")
                albumArtFiles.forEach {
                    val albumArtFilePath = "$songParentDirectory/$it"
                    if (File(albumArtFilePath).exists()) {
                        val bitmap = BitmapFactory.decodeFile(albumArtFilePath)
                        if (bitmap != null) {
                            return bitmap
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && path.startsWith("content://")) {
            try {
                val size = Size(512, 512)
                return context.contentResolver.loadThumbnail(Uri.parse(path), size, null)
            } catch (ignored: Exception) {
            }
        }
        return null
    }
}