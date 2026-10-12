package com.jagex.game.runetek5.toolkit.opengl

import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.model.particle.ParticleNode
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer
import com.jagex.game.runetek5.graphics.model.particle.PointEntity
import jaggl.OpenGL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.ArrayBufferGL
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!sn")
class GlParticleRenderer {

	@OriginalMember(owner = "client!sn", name = "k", descriptor = "Lclient!nh;")
	private var texCoordPointer: OpenGlArrayDrawBuffer? = null

	@OriginalMember(owner = "client!sn", name = "m", descriptor = "Lclient!hca;")
	private var arrayBuffer: ArrayBufferGL? = null

	@OriginalMember(owner = "client!sn", name = "n", descriptor = "Lclient!nh;")
	private var colourPointer: OpenGlArrayDrawBuffer? = null

	@OriginalMember(owner = "client!sn", name = "o", descriptor = "Lclient!nh;")
	private var vertexPointer: OpenGlArrayDrawBuffer? = null

	@OriginalMember(owner = "client!sn", name = "j", descriptor = "[F")
	private val modelViewMatrix: FloatArray = FloatArray(16)

	@OriginalMember(owner = "client!sn", name = "f", descriptor = "Lclient!gh;")
	private val vertices: GlBitBuffer = GlBitBuffer(786336)

	@OriginalMember(owner = "client!sn", name = "l", descriptor = "I")
	private val bucketCountBits: Int = Utils.bitLength(1600)

	@OriginalMember(owner = "client!sn", name = "r", descriptor = "[I")
	private val depths: IntArray = IntArray(8191)

	@OriginalMember(owner = "client!sn", name = "q", descriptor = "[[Lclient!jc;")
	private val buckets: Array<Array<PointEntity?>> = Array(1600) { arrayOfNulls<PointEntity>(64) }

	@OriginalMember(owner = "client!sn", name = "s", descriptor = "[I")
	private val bucketSizes: IntArray = IntArray(1600)

	@OriginalMember(owner = "client!sn", name = "t", descriptor = "I")
	private var overflowBucketCount: Int = 0

	@OriginalMember(owner = "client!sn", name = "p", descriptor = "[[Lclient!jc;")
	private val overflowBuckets: Array<Array<PointEntity?>> = Array(64) { arrayOfNulls<PointEntity>(768) }

	@OriginalMember(owner = "client!sn", name = "u", descriptor = "[I")
	private val overflowBucketSizes: IntArray = IntArray(64)

