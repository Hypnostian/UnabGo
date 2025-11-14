println("DEBUG GRADLE → KEY: ${project.findProperty("OLLAMA_API_KEY")}")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
}

android {
    namespace = "co.edu.unab.sebastianlizcano.unabgo"
    compileSdk = 36

    defaultConfig {
        applicationId = "co.edu.unab.sebastianlizcano.unabgo"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //LEER EL API KEY DESDE local.properties Y PASARLO A BuildConfig
        val ollamaKey = project.providers
            .gradleProperty("OLLAMA_API_KEY")
            .orNull ?: ""
        buildConfigField("String", "OLLAMA_API_KEY", "\"$ollamaKey\"")

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
        buildConfig = true // activa llamados a local properties
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.firebase.firestore)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Navegación para Jetpack Compose
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Animaciones de Compose (para el fade/scale del Splash)
    implementation("androidx.compose.animation:animation")

    // Corrutinas (para delay, ViewModel, etc.)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")

    // Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // DataStore Preferences (para persistir idioma y para el QR)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-messaging")

// Credential Manager + Google Identity
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.1.0")

    //COIL (para cargar imágenes con Compose)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Adaptación en todas las Screens
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")

    // Detectar el QR (ML Kit) / Lanzar el selector de imágenes (Activity Result API) /
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("androidx.activity:activity-compose:1.9.2")

    // Base de Datos Local sirve en Horario y Calculadora
    val roomVersion = "2.8.3"

    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Necesario para la IA
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}
