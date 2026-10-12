package com.jagex.game.runetek5.cache.node.lru

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.periodic.PulseEventHandler
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!et")
class PulseEvent @OriginalMember(owner = "client!et", name = "<init>", descriptor = "(II)V") constructor(@OriginalArg(0) type: Int, @OriginalArg(1) id: Int) : CacheableNode() {

	@OriginalMember(owner = "client!et", name = "A", descriptor = "I")
	@JvmField
	var tertiary: Int = 0

	@OriginalMember(owner = "client!et", name = "B", descriptor = "I")
	@JvmField
	var secondary: Int = 0

	@OriginalMember(owner = "client!et", name = "C", descriptor = "I")
	@JvmField
	var primary: Int = 0

	@OriginalMember(owner = "client!et", name = "E", descriptor = "Ljava/lang/String;")
	@JvmField
	var string: String? = null

	init {
		super.key = (type.toLong() shl 32) or id.toLong()
	}

	@OriginalMember(owner = "client!et", name = "b", descriptor = "(Z)I")
	fun getType(): Int {
		return ((super.key ushr 32) and 0xFFL).toInt()
	}

	@OriginalMember(owner = "client!et", name = "a", descriptor = "(I)V")
	fun queueHighPriority() {
		super.indexReference = super.indexReference or Long.MIN_VALUE
		if (this.executeAfter() == 0L) {
			PulseEventHandler.HIGH_PRIORITY.insert(this)
		}
	}

	@OriginalMember(owner = "client!et", name = "e", descriptor = "(I)I")
	fun getId(): Int {
		return super.key.toInt()
	}

	@OriginalMember(owner = "client!et", name = "e", descriptor = "(B)J")
	fun executeAfter(): Long {
		return super.indexReference and Long.MAX_VALUE
	}

	@OriginalMember(owner = "client!et", name = "f", descriptor = "(I)V")
	fun queueLowPriority() {
		super.indexReference = (Long.MIN_VALUE and super.indexReference) or (Utils.currentTimeMillis() + 500L)
		PulseEventHandler.LOW_PRIORITY.insert(this)
	}

	companion object {
		@OriginalMember(owner = "client!au", name = "a", descriptor = "(IIZ)V")
		@JvmStatic
		fun priorityIfGraphicUpdate(@OriginalArg(0) idHash: Int, @OriginalArg(1) graphicId: Int) {
			@Pc(8) val pulseEvent: PulseEvent? = PulseEventHandler.createPulseEvent(idHash, 14)
			pulseEvent!!.queueHighPriority()
			pulseEvent.primary = graphicId
		}

		@OriginalMember(owner = "client!hc", name = "d", descriptor = "(III)V")
		@JvmStatic
		fun priorityClientvarcUpdate(@OriginalArg(0) varId: Int, @OriginalArg(2) value: Int) {
			@Pc(12) val pulseEvent: PulseEvent? = PulseEventHandler.createPulseEvent(varId, 1)
			pulseEvent!!.queueHighPriority()
			pulseEvent.primary = value
		}

		@OriginalMember(owner = "client!jaa", name = "b", descriptor = "(BI)V")
		@JvmStatic
		fun delayedIfPositionUpdate(@OriginalArg(1) componentHash: Int) {
			@Pc(15) val pulseEvent: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 11)
			pulseEvent!!.queueLowPriority()
		}

