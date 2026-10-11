package com.jagex.game.runetek5;

import com.jagex.game.runetek5.config.item.ItemType;
import com.jagex.game.runetek5.graphics.model.particle.Silhouette;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.InteractiveEntityContainer;
import com.jagex.game.runetek5.entity.PickableEntity;
import com.jagex.game.runetek5.entity.grounditem.AbstractGroundItem;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.cache.node.ObjStack;
import com.jagex.game.runetek5.cache.node.ObjStackEntry;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.scene.SettingsBits;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!mq")
public final class ObjStackEntity extends AbstractGroundItem {

	@OriginalMember(owner = "client!mq", name = "L", descriptor = "I")
	public int firstId;

	@OriginalMember(owner = "client!mq", name = "Q", descriptor = "I")
	public int firstCount;

	@OriginalMember(owner = "client!mq", name = "R", descriptor = "I")
	public int thirdCount;

	@OriginalMember(owner = "client!mq", name = "V", descriptor = "I")
	public int count;

	@OriginalMember(owner = "client!mq", name = "K", descriptor = "I")
	public int thirdId = -1;

	@OriginalMember(owner = "client!mq", name = "O", descriptor = "I")
	private int sphereRadius = 0;

	@OriginalMember(owner = "client!mq", name = "W", descriptor = "Z")
	private boolean transparent = false;

	@OriginalMember(owner = "client!mq", name = "U", descriptor = "I")
	public int secondId = -1;

	@OriginalMember(owner = "client!mq", name = "bb", descriptor = "I")
	public int tileHeightOffset = 0;

	@OriginalMember(owner = "client!mq", name = "<init>", descriptor = "(IIIII)V")
	public ObjStackEntity(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int level, @OriginalArg(4) int virtualLevel) {
		super(x, y, z, level, virtualLevel);
	}

	@OriginalMember(owner = "client!mq", name = "l", descriptor = "(I)I")
	@Override
	public int getPickSizeShift() {
		@Pc(14) ItemType objType = TypeLists.OBJ.list(this.firstId);
		@Pc(17) int picksizeshift = objType.pickSizeShift;
		@Pc(28) ItemType objType2;
		if (this.secondId != -1) {
			objType2 = TypeLists.OBJ.list(this.secondId);
			if (objType2.pickSizeShift > picksizeshift) {
				picksizeshift = objType2.pickSizeShift;
			}
		}
		if (this.thirdId != -1) {
			objType2 = TypeLists.OBJ.list(this.thirdId);
			if (picksizeshift < objType2.pickSizeShift) {
				picksizeshift = objType2.pickSizeShift;
			}
		}
		return picksizeshift;
	}

	@OriginalMember(owner = "client!mq", name = "b", descriptor = "(Lclient!ha;I)Lclient!hw;")
	@Override
	public Silhouette getCylinder(@OriginalArg(0) Toolkit toolkit) {
		return null;
	}

	@OriginalMember(owner = "client!mq", name = "c", descriptor = "(Z)I")
	@Override
	public int getModelMinY() {
		return -10;
	}

	@OriginalMember(owner = "client!mq", name = "c", descriptor = "(Lclient!ha;I)V")
	@Override
	public void particleCycle(@OriginalArg(0) Toolkit toolkit) {
	}

