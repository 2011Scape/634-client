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
import com.jagex.game.runetek5.core.constants.LocShapes;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.OrthoMode;

@OriginalClass("client!dm")
public final class StaticLocation extends AbstractInteractiveEntity implements GameObject {

	@OriginalMember(owner = "client!dm", name = "lb", descriptor = "I")
	public static int lb = -60;

	@OriginalMember(owner = "client!dm", name = "nb", descriptor = "Lclient!hw;")
	private Silhouette cylinder;

	@OriginalMember(owner = "client!dm", name = "ub", descriptor = "S")
	private final short id;

	@OriginalMember(owner = "client!dm", name = "db", descriptor = "B")
	private final byte rotation;

	@OriginalMember(owner = "client!dm", name = "mb", descriptor = "B")
	private final byte shape;

	@OriginalMember(owner = "client!dm", name = "cb", descriptor = "Z")
	private final boolean interactive;

	@OriginalMember(owner = "client!dm", name = "fb", descriptor = "Z")
	private final boolean underwater;

	@OriginalMember(owner = "client!dm", name = "hb", descriptor = "Z")
	private boolean copyNormals;

	@OriginalMember(owner = "client!dm", name = "I", descriptor = "Z")
	private final boolean hardShadow;

	@OriginalMember(owner = "client!dm", name = "qb", descriptor = "Lclient!ka;")
	public Model model;

	@OriginalMember(owner = "client!dm", name = "yb", descriptor = "Lclient!r;")
	private Shadow shadow;

