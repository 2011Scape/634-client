package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.loading.loader.Loader
import com.jagex.game.runetek5.client.loading.loader.LoadingRequirementType

@OriginalClass("client!up")
class LoadingRequirement @OriginalMember(owner = "client!up", name = "<init>", descriptor = "(Lclient!kea;)V") constructor(@OriginalArg(0) loadingRequirementType: LoadingRequirementType?) {

	@OriginalMember(owner = "client!up", name = "i", descriptor = "Lclient!lu;")
	private var loader: Loader? = null

	@OriginalMember(owner = "client!up", name = "e", descriptor = "I")
	private var size: Int = 1

	@OriginalMember(owner = "client!up", name = "c", descriptor = "Lclient!kea;")
	private val type: LoadingRequirementType?

	init {
		this.type = loadingRequirementType
	}

	@OriginalMember(owner = "client!up", name = "a", descriptor = "(I)I")
	fun getSize(): Int {
		return this.size
	}

	@OriginalMember(owner = "client!up", name = "a", descriptor = "(Lclient!lu;Z)V")
	fun setLoader(@OriginalArg(0) loader: Loader?) {
		if (loader!!.getRequirementType() !== this.type) {
			throw IllegalArgumentException()
		}
		this.loader = loader
	}

	@OriginalMember(owner = "client!up", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	@OriginalMember(owner = "client!up", name = "c", descriptor = "(I)Lclient!lu;")
	fun getLoader(): Loader? {
		return this.loader
	}

	@OriginalMember(owner = "client!up", name = "a", descriptor = "(II)V")
	fun setSize(@OriginalArg(1) size: Int) {
		this.size = size
	}
}
