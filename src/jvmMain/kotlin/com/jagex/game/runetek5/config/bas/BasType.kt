package com.jagex.game.runetek5.config.bas

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!sg")
class BasType {

	@OriginalMember(owner = "client!sg", name = "f", descriptor = "[I")
	@JvmField
	var objVisibility: IntArray? = null

	@OriginalMember(owner = "client!sg", name = "g", descriptor = "[I")
	@JvmField
	var turnAngleAdjustment: IntArray? = null

	@OriginalMember(owner = "client!sg", name = "j", descriptor = "[[I")
	@JvmField
	var projectionOffset: Array<IntArray?>? = null

	@OriginalMember(owner = "client!sg", name = "y", descriptor = "[Lclient!bca;")
	private var transformMatrices: Array<Matrix?>? = null

	@OriginalMember(owner = "client!sg", name = "N", descriptor = "[[I")
	@JvmField
	var equipmentTransforms: Array<IntArray?>? = null

	@OriginalMember(owner = "client!sg", name = "h", descriptor = "I")
	@JvmField
	var teleTurnCcwSequence: Int = -1

	@OriginalMember(owner = "client!sg", name = "a", descriptor = "I")
	@JvmField
	var rollAcceleration: Int = 0

	@OriginalMember(owner = "client!sg", name = "k", descriptor = "I")
	@JvmField
	var runTurnCwSequence: Int = -1

	@OriginalMember(owner = "client!sg", name = "o", descriptor = "[I")
	@JvmField
	var randomStandSequences: IntArray? = null

	@OriginalMember(owner = "client!sg", name = "d", descriptor = "I")
	@JvmField
	var pitchMaxVelocity: Int = 0

	@OriginalMember(owner = "client!sg", name = "w", descriptor = "I")
	@JvmField
	var secondaryBarSpriteGroup: Int = -1

	@OriginalMember(owner = "client!sg", name = "r", descriptor = "I")
	@JvmField
	var standSequenceId: Int = -1

	@OriginalMember(owner = "client!sg", name = "i", descriptor = "I")
	@JvmField
	var healthBarSpriteGroup: Int = -1

	@OriginalMember(owner = "client!sg", name = "t", descriptor = "I")
	@JvmField
	var runDir3: Int = -1

	@OriginalMember(owner = "client!sg", name = "p", descriptor = "I")
	@JvmField
	var standTurnCcwSequence: Int = -1

	@OriginalMember(owner = "client!sg", name = "v", descriptor = "I")
	@JvmField
	var standTurnCwSequence: Int = -1

	@OriginalMember(owner = "client!sg", name = "F", descriptor = "I")
	@JvmField
	var modelWidth: Int = 0

	@OriginalMember(owner = "client!sg", name = "l", descriptor = "I")
	@JvmField
	var hillRotateX: Int = 0

	@OriginalMember(owner = "client!sg", name = "q", descriptor = "I")
	@JvmField
	var yawMaxVelocity: Int = 0

	@OriginalMember(owner = "client!sg", name = "n", descriptor = "I")
	@JvmField
	var runDir2: Int = -1

	@OriginalMember(owner = "client!sg", name = "C", descriptor = "[I")
	@JvmField
	var randomStandSequenceChances: IntArray? = null

	@OriginalMember(owner = "client!sg", name = "s", descriptor = "I")
	@JvmField
	var runningSequence: Int = -1

	@OriginalMember(owner = "client!sg", name = "J", descriptor = "I")
	@JvmField
	var rollMaxVelocity: Int = 0

	@OriginalMember(owner = "client!sg", name = "A", descriptor = "I")
	@JvmField
	var teleTurnCwSequence: Int = -1

	@OriginalMember(owner = "client!sg", name = "L", descriptor = "I")
	@JvmField
	var walkDir2: Int = -1

	@OriginalMember(owner = "client!sg", name = "z", descriptor = "I")
	@JvmField
	var pitchTargetAngle: Int = 0

