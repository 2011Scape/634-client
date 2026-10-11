package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationType;
import com.jagex.game.runetek5.graphics.model.particle.Silhouette;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex;
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.ParticleList;
import com.jagex.game.runetek5.PlayerInfoBitDecoder;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.config.sequence.PositionedGfx;
import com.jagex.game.runetek5.rendering.Renderers;
import com.jagex.game.runetek5.SoundManager;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.OrthoMode;

@OriginalClass("client!lf")
public final class SpotAnimation extends AbstractInteractiveEntity {

	@OriginalMember(owner = "client!lf", name = "eb", descriptor = "Lclient!uj;")
	private ParticleSystem particleSystem;

	@OriginalMember(owner = "client!lf", name = "Y", descriptor = "I")
	private int frame = 0;

	@OriginalMember(owner = "client!lf", name = "cb", descriptor = "I")
	private int frameCycle = 0;

	@OriginalMember(owner = "client!lf", name = "bb", descriptor = "Z")
	private boolean transparent = true;

	@OriginalMember(owner = "client!lf", name = "ib", descriptor = "Z")
	public boolean finished = false;

	@OriginalMember(owner = "client!lf", name = "U", descriptor = "I")
	private int objectMinY = 0;

	@OriginalMember(owner = "client!lf", name = "X", descriptor = "I")
	private int rotation = 0;

	@OriginalMember(owner = "client!lf", name = "nb", descriptor = "I")
	private final int nextFrame = -1;

	@OriginalMember(owner = "client!lf", name = "tb", descriptor = "I")
	private int minY = 0;

	@OriginalMember(owner = "client!lf", name = "sb", descriptor = "I")
	private final int id;

	@OriginalMember(owner = "client!lf", name = "mb", descriptor = "I")
	public final int startCycle;

	@OriginalMember(owner = "client!lf", name = "V", descriptor = "Lclient!hg;")
	private SeqType seqType;

	@OriginalMember(owner = "client!lf", name = "<init>", descriptor = "(IIIIIIIIIIIII)V")
	public SpotAnimation(@OriginalArg(0) int id, @OriginalArg(1) int delay, @OriginalArg(2) int currentCycle, @OriginalArg(3) int level, @OriginalArg(4) int virtualLevel, @OriginalArg(5) int x, @OriginalArg(6) int y, @OriginalArg(7) int z, @OriginalArg(8) int startX, @OriginalArg(9) int endX, @OriginalArg(10) int startZ, @OriginalArg(11) int endZ, @OriginalArg(12) int rotation) {
		super(level, virtualLevel, x, y, z, startX, endX, startZ, endZ, false, (byte) 0);
		this.id = id;
		this.rotation = rotation;
		this.startCycle = currentCycle + delay;
		@Pc(53) SpotAnimationType spotAnimationType = TypeLists.SPOT_ANIM.list(this.id);
		@Pc(56) int sequenceId = spotAnimationType.sequenceId;
		if (sequenceId == -1) {
			this.finished = true;
		} else {
			this.seqType = TypeLists.SEQ.list(sequenceId);
			this.finished = false;
		}
		if (this.startCycle == currentCycle) {
			SoundManager.playSequenceSound(this, this.frame, this.seqType);
		}
	}

	@OriginalMember(owner = "client!lf", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	@Override
	public PickableEntity render(@OriginalArg(1) Toolkit toolkit) {
		@Pc(23) Model model = this.generateGfxModel(toolkit, this.id, (this.rotation == 0 ? 0 : 5) | 0x800);
		if (model == null) {
			return null;
		}
		if (this.rotation != 0) {
			model.a(this.rotation * 2048);
		}
		@Pc(39) Matrix matrix = toolkit.scratchMatrix();
		matrix.applyTranslation(super.x, super.y, super.z);
		this.updateParticles(model, toolkit, matrix);
		@Pc(58) PickableEntity pickableEntity = PickableEntity.obtain(1, false);
		if (OrthoMode.orthographicProjection) {
			model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons[0], HintArrow.scaledOrthoZoom, 0);
		} else {
			model.render(matrix, pickableEntity.boundingBoxPolygons[0], 0);
		}
		if (this.particleSystem != null) {
			@Pc(86) ParticleList particles = this.particleSystem.getList();
			if (OrthoMode.orthographicProjection) {
				toolkit.renderOrtho(particles, HintArrow.scaledOrthoZoom);
			} else {
				toolkit.render(particles);
			}
		}
		this.transparent = model.F();
		this.objectMinY = model.fa();
		this.minY = model.ma();
		return pickableEntity;
	}

