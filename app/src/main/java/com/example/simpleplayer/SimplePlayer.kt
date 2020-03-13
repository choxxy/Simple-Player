package com.example.simpleplayer

import android.app.Application
import android.content.Context
import com.example.simpleplayer.di.AppComponent
import com.example.simpleplayer.di.DaggerAppComponent


class SimplePlayer: Application(){

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}

val Context.appComponent: AppComponent
    get() = (this.applicationContext as SimplePlayer).appComponent