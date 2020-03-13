package com.example.simpleplayer.di

import android.content.Context
import com.example.simpleplayer.ui.MainActivity
import dagger.BindsInstance
import dagger.Component

@Component(modules = [ViewModelModule::class, VMFactoryModule::class, DataModule::class])
interface AppComponent {
    // Factory to create instances of the AppComponent
    @Component.Factory
    interface Factory {
        // With @BindsInstance, the Context passed in will be available in the graph
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(mainActivity: MainActivity)

    //fun inject(scheduleFragment: ScheduleFragment)

}