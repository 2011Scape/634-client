plugins {
    application
    kotlin("jvm") version "2.2.20"
}

description = "The RuneScape client from December 2010 (build 634), with a launcher for any Void server."

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

/*
 * The client is Kotlin, in src/main/kotlin (docs/MULTIPLATFORM.md, milestone 2): a straight
 * conversion of the Java, with the native classes keeping their JNI names and signatures.
 */
kotlin {
    jvmToolchain(21)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

dependencies {
    implementation(files("lib/stub.jar"))
    implementation(libs.openrs2.deob.annotations)
}

/*
 * The start scripts (installDist) and the release jar start the launcher, RuneTek5, which takes the
 * server's address, port and keys as options.
 */
application {
    mainClass = "RuneTek5"
    applicationDefaultJvmArgs = listOf(
        "-Xmx256m",
        "-Dsun.java2d.noddraw=true",
        "--add-opens",
        "java.base/java.lang=ALL-UNNAMED",
        "--enable-native-access=ALL-UNNAMED",
    )
}

/*
 * The directory the client unpacks its native libraries into goes on LD_LIBRARY_PATH:
 * -Pclient.nativeDir=<dir>, or on Linux by default ~/.jagex_cache_32/runescape, where the live game0
 * client unpacks them. libhw3d.so (behind the native OpenGL toolkit) links against libjaclib.so by
 * name, and libjaclib.so has no SONAME, so without this the system's loader cannot find it and the
 * client falls back to the Java software toolkit, which looks like safe mode. Not needed for the Java
 * toolkits.
 */
val nativeDir = providers.gradleProperty("client.nativeDir").orNull
    ?: if (System.getProperty("os.name").startsWith("Linux")) System.getProperty("user.home") + "/.jagex_cache_32/runescape" else null

fun JavaExec.withNativeDir() {
    nativeDir?.let { dir ->
        val existing = System.getenv("LD_LIBRARY_PATH")
        environment("LD_LIBRARY_PATH", if (existing.isNullOrEmpty()) dir else "$dir:$existing")
    }
}

/* ./gradlew run starts the launcher: ./gradlew run --args="--address play.example.org --zoom". */
tasks.run.configure {
    withNativeDir()
}

/*
 * ./gradlew runClient starts the client itself against a local Void server.
 * The client decides its own ports in local mode: the game port is the world id plus 40000, so world
 * 3594 connects to 43594. Switches for behaviour that is not part of the original client (see
 * ClientFeatures) are passed as -Pclient.debug=true and the like; none is set unless asked for.
 */
tasks.register<JavaExec>("runClient") {
    group = "application"
    description = "Runs the client itself against a local Void server, with -Pclient.* switches."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "client"
    jvmArgs = application.applicationDefaultJvmArgs.toList()
    args = listOf("3594", "3594", "0", "live", "english", "game0")
    listOf("client.debug", "client.zoom", "client.renderDistance", "client.console", "client.shiftDrop", "client.msaa", "client.smaa", "client.renderScale", "client.a2c", "client.server", "client.packets", "client.fps", "client.scale", "client.address", "client.port",
        "client.test", "client.test.user", "client.test.password", "client.test.frames", "client.test.timeout",
        "client.test.shot", "client.test.reference", "client.test.tolerance", "client.test.detailTolerance", "client.test.command", "client.test.commandFrames", "client.test.chat", "client.test.chatFrames", "client.test.windowModes", "client.test.subInterfaces", "client.test.menuWalk", "client.test.menuWalkHome", "client.test.media", "client.test.markers", "client.tileMarkers", "client.xpDrops", "client.npcLabels", "client.groundItemLabels", "client.test.groundItems", "client.test.npcLabels", "client.test.xpDrops", "client.test.toolkit")
        .forEach { name -> providers.gradleProperty(name).orNull?.let { systemProperty(name, it) } }
    withNativeDir()
}

/*
 * The release jar: everything the client needs in one runnable jar. The manifest opens java.lang the
 * way --add-opens does on the command line, so `java -jar` and a double click both work.
 */
val releaseJar = tasks.register<Jar>("releaseJar") {
    group = "distribution"
    description = "Builds the runnable release jar that starts RuneTek5."
    archiveBaseName = "runetek5-634"
    archiveVersion = project.version.toString().takeUnless { it == "unspecified" } ?: "dev-build"
    destinationDirectory = layout.buildDirectory.dir("release")
    manifest {
        attributes(
            "Main-Class" to "RuneTek5",
            "Add-Opens" to "java.base/java.lang",
            "Enable-Native-Access" to "ALL-UNNAMED",
            "Implementation-Version" to archiveVersion.get(),
        )
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({ configurations.runtimeClasspath.get().map { zipTree(it) } }) {
        exclude("META-INF/MANIFEST.MF", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
}

tasks.register("release") {
    group = "distribution"
    description = "Builds the release jar."
    dependsOn(releaseJar)
}

/*
 * ./gradlew checkClasses (part of check, run by CI): checks that need no game server. Every class of
 * the client must load and pass the JVM's verifier from the release jar, and no method may be over
 * HotSpot's 8000-byte JIT limit unless config/huge-methods.txt lists it (see src/ci/java/CheckClasses).
 * -PcheckClasses.java=25 runs it on another JDK than the toolchain's.
 */
val ci = sourceSets.create("ci")

tasks.register<JavaExec>("checkClasses") {
    group = "verification"
    description = "Loads and verifies every client class from the release jar, and checks method sizes."
    val clientJar = tasks.jar.flatMap { it.archiveFile }
    val allowlist = layout.projectDirectory.file("config/huge-methods.txt")
    inputs.file(clientJar)
    inputs.file(allowlist)
    classpath = ci.output + files(releaseJar.flatMap { it.archiveFile })
    mainClass = "CheckClasses"
    jvmArgs("-Djava.awt.headless=true")
    argumentProviders.add(CommandLineArgumentProvider { listOf(clientJar.get().asFile.path, allowlist.asFile.path) })
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(providers.gradleProperty("checkClasses.java").getOrElse("21").toInt())
    }
}

tasks.check {
    dependsOn("checkClasses")
}
