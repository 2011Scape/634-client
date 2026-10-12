package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.cache.node.Node
import jagtheora.ogg.OggPacket
import jagtheora.ogg.OggStreamState
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!au")
abstract class OggStreamDecoder : Node {

	@OriginalMember(owner = "client!au", name = "t", descriptor = "I")
	@JvmField
	var packetCount: Int = 0

	@OriginalMember(owner = "client!au", name = "u", descriptor = "Lclient!jagtheora/ogg/OggStreamState;")
	@JvmField
	val streamState: OggStreamState?

	@OriginalMember(owner = "client!au", name = "<init>", descriptor = "(Lclient!jagtheora/ogg/OggStreamState;)V")
	constructor(@OriginalArg(0) oggStreamState: OggStreamState?) : super() {
		this.streamState = oggStreamState
	}

	@OriginalMember(owner = "client!au", name = "a", descriptor = "(B)V")
	abstract fun stop()

	@OriginalMember(owner = "client!au", name = "a", descriptor = "(Lclient!jagtheora/ogg/OggPacket;I)V")
	fun processPacket(@OriginalArg(0) oggPacket: OggPacket?) {
		this.decode(oggPacket)
		this.packetCount++
	}

	@OriginalMember(owner = "client!au", name = "a", descriptor = "(BLclient!jagtheora/ogg/OggPacket;)V")
	abstract fun decode(@OriginalArg(1) packet: OggPacket?)

	companion object {
		@OriginalMember(owner = "client!au", name = "q", descriptor = "[F")
		@JvmField
		val SINE: FloatArray = FloatArray(16384)

		@OriginalMember(owner = "client!au", name = "s", descriptor = "[F")
		@JvmField
		val COSINE: FloatArray = FloatArray(16384)

		init {
			@Pc(7) val radiansPerUnit: Double = 3.834951969714103E-4
			for (@Pc(9) angle in 0 until 16384) {
				SINE[angle] = kotlin.math.sin(radiansPerUnit * angle.toDouble()).toFloat()
				COSINE[angle] = kotlin.math.cos(angle.toDouble() * radiansPerUnit).toFloat()
			}
		}
	}
}
