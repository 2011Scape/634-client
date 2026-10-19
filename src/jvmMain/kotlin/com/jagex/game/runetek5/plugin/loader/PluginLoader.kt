package com.jagex.game.runetek5.plugin.loader

import com.jagex.game.runetek5.plugin.PluginFactory
import com.jagex.game.runetek5.plugin.PluginRegistration
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import java.io.File
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.util.Enumeration
import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * Not part of the original client: loads the plugin jars of a folder (docs/PLUGINS.md), in the
 * order of their names. Each jar gets a registration: with its plugin, or failed with the reason
 * when the jar's manifest, contents or class are not what a plugin's should be, so that `::plugins`
 * can say so. Nothing is started here, and nothing of the plugin's runs: its class is looked at
 * without being initialised, and its object is made when the plugin is first turned on
 * (PluginFactory), so a plugin the player turned off stays inert.
 */
object PluginLoader {

	/**
	 * Packages a plugin jar may not carry: it would shadow the client's or the standard library's classes.
	 * The jar's own entries are all a plugin can load from: PluginManifest refuses a `Class-Path` and
	 * a `Multi-Release` jar, whose other classes this look would miss.
	 */
	private val FORBIDDEN_PREFIXES: Array<String> = arrayOf("com/jagex/", "kotlin/", "org/lwjgl/", "jaggl/", "jaclib/", "jagdx/")

	@JvmStatic
	fun load(directory: File): Array<PluginRegistration> {
		if (!directory.isDirectory) {
			System.err.println("Plugins: " + directory.path + " is not a folder")
			return arrayOf()
		}
		val files: Array<File> = directory.listFiles { file -> file.isFile && file.name.lowercase().endsWith(".jar") } ?: return arrayOf()
		files.sortBy { it.name }
		val result: Array<PluginRegistration?> = arrayOfNulls<PluginRegistration?>(files.size)
		for (i in 0 until files.size) {
			result[i] = loadJar(files[i])
		}
		@Suppress("UNCHECKED_CAST")
		return result as Array<PluginRegistration>
	}

	/** The registration for one jar: its plugin, or the reason it has none. */
	@JvmStatic
	@Suppress("TooGenericExceptionCaught") // a jar may fail to load in any way (a linkage error, a throwing constructor): the plugin is refused, not the client
	fun loadJar(file: File): PluginRegistration {
		var descriptor: PluginDescriptor? = null
		var loader: PluginClassLoader? = null
		try {
			val manifest: PluginManifest
			JarFile(file).use { jar ->
				manifest = PluginManifest.read(jar.manifest?.mainAttributes)
				val entries: Enumeration<JarEntry> = jar.entries()
				while (entries.hasMoreElements()) {
					val name: String = entries.nextElement().name
					for (prefix in FORBIDDEN_PREFIXES) {
						if (name.startsWith(prefix)) {
							throw PluginLoadException("the jar carries " + name + ": a plugin must not bundle the client, its API or the Kotlin standard library (use compileOnly)")
						}
					}
				}
			}
			descriptor = manifest.descriptor
			loader = PluginClassLoader(file, PluginLoader::class.java.classLoader)
			val constructor: Constructor<*> = pluginConstructor(manifest.className, loader)
			val registration: PluginRegistration = PluginRegistration(manifest.descriptor, null, false, file.name)
			registration.factory = PluginFactory { create(constructor) }
			registration.loader = loader
			return registration
		} catch (error: PluginLoadException) {
			return failed(file, error.descriptor ?: descriptor, loader, error.message ?: "cannot be loaded")
		} catch (error: ClassNotFoundException) {
			return failed(file, descriptor, loader, "the class " + error.message + " is not in the jar")
		} catch (ignored: NoSuchMethodException) {
			return failed(file, descriptor, loader, "the plugin class has no public constructor without arguments")
		} catch (error: Throwable) {
			System.err.println("Plugin " + file.name + " cannot be loaded:")
			System.err.println(error.stackTraceToString())
			return failed(file, descriptor, loader, error.toString())
		}
	}

	/** The public no-argument constructor of the plugin class [className], without initialising the class. */
	private fun pluginConstructor(className: String, loader: PluginClassLoader): Constructor<*> {
		val pluginClass: Class<*> = Class.forName(className, false, loader)
		if (!Plugin::class.java.isAssignableFrom(pluginClass)) {
			throw PluginLoadException(className + " does not implement " + Plugin::class.java.name)
		}
		if (!Modifier.isPublic(pluginClass.modifiers)) {
			throw PluginLoadException(className + " is not public")
		}
		return pluginClass.getConstructor()
	}

	/** The plugin's object, when it is first turned on; what its constructor or class initialiser throws comes through as itself. */
	private fun create(constructor: Constructor<*>): Plugin {
		try {
			return constructor.newInstance() as Plugin
		} catch (error: InvocationTargetException) {
			throw error.cause ?: error
		}
	}

	/** A registration that says why [file] has no plugin, with its class loader closed. */
	private fun failed(file: File, descriptor: PluginDescriptor?, loader: PluginClassLoader?, reason: String): PluginRegistration {
		try {
			loader?.close()
		} catch (error: java.io.IOException) {
			System.err.println("Plugins: cannot close the loader of " + file.name + ": " + error)
		}
		val registration: PluginRegistration = PluginRegistration(descriptor ?: placeholder(file), null, false, file.name)
		registration.state = PluginRegistration.FAILED
		registration.error = reason
		return registration
	}

	/** A descriptor for a jar whose manifest could not be read, named after the file. */
	private fun placeholder(file: File): PluginDescriptor {
		return PluginDescriptor("file:" + file.name, file.name, "?", PluginApi.VERSION, "", "")
	}
}
