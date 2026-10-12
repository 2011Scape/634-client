package com.jagex.game.runetek5.toolkit

import com.jagex.game.runetek5.VertexAttribute
import com.jagex.game.runetek5.NativeMatrix
import com.jagex.game.runetek5.ParticleList
import com.jagex.game.runetek5.graphics.model.particle.ParticleNode
import jaclib.memory.Buffer
import jaclib.memory.Stream
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.model.particle.PointEntity
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationComponent
import com.jagex.game.runetek5.graphics.GpuIndexBuffer
import com.jagex.game.runetek5.toolkit.companion.NativeStream
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclaration
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationAction
import com.jagex.game.runetek5.PrimitiveType
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField

@OriginalClass("client!ve")
class NativeParticleRenderer {

	@OriginalMember(owner = "client!ve", name = "c", descriptor = "[I")
	private val overflowBucketSizes: IntArray = IntArray(64)

	@OriginalMember(owner = "client!ve", name = "b", descriptor = "I")
	private var overflowBucketCount: Int = 0

	@OriginalMember(owner = "client!ve", name = "h", descriptor = "[[Lclient!jc;")
	private val buckets: Array<Array<PointEntity?>> = Array(1600) { arrayOfNulls<PointEntity>(64) }

	@OriginalMember(owner = "client!ve", name = "g", descriptor = "[I")
	private val depths: IntArray = IntArray(8191)

	@OriginalMember(owner = "client!ve", name = "r", descriptor = "[[Lclient!jc;")
	private val pointEntities: Array<Array<PointEntity?>> = Array(64) { arrayOfNulls<PointEntity>(768) }

	@OriginalMember(owner = "client!ve", name = "l", descriptor = "[I")
	private val bucketSizes: IntArray = IntArray(1600)

	@OriginalMember(owner = "client!ve", name = "m", descriptor = "Lclient!np;")
	private val vertexDeclaration: VertexDeclaration?

	@OriginalMember(owner = "client!ve", name = "n", descriptor = "Lclient!tc;")
	private val dynamicVertexStream: NativeStream?

	@OriginalMember(owner = "client!ve", name = "f", descriptor = "Lclient!tc;")
	private val staticNormalStream: NativeStream?

	@OriginalMember(owner = "client!ve", name = "k", descriptor = "Lclient!uq;")
	private val indexBuffer: GpuIndexBuffer?

	@OriginalMember(owner = "client!ve", name = "<init>", descriptor = "(Lclient!wga;)V")
	constructor(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?) {
		this.vertexDeclaration = nativeToolkit!!.createVertexDeclaration(arrayOf<VertexDeclarationComponent?>(VertexDeclarationComponent(arrayOf<VertexAttribute?>(VertexDeclarationAction.POSITION, VertexDeclarationAction.COLOR, VertexDeclarationAction.TEXCOORD)), VertexDeclarationComponent(VertexDeclarationAction.NORMAL_POINTER_ACTION)))
		this.dynamicVertexStream = nativeToolkit.createStream(true)
		this.staticNormalStream = nativeToolkit.createStream(false)
		this.staticNormalStream!!.create(393168, 12)
		this.indexBuffer = nativeToolkit.createIndexBuffer(false)
		this.indexBuffer!!.allocate(49146)
		@Pc(98) val buffer: Buffer? = this.indexBuffer.lock()
		@Pc(115) var local115: Int
		if (buffer != null) {
			@Pc(105) val stream: Stream = nativeToolkit.wrapBuffer(buffer)!!
			@Pc(109) var quad: Int
			if (Stream.c()) {
				quad = 0
				while (quad < 8191) {
					val baseVertex: Int = quad * 4
					stream.d(baseVertex)
					stream.d(baseVertex + 1)
					stream.d(baseVertex + 2)
					stream.d(baseVertex + 2)
					stream.d(baseVertex + 3)
					stream.d(baseVertex)
					quad++
				}
			} else {
				quad = 0
				while (quad < 8191) {
					val baseVertex: Int = quad * 4
					stream.a(baseVertex)
					stream.a(baseVertex + 1)
					stream.a(baseVertex + 2)
					stream.a(baseVertex + 2)
					stream.a(baseVertex + 3)
					stream.a(baseVertex)
					quad++
				}
			}
			stream.a()
			this.indexBuffer.unlock()
		}
		@Pc(198) val buffer2: Buffer? = this.staticNormalStream.lock()
		if (buffer2 != null) {
			@Pc(205) val stream2: Stream = nativeToolkit.wrapBuffer(buffer2)!!
			if (Stream.c()) {
				for (quadIndex in 0 until 8191) {
					stream2.a(0.0f)
					stream2.a(-1.0f)
					stream2.a(0.0f)
					stream2.a(0.0f)
					stream2.a(-1.0f)
					stream2.a(0.0f)
					stream2.a(0.0f)
					stream2.a(-1.0f)
					stream2.a(0.0f)
					stream2.a(0.0f)
					stream2.a(-1.0f)
					stream2.a(0.0f)
				}
			} else {
				for (quadIndex in 0 until 8191) {
					stream2.b(0.0f)
					stream2.b(-1.0f)
					stream2.b(0.0f)
					stream2.b(0.0f)
					stream2.b(-1.0f)
					stream2.b(0.0f)
					stream2.b(0.0f)
					stream2.b(-1.0f)
					stream2.b(0.0f)
					stream2.b(0.0f)
					stream2.b(-1.0f)
					stream2.b(0.0f)
				}
			}
			stream2.a()
			this.staticNormalStream.unlock()
		}
	}

