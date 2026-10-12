package com.jagex.game.runetek5.config.mec

import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.cache.node.IntNode
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.StringNode
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.vardomain.VarDomain
import com.jagex.game.runetek5.graphics.sprite.NativeSprite
import com.jagex.game.runetek5.graphics.SpriteDataProvider
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!ii")
class MECType {

	@OriginalMember(owner = "client!ii", name = "c", descriptor = "[B")
	@JvmField
	var colorPointers: ByteArray? = null

	@OriginalMember(owner = "client!ii", name = "d", descriptor = "Ljava/lang/String;")
	@JvmField
	var minimenuName: String? = null

	@OriginalMember(owner = "client!ii", name = "e", descriptor = "I")
	@JvmField
	var labelOffsetX: Int = 0

	@OriginalMember(owner = "client!ii", name = "g", descriptor = "I")
	@JvmField
	var labelOffsetY: Int = 0

	@OriginalMember(owner = "client!ii", name = "k", descriptor = "[I")
	@JvmField
	var colors: IntArray? = null

	@OriginalMember(owner = "client!ii", name = "s", descriptor = "I")
	@JvmField
	var defaultTextColor: Int = 0

	@OriginalMember(owner = "client!ii", name = "t", descriptor = "I")
	@JvmField
	var mectypeId: Int = 0

	@OriginalMember(owner = "client!ii", name = "u", descriptor = "I")
	@JvmField
	var lineColor: Int = 0

	@OriginalMember(owner = "client!ii", name = "x", descriptor = "I")
	private var primaryVariableMinValue: Int = 0

	@OriginalMember(owner = "client!ii", name = "y", descriptor = "I")
	private var secondaryVariableMaxValue: Int = 0

	@OriginalMember(owner = "client!ii", name = "z", descriptor = "I")
	private var secondaryVariableMinValue: Int = 0

	@OriginalMember(owner = "client!ii", name = "A", descriptor = "Lclient!eq;")
	private var parameters: IterableHashTable? = null

	@OriginalMember(owner = "client!ii", name = "D", descriptor = "[I")
	@JvmField
	var offsets: IntArray? = null

	@OriginalMember(owner = "client!ii", name = "E", descriptor = "I")
	private var primaryVariableMaxValue: Int = 0

	@OriginalMember(owner = "client!ii", name = "F", descriptor = "Lclient!on;")
	@JvmField
	var initialType: MECTypeList? = null

	@OriginalMember(owner = "client!ii", name = "J", descriptor = "Ljava/lang/String;")
	@JvmField
	var areaName: String? = null

	@OriginalMember(owner = "client!ii", name = "O", descriptor = "I")
	@JvmField
	var backgroundColor: Int = 0

	@OriginalMember(owner = "client!ii", name = "Q", descriptor = "I")
	@JvmField
	var outlineColor: Int = 0

	@OriginalMember(owner = "client!ii", name = "i", descriptor = "I")
	@JvmField
	var maxOffsetY: Int = Int.MIN_VALUE

	@OriginalMember(owner = "client!ii", name = "l", descriptor = "I")
	private var secondaryVarpbit: Int = -1

	@OriginalMember(owner = "client!ii", name = "n", descriptor = "I")
	@JvmField
	var dashLineLength: Int = -1

	@OriginalMember(owner = "client!ii", name = "m", descriptor = "Z")
	@JvmField
	var displayedOnWorldmap: Boolean = true

	@OriginalMember(owner = "client!ii", name = "q", descriptor = "I")
	@JvmField
	var defaultIconArchive: Int = -1

	@OriginalMember(owner = "client!ii", name = "v", descriptor = "I")
	private var secondaryVarp: Int = -1

	@OriginalMember(owner = "client!ii", name = "I", descriptor = "I")
	@JvmField
	var categoryId: Int = -1

	@OriginalMember(owner = "client!ii", name = "L", descriptor = "Z")
	@JvmField
	var hasRandomisedMinimapPosition: Boolean = true

	@OriginalMember(owner = "client!ii", name = "f", descriptor = "I")
	@JvmField
	var mouseOverTextColor: Int = -1

	@OriginalMember(owner = "client!ii", name = "o", descriptor = "I")
	@JvmField
	var baseTextzoom: Int = 0

	@OriginalMember(owner = "client!ii", name = "p", descriptor = "[Ljava/lang/String;")
	@JvmField
	val minimenuActions: Array<String?> = arrayOfNulls<String>(5)

	@OriginalMember(owner = "client!ii", name = "K", descriptor = "I")
	private var spriteId: Int = -1

	@OriginalMember(owner = "client!ii", name = "H", descriptor = "I")
	@JvmField
	var maxOffsetX: Int = Int.MIN_VALUE

	@OriginalMember(owner = "client!ii", name = "w", descriptor = "I")
	@JvmField
	var dashLineSpacing: Int = -1

	@OriginalMember(owner = "client!ii", name = "P", descriptor = "I")
	@JvmField
	var minOffsetX: Int = Int.MAX_VALUE

	@OriginalMember(owner = "client!ii", name = "C", descriptor = "I")
	private var primaryVarpbit: Int = -1

