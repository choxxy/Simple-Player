package com.example.simpleplayer.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
abstract class VMFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: AppVMFactory): ViewModelProvider.Factory
}