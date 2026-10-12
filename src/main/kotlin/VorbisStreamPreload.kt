import com.jagex.game.runetek5.sound.jagtheora.VorbisFileReference
import com.jagex.game.runetek5.config.midi.VorbisNode
import com.jagex.game.runetek5.client.GlColorAttachments
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.interaction.FriendList
import com.jagex.game.runetek5.sound.jagtheora.JagTheoraManager
import com.jagex.game.runetek5.client.ClientState

object VorbisStreamPreload {

	@OriginalMember(owner = "client!qc", name = "a", descriptor = "(IZII)V")
	@JvmStatic
	fun preloadVorbisSound(@OriginalArg(0) volume: Int, @OriginalArg(1) sendPacket: Boolean, @OriginalArg(2) fileId: Int) {
		if (VorbisNode.INDEX36_FILE_CACHE.get(fileId.toLong()) != null) {
			return
		}
		if (JagTheoraManager.jagtheoraLoaded) {
			@Pc(31) val videoType: VorbisNode = VorbisNode(fileId, VorbisFileReference(4096, Resource.VORBIS, fileId), volume, sendPacket)
			videoType.vfReference!!.setLanguage(FriendList.LANGUAGE_CODES[ClientState.language])
			VorbisNode.INDEX36_FILE_CACHE.put(fileId.toLong(), videoType)
		} else {
			VideoEndNotify.playVorbis(sendPacket, fileId)
		}
	}
}
