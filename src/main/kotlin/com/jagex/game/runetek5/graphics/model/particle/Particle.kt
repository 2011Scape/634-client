package com.jagex.game.runetek5.graphics.model.particle

import com.jagex.game.runetek5.cache.node.lru.ParticleEffector
import com.jagex.game.runetek5.entity.ParticleSystem
import com.jagex.game.runetek5.entity.InteractiveEntityContainer
import com.jagex.game.runetek5.scene.tile.Tile
import com.jagex.game.runetek5.cache.node.IntNode
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.config.particle.ParticleType
import com.jagex.game.runetek5.config.effectortype.ParticleEffectorTypeList
import com.jagex.game.runetek5.config.particle.ParticleProducerType
import com.jagex.game.runetek5.scene.ground.Ground
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.entity.ParticleEmitter
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.client.input.camera.Camera
import com.jagex.game.runetek5.scene.Scene

@OriginalClass("client!ps")
class Particle : PointEntity {

	@OriginalMember(owner = "client!ps", name = "u", descriptor = "S")
	private var producerParentSize: Short = 0

	@OriginalMember(owner = "client!ps", name = "v", descriptor = "I")
	private var colourFraction: Int = 0

	@OriginalMember(owner = "client!ps", name = "x", descriptor = "Lclient!rba;")
	@JvmField
	var producer: ParticleEmitter? = null

	@OriginalMember(owner = "client!ps", name = "y", descriptor = "S")
	private var remainingLifetime: Short = 0

	@OriginalMember(owner = "client!ps", name = "w", descriptor = "S")
	private var lifetime: Short = 0

	@OriginalMember(owner = "client!ps", name = "C", descriptor = "S")
	private var directionX: Short = 0

	@OriginalMember(owner = "client!ps", name = "B", descriptor = "S")
	private var directionY: Short = 0

	@OriginalMember(owner = "client!ps", name = "A", descriptor = "S")
	private var directionZ: Short = 0

	@OriginalMember(owner = "client!ps", name = "z", descriptor = "I")
	private var speed: Int = 0

