package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.client.features.SmokeTest;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.ExplicitRuntimeException;
import com.jagex.game.runetek5.MoveSpeed;
import com.jagex.game.runetek5.PathFinder;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance;
import com.jagex.game.runetek5.config.bas.BasType;
import com.jagex.game.runetek5.config.enumtype.EnumType;
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationType;
import com.jagex.game.runetek5.graphics.model.particle.Silhouette;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.Cursor;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.ParticleList;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.scene.tile.Tile;
import com.jagex.game.runetek5.client.ServerEnvironment;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.config.defaults.EquipmentDefaultsType;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.sound.player.area.AmbientSound;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.ModeWhere;
import com.jagex.game.runetek5.scene.environment.EnvironmentManager;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.SceneDrawer;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.OrthoMode;

@OriginalClass("client!ke")
public final class PlayerEntity extends PathingEntity {

	@OriginalMember(owner = "client!ke", name = "td", descriptor = "I")
	private int basId;

	@OriginalMember(owner = "client!ke", name = "Ed", descriptor = "I")
	public int basePositionY;

	@OriginalMember(owner = "client!ke", name = "Jd", descriptor = "Lclient!oo;")
	public PlayerAppearance playerModel;

	@OriginalMember(owner = "client!ke", name = "Kd", descriptor = "Ljava/lang/String;")
	public String username;

	@OriginalMember(owner = "client!ke", name = "Rd", descriptor = "Ljava/lang/String;")
	public String displayName;

	@OriginalMember(owner = "client!ke", name = "Wd", descriptor = "I")
	public int basePositionX;

	@OriginalMember(owner = "client!ke", name = "qd", descriptor = "Z")
	public boolean vorbis = false;

	@OriginalMember(owner = "client!ke", name = "pd", descriptor = "I")
	public int combatLevel = 0;

	@OriginalMember(owner = "client!ke", name = "Id", descriptor = "I")
	public int walkingSoundEffectId = -1;

	@OriginalMember(owner = "client!ke", name = "ud", descriptor = "Z")
	public boolean shouldSuppressMovingAnimation = false;

	@OriginalMember(owner = "client!ke", name = "zd", descriptor = "I")
	public int teleportingSoundEffectId = -1;

	@OriginalMember(owner = "client!ke", name = "Md", descriptor = "Z")
	public boolean hasPendingPositionUpdate = false;

	@OriginalMember(owner = "client!ke", name = "sd", descriptor = "I")
	public int runningSoundEffectId = -1;

	@OriginalMember(owner = "client!ke", name = "vd", descriptor = "I")
	public int prayerHeadIconId = -1;

	@OriginalMember(owner = "client!ke", name = "be", descriptor = "Z")
	public boolean pMapdot = false;

	@OriginalMember(owner = "client!ke", name = "ee", descriptor = "I")
	public int cbLevelWithSumm = 0;

	@OriginalMember(owner = "client!ke", name = "xd", descriptor = "I")
	public int faceDirection = -1;

	@OriginalMember(owner = "client!ke", name = "Yd", descriptor = "Z")
	public boolean hidden = false;

	@OriginalMember(owner = "client!ke", name = "he", descriptor = "I")
	public int defaultSoundEffectId = -1;

	@OriginalMember(owner = "client!ke", name = "Pd", descriptor = "I")
	public int teamId = 0;

	@OriginalMember(owner = "client!ke", name = "ae", descriptor = "I")
	public int isNpc = 0;

	@OriginalMember(owner = "client!ke", name = "de", descriptor = "B")
	private byte titleId = 0;

	@OriginalMember(owner = "client!ke", name = "fe", descriptor = "I")
	public int ambientSoundVolume = 255;

	@OriginalMember(owner = "client!ke", name = "Nd", descriptor = "I")
	public int skullId = -1;

	@OriginalMember(owner = "client!ke", name = "Zd", descriptor = "B")
	private byte titleType = 0;

	@OriginalMember(owner = "client!ke", name = "Ld", descriptor = "B")
	private byte gender = 0;

	@OriginalMember(owner = "client!ke", name = "ie", descriptor = "I")
	public int cbLevelRelated = -1;

	@OriginalMember(owner = "client!ke", name = "le", descriptor = "I")
	public int elo = 0;

