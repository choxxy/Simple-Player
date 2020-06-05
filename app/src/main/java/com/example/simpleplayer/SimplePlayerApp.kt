package com.example.simpleplayer

import android.app.Application
import com.example.simpleplayer.data.repos.songRepositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SimplePlayerApp: Application(){

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@SimplePlayerApp)
            modules(songRepositoryModule)

        }
    }
}