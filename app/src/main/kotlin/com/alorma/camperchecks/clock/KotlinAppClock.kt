package com.alorma.camperchecks.clock

import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

class KotlinAppClock(
  private val clock: Clock = Clock.System,
) : AppClock {
  override fun now(): Instant = clock.now()

  override fun nowDate(timeZone: TimeZone): LocalDate = now().date(timeZone)
}
