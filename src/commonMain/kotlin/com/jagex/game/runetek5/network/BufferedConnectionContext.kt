package com.jagex.game.runetek5.network

import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.ClientPacket
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.network.packet.TcpPacket
import org.openrs2.deob.annotation.OriginalArg
import com.jagex.game.runetek5.buffer.BitPacket
import com.jagex.game.runetek5.network.packet.ServerProt
import com.jagex.game.runetek5.statics.OutboundByteCounter
import io.IOException
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer
import com.jagex.game.runetek5.MainLogicManager
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object BufferedConnectionContext {
	@OriginalMember(owner = "client!mr", name = "n", descriptor = "Z")
	@JvmField
	var dropped: Boolean = false

	@OriginalMember(owner = "client!be", name = "p", descriptor = "I")
	@JvmField
	var readDataSize: Int = 0

	@OriginalMember(owner = "client!hba", name = "a", descriptor = "I")
	@JvmField
	var lastFlush: Int = 0

	@OriginalMember(owner = "client!qo", name = "qe", descriptor = "Lclient!ej;")
	@JvmField
	val WORLDMAP_CLICK: ClientPacket = ClientPacket(58, 4)

	@OriginalMember(owner = "client!oi", name = "I", descriptor = "Lclient!uh;")
	@JvmField
	val queuedPackets: Deque = Deque()
	@OriginalMember(owner = "client!dfa", name = "a", descriptor = "(ILclient!wm;)V")
	@JvmStatic
	fun queuePacket(@OriginalArg(1) node: TcpPacket?) {
		BufferedConnectionContext.queuedPackets.addLast(node)
		node!!.capacity = node.buffer!!.currentPosition
		node.buffer!!.currentPosition = 0
		BufferedConnectionContext.readDataSize += node.capacity
	}

	@OriginalMember(owner = "client!mo", name = "f", descriptor = "Z")
	@JvmField
	var readPadding: Boolean = true
	@OriginalMember(owner = "client!ss", name = "b", descriptor = "Lclient!mu;")
	@JvmField
	var lastPacket: ServerProt? = null

	@OriginalMember(owner = "client!tl", name = "g", descriptor = "(I)V")
	@JvmStatic
	@Throws(IOException::class)
	fun flush() {
		if (Connection.connection == null || BufferedConnectionContext.readDataSize <= 0) {
			return
		}
		@Pc(10) var written: Int = 0
		while (true) {
			@Pc(17) val packet: TcpPacket? = BufferedConnectionContext.queuedPackets.first() as TcpPacket?
			if (packet == null) {
				BufferedConnectionContext.lastFlush = 0
				OutboundByteCounter.bytesWritten += written
				break
			}
			Connection.connection!!.write(packet.capacity, packet.buffer!!.buffer)
			written += packet.capacity
			BufferedConnectionContext.readDataSize -= packet.capacity
			packet.unlink()
			packet.buffer!!.flush()
			packet.push()
		}
	}

	@OriginalMember(owner = "client!cv", name = "r", descriptor = "I")
	@JvmField
	var idleReadPulses: Int = 0

	@OriginalMember(owner = "client!tga", name = "O", descriptor = "I")
	@JvmField
	var currentPacketSize: Int = 0

	@OriginalMember(owner = "client!wh", name = "b", descriptor = "Lclient!ua;")
	@JvmField
	val receivedBuffer: BitPacket = BitPacket(7500)

	@OriginalMember(owner = "client!eu", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun resetQueue() {
		BufferedConnectionContext.queuedPackets.clear()
		BufferedConnectionContext.readDataSize = 0
	}

	@OriginalMember(owner = "client!eu", name = "G", descriptor = "Lclient!mu;")
	@JvmField
	var currentPacket: ServerProt? = null
	@OriginalMember(owner = "client!vs", name = "a", descriptor = "(ZB)V")
	@JvmStatic
	fun sendKeepAlive(@OriginalArg(0) force: Boolean) {
		SoundEffectPlayer.updatePlayers()
		if (!MainLogicManager.isInGameStep(ClientState.mainLogicStep)) {
			return
		}
		BufferedConnectionContext.lastFlush++
		if (BufferedConnectionContext.lastFlush < 50 && !force) {
			return
		}
		BufferedConnectionContext.lastFlush = 0
		if (!BufferedConnectionContext.dropped && Connection.connection != null) {
			@Pc(39) val packet: TcpPacket = TcpPacket.createPacket(ClientProt.PING, TcpPacket.isaac)
			BufferedConnectionContext.queuePacket(packet)
			try {
				BufferedConnectionContext.flush()
			} catch (@Pc(46) ex: IOException) {
				BufferedConnectionContext.dropped = true
			}
		}
		SoundEffectPlayer.updatePlayers()
	}
	@OriginalMember(owner = "client!su", name = "l", descriptor = "Lclient!mu;")
	@JvmField
	var secondLastPacket: ServerProt? = null
	@OriginalMember(owner = "client!haa", name = "kb", descriptor = "J")
	@JvmField
	var NEXT_PING_UPDATE_TO_SERVER: Long = 0L
}
