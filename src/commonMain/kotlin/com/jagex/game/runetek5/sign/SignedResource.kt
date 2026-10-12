package com.jagex.game.runetek5.sign

import com.jagex.game.runetek5.core.constants.SignedResourceType
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import kotlin.concurrent.Volatile
import kotlin.jvm.JvmField

@OriginalClass("client!of")
class SignedResource {

	@OriginalMember(owner = "client!of", name = "a", descriptor = "I")
	@JvmField
	var kind: SignedResourceType? = null

	@OriginalMember(owner = "client!of", name = "b", descriptor = "Lclient!of;")
	@JvmField
	var next: SignedResource? = null

	@OriginalMember(owner = "client!of", name = "c", descriptor = "Ljava/lang/Object;")
	@JvmField
	var objectData: Any? = null

	@OriginalMember(owner = "client!of", name = "e", descriptor = "Ljava/lang/Object;")
	@Volatile
	@JvmField
	var result: Any? = null

	@OriginalMember(owner = "client!of", name = "f", descriptor = "I")
	@JvmField
	var intData2: Int = 0

	@OriginalMember(owner = "client!of", name = "g", descriptor = "I")
	@JvmField
	var intData1: Int = 0

	@OriginalMember(owner = "client!of", name = "d", descriptor = "I")
	@Volatile
	@JvmField
	var status: Int = 0
}
