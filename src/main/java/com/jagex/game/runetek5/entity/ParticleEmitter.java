package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.entity.ParticleSystem;
import com.jagex.game.runetek5.entity.EntityNodeList;
import com.jagex.game.runetek5.entity.EntityNode;
import com.jagex.game.runetek5.graphics.model.particle.Particle;
import com.jagex.game.runetek5.config.particle.ParticleProducerType;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle;
import com.jagex.game.runetek5.graphics.particles.ParticleEmitterRelated;
import com.jagex.game.runetek5.graphics.model.particle.ParticleSystemManager;

@OriginalClass("client!rba")
public final class ParticleEmitter extends EntityNode {

	@OriginalMember(owner = "client!rba", name = "w", descriptor = "I")
	public int particleCount;

	@OriginalMember(owner = "client!rba", name = "B", descriptor = "I")
	private int baseRotationY;

	@OriginalMember(owner = "client!rba", name = "D", descriptor = "I")
	private int randomisedRotationOffsetY;

	@OriginalMember(owner = "client!rba", name = "E", descriptor = "I")
	private int movementDirectionX;

	@OriginalMember(owner = "client!rba", name = "F", descriptor = "I")
	private int randomisedRotationOffsetX;

	@OriginalMember(owner = "client!rba", name = "G", descriptor = "I")
	private int movementDirectionY;

	@OriginalMember(owner = "client!rba", name = "H", descriptor = "I")
	private int movementDirectionZ;

	@OriginalMember(owner = "client!rba", name = "I", descriptor = "I")
	private int baseRotationX;

	@OriginalMember(owner = "client!rba", name = "j", descriptor = "I")
	private int spawnAccumulator = 0;

	@OriginalMember(owner = "client!rba", name = "v", descriptor = "Z")
	public boolean inactive = false;

	@OriginalMember(owner = "client!rba", name = "t", descriptor = "Lclient!vha;")
	public ParticleEmitterRelated triangle = new ParticleEmitterRelated();

	@OriginalMember(owner = "client!rba", name = "y", descriptor = "Lclient!vha;")
	private ParticleEmitterRelated previousTriangle = new ParticleEmitterRelated();

	@OriginalMember(owner = "client!rba", name = "C", descriptor = "Z")
	private boolean finished = false;

	@OriginalMember(owner = "client!rba", name = "k", descriptor = "J")
	private final long startTime;

	@OriginalMember(owner = "client!rba", name = "l", descriptor = "Lclient!nn;")
	public final EmissiveTriangle model;

	@OriginalMember(owner = "client!rba", name = "o", descriptor = "Lclient!uj;")
	public final ParticleSystem system;

	@OriginalMember(owner = "client!rba", name = "q", descriptor = "Lclient!qb;")
	public ParticleProducerType type;

	@OriginalMember(owner = "client!rba", name = "i", descriptor = "Lclient!tca;")
	public final EntityNodeList particles;

