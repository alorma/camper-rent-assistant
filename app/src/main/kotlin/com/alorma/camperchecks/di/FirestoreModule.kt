package com.alorma.camperchecks.di

import com.alorma.camperchecks.firestore.UserFirestoreProvider
import com.alorma.camperchecks.firestore.UserFirestoreProviderImpl
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.koin.dsl.module

val firestoreModule =
  module {
    single { Firebase.firestore }

    single<UserFirestoreProvider> {
      UserFirestoreProviderImpl(
        firestore = get(),
        session = get(),
      )
    }
  }
