package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.entity.gameobject.GameObject;
import com.jagex.game.runetek5.graphics.model.particle.Silhouette;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.ModelAndShadow;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.LocType;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.OrthoMode;

@OriginalClass("client!co")
public final class StaticWallDecor extends WallDecor implements GameObject {

	@OriginalMember(owner = "client!co", name = "Y", descriptor = "Lclient!hw;")
	private Silhouette cylinder;

	@OriginalMember(owner = "client!co", name = "bb", descriptor = "Z")
	private final boolean interactive;

	@OriginalMember(owner = "client!co", name = "qb", descriptor = "B")
	private final byte shape;

	@OriginalMember(owner = "client!co", name = "ab", descriptor = "S")
	private final short id;

	@OriginalMember(owner = "client!co", name = "jb", descriptor = "Z")
	private final boolean underwater;

	@OriginalMember(owner = "client!co", name = "A", descriptor = "B")
	private final byte rotation;

	@OriginalMember(owner = "client!co", name = "ib", descriptor = "Z")
	private final boolean hardShadow;

	@OriginalMember(owner = "client!co", name = "V", descriptor = "Lclient!ka;")
	private Model model;

	@OriginalMember(owner = "client!co", name = "Z", descriptor = "Lclient!r;")
	private Shadow shadow;