	@OriginalMember(owner = "client!ps", name = "<init>", descriptor = "(Lclient!rba;IIIIIIIIIIIZZ)V")
	constructor(@OriginalArg(0) particleEmitter: ParticleEmitter?, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) z: Int, @OriginalArg(4) directionX: Int, @OriginalArg(5) directionY: Int, @OriginalArg(6) directionZ: Int, @OriginalArg(7) speed: Int, @OriginalArg(8) lifetime: Int, @OriginalArg(9) colorArgb: Int, @OriginalArg(10) size: Int, @OriginalArg(11) materialId: Int, @OriginalArg(12) disableHdLighting: Boolean, @OriginalArg(13) lightIntensityAdjustment: Boolean) : super() {
		this.producer = particleEmitter
		this.x = x shl 12
		this.y = y shl 12
		this.z = z shl 12
		this.colorArgb = colorArgb
		this.remainingLifetime = lifetime.toShort()
		this.lifetime = this.remainingLifetime
		this.size = size
		this.materialId = materialId
		this.lightIntensityAdjustment = lightIntensityAdjustment
		this.directionX = directionX.toShort()
		this.directionY = directionY.toShort()
		this.directionZ = directionZ.toShort()
		this.speed = speed
		this.priority = this.producer!!.model!!.priority
		this.link()
	}

	@OriginalMember(owner = "client!ps", name = "a", descriptor = "()V")
	fun remove() {
		this.producer!!.system!!.movingParticles[this.producerParentSize.toInt()] = null
		ParticleSystemManager.particleCache!![ParticleSystemManager.particleCacheCursorPos] = this
		ParticleSystemManager.particleCacheCursorPos = (ParticleSystemManager.particleCacheCursorPos + 1) and 0x3FF
		this.unlinkNode()
		this.unlink2()
	}

	@OriginalMember(owner = "client!ps", name = "b", descriptor = "()V")
	private fun link() {
		@Pc(4) val slotIndex: Int = this.producer!!.system!!.nextParticleSlot
		if (this.producer!!.system!!.movingParticles[slotIndex] != null) {
			this.producer!!.system!!.movingParticles[slotIndex]!!.remove()
		}
		this.producer!!.system!!.movingParticles[slotIndex] = this
		this.producerParentSize = this.producer!!.system!!.nextParticleSlot.toShort()
		this.producer!!.system!!.nextParticleSlot = (slotIndex + 1) and 0x1FFF
		this.producer!!.particles.offer(this)
	}

	@OriginalMember(owner = "client!ps", name = "a", descriptor = "(Lclient!ha;J)V")
	fun collide(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(1) time: Long) {
		@Pc(6) val tileX: Int = this.x shr (Scene.tileUnits + 12)
		@Pc(13) val tileZ: Int = this.z shr (Scene.tileUnits + 12)
		@Pc(18) val worldY: Int = this.y shr 12
		if (worldY > 0 || worldY < -262144 || tileX < 0 || tileX >= Scene.sceneWidth || tileZ < 0 || tileZ >= Camera.sceneLength) {
			this.remove()
			return
		}
		@Pc(40) val system: ParticleSystem? = this.producer!!.system
		@Pc(44) val type: ParticleProducerType? = this.producer!!.type
		@Pc(46) val grounds: Array<Ground?>? = Scene.aClass65Array1
		@Pc(49) var level: Int = system!!.level
		@Pc(58) var tile: Tile? = Scene.aClass100ArrayArrayArray3!![system.level]!![tileX]!![tileZ]
		if (tile != null) {
			level = tile.plane.toInt()
		}
		@Pc(71) val height: Int = grounds!![level]!!.getHeight(tileZ, tileX)
		@Pc(86) val heightAbove: Int
		if (level < Scene.length - 1) {
			heightAbove = grounds[level + 1]!!.getHeight(tileZ, tileX)
		} else {
			heightAbove = height - (0x8 shl Scene.tileUnits)
		}
		if (type!!.adjustsToScene) {
			if (type.lowestDisplayPlane == -1 && worldY > height) {
				this.remove()
				return
			}
			if (type.lowestDisplayPlane >= 0 && worldY > grounds[type.lowestDisplayPlane]!!.getHeight(tileZ, tileX)) {
				this.remove()
				return
			}
			if (type.highestDisplayPlane == -1 && worldY < heightAbove) {
				this.remove()
				return
			}
			if (type.highestDisplayPlane >= 0 && worldY < grounds[type.highestDisplayPlane + 1]!!.getHeight(tileZ, tileX)) {
				this.remove()
				return
			}
		}
		@Pc(154) var particleLevel: Int
		particleLevel = Scene.length - 1
		while (particleLevel > 0 && worldY > grounds[particleLevel]!!.getHeight(tileZ, tileX)) {
			particleLevel--
		}
		if (type.killAboveSurface && particleLevel == 0 && worldY > grounds[0]!!.getHeight(tileZ, tileX)) {
			this.remove()
		} else if (particleLevel == Scene.length - 1 && grounds[particleLevel]!!.getHeight(tileZ, tileX) - worldY > (0x8 shl Scene.tileUnits)) {
			this.remove()
		} else {
			tile = Scene.aClass100ArrayArrayArray3!![particleLevel]!![tileX]!![tileZ]
			@Pc(261) var local261: Int
			if (tile == null) {
				if (particleLevel == 0 || Scene.aClass100ArrayArrayArray3!![0]!![tileX]!![tileZ] == null) {
					val newTile: Tile = Tile(0)
					Scene.aClass100ArrayArrayArray3!![0]!![tileX]!![tileZ] = newTile
					tile = newTile
				}
				@Pc(251) val bridge: Boolean = Scene.aClass100ArrayArrayArray3!![0]!![tileX]!![tileZ]!!.bridge != null
				if (particleLevel == 3 && bridge) {
					this.remove()
					return
				}
				var plane: Int = 1
				while (plane <= particleLevel) {
					if (Scene.aClass100ArrayArrayArray3!![plane]!![tileX]!![tileZ] == null) {
						val newTile: Tile = Tile(plane)
						Scene.aClass100ArrayArrayArray3!![plane]!![tileX]!![tileZ] = newTile
						tile = newTile
						if (bridge) {
							tile.plane++
						}
					}
					plane++
				}
			}
			if (type.killOverlapping) {
				@Pc(304) val worldX: Int = this.x shr 12
				val worldZ: Int = this.z shr 12
				@Pc(318) var cylinder: Silhouette?
				if (tile!!.primaryWall != null) {
					cylinder = tile.primaryWall!!.getCylinder(toolkit)
					if (cylinder != null && cylinder.contains(worldZ, worldY, worldX)) {
						this.remove()
						return
					}
				}
				if (tile.secondaryWall != null) {
					cylinder = tile.secondaryWall!!.getCylinder(toolkit)
					if (cylinder != null && cylinder.contains(worldZ, worldY, worldX)) {
						this.remove()
						return
					}
				}
				if (tile.groundDecoration != null) {
					cylinder = tile.groundDecoration!!.getCylinder(toolkit)
					if (cylinder != null && cylinder.contains(worldZ, worldY, worldX)) {
						this.remove()
						return
					}
				}
				@Pc(375) var node: InteractiveEntityContainer? = tile.interactives
				while (node != null) {
					@Pc(382) val entityCylinder: Silhouette? = node.interactiveObject!!.getCylinder(toolkit)
					if (entityCylinder != null && entityCylinder.contains(worldZ, worldY, worldX)) {
						this.remove()
						return
					}
					node = node.child
				}
			}
			system.list.particles.insert(this)
		}
	}

	@OriginalMember(owner = "client!ps", name = "a", descriptor = "(Lclient!rba;IIIIIIIIIIIZZ)V")
	fun reset(@OriginalArg(0) particleEmitter: ParticleEmitter?, @OriginalArg(1) x: Int, @OriginalArg(2) y: Int, @OriginalArg(3) z: Int, @OriginalArg(4) directionX: Int, @OriginalArg(5) directionY: Int, @OriginalArg(6) directionZ: Int, @OriginalArg(7) speed: Int, @OriginalArg(8) lifetime: Int, @OriginalArg(9) colour: Int, @OriginalArg(10) size: Int, @OriginalArg(11) texture: Int, @OriginalArg(12) disableHdLighting: Boolean, @OriginalArg(13) preserveAmbient: Boolean) {
		this.producer = particleEmitter
		this.x = x shl 12
		this.y = y shl 12
		this.z = z shl 12
		this.colorArgb = colour
		this.remainingLifetime = lifetime.toShort()
		this.lifetime = this.remainingLifetime
		this.size = size
		this.materialId = texture
		this.lightIntensityAdjustment = preserveAmbient
		this.directionX = directionX.toShort()
		this.directionY = directionY.toShort()
		this.directionZ = directionZ.toShort()
		this.speed = speed
		this.priority = this.producer!!.model!!.priority
		this.link()
	}

	@OriginalMember(owner = "client!ps", name = "a", descriptor = "(JI)V")
	fun tick(@OriginalArg(0) time: Long, @OriginalArg(1) elapsedTime: Int) {
		this.remainingLifetime = (this.remainingLifetime - elapsedTime).toShort()
		if (this.remainingLifetime <= 0) {
			this.remove()
			return
		}
		@Pc(17) val worldX: Int = this.x shr 12
		@Pc(22) val worldY: Int = this.y shr 12
		@Pc(27) val worldZ: Int = this.z shr 12
		@Pc(31) val system: ParticleSystem? = this.producer!!.system
		@Pc(35) val type: ParticleProducerType? = this.producer!!.type
		if (type!!.faceColorRgb != 0) {
			@Pc(65) var channel: Int
			if (this.lifetime - this.remainingLifetime <= type.colorFadeStart) {
				channel = ((this.colorArgb shr 8) and 0xFF00) + ((this.colourFraction shr 16) and 0xFF) + type.redFadeStep * elapsedTime
				@Pc(82) var green: Int = (this.colorArgb and 0xFF00) + ((this.colourFraction shr 8) and 0xFF) + type.greenFadeStep * elapsedTime
				@Pc(99) var blue: Int = ((this.colorArgb and 0xFF) shl 8) + (this.colourFraction and 0xFF) + type.blueFadeStep * elapsedTime
				if (channel < 0) {
					channel = 0
				} else if (channel > 65535) {
					channel = 65535
				}
				if (green < 0) {
					green = 0
				} else if (green > 65535) {
					green = 65535
				}
				if (blue < 0) {
					blue = 0
				} else if (blue > 65535) {
					blue = 65535
				}
				this.colorArgb = this.colorArgb and 0xFF000000.toInt()
				this.colorArgb = this.colorArgb or (((channel and 0xFF00) shl 8) + (green and 0xFF00) + ((blue shr 8) and 0xFF))
				this.colourFraction = this.colourFraction and 0xFF000000.toInt()
				this.colourFraction = this.colourFraction or (((channel and 0xFF) shl 16) + ((green and 0xFF) shl 8) + (blue and 0xFF))
			}
			if (this.lifetime - this.remainingLifetime <= type.alphaFadeStart) {
				channel = ((this.colorArgb shr 16) and 0xFF00) + ((this.colourFraction shr 24) and 0xFF) + type.alphaFadeStep * elapsedTime
				if (channel < 0) {
					channel = 0
				} else if (channel > 65535) {
					channel = 65535
				}
				this.colorArgb = this.colorArgb and 0xFFFFFF
				this.colorArgb = this.colorArgb or ((channel and 0xFF00) shl 16)
				this.colourFraction = this.colourFraction and 0xFFFFFF
				this.colourFraction = this.colourFraction or ((channel and 0xFF) shl 24)
			}
		}
		if (type.endSpeed != -1 && this.lifetime - this.remainingLifetime <= type.startSpeedChange) {
			this.speed += type.speedStep * elapsedTime
		}
		if (type.endSize != -1 && this.lifetime - this.remainingLifetime <= type.startSizeChange) {
			this.size += type.sizeChangeStep * elapsedTime
		}
		@Pc(296) var velocityX: Double = this.directionX.toDouble()
		@Pc(300) var velocityY: Double = this.directionY.toDouble()
		@Pc(304) var velocityZ: Double = this.directionZ.toDouble()
		@Pc(306) var velocityChanged: Boolean = false
		@Pc(317) var local317: Int
		@Pc(324) var emitterDeltaY: Int
		@Pc(331) var emitterDeltaZ: Int
		@Pc(348) var emitterDistance: Int
		@Pc(356) var drag: Long
		if (type.speedUpdateType == 1) {
			val emitterDeltaX: Int = worldX - this.producer!!.triangle.centerX
			emitterDeltaY = worldY - this.producer!!.triangle.centerY
			emitterDeltaZ = worldZ - this.producer!!.triangle.centerZ
			emitterDistance = Math.sqrt((emitterDeltaX * emitterDeltaX + emitterDeltaY * emitterDeltaY + emitterDeltaZ * emitterDeltaZ).toDouble()).toInt() shr 2
			drag = (type.speedFallOffStep * emitterDistance * elapsedTime).toLong()
			this.speed = (this.speed.toLong() - ((this.speed.toLong() * drag) shr 18)).toInt()
		} else if (type.speedUpdateType == 2) {
			val emitterDeltaX: Int = worldX - this.producer!!.triangle.centerX
			emitterDeltaY = worldY - this.producer!!.triangle.centerY
			emitterDeltaZ = worldZ - this.producer!!.triangle.centerZ
			emitterDistance = emitterDeltaX * emitterDeltaX + emitterDeltaY * emitterDeltaY + emitterDeltaZ * emitterDeltaZ
			drag = (type.speedFallOffStep * emitterDistance * elapsedTime).toLong()
			this.speed = (this.speed.toLong() - ((this.speed.toLong() * drag) shr 28)).toInt()
		}
		if (type.generalEffectors != null) {
			@Pc(437) val sentinel: Node? = system!!.effectorCache.sentinel
			@Pc(440) var node: Node? = sentinel!!.next
			while (node !== sentinel) {
				@Pc(444) val effector: ParticleEffector? = node as ParticleEffector?
				@Pc(447) val effectorType: ParticleType? = effector!!.type
				if (effectorType!!.particleHandlingType != 1) {
					@Pc(453) var matched: Boolean = false
					@Pc(455) var i: Int = 0
					while (i < type.generalEffectors!!.size) {
						if (type.generalEffectors!![i] == effectorType.partileFileId) {
							matched = true
							break
						}
						i++
					}
					if (matched) {
						@Pc(480) val deltaX: Double = (worldX - effector.verticeZ).toDouble()
						@Pc(486) val deltaY: Double = (worldY - effector.verticeX).toDouble()
						@Pc(492) val deltaZ: Double = (worldZ - effector.verticeY).toDouble()
						@Pc(504) val distanceSquared: Double = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ
						if (!(distanceSquared > effectorType.uid.toDouble())) {
							@Pc(513) var distance: Double = Math.sqrt(distanceSquared)
							if (distance == 0.0) {
								distance = 1.0
							}
							@Pc(545) val cosine: Double = (deltaX * effector.directionX.toDouble() + deltaY * effectorType.offsetY.toDouble() + deltaZ * effector.directionZ.toDouble()) * 65535.0 / (effectorType.size3d.toDouble() * distance)
							if (!(cosine < effectorType.zan.toDouble())) {
								@Pc(553) var falloff: Double = 0.0
								if (effectorType.effectType == 1) {
									falloff = distance / 16.0 * effectorType.sizeMultiplier.toDouble()
								} else if (effectorType.effectType == 2) {
									falloff = distance / 16.0 * (distance / 16.0) * effectorType.sizeMultiplier.toDouble()
								}
								if (effectorType.verticeCalculationType != 0) {
									@Pc(678) val forceX: Double = deltaX / distance * effectorType.size3d.toDouble()
									@Pc(686) val forceY: Double = deltaY / distance * effectorType.size3d.toDouble()
									@Pc(694) val forceZ: Double = deltaZ / distance * effectorType.size3d.toDouble()
									if (effectorType.currentOffset == 0) {
										velocityX += forceX * elapsedTime.toDouble()
										velocityY += forceY * elapsedTime.toDouble()
										velocityZ += forceZ * elapsedTime.toDouble()
										velocityChanged = true
									} else {
										this.x = (this.x.toDouble() + forceX * elapsedTime.toDouble()).toInt()
										this.y = (this.y.toDouble() + forceY * elapsedTime.toDouble()).toInt()
										this.z = (this.z.toDouble() + forceZ * elapsedTime.toDouble()).toInt()
									}
								} else if (effectorType.currentOffset == 0) {
									velocityX += (effector.directionX.toDouble() - falloff) * elapsedTime.toDouble()
									velocityY += (effectorType.offsetY.toDouble() - falloff) * elapsedTime.toDouble()
									velocityZ += (effector.directionZ.toDouble() - falloff) * elapsedTime.toDouble()
									velocityChanged = true
								} else {
									this.x = (this.x.toDouble() + (effector.directionX.toDouble() - falloff) * elapsedTime.toDouble()).toInt()
									this.y = (this.y.toDouble() + (effectorType.offsetY.toDouble() - falloff) * elapsedTime.toDouble()).toInt()
									this.z = (this.z.toDouble() + (effector.directionZ.toDouble() - falloff) * elapsedTime.toDouble()).toInt()
								}
							}
						}
					}
				}
				node = node!!.next
			}
		}
		if (type.effectiveVertexUids != null) {
			var vertexUidIndex: Int = 0
			while (vertexUidIndex < type.effectiveVertexUids!!.size) {
				@Pc(776) var particleEffector: ParticleEffector? = ParticleSystemManager.activeEffectiveVertexEffects.get(type.effectiveVertexUids!![vertexUidIndex].toLong()) as ParticleEffector?
				while (particleEffector != null) {
					@Pc(780) val particleEffectorType: ParticleType? = particleEffector.type
					@Pc(786) val vertexDeltaX: Double = (worldX - particleEffector.verticeZ).toDouble()
					@Pc(792) val vertexDeltaY: Double = (worldY - particleEffector.verticeX).toDouble()
					@Pc(798) val vertexDeltaZ: Double = (worldZ - particleEffector.verticeY).toDouble()
					@Pc(810) val vertexDistanceSquared: Double = vertexDeltaX * vertexDeltaX + vertexDeltaY * vertexDeltaY + vertexDeltaZ * vertexDeltaZ
					if (vertexDistanceSquared > particleEffectorType!!.uid.toDouble()) {
						particleEffector = ParticleSystemManager.activeEffectiveVertexEffects.nextInBucket() as ParticleEffector?
					} else {
						@Pc(825) var vertexDistance: Double = Math.sqrt(vertexDistanceSquared)
						if (vertexDistance == 0.0) {
							vertexDistance = 1.0
						}
						@Pc(857) val vertexCosine: Double = (vertexDeltaX * particleEffector.directionX.toDouble() + vertexDeltaY * particleEffectorType.offsetY.toDouble() + vertexDeltaZ * particleEffector.directionZ.toDouble()) * 65535.0 / (particleEffectorType.size3d.toDouble() * vertexDistance)
						if (vertexCosine < particleEffectorType.zan.toDouble()) {
							particleEffector = ParticleSystemManager.activeEffectiveVertexEffects.nextInBucket() as ParticleEffector?
						} else {
							@Pc(871) var vertexFalloff: Double = 0.0
							if (particleEffectorType.effectType == 1) {
								vertexFalloff = vertexDistance / 16.0 * particleEffectorType.sizeMultiplier.toDouble()
							} else if (particleEffectorType.effectType == 2) {
								vertexFalloff = vertexDistance / 16.0 * (vertexDistance / 16.0) * particleEffectorType.sizeMultiplier.toDouble()
							}
							if (particleEffectorType.verticeCalculationType != 0) {
								@Pc(996) val vertexForceX: Double = vertexDeltaX / vertexDistance * particleEffectorType.size3d.toDouble()
								@Pc(1004) val vertexForceY: Double = vertexDeltaY / vertexDistance * particleEffectorType.size3d.toDouble()
								@Pc(1012) val vertexForceZ: Double = vertexDeltaZ / vertexDistance * particleEffectorType.size3d.toDouble()
								if (particleEffectorType.currentOffset == 0) {
									velocityX += vertexForceX * elapsedTime.toDouble()
									velocityY += vertexForceY * elapsedTime.toDouble()
									velocityZ += vertexForceZ * elapsedTime.toDouble()
									velocityChanged = true
								} else {
									this.x = (this.x.toDouble() + vertexForceX * elapsedTime.toDouble()).toInt()
									this.y = (this.y.toDouble() + vertexForceY * elapsedTime.toDouble()).toInt()
									this.z = (this.z.toDouble() + vertexForceZ * elapsedTime.toDouble()).toInt()
								}
							} else if (particleEffectorType.currentOffset == 0) {
								velocityX += (particleEffector.directionX.toDouble() - vertexFalloff) * elapsedTime.toDouble()
								velocityY += (particleEffectorType.offsetY.toDouble() - vertexFalloff) * elapsedTime.toDouble()
								velocityZ += (particleEffector.directionZ.toDouble() - vertexFalloff) * elapsedTime.toDouble()
								velocityChanged = true
							} else {
								this.x = (this.x.toDouble() + (particleEffector.directionX.toDouble() - vertexFalloff) * elapsedTime.toDouble()).toInt()
								this.y = (this.y.toDouble() + (particleEffectorType.offsetY.toDouble() - vertexFalloff) * elapsedTime.toDouble()).toInt()
								this.z = (this.z.toDouble() + (particleEffector.directionZ.toDouble() - vertexFalloff) * elapsedTime.toDouble()).toInt()
							}
							particleEffector = ParticleSystemManager.activeEffectiveVertexEffects.nextInBucket() as ParticleEffector?
						}
					}
				}
				vertexUidIndex++
			}
		}
		if (type.localEffectors != null) {
			if (type.copiedParticleFileIds == null) {
				type.copiedParticleFileIds = IntArray(type.localEffectors!!.size)
				var localEffectorIndex: Int = 0
				while (localEffectorIndex < type.localEffectors!!.size) {
					ParticleEffectorTypeList.list(type.localEffectors!![localEffectorIndex])
					type.copiedParticleFileIds!![localEffectorIndex] = (ParticleType.particleTypeCache.get(type.localEffectors!![localEffectorIndex].toLong()) as IntNode?)!!.value
					localEffectorIndex++
				}
			}
			var localEffectorIndex: Int = 0
			while (localEffectorIndex < type.copiedParticleFileIds!!.size) {
				@Pc(1137) val particleEffectorType2: ParticleType? = ParticleType.particleTypes[type.copiedParticleFileIds!![localEffectorIndex]]
				if (particleEffectorType2!!.currentOffset == 0) {
					velocityX += (particleEffectorType2.offsetX * elapsedTime).toDouble()
					velocityY += (particleEffectorType2.offsetY * elapsedTime).toDouble()
					velocityZ += (particleEffectorType2.offsetZ * elapsedTime).toDouble()
					velocityChanged = true
				} else {
					this.x += particleEffectorType2.offsetX * elapsedTime
					this.y += particleEffectorType2.offsetY * elapsedTime
					this.z += particleEffectorType2.offsetZ * elapsedTime
				}
				localEffectorIndex++
			}
		}
		if (velocityChanged) {
			while (true) {
				if (!(velocityX > 32767.0) && !(velocityY > 32767.0) && !(velocityZ > 32767.0) && !(velocityX < -32767.0) && !(velocityY < -32767.0) && !(velocityZ < -32767.0)) {
					this.directionX = velocityX.toInt().toShort()
					this.directionY = velocityY.toInt().toShort()
					this.directionZ = velocityZ.toInt().toShort()
					break
				}
				velocityX /= 2.0
				velocityY /= 2.0
				velocityZ /= 2.0
				this.speed = this.speed shl 0x1
			}
		}
		this.x = (this.x.toLong() + ((this.directionX.toLong() * (this.speed shl 2).toLong()) shr 23) * elapsedTime.toLong()).toInt()
		this.y = (this.y.toLong() + ((this.directionY.toLong() * (this.speed shl 2).toLong()) shr 23) * elapsedTime.toLong()).toInt()
		this.z = (this.z.toLong() + ((this.directionZ.toLong() * (this.speed shl 2).toLong()) shr 23) * elapsedTime.toLong()).toInt()
	}
}
