package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.plugin.api.events.EventBus
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.EventType
import com.jagex.game.runetek5.plugin.api.events.Events
import lang.Runnable
import kotlin.jvm.JvmField

/**
 * Not part of the original client: the client's event bus. One array of listeners per event type,
 * grown on subscribe, so that posting allocates nothing; a listener that throws is reported to
 * [onFailure] (the runtime disables its plugin) and the others still run. Unsubscribing during a
 * post leaves a hole that is closed when no post is running. Game thread only.
 */
class EventBusImpl : EventBus {

	/** Reports a listener that threw, with the plugin that owns it. */
	fun interface FailureHandler {
		fun failed(owner: PluginRegistration?, error: Throwable)
	}

	private val listeners: Array<Array<Any?>> = Array(Events.COUNT) { arrayOfNulls<Any?>(4) }

	private val owners: Array<Array<PluginRegistration?>> = Array(Events.COUNT) { arrayOfNulls<PluginRegistration?>(4) }

	private val counts: IntArray = IntArray(Events.COUNT)

	private var depth: Int = 0

	private var dirty: Boolean = false

	@JvmField
	var onFailure: FailureHandler? = null

	/** Sees the plugin whose listener is about to run, when set: the runtime shapes an event to the API version the plugin declared. */
	fun interface ListenerHook {
		fun before(owner: PluginRegistration?)
	}

	/** Whether the plugin's listener gets this post, when set: the runtime keeps from a plugin what its API version did not have. */
	fun interface ListenerFilter {
		fun accepts(owner: PluginRegistration?): Boolean
	}

	@JvmField
	var listenerFilter: ListenerFilter? = null

	@JvmField
	var beforeListener: ListenerHook? = null

	/** Runs after each listener of a post, when set: the runtime puts the drawing state back between draw listeners. */
	@JvmField
	var afterListener: Runnable? = null

	/** The plugin whose context is subscribing, so that the listener is removed with it. */
	override fun <E> subscribe(type: EventType<E>, listener: EventListener<E>) {
		subscribe(type, listener, null)
	}

	fun <E> subscribe(type: EventType<E>, listener: EventListener<E>, owner: PluginRegistration?) {
		val id: Int = type.id
		// only the client's own types: a type made up by a plugin, with the id of another, would hand its
		// listeners the wrong class of event
		if (id < 0 || id >= Events.COUNT || Events.ALL[id] !== type) {
			return
		}
		var list: Array<Any?> = listeners[id]
		var own: Array<PluginRegistration?> = owners[id]
		val n: Int = counts[id]
		if (n == list.size) {
			val grownList: Array<Any?> = arrayOfNulls<Any?>(n * 2)
			val grownOwners: Array<PluginRegistration?> = arrayOfNulls<PluginRegistration?>(n * 2)
			for (i in 0 until n) {
				grownList[i] = list[i]
				grownOwners[i] = own[i]
			}
			list = grownList
			own = grownOwners
			listeners[id] = list
			owners[id] = own
		}
		list[n] = listener
		own[n] = owner
		counts[id] = n + 1
	}

	override fun <E> unsubscribe(type: EventType<E>, listener: EventListener<E>) {
		val id: Int = type.id
		if (id < 0 || id >= Events.COUNT) {
			return
		}
		val list: Array<Any?> = listeners[id]
		for (i in 0 until counts[id]) {
			if (list[i] === listener) {
				list[i] = null
				owners[id][i] = null
				dirty = true
			}
		}
		if (depth == 0) {
			compact()
		}
	}

	/** Removes every listener [owner] subscribed. */
	fun removeOwner(owner: PluginRegistration) {
		for (id in 0 until Events.COUNT) {
			val own: Array<PluginRegistration?> = owners[id]
			for (i in 0 until counts[id]) {
				if (own[i] === owner) {
					listeners[id][i] = null
					own[i] = null
					dirty = true
				}
			}
		}
		if (depth == 0) {
			compact()
		}
	}

	fun listenerCount(type: EventType<*>): Int {
		var n: Int = 0
		val list: Array<Any?> = listeners[type.id]
		for (i in 0 until counts[type.id]) {
			if (list[i] != null) {
				n++
			}
		}
		return n
	}

	/** Whether anyone listens, so that a hook can skip filling in an event nobody reads. */
	fun hasListeners(type: EventType<*>): Boolean {
		return counts[type.id] > 0
	}

	@Suppress("UNCHECKED_CAST", "TooGenericExceptionCaught") // whatever a listener throws, its plugin is disabled and the game goes on
	fun <E> post(type: EventType<E>, event: E) {
		val id: Int = type.id
		val n: Int = counts[id]
		if (n == 0) {
			return
		}
		depth++
		var i: Int = 0
		while (i < n) {
			// read from the arrays as they are now, not a copy: a subscribe during the post may have grown
			// them, and an unsubscribe after that writes its hole into the new ones
			val listener: Any? = listeners[id][i]
			if (listener != null && listenerFilter?.accepts(owners[id][i]) != false) {
				beforeListener?.before(owners[id][i])
				try {
					(listener as EventListener<E>).on(event)
				} catch (error: Throwable) {
					val handler: FailureHandler? = onFailure
					if (handler != null) {
						handler.failed(owners[id][i], error)
					} else {
						lang.printlnErr("Plugins: a listener for " + type.name + " threw " + error)
					}
				}
				afterListener?.run()
			}
			i++
		}
		depth--
		if (depth == 0 && dirty) {
			compact()
		}
	}

	private fun compact() {
		dirty = false
		for (id in 0 until Events.COUNT) {
			val list: Array<Any?> = listeners[id]
			val own: Array<PluginRegistration?> = owners[id]
			var kept: Int = 0
			for (i in 0 until counts[id]) {
				if (list[i] != null) {
					list[kept] = list[i]
					own[kept] = own[i]
					kept++
				}
			}
			for (i in kept until counts[id]) {
				list[i] = null
				own[i] = null
			}
			counts[id] = kept
		}
	}
}