	@OriginalMember(owner = "client!sg", name = "B", descriptor = "I")
	@JvmField
	var iconHeightOverride: Int = -1

	@OriginalMember(owner = "client!sg", name = "P", descriptor = "I")
	@JvmField
	var modelLength: Int = 0

	@OriginalMember(owner = "client!sg", name = "M", descriptor = "I")
	@JvmField
	var walkDir3: Int = -1

	@OriginalMember(owner = "client!sg", name = "T", descriptor = "I")
	@JvmField
	var unused: Int = -1

	@OriginalMember(owner = "client!sg", name = "R", descriptor = "I")
	@JvmField
	var hillRotateZ: Int = 0

	@OriginalMember(owner = "client!sg", name = "e", descriptor = "I")
	@JvmField
	var runTurnCcwSequence: Int = -1

	@OriginalMember(owner = "client!sg", name = "D", descriptor = "I")
	private var randomStandChanceTotal: Int = 0

	@OriginalMember(owner = "client!sg", name = "m", descriptor = "Z")
	@JvmField
	var rendersShadow: Boolean = true

	@OriginalMember(owner = "client!sg", name = "Y", descriptor = "I")
	@JvmField
	var rollTargetAngle: Int = 0

	@OriginalMember(owner = "client!sg", name = "O", descriptor = "I")
	@JvmField
	var teleportSequence: Int = -1

	@OriginalMember(owner = "client!sg", name = "H", descriptor = "I")
	@JvmField
	var walkTurnCcwSequence: Int = -1

	@OriginalMember(owner = "client!sg", name = "Q", descriptor = "I")
	private val toolkitIndex: Int = -1

	@OriginalMember(owner = "client!sg", name = "Z", descriptor = "I")
	@JvmField
	var walkSequence: Int = -1

	@OriginalMember(owner = "client!sg", name = "X", descriptor = "I")
	@JvmField
	var runDir1: Int = -1

	@OriginalMember(owner = "client!sg", name = "W", descriptor = "I")
	@JvmField
	var yawAcceleration: Int = 0

	@OriginalMember(owner = "client!sg", name = "V", descriptor = "I")
	@JvmField
	var teleDir1: Int = -1

	@OriginalMember(owner = "client!sg", name = "x", descriptor = "I")
	@JvmField
	var teleDir3: Int = -1

	@OriginalMember(owner = "client!sg", name = "cb", descriptor = "I")
	@JvmField
	var walkDir1: Int = -1

	@OriginalMember(owner = "client!sg", name = "bb", descriptor = "I")
	@JvmField
	var walkTurnCwSequence: Int = -1

	@OriginalMember(owner = "client!sg", name = "K", descriptor = "I")
	@JvmField
	var pitchAcceleration: Int = 0

	@OriginalMember(owner = "client!sg", name = "gb", descriptor = "I")
	@JvmField
	var teleDir2: Int = -1

	@OriginalMember(owner = "client!sg", name = "a", descriptor = "(Lclient!ha;I)[Lclient!bca;")
	fun getSubmeshMatrices(@OriginalArg(0) toolkit: Toolkit?): Array<Matrix?>? {
		if (this.transformMatrices != null && this.toolkitIndex == toolkit!!.index) {
			return this.transformMatrices
		} else if (this.equipmentTransforms == null) {
			return null
		} else {
			this.transformMatrices = arrayOfNulls<Matrix>(this.equipmentTransforms!!.size)
			@Pc(33) var i: Int = 0
			while (i < this.equipmentTransforms!!.size) {
				@Pc(37) var translateX: Int = 0
				@Pc(39) var translateY: Int = 0
				@Pc(41) var translateZ: Int = 0
				@Pc(43) var rotateX: Int = 0
				@Pc(45) var rotateY: Int = 0
				@Pc(47) var rotateZ: Int = 0
				if (this.equipmentTransforms!![i] != null) {
					translateY = this.equipmentTransforms!![i]!![1]
					translateZ = this.equipmentTransforms!![i]!![2]
					rotateX = this.equipmentTransforms!![i]!![3] shl 3
					translateX = this.equipmentTransforms!![i]!![0]
					rotateY = this.equipmentTransforms!![i]!![4] shl 3
					rotateZ = this.equipmentTransforms!![i]!![5] shl 3
				}
				if (translateX != 0 || translateY != 0 || translateZ != 0 || rotateX != 0 || rotateY != 0 || rotateZ != 0) {
					@Pc(123) val matrix: Matrix? = toolkit!!.createMatrix()
					this.transformMatrices!![i] = matrix
					if (rotateZ != 0) {
						matrix!!.rotateAxisZ(rotateZ)
					}
					if (rotateX != 0) {
						matrix!!.rotateAxisX(rotateX)
					}
					if (rotateY != 0) {
						matrix!!.rotateAxisY(rotateY)
					}
					matrix!!.translate(translateX, translateY, translateZ)
				}
				i++
			}
			return this.transformMatrices
		}
	}

