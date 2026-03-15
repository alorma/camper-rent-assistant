package com.alorma.camperchecks.firestore

import com.google.firebase.firestore.CollectionReference

interface UserFirestoreProvider {
  /**
   * Returns the Firestore collection at /users/{uid}/{collection}.
   * Throws if called when no user is authenticated.
   */
  fun collection(name: String): CollectionReference

  /**
   * Returns the Firestore collection at /users/{uid}/rentals/{rentalId}/{collection}.
   * Throws if called when no user is authenticated.
   */
  fun rentalCollection(rentalId: String, name: String): CollectionReference
}
