package com.jagex.game.runetek5.graphics.light

import com.jagex.game.runetek5.cache.node.impl.ShortNode
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer
import com.jagex.game.runetek5.cache.node.PointLight
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.ArrayBufferGL
import com.jagex.game.runetek5.buffer.ElementsBufferGL
import com.jagex.game.runetek5.scene.ground.GlGround
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!qw")
class LightGL : Node {

	@OriginalMember(owner = "client!qw", name = "C", descriptor = "I")
	private var vertexCount: Int = 0

	@OriginalMember(owner = "client!qw", name = "q", descriptor = "Lclient!ed;")
	private val ground: GlGround?

	@OriginalMember(owner = "client!qw", name = "N", descriptor = "Lclient!qo;")
	private val toolkit: GlToolkit?

	@OriginalMember(owner = "client!qw", name = "y", descriptor = "Lclient!ab;")
	private val pointLight: PointLight?

	@OriginalMember(owner = "client!qw", name = "w", descriptor = "I")
	private val minTileX: Int

	@OriginalMember(owner = "client!qw", name = "E", descriptor = "I")
	private val maxTileX: Int

	@OriginalMember(owner = "client!qw", name = "K", descriptor = "I")
	private val minTileZ: Int

	@OriginalMember(owner = "client!qw", name = "G", descriptor = "I")
	private val maxTileZ: Int

	@OriginalMember(owner = "client!qw", name = "p", descriptor = "[[F")
	private var tileNormalsZ: Array<FloatArray?>? = null

	@OriginalMember(owner = "client!qw", name = "L", descriptor = "[[F")
	private var tileNormalsX: Array<FloatArray?>? = null

	@OriginalMember(owner = "client!qw", name = "J", descriptor = "[[F")
	private var tileNormalsY: Array<FloatArray?>? = null

	@OriginalMember(owner = "client!qw", name = "v", descriptor = "I")
	private var totalIndexCount: Int = 0

	@OriginalMember(owner = "client!qw", name = "x", descriptor = "Lclient!cea;")
	private var indexPacket: Packet? = null

	@OriginalMember(owner = "client!qw", name = "P", descriptor = "Lclient!gh;")
	private var bitBuffer: GlBitBuffer? = null

	@OriginalMember(owner = "client!qw", name = "t", descriptor = "Lclient!eq;")
	private var vertexHashTable: IterableHashTable? = null

	@OriginalMember(owner = "client!qw", name = "H", descriptor = "Lclient!ni;")
	private val indexBuffer: ElementsBufferGL?

	@OriginalMember(owner = "client!qw", name = "O", descriptor = "Lclient!hca;")
	private val vertexBuffer: ArrayBufferGL?

	@OriginalMember(owner = "client!qw", name = "o", descriptor = "Lclient!nh;")
	private val positionDrawBuffer: OpenGlArrayDrawBuffer?

	@OriginalMember(owner = "client!qw", name = "F", descriptor = "Lclient!nh;")
	private val colorDrawBuffer: OpenGlArrayDrawBuffer?

