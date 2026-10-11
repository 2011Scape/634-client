package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.graphics.hint.HintDetails;
import com.jagex.game.runetek5.config.sequence.MovingSequence;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.entity.pathingentity.EasedAngle;
import com.jagex.game.runetek5.config.bas.BasType;
import com.jagex.game.runetek5.config.hitsplat.HitSplatType;
import com.jagex.game.runetek5.config.seqtype.SeqReplayMode;
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationType;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex;
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.sequence.PathingEntitySequence;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.MoveSpeed;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.config.sequence.frameset.FrameSet;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.utils.ArrayUtils;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.SoundManager;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!kda")
public abstract class PathingEntity extends AbstractInteractiveEntity {

	// Not part of the original client: where this entity stood at the previous and the current game
	// cycle, and where it really is while a frame shows it between the two; see ClientFeatures.blendEntities.
	public int blendCycle;
	public int blendGap;
	public int previousX;
	public int previousZ;
	public int previousYaw;
	public int cycleX;
	public int cycleZ;
	public int cycleYaw;
	public int realX;
	public int realZ;
	public int realYaw;
	public int cycleLevel;
	public boolean blended;

	public final PathingEntitySpotAnimation secondarySpotAnimation = new PathingEntitySpotAnimation();

	@OriginalMember(owner = "client!kda", name = "X", descriptor = "B")
	public byte saturation;

	@OriginalMember(owner = "client!kda", name = "Z", descriptor = "I")
	public int modelRotateZ;

	@OriginalMember(owner = "client!kda", name = "bb", descriptor = "I")
	public int secondaryBarDuration;

	public final PathingEntitySpotAnimation primarySpotAnimation = new PathingEntitySpotAnimation();

	@OriginalMember(owner = "client!kda", name = "sb", descriptor = "I")
	public int secondaryBarHoldCycles;

	@OriginalMember(owner = "client!kda", name = "wb", descriptor = "I")
	public int forceMoveDirection;

	@OriginalMember(owner = "client!kda", name = "Eb", descriptor = "I")
	public int forceMovementT1Delay;

	@OriginalMember(owner = "client!kda", name = "Gb", descriptor = "I")
	public int forceMovementT2XOff;

	@OriginalMember(owner = "client!kda", name = "Hb", descriptor = "[I")
	public int[] wornTargets;

	@OriginalMember(owner = "client!kda", name = "Rb", descriptor = "I")
	public int modelTranslateY;

	@OriginalMember(owner = "client!kda", name = "Ub", descriptor = "B")
	public byte hue;

	@OriginalMember(owner = "client!kda", name = "jc", descriptor = "B")
	public byte lightness;

	@OriginalMember(owner = "client!kda", name = "kc", descriptor = "I")
	public int secondaryBarStep;

	@OriginalMember(owner = "client!kda", name = "vc", descriptor = "I")
	public int lastYawRotation;

	@OriginalMember(owner = "client!kda", name = "Bc", descriptor = "I")
	public int forceMovementT2YOff;

	@OriginalMember(owner = "client!kda", name = "Dc", descriptor = "I")
	public int playerIndex;

	@OriginalMember(owner = "client!kda", name = "Gc", descriptor = "I")
	public int forceMovementT1XOff;

	@OriginalMember(owner = "client!kda", name = "Ic", descriptor = "I")
	public int healthRatio;

	@OriginalMember(owner = "client!kda", name = "Jc", descriptor = "[I")
	public int[] wornRotation;

	@OriginalMember(owner = "client!kda", name = "Nc", descriptor = "I")
	public int forceMovementT2Delay;

	@OriginalMember(owner = "client!kda", name = "Oc", descriptor = "I")
	public int drawCycle;

	@OriginalMember(owner = "client!kda", name = "Pc", descriptor = "I")
	public int modelRotateX;

	@OriginalMember(owner = "client!kda", name = "bd", descriptor = "I")
	public int forceMovementT1YOff;

	@OriginalMember(owner = "client!kda", name = "od", descriptor = "Lclient!uj;")
	public ParticleSystem particleSystem;

	public final MovingSequence movingSequence = new MovingSequence();

	@OriginalMember(owner = "client!kda", name = "Bb", descriptor = "[I")
	public int[] currentAnimations = null;

	public final HintDetails hintDetails = new HintDetails();

	@OriginalMember(owner = "client!kda", name = "vb", descriptor = "I")
	public int minY = 0;

	@OriginalMember(owner = "client!kda", name = "Pb", descriptor = "I")
	public int tintEndCycle = -1;

	@OriginalMember(owner = "client!kda", name = "nc", descriptor = "I")
	public int groundContouredOffsetY = 0;

	@OriginalMember(owner = "client!kda", name = "gb", descriptor = "I")
	public int clientCycles = -1;

	@OriginalMember(owner = "client!kda", name = "Mb", descriptor = "I")
	public int turningTicks = 0;

	@OriginalMember(owner = "client!kda", name = "ub", descriptor = "[I")
	public final int[] hitsplatText = new int[PathingEntity.maximumHits];

	public final PathingEntitySequence currentSequence = newCurrentSequence();

	/**
	 * Not part of the original client: builds currentSequence with the initial values its fields had on
	 * this class.
	 */
	private static PathingEntitySequence newCurrentSequence() {
		PathingEntitySequence group = new PathingEntitySequence();
		group.nextFrameId = -1;
		group.sequenceId = -1;
		return group;
	}

	@OriginalMember(owner = "client!kda", name = "Yb", descriptor = "[I")
	public final int[] activeHitsplatIds = new int[PathingEntity.maximumHits];

	@OriginalMember(owner = "client!kda", name = "Ac", descriptor = "I")
	public int secondaryBarEndCycle = -1000;

	@OriginalMember(owner = "client!kda", name = "ac", descriptor = "I")
	public int overheadStackCount = 0;

	@OriginalMember(owner = "client!kda", name = "nb", descriptor = "B")
	private byte hitmarkPointer = 0;

	@OriginalMember(owner = "client!kda", name = "yc", descriptor = "I")
	public int worldPriority = 0;

	@OriginalMember(owner = "client!kda", name = "sc", descriptor = "B")
	public byte opacity = 0;

	@OriginalMember(owner = "client!kda", name = "tc", descriptor = "I")
	private int size = 1;

	@OriginalMember(owner = "client!kda", name = "rb", descriptor = "Z")
	public boolean secondaryBarAlternate = false;

	@OriginalMember(owner = "client!kda", name = "Nb", descriptor = "I")
	public int tintStartCycle = -1;

	@OriginalMember(owner = "client!kda", name = "oc", descriptor = "I")
	public int faceEntity = -1;

	@OriginalMember(owner = "client!kda", name = "kb", descriptor = "[I")
	public final int[] hitsplatCycles = new int[PathingEntity.maximumHits];

	@OriginalMember(owner = "client!kda", name = "Qc", descriptor = "[I")
	public final int[] hitsplatHealthRatios = new int[PathingEntity.maximumHits];

	@OriginalMember(owner = "client!kda", name = "Tc", descriptor = "I")
	public int lastUpdate = 0;

	@OriginalMember(owner = "client!kda", name = "Wc", descriptor = "Z")
	public boolean isHidden = true;

	@OriginalMember(owner = "client!kda", name = "Xc", descriptor = "I")
	public int yawSpeed = 256;

	@OriginalMember(owner = "client!kda", name = "Vc", descriptor = "[Lclient!qca;")
	public final PathingEntitySequence[] sequences = new PathingEntitySequence[12];

	@OriginalMember(owner = "client!kda", name = "Y", descriptor = "I")
	public int sphereRadius = -32768;

	@OriginalMember(owner = "client!kda", name = "Rc", descriptor = "[I")
	public final int[] soakHitsplatIds = new int[PathingEntity.maximumHits];

	@OriginalMember(owner = "client!kda", name = "xc", descriptor = "[I")
	public final int[] soakHitsplatText = new int[PathingEntity.maximumHits];

	@OriginalMember(owner = "client!kda", name = "ob", descriptor = "I")
	public int healthBarEndCycle = -1000;

	@OriginalMember(owner = "client!kda", name = "ib", descriptor = "Lclient!ui;")
	public final EasedAngle yaw = new EasedAngle();

	@OriginalMember(owner = "client!kda", name = "cd", descriptor = "Lclient!ui;")
	private final EasedAngle roll = new EasedAngle();

	@OriginalMember(owner = "client!kda", name = "dd", descriptor = "Lclient!ui;")
	private final EasedAngle pitch = new EasedAngle();

	@OriginalMember(owner = "client!kda", name = "fd", descriptor = "Z")
	public boolean particlesInitialized = false;

	@OriginalMember(owner = "client!kda", name = "ed", descriptor = "[I")
	public final int[] movementBaseY = new int[10];

	@OriginalMember(owner = "client!kda", name = "jd", descriptor = "I")
	public int previousMovementStepsLeft = 0;

	@OriginalMember(owner = "client!kda", name = "ld", descriptor = "Z")
	public boolean transparent = false;

	@OriginalMember(owner = "client!kda", name = "id", descriptor = "[B")
	public final byte[] movementQueueSpeed = new byte[10];

	@OriginalMember(owner = "client!kda", name = "md", descriptor = "I")
	public int walkSpeedAccumulator = 0;

	@OriginalMember(owner = "client!kda", name = "kd", descriptor = "[Lclient!ka;")
	public final Model[] models = new Model[3];

	@OriginalMember(owner = "client!kda", name = "gd", descriptor = "I")
	public int pathPointer = 0;

	@OriginalMember(owner = "client!kda", name = "hd", descriptor = "[I")
	public final int[] movementBaseX = new int[10];

