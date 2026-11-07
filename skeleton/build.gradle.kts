plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    `maven-publish`
}

group = "io.github.manug243"
version = "0.1.0"

kotlin {
    explicitApi()
}

android {
    namespace = "io.github.manug243.composeskeleton"
    compileSdk = 37

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = project.group.toString()
                artifactId = "compose-skeleton"
                version = project.version.toString()
                pom {
                    name.set("Compose Skeleton")
                    description.set("Layout-aware skeleton placeholders with a synchronized shimmer for Jetpack Compose.")
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
    }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.animation.core)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    testImplementation(libs.junit)
}
