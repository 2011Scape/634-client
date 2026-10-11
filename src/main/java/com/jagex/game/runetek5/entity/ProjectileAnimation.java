package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.config.bas.BasType;
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationType;
import com.jagex.game.runetek5.graphics.model.particle.Silhouette;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex;
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.ParticleList;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.cache.node.lru.ProjectileAnimationNode;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.OrthoMode;

@OriginalClass("client!to")
public final class ProjectileAnimation extends AbstractInteractiveEntity {

	@OriginalMember(owner = "client!to", name = "X", descriptor = "D")
	private double translateZ;

	@OriginalMember(owner = "client!to", name = "eb", descriptor = "Lclient!uj;")
	private ParticleSystem particleSystem;

	@OriginalMember(owner = "client!to", name = "jb", descriptor = "D")
	private double verticalAcceleration;

	@OriginalMember(owner = "client!to", name = "kb", descriptor = "D")
	private double translateY;

	@OriginalMember(owner = "client!to", name = "mb", descriptor = "I")
	private int rotateY;

	@OriginalMember(owner = "client!to", name = "wb", descriptor = "I")
	private int rotateX;

	@OriginalMember(owner = "client!to", name = "Ab", descriptor = "D")
	private double horizontalSpeed;

	@OriginalMember(owner = "client!to", name = "Bb", descriptor = "D")
	private double speedX;

	@OriginalMember(owner = "client!to", name = "Ib", descriptor = "D")
	private double translateX;

	@OriginalMember(owner = "client!to", name = "Jb", descriptor = "D")
	private double verticalSpeed;

	@OriginalMember(owner = "client!to", name = "Ob", descriptor = "D")
	private double speedZ;

	@OriginalMember(owner = "client!to", name = "V", descriptor = "I")
	private int nextFrame = -1;

	@OriginalMember(owner = "client!to", name = "I", descriptor = "I")
	private int frame = 0;

	@OriginalMember(owner = "client!to", name = "Db", descriptor = "I")
	private int frameCycle = 0;

	@OriginalMember(owner = "client!to", name = "Hb", descriptor = "Z")
	private boolean ticked = false;

	@OriginalMember(owner = "client!to", name = "zb", descriptor = "Z")
	private boolean transparent = false;

	@OriginalMember(owner = "client!to", name = "Kb", descriptor = "I")
	private int sphereRadius = 0;

	@OriginalMember(owner = "client!to", name = "U", descriptor = "I")
	private int minY = 0;

	@OriginalMember(owner = "client!to", name = "tb", descriptor = "I")
	private final int displacement;

	@OriginalMember(owner = "client!to", name = "T", descriptor = "I")
	private final int wornSlot;

	@OriginalMember(owner = "client!to", name = "Mb", descriptor = "I")
	public final int t1;

	@OriginalMember(owner = "client!to", name = "yb", descriptor = "Z")
	private final boolean groundRelative;

	@OriginalMember(owner = "client!to", name = "S", descriptor = "I")
	public final int entity2;

	@OriginalMember(owner = "client!to", name = "rb", descriptor = "I")
	private final int id;

	@OriginalMember(owner = "client!to", name = "sb", descriptor = "I")
	public final int t2;

	@OriginalMember(owner = "client!to", name = "Nb", descriptor = "I")
	private final int y1;

	@OriginalMember(owner = "client!to", name = "hb", descriptor = "I")
	private final int verticalAngle;

	@OriginalMember(owner = "client!to", name = "Lb", descriptor = "I")
	public final int y2;

	@OriginalMember(owner = "client!to", name = "nb", descriptor = "I")
	private final int entity1;

	@OriginalMember(owner = "client!to", name = "bb", descriptor = "Lclient!hg;")
	private final SeqType seqType;