	@OriginalMember(owner = "client!ii", name = "S", descriptor = "I")
	private var primaryVarp: Int = -1

	@OriginalMember(owner = "client!ii", name = "V", descriptor = "Z")
	@JvmField
	var visible: Boolean = true

	@OriginalMember(owner = "client!ii", name = "h", descriptor = "Z")
	@JvmField
	var displayedOnMinimap: Boolean = false

	@OriginalMember(owner = "client!ii", name = "N", descriptor = "I")
	@JvmField
	var dashLineOffset: Int = -1

	@OriginalMember(owner = "client!ii", name = "T", descriptor = "I")
	@JvmField
	var minOffsetY: Int = Int.MAX_VALUE

	@OriginalMember(owner = "client!ii", name = "R", descriptor = "I")
	@JvmField
	var mouseOverIconArchive: Int = -1

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(I)V")
	fun postDecode() {
		if (this.offsets == null) {
			return
		}
		@Pc(16) var i: Int = 0
		while (i < this.offsets!!.size) {
			if (this.minOffsetX > this.offsets!![i]) {
				this.minOffsetX = this.offsets!![i]
			} else if (this.offsets!![i] > this.maxOffsetX) {
				this.maxOffsetX = this.offsets!![i]
			}
			if (this.offsets!![i + 1] < this.minOffsetY) {
				this.minOffsetY = this.offsets!![i + 1]
			} else if (this.offsets!![i + 1] > this.maxOffsetY) {
				this.maxOffsetY = this.offsets!![i + 1]
			}
			i += 2
		}
	}

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(Lclient!dd;I)Z")
	fun shouldDraw(@OriginalArg(0) varDomain: VarDomain?): Boolean {
		@Pc(22) val primaryValue: Int
		if (this.primaryVarp == -1) {
			if (this.primaryVarpbit == -1) {
				return true
			}
			primaryValue = varDomain!!.getVarBitValue(this.primaryVarpbit)
		} else {
			primaryValue = varDomain!!.getVarValueInt(this.primaryVarp)
		}
		if (primaryValue < this.primaryVariableMinValue || this.primaryVariableMaxValue < primaryValue) {
			return false
		}
		@Pc(67) val secondaryValue: Int
		if (this.secondaryVarp == -1) {
			if (this.secondaryVarpbit == -1) {
				return true
			}
			secondaryValue = varDomain.getVarBitValue(this.secondaryVarpbit)
		} else {
			secondaryValue = varDomain.getVarValueInt(this.secondaryVarp)
		}
		return this.secondaryVariableMinValue <= secondaryValue && this.secondaryVariableMaxValue >= secondaryValue
	}

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(ZLclient!ha;B)Lclient!mi;")
	fun getIcon(@OriginalArg(0) mouseOver: Boolean, @OriginalArg(1) toolkit: Toolkit): NativeSprite? {
		@Pc(11) val iconId: Int = if (mouseOver) this.mouseOverIconArchive else this.defaultIconArchive
		@Pc(23) val cacheKey: Int = iconId or (toolkit.index shl 29)
		@Pc(32) var sprite: NativeSprite? = this.initialType!!.spriteCache.get(cacheKey.toLong()) as NativeSprite?
		if (sprite != null) {
			return sprite
		} else if (this.initialType!!.spriteResource!!.isFileReady(iconId)) {
			@Pc(52) val indexedImage: SpriteType? = SpriteDataProvider.listSingle(this.initialType!!.spriteResource, iconId, 0)
			if (indexedImage != null) {
				sprite = toolkit.generateSprite(indexedImage, true)
				this.initialType!!.spriteCache.put(sprite, cacheKey.toLong())
			}
			return sprite
		} else {
			return null
		}
	}

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(Lclient!ha;Z)Lclient!mi;")
	fun getSprite(@OriginalArg(0) toolkit: Toolkit): NativeSprite? {
		@Pc(28) var sprite: NativeSprite? = this.initialType!!.spriteCache.get((this.spriteId or 0x20000 or (toolkit.index shl 29)).toLong()) as NativeSprite?
		if (sprite != null) {
			return sprite
		}
		this.initialType!!.spriteResource!!.isFileReady(this.spriteId)
		@Pc(48) val indexedImage: SpriteType? = SpriteDataProvider.listSingle(this.initialType!!.spriteResource, this.spriteId, 0)
		if (indexedImage != null) {
			sprite = toolkit.generateSprite(indexedImage, true)
			this.initialType!!.spriteCache.put(sprite, ((toolkit.index shl 29) or this.spriteId or 0x20000).toLong())
		}
		return sprite
	}

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(Ljava/lang/String;IB)Ljava/lang/String;")
	fun getStringParam(@OriginalArg(0) string: String?, @OriginalArg(1) paramKey: Int): String? {
		if (this.parameters == null) {
			return string
		} else {
			@Pc(24) val stringNode: StringNode? = this.parameters!!.get(paramKey.toLong()) as StringNode?
			return if (stringNode == null) string else stringNode.value
		}
	}

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(ILclient!cea;I)V")
	private fun decode(@OriginalArg(0) opcode: Int, @OriginalArg(1) packet: Packet) {
		if (opcode == 1) {
			this.defaultIconArchive = packet.readUShort()
		} else if (opcode == 2) {
			this.mouseOverIconArchive = packet.readUShort()
		} else if (opcode == 3) {
			this.areaName = packet.readString()
		} else if (opcode == 4) {
			this.defaultTextColor = packet.readUMedium()
		} else if (opcode == 5) {
			this.mouseOverTextColor = packet.readUMedium()
		} else if (opcode == 6) {
			this.baseTextzoom = packet.readUByte()
		} else {
			@Pc(70) val local70: Int
			if (opcode == 7) {
				val displayFlags: Int = packet.readUByte()
				if ((displayFlags and 0x1) == 0) {
					this.displayedOnWorldmap = false
				}
				if ((displayFlags and 0x2) == 2) {
					this.displayedOnMinimap = true
				}
			} else if (opcode == 8) {
				this.hasRandomisedMinimapPosition = packet.readUByte() == 1
			} else if (opcode == 9) {
				this.primaryVarpbit = packet.readUShort()
				if (this.primaryVarpbit == 65535) {
					this.primaryVarpbit = -1
				}
				this.primaryVarp = packet.readUShort()
				if (this.primaryVarp == 65535) {
					this.primaryVarp = -1
				}
				this.primaryVariableMinValue = packet.readInt()
				this.primaryVariableMaxValue = packet.readInt()
			} else if (opcode >= 10 && opcode <= 14) {
				this.minimenuActions[opcode - 10] = packet.readString()
			} else {
				@Pc(141) val local141: Int
				@Pc(171) val local171: Int
				if (opcode == 15) {
					val pointCount: Int = packet.readUByte()
					this.offsets = IntArray(pointCount * 2)
					var offsetIndex: Int = 0
					while (offsetIndex < pointCount * 2) {
						this.offsets!![offsetIndex] = packet.readShort()
						offsetIndex++
					}
					this.lineColor = packet.readInt()
					@Pc(165) val colorCount: Int = packet.readUByte()
					this.colors = IntArray(colorCount)
					var colorIndex: Int = 0
					while (colorIndex < this.colors!!.size) {
						this.colors!![colorIndex] = packet.readInt()
						colorIndex++
					}
					this.colorPointers = ByteArray(pointCount)
					@Pc(196) var i: Int = 0
					while (i < pointCount) {
						this.colorPointers!![i] = packet.readByte()
						i++
					}
				} else if (opcode == 16) {
					this.visible = false
				} else if (opcode == 17) {
					this.minimenuName = packet.readString()
				} else if (opcode == 18) {
					this.spriteId = packet.readUShort()
				} else if (opcode == 19) {
					this.categoryId = packet.readUShort()
				} else if (opcode == 20) {
					this.secondaryVarpbit = packet.readUShort()
					if (this.secondaryVarpbit == 65535) {
						this.secondaryVarpbit = -1
					}
					this.secondaryVarp = packet.readUShort()
					if (this.secondaryVarp == 65535) {
						this.secondaryVarp = -1
					}
					this.secondaryVariableMinValue = packet.readInt()
					this.secondaryVariableMaxValue = packet.readInt()
				} else if (opcode == 21) {
					this.outlineColor = packet.readInt()
				} else if (opcode == 22) {
					this.backgroundColor = packet.readInt()
				} else if (opcode == 23) {
					this.dashLineSpacing = packet.readUByte()
					this.dashLineLength = packet.readUByte()
					this.dashLineOffset = packet.readUByte()
				} else if (opcode == 24) {
					this.labelOffsetX = packet.readShort()
					this.labelOffsetY = packet.readShort()
				} else if (opcode == 249) {
					val paramCount: Int = packet.readUByte()
					if (this.parameters == null) {
						val bucketCount: Int = Utils.nextPowerOfTwo(paramCount)
						this.parameters = IterableHashTable(bucketCount)
					}
					var paramIndex: Int = 0
					while (paramIndex < paramCount) {
						@Pc(364) val isString: Boolean = packet.readUByte() == 1
						val paramKey: Int = packet.readUMedium()
						@Pc(377) val node: Node
						if (isString) {
							node = StringNode(packet.readString())
						} else {
							node = IntNode(packet.readInt())
						}
						this.parameters!!.put(paramKey.toLong(), node)
						paramIndex++
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(IIZ)I")
	fun getIntParam(@OriginalArg(0) defaultValue: Int, @OriginalArg(1) paramKey: Int): Int {
		if (this.parameters == null) {
			return defaultValue
		} else {
			@Pc(21) val intNode: IntNode? = this.parameters!!.get(paramKey.toLong()) as IntNode?
			return if (intNode == null) defaultValue else intNode.value
		}
	}

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(Lclient!cea;I)V")
	fun decode(@OriginalArg(0) packet: Packet) {
		while (true) {
			@Pc(15) val opcode: Int = packet.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(opcode, packet)
		}
	}
}
