package com.jagex.game.runetek5.config.quest

import com.jagex.game.runetek5.cache.node.IntNode
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.node.StringNode
import com.jagex.game.runetek5.buffer.Packet
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField

@OriginalClass("client!cq")
class QuestType {

	@OriginalMember(owner = "client!cq", name = "b", descriptor = "[[I")
	private var varbitValues: Array<IntArray?>? = null

	@OriginalMember(owner = "client!cq", name = "c", descriptor = "[[I")
	private var varpValues: Array<IntArray?>? = null

	@OriginalMember(owner = "client!cq", name = "d", descriptor = "[I")
	private var minVarbitValue: IntArray? = null

	@OriginalMember(owner = "client!cq", name = "g", descriptor = "[I")
	private var minVarpValue: IntArray? = null

	@OriginalMember(owner = "client!cq", name = "h", descriptor = "[[I")
	private var levelRequirements: Array<IntArray?>? = null

	@OriginalMember(owner = "client!cq", name = "i", descriptor = "[I")
	private var maxVarbitValue: IntArray? = null

	@OriginalMember(owner = "client!cq", name = "j", descriptor = "[I")
	private var questPrerequisiteIds: IntArray? = null

	@OriginalMember(owner = "client!cq", name = "l", descriptor = "Lclient!eq;")
	private var params: IterableHashTable? = null

	@OriginalMember(owner = "client!cq", name = "m", descriptor = "[I")
	private var maxVarpValue: IntArray? = null

	@OriginalMember(owner = "client!cq", name = "n", descriptor = "[I")
	private var unusedOpcode10: IntArray? = null

	@OriginalMember(owner = "client!cq", name = "o", descriptor = "[I")
	private var varbitRequirements: IntArray? = null

	@OriginalMember(owner = "client!cq", name = "p", descriptor = "Ljava/lang/String;")
	private var sortName: String? = null

	@OriginalMember(owner = "client!cq", name = "q", descriptor = "[Ljava/lang/String;")
	private var varpRequirementNames: Array<String?>? = null

	@OriginalMember(owner = "client!cq", name = "r", descriptor = "Ljava/lang/String;")
	private var name: String? = null

	@OriginalMember(owner = "client!cq", name = "t", descriptor = "[I")
	private var varpRequirements: IntArray? = null

	@OriginalMember(owner = "client!cq", name = "v", descriptor = "[Ljava/lang/String;")
	private var varbitRequirementNames: Array<String?>? = null

	@OriginalMember(owner = "client!cq", name = "f", descriptor = "I")
	@JvmField
	var graphicId: Int = -1

	@OriginalMember(owner = "client!cq", name = "a", descriptor = "(B)V")
	fun postDecode() {
		if (this.sortName == null) {
			this.sortName = this.name
		}
	}

