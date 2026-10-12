package com.jagex.game.runetek5.config.identitykit

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.model.Mesh
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.idktype.IDKTypeList
import kotlin.jvm.JvmField
import withLock

@OriginalClass("client!ic")
class IdkType {

	@OriginalMember(owner = "client!ic", name = "d", descriptor = "[S")
	private var retexSrc: ShortArray? = null

	@OriginalMember(owner = "client!ic", name = "k", descriptor = "[I")
	private var bodyModels: IntArray? = null

	@OriginalMember(owner = "client!ic", name = "m", descriptor = "[S")
	private var recolorSrc: ShortArray? = null

	@OriginalMember(owner = "client!ic", name = "n", descriptor = "Lclient!oha;")
	@JvmField
	var identityTypeList: IDKTypeList? = null

	@OriginalMember(owner = "client!ic", name = "p", descriptor = "[S")
	private var retexDst: ShortArray? = null

	@OriginalMember(owner = "client!ic", name = "s", descriptor = "[S")
	private var recolorDst: ShortArray? = null

	@OriginalMember(owner = "client!ic", name = "c", descriptor = "[I")
	private val headModels: IntArray = intArrayOf(-1, -1, -1, -1, -1)

	@OriginalMember(owner = "client!ic", name = "a", descriptor = "(I)Lclient!bh;")
	fun getBodyMesh(): Mesh? {
		if (this.bodyModels == null) {
			return null
		}
		@Pc(15) val meshes2: Array<Mesh?> = arrayOfNulls<Mesh>(this.bodyModels!!.size)
		@Pc(19) val meshResource: Js5ResourceProvider? = this.identityTypeList!!.meshResource
		@Pc(23) var index: Int
		withLock(this.identityTypeList!!.meshResource!!) {
			index = 0
			while (true) {
				if (index >= this.bodyModels!!.size) {
					break
				}
				meshes2[index] = Mesh.fromResource(this.identityTypeList!!.meshResource, this.bodyModels!![index])
				index++
			}
		}
		@Pc(52) var i: Int = 0
		while (i < this.bodyModels!!.size) {
			if (meshes2[i]!!.version < 13) {
				meshes2[i]!!.upscale()
			}
			i++
		}
		@Pc(91) val mesh: Mesh?
		if (meshes2.size == 1) {
			mesh = meshes2[0]
		} else {
			mesh = Mesh(meshes2, meshes2.size)
		}
		if (mesh == null) {
			return null
		}
		if (this.recolorSrc != null) {
			index = 0
			while (index < this.recolorSrc!!.size) {
				mesh.recolor(this.recolorSrc!![index], this.recolorDst!![index])
				index++
			}
		}
		if (this.retexSrc != null) {
			index = 0
			while (index < this.retexSrc!!.size) {
				mesh.retexture(this.retexSrc!![index], this.retexDst!![index])
				index++
			}
		}
		return mesh
	}

	@OriginalMember(owner = "client!ic", name = "a", descriptor = "(B)Z")
	fun isHeadCached(): Boolean {
		@Pc(7) var cached: Boolean = true
		@Pc(11) val meshResource: Js5ResourceProvider? = this.identityTypeList!!.meshResource
		withLock(this.identityTypeList!!.meshResource!!) {
			@Pc(15) var i: Int = 0
			while (i < 5) {
				if (this.headModels[i] != -1 && !this.identityTypeList!!.meshResource!!.isFileReady(this.headModels[i], 0)) {
					cached = false
				}
				i++
			}
			return cached
		}
	}

	@OriginalMember(owner = "client!ic", name = "b", descriptor = "(B)Lclient!bh;")
	fun getHeadMesh(): Mesh? {
		@Pc(8) val meshes: Array<Mesh?> = arrayOfNulls<Mesh>(5)
		@Pc(10) var meshCount: Int = 0
		@Pc(14) val meshResource: Js5ResourceProvider? = this.identityTypeList!!.meshResource
		@Pc(18) var index: Int
		withLock(this.identityTypeList!!.meshResource!!) {
			index = 0
			while (true) {
				if (index >= 5) {
					break
				}
				if (this.headModels[index] != -1) {
					meshes[meshCount++] = Mesh.fromResource(this.identityTypeList!!.meshResource, this.headModels[index])
				}
				index++
			}
		}
		@Pc(55) var i: Int = 0
		while (i < 5) {
			if (meshes[i] != null && meshes[i]!!.version < 13) {
				meshes[i]!!.upscale()
			}
			i++
		}
		@Pc(90) val mesh: Mesh = Mesh(meshes, meshCount)
		if (this.recolorSrc != null) {
			index = 0
			while (index < this.recolorSrc!!.size) {
				mesh.recolor(this.recolorSrc!![index], this.recolorDst!![index])
				index++
			}
		}
		if (this.retexSrc != null) {
			index = 0
			while (index < this.retexSrc!!.size) {
				mesh.retexture(this.retexSrc!![index], this.retexDst!![index])
				index++
			}
		}
		return mesh
	}

	@OriginalMember(owner = "client!ic", name = "a", descriptor = "(ILclient!cea;B)V")
	private fun decode(@OriginalArg(0) opcode: Int, @OriginalArg(1) packet: Packet?) {
		if (opcode == 1) {
			packet!!.readUByte()
			return
		}
		@Pc(15) var count: Int
		@Pc(21) var index: Int
		if (opcode == 2) {
			count = packet!!.readUByte()
			this.bodyModels = IntArray(count)
			index = 0
			while (index < count) {
				this.bodyModels!![index] = packet.readUShort()
				index++
			}
		} else if (opcode != 3) {
			if (opcode == 40) {
				count = packet!!.readUByte()
				this.recolorDst = ShortArray(count)
				this.recolorSrc = ShortArray(count)
				index = 0
				while (index < count) {
					this.recolorSrc!![index] = packet.readUShort().toShort()
					this.recolorDst!![index] = packet.readUShort().toShort()
					index++
				}
			} else if (opcode == 41) {
				count = packet!!.readUByte()
				this.retexSrc = ShortArray(count)
				this.retexDst = ShortArray(count)
				index = 0
				while (index < count) {
					this.retexSrc!![index] = packet.readUShort().toShort()
					this.retexDst!![index] = packet.readUShort().toShort()
					index++
				}
			} else if (opcode >= 60 && opcode < 70) {
				this.headModels[opcode - 60] = packet!!.readUShort()
			}
		}
	}

	@OriginalMember(owner = "client!ic", name = "a", descriptor = "(ILclient!cea;)V")
	fun decode(@OriginalArg(1) packet: Packet?) {
		while (true) {
			@Pc(9) val opcode: Int = packet!!.readUByte()
			if (opcode == 0) {
				return
			}
			this.decode(opcode, packet)
		}
	}

	@OriginalMember(owner = "client!ic", name = "c", descriptor = "(B)Z")
	fun isBodyCached(): Boolean {
		if (this.bodyModels == null) {
			return true
		}
		@Pc(19) var cached: Boolean = true
		@Pc(23) val meshResource: Js5ResourceProvider? = this.identityTypeList!!.meshResource
		withLock(this.identityTypeList!!.meshResource!!) {
			@Pc(27) var i: Int = 0
			while (i < this.bodyModels!!.size) {
				if (!this.identityTypeList!!.meshResource!!.isFileReady(this.bodyModels!![i], 0)) {
					cached = false
				}
				i++
			}
			return cached
		}
	}
}
