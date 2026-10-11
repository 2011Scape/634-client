package com.jagex.game.runetek5.client.login;

import com.jagex.game.runetek5.core.constants.LoginResponseCode;
import com.jagex.game.runetek5.config.invtype.LoginDisallowReason;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.WorldConnectionInfo;
import com.jagex.game.runetek5.buffer.Packet;
import java.io.IOException;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import java.math.BigInteger;
import com.jagex.game.runetek5.sign.SignedResource;
import com.jagex.game.runetek5.client.ClientState;

public final class LoginManager {

	@OriginalMember(owner = "client!gn", name = "f", descriptor = "I")
	public static int lastDisallowResult = -1;

	@OriginalMember(owner = "client!su", name = "i", descriptor = "I")
	public static int dirtyAreaMinY;

	@OriginalMember(owner = "client!nr", name = "a", descriptor = "(Ljava/lang/String;ZLjava/lang/String;B)V")
	public static void initializeLogin(@OriginalArg(0) String string, @OriginalArg(1) boolean hasStoredCredentials, @OriginalArg(2) String string2) {
		LoginManager.loginUsername = string;
		LoginManager.loginPassword = string2;
		LoginManager.hasStoredCredentials = hasStoredCredentials;
		if (!LoginManager.hasStoredCredentials && (LoginManager.loginUsername.equals("") || LoginManager.loginPassword.equals(""))) {
			LoginManager.setLoginResponse(LoginResponseCode.INVALID_USERNAME_OR_PASSWORD);
			return;
		}
		if (LoginManager.lobbyStage != 1) {
			LoginManager.loginHopTime = 0;
			LoginDisallowReason.loginDisallowReason = -1;
		}
		BufferedConnectionContext.dropped = false;
		LoginManager.setLoginResponse(-3);
		LoginManager.loginStep = LoginStep.CONNECT;
		LoginManager.delay = 0;
		LoginManager.connectionAttempts = 0;
	}
	@OriginalMember(owner = "client!ub", name = "u", descriptor = "Lclient!pc;")
	public static WorldConnectionInfo CURRENT_CONNECTION_CONTEXT;

	@OriginalMember(owner = "client!ad", name = "c", descriptor = "Z")
	public static boolean currentlyInLobby = false;

	@OriginalMember(owner = "client!lh", name = "i", descriptor = "Z")
	public static boolean worldMapUseTargetPosition = false;
	@OriginalMember(owner = "client!aq", name = "j", descriptor = "I")
	public static int lobbyCreditCardExpiry;

	@OriginalMember(owner = "client!k", name = "e", descriptor = "I")
	public static int loginHopTime = 0;
	@OriginalMember(owner = "client!rg", name = "g", descriptor = "I")
	public static int lastLoginDay;

	@OriginalMember(owner = "client!afa", name = "i", descriptor = "I")
	public static int unreadMessages;

	@OriginalMember(owner = "client!ed", name = "a", descriptor = "(Lclient!cea;B)V")
	public static void pushRandomDat(@OriginalArg(0) Packet packet) {
		@Pc(8) byte[] uid = new byte[24];
		if (GameShell.aClass184_2 != null) {
			@Pc(24) int index;
			try {
				GameShell.aClass184_2.seek(0L);
				GameShell.aClass184_2.read(uid);
				for (index = 0; index < 24 && uid[index] == 0; index++) {
				}
				if (index >= 24) {
					throw new IOException();
				}
			} catch (@Pc(48) Exception ex) {
				for (index = 0; index < 24; index++) {
					uid[index] = -1;
				}
			}
		}
		packet.writeBytes(24, 0, uid);
	}

	@OriginalMember(owner = "client!jt", name = "u", descriptor = "I")
	public static int recoveryQuestionsSetDay;

