plugins {
    id("composeapp.multiplatform-library-conventions")
    id("org.jetbrains.compose")
}

android {
    namespace = "com.jarroyo.feature.home.shared"
    sourceSets["main"].apply {
        res.srcDirs("src/androidMain/res", "src/commonMain/resources")
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.runtime)

            implementation(projects.modules.featureHomeApi)
            implementation(projects.modules.libraryNavigation)
            implementation(projects.modules.libraryNetworkApi)
            implementation(projects.modules.libraryNetwork)
            implementation(projects.modules.libraryUiShared)
        }
    }
}