import com.jagex.game.runetek5.config.midi.VorbisNode;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.InterfaceManager;

public final class Static683 {

	@OriginalMember(owner = "client!av", name = "e", descriptor = "(I)V")
	public static void method656() {
		for (@Pc(10) VorbisNode videoType = (VorbisNode) VorbisNode.INDEX36_FILE_CACHE.first(); videoType != null; videoType = (VorbisNode) VorbisNode.INDEX36_FILE_CACHE.next()) {
			VorbisStreamStop.stopVorbisStream(videoType.fileId);
		}
	}

	@OriginalMember(owner = "client!av", name = "a", descriptor = "(BII)V")
	public static void runSubInterfaceHooks(@OriginalArg(1) int interfaceId, @OriginalArg(2) int scriptMode) {
		if (InterfaceManager.loadInterface(interfaceId)) {
			SubInterfaceHookRunner.runInterfaceScripts(Interface.interfaces[interfaceId], scriptMode);
		}
	}
}
