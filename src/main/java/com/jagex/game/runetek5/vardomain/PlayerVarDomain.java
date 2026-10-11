package com.jagex.game.runetek5.vardomain;

import com.jagex.game.runetek5.cache.node.LongNode;
import com.jagex.game.runetek5.config.varp.VarbitType;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.vardomain.VarDomain;
import com.jagex.game.runetek5.config.vartype.player.VarPlayerType;
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!pia")
public final class PlayerVarDomain implements VarDomain {

	@OriginalMember(owner = "client!pia", name = "i", descriptor = "Lclient!eq;")
	private IterableHashTable updates = new IterableHashTable(128);

	@OriginalMember(owner = "client!pia", name = "b", descriptor = "[I")
	public final int[] activeVars = new int[TypeLists.VAR.num];

	@OriginalMember(owner = "client!pia", name = "q", descriptor = "[I")
	private final int[] pendingVars = new int[TypeLists.VAR.num];

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(BZ)I")
	public int removeNext(@OriginalArg(1) boolean first) {
		@Pc(20) long time = Utils.currentTimeMillis();
		for (@Pc(34) LongNode node = first ? (LongNode) this.updates.first() : (LongNode) this.updates.next(); node != null; node = (LongNode) this.updates.next()) {
			if ((node.value & 0x3FFFFFFFFFFFFFFFL) < time) {
				if ((node.value & 0x4000000000000000L) != 0L) {
					@Pc(58) int key = (int) node.key;
					this.activeVars[key] = this.pendingVars[key];
					node.unlink();
					return key;
				}
				node.unlink();
			}
		}
		return -1;
	}

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(BII)V")
	public void setVarValueInt(@OriginalArg(1) int id, @OriginalArg(2) int value) {
		this.activeVars[value] = id;
		@Pc(20) LongNode node = (LongNode) this.updates.get((long) value);
		if (node == null) {
			node = new LongNode(Utils.currentTimeMillis() + 500L);
			this.updates.put((long) value, node);
		} else {
			node.value = Utils.currentTimeMillis() + 500L;
		}
	}

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(II)I")
	@Override
	public int getVarBitValue(@OriginalArg(0) int id) {
		@Pc(8) VarbitType type = TypeLists.VARBIT.list(id);
		@Pc(11) int baseVar = type.configIndex;
		@Pc(19) int startBit = type.startBit;
		@Pc(22) int endBit = type.endBit;
		@Pc(29) int mask = EmissiveTriangle.BIT_MASKS[endBit - startBit];
		return this.activeVars[baseVar] >> startBit & mask;
	}

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(III)V")
	public void setVarBitValue(@OriginalArg(0) int varbitId, @OriginalArg(2) int value) {
		@Pc(8) VarbitType varbitType = TypeLists.VARBIT.list(varbitId);
		@Pc(11) int configIndex = varbitType.configIndex;
		@Pc(14) int startBit = varbitType.startBit;
		@Pc(17) int endBit = varbitType.endBit;
		@Pc(24) int bitmask = EmissiveTriangle.BIT_MASKS[endBit - startBit];
		if (value < 0 || value > bitmask) {
			value = 0;
		}
		bitmask <<= startBit;
		this.setVarValueInt(value << startBit & bitmask | this.activeVars[configIndex] & ~bitmask, configIndex);
	}

	@OriginalMember(owner = "client!pia", name = "b", descriptor = "(BII)V")
	public void updateVarp(@OriginalArg(1) int value, @OriginalArg(2) int id) {
		this.pendingVars[id] = value;
		@Pc(23) LongNode node = (LongNode) this.updates.get((long) id);
		if (node == null) {
			node = new LongNode(4611686018427387905L);
			this.updates.put((long) id, node);
		} else if (node.value != 4611686018427387905L) {
			node.value = Utils.currentTimeMillis() + 500L | 0x4000000000000000L;
		}
	}

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(B)V")
	public void reset() {
		for (@Pc(3) int i = 0; i < TypeLists.VAR.num; i++) {
			@Pc(10) VarPlayerType type = TypeLists.VAR.list(i);
			if (type != null && type.clientCode == 0) {
				this.pendingVars[i] = 0;
				this.activeVars[i] = 0;
			}
		}
		this.updates = new IterableHashTable(128);
	}

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(IB)I")
	@Override
	public int getVarValueInt(@OriginalArg(0) int id) {
		return this.activeVars[id];
	}

	@OriginalMember(owner = "client!pia", name = "c", descriptor = "(BII)V")
	public void setVarBitValueFromServer(@OriginalArg(1) int varbitId, @OriginalArg(2) int value) {
		@Pc(8) VarbitType varbitType = TypeLists.VARBIT.list(varbitId);
		@Pc(11) int configIndex = varbitType.configIndex;
		@Pc(14) int startBit = varbitType.startBit;
		@Pc(21) int endBit = varbitType.endBit;
		@Pc(27) int bitmask = EmissiveTriangle.BIT_MASKS[endBit - startBit];
		if (value < 0 || bitmask < value) {
			value = 0;
		}
		bitmask <<= startBit;
		this.updateVarp(value << startBit & bitmask | ~bitmask & this.pendingVars[configIndex], configIndex);
	}
	@OriginalMember(owner = "client!kda", name = "ab", descriptor = "Lclient!pia;")
	public static PlayerVarDomain playerVarProvider;
	@OriginalMember(owner = "client!fv", name = "r", descriptor = "Z")
	public static boolean varVerifyIdPending = false;

	@OriginalMember(owner = "client!saa", name = "e", descriptor = "(B)Z")
	public static boolean requestVarVerifyid() {
		PlayerVarDomain.varVerifyId++;
		PlayerVarDomain.varVerifyIdPending = true;
		return true;
	}

	@OriginalMember(owner = "client!vp", name = "k", descriptor = "I")
	public static int pulseEventConfigsPtr = 0;

	@OriginalMember(owner = "client!jt", name = "w", descriptor = "I")
	public static int varVerifyId = 0;

	@OriginalMember(owner = "client!de", name = "g", descriptor = "[I")
	public static final int[] LOGIN_XTEAS = new int[32];

	@OriginalMember(owner = "client!ch", name = "a", descriptor = "(I)V")
	public static void sendTransmitVarVerifyId() {
		@Pc(18) TcpPacket packet = TcpPacket.createPacket(ClientProt.TRANSMITVAR_VERIFYID, TcpPacket.isaac);
		packet.buffer.writeShort(PlayerVarDomain.varVerifyId);
		BufferedConnectionContext.queuePacket(packet);
	}
}
