package com.alorma.camperchecks.auth

import kotlinx.coroutines.flow.StateFlow

interface Session {
  val state: StateFlow<SessionState>

  fun signOut()
}
