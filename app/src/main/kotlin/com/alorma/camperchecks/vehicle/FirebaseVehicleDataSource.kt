package com.alorma.camperchecks.vehicle

import com.alorma.camperchecks.auth.Session
import com.alorma.camperchecks.auth.SessionState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseVehicleDataSource(
  private val firestore: FirebaseFirestore,
  private val session: Session,
) : VehicleDataSource {

  override fun getVehicle(): Flow<Vehicle?> =
    session.state.flatMapLatest { state ->
      when (state) {
        is SessionState.Authenticated -> vehicleFlow(state.user.uid)
        else -> flowOf(null)
      }
    }

  override suspend fun saveVehicle(
    name: String,
    plate: String,
  ) {
    val uid = session.currentUid() ?: error("Cannot save vehicle: user not authenticated")
    val doc = vehiclesCollection(uid).document()
    doc.set(
      mapOf(
        "id" to doc.id,
        "name" to name,
        "plate" to plate,
      ),
    ).await()
  }

  private fun vehicleFlow(uid: String): Flow<Vehicle?> =
    callbackFlow {
      val listener =
        vehiclesCollection(uid)
          .limit(1)
          .addSnapshotListener { snapshot, error ->
            if (error != null) {
              Timber.e(error, "Error listening to vehicles")
              trySend(null)
              return@addSnapshotListener
            }
            val vehicle = snapshot?.documents?.firstOrNull()?.toVehicle()
            trySend(vehicle)
          }
      awaitClose { listener.remove() }
    }

  private fun vehiclesCollection(uid: String) =
    firestore.collection("users").document(uid).collection("vehicles")

  private fun com.google.firebase.firestore.DocumentSnapshot.toVehicle(): Vehicle? {
    val id = getString("id") ?: return null
    val name = getString("name") ?: return null
    val plate = getString("plate") ?: return null
    return Vehicle(id = id, name = name, plate = plate)
  }
}

private fun Session.currentUid(): String? =
  (state.value as? SessionState.Authenticated)?.user?.uid
