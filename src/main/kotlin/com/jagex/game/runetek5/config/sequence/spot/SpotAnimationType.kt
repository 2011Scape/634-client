package com.jagex.game.runetek5.config.sequence.spot

import com.jagex.game.runetek5.config.sequence.SeqType
import com.jagex.game.runetek5.config.sequence.SeqTypeList
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.graphics.model.Model
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!fk")
class SpotAnimationType {

	@OriginalMember(owner = "client!fk", name = "e", descriptor = "[S")
	private var recolorSrc: ShortArray? = null

	@OriginalMember(owner = "client!fk", name = "n", descriptor = "[S")
	private var retextureSrc: ShortArray? = null

	@OriginalMember(owner = "client!fk", name = "p", descriptor = "I")
	@JvmField
	var currentId: Int = 0

	@OriginalMember(owner = "client!fk", name = "q", descriptor = "[S")
	private var recolorDst: ShortArray? = null

	@OriginalMember(owner = "client!fk", name = "s", descriptor = "[S")
	private var retextureDst: ShortArray? = null

	@OriginalMember(owner = "client!fk", name = "u", descriptor = "I")
	private var modelId: Int = 0

	@OriginalMember(owner = "client!fk", name = "B", descriptor = "Lclient!cn;")
	@JvmField
	var gfxTypeList: SpotAnimationTypeList? = null

	@OriginalMember(owner = "client!fk", name = "c", descriptor = "B")
	@JvmField
	var contourType: Byte = 0

	@OriginalMember(owner = "client!fk", name = "m", descriptor = "I")
	private var scaleXZ: Int = 128

	@OriginalMember(owner = "client!fk", name = "b", descriptor = "Z")
	@JvmField
	var replay: Boolean = false

	@OriginalMember(owner = "client!fk", name = "w", descriptor = "I")
	private var rotation: Int = 0

	@OriginalMember(owner = "client!fk", name = "r", descriptor = "I")
	@JvmField
	var sequenceId: Int = -1

	@OriginalMember(owner = "client!fk", name = "v", descriptor = "I")
	private var contourModifier: Int = -1

	@OriginalMember(owner = "client!fk", name = "g", descriptor = "I")
	private var scaleY: Int = 128

	@OriginalMember(owner = "client!fk", name = "d", descriptor = "I")
	private var brightness: Int = 0

