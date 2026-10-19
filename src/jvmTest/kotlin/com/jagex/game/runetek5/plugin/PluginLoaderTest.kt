package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.loader.PluginClassLoader
import com.jagex.game.runetek5.plugin.loader.PluginLoader
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.jar.Attributes
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.jar.Manifest
import javax.tools.JavaCompiler
import javax.tools.ToolProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Not part of the original client: loading plugin jars (PluginLoader). Each test writes a plugin in
 * Java, compiles it with the JDK's compiler against the test classpath and jars it, so the plugin's
 * classes are not on the test's own classpath and the jar's class loader is really exercised.
 */
class PluginLoaderTest {

	private val helloSource: String = """
		package fixture;
		import com.jagex.game.runetek5.plugin.api.Plugin;
		import com.jagex.game.runetek5.plugin.api.PluginContext;
		public class Hello implements Plugin {
			public static int started;
			static { System.setProperty("fixture.hello.initialised", "yes"); }
			public Hello() { System.setProperty("fixture.hello.made", "yes"); }
			public void start(PluginContext context) { started++; }
		}
	""".trimIndent()

	private val hiddenSource: String = """
		package fixture;
		import com.jagex.game.runetek5.plugin.api.Plugin;
		import com.jagex.game.runetek5.plugin.api.PluginContext;
		class Hidden implements Plugin {
			public void start(PluginContext context) { }
		}
	""".trimIndent()

	private val throwingSource: String = """
		package fixture;
		import com.jagex.game.runetek5.plugin.api.Plugin;
		import com.jagex.game.runetek5.plugin.api.PluginContext;
		public class Throwing implements Plugin {
			public Throwing() { throw new IllegalStateException("not today"); }
			public void start(PluginContext context) { }
		}
	""".trimIndent()

	private val notAPluginSource: String = """
		package fixture;
		public class Plain { }
	""".trimIndent()

	private fun <T> inTempDir(block: (File) -> T): T {
		val dir: File = Files.createTempDirectory("634-plugin-jars").toFile()
		try {
			return block(dir)
		} finally {
			dir.deleteRecursively()
		}
	}

	/** Compiles [sources] (class name to Java source) and jars them with [attributes], plus empty [extraEntries]. */
	private fun jar(dir: File, name: String, attributes: Map<String, String>, sources: Map<String, String>, extraEntries: List<String> = listOf()): File {
		val sourceDir: File = File(dir, "src-" + name)
		val classDir: File = File(dir, "classes-" + name)
		sourceDir.mkdirs()
		classDir.mkdirs()
		val files: ArrayList<String> = ArrayList()
		for ((className, source) in sources) {
			val file: File = File(sourceDir, className.replace('.', '/') + ".java")
			file.parentFile.mkdirs()
			file.writeText(source)
			files.add(file.path)
		}
		if (files.isNotEmpty()) {
			val compiler: JavaCompiler = assertNotNull(ToolProvider.getSystemJavaCompiler(), "a JDK compiler")
			val args: ArrayList<String> = arrayListOf("-d", classDir.path, "-cp", System.getProperty("java.class.path"), "-proc:none")
			args.addAll(files)
			assertEquals(0, compiler.run(null, null, null, *args.toTypedArray()), "the fixture compiles")
		}
		val manifest: Manifest = Manifest()
		manifest.mainAttributes[Attributes.Name.MANIFEST_VERSION] = "1.0"
		for ((key, value) in attributes) {
			manifest.mainAttributes.putValue(key, value)
		}
		val jarFile: File = File(dir, name + ".jar")
		JarOutputStream(FileOutputStream(jarFile), manifest).use { out ->
			classDir.walkTopDown().filter { it.isFile }.forEach { classFile ->
				out.putNextEntry(JarEntry(classFile.relativeTo(classDir).path.replace(File.separatorChar, '/')))
				out.write(classFile.readBytes())
				out.closeEntry()
			}
			for (entry in extraEntries) {
				out.putNextEntry(JarEntry(entry))
				out.closeEntry()
			}
		}
		return jarFile
	}

	private val helloAttributes: Map<String, String> = mapOf(
		"Plugin-Class" to "fixture.Hello",
		"Plugin-Id" to "hello",
		"Plugin-Name" to "Hello plugin",
		"Plugin-Version" to "1.2",
		"Plugin-Api-Version" to PluginApi.VERSION.toString(),
		"Plugin-Author" to "tests",
	)

	@Test
	fun aPluginJarLoadsThroughItsOwnClassLoaderAndRunsNothingUntilMade() {
		System.clearProperty("fixture.hello.initialised")
		System.clearProperty("fixture.hello.made")
		inTempDir { dir ->
			jar(dir, "hello", helloAttributes, mapOf("fixture.Hello" to helloSource))
			val loaded: Array<PluginRegistration> = PluginLoader.load(dir)
			assertEquals(1, loaded.size)
			val registration: PluginRegistration = loaded[0]
			assertNull(registration.error)
			assertEquals(PluginRegistration.LOADED, registration.state)
			assertEquals("hello", registration.descriptor.id)
			assertEquals("Hello plugin", registration.descriptor.name)
			assertEquals("1.2", registration.descriptor.version)
			assertEquals("tests", registration.descriptor.author)
			assertEquals("hello.jar", registration.source)
			assertNull(registration.plugin, "not made at load")
			assertTrue(registration.loadable())
			assertNull(System.getProperty("fixture.hello.initialised"), "the class is not initialised at load")
			assertNull(System.getProperty("fixture.hello.made"))
			val plugin: Any = assertNotNull(registration.factory).create()
			assertEquals("yes", System.getProperty("fixture.hello.initialised"))
			assertEquals("yes", System.getProperty("fixture.hello.made"))
			assertEquals("fixture.Hello", plugin.javaClass.name)
			val loader: PluginClassLoader = assertNotNull(plugin.javaClass.classLoader as? PluginClassLoader)
			assertSame(PluginLoader::class.java.classLoader, loader.parent, "the client's loader is the parent")
			assertSame(loader, registration.loader)
		}
	}

