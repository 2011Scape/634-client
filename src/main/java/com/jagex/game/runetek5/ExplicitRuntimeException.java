package com.jagex.game.runetek5;

import com.jagex.game.runetek5.client.features.SmokeTest;
import com.jagex.game.runetek5.PlayMusic;
import com.jagex.game.runetek5.sign.SignedResource;
import com.jagex.game.runetek5.sign.SignedResourceStatus;
import com.jagex.game.runetek5.sign.SignLinkEnvironment;
import java.io.DataInputStream;
import java.net.URL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.systeminfo.SystemInfo;
import com.jagex.game.runetek5.utils.Utils;
import java.applet.Applet;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.sign.SignLink;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

@OriginalClass("client!kg")
public final class ExplicitRuntimeException extends RuntimeException {

	@OriginalMember(owner = "client!kg", name = "a", descriptor = "Ljava/lang/String;")
	public String error;

	@OriginalMember(owner = "client!kg", name = "b", descriptor = "Ljava/lang/Throwable;")
	public final Throwable throwable;

	@OriginalMember(owner = "client!kg", name = "<init>", descriptor = "(Ljava/lang/Throwable;Ljava/lang/String;)V")
	public ExplicitRuntimeException(@OriginalArg(0) Throwable throwable, @OriginalArg(1) String string) {
		this.error = string;
		this.throwable = throwable;
	}

	@OriginalMember(owner = "client!os", name = "a", descriptor = "(Ljava/lang/String;Ljava/lang/Throwable;I)V")
	public static void reportError(@OriginalArg(0) String string, @OriginalArg(1) Throwable throwable) {
		// Not part of the original client: an error reported here fails the smoke test (client.test=smoke).
		SmokeTest.error(string, throwable);
		try {
			@Pc(7) String string2 = "";
			if (throwable != null) {
				string2 = ExplicitRuntimeException.formatStackTrace(throwable);
			}
			if (string != null) {
				if (throwable != null) {
					string2 = string2 + " | ";
				}
				string2 = string2 + string;
			}
			ExplicitRuntimeException.printError(string2);
			string2 = Utils.replaceAll("%3a", ":", string2);
			string2 = Utils.replaceAll("%40", "@", string2);
			string2 = Utils.replaceAll("%26", "&", string2);
			string2 = Utils.replaceAll("%23", "#", string2);
			if (ExplicitRuntimeException.applet != null) {
				@Pc(110) SignedResource signedResource = SignLink.PACKET_3.openStream(new URL(ExplicitRuntimeException.applet.getCodeBase(), "clienterror.ws?c=" + SystemInfo.clientBuild + "&u=" + (PlayerUpdate.localUsername == null ? String.valueOf(ExplicitRuntimeException.errorReportUserId) : PlayerUpdate.localUsername) + "&v1=" + PlayerUpdate.javaVendor + "&v2=" + SystemInfo.javaVersion + "&e=" + string2));
				while (signedResource.status == SignedResourceStatus.IDLE) {
					TimeUtils.sleep(1L);
				}
				if (signedResource.status == SignedResourceStatus.SUCCESS) {
					@Pc(131) DataInputStream dataInputStream = (DataInputStream) signedResource.result;
					dataInputStream.read();
					dataInputStream.close();
				}
			}
		} catch (@Pc(138) Exception ex) {
		}
	}
	@OriginalMember(owner = "client!jf", name = "a", descriptor = "(Ljava/lang/Throwable;Ljava/lang/String;)Lclient!kg;")
	public static ExplicitRuntimeException wrap(@OriginalArg(0) Throwable throwable, @OriginalArg(1) String string) {
		@Pc(12) ExplicitRuntimeException jagException;
		if (throwable instanceof ExplicitRuntimeException) {
			jagException = (ExplicitRuntimeException) throwable;
			jagException.error = jagException.error + ' ' + string;
		} else {
			jagException = new ExplicitRuntimeException(throwable, string);
		}
		return jagException;
	}

	@OriginalMember(owner = "client!gia", name = "w", descriptor = "Ljava/applet/Applet;")
	public static Applet applet;
	@OriginalMember(owner = "client!rda", name = "a", descriptor = "(ZLjava/lang/Throwable;)Ljava/lang/String;")
	public static String formatStackTrace(@OriginalArg(1) Throwable throwable) throws IOException {
		@Pc(21) String string;
		if (throwable instanceof ExplicitRuntimeException) {
			@Pc(11) ExplicitRuntimeException jagException = (ExplicitRuntimeException) throwable;
			string = jagException.error + " | ";
			throwable = jagException.throwable;
		} else {
			string = "";
		}
		@Pc(32) StringWriter stringWriter = new StringWriter();
		@Pc(37) PrintWriter printWriter = new PrintWriter(stringWriter);
		throwable.printStackTrace(printWriter);
		printWriter.close();
		@Pc(45) String string2 = stringWriter.toString();
		@Pc(53) BufferedReader bufferedReader = new BufferedReader(new StringReader(string2));
		@Pc(61) String string3 = bufferedReader.readLine();
		while (true) {
			@Pc(64) String string4 = bufferedReader.readLine();
			if (string4 == null) {
				return string + "| " + string3;
			}
			@Pc(70) int openParenIndex = string4.indexOf(40);
			@Pc(77) int closeParenIndex = string4.indexOf(41, openParenIndex + 1);
			@Pc(82) String string5;
			if (openParenIndex == -1) {
				string5 = string4;
			} else {
				string5 = string4.substring(0, openParenIndex);
			}
			string5 = string5.trim();
			string5 = string5.substring(string5.lastIndexOf(32) + 1);
			string5 = string5.substring(string5.lastIndexOf(9) + 1);
			string = string + string5;
			if (openParenIndex != -1 && closeParenIndex != -1) {
				@Pc(129) int javaSuffixIndex = string4.indexOf(".java:", openParenIndex);
				if (javaSuffixIndex >= 0) {
					string = string + string4.substring(javaSuffixIndex + 5, closeParenIndex);
				}
			}
			string = string + ' ';
		}
	}

	@OriginalMember(owner = "client!lh", name = "a", descriptor = "(Ljava/lang/String;I)V")
	public static void printError(@OriginalArg(0) String string) {
		System.out.println("Error: " + Utils.replaceAll("\n", "%0a", string));
	}
	@OriginalMember(owner = "client!lha", name = "b", descriptor = "J")
	public static long errorReportUserId;
}