	@OriginalMember(owner = "client!sn", name = "a", descriptor = "(Lclient!qo;II)V")
	private fun setupOrthoRenderState(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) orthoZoom: Int) {
		GlParticleRenderer.savedSunBrightness = glToolkit!!.sunBrightness
		glToolkit.setOrthoZoom(orthoZoom.toFloat())
		glToolkit.setupOrthoMatrixRendering()
		OpenGL.glDisable(OpenGLConstants.GL_LIGHT0)
		OpenGL.glDisable(OpenGLConstants.GL_LIGHT1)
		glToolkit.setDepthMask(false)
		OpenGL.glNormal3f(0.0f, -1.0f, 0.0f)
	}

	@OriginalMember(owner = "client!sn", name = "a", descriptor = "(Lclient!qo;B)V")
	private fun restoreRenderState(@OriginalArg(0) glToolkit: GlToolkit?) {
		glToolkit!!.setDepthMask(true)
		OpenGL.glEnable(OpenGLConstants.GL_LIGHT0)
		OpenGL.glEnable(OpenGLConstants.GL_LIGHT1)
		if (GlParticleRenderer.savedSunBrightness != glToolkit.sunBrightness) {
			glToolkit.xa(GlParticleRenderer.savedSunBrightness)
		}
	}

	@OriginalMember(owner = "client!sn", name = "a", descriptor = "(Lclient!qo;BILclient!mda;)V")
	fun drawParticles(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(2) orthoZoom: Int, @OriginalArg(3) particleList: ParticleList?) {
		if (glToolkit!!.viewMatrix43 == null) {
			return
		}
		if (orthoZoom >= 0) {
			this.setupOrthoRenderState(glToolkit, orthoZoom)
		} else {
			this.setupSceneRenderState(glToolkit)
		}
		@Pc(26) val zA: Float = glToolkit.viewMatrix43.zA
		@Pc(30) val zB: Float = glToolkit.viewMatrix43.zB
		@Pc(34) val zC: Float = glToolkit.viewMatrix43.zC
		@Pc(38) val zD: Float = glToolkit.viewMatrix43.zD
		try {
			@Pc(40) var depthIndex: Int = 0
			@Pc(42) var minDepth: Int = Int.MAX_VALUE
			@Pc(44) var maxDepth: Int = 0
			@Pc(48) val node2: ParticleNode? = particleList!!.particles.current
			@Pc(51) var node22: ParticleNode?
			@Pc(83) var local83: Int
			node22 = node2!!.next2
			while (node22 !== node2) {
				@Pc(56) val particle: PointEntity = node22 as PointEntity
				val depth: Int = (zD + (particle.y shr 12).toFloat() * zB + (particle.x shr 12).toFloat() * zA + (particle.z shr 12).toFloat() * zC).toInt()
				if (maxDepth < depth) {
					maxDepth = depth
				}
				if (minDepth > depth) {
					minDepth = depth
				}
				this.depths[depthIndex++] = depth
				node22 = node22.next2
			}
			@Pc(109) var bucketCount: Int = maxDepth - minDepth
			val depthShift: Int
			if (bucketCount + 2 > 1600) {
				depthShift = Utils.bitLength(bucketCount) + 1 - this.bucketCountBits
				bucketCount = (bucketCount shr depthShift) + 2
			} else {
				bucketCount += 2
				depthShift = 0
			}
			depthIndex = 0
			node22 = node2.next2
			@Pc(144) var materialId: Int = -2
			@Pc(151) var lightIntensityAdjustment: Boolean = true
			@Pc(153) var startNewBatch: Boolean = true
			while (node2 !== node22) {
				this.overflowBucketCount = 0
				for (@Pc(160) i in 0 until bucketCount) {
					this.bucketSizes[i] = 0
				}
				for (@Pc(177) j in 0 until 64) {
					this.overflowBucketSizes[j] = 0
				}
				while (node2 !== node22) {
					@Pc(195) val particle2: PointEntity = node22 as PointEntity
					if (startNewBatch) {
						lightIntensityAdjustment = particle2.lightIntensityAdjustment
						materialId = particle2.materialId
						startNewBatch = false
					}
					if (depthIndex > 0 && (particle2.materialId != materialId || particle2.lightIntensityAdjustment != lightIntensityAdjustment)) {
						startNewBatch = true
						break
					}
					@Pc(237) val bucketIndex: Int = (this.depths[depthIndex++] - minDepth) shr depthShift
					if (bucketIndex < 1600) {
						if (this.bucketSizes[bucketIndex] < 64) {
							this.buckets[bucketIndex][this.bucketSizes[bucketIndex]++] = particle2
						} else {
							label101@ do {
								if (this.bucketSizes[bucketIndex] == 64) {
									if (this.overflowBucketCount == 64) {
										break@label101
									}
									this.bucketSizes[bucketIndex] += this.overflowBucketCount++ + 1
								}
								this.overflowBuckets[this.bucketSizes[bucketIndex] - 64 - 1][this.overflowBucketSizes[this.bucketSizes[bucketIndex] - 64 - 1]++] = particle2
							} while (false)
						}
					}
					node22 = node22.next2
				}
				if (materialId < 0) {
					glToolkit.renderTexture(-1)
				} else {
					glToolkit.renderTexture(materialId)
				}
				if (lightIntensityAdjustment && GlParticleRenderer.savedSunBrightness != glToolkit.sunBrightness) {
					glToolkit.xa(GlParticleRenderer.savedSunBrightness)
				} else if (glToolkit.sunBrightness != 1.0f) {
					glToolkit.xa(1.0f)
				}
				this.draw(glToolkit, bucketCount)
			}
		} catch (@Pc(374) ex: Exception) {
		}
		this.restoreRenderState(glToolkit)
	}

	@OriginalMember(owner = "client!sn", name = "a", descriptor = "(ZLclient!qo;)V")
	private fun setupSceneRenderState(@OriginalArg(1) glToolkit: GlToolkit?) {
		GlParticleRenderer.savedSunBrightness = glToolkit!!.sunBrightness
		glToolkit.setupMatrixRendering()
		OpenGL.glDisable(16384)
		OpenGL.glDisable(16385)
		glToolkit.setDepthMask(false)
		OpenGL.glNormal3f(0.0f, -1.0f, 0.0f)
	}

	@OriginalMember(owner = "client!sn", name = "a", descriptor = "(ILclient!qo;)V")
	fun initialize(@OriginalArg(1) glToolkit: GlToolkit?) {
		this.arrayBuffer = glToolkit!!.createArrayBuffer(true, 24, null, 196584)
		this.texCoordPointer = OpenGlArrayDrawBuffer(this.arrayBuffer, OpenGLConstants.GL_FLOAT, 2, 0)
		this.vertexPointer = OpenGlArrayDrawBuffer(this.arrayBuffer, OpenGLConstants.GL_FLOAT, 3, 8)
		this.colourPointer = OpenGlArrayDrawBuffer(this.arrayBuffer, OpenGLConstants.GL_UNSIGNED_BYTE, 4, 20)
	}

	@OriginalMember(owner = "client!sn", name = "b", descriptor = "(Lclient!qo;II)V")
	private fun draw(@OriginalArg(0) glToolkit: GlToolkit?, @OriginalArg(1) bucketCount: Int) {
		OpenGL.glGetFloatv(OpenGLConstants.GL_MODELVIEW_MATRIX, this.modelViewMatrix, 0)
		@Pc(19) val xA: Float = this.modelViewMatrix[0]
		@Pc(24) val xB: Float = this.modelViewMatrix[4]
		@Pc(29) val xC: Float = this.modelViewMatrix[8]
		@Pc(34) val yA: Float = this.modelViewMatrix[1]
		@Pc(39) val yB: Float = this.modelViewMatrix[5]
		@Pc(44) val yC: Float = this.modelViewMatrix[9]
		@Pc(48) val scaleXA: Float = yA + xA
		@Pc(52) val scaleYA: Float = yB + xB
		@Pc(56) val scaleZA: Float = yC + xC
		@Pc(60) val scaleXB: Float = xA - yA
		@Pc(65) val scaleYB: Float = xB - yB
		@Pc(70) val scaleZB: Float = xC - yC
		@Pc(75) val scaleXC: Float = yA - xA
		@Pc(80) val scaleYC: Float = yB - xB
		this.vertices.currentPosition = 0
		@Pc(89) val scaleZC: Float = yC - xC
		@Pc(96) var bucketIndex: Int
		@Pc(113) var bucketSize: Int
		@Pc(119) var index: Int
		@Pc(128) var particle: PointEntity
		@Pc(131) var local131: Int
		@Pc(136) var red: Byte
		@Pc(141) var green: Byte
		@Pc(144) var local144: Byte
		@Pc(149) var local149: Byte
		@Pc(155) var x: Float
		@Pc(161) var y: Float
		@Pc(167) var local167: Float
		@Pc(172) var size: Int
		@Pc(497) var overflowZ: Float
		@Pc(502) var overflowSize: Int
		@Pc(442) var overflowBucketIndex: Int
		@Pc(458) var particle2: PointEntity
		@Pc(461) var colorArgb: Int
		@Pc(474) var overflowBlue: Byte
		@Pc(479) var overflowAlpha: Byte
		@Pc(491) var overflowY: Float
		if (glToolkit!!.bigEndianess) {
			bucketIndex = bucketCount - 1
			while (bucketIndex >= 0) {
				bucketSize = if (this.bucketSizes[bucketIndex] > 64) 64 else this.bucketSizes[bucketIndex]
				if (bucketSize > 0) {
					index = bucketSize - 1
					while (index >= 0) {
						particle = this.buckets[bucketIndex][index]!!
						val argb: Int = particle.colorArgb
						red = (argb shr 16).toByte()
						green = (argb shr 8).toByte()
						val blue: Byte = argb.toByte()
						val alpha: Byte = (argb ushr 24).toByte()
						x = (particle.x shr 12).toFloat()
						y = (particle.y shr 12).toFloat()
						val z: Float = (particle.z shr 12).toFloat()
						size = particle.size shr 12
						this.vertices.writeFloatBigEndian(0.0f)
						this.vertices.writeFloatBigEndian(0.0f)
						this.vertices.writeFloatBigEndian((-size).toFloat() * scaleXA + x)
						this.vertices.writeFloatBigEndian(y + scaleYA * (-size).toFloat())
						this.vertices.writeFloatBigEndian(z + scaleZA * (-size).toFloat())
						this.vertices.writeByte(red.toInt())
						this.vertices.writeByte(green.toInt())
						this.vertices.writeByte(blue.toInt())
						this.vertices.writeByte(alpha.toInt())
						this.vertices.writeFloatBigEndian(1.0f)
						this.vertices.writeFloatBigEndian(0.0f)
						this.vertices.writeFloatBigEndian(size.toFloat() * scaleXB + x)
						this.vertices.writeFloatBigEndian(size.toFloat() * scaleYB + y)
						this.vertices.writeFloatBigEndian(z + size.toFloat() * scaleZB)
						this.vertices.writeByte(red.toInt())
						this.vertices.writeByte(green.toInt())
						this.vertices.writeByte(blue.toInt())
						this.vertices.writeByte(alpha.toInt())
						this.vertices.writeFloatBigEndian(1.0f)
						this.vertices.writeFloatBigEndian(1.0f)
						this.vertices.writeFloatBigEndian(x + scaleXA * size.toFloat())
						this.vertices.writeFloatBigEndian(scaleYA * size.toFloat() + y)
						this.vertices.writeFloatBigEndian(z + size.toFloat() * scaleZA)
						this.vertices.writeByte(red.toInt())
						this.vertices.writeByte(green.toInt())
						this.vertices.writeByte(blue.toInt())
						this.vertices.writeByte(alpha.toInt())
						this.vertices.writeFloatBigEndian(0.0f)
						this.vertices.writeFloatBigEndian(1.0f)
						this.vertices.writeFloatBigEndian(scaleXC * size.toFloat() + x)
						this.vertices.writeFloatBigEndian(y + scaleYC * size.toFloat())
						this.vertices.writeFloatBigEndian(scaleZC * size.toFloat() + z)
						this.vertices.writeByte(red.toInt())
						this.vertices.writeByte(green.toInt())
						this.vertices.writeByte(blue.toInt())
						this.vertices.writeByte(alpha.toInt())
						index--
					}
					if (this.bucketSizes[bucketIndex] > 64) {
						overflowBucketIndex = this.bucketSizes[bucketIndex] - 64 - 1
						for (overflowIndex in (this.overflowBucketSizes[overflowBucketIndex] - 1) downTo 0) {
							particle2 = this.overflowBuckets[overflowBucketIndex][overflowIndex]!!
							colorArgb = particle2.colorArgb
							val overflowRed: Byte = (colorArgb shr 16).toByte()
							val overflowGreen: Byte = (colorArgb shr 8).toByte()
							overflowBlue = colorArgb.toByte()
							overflowAlpha = (colorArgb ushr 24).toByte()
							val overflowX: Float = (particle2.x shr 12).toFloat()
							overflowY = (particle2.y shr 12).toFloat()
							overflowZ = (particle2.z shr 12).toFloat()
							overflowSize = particle2.size shr 12
							this.vertices.writeFloatBigEndian(0.0f)
							this.vertices.writeFloatBigEndian(0.0f)
							this.vertices.writeFloatBigEndian((-overflowSize).toFloat() * scaleXA + overflowX)
							this.vertices.writeFloatBigEndian(overflowY + scaleYA * (-overflowSize).toFloat())
							this.vertices.writeFloatBigEndian(overflowZ + scaleZA * (-overflowSize).toFloat())
							this.vertices.writeByte(overflowRed.toInt())
							this.vertices.writeByte(overflowGreen.toInt())
							this.vertices.writeByte(overflowBlue.toInt())
							this.vertices.writeByte(overflowAlpha.toInt())
							this.vertices.writeFloatBigEndian(1.0f)
							this.vertices.writeFloatBigEndian(0.0f)
							this.vertices.writeFloatBigEndian(overflowSize.toFloat() * scaleXB + overflowX)
							this.vertices.writeFloatBigEndian(overflowY + scaleYB * overflowSize.toFloat())
							this.vertices.writeFloatBigEndian(overflowZ + overflowSize.toFloat() * scaleZB)
							this.vertices.writeByte(overflowRed.toInt())
							this.vertices.writeByte(overflowGreen.toInt())
							this.vertices.writeByte(overflowBlue.toInt())
							this.vertices.writeByte(overflowAlpha.toInt())
							this.vertices.writeFloatBigEndian(1.0f)
							this.vertices.writeFloatBigEndian(1.0f)
							this.vertices.writeFloatBigEndian(scaleXA * overflowSize.toFloat() + overflowX)
							this.vertices.writeFloatBigEndian(scaleYA * overflowSize.toFloat() + overflowY)
							this.vertices.writeFloatBigEndian(overflowZ + overflowSize.toFloat() * scaleZA)
							this.vertices.writeByte(overflowRed.toInt())
							this.vertices.writeByte(overflowGreen.toInt())
							this.vertices.writeByte(overflowBlue.toInt())
							this.vertices.writeByte(overflowAlpha.toInt())
							this.vertices.writeFloatBigEndian(0.0f)
							this.vertices.writeFloatBigEndian(1.0f)
							this.vertices.writeFloatBigEndian(overflowSize.toFloat() * scaleXC + overflowX)
							this.vertices.writeFloatBigEndian(overflowSize.toFloat() * scaleYC + overflowY)
							this.vertices.writeFloatBigEndian(overflowSize.toFloat() * scaleZC + overflowZ)
							this.vertices.writeByte(overflowRed.toInt())
							this.vertices.writeByte(overflowGreen.toInt())
							this.vertices.writeByte(overflowBlue.toInt())
							this.vertices.writeByte(overflowAlpha.toInt())
						}
					}
				}
				bucketIndex--
			}
		} else {
			bucketIndex = bucketCount - 1
			while (bucketIndex >= 0) {
				bucketSize = if (this.bucketSizes[bucketIndex] <= 64) this.bucketSizes[bucketIndex] else 64
				if (bucketSize > 0) {
					index = bucketSize - 1
					while (index >= 0) {
						particle = this.buckets[bucketIndex][index]!!
						val argb: Int = particle.colorArgb
						red = (argb shr 16).toByte()
						green = (argb shr 8).toByte()
						val blue: Byte = argb.toByte()
						val alpha: Byte = (argb ushr 24).toByte()
						x = (particle.x shr 12).toFloat()
						y = (particle.y shr 12).toFloat()
						val z: Float = (particle.z shr 12).toFloat()
						size = particle.size shr 12
						this.vertices.writeFloatLittleEndian(0.0f)
						this.vertices.writeFloatLittleEndian(0.0f)
						this.vertices.writeFloatLittleEndian((-size).toFloat() * scaleXA + x)
						this.vertices.writeFloatLittleEndian((-size).toFloat() * scaleYA + y)
						this.vertices.writeFloatLittleEndian(scaleZA * (-size).toFloat() + z)
						this.vertices.writeByte(red.toInt())
						this.vertices.writeByte(green.toInt())
						this.vertices.writeByte(blue.toInt())
						this.vertices.writeByte(alpha.toInt())
						this.vertices.writeFloatLittleEndian(1.0f)
						this.vertices.writeFloatLittleEndian(0.0f)
						this.vertices.writeFloatLittleEndian(x + scaleXB * size.toFloat())
						this.vertices.writeFloatLittleEndian(y + scaleYB * size.toFloat())
						this.vertices.writeFloatLittleEndian(z + scaleZB * size.toFloat())
						this.vertices.writeByte(red.toInt())
						this.vertices.writeByte(green.toInt())
						this.vertices.writeByte(blue.toInt())
						this.vertices.writeByte(alpha.toInt())
						this.vertices.writeFloatLittleEndian(1.0f)
						this.vertices.writeFloatLittleEndian(1.0f)
						this.vertices.writeFloatLittleEndian(size.toFloat() * scaleXA + x)
						this.vertices.writeFloatLittleEndian(y + size.toFloat() * scaleYA)
						this.vertices.writeFloatLittleEndian(z + scaleZA * size.toFloat())
						this.vertices.writeByte(red.toInt())
						this.vertices.writeByte(green.toInt())
						this.vertices.writeByte(blue.toInt())
						this.vertices.writeByte(alpha.toInt())
						this.vertices.writeFloatLittleEndian(0.0f)
						this.vertices.writeFloatLittleEndian(1.0f)
						this.vertices.writeFloatLittleEndian(x + scaleXC * size.toFloat())
						this.vertices.writeFloatLittleEndian(size.toFloat() * scaleYC + y)
						this.vertices.writeFloatLittleEndian(size.toFloat() * scaleZC + z)
						this.vertices.writeByte(red.toInt())
						this.vertices.writeByte(green.toInt())
						this.vertices.writeByte(blue.toInt())
						this.vertices.writeByte(alpha.toInt())
						index--
					}
					if (this.bucketSizes[bucketIndex] > 64) {
						overflowBucketIndex = this.bucketSizes[bucketIndex] - 65
						for (overflowIndex in (this.overflowBucketSizes[overflowBucketIndex] - 1) downTo 0) {
							particle2 = this.overflowBuckets[overflowBucketIndex][overflowIndex]!!
							colorArgb = particle2.colorArgb
							val overflowRed: Byte = (colorArgb shr 16).toByte()
							val overflowGreen: Byte = (colorArgb shr 8).toByte()
							overflowBlue = colorArgb.toByte()
							overflowAlpha = (colorArgb ushr 24).toByte()
							val overflowX: Float = (particle2.x shr 12).toFloat()
							overflowY = (particle2.y shr 12).toFloat()
							overflowZ = (particle2.z shr 12).toFloat()
							overflowSize = particle2.size shr 12
							this.vertices.writeFloatLittleEndian(0.0f)
							this.vertices.writeFloatLittleEndian(0.0f)
							this.vertices.writeFloatLittleEndian(overflowX + (-overflowSize).toFloat() * scaleXA)
							this.vertices.writeFloatLittleEndian(overflowY + scaleYA * (-overflowSize).toFloat())
							this.vertices.writeFloatLittleEndian(overflowZ + scaleZA * (-overflowSize).toFloat())
							this.vertices.writeByte(overflowRed.toInt())
							this.vertices.writeByte(overflowGreen.toInt())
							this.vertices.writeByte(overflowBlue.toInt())
							this.vertices.writeByte(overflowAlpha.toInt())
							this.vertices.writeFloatLittleEndian(1.0f)
							this.vertices.writeFloatLittleEndian(0.0f)
							this.vertices.writeFloatLittleEndian(scaleXB * overflowSize.toFloat() + overflowX)
							this.vertices.writeFloatLittleEndian(overflowSize.toFloat() * scaleYB + overflowY)
							this.vertices.writeFloatLittleEndian(scaleZB * overflowSize.toFloat() + overflowZ)
							this.vertices.writeByte(overflowRed.toInt())
							this.vertices.writeByte(overflowGreen.toInt())
							this.vertices.writeByte(overflowBlue.toInt())
							this.vertices.writeByte(overflowAlpha.toInt())
							this.vertices.writeFloatLittleEndian(1.0f)
							this.vertices.writeFloatLittleEndian(1.0f)
							this.vertices.writeFloatLittleEndian(overflowSize.toFloat() * scaleXA + overflowX)
							this.vertices.writeFloatLittleEndian(overflowSize.toFloat() * scaleYA + overflowY)
							this.vertices.writeFloatLittleEndian(overflowSize.toFloat() * scaleZA + overflowZ)
							this.vertices.writeByte(overflowRed.toInt())
							this.vertices.writeByte(overflowGreen.toInt())
							this.vertices.writeByte(overflowBlue.toInt())
							this.vertices.writeByte(overflowAlpha.toInt())
							this.vertices.writeFloatLittleEndian(0.0f)
							this.vertices.writeFloatLittleEndian(1.0f)
							this.vertices.writeFloatLittleEndian(overflowSize.toFloat() * scaleXC + overflowX)
							this.vertices.writeFloatLittleEndian(overflowSize.toFloat() * scaleYC + overflowY)
							this.vertices.writeFloatLittleEndian(overflowSize.toFloat() * scaleZC + overflowZ)
							this.vertices.writeByte(overflowRed.toInt())
							this.vertices.writeByte(overflowGreen.toInt())
							this.vertices.writeByte(overflowBlue.toInt())
							this.vertices.writeByte(overflowAlpha.toInt())
						}
					}
				}
				bucketIndex--
			}
		}
		if (this.vertices.currentPosition != 0) {
			this.arrayBuffer!!.putArray(24, this.vertices.currentPosition, this.vertices.buffer)
			glToolkit.toggleDrawingClientstates(this.vertexPointer, this.colourPointer, this.texCoordPointer, null)
			glToolkit.drawArrays(this.vertices.currentPosition / 24)
		}
	}

	companion object {
		@OriginalMember(owner = "client!lea", name = "b", descriptor = "F")
		@JvmField
		var savedSunBrightness: Float = 0f
	}
}
