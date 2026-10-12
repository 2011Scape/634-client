package com.jagex.game.runetek5.client.login

import com.jagex.game.runetek5.core.constants.LoginResponseCode
import com.jagex.game.runetek5.config.invtype.LoginDisallowReason
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.WorldConnectionInfo
import com.jagex.game.runetek5.buffer.Packet
import java.io.IOException
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.features.ClientFeatures
import java.math.BigInteger
import com.jagex.game.runetek5.sign.SignedResource
import com.jagex.game.runetek5.client.ClientState

object LoginManager {

	@OriginalMember(owner = "client!gn", name = "f", descriptor = "I")
	@JvmField
	var lastDisallowResult: Int = -1

	@OriginalMember(owner = "client!su", name = "i", descriptor = "I")
	@JvmField
	var dirtyAreaMinY: Int = 0

	@OriginalMember(owner = "client!nr", name = "a", descriptor = "(Ljava/lang/String;ZLjava/lang/String;B)V")
	@JvmStatic
	fun initializeLogin(@OriginalArg(0) string: String?, @OriginalArg(1) hasStoredCredentials: Boolean, @OriginalArg(2) string2: String?) {
		LoginManager.loginUsername = string
		LoginManager.loginPassword = string2
		LoginManager.hasStoredCredentials = hasStoredCredentials
		if (!LoginManager.hasStoredCredentials && (LoginManager.loginUsername!! == "" || LoginManager.loginPassword!! == "")) {
			LoginManager.setLoginResponse(LoginResponseCode.INVALID_USERNAME_OR_PASSWORD)
			return
		}
		if (LoginManager.lobbyStage != 1) {
			LoginManager.loginHopTime = 0
			LoginDisallowReason.loginDisallowReason = -1
		}
		BufferedConnectionContext.dropped = false
		LoginManager.setLoginResponse(-3)
		LoginManager.loginStep = LoginStep.CONNECT
		LoginManager.delay = 0
		LoginManager.connectionAttempts = 0
	}
	@OriginalMember(owner = "client!ub", name = "u", descriptor = "Lclient!pc;")
	@JvmField
	var CURRENT_CONNECTION_CONTEXT: WorldConnectionInfo? = null

	@OriginalMember(owner = "client!ad", name = "c", descriptor = "Z")
	@JvmField
	var currentlyInLobby: Boolean = false

	@OriginalMember(owner = "client!lh", name = "i", descriptor = "Z")
	@JvmField
	var worldMapUseTargetPosition: Boolean = false
	@OriginalMember(owner = "client!aq", name = "j", descriptor = "I")
	@JvmField
	var lobbyCreditCardExpiry: Int = 0

	@OriginalMember(owner = "client!k", name = "e", descriptor = "I")
	@JvmField
	var loginHopTime: Int = 0
	@OriginalMember(owner = "client!rg", name = "g", descriptor = "I")
	@JvmField
	var lastLoginDay: Int = 0

	@OriginalMember(owner = "client!afa", name = "i", descriptor = "I")
	@JvmField
	var unreadMessages: Int = 0

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(Lclient!cea;B)V")
	@JvmStatic
	fun pushRandomDat(@OriginalArg(0) packet: Packet?) {
		@Pc(8) val uid: ByteArray = ByteArray(24)
		if (GameShell.aClass184_2 != null) {
			@Pc(24) var index: Int
			try {
				GameShell.aClass184_2!!.seek(0L)
				GameShell.aClass184_2!!.read(uid)
				index = 0
				while (index < 24 && uid[index].toInt() == 0) {
					index++
				}
				if (index >= 24) {
					throw IOException()
				}
			} catch (@Pc(48) ex: Exception) {
				index = 0
				while (index < 24) {
					uid[index] = -1
					index++
				}
			}
		}
		packet!!.writeBytes(24, 0, uid)
	}

	@OriginalMember(owner = "client!jt", name = "u", descriptor = "I")
	@JvmField
	var recoveryQuestionsSetDay: Int = 0

