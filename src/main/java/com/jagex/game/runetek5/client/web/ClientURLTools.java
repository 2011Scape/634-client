package com.jagex.game.runetek5.client.web;

import com.jagex.game.runetek5.sign.SignLink;
import com.jagex.game.runetek5.sign.SignedResource;
import com.jagex.game.runetek5.sign.SignedResourceStatus;
import com.jagex.game.runetek5.sign.SignLinkEnvironment;
import com.jagex.game.runetek5.statics.BrowserJavaScript;
import java.net.URL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.web.OpenUrlType;
import com.jagex.game.runetek5.systeminfo.SystemInfo;
import com.jagex.game.runetek5.client.GameShell;

public final class ClientURLTools {

	@OriginalMember(owner = "client!bo", name = "a", descriptor = "(Ljava/lang/String;Lclient!wea;ZZI)V")
	public static void openUrl(@OriginalArg(0) String string, @OriginalArg(1) SignLink signLink, @OriginalArg(2) boolean openGlActive, @OriginalArg(3) boolean newWindow) {
		if (!newWindow) {
			ClientURLTools.openUrlWithType(signLink, string, 3);
			return;
		}
		@Pc(14) String string2;
		if (SystemInfo.operatingSystemLowercase.startsWith("win") && signLink.signed) {
			string2 = null;
			if (GameShell.applet != null) {
				string2 = GameShell.applet.getParameter("haveie6");
			}
			if (string2 == null || !string2.equals("1")) {
				@Pc(32) SignedResource signedResource = ClientURLTools.openUrlWithType(signLink, string, 0);
				ClientURLTools.aClass240_12 = signedResource;
				SignLink.signLink = signLink;
				ClientURLTools.pendingUrl = string;
				return;
			}
		}
		if (SystemInfo.operatingSystemLowercase.startsWith("mac")) {
			string2 = null;
			if (GameShell.applet != null) {
				string2 = GameShell.applet.getParameter("havefirefox");
			}
			if (string2 != null && string2.equals("1") && openGlActive) {
				ClientURLTools.openUrlWithType(signLink, string, OpenUrlType.CALL);
				return;
			}
		}
		ClientURLTools.openUrlWithType(signLink, string, 2);
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(Lclient!wea;Ljava/lang/String;II)Lclient!of;")
	public static SignedResource openUrlWithType(@OriginalArg(0) SignLink signLink, @OriginalArg(1) String string, @OriginalArg(3) int type) {
		if (type == OpenUrlType.OPEN_PAGE) {
			return signLink.openPage(string);
		}
		@Pc(44) SignedResource signedResource;
		if (type == OpenUrlType.CALL) {
			try {
				BrowserJavaScript.callJavaScriptWithArgs("openjs", new Object[] { (new URL(GameShell.applet.getCodeBase(), string)).toString() }, GameShell.applet);
				signedResource = new SignedResource();
				signedResource.status = SignedResourceStatus.SUCCESS;
				return signedResource;
			} catch (@Pc(50) Throwable ex) {
				signedResource = new SignedResource();
				signedResource.status = SignedResourceStatus.ERROR;
				return signedResource;
			}
		} else if (type == OpenUrlType.OPEN_IN_NEW_TAB) {
			try {
				GameShell.applet.getAppletContext().showDocument(new URL(GameShell.applet.getCodeBase(), string), "_blank");
				signedResource = new SignedResource();
				signedResource.status = SignedResourceStatus.SUCCESS;
				return signedResource;
			} catch (@Pc(84) Exception ex2) {
				signedResource = new SignedResource();
				signedResource.status = SignedResourceStatus.ERROR;
				return signedResource;
			}
		} else if (type == OpenUrlType.REPLACE) {
			try {
				BrowserJavaScript.callJavaScript(GameShell.applet, "loggedout");
			} catch (@Pc(103) Throwable ex3) {
			}
			try {
				GameShell.applet.getAppletContext().showDocument(new URL(GameShell.applet.getCodeBase(), string), "_top");
				signedResource = new SignedResource();
				signedResource.status = SignedResourceStatus.SUCCESS;
				return signedResource;
			} catch (@Pc(123) Exception ex4) {
				signedResource = new SignedResource();
				signedResource.status = SignedResourceStatus.ERROR;
				return signedResource;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
	@OriginalMember(owner = "client!un", name = "z", descriptor = "Lclient!of;")
	public static SignedResource aClass240_12;

	@OriginalMember(owner = "client!qia", name = "G", descriptor = "Ljava/lang/String;")
	public static String pendingUrl;
	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(I)V")
	public static void processPendingUrlOpen() {
		if (ClientURLTools.aClass240_12 == null) {
			return;
		}
		if (ClientURLTools.aClass240_12.status == 1) {
			ClientURLTools.aClass240_12 = null;
			return;
		}
		if (ClientURLTools.aClass240_12.status == 2) {
			ClientURLTools.openUrlWithType(SignLink.signLink, ClientURLTools.pendingUrl, 2);
			ClientURLTools.aClass240_12 = null;
			return;
		}
	}
}
