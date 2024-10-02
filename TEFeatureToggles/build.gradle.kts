plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android") version "1.8.10"
    `maven-publish`
}

android {
    namespace = "ru.trueengineering.tefeaturetoggles"
    compileSdk = 33

    defaultConfig {
        minSdk = 21 //Min sdk 21 to work with mobile registrator
        targetSdk = 33
        aarMetadata {
            minCompileSdk = 21
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.code.gson:gson:2.8.9")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.True-Engineering"
            artifactId = "FeatureTogglesSDK_Android"
            version = "1.1.2"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
