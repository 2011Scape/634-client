package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.entity.gameobject.GameObject;
import com.jagex.game.runetek5.config.loctype.LocInteractivity;
import com.jagex.game.runetek5.graphics.model.particle.Silhouette;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.LocEntity;
import com.jagex.game.runetek5.LocType;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.ParticleList;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.core.constants.LocShapes;
import com.jagex.game.runetek5.OrthoMode;

@OriginalClass("client!ju")
public final class DynamicWall extends Wall implements GameObject {

	@OriginalMember(owner = "client!ju", name = "db", descriptor = "Lclient!hw;")
	private Silhouette cylinder;

	@OriginalMember(owner = "client!ju", name = "I", descriptor = "Z")
	private boolean transparent = false;

	@OriginalMember(owner = "client!ju", name = "cb", descriptor = "Lclient!bv;")
	public final LocEntity locEntity;

	@OriginalMember(owner = "client!ju", name = "bb", descriptor = "Z")
	private boolean interactive;

	@OriginalMember(owner = "client!ju", name = "<init>", descriptor = "(Lclient!ha;Lclient!iv;IIIIIZIII)V")
	public DynamicWall(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) LocType locType, @OriginalArg(2) int level, @OriginalArg(3) int virtualLevel, @OriginalArg(4) int x, @OriginalArg(5) int y, @OriginalArg(6) int z, @OriginalArg(7) boolean underwater, @OriginalArg(8) int shape, @OriginalArg(9) int rotation, @OriginalArg(10) int animationId) {
		super(x, y, z, level, virtualLevel, LocShapes.getDynamicWallSideMask(shape, rotation));
		this.locEntity = new LocEntity(toolkit, locType, shape, rotation, super.level, virtualLevel, this, underwater, animationId);
		this.interactive = locType.hasActions != LocInteractivity.NONINTERACTIVE && !underwater;
	}

	@OriginalMember(owner = "client!ju", name = "b", descriptor = "(I)I")
	@Override
	public int getShape() {
		return this.locEntity.shape;
	}

	@OriginalMember(owner = "client!ju", name = "a", descriptor = "(ILclient!ha;)V")
	@Override
	public void addShadow(@OriginalArg(1) Toolkit toolkit) {
		this.locEntity.refreshShadow(toolkit);
	}

	@OriginalMember(owner = "client!ju", name = "c", descriptor = "(Lclient!ha;I)V")
	@Override
	public void particleCycle(@OriginalArg(0) Toolkit toolkit) {
		@Pc(20) Model model = this.locEntity.getModel(true, true, 262144, toolkit);
		if (model == null) {
			return;
		}
		@Pc(27) int tileX = super.x >> 9;
		@Pc(32) int tileZ = super.z >> 9;
		@Pc(35) Matrix matrix = toolkit.scratchMatrix();
		matrix.applyTranslation(super.x, super.y, super.z);
		this.locEntity.animateParticles(tileX, matrix, tileZ, toolkit, tileX, model, false, tileZ);
	}

	@OriginalMember(owner = "client!ju", name = "c", descriptor = "(Z)I")
	@Override
	public int getModelMinY() {
		return this.locEntity.getModelMinY();
	}

	@OriginalMember(owner = "client!ju", name = "a", descriptor = "(Lclient!ha;III)Z")
	@Override
	public boolean picked(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int mouseY, @OriginalArg(2) int mouseX) {
		@Pc(12) Model model = this.locEntity.getModel(false, false, 131072, toolkit);
		if (model == null) {
			return false;
		} else {
			@Pc(19) Matrix matrix = toolkit.scratchMatrix();
			matrix.applyTranslation(super.x, super.y, super.z);
			return OrthoMode.orthographicProjection ? model.pickedOrtho(mouseX, mouseY, matrix, false, 0, HintArrow.scaledOrthoZoom) : model.picked(mouseX, mouseY, matrix, false, 0);
		}
	}

	@OriginalMember(owner = "client!ju", name = "g", descriptor = "(I)Z")
	@Override
	public boolean isStationary() {
		return false;
	}

	@OriginalMember(owner = "client!ju", name = "i", descriptor = "(I)I")
	@Override
	public int getMinY(@OriginalArg(0) int check) {
		if (check != -25675) {
			this.interactive = false;
		}
		return this.locEntity.getModelRadius(-71);
	}

	@OriginalMember(owner = "client!ju", name = "a", descriptor = "(I)Z")
	@Override
	public boolean hardShadow() {
		return this.locEntity.hardShadow();
	}

	@OriginalMember(owner = "client!ju", name = "e", descriptor = "(I)I")
	@Override
	public int getId() {
		return this.locEntity.id;
	}

	@OriginalMember(owner = "client!ju", name = "a", descriptor = "(Lclient!ha;I)V")
	@Override
	public void removeShadow(@OriginalArg(0) Toolkit toolkit) {
		this.locEntity.removeShadow(toolkit);
	}

	@OriginalMember(owner = "client!ju", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	@Override
	public PickableEntity render(@OriginalArg(1) Toolkit toolkit) {
		@Pc(16) Model model = this.locEntity.getModel(false, true, 2048, toolkit);
		if (model == null) {
			return null;
		}
		@Pc(23) Matrix matrix = toolkit.scratchMatrix();
		matrix.applyTranslation(super.x, super.y, super.z);
		@Pc(37) PickableEntity pickableEntity = PickableEntity.obtain(1, this.interactive);
		@Pc(42) int localX = super.x >> 9;
		@Pc(47) int localZ = super.z >> 9;
		this.locEntity.animateParticles(localX, matrix, localZ, toolkit, localX, model, true, localZ);
		if (OrthoMode.orthographicProjection) {
			model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons[0], HintArrow.scaledOrthoZoom, 0);
		} else {
			model.render(matrix, pickableEntity.boundingBoxPolygons[0], 0);
		}
		if (this.locEntity.particleSystem != null) {
			@Pc(89) ParticleList particles = this.locEntity.particleSystem.getList();
			if (OrthoMode.orthographicProjection) {
				toolkit.renderOrtho(particles, HintArrow.scaledOrthoZoom);
			} else {
				toolkit.render(particles);
			}
		}
		this.transparent = model.F() || this.locEntity.particleSystem != null;
		if (this.cylinder == null) {
			this.cylinder = Silhouette.fromModel(super.x, model, super.z, super.y);
		} else {
			Silhouette.updateFromModel(super.z, super.y, this.cylinder, model, super.x);
		}
		return pickableEntity;
	}

	@OriginalMember(owner = "client!ju", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	@Override
	public Silhouette getCylinder(@OriginalArg(0) Toolkit toolkit) {
		return this.cylinder;
	}

	@OriginalMember(owner = "client!ju", name = "d", descriptor = "(I)I")
	@Override
	public int getRotation() {
		return this.locEntity.rotation;
	}

	@OriginalMember(owner = "client!ju", name = "c", descriptor = "(I)V")
	@Override
	public void upload() {
	}

	@OriginalMember(owner = "client!ju", name = "j", descriptor = "(I)Z")
	@Override
	public boolean hasTileShadow() {
		return false;
	}

	@OriginalMember(owner = "client!ju", name = "b", descriptor = "(Z)V")
	@Override
	public void removeTileShadow() {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!ju", name = "b", descriptor = "(B)Z")
	@Override
	public boolean hasAlpha() {
		return this.transparent;
	}

	@OriginalMember(owner = "client!ju", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	@Override
	public void shareLight(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int offsetY, @OriginalArg(2) boolean hideSharedFaces, @OriginalArg(3) Entity entity, @OriginalArg(4) int offsetZ, @OriginalArg(6) int offsetX) {
		throw new IllegalStateException();
	}
}
