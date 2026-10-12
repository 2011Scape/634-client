package com.jagex.game.runetek5.cache.node.lru

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.ClientTrigger
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.cache.node.IntNode
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.cache.node.CacheableNodeQueue
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import lang.javaIntern

@OriginalClass("client!jk")
class ClientScript : CacheableNode() {

	@OriginalMember(owner = "client!jk", name = "x", descriptor = "I")
	@JvmField
	var intLocalsCount: Int = 0

	@OriginalMember(owner = "client!jk", name = "y", descriptor = "I")
	@JvmField
	var stringLocalsCount: Int = 0

	@OriginalMember(owner = "client!jk", name = "z", descriptor = "Ljava/lang/String;")
	@JvmField
	var scriptName: String? = null

	@OriginalMember(owner = "client!jk", name = "A", descriptor = "Lclient!uw;")
	@JvmField
	var triggerType: ClientTrigger? = null

	@OriginalMember(owner = "client!jk", name = "B", descriptor = "[Ljava/lang/String;")
	@JvmField
	var stringOpValues: Array<String?>? = null

	@OriginalMember(owner = "client!jk", name = "C", descriptor = "[Lclient!eq;")
	@JvmField
	var switchMaps: Array<IterableHashTable?>? = null

	@OriginalMember(owner = "client!jk", name = "D", descriptor = "[I")
	@JvmField
	var intOpValues: IntArray? = null

	@OriginalMember(owner = "client!jk", name = "E", descriptor = "I")
	@JvmField
	var stringArgsCount: Int = 0

	@OriginalMember(owner = "client!jk", name = "F", descriptor = "[I")
	@JvmField
	var operations: IntArray? = null

	@OriginalMember(owner = "client!jk", name = "G", descriptor = "I")
	@JvmField
	var intArgsCount: Int = 0

