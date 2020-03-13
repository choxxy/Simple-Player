package com.example.simpleplayer.data

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artUri: Uri,
    val album: String,
    val artist: String
)