	@Test
	fun aClassThatIsNotPublicIsRefused() {
		inTempDir { dir ->
			jar(dir, "hidden", helloAttributes + ("Plugin-Class" to "fixture.Hidden"), mapOf("fixture.Hidden" to hiddenSource))
			val registration: PluginRegistration = PluginLoader.load(dir)[0]
			assertEquals(PluginRegistration.FAILED, registration.state)
			assertFalse(registration.loadable())
			assertTrue(registration.error!!.contains("not public"), registration.error)
		}
	}

	@Test
	fun aThrowingConstructorIsRefusedWhenThePluginIsMadeNotWhenItIsLoaded() {
		inTempDir { dir ->
			jar(dir, "throwing", helloAttributes + ("Plugin-Class" to "fixture.Throwing"), mapOf("fixture.Throwing" to throwingSource))
			val registration: PluginRegistration = PluginLoader.load(dir)[0]
			assertEquals(PluginRegistration.LOADED, registration.state)
			assertNull(registration.error)
			val error: Throwable = assertFailsWith<IllegalStateException> { registration.factory!!.create() }
			assertEquals("not today", error.message, "the constructor's own exception, not the reflection wrapper")
		}
	}

	@Test
	fun jarsAreLoadedInNameOrder() {
		inTempDir { dir ->
			jar(dir, "b-second", helloAttributes + ("Plugin-Id" to "second"), mapOf("fixture.Hello" to helloSource))
			jar(dir, "a-first", helloAttributes + ("Plugin-Id" to "first"), mapOf("fixture.Hello" to helloSource))
			val loaded: Array<PluginRegistration> = PluginLoader.load(dir)
			assertEquals(listOf("first", "second"), loaded.map { it.descriptor.id })
		}
	}

	@Test
	fun aJarWithoutAPluginClassIsRefused() {
		inTempDir { dir ->
			jar(dir, "nameless", helloAttributes - "Plugin-Class", mapOf("fixture.Hello" to helloSource))
			val registration: PluginRegistration = PluginLoader.load(dir)[0]
			assertEquals(PluginRegistration.FAILED, registration.state)
			assertNull(registration.plugin)
			assertTrue(registration.error!!.contains("Plugin-Class"), registration.error)
			assertEquals("file:nameless.jar", registration.descriptor.id)
		}
	}

	@Test
	fun aBadIdIsRefused() {
		inTempDir { dir ->
			jar(dir, "badid", helloAttributes + ("Plugin-Id" to "Hello World"), mapOf("fixture.Hello" to helloSource))
			val registration: PluginRegistration = PluginLoader.load(dir)[0]
			assertEquals(PluginRegistration.FAILED, registration.state)
			assertTrue(registration.error!!.contains("Plugin-Id"), registration.error)
		}
	}

	@Test
	fun aPluginForANewerApiIsRefused() {
		inTempDir { dir ->
			jar(dir, "future", helloAttributes + ("Plugin-Api-Version" to "999"), mapOf("fixture.Hello" to helloSource))
			val registration: PluginRegistration = PluginLoader.load(dir)[0]
			assertEquals(PluginRegistration.FAILED, registration.state)
			assertTrue(registration.error!!.contains("999"), registration.error)
			assertEquals("hello", registration.descriptor.id, "the manifest was read before the refusal")
		}
	}

	@Test
	fun aClassThatIsNotAPluginIsRefused() {
		inTempDir { dir ->
			jar(dir, "plain", helloAttributes + ("Plugin-Class" to "fixture.Plain"), mapOf("fixture.Plain" to notAPluginSource))
			val registration: PluginRegistration = PluginLoader.load(dir)[0]
			assertEquals(PluginRegistration.FAILED, registration.state)
			assertTrue(registration.error!!.contains("does not implement"), registration.error)
		}
	}

	@Test
	fun aMissingClassIsRefused() {
		inTempDir { dir ->
			jar(dir, "missing", helloAttributes + ("Plugin-Class" to "fixture.Nowhere"), mapOf("fixture.Hello" to helloSource))
			val registration: PluginRegistration = PluginLoader.load(dir)[0]
			assertEquals(PluginRegistration.FAILED, registration.state)
			assertTrue(registration.error!!.contains("fixture.Nowhere"), registration.error)
		}
	}

	@Test
	fun aJarThatBundlesTheStandardLibraryIsRefused() {
		inTempDir { dir ->
			jar(dir, "shaded", helloAttributes, mapOf("fixture.Hello" to helloSource), listOf("kotlin/Unit.class"))
			val registration: PluginRegistration = PluginLoader.load(dir)[0]
			assertEquals(PluginRegistration.FAILED, registration.state)
			assertTrue(registration.error!!.contains("kotlin/Unit.class"), registration.error)
		}
	}

	@Test
	fun filesThatAreNotJarsAreIgnored() {
		inTempDir { dir ->
			File(dir, "notes.txt").writeText("not a plugin")
			File(dir, "sub").mkdirs()
			assertEquals(0, PluginLoader.load(dir).size)
		}
	}
}
