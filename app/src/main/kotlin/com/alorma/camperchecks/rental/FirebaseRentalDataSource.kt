package com.alorma.camperchecks.rental

import com.alorma.camperchecks.firestore.UserFirestoreProvider
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.LocalDateTime
import timber.log.Timber

class FirebaseRentalDataSource(
  private val firestoreProvider: UserFirestoreProvider,
) : RentalDataSource {
  override fun getRentals(): Flow<List<Rental>> =
    callbackFlow {
      val listener =
        firestoreProvider
          .collection("rentals")
          .addSnapshotListener { snapshot, error ->
            if (error != null) {
              Timber.e(error, "Error listening to rentals")
              trySend(emptyList())
              return@addSnapshotListener
            }
            trySend(snapshot?.documents?.mapNotNull { it.toRental() } ?: emptyList())
          }
      awaitClose { listener.remove() }
    }

  override fun getRentalById(rentalId: String): Flow<Result<Rental>> =
    callbackFlow {
      val listener =
        firestoreProvider
          .collection("rentals")
          .document(rentalId)
          .addSnapshotListener { snapshot, error ->
            if (error != null) {
              Timber.tag("Alorma").e(error, "Error listening to rental $rentalId")
              trySend(Result.failure(error))
              return@addSnapshotListener
            }
            val rental = snapshot?.toRental()
            if (rental != null) {
              trySend(Result.success(rental))
            } else {
              trySend(Result.failure(NoSuchElementException("Rental $rentalId not found")))
            }
          }
      awaitClose { listener.remove() }
    }

  override suspend fun saveRental(
    provider: RentalProvider,
    referenceId: String,
    vehicleId: String,
    startAt: LocalDateTime,
    endAt: LocalDateTime,
    renterName: String,
    renterPhone: String?,
    renterNotes: String?,
    notes: String?,
  ) {
    val existing =
      firestoreProvider
        .collection("rentals")
        .whereEqualTo("providerId", provider.id)
        .whereEqualTo("referenceId", referenceId)
        .get()
        .await()

    if (!existing.isEmpty) {
      error("Rental with provider '${provider.id}' and referenceId '$referenceId' already exists")
    }

    val doc = firestoreProvider.collection("rentals").document()
    doc
      .set(
        mapOf(
          "id" to doc.id,
          "providerId" to provider.id,
          "referenceId" to referenceId,
          "vehicleId" to vehicleId,
          "startAt" to startAt.toString(),
          "endAt" to endAt.toString(),
          "renterName" to renterName,
          "renterPhone" to renterPhone,
          "renterNotes" to renterNotes,
          "notes" to notes,
          "finished" to false,
        ),
      ).await()
  }

  private fun DocumentSnapshot.toRental(): Rental? {
    val id = getString("id") ?: return null
    val providerId = getString("providerId") ?: return null
    val referenceId = getString("referenceId") ?: return null
    val vehicleId = getString("vehicleId") ?: return null
    val startAtStr = getString("startAt") ?: return null
    val endAtStr = getString("endAt") ?: return null
    val renterName = getString("renterName") ?: return null
    val finished = getBoolean("finished") ?: false

    return Rental(
      id = id,
      provider = RentalProvider.fromId(providerId),
      referenceId = referenceId,
      vehicleId = vehicleId,
      startAt = LocalDateTime.parse(startAtStr),
      endAt = LocalDateTime.parse(endAtStr),
      renterName = renterName,
      renterPhone = getString("renterPhone"),
      renterNotes = getString("renterNotes"),
      notes = getString("notes"),
      finished = finished,
    )
  }
}
