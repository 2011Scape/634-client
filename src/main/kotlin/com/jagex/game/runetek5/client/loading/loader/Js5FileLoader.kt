package com.jagex.game.runetek5.client.loading.loader

import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.loading.loader.LoadingRequirementType

@OriginalClass("client!kk")
class Js5FileLoader @OriginalMember(owner = "client!kk", name = "<init>", descriptor = "(Lclient!in;Ljava/lang/String;)V") constructor(@OriginalArg(0) config: Js5ResourceProvider?, @OriginalArg(1) string: String?) : Loader {

	@OriginalMember(owner = "client!kk", name = "n", descriptor = "Lclient!in;")
	private val config: Js5ResourceProvider?

	@OriginalMember(owner = "client!kk", name = "g", descriptor = "Ljava/lang/String;")
	private val fileName: String?

	init {
		this.config = config
		this.fileName = string
	}

	@OriginalMember(owner = "client!kk", name = "a", descriptor = "(I)I")
	override fun completePercentage(): Int {
		return if (this.config!!.isFileReady(this.fileName)) 100 else 0
	}

	@OriginalMember(owner = "client!kk", name = "b", descriptor = "(I)Lclient!kea;")
	override fun getRequirementType(): LoadingRequirementType? {
		return LoadingRequirementType.JS5_FILE
	}
}
