package com.jagex.game.runetek5.entity

import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.graphics.model.particle.Particle
import com.jagex.game.runetek5.config.particle.ParticleProducerType
import com.jagex.game.runetek5.graphics.Toolkit
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle
import com.jagex.game.runetek5.graphics.particles.ParticleEmitterRelated
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager

@OriginalClass("client!rba")
class ParticleEmitter : EntityNode {

	@OriginalMember(owner = "client!rba", name = "w", descriptor = "I")
	@JvmField
	var particleCount: Int = 0

	@OriginalMember(owner = "client!rba", name = "B", descriptor = "I")
	private var baseRotationY: Int = 0

	@OriginalMember(owner = "client!rba", name = "D", descriptor = "I")
	private var randomisedRotationOffsetY: Int = 0

	@OriginalMember(owner = "client!rba", name = "E", descriptor = "I")
	private var movementDirectionX: Int = 0

	@OriginalMember(owner = "client!rba", name = "F", descriptor = "I")
	private var randomisedRotationOffsetX: Int = 0

	@OriginalMember(owner = "client!rba", name = "G", descriptor = "I")
	private var movementDirectionY: Int = 0

	@OriginalMember(owner = "client!rba", name = "H", descriptor = "I")
	private var movementDirectionZ: Int = 0

	@OriginalMember(owner = "client!rba", name = "I", descriptor = "I")
	private var baseRotationX: Int = 0

	@OriginalMember(owner = "client!rba", name = "j", descriptor = "I")
	private var spawnAccumulator: Int = 0

	@OriginalMember(owner = "client!rba", name = "v", descriptor = "Z")
	@JvmField
	var inactive: Boolean = false

	@OriginalMember(owner = "client!rba", name = "t", descriptor = "Lclient!vha;")
	@JvmField
	var triangle: ParticleEmitterRelated = ParticleEmitterRelated()

	@OriginalMember(owner = "client!rba", name = "y", descriptor = "Lclient!vha;")
	private var previousTriangle: ParticleEmitterRelated = ParticleEmitterRelated()

	@OriginalMember(owner = "client!rba", name = "C", descriptor = "Z")
	private var finished: Boolean = false

	@OriginalMember(owner = "client!rba", name = "k", descriptor = "J")
	private val startTime: Long

	@OriginalMember(owner = "client!rba", name = "l", descriptor = "Lclient!nn;")
	@JvmField
	val model: EmissiveTriangle?

	@OriginalMember(owner = "client!rba", name = "o", descriptor = "Lclient!uj;")
	@JvmField
	val system: ParticleSystem?

	@OriginalMember(owner = "client!rba", name = "q", descriptor = "Lclient!qb;")
	@JvmField
	var type: ParticleProducerType?

	@OriginalMember(owner = "client!rba", name = "i", descriptor = "Lclient!tca;")
	@JvmField
	val particles: EntityNodeList