		@OriginalMember(owner = "client!il", name = "a", descriptor = "(ZII)V")
		@JvmStatic
		fun priorityIfHideUpdate(@OriginalArg(1) hidden: Int, @OriginalArg(2) componentHash: Int) {
			@Pc(8) val pulseEvent: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 7)
			pulseEvent!!.queueHighPriority()
			pulseEvent.primary = hidden
		}
		@OriginalMember(owner = "client!rea", name = "a", descriptor = "(II)V")
		@JvmStatic
		fun delayedIfGraphicUpdate(@OriginalArg(0) idHash: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(idHash, 14)
			event!!.queueLowPriority()
		}

		@OriginalMember(owner = "client!nga", name = "a", descriptor = "(IIBII)V")
		@JvmStatic
		fun priorityIfangleUpdate(@OriginalArg(0) zoom: Int, @OriginalArg(1) rotationX: Int, @OriginalArg(3) rotationY: Int, @OriginalArg(4) componentHash: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 8)
			event!!.queueHighPriority()
			event.primary = rotationX
			event.tertiary = zoom
			event.secondary = rotationY
		}

		@OriginalMember(owner = "client!ro", name = "a", descriptor = "(III)V")
		@JvmStatic
		fun priorityT13Update(@OriginalArg(1) componentHash: Int, @OriginalArg(2) value: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 13)
			event!!.queueHighPriority()
			event.primary = value
		}

		@OriginalMember(owner = "client!rl", name = "b", descriptor = "(II)V")
		@JvmStatic
		fun delayedIfScrollPosUpdate(@OriginalArg(1) idHash: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(idHash, 12)
			event!!.queueLowPriority()
		}
		@OriginalMember(owner = "client!tt", name = "a", descriptor = "(ILjava/lang/String;I)V")
		@JvmStatic
		fun priorityIfTextUpdate(@OriginalArg(0) componentHash: Int, @OriginalArg(1) text: String?) {
			@Pc(15) val event: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 3)
			event!!.queueHighPriority()
			event.string = text
		}

		@OriginalMember(owner = "client!paa", name = "a", descriptor = "(BI)V")
		@JvmStatic
		fun delayedIfAnimUpdate(@OriginalArg(1) idHash: Int) {
			@Pc(16) val event: PulseEvent? = PulseEventHandler.createPulseEvent(idHash, 5)
			event!!.queueLowPriority()
		}

		@OriginalMember(owner = "client!rc", name = "b", descriptor = "(II)V")
		@JvmStatic
		fun delayedIfOffsetUpdate(@OriginalArg(0) idHash: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(idHash, 10)
			event!!.queueLowPriority()
		}

		@OriginalMember(owner = "client!ua", name = "b", descriptor = "(III)V")
		@JvmStatic
		fun priorityIfscrollposUpdate(@OriginalArg(0) componentHash: Int, @OriginalArg(2) scrollPos: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 12)
			event!!.queueHighPriority()
			event.primary = scrollPos
		}

		@OriginalMember(owner = "client!qda", name = "a", descriptor = "(Z)V")
		@JvmStatic
		fun delayedMinimapFlagUpdate() {
			@Pc(13) val event: PulseEvent? = PulseEventHandler.createPulseEvent(0, 15)
			event!!.queueLowPriority()
		}

		@OriginalMember(owner = "client!sm", name = "a", descriptor = "(II)V")
		@JvmStatic
		fun delayedVarcUpdate(@OriginalArg(1) varcId: Int) {
			@Pc(13) val event: PulseEvent? = PulseEventHandler.createPulseEvent(varcId, 1)
			event!!.queueLowPriority()
		}

		@OriginalMember(owner = "client!bi", name = "a", descriptor = "(II)V")
		@JvmStatic
		fun delayedIfColourUpdate(@OriginalArg(0) idHash: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(idHash, 6)
			event!!.queueLowPriority()
		}

		@OriginalMember(owner = "client!mea", name = "i", descriptor = "Lclient!eq;")
		@JvmField
		val aClass99_48: IterableHashTable = IterableHashTable(16)

		@OriginalMember(owner = "client!qi", name = "a", descriptor = "(II)V")
		@JvmStatic
		fun delayedIfVideoUpdate(@OriginalArg(0) idHash: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(idHash, 17)
			event!!.queueLowPriority()
		}

		@OriginalMember(owner = "client!hq", name = "a", descriptor = "(III)V")
		@JvmStatic
		fun priorityIfFontUpdate(@OriginalArg(0) fontId: Int, @OriginalArg(2) idHash: Int) {
			@Pc(13) val event: PulseEvent? = PulseEventHandler.createPulseEvent(idHash, 16)
			event!!.queueHighPriority()
			event.primary = fontId
		}

		@OriginalMember(owner = "client!ss", name = "a", descriptor = "(IIIII)V")
		@JvmStatic
		fun priorityIfcontentUpdate(@OriginalArg(0) modelType: Int, @OriginalArg(1) componentHash: Int, @OriginalArg(2) primaryModelId: Int, @OriginalArg(4) secondaryModelId: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 4)
			event!!.queueHighPriority()
			event.secondary = primaryModelId
			event.primary = modelType
			event.tertiary = secondaryModelId
		}

		@OriginalMember(owner = "client!ic", name = "a", descriptor = "(IIIII)V")
		@JvmStatic
		fun priorityIfOffsetUpdate(@OriginalArg(0) offsetX: Int, @OriginalArg(1) offsetY: Int, @OriginalArg(3) idHash: Int, @OriginalArg(4) rotationZ: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(idHash, 10)
			event!!.queueHighPriority()
			event.secondary = offsetY
			event.tertiary = rotationZ
			event.primary = offsetX
		}
		@OriginalMember(owner = "client!pn", name = "a", descriptor = "(III)V")
		@JvmStatic
		fun priorityIfAnimUpdate(@OriginalArg(0) animId: Int, @OriginalArg(2) componentHash: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 5)
			event!!.queueHighPriority()
			event.primary = animId
		}

		@OriginalMember(owner = "client!pr", name = "e", descriptor = "(II)V")
		@JvmStatic
		fun delayedIfAngleUpdate(@OriginalArg(0) idHash: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(idHash, 8)
			event!!.queueLowPriority()
		}

		@OriginalMember(owner = "client!bga", name = "a", descriptor = "(Ljava/lang/String;II)V")
		@JvmStatic
		fun priorityVarcstringUpdate(@OriginalArg(0) value: String?, @OriginalArg(1) varId: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(varId, 2)
			event!!.queueHighPriority()
			event.string = value
		}

		@OriginalMember(owner = "client!pba", name = "a", descriptor = "(IBI)V")
		@JvmStatic
		fun priorityIfVideoUpdate(@OriginalArg(0) videoId: Int, @OriginalArg(2) componentHash: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 17)
			event!!.queueHighPriority()
			event.primary = videoId
		}

		@OriginalMember(owner = "client!mj", name = "a", descriptor = "(BI)V")
		@JvmStatic
		fun delayedIfHideUpdate(@OriginalArg(1) componentHash: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 7)
			event!!.queueLowPriority()
		}
		@OriginalMember(owner = "client!kk", name = "b", descriptor = "(II)V")
		@JvmStatic
		fun delayedIfItemUpdate(@OriginalArg(1) componentHash: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 9)
			event!!.queueLowPriority()
		}

		@OriginalMember(owner = "client!cf", name = "b", descriptor = "(BI)V")
		@JvmStatic
		fun delayedIfFontUpdate(@OriginalArg(1) componentHash: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 16)
			event!!.queueLowPriority()
		}

		@OriginalMember(owner = "client!fn", name = "a", descriptor = "(II)V")
		@JvmStatic
		fun delayedIfContentUpdate(@OriginalArg(0) componentHash: Int) {
			@Pc(16) val event: PulseEvent? = PulseEventHandler.createPulseEvent(componentHash, 4)
			event!!.queueLowPriority()
		}

		@OriginalMember(owner = "client!eea", name = "a", descriptor = "(III)V")
		@JvmStatic
		fun priorityMinimapFlagUpdate(@OriginalArg(1) flagY: Int, @OriginalArg(2) flagX: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(0, 15)
			event!!.queueHighPriority()
			event.primary = flagX
			event.secondary = flagY
		}
		@OriginalMember(owner = "client!li", name = "a", descriptor = "(III)V")
		@JvmStatic
		fun priorityIfColourUpdate(@OriginalArg(1) colour: Int, @OriginalArg(2) idHash: Int) {
			@Pc(8) val event: PulseEvent? = PulseEventHandler.createPulseEvent(idHash, 6)
			event!!.queueHighPriority()
			event.primary = colour
		}

		@OriginalMember(owner = "client!b", name = "a", descriptor = "(IZ)V")
		@JvmStatic
		fun delayedVarcStringUpdate(@OriginalArg(0) varcId: Int) {
			@Pc(13) val event: PulseEvent? = PulseEventHandler.createPulseEvent(varcId, 2)
			event!!.queueLowPriority()
		}
	}
}
