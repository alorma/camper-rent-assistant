package com.alorma.camperchecks.rental

sealed class RentalProvider {
  abstract val id: String
  abstract val displayName: String

  data object Yescapa : RentalProvider() {
    override val id: String = "rent:provider:yescapa"
    override val displayName: String = "Yescapa"
  }

  data class Other(
    override val id: String,
    override val displayName: String,
  ) : RentalProvider()

  companion object {
    val all: List<RentalProvider> = listOf(Yescapa)

    fun fromId(id: String): RentalProvider =
      all.find { it.id == id } ?: Other(id = id, displayName = "Other")
  }
}
