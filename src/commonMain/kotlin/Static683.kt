import com.jagex.game.runetek5.config.midi.VorbisNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.rsinterface.Interface
import com.jagex.game.runetek5.InterfaceManager
import kotlin.jvm.JvmStatic

object Static683 {

	@OriginalMember(owner = "client!av", name = "e", descriptor = "(I)V")
	@JvmStatic
	fun method656() {
		@Pc(10) var videoType: VorbisNode? = VorbisNode.INDEX36_FILE_CACHE.first() as VorbisNode?
		while (videoType != null) {
			VorbisStreamStop.stopVorbisStream(videoType.fileId)
			videoType = VorbisNode.INDEX36_FILE_CACHE.next() as VorbisNode?
		}
	}

	@OriginalMember(owner = "client!av", name = "a", descriptor = "(BII)V")
	@JvmStatic
	fun runSubInterfaceHooks(@OriginalArg(1) interfaceId: Int, @OriginalArg(2) scriptMode: Int) {
		if (InterfaceManager.loadInterface(interfaceId)) {
			SubInterfaceHookRunner.runInterfaceScripts(Interface.interfaces!![interfaceId], scriptMode)
		}
	}
}
