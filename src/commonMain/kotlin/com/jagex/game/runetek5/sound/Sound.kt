package com.jagex.game.runetek5.sound

import com.jagex.game.runetek5.entity.Entity
import com.jagex.game.runetek5.cache.node.VorbisSound
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.cache.node.impl.VariableRateSoundPacket
import com.jagex.game.runetek5.config.midi.SoundEffect
import com.jagex.game.runetek5.sound.jagtheora.AreaSoundStream
import kotlin.jvm.JvmField

@OriginalClass("client!gt")
class Sound {

	@OriginalMember(owner = "client!gt", name = "e", descriptor = "Lclient!via;")
	@JvmField
	var aClass2_Sub8_Sub5_1: AreaSoundStream? = null

	@OriginalMember(owner = "client!gt", name = "l", descriptor = "Lclient!ck;")
	@JvmField
	var aClass61_1: SoundEffect? = null

	@OriginalMember(owner = "client!gt", name = "r", descriptor = "Lclient!ob;")
	@JvmField
	var aClass2_Sub29_Sub1_1: VariableRateSoundPacket? = null

	@OriginalMember(owner = "client!gt", name = "u", descriptor = "Lclient!hp;")
	@JvmField
	var aClass2_Sub22_1: VorbisSound? = null

	@OriginalMember(owner = "client!gt", name = "f", descriptor = "I")
	@JvmField
	val loops: Int

	@OriginalMember(owner = "client!gt", name = "m", descriptor = "I")
	@JvmField
	val volume: Int

	@OriginalMember(owner = "client!gt", name = "q", descriptor = "I")
	@JvmField
	var delay: Int

	@OriginalMember(owner = "client!gt", name = "g", descriptor = "I")
	@JvmField
	val coord: Int

	@OriginalMember(owner = "client!gt", name = "n", descriptor = "I")
	@JvmField
	val id: Int

	@OriginalMember(owner = "client!gt", name = "i", descriptor = "B")
	@JvmField
	val type: Byte

	@OriginalMember(owner = "client!gt", name = "b", descriptor = "I")
	@JvmField
	val rate: Int

	@OriginalMember(owner = "client!gt", name = "a", descriptor = "Lclient!ga;")
	@JvmField
	val entity: Entity?

	@OriginalMember(owner = "client!gt", name = "<init>", descriptor = "(BIIIIIILclient!ga;)V")
	constructor(@OriginalArg(0) type: Byte, @OriginalArg(1) id: Int, @OriginalArg(2) loops: Int, @OriginalArg(3) delay: Int, @OriginalArg(4) volume: Int, @OriginalArg(5) coord: Int, @OriginalArg(6) rate: Int, @OriginalArg(7) entity: Entity?) {
		this.loops = loops
		this.volume = volume
		this.delay = delay
		this.coord = coord
		this.id = id
		this.type = type
		this.rate = rate
		this.entity = entity
	}

	@OriginalMember(owner = "client!gt", name = "c", descriptor = "(B)Z")
	fun isVorbis(): Boolean {
		return this.type.toInt() == SoundType.VORBIS || this.type.toInt() == SoundType.VORBIS_VOICE_OVER
	}
}
