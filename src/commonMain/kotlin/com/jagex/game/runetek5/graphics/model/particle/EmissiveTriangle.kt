package com.jagex.game.runetek5.graphics.model.particle

import com.jagex.game.runetek5.config.particle.ParticleProducerType
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!nn")
class EmissiveTriangle @OriginalMember(owner = "client!nn", name = "<init>", descriptor = "(IIIIB)V") constructor(@OriginalArg(0) producerId: Int, @OriginalArg(1) faceA: Int, @OriginalArg(2) faceB: Int, @OriginalArg(3) faceC: Int, @OriginalArg(4) priority: Byte) {

	@OriginalMember(owner = "client!nn", name = "b", descriptor = "I")
	@JvmField
	var transformedCY: Int = 0

	@OriginalMember(owner = "client!nn", name = "d", descriptor = "I")
	@JvmField
	var verticeYB: Int = 0

	@OriginalMember(owner = "client!nn", name = "h", descriptor = "I")
	@JvmField
	var verticeZB: Int = 0

	@OriginalMember(owner = "client!nn", name = "j", descriptor = "I")
	@JvmField
	var verticeZC: Int = 0

	@OriginalMember(owner = "client!nn", name = "k", descriptor = "I")
	@JvmField
	var verticeZA: Int = 0

	@OriginalMember(owner = "client!nn", name = "l", descriptor = "I")
	@JvmField
	var verticeYC: Int = 0

	@OriginalMember(owner = "client!nn", name = "p", descriptor = "Lclient!nn;")
	@JvmField
	var child: EmissiveTriangle? = null

	@OriginalMember(owner = "client!nn", name = "q", descriptor = "I")
	@JvmField
	var verticeYA: Int = 0

	@OriginalMember(owner = "client!nn", name = "r", descriptor = "I")
	@JvmField
	var verticeXA: Int = 0

	@OriginalMember(owner = "client!nn", name = "s", descriptor = "I")
	@JvmField
	var verticeXC: Int = 0

	@OriginalMember(owner = "client!nn", name = "n", descriptor = "I")
	private val id: Int

	@OriginalMember(owner = "client!nn", name = "c", descriptor = "B")
	@JvmField
	val priority: Byte

	@OriginalMember(owner = "client!nn", name = "i", descriptor = "I")
	@JvmField
	val faceA: Int

	@OriginalMember(owner = "client!nn", name = "t", descriptor = "I")
	@JvmField
	val vertexC: Int

	@OriginalMember(owner = "client!nn", name = "e", descriptor = "I")
	@JvmField
	val faceB: Int

	init {
		this.id = producerId
		this.priority = priority
		this.faceA = faceA
		this.vertexC = faceC
		this.faceB = faceB
	}

	@OriginalMember(owner = "client!nn", name = "a", descriptor = "(IIII)Lclient!nn;")
	fun copyWithFaces(@OriginalArg(0) faceB: Int, @OriginalArg(2) faceC: Int, @OriginalArg(3) faceA: Int): EmissiveTriangle {
		return EmissiveTriangle(this.id, faceA, faceB, faceC, this.priority)
	}

	@OriginalMember(owner = "client!nn", name = "a", descriptor = "(B)Lclient!qb;")
	fun getProducer(): ParticleProducerType {
		return ParticleProducerType.get(this.id)
	}

	companion object {
		@OriginalMember(owner = "client!nn", name = "m", descriptor = "[I")
		@JvmField
		val BIT_MASKS: IntArray = IntArray(32)

		init {
			@Pc(4) var powerOfTwo: Int = 2
			@Pc(6) var index: Int = 0
			while (index < 32) {
				BIT_MASKS[index] = powerOfTwo - 1
				powerOfTwo += powerOfTwo
				index++
			}
		}
	}
}
