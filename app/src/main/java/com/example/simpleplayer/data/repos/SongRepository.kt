package com.example.simpleplayer.data.repos

import androidx.lifecycle.LiveData
import com.example.simpleplayer.data.Song

interface SongRepository {
    fun getSongsfromStorage(): LiveData<List<Song>>
}