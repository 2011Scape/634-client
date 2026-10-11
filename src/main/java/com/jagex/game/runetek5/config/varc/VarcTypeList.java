package com.jagex.game.runetek5.config.varc;

import com.jagex.game.runetek5.config.varc.VarcType;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.JFS32;
import com.jagex.game.runetek5.sign.SignLinkEnvironment;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.sign.SignedResource;
import com.jagex.game.runetek5.sign.SignedResourceStatus;
import com.jagex.game.runetek5.utils.Utils;
import java.io.IOException;

@OriginalClass("client!uea")
public final class VarcTypeList {

	@OriginalMember(owner = "client!uea", name = "e", descriptor = "Lclient!jr;")
	private final LRUCache recentUse = new LRUCache(64);

	@OriginalMember(owner = "client!uea", name = "a", descriptor = "Lclient!in;")
	private final Js5ResourceProvider configClient;

	@OriginalMember(owner = "client!uea", name = "g", descriptor = "I")
	public final int num;

	@OriginalMember(owner = "client!uea", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V")
	public VarcTypeList(@OriginalArg(0) Game modeGame, @OriginalArg(1) int languageId, @OriginalArg(2) Js5ResourceProvider configClient) {
		this.configClient = configClient;
		this.num = this.configClient.getGroupCapacity(19);
	}

	@OriginalMember(owner = "client!uea", name = "a", descriptor = "(II)Lclient!pa;")
	public VarcType list(@OriginalArg(0) int id) {
		@Pc(6) LRUCache referenceCache = this.recentUse;
		@Pc(16) VarcType varcType;
		synchronized (this.recentUse) {
			varcType = (VarcType) this.recentUse.get((long) id);
		}
		if (varcType != null) {
			return varcType;
		}
		@Pc(37) Js5ResourceProvider configClient = this.configClient;
		@Pc(46) byte[] data;
		synchronized (this.configClient) {
			data = this.configClient.getFile(19, id);
		}
		varcType = new VarcType();
		if (data != null) {
			varcType.decode(new Packet(data));
		}
		@Pc(68) LRUCache referenceCache2 = this.recentUse;
		synchronized (this.recentUse) {
			this.recentUse.put(varcType, (long) id);
			return varcType;
		}
	}
	@OriginalMember(owner = "client!tia", name = "m", descriptor = "[Z")
	public static boolean[] VARC_PERSISTENT;

	@OriginalMember(owner = "client!q", name = "t", descriptor = "[Ljava/lang/String;")
	public static String[] VARC_STRING;

	@OriginalMember(owner = "client!ku", name = "o", descriptor = "[I")
	public static int[] VARC_INT;
	@OriginalMember(owner = "client!fha", name = "a", descriptor = "(I)V")
	public static void savePersistentVarcs() {
		@Pc(7) JFS32 fileOnDisk = null;
		try {
			fileOnDisk = SignLinkEnvironment.openGamePreferencesFile("2");
			@Pc(20) Packet packet = new Packet(VarcTypeList.persistentVarcCount * 6 + 3);
			packet.writeByte(1);
			packet.writeShort(VarcTypeList.persistentVarcCount);
			for (@Pc(38) int i = 0; i < VarcTypeList.VARC_INT.length; i++) {
				if (VarcTypeList.VARC_PERSISTENT[i]) {
					packet.writeShort(i);
					packet.writeInt(VarcTypeList.VARC_INT[i]);
				}
			}
			fileOnDisk.write(0, packet.currentPosition, packet.buffer);
		} catch (@Pc(73) Exception ex) {
		}
		try {
			if (fileOnDisk != null) {
				fileOnDisk.close();
			}
		} catch (@Pc(80) Exception ex2) {
		}
		VarcTypeList.exitTime = Utils.currentTimeMillis();
		VarcTypeList.clientIsActive = false;
	}
	@OriginalMember(owner = "client!pi", name = "a", descriptor = "(Lclient!cea;B)V")
	public static void decodePersistentVarcs(@OriginalArg(0) Packet packet) {
		if (packet.buffer.length - packet.currentPosition < 1) {
			return;
		}
		@Pc(20) int version = packet.readUByte();
		if (version < 0 || version > 1 || packet.buffer.length - packet.currentPosition < 2) {
			return;
		}
		@Pc(55) int count = packet.readUShort();
		if (count * 6 > packet.buffer.length - packet.currentPosition) {
			return;
		}
		for (@Pc(69) int i = 0; i < count; i++) {
			@Pc(75) int varcId = packet.readUShort();
			@Pc(79) int value = packet.readInt();
			if (varcId < VarcTypeList.VARC_INT.length && VarcTypeList.VARC_PERSISTENT[varcId] && (TypeLists.aClass338_1.list(varcId).typeChar != '1' || value >= -1 && value <= 1)) {
				VarcTypeList.VARC_INT[varcId] = value;
			}
		}
	}

	@OriginalMember(owner = "client!qea", name = "b", descriptor = "Z")
	public static boolean clientIsActive = false;
	@OriginalMember(owner = "client!vj", name = "K", descriptor = "I")
	public static int varcStrUpdateCycle = 0;

	@OriginalMember(owner = "client!vu", name = "s", descriptor = "[I")
	public static final int[] recentVarcStrUpdates = new int[32];

	@OriginalMember(owner = "client!ic", name = "u", descriptor = "J")
	public static long exitTime = -1L;

	@OriginalMember(owner = "client!nb", name = "a", descriptor = "(I)V")
	public static void loadPersistentVarcs() {
		@Pc(12) JFS32 fileOnDisk = null;
		try {
			@Pc(18) SignedResource signedResource = GameShell.signLink.openCacheFile("2");
			while (signedResource.status == SignedResourceStatus.IDLE) {
				TimeUtils.sleep(1L);
			}
			if (signedResource.status == SignedResourceStatus.SUCCESS) {
				fileOnDisk = (JFS32) signedResource.result;
				@Pc(45) byte[] data = new byte[(int) fileOnDisk.length()];
				@Pc(60) int bytesRead;
				for (@Pc(47) int i = 0; i < data.length; i += bytesRead) {
					bytesRead = fileOnDisk.read(data, i, data.length - i);
					if (bytesRead == -1) {
						throw new IOException("EOF");
					}
				}
				VarcTypeList.decodePersistentVarcs(new Packet(data));
			}
		} catch (@Pc(89) Exception ex) {
		}
		try {
			if (fileOnDisk != null) {
				fileOnDisk.close();
			}
		} catch (@Pc(96) Exception ex2) {
		}
	}

	@OriginalMember(owner = "client!ce", name = "I", descriptor = "I")
	public static int varcUpdateCycle = 0;

	@OriginalMember(owner = "client!ds", name = "c", descriptor = "I")
	public static int persistentVarcCount = 0;
}
