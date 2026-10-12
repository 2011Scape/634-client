package com.jagex.game.runetek5.statics

import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField

object NativeModelScratch {

	@OriginalMember(owner = "client!oa", name = "y", descriptor = "[I")
	private val sharedIntScratch: IntArray = IntArray(kotlin.math.max(kotlin.math.max(104, 20), 24573))

	@OriginalMember(owner = "client!oa", name = "F", descriptor = "[F")
	private val sharedFloatScratch: FloatArray = FloatArray(20)

	@OriginalMember(owner = "client!oa", name = "G", descriptor = "[I")
	@JvmField
	val pointLightData: IntArray = sharedIntScratch

	@OriginalMember(owner = "client!oa", name = "I", descriptor = "[I")
	@JvmField
	val pickingResult: IntArray = IntArray(6)

	@OriginalMember(owner = "client!oa", name = "J", descriptor = "[S")
	@JvmField
	val particleMaterialIds: ShortArray = ShortArray(8191)

	@OriginalMember(owner = "client!oa", name = "K", descriptor = "[B")
	@JvmField
	val aByteArray96: ByteArray = ByteArray(8191)

	@OriginalMember(owner = "client!oa", name = "L", descriptor = "[I")
	@JvmField
	val particleColours: IntArray = IntArray(8191)

	@OriginalMember(owner = "client!oa", name = "M", descriptor = "[I")
	@JvmField
	val particlePositions: IntArray = sharedIntScratch

	@OriginalMember(owner = "client!oa", name = "O", descriptor = "[F")
	@JvmField
	val pointLightIntensities: FloatArray = sharedFloatScratch

	@OriginalMember(owner = "client!oa", name = "P", descriptor = "[I")
	@JvmField
	val particleSizes: IntArray = IntArray(8191)

	@OriginalMember(owner = "client!oa", name = "Q", descriptor = "[I")
	@JvmField
	val effectVertices: IntArray = sharedIntScratch
}
