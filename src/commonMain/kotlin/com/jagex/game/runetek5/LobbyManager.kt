package com.jagex.game.runetek5

import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.sign.SignedResourceStatus
import io.IOException
import net.Socket
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.client.login.LoginProt
import com.jagex.game.runetek5.network.AsyncConnection
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.network.Connection
import com.jagex.game.runetek5.statics.BrowserJavaScript
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import secureSeed

object LobbyManager {

	@OriginalMember(owner = "client!iq", name = "a", descriptor = "(Ljava/lang/String;Ljava/lang/String;ZIZ)V")
	@JvmStatic
	fun sendCreateConnectRequest(@OriginalArg(0) string: String?, @OriginalArg(1) string2: String?, @OriginalArg(3) age: Int, @OriginalArg(4) agreedToTerms: Boolean) {
		@Pc(6) val packet: TcpPacket = TcpPacket.createLoginPacket()
		packet.buffer!!.writeByte(LoginProt.aClass141_9.id)
		packet.buffer!!.writeShort(0)
		@Pc(21) val currentPosition: Int = packet.buffer!!.currentPosition
		packet.buffer!!.writeShort(634)
		@Pc(30) val xteaKeys: IntArray? = LobbyManager.writeRsaBlock(packet)
		@Pc(34) val xteaStart: Int = packet.buffer!!.currentPosition
		packet.buffer!!.writeString(string2)
		packet.buffer!!.writeShort(GameShell.AFFILIATE)
		packet.buffer!!.writeString(string)
		packet.buffer!!.writeLong(GameShell.machineUid)
		packet.buffer!!.writeByte(ClientState.language)
		packet.buffer!!.writeByte(Game.CURRENT_GAME!!.id)
		LoginManager.pushRandomDat(packet.buffer)
		@Pc(71) val string3: String? = GameShell.additionalInfo
		packet.buffer!!.writeByte(if (string3 == null) 0 else 1)
		if (string3 != null) {
			packet.buffer!!.writeString(string3)
		}
		packet.buffer!!.writeByte(age)
		packet.buffer!!.writeByte(if (agreedToTerms) 1 else 0)
		packet.buffer!!.currentPosition += 7
		packet.buffer!!.encryptXtea(xteaStart, xteaKeys, packet.buffer!!.currentPosition)
		packet.buffer!!.writeShortLength(packet.buffer!!.currentPosition - currentPosition)
		BufferedConnectionContext.queuePacket(packet)
		LobbyManager.accountCreationReply = -3
		LobbyManager.accountCreationStep = 1
		LobbyManager.accountCreationTimeoutCycles = 0
		LobbyManager.accountCreationRetries = 0
		if (age < 13) {
			LobbyManager.isUnder13 = true
			LobbyManager.setUnder13Cookie()
		}
	}

