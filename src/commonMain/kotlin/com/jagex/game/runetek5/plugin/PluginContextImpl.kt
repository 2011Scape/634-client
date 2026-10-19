package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.events.EventBus
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.EventType

/**
 * Not part of the original client: what one plugin is given. Everything it registers is tagged
 * with its registration, so that stopping the plugin removes it all.
 */
class PluginContextImpl(private val registration: PluginRegistration) : PluginContext {

	private val config: PluginConfigImpl = PluginConfigImpl(registration.descriptor.id)

	private val events: EventBus = object : EventBus {
		override fun <E> subscribe(type: EventType<E>, listener: EventListener<E>) {
			PluginRuntime.bus.subscribe(type, listener, registration)
		}

		override fun <E> unsubscribe(type: EventType<E>, listener: EventListener<E>) {
			PluginRuntime.bus.unsubscribe(type, listener)
		}
	}

	override fun descriptor(): PluginDescriptor {
		return registration.descriptor
	}

	override fun client(): ClientApi {
		return ClientApiImpl
	}

	override fun events(): EventBus {
		return events
	}

	override fun config(): PluginConfig {
		return config
	}

	override fun addOverlay(overlay: Overlay) {
		PluginRuntime.addOverlay(registration, overlay)
	}

	override fun removeOverlay(overlay: Overlay) {
		PluginRuntime.removeOverlay(overlay)
	}

	override fun registerCommand(name: String, handler: CommandHandler): Boolean {
		return PluginRuntime.registerCommand(registration, name, handler)
	}

	override fun registerMenuAction(action: MenuAction): Int {
		return PluginRuntime.registerMenuAction(registration, action)
	}

	override fun log(message: String) {
		lang.printlnErr("[" + registration.descriptor.id + "] " + message)
	}
}
