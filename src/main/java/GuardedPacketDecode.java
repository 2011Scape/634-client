import com.jagex.game.runetek5.ThirdLastPacket;
import com.jagex.game.runetek5.ExplicitRuntimeException;
import java.io.IOException;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.MapRegion;

public final class GuardedPacketDecode {

	@OriginalMember(owner = "client!w", name = "a", descriptor = "(B)Z")
	public static boolean decodePacketFromStream() {
		try {
			return PacketDecoder.decode();
		} catch (@Pc(17) IOException ex) {
			ConnectionLostHandler.handleConnectionLost();
			return true;
		} catch (@Pc(22) Exception ex2) {
			@Pc(81) String string = "T2 - " + (BufferedConnectionContext.currentPacket == null ? -1 : BufferedConnectionContext.currentPacket.getOpcode()) + "," + (BufferedConnectionContext.secondLastPacket == null ? -1 : BufferedConnectionContext.secondLastPacket.getOpcode()) + "," + (ThirdLastPacket.thirdLastPacket == null ? -1 : ThirdLastPacket.thirdLastPacket.getOpcode()) + " - " + BufferedConnectionContext.currentPacketSize + "," + (PlayerUpdate.localPlayer.movementBaseX[0] + MapRegion.sceneBaseX) + "," + (MapRegion.sceneBaseY + PlayerUpdate.localPlayer.movementBaseY[0]) + " - ";
			for (@Pc(83) int i = 0; BufferedConnectionContext.currentPacketSize > i && i < 50; i++) {
				string = string + BufferedConnectionContext.receivedBuffer.buffer[i] + ",";
			}
			ExplicitRuntimeException.reportError(string, ex2);
			ClientLogout.logout(false);
			return true;
		}
	}
}
