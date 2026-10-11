package com.jagex.game.runetek5.graphics.model.particle;

import com.jagex.game.runetek5.cache.node.lru.ParticleEffector;
import com.jagex.game.runetek5.entity.ParticleSystem;
import com.jagex.game.runetek5.entity.InteractiveEntityContainer;
import com.jagex.game.runetek5.scene.tile.Tile;
import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.config.particle.ParticleType;
import com.jagex.game.runetek5.config.effectortype.ParticleEffectorTypeList;
import com.jagex.game.runetek5.config.particle.ParticleProducerType;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.entity.ParticleEmitter;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.scene.Scene;

@OriginalClass("client!ps")
public final class Particle extends PointEntity {

	@OriginalMember(owner = "client!ps", name = "u", descriptor = "S")
	private short producerParentSize;

	@OriginalMember(owner = "client!ps", name = "v", descriptor = "I")
	private int colourFraction;

	@OriginalMember(owner = "client!ps", name = "x", descriptor = "Lclient!rba;")
	public ParticleEmitter producer;

	@OriginalMember(owner = "client!ps", name = "y", descriptor = "S")
	private short remainingLifetime;

	@OriginalMember(owner = "client!ps", name = "w", descriptor = "S")
	private short lifetime;

	@OriginalMember(owner = "client!ps", name = "C", descriptor = "S")
	private short directionX;

	@OriginalMember(owner = "client!ps", name = "B", descriptor = "S")
	private short directionY;

	@OriginalMember(owner = "client!ps", name = "A", descriptor = "S")
	private short directionZ;

	@OriginalMember(owner = "client!ps", name = "z", descriptor = "I")
	private int speed;

	@OriginalMember(owner = "client!ps", name = "<init>", descriptor = "(Lclient!rba;IIIIIIIIIIIZZ)V")
	public Particle(@OriginalArg(0) ParticleEmitter particleEmitter, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int z, @OriginalArg(4) int directionX, @OriginalArg(5) int directionY, @OriginalArg(6) int directionZ, @OriginalArg(7) int speed, @OriginalArg(8) int lifetime, @OriginalArg(9) int colorArgb, @OriginalArg(10) int size, @OriginalArg(11) int materialId, @OriginalArg(12) boolean disableHdLighting, @OriginalArg(13) boolean lightIntensityAdjustment) {
		this.producer = particleEmitter;
		super.x = x << 12;
		super.y = y << 12;
		super.z = z << 12;
		super.colorArgb = colorArgb;
		this.lifetime = this.remainingLifetime = (short) lifetime;
		super.size = size;
		super.materialId = materialId;
		super.lightIntensityAdjustment = lightIntensityAdjustment;
		this.directionX = (short) directionX;
		this.directionY = (short) directionY;
		this.directionZ = (short) directionZ;
		this.speed = speed;
		super.priority = this.producer.model.priority;
		this.link();
	}

	@OriginalMember(owner = "client!ps", name = "a", descriptor = "()V")
	public void remove() {
		this.producer.system.movingParticles[this.producerParentSize] = null;
		ParticleSystemManager.particleCache[ParticleSystemManager.particleCacheCursorPos] = this;
		ParticleSystemManager.particleCacheCursorPos = ParticleSystemManager.particleCacheCursorPos + 1 & 0x3FF;
		this.unlinkNode();
		this.unlink2();
	}

	@OriginalMember(owner = "client!ps", name = "b", descriptor = "()V")
	private void link() {
		@Pc(4) int slotIndex = this.producer.system.nextParticleSlot;
		if (this.producer.system.movingParticles[slotIndex] != null) {
			this.producer.system.movingParticles[slotIndex].remove();
		}
		this.producer.system.movingParticles[slotIndex] = this;
		this.producerParentSize = (short) this.producer.system.nextParticleSlot;
		this.producer.system.nextParticleSlot = slotIndex + 1 & 0x1FFF;
		this.producer.particles.offer(this);
	}

