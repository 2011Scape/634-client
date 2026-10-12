package com.jagex.game.runetek5.client.loading.loader

import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.loading.loader.LoadingRequirementType

@OriginalClass("client!nv")
class Js5GroupLoader @OriginalMember(owner = "client!nv", name = "<init>", descriptor = "(Lclient!in;Ljava/lang/String;)V") constructor(@OriginalArg(0) config: Js5ResourceProvider?, @OriginalArg(1) string: String?) : Loader {

	@OriginalMember(owner = "client!nv", name = "b", descriptor = "Ljava/lang/String;")
	private val group: String?

	@OriginalMember(owner = "client!nv", name = "c", descriptor = "Lclient!in;")
	private val config: Js5ResourceProvider?

	init {
		this.group = string
		this.config = config
	}

	@OriginalMember(owner = "client!nv", name = "b", descriptor = "(I)Lclient!kea;")
	override fun getRequirementType(): LoadingRequirementType? {
		return LoadingRequirementType.JS5_GROUP
	}

	@OriginalMember(owner = "client!nv", name = "a", descriptor = "(I)I")
	override fun completePercentage(): Int {
		return if (this.config!!.isGroupReady(this.group)) 100 else this.config.getGroupCompletePercentage(this.group)
	}
}
