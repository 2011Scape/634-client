package com.jagex.game.runetek5

import com.jagex.game.runetek5.network.packet.TcpPacket
import com.jagex.game.runetek5.LocalisedText
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.tools.LocalizedText
import com.jagex.game.runetek5.network.BufferedConnectionContext
import com.jagex.game.runetek5.network.packet.ClientProt
import com.jagex.game.runetek5.encoding.HuffmanList
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.client.ClientState
import util.Locale
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import util.lowercase

object ScriptRunner {

	@OriginalMember(owner = "client!hg", name = "v", descriptor = "[I")
	@JvmField
	var threadSplitX: IntArray? = null

	@OriginalMember(owner = "client!vg", name = "h", descriptor = "I")
	@JvmField
	var minimumDrawLevel: Int = 0

	@OriginalMember(owner = "client!bb", name = "kb", descriptor = "I")
	@JvmField
	var menuCornerSpriteId: Int = 0

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "()V")
	@JvmStatic
	fun profileClear() {
	}

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(Ljava/lang/String;I)V")
	@JvmStatic
	fun sendPublicChat(@OriginalArg(0) string: String?, @OriginalArg(1) opcode: Int) {
		if (LoginManager.staffModLevel == 0 && (LoginManager.userDetailQuickchat && !LoginManager.verifiedEmailAddress || LoginManager.quickchatOnly)) {
			return
		}
		var string: String = string!!
		@Pc(11) var string2: String = string.lowercase(util.defaultLocale())
		@Pc(13) var color: Byte = 0
		if (string2.startsWith(LocalizedText.YELLOW.get(0)!!)) {
			color = 0
			string = string.substring(LocalizedText.YELLOW.get(0)!!.length)
		} else if (string2.startsWith(LocalizedText.RED.get(0)!!)) {
			color = 1
			string = string.substring(LocalizedText.RED.get(0)!!.length)
		} else if (string2.startsWith(LocalizedText.GREEN.get(0)!!)) {
			color = 2
			string = string.substring(LocalizedText.GREEN.get(0)!!.length)
		} else if (string2.startsWith(LocalizedText.CYAN.get(0)!!)) {
			color = 3
			string = string.substring(LocalizedText.CYAN.get(0)!!.length)
		} else if (string2.startsWith(LocalizedText.PURPLE.get(0)!!)) {
			color = 4
			string = string.substring(LocalizedText.PURPLE.get(0)!!.length)
		} else if (string2.startsWith(LocalizedText.WHITE.get(0)!!)) {
			color = 5
			string = string.substring(LocalizedText.WHITE.get(0)!!.length)
		} else if (string2.startsWith(LocalizedText.FLASH1.get(0)!!)) {
			color = 6
			string = string.substring(LocalizedText.FLASH1.get(0)!!.length)
		} else if (string2.startsWith(LocalizedText.FLASH2.get(0)!!)) {
			color = 7
			string = string.substring(LocalizedText.FLASH2.get(0)!!.length)
		} else if (string2.startsWith(LocalizedText.FLASH3.get(0)!!)) {
			color = 8
			string = string.substring(LocalizedText.FLASH3.get(0)!!.length)
		} else if (string2.startsWith(LocalizedText.GLOW1.get(0)!!)) {
			color = 9
			string = string.substring(LocalizedText.GLOW1.get(0)!!.length)
		} else if (string2.startsWith(LocalisedText.GLOW2.get(0)!!)) {
			color = 10
			string = string.substring(LocalisedText.GLOW2.get(0)!!.length)
		} else if (string2.startsWith(LocalizedText.GLOW3.get(0)!!)) {
			color = 11
			string = string.substring(LocalizedText.GLOW3.get(0)!!.length)
		} else if (ClientState.language != 0) {
			if (string2.startsWith(LocalizedText.YELLOW.get(ClientState.language)!!)) {
				color = 0
				string = string.substring(LocalizedText.YELLOW.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalizedText.RED.get(ClientState.language)!!)) {
				color = 1
				string = string.substring(LocalizedText.RED.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalizedText.GREEN.get(ClientState.language)!!)) {
				color = 2
				string = string.substring(LocalizedText.GREEN.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalizedText.CYAN.get(ClientState.language)!!)) {
				color = 3
				string = string.substring(LocalizedText.CYAN.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalizedText.PURPLE.get(ClientState.language)!!)) {
				color = 4
				string = string.substring(LocalizedText.PURPLE.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalizedText.WHITE.get(ClientState.language)!!)) {
				color = 5
				string = string.substring(LocalizedText.WHITE.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalizedText.FLASH1.get(ClientState.language)!!)) {
				color = 6
				string = string.substring(LocalizedText.FLASH1.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalizedText.FLASH2.get(ClientState.language)!!)) {
				color = 7
				string = string.substring(LocalizedText.FLASH2.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalizedText.FLASH3.get(ClientState.language)!!)) {
				color = 8
				string = string.substring(LocalizedText.FLASH3.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalizedText.GLOW1.get(ClientState.language)!!)) {
				color = 9
				string = string.substring(LocalizedText.GLOW1.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalisedText.GLOW2.get(ClientState.language)!!)) {
				color = 10
				string = string.substring(LocalisedText.GLOW2.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalizedText.GLOW3.get(ClientState.language)!!)) {
				color = 11
				string = string.substring(LocalizedText.GLOW3.get(ClientState.language)!!.length)
			}
		}
		string2 = string.lowercase(util.defaultLocale())
		@Pc(451) var effect: Byte = 0
		if (string2.startsWith(LocalizedText.WAVE.get(0)!!)) {
			effect = 1
			string = string.substring(LocalizedText.WAVE.get(0)!!.length)
		} else if (string2.startsWith(LocalizedText.WAVE2.get(0)!!)) {
			effect = 2
			string = string.substring(LocalizedText.WAVE2.get(0)!!.length)
		} else if (string2.startsWith(LocalizedText.SHAKE.get(0)!!)) {
			effect = 3
			string = string.substring(LocalizedText.SHAKE.get(0)!!.length)
		} else if (string2.startsWith(LocalizedText.SCROLL.get(0)!!)) {
			effect = 4
			string = string.substring(LocalizedText.SCROLL.get(0)!!.length)
		} else if (string2.startsWith(LocalizedText.SLIDE.get(0)!!)) {
			effect = 5
			string = string.substring(LocalizedText.SLIDE.get(0)!!.length)
		} else if (ClientState.language != 0) {
			if (string2.startsWith(LocalizedText.WAVE.get(ClientState.language)!!)) {
				effect = 1
				string = string.substring(LocalizedText.WAVE.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalizedText.WAVE2.get(ClientState.language)!!)) {
				effect = 2
				string = string.substring(LocalizedText.WAVE2.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalizedText.SHAKE.get(ClientState.language)!!)) {
				effect = 3
				string = string.substring(LocalizedText.SHAKE.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalizedText.SCROLL.get(ClientState.language)!!)) {
				effect = 4
				string = string.substring(LocalizedText.SCROLL.get(ClientState.language)!!.length)
			} else if (string2.startsWith(LocalizedText.SLIDE.get(ClientState.language)!!)) {
				effect = 5
				string = string.substring(LocalizedText.SLIDE.get(ClientState.language)!!.length)
			}
		}
		@Pc(641) val packet: TcpPacket = TcpPacket.createPacket(ClientProt.MESSAGE_PUBLIC, TcpPacket.isaac)
		packet.buffer!!.writeByte(0)
		@Pc(650) val currentPosition: Int = packet.buffer!!.currentPosition
		packet.buffer!!.writeByte(color.toInt())
		packet.buffer!!.writeByte(effect.toInt())
		HuffmanList.writeChatMessage(packet.buffer, string)
		packet.buffer!!.writeIndex(packet.buffer!!.currentPosition - currentPosition)
		BufferedConnectionContext.queuePacket(packet)
	}

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "[I")
	@JvmField
	val intStack: IntArray = IntArray(1000)

	@OriginalMember(owner = "client!kca", name = "g", descriptor = "[Ljava/lang/String;")
	@JvmField
	var stringVars: Array<String?>? = null

	@OriginalMember(owner = "client!kca", name = "m", descriptor = "[[I")
	@JvmField
	// Java: new int[5][5000]. Written as a loop: Kotlin 2.2.20 emits unverifiable bytecode (an unset loop
	// counter, "Bad local variable type") for Array(5) { IntArray(5000) } in this object's static initializer.
	val arrayVars: Array<IntArray?> = arrayOfNulls<IntArray>(5).also { rows ->
		var row = 0
		while (row < 5) {
			rows[row] = IntArray(5000)
			row++
		}
	}

	@OriginalMember(owner = "client!kca", name = "p", descriptor = "[I")
	@JvmField
	var intLocals: IntArray? = null
}
