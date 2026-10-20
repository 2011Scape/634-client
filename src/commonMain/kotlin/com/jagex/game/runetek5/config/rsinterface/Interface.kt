package com.jagex.game.runetek5.config.rsinterface

import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.InterfaceManager
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object Interface {
	@OriginalMember(owner = "client!qba", name = "D", descriptor = "I")
	@JvmField
	var preferenceMask: Int = 0

	@OriginalMember(owner = "client!gda", name = "a", descriptor = "(IB)V")
	@JvmStatic
	fun resetComponentSequences(@OriginalArg(0) interfaceId: Int) {
		if (!InterfaceManager.loadInterface(interfaceId)) {
			return
		}
		@Pc(18) val components: Array<Component?>? = Interface.interfaces!![interfaceId]
		@Pc(20) var i: Int = 0
		while (i < components!!.size) {
			@Pc(26) val component: Component? = components[i]
			if (component != null) {
				component.currentFrameId = 0
				component.nextFrameId = 1
				component.currentFrameDuration = 0
			}
			i++
		}
	}

	@OriginalMember(owner = "client!hda", name = "a", descriptor = "(IB)V")
	@JvmStatic
	fun unloadInterface(@OriginalArg(0) interfaceId: Int) {
		if (interfaceId != -1 && InterfaceManager.loadedInterfaces!![interfaceId]) {
			Resource.aClass157_255!!.discardUnpacked(interfaceId)
			Interface.interfaces!![interfaceId] = null
			Interface.aClass24ArrayArray1!![interfaceId] = null
			InterfaceManager.loadedInterfaces!![interfaceId] = false
		}
	}

	@OriginalMember(owner = "client!wj", name = "K", descriptor = "[[Lclient!at;")
	@JvmField
	var interfaces: Array<Array<Component?>?>? = null

	@OriginalMember(owner = "client!pg", name = "b", descriptor = "(II)V")
	@JvmStatic
	fun clearInterfaceSettings(@OriginalArg(1) interfaceId: Int) {
		@Pc(11) var node: Node? = IFEvents.ICOMPONENT_SETTINGS_SLOTS.first()
		while (node != null) {
			if ((node.key shr 48 and 0xFFFFL) == interfaceId.toLong()) {
				node.unlink()
			}
			node = IFEvents.ICOMPONENT_SETTINGS_SLOTS.next()
		}
	}

	/** Not part of the original client: the components of [interfaceId] as loaded, or null, loading nothing (the plugin API). */
	@JvmStatic
	fun peekComponents(interfaceId: Int): Array<Component?>? {
		val interfaces: Array<Array<Component?>?> = Interface.interfaces ?: return null
		return if (interfaceId >= 0 && interfaceId < interfaces.size) interfaces[interfaceId] else null
	}

	/** Not part of the original client: the component [idHash] as loaded, or null, loading nothing (the plugin API). */
	@JvmStatic
	fun peekComponent(idHash: Int): Component? {
		if (idHash < 0) {
			return null
		}
		val components: Array<Component?> = peekComponents(idHash ushr 16) ?: return null
		val componentId: Int = idHash and 0xFFFF
		return if (componentId < components.size) components[componentId] else null
	}

	@OriginalMember(owner = "client!da", name = "a", descriptor = "(II)Lclient!at;")
	@JvmStatic
	fun getComponent(@OriginalArg(1) idHash: Int): Component? {
		@Pc(12) val interfaceId: Int = idHash shr 16
		@Pc(16) val componentId: Int = idHash and 0xFFFF
		if (Interface.interfaces!![interfaceId] == null || Interface.interfaces!![interfaceId]!![componentId] == null) {
			@Pc(30) val loaded: Boolean = InterfaceManager.loadInterface(interfaceId)
			if (!loaded) {
				return null
			}
		}
		return Interface.interfaces!![interfaceId]!![componentId]
	}

	@OriginalMember(owner = "client!kb", name = "a", descriptor = "(Lclient!in;BLclient!in;Lclient!in;Lclient!in;)V")
	@JvmStatic
	fun init(@OriginalArg(0) fontMetricsResource: Js5ResourceProvider?, @OriginalArg(2) interfaceResource: Js5ResourceProvider?, @OriginalArg(3) packet: Js5ResourceProvider?, @OriginalArg(4) modelResource: Js5ResourceProvider?) {
		Resource.aClass157_291 = modelResource
		Component.packet = packet
		Resource.aClass157_255 = interfaceResource
		Interface.interfaces = arrayOfNulls<Array<Component?>>(Resource.aClass157_255!!.getCapacity())
		InterfaceManager.loadedInterfaces = BooleanArray(Resource.aClass157_255!!.getCapacity())
	}

	@OriginalMember(owner = "client!ou", name = "p", descriptor = "[[Lclient!at;")
	@JvmField
	var aClass24ArrayArray1: Array<Array<Component?>?>? = null
}
