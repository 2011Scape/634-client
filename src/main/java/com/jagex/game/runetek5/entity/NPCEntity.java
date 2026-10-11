package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.config.bas.BasType;
import com.jagex.game.runetek5.config.npc.NPCType;
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationType;
import com.jagex.game.runetek5.graphics.model.particle.Silhouette;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.ParticleList;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.scene.tile.Tile;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.SoundManager;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.scene.SettingsBits;
import com.jagex.game.runetek5.SceneDrawer;
import com.jagex.game.runetek5.config.sequence.PathingEntitySequence;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.OrthoMode;

@OriginalClass("client!ff")
public final class NPCEntity extends PathingEntity {

	@OriginalMember(owner = "client!ff", name = "Bd", descriptor = "Lclient!bb;")
	public NPCType npcType;

	@OriginalMember(owner = "client!ff", name = "vd", descriptor = "I")
	public int faceX = -1;

	@OriginalMember(owner = "client!ff", name = "Id", descriptor = "I")
	public int faceY = -1;

	@OriginalMember(owner = "client!ff", name = "m", descriptor = "(I)Z")
	private boolean isVisible() {
		return this.npcType.visible;
	}

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	@Override
	public void shareLight(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int offsetY, @OriginalArg(2) boolean hideSharedFaces, @OriginalArg(3) Entity entity, @OriginalArg(4) int offsetZ, @OriginalArg(6) int offsetX) {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!ff", name = "b", descriptor = "(Z)V")
	@Override
	public void removeTileShadow() {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(Lclient!ha;III)Z")
	@Override
	public boolean picked(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int mouseY, @OriginalArg(2) int mouseX) {
		if (this.npcType == null || !this.getAnimatedBodyModel(131072, toolkit)) {
			return false;
		}
		@Pc(19) Matrix matrix = toolkit.scratchMatrix();
		@Pc(24) int yawRotation = super.yaw.getRotation();
		matrix.rotate(yawRotation);
		matrix.translate(super.x, super.y, super.z);
		@Pc(37) boolean picked = false;
		for (@Pc(39) int i = 0; i < super.models.length; i++) {
			if (super.models[i] != null) {
				@Pc(63) boolean var10000;
				label41: {
					if (this.npcType.sizeShift <= 0) {
						label39: {
							if (this.npcType.shadowSize == -1) {
								if (this.npcType.size == 1) {
									break label39;
								}
								var10000 = false;
							} else {
								if (this.npcType.shadowSize == 1) {
									break label39;
								}
								var10000 = false;
							}
							if (!var10000) {
								var10000 = false;
								break label41;
							}
						}
					}
					var10000 = true;
				}
				@Pc(78) boolean quick = var10000;
				@Pc(94) boolean modelPicked;
				if (OrthoMode.orthographicProjection) {
					modelPicked = super.models[i].pickedOrtho(mouseX, mouseY, matrix, quick, this.npcType.sizeShift, HintArrow.scaledOrthoZoom);
				} else {
					modelPicked = super.models[i].picked(mouseX, mouseY, matrix, quick, this.npcType.sizeShift);
				}
				if (modelPicked) {
					picked = true;
					break;
				}
			}
		}
		super.models[0] = super.models[1] = super.models[2] = null;
		return picked;
	}

	@OriginalMember(owner = "client!ff", name = "b", descriptor = "(III)V")
	public void move(@OriginalArg(0) int speed, @OriginalArg(1) int movementType) {
		@Pc(10) int baseX = super.movementBaseX[0];
		@Pc(15) int baseY = super.movementBaseY[0];
		if (speed == 0) {
			baseY++;
		}
		if (speed == 1) {
			baseX++;
			baseY++;
		}
		if (speed == 2) {
			baseX++;
		}
		if (speed == 3) {
			baseX++;
			baseY--;
		}
		if (speed == 4) {
			baseY--;
		}
		if (speed == 5) {
			baseX--;
			baseY--;
		}
		if (speed == 6) {
			baseX--;
		}
		if (speed == 7) {
			baseX--;
			baseY++;
		}
		if (super.currentSequence.sequenceId != -1 && TypeLists.SEQ.list(super.currentSequence.sequenceId).moveType == 1) {
			super.currentSequence.sequenceId = -1;
			super.currentAnimations = null;
		}
		@Pc(90) SpotAnimationType spotAnimationType;
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
		if (super.pathPointer < 9) {
			super.pathPointer++;
		}
		for (@Pc(155) int pathPointer = super.pathPointer; pathPointer > 0; pathPointer--) {
			super.movementBaseX[pathPointer] = super.movementBaseX[pathPointer - 1];
			super.movementBaseY[pathPointer] = super.movementBaseY[pathPointer - 1];
			super.movementQueueSpeed[pathPointer] = super.movementQueueSpeed[pathPointer - 1];
		}
		super.movementBaseX[0] = baseX;
		super.movementBaseY[0] = baseY;
		super.movementQueueSpeed[0] = (byte) movementType;
	}

	@OriginalMember(owner = "client!ff", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	@Override
	public PickableEntity render(@OriginalArg(1) Toolkit toolkit) {
		if (this.npcType == null || !this.getAnimatedBodyModel(2048, toolkit)) {
			return null;
		}
		@Pc(21) Matrix matrix = toolkit.scratchMatrix();
		@Pc(26) int yawRotation = super.yaw.getRotation();
		matrix.rotate(yawRotation);
		@Pc(44) Tile tile = Scene.aClass100ArrayArrayArray3[super.level][super.x >> Scene.tileUnits][super.z >> Scene.tileUnits];
		if (tile == null || tile.groundDecoration == null) {
			super.groundContouredOffsetY = (int) ((float) super.groundContouredOffsetY - (float) super.groundContouredOffsetY / 10.0F);
		} else {
			@Pc(57) int offsetDelta = super.groundContouredOffsetY - tile.groundDecoration.offsetY;
			super.groundContouredOffsetY = (int) ((float) super.groundContouredOffsetY - (float) offsetDelta / 10.0F);
		}
		matrix.translate(super.x, -super.groundContouredOffsetY + super.y - 20, super.z);
		@Pc(100) BasType basType = this.getBasType();
		@Pc(113) NPCType npcType = this.npcType.alternativeIds == null ? this.npcType : this.npcType.getMultiNpc(PlayerVarDomain.playerVarProvider);
		super.transparent = false;
		@Pc(118) PickableEntity pickableEntity = null;
		if (GamePreferences.uid.characterShadows.getValue() == 1 && npcType.shadowed && basType.rendersShadow) {
			@Pc(145) SeqType seqType = super.currentSequence.sequenceId != -1 && super.currentSequence.speed == 0 ? TypeLists.SEQ.list(super.currentSequence.sequenceId) : null;
			@Pc(162) SeqType seqType2 = super.movingSequence.sequenceId == -1 || super.movingSequence.hasType && seqType != null ? null : TypeLists.SEQ.list(super.movingSequence.sequenceId);
			@Pc(212) Model model = PathingEntity.getAnimatedShadowModel(super.modelRotateX, yawRotation, super.models[0], super.modelRotateZ, seqType2 == null ? super.currentSequence.currentFrameId : super.movingSequence.currentFrameId, this.npcType.shadowColorSrc & 0xFFFF, this.npcType.size, super.modelTranslateY, toolkit, this.npcType.shadowAlphaSrc & 0xFF, this.npcType.shadowColorDst & 0xFFFF, this.npcType.shadowAlphaDst & 0xFF, seqType2 == null ? seqType : seqType2);
			if (model != null) {
				pickableEntity = PickableEntity.obtain(super.models.length + 1, this.isVisible());
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
		matrix.rotate(yawRotation);
		matrix.translate(super.x, -super.groundContouredOffsetY + super.y - 5, super.z);
		if (pickableEntity == null) {
			pickableEntity = PickableEntity.obtain(super.models.length, this.isVisible());
		}
		this.updateParticles(toolkit, matrix, false, super.models);
		@Pc(299) int modelIndex;
		if (OrthoMode.orthographicProjection) {
			for (modelIndex = 0; super.models.length > modelIndex; modelIndex++) {
				if (super.models[modelIndex] != null) {
					super.models[modelIndex].renderOrtho(matrix, pickableEntity.boundingBoxPolygons[modelIndex], HintArrow.scaledOrthoZoom, 0);
				}
			}
		} else {
			for (modelIndex = 0; super.models.length > modelIndex; modelIndex++) {
				if (super.models[modelIndex] != null) {
					super.models[modelIndex].render(matrix, pickableEntity.boundingBoxPolygons[modelIndex], 0);
				}
			}
		}
		if (super.particleSystem != null) {
			@Pc(359) ParticleList particleList = super.particleSystem.getList();
			if (OrthoMode.orthographicProjection) {
				toolkit.renderOrtho(particleList, HintArrow.scaledOrthoZoom);
			} else {
				toolkit.render(particleList);
			}
		}
		for (modelIndex = 0; modelIndex < super.models.length; modelIndex++) {
			if (super.models[modelIndex] != null) {
				super.transparent |= super.models[modelIndex].F();
			}
		}
		super.drawCycle = SceneDrawer.timesSceneRendered;
		super.models[0] = super.models[1] = super.models[2] = null;
		return pickableEntity;
	}

	@OriginalMember(owner = "client!ff", name = "f", descriptor = "(B)I")
	@Override
	public int getBasId() {
		if (this.npcType.alternativeIds != null) {
			@Pc(13) NPCType npcType = this.npcType.getMultiNpc(PlayerVarDomain.playerVarProvider);
			if (npcType != null && npcType.npcBasId != -1) {
				return npcType.npcBasId;
			}
		}
		return this.npcType.npcBasId;
	}

	@OriginalMember(owner = "client!ff", name = "l", descriptor = "(I)I")
	@Override
	public int getPickSizeShift() {
		return this.npcType == null ? 0 : this.npcType.sizeShift;
	}

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(IZIIII)V")
	public void clearPath(@OriginalArg(0) int baseY, @OriginalArg(1) boolean teleport, @OriginalArg(2) int z, @OriginalArg(4) int size, @OriginalArg(5) int plane) {
		super.level = super.virtualLevel = (byte) plane;
		if (SettingsBits.isBridge(baseY, z)) {
			super.virtualLevel++;
		}
		if (super.currentSequence.sequenceId != -1 && TypeLists.SEQ.list(super.currentSequence.sequenceId).moveType == 1) {
			super.currentAnimations = null;
			super.currentSequence.sequenceId = -1;
		}
		@Pc(55) SpotAnimationType spotAnimationType;
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
		if (!teleport) {
			@Pc(114) int offsetX = z - super.movementBaseX[0];
			@Pc(122) int offsetY = baseY - super.movementBaseY[0];
			if (offsetX >= -8 && offsetX <= 8 && offsetY >= -8 && offsetY <= 8) {
				if (super.pathPointer < 9) {
					super.pathPointer++;
				}
				for (@Pc(151) int pathPointer = super.pathPointer; pathPointer > 0; pathPointer--) {
					super.movementBaseX[pathPointer] = super.movementBaseX[pathPointer - 1];
					super.movementBaseY[pathPointer] = super.movementBaseY[pathPointer - 1];
					super.movementQueueSpeed[pathPointer] = super.movementQueueSpeed[pathPointer - 1];
				}
				super.movementBaseX[0] = z;
				super.movementBaseY[0] = baseY;
				super.movementQueueSpeed[0] = 1;
				return;
			}
		}
		super.previousMovementStepsLeft = 0;
		super.pathPointer = 0;
		super.movementBaseX[0] = z;
		super.stalledStepCount = 0;
		super.movementBaseY[0] = baseY;
		super.x = (super.movementBaseX[0] << 9) + (size << 8);
		super.z = (super.movementBaseY[0] << 9) + (size << 8);
		if (super.particleSystem != null) {
			super.particleSystem.restart();
		}
	}

	@OriginalMember(owner = "client!ff", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	@Override
	public Silhouette getCylinder(@OriginalArg(0) Toolkit toolkit) {
		return null;
	}

	@OriginalMember(owner = "client!ff", name = "i", descriptor = "(B)Z")
	public boolean hasType() {
		return this.npcType != null;
	}

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(I)I")
	@Override
	public int getOverheadSprite() {
		if (this.npcType.alternativeIds != null) {
			@Pc(13) NPCType npcType = this.npcType.getMultiNpc(PlayerVarDomain.playerVarProvider);
			if (npcType != null && npcType.overheadSprite != -1) {
				return npcType.overheadSprite;
			}
		}
		return this.npcType.overheadSprite;
	}

	@OriginalMember(owner = "client!ff", name = "j", descriptor = "(I)Z")
	@Override
	public boolean hasTileShadow() {
		return false;
	}

	@OriginalMember(owner = "client!ff", name = "b", descriptor = "(I)I")
	@Override
	public int getOverheadHeight() {
		if (this.npcType.alternativeIds != null) {
			@Pc(18) NPCType npcType = this.npcType.getMultiNpc(PlayerVarDomain.playerVarProvider);
			if (npcType != null && npcType.iconHeight != -1) {
				return npcType.iconHeight;
			}
		}
		return this.npcType.iconHeight == -1 ? super.getOverheadHeight() : this.npcType.iconHeight;
	}

	@OriginalMember(owner = "client!ff", name = "c", descriptor = "(Lclient!ha;I)V")
	@Override
	public void particleCycle(@OriginalArg(0) Toolkit toolkit) {
		if (this.npcType == null || !super.particlesInitialized && !this.getAnimatedBodyModel(0, toolkit)) {
			return;
		}
		@Pc(30) Matrix matrix = toolkit.scratchMatrix();
		matrix.rotate(super.yaw.getRotation());
		matrix.translate(super.x, super.y - 20, super.z);
		this.updateParticles(toolkit, matrix, super.particlesInitialized, super.models);
		super.models[0] = super.models[1] = super.models[2] = null;
	}

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(IILclient!ha;)Z")
	private boolean getAnimatedBodyModel(@OriginalArg(1) int functionMask, @OriginalArg(2) Toolkit toolkit) {
		@Pc(13) int initialFunctionMask = functionMask;
		@Pc(17) BasType basType = this.getBasType();
		@Pc(32) SeqType seqType = super.currentSequence.sequenceId != -1 && super.currentSequence.speed == 0 ? TypeLists.SEQ.list(super.currentSequence.sequenceId) : null;
		@Pc(49) SeqType seqType2 = super.movingSequence.sequenceId == -1 || super.movingSequence.hasType && seqType != null ? null : TypeLists.SEQ.list(super.movingSequence.sequenceId);
		@Pc(52) int modelWidth = basType.modelWidth;
		@Pc(55) int modelLength = basType.modelLength;
		if (modelWidth != 0 || modelLength != 0 || basType.rollTargetAngle != 0 || basType.pitchTargetAngle != 0) {
			functionMask |= 0x7;
		}
		@Pc(90) boolean hasTint = super.opacity != 0 && ClientState.clientCycle >= super.tintStartCycle && ClientState.clientCycle < super.tintEndCycle;
		if (hasTint) {
			functionMask |= 0x80000;
		}
		@Pc(101) int yawRotation = super.yaw.getRotation();
		@Pc(135) Model model = super.models[0] = this.npcType.getBodyModel(yawRotation, super.sequences, TypeLists.SEQ, seqType2, super.movingSequence.currentFrameDuration, TypeLists.BAS, super.currentSequence.currentFrameId, seqType, PlayerVarDomain.playerVarProvider, toolkit, super.movingSequence.nextFrameId, super.wornRotation, super.currentSequence.nextFrameId, super.movingSequence.currentFrameId, functionMask, super.currentSequence.currentFrameDuration);
		if (model == null) {
			return false;
		}
		super.sphereRadius = model.fa();
		super.minY = model.ma();
		this.rotate(model);
		if (modelWidth == 0 && modelLength == 0) {
			this.hillChange(yawRotation, this.getSize() << 9, 0, this.getSize() << 9, 0);
		} else {
			this.hillChange(yawRotation, modelWidth, basType.hillRotateZ, modelLength, basType.hillRotateX);
			if (super.modelRotateX != 0) {
				super.models[0].FA(super.modelRotateX);
			}
			if (super.modelRotateZ != 0) {
				super.models[0].VA(super.modelRotateZ);
			}
			if (super.modelTranslateY != 0) {
				super.models[0].H(0, super.modelTranslateY, 0);
			}
		}
		if (hasTint) {
			model.adjustColours(super.hue, super.saturation, super.lightness, super.opacity & 0xFF);
		}
		@Pc(253) SpotAnimationType spotAnimationType;
		@Pc(267) boolean fullDisplacement;
		@Pc(269) int spotFunctionMask;
		@Pc(322) Model model2;
		@Pc(343) int translateX;
		@Pc(345) int translateY;
		@Pc(347) int translateZ;
		@Pc(434) int equipmentRotation;
		@Pc(462) int angleY;
		@Pc(474) int sin;
		@Pc(478) int cos;
		@Pc(489) int rotatedX;
		if (super.primarySpotAnimation.spotAnimationId == -1 || super.primarySpotAnimation.currentFrameId == -1) {
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
				if (super.primarySpotAnimation.adjustmentType >= 0) {
					translateX = 0;
					translateY = 0;
					translateZ = 0;
					if (basType.equipmentTransforms != null && basType.equipmentTransforms[super.primarySpotAnimation.adjustmentType] != null) {
						translateZ = basType.equipmentTransforms[super.primarySpotAnimation.adjustmentType][2];
						translateX = basType.equipmentTransforms[super.primarySpotAnimation.adjustmentType][0];
						translateY = basType.equipmentTransforms[super.primarySpotAnimation.adjustmentType][1];
					}
					if (basType.projectionOffset != null && basType.projectionOffset[super.primarySpotAnimation.adjustmentType] != null) {
						translateY += basType.projectionOffset[super.primarySpotAnimation.adjustmentType][1];
						translateX += basType.projectionOffset[super.primarySpotAnimation.adjustmentType][0];
						translateZ += basType.projectionOffset[super.primarySpotAnimation.adjustmentType][2];
					}
					if (translateZ != 0 || translateX != 0) {
						equipmentRotation = yawRotation;
						if (super.wornRotation != null && super.wornRotation[super.primarySpotAnimation.adjustmentType] != -1) {
							equipmentRotation = super.wornRotation[super.primarySpotAnimation.adjustmentType];
						}
						angleY = equipmentRotation + super.primarySpotAnimation.rotation * 2048 - yawRotation & 0x3FFF;
						if (angleY != 0) {
							model2.a(angleY);
						}
						sin = TextureCombineSource.SINE[angleY];
						cos = TextureCombineSource.COSINE[angleY];
						rotatedX = translateZ * sin + translateX * cos >> 14;
						translateZ = translateZ * cos - translateX * sin >> 14;
						translateX = rotatedX;
					}
					model2.H(translateX, translateY, translateZ);
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
		if (super.secondarySpotAnimation.spotAnimationId == -1 || super.secondarySpotAnimation.currentFrameId == -1) {
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
					translateX = 0;
					translateY = 0;
					translateZ = 0;
					if (basType.equipmentTransforms != null && basType.equipmentTransforms[super.secondarySpotAnimation.adjustmentType] != null) {
						translateY = basType.equipmentTransforms[super.secondarySpotAnimation.adjustmentType][1];
						translateX = basType.equipmentTransforms[super.secondarySpotAnimation.adjustmentType][0];
						translateZ = basType.equipmentTransforms[super.secondarySpotAnimation.adjustmentType][2];
					}
					if (basType.projectionOffset != null && basType.projectionOffset[super.secondarySpotAnimation.adjustmentType] != null) {
						translateY += basType.projectionOffset[super.secondarySpotAnimation.adjustmentType][1];
						translateX += basType.projectionOffset[super.secondarySpotAnimation.adjustmentType][0];
						translateZ += basType.projectionOffset[super.secondarySpotAnimation.adjustmentType][2];
					}
					if (translateZ != 0 || translateX != 0) {
						equipmentRotation = yawRotation;
						if (super.wornRotation != null && super.wornRotation[super.secondarySpotAnimation.adjustmentType] != -1) {
							equipmentRotation = super.wornRotation[super.secondarySpotAnimation.adjustmentType];
						}
						angleY = equipmentRotation + super.secondarySpotAnimation.rotation * 2048 - yawRotation & 0x3FFF;
						if (angleY != 0) {
							model2.a(angleY);
						}
						sin = TextureCombineSource.SINE[angleY];
						cos = TextureCombineSource.COSINE[angleY];
						rotatedX = translateZ * sin + cos * translateX >> 14;
						translateZ = cos * translateZ - sin * translateX >> 14;
						translateX = rotatedX;
					}
					model2.H(translateX, translateY, translateZ);
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

	@OriginalMember(owner = "client!ff", name = "a", descriptor = "(Lclient!bb;I)V")
	public void setNpcType(@OriginalArg(0) NPCType npcType) {
		this.npcType = npcType;
		if (super.particleSystem != null) {
			super.particleSystem.restart();
		}
	}
	@OriginalMember(owner = "client!jf", name = "a", descriptor = "(IB[ILclient!ff;)V")
	public static void animateNpc(@OriginalArg(0) int speed, @OriginalArg(2) int[] animations, @OriginalArg(3) NPCEntity npcEntity) {
		@Pc(10) boolean local10;
		@Pc(12) int i;
		if (npcEntity.currentAnimations != null) {
			boolean animationsMatch = true;
			for (i = 0; i < npcEntity.currentAnimations.length; i++) {
				if (npcEntity.currentAnimations[i] != animations[i]) {
					animationsMatch = false;
					break;
				}
			}
			if (animationsMatch && npcEntity.currentSequence.sequenceId != -1) {
				@Pc(48) SeqType seqType = TypeLists.SEQ.list(npcEntity.currentSequence.sequenceId);
				@Pc(51) int loopType = seqType.loopType;
				if (loopType == 1) {
					npcEntity.currentSequence.currentFrameDuration = 0;
					npcEntity.currentSequence.speed = speed;
					npcEntity.currentSequence.currentLoopCount = 0;
					npcEntity.currentSequence.currentFrameId = 0;
					npcEntity.currentSequence.nextFrameId = 1;
					if (!npcEntity.isHidden) {
						SoundManager.playSequenceSound(npcEntity, npcEntity.currentSequence.currentFrameId, seqType);
					}
				}
				if (loopType == 2) {
					npcEntity.currentSequence.currentLoopCount = 0;
				}
			}
		}
		boolean allCleared = true;
		for (i = 0; i < animations.length; i++) {
			if (animations[i] != -1) {
				allCleared = false;
			}
			if (npcEntity.currentAnimations == null || npcEntity.currentAnimations[i] == -1 || TypeLists.SEQ.list(animations[i]).priority >= TypeLists.SEQ.list(npcEntity.currentAnimations[i]).priority) {
				npcEntity.currentSequence.speed = speed;
				npcEntity.previousMovementStepsLeft = npcEntity.pathPointer;
				npcEntity.currentAnimations = animations;
			}
		}
		if (allCleared) {
			npcEntity.currentAnimations = animations;
			npcEntity.currentSequence.speed = speed;
			npcEntity.previousMovementStepsLeft = npcEntity.pathPointer;
		}
	}
	@OriginalMember(owner = "client!kl", name = "a", descriptor = "(ILclient!ff;[I[I[I)V")
	public static void animateNpcSlots(@OriginalArg(1) NPCEntity npcEntity, @OriginalArg(2) int[] slotMasks, @OriginalArg(3) int[] sequenceIds, @OriginalArg(4) int[] speeds) {
		for (@Pc(8) int i = 0; i < sequenceIds.length; i++) {
			@Pc(14) int sequenceId = sequenceIds[i];
			@Pc(18) int slotMask = slotMasks[i];
			@Pc(22) int speed = speeds[i];
			for (@Pc(24) int j = 0; slotMask != 0 && j < npcEntity.sequences.length; j++) {
				if ((slotMask & 0x1) != 0) {
					if (sequenceId == -1) {
						npcEntity.sequences[j] = null;
					} else {
						@Pc(41) SeqType seqType = TypeLists.SEQ.list(sequenceId);
						@Pc(44) int loopType = seqType.loopType;
						@Pc(49) PathingEntitySequence pathingEntitySequence = npcEntity.sequences[j];
						if (pathingEntitySequence != null) {
							if (pathingEntitySequence.sequenceId == sequenceId) {
								if (loopType == 0) {
									pathingEntitySequence = npcEntity.sequences[j] = null;
								} else if (loopType == 1) {
									pathingEntitySequence.speed = speed;
									pathingEntitySequence.currentFrameDuration = 0;
									pathingEntitySequence.nextFrameId = 1;
									pathingEntitySequence.currentFrameId = 0;
									pathingEntitySequence.currentLoopCount = 0;
									if (!npcEntity.isHidden) {
										SoundManager.playSequenceSound(npcEntity, 0, seqType);
									}
								} else if (loopType == 2) {
									pathingEntitySequence.currentLoopCount = 0;
								}
							} else if (seqType.priority >= TypeLists.SEQ.list(pathingEntitySequence.sequenceId).priority) {
								pathingEntitySequence = npcEntity.sequences[j] = null;
							}
						}
						if (pathingEntitySequence == null) {
							pathingEntitySequence = npcEntity.sequences[j] = new PathingEntitySequence();
							pathingEntitySequence.nextFrameId = 1;
							pathingEntitySequence.sequenceId = sequenceId;
							pathingEntitySequence.currentFrameDuration = 0;
							pathingEntitySequence.currentLoopCount = 0;
							pathingEntitySequence.currentFrameId = 0;
							pathingEntitySequence.speed = speed;
							if (!npcEntity.isHidden) {
								SoundManager.playSequenceSound(npcEntity, 0, seqType);
							}
						}
					}
				}
				slotMask >>>= 0x1;
			}
		}
	}
}
