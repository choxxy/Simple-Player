package com.example.simpleplayer.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simpleplayer.ui.SongFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SongFragmentViewModel::class)
    abstract fun bindDataViewModel(songFragmentViewModel: SongFragmentViewModel): ViewModel
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(PlayerViewModel::class)
//    abstract fun bindPlayerViewModel(playerViewModel: PlayerViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: AppVMFactory): ViewModelProvider.Factory
}