package com.example.simpleplayer.di

import com.example.simpleplayer.data.repos.SongRepository
import com.example.simpleplayer.data.repos.SongRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class DataModule {
    @Binds
    abstract fun providesSongRepository(songRepository: SongRepositoryImpl): SongRepository

}