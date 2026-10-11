package com.jagex.game.runetek5.config.material.texture;

import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.config.material.texture.ColorImageCache;
import com.jagex.game.runetek5.config.material.texture.MonochromeImageCache;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpBinary;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpBoxBlur;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpBricks;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpBrightness;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpClamp;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpColorEdgeDetector;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpColorFill;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpColorGradient;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpColorize;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpCombine;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpConstant;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpCurve;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpDiagonalWaves;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpEmboss;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpGreyscale;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpHerringbone;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpHslAdjust;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpInterpolate;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpInvert;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpIrregularBricks;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpLineNoise;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpMandelbrot;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpMonochromeEdgeDetector;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpNoise;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpPerlinNoise;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpPolarDistortion;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpRange;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpSprite;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpSquareWaveform;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpTexture;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpTile;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpTiledSprite;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpVoronoiNoise;
import com.jagex.game.runetek5.config.material.texture.operation.TextureOpWaveform;

@OriginalClass("client!tf")
public abstract class TextureOperation extends Node {

	@OriginalMember(owner = "client!tf", name = "u", descriptor = "Lclient!qk;")
	public MonochromeImageCache monochromeImageCache;

	@OriginalMember(owner = "client!tf", name = "v", descriptor = "Lclient!afa;")
	public ColorImageCache colorImageCache;

	@OriginalMember(owner = "client!tf", name = "y", descriptor = "I")
	public int imageCacheCapacity;

	@OriginalMember(owner = "client!tf", name = "t", descriptor = "[Lclient!tf;")
	public final TextureOperation[] operations;

	@OriginalMember(owner = "client!tf", name = "H", descriptor = "Z")
	public boolean monochrome;

	@OriginalMember(owner = "client!tf", name = "<init>", descriptor = "(IZ)V")
	public TextureOperation(@OriginalArg(0) int size, @OriginalArg(1) boolean monochrome) {
		this.operations = new TextureOperation[size];
		this.monochrome = monochrome;
	}

	@OriginalMember(owner = "client!tf", name = "a", descriptor = "(I)I")
	public int getSpriteId() {
		return -1;
	}

	@OriginalMember(owner = "client!tf", name = "a", descriptor = "(BII)[[I")
	public final int[][] getChildColorOutput(@OriginalArg(1) int row, @OriginalArg(2) int childIndex) {
		if (this.operations[childIndex].monochrome) {
			@Pc(25) int[] monochromeOutput = this.operations[childIndex].monochromeOutput(row);
			return new int[][] { monochromeOutput, monochromeOutput, monochromeOutput };
		} else {
			return this.operations[childIndex].getColorOutput(row);
		}
	}