	@OriginalMember(owner = "client!kda", name = "nd", descriptor = "I")
	public int stalledStepCount = 0;

	@OriginalMember(owner = "client!kda", name = "<init>", descriptor = "()V")
	public PathingEntity() {
		super(0, 0, 0, 0, 0, 0, 0, 0, 0, false, (byte) 0);
	}

	@OriginalMember(owner = "client!kda", name = "f", descriptor = "(B)I")
	public abstract int getBasId();

	@OriginalMember(owner = "client!kda", name = "g", descriptor = "(B)Lclient!sg;")
	public final BasType getBasType() {
		@Pc(20) int basId = this.getBasId();
		return basId == -1 ? PathingEntity.DEFAULT_BASTYPE : TypeLists.BAS.list(basId);
	}

	@OriginalMember(owner = "client!kda", name = "e", descriptor = "(B)V")
	@Override
	public final void updateBounds() {
		@Pc(12) int offset = (this.size - 1 << 8) + 240;
		super.endZ = (short) (offset + super.z >> 9);
		super.startZ = (short) (super.z - offset >> 9);
		super.endX = (short) (offset + super.x >> 9);
		super.startX = (short) (super.x - offset >> 9);
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(III)Z")
	public final boolean rotateEquipment(@OriginalArg(1) int equipIndex, @OriginalArg(2) int requestedAngle) {
		if (this.wornRotation == null) {
			if (requestedAngle == -1) {
				return true;
			}
			this.wornRotation = new int[12];
			for (@Pc(19) int i = 0; i < 12; i++) {
				this.wornRotation[i] = -1;
			}
		}
		@Pc(42) BasType basType = this.getBasType();
		@Pc(44) int angleAdjustment = 256;
		if (basType.turnAngleAdjustment != null && basType.turnAngleAdjustment[equipIndex] > 0) {
			angleAdjustment = basType.turnAngleAdjustment[equipIndex];
		}
		@Pc(75) int local75;
		@Pc(80) int local80;
		if (requestedAngle != -1) {
			if (this.wornRotation[equipIndex] == -1) {
				this.wornRotation[equipIndex] = this.yaw.getRotation();
			}
			int currentAngle = this.wornRotation[equipIndex];
			int angleDiff = requestedAngle - currentAngle;
			if (-angleAdjustment <= angleDiff && angleDiff <= angleAdjustment) {
				this.wornRotation[equipIndex] = requestedAngle;
				return true;
			}
			if ((angleDiff <= 0 || angleDiff > 8192) && angleDiff > -8192) {
				this.wornRotation[equipIndex] = currentAngle - angleAdjustment & 0x3FFF;
			} else {
				this.wornRotation[equipIndex] = currentAngle + angleAdjustment & 0x3FFF;
			}
			return false;
		} else if (this.wornRotation[equipIndex] == -1) {
			return true;
		} else {
			int yawAngle = this.yaw.getRotation();
			int currentAngle = this.wornRotation[equipIndex];
			@Pc(85) int angleDelta = yawAngle - currentAngle;
			if (angleDelta < -angleAdjustment || angleDelta > angleAdjustment) {
				if ((angleDelta <= 0 || angleDelta > 8192) && angleDelta > -8192) {
					this.wornRotation[equipIndex] = currentAngle - angleAdjustment & 0x3FFF;
				} else {
					this.wornRotation[equipIndex] = currentAngle + angleAdjustment & 0x3FFF;
				}
				return false;
			}
			this.wornRotation[equipIndex] = -1;
			for (@Pc(99) int j = 0; j < 12; j++) {
				if (this.wornRotation[j] != -1) {
					return true;
				}
			}
			this.wornRotation = null;
			return true;
		}
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(IIIIBI)V")
	public final void hillChange(@OriginalArg(0) int rotation, @OriginalArg(1) int sizeX, @OriginalArg(2) int maxRotateZ, @OriginalArg(3) int sizeZ, @OriginalArg(5) int maxRotateX) {
		@Pc(13) int centerX = super.startX + super.endX >> 1;
		@Pc(21) int centerZ = super.startZ + super.endZ >> 1;
		@Pc(25) int sinRotation = TextureCombineSource.SINE[rotation];
		@Pc(29) int cosRotation = TextureCombineSource.COSINE[rotation];
		@Pc(34) int nwLocalX = -sizeX / 2;
		@Pc(39) int nwLocalZ = -sizeZ / 2;
		@Pc(49) int nwRotatedX = nwLocalX * cosRotation + nwLocalZ * sinRotation >> 14;
		@Pc(60) int nwRotatedZ = nwLocalZ * cosRotation - sinRotation * nwLocalX >> 14;
		@Pc(75) int nwHeight = Scene.getBridgedAverageHeight(nwRotatedX + super.x, nwRotatedZ + super.z, centerX, super.level, centerZ);
		@Pc(79) int neLocalX = sizeX / 2;
		@Pc(84) int neLocalZ = -sizeZ / 2;
		@Pc(94) int neRotatedX = neLocalX * cosRotation + sinRotation * neLocalZ >> 14;
		@Pc(105) int neRotatedZ = neLocalZ * cosRotation - sinRotation * neLocalX >> 14;
		@Pc(120) int neHeight = Scene.getBridgedAverageHeight(neRotatedX + super.x, super.z + neRotatedZ, centerX, super.level, centerZ);
		@Pc(125) int swLocalX = -sizeX / 2;
		@Pc(129) int swLocalZ = sizeZ / 2;
		@Pc(140) int swRotatedX = swLocalZ * sinRotation + swLocalX * cosRotation >> 14;
		@Pc(151) int swRotatedZ = swLocalZ * cosRotation - swLocalX * sinRotation >> 14;
		@Pc(166) int swHeight = Scene.getBridgedAverageHeight(swRotatedX + super.x, super.z + swRotatedZ, centerX, super.level, centerZ);
		@Pc(176) int seLocalX = sizeX / 2;
		@Pc(180) int seLocalZ = sizeZ / 2;
		@Pc(190) int seRotatedX = cosRotation * seLocalX + seLocalZ * sinRotation >> 14;
		@Pc(201) int seRotatedZ = cosRotation * seLocalZ - sinRotation * seLocalX >> 14;
		@Pc(216) int seHeight = Scene.getBridgedAverageHeight(seRotatedX + super.x, super.z + seRotatedZ, centerX, super.level, centerZ);
		@Pc(227) int minNorthHeight = nwHeight >= neHeight ? neHeight : nwHeight;
		@Pc(234) int minSouthHeight = swHeight < seHeight ? swHeight : seHeight;
		@Pc(245) int minEastHeight = neHeight >= seHeight ? seHeight : neHeight;
		this.modelRotateX = (int) (Math.atan2((double) (minNorthHeight - minSouthHeight), (double) sizeZ) * 2607.5945876176133D) & 0x3FFF;
		@Pc(267) int minWestHeight = nwHeight < swHeight ? nwHeight : swHeight;
		this.modelRotateZ = (int) (Math.atan2((double) (minWestHeight - minEastHeight), (double) sizeX) * 2607.5945876176133D) & 0x3FFF;
		@Pc(294) int clampLimit;
		if (this.modelRotateX != 0 && maxRotateX != 0) {
			clampLimit = 16384 - maxRotateX;
			if (this.modelRotateX <= 8192) {
				if (this.modelRotateX > maxRotateX) {
					this.modelRotateX = maxRotateX;
				}
			} else if (this.modelRotateX < clampLimit) {
				this.modelRotateX = clampLimit;
			}
		}
		if (this.modelRotateZ != 0 && maxRotateZ != 0) {
			clampLimit = 16384 - maxRotateZ;
			if (this.modelRotateZ > 8192) {
				if (clampLimit > this.modelRotateZ) {
					this.modelRotateZ = clampLimit;
				}
			} else if (this.modelRotateZ > maxRotateZ) {
				this.modelRotateZ = maxRotateZ;
			}
		}
		this.modelTranslateY = nwHeight + seHeight;
		if (this.modelTranslateY > neHeight + swHeight) {
			this.modelTranslateY = neHeight + swHeight;
		}
		this.modelTranslateY = (this.modelTranslateY >> 1) - super.y;
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(I)I")
	public abstract int getOverheadSprite();

	@OriginalMember(owner = "client!kda", name = "b", descriptor = "(I)I")
	public int getOverheadHeight() {
		@Pc(7) BasType basType = this.getBasType();
		if (basType.iconHeightOverride == -1) {
			return this.sphereRadius == -32768 ? 200 : -this.sphereRadius;
		} else {
			return basType.iconHeightOverride;
		}
	}

	@OriginalMember(owner = "client!kda", name = "c", descriptor = "(I)V")
	public final void stopMoving() {
		this.pathPointer = 0;
		this.previousMovementStepsLeft = 0;
	}

	@OriginalMember(owner = "client!kda", name = "d", descriptor = "(I)I")
	public final int rotationTick() {
		@Pc(9) BasType basType = this.getBasType();
		@Pc(20) int yaw = this.yaw.rotation;
		@Pc(34) boolean isTurning;
		if (basType.yawAcceleration == 0) {
			isTurning = this.yaw.calculateRotation(this.yawSpeed, this.lastYawRotation, this.yawSpeed);
		} else {
			isTurning = this.yaw.calculateRotation(basType.yawMaxVelocity, this.lastYawRotation, basType.yawAcceleration);
		}
		@Pc(54) int deltaYaw = this.yaw.rotation - yaw;
		if (deltaYaw == 0) {
			this.turningTicks = 0;
			this.yaw.setRotation(this.lastYawRotation);
		} else {
			this.turningTicks++;
		}
		if (isTurning) {
			if (basType.rollAcceleration != 0) {
				if (deltaYaw <= 0) {
					this.roll.calculateRotation(basType.rollMaxVelocity, -basType.rollTargetAngle, basType.rollAcceleration);
				} else {
					this.roll.calculateRotation(basType.rollMaxVelocity, basType.rollTargetAngle, basType.rollAcceleration);
				}
			}
			if (basType.pitchAcceleration != 0) {
				this.pitch.calculateRotation(basType.pitchMaxVelocity, basType.pitchTargetAngle, basType.pitchAcceleration);
			}
		} else {
			if (basType.rollAcceleration == 0) {
				this.roll.setRotation(0);
			} else {
				this.roll.calculateRotation(basType.rollMaxVelocity, 0, basType.rollAcceleration);
			}
			if (basType.pitchAcceleration == 0) {
				this.pitch.setRotation(0);
			} else {
				this.pitch.calculateRotation(basType.pitchMaxVelocity, 0, basType.pitchAcceleration);
			}
		}
		return deltaYaw;
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "([I[II)V")
	public final void updateWornTargets(@OriginalArg(0) int[] flags, @OriginalArg(1) int[] targets) {
		if (this.wornTargets == null && targets != null) {
			this.wornTargets = new int[12];
		} else if (targets == null) {
			this.wornTargets = null;
			return;
		}
		for (@Pc(22) int i = 0; i < this.wornTargets.length; i++) {
			this.wornTargets[i] = -1;
		}
		for (@Pc(37) int j = 0; j < targets.length; j++) {
			@Pc(43) int flag = flags[j];
			for (@Pc(45) int slot = 0; slot < this.wornTargets.length; slot++) {
				if ((flag & 0x1) != 0) {
					this.wornTargets[slot] = targets[j];
				}
				flag >>= 0x1;
			}
		}
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(Lclient!ha;ILclient!bca;Z[Lclient!ka;)V")
	public final void updateParticles(@OriginalArg(0) Toolkit toolkit, @OriginalArg(2) Matrix matrix, @OriginalArg(3) boolean alreadyInitialized, @OriginalArg(4) Model[] models) {
		if (!alreadyInitialized) {
			@Pc(13) Model model = models[0];
			@Pc(17) Model model2 = models[1];
			@Pc(21) Model model3 = models[2];
			if (model != null) {
				model.updateParticlePosition(matrix);
				@Pc(28) int length = 0;
				@Pc(30) int modelsWithEmissiveTriangles = 0;
				@Pc(32) int effectiveVertexCount = 0;
				@Pc(34) int modelsWithEffectiveVertices = 0;
				@Pc(37) EmissiveTriangle[] modelParticleEmitters = model.getEmissiveTriangles();
				@Pc(40) EffectiveVertex[] modelParticleEffectors = model.getEffectiveVertices();
				if (modelParticleEffectors != null) {
					modelsWithEffectiveVertices++;
					effectiveVertexCount = modelParticleEffectors.length;
				}
				if (modelParticleEmitters != null) {
					length = modelParticleEmitters.length;
					modelsWithEmissiveTriangles++;
				}
				@Pc(58) EmissiveTriangle[] modelParticleEmitters2 = null;
				@Pc(60) EffectiveVertex[] modelParticleEffectors2 = null;
				if (model2 != null) {
					model2.updateParticlePosition(matrix);
					modelParticleEmitters2 = model2.getEmissiveTriangles();
					modelParticleEffectors2 = model2.getEffectiveVertices();
				}
				if (modelParticleEffectors2 != null) {
					modelsWithEffectiveVertices++;
					effectiveVertexCount += modelParticleEffectors2.length;
				}
				if (modelParticleEmitters2 != null) {
					modelsWithEmissiveTriangles++;
					length += modelParticleEmitters2.length;
				}
				@Pc(89) EmissiveTriangle[] modelParticleEmitters3 = null;
				@Pc(91) EffectiveVertex[] modelParticleEffectors3 = null;
				if (model3 != null) {
					model3.updateParticlePosition(matrix);
					modelParticleEmitters3 = model3.getEmissiveTriangles();
					modelParticleEffectors3 = model3.getEffectiveVertices();
				}
				if (modelParticleEffectors3 != null) {
					modelsWithEffectiveVertices++;
					effectiveVertexCount += modelParticleEffectors3.length;
				}
				if (modelParticleEmitters3 != null) {
					length += modelParticleEmitters3.length;
					modelsWithEmissiveTriangles++;
				}
				if ((this.particleSystem == null || this.particleSystem.removed) && (modelsWithEmissiveTriangles > 0 || modelsWithEffectiveVertices > 0)) {
					this.particleSystem = ParticleSystem.get(ClientState.clientCycle, true);
				}
				if (this.particleSystem != null) {
					@Pc(145) EmissiveTriangle[] modelParticleEmitters4;
					if (modelsWithEmissiveTriangles != 1) {
						modelParticleEmitters4 = new EmissiveTriangle[length];
						@Pc(147) int trianglePosition = 0;
						if (modelParticleEmitters != null) {
							Arrays.copy(modelParticleEmitters, 0, modelParticleEmitters4, 0, modelParticleEmitters.length);
							trianglePosition = modelParticleEmitters.length;
						}
						if (modelParticleEmitters2 != null) {
							Arrays.copy(modelParticleEmitters2, 0, modelParticleEmitters4, trianglePosition, modelParticleEmitters2.length);
							trianglePosition += modelParticleEmitters2.length;
						}
						if (modelParticleEmitters3 != null) {
							Arrays.copy(modelParticleEmitters3, 0, modelParticleEmitters4, trianglePosition, modelParticleEmitters3.length);
						}
					} else if (modelParticleEmitters2 != null) {
						modelParticleEmitters4 = modelParticleEmitters2;
					} else if (modelParticleEmitters3 == null) {
						modelParticleEmitters4 = modelParticleEmitters;
					} else {
						modelParticleEmitters4 = modelParticleEmitters3;
					}
					@Pc(208) EffectiveVertex[] modelParticleEffectors4;
					if (modelsWithEffectiveVertices != 1) {
						modelParticleEffectors4 = new EffectiveVertex[effectiveVertexCount];
						@Pc(222) int vertexPosition = 0;
						if (modelParticleEffectors != null) {
							Arrays.copy(modelParticleEffectors, 0, modelParticleEffectors4, 0, modelParticleEffectors.length);
							vertexPosition = modelParticleEffectors.length;
						}
						if (modelParticleEffectors2 != null) {
							Arrays.copy(modelParticleEffectors2, 0, modelParticleEffectors4, vertexPosition, modelParticleEffectors2.length);
							vertexPosition += modelParticleEffectors2.length;
						}
						if (modelParticleEffectors3 != null) {
							Arrays.copy(modelParticleEffectors3, 0, modelParticleEffectors4, vertexPosition, modelParticleEffectors3.length);
						}
					} else if (modelParticleEffectors2 != null) {
						modelParticleEffectors4 = modelParticleEffectors2;
					} else if (modelParticleEffectors3 == null) {
						modelParticleEffectors4 = modelParticleEffectors;
					} else {
						modelParticleEffectors4 = modelParticleEffectors3;
					}
					this.particleSystem.update(toolkit, (long) ClientState.clientCycle, modelParticleEmitters4, modelParticleEffectors4);
				}
				this.particlesInitialized = true;
			}
		} else if (this.particleSystem != null) {
			this.particleSystem.setClock((long) ClientState.clientCycle);
		}
		if (this.particleSystem != null) {
			this.particleSystem.updateBounds(super.level, super.startX, super.endX, super.startZ, super.endZ);
		}
	}

	@OriginalMember(owner = "client!kda", name = "finalize", descriptor = "()V")
	@Override
	public final void finalize() {
		if (this.particleSystem != null) {
			this.particleSystem.stopped();
		}
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(BI)V")
	public final void setSize(@OriginalArg(1) int size) {
		this.size = size;
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(BIZ)V")
	public final void turn(@OriginalArg(1) int rotation, @OriginalArg(2) boolean force) {
		@Pc(7) BasType basType = this.getBasType();
		if (force || basType.yawAcceleration != 0 || this.yawSpeed != 0) {
			this.lastYawRotation = rotation & 0x3FFF;
			this.yaw.setRotation(this.lastYawRotation);
		}
	}

	@OriginalMember(owner = "client!kda", name = "i", descriptor = "(I)I")
	@Override
	public final int getMinY(@OriginalArg(0) int check) {
		return check == -25675 ? this.minY : -109;
	}

	@OriginalMember(owner = "client!kda", name = "c", descriptor = "(Z)I")
	@Override
	public final int getModelMinY() {
		return this.sphereRadius == -32768 ? 0 : this.sphereRadius;
	}

	@OriginalMember(owner = "client!kda", name = "h", descriptor = "(B)I")
	public int getSize() {
		return this.size;
	}

	@OriginalMember(owner = "client!kda", name = "g", descriptor = "(I)Z")
	@Override
	public final boolean isStationary() {
		return false;
	}

	@OriginalMember(owner = "client!kda", name = "b", descriptor = "(B)Z")
	@Override
	public final boolean hasAlpha() {
		return this.transparent;
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(IIIIZI)V")
	public final void playSpotAnimation(@OriginalArg(0) int rotation, @OriginalArg(1) int spotAnimationId, @OriginalArg(3) int adjustmentType, @OriginalArg(4) boolean secondary, @OriginalArg(5) int packedDelayAndHeight) {
		@Pc(11) int currentSpotAnimationId = secondary ? this.secondarySpotAnimation.spotAnimationId : this.primarySpotAnimation.spotAnimationId;
		@Pc(49) SeqType seqType;
		if (spotAnimationId != -1 && currentSpotAnimationId != -1) {
			@Pc(29) SpotAnimationType spotAnimationType;
			if (spotAnimationId == currentSpotAnimationId) {
				spotAnimationType = TypeLists.SPOT_ANIM.list(spotAnimationId);
				if (spotAnimationType.replay && spotAnimationType.sequenceId != -1) {
					@Pc(81) SeqType seqType2 = TypeLists.SEQ.list(spotAnimationType.sequenceId);
					@Pc(84) int loopType = seqType2.loopType;
					if (loopType == SeqReplayMode.STOP) {
						return;
					}
					if (loopType == SeqReplayMode.RESTART_LOOP) {
						if (!secondary) {
							this.primarySpotAnimation.currentLoopCount = 0;
							return;
						}
						this.secondarySpotAnimation.currentLoopCount = 0;
						return;
					}
				}
			} else {
				spotAnimationType = TypeLists.SPOT_ANIM.list(spotAnimationId);
				@Pc(34) SpotAnimationType spotAnimationType2 = TypeLists.SPOT_ANIM.list(currentSpotAnimationId);
				if (spotAnimationType.sequenceId != -1 && spotAnimationType2.sequenceId != -1) {
					seqType = TypeLists.SEQ.list(spotAnimationType.sequenceId);
					@Pc(57) SeqType seqType3 = TypeLists.SEQ.list(spotAnimationType2.sequenceId);
					if (seqType3.priority > seqType.priority) {
						return;
					}
				}
			}
		}
		@Pc(105) int currentCycle = ClientState.clientCycle;
		@Pc(172) int sequenceId;
		if (secondary) {
			this.secondarySpotAnimation.nextFrameId = 1;
			this.secondarySpotAnimation.height = packedDelayAndHeight >> 16;
			this.secondarySpotAnimation.spotAnimationId = spotAnimationId;
			this.secondarySpotAnimation.startCycle = (packedDelayAndHeight & 0xFFFF) + currentCycle;
			this.secondarySpotAnimation.currentFrameId = 0;
			this.secondarySpotAnimation.adjustmentType = adjustmentType;
			this.secondarySpotAnimation.rotation = rotation;
			this.secondarySpotAnimation.currentFrameDuration = 0;
			if (currentCycle < this.secondarySpotAnimation.startCycle) {
				this.secondarySpotAnimation.currentFrameId = -1;
			}
			if (this.secondarySpotAnimation.spotAnimationId != -1 && currentCycle == this.secondarySpotAnimation.startCycle) {
				sequenceId = TypeLists.SPOT_ANIM.list(this.secondarySpotAnimation.spotAnimationId).sequenceId;
				if (sequenceId != -1) {
					seqType = TypeLists.SEQ.list(sequenceId);
					if (seqType != null && seqType.frames != null && !this.isHidden) {
						SoundManager.playSequenceSound(this, 0, seqType);
						return;
					}
				}
			}
			return;
		}
		this.primarySpotAnimation.currentFrameDuration = 0;
		this.primarySpotAnimation.adjustmentType = adjustmentType;
		this.primarySpotAnimation.rotation = rotation;
		this.primarySpotAnimation.startCycle = (packedDelayAndHeight & 0xFFFF) + currentCycle;
		this.primarySpotAnimation.nextFrameId = 1;
		this.primarySpotAnimation.currentFrameId = 0;
		this.primarySpotAnimation.spotAnimationId = spotAnimationId;
		this.primarySpotAnimation.height = packedDelayAndHeight >> 16;
		if (currentCycle < this.primarySpotAnimation.startCycle) {
			this.primarySpotAnimation.currentFrameId = -1;
		}
		if (this.primarySpotAnimation.spotAnimationId != -1 && this.primarySpotAnimation.startCycle == currentCycle) {
			sequenceId = TypeLists.SPOT_ANIM.list(this.primarySpotAnimation.spotAnimationId).sequenceId;
			if (sequenceId != -1) {
				seqType = TypeLists.SEQ.list(sequenceId);
				if (seqType != null && seqType.frames != null && !this.isHidden) {
					SoundManager.playSequenceSound(this, 0, seqType);
					return;
				}
			}
		}
		return;
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(IIIIIIIB)V")
	public final void hit(@OriginalArg(0) int hitAmount, @OriginalArg(1) int currentCycle, @OriginalArg(2) int hitsplatId, @OriginalArg(3) int soakAmount, @OriginalArg(4) int soakHitsplatId, @OriginalArg(5) int delay, @OriginalArg(6) int healthRatio) {
		@Pc(7) boolean clearCurrentHits = true;
		@Pc(9) boolean allSlotsActive = true;
		for (@Pc(15) int i = 0; i < PathingEntity.maximumHits; i++) {
			if (currentCycle < this.hitsplatCycles[i]) {
				clearCurrentHits = false;
			} else {
				allSlotsActive = false;
			}
		}
		@Pc(39) int hitIndex = -1;
		@Pc(41) int displayType = -1;
		@Pc(43) int cyclesVisible = 0;
		if (hitsplatId >= 0) {
			@Pc(50) HitSplatType hitmarkType = TypeLists.aClass307_2.list(hitsplatId);
			displayType = hitmarkType.displayType;
			cyclesVisible = hitmarkType.cyclesVisible;
		}
		@Pc(65) int local65;
		if (allSlotsActive) {
			if (displayType == -1) {
				return;
			}
			hitIndex = 0;
			int lowestValue = 0;
			if (displayType == 0) {
				lowestValue = this.hitsplatCycles[0];
			} else if (displayType == 1) {
				lowestValue = this.hitsplatText[0];
			}
			for (@Pc(136) int j = 1; j < PathingEntity.maximumHits; j++) {
				if (displayType == 0) {
					if (this.hitsplatCycles[j] < lowestValue) {
						lowestValue = this.hitsplatCycles[j];
						hitIndex = j;
					}
				} else if (displayType == 1 && this.hitsplatText[j] < lowestValue) {
					lowestValue = this.hitsplatText[j];
					hitIndex = j;
				}
			}
			if (displayType == 1 && hitAmount <= lowestValue) {
				return;
			}
		} else {
			if (clearCurrentHits) {
				this.hitmarkPointer = 0;
			}
			for (int attempt = 0; attempt < PathingEntity.maximumHits; attempt++) {
				@Pc(70) byte hitmarkPointer = this.hitmarkPointer;
				this.hitmarkPointer = (byte) ((this.hitmarkPointer + 1) % PathingEntity.maximumHits);
				if (this.hitsplatCycles[hitmarkPointer] <= currentCycle) {
					hitIndex = hitmarkPointer;
					break;
				}
			}
		}
		if (hitIndex < 0) {
			return;
		}
		this.activeHitsplatIds[hitIndex] = hitsplatId;
		this.hitsplatText[hitIndex] = hitAmount;
		this.soakHitsplatIds[hitIndex] = soakHitsplatId;
		this.soakHitsplatText[hitIndex] = soakAmount;
		this.hitsplatCycles[hitIndex] = delay + cyclesVisible + currentCycle;
		this.hitsplatHealthRatios[hitIndex] = healthRatio;
	}

	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(ILclient!ka;)V")
	public final void rotate(@OriginalArg(1) Model model) {
		@Pc(7) int rotation = this.roll.rotation;
		@Pc(17) int angleX = this.pitch.rotation;
		if (rotation == 0 && angleX == 0) {
			return;
		}
		@Pc(26) int translateY = model.fa() / 2;
		model.H(0, -translateY, 0);
		model.VA(rotation & 0x3FFF);
		model.FA(angleX & 0x3FFF);
		model.H(0, translateY, 0);
	}

	@OriginalMember(owner = "client!kda", name = "b", descriptor = "(BI)V")
	public final void turn(@OriginalArg(1) int rotation) {
		@Pc(7) BasType basType = this.getBasType();
		if (basType.yawAcceleration == 0 && this.yawSpeed == 0) {
			return;
		}
		this.yaw.clamp();
		@Pc(33) int delta = rotation - this.yaw.rotation & 0x3FFF;
		if (delta <= 8192) {
			this.lastYawRotation = this.yaw.rotation + delta;
		} else {
			this.lastYawRotation = this.yaw.rotation + delta - 16384;
		}
	}

	@OriginalMember(owner = "client!in", name = "R", descriptor = "I")
	public static int overheadSpriteCount = 0;
	@OriginalMember(owner = "client!sg", name = "E", descriptor = "Lclient!sg;")
	public static final BasType DEFAULT_BASTYPE = new BasType();
	@OriginalMember(owner = "client!kga", name = "a", descriptor = "(IIBLclient!kda;I)V")
	public static void animateMovement(@OriginalArg(0) int moveType, @OriginalArg(1) int rotation, @OriginalArg(3) PathingEntity sequence, @OriginalArg(4) int turnDirectIndex) {
		@Pc(14) BasType basType = sequence.getBasType();
		@Pc(23) int turnDirection = sequence.lastYawRotation - sequence.yaw.rotation & 0x3FFF;
		if (moveType == MoveSpeed.STATIONARY) {
			if (turnDirection != 0 || sequence.turningTicks > 25) {
				if (rotation < 0 && basType.standTurnCcwSequence != -1) {
					sequence.movingSequence.hasType = false;
					sequence.movingSequence.sequenceId = basType.standTurnCcwSequence;
				} else if (rotation <= 0 || basType.standTurnCwSequence == -1) {
					sequence.movingSequence.sequenceId = basType.walkSequence;
				} else {
					sequence.movingSequence.sequenceId = basType.standTurnCwSequence;
				}
				sequence.movingSequence.hasType = false;
			} else if (!sequence.movingSequence.hasType || !basType.isReady(sequence.movingSequence.sequenceId)) {
				sequence.movingSequence.sequenceId = basType.ready();
				sequence.movingSequence.hasType = sequence.movingSequence.sequenceId != -1;
			}
		} else if (sequence.faceEntity != -1 && (turnDirection >= 10240 || turnDirection <= 2048)) {
			@Pc(216) int walkDirection = MiniMenu.NPC_TURN_DIRECTION[turnDirectIndex] - sequence.yaw.rotation & 0x3FFF;
			if (moveType == MoveSpeed.RUNNING && basType.runningSequence != -1) {
				if (walkDirection > 2048 && walkDirection <= 6144 && basType.runDir1 != -1) {
					sequence.movingSequence.sequenceId = basType.runDir1;
				} else if (walkDirection >= 10240 && walkDirection < 14336 && basType.runDir2 != -1) {
					sequence.movingSequence.sequenceId = basType.runDir2;
				} else if (walkDirection <= 6144 || walkDirection >= 10240 || basType.runDir3 == -1) {
					sequence.movingSequence.sequenceId = basType.runningSequence;
				} else {
					sequence.movingSequence.sequenceId = basType.runDir3;
				}
			} else if (moveType == MoveSpeed.CRAWL && basType.teleportSequence != -1) {
				if (walkDirection > 2048 && walkDirection <= 6144 && basType.teleDir1 != -1) {
					sequence.movingSequence.sequenceId = basType.teleDir1;
				} else if (walkDirection >= 10240 && walkDirection < 14336 && basType.teleDir2 != -1) {
					sequence.movingSequence.sequenceId = basType.teleDir2;
				} else if (walkDirection <= 6144 || walkDirection >= 10240 || basType.teleDir3 == -1) {
					sequence.movingSequence.sequenceId = basType.teleportSequence;
				} else {
					sequence.movingSequence.sequenceId = basType.teleDir3;
				}
			} else if (walkDirection > 2048 && walkDirection <= 6144 && basType.walkDir1 != -1) {
				sequence.movingSequence.sequenceId = basType.walkDir1;
			} else if (walkDirection >= 10240 && walkDirection < 14336 && basType.walkDir2 != -1) {
				sequence.movingSequence.sequenceId = basType.walkDir2;
			} else if (walkDirection <= 6144 || walkDirection >= 10240 || basType.walkDir3 == -1) {
				sequence.movingSequence.sequenceId = basType.walkSequence;
			} else {
				sequence.movingSequence.sequenceId = basType.walkDir3;
			}
			sequence.movingSequence.hasType = false;
		} else if (turnDirection == 0 && sequence.turningTicks <= 25) {
			if (moveType == MoveSpeed.RUNNING && basType.runningSequence != -1) {
				sequence.movingSequence.sequenceId = basType.runningSequence;
			} else if (moveType == MoveSpeed.CRAWL && basType.teleportSequence != -1) {
				sequence.movingSequence.sequenceId = basType.teleportSequence;
			} else {
				sequence.movingSequence.sequenceId = basType.walkSequence;
			}
			sequence.movingSequence.hasType = false;
		} else {
			if (moveType == MoveSpeed.RUNNING && basType.runningSequence != -1) {
				if (rotation < 0 && basType.runTurnCcwSequence != -1) {
					sequence.movingSequence.sequenceId = basType.runTurnCcwSequence;
				} else if (rotation <= 0 || basType.runTurnCwSequence == -1) {
					sequence.movingSequence.sequenceId = basType.runningSequence;
				} else {
					sequence.movingSequence.sequenceId = basType.runTurnCwSequence;
				}
			} else if (moveType == MoveSpeed.CRAWL && basType.teleportSequence != -1) {
				if (rotation < 0 && basType.teleTurnCcwSequence != -1) {
					sequence.movingSequence.sequenceId = basType.teleTurnCcwSequence;
				} else if (rotation <= 0 || basType.teleTurnCwSequence == -1) {
					sequence.movingSequence.sequenceId = basType.teleportSequence;
				} else {
					sequence.movingSequence.sequenceId = basType.teleTurnCwSequence;
				}
			} else if (rotation < 0 && basType.walkTurnCcwSequence != -1) {
				sequence.movingSequence.sequenceId = basType.walkTurnCcwSequence;
			} else if (rotation <= 0 || basType.walkTurnCwSequence == -1) {
				sequence.movingSequence.sequenceId = basType.walkSequence;
			} else {
				sequence.movingSequence.sequenceId = basType.walkTurnCwSequence;
			}
			sequence.movingSequence.hasType = false;
		}
	}
	@OriginalMember(owner = "client!eh", name = "a", descriptor = "(IILclient!ka;IZIIIILclient!ha;IIILclient!hg;)Lclient!ka;")
	public static Model getAnimatedShadowModel(@OriginalArg(0) int rotateX, @OriginalArg(1) int yaw, @OriginalArg(2) Model model, @OriginalArg(3) int rotateZ, @OriginalArg(5) int frameId, @OriginalArg(6) int shadowColourSrc, @OriginalArg(7) int size, @OriginalArg(8) int translateY, @OriginalArg(9) Toolkit toolkit, @OriginalArg(10) int shadowAlphaSrc, @OriginalArg(11) int shadowColourDst, @OriginalArg(12) int shadowAlphaDst, @OriginalArg(13) SeqType seqType) {
		if (model == null) {
			return null;
		}
		@Pc(11) int functionMask = 2055;
		if (seqType != null) {
			functionMask = seqType.getFunctionMask(-1, frameId, false) | 0x807;
			functionMask &= 0xFFFFFDFF;
		}
		@Pc(53) long key = ((long) shadowColourDst << 48) + (long) (size + (shadowAlphaSrc << 16) + (shadowAlphaDst << 24)) + ((long) shadowColourSrc << 32);
		@Pc(55) LRUCache referenceCache = HintArrow.hintCache;
		@Pc(63) Model model2;
		synchronized (HintArrow.hintCache) {
			model2 = (Model) HintArrow.hintCache.get(key);
		}
		if (model2 == null || toolkit.compareFunctionMasks(model2.ua(), functionMask) != 0) {
			if (model2 != null) {
				functionMask = toolkit.combineFunctionMasks(functionMask, model2.ua());
			}
			@Pc(96) byte segmentCount;
			if (size == 1) {
				segmentCount = 9;
			} else if (size == 2) {
				segmentCount = 12;
			} else if (size == 3) {
				segmentCount = 15;
			} else if (size == 4) {
				segmentCount = 18;
			} else {
				segmentCount = 21;
			}
			@Pc(133) int[] radii = new int[] { 64, 96, 128 };
			@Pc(151) Mesh mesh = new Mesh(segmentCount * 3 + 1, segmentCount * 2 * 3 + -segmentCount, 0);
			@Pc(158) int centerVertex = mesh.addVertex(0, 0, 0);
			@Pc(162) int[][] ringVertices = new int[3][segmentCount];
			@Pc(170) int local170;
			@Pc(174) int local174;
			@Pc(176) int local176;
			@Pc(200) int local200;
			for (@Pc(164) int i = 0; i < 3; i++) {
				int radiusZ = radii[i];
				int radiusY = radii[i];
				for (int segment = 0; segment < segmentCount; segment++) {
					@Pc(184) int angle = (segment << 14) / segmentCount;
					@Pc(192) int vertexZ = TextureCombineSource.SINE[angle] * radiusZ >> 14;
					int vertexY = TextureCombineSource.COSINE[angle] * radiusY >> 14;
					ringVertices[i][segment] = mesh.addVertex(0, vertexY, vertexZ);
				}
			}
			for (int ring = 0; ring < 3; ring++) {
				int dstWeight = (ring * 256 + 128) / 3;
				int srcWeight = 256 - dstWeight;
				@Pc(251) byte alpha = (byte) (dstWeight * shadowAlphaDst + shadowAlphaSrc * srcWeight >> 8);
				@Pc(296) short colour = (short) (((shadowColourDst & 0xFC00) * dstWeight + srcWeight * (shadowColourSrc & 0xFC00) & 0xFC0000) + ((shadowColourDst & 0x380) * dstWeight + srcWeight * (shadowColourSrc & 0x380) & 0x38000) + (dstWeight * (shadowColourDst & 0x7F) + (shadowColourSrc & 0x7F) * srcWeight & 0x7F00) >> 8);
				for (int segment = 0; segment < segmentCount; segment++) {
					if (ring == 0) {
						mesh.addFace(centerVertex, (byte) 1, (byte) -1, (short) -1, colour, ringVertices[0][(segment + 1) % segmentCount], alpha, ringVertices[0][segment]);
					} else {
						mesh.addFace(ringVertices[ring - 1][segment], (byte) 1, (byte) -1, (short) -1, colour, ringVertices[ring - 1][(segment + 1) % segmentCount], alpha, ringVertices[ring][(segment + 1) % segmentCount]);
						mesh.addFace(ringVertices[ring - 1][segment], (byte) 1, (byte) -1, (short) -1, colour, ringVertices[ring][(segment + 1) % segmentCount], alpha, ringVertices[ring][segment]);
					}
				}
			}
			model2 = toolkit.createModel(mesh, functionMask, HintArrow.hintPreferenceFlags, 64, 768);
			@Pc(420) LRUCache referenceCache2 = HintArrow.hintCache;
			synchronized (HintArrow.hintCache) {
				HintArrow.hintCache.put(model2, key);
			}
		}
		@Pc(436) int minX = model.V();
		@Pc(439) int maxX = model.RA();
		@Pc(442) int minZ = model.HA();
		@Pc(445) int maxZ = model.G();
		@Pc(447) FrameSet animFrameset = null;
		if (seqType != null) {
			@Pc(454) int packedFrame = seqType.frames[frameId];
			animFrameset = TypeLists.SEQ.listFrameSet(packedFrame >> 16);
			frameId = packedFrame & 0xFFFF;
		}
		if (animFrameset == null) {
			model2 = model2.copy((byte) 3, functionMask, true);
			model2.O(maxX - minX >> 1, 128, maxZ - minZ >> 1);
			model2.H(maxX + minX >> 1, 0, maxZ + minZ >> 1);
		} else {
			model2 = model2.copy((byte) 3, functionMask, true);
			model2.O(maxX - minX >> 1, 128, maxZ - minZ >> 1);
			model2.H(maxX + minX >> 1, 0, maxZ + minZ >> 1);
			model2.animateShadowFrame(frameId, animFrameset);
		}
		if (rotateX != 0) {
			model2.FA(rotateX);
		}
		if (rotateZ != 0) {
			model2.VA(rotateZ);
		}
		if (translateY != 0) {
			model2.H(0, translateY, 0);
		}
		return model2;
	}

	@OriginalMember(owner = "client!sj", name = "a", descriptor = "(ILclient!kda;)V")
	public static void updateAnimations(@OriginalArg(1) PathingEntity pathingEntity) {
		@Pc(15) SeqType seqType;
		if (pathingEntity.movingSequence.sequenceId != -1) {
			seqType = TypeLists.SEQ.list(pathingEntity.movingSequence.sequenceId);
			if (seqType == null || seqType.frames == null) {
				pathingEntity.movingSequence.hasType = false;
				pathingEntity.movingSequence.sequenceId = -1;
			} else {
				label357: {
					pathingEntity.movingSequence.currentFrameDuration++;
					if (pathingEntity.movingSequence.currentFrameId < seqType.frames.length && seqType.durations[pathingEntity.movingSequence.currentFrameId] < pathingEntity.movingSequence.currentFrameDuration) {
						pathingEntity.movingSequence.nextFrameId++;
						pathingEntity.movingSequence.currentFrameId++;
						pathingEntity.movingSequence.currentFrameDuration = 1;
						if (!pathingEntity.isHidden) {
							SoundManager.playSequenceSound(pathingEntity, pathingEntity.movingSequence.currentFrameId, seqType);
						}
					}
					if (pathingEntity.movingSequence.currentFrameId >= seqType.frames.length) {
						pathingEntity.movingSequence.currentFrameId = 0;
						pathingEntity.movingSequence.currentFrameDuration = 0;
						if (pathingEntity.movingSequence.hasType) {
							pathingEntity.movingSequence.sequenceId = pathingEntity.getBasType().ready();
							if (pathingEntity.movingSequence.sequenceId == -1) {
								pathingEntity.movingSequence.hasType = false;
								break label357;
							}
							seqType = TypeLists.SEQ.list(pathingEntity.movingSequence.sequenceId);
						}
						if (!pathingEntity.isHidden) {
							SoundManager.playSequenceSound(pathingEntity, pathingEntity.movingSequence.currentFrameId, seqType);
						}
					}
					pathingEntity.movingSequence.nextFrameId = pathingEntity.movingSequence.currentFrameId + 1;
					if (seqType.frames == null) {
						pathingEntity.movingSequence.sequenceId = -1;
						pathingEntity.movingSequence.hasType = false;
					} else if (pathingEntity.movingSequence.nextFrameId >= seqType.frames.length) {
						pathingEntity.movingSequence.nextFrameId = 0;
					}
				}
			}
		}
		@Pc(161) int sequenceId;
		@Pc(169) SeqType seqType2;
		@Pc(158) SpotAnimationType spotAnimationType;
		if (pathingEntity.primarySpotAnimation.spotAnimationId != -1 && ClientState.clientCycle >= pathingEntity.primarySpotAnimation.startCycle) {
			spotAnimationType = TypeLists.SPOT_ANIM.list(pathingEntity.primarySpotAnimation.spotAnimationId);
			sequenceId = spotAnimationType.sequenceId;
			if (sequenceId == -1) {
				pathingEntity.primarySpotAnimation.spotAnimationId = -1;
			} else {
				label358: {
					seqType2 = TypeLists.SEQ.list(sequenceId);
					if (spotAnimationType.replay) {
						if (seqType2.interruptType == 3) {
							if (pathingEntity.previousMovementStepsLeft > 0 && pathingEntity.forceMovementT1Delay <= ClientState.clientCycle && ClientState.clientCycle > pathingEntity.forceMovementT2Delay) {
								pathingEntity.primarySpotAnimation.spotAnimationId = -1;
								break label358;
							}
						} else if (seqType2.interruptType == 1 && pathingEntity.previousMovementStepsLeft > 0 && pathingEntity.forceMovementT1Delay <= ClientState.clientCycle && ClientState.clientCycle > pathingEntity.forceMovementT2Delay) {
							pathingEntity.primarySpotAnimation.startCycle = ClientState.clientCycle + 1;
							break label358;
						}
					}
					if (seqType2 == null || seqType2.frames == null) {
						pathingEntity.primarySpotAnimation.spotAnimationId = -1;
					} else {
						if (pathingEntity.primarySpotAnimation.currentFrameId < 0) {
							pathingEntity.primarySpotAnimation.currentFrameId = 0;
							if (!pathingEntity.isHidden) {
								SoundManager.playSequenceSound(pathingEntity, 0, seqType2);
							}
						}
						pathingEntity.primarySpotAnimation.currentFrameDuration++;
						if (pathingEntity.primarySpotAnimation.currentFrameId < seqType2.frames.length && seqType2.durations[pathingEntity.primarySpotAnimation.currentFrameId] < pathingEntity.primarySpotAnimation.currentFrameDuration) {
							pathingEntity.primarySpotAnimation.currentFrameId++;
							pathingEntity.primarySpotAnimation.currentFrameDuration = 1;
							if (!pathingEntity.isHidden) {
								SoundManager.playSequenceSound(pathingEntity, pathingEntity.primarySpotAnimation.currentFrameId, seqType2);
							}
						}
						if (seqType2.frames.length <= pathingEntity.primarySpotAnimation.currentFrameId) {
							if (spotAnimationType.replay) {
								pathingEntity.primarySpotAnimation.currentLoopCount++;
								pathingEntity.primarySpotAnimation.currentFrameId -= seqType2.replayInterval;
								if (seqType2.replayCount <= pathingEntity.primarySpotAnimation.currentLoopCount) {
									pathingEntity.primarySpotAnimation.spotAnimationId = -1;
								} else if (pathingEntity.primarySpotAnimation.currentFrameId < 0 || pathingEntity.primarySpotAnimation.currentFrameId >= seqType2.frames.length) {
									pathingEntity.primarySpotAnimation.spotAnimationId = -1;
								} else if (!pathingEntity.isHidden) {
									SoundManager.playSequenceSound(pathingEntity, pathingEntity.primarySpotAnimation.currentFrameId, seqType2);
								}
							} else {
								pathingEntity.primarySpotAnimation.spotAnimationId = -1;
							}
						}
						pathingEntity.primarySpotAnimation.nextFrameId = pathingEntity.primarySpotAnimation.currentFrameId + 1;
						if (seqType2.frames.length <= pathingEntity.primarySpotAnimation.nextFrameId) {
							if (spotAnimationType.replay) {
								pathingEntity.primarySpotAnimation.nextFrameId -= seqType2.replayInterval;
								if (pathingEntity.primarySpotAnimation.currentLoopCount + 1 >= seqType2.replayCount) {
									pathingEntity.primarySpotAnimation.nextFrameId = -1;
								} else if (pathingEntity.primarySpotAnimation.nextFrameId < 0 || seqType2.frames.length <= pathingEntity.primarySpotAnimation.nextFrameId) {
									pathingEntity.primarySpotAnimation.nextFrameId = -1;
								}
							} else {
								pathingEntity.primarySpotAnimation.nextFrameId = -1;
							}
						}
					}
				}
			}
		}
		if (pathingEntity.secondarySpotAnimation.spotAnimationId != -1 && ClientState.clientCycle >= pathingEntity.secondarySpotAnimation.startCycle) {
			spotAnimationType = TypeLists.SPOT_ANIM.list(pathingEntity.secondarySpotAnimation.spotAnimationId);
			sequenceId = spotAnimationType.sequenceId;
			if (sequenceId == -1) {
				pathingEntity.secondarySpotAnimation.spotAnimationId = -1;
			} else {
				label362: {
					seqType2 = TypeLists.SEQ.list(sequenceId);
					if (spotAnimationType.replay) {
						if (seqType2.interruptType == 3) {
							if (pathingEntity.previousMovementStepsLeft > 0 && ClientState.clientCycle >= pathingEntity.forceMovementT1Delay && ClientState.clientCycle > pathingEntity.forceMovementT2Delay) {
								pathingEntity.secondarySpotAnimation.spotAnimationId = -1;
								break label362;
							}
						} else if (seqType2.interruptType == 1 && pathingEntity.previousMovementStepsLeft > 0 && ClientState.clientCycle >= pathingEntity.forceMovementT1Delay && pathingEntity.forceMovementT2Delay < ClientState.clientCycle) {
							pathingEntity.secondarySpotAnimation.startCycle = ClientState.clientCycle + 1;
							break label362;
						}
					}
					if (seqType2 == null || seqType2.frames == null) {
						pathingEntity.secondarySpotAnimation.spotAnimationId = -1;
					} else {
						if (pathingEntity.secondarySpotAnimation.currentFrameId < 0) {
							pathingEntity.secondarySpotAnimation.currentFrameId = 0;
							if (!pathingEntity.isHidden) {
								SoundManager.playSequenceSound(pathingEntity, 0, seqType2);
							}
						}
						pathingEntity.secondarySpotAnimation.currentFrameDuration++;
						if (seqType2.frames.length > pathingEntity.secondarySpotAnimation.currentFrameId && seqType2.durations[pathingEntity.secondarySpotAnimation.currentFrameId] < pathingEntity.secondarySpotAnimation.currentFrameDuration) {
							pathingEntity.secondarySpotAnimation.currentFrameId++;
							pathingEntity.secondarySpotAnimation.currentFrameDuration = 1;
							if (!pathingEntity.isHidden) {
								SoundManager.playSequenceSound(pathingEntity, pathingEntity.secondarySpotAnimation.currentFrameId, seqType2);
							}
						}
						if (pathingEntity.secondarySpotAnimation.currentFrameId >= seqType2.frames.length) {
							if (spotAnimationType.replay) {
								pathingEntity.secondarySpotAnimation.currentLoopCount++;
								pathingEntity.secondarySpotAnimation.currentFrameId -= seqType2.replayInterval;
								if (seqType2.replayCount <= pathingEntity.secondarySpotAnimation.currentLoopCount) {
									pathingEntity.secondarySpotAnimation.spotAnimationId = -1;
								} else if (pathingEntity.secondarySpotAnimation.currentFrameId < 0 || pathingEntity.secondarySpotAnimation.currentFrameId >= seqType2.frames.length) {
									pathingEntity.secondarySpotAnimation.spotAnimationId = -1;
								} else if (!pathingEntity.isHidden) {
									SoundManager.playSequenceSound(pathingEntity, pathingEntity.secondarySpotAnimation.currentFrameId, seqType2);
								}
							} else {
								pathingEntity.secondarySpotAnimation.spotAnimationId = -1;
							}
						}
						pathingEntity.secondarySpotAnimation.nextFrameId = pathingEntity.secondarySpotAnimation.currentFrameId + 1;
						if (seqType2.frames.length <= pathingEntity.secondarySpotAnimation.nextFrameId) {
							if (spotAnimationType.replay) {
								pathingEntity.secondarySpotAnimation.nextFrameId -= seqType2.replayInterval;
								if (pathingEntity.secondarySpotAnimation.currentLoopCount + 1 >= seqType2.replayCount) {
									pathingEntity.secondarySpotAnimation.nextFrameId = -1;
								} else if (pathingEntity.secondarySpotAnimation.nextFrameId < 0 || seqType2.frames.length <= pathingEntity.secondarySpotAnimation.nextFrameId) {
									pathingEntity.secondarySpotAnimation.nextFrameId = -1;
								}
							} else {
								pathingEntity.secondarySpotAnimation.nextFrameId = -1;
							}
						}
					}
				}
			}
		}
		if (pathingEntity.currentSequence.sequenceId != -1 && pathingEntity.currentSequence.speed <= 1) {
			seqType = TypeLists.SEQ.list(pathingEntity.currentSequence.sequenceId);
			if (seqType.interruptType == 3) {
				if (pathingEntity.previousMovementStepsLeft > 0 && pathingEntity.forceMovementT1Delay <= ClientState.clientCycle && ClientState.clientCycle > pathingEntity.forceMovementT2Delay) {
					pathingEntity.currentSequence.sequenceId = -1;
					pathingEntity.currentAnimations = null;
				}
			} else if (seqType.interruptType == 1 && pathingEntity.previousMovementStepsLeft > 0 && pathingEntity.forceMovementT1Delay <= ClientState.clientCycle && pathingEntity.forceMovementT2Delay < ClientState.clientCycle) {
				pathingEntity.currentSequence.speed = 2;
			}
		}
		if (pathingEntity.currentSequence.sequenceId != -1 && pathingEntity.currentSequence.speed == 0) {
			seqType = TypeLists.SEQ.list(pathingEntity.currentSequence.sequenceId);
			if (seqType == null || seqType.frames == null) {
				pathingEntity.currentSequence.sequenceId = -1;
				pathingEntity.currentAnimations = null;
			} else {
				pathingEntity.currentSequence.currentFrameDuration++;
				if (seqType.frames.length > pathingEntity.currentSequence.currentFrameId && pathingEntity.currentSequence.currentFrameDuration > seqType.durations[pathingEntity.currentSequence.currentFrameId]) {
					pathingEntity.currentSequence.currentFrameId++;
					pathingEntity.currentSequence.currentFrameDuration = 1;
					if (!pathingEntity.isHidden) {
						SoundManager.playSequenceSound(pathingEntity, pathingEntity.currentSequence.currentFrameId, seqType);
					}
				}
				if (seqType.frames.length <= pathingEntity.currentSequence.currentFrameId) {
					pathingEntity.currentSequence.currentFrameId -= seqType.replayInterval;
					pathingEntity.currentSequence.currentLoopCount++;
					if (seqType.replayCount <= pathingEntity.currentSequence.currentLoopCount) {
						pathingEntity.currentAnimations = null;
						pathingEntity.currentSequence.sequenceId = -1;
					} else if (pathingEntity.currentSequence.currentFrameId < 0 || seqType.frames.length <= pathingEntity.currentSequence.currentFrameId) {
						pathingEntity.currentAnimations = null;
						pathingEntity.currentSequence.sequenceId = -1;
					} else if (!pathingEntity.isHidden) {
						SoundManager.playSequenceSound(pathingEntity, pathingEntity.currentSequence.currentFrameId, seqType);
					}
				}
				pathingEntity.currentSequence.nextFrameId = pathingEntity.currentSequence.currentFrameId + 1;
				if (pathingEntity.currentSequence.nextFrameId >= seqType.frames.length) {
					pathingEntity.currentSequence.nextFrameId -= seqType.replayInterval;
					if (seqType.replayCount <= pathingEntity.currentSequence.currentLoopCount + 1) {
						pathingEntity.currentSequence.nextFrameId = -1;
					} else if (pathingEntity.currentSequence.nextFrameId < 0 || seqType.frames.length <= pathingEntity.currentSequence.nextFrameId) {
						pathingEntity.currentSequence.nextFrameId = -1;
					}
				}
			}
		}
		if (pathingEntity.currentSequence.speed > 0) {
			pathingEntity.currentSequence.speed--;
		}
		for (sequenceId = 0; sequenceId < pathingEntity.sequences.length; sequenceId++) {
			@Pc(975) PathingEntitySequence pathingEntitySequence = pathingEntity.sequences[sequenceId];
			if (pathingEntitySequence != null) {
				if (pathingEntitySequence.speed > 0) {
					pathingEntitySequence.speed--;
				} else {
					@Pc(994) SeqType seqType3 = TypeLists.SEQ.list(pathingEntitySequence.sequenceId);
					if (seqType3 == null || seqType3.frames == null) {
						pathingEntity.sequences[sequenceId] = null;
					} else {
						pathingEntitySequence.currentFrameDuration++;
						if (pathingEntitySequence.currentFrameId < seqType3.frames.length && seqType3.durations[pathingEntitySequence.currentFrameId] < pathingEntitySequence.currentFrameDuration) {
							pathingEntitySequence.currentFrameId++;
							pathingEntitySequence.currentFrameDuration = 1;
							if (!pathingEntity.isHidden) {
								SoundManager.playSequenceSound(pathingEntity, pathingEntitySequence.currentFrameId, seqType3);
							}
						}
						if (pathingEntitySequence.currentFrameId >= seqType3.frames.length) {
							pathingEntitySequence.currentFrameId -= seqType3.replayInterval;
							pathingEntitySequence.currentLoopCount++;
							if (pathingEntitySequence.currentLoopCount >= seqType3.replayCount) {
								pathingEntity.sequences[sequenceId] = null;
							} else if (pathingEntitySequence.currentFrameId < 0 || seqType3.frames.length <= pathingEntitySequence.currentFrameId) {
								pathingEntity.sequences[sequenceId] = null;
							} else if (!pathingEntity.isHidden) {
								SoundManager.playSequenceSound(pathingEntity, pathingEntitySequence.currentFrameId, seqType3);
							}
						}
						pathingEntitySequence.nextFrameId = pathingEntitySequence.currentFrameId + 1;
						if (seqType3.frames.length <= pathingEntitySequence.nextFrameId) {
							pathingEntitySequence.nextFrameId -= seqType3.replayInterval;
							if (seqType3.replayCount <= pathingEntitySequence.currentLoopCount + 1) {
								pathingEntitySequence.nextFrameId = -1;
							} else if (pathingEntitySequence.nextFrameId < 0 || seqType3.frames.length <= pathingEntitySequence.nextFrameId) {
								pathingEntitySequence.nextFrameId = -1;
							}
						}
					}
				}
			}
		}
	}
	@OriginalMember(owner = "client!gia", name = "a", descriptor = "(BLclient!kda;)I")
	public static int turnTo(@OriginalArg(1) PathingEntity pathingEntity) {
		if (pathingEntity.yawSpeed == 0) {
			return 0;
		}
		@Pc(66) int deltaZ;
		@Pc(59) int deltaX;
		if (pathingEntity.faceEntity != -1) {
			@Pc(19) PathingEntity pathingEntity2 = null;
			if (pathingEntity.faceEntity < 32768) {
				@Pc(45) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) pathingEntity.faceEntity);
				if (npcEntityNode != null) {
					pathingEntity2 = npcEntityNode.npc;
				}
			} else if (pathingEntity.faceEntity >= 32768) {
				pathingEntity2 = PlayerUpdate.players[pathingEntity.faceEntity - 32768];
			}
			if (pathingEntity2 != null) {
				deltaX = pathingEntity.x - pathingEntity2.x;
				deltaZ = pathingEntity.z - pathingEntity2.z;
				if (deltaX != 0 || deltaZ != 0) {
					pathingEntity.turn((int) (Math.atan2((double) deltaX, (double) deltaZ) * 2607.5945876176133D) & 0x3FFF);
				}
			}
		}
		if (pathingEntity instanceof PlayerEntity) {
			@Pc(92) PlayerEntity playerEntity = (PlayerEntity) pathingEntity;
			if (playerEntity.faceDirection != -1 && (playerEntity.pathPointer == 0 || playerEntity.stalledStepCount > 0)) {
				playerEntity.turn(playerEntity.faceDirection);
				playerEntity.faceDirection = -1;
			}
		} else if (pathingEntity instanceof NPCEntity) {
			@Pc(122) NPCEntity npcEntity = (NPCEntity) pathingEntity;
			if (npcEntity.faceX != -1 && (npcEntity.pathPointer == 0 || npcEntity.stalledStepCount > 0)) {
				deltaX = npcEntity.x - (npcEntity.faceX - MapRegion.sceneBaseX - MapRegion.sceneBaseX) * 256;
				deltaZ = npcEntity.z - (npcEntity.faceY - MapRegion.sceneBaseY - MapRegion.sceneBaseY) * 256;
				if (deltaX != 0 || deltaZ != 0) {
					npcEntity.turn((int) (Math.atan2((double) deltaX, (double) deltaZ) * 2607.5945876176133D) & 0x3FFF);
				}
				npcEntity.faceX = -1;
			}
		}
		return pathingEntity.rotationTick();
	}

	@OriginalMember(owner = "client!client", name = "a", descriptor = "()V")
	public static void buildOverlappingEntityList() {
		PathingEntity.overheadSpriteCount = 0;
		for (@Pc(3) int i = 0; i < NPCList.NPC_UPDATE_INDEX; i++) {
			@Pc(14) NPCEntity npcEntity = ((NPCEntityNode) NPCList.NPC_MAP.get((long) NPCList.NPC_UPDATE_INDICES[i])).npc;
			if (npcEntity.isHidden && npcEntity.getOverheadSprite() != -1) {
				@Pc(32) int sizeOffset = (npcEntity.getSize() - 1) * 256 + 252;
				@Pc(39) int tileX = npcEntity.x - sizeOffset >> 9;
				@Pc(46) int tileZ = npcEntity.z - sizeOffset >> 9;
				@Pc(53) PathingEntity pathingEntity = Scene.getLargestPathingEntityOnTile(tileX, npcEntity.level, tileZ);
				if (pathingEntity != null) {
					@Pc(58) int entityIndex = pathingEntity.playerIndex;
					if (pathingEntity instanceof NPCEntity) {
						entityIndex += 2048;
					}
					if (pathingEntity.overheadStackCount == 0 && pathingEntity.getOverheadSprite() != -1) {
						PathingEntity.ENTITY_INDEX_REFERENCES[PathingEntity.overheadSpriteCount] = entityIndex;
						PathingEntity.NPC_INDEX_REFERENCES[PathingEntity.overheadSpriteCount] = entityIndex;
						PathingEntity.overheadSpriteCount++;
						pathingEntity.overheadStackCount++;
					}
					PathingEntity.ENTITY_INDEX_REFERENCES[PathingEntity.overheadSpriteCount] = entityIndex;
					PathingEntity.NPC_INDEX_REFERENCES[PathingEntity.overheadSpriteCount] = npcEntity.playerIndex + 2048;
					PathingEntity.overheadSpriteCount++;
					pathingEntity.overheadStackCount++;
				}
			}
		}
		ArrayUtils.quicksort(PathingEntity.NPC_INDEX_REFERENCES, PathingEntity.ENTITY_INDEX_REFERENCES, 0, PathingEntity.overheadSpriteCount - 1);
	}

	@OriginalMember(owner = "client!nr", name = "a", descriptor = "I")
	public static int maximumHits = 4;

	@OriginalMember(owner = "client!mf", name = "a", descriptor = "(ILclient!kda;)V")
	public static void processForceMovementT2(@OriginalArg(1) PathingEntity pathingEntity) {
		@Pc(5) boolean shouldInterpolate = false;
		if (ClientState.clientCycle == pathingEntity.forceMovementT2Delay || pathingEntity.currentSequence.sequenceId == -1 || pathingEntity.currentSequence.speed != 0) {
			shouldInterpolate = true;
		} else {
			@Pc(28) SeqType seqType = TypeLists.SEQ.list(pathingEntity.currentSequence.sequenceId);
			if (seqType.tweened || pathingEntity.currentSequence.currentFrameDuration + 1 > seqType.durations[pathingEntity.currentSequence.currentFrameId]) {
				shouldInterpolate = true;
			}
		}
		if (shouldInterpolate) {
			@Pc(60) int duration = pathingEntity.forceMovementT2Delay - pathingEntity.forceMovementT1Delay;
			@Pc(66) int elapsed = ClientState.clientCycle - pathingEntity.forceMovementT1Delay;
			@Pc(77) int startX = pathingEntity.forceMovementT1XOff * 512 + pathingEntity.getSize() * 256;
			@Pc(89) int startZ = pathingEntity.forceMovementT1YOff * 512 + pathingEntity.getSize() * 256;
			@Pc(100) int endX = pathingEntity.forceMovementT2XOff * 512 + pathingEntity.getSize() * 256;
			@Pc(112) int endZ = pathingEntity.forceMovementT2YOff * 512 + pathingEntity.getSize() * 256;
			pathingEntity.x = (endX * elapsed + (duration - elapsed) * startX) / duration;
			pathingEntity.z = (startZ * (duration - elapsed) + elapsed * endZ) / duration;
		}
		pathingEntity.stalledStepCount = 0;
		if (pathingEntity.forceMoveDirection == 0) {
			pathingEntity.turn(8192, false);
		}
		if (pathingEntity.forceMoveDirection == 1) {
			pathingEntity.turn(12288, false);
		}
		if (pathingEntity.forceMoveDirection == 2) {
			pathingEntity.turn(0, false);
		}
		if (pathingEntity.forceMoveDirection == 3) {
			pathingEntity.turn(4096, false);
		}
	}
	@OriginalMember(owner = "client!cu", name = "a", descriptor = "(ZLclient!kda;)V")
	public static void updateEquipmentRotation(@OriginalArg(1) PathingEntity pathingEntity) {
		if (pathingEntity.wornTargets == null && pathingEntity.wornRotation == null) {
			return;
		}
		@Pc(14) boolean allCleared = true;
		for (@Pc(16) int i = 0; i < pathingEntity.wornTargets.length; i++) {
			@Pc(20) int target = -1;
			if (pathingEntity.wornTargets != null) {
				target = pathingEntity.wornTargets[i];
			}
			if (target != -1) {
				allCleared = false;
				@Pc(74) int dx;
				@Pc(89) int dz;
				@Pc(59) int local59;
				if ((target & 0xC0000000) == -1073741824) {
					int packedCoord = target & 0xFFFFFFF;
					@Pc(63) int targetX = packedCoord >> 14;
					dx = pathingEntity.x - (targetX - MapRegion.sceneBaseX) * 512 - 256;
					@Pc(78) int targetZ = packedCoord & 0x3FFF;
					dz = pathingEntity.z - (targetZ - MapRegion.sceneBaseY) * 512 - 256;
				} else if ((target & 0x8000) == 0) {
					@Pc(102) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) target);
					if (npcEntityNode == null) {
						pathingEntity.rotateEquipment(i, -1);
						continue;
					}
					@Pc(115) NPCEntity npcEntity = npcEntityNode.npc;
					dz = pathingEntity.z - npcEntity.z;
					dx = pathingEntity.x - npcEntity.x;
				} else {
					int targetPlayerIndex = target & 0x7FFF;
					@Pc(138) PlayerEntity playerEntity = PlayerUpdate.players[targetPlayerIndex];
					if (playerEntity == null) {
						pathingEntity.rotateEquipment(i, -1);
						continue;
					}
					dz = pathingEntity.z - playerEntity.z;
					dx = pathingEntity.x - playerEntity.x;
				}
				if (dx != 0 || dz != 0) {
					pathingEntity.rotateEquipment(i, (int) (Math.atan2((double) dx, (double) dz) * 2607.5945876176133D) & 0x3FFF);
				}
			} else if (!pathingEntity.rotateEquipment(i, -1)) {
				allCleared = false;
			}
		}
		if (allCleared) {
			pathingEntity.wornTargets = null;
			pathingEntity.wornRotation = null;
		}
	}

	@OriginalMember(owner = "client!cb", name = "d", descriptor = "[I")
	public static final int[] NPC_INDEX_REFERENCES = new int[500];

	@OriginalMember(owner = "client!wha", name = "w", descriptor = "[I")
	public static final int[] ENTITY_INDEX_REFERENCES = new int[500];
	@OriginalMember(owner = "client!fca", name = "a", descriptor = "(Lclient!kda;B)V")
	public static void processForceMovementT1(@OriginalArg(0) PathingEntity pathingEntity) {
		@Pc(8) int remainingCycles = pathingEntity.forceMovementT1Delay - ClientState.clientCycle;
		@Pc(22) int destX = pathingEntity.forceMovementT1XOff * 512 + pathingEntity.getSize() * 256;
		@Pc(34) int destZ = pathingEntity.forceMovementT1YOff * 512 + pathingEntity.getSize() * 256;
		pathingEntity.stalledStepCount = 0;
		pathingEntity.z += (destZ - pathingEntity.z) / remainingCycles;
		pathingEntity.x += (destX - pathingEntity.x) / remainingCycles;
		if (pathingEntity.forceMoveDirection == 0) {
			pathingEntity.turn(8192);
		}
		if (pathingEntity.forceMoveDirection == 1) {
			pathingEntity.turn(12288);
		}
		if (pathingEntity.forceMoveDirection == 2) {
			pathingEntity.turn(0);
		}
		if (pathingEntity.forceMoveDirection == 3) {
			pathingEntity.turn(4096);
		}
	}
}
