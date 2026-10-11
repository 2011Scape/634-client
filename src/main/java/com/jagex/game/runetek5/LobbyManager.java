package com.jagex.game.runetek5;

import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.sign.SignedResourceStatus;
import java.io.IOException;
import java.net.Socket;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.client.login.LoginProt;
import com.jagex.game.runetek5.network.AsyncConnection;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.network.Connection;
import com.jagex.game.runetek5.statics.BrowserJavaScript;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

public final class LobbyManager {

	@OriginalMember(owner = "client!iq", name = "a", descriptor = "(Ljava/lang/String;Ljava/lang/String;ZIZ)V")
	public static void sendCreateConnectRequest(@OriginalArg(0) String string, @OriginalArg(1) String string2, @OriginalArg(3) int age, @OriginalArg(4) boolean agreedToTerms) {
		@Pc(6) TcpPacket packet = TcpPacket.createLoginPacket();
		packet.buffer.writeByte(LoginProt.aClass141_9.id);
		packet.buffer.writeShort(0);
		@Pc(21) int currentPosition = packet.buffer.currentPosition;
		packet.buffer.writeShort(634);
		@Pc(30) int[] xteaKeys = LobbyManager.writeRsaBlock(packet);
		@Pc(34) int xteaStart = packet.buffer.currentPosition;
		packet.buffer.writeString(string2);
		packet.buffer.writeShort(GameShell.AFFILIATE);
		packet.buffer.writeString(string);
		packet.buffer.writeLong(GameShell.machineUid);
		packet.buffer.writeByte(ClientState.language);
		packet.buffer.writeByte(Game.CURRENT_GAME.id);
		LoginManager.pushRandomDat(packet.buffer);
		@Pc(71) String string3 = GameShell.additionalInfo;
		packet.buffer.writeByte(string3 == null ? 0 : 1);
		if (string3 != null) {
			packet.buffer.writeString(string3);
		}
		packet.buffer.writeByte(age);
		packet.buffer.writeByte(agreedToTerms ? 1 : 0);
		packet.buffer.currentPosition += 7;
		packet.buffer.encryptXtea(xteaStart, xteaKeys, packet.buffer.currentPosition);
		packet.buffer.writeShortLength(packet.buffer.currentPosition - currentPosition);
		BufferedConnectionContext.queuePacket(packet);
		LobbyManager.accountCreationReply = -3;
		LobbyManager.accountCreationStep = 1;
		LobbyManager.accountCreationTimeoutCycles = 0;
		LobbyManager.accountCreationRetries = 0;
		if (age < 13) {
			LobbyManager.isUnder13 = true;
			LobbyManager.setUnder13Cookie();
		}
	}

	@OriginalMember(owner = "client!fba", name = "a", descriptor = "(I)V")
	public static void processAccountCreation() {
		if (LobbyManager.accountCreationStep == 0) {
			return;
		}
		try {
			if (++LobbyManager.accountCreationTimeoutCycles > 2000) {
				if (Connection.connection != null) {
					Connection.connection.close();
					Connection.connection = null;
				}
				if (LobbyManager.accountCreationRetries >= 2) {
					LobbyManager.accountCreationStep = 0;
					LobbyManager.accountCreationReply = -5;
					return;
				}
				LoginManager.CURRENT_CONNECTION_CONTEXT.cycleConnectionMode();
				LobbyManager.accountCreationStep = 1;
				LobbyManager.accountCreationTimeoutCycles = 0;
				LobbyManager.accountCreationRetries++;
			}
			if (LobbyManager.accountCreationStep == 1) {
				LoginManager.aClass240_3 = LoginManager.CURRENT_CONNECTION_CONTEXT.openSocket(GameShell.signLink);
				LobbyManager.accountCreationStep = 2;
			}
			if (LobbyManager.accountCreationStep == 2) {
				if (LoginManager.aClass240_3.status == SignedResourceStatus.ERROR) {
					throw new IOException();
				}
				if (LoginManager.aClass240_3.status != SignedResourceStatus.SUCCESS) {
					return;
				}
				Connection.connection = AsyncConnection.create((Socket) LoginManager.aClass240_3.result);
				LoginManager.aClass240_3 = null;
				BufferedConnectionContext.flush();
				LobbyManager.accountCreationStep = 4;
			}
			if (LobbyManager.accountCreationStep == 4 && Connection.connection.hasAvailable(1)) {
				Connection.connection.read(1, 0, BufferedConnectionContext.receivedBuffer.buffer);
				@Pc(117) int reply = BufferedConnectionContext.receivedBuffer.buffer[0] & 0xFF;
				LobbyManager.accountCreationReply = reply;
				LobbyManager.accountCreationStep = 0;
				Connection.connection.close();
				Connection.connection = null;
			}
		} catch (@Pc(128) IOException ex) {
			if (Connection.connection != null) {
				Connection.connection.close();
				Connection.connection = null;
			}
			if (LobbyManager.accountCreationRetries < 2) {
				LoginManager.CURRENT_CONNECTION_CONTEXT.cycleConnectionMode();
				LobbyManager.accountCreationTimeoutCycles = 0;
				LobbyManager.accountCreationRetries++;
				LobbyManager.accountCreationStep = 1;
			} else {
				LobbyManager.accountCreationReply = -4;
				LobbyManager.accountCreationStep = 0;
			}
		}
	}

