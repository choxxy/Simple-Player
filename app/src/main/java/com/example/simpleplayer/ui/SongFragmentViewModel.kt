package com.example.simpleplayer.ui


import androidx.lifecycle.ViewModel
import com.example.simpleplayer.MusicManager
import com.example.simpleplayer.data.repos.SongRepository
import com.google.android.exoplayer2.ui.PlayerControlView
import javax.inject.Inject

class SongFragmentViewModel @Inject constructor(private val musicManager: MusicManager,
                                                private val songRepository: SongRepository) : ViewModel(){

    var playerControlView: PlayerControlView? =  null

    fun getSongs() =  songRepository.getSongsfromStorage()

    fun playSong(songId: Long){

    }

}