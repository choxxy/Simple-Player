package com.example.simpleplayer.data

import android.graphics.Bitmap

data class Song(
    val id: Long,
    val title: String,
    val albumArt: Bitmap?,
    val album: String,
    val artist: String,
    val duration: Long
)