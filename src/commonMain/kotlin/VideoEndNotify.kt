import com.jagex.game.runetek5.network.packet.TcpPacket
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.ClientTrigger
import com.jagex.game.runetek5.network.packet.ClientProt
import kotlin.jvm.JvmStatic

object VideoEndNotify {

	@OriginalMember(owner = "client!wp", name = "a", descriptor = "(ZBI)V")
	@JvmStatic
	fun playVorbis(@OriginalArg(0) sendPacket: Boolean, @OriginalArg(2) fileId: Int) {
		if (sendPacket) {
			@Pc(20) val packet: TcpPacket? = TcpPacket.createPacket(ClientProt.aClass94_81, TcpPacket.isaac)
			packet!!.buffer!!.writeShort(fileId)
			BufferedConnectionContext.queuePacket(packet)
		} else {
			CS2Interpreter.executeTrigger(ClientTrigger.aClass350_7, fileId, -1)
		}
	}
}