	@OriginalMember(owner = "client!ab", name = "u", descriptor = "I")
	public static int lobbyPlayAge;
	@OriginalMember(owner = "client!qca", name = "a", descriptor = "(I)V")
	public static void lobbyLoginWithStoredCredentials() {
		LoginManager.anInt6172 = -1;
		LoginManager.lobbyStage = 1;
		if (LoginManager.ssKey == null) {
			LoginManager.setLoginResponse(35);
		} else {
			@Pc(20) Packet packet = new Packet(Utils.base64Decode(Utils.urlDecode(LoginManager.ssKey)));
			@Pc(24) long encodedUsername = packet.readLong();
			LoginManager.ssKeyNonce = packet.readLong();
			LoginManager.initializeLogin(Utils.nameForLong(encodedUsername), true, "");
		}
	}

	@OriginalMember(owner = "client!bc", name = "b", descriptor = "(B)V")
	public static void confirmLoginContinue() {
		if (LoginManager.loginStep == LoginStep.SHOWING_ADVERTISEMENT) {
			LoginManager.loginStep = LoginStep.ADVERTISEMENT_FINISHED;
		}
	}

	@OriginalMember(owner = "client!kda", name = "Db", descriptor = "Z")
	public static boolean hasStoredCredentials = false;

	@OriginalMember(owner = "client!kaa", name = "a", descriptor = "(IB)V")
	public static void worldLoginWithStoredCredentials(@OriginalArg(0) int arg0) {
		LoginManager.lobbyStage = 2;
		LoginManager.anInt6172 = arg0;
		if (LoginManager.ssKey == null) {
			LoginManager.setLoginResponse(35);
		} else {
			@Pc(24) Packet packet = new Packet(Utils.base64Decode(Utils.urlDecode(LoginManager.ssKey)));
			@Pc(28) long encodedUsername = packet.readLong();
			LoginManager.ssKeyNonce = packet.readLong();
			LoginManager.initializeLogin(Utils.nameForLong(encodedUsername), true, "");
		}
	}
	@OriginalMember(owner = "client!cd", name = "a", descriptor = "Z")
	public static boolean isDobRequested;

	@OriginalMember(owner = "client!sg", name = "db", descriptor = "I")
	public static LoginStep loginStep = LoginStep.IDLE;
	@OriginalMember(owner = "client!hba", name = "e", descriptor = "I")
	public static int delay = 0;
	@OriginalMember(owner = "client!mn", name = "d", descriptor = "I")
	public static int lobbyLoginResponse = -2;

	@OriginalMember(owner = "client!raa", name = "A", descriptor = "J")
	public static long ssKeyNonce = 0L;

	@OriginalMember(owner = "client!gba", name = "s", descriptor = "I")
	public static int worldLoginResponse = -2;

	@OriginalMember(owner = "client!gba", name = "q", descriptor = "I")
	public static int lobbyGraceExpiry;

	@OriginalMember(owner = "client!u", name = "a", descriptor = "(Ljava/lang/String;ILjava/lang/String;Z)V")
	public static void worldLogin(@OriginalArg(0) String string, @OriginalArg(1) int arg1, @OriginalArg(2) String string2) {
		LoginManager.anInt6172 = arg1;
		LoginManager.lobbyStage = 2;
		LoginManager.initializeLogin(string2, false, string);
	}

	@OriginalMember(owner = "client!pia", name = "d", descriptor = "I")
	public static int lastLoginIpAddress;

	@OriginalMember(owner = "client!rg", name = "y", descriptor = "Z")
	public static boolean isMember = false;

	@OriginalMember(owner = "client!ok", name = "a", descriptor = "(Ljava/lang/String;Ljava/lang/String;I)V")
	public static void lobbyLogin(@OriginalArg(0) String string, @OriginalArg(1) String string2) {
		LoginManager.anInt6172 = -1;
		// Not part of the original client, which logs in to the lobby (1); Void has no lobby, so with
		// client.server=void the client logs in to the game world (2).
		LoginManager.lobbyStage = ClientFeatures.VOID ? 2 : 1;
		LoginManager.initializeLogin(string, false, string2);
	}

