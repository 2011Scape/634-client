package com.jagex.game.runetek5;

import com.jagex.game.runetek5.core.constants.LocShapes;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.ModelAndShadow;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex;
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.entity.Entity;
import com.jagex.game.runetek5.entity.ParticleSystem;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!bv")
public final class LocEntity {

	@OriginalMember(owner = "client!bv", name = "e", descriptor = "I")
	private int frameCycle;

	@OriginalMember(owner = "client!bv", name = "h", descriptor = "Lclient!hg;")
	private SeqType sequence;

	@OriginalMember(owner = "client!bv", name = "j", descriptor = "Lclient!ka;")
	private Model model;

	@OriginalMember(owner = "client!bv", name = "v", descriptor = "Lclient!r;")
	private Shadow shadow;

	@OriginalMember(owner = "client!bv", name = "w", descriptor = "[Z")
	private boolean[] castShadowAtPlane;

	@OriginalMember(owner = "client!bv", name = "y", descriptor = "I")
	private int frameStartCycle;

	@OriginalMember(owner = "client!bv", name = "F", descriptor = "Lclient!uj;")
	public ParticleSystem particleSystem;

	@OriginalMember(owner = "client!bv", name = "H", descriptor = "I")
	private int nextFrame;

	@OriginalMember(owner = "client!bv", name = "I", descriptor = "I")
	private int currentFrame;

	@OriginalMember(owner = "client!bv", name = "k", descriptor = "I")
	private int modelMinY = 0;

	@OriginalMember(owner = "client!bv", name = "f", descriptor = "I")
	private int modelRadius = 0;

	@OriginalMember(owner = "client!bv", name = "o", descriptor = "Z")
	private boolean hasSequenceId = false;

	@OriginalMember(owner = "client!bv", name = "d", descriptor = "I")
	private int lastLocId = -1;

	@OriginalMember(owner = "client!bv", name = "t", descriptor = "Z")
	private boolean underwater = false;

	@OriginalMember(owner = "client!bv", name = "g", descriptor = "I")
	private int shadowLocId = -1;

	@OriginalMember(owner = "client!bv", name = "x", descriptor = "I")
	private int shadowFrame = -1;

	@OriginalMember(owner = "client!bv", name = "D", descriptor = "I")
	public final int shape;

	@OriginalMember(owner = "client!bv", name = "n", descriptor = "I")
	public final int id;

	@OriginalMember(owner = "client!bv", name = "b", descriptor = "Lclient!ga;")
	private final Entity entity;

	@OriginalMember(owner = "client!bv", name = "J", descriptor = "B")
	private final byte level;

	@OriginalMember(owner = "client!bv", name = "r", descriptor = "B")
	private final byte virtualLevel;

	@OriginalMember(owner = "client!bv", name = "c", descriptor = "I")
	public final int rotation;

	@OriginalMember(owner = "client!bv", name = "p", descriptor = "Z")
	private final boolean hardShadow;