	@OriginalMember(owner = "client!ve", name = "a", descriptor = "(Lclient!wga;II)V")
	private fun setupOrthoRenderState(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(2) orthoZoom: Int) {
		NativeParticleRenderer.intensity = nativeToolkit!!.intensity
		nativeToolkit.setOrthoZoom(orthoZoom.toFloat())
		nativeToolkit.setupOrthoSceneRenderState()
		nativeToolkit.setSunEnabled(false)
		nativeToolkit.setDepthMaskEnabled(false)
		nativeToolkit.resetModelMatrix()
	}

	@OriginalMember(owner = "client!ve", name = "a", descriptor = "(ILclient!wga;)V")
	private fun restoreRenderState(@OriginalArg(1) nativeToolkit: AbstractNativeToolkit?) {
		nativeToolkit!!.setDepthMaskEnabled(true)
		nativeToolkit.setSunEnabled(true)
		if (NativeParticleRenderer.intensity != nativeToolkit.intensity) {
			nativeToolkit.xa(NativeParticleRenderer.intensity)
		}
	}

	@OriginalMember(owner = "client!ve", name = "a", descriptor = "(BLclient!wga;)V")
	fun resetVertexStream(@OriginalArg(1) nativeToolkit: AbstractNativeToolkit?) {
		this.dynamicVertexStream!!.create(786336, 24)
	}

