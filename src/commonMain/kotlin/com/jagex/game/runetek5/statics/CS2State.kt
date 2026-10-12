package com.jagex.game.runetek5.statics

import com.jagex.game.runetek5.QuickChatPhrase
import com.jagex.game.runetek5.StackFrame
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.utils.Utils
import java.util.Calendar
import java.util.Date
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object CS2State {

	@OriginalMember(owner = "client!kca", name = "b", descriptor = "Lclient!at;")
	@JvmField
	var activeComponent: Component? = null

	@OriginalMember(owner = "client!kca", name = "c", descriptor = "Lclient!iia;")
	@JvmField
	var currentQuickChatPhrase: QuickChatPhrase? = null

	@OriginalMember(owner = "client!kca", name = "u", descriptor = "Lclient!at;")
	@JvmField
	var activeComponent2: Component? = null

	@OriginalMember(owner = "client!kca", name = "d", descriptor = "[Ljava/lang/String;")
	@JvmField
	val stringStack: Array<String?> = arrayOfNulls<String>(1000)

	@OriginalMember(owner = "client!kca", name = "f", descriptor = "I")
	@JvmField
	var framePtr: Int = 0

	@OriginalMember(owner = "client!kca", name = "l", descriptor = "Ljava/util/Calendar;")
	@JvmField
	val LOCAL_CALENDAR: Calendar = Calendar.getInstance()

	@OriginalMember(owner = "client!kca", name = "t", descriptor = "[Lclient!qea;")
	@JvmField
	val frames: Array<StackFrame?> = arrayOfNulls<StackFrame>(50)

	@OriginalMember(owner = "client!kca", name = "v", descriptor = "I")
	@JvmField
	var stringStackPtr: Int = 0

	@OriginalMember(owner = "client!kca", name = "x", descriptor = "[I")
	@JvmField
	val globalArrayLengths: IntArray = IntArray(5)

	@OriginalMember(owner = "client!kca", name = "y", descriptor = "I")
	@JvmField
	var intStackPtr: Int = 0

	@OriginalMember(owner = "client!kca", name = "z", descriptor = "Lclient!jr;")
	@JvmField
	val aClass169_33: LRUCache = LRUCache(4)

	@OriginalMember(owner = "client!kca", name = "A", descriptor = "[I")
	@JvmField
	val coordConversionBuffer: IntArray = IntArray(3)

	@OriginalMember(owner = "client!kca", name = "B", descriptor = "[Ljava/lang/String;")
	private val monthNames: Array<String?> = arrayOf<String?>("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

	@OriginalMember(owner = "client!kca", name = "C", descriptor = "I")
	@JvmField
	var hookRequestCount: Int = 0

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun sendToBack(@OriginalArg(0) idHash: Int) {
		@Pc(3) val component: Component? = Interface.getComponent(idHash)
		if (component == null) {
			return
		}
		@Pc(9) val interfaceId: Int = idHash ushr 16
		@Pc(13) var components: Array<Component?>? = Interface.aClass24ArrayArray1!![interfaceId]
		if (components == null) {
			@Pc(19) val components2: Array<Component?> = Interface.interfaces!![interfaceId]!!
			@Pc(22) val length: Int = components2.size
			components = arrayOfNulls<Component>(length)
			Interface.aClass24ArrayArray1!![interfaceId] = components
			Arrays.copy(components2, 0, components, 0, components2.size)
		}
		@Pc(38) var index: Int = 0
		while (index < components.size && components[index] !== component) {
			index++
		}
		if (index >= components.size) {
			return
		}
		Arrays.copy(components, 0, components, 1, index)
		components[0] = component
	}

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(C)I")
	@JvmStatic
	fun isPrintableChar(@OriginalArg(0) c: Char): Int {
		return if (Utils.isPrintableChar(c)) 1 else 0
	}

	@OriginalMember(owner = "client!kca", name = "c", descriptor = "(I)Ljava/lang/String;")
	@JvmStatic
	fun runedayToDateString(@OriginalArg(0) runeday: Int): String {
		@Pc(6) val millis: Long = (runeday.toLong() + 11745L) * 86400000L
		LOCAL_CALENDAR.setTime(Date(millis))
		@Pc(16) val day: Int = LOCAL_CALENDAR.get(5)
		@Pc(20) val month: Int = LOCAL_CALENDAR.get(2)
		@Pc(24) val year: Int = LOCAL_CALENDAR.get(1)
		return day.toString() + "-" + monthNames[month] + "-" + year
	}

	@OriginalMember(owner = "client!kca", name = "d", descriptor = "(I)V")
	@JvmStatic
	fun sendToFront(@OriginalArg(0) idHash: Int) {
		@Pc(3) val component: Component? = Interface.getComponent(idHash)
		if (component == null) {
			return
		}
		@Pc(9) val interfaceId: Int = idHash ushr 16
		@Pc(13) var components: Array<Component?>? = Interface.aClass24ArrayArray1!![interfaceId]
		if (components == null) {
			@Pc(19) val components2: Array<Component?> = Interface.interfaces!![interfaceId]!!
			@Pc(22) val length: Int = components2.size
			components = arrayOfNulls<Component>(length)
			Interface.aClass24ArrayArray1!![interfaceId] = components
			Arrays.copy(components2, 0, components, 0, components2.size)
		}
		@Pc(38) var index: Int = 0
		while (index < components.size && components[index] !== component) {
			index++
		}
		if (index >= components.size) {
			return
		}
		Arrays.copy(components, index + 1, components, index, components.size - index - 1)
		components[components.size - 1] = component
	}

	@OriginalMember(owner = "client!kca", name = "c", descriptor = "(IZ)V")
	@JvmStatic
	fun profileOutput() {
	}
}
