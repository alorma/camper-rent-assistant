enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "CamperChecks"

pluginManagement {
  includeBuild("build-logic")
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
  }
}

include(":app")
include(":icons")