	@OriginalMember(owner = "client!kg", name = "c", descriptor = "I")
	public static int anInt6172 = -1;

	@OriginalMember(owner = "client!uo", name = "J", descriptor = "I")
	public static int connectionAttempts = 0;

	@OriginalMember(owner = "client!tw", name = "c", descriptor = "(IB)V")
	public static void setLoginResponse(@OriginalArg(0) int response) {
		if (LoginManager.lobbyStage == 1) {
			LoginManager.lobbyLoginResponse = response;
		} else if (LoginManager.lobbyStage == 2) {
			// Not part of the original client, which set only the world code here. The login screen
			// reads the lobby code, so with the lobby skipped (client.server=void) it would never show the reply.
			if (ClientFeatures.VOID) {
				LoginManager.lobbyLoginResponse = response;
			}
			LoginManager.worldLoginResponse = response;
		}
	}

	@OriginalMember(owner = "client!paa", name = "Jb", descriptor = "Ljava/lang/String;")
	public static String loginUsername = "";

	@OriginalMember(owner = "client!np", name = "g", descriptor = "Z")
	public static boolean memberWorld = false;

	@OriginalMember(owner = "client!qg", name = "g", descriptor = "Ljava/lang/String;")
	public static String loginPassword = "";

	@OriginalMember(owner = "client!qfa", name = "E", descriptor = "Ljava/math/BigInteger;")
	public static BigInteger rsaExponent = new BigInteger("10001", 16);
	@OriginalMember(owner = "client!gfa", name = "l", descriptor = "Z")
	public static boolean quickchatOnly = false;

	@OriginalMember(owner = "client!pk", name = "z", descriptor = "Ljava/lang/String;")
	public static String ssKey = null;

	@OriginalMember(owner = "client!av", name = "x", descriptor = "I")
	public static int lobbyStage;

	@OriginalMember(owner = "client!pi", name = "w", descriptor = "Ljava/math/BigInteger;")
	public static BigInteger rsaModulus = new BigInteger("ea3680fdebf2621da7a33601ba39925ee203b3fc80775cd3727bf27fd8c0791c803e0bdb42b8b5257567177f8569024569da9147cef59009ed016af6007e57a556f1754f09ca84dd39a03287f7e41e8626fd78ab3b53262bd63f2e37403a549980bf3077bd402b82ef5fac269eb3c04d2a9b7712a67a018321ceba6c3bfb8f7f", 16);

	@OriginalMember(owner = "client!aq", name = "m", descriptor = "Lclient!of;")
	public static SignedResource aClass240_3;

	@OriginalMember(owner = "client!hga", name = "J", descriptor = "Z")
	public static boolean userDetailQuickchat = false;

	@OriginalMember(owner = "client!ks", name = "k", descriptor = "I")
	public static int loginPayloadSize;

	@OriginalMember(owner = "client!nk", name = "x", descriptor = "Lclient!of;")
	public static SignedResource lastLoginHostLookup;
	@OriginalMember(owner = "client!qq", name = "j", descriptor = "I")
	public static int staffModLevel = 0;

	@OriginalMember(owner = "client!nha", name = "g", descriptor = "I")
	public static int playerModLevel = 0;

	@OriginalMember(owner = "client!tk", name = "h", descriptor = "J")
	public static final long clientRandomSeed = (long) (Math.random() * 9.999999999E9D);

	@OriginalMember(owner = "client!jca", name = "j", descriptor = "Z")
	public static boolean hasAccountSecurity = false;

	@OriginalMember(owner = "client!ap", name = "f", descriptor = "I")
	public static int playerDob;

	@OriginalMember(owner = "client!ns", name = "d", descriptor = "Z")
	public static boolean verifiedEmailAddress = false;
}
