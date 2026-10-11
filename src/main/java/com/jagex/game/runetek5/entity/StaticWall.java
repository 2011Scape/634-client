package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.entity.gameobject.GameObject;
import com.jagex.game.runetek5.config.loctype.LocInteractivity;
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
import com.jagex.game.runetek5.core.constants.LocShapes;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.OrthoMode;

@OriginalClass("client!ge")
public final class StaticWall extends Wall implements GameObject {

	@OriginalMember(owner = "client!ge", name = "db", descriptor = "Lclient!hw;")
	private Silhouette cylinder;

	@OriginalMember(owner = "client!ge", name = "cb", descriptor = "S")
	private final short id;

	@OriginalMember(owner = "client!ge", name = "M", descriptor = "Z")
	private final boolean underwater;

	@OriginalMember(owner = "client!ge", name = "lb", descriptor = "Z")
	private boolean lb;

	@OriginalMember(owner = "client!ge", name = "X", descriptor = "Z")
	private final boolean interactive;

	@OriginalMember(owner = "client!ge", name = "ab", descriptor = "B")
	private byte shape;

	@OriginalMember(owner = "client!ge", name = "I", descriptor = "B")
	private final byte rotation;

	@OriginalMember(owner = "client!ge", name = "K", descriptor = "Z")
	private final boolean hardShadow;

	@OriginalMember(owner = "client!ge", name = "pb", descriptor = "Lclient!r;")
	private Shadow shadow;

	@OriginalMember(owner = "client!ge", name = "S", descriptor = "Lclient!ka;")
	public Model model;

