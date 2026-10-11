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

@OriginalClass("client!fea")
public final class DynamicLocation extends AbstractInteractiveEntity implements GameObject {

	@OriginalMember(owner = "client!fea", name = "eb", descriptor = "Lclient!hw;")
	private Silhouette cylinder;

	@OriginalMember(owner = "client!fea", name = "ib", descriptor = "Z")
	private boolean transparent = false;

	@OriginalMember(owner = "client!fea", name = "Y", descriptor = "Lclient!bv;")
	public final LocEntity locEntity;

	@OriginalMember(owner = "client!fea", name = "jb", descriptor = "Z")
	private final boolean interactive;

	@OriginalMember(owner = "client!fea", name = "<init>", descriptor = "(Lclient!ha;Lclient!iv;IIIIIZIIIIIII)V")
	public DynamicLocation(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) LocType locType, @OriginalArg(2) int level, @OriginalArg(3) int virtualLevel, @OriginalArg(4) int x, @OriginalArg(5) int y, @OriginalArg(6) int z, @OriginalArg(7) boolean underwater, @OriginalArg(8) int minTileX, @OriginalArg(9) int maxTileX, @OriginalArg(10) int minTileZ, @OriginalArg(11) int maxTileZ, @OriginalArg(12) int shape, @OriginalArg(13) int rotation, @OriginalArg(14) int animationId) {
		super(level, virtualLevel, x, y, z, minTileX, maxTileX, minTileZ, maxTileZ, locType.supportsItems == 1, LocShapes.getDiagonalRotationFlag(rotation, shape));
		this.locEntity = new LocEntity(toolkit, locType, shape, rotation, super.level, virtualLevel, this, underwater, animationId);
		this.interactive = locType.hasActions != LocInteractivity.NONINTERACTIVE && !underwater;
	}

	@OriginalMember(owner = "client!fea", name = "g", descriptor = "(I)Z")
	@Override
	public boolean isStationary() {
		return false;
	}

	@OriginalMember(owner = "client!fea", name = "i", descriptor = "(I)I")
	@Override
	public int getMinY(@OriginalArg(0) int check) {
		return check == -25675 ? this.locEntity.getModelRadius(check ^ 0x6462) : -125;
	}

	@OriginalMember(owner = "client!fea", name = "j", descriptor = "(I)Z")
	@Override
	public boolean hasTileShadow() {
		return false;
	}

	@OriginalMember(owner = "client!fea", name = "c", descriptor = "(I)V")
	@Override
	public void upload() {
	}

	@OriginalMember(owner = "client!fea", name = "a", descriptor = "(Lclient!ha;III)Z")
	@Override
	public boolean picked(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int mouseY, @OriginalArg(2) int mouseX) {
		@Pc(14) Model model = this.locEntity.getModel(false, false, 131072, toolkit);
		if (model == null) {
			return false;
		} else {
			@Pc(25) Matrix matrix = toolkit.scratchMatrix();
			matrix.applyTranslation(super.x, super.y, super.z);
			return OrthoMode.orthographicProjection ? model.pickedOrtho(mouseX, mouseY, matrix, false, 0, HintArrow.scaledOrthoZoom) : model.picked(mouseX, mouseY, matrix, false, 0);
		}
	}

	@OriginalMember(owner = "client!fea", name = "d", descriptor = "(I)I")
	@Override
	public int getRotation() {
		return this.locEntity.rotation;
	}

	@OriginalMember(owner = "client!fea", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	@Override
	public Silhouette getCylinder(@OriginalArg(0) Toolkit toolkit) {
		return this.cylinder;
	}

	@OriginalMember(owner = "client!fea", name = "b", descriptor = "(B)Z")
	@Override
	public boolean hasAlpha() {
		return this.transparent;
	}

	@OriginalMember(owner = "client!fea", name = "a", descriptor = "(Lclient!ha;I)V")
	@Override
	public void removeShadow(@OriginalArg(0) Toolkit toolkit) {
		this.locEntity.removeShadow(toolkit);
	}

	@OriginalMember(owner = "client!fea", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	@Override
	public PickableEntity render(@OriginalArg(1) Toolkit toolkit) {
		@Pc(14) Model model = this.locEntity.getModel(false, true, 2048, toolkit);
		if (model == null) {
			return null;
		}
		@Pc(21) Matrix matrix = toolkit.scratchMatrix();
		matrix.applyTranslation(super.x, super.y, super.z);
		@Pc(35) PickableEntity pickableEntity = PickableEntity.obtain(1, this.interactive);
		this.locEntity.animateParticles(super.endX, matrix, super.endZ, toolkit, super.startX, model, true, super.startZ);
		if (OrthoMode.orthographicProjection) {
			model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons[0], HintArrow.scaledOrthoZoom, 0);
		} else {
			model.render(matrix, pickableEntity.boundingBoxPolygons[0], 0);
		}
		if (this.locEntity.particleSystem != null) {
			@Pc(81) ParticleList particles = this.locEntity.particleSystem.getList();
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

	@OriginalMember(owner = "client!fea", name = "b", descriptor = "(I)I")
	@Override
	public int getShape() {
		return this.locEntity.shape;
	}

	@OriginalMember(owner = "client!fea", name = "b", descriptor = "(Z)V")
	@Override
	public void removeTileShadow() {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!fea", name = "e", descriptor = "(I)I")
	@Override
	public int getId() {
		return this.locEntity.id;
	}

	@OriginalMember(owner = "client!fea", name = "a", descriptor = "(ILclient!ha;)V")
	@Override
	public void addShadow(@OriginalArg(1) Toolkit toolkit) {
		this.locEntity.refreshShadow(toolkit);
	}

	@OriginalMember(owner = "client!fea", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	@Override
	public void shareLight(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int offsetY, @OriginalArg(2) boolean hideSharedFaces, @OriginalArg(3) Entity entity, @OriginalArg(4) int offsetZ, @OriginalArg(6) int offsetX) {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!fea", name = "a", descriptor = "(I)Z")
	@Override
	public boolean hardShadow() {
		return this.locEntity.hardShadow();
	}

	@OriginalMember(owner = "client!fea", name = "c", descriptor = "(Lclient!ha;I)V")
	@Override
	public void particleCycle(@OriginalArg(0) Toolkit toolkit) {
		@Pc(12) Model model = this.locEntity.getModel(true, true, 262144, toolkit);
		if (model != null) {
			@Pc(17) Matrix matrix = toolkit.scratchMatrix();
			matrix.applyTranslation(super.x, super.y, super.z);
			this.locEntity.animateParticles(super.endX, matrix, super.endZ, toolkit, super.startX, model, false, super.startZ);
		}
	}

	@OriginalMember(owner = "client!fea", name = "c", descriptor = "(Z)I")
	@Override
	public int getModelMinY() {
		return this.locEntity.getModelMinY();
	}
}
