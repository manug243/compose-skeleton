plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    `maven-publish`
}

group = "io.github.manug243"
version = "0.1.0"

kotlin {
    explicitApi()

    androidLibrary {
        namespace = "io.github.manug243.composeskeleton"
        compileSdk = 37
        minSdk = 24

        withHostTestBuilder {}
    }
    jvm("desktop")
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.mp.ui)
            implementation(libs.compose.mp.animation.core)
            implementation(libs.compose.mp.foundation)
            implementation(libs.compose.mp.runtime)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set("Compose Skeleton")
            description.set("Layout-aware skeleton placeholders with a synchronized shimmer for Compose Multiplatform.")
            url.set("https://github.com/manug243/compose-skeleton")
            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("manug243")
                    name.set("manug243")
                }
            }
            scm {
                url.set("https://github.com/manug243/compose-skeleton")
                connection.set("scm:git:https://github.com/manug243/compose-skeleton.git")
            }
        }
    }
}

afterEvaluate {
    publishing.publications.withType<MavenPublication>().configureEach {
        artifactId = artifactId.replaceFirst("skeleton", "compose-skeleton")
    }
}
