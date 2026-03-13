package com.alorma.camperchecks.auth

/**
 * Wrapper for the Firebase/Google Web Client ID (OAuth 2.0 client ID of type "Web application").
 * Provided via Koin so that the actual string is never hardcoded in implementation classes.
 */
@JvmInline
value class WebClientId(
  val value: String,
)
