package com.jagex.game.runetek5.sound.jagtheora

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.cache.node.impl.AudioNode
import org.openrs2.deob.annotation.Pc
import kotlin.concurrent.Volatile
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!jba")
abstract class PcmStream : Node() {

	@OriginalMember(owner = "client!jba", name = "p", descriptor = "Lclient!jba;")
	@JvmField
	var nextStream: PcmStream? = null

	@OriginalMember(owner = "client!jba", name = "q", descriptor = "I")
	@JvmField
	var streamPriority: Int = 0

	@OriginalMember(owner = "client!jba", name = "r", descriptor = "Lclient!ji;")
	@JvmField
	var variableRateSoundPacket: AudioNode? = null

	@OriginalMember(owner = "client!jba", name = "o", descriptor = "Z")
	@Volatile
	@JvmField
	var fill: Boolean = true

	@OriginalMember(owner = "client!jba", name = "a", descriptor = "([III)V")
	fun process(@OriginalArg(0) buffer: IntArray?, @OriginalArg(1) offset: Int, @OriginalArg(2) length: Int) {
		if (this.fill) {
			this.fill(buffer, offset, length)
		} else {
			this.skip(length)
		}
	}

	@OriginalMember(owner = "client!jba", name = "a", descriptor = "()Lclient!jba;")
	abstract fun getFirstStream(): PcmStream?

	@OriginalMember(owner = "client!jba", name = "b", descriptor = "([III)V")
	abstract fun fill(@OriginalArg(0) buffer: IntArray?, @OriginalArg(1) offset: Int, @OriginalArg(2) length: Int)

	@OriginalMember(owner = "client!jba", name = "b", descriptor = "()Lclient!jba;")
	abstract fun getNextStream(): PcmStream?

	@OriginalMember(owner = "client!jba", name = "a", descriptor = "(I)V")
	abstract fun skip(@OriginalArg(0) length: Int)

	@OriginalMember(owner = "client!jba", name = "c", descriptor = "()I")
	open fun getPriority(): Int {
		return 255
	}

	@OriginalMember(owner = "client!jba", name = "d", descriptor = "()I")
	abstract fun getActiveVoiceCount(): Int

	companion object {
		@OriginalMember(owner = "client!il", name = "a", descriptor = "(Lclient!jba;I)V")
		@JvmStatic
		fun resetMixState(@OriginalArg(0) audioBuss: PcmStream?) {
			audioBuss!!.fill = false
			if (audioBuss.variableRateSoundPacket != null) {
				audioBuss.variableRateSoundPacket!!.activeVoiceCount = 0
			}
			@Pc(22) var audioBuss2: PcmStream? = audioBuss.getFirstStream()
			while (audioBuss2 != null) {
				resetMixState(audioBuss2)
				audioBuss2 = audioBuss.getNextStream()
			}
		}
	}
}