	@OriginalMember(owner = "client!to", name = "<init>", descriptor = "(IIIIIIIIIIIIIZI)V")
	public ProjectileAnimation(@OriginalArg(0) int id, @OriginalArg(1) int level, @OriginalArg(2) int virtualLevel, @OriginalArg(3) int x, @OriginalArg(4) int z, @OriginalArg(5) int startHeight, @OriginalArg(6) int startCycle, @OriginalArg(7) int endCycle, @OriginalArg(8) int verticalAngle, @OriginalArg(9) int displacement, @OriginalArg(10) int entity1, @OriginalArg(11) int targetIndex, @OriginalArg(12) int entity2, @OriginalArg(13) boolean groundRelative, @OriginalArg(14) int wornSlot) {
		super(level, virtualLevel, x, Scene.tileHeightForAbsolutePosition(x, level, z) - startHeight, z, x >> 9, x >> 9, z >> 9, z >> 9, false, (byte) 0);
		this.displacement = displacement;
		this.wornSlot = wornSlot;
		this.t1 = targetIndex;
		this.groundRelative = groundRelative;
		this.ticked = false;
		this.entity2 = entity2;
		this.id = id;
		this.t2 = endCycle;
		this.y1 = startHeight;
		this.verticalAngle = verticalAngle;
		this.y2 = startCycle;
		this.entity1 = entity1;
		@Pc(91) int seqId = TypeLists.SPOT_ANIM.list(this.id).sequenceId;
		if (seqId == -1) {
			this.seqType = null;
		} else {
			this.seqType = TypeLists.SEQ.list(seqId);
		}
	}