	@OriginalMember(owner = "client!cq", name = "a", descriptor = "(Lclient!cea;IB)V")
	private fun decode(@OriginalArg(0) packet: Packet, @OriginalArg(1) opcode: Int) {
		if (opcode == 1) {
			this.name = packet.readJagString()
		} else if (opcode == 2) {
			this.sortName = packet.readJagString()
		} else {
			@Pc(38) var count: Int
			@Pc(45) val local45: Int
			if (opcode == 3) {
				count = packet.readUByte()
				this.varpValues = Array<IntArray?>(count) { IntArray(3) }
				var varpValueIndex: Int = 0
				while (varpValueIndex < count) {
					this.varpValues!![varpValueIndex]!![0] = packet.readUShort()
					this.varpValues!![varpValueIndex]!![1] = packet.readInt()
					this.varpValues!![varpValueIndex]!![2] = packet.readInt()
					varpValueIndex++
				}
			} else if (opcode == 4) {
				count = packet.readUByte()
				this.varbitValues = Array<IntArray?>(count) { IntArray(3) }
				var varbitValueIndex: Int = 0
				while (varbitValueIndex < count) {
					this.varbitValues!![varbitValueIndex]!![0] = packet.readUShort()
					this.varbitValues!![varbitValueIndex]!![1] = packet.readInt()
					this.varbitValues!![varbitValueIndex]!![2] = packet.readInt()
					varbitValueIndex++
				}
			} else if (opcode == 5) {
				packet.readUShort()
			} else if (opcode == 6) {
				packet.readUByte()
				return
			} else if (opcode == 7) {
				packet.readUByte()
				return
			} else if (opcode != 8) {
				if (opcode == 9) {
					packet.readUByte()
					return
				}
				if (opcode == 10) {
					count = packet.readUByte()
					this.unusedOpcode10 = IntArray(count)
					var unusedIndex: Int = 0
					while (unusedIndex < count) {
						this.unusedOpcode10!![unusedIndex] = packet.readInt()
						unusedIndex++
					}
					return
				}
				if (opcode == 12) {
					packet.readInt()
					return
				}
				if (opcode != 13) {
					if (opcode == 14) {
						count = packet.readUByte()
						this.levelRequirements = Array<IntArray?>(count) { IntArray(2) }
						var levelIndex: Int = 0
						while (levelIndex < count) {
							this.levelRequirements!![levelIndex]!![0] = packet.readUByte()
							this.levelRequirements!![levelIndex]!![1] = packet.readUByte()
							levelIndex++
						}
					} else if (opcode == 15) {
						packet.readUShort()
						return
					} else if (opcode == 17) {
						this.graphicId = packet.readUShort()
						return
					} else if (opcode == 18) {
						count = packet.readUByte()
						this.varpRequirements = IntArray(count)
						this.varpRequirementNames = arrayOfNulls<String>(count)
						this.maxVarpValue = IntArray(count)
						this.minVarpValue = IntArray(count)
						var varpRequirementIndex: Int = 0
						while (varpRequirementIndex < count) {
							this.varpRequirements!![varpRequirementIndex] = packet.readInt()
							this.minVarpValue!![varpRequirementIndex] = packet.readInt()
							this.maxVarpValue!![varpRequirementIndex] = packet.readInt()
							this.varpRequirementNames!![varpRequirementIndex] = packet.readString()
							varpRequirementIndex++
						}
						return
					} else if (opcode == 19) {
						count = packet.readUByte()
						this.varbitRequirementNames = arrayOfNulls<String>(count)
						this.maxVarbitValue = IntArray(count)
						this.varbitRequirements = IntArray(count)
						this.minVarbitValue = IntArray(count)
						var varbitRequirementIndex: Int = 0
						while (varbitRequirementIndex < count) {
							this.varbitRequirements!![varbitRequirementIndex] = packet.readInt()
							this.minVarbitValue!![varbitRequirementIndex] = packet.readInt()
							this.maxVarbitValue!![varbitRequirementIndex] = packet.readInt()
							this.varbitRequirementNames!![varbitRequirementIndex] = packet.readString()
							varbitRequirementIndex++
						}
						return
					} else if (opcode == 249) {
						count = packet.readUByte()
						if (this.params == null) {
							val bucketCount: Int = Utils.nextPowerOfTwo(count)
							this.params = IterableHashTable(bucketCount)
						}
						var paramIndex: Int = 0
						while (paramIndex < count) {
							@Pc(351) val isString: Boolean = packet.readUByte() == 1
							@Pc(355) val key: Int = packet.readUMedium()
							@Pc(364) val node: Node
							if (isString) {
								node = StringNode(packet.readString())
							} else {
								node = IntNode(packet.readInt())
							}
							this.params!!.put(key.toLong(), node)
							paramIndex++
						}
						return
					}
					return
				}
				count = packet.readUByte()
				this.questPrerequisiteIds = IntArray(count)
				var prerequisiteIndex: Int = 0
				while (prerequisiteIndex < count) {
					this.questPrerequisiteIds!![prerequisiteIndex] = packet.readUShort()
					prerequisiteIndex++
				}
				return
			}
		}
	}

	@OriginalMember(owner = "client!cq", name = "a", descriptor = "(ILclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet) {
		while (true) {
			@Pc(17) val opcode: Int = packet.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}
}
