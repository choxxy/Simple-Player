package com.example.simpleplayer.data.repos

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AlbumColumns.ALBUM_ART
import android.util.Size
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.simpleplayer.Utils
import com.example.simpleplayer.data.Song


class SongRepository constructor(private val context: Context) {

    var selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DATA
    )

    private fun getColumnIndex(cursor: Cursor, columnName: String): Int {
        return cursor.getColumnIndex(columnName)
    }

    fun getSongs(): LiveData<List<Song>> {

        val songsLiveData = MutableLiveData<List<Song>>()
        val songList = mutableListOf<Song>()

        //Get songs from provider
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Video.Media.DISPLAY_NAME} ASC" //Sort in alphabetical order based on display name.
        ).use { cursor ->
            if (cursor?.moveToFirst() == true) {
                do {
                    val id =
                        cursor.getLong(getColumnIndex(cursor, MediaStore.Audio.Media._ID))
                    val title =
                        cursor.getString(getColumnIndex(cursor, MediaStore.Audio.Media.TITLE))
                    val artist =
                        cursor.getString(getColumnIndex(cursor, MediaStore.Audio.Media.ARTIST))
                    val album =
                        cursor.getString(getColumnIndex(cursor, MediaStore.Audio.Media.ALBUM))
                    val albumId =
                        cursor.getLong(getColumnIndex(cursor, MediaStore.Audio.Media.ALBUM_ID))

                    val path = cursor.getString(getColumnIndex(cursor, MediaStore.Audio.Media.DATA))

                    val uri = ContentUris.appendId(
                        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI.buildUpon(),
                        albumId
                    ).build()


                    var albumArt : Bitmap?= null

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            try {
                                albumArt = context.contentResolver.loadThumbnail(uri, Size(512, 512), null)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            albumArt = Utils.getAlbumImage(context, path)
                        }

                    songList.add(
                        Song(
                            id = id,
                            title = title,
                            albumArt = albumArt,
                            album = album,
                            artist = artist,
                            duration = 0
                        )
                    )
                } while (cursor.moveToNext())
            }
        }

        return songsLiveData.apply {
            value = songList
        }

    }

    companion object {
        private var instance: SongRepository? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: SongRepository(context)
                    .also { instance = it }
            }
    }
}