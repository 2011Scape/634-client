import com.jagex.game.runetek5.sound.jagtheora.JagTheoraVorbisNode;
import com.jagex.game.runetek5.config.midi.VorbisNode;
import com.jagex.game.runetek5.ExplicitRuntimeException;
import com.jagex.game.runetek5.sound.jagtheora.VorbisPcmStream;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.sound.player.area.AmbientSound;

public final class VideoStreamTick {

	@OriginalMember(owner = "client!mba", name = "g", descriptor = "(I)V")
	public static void processVorbisStreams() {
		for (@Pc(6) VorbisNode videoType = (VorbisNode) VorbisNode.INDEX36_FILE_CACHE.first(); videoType != null; videoType = (VorbisNode) VorbisNode.INDEX36_FILE_CACHE.next()) {
			if (videoType.vfReference.isPlaybackComplete()) {
				VorbisStreamStop.stopVorbisStream(videoType.fileId);
			} else {
				videoType.vfReference.readNextPages();
				try {
					videoType.vfReference.tick();
				} catch (@Pc(29) Exception ex) {
					ExplicitRuntimeException.reportError("TV: " + videoType.fileId, ex);
					VorbisStreamStop.stopVorbisStream(videoType.fileId);
				}
				if (!videoType.isStreamActive && !videoType.isPaused) {
					@Pc(55) JagTheoraVorbisNode oggVorbisStream = videoType.vfReference.getVorbisDecoder();
					if (oggVorbisStream != null) {
						@Pc(61) VorbisPcmStream queueBuss = oggVorbisStream.getPcmStream();
						if (queueBuss != null) {
							queueBuss.setVolume(videoType.id);
							AmbientSound.gameSounds.addSubStream(queueBuss);
							videoType.isStreamActive = true;
						}
					}
				}
			}
		}
	}
}