	@OriginalMember(owner = "client!rba", name = "<init>", descriptor = "(Lclient!ha;Lclient!nn;Lclient!uj;J)V")
	constructor(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) modelParticleEmitter: EmissiveTriangle?, @OriginalArg(2) particleSystem: ParticleSystem?, @OriginalArg(3) startTime: Long) : super() {
		this.startTime = startTime
		this.model = modelParticleEmitter
		this.system = particleSystem
		this.type = this.model!!.getProducer()
		if (!toolkit!!.hasTextures() && this.type!!.nonTexturedProducerId != -1) {
			this.type = ParticleProducerType.get(this.type!!.nonTexturedProducerId)
		}
		this.particles = EntityNodeList()
		this.spawnAccumulator = (this.spawnAccumulator.toDouble() + Math.random() * 64.0).toInt()
		this.updateTriangle()
		this.previousTriangle.verticeXB = this.triangle.verticeXB
		this.previousTriangle.verticeYB = this.triangle.verticeYB
		this.previousTriangle.verticeXA = this.triangle.verticeXA
		this.previousTriangle.verticeXC = this.triangle.verticeXC
		this.previousTriangle.verticeYA = this.triangle.verticeYA
		this.previousTriangle.verticeYC = this.triangle.verticeYC
		this.previousTriangle.verticeZC = this.triangle.verticeZC
		this.previousTriangle.verticeZB = this.triangle.verticeZB
		this.previousTriangle.verticeZA = this.triangle.verticeZA
	}

	@OriginalMember(owner = "client!rba", name = "b", descriptor = "(Z)V")
	fun updateTriangle() {
		this.triangle.verticeYA = this.model!!.verticeXA
		this.triangle.verticeZC = this.model.verticeYA
		this.triangle.verticeXA = this.model.verticeZA
		this.triangle.verticeYB = this.model.transformedCY
		this.triangle.verticeXB = this.model.verticeYB
		this.triangle.verticeZA = this.model.verticeZB
		this.triangle.verticeYC = this.model.verticeXC
		this.triangle.verticeZB = this.model.verticeYC
		this.triangle.verticeXC = this.model.verticeZC
		if (this.triangle.verticeXC == this.triangle.verticeXA && this.triangle.verticeXB == this.triangle.verticeXA && this.triangle.verticeYC == this.triangle.verticeYA && this.triangle.verticeYA == this.triangle.verticeYB && this.triangle.verticeZA == this.triangle.verticeZC && this.triangle.verticeZA == this.triangle.verticeZB) {
			this.finished = true
		} else if (this.finished) {
			this.previousTriangle.verticeYC = this.triangle.verticeYC
			this.finished = false
			this.previousTriangle.verticeZB = this.triangle.verticeZB
			this.previousTriangle.verticeZC = this.triangle.verticeZC
			this.previousTriangle.verticeYA = this.triangle.verticeYA
			this.previousTriangle.verticeXA = this.triangle.verticeXA
			this.previousTriangle.verticeZA = this.triangle.verticeZA
			this.previousTriangle.verticeXC = this.triangle.verticeXC
			this.previousTriangle.verticeYB = this.triangle.verticeYB
			this.previousTriangle.verticeXB = this.triangle.verticeXB
		}
	}

	@OriginalMember(owner = "client!rba", name = "a", descriptor = "(ZLclient!ha;J)V")
	fun processCycle(@OriginalArg(1) toolkit: Toolkit?, @OriginalArg(2) time: Long) {
		@Pc(17) var movingParticle: Particle? = this.particles.getFirst() as Particle?
		while (movingParticle != null) {
			movingParticle.collide(toolkit, time)
			movingParticle = this.particles.getNext() as Particle?
		}
	}

	@OriginalMember(owner = "client!rba", name = "a", descriptor = "(Lclient!ha;IJIZ)V")
	fun performUpdate(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) elapsedTime: Int, @OriginalArg(2) time: Long, @OriginalArg(4) emit: Boolean) {
		var emit: Boolean = emit
		@Pc(33) val local33: Int
		if (this.inactive) {
			emit = false
		} else if (ParticleSystemManager.detailsSetting < this.type!!.minimumSetting) {
			emit = false
		} else if (ParticleSystemManager.lastRenderedParticleCount > ParticleSystemManager.PARTICLE_COUNT_LIMITS[ParticleSystemManager.detailsSetting]) {
			emit = false
		} else if (this.finished) {
			emit = false
		} else if (this.type!!.lifetime != -1) {
			var emitterAge: Int = (time - this.startTime).toInt()
			if (this.type!!.periodic || this.type!!.lifetime >= emitterAge) {
				emitterAge %= this.type!!.lifetime
			} else {
				emit = false
			}
			if (!this.type!!.activeFirst && emitterAge < this.type!!.emissionEndTime) {
				emit = false
			}
			if (this.type!!.activeFirst && this.type!!.emissionEndTime <= emitterAge) {
				emit = false
			}
		}
		if (emit) {
			ParticleSystemManager.emitterCount++
			val centerX: Int = (this.triangle.verticeXC + this.triangle.verticeXA + this.triangle.verticeXB) / 3
			@Pc(128) val centerY: Int = (this.triangle.verticeYB + this.triangle.verticeYC + this.triangle.verticeYA) / 3
			@Pc(142) val centerZ: Int = (this.triangle.verticeZB + this.triangle.verticeZA + this.triangle.verticeZC) / 3
			@Pc(186) val local186: Int
			@Pc(195) val local195: Int
			@Pc(204) val local204: Int
			@Pc(212) val local212: Int
			@Pc(220) val local220: Int
			@Pc(229) val local229: Int
			@Pc(330) val local330: Int
			@Pc(380) val local380: Int
			@Pc(401) val local401: Int
			if (this.triangle.centerX != centerX || centerY != this.triangle.centerY || this.triangle.centerZ != centerZ) {
				this.triangle.centerY = centerY
				this.triangle.centerX = centerX
				this.triangle.centerZ = centerZ
				val edgeAX: Int = this.triangle.verticeXA - this.triangle.verticeXC
				val edgeAY: Int = this.triangle.verticeYA - this.triangle.verticeYC
				val edgeAZ: Int = this.triangle.verticeZA - this.triangle.verticeZC
				val edgeBX: Int = this.triangle.verticeXB - this.triangle.verticeXC
				val edgeBY: Int = this.triangle.verticeYB - this.triangle.verticeYC
				val edgeBZ: Int = this.triangle.verticeZB - this.triangle.verticeZC
				this.movementDirectionY = edgeBX * edgeAZ - edgeAX * edgeBZ
				this.movementDirectionX = edgeAY * edgeBZ - edgeBY * edgeAZ
				this.movementDirectionZ = edgeBY * edgeAX - edgeBX * edgeAY
				while (true) {
					if (this.movementDirectionX <= 32767 && this.movementDirectionY <= 32767 && this.movementDirectionZ <= 32767 && this.movementDirectionX >= -32767 && this.movementDirectionY >= -32767 && this.movementDirectionZ >= -32767) {
						var magnitude: Int = Math.sqrt((this.movementDirectionX * this.movementDirectionX + this.movementDirectionY * this.movementDirectionY + this.movementDirectionZ * this.movementDirectionZ).toDouble()).toInt()
						if (magnitude <= 0) {
							magnitude = 1
						}
						this.movementDirectionX = this.movementDirectionX * 32767 / magnitude
						this.movementDirectionZ = this.movementDirectionZ * 32767 / magnitude
						this.movementDirectionY = this.movementDirectionY * 32767 / magnitude
						if (this.type!!.maximalAngleH > 0 || this.type!!.maximalAngleV > 0) {
							val directionAngleH: Int = (Math.atan2(this.movementDirectionZ.toDouble(), this.movementDirectionX.toDouble()) * 2607.5945876176133).toInt()
							val directionAngleV: Int = (Math.atan2(this.movementDirectionY.toDouble(), Math.sqrt((this.movementDirectionZ * this.movementDirectionZ + this.movementDirectionX * this.movementDirectionX).toDouble())) * 2607.5945876176133).toInt()
							this.randomisedRotationOffsetX = this.type!!.maximalAngleH - this.type!!.minimalAngleH
							this.baseRotationX = this.type!!.minimalAngleH + directionAngleH - (this.randomisedRotationOffsetX shr 1)
							this.randomisedRotationOffsetY = this.type!!.maximalAngleV - this.type!!.minimumAngleV
							this.baseRotationY = directionAngleV + this.type!!.minimumAngleV - (this.randomisedRotationOffsetY shr 1)
						}
						break
					}
					this.movementDirectionZ = this.movementDirectionZ shr 0x1
					this.movementDirectionY = this.movementDirectionY shr 0x1
					this.movementDirectionX = this.movementDirectionX shr 0x1
				}
			}
			this.spawnAccumulator += (((this.type!!.maximumParticleRate - this.type!!.minimumParticleRate).toDouble() * Math.random() + this.type!!.minimumParticleRate.toDouble()) * elapsedTime.toDouble()).toInt()
			if (this.spawnAccumulator > 63) {
				val spawnCount: Int = this.spawnAccumulator shr 6
				this.spawnAccumulator = this.spawnAccumulator and 0x3F
				var particleIndex: Int = 0
				while (particleIndex < spawnCount) {
					@Pc(555) val local555: Int
					@Pc(559) val local559: Int
					val directionX: Int
					val directionY: Int
					val directionZ: Int
					if (this.type!!.maximalAngleH <= 0 && this.type!!.maximalAngleV <= 0) {
						directionX = this.movementDirectionX
						directionZ = this.movementDirectionZ
						directionY = this.movementDirectionY
					} else {
						var angleH: Int = (Math.random() * this.randomisedRotationOffsetX.toDouble()).toInt() + this.baseRotationX
						angleH = angleH and 0x3FFF
						val sinH: Int = TextureCombineSource.SINE[angleH]
						val cosH: Int = TextureCombineSource.COSINE[angleH]
						var angleV: Int = this.baseRotationY + (this.randomisedRotationOffsetY.toDouble() * Math.random()).toInt()
						angleV = angleV and 0x1FFF
						val sinV: Int = TextureCombineSource.SINE[angleV]
						val cosV: Int = TextureCombineSource.COSINE[angleV]
						directionX = (sinV * cosH) shr 13
						directionY = (cosV shl 1) * -1
						directionZ = (sinV * sinH) shr 13
					}
					@Pc(582) var weightA: Float = Math.random().toFloat()
					@Pc(585) var weightB: Float = Math.random().toFloat()
					if (weightA + weightB > 1.0f) {
						weightB = 1.0f - weightB
						weightA = 1.0f - weightA
					}
					@Pc(606) val weightC: Float = 1.0f - weightA - weightB
					val spawnX: Int = (this.triangle.verticeXB.toFloat() * weightC + weightA * this.triangle.verticeXC.toFloat() + weightB * this.triangle.verticeXA.toFloat()).toInt()
					val spawnY: Int = (weightA * this.triangle.verticeYC.toFloat() + this.triangle.verticeYA.toFloat() * weightB + this.triangle.verticeYB.toFloat() * weightC).toInt()
					val spawnZ: Int = (this.triangle.verticeZC.toFloat() * weightA + this.triangle.verticeZA.toFloat() * weightB + weightC * this.triangle.verticeZB.toFloat()).toInt()
					@Pc(694) val previousX: Int = (this.previousTriangle.verticeXB.toFloat() * weightC + weightB * this.previousTriangle.verticeXA.toFloat() + weightA * this.previousTriangle.verticeXC.toFloat()).toInt()
					@Pc(716) val previousY: Int = (this.previousTriangle.verticeYA.toFloat() * weightB + this.previousTriangle.verticeYC.toFloat() * weightA + this.previousTriangle.verticeYB.toFloat() * weightC).toInt()
					@Pc(738) val previousZ: Int = (this.previousTriangle.verticeZB.toFloat() * weightC + this.previousTriangle.verticeZC.toFloat() * weightA + this.previousTriangle.verticeZA.toFloat() * weightB).toInt()
					@Pc(742) val deltaX: Int = spawnX - previousX
					@Pc(747) val deltaY: Int = spawnY - previousY
					@Pc(752) val deltaZ: Int = spawnZ - previousZ
					@Pc(761) val x: Int = (deltaX.toDouble() * Math.random() + previousX.toDouble()).toInt()
					@Pc(770) val y: Int = (previousY.toDouble() + deltaY.toDouble() * Math.random()).toInt()
					@Pc(779) val z: Int = (previousZ.toDouble() + deltaZ.toDouble() * Math.random()).toInt()
					@Pc(796) val speed: Int = (Math.random() * (this.type!!.maximumSpeed - this.type!!.minimumSpeed).toDouble()).toInt() + this.type!!.minimumSpeed
					@Pc(813) val lifetime: Int = this.type!!.minimumLifetime + ((this.type!!.maximumLifetime - this.type!!.minimumLifetime).toDouble() * Math.random()).toInt()
					@Pc(829) val size: Int = ((this.type!!.maximumSize - this.type!!.minimumSize).toDouble() * Math.random()).toInt() + this.type!!.minimumSize
					@Pc(891) val color: Int
					if (this.type!!.uniformColorVariance) {
						@Pc(895) val colourRandom: Double = Math.random()
						color = (colourRandom * this.type!!.blueVariance.toDouble() + this.type!!.minimumStartB.toDouble()).toInt() or ((this.type!!.redVariance.toDouble() * colourRandom + this.type!!.minimumStartR.toDouble()).toInt() shl 16) or ((this.type!!.minimumStartG.toDouble() + this.type!!.greenVariance.toDouble() * colourRandom).toInt() shl 8) or ((this.type!!.minimumStartA.toDouble() + this.type!!.alphaVariance.toDouble() * Math.random()).toInt() shl 24)
					} else {
						color = ((this.type!!.alphaVariance.toDouble() * Math.random() + this.type!!.minimumStartA.toDouble()).toInt() shl 24) or ((Math.random() * this.type!!.redVariance.toDouble() + this.type!!.minimumStartR.toDouble()).toInt() shl 16) or ((Math.random() * this.type!!.greenVariance.toDouble() + this.type!!.minimumStartG.toDouble()).toInt() shl 8) or (this.type!!.blueVariance.toDouble() * Math.random() + this.type!!.minimumStartB.toDouble()).toInt()
					}
					@Pc(957) var materialId: Int = this.type!!.materialId
					if (!toolkit!!.hasTextures() && !this.type!!.isTextured) {
						materialId = -1
					}
					if (ParticleSystemManager.particleCacheCursorPos == ParticleSystemManager.killIndex) {
						Particle(this, x, y, z, directionX, directionY, directionZ, speed, lifetime, color, size, materialId, this.type!!.unusedOpcode26, this.type!!.adjustsLightIntensity)
					} else {
						@Pc(973) val movingParticle: Particle? = ParticleSystemManager.particleCache[ParticleSystemManager.killIndex]
						ParticleSystemManager.killIndex = (ParticleSystemManager.killIndex + 1) and 0x3FF
						movingParticle!!.reset(this, x, y, z, directionX, directionY, directionZ, speed, lifetime, color, size, materialId, this.type!!.unusedOpcode26, this.type!!.adjustsLightIntensity)
					}
					particleIndex++
				}
			}
		}
		if (!this.triangle.hasSameCenter(this.previousTriangle)) {
			@Pc(1041) val particleEmitterRelated: ParticleEmitterRelated = this.previousTriangle
			this.previousTriangle = this.triangle
			this.triangle = particleEmitterRelated
			this.triangle.verticeYC = this.model!!.verticeXC
			this.triangle.verticeXC = this.model.verticeZC
			this.triangle.verticeYA = this.model.verticeXA
			this.triangle.verticeZB = this.model.verticeYC
			this.triangle.verticeXB = this.model.verticeYB
			this.triangle.verticeZA = this.model.verticeZB
			this.triangle.verticeZC = this.model.verticeYA
			this.triangle.centerX = this.previousTriangle.centerX
			this.triangle.verticeYB = this.model.transformedCY
			this.triangle.centerZ = this.previousTriangle.centerZ
			this.triangle.centerY = this.previousTriangle.centerY
			this.triangle.verticeXA = this.model.verticeZA
		}
		this.particleCount = 0
		@Pc(1131) var movingParticle2: Particle? = this.particles.getFirst() as Particle?
		while (movingParticle2 != null) {
			movingParticle2.tick(time, elapsedTime)
			this.particleCount++
			movingParticle2 = this.particles.getNext() as Particle?
		}
		ParticleSystemManager.renderedParticleCount += this.particleCount
	}
}
