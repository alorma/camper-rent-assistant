package com.alorma.camperchecks.di

import com.alorma.camperchecks.auth.GoogleSignInProvider
import com.alorma.camperchecks.auth.GoogleSignInProviderImpl
import com.alorma.camperchecks.auth.Session
import com.alorma.camperchecks.auth.SessionImpl
import com.alorma.camperchecks.auth.WebClientId
import com.alorma.camperchecks.screens.login.LoginViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val authScope = named("auth")

val authModule =
  module {
    single { Firebase.auth }

    single<CoroutineScope>(qualifier = authScope) {
      CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    single<WebClientId> {
      WebClientId("412331751935-hju27jtkcmf7gsivglgc1ni8t9g0tt93.apps.googleusercontent.com")
    }

    single<Session> {
      SessionImpl(
        firebaseAuth = get(),
        scope = get(qualifier = authScope),
      )
    }

    single<GoogleSignInProvider> {
      GoogleSignInProviderImpl(
        context = androidContext(),
        firebaseAuth = get(),
        webClientId = get<WebClientId>().value,
      )
    }

    viewModelOf(::LoginViewModel)
  }
