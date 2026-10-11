package com.jagex.game.runetek5.scene.ground;

import com.jagex.game.runetek5.cache.node.PointLight;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.scene.Scene;

@OriginalClass("client!s")
public abstract class Ground {

	@OriginalMember(owner = "client!s", name = "d", descriptor = "I")
	public final int width;

	@OriginalMember(owner = "client!s", name = "g", descriptor = "I")
	public final int length;

	@OriginalMember(owner = "client!s", name = "i", descriptor = "I")
	public final int tileUnits;

	@OriginalMember(owner = "client!s", name = "a", descriptor = "[[I")
	public final int[][] tileHeights;

	@OriginalMember(owner = "client!s", name = "e", descriptor = "I")
	public final int tileScale;

	@OriginalMember(owner = "client!s", name = "<init>", descriptor = "(III[[I)V")
	public Ground(@OriginalArg(0) int width, @OriginalArg(1) int length, @OriginalArg(2) int scaling, @OriginalArg(3) int[][] tileHeights) {
		this.width = width;
		this.length = length;
		@Pc(11) int tileScale = 0;
		while (scaling > 1) {
			tileScale++;
			scaling >>= 0x1;
		}
		this.tileUnits = 0x1 << tileScale;
		this.tileHeights = tileHeights;
		this.tileScale = tileScale;
	}

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(II[I[I[I[I[I[I[I[I[I[I[IIIIZ)V")
	public abstract void addTile(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int[] offsetsX, @OriginalArg(3) int[] heightOffsets, @OriginalArg(4) int[] offsetsZ, @OriginalArg(5) int[] waterDepths, @OriginalArg(6) int[] faceA, @OriginalArg(7) int[] faceB, @OriginalArg(8) int[] faceC, @OriginalArg(9) int[] colours, @OriginalArg(10) int[] blendedColours, @OriginalArg(11) int[] materialIds, @OriginalArg(12) int[] textureScales, @OriginalArg(13) int waterColour, @OriginalArg(14) int waterDepth, @OriginalArg(15) int waterBias);

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(II)V")
	public abstract void renderTile(@OriginalArg(0) int x, @OriginalArg(1) int z);

	@OriginalMember(owner = "client!s", name = "wa", descriptor = "(Lclient!r;IIIIZ)V")
	public abstract void wa(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int x, @OriginalArg(2) int heightOffset, @OriginalArg(3) int z, @OriginalArg(4) int arg4, @OriginalArg(5) boolean arg5);

	@OriginalMember(owner = "client!s", name = "ka", descriptor = "(III)V")
	public abstract void ka(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int shadowLevel);

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(Lclient!ab;[I)V")
	public abstract void addLight(@OriginalArg(0) PointLight pointLight, @OriginalArg(1) int[] tileMask);

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(BII)I")
	public final int getHeight(@OriginalArg(1) int z, @OriginalArg(2) int x) {
		return this.tileHeights[x][z];
	}

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(III[[ZZI)V")
	public abstract void renderTiles(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int radius, @OriginalArg(3) boolean[][] visibility, @OriginalArg(4) boolean belowLowestPlane, @OriginalArg(5) int skipFlags);

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(III[[ZZII)V")
	public abstract void renderFloor(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int radius, @OriginalArg(3) boolean[][] visibility, @OriginalArg(4) boolean belowLowestPlane, @OriginalArg(5) int orthoZoom, @OriginalArg(6) int skipFlags);

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(IIB)I")
	public final int averageHeight(@OriginalArg(0) int z, @OriginalArg(1) int x) {
		@Pc(8) int x1 = z >> this.tileScale;
		@Pc(13) int z1 = x >> this.tileScale;
		if (x1 < 0 || z1 < 0 || x1 > this.width - 1 || this.length - 1 < z1) {
			return 0;
		}
		@Pc(53) int offsetX = z & this.tileUnits - 1;
		@Pc(60) int offsetZ = x & this.tileUnits - 1;
		@Pc(88) int height0 = (this.tileUnits - offsetX) * this.tileHeights[x1][z1] + this.tileHeights[x1 + 1][z1] * offsetX >> this.tileScale;
		@Pc(118) int height1 = offsetX * this.tileHeights[x1 + 1][z1 + 1] + (this.tileUnits - offsetX) * this.tileHeights[x1][z1 + 1] >> this.tileScale;
		return offsetZ * height1 + (this.tileUnits - offsetZ) * height0 >> this.tileScale;
	}

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(IIIIIII[[Z)V")
	public abstract void drawMinimap(@OriginalArg(3) int x1, @OriginalArg(4) int y1, @OriginalArg(5) int x2, @OriginalArg(6) int y2, @OriginalArg(7) boolean[][] visibility);

	@OriginalMember(owner = "client!s", name = "U", descriptor = "(II[I[I[I[I[I[I[I[IIIIZ)V")
	public abstract void U(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) int[] offsetsX, @OriginalArg(3) int[] heightOffsets, @OriginalArg(4) int[] offsetsZ, @OriginalArg(5) int[] waterDepths, @OriginalArg(6) int[] colours, @OriginalArg(7) int[] blendedColours, @OriginalArg(8) int[] materialIds, @OriginalArg(9) int[] textureScales, @OriginalArg(10) int waterColour, @OriginalArg(11) int waterDepth, @OriginalArg(12) int waterBias, @OriginalArg(13) boolean hasShadow);

	@OriginalMember(owner = "client!s", name = "CA", descriptor = "(Lclient!r;IIIIZ)V")
	public abstract void CA(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int x, @OriginalArg(2) int heightOffset, @OriginalArg(3) int z, @OriginalArg(4) int arg4, @OriginalArg(5) boolean arg5);

	/** Casts the shadow onto this ground at x, z, raised by heightOffset; returns whether this level took it (the Java ground takes none). */
	@OriginalMember(owner = "client!s", name = "a", descriptor = "(Lclient!r;IIIIZ)Z")
	public abstract boolean method8002(@OriginalArg(0) Shadow shadow, @OriginalArg(1) int x, @OriginalArg(2) int heightOffset, @OriginalArg(3) int z);

	@OriginalMember(owner = "client!s", name = "YA", descriptor = "()V")
	public abstract void YA();

	@OriginalMember(owner = "client!s", name = "fa", descriptor = "(IILclient!r;)Lclient!r;")
	public abstract Shadow fa(@OriginalArg(0) int x, @OriginalArg(1) int z, @OriginalArg(2) Shadow shadow);
	@OriginalMember(owner = "client!tp", name = "a", descriptor = "()V")
	public static void addHdShadow() {
		Ground.addHdShadow(Scene.length);
	}

	@OriginalMember(owner = "client!za", name = "c", descriptor = "(II)V")
	public static void addHdShadow(@OriginalArg(1) int planeCount) {
		@Pc(1) Shadow shadow = null;
		for (@Pc(3) int i = 1; i < planeCount; i++) {
			@Pc(8) Ground ground = Scene.aClass65Array2[i];
			if (ground != null) {
				for (@Pc(12) int j = 0; j < Camera.sceneLength; j++) {
					for (@Pc(15) int k = 0; k < Scene.sceneWidth; k++) {
						shadow = ground.fa(k, j, shadow);
						if (shadow != null) {
							@Pc(28) int x = k << Scene.tileUnits;
							@Pc(32) int z = j << Scene.tileUnits;
							for (@Pc(36) int m = i - 1; m >= 0; m--) {
								@Pc(41) Ground ground2 = Scene.aClass65Array2[m];
								if (ground2 != null) {
									@Pc(55) int heightDiffSw = ground.getHeight(j, k) - ground2.getHeight(j, k);
									@Pc(71) int heightDiffSe = ground.getHeight(j, k + 1) - ground2.getHeight(j, k + 1);
									@Pc(91) int heightDiffNe = ground.getHeight(j + 1, k + 1) - ground2.getHeight(j + 1, k + 1);
									@Pc(107) int heightDiffNw = ground.getHeight(j + 1, k) - ground2.getHeight(j + 1, k);
									ground2.CA(shadow, x, (heightDiffSw + heightDiffSe + heightDiffNe + heightDiffNw) / 4, z, 0, false);
								}
							}
						}
					}
				}
			}
		}
	}
}