	@OriginalMember(owner = "client!ve", name = "a", descriptor = "(Lclient!wga;Lclient!mda;II)V")
	fun render(@OriginalArg(0) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(1) particleList: ParticleList?, @OriginalArg(2) orthoZoom: Int) {
		if (nativeToolkit!!.viewMatrix43 == null) {
			return
		}
		if (orthoZoom >= 0) {
			this.setupOrthoRenderState(nativeToolkit, orthoZoom)
		} else {
			this.setupSceneRenderState(nativeToolkit)
		}
		@Pc(29) val zA: Float = nativeToolkit.viewMatrix43!!.zA
		@Pc(33) val zB: Float = nativeToolkit.viewMatrix43!!.zB
		@Pc(37) val zC: Float = nativeToolkit.viewMatrix43!!.zC
		@Pc(41) val zD: Float = nativeToolkit.viewMatrix43!!.zD
		try {
			@Pc(43) var depthIndex = 0
			@Pc(45) var minDepth = Int.MAX_VALUE
			@Pc(47) var maxDepth = 0
			@Pc(51) val node2: ParticleNode? = particleList!!.particles.current
			@Pc(54) var node22: ParticleNode?
			@Pc(86) var local86: Int
			node22 = node2!!.next2
			while (node22 !== node2) {
				@Pc(59) val particle: PointEntity = node22 as PointEntity
				val depth: Int = ((particle.x shr 12).toFloat() * zA + zB * (particle.y shr 12).toFloat() + (particle.z shr 12).toFloat() * zC + zD).toInt()
				if (depth > maxDepth) {
					maxDepth = depth
				}
				this.depths[depthIndex++] = depth
				if (minDepth > depth) {
					minDepth = depth
				}
				node22 = node22.next2
			}
			@Pc(117) var bucketCount: Int = maxDepth - minDepth
			val depthShift: Int
			if (bucketCount + 2 > 1600) {
				depthShift = Utils.bitLength(bucketCount) + 1 - NativeParticleRenderer.DEPTH_BUCKET_SHIFT_BITS
				bucketCount = (bucketCount shr depthShift) + 2
			} else {
				depthShift = 0
				bucketCount += 2
			}
			node22 = node2.next2
			depthIndex = 0
			@Pc(151) var materialId = -2
			@Pc(153) var lightIntensityAdjustment = true
			@Pc(155) var startNewBatch = true
			while (node22 !== node2) {
				this.overflowBucketCount = 0
				for (@Pc(162) i in 0 until bucketCount) {
					this.bucketSizes[i] = 0
				}
				for (@Pc(179) j in 0 until 64) {
					this.overflowBucketSizes[j] = 0
				}
				while (node22 !== node2) {
					@Pc(195) val particle2: PointEntity = node22 as PointEntity
					if (startNewBatch) {
						lightIntensityAdjustment = particle2.lightIntensityAdjustment
						materialId = particle2.materialId
						startNewBatch = false
					}
					if (depthIndex > 0 && (materialId != particle2.materialId || lightIntensityAdjustment != particle2.lightIntensityAdjustment)) {
						startNewBatch = true
						break
					}
					@Pc(238) val bucketIndex: Int = (this.depths[depthIndex++] - minDepth) shr depthShift
					if (bucketIndex < 1600) {
						if (this.bucketSizes[bucketIndex] < 64) {
							this.buckets[bucketIndex][this.bucketSizes[bucketIndex]++] = particle2
						} else {
							label107@ do {
								if (this.bucketSizes[bucketIndex] == 64) {
									if (this.overflowBucketCount == 64) {
										break@label107
									}
									this.bucketSizes[bucketIndex] += this.overflowBucketCount++ + 1
								}
								@Pc(305) val particles: Array<PointEntity?> = this.pointEntities[this.bucketSizes[bucketIndex] - 65]
								@Pc(313) val overflowBucketIndex: Int = this.bucketSizes[bucketIndex] - 65
								@Pc(315) val overflowSlot: Int = this.overflowBucketSizes[this.bucketSizes[bucketIndex] - 65]
								this.overflowBucketSizes[overflowBucketIndex] = this.overflowBucketSizes[this.bucketSizes[bucketIndex] - 65] + 1
								particles[overflowSlot] = particle2
							} while (false)
						}
					}
					node22 = node22.next2
				}
				nativeToolkit.setActiveMaterial(false, false, if (materialId >= 0) materialId else -1)
				if (lightIntensityAdjustment && NativeParticleRenderer.intensity != nativeToolkit.intensity) {
					nativeToolkit.xa(NativeParticleRenderer.intensity)
				} else if (nativeToolkit.intensity != 1.0f) {
					nativeToolkit.xa(1.0f)
				}
				this.renderBatch(nativeToolkit, bucketCount)
			}
		} catch (@Pc(367) ex: Exception) {
		}
		this.restoreRenderState(nativeToolkit)
	}

