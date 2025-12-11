plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")

}

android {
    namespace = "com.yey.semilla"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.yey.semilla"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    // --- AGREGAR ESTO PARA LA API (RETROFIT) ---
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // Para ver los logs de la API

    // --- AGREGAR ESTO PARA INYECCIÓN DE DEPENDENCIAS (Hilt) ---
    // (Opcional por ahora, pero recomendado para la Evaluación de Tests)
    // implementation("com.google.dagger:hilt-android:2.48")
    // kapt("com.google.dagger:hilt-android-compiler:2.48")
        // Iconos Material extendidos (Necesario para usar más iconos además de los básicos)
        implementation("androidx.compose.material:material-icons-extended:1.7.8")
        // coil compose
        implementation("io.coil-kt:coil-compose:2.7.0")
        //camara
        implementation("androidx.activity:activity-compose:1.11.0")
        // ViewModel KTX (Necesario para usar el ViewModel en Compose)
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
        // Navigation Compose (Necesario para la navegación entre pantallas)
        implementation("androidx.navigation:navigation-compose:2.7.0")
        // Corrutinas (Para tareas asíncronas en el ViewModel)
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
        val room_version = "2.8.3"
        implementation("androidx.room:room-runtime:$room_version")
        // Room KTX (Necesario para Corrutinas en Room)
        implementation("androidx.room:room-ktx:$room_version")
        // Room Compiler (Necesario para la generación de código de la base de datos)
        kapt ("androidx.room:room-compiler:$room_version")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}