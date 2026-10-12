package com.jagex.game.runetek5.js5.dependency

import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.loading.loader.LoadingRequirementType
import com.jagex.game.runetek5.client.loading.loader.Loader

@OriginalClass("client!w")
class CacheableResource : Loader {

	@OriginalMember(owner = "client!w", name = "h", descriptor = "Lclient!in;")
	private val resourceProvider: Js5ResourceProvider?

	@OriginalMember(owner = "client!w", name = "<init>", descriptor = "(Lclient!in;)V")
	constructor(@OriginalArg(0) resourceProvider: Js5ResourceProvider?) {
		this.resourceProvider = resourceProvider
	}

	@OriginalMember(owner = "client!w", name = "a", descriptor = "(I)I")
	override fun completePercentage(): Int {
		return if (this.resourceProvider!!.fullyLoaded()) 100 else this.resourceProvider.getCompletePercentage()
	}

	@OriginalMember(owner = "client!w", name = "b", descriptor = "(I)Lclient!kea;")
	override fun getRequirementType(): LoadingRequirementType? {
		return LoadingRequirementType.JS5
	}
}
