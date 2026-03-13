plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kapt)
    kotlin("plugin.serialization") version "1.9.10"
}

android {
    namespace = "org.strawberryfoundations.wear.reply"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "org.strawberryfoundations.reply"
        minSdk = 33
        targetSdk = 36
        versionCode = 7
        versionName = "2.0.3"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    useLibrary("wear-sdk")
    buildFeatures {
        compose = true
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    // General compose dependencies
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.activity)
    implementation(libs.splashscreen)

    // M3 & Play Services
    implementation(libs.wear.compose.material)
    implementation(libs.wear.compose.material3)
    implementation(libs.wear.compose.foundation)
    implementation(libs.wear.compose.navigation)
    implementation(libs.wear.input)
    implementation(libs.wear.ongoing)
    implementation(libs.wear.gms.playservices)
    implementation(libs.compose.material3)

    // Material icons
    implementation(libs.material.icons.core)
    implementation(libs.material.icons.extended)

    // Data store
    implementation(libs.datastore.core)
    implementation(libs.datastore.preferences)

    // Room database
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)

    // Horologist
    implementation(libs.horologist.compose.layout)
    implementation(libs.horologist.compose.material)
    implementation(libs.horologist.tiles)

    // JSON
    implementation(libs.kotlinx.serialization.json)

    // Tiles
    implementation(libs.tiles)
    implementation(libs.tiles.material)
    implementation(libs.tiles.tooling.preview)

    // Protolayout
    implementation(libs.protolayout.material)
    implementation(libs.protolayout.expression)

    // Preview Tooling
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.wear.compose.ui.tooling)
    implementation(libs.compose.ui.test.manifest)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Testing
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.rule)
    testImplementation(libs.horologist.roboscreenshots)

    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(platform(libs.compose.bom))

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    debugImplementation(platform(libs.compose.bom))
    debugImplementation(libs.tiles.tooling)

    ksp(libs.room.compiler)
}