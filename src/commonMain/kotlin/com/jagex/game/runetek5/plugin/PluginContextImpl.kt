package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.PluginManagerScreen
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.ColourCallback
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.MenuAction
import com.jagex.game.runetek5.plugin.api.Overlay
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.events.EventBus
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.EventType
import kotlin.jvm.JvmField

/**
 * Not part of the original client: what one plugin is given. Everything it registers is tagged
 * with its registration, so that stopping the plugin removes it all; once stopped, the context is
 * [closed] and registers nothing more, so a plugin that kept it cannot come back to life.
 */
class PluginContextImpl(private val registration: PluginRegistration) : PluginContext {

	/** Set when the plugin is stopped or disabled: every registration through this context is refused from then on. */
	@JvmField
	var closed: Boolean = false

	private val config: PluginConfigImpl = PluginConfigImpl(registration.descriptor.id)

	/** The settings the plugin declared, for the plugin manager. */
	@JvmField
	val schema: PluginSchemaImpl = PluginSchemaImpl()

	private val events: EventBus = object : EventBus {
		override fun <E> subscribe(type: EventType<E>, listener: EventListener<E>) {
			if (closed) {
				return
			}
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
		if (closed) {
			return
		}
		PluginRuntime.addOverlay(registration, overlay)
	}

	override fun removeOverlay(overlay: Overlay) {
		PluginRuntime.removeOverlay(overlay)
	}

	override fun registerCommand(name: String, handler: CommandHandler): Boolean {
		if (closed) {
			return false
		}
		return PluginRuntime.registerCommand(registration, name, handler)
	}

	override fun registerMenuAction(action: MenuAction): Int {
		if (closed) {
			return -1
		}
		return PluginRuntime.registerMenuAction(registration, action)
	}

	override fun settings(): SettingsSchema {
		return schema
	}

	override fun log(message: String) {
		lang.printlnErr("[" + registration.descriptor.id + "] " + message)
	}

	override fun invokeLater(task: Task) {
		if (closed) {
			return
		}
		PluginRuntime.invokeLater(registration, task)
	}

	override fun schedule(ticks: Int, task: Task): Boolean {
		if (closed) {
			return false
		}
		PluginRuntime.schedule(registration, ticks, task)
		return true
	}

	override fun startThread(name: String, task: Task) {
		if (closed) {
			return
		}
		PluginRuntime.startThread(registration, name, task)
	}

	@Suppress("TooGenericExceptionCaught") // a plugin that throws is disabled and the game goes on
	override fun pickColour(initial: Int, alpha: Boolean, callback: ColourCallback): Boolean {
		if (closed) {
			return false
		}
		val why: String? = PluginManagerScreen.pickColour(initial, alpha) { argb ->
			if (!closed) {
				try {
					callback.picked(argb)
				} catch (error: Throwable) {
					PluginRuntime.fail(registration, error)
				}
			}
		}
		return why == null
	}
}
