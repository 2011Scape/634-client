plugins {
    kotlin("multiplatform") version "2.2.20"
    // Lint gates, part of check (so of build and CI): ktlint for formatting, detekt for code smells. The
    // original (deobfuscated) code is baselined; only new findings fail.
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

description = "The RuneScape client from December 2010 (build 634), with a launcher for any Void server."

val lwjglVersion = "3.4.3"
val lwjglAwtVersion = "0.2.5"
val lwjglNatives = listOf(
    "natives-linux", "natives-linux-arm64",
    // windows-x86: the README's 32-bit Java for DirectX had OpenGL from the cache's x86 libjaggl
    "natives-windows", "natives-windows-x86", "natives-windows-arm64",
    "natives-macos", "natives-macos-arm64",
)

/*
 * A Kotlin Multiplatform project with one target, the JVM.
 * The game is in src/commonMain, on shims of the Java APIs it uses; the native classes keep their
 * JNI names and signatures in src/jvmMain, with expect declarations in commonMain.
 */
kotlin {
    jvmToolchain(21)
    jvm()
    /*
     * The browser target: ./gradlew jsBrowserDevelopmentWebpack
     * builds a servable bundle in build/kotlin-webpack/js/developmentExecutable. It also keeps
     * commonMain honest: compileCommonMainKotlinMetadata compiles it against the common standard
     * library, not the JDK.
     */
    js {
        browser {
            commonWebpackConfig {
                outputFileName = "runetek5-634.js"
            }
        }
        binaries.executable()
    }
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        /*
         * The deobfuscated classes keep the 2010 jar's parameter names where they override a supertype's
         * (2,856 warnings that hide the rest); the remaining warnings are the ones worth reading.
         */
        freeCompilerArgs.add("-Xwarning-level=PARAMETER_NAME_CHANGED_ON_OVERRIDE:disabled")
        // Every other warning fails the build; the original code's are suppressed per file with the reason
        allWarningsAsErrors.set(true)
    }
    sourceSets {
        /* The plugin API (api/): the client's common code implements it and plugins compile against it. */
        commonMain.dependencies {
            api(project(":api"))
        }
        jvmMain.dependencies {
            implementation(files("lib/stub.jar"))
            /*
             * jaggl and jaclib run on LWJGL instead of the JNI libraries in the game cache:
             * lwjgl-opengl for the GL calls, lwjgl3-awt for a context on the client's AWT Canvas,
             * MemoryUtil for jaclib's native memory. The natives of every desktop platform go in.
             */
            implementation(project.dependencies.platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
            implementation("org.lwjgl:lwjgl")
            implementation("org.lwjgl:lwjgl-opengl")
            implementation("org.lwjgl:lwjgl-jawt")
            implementation("org.lwjglx:lwjgl3-awt:$lwjglAwtVersion")
            for (natives in lwjglNatives) {
                runtimeOnly("org.lwjgl:lwjgl::$natives")
                runtimeOnly("org.lwjgl:lwjgl-opengl::$natives")
            }
        }
        jsMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        }
        /* Unit tests of the JVM target (src/jvmTest): ./gradlew jvmTest, part of build. */
        jvmTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

val jvmJar = tasks.named<Jar>("jvmJar")
val jvmRuntimeClasspath = configurations.named("jvmRuntimeClasspath")
val clientClasspath = files(jvmJar, jvmRuntimeClasspath)
val clientLauncher = javaToolchains.launcherFor { languageVersion = JavaLanguageVersion.of(21) }

/* The JVM options of the launcher, the client and the start scripts (512m: 634HD's draw distance 2x builds
   a 168-tile scene, which needs about twice the game's 104-tile one). */
val clientJvmArgs = listOf(
    "-Xmx512m",
    "-Dsun.java2d.noddraw=true",
    "--add-opens",
    "java.base/java.lang=ALL-UNNAMED",
    "--enable-native-access=ALL-UNNAMED",
)

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
tasks.register<JavaExec>("run") {
    group = "application"
    description = "Runs the launcher, RuneTek5, with --args."
    classpath = clientClasspath
    mainClass = "RuneTek5"
    javaLauncher = clientLauncher
    jvmArgs = clientJvmArgs
    withNativeDir()
}

/*
 * ./gradlew installDist: the client with start scripts in build/install/runetek5-634
 * (bin/runetek5-634, or bin\runetek5-634.bat on Windows), which start the launcher.
 */
val startScripts = tasks.register<CreateStartScripts>("startScripts") {
    applicationName = "runetek5-634"
    mainClass = "RuneTek5"
    defaultJvmOpts = clientJvmArgs
    classpath = clientClasspath
    outputDir = layout.buildDirectory.dir("scripts").get().asFile
}

tasks.register<Sync>("installDist") {
    group = "distribution"
    description = "Installs the client with start scripts into build/install/runetek5-634."
    into(layout.buildDirectory.dir("install/runetek5-634"))
    into("lib") {
        from(clientClasspath)
    }
    into("bin") {
        from(startScripts)
    }
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
    classpath = clientClasspath
    mainClass = "client"
    javaLauncher = clientLauncher
    jvmArgs = clientJvmArgs
    args = listOf("3594", "3594", "0", "live", "english", "game0")
    listOf("client.debug", "client.zoom", "client.renderDistance", "client.console", "client.devCommands", "client.shiftDrop", "client.msaa", "client.smaa", "client.renderScale", "client.a2c", "client.server", "client.packets", "client.fps", "client.scale", "client.address", "client.port", "client.plugins", "client.prices",
        "client.test", "client.test.user", "client.test.password", "client.test.frames", "client.test.timeout",
        "client.test.shot", "client.test.reference", "client.test.tolerance", "client.test.detailTolerance", "client.test.command", "client.test.commandFrames", "client.test.chat", "client.test.chatFrames", "client.test.windowModes", "client.test.subInterfaces", "client.test.menuWalk", "client.test.menuCheck", "client.test.npcMenu", "client.test.pluginManager", "client.test.pluginHome", "client.test.menuWalkHome", "client.test.media", "client.test.markers", "client.test.xpDrops", "client.test.respawn", "client.test.toolkit", "client.test.bench", "client.test.benchMode",
        "client.test.scenes", "client.test.sceneCommand", "client.test.npcDump", "client.test.sceneFrames", "client.test.sceneReferenceToolkit", "client.test.scenePixelTolerance", "client.test.sceneMaxDifferent", "client.toolkit", "client.test.hdFail", "client.test.findText", "client.test.benchWindow", "client.test.scenePosition", "client.test.hdDebug", "client.test.hdNoTextures", "client.test.hdWideShot", "client.hd.dataDir", "client.hd.uncompressedMaps", "client.test.hdOverlay", "client.test.hdPanelShot", "client.test.hdGlVersion", "client.test.sceneBurst", "client.test.sceneSpin", "client.test.hdCycle", "client.test.sceneWalk", "client.test.hdShadowParts", "client.test.toggleRounds", "client.test.hdDebugCasters", "client.test.hdDebugCastersRadius", "client.test.soak", "client.test.soakFrames", "client.test.soakToggle", "client.test.characterShadows", "client.test.wear", "client.test.materialScan", "client.test.sceneWalkTiles", "client.test.sceneWalkEvery", "client.test.sceneBurstLoc", "client.test.sceneBurstSave", "client.test.sceneLumaJump", "client.test.sceneLumaLog", "client.test.inspect", "client.test.inspectRadius", "client.test.timeOfDay", "client.test.hdFloorTally", "client.test.hdLocScan", "client.test.openAt", "client.test.openMode", "client.test.timeRuns", "client.test.hdItemScan", "client.test.carry", "client.test.hdReflectionShot", "client.test.hdReflectionParts", "client.test.hdSceneMax", "client.test.hdOverlayRaw", "client.test.hdGpu", "client.test.hdGridDump")
        .forEach { name -> providers.gradleProperty(name).orNull?.let { systemProperty(name, it) } }
    // 634HD's feature switches, client.hd.<name> (HdSettings), all forwarded
    providers.gradlePropertiesPrefixedBy("client.hd.").get().forEach { (name, value) -> systemProperty(name, value) }
    withNativeDir()
}

/*
 * The release jar: everything the client needs in one runnable jar. The manifest opens java.lang the
 * way --add-opens does on the command line, so `java -jar` and a double click both work.
 */
// 634HD's material maps: derived from dumped cache textures, then precompressed to BC5/BC7 for the
// GPU. Run by hand after changing a map (the results are committed); needs python3 with Pillow and
// NumPy. See tools/make_material_maps.py and tools/compress_material_maps.py.
tasks.register<Exec>("compressMaterialMaps") {
    group = "634hd"
    description = "Precompresses src/jvmMain/resources/634hd/materials/*/ to BC5/BC7 (tools/compress_material_maps.py)."
    commandLine("python3", "tools/compress_material_maps.py")
}

// The jar carries the material maps as their precompressed blocks only: HdMaterialMaps decodes those
// itself where a GPU format is missing, so the PNGs (but the fresh textures' albedo.png, which has no
// block form), and the provenance files beside them, stay in the repository. 30 MB less in the jar.
tasks.named<ProcessResources>("jvmProcessResources") {
    exclude(
        "634hd/materials/**/normal.png", "634hd/materials/**/orm.png", "634hd/materials/**/emissive.png",
        "634hd/materials/**/recipe.txt", "634hd/materials/**/prompt.txt",
    )
}

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
            // LWJGL keeps its Java 9+ classes under META-INF/versions
            "Multi-Release" to "true",
        )
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(jvmJar.map { zipTree(it.archiveFile) })
    from(jvmRuntimeClasspath.map { classpath -> classpath.map { zipTree(it) } }) {
        exclude("META-INF/MANIFEST.MF", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
        // one jar's module descriptor would misdescribe the merged jar
        exclude("module-info.class", "META-INF/versions/*/module-info.class")
    }
}

/*
 * The browser client for a release: the production bundle with its page and the SMAA files, zipped
 * beside the jar. Served behind a WebSocket relay to a game server (README: "Running the browser client").
 */
val releaseBrowser = tasks.register<Zip>("releaseBrowser") {
    group = "distribution"
    description = "Zips the browser bundle beside the release jar."
    archiveBaseName = "runetek5-634-browser"
    archiveVersion = project.version.toString().takeUnless { it == "unspecified" } ?: "dev-build"
    destinationDirectory = layout.buildDirectory.dir("release")
    from(tasks.named("jsBrowserDistribution"))
}

/*
 * The plugin API for plugin authors: api/'s JVM jar and its sources, renamed to go beside the client jar
 * as runetek5-plugin-api-<version>.jar and -sources.jar (docs/PLUGINS.md). The same classes are inside the
 * client jar too; this copy is what a plugin's build compiles against. Not "runetek5-634-...": the Void
 * launcher takes the first runetek5-634-*.jar in a release's SHA256SUMS as the client.
 */
val releaseApi = tasks.register<Copy>("releaseApi") {
    group = "distribution"
    description = "Copies the plugin API jar and its sources beside the release jar."
    // local values, so that the rename lambdas capture strings and not the build script (configuration cache)
    val apiJarName: String = "runetek5-plugin-api-" + (project.version.toString().takeUnless { it == "unspecified" } ?: "dev-build") + ".jar"
    val apiSourcesName: String = apiJarName.removeSuffix(".jar") + "-sources.jar"
    from(project(":api").tasks.named("jvmJar")) {
        rename { apiJarName }
    }
    from(project(":api").tasks.named("jvmSourcesJar")) {
        rename { apiSourcesName }
    }
    into(layout.buildDirectory.dir("release"))
}

tasks.register("release") {
    group = "distribution"
    description = "Builds the release jar, the plugin API jar and the browser zip."
    dependsOn(releaseJar, releaseBrowser, releaseApi)
}

/*
 * ./gradlew checkClasses (part of check, run by CI): checks that need no game server. Every class of
 * the client must load and pass the JVM's verifier from the release jar, and no method may be over
 * HotSpot's 8000-byte JIT limit unless config/huge-methods.txt lists it (see src/ci/java/CheckClasses).
 * -PcheckClasses.java=25 runs it on another JDK than the toolchain's.
 */
val compileCi = tasks.register<JavaCompile>("compileCi") {
    source = fileTree("src/ci/java")
    classpath = files()
    destinationDirectory = layout.buildDirectory.dir("classes/java/ci")
    javaCompiler = javaToolchains.compilerFor { languageVersion = JavaLanguageVersion.of(21) }
    options.encoding = "UTF-8"
}

tasks.register<JavaExec>("checkClasses") {
    group = "verification"
    description = "Loads and verifies every client class from the release jar, and checks method sizes."
    val clientJar = jvmJar.flatMap { it.archiveFile }
    val allowlist = layout.projectDirectory.file("config/huge-methods.txt")
    inputs.file(clientJar)
    inputs.file(allowlist)
    classpath = files(compileCi) + files(releaseJar.flatMap { it.archiveFile })
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

// both put files in build/release: the class check reads the release jar there after the API copy, never beside it
tasks.named("checkClasses") {
    mustRunAfter(releaseApi)
}

/*
 * The SMAA shader and lookup textures (src/jvmMain/resources/smaa) go beside the browser bundle too:
 * with client.smaa the page fetches them before the client starts (main.js.kt).
 */
tasks.named<ProcessResources>("jsProcessResources") {
    from("src/jvmMain/resources") {
        include("smaa/**")
    }
}

/*
 * The development bundle's folder is servable as it is: the page (index.html) and its icon are
 * copied in beside runetek5-634.js. Serve it with node tools/serve.mjs, with the relay running
 * (node tools/relay.mjs), as the README's "Running the browser client" says.
 */
val jsCopyDevResources = tasks.register<Copy>("jsCopyDevResources") {
    from(tasks.named("jsProcessResources"))
    into(layout.buildDirectory.dir("kotlin-webpack/js/developmentExecutable"))
}

tasks.named("jsBrowserDevelopmentWebpack") {
    finalizedBy(jsCopyDevResources)
}

ktlint {
    // the client's 2010 idiom (tabs, the deobfuscated names) is baselined, not restyled
    baseline.set(file("config/ktlint-baseline.xml"))
    filter {
        exclude { it.file.path.contains("/build/") }
    }
}

detekt {
    baseline = file("config/detekt-baseline.xml")
    buildUponDefaultConfig = true
    config.setFrom(file("config/detekt.yml"))
    source.setFrom("src/commonMain/kotlin", "src/jvmMain/kotlin", "src/jsMain/kotlin", "src/jvmTest/kotlin", "api/src/commonMain/kotlin")
}