	@OriginalMember(owner = "client!ve", name = "a", descriptor = "(ZLclient!wga;I)V")
	private fun renderBatch(@OriginalArg(1) nativeToolkit: AbstractNativeToolkit?, @OriginalArg(2) bucketCount: Int) {
		@Pc(15) var particleCount = 0
		@Pc(19) val viewMatrix: NativeMatrix = nativeToolkit!!.calibrateViewPosition()!!
		@Pc(22) val xA: Float = viewMatrix.xA
		@Pc(25) val xB: Float = viewMatrix.xB
		@Pc(28) val xC: Float = viewMatrix.xC
		@Pc(31) val yA: Float = viewMatrix.yA
		@Pc(34) val yB: Float = viewMatrix.yB
		@Pc(37) val yC: Float = viewMatrix.yC
		@Pc(41) val scaleXA: Float = yA + xA
		@Pc(45) val scaleYA: Float = xB + yB
		@Pc(49) val scaleZA: Float = xC + yC
		@Pc(53) val scaleXB: Float = xA - yA
		@Pc(57) val scaleYB: Float = xB - yB
		@Pc(62) val scaleZB: Float = xC - yC
		@Pc(66) val scaleXC: Float = yA - xA
		@Pc(71) val scaleYC: Float = yB - xB
		@Pc(76) val scaleZC: Float = yC - xC
		@Pc(82) val buffer: Buffer? = this.dynamicVertexStream!!.lock()
		if (buffer == null) {
			return
		}
		@Pc(90) val stream: Stream = nativeToolkit.wrapBuffer(buffer)!!
		@Pc(96) var bucketIndex: Int
		@Pc(111) var bucketSize: Int
		@Pc(120) var index: Int
		@Pc(129) var particle: PointEntity
		@Pc(132) var local132: Int
		@Pc(137) var red: Byte
		@Pc(142) var green: Byte
		@Pc(145) var local145: Byte
		@Pc(150) var local150: Byte
		@Pc(156) var x: Float
		@Pc(162) var y: Float
		@Pc(168) var local168: Float
		@Pc(173) var size: Int
		@Pc(444) var overflowZ: Float
		@Pc(449) var overflowSize: Int
		@Pc(389) var overflowBucketIndex: Int
		@Pc(405) var particle2: PointEntity
		@Pc(408) var colorArgb: Int
		@Pc(421) var overflowBlue: Byte
		@Pc(426) var overflowAlpha: Byte
		@Pc(438) var overflowY: Float
		if (Stream.c()) {
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
						stream.a(scaleXA * (-size).toFloat() + x)
						stream.a(y + scaleYA * (-size).toFloat())
						stream.a(z + (-size).toFloat() * scaleZA)
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						} else {
							stream.a(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						}
						stream.a(0.0f)
						stream.a(0.0f)
						stream.a(scaleXB * size.toFloat() + x)
						stream.a(size.toFloat() * scaleYB + y)
						stream.a(z + scaleZB * size.toFloat())
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						} else {
							stream.a(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						}
						stream.a(1.0f)
						stream.a(0.0f)
						stream.a(x + scaleXA * size.toFloat())
						stream.a(y + scaleYA * size.toFloat())
						stream.a(scaleZA * size.toFloat() + z)
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						} else {
							stream.a(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						}
						stream.a(1.0f)
						stream.a(1.0f)
						stream.a(x + scaleXC * size.toFloat())
						stream.a(y + scaleYC * size.toFloat())
						stream.a(z + scaleZC * size.toFloat())
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						} else {
							stream.a(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						}
						stream.a(0.0f)
						particleCount++
						stream.a(1.0f)
						index--
					}
					if (this.bucketSizes[bucketIndex] > 64) {
						overflowBucketIndex = this.bucketSizes[bucketIndex] - 65
						var overflowIndex: Int = this.overflowBucketSizes[overflowBucketIndex] - 1
						while (overflowIndex >= 0) {
							particle2 = this.pointEntities[overflowBucketIndex][overflowIndex]!!
							colorArgb = particle2.colorArgb
							val overflowRed: Byte = (colorArgb shr 16).toByte()
							val overflowGreen: Byte = (colorArgb shr 8).toByte()
							overflowBlue = colorArgb.toByte()
							overflowAlpha = (colorArgb ushr 24).toByte()
							val overflowX: Float = (particle2.x shr 12).toFloat()
							overflowY = (particle2.y shr 12).toFloat()
							overflowZ = (particle2.z shr 12).toFloat()
							overflowSize = particle2.size shr 12
							stream.a(scaleXA * (-overflowSize).toFloat() + overflowX)
							stream.a((-overflowSize).toFloat() * scaleYA + overflowY)
							stream.a(overflowZ + scaleZA * (-overflowSize).toFloat())
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							} else {
								stream.a(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							}
							stream.a(0.0f)
							stream.a(0.0f)
							stream.a(scaleXB * overflowSize.toFloat() + overflowX)
							stream.a(overflowSize.toFloat() * scaleYB + overflowY)
							stream.a(overflowSize.toFloat() * scaleZB + overflowZ)
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							} else {
								stream.a(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							}
							stream.a(1.0f)
							stream.a(0.0f)
							stream.a(overflowX + overflowSize.toFloat() * scaleXA)
							stream.a(overflowSize.toFloat() * scaleYA + overflowY)
							stream.a(overflowSize.toFloat() * scaleZA + overflowZ)
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							} else {
								stream.a(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							}
							stream.a(1.0f)
							stream.a(1.0f)
							stream.a(overflowSize.toFloat() * scaleXC + overflowX)
							stream.a(overflowSize.toFloat() * scaleYC + overflowY)
							stream.a(overflowZ + overflowSize.toFloat() * scaleZC)
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							} else {
								stream.a(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							}
							stream.a(0.0f)
							particleCount++
							stream.a(1.0f)
							overflowIndex--
						}
					}
				}
				bucketIndex--
			}
		} else {
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
						stream.b((-size).toFloat() * scaleXA + x)
						stream.b(y + (-size).toFloat() * scaleYA)
						stream.b((-size).toFloat() * scaleZA + z)
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						} else {
							stream.a(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						}
						stream.b(0.0f)
						stream.b(0.0f)
						stream.b(x + size.toFloat() * scaleXB)
						stream.b(y + size.toFloat() * scaleYB)
						stream.b(z + size.toFloat() * scaleZB)
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						} else {
							stream.a(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						}
						stream.b(1.0f)
						stream.b(0.0f)
						stream.b(x + scaleXA * size.toFloat())
						stream.b(scaleYA * size.toFloat() + y)
						stream.b(scaleZA * size.toFloat() + z)
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						} else {
							stream.a(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						}
						stream.b(1.0f)
						stream.b(1.0f)
						stream.b(scaleXC * size.toFloat() + x)
						stream.b(size.toFloat() * scaleYC + y)
						stream.b(z + scaleZC * size.toFloat())
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						} else {
							stream.a(red.toInt(), green.toInt(), blue.toInt(), alpha.toInt())
						}
						stream.b(0.0f)
						particleCount++
						stream.b(1.0f)
						index--
					}
					if (this.bucketSizes[bucketIndex] > 64) {
						overflowBucketIndex = this.bucketSizes[bucketIndex] - 64 - 1
						var overflowIndex: Int = this.overflowBucketSizes[overflowBucketIndex] - 1
						while (overflowIndex >= 0) {
							particle2 = this.pointEntities[overflowBucketIndex][overflowIndex]!!
							colorArgb = particle2.colorArgb
							val overflowRed: Byte = (colorArgb shr 16).toByte()
							val overflowGreen: Byte = (colorArgb shr 8).toByte()
							overflowBlue = colorArgb.toByte()
							overflowAlpha = (colorArgb ushr 24).toByte()
							val overflowX: Float = (particle2.x shr 12).toFloat()
							overflowY = (particle2.y shr 12).toFloat()
							overflowZ = (particle2.z shr 12).toFloat()
							overflowSize = particle2.size shr 12
							stream.b(overflowX + (-overflowSize).toFloat() * scaleXA)
							stream.b((-overflowSize).toFloat() * scaleYA + overflowY)
							stream.b(overflowZ + scaleZA * (-overflowSize).toFloat())
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							} else {
								stream.a(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							}
							stream.b(0.0f)
							stream.b(0.0f)
							stream.b(overflowX + overflowSize.toFloat() * scaleXB)
							stream.b(scaleYB * overflowSize.toFloat() + overflowY)
							stream.b(scaleZB * overflowSize.toFloat() + overflowZ)
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							} else {
								stream.a(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							}
							stream.b(1.0f)
							stream.b(0.0f)
							stream.b(overflowX + overflowSize.toFloat() * scaleXA)
							stream.b(overflowSize.toFloat() * scaleYA + overflowY)
							stream.b(scaleZA * overflowSize.toFloat() + overflowZ)
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							} else {
								stream.a(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							}
							stream.b(1.0f)
							stream.b(1.0f)
							stream.b(overflowX + scaleXC * overflowSize.toFloat())
							stream.b(overflowY + overflowSize.toFloat() * scaleYC)
							stream.b(overflowSize.toFloat() * scaleZC + overflowZ)
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							} else {
								stream.a(overflowRed.toInt(), overflowGreen.toInt(), overflowBlue.toInt(), overflowAlpha.toInt())
							}
							stream.b(0.0f)
							stream.b(1.0f)
							particleCount++
							overflowIndex--
						}
					}
				}
				bucketIndex--
			}
		}
		stream.a()
		if (this.dynamicVertexStream.unlock()) {
			nativeToolkit.setStreamSource(this.dynamicVertexStream, 0)
			nativeToolkit.setStreamSource(this.staticNormalStream, 1)
			nativeToolkit.setVertexDeclaration(this.vertexDeclaration)
			nativeToolkit.drawIndexedPrimitive(PrimitiveType.aClass134_6, particleCount * 2, this.indexBuffer, 0, 0, particleCount * 4)
		}
	}

	@OriginalMember(owner = "client!ve", name = "b", descriptor = "(BLclient!wga;)V")
	private fun setupSceneRenderState(@OriginalArg(1) nativeToolkit: AbstractNativeToolkit?) {
		NativeParticleRenderer.intensity = nativeToolkit!!.intensity
		nativeToolkit.setupSceneRenderState()
		nativeToolkit.setSunEnabled(false)
		nativeToolkit.setDepthMaskEnabled(false)
		nativeToolkit.resetModelMatrix()
	}

	@OriginalMember(owner = "client!ve", name = "b", descriptor = "(I)V")
	fun release() {
		this.dynamicVertexStream!!.deallocate()
	}

	companion object {
		@OriginalMember(owner = "client!pha", name = "i", descriptor = "I")
		@JvmField
		val DEPTH_BUCKET_SHIFT_BITS: Int = Utils.bitLength(1600)

		@OriginalMember(owner = "client!rga", name = "M", descriptor = "F")
		@JvmField
		var intensity: Float = 0f
	}
}
