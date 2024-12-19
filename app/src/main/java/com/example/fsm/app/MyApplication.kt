package com.example.fsm.app

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.fsm.core.extentions.ContextManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.binds
import org.koin.dsl.module

class MyApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

        ContextManager.init(this)

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(MainModule.instance)
        }
    }

    override fun newImageLoader(): ImageLoader {
        val imageLoader = ImageLoader.Builder(this)
            .crossfade(true)
            .allowHardware(false)
            .build()
        return imageLoader
    }
}