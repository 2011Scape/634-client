package com.jagex.game.runetek5.client.loading.loader

import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

@OriginalClass("client!kea")
class LoadingRequirementType {

	@OriginalMember(owner = "client!kea", name = "toString", descriptor = "()Ljava/lang/String;")
	override fun toString(): String {
		throw IllegalStateException()
	}

	companion object {
		@OriginalMember(owner = "client!kea", name = "i", descriptor = "Lclient!kea;")
		@JvmField
		val NATIVE_LIBRARY: LoadingRequirementType = LoadingRequirementType()

		@OriginalMember(owner = "client!kea", name = "g", descriptor = "Lclient!kea;")
		@JvmField
		val JS5_FILE: LoadingRequirementType = LoadingRequirementType()

		@OriginalMember(owner = "client!kea", name = "e", descriptor = "Lclient!kea;")
		@JvmField
		val JS5: LoadingRequirementType = LoadingRequirementType()

		@OriginalMember(owner = "client!kea", name = "h", descriptor = "Lclient!kea;")
		@JvmField
		val JS5_GROUP: LoadingRequirementType = LoadingRequirementType()
	}
}
