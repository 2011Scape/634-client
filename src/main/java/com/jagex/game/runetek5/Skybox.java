package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.config.material.MaterialType;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.config.sun.Sun;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!wr")
public final class Skybox {

	@OriginalMember(owner = "client!wr", name = "a", descriptor = "I")
	private int availableSunCount;

	@OriginalMember(owner = "client!wr", name = "l", descriptor = "I")
	private int spriteSize;

	@OriginalMember(owner = "client!wr", name = "p", descriptor = "Lclient!mi;")
	private NativeSprite backgroundSprite;

	@OriginalMember(owner = "client!wr", name = "f", descriptor = "Z")
	private boolean prepareSun = true;

	@OriginalMember(owner = "client!wr", name = "h", descriptor = "I")
	private int lastComponentHeight = -1;

	@OriginalMember(owner = "client!wr", name = "b", descriptor = "I")
	private final int positionZ;

	@OriginalMember(owner = "client!wr", name = "g", descriptor = "I")
	private final int materialId;

	@OriginalMember(owner = "client!wr", name = "k", descriptor = "[Lclient!ki;")
	private final Sun[] skyBoxSpheres;

	@OriginalMember(owner = "client!wr", name = "c", descriptor = "I")
	private final int positionY;

	@OriginalMember(owner = "client!wr", name = "o", descriptor = "I")
	private final int positionX;

	@OriginalMember(owner = "client!wr", name = "n", descriptor = "[Lclient!ki;")
	private final Sun[] availableSuns;

	@OriginalMember(owner = "client!wr", name = "t", descriptor = "Lclient!ki;")
	private final Sun activeSun;

	@OriginalMember(owner = "client!wr", name = "<init>", descriptor = "(I[Lclient!ki;IIII)V")
	public Skybox(@OriginalArg(0) int materialId, @OriginalArg(1) Sun[] skyBoxSpheres, @OriginalArg(2) int defaultSunIndex, @OriginalArg(3) int positionX, @OriginalArg(4) int positionY, @OriginalArg(5) int positionZ) {
		this.positionZ = positionZ;
		this.materialId = materialId;
		this.skyBoxSpheres = skyBoxSpheres;
		this.positionY = positionY;
		this.positionX = positionX;
		if (skyBoxSpheres == null) {
			this.availableSuns = null;
			this.activeSun = null;
		} else {
			this.availableSuns = new Sun[skyBoxSpheres.length];
			this.activeSun = defaultSunIndex < 0 ? null : skyBoxSpheres[defaultSunIndex];
		}
	}

	@OriginalMember(owner = "client!wr", name = "a", descriptor = "(ILclient!ha;I)Z")
	public boolean updateForHeight(@OriginalArg(1) Toolkit toolkit, @OriginalArg(2) int componentHeight) {
		if (componentHeight != this.lastComponentHeight) {
			this.lastComponentHeight = componentHeight;
			@Pc(16) int size = Utils.floorPowerOfTwo(componentHeight);
			if (size > 512) {
				size = 512;
			}
			if (size <= 0) {
				size = 1;
			}
			if (this.spriteSize != size) {
				this.backgroundSprite = null;
				this.spriteSize = size;
			}
			if (this.skyBoxSpheres != null) {
				this.availableSunCount = 0;
				@Pc(53) int[] sunSizes = new int[this.skyBoxSpheres.length];
				for (@Pc(55) int i = 0; i < this.skyBoxSpheres.length; i++) {
					@Pc(62) Sun skyBoxSphere = this.skyBoxSpheres[i];
					if (skyBoxSphere.update(this.positionX, this.positionY, this.positionZ, this.lastComponentHeight)) {
						sunSizes[this.availableSunCount] = skyBoxSphere.distance;
						this.availableSuns[this.availableSunCount++] = skyBoxSphere;
					}
				}
				IntKeyedQuicksort.quicksort(this.availableSunCount - 1, 0, sunSizes, this.availableSuns);
			}
			this.prepareSun = true;
		}
		@Pc(121) boolean ready = false;
		if (this.prepareSun) {
			this.prepareSun = false;
			for (@Pc(132) int j = this.availableSunCount - 1; j >= 0; j--) {
				@Pc(143) boolean sunReady = this.availableSuns[j].isSunGenerated(toolkit, this.activeSun);
				ready |= sunReady;
				this.prepareSun |= !sunReady;
			}
		}
		return ready;
	}

	@OriginalMember(owner = "client!wr", name = "a", descriptor = "(ILclient!ha;BIIIIIII)V")
	public void renderSkybox(@OriginalArg(0) int angle, @OriginalArg(1) Toolkit toolkit, @OriginalArg(3) int x, @OriginalArg(4) int width, @OriginalArg(5) int y, @OriginalArg(6) int rotationY, @OriginalArg(7) int sunDeltaZ, @OriginalArg(8) int backgroundColour, @OriginalArg(9) int height) {
		@Pc(7) int adjustedAngle = angle + rotationY & 0x3FFF;
		if (this.materialId == -1 || this.spriteSize == 0) {
			toolkit.aa(x, y, width, height, backgroundColour, 0);
		} else {
			@Pc(28) MaterialType textureMetrics = TextureTypeListInit.textureSource.getMetrics(this.materialId);
			if (this.backgroundSprite == null && TextureTypeListInit.textureSource.textureAvailable(this.materialId)) {
				@Pc(67) int[] pixels = textureMetrics.effectCombiner == 2 ? TextureTypeListInit.textureSource.getPixelsArgb(this.spriteSize, 0.7F, this.materialId, this.spriteSize) : TextureTypeListInit.textureSource.getPixels(false, this.materialId, 0.7F, this.spriteSize, this.spriteSize);
				this.backgroundSprite = toolkit.createNativeSprite(this.spriteSize, pixels, this.spriteSize, this.spriteSize);
			}
			if (textureMetrics.effectCombiner == 2) {
				toolkit.aa(x, y, width, height, backgroundColour, 0);
			}
			if (this.backgroundSprite != null) {
				@Pc(105) int combineMode = textureMetrics.effectCombiner == 2 ? 1 : 0;
				@Pc(111) int yOffset = height * sunDeltaZ / -4096;
				@Pc(124) int xOffset;
				for (xOffset = (width - height) / 2 + height * adjustedAngle / 4096; xOffset > height; xOffset -= height) {
				}
				while (xOffset < 0) {
					xOffset += height;
				}
				while (height < yOffset) {
					yOffset -= height;
				}
				while (yOffset < 0) {
					yOffset += height;
				}
				for (@Pc(163) int i = xOffset - height; i < width; i += height) {
					for (@Pc(170) int j = yOffset - height; j < height; j += height) {
						this.backgroundSprite.drawCircle(x + i, j + y, height, height, 1, 0, combineMode);
					}
				}
			}
		}
		for (@Pc(223) int k = this.availableSunCount - 1; k >= 0; k--) {
			this.availableSuns[k].calculateProjection(toolkit, x, y, width, height, sunDeltaZ, adjustedAngle);
		}
	}

	@OriginalMember(owner = "client!wr", name = "a", descriptor = "(B)V")
	public void clearSprites() {
		if (this.skyBoxSpheres != null) {
			for (@Pc(10) int i = 0; i < this.skyBoxSpheres.length; i++) {
				this.skyBoxSpheres[i].resetSprite();
			}
		}
		this.backgroundSprite = null;
	}
	@OriginalMember(owner = "client!qf", name = "c", descriptor = "I")
	public static int skyboxAngle;

	@OriginalMember(owner = "client!uea", name = "f", descriptor = "Lclient!wr;")
	public static Skybox configNpcJs5;
}
