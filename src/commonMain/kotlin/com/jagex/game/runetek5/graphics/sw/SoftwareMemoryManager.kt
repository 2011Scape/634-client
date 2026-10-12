package com.jagex.game.runetek5.graphics.sw

import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.cache.node.SoftwareObjectNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.jvm.Synchronized

object SoftwareMemoryManager {

	@OriginalMember(owner = "client!caa", name = "a", descriptor = "(B)V")
	@JvmStatic
	@Synchronized
	fun freePending() {
		while (true) {
			@Pc(4) val softwareObjectNode: SoftwareObjectNode? = objects.removeFirst() as SoftwareObjectNode?
			if (softwareObjectNode == null) {
				return
			}
			softwareObjectNode.`object`!!.w(true)
			softwareObjectNode.unlink()
		}
	}

	@OriginalMember(owner = "client!caa", name = "a", descriptor = "(BZ)V")
	@JvmStatic
	@Synchronized
	fun markShutdown() {
		shutdown = true
	}

	@OriginalMember(owner = "client!caa", name = "a", descriptor = "(I)V")
	@JvmStatic
	@Synchronized
	fun instanceDestroyed() {
		instances--
		if (instances == 0) {
			freePending()
		}
	}

	@OriginalMember(owner = "client!caa", name = "a", descriptor = "(ILclient!e;)V")
	@JvmStatic
	@Synchronized
	fun free(@OriginalArg(1) `object`: SoftwareObject?) {
		if (shutdown) {
			return
		}
		if (instances <= 0) {
			`object`!!.w(false)
		} else {
			@Pc(13) val node: SoftwareObjectNode = SoftwareObjectNode()
			node.`object` = `object`
			objects.addLast(node)
		}
	}

	@OriginalMember(owner = "client!caa", name = "a", descriptor = "Z")
	@JvmField
	var shutdown: Boolean = false

	@OriginalMember(owner = "client!caa", name = "b", descriptor = "(B)V")
	@JvmStatic
	@Synchronized
	fun instanceCreated() {
		instances++
	}

	@OriginalMember(owner = "client!caa", name = "b", descriptor = "Lclient!uh;")
	@JvmField
	val objects: Deque = Deque()

	@OriginalMember(owner = "client!caa", name = "c", descriptor = "I")
	@JvmField
	var instances: Int = 0
}