	@OriginalMember(owner = "client!sg", name = "a", descriptor = "(Lclient!cea;I)V")
	fun decode(@OriginalArg(0) packet: Packet?) {
		while (true) {
			@Pc(5) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}

	@OriginalMember(owner = "client!sg", name = "b", descriptor = "(B)I")
	fun ready(): Int {
		if (this.standSequenceId != -1) {
			return this.standSequenceId
		} else if (this.randomStandSequences == null) {
			return -1
		} else {
			@Pc(22) var weight: Int = (this.randomStandChanceTotal.toDouble() * Math.random()).toInt()
			@Pc(24) var i: Int
			i = 0
			while (weight >= this.randomStandSequenceChances!![i]) {
				weight -= this.randomStandSequenceChances!![i]
				i++
			}
			return this.randomStandSequences!![i]
		}
	}

	@OriginalMember(owner = "client!sg", name = "a", descriptor = "(Lclient!cea;II)V")
	private fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(2) opcode: Int) {
		if (opcode == 1) {
			this.standSequenceId = packet!!.readUShort()
			this.walkSequence = packet.readUShort()
			if (this.walkSequence == 65535) {
				this.walkSequence = -1
			}
			if (this.standSequenceId == 65535) {
				this.standSequenceId = -1
			}
		} else if (opcode == 2) {
			this.teleportSequence = packet!!.readUShort()
		} else if (opcode == 3) {
			this.teleDir3 = packet!!.readUShort()
		} else if (opcode == 4) {
			this.teleDir2 = packet!!.readUShort()
		} else if (opcode == 5) {
			this.teleDir1 = packet!!.readUShort()
		} else if (opcode == 6) {
			this.runningSequence = packet!!.readUShort()
		} else if (opcode == 7) {
			this.runDir3 = packet!!.readUShort()
		} else if (opcode == 8) {
			this.runDir2 = packet!!.readUShort()
		} else if (opcode == 9) {
			this.runDir1 = packet!!.readUShort()
		} else if (opcode == 26) {
			this.modelWidth = (packet!!.readUByte() * 4).toShort().toInt()
			this.modelLength = (packet.readUByte() * 4).toShort().toInt()
		} else {
			@Pc(111) var local111: Int
			@Pc(119) var i: Int
			if (opcode == 27) {
				if (this.equipmentTransforms == null) {
					this.equipmentTransforms = arrayOfNulls<IntArray>(12)
				}
				val slot: Int = packet!!.readUByte()
				this.equipmentTransforms!![slot] = IntArray(6)
				i = 0
				while (i < 6) {
					this.equipmentTransforms!![slot]!![i] = packet.readShort()
					i++
				}
			} else if (opcode == 28) {
				this.objVisibility = IntArray(12)
				var slot: Int = 0
				while (slot < 12) {
					this.objVisibility!![slot] = packet!!.readUByte()
					if (this.objVisibility!![slot] == 255) {
						this.objVisibility!![slot] = -1
					}
					slot++
				}
			} else if (opcode == 29) {
				this.yawAcceleration = packet!!.readUByte()
			} else if (opcode == 30) {
				this.yawMaxVelocity = packet!!.readUShort()
			} else if (opcode == 31) {
				this.rollAcceleration = packet!!.readUByte()
			} else if (opcode == 32) {
				this.rollMaxVelocity = packet!!.readUShort()
			} else if (opcode == 33) {
				this.rollTargetAngle = packet!!.readShort()
			} else if (opcode == 34) {
				this.pitchAcceleration = packet!!.readUByte()
			} else if (opcode == 35) {
				this.pitchMaxVelocity = packet!!.readUShort()
			} else if (opcode == 36) {
				this.pitchTargetAngle = packet!!.readShort()
			} else if (opcode == 37) {
				this.unused = packet!!.readUByte()
			} else if (opcode == 38) {
				this.standTurnCcwSequence = packet!!.readUShort()
			} else if (opcode == 39) {
				this.standTurnCwSequence = packet!!.readUShort()
			} else if (opcode == 40) {
				this.walkDir3 = packet!!.readUShort()
			} else if (opcode == 41) {
				this.walkDir2 = packet!!.readUShort()
			} else if (opcode == 42) {
				this.walkDir1 = packet!!.readUShort()
			} else if (opcode == 43) {
				this.healthBarSpriteGroup = packet!!.readUShort()
			} else if (opcode == 44) {
				this.secondaryBarSpriteGroup = packet!!.readUShort()
			} else if (opcode == 45) {
				this.iconHeightOverride = packet!!.readUShort()
			} else if (opcode == 46) {
				this.teleTurnCcwSequence = packet!!.readUShort()
			} else if (opcode == 47) {
				this.teleTurnCwSequence = packet!!.readUShort()
			} else if (opcode == 48) {
				this.runTurnCcwSequence = packet!!.readUShort()
			} else if (opcode == 49) {
				this.runTurnCwSequence = packet!!.readUShort()
			} else if (opcode == 50) {
				this.walkTurnCcwSequence = packet!!.readUShort()
			} else if (opcode == 51) {
				this.walkTurnCwSequence = packet!!.readUShort()
			} else if (opcode == 52) {
				val sequenceCount: Int = packet!!.readUByte()
				this.randomStandSequences = IntArray(sequenceCount)
				this.randomStandSequenceChances = IntArray(sequenceCount)
				i = 0
				while (i < sequenceCount) {
					this.randomStandSequences!![i] = packet.readUShort()
					@Pc(346) val chance: Int = packet.readUByte()
					this.randomStandSequenceChances!![i] = chance
					this.randomStandChanceTotal += chance
					i++
				}
			} else if (opcode == 53) {
				this.rendersShadow = false
			} else if (opcode == 54) {
				this.hillRotateX = packet!!.readUByte() shl 6
				this.hillRotateZ = packet.readUByte() shl 6
			} else if (opcode == 55) {
				if (this.turnAngleAdjustment == null) {
					this.turnAngleAdjustment = IntArray(12)
				}
				val slot: Int = packet!!.readUByte()
				this.turnAngleAdjustment!![slot] = packet.readUShort()
			} else if (opcode == 56) {
				if (this.projectionOffset == null) {
					this.projectionOffset = arrayOfNulls<IntArray>(12)
				}
				val slot: Int = packet!!.readUByte()
				this.projectionOffset!![slot] = IntArray(3)
				i = 0
				while (i < 3) {
					this.projectionOffset!![slot]!![i] = packet.readShort()
					i++
				}
			}
		}
	}

	@OriginalMember(owner = "client!sg", name = "a", descriptor = "(II)Z")
	fun isReady(@OriginalArg(0) id: Int): Boolean {
		if (id == -1) {
			return false
		} else if (id == this.standSequenceId) {
			return true
		} else {
			if (this.randomStandSequences != null) {
				@Pc(34) var i: Int = 0
				while (i < this.randomStandSequences!!.size) {
					if (id == this.randomStandSequences!![i]) {
						return true
					}
					i++
				}
			}
			return false
		}
	}
}