	@OriginalMember(owner = "client!tf", name = "c", descriptor = "(II)[I")
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		throw new IllegalStateException("This operation does not have a monochrome output");
	}

	@OriginalMember(owner = "client!tf", name = "d", descriptor = "(I)I")
	public int getTextureId() {
		return -1;
	}

	@OriginalMember(owner = "client!tf", name = "e", descriptor = "(I)V")
	public void postDecode() {
	}

	@OriginalMember(owner = "client!tf", name = "b", descriptor = "(III)V")
	public void initCache(@OriginalArg(0) int height, @OriginalArg(1) int width) {
		@Pc(14) int size = this.imageCacheCapacity == 255 ? width : this.imageCacheCapacity;
		if (this.monochrome) {
			this.monochromeImageCache = new MonochromeImageCache(size, width, height);
		} else {
			this.colorImageCache = new ColorImageCache(size, width, height);
		}
	}

	@OriginalMember(owner = "client!tf", name = "a", descriptor = "(B)V")
	public void cacheReset() {
		if (this.monochrome) {
			this.monochromeImageCache.reset();
			this.monochromeImageCache = null;
		} else {
			this.colorImageCache.reset();
			this.colorImageCache = null;
		}
	}

	@OriginalMember(owner = "client!tf", name = "d", descriptor = "(II)[[I")
	public int[][] getColorOutput(@OriginalArg(0) int row) {
		throw new IllegalStateException("This operation does not have a colour output");
	}

	@OriginalMember(owner = "client!tf", name = "c", descriptor = "(III)[I")
	public final int[] getChildMonochromeOutput(@OriginalArg(0) int row, @OriginalArg(2) int childIndex) {
		return this.operations[childIndex].monochrome ? this.operations[childIndex].monochromeOutput(row) : this.operations[childIndex].getColorOutput(row)[0];
	}

	@OriginalMember(owner = "client!tf", name = "a", descriptor = "(Lclient!cea;II)V")
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
	}
	@OriginalMember(owner = "client!hha", name = "a", descriptor = "(IB)Lclient!tf;")
	public static TextureOperation implementationForType(@OriginalArg(0) int type) {
		if (type == 0) {
			return new TextureOpConstant();
		} else if (type == 1) {
			return new TextureOpColorFill();
		} else if (type == 2) {
			return new TextureOpHorizontalGradient();
		} else if (type == 3) {
			return new TextureOpVerticalGradient();
		} else if (type == 4) {
			return new TextureOpBricks();
		} else if (type == 5) {
			return new TextureOpBoxBlur();
		} else if (type == 6) {
			return new TextureOpClamp();
		} else if (type == 7) {
			return new TextureOpCombine();
		} else if (type == 8) {
			return new TextureOpCurve();
		} else if (type == 9) {
			return new TextureOpFlip();
		} else if (type == 10) {
			return new TextureOpColorGradient();
		} else if (type == 11) {
			return new TextureOpColorize();
		} else if (type == 12) {
			return new TextureOpWaveform();
		} else if (type == 13) {
			return new TextureOpNoise();
		} else if (type == 14) {
			return new TextureOpWeave();
		} else if (type == 15) {
			return new TextureOpVoronoiNoise();
		} else if (type == 16) {
			return new TextureOpHerringbone();
		} else if (type == 17) {
			return new TextureOpHslAdjust();
		} else if (type == 18) {
			return new TextureOpTiledSprite();
		} else if (type == 19) {
			return new TextureOpPolarDistortion();
		} else if (type == 20) {
			return new TextureOpTile();
		} else if (type == 21) {
			return new TextureOpInterpolate();
		} else if (type == 22) {
			return new TextureOpInvert();
		} else if (type == 23) {
			return new TextureOpKaleidoscope();
		} else if (type == 24) {
			return new TextureOpGreyscale();
		} else if (type == 25) {
			return new TextureOpBrightness();
		} else if (type == 26) {
			return new TextureOpBinary();
		} else if (type == 27) {
			return new TextureOpSquareWaveform();
		} else if (type == 28) {
			return new TextureOpIrregularBricks();
		} else if (type == 29) {
			return new TextureOpRasterizer();
		} else if (type == 30) {
			return new TextureOpRange();
		} else if (type == 31) {
			return new TextureOpMandelbrot();
		} else if (type == 32) {
			return new TextureOpEmboss();
		} else if (type == 33) {
			return new TextureOpColorEdgeDetector();
		} else if (type == 34) {
			return new TextureOpPerlinNoise();
		} else if (type == 35) {
			return new TextureOpMonochromeEdgeDetector();
		} else if (type == 36) {
			return new TextureOpTexture();
		} else if (type == 37) {
			return new TextureOpDiagonalWaves();
		} else if (type == 38) {
			return new TextureOpLineNoise();
		} else if (type == 39) {
			return new TextureOpSprite();
		} else {
			return null;
		}
	}
	@OriginalMember(owner = "client!vc", name = "e", descriptor = "I")
	public static int columnEnd;

	@OriginalMember(owner = "client!di", name = "n", descriptor = "I")
	public static int currentHeight;
}
