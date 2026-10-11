import com.jagex.game.runetek5.sound.jagtheora.VorbisFileReference;
import com.jagex.game.runetek5.config.midi.VorbisNode;
import com.jagex.game.runetek5.client.GlColorAttachments;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.interaction.FriendList;
import com.jagex.game.runetek5.sound.jagtheora.JagTheoraManager;
import com.jagex.game.runetek5.client.ClientState;

public final class VorbisStreamPreload {

	@OriginalMember(owner = "client!qc", name = "a", descriptor = "(IZII)V")
	public static void preloadVorbisSound(@OriginalArg(0) int volume, @OriginalArg(1) boolean sendPacket, @OriginalArg(2) int fileId) {
		if (VorbisNode.INDEX36_FILE_CACHE.get((long) fileId) != null) {
			return;
		}
		if (JagTheoraManager.jagtheoraLoaded) {
			@Pc(31) VorbisNode videoType = new VorbisNode(fileId, new VorbisFileReference(4096, Resource.VORBIS, fileId), volume, sendPacket);
			videoType.vfReference.setLanguage(FriendList.LANGUAGE_CODES[ClientState.language]);
			VorbisNode.INDEX36_FILE_CACHE.put((long) fileId, videoType);
		} else {
			VideoEndNotify.playVorbis(sendPacket, fileId);
		}
	}
}
