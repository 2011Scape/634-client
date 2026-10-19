plugins {
    kotlin("multiplatform")
    id("org.jlleitschuh.gradle.ktlint")
}

description = "The plugin API of the 634 client: the interfaces, events and types a plugin compiles against."

/*
 * A library with no dependencies but the Kotlin standard library, built for both of the client's
 * targets so that the client's common code can use it. Its JVM jar is published beside the client
 * as runetek5-plugin-api-<version>.jar (the root's releaseApi task); a plugin compiles against that jar
 * with compileOnly and never bundles it (docs/PLUGINS.md).
 */
kotlin {
    jvmToolchain(21)
    jvm {
        withSourcesJar()
    }
    js {
        browser()
    }
    compilerOptions {
        allWarningsAsErrors.set(true)
    }
}

ktlint {
    filter {
        exclude { it.file.path.contains("/build/") }
    }
}
