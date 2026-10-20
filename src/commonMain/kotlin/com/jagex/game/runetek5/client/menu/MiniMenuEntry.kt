package com.jagex.game.runetek5.client.menu

import com.jagex.game.runetek5.cache.node.lru.CacheableNode
import com.jagex.game.runetek5.plugin.api.MenuEntry
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!db")
class MiniMenuEntry : CacheableNode, MenuEntry {

	/* Not part of the original client: the plugin API's view of an entry (plugin.api.MenuEntry). */

	override fun option(): String? {
		return this.opBase
	}

	override fun target(): String? {
		return this.op
	}

	override fun clickType(): Int {
		return this.clickType
	}

	override fun param1(): Int {
		return this.param1
	}

	override fun param0(): Int {
		return this.param0
	}

	override fun itemId(): Int {
		return this.itemId
	}

	override fun entityUid(): Long {
		return this.entityUid
	}

	@OriginalMember(owner = "client!db", name = "y", descriptor = "Ljava/lang/String;")
	@JvmField
	var targetName: String? = null

	@OriginalMember(owner = "client!db", name = "M", descriptor = "I")
	@JvmField
	val param1: Int

	@OriginalMember(owner = "client!db", name = "N", descriptor = "I")
	@JvmField
	var clickType: Int

	@OriginalMember(owner = "client!db", name = "D", descriptor = "I")
	@JvmField
	val itemId: Int

	@OriginalMember(owner = "client!db", name = "J", descriptor = "J")
	@JvmField
	val entityUid: Long

	@OriginalMember(owner = "client!db", name = "Q", descriptor = "Z")
	@JvmField
	val showOtherLevelIcon: Boolean

	@OriginalMember(owner = "client!db", name = "B", descriptor = "Z")
	@JvmField
	val isSelectable: Boolean

	@OriginalMember(owner = "client!db", name = "F", descriptor = "Ljava/lang/String;")
	@JvmField
	val op: String?

	@OriginalMember(owner = "client!db", name = "R", descriptor = "Z")
	@JvmField
	val isUncategorized: Boolean

	@OriginalMember(owner = "client!db", name = "w", descriptor = "Ljava/lang/String;")
	@JvmField
	var opBase: String? // Not part of the original client: a var, so that a plugin can rename an entry (PluginRuntime.renameMenuEntry)

	@OriginalMember(owner = "client!db", name = "P", descriptor = "I")
	@JvmField
	val cursor: Int

	@OriginalMember(owner = "client!db", name = "E", descriptor = "J")
	@JvmField
	val bitpacked: Long

	@OriginalMember(owner = "client!db", name = "G", descriptor = "I")
	@JvmField
	val param0: Int

	@OriginalMember(owner = "client!db", name = "<init>", descriptor = "(Ljava/lang/String;Ljava/lang/String;IIIJIIZZJZ)V")
	constructor(@OriginalArg(0) string: String?, @OriginalArg(1) string2: String?, @OriginalArg(2) cursor: Int, @OriginalArg(3) clickType: Int, @OriginalArg(4) itemId: Int, @OriginalArg(5) entityUid: Long, @OriginalArg(6) param0: Int, @OriginalArg(7) param1: Int, @OriginalArg(8) showOtherLevelIcon: Boolean, @OriginalArg(9) isSelectable: Boolean, @OriginalArg(10) bitpacked: Long, @OriginalArg(11) isUncategorized: Boolean) : super() {
		this.param1 = param1
		this.clickType = clickType
		this.itemId = itemId
		this.entityUid = entityUid
		this.showOtherLevelIcon = showOtherLevelIcon
		this.isSelectable = isSelectable
		this.op = string2
		this.isUncategorized = isUncategorized
		this.opBase = string
		this.cursor = cursor
		this.bitpacked = bitpacked
		this.param0 = param0
	}
}