	@OriginalMember(owner = "client!mq", name = "a", descriptor = "(Lclient!ha;III)Z")
	@Override
	public boolean picked(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int mouseY, @OriginalArg(2) int mouseX) {
		@Pc(6) Matrix matrix = toolkit.scratchMatrix();
		matrix.applyTranslation(super.x, super.y - 10, super.z);
		@Pc(24) ItemType objType = TypeLists.OBJ.list(this.firstId);
		@Pc(37) Model model = objType.getAnimatedModel(null, null, toolkit, 131072, 0, this.firstCount, 0, -1);
		if (model != null && (OrthoMode.orthographicProjection ? model.pickedOrtho(mouseX, mouseY, matrix, true, objType.pickSizeShift, HintArrow.scaledOrthoZoom) : model.picked(mouseX, mouseY, matrix, true, objType.pickSizeShift))) {
			return true;
		}
		@Pc(74) ItemType objType2;
		if (this.secondId != -1) {
			objType2 = TypeLists.OBJ.list(this.secondId);
			model = objType2.getAnimatedModel(null, null, toolkit, 131072, 0, this.count, 0, -1);
			if (model != null && (OrthoMode.orthographicProjection ? model.pickedOrtho(mouseX, mouseY, matrix, true, objType2.pickSizeShift, HintArrow.scaledOrthoZoom) : model.picked(mouseX, mouseY, matrix, true, objType2.pickSizeShift))) {
				return true;
			}
		}
		if (this.thirdId != -1) {
			objType2 = TypeLists.OBJ.list(this.thirdId);
			model = objType2.getAnimatedModel(null, null, toolkit, 131072, 0, this.thirdCount, 0, -1);
			if (model != null && (OrthoMode.orthographicProjection ? model.pickedOrtho(mouseX, mouseY, matrix, true, objType2.pickSizeShift, HintArrow.scaledOrthoZoom) : model.picked(mouseX, mouseY, matrix, true, objType2.pickSizeShift))) {
				return true;
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!mq", name = "b", descriptor = "(ILclient!ha;)Lclient!jda;")
	@Override
	public PickableEntity render(@OriginalArg(1) Toolkit toolkit) {
		@Pc(25) InteractiveEntityContainer positionEntityNode = Scene.getTileInteractives(super.level, super.x >> Scene.tileUnits, super.z >> Scene.tileUnits);
		if (positionEntityNode != null && positionEntityNode.interactiveObject.supportsItems) {
			@Pc(36) int supportHeight = positionEntityNode.interactiveObject.getModelMinY();
			if (this.tileHeightOffset != supportHeight) {
				super.y -= this.tileHeightOffset;
				this.tileHeightOffset = supportHeight;
				super.y += supportHeight;
			}
		}
		@Pc(59) Matrix matrix = toolkit.scratchMatrix();
		matrix.makeIdentity();
		if (positionEntityNode == null || !positionEntityNode.interactiveObject.supportsItems) {
			@Pc(78) Ground ground = Scene.aClass65Array1[super.virtualLevel];
			@Pc(83) int sampleSize = this.sphereRadius << 1;
			@Pc(90) int nwOffsetX = -sampleSize / 2;
			@Pc(95) int nwOffsetZ = -sampleSize / 2;
			@Pc(107) int nwHeight = ground.averageHeight(super.x + nwOffsetX, super.z + nwOffsetZ);
			@Pc(111) int neOffsetX = sampleSize / 2;
			@Pc(116) int neOffsetZ = -sampleSize / 2;
			@Pc(129) int neHeight = ground.averageHeight(neOffsetX + super.x, super.z - -neOffsetZ);
			@Pc(134) int swOffsetX = -sampleSize / 2;
			@Pc(138) int swOffsetZ = sampleSize / 2;
			@Pc(150) int swHeight = ground.averageHeight(super.x + swOffsetX, swOffsetZ + super.z);
			@Pc(154) int seOffsetX = sampleSize / 2;
			@Pc(158) int seOffsetZ = sampleSize / 2;
			@Pc(170) int seHeight = ground.averageHeight(super.x + seOffsetX, super.z + seOffsetZ);
			@Pc(177) int minNorthHeight = nwHeight >= neHeight ? neHeight : nwHeight;
			@Pc(188) int minSouthHeight = seHeight > swHeight ? swHeight : seHeight;
			@Pc(199) int minEastHeight = neHeight < seHeight ? neHeight : seHeight;
			if (sampleSize != 0) {
				@Pc(215) int tiltX = (int) (Math.atan2((double) (minNorthHeight - minSouthHeight), (double) sampleSize) * 2607.5945876176133D) & 0x3FFF;
				if (tiltX != 0) {
					matrix.rotateAxisX(tiltX);
				}
			}
			@Pc(231) int minWestHeight = nwHeight >= swHeight ? swHeight : nwHeight;
			@Pc(236) int heightOffset = nwHeight + seHeight;
			if (sampleSize != 0) {
				@Pc(251) int tiltZ = (int) (Math.atan2((double) (minWestHeight - minEastHeight), (double) sampleSize) * 2607.5945876176133D) & 0x3FFF;
				if (tiltZ != 0) {
					matrix.rotateAxisZ(-tiltZ);
				}
			}
			if (heightOffset > swHeight + neHeight) {
				heightOffset = swHeight + neHeight;
			}
			heightOffset = (heightOffset >> 1) - super.y;
			if (heightOffset != 0) {
				matrix.translate(0, heightOffset, 0);
			}
		}
		matrix.translate(super.x, super.y - 10, super.z);
		@Pc(303) PickableEntity pickableEntity = PickableEntity.obtain(3, true);
		this.transparent = false;
		this.sphereRadius = 0;
		@Pc(333) Model model;
		if (this.thirdId != -1) {
			model = TypeLists.OBJ.list(this.thirdId).getAnimatedModel(null, null, toolkit, 2048, 0, this.thirdCount, 0, -1);
			if (model != null) {
				if (OrthoMode.orthographicProjection) {
					model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons[2], HintArrow.scaledOrthoZoom, 0);
				} else {
					model.render(matrix, pickableEntity.boundingBoxPolygons[2], 0);
				}
				this.transparent |= model.F();
				this.sphereRadius = model.ma();
			}
		}
		if (this.secondId != -1) {
			model = TypeLists.OBJ.list(this.secondId).getAnimatedModel(null, null, toolkit, 2048, 0, this.count, 0, -1);
			if (model != null) {
				if (OrthoMode.orthographicProjection) {
					model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons[1], HintArrow.scaledOrthoZoom, 0);
				} else {
					model.render(matrix, pickableEntity.boundingBoxPolygons[1], 0);
				}
				this.transparent |= model.F();
				if (model.ma() > this.sphereRadius) {
					this.sphereRadius = model.ma();
				}
			}
		}
		model = TypeLists.OBJ.list(this.firstId).getAnimatedModel(null, null, toolkit, 2048, 0, this.firstCount, 0, -1);
		if (model != null) {
			if (OrthoMode.orthographicProjection) {
				model.renderOrtho(matrix, pickableEntity.boundingBoxPolygons[0], HintArrow.scaledOrthoZoom, 0);
			} else {
				model.render(matrix, pickableEntity.boundingBoxPolygons[0], 0);
			}
			this.transparent |= model.F();
			if (model.ma() > this.sphereRadius) {
				this.sphereRadius = model.ma();
			}
		}
		return pickableEntity;
	}

	@OriginalMember(owner = "client!mq", name = "i", descriptor = "(I)I")
	@Override
	public int getMinY(@OriginalArg(0) int check) {
		if (check != -25675) {
			QuickChatValueType.aClass234_18 = null;
		}
		return this.sphereRadius;
	}

	@OriginalMember(owner = "client!mq", name = "b", descriptor = "(B)Z")
	@Override
	public boolean hasAlpha() {
		return this.transparent;
	}

	@OriginalMember(owner = "client!mq", name = "g", descriptor = "(I)Z")
	@Override
	public boolean isStationary() {
		return false;
	}
	@OriginalMember(owner = "client!rea", name = "a", descriptor = "(IBII)V")
	public static void refreshObjStack(@OriginalArg(0) int y, @OriginalArg(2) int x, @OriginalArg(3) int level) {
		@Pc(10) int absoluteX = x + MapRegion.sceneBaseX;
		@Pc(14) int absoluteY = MapRegion.sceneBaseY + y;
		if (Scene.aClass100ArrayArrayArray3 == null || x < 0 || y < 0 || x >= MapRegion.sceneSizeX || MapRegion.sceneSizeY <= y || GamePreferences.uid.animateBackgroundOverride.getValue() == 0 && level != PlayerUpdate.localPlayer.level) {
			return;
		}
		@Pc(52) long key = (long) (absoluteY << 14 | level << 28 | absoluteX);
		@Pc(58) ObjStack objStack = (ObjStack) ObjStack.GROUND_ITEMS.get(key);
		if (objStack == null) {
			Scene.removeGroundItem(level, x, y);
			return;
		}
		@Pc(72) ObjStackEntry objStackEntry = (ObjStackEntry) objStack.groundItems.first();
		if (objStackEntry == null) {
			Scene.removeGroundItem(level, x, y);
			return;
		}
		@Pc(86) ObjStackEntity objStackEntity = (ObjStackEntity) Scene.removeGroundItem(level, x, y);
		if (objStackEntity == null) {
			objStackEntity = new ObjStackEntity(x << 9, Scene.aClass65Array1[level].getHeight(y, x), y << 9, level, level);
		} else {
			objStackEntity.secondId = objStackEntity.thirdId = -1;
		}
		objStackEntity.firstId = objStackEntry.id;
		objStackEntity.firstCount = objStackEntry.count;
		label56: while (true) {
			@Pc(134) ObjStackEntry objStackEntry2 = (ObjStackEntry) objStack.groundItems.next();
			if (objStackEntry2 == null) {
				break;
			}
			if (objStackEntity.firstId != objStackEntry2.id) {
				objStackEntity.secondId = objStackEntry2.id;
				objStackEntity.count = objStackEntry2.count;
				while (true) {
					@Pc(159) ObjStackEntry objStackEntry3 = (ObjStackEntry) objStack.groundItems.next();
					if (objStackEntry3 == null) {
						break label56;
					}
					if (objStackEntry3.id != objStackEntity.firstId && objStackEntry3.id != objStackEntity.secondId) {
						objStackEntity.thirdId = objStackEntry3.id;
						objStackEntity.thirdCount = objStackEntry3.count;
					}
				}
			}
		}
		@Pc(207) int height = Scene.tileHeightForAbsolutePosition((x << 9) + 256, level, (y << 9) + 256);
		objStackEntity.z = y << 9;
		objStackEntity.level = (byte) level;
		objStackEntity.tileHeightOffset = 0;
		objStackEntity.x = x << 9;
		objStackEntity.y = height;
		objStackEntity.virtualLevel = (byte) level;
		if (SettingsBits.isBridge(y, x)) {
			objStackEntity.virtualLevel++;
		}
		Scene.addGroundItem(level, x, y, height, objStackEntity);
	}
}