	@OriginalMember(owner = "client!qw", name = "<init>", descriptor = "(Lclient!qo;Lclient!ed;Lclient!ab;[I)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) glGround: GlGround?, @OriginalArg(2) pointLight: PointLight?, @OriginalArg(3) tileMask: IntArray?) : super() {
		this.ground = glGround
		this.toolkit = glToolkit
		this.pointLight = pointLight
		@Pc(23) val lightRadius: Int = this.pointLight!!.getRadius() - (glGround!!.tileUnits shr 1)
		this.minTileX = (this.pointLight.getX() - lightRadius) shr glGround.tileScale
		this.maxTileX = (this.pointLight.getX() + lightRadius) shr glGround.tileScale
		this.minTileZ = (this.pointLight.getZ() - lightRadius) shr glGround.tileScale
		this.maxTileZ = (this.pointLight.getZ() + lightRadius) shr glGround.tileScale
		@Pc(79) val tileRangeX: Int = this.maxTileX + 1 - this.minTileX
		@Pc(88) val tileRangeZ: Int = this.maxTileZ + 1 - this.minTileZ
		this.tileNormalsZ = Array<FloatArray?>(tileRangeX + 1) { FloatArray(tileRangeZ + 1) }
		this.tileNormalsX = Array<FloatArray?>(tileRangeX + 1) { FloatArray(tileRangeZ + 1) }
		this.tileNormalsY = Array<FloatArray?>(tileRangeX + 1) { FloatArray(tileRangeZ + 1) }
		@Pc(125) var local125: Int
		@Pc(139) var minTileZ: Int
		@Pc(147) var local147: Int
		@Pc(175) var local175: Int
		@Pc(191) var local191: Int
		@Pc(117) var i: Int = 0
		while (i <= tileRangeZ) {
			val tileZ: Int = i + this.minTileZ
			if (tileZ > 0 && tileZ < this.ground!!.length - 1) {
				minTileZ = 0
				while (minTileZ <= tileRangeX) {
					val tileX: Int = minTileZ + this.minTileX
					if (tileX > 0 && tileX < this.ground.width - 1) {
						val heightDiffX: Int = glGround.getHeight(tileZ, tileX + 1) - glGround.getHeight(tileZ, tileX - 1)
						val heightDiffZ: Int = glGround.getHeight(tileZ + 1, tileX) - glGround.getHeight(tileZ - 1, tileX)
						@Pc(207) val invMagnitude: Float = (1.0 / Math.sqrt((heightDiffX * heightDiffX + heightDiffZ * heightDiffZ + 65536).toDouble())).toFloat()
						this.tileNormalsX!![minTileZ]!![i] = heightDiffX.toFloat() * invMagnitude
						this.tileNormalsY!![minTileZ]!![i] = -256.0f * invMagnitude
						this.tileNormalsZ!![minTileZ]!![i] = invMagnitude * heightDiffZ.toFloat()
					}
					minTileZ++
				}
			}
			i++
		}
		var maskIndex: Int = 0
		@Pc(317) var minTileX: Int
		minTileZ = this.minTileZ
		while (minTileZ <= this.maxTileZ) {
			if (minTileZ >= 0 && minTileZ < glGround.length) {
				var tileX: Int = this.minTileX
				while (tileX <= this.maxTileX) {
					if (tileX >= 0 && glGround.width > tileX) {
						val tileMaskType: Int = tileMask!![maskIndex]
						@Pc(300) val tileColours: IntArray? = glGround.tileColours!![tileX][minTileZ]
						if (tileColours != null && tileMaskType != 0) {
							if (tileMaskType == 1) {
								minTileX = 0
								while (tileColours.size > minTileX) {
									if (tileColours[minTileX++] != -1 && tileColours[minTileX++] != -1 && tileColours[minTileX++] != -1) {
										this.totalIndexCount += 3
									}
								}
							} else {
								this.totalIndexCount += 3
							}
						}
					}
					maskIndex++
					tileX++
				}
			} else {
				maskIndex += this.maxTileX - this.minTileX
			}
			minTileZ++
		}
		if (this.totalIndexCount > 0) {
			this.indexPacket = Packet(this.totalIndexCount * 2)
			this.bitBuffer = GlBitBuffer(this.totalIndexCount * 16)
			this.vertexHashTable = IterableHashTable(Utils.nextPowerOfTwo(this.totalIndexCount))
			var localTileZ: Int = 0
			maskIndex = 0
			var tileZ: Int = this.minTileZ
			while (tileZ <= this.maxTileZ) {
				if (tileZ >= 0 && tileZ < glGround.length) {
					var localTileX: Int = 0
					minTileX = this.minTileX
					while (minTileX <= this.maxTileX) {
						if (minTileX >= 0 && glGround.width > minTileX) {
							@Pc(440) val maskType: Int = tileMask!![maskIndex]
							@Pc(447) val tileColours: IntArray? = glGround.tileColours!![minTileX][tileZ]
							if (tileColours != null && maskType != 0) {
								if (maskType == 1) {
									@Pc(605) val tileOffsetsX: IntArray? = glGround.tileOffsetsX!![minTileX][tileZ]
									@Pc(612) val tileOffsetsZ: IntArray? = glGround.tileOffsetsZ!![minTileX][tileZ]
									@Pc(614) var index: Int = 0
									label105@ while (true) {
										while (true) {
											if (index >= tileColours.size) {
												break@label105
											}
											if (tileColours[index] == -1 || tileColours[index + 1] == -1 || tileColours[index + 2] == -1) {
												index += 3
											} else {
												this.addVertex(tileOffsetsZ!![index], localTileX, localTileZ, minTileX, tileOffsetsX!![index], tileZ)
												index++
												this.addVertex(tileOffsetsZ[index], localTileX, localTileZ, minTileX, tileOffsetsX[index], tileZ)
												index++
												this.addVertex(tileOffsetsZ[index], localTileX, localTileZ, minTileX, tileOffsetsX[index], tileZ)
												index++
											}
										}
									}
								} else if (maskType == 3) {
									this.addVertex(0, localTileX, localTileZ, minTileX, 0, tileZ)
									this.addVertex(0, localTileX, localTileZ, minTileX, glGround.tileUnits, tileZ)
									this.addVertex(glGround.tileUnits, localTileX, localTileZ, minTileX, 0, tileZ)
								} else if (maskType == 2) {
									this.addVertex(0, localTileX, localTileZ, minTileX, glGround.tileUnits, tileZ)
									this.addVertex(glGround.tileUnits, localTileX, localTileZ, minTileX, glGround.tileUnits, tileZ)
									this.addVertex(0, localTileX, localTileZ, minTileX, 0, tileZ)
								} else if (maskType == 5) {
									this.addVertex(glGround.tileUnits, localTileX, localTileZ, minTileX, glGround.tileUnits, tileZ)
									this.addVertex(glGround.tileUnits, localTileX, localTileZ, minTileX, 0, tileZ)
									this.addVertex(0, localTileX, localTileZ, minTileX, glGround.tileUnits, tileZ)
								} else if (maskType == 4) {
									this.addVertex(glGround.tileUnits, localTileX, localTileZ, minTileX, 0, tileZ)
									this.addVertex(0, localTileX, localTileZ, minTileX, 0, tileZ)
									this.addVertex(glGround.tileUnits, localTileX, localTileZ, minTileX, glGround.tileUnits, tileZ)
								}
							}
						}
						localTileX++
						maskIndex++
						minTileX++
					}
				} else {
					maskIndex += this.maxTileX - this.minTileX
				}
				localTileZ++
				tileZ++
			}
			this.indexBuffer = this.toolkit!!.createElementsArrayBuffer(this.indexPacket!!.currentPosition, this.indexPacket!!.buffer, false)
			this.vertexBuffer = this.toolkit.createArrayBuffer(false, 16, this.bitBuffer!!.buffer, this.bitBuffer!!.currentPosition)
			this.positionDrawBuffer = OpenGlArrayDrawBuffer(this.vertexBuffer, 5126, 3, 0)
			this.colorDrawBuffer = OpenGlArrayDrawBuffer(this.vertexBuffer, 5121, 4, 12)
		} else {
			this.vertexBuffer = null
			this.indexBuffer = null
			this.positionDrawBuffer = null
			this.colorDrawBuffer = null
		}
		this.bitBuffer = null
		this.tileNormalsZ = null
		this.tileNormalsY = this.tileNormalsZ
		this.tileNormalsX = this.tileNormalsY
		this.indexPacket = null
		this.vertexHashTable = null
	}

