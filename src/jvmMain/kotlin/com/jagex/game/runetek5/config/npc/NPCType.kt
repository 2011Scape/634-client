package com.jagex.game.runetek5.config.npc

import com.jagex.game.runetek5.config.sequence.frameset.FrameSet
import com.jagex.game.runetek5.config.sequence.PathingEntitySequence
import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.config.sequence.SeqTypeList
import com.jagex.game.runetek5.cache.node.IntNode
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.StringNode
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.config.bas.BasType
import com.jagex.game.runetek5.config.bas.BasTypeList
import com.jagex.game.runetek5.vardomain.VarDomain
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.npctype.NPCTypeList
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!bb")
class NPCType {

	@OriginalMember(owner = "client!bb", name = "d", descriptor = "[S")
	private var recolorSrc: ShortArray? = null

	@OriginalMember(owner = "client!bb", name = "f", descriptor = "B")
	private var tintLightness: Byte = 0

	@OriginalMember(owner = "client!bb", name = "r", descriptor = "[I")
	@JvmField
	var quests: IntArray? = null

	@OriginalMember(owner = "client!bb", name = "t", descriptor = "I")
	@JvmField
	var id: Int = 0

	@OriginalMember(owner = "client!bb", name = "x", descriptor = "Lclient!vda;")
	@JvmField
	var npcTypeList: NPCTypeList? = null

	@OriginalMember(owner = "client!bb", name = "B", descriptor = "[S")
	private var recolorDst: ShortArray? = null

	@OriginalMember(owner = "client!bb", name = "J", descriptor = "B")
	private var tintSaturation: Byte = 0

	@OriginalMember(owner = "client!bb", name = "T", descriptor = "Z")
	@JvmField
	var instrumentSoundEffect: Boolean = false

	@OriginalMember(owner = "client!bb", name = "X", descriptor = "[B")
	private var recolorDPalette: ByteArray? = null

	@OriginalMember(owner = "client!bb", name = "Z", descriptor = "B")
	private var tintHue: Byte = 0

	@OriginalMember(owner = "client!bb", name = "ab", descriptor = "[I")
	@JvmField
	var alternativeIds: IntArray? = null

	@OriginalMember(owner = "client!bb", name = "db", descriptor = "[I")
	private var headModels: IntArray? = null

	@OriginalMember(owner = "client!bb", name = "jb", descriptor = "Lclient!eq;")
	private var parameters: IterableHashTable? = null

	@OriginalMember(owner = "client!bb", name = "lb", descriptor = "[[I")
	private var modelOffsets: Array<IntArray?>? = null

	@OriginalMember(owner = "client!bb", name = "ob", descriptor = "[S")
	private var retextureDst: ShortArray? = null

	@OriginalMember(owner = "client!bb", name = "qb", descriptor = "[S")
	private var retextureSrc: ShortArray? = null

	@OriginalMember(owner = "client!bb", name = "zb", descriptor = "[I")
	private var modelIds: IntArray? = null

	@OriginalMember(owner = "client!bb", name = "i", descriptor = "I")
	@JvmField
	var shadowSize: Int = -1

	@OriginalMember(owner = "client!bb", name = "z", descriptor = "S")
	@JvmField
	var shadowColorDst: Short = 0

	@OriginalMember(owner = "client!bb", name = "v", descriptor = "I")
	private var scaleXZ: Int = 128

	@OriginalMember(owner = "client!bb", name = "k", descriptor = "I")
	@JvmField
	var primaryCursorActionIndex: Int = -1

	@OriginalMember(owner = "client!bb", name = "m", descriptor = "I")
	@JvmField
	var sizeShift: Int = 0

	@OriginalMember(owner = "client!bb", name = "D", descriptor = "I")
	private var varp: Int = -1

	@OriginalMember(owner = "client!bb", name = "u", descriptor = "Z")
	@JvmField
	var highPriority: Boolean = false

	@OriginalMember(owner = "client!bb", name = "l", descriptor = "I")
	@JvmField
	var overheadSprite: Int = -1

	@OriginalMember(owner = "client!bb", name = "E", descriptor = "B")
	@JvmField
	var respawnDirection: Byte = 4

	@OriginalMember(owner = "client!bb", name = "F", descriptor = "I")
	@JvmField
	var ambientSoundVolume: Int = 255

	@OriginalMember(owner = "client!bb", name = "p", descriptor = "I")
	@JvmField
	var ambientSoundMaxDelay: Int = 256

	@OriginalMember(owner = "client!bb", name = "W", descriptor = "I")
	@JvmField
	var healthBarSpriteGroup: Int = -1

	@OriginalMember(owner = "client!bb", name = "Y", descriptor = "I")
	@JvmField
	var headIcons: Int = -1

	@OriginalMember(owner = "client!bb", name = "n", descriptor = "I")
	@JvmField
	var secondaryCursor: Int = -1

	@OriginalMember(owner = "client!bb", name = "M", descriptor = "I")
	@JvmField
	var ambientSoundMinDelay: Int = 256

	@OriginalMember(owner = "client!bb", name = "o", descriptor = "S")
	@JvmField
	var shadowColorSrc: Short = 0

	@OriginalMember(owner = "client!bb", name = "y", descriptor = "[Ljava/lang/String;")
	@JvmField
	val actions: Array<String?> = arrayOfNulls<String>(5)

	@OriginalMember(owner = "client!bb", name = "H", descriptor = "I")
	private var scaleY: Int = 128

	@OriginalMember(owner = "client!bb", name = "C", descriptor = "B")
	@JvmField
	var shadowAlphaSrc: Byte = -96

	@OriginalMember(owner = "client!bb", name = "L", descriptor = "Z")
	@JvmField
	var lowPriority: Boolean = false

	@OriginalMember(owner = "client!bb", name = "s", descriptor = "I")
	@JvmField
	var walkingSoundEffect: Int = -1

	@OriginalMember(owner = "client!bb", name = "hb", descriptor = "B")
	@JvmField
	var gameType: Byte = -1

