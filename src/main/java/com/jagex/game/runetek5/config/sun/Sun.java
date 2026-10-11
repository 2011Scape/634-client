package com.jagex.game.runetek5.config.sun;

import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.skybox.SunModelReset;
import com.jagex.game.runetek5.config.material.texture.TextureTypeList;
import com.jagex.game.runetek5.utils.Utils;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.TextureTypeListInit;

@OriginalClass("client!ki")
public final class Sun {

	@OriginalMember(owner = "client!ki", name = "c", descriptor = "I")
	private int positionOffset;

	@OriginalMember(owner = "client!ki", name = "e", descriptor = "I")
	private int sunRotationY;

	@OriginalMember(owner = "client!ki", name = "f", descriptor = "I")
	private int sunUseDimension;

	@OriginalMember(owner = "client!ki", name = "g", descriptor = "Lclient!mi;")
	private NativeSprite sunSprite;

	@OriginalMember(owner = "client!ki", name = "q", descriptor = "I")
	private int sunRotationX;

	@OriginalMember(owner = "client!ki", name = "r", descriptor = "I")
	public int distance;

	@OriginalMember(owner = "client!ki", name = "k", descriptor = "I")
	private final int x;

	@OriginalMember(owner = "client!ki", name = "b", descriptor = "I")
	private final int y;

	@OriginalMember(owner = "client!ki", name = "o", descriptor = "I")
	private final int z;

	@OriginalMember(owner = "client!ki", name = "i", descriptor = "Z")
	private final boolean invertPosition;

	@OriginalMember(owner = "client!ki", name = "j", descriptor = "I")
	private final int sunMaterialId;

	@OriginalMember(owner = "client!ki", name = "h", descriptor = "I")
	private final int renderType;

	@OriginalMember(owner = "client!ki", name = "d", descriptor = "I")
	private final int positionOffsetStep;

	@OriginalMember(owner = "client!ki", name = "p", descriptor = "I")
	private final int colour;