	@OriginalMember(owner = "client!ps", name = "a", descriptor = "(Lclient!ha;J)V")
	public void collide(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) long time) {
		@Pc(6) int tileX = super.x >> Scene.tileUnits + 12;
		@Pc(13) int tileZ = super.z >> Scene.tileUnits + 12;
		@Pc(18) int worldY = super.y >> 12;
		if (worldY > 0 || worldY < -262144 || tileX < 0 || tileX >= Scene.sceneWidth || tileZ < 0 || tileZ >= Camera.sceneLength) {
			this.remove();
			return;
		}
		@Pc(40) ParticleSystem system = this.producer.system;
		@Pc(44) ParticleProducerType type = this.producer.type;
		@Pc(46) Ground[] grounds = Scene.aClass65Array1;
		@Pc(49) int level = system.level;
		@Pc(58) Tile tile = Scene.aClass100ArrayArrayArray3[system.level][tileX][tileZ];
		if (tile != null) {
			level = tile.plane;
		}
		@Pc(71) int height = grounds[level].getHeight(tileZ, tileX);
		@Pc(86) int heightAbove;
		if (level < Scene.length - 1) {
			heightAbove = grounds[level + 1].getHeight(tileZ, tileX);
		} else {
			heightAbove = height - (0x8 << Scene.tileUnits);
		}
		if (type.adjustsToScene) {
			if (type.lowestDisplayPlane == -1 && worldY > height) {
				this.remove();
				return;
			}
			if (type.lowestDisplayPlane >= 0 && worldY > grounds[type.lowestDisplayPlane].getHeight(tileZ, tileX)) {
				this.remove();
				return;
			}
			if (type.highestDisplayPlane == -1 && worldY < heightAbove) {
				this.remove();
				return;
			}
			if (type.highestDisplayPlane >= 0 && worldY < grounds[type.highestDisplayPlane + 1].getHeight(tileZ, tileX)) {
				this.remove();
				return;
			}
		}
		@Pc(154) int particleLevel;
		for (particleLevel = Scene.length - 1; particleLevel > 0 && worldY > grounds[particleLevel].getHeight(tileZ, tileX); particleLevel--) {
		}
		if (type.killAboveSurface && particleLevel == 0 && worldY > grounds[0].getHeight(tileZ, tileX)) {
			this.remove();
		} else if (particleLevel == Scene.length - 1 && grounds[particleLevel].getHeight(tileZ, tileX) - worldY > 0x8 << Scene.tileUnits) {
			this.remove();
		} else {
			tile = Scene.aClass100ArrayArrayArray3[particleLevel][tileX][tileZ];
			@Pc(261) int local261;
			if (tile == null) {
				if (particleLevel == 0 || Scene.aClass100ArrayArrayArray3[0][tileX][tileZ] == null) {
					tile = Scene.aClass100ArrayArrayArray3[0][tileX][tileZ] = new Tile(0);
				}
				@Pc(251) boolean bridge = Scene.aClass100ArrayArrayArray3[0][tileX][tileZ].bridge != null;
				if (particleLevel == 3 && bridge) {
					this.remove();
					return;
				}
				for (int plane = 1; plane <= particleLevel; plane++) {
					if (Scene.aClass100ArrayArrayArray3[plane][tileX][tileZ] == null) {
						tile = Scene.aClass100ArrayArrayArray3[plane][tileX][tileZ] = new Tile(plane);
						if (bridge) {
							tile.plane++;
						}
					}
				}
			}
			if (type.killOverlapping) {
				@Pc(304) int worldX = super.x >> 12;
				int worldZ = super.z >> 12;
				@Pc(318) Silhouette cylinder;
				if (tile.primaryWall != null) {
					cylinder = tile.primaryWall.getCylinder(toolkit);
					if (cylinder != null && cylinder.contains(worldZ, worldY, worldX)) {
						this.remove();
						return;
					}
				}
				if (tile.secondaryWall != null) {
					cylinder = tile.secondaryWall.getCylinder(toolkit);
					if (cylinder != null && cylinder.contains(worldZ, worldY, worldX)) {
						this.remove();
						return;
					}
				}
				if (tile.groundDecoration != null) {
					cylinder = tile.groundDecoration.getCylinder(toolkit);
					if (cylinder != null && cylinder.contains(worldZ, worldY, worldX)) {
						this.remove();
						return;
					}
				}
				for (@Pc(375) InteractiveEntityContainer node = tile.interactives; node != null; node = node.child) {
					@Pc(382) Silhouette entityCylinder = node.interactiveObject.getCylinder(toolkit);
					if (entityCylinder != null && entityCylinder.contains(worldZ, worldY, worldX)) {
						this.remove();
						return;
					}
				}
			}
			system.list.particles.insert(this);
		}
	}

	@OriginalMember(owner = "client!ps", name = "a", descriptor = "(Lclient!rba;IIIIIIIIIIIZZ)V")
	public void reset(@OriginalArg(0) ParticleEmitter particleEmitter, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int z, @OriginalArg(4) int directionX, @OriginalArg(5) int directionY, @OriginalArg(6) int directionZ, @OriginalArg(7) int speed, @OriginalArg(8) int lifetime, @OriginalArg(9) int colour, @OriginalArg(10) int size, @OriginalArg(11) int texture, @OriginalArg(12) boolean disableHdLighting, @OriginalArg(13) boolean preserveAmbient) {
		this.producer = particleEmitter;
		super.x = x << 12;
		super.y = y << 12;
		super.z = z << 12;
		super.colorArgb = colour;
		this.lifetime = this.remainingLifetime = (short) lifetime;
		super.size = size;
		super.materialId = texture;
		super.lightIntensityAdjustment = preserveAmbient;
		this.directionX = (short) directionX;
		this.directionY = (short) directionY;
		this.directionZ = (short) directionZ;
		this.speed = speed;
		super.priority = this.producer.model.priority;
		this.link();
	}

	@OriginalMember(owner = "client!ps", name = "a", descriptor = "(JI)V")
	public void tick(@OriginalArg(0) long time, @OriginalArg(1) int elapsedTime) {
		this.remainingLifetime = (short) (this.remainingLifetime - elapsedTime);
		if (this.remainingLifetime <= 0) {
			this.remove();
			return;
		}
		@Pc(17) int worldX = super.x >> 12;
		@Pc(22) int worldY = super.y >> 12;
		@Pc(27) int worldZ = super.z >> 12;
		@Pc(31) ParticleSystem system = this.producer.system;
		@Pc(35) ParticleProducerType type = this.producer.type;
		if (type.faceColorRgb != 0) {
			@Pc(65) int channel;
			if (this.lifetime - this.remainingLifetime <= type.colorFadeStart) {
				channel = (super.colorArgb >> 8 & 0xFF00) + (this.colourFraction >> 16 & 0xFF) + type.redFadeStep * elapsedTime;
				@Pc(82) int green = (super.colorArgb & 0xFF00) + (this.colourFraction >> 8 & 0xFF) + type.greenFadeStep * elapsedTime;
				@Pc(99) int blue = ((super.colorArgb & 0xFF) << 8) + (this.colourFraction & 0xFF) + type.blueFadeStep * elapsedTime;
				if (channel < 0) {
					channel = 0;
				} else if (channel > 65535) {
					channel = 65535;
				}
				if (green < 0) {
					green = 0;
				} else if (green > 65535) {
					green = 65535;
				}
				if (blue < 0) {
					blue = 0;
				} else if (blue > 65535) {
					blue = 65535;
				}
				super.colorArgb &= 0xFF000000;
				super.colorArgb |= ((channel & 0xFF00) << 8) + (green & 0xFF00) + (blue >> 8 & 0xFF);
				this.colourFraction &= 0xFF000000;
				this.colourFraction |= ((channel & 0xFF) << 16) + ((green & 0xFF) << 8) + (blue & 0xFF);
			}
			if (this.lifetime - this.remainingLifetime <= type.alphaFadeStart) {
				channel = (super.colorArgb >> 16 & 0xFF00) + (this.colourFraction >> 24 & 0xFF) + type.alphaFadeStep * elapsedTime;
				if (channel < 0) {
					channel = 0;
				} else if (channel > 65535) {
					channel = 65535;
				}
				super.colorArgb &= 0xFFFFFF;
				super.colorArgb |= (channel & 0xFF00) << 16;
				this.colourFraction &= 0xFFFFFF;
				this.colourFraction |= (channel & 0xFF) << 24;
			}
		}
		if (type.endSpeed != -1 && this.lifetime - this.remainingLifetime <= type.startSpeedChange) {
			this.speed += type.speedStep * elapsedTime;
		}
		if (type.endSize != -1 && this.lifetime - this.remainingLifetime <= type.startSizeChange) {
			super.size += type.sizeChangeStep * elapsedTime;
		}
		@Pc(296) double velocityX = (double) this.directionX;
		@Pc(300) double velocityY = (double) this.directionY;
		@Pc(304) double velocityZ = (double) this.directionZ;
		@Pc(306) boolean velocityChanged = false;
		@Pc(317) int local317;
		@Pc(324) int emitterDeltaY;
		@Pc(331) int emitterDeltaZ;
		@Pc(348) int emitterDistance;
		@Pc(356) long drag;
		if (type.speedUpdateType == 1) {
			int emitterDeltaX = worldX - this.producer.triangle.centerX;
			emitterDeltaY = worldY - this.producer.triangle.centerY;
			emitterDeltaZ = worldZ - this.producer.triangle.centerZ;
			emitterDistance = (int) Math.sqrt((double) (emitterDeltaX * emitterDeltaX + emitterDeltaY * emitterDeltaY + emitterDeltaZ * emitterDeltaZ)) >> 2;
			drag = type.speedFallOffStep * emitterDistance * elapsedTime;
			this.speed = (int) ((long) this.speed - ((long) this.speed * drag >> 18));
		} else if (type.speedUpdateType == 2) {
			int emitterDeltaX = worldX - this.producer.triangle.centerX;
			emitterDeltaY = worldY - this.producer.triangle.centerY;
			emitterDeltaZ = worldZ - this.producer.triangle.centerZ;
			emitterDistance = emitterDeltaX * emitterDeltaX + emitterDeltaY * emitterDeltaY + emitterDeltaZ * emitterDeltaZ;
			drag = type.speedFallOffStep * emitterDistance * elapsedTime;
			this.speed = (int) ((long) this.speed - ((long) this.speed * drag >> 28));
		}
		if (type.generalEffectors != null) {
			@Pc(437) Node sentinel = system.effectorCache.sentinel;
			for (@Pc(440) Node node = sentinel.next; node != sentinel; node = node.next) {
				@Pc(444) ParticleEffector effector = (ParticleEffector) node;
				@Pc(447) ParticleType effectorType = effector.type;
				if (effectorType.particleHandlingType != 1) {
					@Pc(453) boolean matched = false;
					for (@Pc(455) int i = 0; i < type.generalEffectors.length; i++) {
						if (type.generalEffectors[i] == effectorType.partileFileId) {
							matched = true;
							break;
						}
					}
					if (matched) {
						@Pc(480) double deltaX = (double) (worldX - effector.verticeZ);
						@Pc(486) double deltaY = (double) (worldY - effector.verticeX);
						@Pc(492) double deltaZ = (double) (worldZ - effector.verticeY);
						@Pc(504) double distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
						if (!(distanceSquared > (double) effectorType.uid)) {
							@Pc(513) double distance = Math.sqrt(distanceSquared);
							if (distance == 0.0D) {
								distance = 1.0D;
							}
							@Pc(545) double cosine = (deltaX * (double) effector.directionX + deltaY * (double) effectorType.offsetY + deltaZ * (double) effector.directionZ) * 65535.0D / ((double) effectorType.size3d * distance);
							if (!(cosine < (double) effectorType.zan)) {
								@Pc(553) double falloff = 0.0D;
								if (effectorType.effectType == 1) {
									falloff = distance / 16.0D * (double) effectorType.sizeMultiplier;
								} else if (effectorType.effectType == 2) {
									falloff = distance / 16.0D * (distance / 16.0D) * (double) effectorType.sizeMultiplier;
								}
								if (effectorType.verticeCalculationType != 0) {
									@Pc(678) double forceX = deltaX / distance * (double) effectorType.size3d;
									@Pc(686) double forceY = deltaY / distance * (double) effectorType.size3d;
									@Pc(694) double forceZ = deltaZ / distance * (double) effectorType.size3d;
									if (effectorType.currentOffset == 0) {
										velocityX += forceX * (double) elapsedTime;
										velocityY += forceY * (double) elapsedTime;
										velocityZ += forceZ * (double) elapsedTime;
										velocityChanged = true;
									} else {
										super.x = (int) ((double) super.x + forceX * (double) elapsedTime);
										super.y = (int) ((double) super.y + forceY * (double) elapsedTime);
										super.z = (int) ((double) super.z + forceZ * (double) elapsedTime);
									}
								} else if (effectorType.currentOffset == 0) {
									velocityX += ((double) effector.directionX - falloff) * (double) elapsedTime;
									velocityY += ((double) effectorType.offsetY - falloff) * (double) elapsedTime;
									velocityZ += ((double) effector.directionZ - falloff) * (double) elapsedTime;
									velocityChanged = true;
								} else {
									super.x = (int) ((double) super.x + ((double) effector.directionX - falloff) * (double) elapsedTime);
									super.y = (int) ((double) super.y + ((double) effectorType.offsetY - falloff) * (double) elapsedTime);
									super.z = (int) ((double) super.z + ((double) effector.directionZ - falloff) * (double) elapsedTime);
								}
							}
						}
					}
				}
			}
		}
		if (type.effectiveVertexUids != null) {
			for (int vertexUidIndex = 0; vertexUidIndex < type.effectiveVertexUids.length; vertexUidIndex++) {
				@Pc(776) ParticleEffector particleEffector = (ParticleEffector) ParticleSystemManager.activeEffectiveVertexEffects.get((long) type.effectiveVertexUids[vertexUidIndex]);
				while (particleEffector != null) {
					@Pc(780) ParticleType particleEffectorType = particleEffector.type;
					@Pc(786) double vertexDeltaX = (double) (worldX - particleEffector.verticeZ);
					@Pc(792) double vertexDeltaY = (double) (worldY - particleEffector.verticeX);
					@Pc(798) double vertexDeltaZ = (double) (worldZ - particleEffector.verticeY);
					@Pc(810) double vertexDistanceSquared = vertexDeltaX * vertexDeltaX + vertexDeltaY * vertexDeltaY + vertexDeltaZ * vertexDeltaZ;
					if (vertexDistanceSquared > (double) particleEffectorType.uid) {
						particleEffector = (ParticleEffector) ParticleSystemManager.activeEffectiveVertexEffects.nextInBucket();
					} else {
						@Pc(825) double vertexDistance = Math.sqrt(vertexDistanceSquared);
						if (vertexDistance == 0.0D) {
							vertexDistance = 1.0D;
						}
						@Pc(857) double vertexCosine = (vertexDeltaX * (double) particleEffector.directionX + vertexDeltaY * (double) particleEffectorType.offsetY + vertexDeltaZ * (double) particleEffector.directionZ) * 65535.0D / ((double) particleEffectorType.size3d * vertexDistance);
						if (vertexCosine < (double) particleEffectorType.zan) {
							particleEffector = (ParticleEffector) ParticleSystemManager.activeEffectiveVertexEffects.nextInBucket();
						} else {
							@Pc(871) double vertexFalloff = 0.0D;
							if (particleEffectorType.effectType == 1) {
								vertexFalloff = vertexDistance / 16.0D * (double) particleEffectorType.sizeMultiplier;
							} else if (particleEffectorType.effectType == 2) {
								vertexFalloff = vertexDistance / 16.0D * (vertexDistance / 16.0D) * (double) particleEffectorType.sizeMultiplier;
							}
							if (particleEffectorType.verticeCalculationType != 0) {
								@Pc(996) double vertexForceX = vertexDeltaX / vertexDistance * (double) particleEffectorType.size3d;
								@Pc(1004) double vertexForceY = vertexDeltaY / vertexDistance * (double) particleEffectorType.size3d;
								@Pc(1012) double vertexForceZ = vertexDeltaZ / vertexDistance * (double) particleEffectorType.size3d;
								if (particleEffectorType.currentOffset == 0) {
									velocityX += vertexForceX * (double) elapsedTime;
									velocityY += vertexForceY * (double) elapsedTime;
									velocityZ += vertexForceZ * (double) elapsedTime;
									velocityChanged = true;
								} else {
									super.x = (int) ((double) super.x + vertexForceX * (double) elapsedTime);
									super.y = (int) ((double) super.y + vertexForceY * (double) elapsedTime);
									super.z = (int) ((double) super.z + vertexForceZ * (double) elapsedTime);
								}
							} else if (particleEffectorType.currentOffset == 0) {
								velocityX += ((double) particleEffector.directionX - vertexFalloff) * (double) elapsedTime;
								velocityY += ((double) particleEffectorType.offsetY - vertexFalloff) * (double) elapsedTime;
								velocityZ += ((double) particleEffector.directionZ - vertexFalloff) * (double) elapsedTime;
								velocityChanged = true;
							} else {
								super.x = (int) ((double) super.x + ((double) particleEffector.directionX - vertexFalloff) * (double) elapsedTime);
								super.y = (int) ((double) super.y + ((double) particleEffectorType.offsetY - vertexFalloff) * (double) elapsedTime);
								super.z = (int) ((double) super.z + ((double) particleEffector.directionZ - vertexFalloff) * (double) elapsedTime);
							}
							particleEffector = (ParticleEffector) ParticleSystemManager.activeEffectiveVertexEffects.nextInBucket();
						}
					}
				}
			}
		}
		if (type.localEffectors != null) {
			if (type.copiedParticleFileIds == null) {
				type.copiedParticleFileIds = new int[type.localEffectors.length];
				for (int localEffectorIndex = 0; localEffectorIndex < type.localEffectors.length; localEffectorIndex++) {
					ParticleEffectorTypeList.list(type.localEffectors[localEffectorIndex]);
					type.copiedParticleFileIds[localEffectorIndex] = ((IntNode) ParticleType.particleTypeCache.get((long) type.localEffectors[localEffectorIndex])).value;
				}
			}
			for (int localEffectorIndex = 0; localEffectorIndex < type.copiedParticleFileIds.length; localEffectorIndex++) {
				@Pc(1137) ParticleType particleEffectorType2 = ParticleType.particleTypes[type.copiedParticleFileIds[localEffectorIndex]];
				if (particleEffectorType2.currentOffset == 0) {
					velocityX += particleEffectorType2.offsetX * elapsedTime;
					velocityY += particleEffectorType2.offsetY * elapsedTime;
					velocityZ += particleEffectorType2.offsetZ * elapsedTime;
					velocityChanged = true;
				} else {
					super.x += particleEffectorType2.offsetX * elapsedTime;
					super.y += particleEffectorType2.offsetY * elapsedTime;
					super.z += particleEffectorType2.offsetZ * elapsedTime;
				}
			}
		}
		if (velocityChanged) {
			while (true) {
				if (!(velocityX > 32767.0D) && !(velocityY > 32767.0D) && !(velocityZ > 32767.0D) && !(velocityX < -32767.0D) && !(velocityY < -32767.0D) && !(velocityZ < -32767.0D)) {
					this.directionX = (short) velocityX;
					this.directionY = (short) velocityY;
					this.directionZ = (short) velocityZ;
					break;
				}
				velocityX /= 2.0D;
				velocityY /= 2.0D;
				velocityZ /= 2.0D;
				this.speed <<= 0x1;
			}
		}
		super.x = (int) ((long) super.x + ((long) this.directionX * (long) (this.speed << 2) >> 23) * (long) elapsedTime);
		super.y = (int) ((long) super.y + ((long) this.directionY * (long) (this.speed << 2) >> 23) * (long) elapsedTime);
		super.z = (int) ((long) super.z + ((long) this.directionZ * (long) (this.speed << 2) >> 23) * (long) elapsedTime);
	}
}