	@OriginalMember(owner = "client!rba", name = "<init>", descriptor = "(Lclient!ha;Lclient!nn;Lclient!uj;J)V")
	public ParticleEmitter(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) EmissiveTriangle modelParticleEmitter, @OriginalArg(2) ParticleSystem particleSystem, @OriginalArg(3) long startTime) {
		this.startTime = startTime;
		this.model = modelParticleEmitter;
		this.system = particleSystem;
		this.type = this.model.getProducer();
		if (!toolkit.hasTextures() && this.type.nonTexturedProducerId != -1) {
			this.type = ParticleProducerType.get(this.type.nonTexturedProducerId);
		}
		this.particles = new EntityNodeList();
		this.spawnAccumulator = (int) ((double) this.spawnAccumulator + Math.random() * 64.0D);
		this.updateTriangle();
		this.previousTriangle.verticeXB = this.triangle.verticeXB;
		this.previousTriangle.verticeYB = this.triangle.verticeYB;
		this.previousTriangle.verticeXA = this.triangle.verticeXA;
		this.previousTriangle.verticeXC = this.triangle.verticeXC;
		this.previousTriangle.verticeYA = this.triangle.verticeYA;
		this.previousTriangle.verticeYC = this.triangle.verticeYC;
		this.previousTriangle.verticeZC = this.triangle.verticeZC;
		this.previousTriangle.verticeZB = this.triangle.verticeZB;
		this.previousTriangle.verticeZA = this.triangle.verticeZA;
	}

	@OriginalMember(owner = "client!rba", name = "b", descriptor = "(Z)V")
	public void updateTriangle() {
		this.triangle.verticeYA = this.model.verticeXA;
		this.triangle.verticeZC = this.model.verticeYA;
		this.triangle.verticeXA = this.model.verticeZA;
		this.triangle.verticeYB = this.model.transformedCY;
		this.triangle.verticeXB = this.model.verticeYB;
		this.triangle.verticeZA = this.model.verticeZB;
		this.triangle.verticeYC = this.model.verticeXC;
		this.triangle.verticeZB = this.model.verticeYC;
		this.triangle.verticeXC = this.model.verticeZC;
		if (this.triangle.verticeXC == this.triangle.verticeXA && this.triangle.verticeXB == this.triangle.verticeXA && this.triangle.verticeYC == this.triangle.verticeYA && this.triangle.verticeYA == this.triangle.verticeYB && this.triangle.verticeZA == this.triangle.verticeZC && this.triangle.verticeZA == this.triangle.verticeZB) {
			this.finished = true;
		} else if (this.finished) {
			this.previousTriangle.verticeYC = this.triangle.verticeYC;
			this.finished = false;
			this.previousTriangle.verticeZB = this.triangle.verticeZB;
			this.previousTriangle.verticeZC = this.triangle.verticeZC;
			this.previousTriangle.verticeYA = this.triangle.verticeYA;
			this.previousTriangle.verticeXA = this.triangle.verticeXA;
			this.previousTriangle.verticeZA = this.triangle.verticeZA;
			this.previousTriangle.verticeXC = this.triangle.verticeXC;
			this.previousTriangle.verticeYB = this.triangle.verticeYB;
			this.previousTriangle.verticeXB = this.triangle.verticeXB;
		}
	}

	@OriginalMember(owner = "client!rba", name = "a", descriptor = "(ZLclient!ha;J)V")
	public void processCycle(@OriginalArg(1) Toolkit toolkit, @OriginalArg(2) long time) {
		for (@Pc(17) Particle movingParticle = (Particle) this.particles.getFirst(); movingParticle != null; movingParticle = (Particle) this.particles.getNext()) {
			movingParticle.collide(toolkit, time);
		}
	}

	@OriginalMember(owner = "client!rba", name = "a", descriptor = "(Lclient!ha;IJIZ)V")
	public void performUpdate(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int elapsedTime, @OriginalArg(2) long time, @OriginalArg(4) boolean emit) {
		@Pc(33) int local33;
		if (this.inactive) {
			emit = false;
		} else if (ParticleSystemManager.detailsSetting < this.type.minimumSetting) {
			emit = false;
		} else if (ParticleSystemManager.lastRenderedParticleCount > ParticleSystemManager.PARTICLE_COUNT_LIMITS[ParticleSystemManager.detailsSetting]) {
			emit = false;
		} else if (this.finished) {
			emit = false;
		} else if (this.type.lifetime != -1) {
			int emitterAge = (int) (time - this.startTime);
			if (this.type.periodic || this.type.lifetime >= emitterAge) {
				emitterAge %= this.type.lifetime;
			} else {
				emit = false;
			}
			if (!this.type.activeFirst && emitterAge < this.type.emissionEndTime) {
				emit = false;
			}
			if (this.type.activeFirst && this.type.emissionEndTime <= emitterAge) {
				emit = false;
			}
		}
		if (emit) {
			ParticleSystemManager.emitterCount++;
			int centerX = (this.triangle.verticeXC + this.triangle.verticeXA + this.triangle.verticeXB) / 3;
			@Pc(128) int centerY = (this.triangle.verticeYB + this.triangle.verticeYC + this.triangle.verticeYA) / 3;
			@Pc(142) int centerZ = (this.triangle.verticeZB + this.triangle.verticeZA + this.triangle.verticeZC) / 3;
			@Pc(186) int local186;
			@Pc(195) int local195;
			@Pc(204) int local204;
			@Pc(212) int local212;
			@Pc(220) int local220;
			@Pc(229) int local229;
			@Pc(330) int local330;
			@Pc(380) int local380;
			@Pc(401) int local401;
			if (this.triangle.centerX != centerX || centerY != this.triangle.centerY || this.triangle.centerZ != centerZ) {
				this.triangle.centerY = centerY;
				this.triangle.centerX = centerX;
				this.triangle.centerZ = centerZ;
				int edgeAX = this.triangle.verticeXA - this.triangle.verticeXC;
				int edgeAY = this.triangle.verticeYA - this.triangle.verticeYC;
				int edgeAZ = this.triangle.verticeZA - this.triangle.verticeZC;
				int edgeBX = this.triangle.verticeXB - this.triangle.verticeXC;
				int edgeBY = this.triangle.verticeYB - this.triangle.verticeYC;
				int edgeBZ = this.triangle.verticeZB - this.triangle.verticeZC;
				this.movementDirectionY = edgeBX * edgeAZ - edgeAX * edgeBZ;
				this.movementDirectionX = edgeAY * edgeBZ - edgeBY * edgeAZ;
				this.movementDirectionZ = edgeBY * edgeAX - edgeBX * edgeAY;
				while (true) {
					if (this.movementDirectionX <= 32767 && this.movementDirectionY <= 32767 && this.movementDirectionZ <= 32767 && this.movementDirectionX >= -32767 && this.movementDirectionY >= -32767 && this.movementDirectionZ >= -32767) {
						int magnitude = (int) Math.sqrt((double) (this.movementDirectionX * this.movementDirectionX + this.movementDirectionY * this.movementDirectionY + this.movementDirectionZ * this.movementDirectionZ));
						if (magnitude <= 0) {
							magnitude = 1;
						}
						this.movementDirectionX = this.movementDirectionX * 32767 / magnitude;
						this.movementDirectionZ = this.movementDirectionZ * 32767 / magnitude;
						this.movementDirectionY = this.movementDirectionY * 32767 / magnitude;
						if (this.type.maximalAngleH > 0 || this.type.maximalAngleV > 0) {
							int directionAngleH = (int) (Math.atan2((double) this.movementDirectionZ, (double) this.movementDirectionX) * 2607.5945876176133D);
							int directionAngleV = (int) (Math.atan2((double) this.movementDirectionY, Math.sqrt((double) (this.movementDirectionZ * this.movementDirectionZ + this.movementDirectionX * this.movementDirectionX))) * 2607.5945876176133D);
							this.randomisedRotationOffsetX = this.type.maximalAngleH - this.type.minimalAngleH;
							this.baseRotationX = this.type.minimalAngleH + directionAngleH - (this.randomisedRotationOffsetX >> 1);
							this.randomisedRotationOffsetY = this.type.maximalAngleV - this.type.minimumAngleV;
							this.baseRotationY = directionAngleV + this.type.minimumAngleV - (this.randomisedRotationOffsetY >> 1);
						}
						break;
					}
					this.movementDirectionZ >>= 0x1;
					this.movementDirectionY >>= 0x1;
					this.movementDirectionX >>= 0x1;
				}
			}
			this.spawnAccumulator += (int) (((double) (this.type.maximumParticleRate - this.type.minimumParticleRate) * Math.random() + (double) this.type.minimumParticleRate) * (double) elapsedTime);
			if (this.spawnAccumulator > 63) {
				int spawnCount = this.spawnAccumulator >> 6;
				this.spawnAccumulator &= 0x3F;
				for (int particleIndex = 0; particleIndex < spawnCount; particleIndex++) {
					@Pc(555) int local555;
					@Pc(559) int local559;
					int directionX;
					int directionY;
					int directionZ;
					if (this.type.maximalAngleH <= 0 && this.type.maximalAngleV <= 0) {
						directionX = this.movementDirectionX;
						directionZ = this.movementDirectionZ;
						directionY = this.movementDirectionY;
					} else {
						int angleH = (int) (Math.random() * (double) this.randomisedRotationOffsetX) + this.baseRotationX;
						angleH &= 0x3FFF;
						int sinH = TextureCombineSource.SINE[angleH];
						int cosH = TextureCombineSource.COSINE[angleH];
						int angleV = this.baseRotationY + (int) ((double) this.randomisedRotationOffsetY * Math.random());
						angleV &= 0x1FFF;
						int sinV = TextureCombineSource.SINE[angleV];
						int cosV = TextureCombineSource.COSINE[angleV];
						directionX = sinV * cosH >> 13;
						directionY = (cosV << 1) * -1;
						directionZ = sinV * sinH >> 13;
					}
					@Pc(582) float weightA = (float) Math.random();
					@Pc(585) float weightB = (float) Math.random();
					if (weightA + weightB > 1.0F) {
						weightB = 1.0F - weightB;
						weightA = 1.0F - weightA;
					}
					@Pc(606) float weightC = 1.0F - weightA - weightB;
					int spawnX = (int) ((float) this.triangle.verticeXB * weightC + weightA * (float) this.triangle.verticeXC + weightB * (float) this.triangle.verticeXA);
					int spawnY = (int) (weightA * (float) this.triangle.verticeYC + (float) this.triangle.verticeYA * weightB + (float) this.triangle.verticeYB * weightC);
					int spawnZ = (int) ((float) this.triangle.verticeZC * weightA + (float) this.triangle.verticeZA * weightB + weightC * (float) this.triangle.verticeZB);
					@Pc(694) int previousX = (int) ((float) this.previousTriangle.verticeXB * weightC + weightB * (float) this.previousTriangle.verticeXA + weightA * (float) this.previousTriangle.verticeXC);
					@Pc(716) int previousY = (int) ((float) this.previousTriangle.verticeYA * weightB + (float) this.previousTriangle.verticeYC * weightA + (float) this.previousTriangle.verticeYB * weightC);
					@Pc(738) int previousZ = (int) ((float) this.previousTriangle.verticeZB * weightC + (float) this.previousTriangle.verticeZC * weightA + (float) this.previousTriangle.verticeZA * weightB);
					@Pc(742) int deltaX = spawnX - previousX;
					@Pc(747) int deltaY = spawnY - previousY;
					@Pc(752) int deltaZ = spawnZ - previousZ;
					@Pc(761) int x = (int) ((double) deltaX * Math.random() + (double) previousX);
					@Pc(770) int y = (int) ((double) previousY + (double) deltaY * Math.random());
					@Pc(779) int z = (int) ((double) previousZ + (double) deltaZ * Math.random());
					@Pc(796) int speed = (int) (Math.random() * (double) (this.type.maximumSpeed - this.type.minimumSpeed)) + this.type.minimumSpeed;
					@Pc(813) int lifetime = this.type.minimumLifetime + (int) ((double) (this.type.maximumLifetime - this.type.minimumLifetime) * Math.random());
					@Pc(829) int size = (int) ((double) (this.type.maximumSize - this.type.minimumSize) * Math.random()) + this.type.minimumSize;
					@Pc(891) int color;
					if (this.type.uniformColorVariance) {
						@Pc(895) double colourRandom = Math.random();
						color = (int) (colourRandom * (double) this.type.blueVariance + (double) this.type.minimumStartB) | (int) ((double) this.type.redVariance * colourRandom + (double) this.type.minimumStartR) << 16 | (int) ((double) this.type.minimumStartG + (double) this.type.greenVariance * colourRandom) << 8 | (int) ((double) this.type.minimumStartA + (double) this.type.alphaVariance * Math.random()) << 24;
					} else {
						color = (int) ((double) this.type.alphaVariance * Math.random() + (double) this.type.minimumStartA) << 24 | (int) (Math.random() * (double) this.type.redVariance + (double) this.type.minimumStartR) << 16 | (int) (Math.random() * (double) this.type.greenVariance + (double) this.type.minimumStartG) << 8 | (int) ((double) this.type.blueVariance * Math.random() + (double) this.type.minimumStartB);
					}
					@Pc(957) int materialId = this.type.materialId;
					if (!toolkit.hasTextures() && !this.type.isTextured) {
						materialId = -1;
					}
					if (ParticleSystemManager.particleCacheCursorPos == ParticleSystemManager.killIndex) {
						new Particle(this, x, y, z, directionX, directionY, directionZ, speed, lifetime, color, size, materialId, this.type.unusedOpcode26, this.type.adjustsLightIntensity);
					} else {
						@Pc(973) Particle movingParticle = ParticleSystemManager.particleCache[ParticleSystemManager.killIndex];
						ParticleSystemManager.killIndex = ParticleSystemManager.killIndex + 1 & 0x3FF;
						movingParticle.reset(this, x, y, z, directionX, directionY, directionZ, speed, lifetime, color, size, materialId, this.type.unusedOpcode26, this.type.adjustsLightIntensity);
					}
				}
			}
		}
		if (!this.triangle.hasSameCenter(this.previousTriangle)) {
			@Pc(1041) ParticleEmitterRelated particleEmitterRelated = this.previousTriangle;
			this.previousTriangle = this.triangle;
			this.triangle = particleEmitterRelated;
			this.triangle.verticeYC = this.model.verticeXC;
			this.triangle.verticeXC = this.model.verticeZC;
			this.triangle.verticeYA = this.model.verticeXA;
			this.triangle.verticeZB = this.model.verticeYC;
			this.triangle.verticeXB = this.model.verticeYB;
			this.triangle.verticeZA = this.model.verticeZB;
			this.triangle.verticeZC = this.model.verticeYA;
			this.triangle.centerX = this.previousTriangle.centerX;
			this.triangle.verticeYB = this.model.transformedCY;
			this.triangle.centerZ = this.previousTriangle.centerZ;
			this.triangle.centerY = this.previousTriangle.centerY;
			this.triangle.verticeXA = this.model.verticeZA;
		}
		this.particleCount = 0;
		for (@Pc(1131) Particle movingParticle2 = (Particle) this.particles.getFirst(); movingParticle2 != null; movingParticle2 = (Particle) this.particles.getNext()) {
			movingParticle2.tick(time, elapsedTime);
			this.particleCount++;
		}
		ParticleSystemManager.renderedParticleCount += this.particleCount;
	}
}
