plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val releaseKeystorePath = providers.environmentVariable("APOLO_RELEASE_KEYSTORE_FILE").orNull
val releaseStorePassword = providers.environmentVariable("APOLO_RELEASE_STORE_PASSWORD").orNull
val releaseKeyAlias = providers.environmentVariable("APOLO_RELEASE_KEY_ALIAS").orNull
val releaseKeyPassword = providers.environmentVariable("APOLO_RELEASE_KEY_PASSWORD").orNull
val hasReleaseSigning = listOf(
    releaseKeystorePath,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword
).all { !it.isNullOrBlank() }

android {
    namespace = "com.apolo.academy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.apolo.academy"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.1.0"
    }

    if (hasReleaseSigning) {
        signingConfigs {
            create("release") {
                storeFile = file(requireNotNull(releaseKeystorePath))
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

// Never allow a release artifact to be produced without the permanent release key.
// Debug builds remain available without signing secrets.
tasks.configureEach {
    if (name == "assembleRelease" || name == "bundleRelease") {
        doFirst {
            check(hasReleaseSigning) {
                "Release signing is mandatory. Configure APOLO_RELEASE_KEYSTORE_FILE, " +
                    "APOLO_RELEASE_STORE_PASSWORD, APOLO_RELEASE_KEY_ALIAS and " +
                    "APOLO_RELEASE_KEY_PASSWORD. Do not use a debug key for releases."
            }
            check(file(requireNotNull(releaseKeystorePath)).isFile) {
                "Release keystore file does not exist: $releaseKeystorePath"
            }
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