	@OriginalMember(owner = "client!fba", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun processAccountCreation() {
		if (LobbyManager.accountCreationStep == 0) {
			return
		}
		try {
			if (++LobbyManager.accountCreationTimeoutCycles > 2000) {
				if (Connection.connection != null) {
					Connection.connection!!.close()
					Connection.connection = null
				}
				if (LobbyManager.accountCreationRetries >= 2) {
					LobbyManager.accountCreationStep = 0
					LobbyManager.accountCreationReply = -5
					return
				}
				LoginManager.CURRENT_CONNECTION_CONTEXT!!.cycleConnectionMode()
				LobbyManager.accountCreationStep = 1
				LobbyManager.accountCreationTimeoutCycles = 0
				LobbyManager.accountCreationRetries++
			}
			if (LobbyManager.accountCreationStep == 1) {
				LoginManager.aClass240_3 = LoginManager.CURRENT_CONNECTION_CONTEXT!!.openSocket(GameShell.signLink)
				LobbyManager.accountCreationStep = 2
			}
			if (LobbyManager.accountCreationStep == 2) {
				if (LoginManager.aClass240_3!!.status == SignedResourceStatus.ERROR) {
					throw IOException()
				}
				if (LoginManager.aClass240_3!!.status != SignedResourceStatus.SUCCESS) {
					return
				}
				Connection.connection = AsyncConnection.create(LoginManager.aClass240_3!!.result as Socket?)
				LoginManager.aClass240_3 = null
				BufferedConnectionContext.flush()
				LobbyManager.accountCreationStep = 4
			}
			if (LobbyManager.accountCreationStep == 4 && Connection.connection!!.hasAvailable(1)) {
				Connection.connection!!.read(1, 0, BufferedConnectionContext.receivedBuffer.buffer)
				@Pc(117) val reply: Int = BufferedConnectionContext.receivedBuffer.buffer!![0].toInt() and 0xFF
				LobbyManager.accountCreationReply = reply
				LobbyManager.accountCreationStep = 0
				Connection.connection!!.close()
				Connection.connection = null
			}
		} catch (@Pc(128) ex: IOException) {
			if (Connection.connection != null) {
				Connection.connection!!.close()
				Connection.connection = null
			}
			if (LobbyManager.accountCreationRetries < 2) {
				LoginManager.CURRENT_CONNECTION_CONTEXT!!.cycleConnectionMode()
				LobbyManager.accountCreationTimeoutCycles = 0
				LobbyManager.accountCreationRetries++
				LobbyManager.accountCreationStep = 1
			} else {
				LobbyManager.accountCreationReply = -4
				LobbyManager.accountCreationStep = 0
			}
		}
	}

	@OriginalMember(owner = "client!vea", name = "a", descriptor = "(Ljava/lang/String;I)V")
	@JvmStatic
	fun sendCreateAvailableRequest(@OriginalArg(0) string: String?) {
		@Pc(6) val packet: TcpPacket = TcpPacket.createLoginPacket()
		packet.buffer!!.writeByte(LoginProt.aClass141_14.id)
		packet.buffer!!.writeShort(0)
		@Pc(21) val currentPosition: Int = packet.buffer!!.currentPosition
		packet.buffer!!.writeShort(634)
		@Pc(30) val xteaKeys: IntArray? = LobbyManager.writeRsaBlock(packet)
		@Pc(34) val xteaStart: Int = packet.buffer!!.currentPosition
		packet.buffer!!.writeString(string)
		packet.buffer!!.writeByte(ClientState.language)
		packet.buffer!!.currentPosition += 7
		packet.buffer!!.encryptXtea(xteaStart, xteaKeys, packet.buffer!!.currentPosition)
		packet.buffer!!.writeShortLength(packet.buffer!!.currentPosition - currentPosition)
		BufferedConnectionContext.queuePacket(packet)
		LobbyManager.accountCreationStep = 1
		LobbyManager.accountCreationReply = -3
		LobbyManager.accountCreationTimeoutCycles = 0
		LobbyManager.accountCreationRetries = 0
	}

	@OriginalMember(owner = "client!jd", name = "i", descriptor = "I")
	@JvmField
	var accountCreationRetries: Int = 0

	@OriginalMember(owner = "client!pha", name = "h", descriptor = "I")
	@JvmField
	var accountCreationTimeoutCycles: Int = 0

	@OriginalMember(owner = "client!dg", name = "r", descriptor = "Z")
	@JvmField
	var isUnder13: Boolean = false

	@OriginalMember(owner = "client!el", name = "h", descriptor = "I")
	@JvmField
	var accountCreationReply: Int = -2

	@OriginalMember(owner = "client!km", name = "K", descriptor = "I")
	@JvmField
	var lobbyMembershipStatus: Int = 0

	@OriginalMember(owner = "client!uv", name = "f", descriptor = "(I)V")
	@JvmStatic
	fun setUnder13Cookie() {
		if (GameShell.applet == null) {
			return
		}
		try {
			@Pc(18) val string: String? = GameShell.applet!!.getParameter("cookiehost")
			@Pc(26) val runeDay: Int = (Utils.currentTimeMillis() / TimeUtils.MILLISECONDS_PER_DAY).toInt() - TimeUtils.RUNEDAYS_SINCE_UNIX_EPOCH
			@Pc(38) val string2: String = "usrdob=" + runeDay + "; version=1; path=/; domain=" + string
			BrowserJavaScript.evalJavaScript(GameShell.applet, "document.cookie=\"" + string2 + "\"")
		} catch (@Pc(52) ex: Throwable) {
		}
	}

	@OriginalMember(owner = "client!gb", name = "a", descriptor = "(Lclient!wm;Z)[I")
	@JvmStatic
	fun writeRsaBlock(@OriginalArg(0) tcpPacket: TcpPacket?): IntArray? {
		@Pc(10) val packet: Packet = Packet(518)
		@Pc(13) val xteaKeys: IntArray = IntArray(4)
		@Pc(15) var i: Int = 0
		while (i < 4) {
			// Not part of the original client: the keys come from a cryptographic random source.
			xteaKeys[i] = secureSeed()
			i++
		}
		packet.writeByte(10)
		packet.writeInt(xteaKeys[0])
		packet.writeInt(xteaKeys[1])
		packet.writeInt(xteaKeys[2])
		packet.writeInt(xteaKeys[3])
		@Pc(60) var j: Int = 0
		while (j < 10) {
			packet.writeInt((util.random() * 9.9999999E7).toInt())
			j++
		}
		packet.writeShort((util.random() * 9.9999999E7).toInt())
		packet.applyRSA(LoginManager.rsaModulus, LoginManager.rsaExponent)
		tcpPacket!!.buffer!!.writeBytes(packet.currentPosition, 0, packet.buffer)
		return xteaKeys
	}

	@OriginalMember(owner = "client!ov", name = "t", descriptor = "I")
	@JvmField
	var accountCreationStep: Int = 0
}
