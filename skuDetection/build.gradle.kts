plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.scorecarts.skudetection"
    compileSdk = 33



    defaultConfig {
        minSdk = 24

        ndk{
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
            abiFilters.add("x86")

        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")


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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // tflite
    implementation ("org.tensorflow:tensorflow-lite:2.4.0")
    implementation ("org.tensorflow:tensorflow-lite-gpu:2.4.0")
}

publishing {
    publications {
        create<MavenPublication>("myPublication") {


            groupId = "com.github.muzammal1451415" // Replace with your group ID
            artifactId = "image-detection-library" // Replace with your artifact ID
            version = "1.0.0" // Replace with your version
        }
    }

}