	@OriginalMember(owner = "client!qw", name = "a", descriptor = "(IIIIIBI)V")
	private fun addVertex(@OriginalArg(0) tileUnits: Int, @OriginalArg(1) localTileX: Int, @OriginalArg(2) localTileZ: Int, @OriginalArg(3) tileX: Int, @OriginalArg(4) offsetX: Int, @OriginalArg(6) tileZ: Int) {
		@Pc(7) var hashKey: Long = -1L
		@Pc(16) val sceneX: Int = offsetX + (tileX shl this.ground!!.tileScale)
		@Pc(24) val sceneZ: Int = tileUnits + (tileZ shl this.ground.tileScale)
		@Pc(31) val sceneY: Int = this.ground.averageHeight(sceneX, sceneZ)
		if ((offsetX and 0x7F) == 0 || (tileUnits and 0x7F) == 0) {
			hashKey = ((sceneZ.toLong() shl 16) and (0xFFFFL shl 16)) or (sceneX.toLong() and 0xFFFFL)
			@Pc(62) val node: Node? = this.vertexHashTable!!.get(hashKey)
			if (node != null) {
				this.writeIndex((node as ShortNode).value)
				return
			}
		}
		@Pc(80) val vertexIndex: Short = (this.vertexCount++).toShort()
		if (hashKey != -1L) {
			this.vertexHashTable!!.put(hashKey, ShortNode(vertexIndex))
		}
		@Pc(138) val normalX: Float
		@Pc(129) val normalY: Float
		@Pc(120) val normalZ: Float
		@Pc(165) var local165: Float
		@Pc(173) var local173: Float
		@Pc(180) var local180: Float
		@Pc(187) var local187: Float
		@Pc(194) var local194: Float
		@Pc(203) var local203: Float
		@Pc(212) var local212: Float
		@Pc(270) var local270: Float
		if (offsetX == 0 && tileUnits == 0) {
			normalX = this.tileNormalsX!![localTileX]!![localTileZ]
			normalZ = this.tileNormalsZ!![localTileX]!![localTileZ]
			normalY = this.tileNormalsY!![localTileX]!![localTileZ]
		} else if (offsetX == this.ground.tileUnits && tileUnits == 0) {
			normalZ = this.tileNormalsZ!![localTileX + 1]!![localTileZ]
			normalY = this.tileNormalsY!![localTileX + 1]!![localTileZ]
			normalX = this.tileNormalsX!![localTileX + 1]!![localTileZ]
		} else if (offsetX == this.ground.tileUnits && this.ground.tileUnits == tileUnits) {
			normalX = this.tileNormalsX!![localTileX + 1]!![localTileZ + 1]
			normalZ = this.tileNormalsZ!![localTileX + 1]!![localTileZ + 1]
			normalY = this.tileNormalsY!![localTileX + 1]!![localTileZ + 1]
		} else if (offsetX == 0 && this.ground.tileUnits == tileUnits) {
			normalX = this.tileNormalsX!![localTileX]!![localTileZ + 1]
			normalZ = this.tileNormalsZ!![localTileX]!![localTileZ + 1]
			normalY = this.tileNormalsY!![localTileX]!![localTileZ + 1]
		} else {
			val fracX: Float = offsetX.toFloat() / this.ground.tileUnits.toFloat()
			val fracZ: Float = tileUnits.toFloat() / this.ground.tileUnits.toFloat()
			val normalX00: Float = this.tileNormalsX!![localTileX]!![localTileZ]
			val normalY00: Float = this.tileNormalsY!![localTileX]!![localTileZ]
			val normalZ00: Float = this.tileNormalsZ!![localTileX]!![localTileZ]
			val normalX10: Float = this.tileNormalsX!![localTileX + 1]!![localTileZ]
			val normalY10: Float = this.tileNormalsY!![localTileX + 1]!![localTileZ]
			@Pc(229) val rightNormalY: Float = normalY10 + (this.tileNormalsY!![localTileX + 1]!![localTileZ + 1] - normalY10) * fracX
			@Pc(246) val rightNormalX: Float = normalX10 + fracX * (this.tileNormalsX!![localTileX + 1]!![localTileZ + 1] - normalX10)
			@Pc(261) val leftNormalZ: Float = normalZ00 + (this.tileNormalsZ!![localTileX]!![localTileZ + 1] - normalZ00) * fracX
			val normalZ10: Float = this.tileNormalsZ!![localTileX + 1]!![localTileZ]
			@Pc(285) val leftNormalY: Float = normalY00 + fracX * (this.tileNormalsY!![localTileX]!![localTileZ + 1] - normalY00)
			@Pc(301) val leftNormalX: Float = normalX00 + fracX * (this.tileNormalsX!![localTileX]!![localTileZ + 1] - normalX00)
			normalX = leftNormalX + (rightNormalX - leftNormalX) * fracZ
			normalY = leftNormalY + fracZ * (rightNormalY - leftNormalY)
			@Pc(336) val rightNormalZ: Float = normalZ10 + fracX * (this.tileNormalsZ!![localTileX + 1]!![localTileZ + 1] - normalZ10)
			normalZ = leftNormalZ + fracZ * (rightNormalZ - leftNormalZ)
		}
		var dirX: Float = (this.pointLight!!.getX() - sceneX).toFloat()
		var dirY: Float = (this.pointLight.getY() - sceneY).toFloat()
		var dirZ: Float = (this.pointLight.getZ() - sceneZ).toFloat()
		val distance: Float = Math.sqrt((dirY * dirY + dirX * dirX + dirZ * dirZ).toDouble()).toFloat()
		val invDistance: Float = 1.0f / distance
		dirZ *= invDistance
		dirX *= invDistance
		dirY *= invDistance
		val distanceRatio: Float = distance / this.pointLight.getRadius().toFloat()
		var attenuation: Float = 1.0f - distanceRatio * distanceRatio
		if (attenuation < 0.0f) {
			attenuation = 0.0f
		}
		var diffuse: Float = dirZ * normalZ + dirY * normalY + normalX * dirX
		if (diffuse < 0.0f) {
			diffuse = 0.0f
		}
		@Pc(534) var lightFactor: Float = attenuation * 2.0f * diffuse
		if (lightFactor > 1.0f) {
			lightFactor = 1.0f
		}
		@Pc(553) val range: Int = this.pointLight.getColour()
		@Pc(563) var red: Int = (((range shr 16) and 0xFF).toFloat() * lightFactor).toInt()
		if (red > 255) {
			red = 255
		}
		@Pc(580) var green: Int = (((range shr 8) and 0xFF).toFloat() * lightFactor).toInt()
		if (green > 255) {
			green = 255
		}
		@Pc(595) var blue: Int = (lightFactor * (range and 0xFF).toFloat()).toInt()
		if (this.toolkit!!.bigEndianess) {
			this.bitBuffer!!.writeFloatBigEndian(sceneX.toFloat())
			this.bitBuffer!!.writeFloatBigEndian(sceneY.toFloat())
			this.bitBuffer!!.writeFloatBigEndian(sceneZ.toFloat())
		} else {
			this.bitBuffer!!.writeFloatLittleEndian(sceneX.toFloat())
			this.bitBuffer!!.writeFloatLittleEndian(sceneY.toFloat())
			this.bitBuffer!!.writeFloatLittleEndian(sceneZ.toFloat())
		}
		if (blue > 255) {
			blue = 255
		}
		this.bitBuffer!!.writeByte(red)
		this.bitBuffer!!.writeByte(green)
		this.bitBuffer!!.writeByte(blue)
		this.bitBuffer!!.writeByte(255)
		this.writeIndex(vertexIndex)
	}

