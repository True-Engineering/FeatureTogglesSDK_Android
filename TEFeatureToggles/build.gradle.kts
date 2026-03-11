plugins {
    id("com.android.library")
    `maven-publish`
}

android {
    namespace = "ru.trueengineering.tefeaturetoggles"
    compileSdk = 36

    defaultConfig {
        minSdk = 21 //Min sdk 21 to work with mobile registrator
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

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation("com.squareup.okhttp3:okhttp-coroutines:5.3.2")
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.True-Engineering"
            artifactId = "FeatureTogglesSDK_Android"
            version = "1.1.6"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
