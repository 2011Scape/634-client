import com.jagex.game.runetek5.sound.jagtheora.JagTheoraVorbisNode
import com.jagex.game.runetek5.config.midi.VorbisNode
import com.jagex.game.runetek5.ExplicitRuntimeException
import com.jagex.game.runetek5.sound.jagtheora.VorbisPcmStream
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.sound.player.area.AmbientSound
import kotlin.jvm.JvmStatic

object VideoStreamTick {

	@OriginalMember(owner = "client!mba", name = "g", descriptor = "(I)V")
	@JvmStatic
	fun processVorbisStreams() {
		@Pc(6) var videoType: VorbisNode? = VorbisNode.INDEX36_FILE_CACHE.first() as VorbisNode?
		while (videoType != null) {
			if (videoType.vfReference!!.isPlaybackComplete()) {
				VorbisStreamStop.stopVorbisStream(videoType.fileId)
			} else {
				videoType.vfReference!!.readNextPages()
				try {
					videoType.vfReference!!.tick()
				} catch (@Pc(29) ex: Exception) {
					ExplicitRuntimeException.reportError("TV: " + videoType.fileId, ex)
					VorbisStreamStop.stopVorbisStream(videoType.fileId)
				}
				if (!videoType.isStreamActive && !videoType.isPaused) {
					@Pc(55) val oggVorbisStream: JagTheoraVorbisNode? = videoType.vfReference!!.getVorbisDecoder()
					if (oggVorbisStream != null) {
						@Pc(61) val queueBuss: VorbisPcmStream? = oggVorbisStream.getPcmStream()
						if (queueBuss != null) {
							queueBuss.setVolume(videoType.id)
							AmbientSound.gameSounds!!.addSubStream(queueBuss)
							videoType.isStreamActive = true
						}
					}
				}
			}
			videoType = VorbisNode.INDEX36_FILE_CACHE.next() as VorbisNode?
		}
	}
}
