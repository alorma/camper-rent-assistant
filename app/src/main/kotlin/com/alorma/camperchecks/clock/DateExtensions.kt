package com.alorma.camperchecks.clock

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun Instant.date(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate = this.toLocalDateTime(timeZone).date
