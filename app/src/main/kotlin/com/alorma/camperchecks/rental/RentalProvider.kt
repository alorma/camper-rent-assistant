package com.alorma.camperchecks.rental

sealed class RentalProvider(
  val id: String,
  val displayName: String,
) {
  data object Yescapa : RentalProvider(
    id = "rent:provider:yescapa",
    displayName = "Yescapa",
  )

  data class Other(
    val providerId: String,
    val providerDisplayName: String,
  ) : RentalProvider(id = providerId, displayName = providerDisplayName)

  companion object {
    val all: List<RentalProvider> = listOf(Yescapa)

    fun fromId(id: String): RentalProvider =
      all.find { it.id == id } ?: Other(providerId = id, providerDisplayName = "Other")
  }
}