	@OriginalMember(owner = "client!fk", name = "A", descriptor = "I")
	private var contrast: Int = 0

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(IILclient!ha;IILclient!lfa;I)Lclient!ka;")
	fun getSecondaryModel(@OriginalArg(0) frameCycle: Int, @OriginalArg(1) nextFrameId: Int, @OriginalArg(2) toolkit: Toolkit?, @OriginalArg(3) functionMask: Int, @OriginalArg(5) seqTypeList: SeqTypeList?, @OriginalArg(6) currentFrameId: Int): Model? {
		return this.getAnimatedModel(toolkit, currentFrameId, functionMask, null, 0, seqTypeList, frameCycle, 5.toByte(), null, 0, false, 0, nextFrameId)
	}

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(ILclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet?) {
		while (true) {
			@Pc(13) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(packet, opcode)
		}
	}

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(ILclient!ha;IIILclient!lfa;B)Lclient!ka;")
	fun getModel(@OriginalArg(0) currentFrameId: Int, @OriginalArg(1) toolkit: Toolkit?, @OriginalArg(2) frameCycle: Int, @OriginalArg(3) functionMask: Int, @OriginalArg(4) nextFrameId: Int, @OriginalArg(5) seqTypeList: SeqTypeList?): Model? {
		return this.getAnimatedModel(toolkit, currentFrameId, functionMask, null, 0, seqTypeList, frameCycle, 2.toByte(), null, 0, false, 0, nextFrameId)
	}

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(ILclient!cea;I)V")
	private fun decode(@OriginalArg(1) packet: Packet?, @OriginalArg(2) opcode: Int) {
		if (opcode == 1) {
			this.modelId = packet!!.readUShort()
		} else if (opcode == 2) {
			this.sequenceId = packet!!.readUShort()
		} else if (opcode == 4) {
			this.scaleXZ = packet!!.readUShort()
		} else if (opcode == 5) {
			this.scaleY = packet!!.readUShort()
		} else if (opcode == 6) {
			this.rotation = packet!!.readUShort()
		} else if (opcode == 7) {
			this.brightness = packet!!.readUByte()
		} else if (opcode == 8) {
			this.contrast = packet!!.readUByte()
		} else if (opcode == 9) {
			this.contourModifier = 8224
			this.contourType = 3
		} else if (opcode == 10) {
			this.replay = true
		} else if (opcode == 11) {
			this.contourType = 1
		} else if (opcode == 12) {
			this.contourType = 4
		} else if (opcode == 13) {
			this.contourType = 5
		} else if (opcode == 14) {
			this.contourType = 2
			this.contourModifier = packet!!.readUByte() * 256
		} else if (opcode == 15) {
			this.contourType = 3
			this.contourModifier = packet!!.readUShort()
		} else if (opcode == 16) {
			this.contourType = 3
			this.contourModifier = packet!!.readInt()
		} else {
			@Pc(124) val count: Int
			@Pc(134) var i: Int
			if (opcode == 40) {
				count = packet!!.readUByte()
				this.recolorSrc = ShortArray(count)
				this.recolorDst = ShortArray(count)
				i = 0
				while (i < count) {
					this.recolorSrc!![i] = packet.readUShort().toShort()
					this.recolorDst!![i] = packet.readUShort().toShort()
					i++
				}
			} else if (opcode == 41) {
				count = packet!!.readUByte()
				this.retextureDst = ShortArray(count)
				this.retextureSrc = ShortArray(count)
				i = 0
				while (i < count) {
					this.retextureSrc!![i] = packet.readUShort().toShort()
					this.retextureDst!![i] = packet.readUShort().toShort()
					i++
				}
				return
			}
		}
	}

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(IIIIZLclient!ha;IIILclient!lfa;Lclient!s;ILclient!s;)Lclient!ka;")
	fun getContouredModel(@OriginalArg(0) frameCycle: Int, @OriginalArg(1) currentFrameId: Int, @OriginalArg(2) y: Int, @OriginalArg(3) functionMask: Int, @OriginalArg(5) toolkit: Toolkit?, @OriginalArg(6) nextFrameId: Int, @OriginalArg(7) x: Int, @OriginalArg(9) seqTypeList: SeqTypeList?, @OriginalArg(10) ground: Ground?, @OriginalArg(11) z: Int, @OriginalArg(12) ground2: Ground?): Model? {
		return this.getAnimatedModel(toolkit, currentFrameId, functionMask, ground, z, seqTypeList, frameCycle, 2.toByte(), ground2, y, true, x, nextFrameId)
	}

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(Lclient!ha;IILclient!s;ILclient!lfa;IBLclient!s;IZIBI)Lclient!ka;")
	private fun getAnimatedModel(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) currentFrameId: Int, @OriginalArg(2) initialFunctionMask: Int, @OriginalArg(3) ground: Ground?, @OriginalArg(4) z: Int, @OriginalArg(5) seqTypeList: SeqTypeList?, @OriginalArg(6) frameCycle: Int, @OriginalArg(7) copyType: Byte, @OriginalArg(8) ground2: Ground?, @OriginalArg(9) y: Int, @OriginalArg(10) allowContouring: Boolean, @OriginalArg(11) x: Int, @OriginalArg(13) nextFrameId: Int): Model? {
		@Pc(7) var functionMask: Int = initialFunctionMask
		@Pc(22) val seqType: SeqType? = if (this.sequenceId == -1 || currentFrameId == -1) null else seqTypeList!!.list(this.sequenceId)
		@Pc(31) val adjustToGround: Boolean = allowContouring and (this.contourType.toInt() != 0)
		if (seqType != null) {
			functionMask = initialFunctionMask or seqType.getFunctionMask(nextFrameId, currentFrameId, false)
		}
		if (adjustToGround) {
			functionMask = functionMask or (if (this.contourType.toInt() == 3) 7 else 2)
		}
		if (this.scaleY != 128) {
			functionMask = functionMask or 0x2
		}
		if (this.scaleXZ != 128 || this.rotation != 0) {
			functionMask = functionMask or 0x5
		}
		@Pc(86) val referenceCache: LRUCache = this.gfxTypeList!!.modelCache
		@Pc(106) var model: Model?
		synchronized(this.gfxTypeList!!.modelCache) {
			val modelCache: LRUCache = this.gfxTypeList!!.modelCache
			this.currentId = this.currentId or (toolkit!!.index shl 29)
			model = modelCache.get(this.currentId.toLong()) as Model?
		}
		if (model == null || toolkit!!.compareFunctionMasks(model!!.ua(), functionMask) != 0) {
			if (model != null) {
				functionMask = toolkit!!.combineFunctionMasks(functionMask, model!!.ua())
			}
			@Pc(133) var modelFunctionMask: Int = functionMask
			if (this.recolorSrc != null) {
				modelFunctionMask = functionMask or 0x4000
			}
			if (this.retextureSrc != null) {
				modelFunctionMask = modelFunctionMask or 0x8000
			}
			@Pc(156) val mesh: Mesh? = Mesh.fromResource(this.gfxTypeList!!.modelResource, this.modelId)
			if (mesh == null) {
				return null
			}
			if (mesh.version < 13) {
				mesh.upscale()
			}
			model = toolkit!!.createModel(mesh, modelFunctionMask, this.gfxTypeList!!.featureMask, this.brightness + 64, this.contrast - -850)
			@Pc(191) var i: Int
			if (this.recolorSrc != null) {
				i = 0
				while (i < this.recolorSrc!!.size) {
					model!!.ia(this.recolorSrc!![i], this.recolorDst!![i])
					i++
				}
			}
			if (this.retextureSrc != null) {
				i = 0
				while (i < this.retextureSrc!!.size) {
					model!!.aa(this.retextureSrc!![i], this.retextureDst!![i])
					i++
				}
			}
			model!!.s(functionMask)
			@Pc(247) val referenceCache2: LRUCache = this.gfxTypeList!!.modelCache
			synchronized(this.gfxTypeList!!.modelCache) {
				val modelCache: LRUCache = this.gfxTypeList!!.modelCache
				this.currentId = this.currentId or (toolkit.index shl 29)
				modelCache.put(model, this.currentId.toLong())
			}
		}
		@Pc(296) val model2: Model? = if (seqType == null) model!!.copy(copyType, functionMask, true) else seqType.animateRotatedModel(model, 0, nextFrameId, copyType, currentFrameId, frameCycle, functionMask)
		if (this.scaleXZ != 128 || this.scaleY != 128) {
			model2!!.O(this.scaleXZ, this.scaleY, this.scaleXZ)
		}
		if (this.rotation != 0) {
			if (this.rotation == 90) {
				model2!!.a(4096)
			}
			if (this.rotation == 180) {
				model2!!.a(8192)
			}
			if (this.rotation == 270) {
				model2!!.a(12288)
			}
		}
		if (adjustToGround) {
			model2!!.p(this.contourType.toInt(), this.contourModifier, ground2, ground, x, y, z)
		}
		model2!!.s(initialFunctionMask)
		return model2
	}
}
