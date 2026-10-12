package com.jagex.game.runetek5.client.web

import com.jagex.game.runetek5.sign.SignLink
import com.jagex.game.runetek5.sign.SignedResource
import com.jagex.game.runetek5.sign.SignedResourceStatus
import com.jagex.game.runetek5.sign.SignLinkEnvironment
import com.jagex.game.runetek5.statics.BrowserJavaScript
import net.URL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.web.OpenUrlType
import com.jagex.game.runetek5.systeminfo.SystemInfo
import com.jagex.game.runetek5.client.GameShell
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object ClientURLTools {

	@OriginalMember(owner = "client!bo", name = "a", descriptor = "(Ljava/lang/String;Lclient!wea;ZZI)V")
	@JvmStatic
	fun openUrl(@OriginalArg(0) string: String?, @OriginalArg(1) signLink: SignLink?, @OriginalArg(2) openGlActive: Boolean, @OriginalArg(3) newWindow: Boolean) {
		if (!newWindow) {
			ClientURLTools.openUrlWithType(signLink, string, 3)
			return
		}
		@Pc(14) var string2: String?
		if (SystemInfo.operatingSystemLowercase!!.startsWith("win") && signLink!!.signed) {
			string2 = null
			if (GameShell.applet != null) {
				string2 = GameShell.applet!!.getParameter("haveie6")
			}
			if (string2 == null || string2 != "1") {
				@Pc(32) val signedResource: SignedResource? = ClientURLTools.openUrlWithType(signLink, string, 0)
				ClientURLTools.aClass240_12 = signedResource
				SignLink.signLink = signLink
				ClientURLTools.pendingUrl = string
				return
			}
		}
		if (SystemInfo.operatingSystemLowercase!!.startsWith("mac")) {
			string2 = null
			if (GameShell.applet != null) {
				string2 = GameShell.applet!!.getParameter("havefirefox")
			}
			if (string2 != null && string2 == "1" && openGlActive) {
				ClientURLTools.openUrlWithType(signLink, string, OpenUrlType.CALL)
				return
			}
		}
		ClientURLTools.openUrlWithType(signLink, string, 2)
	}

	@OriginalMember(owner = "client!ma", name = "a", descriptor = "(Lclient!wea;Ljava/lang/String;II)Lclient!of;")
	@JvmStatic
	fun openUrlWithType(@OriginalArg(0) signLink: SignLink?, @OriginalArg(1) string: String?, @OriginalArg(3) type: Int): SignedResource? {
		if (type == OpenUrlType.OPEN_PAGE) {
			return signLink!!.openPage(string)
		}
		@Pc(44) var signedResource: SignedResource
		if (type == OpenUrlType.CALL) {
			try {
				BrowserJavaScript.callJavaScriptWithArgs("openjs", arrayOf<Any?>((URL(GameShell.applet!!.getCodeBase(), string)).toString()), GameShell.applet)
				signedResource = SignedResource()
				signedResource.status = SignedResourceStatus.SUCCESS
				return signedResource
			} catch (@Pc(50) ex: Throwable) {
				signedResource = SignedResource()
				signedResource.status = SignedResourceStatus.ERROR
				return signedResource
			}
		} else if (type == OpenUrlType.OPEN_IN_NEW_TAB) {
			try {
				GameShell.applet!!.getAppletContext()!!.showDocument(URL(GameShell.applet!!.getCodeBase(), string), "_blank")
				signedResource = SignedResource()
				signedResource.status = SignedResourceStatus.SUCCESS
				return signedResource
			} catch (@Pc(84) ex2: Exception) {
				signedResource = SignedResource()
				signedResource.status = SignedResourceStatus.ERROR
				return signedResource
			}
		} else if (type == OpenUrlType.REPLACE) {
			try {
				BrowserJavaScript.callJavaScript(GameShell.applet, "loggedout")
			} catch (@Pc(103) ex3: Throwable) {
			}
			try {
				GameShell.applet!!.getAppletContext()!!.showDocument(URL(GameShell.applet!!.getCodeBase(), string), "_top")
				signedResource = SignedResource()
				signedResource.status = SignedResourceStatus.SUCCESS
				return signedResource
			} catch (@Pc(123) ex4: Exception) {
				signedResource = SignedResource()
				signedResource.status = SignedResourceStatus.ERROR
				return signedResource
			}
		} else {
			throw IllegalArgumentException()
		}
	}
	@OriginalMember(owner = "client!un", name = "z", descriptor = "Lclient!of;")
	@JvmField
	var aClass240_12: SignedResource? = null

	@OriginalMember(owner = "client!qia", name = "G", descriptor = "Ljava/lang/String;")
	@JvmField
	var pendingUrl: String? = null
	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun processPendingUrlOpen() {
		if (ClientURLTools.aClass240_12 == null) {
			return
		}
		if (ClientURLTools.aClass240_12!!.status == 1) {
			ClientURLTools.aClass240_12 = null
			return
		}
		if (ClientURLTools.aClass240_12!!.status == 2) {
			ClientURLTools.openUrlWithType(SignLink.signLink, ClientURLTools.pendingUrl, 2)
			ClientURLTools.aClass240_12 = null
			return
		}
	}
}