	@OriginalMember(owner = "client!ge", name = "<init>", descriptor = "(Lclient!ha;Lclient!iv;IIIIIZIIZ)V")
	public StaticWall(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) LocType locType, @OriginalArg(2) int level, @OriginalArg(3) int virtualLevel, @OriginalArg(4) int x, @OriginalArg(5) int y, @OriginalArg(6) int z, @OriginalArg(7) boolean underwater, @OriginalArg(8) int shape, @OriginalArg(9) int rotation, @OriginalArg(10) boolean copyNormals) {
		super(x, y, z, level, virtualLevel, LocShapes.getWallSideMask(rotation, shape));
		this.id = (short) locType.id;
		this.underwater = underwater;
		this.lb = copyNormals;
		super.x = x;
		this.interactive = locType.hasActions != LocInteractivity.NONINTERACTIVE && !underwater;
		super.z = z;
		this.shape = (byte) shape;
		this.rotation = (byte) rotation;
		this.hardShadow = toolkit.hardShadow() && locType.dynamicShadow && !this.underwater && GamePreferences.uid.hardShadows.getValue() != 0;
		@Pc(69) int functionMask = 2048;
		if (this.lb) {
			functionMask |= 0x10000;
		}
		@Pc(84) ModelAndShadow modelAndShadow = this.getModelAndShadow(this.hardShadow, functionMask, toolkit);
		if (modelAndShadow != null) {
			this.shadow = modelAndShadow.shadow;
			this.model = modelAndShadow.model;
			if (this.lb) {
				this.model = this.model.copy((byte) 0, functionMask, false);
				return;
			}
		}
	}

	@OriginalMember(owner = "client!ge", name = "b", descriptor = "(I)I")
	@Override
	public int getShape() {
		return this.shape;
	}

	@OriginalMember(owner = "client!ge", name = "c", descriptor = "(I)V")
	@Override
	public void upload() {
		if (this.model != null) {
			this.model.uploadToGpu();
		}
	}

	@OriginalMember(owner = "client!ge", name = "a", descriptor = "(Lclient!ha;III)Z")
	@Override
	public boolean picked(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int mouseY, @OriginalArg(2) int mouseX) {
		@Pc(9) Model model = this.getModel(toolkit, 131072);
		if (model == null) {
			return false;
		} else {
			@Pc(20) Matrix matrix = toolkit.scratchMatrix();
			matrix.applyTranslation(super.x, super.y, super.z);
			return OrthoMode.orthographicProjection ? model.pickedOrtho(mouseX, mouseY, matrix, false, 0, HintArrow.scaledOrthoZoom) : model.picked(mouseX, mouseY, matrix, false, 0);
		}
	}

	@OriginalMember(owner = "client!ge", name = "c", descriptor = "(Lclient!ha;I)V")
	@Override
	public void particleCycle(@OriginalArg(0) Toolkit toolkit) {
	}

	@OriginalMember(owner = "client!ge", name = "d", descriptor = "(I)I")
	@Override
	public int getRotation() {
		return this.rotation;
	}

	@OriginalMember(owner = "client!ge", name = "b", descriptor = "(Z)V")
	@Override
	public void removeTileShadow() {
		this.lb = false;
		if (this.model != null) {
			this.model.s(this.model.ua() & 0xFFFEFFFF);
		}
	}

	@OriginalMember(owner = "client!ge", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	@Override
	public Silhouette getCylinder(@OriginalArg(0) Toolkit toolkit) {
		if (this.cylinder == null) {
			this.cylinder = Silhouette.fromModel(super.x, this.getModel(toolkit, 0), super.z, super.y);
		}
		return this.cylinder;
	}

	@OriginalMember(owner = "client!ge", name = "e", descriptor = "(I)I")
	@Override
	public int getId() {
		return this.id & 0xFFFF;
	}

	@OriginalMember(owner = "client!ge", name = "i", descriptor = "(I)I")
	@Override
	public int getMinY(@OriginalArg(0) int check) {
		if (check != -25675) {
			this.shape = 53;
		}
		return this.model == null ? 0 : this.model.ma();
	}

	@OriginalMember(owner = "client!ge", name = "a", descriptor = "(I)Z")
	@Override
	public boolean hardShadow() {
		return this.hardShadow;
	}

	@OriginalMember(owner = "client!ge", name = "a", descriptor = "(Lclient!ha;IZLclient!ga;IBI)V")
	@Override
	public void shareLight(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int offsetY, @OriginalArg(2) boolean hideSharedFaces, @OriginalArg(3) Entity entity, @OriginalArg(4) int offsetZ, @OriginalArg(6) int offsetX) {
		if (entity instanceof StaticWall) {
			@Pc(12) StaticWall staticWall = (StaticWall) entity;
			if (this.model != null && staticWall.model != null) {
				this.model.shareLight(staticWall.model, offsetX, offsetY, offsetZ, hideSharedFaces);
			}
		} else if (entity instanceof StaticLocation) {
			@Pc(35) StaticLocation staticLocation = (StaticLocation) entity;
			if (this.model != null && staticLocation.model != null) {
				this.model.shareLight(staticLocation.model, offsetX, offsetY, offsetZ, hideSharedFaces);
			}
		}
	}

	@OriginalMember(owner = "client!ge", name = "a", descriptor = "(ILclient!ha;)V")
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

	@OriginalMember(owner = "client!ge", name = "a", descriptor = "(ZIILclient!ha;)Lclient!ak;")
	private ModelAndShadow getModelAndShadow(@OriginalArg(0) boolean withShadow, @OriginalArg(1) int functionMask, @OriginalArg(3) Toolkit toolkit) {
		@Pc(19) LocType locType = TypeLists.LOC.list(this.id & 0xFFFF);
		@Pc(27) Ground ground;
		@Pc(40) Ground ground2;
		if (this.underwater) {
			ground2 = Scene.aClass65Array2[0];
			ground = Scene.aClass65Array3[super.virtualLevel];
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

	@OriginalMember(owner = "client!ge", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
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

	@OriginalMember(owner = "client!ge", name = "a", descriptor = "(Lclient!ha;BI)Lclient!ka;")
	private Model getModel(@OriginalArg(0) Toolkit toolkit, @OriginalArg(2) int functionMask) {
		if (this.model != null && toolkit.compareFunctionMasks(this.model.ua(), functionMask) == 0) {
			return this.model;
		} else {
			@Pc(33) ModelAndShadow modelAndShadow = this.getModelAndShadow(false, functionMask, toolkit);
			return modelAndShadow == null ? null : modelAndShadow.model;
		}
	}

	@OriginalMember(owner = "client!ge", name = "c", descriptor = "(Z)I")
	@Override
	public int getModelMinY() {
		return this.model == null ? 0 : this.model.fa();
	}

	@OriginalMember(owner = "client!ge", name = "j", descriptor = "(I)Z")
	@Override
	public boolean hasTileShadow() {
		return this.lb;
	}

	@OriginalMember(owner = "client!ge", name = "b", descriptor = "(B)Z")
	@Override
	public boolean hasAlpha() {
		return this.model == null ? false : this.model.F();
	}

	@OriginalMember(owner = "client!ge", name = "g", descriptor = "(I)Z")
	@Override
	public boolean isStationary() {
		if (this.model == null) {
			return true;
		} else {
			return !this.model.r();
		}
	}

	@OriginalMember(owner = "client!ge", name = "a", descriptor = "(Lclient!ha;I)V")
	@Override
	public void removeShadow(@OriginalArg(0) Toolkit toolkit) {
		@Pc(31) Shadow shadow;
		if (this.shadow == null && this.hardShadow) {
			@Pc(24) ModelAndShadow modelAndShadow = this.getModelAndShadow(true, 262144, toolkit);
			shadow = modelAndShadow == null ? null : modelAndShadow.shadow;
		} else {
			shadow = this.shadow;
			this.shadow = null;
		}
		if (shadow != null) {
			Scene.removeShadow(shadow, super.virtualLevel, super.x, super.z, null);
		}
	}
}
