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
import com.jagex.game.runetek5.OrthoMode;

@OriginalClass("client!pw")
public final class DynamicWallDecor extends WallDecor implements GameObject {

	@OriginalMember(owner = "client!pw", name = "O", descriptor = "Lclient!hw;")
	private Silhouette cylinder;

	@OriginalMember(owner = "client!pw", name = "A", descriptor = "Z")
	private boolean transparent = false;

	@OriginalMember(owner = "client!pw", name = "W", descriptor = "Lclient!bv;")
	public final LocEntity locEntity;

	@OriginalMember(owner = "client!pw", name = "U", descriptor = "Z")
	private boolean interactive;

	@OriginalMember(owner = "client!pw", name = "<init>", descriptor = "(Lclient!ha;Lclient!iv;IIIIIZIIIII)V")
	public DynamicWallDecor(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) LocType locType, @OriginalArg(2) int level, @OriginalArg(3) int virtualLevel, @OriginalArg(4) int x, @OriginalArg(5) int y, @OriginalArg(6) int z, @OriginalArg(7) boolean underwater, @OriginalArg(8) int offsetX, @OriginalArg(9) int offsetZ, @OriginalArg(10) int shape, @OriginalArg(11) int rotation, @OriginalArg(12) int animationId) {
		super(x, y, z, level, virtualLevel, offsetX, offsetZ);
		this.locEntity = new LocEntity(toolkit, locType, shape, rotation, super.level, virtualLevel, this, underwater, animationId);
		this.interactive = locType.hasActions != LocInteractivity.NONINTERACTIVE && !underwater;
	}

	@OriginalMember(owner = "client!pw", name = "a", descriptor = "(Lclient!ha;I)V")
	@Override
	public void removeShadow(@OriginalArg(0) Toolkit toolkit) {
		this.locEntity.removeShadow(toolkit);
	}

	@OriginalMember(owner = "client!pw", name = "c", descriptor = "(Z)I")
	@Override
	public int getModelMinY() {
		return this.locEntity.getModelMinY();
	}

	@OriginalMember(owner = "client!pw", name = "d", descriptor = "(I)I")
	@Override
	public int getRotation() {
		return this.locEntity.rotation;
	}

	@OriginalMember(owner = "client!pw", name = "a", descriptor = "(I)Z")
	@Override
	public boolean hardShadow() {
		return this.locEntity.hardShadow();
	}

	@OriginalMember(owner = "client!pw", name = "a", descriptor = "(ILclient!ha;)V")
	@Override
	public void addShadow(@OriginalArg(1) Toolkit toolkit) {
		this.locEntity.refreshShadow(toolkit);
	}

	@OriginalMember(owner = "client!pw", name = "e", descriptor = "(I)I")
	@Override
	public int getId() {
		return this.locEntity.id;
	}

	@OriginalMember(owner = "client!pw", name = "c", descriptor = "(Lclient!ha;I)V")
	@Override
	public void particleCycle(@OriginalArg(0) Toolkit toolkit) {
		@Pc(18) Model model = this.locEntity.getModel(false, true, 262144, toolkit);
		if (model == null) {
			return;
		}
		@Pc(25) int tileX = super.x >> 9;
		@Pc(30) int tileZ = super.z >> 9;
		@Pc(33) Matrix matrix = toolkit.scratchMatrix();
		matrix.applyTranslation(super.x, super.y, super.z);
		this.locEntity.animateParticles(tileX, matrix, tileZ, toolkit, tileX, model, false, tileZ);
	}

	@OriginalMember(owner = "client!pw", name = "b", descriptor = "(I)I")
	@Override
	public int getShape() {
		return this.locEntity.shape;
	}

	@OriginalMember(owner = "client!pw", name = "g", descriptor = "(I)Z")
	@Override
	public boolean isStationary() {
		return false;
	}

	@OriginalMember(owner = "client!pw", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	@Override
	public PickableEntity render(@OriginalArg(1) Toolkit toolkit) {
		@Pc(14) Model model = this.locEntity.getModel(false, true, 2048, toolkit);
		if (model == null) {
			return null;
		}
		@Pc(21) Matrix matrix = toolkit.scratchMatrix();
		matrix.applyTranslation(super.x + super.offsetX, super.y, super.z + super.offsetZ);
		@Pc(41) PickableEntity pickableEntity = PickableEntity.obtain(1, this.interactive);
		@Pc(46) int tileX = super.x >> 9;
		@Pc(51) int tileZ = super.z >> 9;
		this.locEntity.animateParticles(tileX, matrix, tileZ, toolkit, tileX, model, true, tileZ);
		if (OrthoMode.orthographicProjection) {
			model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons[0], HintArrow.scaledOrthoZoom, 0);
		} else {
			model.render(matrix, pickableEntity.boundingBoxPolygons[0], 0);
		}
		if (this.locEntity.particleSystem != null) {
			@Pc(93) ParticleList particleList = this.locEntity.particleSystem.getList();
			if (OrthoMode.orthographicProjection) {
				toolkit.renderOrtho(particleList, HintArrow.scaledOrthoZoom);
			} else {
				toolkit.render(particleList);
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

	@OriginalMember(owner = "client!pw", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	@Override
	public Silhouette getCylinder(@OriginalArg(0) Toolkit toolkit) {
		return this.cylinder;
	}

	@OriginalMember(owner = "client!pw", name = "b", descriptor = "(B)Z")
	@Override
	public boolean hasAlpha() {
		return this.transparent;
	}

	@OriginalMember(owner = "client!pw", name = "a", descriptor = "(Lclient!ha;III)Z")
	@Override
	public boolean picked(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int mouseY, @OriginalArg(2) int mouseX) {
		@Pc(12) Model model = this.locEntity.getModel(false, false, 131072, toolkit);
		if (model == null) {
			return false;
		} else {
			@Pc(23) Matrix matrix = toolkit.scratchMatrix();
			matrix.applyTranslation(super.offsetX + super.x, super.y, super.offsetZ + super.z);
			return OrthoMode.orthographicProjection ? model.pickedOrtho(mouseX, mouseY, matrix, false, 0, HintArrow.scaledOrthoZoom) : model.picked(mouseX, mouseY, matrix, false, 0);
		}
	}

	@OriginalMember(owner = "client!pw", name = "i", descriptor = "(I)I")
	@Override
	public int getMinY(@OriginalArg(0) int check) {
		if (check != -25675) {
			this.interactive = false;
		}
		return this.locEntity.getModelRadius(-119);
	}

	@OriginalMember(owner = "client!pw", name = "c", descriptor = "(I)V")
	@Override
	public void upload() {
	}
}