	@OriginalMember(owner = "client!ke", name = "c", descriptor = "(Lclient!ha;I)V")
	@Override
	public void particleCycle(@OriginalArg(0) Toolkit toolkit) {
		if (this.playerModel == null || !super.particlesInitialized && !this.getAnimatedBodyModel(0, toolkit)) {
			return;
		}
		@Pc(20) Matrix matrix = toolkit.scratchMatrix();
		matrix.rotate(super.yaw.getRotation());
		matrix.translate(super.x, super.y - 5, super.z);
		this.updateParticles(toolkit, matrix, super.particlesInitialized, super.models);
		super.models[0] = super.models[1] = super.models[2] = null;
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(IIB)V")
	public void move(@OriginalArg(0) int baseY, @OriginalArg(1) int baseX) {
		super.previousMovementStepsLeft = 0;
		super.movementBaseX[0] = baseX;
		super.pathPointer = 0;
		super.stalledStepCount = 0;
		super.movementBaseY[0] = baseY;
		@Pc(31) int size = this.getSize();
		super.z = size * 256 + super.movementBaseY[0] * 512;
		super.x = super.movementBaseX[0] * 512 + size * 256;
		if (PlayerUpdate.localPlayer == this) {
			EnvironmentManager.requestDirectEnvironmentTransition();
		}
		if (super.particleSystem != null) {
			super.particleSystem.restart();
		}
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(ZI)Ljava/lang/String;")
	public String getDisplayName() {
		return this.displayName;
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(BIII)V")
	public void addMovementStep(@OriginalArg(0) byte moveSpeed, @OriginalArg(1) int baseX, @OriginalArg(3) int baseY) {
		if (super.pathPointer < 9) {
			super.pathPointer++;
		}
		for (@Pc(26) int pathPointer = super.pathPointer; pathPointer > 0; pathPointer--) {
			super.movementBaseX[pathPointer] = super.movementBaseX[pathPointer - 1];
			super.movementBaseY[pathPointer] = super.movementBaseY[pathPointer - 1];
			super.movementQueueSpeed[pathPointer] = super.movementQueueSpeed[pathPointer - 1];
		}
		super.movementBaseX[0] = baseX;
		super.movementQueueSpeed[0] = moveSpeed;
		super.movementBaseY[0] = baseY;
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(Lclient!ha;III)Z")
	@Override
	public boolean picked(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int mouseY, @OriginalArg(2) int mouseX) {
		if (this.playerModel == null || !this.getAnimatedBodyModel(131072, toolkit)) {
			return false;
		}
		@Pc(19) Matrix matrix = toolkit.scratchMatrix();
		@Pc(24) int rotation = super.yaw.getRotation();
		matrix.rotate(rotation);
		matrix.translate(super.x, super.y, super.z);
		@Pc(37) boolean picked = false;
		for (@Pc(39) int i = 0; i < super.models.length; i++) {
			if (super.models[i] != null && (OrthoMode.orthographicProjection ? super.models[i].pickedOrtho(mouseX, mouseY, matrix, true, 0, HintArrow.scaledOrthoZoom) : super.models[i].picked(mouseX, mouseY, matrix, true, 0))) {
				picked = true;
				break;
			}
		}
		super.models[0] = super.models[1] = super.models[2] = null;
		return picked;
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(BLclient!cea;)V")
	public void decodeAppearance(@OriginalArg(1) Packet buffer) {
		buffer.currentPosition = 0;
		@Pc(12) int flags = buffer.readUByte();
		this.gender = (byte) (flags & 0x1);
		@Pc(21) boolean vorbis = this.vorbis;
		this.vorbis = (flags & 0x2) != 0;
		@Pc(41) boolean showElo = (flags & 0x4) != 0;
		@Pc(45) int size = super.getSize();
		this.setSize((flags >> 3 & 0x7) + 1);
		this.titleType = (byte) (flags >> 6 & 0x3);
		super.x += this.getSize() - size << 8;
		super.z += this.getSize() - size << 8;
		this.titleId = buffer.readByte();
		this.skullId = buffer.readByte();
		this.prayerHeadIconId = buffer.readByte();
		this.hidden = buffer.readByte() == 1;
		if (ServerEnvironment.LIVE == ModeWhere.WTRC && LoginManager.staffModLevel >= 2) {
			this.hidden = false;
		}
		this.teamId = 0;
		@Pc(132) int morphId = -1;
		@Pc(135) int[] bodyAppearances = new int[12];
		@Pc(158) int local158;
		@Pc(164) int local164;
		@Pc(205) int local205;
		for (@Pc(137) int i = 0; i < 12; i++) {
			@Pc(143) int equipmentHigh = buffer.readUByte();
			if (equipmentHigh == 0) {
				bodyAppearances[i] = 0;
			} else {
				int equipmentLow = buffer.readUByte();
				int equipmentId = equipmentLow + (equipmentHigh << 8);
				if (i == 0 && equipmentId == 65535) {
					morphId = buffer.readUShort();
					this.teamId = buffer.readUByte();
					break;
				}
				if (equipmentId >= 32768) {
					equipmentId = EquipmentDefaultsType.customizableObjSlots[equipmentId - 32768];
					bodyAppearances[i] = equipmentId | 0x40000000;
					int objTeamId = TypeLists.OBJ.list(equipmentId).teamId;
					if (objTeamId != 0) {
						this.teamId = objTeamId;
					}
				} else {
					bodyAppearances[i] = equipmentId - 256 | Integer.MIN_VALUE;
				}
			}
		}
		@Pc(229) int[] newRecolorDstIndices = new int[5];
		for (int recolorSlot = 0; recolorSlot < 5; recolorSlot++) {
			int colourIndex = buffer.readUByte();
			if (PlayerAppearance.recolorPaletteDst.length < 1 || colourIndex < 0 || PlayerAppearance.recolorPaletteDst[0][recolorSlot].length <= colourIndex) {
				colourIndex = 0;
			}
			newRecolorDstIndices[recolorSlot] = colourIndex;
		}
		this.basId = buffer.readUShort();
		this.username = buffer.readString();
		this.displayName = this.username;
		if (PlayerUpdate.localPlayer == this) {
			PlayerUpdate.localUsername = this.username;
		}
		this.combatLevel = buffer.readUByte();
		if (showElo) {
			this.elo = buffer.readUShort();
			this.cbLevelWithSumm = this.combatLevel;
			if (this.elo == 65535) {
				this.elo = -1;
			}
			this.cbLevelRelated = -1;
		} else {
			this.elo = 0;
			this.cbLevelWithSumm = buffer.readUByte();
			this.cbLevelRelated = buffer.readUByte();
			if (this.cbLevelRelated == 255) {
				this.cbLevelRelated = -1;
			}
		}
		int previousIsNpc = this.isNpc;
		this.isNpc = buffer.readUByte();
		@Pc(371) int local371;
		if (this.isNpc == 0) {
			AmbientSound.unlinkPlayerSound(this);
		} else {
			int previousWalkingSound = this.walkingSoundEffectId;
			@Pc(368) int previousTeleportSound = this.teleportingSoundEffectId;
			int previousDefaultSound = this.defaultSoundEffectId;
			@Pc(374) int previousRunningSound = this.runningSoundEffectId;
			@Pc(377) int previousAmbientVolume = this.ambientSoundVolume;
			this.walkingSoundEffectId = buffer.readUShort();
			this.teleportingSoundEffectId = buffer.readUShort();
			this.defaultSoundEffectId = buffer.readUShort();
			this.runningSoundEffectId = buffer.readUShort();
			this.ambientSoundVolume = buffer.readUByte();
			if (this.vorbis != vorbis || this.isNpc != previousIsNpc || this.walkingSoundEffectId != previousWalkingSound || previousTeleportSound != this.teleportingSoundEffectId || previousDefaultSound != this.defaultSoundEffectId || this.runningSoundEffectId != previousRunningSound || previousAmbientVolume != this.ambientSoundVolume) {
				AmbientSound.updatePlayerAmbientSound(this);
			}
		}
		if (this.playerModel == null) {
			this.playerModel = new PlayerAppearance();
		}
		int previousMorphId = this.playerModel.morphism;
		@Pc(467) int[] recolorDstIndices = this.playerModel.recolorDstIndices;
		this.playerModel.setAppearance(morphId, this.getBasId(), bodyAppearances, this.gender == 1, newRecolorDstIndices);
		if (morphId != previousMorphId) {
			super.x = (super.movementBaseX[0] << 9) + (this.getSize() << 8);
			super.z = (super.movementBaseY[0] << 9) + (this.getSize() << 8);
		}
		if (super.playerIndex == PlayerUpdate.myPlayerIndex && recolorDstIndices != null) {
			for (int recolorSlot = 0; recolorSlot < newRecolorDstIndices.length; recolorSlot++) {
				if (newRecolorDstIndices[recolorSlot] != recolorDstIndices[recolorSlot]) {
					TypeLists.OBJ.spriteCacheReset();
					break;
				}
			}
		}
		if (super.particleSystem != null) {
			super.particleSystem.restart();
		}
		if (super.movingSequence.sequenceId == -1 || !super.movingSequence.hasType) {
			return;
		}
		@Pc(567) BasType basType = this.getBasType();
		if (!basType.isReady(super.movingSequence.sequenceId)) {
			super.movingSequence.hasType = false;
			super.movingSequence.sequenceId = -1;
			return;
		}
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(ILclient!ha;ILclient!ka;IILclient!bca;I)V")
	private void drawHint(@OriginalArg(0) int deltaX, @OriginalArg(1) Toolkit toolkit, @OriginalArg(2) int maxDistanceSquared, @OriginalArg(3) Model model, @OriginalArg(5) int deltaY, @OriginalArg(6) Matrix matrix, @OriginalArg(7) int modelId) {
		@Pc(14) int distanceSquared = deltaX * deltaX + deltaY * deltaY;
		if (distanceSquared < 262144 || maxDistanceSquared < distanceSquared) {
			return;
		}
		@Pc(34) int angle = (int) (Math.atan2((double) deltaX, (double) deltaY) * 2607.5945876176133D) & 0x3FFF;
		@Pc(46) Model model2 = PlayerEntity.createPlayerHint(super.modelTranslateY, super.modelRotateX, super.modelRotateZ, angle, modelId, toolkit);
		if (model2 != null) {
			toolkit.C(false);
			model2.render(matrix, null, 0);
			toolkit.C(true);
		}
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(IIIILclient!ka;ILclient!bca;Lclient!ha;I)V")
	private void drawHintOrtho(@OriginalArg(1) int deltaX, @OriginalArg(2) int orthoZoom, @OriginalArg(3) int modelId, @OriginalArg(4) Model model, @OriginalArg(5) int maxDistanceSquared, @OriginalArg(6) Matrix matrix, @OriginalArg(7) Toolkit toolkit, @OriginalArg(8) int deltaY) {
		@Pc(17) int distanceSquared = deltaY * deltaY + deltaX * deltaX;
		if (distanceSquared < 262144 || maxDistanceSquared < distanceSquared) {
			return;
		}
		@Pc(39) int angle = (int) (Math.atan2((double) deltaX, (double) deltaY) * 2607.5945876176133D) & 0x3FFF;
		@Pc(51) Model model2 = PlayerEntity.createPlayerHint(super.modelTranslateY, super.modelRotateX, super.modelRotateZ, angle, modelId, toolkit);
		if (model2 != null) {
			toolkit.C(false);
			model2.renderOrtho(matrix, null, orthoZoom, 0);
			toolkit.C(true);
		}
	}

	@OriginalMember(owner = "client!ke", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	@Override
	public Silhouette getCylinder(@OriginalArg(0) Toolkit toolkit) {
		return null;
	}

	@OriginalMember(owner = "client!ke", name = "b", descriptor = "(Z)V")
	@Override
	public void removeTileShadow() {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	@Override
	public void shareLight(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int offsetY, @OriginalArg(2) boolean hideSharedFaces, @OriginalArg(3) Entity entity, @OriginalArg(4) int offsetZ, @OriginalArg(6) int offsetX) {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(IIBI)V")
	public void move(@OriginalArg(0) int baseY, @OriginalArg(2) byte moveSpeed, @OriginalArg(3) int baseX) {
		if (super.currentSequence.sequenceId != -1 && TypeLists.SEQ.list(super.currentSequence.sequenceId).moveType == 1) {
			super.currentSequence.sequenceId = -1;
			super.currentAnimations = null;
		}
		@Pc(42) SpotAnimationType spotAnimationType;
		if (super.primarySpotAnimation.spotAnimationId != -1) {
			spotAnimationType = TypeLists.SPOT_ANIM.list(super.primarySpotAnimation.spotAnimationId);
			if (spotAnimationType.replay && spotAnimationType.sequenceId != -1 && TypeLists.SEQ.list(spotAnimationType.sequenceId).moveType == 1) {
				super.primarySpotAnimation.spotAnimationId = -1;
			}
		}
		if (super.secondarySpotAnimation.spotAnimationId != -1) {
			spotAnimationType = TypeLists.SPOT_ANIM.list(super.secondarySpotAnimation.spotAnimationId);
			if (spotAnimationType.replay && spotAnimationType.sequenceId != -1 && TypeLists.SEQ.list(spotAnimationType.sequenceId).moveType == 1) {
				super.secondarySpotAnimation.spotAnimationId = -1;
			}
		}
		this.faceDirection = -1;
		if (baseX < 0 || baseX >= MapRegion.sceneSizeX || baseY < 0 || baseY >= MapRegion.sceneSizeY) {
			this.move(baseY, baseX);
		} else if (super.movementBaseX[0] >= 0 && MapRegion.sceneSizeX > super.movementBaseX[0] && super.movementBaseY[0] >= 0 && MapRegion.sceneSizeY > super.movementBaseY[0]) {
			if (moveSpeed == MoveSpeed.RUNNING) {
				PlayerEntity.findRoute(baseY, baseX, this);
			}
			this.addMovementStep(moveSpeed, baseX, baseY);
		} else {
			this.move(baseY, baseX);
		}
	}

	@OriginalMember(owner = "client!ke", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	@Override
	public PickableEntity render(@OriginalArg(1) Toolkit toolkit) {
		if (this.playerModel == null || !this.getAnimatedBodyModel(2048, toolkit)) {
			return null;
		}
		@Pc(19) Matrix matrix = toolkit.scratchMatrix();
		@Pc(24) int rotation = super.yaw.getRotation();
		matrix.rotate(rotation);
		@Pc(42) Tile tile = Scene.aClass100ArrayArrayArray3[super.level][super.x >> Scene.tileUnits][super.z >> Scene.tileUnits];
		if (tile == null || tile.groundDecoration == null) {
			super.groundContouredOffsetY = (int) ((float) super.groundContouredOffsetY - (float) super.groundContouredOffsetY / 10.0F);
		} else {
			@Pc(69) int offsetDelta = super.groundContouredOffsetY - tile.groundDecoration.offsetY;
			super.groundContouredOffsetY = (int) ((float) super.groundContouredOffsetY - (float) offsetDelta / 10.0F);
		}
		matrix.translate(super.x, super.y - super.groundContouredOffsetY - 20, super.z);
		@Pc(95) PickableEntity pickableEntity = null;
		super.transparent = false;
		if (GamePreferences.uid.characterShadows.getValue() == 1) {
			@Pc(110) BasType basType = this.getBasType();
			if (basType.rendersShadow && (this.playerModel.morphism == -1 || TypeLists.NPC.list(this.playerModel.morphism).shadowed)) {
				@Pc(144) SeqType seqType = super.currentSequence.sequenceId != -1 && super.currentSequence.speed == 0 ? TypeLists.SEQ.list(super.currentSequence.sequenceId) : null;
				@Pc(165) SeqType seqType2 = super.movingSequence.sequenceId == -1 || this.shouldSuppressMovingAnimation || super.movingSequence.hasType && seqType != null ? null : TypeLists.SEQ.list(super.movingSequence.sequenceId);
				@Pc(197) Model model = PathingEntity.getAnimatedShadowModel(super.modelRotateX, rotation, super.models[0], super.modelRotateZ, seqType2 == null ? super.currentSequence.currentFrameId : super.movingSequence.currentFrameId, 0, 1, super.modelTranslateY, toolkit, 160, 0, 240, seqType2 == null ? seqType : seqType2);
				if (model != null) {
					pickableEntity = PickableEntity.obtain(super.models.length + 1, true);
					super.transparent = true;
					toolkit.C(false);
					if (OrthoMode.orthographicProjection) {
						model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons[super.models.length], HintArrow.scaledOrthoZoom, 0);
					} else {
						model.render(matrix, pickableEntity.boundingBoxPolygons[super.models.length], 0);
					}
					toolkit.C(true);
				}
			}
		}
		@Pc(258) int local258;
		if (PlayerUpdate.localPlayer == this) {
			matrix.applyTranslation(super.x, super.y, super.z);
			for (int hintIndex = PlayerUpdate.HINT_ARROWS.length - 1; hintIndex >= 0; hintIndex--) {
				@Pc(264) HintArrow hintArrow = PlayerUpdate.HINT_ARROWS[hintIndex];
				if (hintArrow != null && hintArrow.modelId != -1) {
					@Pc(296) int local296;
					if (hintArrow.targetType == 1) {
						@Pc(284) NPCEntityNode npcEntityNode = (NPCEntityNode) NPCList.NPC_MAP.get((long) hintArrow.targetIndex);
						if (npcEntityNode != null) {
							@Pc(289) NPCEntity npcEntity = npcEntityNode.npc;
							int deltaX = npcEntity.x - PlayerUpdate.localPlayer.x;
							@Pc(303) int deltaY = npcEntity.z - PlayerUpdate.localPlayer.z;
							if (OrthoMode.orthographicProjection) {
								this.drawHintOrtho(deltaX, HintArrow.scaledOrthoZoom, hintArrow.modelId, super.models[0], 92160000, matrix, toolkit, deltaY);
							} else {
								this.drawHint(deltaX, toolkit, 92160000, super.models[0], deltaY, matrix, hintArrow.modelId);
							}
						}
					}
					@Pc(358) int local358;
					if (hintArrow.targetType == 2) {
						@Pc(349) int deltaX = hintArrow.x + 256 - PlayerUpdate.localPlayer.x;
						int deltaY = hintArrow.y + 256 - PlayerUpdate.localPlayer.z;
						int maxDistanceSquared = hintArrow.distance << 9;
						maxDistanceSquared *= maxDistanceSquared;
						if (OrthoMode.orthographicProjection) {
							this.drawHintOrtho(deltaX, HintArrow.scaledOrthoZoom, hintArrow.modelId, super.models[0], maxDistanceSquared, matrix, toolkit, deltaY);
						} else {
							this.drawHint(deltaX, toolkit, maxDistanceSquared, super.models[0], deltaY, matrix, hintArrow.modelId);
						}
					}
					if (hintArrow.targetType == 10 && hintArrow.targetIndex >= 0 && PlayerUpdate.players.length > hintArrow.targetIndex) {
						@Pc(424) PlayerEntity playerEntity = PlayerUpdate.players[hintArrow.targetIndex];
						if (playerEntity != null) {
							int deltaX = playerEntity.x - PlayerUpdate.localPlayer.x;
							int deltaY = playerEntity.z - PlayerUpdate.localPlayer.z;
							if (OrthoMode.orthographicProjection) {
								this.drawHintOrtho(deltaX, HintArrow.scaledOrthoZoom, hintArrow.modelId, super.models[0], 92160000, matrix, toolkit, deltaY);
							} else {
								this.drawHint(deltaX, toolkit, 92160000, super.models[0], deltaY, matrix, hintArrow.modelId);
							}
						}
					}
				}
			}
			matrix.rotate(rotation);
			matrix.translate(super.x, super.y, super.z);
		}
		matrix.rotate(rotation);
		matrix.translate(super.x, super.y - super.groundContouredOffsetY - 5, super.z);
		if (pickableEntity == null) {
			pickableEntity = PickableEntity.obtain(super.models.length, true);
		}
		this.updateParticles(toolkit, matrix, false, super.models);
		if (OrthoMode.orthographicProjection) {
			for (int modelIndex = 0; modelIndex < super.models.length; modelIndex++) {
				if (super.models[modelIndex] != null) {
					super.models[modelIndex].renderOrtho(matrix, pickableEntity.boundingBoxPolygons[modelIndex], HintArrow.scaledOrthoZoom, PlayerUpdate.localPlayer == this ? 1 : 0);
				}
			}
		} else {
			for (int modelIndex = 0; modelIndex < super.models.length; modelIndex++) {
				if (super.models[modelIndex] != null) {
					super.models[modelIndex].render(matrix, pickableEntity.boundingBoxPolygons[modelIndex], PlayerUpdate.localPlayer == this ? 1 : 0);
				}
			}
		}
		if (super.particleSystem != null) {
			@Pc(597) ParticleList particleList = super.particleSystem.getList();
			if (OrthoMode.orthographicProjection) {
				toolkit.renderOrtho(particleList, HintArrow.scaledOrthoZoom);
			} else {
				toolkit.render(particleList);
			}
		}
		for (int modelIndex = 0; modelIndex < super.models.length; modelIndex++) {
			if (super.models[modelIndex] != null) {
				super.transparent |= super.models[modelIndex].F();
			}
		}
		// Not part of the original client: the smoke test (client.test=smoke) sees which models were drawn.
		SmokeTest.drawn(this, super.models);
		super.models[0] = super.models[1] = super.models[2] = null;
		super.drawCycle = SceneDrawer.timesSceneRendered;
		return pickableEntity;
	}

	@OriginalMember(owner = "client!ke", name = "b", descriptor = "(ZI)Ljava/lang/String;")
	public String getUsernameWithTitle() {
		@Pc(7) String string = "";
		if (PlayerEntity.playerNamePrefixes != null) {
			string = string + PlayerEntity.playerNamePrefixes[this.titleType];
		}
		@Pc(31) int[] titleEnumIds;
		if (this.gender == 1 && PlayerEntity.femaleTitleEnumIds != null) {
			titleEnumIds = PlayerEntity.femaleTitleEnumIds;
		} else {
			titleEnumIds = PlayerEntity.defaultTitleEnumIds;
		}
		if (titleEnumIds != null && titleEnumIds[this.titleType] != -1) {
			@Pc(57) EnumType enumType = TypeLists.ENUM.list(titleEnumIds[this.titleType]);
			if (enumType.valueType == 's') {
				string = string + enumType.getString(this.titleId & 0xFF);
			} else {
				ExplicitRuntimeException.reportError("gdn1", new Throwable());
				titleEnumIds[this.titleType] = -1;
			}
		}
		string = string + this.username;
		if (PlayerEntity.playerNameSuffixes != null) {
			string = string + PlayerEntity.playerNameSuffixes[this.titleType];
		}
		return string;
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(I)I")
	@Override
	public int getOverheadSprite() {
		return -1;
	}

	@OriginalMember(owner = "client!ke", name = "i", descriptor = "(B)Z")
	public boolean hasAppearance() {
		return this.playerModel != null;
	}

	@OriginalMember(owner = "client!ke", name = "j", descriptor = "(I)Z")
	@Override
	public boolean hasTileShadow() {
		return false;
	}

	@OriginalMember(owner = "client!ke", name = "h", descriptor = "(B)I")
	@Override
	public int getSize() {
		return this.playerModel == null || this.playerModel.morphism == -1 ? super.getSize() : TypeLists.NPC.list(this.playerModel.morphism).size;
	}

	@OriginalMember(owner = "client!ke", name = "a", descriptor = "(ILclient!ha;I)Z")
	private boolean getAnimatedBodyModel(@OriginalArg(0) int functionMask, @OriginalArg(1) Toolkit toolkit) {
		@Pc(7) int initialFunctionMask = functionMask;
		@Pc(11) BasType basType = this.getBasType();
		@Pc(26) SeqType seqType = super.currentSequence.sequenceId != -1 && super.currentSequence.speed == 0 ? TypeLists.SEQ.list(super.currentSequence.sequenceId) : null;
		@Pc(47) SeqType seqType2 = super.movingSequence.sequenceId == -1 || this.shouldSuppressMovingAnimation || super.movingSequence.hasType && seqType != null ? null : TypeLists.SEQ.list(super.movingSequence.sequenceId);
		@Pc(50) int modelWidth = basType.modelWidth;
		@Pc(53) int modelLength = basType.modelLength;
		if (modelWidth != 0 || modelLength != 0 || basType.rollTargetAngle != 0 || basType.pitchTargetAngle != 0) {
			functionMask |= 0x7;
		}
		@Pc(75) int rotation = super.yaw.getRotation();
		@Pc(94) boolean tinted = super.opacity != 0 && super.tintStartCycle <= ClientState.clientCycle && super.tintEndCycle > ClientState.clientCycle;
		if (tinted) {
			functionMask |= 0x80000;
		}
		@Pc(138) Model model = super.models[0] = this.playerModel.getBodyModel(PlayerVarDomain.playerVarProvider, seqType2, super.sequences, TypeLists.OBJ, rotation, seqType, super.currentSequence.nextFrameId, super.movingSequence.nextFrameId, super.wornRotation, functionMask, TypeLists.IDK, super.currentSequence.currentFrameDuration, toolkit, TypeLists.NPC, TypeLists.SEQ, super.movingSequence.currentFrameId, super.currentSequence.currentFrameId, super.movingSequence.currentFrameDuration, TypeLists.BAS);
		@Pc(141) int bodyModelCount = PlayerAppearance.countBodyModels();
		if (GameShell.maxMemory < 96 && bodyModelCount > 50) {
			Scene.uploadAllGameObjects();
		}
		if (ModeWhere.WTRC != ServerEnvironment.LIVE && bodyModelCount < 50) {
			@Pc(172) int targetBufferCount = 50 - bodyModelCount;
			while (targetBufferCount > Cursor.interaction) {
				PlayerAppearance.TEST_ENVIRONMENT_APPEARANCE_OVERRIDE_DATA[Cursor.interaction] = new byte[102400];
				Cursor.interaction++;
			}
			while (Cursor.interaction > targetBufferCount) {
				Cursor.interaction--;
				PlayerAppearance.TEST_ENVIRONMENT_APPEARANCE_OVERRIDE_DATA[Cursor.interaction] = null;
			}
		} else if (ServerEnvironment.LIVE != ModeWhere.WTRC) {
			Cursor.interaction = 0;
			PlayerAppearance.TEST_ENVIRONMENT_APPEARANCE_OVERRIDE_DATA = new byte[50][];
		}
		if (model == null) {
			return false;
		}
		super.sphereRadius = model.fa();
		super.minY = model.ma();
		this.rotate(model);
		if (modelWidth == 0 && modelLength == 0) {
			this.hillChange(rotation, this.getSize() << 9, 0, this.getSize() << 9, 0);
		} else {
			this.hillChange(rotation, modelWidth, basType.hillRotateZ, modelLength, basType.hillRotateX);
			if (super.modelRotateX != 0) {
				model.FA(super.modelRotateX);
			}
			if (super.modelRotateZ != 0) {
				model.VA(super.modelRotateZ);
			}
			if (super.modelTranslateY != 0) {
				model.H(0, super.modelTranslateY, 0);
			}
		}
		if (tinted) {
			model.adjustColours(super.hue, super.saturation, super.lightness, super.opacity & 0xFF);
		}
		@Pc(339) boolean fullDisplacement;
		@Pc(341) int spotFunctionMask;
		@Pc(391) Model model2;
		@Pc(407) int offsetX;
		@Pc(409) int offsetY;
		@Pc(411) int offsetZ;
		@Pc(495) int equipmentRotation;
		@Pc(523) int spotRotation;
		@Pc(535) int sine;
		@Pc(539) int cosine;
		@Pc(549) int rotatedX;
		@Pc(324) SpotAnimationType spotAnimationType;
		if (this.shouldSuppressMovingAnimation || super.primarySpotAnimation.spotAnimationId == -1 || super.primarySpotAnimation.currentFrameId == -1) {
			super.models[1] = null;
		} else {
			spotAnimationType = TypeLists.SPOT_ANIM.list(super.primarySpotAnimation.spotAnimationId);
			fullDisplacement = spotAnimationType.contourType == 3 && (modelWidth != 0 || modelLength != 0);
			spotFunctionMask = initialFunctionMask;
			if (fullDisplacement) {
				spotFunctionMask = initialFunctionMask | 0x7;
			} else {
				if (super.primarySpotAnimation.rotation != 0) {
					spotFunctionMask = initialFunctionMask | 0x5;
				}
				if (super.primarySpotAnimation.height != 0) {
					spotFunctionMask |= 0x2;
				}
				if (super.primarySpotAnimation.adjustmentType >= 0) {
					spotFunctionMask |= 0x7;
				}
			}
			model2 = super.models[1] = spotAnimationType.getModel(super.primarySpotAnimation.currentFrameId, toolkit, super.primarySpotAnimation.currentFrameDuration, spotFunctionMask, super.primarySpotAnimation.nextFrameId, TypeLists.SEQ);
			if (model2 != null) {
				if (super.primarySpotAnimation.adjustmentType >= 0 && basType.equipmentTransforms != null && basType.equipmentTransforms[super.primarySpotAnimation.adjustmentType] != null) {
					offsetX = 0;
					offsetY = 0;
					offsetZ = 0;
					if (basType.equipmentTransforms != null && basType.equipmentTransforms[super.primarySpotAnimation.adjustmentType] != null) {
						offsetY = basType.equipmentTransforms[super.primarySpotAnimation.adjustmentType][1];
						offsetZ = basType.equipmentTransforms[super.primarySpotAnimation.adjustmentType][2];
						offsetX = basType.equipmentTransforms[super.primarySpotAnimation.adjustmentType][0];
					}
					if (basType.projectionOffset != null && basType.projectionOffset[super.primarySpotAnimation.adjustmentType] != null) {
						offsetY += basType.projectionOffset[super.primarySpotAnimation.adjustmentType][1];
						offsetZ += basType.projectionOffset[super.primarySpotAnimation.adjustmentType][2];
						offsetX += basType.projectionOffset[super.primarySpotAnimation.adjustmentType][0];
					}
					if (offsetZ != 0 || offsetX != 0) {
						equipmentRotation = rotation;
						if (super.wornRotation != null && super.wornRotation[super.primarySpotAnimation.adjustmentType] != -1) {
							equipmentRotation = super.wornRotation[super.primarySpotAnimation.adjustmentType];
						}
						spotRotation = equipmentRotation + super.primarySpotAnimation.rotation * 2048 - rotation & 0x3FFF;
						if (spotRotation != 0) {
							model2.a(spotRotation);
						}
						sine = TextureCombineSource.SINE[spotRotation];
						cosine = TextureCombineSource.COSINE[spotRotation];
						rotatedX = cosine * offsetX + offsetZ * sine >> 14;
						offsetZ = offsetZ * cosine - offsetX * sine >> 14;
						offsetX = rotatedX;
					}
					model2.H(offsetX, offsetY, offsetZ);
				} else if (super.primarySpotAnimation.rotation != 0) {
					model2.a(super.primarySpotAnimation.rotation * 2048);
				}
				if (super.primarySpotAnimation.height != 0) {
					model2.H(0, -super.primarySpotAnimation.height << 2, 0);
				}
				if (fullDisplacement) {
					if (super.modelRotateX != 0) {
						model2.FA(super.modelRotateX);
					}
					if (super.modelRotateZ != 0) {
						model2.VA(super.modelRotateZ);
					}
					if (super.modelTranslateY != 0) {
						model2.H(0, super.modelTranslateY, 0);
					}
				}
			}
		}
		if (this.shouldSuppressMovingAnimation || super.secondarySpotAnimation.spotAnimationId == -1 || super.secondarySpotAnimation.currentFrameId == -1) {
			super.models[2] = null;
		} else {
			spotAnimationType = TypeLists.SPOT_ANIM.list(super.secondarySpotAnimation.spotAnimationId);
			fullDisplacement = spotAnimationType.contourType == 3 && (modelWidth != 0 || modelLength != 0);
			spotFunctionMask = initialFunctionMask;
			if (fullDisplacement) {
				spotFunctionMask = initialFunctionMask | 0x7;
			} else {
				if (super.secondarySpotAnimation.rotation != 0) {
					spotFunctionMask = initialFunctionMask | 0x5;
				}
				if (super.secondarySpotAnimation.height != 0) {
					spotFunctionMask |= 0x2;
				}
				if (super.secondarySpotAnimation.adjustmentType >= 0) {
					spotFunctionMask |= 0x7;
				}
			}
			model2 = super.models[2] = spotAnimationType.getSecondaryModel(super.secondarySpotAnimation.currentFrameDuration, super.secondarySpotAnimation.nextFrameId, toolkit, spotFunctionMask, TypeLists.SEQ, super.secondarySpotAnimation.currentFrameId);
			if (model2 != null) {
				if (super.secondarySpotAnimation.adjustmentType >= 0 && basType.equipmentTransforms != null && basType.equipmentTransforms[super.secondarySpotAnimation.adjustmentType] != null) {
					offsetX = 0;
					offsetY = 0;
					offsetZ = 0;
					if (basType.equipmentTransforms != null && basType.equipmentTransforms[super.secondarySpotAnimation.adjustmentType] != null) {
						offsetX = basType.equipmentTransforms[super.secondarySpotAnimation.adjustmentType][0];
						offsetY = basType.equipmentTransforms[super.secondarySpotAnimation.adjustmentType][1];
						offsetZ = basType.equipmentTransforms[super.secondarySpotAnimation.adjustmentType][2];
					}
					if (basType.projectionOffset != null && basType.projectionOffset[super.secondarySpotAnimation.adjustmentType] != null) {
						offsetX += basType.projectionOffset[super.secondarySpotAnimation.adjustmentType][0];
						offsetY += basType.projectionOffset[super.secondarySpotAnimation.adjustmentType][1];
						offsetZ += basType.projectionOffset[super.secondarySpotAnimation.adjustmentType][2];
					}
					if (offsetZ != 0 || offsetX != 0) {
						equipmentRotation = rotation;
						if (super.wornRotation != null && super.wornRotation[super.secondarySpotAnimation.adjustmentType] != -1) {
							equipmentRotation = super.wornRotation[super.secondarySpotAnimation.adjustmentType];
						}
						spotRotation = super.secondarySpotAnimation.rotation * 2048 + equipmentRotation - rotation & 0x3FFF;
						if (spotRotation != 0) {
							model2.a(spotRotation);
						}
						sine = TextureCombineSource.SINE[spotRotation];
						cosine = TextureCombineSource.COSINE[spotRotation];
						rotatedX = offsetZ * sine + cosine * offsetX >> 14;
						offsetZ = cosine * offsetZ - sine * offsetX >> 14;
						offsetX = rotatedX;
					}
					model2.H(offsetX, offsetY, offsetZ);
				} else if (super.secondarySpotAnimation.rotation != 0) {
					model2.a(super.secondarySpotAnimation.rotation * 2048);
				}
				if (super.secondarySpotAnimation.height != 0) {
					model2.H(0, -super.secondarySpotAnimation.height << 2, 0);
				}
				if (fullDisplacement) {
					if (super.modelRotateX != 0) {
						model2.FA(super.modelRotateX);
					}
					if (super.modelRotateZ != 0) {
						model2.VA(super.modelRotateZ);
					}
					if (super.modelTranslateY != 0) {
						model2.H(0, super.modelTranslateY, 0);
					}
				}
			}
		}
		return true;
	}

	@OriginalMember(owner = "client!ke", name = "f", descriptor = "(B)I")
	@Override
	public int getBasId() {
		return this.basId;
	}

	@OriginalMember(owner = "client!rw", name = "a", descriptor = "(BIIILclient!ke;)V")
	public static void findRoute(@OriginalArg(1) int baseY, @OriginalArg(3) int baseX, @OriginalArg(4) PlayerEntity playerEntity) {
		@Pc(10) int currentX = playerEntity.movementBaseX[0];
		@Pc(15) int currentY = playerEntity.movementBaseY[0];
		if (currentX < 0 || MapRegion.sceneSizeX <= currentX || currentY < 0 || MapRegion.sceneSizeY <= currentY || (baseX < 0 || MapRegion.sceneSizeX <= baseX || baseY < 0 || baseY >= MapRegion.sceneSizeY)) {
			return;
		}
		@Pc(77) int stepCount = PathFinder.findRoute(baseX, playerEntity.getSize(), 0, Camera.WALKING_STEPS_Y, true, -4, currentX, currentY, PathFinder.WALKING_STEPS_X, baseY, MapRegion.aClass104Array3[playerEntity.level], 0, 0, 0);
		if (stepCount >= 1 && stepCount <= 3) {
			for (@Pc(94) int i = 0; i < stepCount - 1; i++) {
				playerEntity.addMovementStep((byte) 2, PathFinder.WALKING_STEPS_X[i], Camera.WALKING_STEPS_Y[i]);
			}
		}
	}
	@OriginalMember(owner = "client!hba", name = "a", descriptor = "(Lclient!ke;IBI)V")
	public static void animatePlayer(@OriginalArg(0) PlayerEntity playerEntity, @OriginalArg(1) int animationId, @OriginalArg(3) int speed) {
		@Pc(11) int[] animationIds = new int[4];
		Arrays.setVariables(animationIds, 0, animationIds.length, animationId);
		PlayerUpdate.animate(animationIds, speed, playerEntity);
	}
	@OriginalMember(owner = "client!vg", name = "a", descriptor = "(IIIIIBLclient!ha;)Lclient!ka;")
	public static Model createPlayerHint(@OriginalArg(0) int translateY, @OriginalArg(1) int rotateX, @OriginalArg(2) int rotateZ, @OriginalArg(3) int angle, @OriginalArg(4) int modelId, @OriginalArg(6) Toolkit toolkit) {
		@Pc(11) long key = (long) modelId;
		@Pc(17) Model model = (Model) PlayerEntity.aClass169_28.get(key);
		if (model == null) {
			@Pc(27) Mesh mesh = Mesh.fromResource(Resource.MODEL, modelId);
			if (mesh == null) {
				return null;
			}
			if (mesh.version < 13) {
				mesh.upscale();
			}
			model = toolkit.createModel(mesh, 2055, PlayerEntity.playerHintFeatureMask, 64, 768);
			PlayerEntity.aClass169_28.put(model, key);
		}
		model = model.copy((byte) 2, 2055, true);
		if (angle != 0) {
			model.a(angle);
		}
		if (rotateX != 0) {
			model.FA(rotateX);
		}
		if (rotateZ != 0) {
			model.VA(rotateZ);
		}
		if (translateY != 0) {
			model.H(0, translateY, 0);
		}
		return model;
	}

	@OriginalMember(owner = "client!ti", name = "r", descriptor = "[I")
	public static int[] femaleTitleEnumIds;
	@OriginalMember(owner = "client!ko", name = "u", descriptor = "[Ljava/lang/String;")
	public static String[] playerNamePrefixes;

	@OriginalMember(owner = "client!jea", name = "i", descriptor = "[Ljava/lang/String;")
	public static String[] playerNameSuffixes;
	@OriginalMember(owner = "client!al", name = "a", descriptor = "(II)V")
	public static void setPlayerHintFeatureMask(@OriginalArg(0) int featureMask) {
		PlayerEntity.playerHintFeatureMask = featureMask;
		PlayerEntity.aClass169_28.reset();
	}

	@OriginalMember(owner = "client!pl", name = "i", descriptor = "I")
	public static int playerHintFeatureMask;

	@OriginalMember(owner = "client!cj", name = "b", descriptor = "(I)V")
	public static void clearPlayerHintModels() {
		PlayerEntity.aClass169_28.reset();
	}

	@OriginalMember(owner = "client!uu", name = "f", descriptor = "[I")
	public static final int[] playerNameAffixCounts = new int[] { 1, 4, 1, 2 };

	@OriginalMember(owner = "client!kg", name = "j", descriptor = "[I")
	public static int[] defaultTitleEnumIds;

	@OriginalMember(owner = "client!lw", name = "a", descriptor = "(II)V")
	public static void cleanPlayerHintModelCache() {
		PlayerEntity.aClass169_28.clean(5);
	}

	@OriginalMember(owner = "client!hea", name = "a", descriptor = "Lclient!jr;")
	public static final LRUCache aClass169_28 = new LRUCache(4);

	@OriginalMember(owner = "client!ik", name = "a", descriptor = "(B)V")
	public static void removePlayerHintModelSoftRefs() {
		PlayerEntity.aClass169_28.removeSoftReferences();
	}
}