	@OriginalMember(owner = "client!bv", name = "<init>", descriptor = "(Lclient!ha;Lclient!iv;IIIILclient!ga;ZI)V")
	public LocEntity(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) LocType locType, @OriginalArg(2) int shape, @OriginalArg(3) int rotation, @OriginalArg(4) int level, @OriginalArg(5) int virtualLevel, @OriginalArg(6) Entity entity, @OriginalArg(7) boolean underwater, @OriginalArg(8) int sequenceId) {
		this.shape = shape;
		this.id = locType.id;
		this.entity = entity;
		this.level = (byte) level;
		this.virtualLevel = (byte) virtualLevel;
		this.underwater = underwater;
		this.rotation = rotation;
		this.hardShadow = toolkit.hardShadow() && locType.dynamicShadow && !this.underwater;
		if (sequenceId != -1) {
			this.hasSequenceId = true;
		}
		this.updateSequence(sequenceId);
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(I)I")
	public int getModelMinY() {
		return this.modelMinY;
	}

	@OriginalMember(owner = "client!bv", name = "b", descriptor = "(I)I")
	public int getModelRadius(@OriginalArg(0) int arg0) {
		if (arg0 > -20) {
			this.finalize();
		}
		return this.modelRadius;
	}

	@OriginalMember(owner = "client!bv", name = "c", descriptor = "(I)Z")
	public boolean hardShadow() {
		return this.hardShadow;
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(BI)V")
	private void updateSequence(@OriginalArg(1) int sequenceId) {
		@Pc(13) int resolvedSequenceId = sequenceId;
		@Pc(15) boolean replaySequence = false;
		if (sequenceId == -1) {
			@Pc(26) LocType locType = TypeLists.LOC.list(this.id);
			@Pc(28) LocType locType2 = locType;
			if (locType.alternativeIds != null) {
				locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider);
			}
			if (locType == null) {
				return;
			}
			if (locType2 == locType) {
				locType2 = null;
			}
			if (locType.animations != null) {
				if (this.sequence != null && locType.hasAnimation(this.sequence.id)) {
					return;
				}
				resolvedSequenceId = locType.randomAnimation();
				if (locType.id != this.lastLocId) {
					replaySequence = locType.replaySequence;
				}
			} else if (locType.animationId == -1) {
				if (locType2 != null && locType2.animations != null) {
					if (this.sequence != null && locType2.hasAnimation(this.sequence.id)) {
						return;
					}
					resolvedSequenceId = locType2.randomAnimation();
					if (this.lastLocId != locType2.id) {
						replaySequence = locType2.replaySequence;
					}
				} else if (locType2 != null && locType2.animationId != -1 && locType2.id != this.lastLocId) {
					replaySequence = locType2.replaySequence;
					resolvedSequenceId = locType2.animationId;
				}
			} else if (locType.id != this.lastLocId) {
				resolvedSequenceId = locType.animationId;
				replaySequence = locType.replaySequence;
			}
		}
		if (resolvedSequenceId == -1) {
			this.sequence = null;
			return;
		}
		this.model = null;
		if (this.sequence == null || resolvedSequenceId != this.sequence.id) {
			this.sequence = TypeLists.SEQ.list(resolvedSequenceId);
		} else if (this.sequence.loopType == 0) {
			return;
		}
		if (this.sequence.frames == null) {
			this.sequence = null;
			return;
		}
		if (replaySequence) {
			this.currentFrame = (int) ((double) this.sequence.frames.length * Math.random());
			this.frameCycle = (int) ((double) this.sequence.durations[this.currentFrame] * Math.random()) + 1;
		} else {
			this.currentFrame = 0;
			this.frameCycle = 1;
		}
		this.nextFrame = this.currentFrame + 1;
		if (this.nextFrame < 0 || this.sequence.frames.length <= this.nextFrame) {
			this.nextFrame = -1;
		}
		this.frameStartCycle = ClientState.clientCycle - this.frameCycle;
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(BLclient!ha;)V")
	public void refreshShadow(@OriginalArg(1) Toolkit toolkit) {
		this.getModel(true, true, 262144, toolkit);
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(ZZIILclient!ha;)Lclient!ka;")
	public Model getModel(@OriginalArg(0) boolean shadowOnly, @OriginalArg(1) boolean castShadow, @OriginalArg(3) int functionMask, @OriginalArg(4) Toolkit toolkit) {
		@Pc(11) LocType locType = TypeLists.LOC.list(this.id);
		if (locType.alternativeIds != null) {
			locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider);
		}
		if (locType == null) {
			this.removeShadow(toolkit);
			this.shadowFrame = -1;
			this.shadowLocId = -1;
			this.lastLocId = -1;
			return null;
		}
		if (!this.hasSequenceId && this.lastLocId != locType.id) {
			this.model = null;
			this.updateSequence(-1);
		}
		this.progressAnimation(this.entity);
		if (castShadow) {
			@Pc(76) boolean shadowEnabled = castShadow & this.hardShadow & GamePreferences.uid.hardShadows.getValue() != 0;
			castShadow = shadowEnabled & (locType.id != this.shadowLocId || this.sequence != null && GamePreferences.uid.hardShadows.getValue() >= 2 && (this.shadowFrame != this.currentFrame || (this.sequence.shadowTweened || SeqType.forceTweening) && this.currentFrame != this.nextFrame));
		}
		if (shadowOnly && !castShadow) {
			this.lastLocId = locType.id;
			return null;
		}
		if (castShadow) {
			Scene.removeShadow(this.shadow, this.virtualLevel, this.entity.x, this.entity.z, this.castShadowAtPlane);
			this.shadowLocId = -1;
			this.shadowFrame = -1;
		}
		@Pc(161) Ground ground = Scene.aClass65Array1[this.virtualLevel];
		@Pc(177) Ground ground2;
		if (this.underwater) {
			ground2 = Scene.aClass65Array2[0];
		} else {
			ground2 = this.virtualLevel >= 3 ? null : Scene.aClass65Array1[this.virtualLevel + 1];
		}
		@Pc(185) Model model = null;
		if (this.sequence != null) {
			if (castShadow) {
				functionMask |= 0x40000;
			}
			model = locType.getAnimatedModel(this.nextFrame, this.frameCycle, this.shape == 11 ? 10 : this.shape, ground.averageHeight(this.entity.x, this.entity.z), toolkit, this.shape == 11 ? this.rotation + 4 : this.rotation, functionMask, ground2, this.entity.z, this.currentFrame, this.entity.x, this.sequence, ground);
			if (model == null) {
				this.modelRadius = 0;
				this.castShadowAtPlane = null;
				this.modelMinY = 0;
				this.shadow = null;
			} else {
				if (castShadow) {
					if (this.castShadowAtPlane == null) {
						this.castShadowAtPlane = new boolean[4];
					}
					this.shadow = model.ba(this.shadow);
					Shadow.renderShadows(this.shadow, this.virtualLevel, this.entity.x, this.entity.z, this.castShadowAtPlane);
					this.shadowFrame = this.currentFrame;
					this.shadowLocId = locType.id;
				}
				this.modelMinY = model.fa();
				this.modelRadius = model.ma();
			}
			this.model = null;
		} else if (this.model != null && functionMask == (functionMask & this.model.ua()) && locType.id == this.lastLocId) {
			model = this.model;
		} else {
			if (this.model != null) {
				functionMask |= this.model.ua();
			}
			@Pc(261) ModelAndShadow modelAndShadow = locType.getModelAndShadow(toolkit, ground2, this.shape == LocShapes.CENTREPIECE_DIAGONAL ? 10 : this.shape, functionMask, this.entity.x, castShadow, ground, ground.averageHeight(this.entity.x, this.entity.z), this.entity.z, this.shape == 11 ? this.rotation + 4 : this.rotation);
			if (modelAndShadow == null) {
				this.modelRadius = 0;
				this.castShadowAtPlane = null;
				this.shadow = null;
				this.modelMinY = 0;
				this.model = null;
			} else {
				model = modelAndShadow.model;
				this.model = modelAndShadow.model;
				if (castShadow) {
					this.castShadowAtPlane = null;
					this.shadow = modelAndShadow.shadow;
					Shadow.renderShadows(this.shadow, this.virtualLevel, this.entity.x, this.entity.z, null);
					this.shadowFrame = -1;
					this.shadowLocId = locType.id;
				}
				this.modelMinY = model.fa();
				this.modelRadius = model.ma();
			}
		}
		this.lastLocId = locType.id;
		return model;
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(ILclient!bca;ILclient!ha;ILclient!ka;ZBI)V")
	public void animateParticles(@OriginalArg(0) int endX, @OriginalArg(1) Matrix matrix, @OriginalArg(2) int endZ, @OriginalArg(3) Toolkit toolkit, @OriginalArg(4) int startX, @OriginalArg(5) Model model, @OriginalArg(6) boolean performCycle, @OriginalArg(8) int startZ) {
		@Pc(12) EmissiveTriangle[] modelParticleEmitters = model.getEmissiveTriangles();
		@Pc(15) EffectiveVertex[] modelParticleEffectors = model.getEffectiveVertices();
		if ((this.particleSystem == null || this.particleSystem.removed) && (modelParticleEmitters != null || modelParticleEffectors != null)) {
			@Pc(32) LocType locType = TypeLists.LOC.list(this.id);
			if (locType.alternativeIds != null) {
				locType = locType.getMultiLoc(PlayerVarDomain.playerVarProvider);
			}
			if (locType != null) {
				this.particleSystem = ParticleSystem.get(ClientState.clientCycle, true);
			}
		}
		if (this.particleSystem == null) {
			return;
		}
		model.updateParticlePosition(matrix);
		if (performCycle) {
			this.particleSystem.update(toolkit, (long) ClientState.clientCycle, modelParticleEmitters, modelParticleEffectors);
		} else {
			this.particleSystem.setClock((long) ClientState.clientCycle);
		}
		this.particleSystem.updateBounds(this.level, startX, endX, startZ, endZ);
	}

	@OriginalMember(owner = "client!bv", name = "b", descriptor = "(II)V")
	public void playSequence(@OriginalArg(1) int sequenceId) {
		this.hasSequenceId = true;
		this.updateSequence(sequenceId);
	}

	@OriginalMember(owner = "client!bv", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() {
		if (this.particleSystem != null) {
			this.particleSystem.stopped();
		}
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(Lclient!ga;Z)V")
	private void progressAnimation(@OriginalArg(0) Entity entity) {
		label80: while (true) {
			if (this.sequence == null) {
				if (this.hasSequenceId) {
					return;
				}
				this.updateSequence(-1);
				if (this.sequence == null) {
					return;
				}
			}
			@Pc(33) int elapsed = ClientState.clientCycle - this.frameStartCycle;
			if (elapsed > 100 && this.sequence.replayInterval > 0) {
				@Pc(50) int replayStart = this.sequence.frames.length - this.sequence.replayInterval;
				while (this.currentFrame < replayStart && elapsed > this.sequence.durations[this.currentFrame]) {
					elapsed -= this.sequence.durations[this.currentFrame];
					this.currentFrame++;
				}
				if (this.currentFrame >= replayStart) {
					@Pc(93) int loopDuration = 0;
					for (@Pc(95) int i = replayStart; i < this.sequence.frames.length; i++) {
						loopDuration += this.sequence.durations[i];
					}
					elapsed %= loopDuration;
				}
				this.nextFrame = this.currentFrame + 1;
				if (this.nextFrame >= this.sequence.frames.length) {
					this.nextFrame -= this.sequence.replayInterval;
					if (this.nextFrame < 0 || this.sequence.frames.length <= this.nextFrame) {
						this.nextFrame = -1;
					}
				}
			}
			while (elapsed > this.sequence.durations[this.currentFrame]) {
				SoundManager.playSequenceSound(entity, this.currentFrame, this.sequence);
				elapsed -= this.sequence.durations[this.currentFrame];
				this.currentFrame++;
				if (this.sequence.frames.length <= this.currentFrame) {
					this.currentFrame -= this.sequence.replayInterval;
					if (this.currentFrame < 0 || this.sequence.frames.length <= this.currentFrame) {
						this.sequence = null;
						continue label80;
					}
				}
				this.nextFrame = this.currentFrame + 1;
				if (this.sequence.frames.length <= this.nextFrame) {
					this.nextFrame -= this.sequence.replayInterval;
					if (this.nextFrame < 0 || this.nextFrame >= this.sequence.frames.length) {
						this.nextFrame = -1;
					}
				}
			}
			this.frameCycle = elapsed;
			this.frameStartCycle = ClientState.clientCycle - elapsed;
			return;
		}
	}

	@OriginalMember(owner = "client!bv", name = "a", descriptor = "(ILclient!ha;)V")
	public void removeShadow(@OriginalArg(1) Toolkit toolkit) {
		if (this.shadow != null) {
			Scene.removeShadow(this.shadow, this.virtualLevel, this.entity.x, this.entity.z, this.castShadowAtPlane);
			this.shadow = null;
			this.castShadowAtPlane = null;
		}
	}
}
