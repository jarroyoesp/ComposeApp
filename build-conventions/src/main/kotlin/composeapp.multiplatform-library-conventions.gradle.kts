import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.kotlin.dsl.the

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-parcelize")
}

val libs = the<LibrariesForLibs>()

android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    androidTarget()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    jvm("desktop")
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.ktor.client.android)
        }

        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.kamel)
            implementation(libs.koin.core)
            implementation(libs.kotlin.result)
            implementation(libs.multiplatform.log)
            implementation(libs.tlaster.precompose)
            implementation(libs.tlaster.precompose.viewmodel)

        }

        commonTest.dependencies {
            api(libs.apollo.testing.support)
            api(libs.coroutines.test)
            api(libs.junit)
            api(libs.kotlin.test)
            api(libs.kotlin.test.junit)
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.ktor.client.cio)
            }
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}
