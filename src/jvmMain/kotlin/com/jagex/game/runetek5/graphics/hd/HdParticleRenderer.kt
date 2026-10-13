/*
 * Not part of the original client: 634HD (toolkit 6), copied from GlParticleRenderer (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.core.constants.OpenGLConstants
import com.jagex.game.runetek5.graphics.model.particle.ParticleNode
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer
import com.jagex.game.runetek5.graphics.model.particle.PointEntity
import com.jagex.game.runetek5.graphics.hd.gl.HdGl
import com.jagex.game.runetek5.graphics.hd.gl.HdContext
import com.jagex.game.runetek5.buffer.ArrayBufferGL
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField

class HdParticleRenderer {

	private var texCoordPointer: OpenGlArrayDrawBuffer? = null

	private var arrayBuffer: ArrayBufferGL? = null

	private var colourPointer: OpenGlArrayDrawBuffer? = null

	private var vertexPointer: OpenGlArrayDrawBuffer? = null

	private val modelViewMatrix: FloatArray = FloatArray(16)

	private val vertices: GlBitBuffer = GlBitBuffer(786336)

	private val bucketCountBits: Int = Utils.bitLength(1600)

	private val depths: IntArray = IntArray(8191)

	private val buckets: Array<Array<PointEntity?>> = Array(1600) { arrayOfNulls<PointEntity>(64) }

	private val bucketSizes: IntArray = IntArray(1600)

	private var overflowBucketCount: Int = 0

	private val overflowBuckets: Array<Array<PointEntity?>> = Array(64) { arrayOfNulls<PointEntity>(768) }

	private val overflowBucketSizes: IntArray = IntArray(64)

	private fun setupOrthoRenderState(glToolkit: HdToolkit?, orthoZoom: Int) {
		HdParticleRenderer.savedSunBrightness = glToolkit!!.sunBrightness
		glToolkit.setOrthoZoom(orthoZoom.toFloat())
		glToolkit.setupOrthoMatrixRendering()
		HdGl.glDisable(OpenGLConstants.GL_LIGHT0)
		HdGl.glDisable(OpenGLConstants.GL_LIGHT1)
		glToolkit.setDepthMask(false)
		HdGl.glNormal3f(0.0f, -1.0f, 0.0f)
	}

	private fun restoreRenderState(glToolkit: HdToolkit?) {
		glToolkit!!.setDepthMask(true)
		HdGl.glEnable(OpenGLConstants.GL_LIGHT0)
		HdGl.glEnable(OpenGLConstants.GL_LIGHT1)
		if (HdParticleRenderer.savedSunBrightness != glToolkit.sunBrightness) {
			glToolkit.xa(HdParticleRenderer.savedSunBrightness)
		}
	}

	fun drawParticles(glToolkit: HdToolkit?, orthoZoom: Int, particleList: ParticleList?) {
		if (glToolkit!!.viewMatrix43 == null) {
			return
		}
		if (orthoZoom >= 0) {
			this.setupOrthoRenderState(glToolkit, orthoZoom)
		} else {
			this.setupSceneRenderState(glToolkit)
		}
		val zA: Float = glToolkit.viewMatrix43.zA
		val zB: Float = glToolkit.viewMatrix43.zB
		val zC: Float = glToolkit.viewMatrix43.zC
		val zD: Float = glToolkit.viewMatrix43.zD
		try {
			var depthIndex: Int = 0
			var minDepth: Int = Int.MAX_VALUE
			var maxDepth: Int = 0
			val node2: ParticleNode? = particleList!!.particles.current
			var node22: ParticleNode?
			var local83: Int
			node22 = node2!!.next2
			while (node22 !== node2) {
				val particle: PointEntity = node22 as PointEntity
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
			var bucketCount: Int = maxDepth - minDepth
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
			var materialId: Int = -2
			var lightIntensityAdjustment: Boolean = true
			var startNewBatch: Boolean = true
			while (node2 !== node22) {
				this.overflowBucketCount = 0
				for (i in 0 until bucketCount) {
					this.bucketSizes[i] = 0
				}
				for (j in 0 until 64) {
					this.overflowBucketSizes[j] = 0
				}
				while (node2 !== node22) {
					val particle2: PointEntity = node22 as PointEntity
					if (startNewBatch) {
						lightIntensityAdjustment = particle2.lightIntensityAdjustment
						materialId = particle2.materialId
						startNewBatch = false
					}
					if (depthIndex > 0 && (particle2.materialId != materialId || particle2.lightIntensityAdjustment != lightIntensityAdjustment)) {
						startNewBatch = true
						break
					}
					val bucketIndex: Int = (this.depths[depthIndex++] - minDepth) shr depthShift
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
				if (lightIntensityAdjustment && HdParticleRenderer.savedSunBrightness != glToolkit.sunBrightness) {
					glToolkit.xa(HdParticleRenderer.savedSunBrightness)
				} else if (glToolkit.sunBrightness != 1.0f) {
					glToolkit.xa(1.0f)
				}
				this.draw(glToolkit, bucketCount)
			}
		} catch (ex: Exception) {
		}
		this.restoreRenderState(glToolkit)
	}

	private fun setupSceneRenderState(glToolkit: HdToolkit?) {
		HdParticleRenderer.savedSunBrightness = glToolkit!!.sunBrightness
		glToolkit.setupMatrixRendering()
		HdGl.glDisable(16384)
		HdGl.glDisable(16385)
		glToolkit.setDepthMask(false)
		HdGl.glNormal3f(0.0f, -1.0f, 0.0f)
	}

	fun initialize(glToolkit: HdToolkit?) {
		this.arrayBuffer = glToolkit!!.createArrayBuffer(true, 24, null, 196584)
		this.texCoordPointer = OpenGlArrayDrawBuffer(this.arrayBuffer, OpenGLConstants.GL_FLOAT, 2, 0)
		this.vertexPointer = OpenGlArrayDrawBuffer(this.arrayBuffer, OpenGLConstants.GL_FLOAT, 3, 8)
		this.colourPointer = OpenGlArrayDrawBuffer(this.arrayBuffer, OpenGLConstants.GL_UNSIGNED_BYTE, 4, 20)
	}

	private fun draw(glToolkit: HdToolkit?, bucketCount: Int) {
		HdGl.glGetFloatv(OpenGLConstants.GL_MODELVIEW_MATRIX, this.modelViewMatrix, 0)
		val xA: Float = this.modelViewMatrix[0]
		val xB: Float = this.modelViewMatrix[4]
		val xC: Float = this.modelViewMatrix[8]
		val yA: Float = this.modelViewMatrix[1]
		val yB: Float = this.modelViewMatrix[5]
		val yC: Float = this.modelViewMatrix[9]
		val scaleXA: Float = yA + xA
		val scaleYA: Float = yB + xB
		val scaleZA: Float = yC + xC
		val scaleXB: Float = xA - yA
		val scaleYB: Float = xB - yB
		val scaleZB: Float = xC - yC
		val scaleXC: Float = yA - xA
		val scaleYC: Float = yB - xB
		this.vertices.currentPosition = 0
		val scaleZC: Float = yC - xC
		var bucketIndex: Int
		var bucketSize: Int
		var index: Int
		var particle: PointEntity
		var local131: Int
		var red: Byte
		var green: Byte
		var local144: Byte
		var local149: Byte
		var x: Float
		var y: Float
		var local167: Float
		var size: Int
		var overflowZ: Float
		var overflowSize: Int
		var overflowBucketIndex: Int
		var particle2: PointEntity
		var colorArgb: Int
		var overflowBlue: Byte
		var overflowAlpha: Byte
		var overflowY: Float
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
		@JvmField
		var savedSunBrightness: Float = 0f
	}
}
