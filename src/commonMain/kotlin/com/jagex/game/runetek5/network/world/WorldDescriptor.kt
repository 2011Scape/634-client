package com.jagex.game.runetek5.network.world

import com.jagex.game.runetek5.Country
import com.jagex.game.runetek5.network.WorldConnection
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!mf")
class WorldDescriptor : WorldDescriptorFlags() {

	@OriginalMember(owner = "client!mf", name = "q", descriptor = "I")
	@JvmField
	var worldNumber: Int = 0

	@OriginalMember(owner = "client!mf", name = "r", descriptor = "Ljava/lang/String;")
	@JvmField
	var address: String? = null

	@OriginalMember(owner = "client!mf", name = "y", descriptor = "Ljava/lang/String;")
	@JvmField
	var activity: String? = null

	@OriginalMember(owner = "client!mf", name = "s", descriptor = "I")
	@JvmField
	var ping: Int = -1

	@OriginalMember(owner = "client!mf", name = "b", descriptor = "(Z)Lclient!vh;")
	fun getCountry(): Country? {
		return Country.aClass359Array1!![super.country]
	}

	companion object {
		@OriginalMember(owner = "client!ce", name = "E", descriptor = "I")
		@JvmField
		var port: Int = 0
		@OriginalMember(owner = "client!fha", name = "B", descriptor = "I")
		@JvmField
		var WORLD_LIST_START: Int = 0
		@OriginalMember(owner = "client!un", name = "o", descriptor = "(I)Lclient!mf;")
		@JvmStatic
		fun getFirstWorldDescriptor(): WorldDescriptor? {
			WorldConnection.nextWorldDescriptorIndex = 0
			return WorldConnection.getNextWorldDescriptor()
		}
	}
}
