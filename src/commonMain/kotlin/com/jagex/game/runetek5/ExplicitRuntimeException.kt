package com.jagex.game.runetek5

import com.jagex.game.runetek5.client.features.SmokeTest
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.sign.SignLink
import com.jagex.game.runetek5.sign.SignedResource
import com.jagex.game.runetek5.sign.SignedResourceStatus
import com.jagex.game.runetek5.systeminfo.SystemInfo
import com.jagex.game.runetek5.utils.Utils
import applet.Applet
import io.BufferedReader
import io.DataInputStream
import io.IOException
import io.PrintWriter
import io.StringReader
import io.StringWriter
import net.URL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import lang.printStackTraceTo

@OriginalClass("client!kg")
class ExplicitRuntimeException : RuntimeException {

	@OriginalMember(owner = "client!kg", name = "a", descriptor = "Ljava/lang/String;")
	@JvmField
	var error: String?

	@OriginalMember(owner = "client!kg", name = "b", descriptor = "Ljava/lang/Throwable;")
	@JvmField
	val throwable: Throwable?

	@OriginalMember(owner = "client!kg", name = "<init>", descriptor = "(Ljava/lang/Throwable;Ljava/lang/String;)V")
	constructor(@OriginalArg(0) throwable: Throwable?, @OriginalArg(1) string: String?) : super() {
		this.error = string
		this.throwable = throwable
	}

	companion object {
		@OriginalMember(owner = "client!os", name = "a", descriptor = "(Ljava/lang/String;Ljava/lang/Throwable;I)V")
		@JvmStatic
		fun reportError(@OriginalArg(0) string: String?, @OriginalArg(1) throwable: Throwable?) {
			// Not part of the original client: an error reported here fails the smoke test (client.test=smoke).
			SmokeTest.error(string, throwable)
			try {
				@Pc(7) var string2: String = ""
				if (throwable != null) {
					string2 = ExplicitRuntimeException.formatStackTrace(throwable)
				}
				if (string != null) {
					if (throwable != null) {
						string2 = string2 + " | "
					}
					string2 = string2 + string
				}
				ExplicitRuntimeException.printError(string2)
				string2 = Utils.replaceAll("%3a", ":", string2)
				string2 = Utils.replaceAll("%40", "@", string2)
				string2 = Utils.replaceAll("%26", "&", string2)
				string2 = Utils.replaceAll("%23", "#", string2)
				if (ExplicitRuntimeException.applet != null) {
					@Pc(110) val signedResource: SignedResource = SignLink.PACKET_3!!.openStream(URL(ExplicitRuntimeException.applet!!.getCodeBase(), "clienterror.ws?c=" + SystemInfo.clientBuild + "&u=" + (if (PlayerUpdate.localUsername == null) ExplicitRuntimeException.errorReportUserId.toString() else PlayerUpdate.localUsername) + "&v1=" + PlayerUpdate.javaVendor + "&v2=" + SystemInfo.javaVersion + "&e=" + string2))
					while (signedResource.status == SignedResourceStatus.IDLE) {
						TimeUtils.sleep(1L)
					}
					if (signedResource.status == SignedResourceStatus.SUCCESS) {
						@Pc(131) val dataInputStream: DataInputStream = signedResource.result as DataInputStream
						dataInputStream.read()
						dataInputStream.close()
					}
				}
			} catch (@Pc(138) ex: Exception) {
			}
		}

		@OriginalMember(owner = "client!jf", name = "a", descriptor = "(Ljava/lang/Throwable;Ljava/lang/String;)Lclient!kg;")
		@JvmStatic
		fun wrap(@OriginalArg(0) throwable: Throwable?, @OriginalArg(1) string: String?): ExplicitRuntimeException {
			@Pc(12) val jagException: ExplicitRuntimeException
			if (throwable is ExplicitRuntimeException) {
				jagException = throwable
				jagException.error = jagException.error + ' ' + string
			} else {
				jagException = ExplicitRuntimeException(throwable, string)
			}
			return jagException
		}

		@OriginalMember(owner = "client!gia", name = "w", descriptor = "Ljava/applet/Applet;")
		@JvmField
		var applet: Applet? = null

		@OriginalMember(owner = "client!rda", name = "a", descriptor = "(ZLjava/lang/Throwable;)Ljava/lang/String;")
		@JvmStatic
		@Throws(IOException::class)
		fun formatStackTrace(@OriginalArg(1) throwable: Throwable?): String {
			var throwable: Throwable? = throwable
			@Pc(21) var string: String
			if (throwable is ExplicitRuntimeException) {
				@Pc(11) val jagException: ExplicitRuntimeException = throwable
				string = jagException.error + " | "
				throwable = jagException.throwable
			} else {
				string = ""
			}
			@Pc(32) val stringWriter: StringWriter = StringWriter()
			@Pc(37) val printWriter: PrintWriter = PrintWriter(stringWriter)
			throwable!!.printStackTraceTo(printWriter)
			printWriter.close()
			@Pc(45) val string2: String = stringWriter.toString()
			@Pc(53) val bufferedReader: BufferedReader = BufferedReader(StringReader(string2))
			@Pc(61) val string3: String? = bufferedReader.readLine()
			while (true) {
				@Pc(64) val string4: String? = bufferedReader.readLine()
				if (string4 == null) {
					return string + "| " + string3
				}
				@Pc(70) val openParenIndex: Int = string4.indexOf('(')
				@Pc(77) val closeParenIndex: Int = string4.indexOf(')', openParenIndex + 1)
				@Pc(82) var string5: String
				if (openParenIndex == -1) {
					string5 = string4
				} else {
					string5 = string4.substring(0, openParenIndex)
				}
				string5 = string5.trim { it <= ' ' }
				string5 = string5.substring(string5.lastIndexOf(' ') + 1)
				string5 = string5.substring(string5.lastIndexOf('\t') + 1)
				string = string + string5
				if (openParenIndex != -1 && closeParenIndex != -1) {
					@Pc(129) val javaSuffixIndex: Int = string4.indexOf(".java:", openParenIndex)
					if (javaSuffixIndex >= 0) {
						string = string + string4.substring(javaSuffixIndex + 5, closeParenIndex)
					}
				}
				string = string + ' '
			}
		}

		@OriginalMember(owner = "client!lh", name = "a", descriptor = "(Ljava/lang/String;I)V")
		@JvmStatic
		fun printError(@OriginalArg(0) string: String?) {
			println("Error: " + Utils.replaceAll("\n", "%0a", string!!))
		}

		@OriginalMember(owner = "client!lha", name = "b", descriptor = "J")
		@JvmField
		var errorReportUserId: Long = 0L
	}
}