	@OriginalMember(owner = "client!co", name = "<init>", descriptor = "(Lclient!ha;Lclient!iv;IIIIIZIIII)V")
	public StaticWallDecor(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) LocType locType, @OriginalArg(2) int level, @OriginalArg(3) int virtualLevel, @OriginalArg(4) int x, @OriginalArg(5) int y, @OriginalArg(6) int z, @OriginalArg(7) boolean underwater, @OriginalArg(8) int offsetX, @OriginalArg(9) int offsetZ, @OriginalArg(10) int shape, @OriginalArg(11) int rotation) {
		super(x, y, z, level, virtualLevel, offsetX, offsetZ);
		this.interactive = locType.hasActions != 0 && !underwater;
		this.shape = (byte) shape;
		this.id = (short) locType.id;
		super.z = z;
		this.underwater = underwater;
		super.x = x;
		this.rotation = (byte) rotation;
		this.hardShadow = toolkit.hardShadow() && locType.dynamicShadow && !this.underwater && GamePreferences.uid.hardShadows.getValue() != 0;
		@Pc(70) ModelAndShadow modelAndShadow = this.getModelAndShadow(this.hardShadow, 2048, toolkit);
		if (modelAndShadow != null) {
			this.model = modelAndShadow.model;
			this.shadow = modelAndShadow.shadow;
		}
	}

	@OriginalMember(owner = "client!co", name = "a", descriptor = "(IILclient!ha;)Lclient!ka;")
	private Model getModel(@OriginalArg(0) int functionMask, @OriginalArg(2) Toolkit toolkit) {
		if (this.model != null && toolkit.compareFunctionMasks(this.model.ua(), functionMask) == 0) {
			return this.model;
		} else {
			@Pc(27) ModelAndShadow modelAndShadow = this.getModelAndShadow(false, functionMask, toolkit);
			return modelAndShadow == null ? null : modelAndShadow.model;
		}
	}

	@OriginalMember(owner = "client!co", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	@Override
	public PickableEntity render(@OriginalArg(1) Toolkit toolkit) {
		if (this.model == null) {
			return null;
		}
		@Pc(11) Matrix matrix = toolkit.scratchMatrix();
		matrix.applyTranslation(super.x + super.offsetX, super.y, super.z + super.offsetZ);
		@Pc(33) PickableEntity entity = PickableEntity.obtain(1, this.interactive);
		if (OrthoMode.orthographicProjection) {
			this.model.renderOrtho(matrix, entity.boundingBoxPolygons[0], HintArrow.scaledOrthoZoom, 0);
		} else {
			this.model.render(matrix, entity.boundingBoxPolygons[0], 0);
		}
		return entity;
	}

	@OriginalMember(owner = "client!co", name = "c", descriptor = "(I)V")
	@Override
	public void upload() {
		if (this.model != null) {
			this.model.uploadToGpu();
		}
	}

	@OriginalMember(owner = "client!co", name = "b", descriptor = "(B)Z")
	@Override
	public boolean hasAlpha() {
		return this.model == null ? false : this.model.F();
	}

	@OriginalMember(owner = "client!co", name = "e", descriptor = "(I)I")
	@Override
	public int getId() {
		return this.id & 0xFFFF;
	}

	@OriginalMember(owner = "client!co", name = "i", descriptor = "(I)I")
	@Override
	public int getMinY(@OriginalArg(0) int check) {
		if (check != -25675) {
			this.model = null;
		}
		return this.model == null ? 0 : this.model.ma();
	}

	@OriginalMember(owner = "client!co", name = "a", descriptor = "(Lclient!ha;III)Z")
	@Override
	public boolean picked(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int mouseY, @OriginalArg(2) int mouseX) {
		@Pc(9) Model model = this.getModel(131072, toolkit);
		if (model == null) {
			return false;
		} else {
			@Pc(14) Matrix matrix = toolkit.scratchMatrix();
			matrix.applyTranslation(super.x, super.y, super.z);
			return OrthoMode.orthographicProjection ? model.pickedOrtho(mouseX, mouseY, matrix, false, 0, HintArrow.scaledOrthoZoom) : model.picked(mouseX, mouseY, matrix, false, 0);
		}
	}

	@OriginalMember(owner = "client!co", name = "g", descriptor = "(I)Z")
	@Override
	public boolean isStationary() {
		if (this.model == null) {
			return true;
		} else {
			return !this.model.r();
		}
	}

	@OriginalMember(owner = "client!co", name = "b", descriptor = "(I)I")
	@Override
	public int getShape() {
		return this.shape;
	}

	@OriginalMember(owner = "client!co", name = "a", descriptor = "(Lclient!ha;I)V")
	@Override
	public void removeShadow(@OriginalArg(0) Toolkit toolkit) {
		@Pc(20) Shadow shadow;
		if (this.shadow == null && this.hardShadow) {
			@Pc(31) ModelAndShadow modelAndShadow = this.getModelAndShadow(true, 262144, toolkit);
			shadow = modelAndShadow == null ? null : modelAndShadow.shadow;
		} else {
			shadow = this.shadow;
			this.shadow = null;
		}
		if (shadow != null) {
			Scene.removeShadow(shadow, super.virtualLevel, super.x, super.z, null);
		}
	}

	@OriginalMember(owner = "client!co", name = "c", descriptor = "(Lclient!ha;I)V")
	@Override
	public void particleCycle(@OriginalArg(0) Toolkit toolkit) {
	}

	@OriginalMember(owner = "client!co", name = "a", descriptor = "(ILclient!ha;)V")
	@Override
	public void addShadow(@OriginalArg(1) Toolkit toolkit) {
		@Pc(25) Shadow shadow;
		if (this.shadow == null && this.hardShadow) {
			@Pc(18) ModelAndShadow modelAndShadow = this.getModelAndShadow(true, 262144, toolkit);
			shadow = modelAndShadow == null ? null : modelAndShadow.shadow;
		} else {
			shadow = this.shadow;
			this.shadow = null;
		}
		if (shadow != null) {
			Shadow.renderShadows(shadow, super.virtualLevel, super.x, super.z, null);
		}
	}

	@OriginalMember(owner = "client!co", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	@Override
	public Silhouette getCylinder(@OriginalArg(0) Toolkit toolkit) {
		if (this.cylinder == null) {
			this.cylinder = Silhouette.fromModel(super.x, this.getModel(0, toolkit), super.z, super.y);
		}
		return this.cylinder;
	}

	@OriginalMember(owner = "client!co", name = "a", descriptor = "(BZILclient!ha;)Lclient!ak;")
	private ModelAndShadow getModelAndShadow(@OriginalArg(1) boolean withShadow, @OriginalArg(2) int functionMask, @OriginalArg(3) Toolkit toolkit) {
		@Pc(18) LocType locType = TypeLists.LOC.list(this.id & 0xFFFF);
		@Pc(26) Ground ground;
		@Pc(39) Ground ground2;
		if (this.underwater) {
			ground = Scene.aClass65Array3[super.virtualLevel];
			ground2 = Scene.aClass65Array2[0];
		} else {
			ground = Scene.aClass65Array2[super.virtualLevel];
			if (super.virtualLevel < 3) {
				ground2 = Scene.aClass65Array2[super.virtualLevel + 1];
			} else {
				ground2 = null;
			}
		}
		return locType.getModelAndShadow(toolkit, ground2, this.shape, functionMask, super.x, withShadow, ground, super.y, super.z, this.rotation);
	}

	@OriginalMember(owner = "client!co", name = "c", descriptor = "(Z)I")
	@Override
	public int getModelMinY() {
		return this.model == null ? 0 : this.model.fa();
	}

	@OriginalMember(owner = "client!co", name = "d", descriptor = "(I)I")
	@Override
	public int getRotation() {
		return this.rotation;
	}

	@OriginalMember(owner = "client!co", name = "a", descriptor = "(I)Z")
	@Override
	public boolean hardShadow() {
		return this.hardShadow;
	}
}
