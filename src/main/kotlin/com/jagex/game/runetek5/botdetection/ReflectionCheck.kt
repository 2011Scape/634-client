package com.jagex.game.runetek5.botdetection

import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.sign.SignedResource
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!bw")
class ReflectionCheck : Node() {

	@OriginalMember(owner = "client!bw", name = "o", descriptor = "I")
	@JvmField
	var operationCount: Int = 0

	@OriginalMember(owner = "client!bw", name = "p", descriptor = "[Lclient!of;")
	@JvmField
	var methodsToCheck: Array<SignedResource?>? = null

	@OriginalMember(owner = "client!bw", name = "q", descriptor = "[[[B")
	@JvmField
	var argumentBuffers: Array<Array<ByteArray?>?>? = null

	@OriginalMember(owner = "client!bw", name = "s", descriptor = "I")
	@JvmField
	var id: Int = 0

	@OriginalMember(owner = "client!bw", name = "t", descriptor = "[I")
	@JvmField
	var exceptionsEncountered: IntArray? = null

	@OriginalMember(owner = "client!bw", name = "u", descriptor = "[I")
	@JvmField
	var operations: IntArray? = null

	@OriginalMember(owner = "client!bw", name = "x", descriptor = "[Lclient!of;")
	@JvmField
	var fieldsToCheck: Array<SignedResource?>? = null

	@OriginalMember(owner = "client!bw", name = "y", descriptor = "[I")
	@JvmField
	var fieldInput: IntArray? = null
}
