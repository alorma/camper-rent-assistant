package com.alorma.camperchecks

import android.app.Application
import com.alorma.camperchecks.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AppApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    startKoin {
      androidLogger(Level.ERROR) // Only log errors in production
      androidContext(this@AppApplication)
      workManagerFactory() // Enable Koin WorkManager integration
      modules(appModule)
    }
  }
}
