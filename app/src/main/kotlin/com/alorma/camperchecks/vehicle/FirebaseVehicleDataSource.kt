package com.alorma.camperchecks.vehicle

import com.alorma.camperchecks.auth.Session
import com.alorma.camperchecks.auth.SessionState
import com.alorma.camperchecks.firestore.UserFirestoreProvider
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseVehicleDataSource(
  private val firestoreProvider: UserFirestoreProvider,
  private val session: Session,
) : VehicleDataSource {

  override fun getVehicle(): Flow<Vehicle?> =
    session.state.flatMapLatest { state ->
      when (state) {
        is SessionState.Authenticated -> vehicleFlow()
        else -> flowOf(null)
      }
    }

  override fun getVehicleState(): Flow<VehicleState> =
    session.state.flatMapLatest { state ->
      when (state) {
        is SessionState.Authenticated -> vehicleStateFlow()
        SessionState.Loading -> flowOf(VehicleState.Loading)
        SessionState.Unauthenticated -> flowOf(VehicleState.NotFound)
      }
    }

  private fun vehicleStateFlow(): Flow<VehicleState> =
    flow {
      emit(VehicleState.Loading)
      vehicleFlow().collect { vehicle ->
        emit(if (vehicle != null) VehicleState.Found(vehicle) else VehicleState.NotFound)
      }
    }

  override suspend fun saveVehicle(
    name: String,
    plate: String,
  ) {
    val doc = firestoreProvider.collection("vehicles").document()
    doc.set(
      mapOf(
        "id" to doc.id,
        "name" to name,
        "plate" to plate,
      ),
    ).await()
  }

  private fun vehicleFlow(): Flow<Vehicle?> =
    callbackFlow {
      val listener =
        firestoreProvider.collection("vehicles")
          .limit(1)
          .addSnapshotListener { snapshot, error ->
            if (error != null) {
              Timber.e(error, "Error listening to vehicles")
              trySend(null)
              return@addSnapshotListener
            }
            trySend(snapshot?.documents?.firstOrNull()?.toVehicle())
          }
      awaitClose { listener.remove() }
    }

  private fun DocumentSnapshot.toVehicle(): Vehicle? {
    val id = getString("id") ?: return null
    val name = getString("name") ?: return null
    val plate = getString("plate") ?: return null
    return Vehicle(id = id, name = name, plate = plate)
  }
}
