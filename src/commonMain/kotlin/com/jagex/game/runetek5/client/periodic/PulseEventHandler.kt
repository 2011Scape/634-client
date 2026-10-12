package com.jagex.game.runetek5.client.periodic

import com.jagex.game.runetek5.cache.node.Queue
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.cache.node.lru.PulseEvent
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object PulseEventHandler {
	@OriginalMember(owner = "client!bk", name = "z", descriptor = "Lclient!mn;")
	@JvmField
	val HIGH_PRIORITY: Queue = Queue()

	@OriginalMember(owner = "client!ed", name = "r", descriptor = "Lclient!mn;")
	@JvmField
	val LOW_PRIORITY: Queue = Queue()

	@OriginalMember(owner = "client!dia", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun clearAllPulseEvents() {
		PulseEvent.aClass99_48.clear()
		PulseEventHandler.LOW_PRIORITY.clear()
		PulseEventHandler.HIGH_PRIORITY.clear()
	}

	@OriginalMember(owner = "client!vj", name = "f", descriptor = "(I)Lclient!et;")
	@JvmStatic
	fun getNextExecutedEvent(): PulseEvent? {
		@Pc(13) var event: PulseEvent? = PulseEventHandler.HIGH_PRIORITY.first() as PulseEvent?
		if (event != null) {
			event.unlink()
			event.unlinkSecondary()
			return event
		}
		do {
			event = PulseEventHandler.LOW_PRIORITY.first() as PulseEvent?
			if (event == null) {
				return null
			}
			if (event.executeAfter() > Utils.currentTimeMillis()) {
				return null
			}
			event.unlink()
			event.unlinkSecondary()
		} while ((Long.MIN_VALUE and event!!.indexReference) == 0L)
		return event
	}

	@OriginalMember(owner = "client!ev", name = "a", descriptor = "(IBI)Lclient!et;")
	@JvmStatic
	fun createPulseEvent(@OriginalArg(0) id: Int, @OriginalArg(2) type: Int): PulseEvent? {
		@Pc(17) var event: PulseEvent? = PulseEvent.aClass99_48.get(type.toLong() shl 32 or id.toLong()) as PulseEvent?
		if (event == null) {
			event = PulseEvent(type, id)
			PulseEvent.aClass99_48.put(event.key, event)
		}
		return event
	}
}
