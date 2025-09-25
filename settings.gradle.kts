pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
		maven("https://jitpack.io")  // 👈 JitPack va afuera, no dentro de google()
	}
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
		maven("https://jitpack.io")
	}
}

rootProject.name = "MonitorWidget"
include(":app")
