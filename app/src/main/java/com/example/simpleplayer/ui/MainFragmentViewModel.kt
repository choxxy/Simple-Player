package com.example.simpleplayer.ui

import androidx.lifecycle.ViewModel
import com.example.simpleplayer.data.repos.SongRepository
import javax.inject.Inject

class MainFragmentViewModel @Inject constructor(private val songRepository: SongRepository) : ViewModel(){

    fun getSongs() =  songRepository.getSongsfromStorage()
}