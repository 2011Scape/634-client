package com.jagex.game.runetek5.config.rsinterface

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable

@OriginalClass("client!wb")
class IFEvents : Node {

	@OriginalMember(owner = "client!wb", name = "q", descriptor = "I")
	@JvmField
	val interfaceId: Int

	@OriginalMember(owner = "client!wb", name = "v", descriptor = "I")
	@JvmField
	val settingsHash: Int

	@OriginalMember(owner = "client!wb", name = "<init>", descriptor = "(II)V")
	constructor(@OriginalArg(0) events: Int, @OriginalArg(1) targetParam: Int) : super() {
		this.interfaceId = targetParam
		this.settingsHash = events
	}

	@OriginalMember(owner = "client!wb", name = "a", descriptor = "(IZ)Z")
	fun isOpEnabled(@OriginalArg(0) op: Int): Boolean {
		return (this.settingsHash shr op + 1 and 0x1) != 0
	}

	@OriginalMember(owner = "client!wb", name = "a", descriptor = "(I)Z")
	fun dragEnabled(): Boolean {
		return (this.settingsHash shr 21 and 0x1) != 0
	}

	@OriginalMember(owner = "client!wb", name = "b", descriptor = "(I)Z")
	fun isTargetableByUse(): Boolean {
		return (this.settingsHash shr 22 and 0x1) != 0
	}

	@OriginalMember(owner = "client!wb", name = "a", descriptor = "(B)I")
	fun getDragDepth(): Int {
		return this.settingsHash shr 18 and 0x7
	}

	@OriginalMember(owner = "client!wb", name = "d", descriptor = "(I)Z")
	fun continueOptionEnabled(): Boolean {
		return (this.settingsHash and 0x1) != 0
	}

	@OriginalMember(owner = "client!wb", name = "e", descriptor = "(I)I")
	fun getUseOptionFlags(): Int {
		return IFEvents.getUseOptionFlags(this.settingsHash)
	}

	companion object {
		@OriginalMember(owner = "client!hn", name = "a", descriptor = "(IZ)I")
		@JvmStatic
		fun getUseOptionFlags(@OriginalArg(0) settingsHash: Int): Int {
			return settingsHash shr 11 and 0x7F
		}

		@OriginalMember(owner = "client!sga", name = "a", descriptor = "Lclient!eq;")
		@JvmField
		val ICOMPONENT_SETTINGS_SLOTS: IterableHashTable = IterableHashTable(512)
	}
}