	companion object {
		@OriginalMember(owner = "client!jda", name = "a", descriptor = "([BI)Lclient!jk;")
		@JvmStatic
		fun decodeScript(@OriginalArg(0) src: ByteArray?): ClientScript {
			@Pc(14) val clientScript: ClientScript = ClientScript()
			@Pc(19) val packet: Packet = Packet(src)
			packet.currentPosition = packet.buffer!!.size - 2
			@Pc(30) val switchDataLength: Int = packet.readUShort()
			@Pc(41) val codeEnd: Int = packet.buffer!!.size - switchDataLength - 12 - 2
			packet.currentPosition = codeEnd
			@Pc(48) val instructionCount: Int = packet.readInt()
			clientScript.intLocalsCount = packet.readUShort()
			clientScript.stringLocalsCount = packet.readUShort()
			clientScript.intArgsCount = packet.readUShort()
			clientScript.stringArgsCount = packet.readUShort()
			@Pc(72) val switchCount: Int = packet.readUByte()
			@Pc(80) var local80: Int
			@Pc(86) var local86: Int
			if (switchCount > 0) {
				clientScript.switchMaps = arrayOfNulls<IterableHashTable>(switchCount)
				var switchIndex: Int = 0
				while (switchIndex < switchCount) {
					var caseCount: Int = packet.readUShort()
					@Pc(93) val iterableHashTable: IterableHashTable = IterableHashTable(Utils.nextPowerOfTwo(caseCount))
					clientScript.switchMaps!![switchIndex] = iterableHashTable
					while (caseCount-- > 0) {
						@Pc(104) val caseValue: Int = packet.readInt()
						@Pc(108) val jumpOffset: Int = packet.readInt()
						iterableHashTable.put(caseValue.toLong(), IntNode(jumpOffset))
					}
					switchIndex++
				}
			}
			packet.currentPosition = 0
			clientScript.scriptName = packet.readNullString()
			clientScript.intOpValues = IntArray(instructionCount)
			clientScript.operations = IntArray(instructionCount)
			clientScript.stringOpValues = arrayOfNulls<String>(instructionCount)
			var instructionIndex: Int = 0
			while (codeEnd > packet.currentPosition) {
				val opcode: Int = packet.readUShort()
				if (opcode == 3) {
					clientScript.stringOpValues!![instructionIndex] = packet.readString()!!.javaIntern()
				} else if (opcode >= 100 || opcode == 21 || opcode == 38 || opcode == 39) {
					clientScript.intOpValues!![instructionIndex] = packet.readUByte()
				} else {
					clientScript.intOpValues!![instructionIndex] = packet.readInt()
				}
				clientScript.operations!![instructionIndex++] = opcode
			}
			return clientScript
		}

		@OriginalMember(owner = "client!on", name = "a", descriptor = "(IIILclient!uw;)Lclient!jk;")
		@JvmStatic
		fun getTriggerScript(@OriginalArg(0) subjectId: Int, @OriginalArg(1) categoryId: Int, @OriginalArg(3) triggerType: ClientTrigger?): ClientScript? {
			@Pc(10) var key: Int = triggerType!!.id or (subjectId shl 10)
			@Pc(19) var clientScript: ClientScript? = ClientScript.aClass379_1.get(key.toLong() shl 16) as ClientScript?
			if (clientScript != null) {
				return clientScript
			}
			@Pc(31) var data: ByteArray? = Resource.CS2!!.getFile(Resource.CS2!!.getGroupId(key))
			if (data == null) {
				key = triggerType.id or ((categoryId + 65536) shl 10)
				clientScript = ClientScript.aClass379_1.get(key.toLong() shl 16) as ClientScript?
				if (clientScript != null) {
					return clientScript
				}
				data = Resource.CS2!!.getFile(Resource.CS2!!.getGroupId(key))
				if (data == null) {
					key = triggerType.id or 0x3FFFC00
					clientScript = ClientScript.aClass379_1.get(key.toLong() shl 16) as ClientScript?
					if (clientScript != null) {
						return clientScript
					}
					data = Resource.CS2!!.getFile(Resource.CS2!!.getGroupId(key))
					if (data == null) {
						return null
					} else if (data.size <= 1) {
						return null
					} else {
						try {
							clientScript = ClientScript.decodeScript(data)
						} catch (@Pc(191) ex: Exception) {
							throw RuntimeException(ex.message + " S: " + key)
						}
						clientScript.triggerType = triggerType
						ClientScript.aClass379_1.put(key.toLong() shl 16, clientScript)
						return clientScript
					}
				} else if (data.size <= 1) {
					return null
				} else {
					try {
						clientScript = ClientScript.decodeScript(data)
					} catch (@Pc(121) ex2: Exception) {
						throw RuntimeException(ex2.message + " S: " + key)
					}
					clientScript.triggerType = triggerType
					ClientScript.aClass379_1.put(key.toLong() shl 16, clientScript)
					return clientScript
				}
			} else if (data.size <= 1) {
				return null
			} else {
				try {
					clientScript = ClientScript.decodeScript(data)
				} catch (@Pc(47) ex3: Exception) {
					throw RuntimeException(ex3.message + " S: " + key)
				}
				clientScript.triggerType = triggerType
				ClientScript.aClass379_1.put(key.toLong() shl 16, clientScript)
				return clientScript
			}
		}

		@OriginalMember(owner = "client!dea", name = "a", descriptor = "(II)Lclient!jk;")
		@JvmStatic
		fun getClientScript(@OriginalArg(1) scriptId: Int): ClientScript? {
			@Pc(10) var clientScript: ClientScript? = ClientScript.aClass379_1.get(scriptId.toLong()) as ClientScript?
			if (clientScript != null) {
				return clientScript
			}
			@Pc(20) val data: ByteArray? = Resource.CS2!!.getFile(scriptId, 0)
			if (data == null || data.size <= 1) {
				return null
			}
			try {
				clientScript = ClientScript.decodeScript(data)
			} catch (@Pc(34) ex: Exception) {
				throw RuntimeException(ex.message + " S: " + scriptId)
			}
			ClientScript.aClass379_1.put(scriptId.toLong(), clientScript)
			return clientScript
		}

		@OriginalMember(owner = "client!afa", name = "s", descriptor = "Lclient!wu;")
		@JvmField
		val aClass379_1: CacheableNodeQueue = CacheableNodeQueue(128)

		@OriginalMember(owner = "client!gea", name = "b", descriptor = "(I)V")
		@JvmStatic
		fun clearCache() {
			ClientScript.aClass379_1.clear()
		}
	}
}
