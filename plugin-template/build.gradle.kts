/*
 * A plugin for the 634 client (docs/PLUGINS.md in the client repository). Copy this folder to start
 * your own: change the manifest attributes below and the code in src/main/kotlin.
 *
 * It compiles against the client's plugin API jar, runetek5-plugin-api-<version>.jar, from a client
 * release (https://github.com/2011Scape/634-client/releases) or a client build (./gradlew releaseApi
 * puts it in build/release). Say where it is with -PapiJar=<path>, absolute or relative to this folder
 * (Gradle resolves it against the project, not against where you ran it); without that, the newest
 * one in ../build/release is used, which suits this folder inside the client repository.
 *
 * The API and the Kotlin standard library are compileOnly: the client has both, and a plugin jar that
 * carries its own copy is refused. The Kotlin version here must not be newer than the client's.
 */
plugins {
    kotlin("jvm") version "2.2.20"
}

group = "example"
version = "0.1"

// looked up when the compiler asks for its classpath, so that a missing jar fails compileKotlin and
// nothing else (and the configuration cache has nothing to track here)
val projectDir: Directory = layout.projectDirectory
val apiJar: Provider<File> = providers.gradleProperty("apiJar").map { projectDir.file(it).asFile }.orElse(
    providers.provider {
        projectDir.dir("../build/release").asFile.listFiles { f -> f.name.startsWith("runetek5-plugin-api-") && f.name.endsWith(".jar") && !f.name.endsWith("-sources.jar") }
            ?.maxByOrNull { it.lastModified() }
            ?: error("No plugin API jar: pass -PapiJar=<path to runetek5-plugin-api-<version>.jar> (absolute, or relative to this folder), or build the client with ./gradlew releaseApi")
    },
)

dependencies {
    compileOnly(files(apiJar))
    compileOnly(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(21)
}

tasks.jar {
    archiveBaseName = "example-plugin"
    // the cookbook's plugins have jars of their own (recipeJars); the example's carries only the example
    exclude("recipes/**")
    manifest {
        attributes(
            "Plugin-Class" to "example.ExamplePlugin",
            "Plugin-Id" to "example",
            "Plugin-Name" to "Example plugin",
            "Plugin-Version" to version.toString(),
            // the PluginApi.VERSION the plugin was written against; the client refuses a newer one than its own
            "Plugin-Api-Version" to "4",
            "Plugin-Author" to "you",
            "Plugin-Description" to "Says hello on the game view and in the chat.",
            // a PNG in src/main/resources, shown at 25x25 in the Plugins window; drop the line for no icon
            "Plugin-Icon" to "icon.png",
        )
    }
}

/*
 * The cookbook's plugins (src/main/kotlin/recipes, docs/PLUGINS.md in the client repository): one jar
 * each, holding only its own classes and icon, in build/libs/recipes. `./gradlew recipeJars` builds them all;
 * `./gradlew recipeJarAttackTimer` one. Copy a jar into ~/.634-client/plugins to run it.
 */
val recipes: Map<String, List<String>> = mapOf(
    // class in recipes/ to plugin id, name and description
    "AttackTimer" to listOf("attacktimer", "Attack timer", "A cooldown bar under you after each attack, and an idle warning (::attackspeed <ticks>)."),
    "GroundItemValues" to listOf("grounditemvalues", "Ground item values", "Every pile's items with their worth, and valuable drops in the chat."),
    "ItemTags" to listOf("itemtags", "Item tags", "Tag an item from its right-click menu; it is outlined in the backpack and the bank."),
)
val recipeJarTasks: List<TaskProvider<Jar>> = recipes.map { (recipeClass, meta) ->
    tasks.register<Jar>("recipeJar$recipeClass") {
        group = "build"
        description = "The cookbook's $recipeClass plugin as a jar of its own."
        from(sourceSets.main.get().output) {
            // its classes and its icon (src/main/resources/recipes/<id>.png)
            include("recipes/$recipeClass.class", "recipes/$recipeClass$*.class", "recipes/${meta[0]}.png")
        }
        archiveBaseName = meta[0]
        archiveVersion = version.toString()
        destinationDirectory = layout.buildDirectory.dir("libs/recipes")
        manifest {
            attributes(
                "Plugin-Class" to "recipes.$recipeClass",
                "Plugin-Id" to meta[0],
                "Plugin-Name" to meta[1],
                "Plugin-Version" to version.toString(),
                "Plugin-Api-Version" to "4",
                "Plugin-Author" to "Pixel",
                "Plugin-Description" to meta[2],
                "Plugin-Icon" to "recipes/${meta[0]}.png",
            )
        }
    }
}
tasks.register("recipeJars") {
    group = "build"
    description = "The cookbook's plugins, one jar each, in build/libs/recipes."
    dependsOn(recipeJarTasks)
}
