import com.jagex.game.runetek5.ThirdLastPacket
import com.jagex.game.runetek5.ExplicitRuntimeException
import io.IOException
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.MapRegion
import kotlin.jvm.JvmStatic

object GuardedPacketDecode {

	@OriginalMember(owner = "client!w", name = "a", descriptor = "(B)Z")
	@JvmStatic
	fun decodePacketFromStream(): Boolean {
		try {
			return PacketDecoder.decode()
		} catch (@Pc(17) ex: IOException) {
			ConnectionLostHandler.handleConnectionLost()
			return true
		} catch (@Pc(22) ex2: Exception) {
			@Pc(81) var string: String = "T2 - " + (if (BufferedConnectionContext.currentPacket == null) -1 else BufferedConnectionContext.currentPacket!!.getOpcode()) + "," + (if (BufferedConnectionContext.secondLastPacket == null) -1 else BufferedConnectionContext.secondLastPacket!!.getOpcode()) + "," + (if (ThirdLastPacket.thirdLastPacket == null) -1 else ThirdLastPacket.thirdLastPacket!!.getOpcode()) + " - " + BufferedConnectionContext.currentPacketSize + "," + GuardedPacketDecode.playerPosition() + " - "
			@Pc(83) var i: Int = 0
			while (BufferedConnectionContext.currentPacketSize > i && i < 50) {
				string = string + BufferedConnectionContext.receivedBuffer.buffer!![i] + ","
				i++
			}
			ExplicitRuntimeException.reportError(string, ex2)
			ClientLogout.logout(false)
			return true
		}
	}

	/**
	 * Not part of the original client: the local player's tile for the T2 report, or "-1,-1" when there
	 * is no local player, so that the report cannot itself throw out of the guarded decode.
	 */
	@JvmStatic
	fun playerPosition(): String {
		val player: PlayerEntity? = PlayerUpdate.localPlayer
		if (player == null) {
			return "-1,-1"
		}
		return "" + (player.movementBaseX[0] + MapRegion.sceneBaseX) + "," + (MapRegion.sceneBaseY + player.movementBaseY[0])
	}
}
