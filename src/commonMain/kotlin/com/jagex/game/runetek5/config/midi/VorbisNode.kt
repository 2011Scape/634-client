package com.jagex.game.runetek5.config.midi

import com.jagex.game.runetek5.cache.node.Node
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.sound.jagtheora.VorbisFileReference
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.sound.jagtheora.JagTheoraTheoraNode
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!il")
class VorbisNode : Node {

	@OriginalMember(owner = "client!il", name = "s", descriptor = "Z")
	@JvmField
	var isReferencedThisCycle: Boolean = false

	@OriginalMember(owner = "client!il", name = "B", descriptor = "Z")
	@JvmField
	var isPaused: Boolean = false

	@OriginalMember(owner = "client!il", name = "D", descriptor = "Z")
	@JvmField
	var isStreamActive: Boolean = false

	@OriginalMember(owner = "client!il", name = "C", descriptor = "I")
	@JvmField
	val id: Int

	@OriginalMember(owner = "client!il", name = "o", descriptor = "Lclient!qc;")
	@JvmField
	val vfReference: VorbisFileReference?

	@OriginalMember(owner = "client!il", name = "t", descriptor = "I")
	@JvmField
	val fileId: Int

	@OriginalMember(owner = "client!il", name = "w", descriptor = "Z")
	@JvmField
	val sendPacket: Boolean

	@OriginalMember(owner = "client!il", name = "<init>", descriptor = "(ILclient!qc;IZ)V")
	constructor(@OriginalArg(0) fileId: Int, @OriginalArg(1) js5Video: VorbisFileReference?, @OriginalArg(2) volume: Int, @OriginalArg(3) sendPacket: Boolean) : super() {
		this.id = volume
		this.vfReference = js5Video
		this.fileId = fileId
		this.sendPacket = sendPacket
	}

	companion object {
		@OriginalMember(owner = "client!qda", name = "a", descriptor = "(Lclient!ha;II)Lclient!mi;")
		@JvmStatic
		fun getVideoFrame(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(2) videoId: Int): NativeSprite? {
			@Pc(10) val videoType: VorbisNode? = VorbisNode.INDEX36_FILE_CACHE.get(videoId.toLong()) as VorbisNode?
			if (videoType != null) {
				@Pc(17) val oggTheoraStream: JagTheoraTheoraNode? = videoType.vfReference!!.getTheoraDecoder()
				videoType.isReferencedThisCycle = true
				if (oggTheoraStream != null) {
					return oggTheoraStream.getFrameSprite(toolkit)
				}
			}
			return null
		}

		@OriginalMember(owner = "client!ht", name = "c", descriptor = "Lclient!eq;")
		@JvmField
		val INDEX36_FILE_CACHE: IterableHashTable = IterableHashTable(4)

		@OriginalMember(owner = "client!oea", name = "a", descriptor = "(II)V")
		@JvmStatic
		fun toggleVorbisPause(@OriginalArg(1) videoId: Int) {
			@Pc(15) val videoType: VorbisNode? = VorbisNode.INDEX36_FILE_CACHE.get(videoId.toLong()) as VorbisNode?
			if (videoType != null) {
				videoType.isPaused = !videoType.isPaused
				videoType.vfReference!!.togglePause(videoType.isPaused)
			}
		}
	}
}