	@OriginalMember(owner = "client!bb", name = "eb", descriptor = "Z")
	@JvmField
	var mediumPriority: Boolean = false

	@OriginalMember(owner = "client!bb", name = "gb", descriptor = "I")
	@JvmField
	var mecId: Int = -1

	@OriginalMember(owner = "client!bb", name = "V", descriptor = "Ljava/lang/String;")
	@JvmField
	var name: String? = "null"

	@OriginalMember(owner = "client!bb", name = "fb", descriptor = "I")
	@JvmField
	var secondaryBarSpriteGroup: Int = -1

	@OriginalMember(owner = "client!bb", name = "U", descriptor = "I")
	@JvmField
	var primaryCursor: Int = -1

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "B")
	@JvmField
	var walkingFlags: Byte = 0

	@OriginalMember(owner = "client!bb", name = "R", descriptor = "I")
	private var varpbit: Int = -1

	@OriginalMember(owner = "client!bb", name = "nb", descriptor = "I")
	@JvmField
	var iconHeight: Int = -1

	@OriginalMember(owner = "client!bb", name = "g", descriptor = "Z")
	@JvmField
	var delayMovement: Boolean = true

	@OriginalMember(owner = "client!bb", name = "w", descriptor = "B")
	@JvmField
	var shadowAlphaDst: Byte = -16

	@OriginalMember(owner = "client!bb", name = "P", descriptor = "I")
	@JvmField
	var npcBasId: Int = -1

	@OriginalMember(owner = "client!bb", name = "vb", descriptor = "I")
	private var ambient: Int = 0

	@OriginalMember(owner = "client!bb", name = "pb", descriptor = "I")
	@JvmField
	var ambientSoundMinHearDistance: Int = 0

	@OriginalMember(owner = "client!bb", name = "N", descriptor = "I")
	@JvmField
	var idleSoundEffect: Int = -1

	@OriginalMember(owner = "client!bb", name = "c", descriptor = "I")
	@JvmField
	var runningSoundEffect: Int = -1

	@OriginalMember(owner = "client!bb", name = "K", descriptor = "I")
	@JvmField
	var combatLevel: Int = -1

	@OriginalMember(owner = "client!bb", name = "e", descriptor = "I")
	@JvmField
	var turnDirection: Int = 32

	@OriginalMember(owner = "client!bb", name = "Cb", descriptor = "B")
	private var tintOpacity: Byte = 0

	@OriginalMember(owner = "client!bb", name = "ib", descriptor = "I")
	@JvmField
	var secondaryCursorActionIndex: Int = -1

	@OriginalMember(owner = "client!bb", name = "sb", descriptor = "I")
	@JvmField
	var teleportSoundEffect: Int = -1

	@OriginalMember(owner = "client!bb", name = "S", descriptor = "Z")
	@JvmField
	var shadowed: Boolean = true

	@OriginalMember(owner = "client!bb", name = "wb", descriptor = "I")
	@JvmField
	var size: Int = 1

	@OriginalMember(owner = "client!bb", name = "yb", descriptor = "I")
	@JvmField
	var attackCursor: Int = -1

	@OriginalMember(owner = "client!bb", name = "tb", descriptor = "Z")
	@JvmField
	var visible: Boolean = true

	@OriginalMember(owner = "client!bb", name = "ub", descriptor = "Z")
	@JvmField
	var displayOnMinimap: Boolean = true

	@OriginalMember(owner = "client!bb", name = "Db", descriptor = "I")
	private var contrast: Int = 0

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(I)Z")
	fun hasSounds(): Boolean {
		if (this.alternativeIds == null) {
			return this.walkingSoundEffect != -1 || this.idleSoundEffect != -1 || this.runningSoundEffect != -1
		}
		@Pc(28) var i: Int = 0
		while (i < this.alternativeIds!!.size) {
			if (this.alternativeIds!![i] != -1) {
				@Pc(46) val type: NPCType = this.npcTypeList!!.list(this.alternativeIds!![i])
				if (type.walkingSoundEffect != -1 || type.idleSoundEffect != -1 || type.runningSoundEffect != -1) {
					return true
				}
			}
			i++
		}
		return false
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(Lclient!dd;I)Lclient!bb;")
	fun getMultiNpc(@OriginalArg(0) varDomain: VarDomain?): NPCType? {
		@Pc(5) var varValue: Int = -1
		if (this.varpbit != -1) {
			varValue = varDomain!!.getVarBitValue(this.varpbit)
		} else if (this.varp != -1) {
			varValue = varDomain!!.getVarValueInt(this.varp)
		}
		if (varValue < 0 || varValue >= this.alternativeIds!!.size - 1 || this.alternativeIds!![varValue] == -1) {
			@Pc(55) val defaultId: Int = this.alternativeIds!![this.alternativeIds!!.size - 1]
			return if (defaultId == -1) null else this.npcTypeList!!.list(defaultId)
		} else {
			return this.npcTypeList!!.list(this.alternativeIds!![varValue])
		}
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(Lclient!cea;II)V")
	private fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		@Pc(16) var local16: Int
		@Pc(22) var local22: Int
		if (opcode == 1) {
			val modelCount: Int = packet!!.readUByte()
			this.modelIds = IntArray(modelCount)
			var modelIndex: Int = 0
			while (modelIndex < modelCount) {
				this.modelIds!![modelIndex] = packet.readUShort()
				if (this.modelIds!![modelIndex] == 65535) {
					this.modelIds!![modelIndex] = -1
				}
				modelIndex++
			}
		} else if (opcode == 2) {
			this.name = packet!!.readString()
		} else if (opcode == 12) {
			this.size = packet!!.readUByte()
		} else if (opcode >= 30 && opcode < 35) {
			this.actions[opcode - 30] = packet!!.readString()
		} else if (opcode == 40) {
			val recolorCount: Int = packet!!.readUByte()
			this.recolorSrc = ShortArray(recolorCount)
			this.recolorDst = ShortArray(recolorCount)
			var recolorIndex: Int = 0
			while (recolorIndex < recolorCount) {
				this.recolorSrc!![recolorIndex] = packet.readUShort().toShort()
				this.recolorDst!![recolorIndex] = packet.readUShort().toShort()
				recolorIndex++
			}
		} else if (opcode == 41) {
			val retextureCount: Int = packet!!.readUByte()
			this.retextureSrc = ShortArray(retextureCount)
			this.retextureDst = ShortArray(retextureCount)
			var retextureIndex: Int = 0
			while (retextureIndex < retextureCount) {
				this.retextureSrc!![retextureIndex] = packet.readUShort().toShort()
				this.retextureDst!![retextureIndex] = packet.readUShort().toShort()
				retextureIndex++
			}
		} else if (opcode == 42) {
			val paletteCount: Int = packet!!.readUByte()
			this.recolorDPalette = ByteArray(paletteCount)
			var paletteIndex: Int = 0
			while (paletteIndex < paletteCount) {
				this.recolorDPalette!![paletteIndex] = packet.readByte()
				paletteIndex++
			}
		} else if (opcode == 60) {
			val headModelCount: Int = packet!!.readUByte()
			this.headModels = IntArray(headModelCount)
			var headModelIndex: Int = 0
			while (headModelIndex < headModelCount) {
				this.headModels!![headModelIndex] = packet.readUShort()
				headModelIndex++
			}
		} else if (opcode == 93) {
			this.displayOnMinimap = false
		} else if (opcode == 95) {
			this.combatLevel = packet!!.readUShort()
		} else if (opcode == 97) {
			this.scaleXZ = packet!!.readUShort()
		} else if (opcode == 98) {
			this.scaleY = packet!!.readUShort()
		} else if (opcode == 99) {
			this.highPriority = true
		} else if (opcode == 100) {
			this.ambient = packet!!.readByte().toInt()
		} else if (opcode == 101) {
			this.contrast = packet!!.readByte().toInt() * 5
		} else if (opcode == 102) {
			this.headIcons = packet!!.readUShort()
		} else if (opcode == 103) {
			this.turnDirection = packet!!.readUShort()
		} else {
			@Pc(267) var local267: Int
			if (opcode == 106 || opcode == 118) {
				this.varpbit = packet!!.readUShort()
				if (this.varpbit == 65535) {
					this.varpbit = -1
				}
				this.varp = packet.readUShort()
				if (this.varp == 65535) {
					this.varp = -1
				}
				var defaultAlternativeId: Int = -1
				if (opcode == 118) {
					defaultAlternativeId = packet.readUShort()
					if (defaultAlternativeId == 65535) {
						defaultAlternativeId = -1
					}
				}
				val lastAlternativeIndex: Int = packet.readUByte()
				this.alternativeIds = IntArray(lastAlternativeIndex + 2)
				var alternativeIndex: Int = 0
				while (alternativeIndex <= lastAlternativeIndex) {
					this.alternativeIds!![alternativeIndex] = packet.readUShort()
					if (this.alternativeIds!![alternativeIndex] == 65535) {
						this.alternativeIds!![alternativeIndex] = -1
					}
					alternativeIndex++
				}
				this.alternativeIds!![lastAlternativeIndex + 1] = defaultAlternativeId
			} else if (opcode == 107) {
				this.visible = false
			} else if (opcode == 109) {
				this.delayMovement = false
			} else if (opcode == 111) {
				this.shadowed = false
			} else if (opcode == 113) {
				this.shadowColorSrc = packet!!.readUShort().toShort()
				this.shadowColorDst = packet.readUShort().toShort()
			} else if (opcode == 114) {
				this.shadowAlphaSrc = packet!!.readByte()
				this.shadowAlphaDst = packet.readByte()
			} else if (opcode == 119) {
				this.walkingFlags = packet!!.readByte()
			} else if (opcode == 121) {
				this.modelOffsets = arrayOfNulls<IntArray>(this.modelIds!!.size)
				val offsetCount: Int = packet!!.readUByte()
				var offsetIndex: Int = 0
				while (offsetIndex < offsetCount) {
					val modelIndex: Int = packet.readUByte()
					@Pc(377) val offsets: IntArray = IntArray(3)
					this.modelOffsets!![modelIndex] = offsets
					offsets[0] = packet.readByte().toInt()
					offsets[1] = packet.readByte().toInt()
					offsets[2] = packet.readByte().toInt()
					offsetIndex++
				}
			} else if (opcode == 122) {
				this.healthBarSpriteGroup = packet!!.readUShort()
			} else if (opcode == 123) {
				this.iconHeight = packet!!.readUShort()
			} else if (opcode == 125) {
				this.respawnDirection = packet!!.readByte()
			} else if (opcode == 127) {
				this.npcBasId = packet!!.readUShort()
			} else if (opcode == 128) {
				packet!!.readUByte()
			} else if (opcode == 134) {
				this.walkingSoundEffect = packet!!.readUShort()
				if (this.walkingSoundEffect == 65535) {
					this.walkingSoundEffect = -1
				}
				this.teleportSoundEffect = packet.readUShort()
				if (this.teleportSoundEffect == 65535) {
					this.teleportSoundEffect = -1
				}
				this.idleSoundEffect = packet.readUShort()
				if (this.idleSoundEffect == 65535) {
					this.idleSoundEffect = -1
				}
				this.runningSoundEffect = packet.readUShort()
				if (this.runningSoundEffect == 65535) {
					this.runningSoundEffect = -1
				}
				this.ambientSoundMinHearDistance = packet.readUByte()
			} else if (opcode == 135) {
				this.primaryCursorActionIndex = packet!!.readUByte()
				this.primaryCursor = packet.readUShort()
			} else if (opcode == 136) {
				this.secondaryCursorActionIndex = packet!!.readUByte()
				this.secondaryCursor = packet.readUShort()
			} else if (opcode == 137) {
				this.attackCursor = packet!!.readUShort()
			} else if (opcode == 138) {
				this.overheadSprite = packet!!.readUShort()
			} else if (opcode == 139) {
				this.secondaryBarSpriteGroup = packet!!.readUShort()
			} else if (opcode == 140) {
				this.ambientSoundVolume = packet!!.readUByte()
			} else if (opcode == 141) {
				this.lowPriority = true
			} else if (opcode == 142) {
				this.mecId = packet!!.readUShort()
			} else if (opcode == 143) {
				this.mediumPriority = true
			} else if (opcode >= 150 && opcode < 155) {
				this.actions[opcode - 150] = packet!!.readString()
				if (!this.npcTypeList!!.allowMembers) {
					this.actions[opcode - 150] = null
				}
			} else if (opcode == 155) {
				this.tintHue = packet!!.readByte()
				this.tintSaturation = packet.readByte()
				this.tintLightness = packet.readByte()
				this.tintOpacity = packet.readByte()
			} else if (opcode == 158) {
				this.gameType = 1
			} else if (opcode == 159) {
				this.gameType = 0
			} else if (opcode == 160) {
				val questCount: Int = packet!!.readUByte()
				this.quests = IntArray(questCount)
				var questIndex: Int = 0
				while (questIndex < questCount) {
					this.quests!![questIndex] = packet.readUShort()
					questIndex++
				}
			} else if (opcode == 162) {
				this.instrumentSoundEffect = true
			} else if (opcode == 163) {
				this.shadowSize = packet!!.readUByte()
			} else if (opcode == 164) {
				this.ambientSoundMaxDelay = packet!!.readUShort()
				this.ambientSoundMinDelay = packet.readUShort()
			} else if (opcode == 165) {
				this.sizeShift = packet!!.readUByte()
			} else if (opcode == 249) {
				val paramCount: Int = packet!!.readUByte()
				if (this.parameters == null) {
					val bucketCount: Int = Utils.nextPowerOfTwo(paramCount)
					this.parameters = IterableHashTable(bucketCount)
				}
				var paramIndex: Int = 0
				while (paramIndex < paramCount) {
					@Pc(687) val isString: Boolean = packet.readUByte() == 1
					@Pc(691) val key: Int = packet.readUMedium()
					@Pc(700) val node: Node
					if (isString) {
						node = StringNode(packet.readString())
					} else {
						node = IntNode(packet.readInt())
					}
					this.parameters!!.put(key.toLong(), node)
					paramIndex++
				}
			}
		}
	}

	@OriginalMember(owner = "client!bb", name = "b", descriptor = "(Lclient!dd;I)Z")
	fun resolvesToNpc(@OriginalArg(0) varDomain: VarDomain?): Boolean {
		if (this.alternativeIds == null) {
			return true
		}
		@Pc(10) var varValue: Int = -1
		if (this.varpbit != -1) {
			varValue = varDomain!!.getVarBitValue(this.varpbit)
		} else if (this.varp != -1) {
			varValue = varDomain!!.getVarValueInt(this.varp)
		}
		if (varValue < 0 || varValue >= this.alternativeIds!!.size - 1 || this.alternativeIds!![varValue] == -1) {
			@Pc(61) val defaultId: Int = this.alternativeIds!![this.alternativeIds!!.size - 1]
			return defaultId != -1
		} else {
			return true
		}
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(ILclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet?) {
		while (true) {
			@Pc(5) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}

	@OriginalMember(owner = "client!bb", name = "b", descriptor = "(I)V")
	fun postDecode() {
		if (this.modelIds == null) {
			this.modelIds = IntArray(0)
		}
		if (this.gameType.toInt() != -1) {
			return
		}
		if (Game.RUNESCAPE === this.npcTypeList!!.game) {
			this.gameType = 1
		} else {
			this.gameType = 0
		}
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(I[Lclient!qca;Lclient!lfa;ZLclient!hg;ILclient!uga;ILclient!hg;Lclient!dd;Lclient!ha;I[IIIII)Lclient!ka;")
	fun getBodyModel(@OriginalArg(0) rotation: Int, @OriginalArg(1) sequences: Array<PathingEntitySequence?>?, @OriginalArg(2) seqTypeList: SeqTypeList?, @OriginalArg(4) seqType: SeqType?, @OriginalArg(5) moveFrameDuration: Int, @OriginalArg(6) basTypeList: BasTypeList?, @OriginalArg(7) animFrameIndex: Int, @OriginalArg(8) seqType2: SeqType?, @OriginalArg(9) varDomain: VarDomain?, @OriginalArg(10) toolkit: Toolkit?, @OriginalArg(11) moveNextFrameIndex: Int, @OriginalArg(12) wornRotations: IntArray?, @OriginalArg(13) animNextFrameIndex: Int, @OriginalArg(14) moveFrameIndex: Int, @OriginalArg(15) initialFunctionMask: Int, @OriginalArg(16) animFrameDuration: Int): Model? {
		if (this.alternativeIds != null) {
			@Pc(13) val npcType: NPCType? = this.getMultiNpc(varDomain)
			return if (npcType == null) null else npcType.getBodyModel(rotation, sequences, seqTypeList, seqType, moveFrameDuration, basTypeList, animFrameIndex, seqType2, varDomain, toolkit, moveNextFrameIndex, wornRotations, animNextFrameIndex, moveFrameIndex, initialFunctionMask, animFrameDuration)
		}
		@Pc(39) var functionMask: Int = initialFunctionMask
		if (this.scaleY != 128) {
			functionMask = initialFunctionMask or 0x2
		}
		if (this.scaleXZ != 128) {
			functionMask = functionMask or 0x5
		}
		@Pc(63) var animated: Boolean = seqType2 != null || seqType != null
		@Pc(65) var modifiesAlpha: Boolean = false
		@Pc(67) var modifiesColor: Boolean = false
		@Pc(69) var lights: Boolean = false
		@Pc(71) var modifiesBillboard: Boolean = false
		@Pc(78) val sequenceCount: Int = if (sequences == null) 0 else sequences.size
		@Pc(112) var local112: Int
		@Pc(117) var local117: Int
		@Pc(80) var i: Int = 0
		while (i < sequenceCount) {
			NPCType.aClass2_Sub6_Sub9Array1[i] = null
			if (sequences!![i] != null) {
				@Pc(98) val seqType3: SeqType = seqTypeList!!.list(sequences[i]!!.sequenceId)
				if (seqType3.frames != null) {
					animated = true
					NPCType.aClass130Array2[i] = seqType3
					val currentFrameIndex: Int = sequences[i]!!.currentFrameId
					val nextFrameIndex: Int = sequences[i]!!.nextFrameId
					@Pc(122) val frame: Int = seqType3.frames!![currentFrameIndex]
					NPCType.aClass2_Sub6_Sub9Array1[i] = seqTypeList.listFrameSet(frame ushr 16)
					@Pc(135) val frameId: Int = frame and 0xFFFF
					NPCType.ANIM_FRAME_IDS[i] = frameId
					if (NPCType.aClass2_Sub6_Sub9Array1[i] != null) {
						modifiesColor = modifiesColor or NPCType.aClass2_Sub6_Sub9Array1[i]!!.modifiesColors(frameId)
						modifiesAlpha = modifiesAlpha or NPCType.aClass2_Sub6_Sub9Array1[i]!!.modifiesAlpha(frameId)
						modifiesBillboard = modifiesBillboard or NPCType.aClass2_Sub6_Sub9Array1[i]!!.modifiesBillboards(frameId)
						lights = lights or seqType3.lights
					}
					if ((seqType3.tweened || SeqType.forceTweening) && nextFrameIndex != -1 && nextFrameIndex < seqType3.frames!!.size) {
						NPCType.ANIM_FRAME_LENGTHS[i] = seqType3.durations!![currentFrameIndex]
						NPCType.ANIM_FRAME_ELAPSED[i] = sequences[i]!!.currentFrameDuration
						@Pc(207) val nextFrame: Int = seqType3.frames!![nextFrameIndex]
						NPCType.aClass2_Sub6_Sub9Array2[i] = seqTypeList.listFrameSet(nextFrame ushr 16)
						@Pc(220) val nextFrameId: Int = nextFrame and 0xFFFF
						NPCType.ANIM_NEXT_FRAME_IDS[i] = nextFrameId
						if (NPCType.aClass2_Sub6_Sub9Array2[i] != null) {
							modifiesColor = modifiesColor or NPCType.aClass2_Sub6_Sub9Array2[i]!!.modifiesColors(nextFrameId)
							modifiesAlpha = modifiesAlpha or NPCType.aClass2_Sub6_Sub9Array2[i]!!.modifiesAlpha(nextFrameId)
							modifiesBillboard = modifiesBillboard or NPCType.aClass2_Sub6_Sub9Array2[i]!!.modifiesBillboards(nextFrameId)
						}
					} else {
						NPCType.ANIM_FRAME_LENGTHS[i] = 0
						NPCType.ANIM_FRAME_ELAPSED[i] = 0
						NPCType.aClass2_Sub6_Sub9Array2[i] = null
						NPCType.ANIM_NEXT_FRAME_IDS[i] = -1
					}
				}
			}
			i++
		}
		@Pc(283) var animFrame: Int = -1
		var animNextFrame: Int = -1
		var animFrameLength: Int = 0
		@Pc(289) var animFrameset: FrameSet? = null
		@Pc(291) var animFrameset2: FrameSet? = null
		@Pc(293) var moveFrame: Int = -1
		@Pc(295) var moveNextFrame: Int = -1
		@Pc(297) var moveFrameLength: Int = 0
		@Pc(299) var animFrameset3: FrameSet? = null
		@Pc(301) var animFrameset4: FrameSet? = null
		if (animated) {
			functionMask = functionMask or 0x20
			@Pc(318) var frameSetId: Int
			@Pc(382) var nextFrameSetId: Int
			if (seqType2 != null) {
				animFrame = seqType2.frames!![animFrameIndex]
				frameSetId = animFrame ushr 16
				animFrame = animFrame and 0xFFFF
				animFrameset = seqTypeList!!.listFrameSet(frameSetId)
				if (animFrameset != null) {
					modifiesColor = modifiesColor or animFrameset.modifiesColors(animFrame)
					modifiesAlpha = modifiesAlpha or animFrameset.modifiesAlpha(animFrame)
					modifiesBillboard = modifiesBillboard or animFrameset.modifiesBillboards(animFrame)
					lights = lights or seqType2.lights
				}
				if ((seqType2.tweened || SeqType.forceTweening) && animNextFrameIndex != -1 && seqType2.frames!!.size > animNextFrameIndex) {
					animNextFrame = seqType2.frames!![animNextFrameIndex]
					animFrameLength = seqType2.durations!![animFrameIndex]
					nextFrameSetId = animNextFrame ushr 16
					if (frameSetId == nextFrameSetId) {
						animFrameset2 = animFrameset
					} else {
						animFrameset2 = seqTypeList.listFrameSet(nextFrameSetId)
					}
					animNextFrame = animNextFrame and 0xFFFF
					if (animFrameset2 != null) {
						modifiesColor = modifiesColor or animFrameset2.modifiesColors(animNextFrame)
						modifiesAlpha = modifiesAlpha or animFrameset2.modifiesAlpha(animNextFrame)
						modifiesBillboard = modifiesBillboard or animFrameset2.modifiesBillboards(animNextFrame)
					}
				}
			}
			if (seqType != null) {
				moveFrame = seqType.frames!![moveFrameIndex]
				frameSetId = moveFrame ushr 16
				animFrameset3 = seqTypeList!!.listFrameSet(frameSetId)
				moveFrame = moveFrame and 0xFFFF
				if (animFrameset3 != null) {
					modifiesColor = modifiesColor or animFrameset3.modifiesColors(moveFrame)
					modifiesAlpha = modifiesAlpha or animFrameset3.modifiesAlpha(moveFrame)
					modifiesBillboard = modifiesBillboard or animFrameset3.modifiesBillboards(moveFrame)
					lights = lights or seqType.lights
				}
				if ((seqType.tweened || SeqType.forceTweening) && moveNextFrameIndex != -1 && seqType.frames!!.size > moveNextFrameIndex) {
					moveFrameLength = seqType.durations!![moveFrameIndex]
					moveNextFrame = seqType.frames!![moveNextFrameIndex]
					nextFrameSetId = moveNextFrame ushr 16
					if (nextFrameSetId == frameSetId) {
						animFrameset4 = animFrameset3
					} else {
						animFrameset4 = seqTypeList.listFrameSet(nextFrameSetId)
					}
					moveNextFrame = moveNextFrame and 0xFFFF
					if (animFrameset4 != null) {
						modifiesColor = modifiesColor or animFrameset4.modifiesColors(moveNextFrame)
						modifiesAlpha = modifiesAlpha or animFrameset4.modifiesAlpha(moveNextFrame)
						modifiesBillboard = modifiesBillboard or animFrameset4.modifiesBillboards(moveNextFrame)
					}
				}
			}
			if (modifiesColor) {
				functionMask = functionMask or 0x80
			}
			if (modifiesAlpha) {
				functionMask = functionMask or 0x100
			}
			if (lights) {
				functionMask = functionMask or 0x200
			}
			if (modifiesBillboard) {
				functionMask = functionMask or 0x400
			}
		}
		@Pc(566) val cacheKey: Long = ((toolkit!!.index shl 16) or this.id).toLong()
		@Pc(570) val referenceCache: LRUCache = this.npcTypeList!!.modelCache
		@Pc(580) var model: Model?
		synchronized(this.npcTypeList!!.modelCache) {
			model = this.npcTypeList!!.modelCache.get(cacheKey) as Model?
		}
		@Pc(588) var basType: BasType? = null
		if (this.npcBasId != -1) {
			basType = basTypeList!!.list(this.npcBasId)
		}
		@Pc(621) var local621: Boolean
		@Pc(629) var local629: Int
		@Pc(789) var local789: Int
		@Pc(771) var index: Int
		if (model == null || (functionMask and model!!.ua()) != functionMask) {
			if (model != null) {
				functionMask = functionMask or model!!.ua()
			}
			@Pc(619) var modelFunctionMask: Int = functionMask
			var meshesNotReady: Boolean = false
			@Pc(625) val meshResource: Js5ResourceProvider? = this.npcTypeList!!.meshes
			synchronized(this.npcTypeList!!.meshes!!) {
				var modelIndex: Int = 0
				while (modelIndex < this.modelIds!!.size) {
					if (this.modelIds!![modelIndex] != -1 && !this.npcTypeList!!.meshes!!.isFileReady(this.modelIds!![modelIndex], 0)) {
						meshesNotReady = true
					}
					modelIndex++
				}
			}
			if (meshesNotReady) {
				return null
			}
			@Pc(675) val meshes: Array<Mesh?> = arrayOfNulls<Mesh>(this.modelIds!!.size)
			var modelIndex: Int = 0
			while (modelIndex < this.modelIds!!.size) {
				if (this.modelIds!![modelIndex] != -1) {
					@Pc(690) val meshResource2: Js5ResourceProvider? = this.npcTypeList!!.meshes
					synchronized(this.npcTypeList!!.meshes!!) {
						meshes[modelIndex] = Mesh.fromResource(this.npcTypeList!!.meshes, this.modelIds!![modelIndex])
					}
					if (meshes[modelIndex] != null) {
						if (meshes[modelIndex]!!.version < 13) {
							meshes[modelIndex]!!.upscale()
						}
						if (this.modelOffsets != null && this.modelOffsets!![modelIndex] != null) {
							meshes[modelIndex]!!.translate(this.modelOffsets!![modelIndex]!![2], this.modelOffsets!![modelIndex]!![0], this.modelOffsets!![modelIndex]!![1])
						}
					}
				}
				modelIndex++
			}
			if (basType != null && basType.equipmentTransforms != null) {
				index = 0
				while (index < basType.equipmentTransforms!!.size) {
					if (index < meshes.size && meshes[index] != null) {
						var translateX: Int = 0
						@Pc(791) var translateY: Int = 0
						@Pc(793) var translateZ: Int = 0
						@Pc(795) var rotateX: Int = 0
						@Pc(797) var rotateY: Int = 0
						@Pc(799) var rotateZ: Int = 0
						if (basType.equipmentTransforms!![index] != null) {
							rotateY = basType.equipmentTransforms!![index]!![4] shl 3
							rotateZ = basType.equipmentTransforms!![index]!![5] shl 3
							translateY = basType.equipmentTransforms!![index]!![1]
							translateZ = basType.equipmentTransforms!![index]!![2]
							rotateX = basType.equipmentTransforms!![index]!![3] shl 3
							translateX = basType.equipmentTransforms!![index]!![0]
						}
						if (rotateX != 0 || rotateY != 0 || rotateZ != 0) {
							meshes[index]!!.rotateWithoutNormals(rotateY, rotateZ, rotateX)
						}
						if (translateX != 0 || translateY != 0 || translateZ != 0) {
							meshes[index]!!.translate(translateZ, translateX, translateY)
						}
					}
					index++
				}
			}
			@Pc(908) val mesh: Mesh?
			if (meshes.size == 1) {
				mesh = meshes[0]
			} else {
				mesh = Mesh(meshes, meshes.size)
			}
			if (this.recolorSrc != null) {
				modelFunctionMask = functionMask or 0x4000
			}
			if (this.retextureSrc != null) {
				modelFunctionMask = modelFunctionMask or 0x8000
			}
			if (this.tintOpacity.toInt() != 0) {
				modelFunctionMask = modelFunctionMask or 0x80000
			}
			val newModel: Model = toolkit.createModel(mesh, modelFunctionMask, this.npcTypeList!!.featureMask, this.ambient + 64, this.contrast + 850)!!
			model = newModel
			if (this.recolorSrc != null) {
				index = 0
				while (index < this.recolorSrc!!.size) {
					if (this.recolorDPalette == null || index >= this.recolorDPalette!!.size) {
						newModel.ia(this.recolorSrc!![index], this.recolorDst!![index])
					} else {
						newModel.ia(this.recolorSrc!![index], NPCType.npcRecolorPalette[this.recolorDPalette!![index].toInt() and 0xFF])
					}
					index++
				}
			}
			if (this.retextureSrc != null) {
				index = 0
				while (index < this.retextureSrc!!.size) {
					newModel.aa(this.retextureSrc!![index], this.retextureDst!![index])
					index++
				}
			}
			if (this.tintOpacity.toInt() != 0) {
				newModel.adjustColours(this.tintHue.toInt(), this.tintSaturation.toInt(), this.tintLightness.toInt(), this.tintOpacity.toInt() and 0xFF)
			}
			newModel.s(functionMask)
			@Pc(1059) val referenceCache2: LRUCache = this.npcTypeList!!.modelCache
			synchronized(this.npcTypeList!!.modelCache) {
				this.npcTypeList!!.modelCache.put(newModel, (this.id or (toolkit.index shl 16)).toLong())
			}
		}
		@Pc(1087) val model2: Model = model!!.copy(4.toByte(), functionMask, true)!!
		var hasWornRotation: Boolean = false
		if (wornRotations != null) {
			@Pc(1093) var j: Int = 0
			while (j < 12) {
				if (wornRotations[j] != -1) {
					hasWornRotation = true
				}
				j++
			}
		}
		if (!animated && !hasWornRotation) {
			return model2
		}
		@Pc(1115) var matrixes: Array<Matrix?>? = null
		if (basType != null) {
			matrixes = basType.getSubmeshMatrices(toolkit)
		}
		@Pc(1128) var slot: Int
		if (hasWornRotation && matrixes != null) {
			slot = 0
			while (slot < 12) {
				if (matrixes[slot] != null) {
					model2.transform(matrixes[slot], 0x1 shl slot, true)
				}
				slot++
			}
		}
		slot = 0
		var submeshMask: Int = 1
		while (slot < sequenceCount) {
			if (NPCType.aClass2_Sub6_Sub9Array1[slot] != null) {
				model2.animateUnrotatedSubmesh(NPCType.ANIM_FRAME_ELAPSED[slot] - 1, null, submeshMask, NPCType.ANIM_FRAME_IDS[slot], NPCType.ANIM_NEXT_FRAME_IDS[slot], NPCType.aClass2_Sub6_Sub9Array2[slot], NPCType.aClass2_Sub6_Sub9Array1[slot], NPCType.aClass130Array2[slot]!!.lights, NPCType.ANIM_FRAME_LENGTHS[slot])
			}
			slot++
			submeshMask = submeshMask shl 0x1
		}
		if (hasWornRotation) {
			index = 0
			while (index < 12) {
				if (wornRotations!![index] != -1) {
					var angle: Int = wornRotations[index] - rotation
					angle = angle and 0x3FFF
					@Pc(1223) val matrix: Matrix = toolkit.createMatrix()!!
					matrix.rotate(angle)
					model2.transform(matrix, 0x1 shl index, false)
				}
				index++
			}
		}
		if (hasWornRotation && matrixes != null) {
			index = 0
			while (index < 12) {
				if (matrixes[index] != null) {
					model2.transform(matrixes[index], 0x1 shl index, false)
				}
				index++
			}
		}
		if (animFrameset != null && animFrameset3 != null) {
			model2.doubleAnimation(animFrameset2, animFrameLength, moveFrameLength, animFrameset3, animFrame, moveFrame, animFrameDuration - 1, animFrameset, seqType2!!.lights or seqType!!.lights, animNextFrame, animFrameset4, moveFrameDuration - 1, seqType2.interLeaveOrder, moveNextFrame)
		} else if (animFrameset != null) {
			model2.animateModelFrame(animFrame, animFrameLength, animFrameset2, 0, animFrameset, seqType2!!.lights, animNextFrame, animFrameDuration - 1)
		} else if (animFrameset3 != null) {
			model2.animateModelFrame(moveFrame, moveFrameLength, animFrameset4, 0, animFrameset3, seqType!!.lights, moveNextFrame, moveFrameDuration - 1)
		}
		index = 0
		while (index < sequenceCount) {
			NPCType.aClass2_Sub6_Sub9Array1[index] = null
			NPCType.aClass2_Sub6_Sub9Array2[index] = null
			NPCType.aClass130Array2[index] = null
			index++
		}
		if (this.scaleXZ != 128 || this.scaleY != 128) {
			model2.O(this.scaleXZ, this.scaleY, this.scaleXZ)
		}
		model2.s(initialFunctionMask)
		return model2
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(BILjava/lang/String;)Ljava/lang/String;")
	fun getStringParam(@OriginalArg(1) paramKey: Int, @OriginalArg(2) string: String?): String? {
		if (this.parameters == null) {
			return string
		} else {
			@Pc(27) val stringNode: StringNode? = this.parameters!!.get(paramKey.toLong()) as StringNode?
			return if (stringNode == null) string else stringNode.value
		}
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(Lclient!dd;Lclient!ha;IILclient!hg;Lclient!lfa;III)Lclient!ka;")
	fun getAnimatedHeadModel(@OriginalArg(0) varDomain: VarDomain?, @OriginalArg(1) toolkit: Toolkit?, @OriginalArg(2) currentFrameDuration: Int, @OriginalArg(3) nextFrameId: Int, @OriginalArg(4) seqType: SeqType?, @OriginalArg(5) seqTypeList: SeqTypeList?, @OriginalArg(6) currentFrameId: Int): Model? {
		if (this.alternativeIds != null) {
			@Pc(13) val npcType: NPCType? = this.getMultiNpc(varDomain)
			return if (npcType == null) null else npcType.getAnimatedHeadModel(varDomain, toolkit, currentFrameDuration, nextFrameId, seqType, seqTypeList, currentFrameId)
		} else if (this.headModels == null) {
			return null
		} else {
			@Pc(44) var functionMask: Int = 2048
			if (seqType != null && currentFrameId != -1) {
				functionMask = seqType.getFunctionMask(nextFrameId, currentFrameId, true) or 0x800
			}
			@Pc(62) val referenceCache: LRUCache = this.npcTypeList!!.headModels
			@Pc(79) var model: Model?
			synchronized(this.npcTypeList!!.headModels) {
				model = this.npcTypeList!!.headModels.get(((toolkit!!.index shl 16) or this.id).toLong()) as Model?
			}
			if (model == null || (model!!.ua() and functionMask) != functionMask) {
				if (model != null) {
					functionMask = functionMask or model!!.ua()
				}
				@Pc(106) var modelFunctionMask: Int = functionMask
				@Pc(108) var missingModels: Boolean = false
				@Pc(112) val meshResource: Js5ResourceProvider? = this.npcTypeList!!.meshes
				synchronized(this.npcTypeList!!.meshes!!) {
					@Pc(116) var index: Int = 0
					while (true) {
						if (index >= this.headModels!!.size) {
							break
						}
						if (!this.npcTypeList!!.meshes!!.isFileReady(this.headModels!![index], 0)) {
							missingModels = true
						}
						index++
					}
				}
				if (missingModels) {
					return null
				}
				@Pc(156) val meshes: Array<Mesh?> = arrayOfNulls<Mesh>(this.headModels!!.size)
				@Pc(160) val meshResource2: Js5ResourceProvider? = this.npcTypeList!!.meshes
				@Pc(164) var index: Int
				synchronized(this.npcTypeList!!.meshes!!) {
					index = 0
					while (index < this.headModels!!.size) {
						meshes[index] = Mesh.fromResource(this.npcTypeList!!.meshes, this.headModels!![index])
						index++
					}
				}
				@Pc(197) var i: Int = 0
				while (i < this.headModels!!.size) {
					if (meshes[i] != null && meshes[i]!!.version < 13) {
						meshes[i]!!.upscale()
					}
					i++
				}
				@Pc(235) val mesh: Mesh?
				if (meshes.size == 1) {
					mesh = meshes[0]
				} else {
					mesh = Mesh(meshes, meshes.size)
				}
				if (this.recolorSrc != null) {
					modelFunctionMask = functionMask or 0x4000
				}
				if (this.retextureSrc != null) {
					modelFunctionMask = modelFunctionMask or 0x8000
				}
				if (this.tintOpacity.toInt() != 0) {
					modelFunctionMask = modelFunctionMask or 0x80000
				}
				val newModel: Model = toolkit!!.createModel(mesh, modelFunctionMask, this.npcTypeList!!.featureMask, 64, 768)!!
				model = newModel
				if (this.recolorSrc != null) {
					index = 0
					while (index < this.recolorSrc!!.size) {
						if (this.recolorDPalette == null || index >= this.recolorDPalette!!.size) {
							newModel.ia(this.recolorSrc!![index], this.recolorDst!![index])
						} else {
							newModel.ia(this.recolorSrc!![index], NPCType.npcRecolorPalette[this.recolorDPalette!![index].toInt() and 0xFF])
						}
						index++
					}
				}
				if (this.retextureSrc != null) {
					index = 0
					while (index < this.retextureSrc!!.size) {
						newModel.aa(this.retextureSrc!![index], this.retextureDst!![index])
						index++
					}
				}
				if (this.tintOpacity.toInt() != 0) {
					newModel.adjustColours(this.tintHue.toInt(), this.tintSaturation.toInt(), this.tintLightness.toInt(), this.tintOpacity.toInt() and 0xFF)
				}
				newModel.s(functionMask)
				@Pc(377) val referenceCache2: LRUCache = this.npcTypeList!!.headModels
				synchronized(this.npcTypeList!!.headModels) {
					this.npcTypeList!!.headModels.put(newModel, ((toolkit.index shl 16) or this.id).toLong())
				}
			}
			if (seqType != null && currentFrameId != -1) {
				model = seqType.animateModel(model, nextFrameId, currentFrameDuration, functionMask, currentFrameId)
			}
			model!!.s(2048)
			return model
		}
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(III)I")
	fun getParam(@OriginalArg(0) defaultInt: Int, @OriginalArg(1) paramKey: Int): Int {
		if (this.parameters == null) {
			return defaultInt
		} else {
			@Pc(25) val intNode: IntNode? = this.parameters!!.get(paramKey.toLong()) as IntNode?
			return if (intNode == null) defaultInt else intNode.value
		}
	}

	companion object {
		@OriginalMember(owner = "client!lk", name = "A", descriptor = "[S")
		@JvmField
		var npcRecolorPalette: ShortArray = ShortArray(256)

		@OriginalMember(owner = "client!dm", name = "Y", descriptor = "[Lclient!gw;")
		@JvmField
		val aClass2_Sub6_Sub9Array1: Array<FrameSet?> = arrayOfNulls<FrameSet>(14)

		@OriginalMember(owner = "client!gw", name = "z", descriptor = "[Lclient!gw;")
		@JvmField
		val aClass2_Sub6_Sub9Array2: Array<FrameSet?> = arrayOfNulls<FrameSet>(14)

		@OriginalMember(owner = "client!qe", name = "j", descriptor = "[I")
		@JvmField
		val ANIM_NEXT_FRAME_IDS: IntArray = IntArray(14)

		@OriginalMember(owner = "client!kd", name = "g", descriptor = "[I")
		@JvmField
		val ANIM_FRAME_ELAPSED: IntArray = IntArray(14)

		@OriginalMember(owner = "client!ts", name = "z", descriptor = "[I")
		@JvmField
		val ANIM_FRAME_LENGTHS: IntArray = IntArray(14)

		@OriginalMember(owner = "client!pe", name = "k", descriptor = "[Lclient!hg;")
		@JvmField
		val aClass130Array2: Array<SeqType?> = arrayOfNulls<SeqType>(14)

		@OriginalMember(owner = "client!bc", name = "g", descriptor = "[I")
		@JvmField
		val ANIM_FRAME_IDS: IntArray = IntArray(14)
	}
}
