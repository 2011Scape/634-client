// The warnings of the original (deobfuscated) code, left as it was: the build treats warnings as errors.
@file:Suppress("SENSELESS_COMPARISON")

import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.network.world.WorldList
import com.jagex.game.runetek5.config.iftype.TargetMask
import com.jagex.game.runetek5.statics.OutPlayerIndexCount
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.menu.MiniMenuBuilder
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.ClientPacket
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.interaction.chat.ChatBox
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.PathFinder
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmStatic

object InterfaceOnPlayerAction {

	@OriginalMember(owner = "client!aj", name = "a", descriptor = "(Ljava/lang/String;B)V")
	@JvmStatic
	fun sendInterfaceOnPlayer(@OriginalArg(0) string: String?) {
		if (!MiniMenu.interfaceSelectionActive || (MiniMenu.useTargetMask and 0x18) == 0) {
			return
		}
		@Pc(25) var found: Boolean = false
		@Pc(27) val count: Int = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES
		@Pc(29) val outPlayerIndices: IntArray = PlayerUpdate.OUT_PLAYER_INDICES
		for (@Pc(31) i in 0 until count) {
			@Pc(39) val playerEntity: PlayerEntity? = PlayerUpdate.players[outPlayerIndices[i]]
			if (playerEntity!!.displayName != null && playerEntity.displayName.equals(string, ignoreCase = true) && (playerEntity === PlayerUpdate.localPlayer && (MiniMenu.useTargetMask and TargetMask.TGT_SELF) != 0 || playerEntity != null && (MiniMenu.useTargetMask and TargetMask.TGT_PLAYER) != 0)) {
				@Pc(72) val packet: TcpPacket = TcpPacket.createPacket(ClientProt.OPPLAYERT, TcpPacket.isaac)
				packet.buffer!!.writeShortAddLittle(MiniMenu.slotId)
				packet.buffer!!.writeShortLittle(outPlayerIndices[i])
				packet.buffer!!.writeShortLittle(MiniMenu.selectedItemId)
				packet.buffer!!.writeIntInverseMiddle(MiniMenuBuilder.USE_OPTIONS_FLAGS)
				packet.buffer!!.writeByteInverse(0)
				BufferedConnectionContext.queuePacket(packet)
				found = true
				PathFinder.calculateRoute(true, playerEntity.movementBaseX[0], playerEntity.movementBaseY[0], playerEntity.getSize(), 0, playerEntity.getSize(), -2, 0)
				break
			}
		}
		if (!found) {
			ChatBox.writeGameMessage(LocalizedText.aClass351_23!!.get(ClientState.language) + string)
		}
		if (MiniMenu.interfaceSelectionActive) {
			Static657.cancelInterfaceSelection()
		}
	}
}
