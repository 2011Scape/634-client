package com.jagex.game.runetek5.network;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.ClientPacket;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.network.packet.TcpPacket;
import org.openrs2.deob.annotation.OriginalArg;
import com.jagex.game.runetek5.buffer.BitPacket;
import com.jagex.game.runetek5.network.packet.ServerProt;
import com.jagex.game.runetek5.statics.OutboundByteCounter;
import java.io.IOException;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.sound.player.area.SoundEffectPlayer;
import com.jagex.game.runetek5.MainLogicManager;
import com.jagex.game.runetek5.client.ClientState;

public final class BufferedConnectionContext {
	@OriginalMember(owner = "client!mr", name = "n", descriptor = "Z")
	public static boolean dropped = false;

	@OriginalMember(owner = "client!be", name = "p", descriptor = "I")
	public static int readDataSize = 0;

	@OriginalMember(owner = "client!hba", name = "a", descriptor = "I")
	public static int lastFlush = 0;

	private BufferedConnectionContext() {
		/* empty */
	}
	@OriginalMember(owner = "client!qo", name = "qe", descriptor = "Lclient!ej;")
	public static final ClientPacket WORLDMAP_CLICK = new ClientPacket(58, 4);

	@OriginalMember(owner = "client!oi", name = "I", descriptor = "Lclient!uh;")
	public static final Deque queuedPackets = new Deque();
	@OriginalMember(owner = "client!dfa", name = "a", descriptor = "(ILclient!wm;)V")
	public static void queuePacket(@OriginalArg(1) TcpPacket node) {
		BufferedConnectionContext.queuedPackets.addLast(node);
		node.capacity = node.buffer.currentPosition;
		node.buffer.currentPosition = 0;
		BufferedConnectionContext.readDataSize += node.capacity;
	}

	@OriginalMember(owner = "client!mo", name = "f", descriptor = "Z")
	public static boolean readPadding = true;
	@OriginalMember(owner = "client!ss", name = "b", descriptor = "Lclient!mu;")
	public static ServerProt lastPacket;

	@OriginalMember(owner = "client!tl", name = "g", descriptor = "(I)V")
	public static void flush() throws IOException {
		if (Connection.connection == null || BufferedConnectionContext.readDataSize <= 0) {
			return;
		}
		@Pc(10) int written = 0;
		while (true) {
			@Pc(17) TcpPacket packet = (TcpPacket) BufferedConnectionContext.queuedPackets.first();
			if (packet == null) {
				BufferedConnectionContext.lastFlush = 0;
				OutboundByteCounter.bytesWritten += written;
				break;
			}
			Connection.connection.write(packet.capacity, packet.buffer.buffer);
			written += packet.capacity;
			BufferedConnectionContext.readDataSize -= packet.capacity;
			packet.unlink();
			packet.buffer.flush();
			packet.push();
		}
	}

	@OriginalMember(owner = "client!cv", name = "r", descriptor = "I")
	public static int idleReadPulses = 0;

	@OriginalMember(owner = "client!tga", name = "O", descriptor = "I")
	public static int currentPacketSize = 0;

	@OriginalMember(owner = "client!wh", name = "b", descriptor = "Lclient!ua;")
	public static final BitPacket receivedBuffer = new BitPacket(7500);

	@OriginalMember(owner = "client!eu", name = "a", descriptor = "(I)V")
	public static void resetQueue() {
		BufferedConnectionContext.queuedPackets.clear();
		BufferedConnectionContext.readDataSize = 0;
	}

	@OriginalMember(owner = "client!eu", name = "G", descriptor = "Lclient!mu;")
	public static ServerProt currentPacket = null;
	@OriginalMember(owner = "client!vs", name = "a", descriptor = "(ZB)V")
	public static void sendKeepAlive(@OriginalArg(0) boolean force) {
		SoundEffectPlayer.updatePlayers();
		if (!MainLogicManager.isInGameStep(ClientState.mainLogicStep)) {
			return;
		}
		BufferedConnectionContext.lastFlush++;
		if (BufferedConnectionContext.lastFlush < 50 && !force) {
			return;
		}
		BufferedConnectionContext.lastFlush = 0;
		if (!BufferedConnectionContext.dropped && Connection.connection != null) {
			@Pc(39) TcpPacket packet = TcpPacket.createPacket(ClientProt.PING, TcpPacket.isaac);
			BufferedConnectionContext.queuePacket(packet);
			try {
				BufferedConnectionContext.flush();
			} catch (@Pc(46) IOException ex) {
				BufferedConnectionContext.dropped = true;
			}
		}
		SoundEffectPlayer.updatePlayers();
	}
	@OriginalMember(owner = "client!su", name = "l", descriptor = "Lclient!mu;")
	public static ServerProt secondLastPacket;
	@OriginalMember(owner = "client!haa", name = "kb", descriptor = "J")
	public static long NEXT_PING_UPDATE_TO_SERVER;
}