	@OriginalMember(owner = "client!ki", name = "<init>", descriptor = "(IIIIIIIZ)V")
	public Sun(@OriginalArg(0) int colour, @OriginalArg(1) int sunMaterialId, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int z, @OriginalArg(5) int positionOffsetStep, @OriginalArg(6) int renderType, @OriginalArg(7) boolean invertPosition) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.invertPosition = invertPosition;
		this.sunMaterialId = sunMaterialId;
		this.renderType = renderType;
		this.positionOffsetStep = positionOffsetStep;
		this.colour = colour;
	}

	@OriginalMember(owner = "client!ki", name = "a", descriptor = "(Lclient!ha;Lclient!ki;)Z")
	private boolean performAction(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) Sun sun) {
		if (this.sunSprite == null) {
			if (this.colour == 0) {
				if (TextureTypeListInit.textureSource.textureAvailable(this.sunMaterialId)) {
					@Pc(23) int[] pixels = TextureTypeListInit.textureSource.getPixelsArgb(this.sunUseDimension, 0.7F, this.sunMaterialId, this.sunUseDimension);
					this.sunSprite = toolkit.createNativeSprite(this.sunUseDimension, pixels, this.sunUseDimension, this.sunUseDimension);
				}
			} else if (this.colour == 2) {
				this.drawColoredSun(toolkit, sun);
			} else if (this.colour == 1) {
				this.drawTexturedSun(toolkit, sun);
			}
		}
		return this.sunSprite != null;
	}

	@OriginalMember(owner = "client!ki", name = "a", descriptor = "(IIII)Z")
	public boolean update(@OriginalArg(0) int offsetX, @OriginalArg(1) int offsetY, @OriginalArg(2) int offsetZ, @OriginalArg(3) int viewportHeight) {
		@Pc(7) int dx;
		@Pc(12) int dy;
		@Pc(17) int dz;
		if (this.invertPosition) {
			this.distance = 1073741823;
			dx = this.x;
			dy = this.y;
			dz = this.z;
		} else {
			dx = this.x - offsetX;
			dy = this.y - offsetY;
			dz = this.z - offsetZ;
			this.distance = (int) Math.sqrt((double) (dx * dx + dy * dy + dz * dz));
			if (this.distance == 0) {
				this.distance = 1;
			}
			dx = (dx << 8) / this.distance;
			dy = (dy << 8) / this.distance;
			dz = (dz << 8) / this.distance;
		}
		@Pc(90) int length = (int) (Math.sqrt((double) (dx * dx + dy * dy + dz * dz)) * 256.0D);
		if (length > 128) {
			dx = (dx << 16) / length;
			dy = (dy << 16) / length;
			dz = (dz << 16) / length;
			this.positionOffset = this.positionOffsetStep * viewportHeight / (this.invertPosition ? 1024 : this.distance);
		} else {
			this.positionOffset = 0;
		}
		if (this.positionOffset < 8) {
			this.sunSprite = null;
			return false;
		}
		@Pc(143) int nextSize = Utils.nextPowerOfTwo(this.positionOffset);
		if (nextSize > viewportHeight) {
			nextSize = Utils.floorPowerOfTwo(viewportHeight);
		}
		if (nextSize > 512) {
			nextSize = 512;
		}
		if (nextSize != this.sunUseDimension) {
			this.sunUseDimension = nextSize;
		}
		this.sunRotationX = (int) (Math.asin((double) ((float) dy / 256.0F)) * 2607.5945876176133D) & 0x3FFF;
		this.sunRotationY = (int) (Math.atan2((double) dx, (double) -dz) * 2607.5945876176133D) & 0x3FFF;
		this.sunSprite = null;
		return true;
	}

	@OriginalMember(owner = "client!ki", name = "b", descriptor = "()V")
	public void resetSprite() {
		this.sunSprite = null;
	}

	@OriginalMember(owner = "client!ki", name = "b", descriptor = "(Lclient!ha;Lclient!ki;)V")
	private void drawTexturedSun(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) Sun sun) {
		Sun.generateModel(toolkit);
		Sun.generateSunSprites(toolkit);
		toolkit.K(Sun.SUN_CLIPPING);
		toolkit.KA(0, 0, this.sunUseDimension, this.sunUseDimension);
		toolkit.ya();
		toolkit.aa(0, 0, this.sunUseDimension, this.sunUseDimension, this.renderType | 0xFF000000, 0);
		@Pc(31) int positionX = 0;
		@Pc(33) int positionY = 0;
		@Pc(35) int positionZ = 256;
		if (sun != null) {
			if (sun.invertPosition) {
				positionX = -sun.x;
				positionY = -sun.y;
				positionZ = -sun.z;
			} else {
				positionX = sun.x - this.x;
				positionY = sun.y - this.y;
				positionZ = sun.z - this.z;
			}
		}
		@Pc(79) int sin;
		@Pc(84) int local84;
		@Pc(94) int temp;
		if (this.sunRotationX != 0) {
			sin = TextureCombineSource.SINE[this.sunRotationX];
			int cos = TextureCombineSource.COSINE[this.sunRotationX];
			temp = positionY * cos - positionZ * sin >> 14;
			positionZ = positionY * sin + positionZ * cos >> 14;
			positionY = temp;
		}
		if (this.sunRotationY != 0) {
			sin = TextureCombineSource.SINE[this.sunRotationY];
			int cos = TextureCombineSource.COSINE[this.sunRotationY];
			temp = positionZ * sin + positionX * cos >> 14;
			positionZ = positionZ * cos - positionX * sin >> 14;
			positionX = temp;
		}
		@Pc(147) Model model = Sun.sunModel.copy((byte) 0, 51200, true);
		model.aa((short) 0, (short) this.sunMaterialId);
		toolkit.xa(1.0F);
		toolkit.ZA(16777215, 1.0F, 1.0F, (float) positionX, (float) positionY, (float) positionZ);
		int scale = this.sunUseDimension * 1024 / (model.RA() - model.V());
		if (this.renderType != 0) {
			scale = scale * 13 / 16;
		}
		toolkit.DA(this.sunUseDimension / 2, this.sunUseDimension / 2, scale, scale);
		toolkit.setCamera(toolkit.createMatrix());
		@Pc(206) Matrix matrix = toolkit.createMatrix();
		matrix.applyTranslation(0, 0, toolkit.i() - model.HA());
		model.renderOrtho(matrix, null, 1024, 1);
		@Pc(228) int antiSunSize = this.sunUseDimension * 13 / 16;
		@Pc(235) int antiSunOffset = (this.sunUseDimension - antiSunSize) / 2;
		Sun.activeAntiSun.drawCircle(antiSunOffset, antiSunOffset, antiSunSize, antiSunSize, 0, this.renderType | 0xFF000000, 1);
		this.sunSprite = toolkit.createNativeSprite(0, 0, this.sunUseDimension, this.sunUseDimension, true);
		toolkit.ya();
		toolkit.aa(0, 0, this.sunUseDimension, this.sunUseDimension, 0, 0);
		Sun.activeSun.drawCircle(0, 0, this.sunUseDimension, this.sunUseDimension, 1, 0, 0);
		this.sunSprite.copyAlpha(0);
		toolkit.KA(Sun.SUN_CLIPPING[0], Sun.SUN_CLIPPING[1], Sun.SUN_CLIPPING[2], Sun.SUN_CLIPPING[3]);
	}

	@OriginalMember(owner = "client!ki", name = "c", descriptor = "(Lclient!ha;Lclient!ki;)Z")
	public boolean isSunGenerated(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) Sun sun) {
		return this.sunSprite != null || this.performAction(toolkit, sun);
	}

	@OriginalMember(owner = "client!ki", name = "a", descriptor = "(Lclient!ha;IIIIII)V")
	public void calculateProjection(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) int width, @OriginalArg(2) int height, @OriginalArg(3) int sunDeltaX, @OriginalArg(4) int sunDeltaY, @OriginalArg(5) int sunDeltaZ, @OriginalArg(6) int alpha) {
		if (this.sunSprite == null) {
			return;
		}
		@Pc(10) int rotationXDelta = this.sunRotationX - sunDeltaZ & 0x3FFF;
		@Pc(17) int rotationYDelta = this.sunRotationY - alpha & 0x3FFF;
		if (rotationYDelta > 8192) {
			rotationYDelta -= 16384;
		}
		if (rotationXDelta > 8192) {
			rotationXDelta -= 16384;
		}
		@Pc(38) int drawY = rotationXDelta * sunDeltaY / 4096 + (sunDeltaY - this.positionOffset) / 2;
		@Pc(51) int drawX = rotationYDelta * sunDeltaY / -4096 + (sunDeltaX - this.positionOffset) / 2;
		if (drawY < sunDeltaY && drawY + this.positionOffset > 0 && drawX < sunDeltaX && drawX + this.positionOffset > 0) {
			this.sunSprite.drawTintedScaled(drawX + width, drawY + height, this.positionOffset, this.positionOffset);
		}
	}

	@OriginalMember(owner = "client!ki", name = "d", descriptor = "(Lclient!ha;Lclient!ki;)V")
	private void drawColoredSun(@OriginalArg(0) Toolkit toolkit, @OriginalArg(1) Sun sun) {
		@Pc(6) Mesh mesh = Mesh.fromResource(TextureTypeList.modelResource, this.sunMaterialId);
		if (mesh == null) {
			return;
		}
		toolkit.K(Sun.SUN_CLIPPING);
		toolkit.KA(0, 0, this.sunUseDimension, this.sunUseDimension);
		toolkit.aa(0, 0, this.sunUseDimension, this.sunUseDimension, 0, 0);
		@Pc(32) int positionX = 0;
		@Pc(34) int positionY = 0;
		@Pc(36) int positionZ = 256;
		if (sun != null) {
			if (sun.invertPosition) {
				positionX = -sun.x;
				positionY = -sun.y;
				positionZ = -sun.z;
			} else {
				positionX = this.x - sun.x;
				positionY = this.y - sun.y;
				positionZ = this.z - sun.z;
			}
		}
		@Pc(81) int rotation;
		@Pc(85) int local85;
		@Pc(89) int local89;
		@Pc(99) int local99;
		if (this.sunRotationX != 0) {
			rotation = -this.sunRotationX & 0x3FFF;
			int sin = TextureCombineSource.SINE[rotation];
			int cos = TextureCombineSource.COSINE[rotation];
			int rotatedY = positionY * cos - positionZ * sin >> 14;
			positionZ = positionY * sin + positionZ * cos >> 14;
			positionY = rotatedY;
		}
		if (this.sunRotationY != 0) {
			rotation = -this.sunRotationY & 0x3FFF;
			int sin = TextureCombineSource.SINE[rotation];
			int cos = TextureCombineSource.COSINE[rotation];
			int rotatedX = positionZ * sin + positionX * cos >> 14;
			positionZ = positionZ * cos - positionX * sin >> 14;
			positionX = rotatedX;
		}
		toolkit.xa(1.0F);
		toolkit.ZA(16777215, 1.0F, 1.0F, (float) positionX, (float) positionY, (float) positionZ);
		@Pc(172) Model model = toolkit.createModel(mesh, 2048, 0, 64, 768);
		int modelWidth = model.RA() - model.V();
		int modelDepth = model.EA() - model.fa();
		int centerX = model.V() + modelWidth / 2;
		@Pc(198) int centerY = model.fa() + modelDepth / 2;
		@Pc(205) int maxSize = modelWidth > modelDepth ? modelWidth : modelDepth;
		toolkit.DA(centerX, centerY, maxSize, maxSize);
		toolkit.setCamera(toolkit.createMatrix());
		@Pc(218) Matrix matrix = toolkit.scratchMatrix();
		matrix.applyTranslation(0, 0, toolkit.i() - model.HA());
		model.renderOrtho(matrix, null, toolkit.i(), 1);
		this.sunSprite = toolkit.createNativeSprite(0, 0, this.sunUseDimension, this.sunUseDimension, true);
		this.sunSprite.copyAlpha(3);
		toolkit.KA(Sun.SUN_CLIPPING[0], Sun.SUN_CLIPPING[1], Sun.SUN_CLIPPING[2], Sun.SUN_CLIPPING[3]);
	}

	@OriginalMember(owner = "client!ki", name = "l", descriptor = "[I")
	public static final int[] SUN_CLIPPING = new int[4];
	@OriginalMember(owner = "client!ki", name = "m", descriptor = "Lclient!ka;")
	public static Model sunModel;

	@OriginalMember(owner = "client!ki", name = "b", descriptor = "(Lclient!ha;)V")
	public static void generateModel(@OriginalArg(0) Toolkit toolkit) {
		if (sunModel != null) {
			return;
		}
		@Pc(8) Mesh mesh = new Mesh(580, 1104, 1);
		mesh.addSunTexture();
		mesh.addVertex(128, 0, 0);
		mesh.addVertex(-128, 0, 0);
		for (@Pc(37) int i = 0; i <= 24; i++) {
			@Pc(44) int longitude = i * 8192 / 24;
			@Pc(48) int sinLongitude = TextureCombineSource.SINE[longitude];
			@Pc(52) int cosLongitude = TextureCombineSource.COSINE[longitude];
			@Pc(61) int local61;
			@Pc(67) int local67;
			@Pc(75) int local75;
			@Pc(83) int local83;
			for (@Pc(54) int j = 1; j < 24; j++) {
				int latitude = j * 8192 / 24;
				int vertexX = TextureCombineSource.COSINE[latitude] >> 7;
				int vertexY = TextureCombineSource.SINE[latitude] * sinLongitude >> 21;
				int vertexZ = TextureCombineSource.SINE[latitude] * cosLongitude >> 21;
				mesh.addVertex(vertexX, -vertexY, vertexZ);
			}
			if (i > 0) {
				int ringVertex = i * 23 + 2;
				int prevRingVertex = ringVertex - 23;
				mesh.addFace(0, (byte) 0, (byte) 0, (short) 0, (short) 127, prevRingVertex, (byte) 0, ringVertex);
				for (int segment = 1; segment < 23; segment++) {
					int nextPrevRingVertex = prevRingVertex + 1;
					@Pc(130) int nextVertex = ringVertex + 1;
					mesh.addFace(prevRingVertex, (byte) 0, (byte) 0, (short) 0, (short) 127, nextPrevRingVertex, (byte) 0, ringVertex);
					mesh.addFace(nextPrevRingVertex, (byte) 0, (byte) 0, (short) 0, (short) 127, nextVertex, (byte) 0, ringVertex);
					prevRingVertex = nextPrevRingVertex;
					ringVertex = nextVertex;
				}
				mesh.addFace(ringVertex, (byte) 0, (byte) 0, (short) 0, (short) 127, prevRingVertex, (byte) 0, 1);
			}
		}
		mesh.maxDepth = mesh.numVertices;
		mesh.faceGroups = null;
		mesh.vertexGroups = null;
		mesh.facesLayer = null;
		sunModel = toolkit.createModel(mesh, 51200, 33, 64, 768);
	}

	@OriginalMember(owner = "client!ki", name = "a", descriptor = "Lclient!mi;")
	public static NativeSprite activeAntiSun;

	@OriginalMember(owner = "client!ki", name = "a", descriptor = "(Lclient!ha;)V")
	public static void generateSunSprites(@OriginalArg(0) Toolkit toolkit) {
		if (activeSun != null) {
			return;
		}
		@Pc(4) int[] shading = new int[16384];
		@Pc(7) int[] mask = new int[16384];
		for (@Pc(9) int i = 0; i < 64; i++) {
			@Pc(14) int dy = 64 - i;
			@Pc(18) int dySquared = dy * dy;
			@Pc(24) int mirrorY = 128 - i - 1;
			@Pc(28) int row = i * 128;
			@Pc(32) int mirrorRow = mirrorY * 128;
			for (@Pc(34) int j = 0; j < 64; j++) {
				@Pc(39) int dx = 64 - j;
				@Pc(43) int dxSquared = dx * dx;
				@Pc(49) int mirrorX = 128 - j - 1;
				@Pc(59) int intensity = 256 - (dxSquared + dySquared << 8) / 4096;
				intensity = intensity * 16 * 192 / 1536;
				if (intensity < 0) {
					intensity = 0;
				} else if (intensity > 255) {
					intensity = 255;
				}
				@Pc(81) int halfIntensity = intensity / 2;
				mask[row + j] = mask[row + mirrorX] = mask[mirrorRow + j] = mask[mirrorRow + mirrorX] = (intensity | 0xFF00) << 16;
				shading[row + j] = shading[row + mirrorX] = shading[mirrorRow + j] = shading[mirrorRow + mirrorX] = 127 - halfIntensity << 24 | 0xFFFFFF;
			}
		}
		activeSun = toolkit.createNativeSprite(128, mask, 128, 128);
		activeAntiSun = toolkit.createNativeSprite(128, shading, 128, 128);
	}

	@OriginalMember(owner = "client!ki", name = "n", descriptor = "Lclient!mi;")
	public static NativeSprite activeSun;
}