	@OriginalMember(owner = "client!vea", name = "a", descriptor = "(Ljava/lang/String;I)V")
	public static void sendCreateAvailableRequest(@OriginalArg(0) String string) {
		@Pc(6) TcpPacket packet = TcpPacket.createLoginPacket();
		packet.buffer.writeByte(LoginProt.aClass141_14.id);
		packet.buffer.writeShort(0);
		@Pc(21) int currentPosition = packet.buffer.currentPosition;
		packet.buffer.writeShort(634);
		@Pc(30) int[] xteaKeys = LobbyManager.writeRsaBlock(packet);
		@Pc(34) int xteaStart = packet.buffer.currentPosition;
		packet.buffer.writeString(string);
		packet.buffer.writeByte(ClientState.language);
		packet.buffer.currentPosition += 7;
		packet.buffer.encryptXtea(xteaStart, xteaKeys, packet.buffer.currentPosition);
		packet.buffer.writeShortLength(packet.buffer.currentPosition - currentPosition);
		BufferedConnectionContext.queuePacket(packet);
		LobbyManager.accountCreationStep = 1;
		LobbyManager.accountCreationReply = -3;
		LobbyManager.accountCreationTimeoutCycles = 0;
		LobbyManager.accountCreationRetries = 0;
	}
	@OriginalMember(owner = "client!jd", name = "i", descriptor = "I")
	public static int accountCreationRetries = 0;
	@OriginalMember(owner = "client!pha", name = "h", descriptor = "I")
	public static int accountCreationTimeoutCycles = 0;
	@OriginalMember(owner = "client!dg", name = "r", descriptor = "Z")
	public static boolean isUnder13 = false;

	@OriginalMember(owner = "client!el", name = "h", descriptor = "I")
	public static int accountCreationReply = -2;

	@OriginalMember(owner = "client!km", name = "K", descriptor = "I")
	public static int lobbyMembershipStatus;

	@OriginalMember(owner = "client!uv", name = "f", descriptor = "(I)V")
	public static void setUnder13Cookie() {
		if (GameShell.applet == null) {
			return;
		}
		try {
			@Pc(18) String string = GameShell.applet.getParameter("cookiehost");
			@Pc(26) int runeDay = (int) (Utils.currentTimeMillis() / TimeUtils.MILLISECONDS_PER_DAY) - TimeUtils.RUNEDAYS_SINCE_UNIX_EPOCH;
			@Pc(38) String string2 = "usrdob=" + runeDay + "; version=1; path=/; domain=" + string;
			BrowserJavaScript.evalJavaScript(GameShell.applet, "document.cookie=\"" + string2 + "\"");
		} catch (@Pc(52) Throwable ex) {
		}
	}
	@OriginalMember(owner = "client!gb", name = "a", descriptor = "(Lclient!wm;Z)[I")
	public static int[] writeRsaBlock(@OriginalArg(0) TcpPacket tcpPacket) {
		@Pc(10) Packet packet = new Packet(518);
		@Pc(13) int[] xteaKeys = new int[4];
		for (@Pc(15) int i = 0; i < 4; i++) {
			xteaKeys[i] = (int) (Math.random() * 9.9999999E7D);
		}
		packet.writeByte(10);
		packet.writeInt(xteaKeys[0]);
		packet.writeInt(xteaKeys[1]);
		packet.writeInt(xteaKeys[2]);
		packet.writeInt(xteaKeys[3]);
		for (@Pc(60) int j = 0; j < 10; j++) {
			packet.writeInt((int) (Math.random() * 9.9999999E7D));
		}
		packet.writeShort((int) (Math.random() * 9.9999999E7D));
		packet.applyRSA(LoginManager.rsaModulus, LoginManager.rsaExponent);
		tcpPacket.buffer.writeBytes(packet.currentPosition, 0, packet.buffer);
		return xteaKeys;
	}

	@OriginalMember(owner = "client!ov", name = "t", descriptor = "I")
	public static int accountCreationStep = 0;
}
