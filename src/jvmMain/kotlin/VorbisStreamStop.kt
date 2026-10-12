import com.jagex.game.runetek5.config.midi.VorbisNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object VorbisStreamStop {

	@OriginalMember(owner = "client!paa", name = "c", descriptor = "(BI)V")
	@JvmStatic
	fun stopVorbisStream(@OriginalArg(1) fileId: Int) {
		@Pc(10) val videoType: VorbisNode? = VorbisNode.INDEX36_FILE_CACHE.get(fileId.toLong()) as VorbisNode?
		if (videoType != null) {
			videoType.vfReference!!.stop()
			VideoEndNotify.playVorbis(videoType.sendPacket, videoType.fileId)
			videoType.unlink()
		}
	}
}