	@OriginalMember(owner = "client!qw", name = "a", descriptor = "(BS)V")
	private fun writeIndex(@OriginalArg(1) index: Short) {
		if (this.toolkit!!.bigEndianess) {
			this.indexPacket!!.writeShort(index.toInt())
		} else {
			this.indexPacket!!.writeShortLE(index.toInt())
		}
	}

	@OriginalMember(owner = "client!qw", name = "a", descriptor = "([[ZIIII)V")
	fun drawLightMesh(@OriginalArg(0) visibility: Array<BooleanArray?>?, @OriginalArg(2) x: Int, @OriginalArg(3) z: Int, @OriginalArg(4) radius: Int) {
		if (this.indexBuffer == null || (radius + x < this.minTileX || (x - radius > this.maxTileX || (z + radius < this.minTileZ || this.maxTileZ < z - radius)))) {
			return
		}
		@Pc(50) var minTileZ: Int = this.minTileZ
		while (minTileZ <= this.maxTileZ) {
			@Pc(55) var minTileX: Int = this.minTileX
			while (minTileX <= this.maxTileX) {
				@Pc(61) val relativeX: Int = minTileX - x
				@Pc(66) val relativeZ: Int = minTileZ - z
				if (relativeX > -radius && radius > relativeX && -radius < relativeZ && relativeZ < radius && visibility!![relativeX + radius]!![radius + relativeZ]) {
					this.toolkit!!.setTextureEnvColour((this.pointLight!!.getIntensity() * 255.0f).toInt() shl 24)
					this.toolkit.toggleDrawingClientstates(this.positionDrawBuffer, this.colorDrawBuffer, null, null)
					this.toolkit.drawElements(this.totalIndexCount, this.indexBuffer, 0)
					return
				}
				minTileX++
			}
			minTileZ++
		}
	}
}