	@OriginalMember(owner = "client!to", name = "c", descriptor = "(Lclient!ha;I)V")
	@Override
	public void particleCycle(@OriginalArg(0) Toolkit toolkit) {
		@Pc(9) Model model = this.generateGfxModel(0, toolkit);
		if (model == null) {
			return;
		}
		@Pc(21) Matrix matrix = toolkit.scratchMatrix();
		matrix.makeRotationX(this.rotateX);
		matrix.rotateAxisY(this.rotateY);
		matrix.translate((int) this.translateX, (int) this.translateY, (int) this.translateZ);
		this.sphereRadius = model.fa();
		this.minY = model.ma();
		this.updateParticles(matrix, model, toolkit);
	}

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(BIIII)V")
	public void target(@OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int currentCycle, @OriginalArg(4) int x) {
		@Pc(9) double local9;
		if (!this.ticked) {
			double offsetX = x - super.x;
			@Pc(16) double offsetZ = (double) (z - super.z);
			@Pc(25) double distance = Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);
			this.translateX = (double) super.x + (double) this.displacement * offsetX / distance;
			this.translateZ = (double) this.displacement * offsetZ / distance + (double) super.z;
			if (this.groundRelative) {
				this.translateY = Scene.tileHeightForAbsolutePosition((int) this.translateX, super.level, (int) this.translateZ) - this.y1;
			} else {
				this.translateY = super.y;
			}
		}
		double remainingCycles = this.t2 + 1 - currentCycle;
		this.speedZ = ((double) z - this.translateZ) / remainingCycles;
		this.speedX = ((double) x - this.translateX) / remainingCycles;
		this.horizontalSpeed = Math.sqrt(this.speedX * this.speedX + this.speedZ * this.speedZ);
		if (this.verticalAngle == -1) {
			this.verticalSpeed = ((double) y - this.translateY) / remainingCycles;
		} else {
			if (!this.ticked) {
				this.verticalSpeed = -this.horizontalSpeed * Math.tan((double) this.verticalAngle * 0.02454369D);
			}
			this.verticalAcceleration = ((double) y - this.translateY - remainingCycles * this.verticalSpeed) * 2.0D / (remainingCycles * remainingCycles);
		}
	}

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	@Override
	public void shareLight(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int offsetY, @OriginalArg(2) boolean hideSharedFaces, @OriginalArg(3) Entity entity, @OriginalArg(4) int offsetZ, @OriginalArg(6) int offsetX) {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!to", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() {
		if (this.particleSystem != null) {
			this.particleSystem.stopped();
		}
	}

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(I)V")
	public void stopParticles() {
		if (this.particleSystem != null) {
			this.particleSystem.stopped();
		}
	}

	@OriginalMember(owner = "client!to", name = "j", descriptor = "(I)Z")
	@Override
	public boolean hasTileShadow() {
		return false;
	}

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(Lclient!bca;Lclient!ka;Lclient!ha;I)V")
	private void updateParticles(@OriginalArg(0) Matrix matrix, @OriginalArg(1) Model model, @OriginalArg(2) Toolkit toolkit) {
		model.updateParticlePosition(matrix);
		@Pc(9) EmissiveTriangle[] modelParticleEmitters = model.getEmissiveTriangles();
		@Pc(12) EffectiveVertex[] modelParticleEffectors = model.getEffectiveVertices();
		if ((this.particleSystem == null || this.particleSystem.removed) && (modelParticleEmitters != null || modelParticleEffectors != null)) {
			this.particleSystem = ParticleSystem.get(ClientState.clientCycle, true);
		}
		if (this.particleSystem != null) {
			this.particleSystem.update(toolkit, (long) ClientState.clientCycle, modelParticleEmitters, modelParticleEffectors);
			this.particleSystem.updateBounds(super.level, super.startX, super.endX, super.startZ, super.endZ);
		}
	}

	@OriginalMember(owner = "client!to", name = "i", descriptor = "(I)I")
	@Override
	public int getMinY(@OriginalArg(0) int check) {
		return check == -25675 ? this.minY : -92;
	}

	@OriginalMember(owner = "client!to", name = "c", descriptor = "(Z)I")
	@Override
	public int getModelMinY() {
		return this.sphereRadius;
	}

	@OriginalMember(owner = "client!to", name = "f", descriptor = "(B)V")
	public void cycle() {
		if (this.ticked || this.entity1 == 0) {
			return;
		}
		@Pc(19) PathingEntity pathingEntity = null;
		@Pc(31) int entityIndex;
		if (this.entity1 < 0) {
			entityIndex = -this.entity1 - 1;
			if (PlayerUpdate.myPlayerIndex == entityIndex) {
				pathingEntity = PlayerUpdate.localPlayer;
			} else {
				pathingEntity = PlayerUpdate.players[entityIndex];
			}
		} else {
			entityIndex = this.entity1 - 1;
			@Pc(58) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) entityIndex);
			if (npcEntityNode != null) {
				pathingEntity = npcEntityNode.npc;
			}
		}
		if (pathingEntity == null) {
			return;
		}
		super.z = pathingEntity.z;
		super.x = pathingEntity.x;
		super.y = Scene.tileHeightForAbsolutePosition(pathingEntity.x, super.level, pathingEntity.z) - this.y1;
		if (this.wornSlot < 0) {
			return;
		}
		@Pc(94) BasType basType = pathingEntity.getBasType();
		@Pc(96) int projectionX = 0;
		@Pc(98) int projectionZ = 0;
		if (basType.equipmentTransforms != null && basType.equipmentTransforms[this.wornSlot] != null) {
			projectionX = basType.equipmentTransforms[this.wornSlot][0];
			projectionZ = basType.equipmentTransforms[this.wornSlot][2];
		}
		if (basType.projectionOffset != null && basType.projectionOffset[this.wornSlot] != null) {
			projectionZ += basType.projectionOffset[this.wornSlot][2];
			projectionX += basType.projectionOffset[this.wornSlot][0];
		}
		if (projectionX != 0 || projectionZ != 0) {
			@Pc(171) int entityYaw = pathingEntity.yaw.getRotation();
			@Pc(173) int baseRotation = entityYaw;
			if (pathingEntity.wornRotation != null && pathingEntity.wornRotation[this.wornSlot] != -1) {
				baseRotation = pathingEntity.wornRotation[this.wornSlot];
			}
			@Pc(197) int relativeRotation = baseRotation - entityYaw & 0x3FFF;
			@Pc(201) int sin = TextureCombineSource.SINE[relativeRotation];
			@Pc(205) int cos = TextureCombineSource.COSINE[relativeRotation];
			@Pc(215) int offsetX = projectionZ * sin + projectionX * cos >> 14;
			projectionZ = projectionZ * cos - projectionX * sin >> 14;
			super.x += offsetX;
			super.z += projectionZ;
		}
		return;
	}

	@OriginalMember(owner = "client!to", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	@Override
	public Silhouette getCylinder(@OriginalArg(0) Toolkit toolkit) {
		return null;
	}

	@OriginalMember(owner = "client!to", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	@Override
	public PickableEntity render(@OriginalArg(1) Toolkit toolkit) {
		@Pc(11) Model model = this.generateGfxModel(2048, toolkit);
		if (model == null) {
			return null;
		}
		@Pc(18) Matrix matrix = toolkit.scratchMatrix();
		matrix.makeRotationX(this.rotateX);
		matrix.rotateAxisY(this.rotateY);
		matrix.translate((int) this.translateX, (int) this.translateY, (int) this.translateZ);
		this.updateParticles(matrix, model, toolkit);
		@Pc(48) PickableEntity pickableEntity = PickableEntity.obtain(1, false);
		if (OrthoMode.orthographicProjection) {
			model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons[0], HintArrow.scaledOrthoZoom, 0);
		} else {
			model.render(matrix, pickableEntity.boundingBoxPolygons[0], 0);
		}
		if (this.particleSystem != null) {
			@Pc(76) ParticleList particles = this.particleSystem.getList();
			if (OrthoMode.orthographicProjection) {
				toolkit.renderOrtho(particles, HintArrow.scaledOrthoZoom);
			} else {
				toolkit.render(particles);
			}
		}
		this.transparent = model.F();
		this.sphereRadius = model.fa();
		this.minY = model.ma();
		return pickableEntity;
	}

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(Lclient!ha;III)Z")
	@Override
	public boolean picked(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int mouseY, @OriginalArg(2) int mouseX) {
		return false;
	}

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(BI)V")
	public void tick(@OriginalArg(1) int time) {
		this.ticked = true;
		this.translateX += (double) time * this.speedX;
		this.translateZ += (double) time * this.speedZ;
		if (this.groundRelative) {
			this.translateY = Scene.tileHeightForAbsolutePosition((int) this.translateX, super.level, (int) this.translateZ) - this.y1;
		} else if (this.verticalAngle == -1) {
			this.translateY += this.verticalSpeed * (double) time;
		} else {
			this.translateY += (double) time * this.verticalAcceleration * 0.5D * (double) time + (double) time * this.verticalSpeed;
			this.verticalSpeed += this.verticalAcceleration * (double) time;
		}
		this.rotateY = (int) (Math.atan2(this.speedX, this.speedZ) * 2607.5945876176133D) + 8192 & 0x3FFF;
		this.rotateX = (int) (Math.atan2(this.verticalSpeed, this.horizontalSpeed) * 2607.5945876176133D) & 0x3FFF;
		if (this.seqType == null) {
			return;
		}
		this.frameCycle += time;
		while (true) {
			do {
				do {
					if (this.frameCycle <= this.seqType.durations[this.frame]) {
						return;
					}
					this.frameCycle -= this.seqType.durations[this.frame];
					this.frame++;
					if (this.seqType.frames.length <= this.frame) {
						this.frame -= this.seqType.replayInterval;
						if (this.frame < 0 || this.frame >= this.seqType.frames.length) {
							this.frame = 0;
						}
					}
					this.nextFrame = this.frame + 1;
				} while (this.seqType.frames.length > this.nextFrame);
				this.nextFrame -= this.seqType.replayInterval;
			} while (this.nextFrame >= 0 && this.seqType.frames.length > this.nextFrame);
			this.nextFrame = -1;
		}
	}

	@OriginalMember(owner = "client!to", name = "b", descriptor = "(Z)V")
	@Override
	public void removeTileShadow() {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!to", name = "g", descriptor = "(I)Z")
	@Override
	public boolean isStationary() {
		return false;
	}

	@OriginalMember(owner = "client!to", name = "e", descriptor = "(B)V")
	@Override
	public void updateBounds() {
		super.startX = super.endX = (short) (this.translateX / 512.0D);
		super.startZ = super.endZ = (short) (this.translateZ / 512.0D);
	}

	@OriginalMember(owner = "client!to", name = "a", descriptor = "(ILclient!ha;B)Lclient!ka;")
	private Model generateGfxModel(@OriginalArg(0) int functionMask, @OriginalArg(1) Toolkit toolkit) {
		@Pc(9) SpotAnimationType spotAnimationType = TypeLists.SPOT_ANIM.list(this.id);
		return spotAnimationType.getModel(this.frame, toolkit, this.frameCycle, functionMask, this.nextFrame, TypeLists.SEQ);
	}

	@OriginalMember(owner = "client!to", name = "b", descriptor = "(B)Z")
	@Override
	public boolean hasAlpha() {
		return this.transparent;
	}
	@OriginalMember(owner = "client!vga", name = "a", descriptor = "(I)V")
	public static void updateProjectiles() {
		for (@Pc(10) ProjectileAnimationNode projectileAnimationNode = (ProjectileAnimationNode) ProjectileAnimation.aClass341_24.first(); projectileAnimationNode != null; projectileAnimationNode = (ProjectileAnimationNode) ProjectileAnimation.aClass341_24.next()) {
			@Pc(23) ProjectileAnimation projectileAnimation = projectileAnimationNode.projectileAnimation;
			if (projectileAnimation.t2 < ClientState.clientCycle) {
				projectileAnimationNode.unlink();
				projectileAnimation.stopParticles();
			} else if (ClientState.clientCycle >= projectileAnimation.y2) {
				projectileAnimation.cycle();
				if (projectileAnimation.t1 > 0) {
					@Pc(63) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) (projectileAnimation.t1 - 1));
					if (npcEntityNode != null) {
						@Pc(68) NPCEntity npcEntity = npcEntityNode.npc;
						if (npcEntity.x >= 0 && MapRegion.sceneSizeX * 512 > npcEntity.x && npcEntity.z >= 0 && MapRegion.sceneSizeY * 512 > npcEntity.z) {
							projectileAnimation.target(Scene.tileHeightForAbsolutePosition(npcEntity.x, projectileAnimation.level, npcEntity.z) - projectileAnimation.entity2, npcEntity.z, ClientState.clientCycle, npcEntity.x);
						}
					}
				}
				if (projectileAnimation.t1 < 0) {
					@Pc(121) int playerIndex = -projectileAnimation.t1 - 1;
					@Pc(126) PlayerEntity playerEntity;
					if (playerIndex == PlayerUpdate.myPlayerIndex) {
						playerEntity = PlayerUpdate.localPlayer;
					} else {
						playerEntity = PlayerUpdate.players[playerIndex];
					}
					if (playerEntity != null && playerEntity.x >= 0 && MapRegion.sceneSizeX * 512 > playerEntity.x && playerEntity.z >= 0 && playerEntity.z < MapRegion.sceneSizeY * 512) {
						projectileAnimation.target(Scene.tileHeightForAbsolutePosition(playerEntity.x, projectileAnimation.level, playerEntity.z) - projectileAnimation.entity2, playerEntity.z, ClientState.clientCycle, playerEntity.x);
					}
				}
				projectileAnimation.tick(ClientState.animatingStep);
				Scene.addInteractive(projectileAnimation, true);
			}
		}
	}
	@OriginalMember(owner = "client!pj", name = "R", descriptor = "Lclient!uh;")
	public static final Deque aClass341_24 = new Deque();
}
