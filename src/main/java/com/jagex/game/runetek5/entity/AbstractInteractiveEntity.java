package com.jagex.game.runetek5.entity;

import com.jagex.game.runetek5.graphics.light.StaticPointLight;
import com.jagex.game.runetek5.cache.node.PointLight;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.SceneVisibleTiles;
import com.jagex.game.runetek5.scene.culling.Culling;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.SceneDrawer;

@OriginalClass("client!jb")
public abstract class AbstractInteractiveEntity extends Entity {

	@OriginalMember(owner = "client!jb", name = "B", descriptor = "Z")
	public final boolean supportsItems;

	@OriginalMember(owner = "client!jb", name = "H", descriptor = "S")
	public short endZ;

	@OriginalMember(owner = "client!jb", name = "F", descriptor = "B")
	public final byte rotation;

	@OriginalMember(owner = "client!jb", name = "M", descriptor = "S")
	public short endX;

	@OriginalMember(owner = "client!jb", name = "L", descriptor = "S")
	public short startZ;

	@OriginalMember(owner = "client!jb", name = "D", descriptor = "S")
	public short startX;

	@OriginalMember(owner = "client!jb", name = "<init>", descriptor = "(IIIIIIIIIZB)V")
	public AbstractInteractiveEntity(@OriginalArg(0) int level, @OriginalArg(1) int virtualLevel, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int z, @OriginalArg(5) int startX, @OriginalArg(6) int endX, @OriginalArg(7) int startZ, @OriginalArg(8) int endZ, @OriginalArg(9) boolean supportsItems, @OriginalArg(10) byte rotation) {
		super.y = y;
		this.supportsItems = supportsItems;
		this.endZ = (short) endZ;
		super.x = x;
		this.rotation = rotation;
		this.endX = (short) endX;
		super.z = z;
		super.level = (byte) level;
		this.startZ = (short) startZ;
		this.startX = (short) startX;
		super.virtualLevel = (byte) virtualLevel;
	}

	@OriginalMember(owner = "client!jb", name = "h", descriptor = "(I)Z")
	@Override
	public final boolean canRender() {
		for (@Pc(13) int i = this.startX; i <= this.endX; i++) {
			for (@Pc(18) int j = this.startZ; j <= this.endZ; j++) {
				@Pc(27) int tileX = i + SceneVisibleTiles.visibleTiles - SceneDrawer.cameraGridPositionX;
				if (tileX >= 0 && tileX < Scene.tileInFrustum.length) {
					@Pc(48) int tileZ = SceneVisibleTiles.visibleTiles + j - Scene.cameraTileZ;
					if (tileZ >= 0 && tileZ < Scene.tileInFrustum.length && Scene.tileInFrustum[tileX][tileZ]) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@OriginalMember(owner = "client!jb", name = "c", descriptor = "(B)Z")
	@Override
	public final boolean isOccluded() {
		return Culling.hideTile(super.virtualLevel, this.endZ, this.startZ, this.getModelMinY(), this.startX, this.endX);
	}

	@OriginalMember(owner = "client!jb", name = "e", descriptor = "(B)V")
	public void updateBounds() {
	}

	@OriginalMember(owner = "client!jb", name = "a", descriptor = "([Lclient!ab;I)I")
	@Override
	public final int findLights(@OriginalArg(0) PointLight[] pointLights) {
		@Pc(7) int lightCount = 0;
		@Pc(53) int local53;
		label113: for (@Pc(10) int i = this.startX; i <= this.endX; i++) {
			label111: for (@Pc(15) int j = this.startZ; j <= this.endZ; j++) {
				@Pc(26) long lightUids = Scene.tileLightUids[super.level][i][j];
				@Pc(28) long uidShift = 0L;
				while (true) {
					label106: while (true) {
						if (uidShift > 48L) {
							continue label111;
						}
						@Pc(38) int slot = (int) (lightUids >>> (int) uidShift & 0xFFFFL);
						if (slot <= 0) {
							continue label111;
						}
						@Pc(51) StaticPointLight staticPointLight = Scene.aClass300Array1[slot - 1];
						for (int lightIndex = 0; lightIndex < lightCount; lightIndex++) {
							if (staticPointLight.pointLight == pointLights[lightIndex]) {
								uidShift += 16L;
								continue label106;
							}
						}
						pointLights[lightCount++] = staticPointLight.pointLight;
						if (lightCount == 4) {
							break label113;
						}
						uidShift += 16L;
					}
				}
			}
		}
		for (@Pc(114) int k = lightCount; k < 4; k++) {
			pointLights[k] = null;
		}
		if (this.rotation != 0) {
			@Pc(135) int cameraOffsetX = this.startX - SceneDrawer.cameraGridPositionX;
			int cameraOffsetZ = this.startZ - Scene.cameraTileZ;
			@Pc(153) short adjacentX1;
			@Pc(166) int adjacentX2;
			@Pc(161) int adjacentZ1;
			@Pc(156) short adjacentZ2;
			if (this.rotation == 1) {
				if (cameraOffsetX >= cameraOffsetZ) {
					adjacentX1 = this.startX;
					adjacentZ2 = this.startZ;
					adjacentZ1 = this.startZ + 1;
					adjacentX2 = this.startX - 1;
				} else {
					adjacentZ2 = this.startZ;
					adjacentZ1 = this.startZ - 1;
					adjacentX1 = this.startX;
					adjacentX2 = this.startX + 1;
				}
			} else if (cameraOffsetZ > -cameraOffsetX) {
				adjacentX2 = this.startX - 1;
				adjacentZ2 = this.startZ;
				adjacentZ1 = this.startZ - 1;
				adjacentX1 = this.startX;
			} else {
				adjacentZ1 = this.startZ + 1;
				adjacentX1 = this.startX;
				adjacentZ2 = this.startZ;
				adjacentX2 = this.startX + 1;
			}
			label77: for (@Pc(230) int m = 0; m < lightCount; m++) {
				@Pc(241) long adjacentLightUids = Scene.tileLightUids[super.level][adjacentX1][adjacentZ1];
				@Pc(252) StaticPointLight staticPointLight2;
				while (adjacentLightUids != 0L) {
					staticPointLight2 = Scene.aClass300Array1[(int) ((adjacentLightUids & 0xFFFFL) - 1L)];
					adjacentLightUids >>>= 0x10;
					if (staticPointLight2.pointLight == pointLights[m]) {
						continue label77;
					}
				}
				adjacentLightUids = Scene.tileLightUids[super.level][adjacentX2][adjacentZ2];
				while (adjacentLightUids != 0L) {
					staticPointLight2 = Scene.aClass300Array1[(int) ((adjacentLightUids & 0xFFFFL) - 1L)];
					adjacentLightUids >>>= 0x10;
					if (pointLights[m] == staticPointLight2.pointLight) {
						continue label77;
					}
				}
				for (@Pc(308) int index = m; index < lightCount - 1; index++) {
					pointLights[index] = pointLights[index + 1];
				}
				lightCount--;
			}
		}
		return lightCount;
	}
}