	@OriginalMember(owner = "client!dm", name = "<init>", descriptor = "(Lclient!ha;Lclient!iv;IIIIIZIIIIIIZ)V")
	public StaticLocation(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) LocType locType, @OriginalArg(2) int level, @OriginalArg(3) int virtualLevel, @OriginalArg(4) int x, @OriginalArg(5) int y, @OriginalArg(6) int z, @OriginalArg(7) boolean underwater, @OriginalArg(8) int startX, @OriginalArg(9) int endX, @OriginalArg(10) int startZ, @OriginalArg(11) int endZ, @OriginalArg(12) int shape, @OriginalArg(13) int rotation, @OriginalArg(14) boolean copyNormals) {
		super(level, virtualLevel, x, y, z, startX, endX, startZ, endZ, locType.supportsItems == 1, LocShapes.getDiagonalOrientation(rotation, shape));
		this.id = (short) locType.id;
		this.rotation = (byte) rotation;
		this.shape = (byte) shape;
		this.interactive = locType.hasActions != 0 && !underwater;
		this.underwater = underwater;
		this.copyNormals = copyNormals;
		super.virtualLevel = (byte) virtualLevel;
		this.hardShadow = toolkit.hardShadow() && locType.dynamicShadow && !this.underwater && GamePreferences.uid.hardShadows.getValue() != 0;
		@Pc(77) int functionMask = 2048;
		if (this.copyNormals) {
			functionMask |= 0x10000;
		}
		@Pc(92) ModelAndShadow modelAndShadow = this.getModelAndShadow(toolkit, functionMask, this.hardShadow);
		if (modelAndShadow != null) {
			this.model = modelAndShadow.model;
			this.shadow = modelAndShadow.shadow;
			if (this.copyNormals) {
				this.model = this.model.copy((byte) 0, functionMask, false);
				return;
			}
		}
	}

	@OriginalMember(owner = "client!dm", name = "c", descriptor = "(I)V")
	@Override
	public void upload() {
		if (this.model != null) {
			this.model.uploadToGpu();
		}
	}

	@OriginalMember(owner = "client!dm", name = "c", descriptor = "(Z)I")
	@Override
	public int getModelMinY() {
		return this.model == null ? 0 : this.model.fa();
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(ILclient!ha;)V")
	@Override
	public void addShadow(@OriginalArg(1) Toolkit toolkit) {
		@Pc(14) Shadow shadow;
		if (this.shadow == null && this.hardShadow) {
			@Pc(25) ModelAndShadow modelAndShadow = this.getModelAndShadow(toolkit, 262144, true);
			shadow = modelAndShadow == null ? null : modelAndShadow.shadow;
		} else {
			shadow = this.shadow;
			this.shadow = null;
		}
		if (shadow != null) {
			Shadow.renderShadows(shadow, super.virtualLevel, super.x, super.z, null);
		}
	}

	@OriginalMember(owner = "client!dm", name = "i", descriptor = "(I)I")
	@Override
	public int getMinY(@OriginalArg(0) int check) {
		if (check != -25675) {
			Utils.hashString(null);
		}
		return this.model == null ? 0 : this.model.ma();
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(I)Z")
	@Override
	public boolean hardShadow() {
		return this.hardShadow;
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(Lclient!ha;III)Z")
	@Override
	public boolean picked(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int mouseY, @OriginalArg(2) int mouseX) {
		@Pc(13) Model model = this.getModel(toolkit, 131072);
		if (model == null) {
			return false;
		} else {
			@Pc(18) Matrix matrix = toolkit.scratchMatrix();
			matrix.applyTranslation(super.x, super.y, super.z);
			return OrthoMode.orthographicProjection ? model.pickedOrtho(mouseX, mouseY, matrix, false, 0, HintArrow.scaledOrthoZoom) : model.picked(mouseX, mouseY, matrix, false, 0);
		}
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(ILclient!ha;I)Lclient!ka;")
	private Model getModel(@OriginalArg(1) Toolkit toolkit, @OriginalArg(2) int functionMask) {
		if (this.model != null && toolkit.compareFunctionMasks(this.model.ua(), functionMask) == 0) {
			return this.model;
		} else {
			@Pc(32) ModelAndShadow modelAndShadow = this.getModelAndShadow(toolkit, functionMask, false);
			return modelAndShadow == null ? null : modelAndShadow.model;
		}
	}

	@OriginalMember(owner = "client!dm", name = "c", descriptor = "(Lclient!ha;I)V")
	@Override
	public void particleCycle(@OriginalArg(0) Toolkit toolkit) {
	}

	@OriginalMember(owner = "client!dm", name = "n", descriptor = "(I)I")
	public int shadowValue() {
		return this.model == null ? 15 : this.model.na() / 4;
	}

	@OriginalMember(owner = "client!dm", name = "j", descriptor = "(I)Z")
	@Override
	public boolean hasTileShadow() {
		return this.copyNormals;
	}

	@OriginalMember(owner = "client!dm", name = "d", descriptor = "(I)I")
	@Override
	public int getRotation() {
		return this.rotation;
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	@Override
	public void shareLight(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int offsetY, @OriginalArg(2) boolean hideSharedFaces, @OriginalArg(3) Entity entity, @OriginalArg(4) int offsetZ, @OriginalArg(6) int offsetX) {
		if (entity instanceof StaticWall) {
			@Pc(9) StaticWall staticWall = (StaticWall) entity;
			if (this.model != null && staticWall.model != null) {
				this.model.shareLight(staticWall.model, offsetX, offsetY, offsetZ, hideSharedFaces);
			}
		} else if (entity instanceof StaticLocation) {
			@Pc(32) StaticLocation staticLocation = (StaticLocation) entity;
			if (this.model != null && staticLocation.model != null) {
				this.model.shareLight(staticLocation.model, offsetX, offsetY, offsetZ, hideSharedFaces);
			}
		}
	}

	@OriginalMember(owner = "client!dm", name = "e", descriptor = "(I)I")
	@Override
	public int getId() {
		return this.id & 0xFFFF;
	}

	@OriginalMember(owner = "client!dm", name = "b", descriptor = "(Z)V")
	@Override
	public void removeTileShadow() {
		this.copyNormals = false;
		if (this.model != null) {
			this.model.s(this.model.ua() & 0xFFFEFFFF);
		}
	}

	@OriginalMember(owner = "client!dm", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	@Override
	public Silhouette getCylinder(@OriginalArg(0) Toolkit toolkit) {
		if (this.cylinder == null) {
			this.cylinder = Silhouette.fromModel(super.x, this.getModel(toolkit, 0), super.z, super.y);
		}
		return this.cylinder;
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(ILclient!ha;IZ)Lclient!ak;")
	private ModelAndShadow getModelAndShadow(@OriginalArg(1) Toolkit toolkit, @OriginalArg(2) int functionMask, @OriginalArg(3) boolean withShadow) {
		@Pc(13) LocType locType = TypeLists.LOC.list(this.id & 0xFFFF);
		@Pc(21) Ground ground;
		@Pc(25) Ground ground2;
		if (this.underwater) {
			ground = Scene.aClass65Array3[super.virtualLevel];
			ground2 = Scene.aClass65Array2[0];
		} else {
			ground = Scene.aClass65Array2[super.virtualLevel];
			if (super.virtualLevel >= 3) {
				ground2 = null;
			} else {
				ground2 = Scene.aClass65Array2[super.virtualLevel + 1];
			}
		}
		return locType.getModelAndShadow(toolkit, ground2, this.shape == 11 ? 10 : this.shape, functionMask, super.x, withShadow, ground, super.y, super.z, this.shape == 11 ? this.rotation + 4 : this.rotation);
	}

	@OriginalMember(owner = "client!dm", name = "g", descriptor = "(I)Z")
	@Override
	public boolean isStationary() {
		if (this.model == null) {
			return true;
		} else {
			return !this.model.r();
		}
	}

	@OriginalMember(owner = "client!dm", name = "b", descriptor = "(B)Z")
	@Override
	public boolean hasAlpha() {
		return this.model == null ? false : this.model.F();
	}

	@OriginalMember(owner = "client!dm", name = "a", descriptor = "(Lclient!ha;I)V")
	@Override
	public void removeShadow(@OriginalArg(0) Toolkit toolkit) {
		@Pc(33) Shadow shadow;
		if (this.shadow == null && this.hardShadow) {
			@Pc(26) ModelAndShadow modelAndShadow = this.getModelAndShadow(toolkit, 262144, true);
			shadow = modelAndShadow == null ? null : modelAndShadow.shadow;
		} else {
			shadow = this.shadow;
			this.shadow = null;
		}
		if (shadow != null) {
			Scene.removeShadow(shadow, super.virtualLevel, super.x, super.z, null);
		}
	}

	@OriginalMember(owner = "client!dm", name = "b", descriptor = "(I)I")
	@Override
	public int getShape() {
		return this.shape;
	}

	@OriginalMember(owner = "client!dm", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	@Override
	public PickableEntity render(@OriginalArg(1) Toolkit toolkit) {
		if (this.model == null) {
			return null;
		}
		@Pc(11) Matrix matrix = toolkit.scratchMatrix();
		matrix.applyTranslation(super.x, super.y, super.z);
		@Pc(25) PickableEntity entity = PickableEntity.obtain(1, this.interactive);
		if (OrthoMode.orthographicProjection) {
			this.model.renderOrtho(matrix, entity.boundingBoxPolygons[0], HintArrow.scaledOrthoZoom, 0);
		} else {
			this.model.render(matrix, entity.boundingBoxPolygons[0], 0);
		}
		return entity;
	}
}