	@OriginalMember(owner = "client!lf", name = "c", descriptor = "(Z)I")
	@Override
	public int getModelMinY() {
		return this.objectMinY;
	}

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	@Override
	public void shareLight(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int offsetY, @OriginalArg(2) boolean hideSharedFaces, @OriginalArg(3) Entity entity, @OriginalArg(4) int offsetZ, @OriginalArg(6) int offsetX) {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!lf", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() {
		if (this.particleSystem != null) {
			this.particleSystem.stopped();
		}
	}

	@OriginalMember(owner = "client!lf", name = "b", descriptor = "(Z)V")
	@Override
	public void removeTileShadow() {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!lf", name = "g", descriptor = "(I)Z")
	@Override
	public boolean isStationary() {
		return false;
	}

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(Lclient!ha;IIB)Lclient!ka;")
	private Model generateGfxModel(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int currentId, @OriginalArg(2) int functionMask) {
		@Pc(13) SpotAnimationType spotAnimationType = TypeLists.SPOT_ANIM.list(currentId);
		@Pc(18) Ground ground = Scene.aClass65Array2[super.level];
		@Pc(33) Ground ground2 = super.virtualLevel < 3 ? Scene.aClass65Array2[super.virtualLevel + 1] : null;
		return this.finished ? spotAnimationType.getContouredModel(0, -1, super.y, functionMask, toolkit, -1, super.x, TypeLists.SEQ, ground2, super.z, ground) : spotAnimationType.getContouredModel(this.frameCycle, this.frame, super.y, functionMask, toolkit, this.nextFrame, super.x, TypeLists.SEQ, ground2, super.z, ground);
	}

	@OriginalMember(owner = "client!lf", name = "e", descriptor = "(Z)V")
	public void stopParticleSystem() {
		if (this.particleSystem != null) {
			this.particleSystem.stopped();
		}
	}

	@OriginalMember(owner = "client!lf", name = "b", descriptor = "(B)Z")
	@Override
	public boolean hasAlpha() {
		return this.transparent;
	}

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(Lclient!ka;Lclient!ha;ILclient!bca;)V")
	private void updateParticles(@OriginalArg(0) Model model, @OriginalArg(1) Toolkit toolkit, @OriginalArg(3) Matrix matrix) {
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

	@OriginalMember(owner = "client!lf", name = "i", descriptor = "(I)I")
	@Override
	public int getMinY(@OriginalArg(0) int check) {
		if (check != -25675) {
			this.rotation = 73;
		}
		return this.minY;
	}

	@OriginalMember(owner = "client!lf", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	@Override
	public Silhouette getCylinder(@OriginalArg(0) Toolkit toolkit) {
		return null;
	}

	@OriginalMember(owner = "client!lf", name = "j", descriptor = "(I)Z")
	@Override
	public boolean hasTileShadow() {
		return false;
	}

	@OriginalMember(owner = "client!lf", name = "c", descriptor = "(Lclient!ha;I)V")
	@Override
	public void particleCycle(@OriginalArg(0) Toolkit toolkit) {
		@Pc(17) Model model = this.generateGfxModel(toolkit, this.id, 0);
		if (model != null) {
			@Pc(22) Matrix matrix = toolkit.scratchMatrix();
			matrix.applyTranslation(super.x, super.y, super.z);
			this.updateParticles(model, toolkit, matrix);
		}
	}

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(Lclient!ha;III)Z")
	@Override
	public boolean picked(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int mouseY, @OriginalArg(2) int mouseX) {
		return false;
	}

	@OriginalMember(owner = "client!lf", name = "a", descriptor = "(IB)V")
	public void tick(@OriginalArg(0) int time) {
		if (this.finished) {
			return;
		}
		this.frameCycle += time;
		while (this.frameCycle > this.seqType.durations[this.frame]) {
			this.frameCycle -= this.seqType.durations[this.frame];
			this.frame++;
			if (this.seqType.frames.length <= this.frame) {
				this.finished = true;
				break;
			}
		}
		if (!this.finished) {
			SoundManager.playSequenceSound(this, this.frame, this.seqType);
		}
	}
	@OriginalMember(owner = "client!mo", name = "a", descriptor = "(I)V")
	public static void addSpotAnimationsToScene() {
		for (@Pc(10) PositionedGfx spotAnimationNode = (PositionedGfx) Renderers.activeToolkit.first(); spotAnimationNode != null; spotAnimationNode = (PositionedGfx) Renderers.activeToolkit.next()) {
			@Pc(15) SpotAnimation spotAnimation = spotAnimationNode.objectGfx;
			if (spotAnimation.finished) {
				spotAnimationNode.unlink();
				spotAnimation.stopParticleSystem();
			} else if (spotAnimation.startCycle <= ClientState.clientCycle) {
				spotAnimation.tick(ClientState.animatingStep);
				if (spotAnimation.finished) {
					spotAnimationNode.unlink();
				} else {
					Scene.addInteractive(spotAnimation, true);
				}
			}
		}
	}
}
