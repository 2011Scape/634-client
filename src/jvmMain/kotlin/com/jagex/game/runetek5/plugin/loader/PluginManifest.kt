package com.jagex.game.runetek5.plugin.loader

import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import java.util.jar.Attributes

/**
 * Not part of the original client: what a plugin jar's manifest says about it (docs/PLUGINS.md).
 * `Plugin-Class` and `Plugin-Id` are needed; `Plugin-Name`, `Plugin-Version`, `Plugin-Api-Version`
 * (1 when missing), `Plugin-Author` and `Plugin-Description` are not.
 */
class PluginManifest(@JvmField val className: String, @JvmField val descriptor: PluginDescriptor) {

	companion object {
		const val CLASS: String = "Plugin-Class"
		const val ID: String = "Plugin-Id"
		const val NAME: String = "Plugin-Name"
		const val VERSION: String = "Plugin-Version"
		const val API_VERSION: String = "Plugin-Api-Version"
		const val AUTHOR: String = "Plugin-Author"
		const val DESCRIPTION: String = "Plugin-Description"
		const val DEPENDS: String = "Plugin-Depends"

		/** Reads the plugin's attributes, or throws [PluginLoadException] saying what is wrong with them. */
		@JvmStatic
		@Suppress("ThrowsCount") // each check says in words what is wrong with the manifest
		fun read(attributes: Attributes?): PluginManifest {
			if (attributes == null) {
				throw PluginLoadException("the jar has no manifest")
			}
			val className: String = attributes.getValue(CLASS)?.trim() ?: throw PluginLoadException("the manifest has no " + CLASS)
			if (className.isEmpty()) {
				throw PluginLoadException("the manifest's " + CLASS + " is empty")
			}
			val id: String = attributes.getValue(ID)?.trim() ?: throw PluginLoadException("the manifest has no " + ID)
			if (!PluginDescriptor.isValidId(id)) {
				throw PluginLoadException("the manifest's " + ID + " \"" + id + "\" is not 2 to 32 of a-z, 0-9 and -, starting with a letter")
			}
			val apiVersionText: String = attributes.getValue(API_VERSION)?.trim() ?: "1"
			val apiVersion: Int = apiVersionText.toIntOrNull() ?: throw PluginLoadException("the manifest's " + API_VERSION + " \"" + apiVersionText + "\" is not a number")
			if (apiVersion < 1) {
				throw PluginLoadException("the manifest's " + API_VERSION + " must be 1 or more")
			}
			val descriptor: PluginDescriptor = PluginDescriptor(
				id,
				attributes.getValue(NAME)?.trim() ?: id,
				attributes.getValue(VERSION)?.trim() ?: "0",
				apiVersion,
				attributes.getValue(DESCRIPTION)?.trim() ?: "",
				attributes.getValue(AUTHOR)?.trim() ?: "",
			)
			// refused with the descriptor, so that ::plugins can still name the plugin
			if (apiVersion > PluginApi.VERSION) {
				throw PluginLoadException("it needs plugin API version " + apiVersion + ", and this client has " + PluginApi.VERSION + ": update the client", descriptor)
			}
			if (attributes.getValue(DEPENDS) != null) {
				throw PluginLoadException(DEPENDS + " is not supported yet", descriptor)
			}
			return PluginManifest(className, descriptor)
		}
	}
}

/** Why a plugin jar cannot be loaded, in words for the player, with the plugin's descriptor when the manifest could be read. */
class PluginLoadException(message: String, @JvmField val descriptor: PluginDescriptor? = null) : Exception(message)
