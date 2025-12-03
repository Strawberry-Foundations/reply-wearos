plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization") version "1.9.10"
}

android {
    namespace = "org.strawberryfoundations.wear.replicity"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.strawberryfoundations.replicity"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.2.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)

    // General compose dependencies
    implementation(composeBom)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.splashscreen)

    // Material icons
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    // Data store
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)

    // Room database
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)

    // M3 & Play Services
    implementation(libs.wear.compose.material)
    implementation(libs.wear.compose.material3)
    implementation(libs.wear.compose.foundation)
    implementation(libs.wear.compose.navigation)
    implementation(libs.wear.gms.playservices)

    // Horologist for correct Compose layout
    implementation(libs.horologist.compose.layout)
    implementation(libs.horologist.compose.material)

    // JSON
    implementation(libs.kotlinx.serialization.json)

    // Preview Tooling
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.ui.test.manifest)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Testing
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.rule)
    testImplementation(libs.horologist.roboscreenshots)

    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(composeBom)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(composeBom)

    ksp(libs.androidx.room.compiler)
}