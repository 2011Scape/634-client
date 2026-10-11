import com.jagex.game.runetek5.config.midi.VorbisNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class VorbisStreamStop {

	@OriginalMember(owner = "client!paa", name = "c", descriptor = "(BI)V")
	public static void stopVorbisStream(@OriginalArg(1) int fileId) {
		@Pc(10) VorbisNode videoType = (VorbisNode) VorbisNode.INDEX36_FILE_CACHE.get((long) fileId);
		if (videoType != null) {
			videoType.vfReference.stop();
			VideoEndNotify.playVorbis(videoType.sendPacket, videoType.fileId);
			videoType.unlink();
		}
	}
}