	@OriginalMember(owner = "client!ab", name = "u", descriptor = "I")
	@JvmField
	var lobbyPlayAge: Int = 0
	@OriginalMember(owner = "client!qca", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun lobbyLoginWithStoredCredentials() {
		LoginManager.anInt6172 = -1
		LoginManager.lobbyStage = 1
		if (LoginManager.ssKey == null) {
			LoginManager.setLoginResponse(35)
		} else {
			@Pc(20) val packet: Packet = Packet(Utils.base64Decode(Utils.urlDecode(LoginManager.ssKey!!)))
			@Pc(24) val encodedUsername: Long = packet.readLong()
			LoginManager.ssKeyNonce = packet.readLong()
			LoginManager.initializeLogin(Utils.nameForLong(encodedUsername), true, "")
		}
	}

	@OriginalMember(owner = "client!bc", name = "b", descriptor = "(B)V")
	@JvmStatic
	fun confirmLoginContinue() {
		if (LoginManager.loginStep == LoginStep.SHOWING_ADVERTISEMENT) {
			LoginManager.loginStep = LoginStep.ADVERTISEMENT_FINISHED
		}
	}

	@OriginalMember(owner = "client!kda", name = "Db", descriptor = "Z")
	@JvmField
	var hasStoredCredentials: Boolean = false

	@OriginalMember(owner = "client!kaa", name = "a", descriptor = "(IB)V")
	@JvmStatic
	fun worldLoginWithStoredCredentials(@OriginalArg(0) arg0: Int) {
		LoginManager.lobbyStage = 2
		LoginManager.anInt6172 = arg0
		if (LoginManager.ssKey == null) {
			LoginManager.setLoginResponse(35)
		} else {
			@Pc(24) val packet: Packet = Packet(Utils.base64Decode(Utils.urlDecode(LoginManager.ssKey!!)))
			@Pc(28) val encodedUsername: Long = packet.readLong()
			LoginManager.ssKeyNonce = packet.readLong()
			LoginManager.initializeLogin(Utils.nameForLong(encodedUsername), true, "")
		}
	}
	@OriginalMember(owner = "client!cd", name = "a", descriptor = "Z")
	@JvmField
	var isDobRequested: Boolean = false

	@OriginalMember(owner = "client!sg", name = "db", descriptor = "I")
	@JvmField
	var loginStep: LoginStep? = LoginStep.IDLE
	@OriginalMember(owner = "client!hba", name = "e", descriptor = "I")
	@JvmField
	var delay: Int = 0
	@OriginalMember(owner = "client!mn", name = "d", descriptor = "I")
	@JvmField
	var lobbyLoginResponse: Int = -2

	@OriginalMember(owner = "client!raa", name = "A", descriptor = "J")
	@JvmField
	var ssKeyNonce: Long = 0L

	@OriginalMember(owner = "client!gba", name = "s", descriptor = "I")
	@JvmField
	var worldLoginResponse: Int = -2

	@OriginalMember(owner = "client!gba", name = "q", descriptor = "I")
	@JvmField
	var lobbyGraceExpiry: Int = 0

	@OriginalMember(owner = "client!u", name = "a", descriptor = "(Ljava/lang/String;ILjava/lang/String;Z)V")
	@JvmStatic
	fun worldLogin(@OriginalArg(0) string: String?, @OriginalArg(1) arg1: Int, @OriginalArg(2) string2: String?) {
		LoginManager.anInt6172 = arg1
		LoginManager.lobbyStage = 2
		LoginManager.initializeLogin(string2, false, string)
	}

	@OriginalMember(owner = "client!pia", name = "d", descriptor = "I")
	@JvmField
	var lastLoginIpAddress: Int = 0

	@OriginalMember(owner = "client!rg", name = "y", descriptor = "Z")
	@JvmField
	var isMember: Boolean = false

	@OriginalMember(owner = "client!ok", name = "a", descriptor = "(Ljava/lang/String;Ljava/lang/String;I)V")
	@JvmStatic
	fun lobbyLogin(@OriginalArg(0) string: String?, @OriginalArg(1) string2: String?) {
		LoginManager.anInt6172 = -1
		// Not part of the original client, which logs in to the lobby (1); Void has no lobby, so with
		// client.server=void the client logs in to the game world (2).
		LoginManager.lobbyStage = if (ClientFeatures.VOID) 2 else 1
		LoginManager.initializeLogin(string, false, string2)
	}

	@OriginalMember(owner = "client!kg", name = "c", descriptor = "I")
	@JvmField
	var anInt6172: Int = -1

	@OriginalMember(owner = "client!uo", name = "J", descriptor = "I")
	@JvmField
	var connectionAttempts: Int = 0

	@OriginalMember(owner = "client!tw", name = "c", descriptor = "(IB)V")
	@JvmStatic
	fun setLoginResponse(@OriginalArg(0) response: Int) {
		if (LoginManager.lobbyStage == 1) {
			LoginManager.lobbyLoginResponse = response
		} else if (LoginManager.lobbyStage == 2) {
			// Not part of the original client, which set only the world code here. The login screen
			// reads the lobby code, so with the lobby skipped (client.server=void) it would never show the reply.
			if (ClientFeatures.VOID) {
				LoginManager.lobbyLoginResponse = response
			}
			LoginManager.worldLoginResponse = response
		}
	}

	@OriginalMember(owner = "client!paa", name = "Jb", descriptor = "Ljava/lang/String;")
	@JvmField
	var loginUsername: String? = ""

	@OriginalMember(owner = "client!np", name = "g", descriptor = "Z")
	@JvmField
	var memberWorld: Boolean = false

	@OriginalMember(owner = "client!qg", name = "g", descriptor = "Ljava/lang/String;")
	@JvmField
	var loginPassword: String? = ""

	@OriginalMember(owner = "client!qfa", name = "E", descriptor = "Ljava/math/BigInteger;")
	@JvmField
	var rsaExponent: BigInteger? = BigInteger("10001", 16)
	@OriginalMember(owner = "client!gfa", name = "l", descriptor = "Z")
	@JvmField
	var quickchatOnly: Boolean = false

	@OriginalMember(owner = "client!pk", name = "z", descriptor = "Ljava/lang/String;")
	@JvmField
	var ssKey: String? = null

	@OriginalMember(owner = "client!av", name = "x", descriptor = "I")
	@JvmField
	var lobbyStage: Int = 0

	@OriginalMember(owner = "client!pi", name = "w", descriptor = "Ljava/math/BigInteger;")
	@JvmField
	var rsaModulus: BigInteger? = BigInteger("ea3680fdebf2621da7a33601ba39925ee203b3fc80775cd3727bf27fd8c0791c803e0bdb42b8b5257567177f8569024569da9147cef59009ed016af6007e57a556f1754f09ca84dd39a03287f7e41e8626fd78ab3b53262bd63f2e37403a549980bf3077bd402b82ef5fac269eb3c04d2a9b7712a67a018321ceba6c3bfb8f7f", 16)

	@OriginalMember(owner = "client!aq", name = "m", descriptor = "Lclient!of;")
	@JvmField
	var aClass240_3: SignedResource? = null

	@OriginalMember(owner = "client!hga", name = "J", descriptor = "Z")
	@JvmField
	var userDetailQuickchat: Boolean = false

	@OriginalMember(owner = "client!ks", name = "k", descriptor = "I")
	@JvmField
	var loginPayloadSize: Int = 0

	@OriginalMember(owner = "client!nk", name = "x", descriptor = "Lclient!of;")
	@JvmField
	var lastLoginHostLookup: SignedResource? = null
	@OriginalMember(owner = "client!qq", name = "j", descriptor = "I")
	@JvmField
	var staffModLevel: Int = 0

	@OriginalMember(owner = "client!nha", name = "g", descriptor = "I")
	@JvmField
	var playerModLevel: Int = 0

	@OriginalMember(owner = "client!tk", name = "h", descriptor = "J")
	@JvmField
	val clientRandomSeed: Long = (Math.random() * 9.999999999E9).toLong()

	@OriginalMember(owner = "client!jca", name = "j", descriptor = "Z")
	@JvmField
	var hasAccountSecurity: Boolean = false

	@OriginalMember(owner = "client!ap", name = "f", descriptor = "I")
	@JvmField
	var playerDob: Int = 0

	@OriginalMember(owner = "client!ns", name = "d", descriptor = "Z")
	@JvmField
	var verifiedEmailAddress: Boolean = false
}
