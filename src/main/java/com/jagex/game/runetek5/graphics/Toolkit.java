package com.jagex.game.runetek5.graphics;

import com.jagex.game.runetek5.DepthBuffer;
import com.jagex.game.runetek5.EnvironmentSampler;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.ParticleList;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.FlipException;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.cache.node.MemoryPool;
import com.jagex.game.runetek5.graphics.OffscreenSurface;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.graphics.Renderer;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.sprite.Sprite;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import java.awt.Canvas;
import java.awt.Rectangle;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.toolkit.AbstractToolkit;

@OriginalClass("client!ha")
public abstract class Toolkit {

	@OriginalMember(owner = "client!ha", name = "s", descriptor = "Lclient!d;")
	public final MaterialProvider materialProvider;

	@OriginalMember(owner = "client!ha", name = "g", descriptor = "I")
	public final int index;

	@OriginalMember(owner = "client!ha", name = "<init>", descriptor = "(Lclient!d;)V")
	public Toolkit(@OriginalArg(0) MaterialProvider textureSource) {
		this.materialProvider = textureSource;
		@Pc(8) int freeIndex = -1;
		for (@Pc(10) int i = 0; i < 8; i++) {
			if (!AbstractToolkit.ACTIVE_RENDER_UNITS[i]) {
				AbstractToolkit.ACTIVE_RENDER_UNITS[i] = true;
				freeIndex = i;
				break;
			}
		}
		if (freeIndex == -1) {
			throw new IllegalStateException("NFTI");
		}
		this.index = freeIndex;
	}

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(II)Lclient!vm;")
	public abstract DepthBuffer createDepthBuffer(@OriginalArg(0) int width, @OriginalArg(1) int height);

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(IIIID)V")
	public abstract void b(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) double depthOffset);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!bh;IIII)Lclient!ka;")
	public abstract Model createModel(@OriginalArg(0) Mesh mesh, @OriginalArg(1) int functionMask, @OriginalArg(2) int featureMask, @OriginalArg(3) int ambient, @OriginalArg(4) int contrast);

	@OriginalMember(owner = "client!ha", name = "F", descriptor = "(II)V")
	public abstract void F(@OriginalArg(0) int offsetX, @OriginalArg(1) int offsetY);

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(II)V")
	public abstract void flip(@OriginalArg(0) int x, @OriginalArg(1) int y) throws FlipException;

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "()Z")
	public abstract boolean supportsBloom();

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIII)V")
	public abstract void outlineRect(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int colour, @OriginalArg(5) int mode);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIZ)Lclient!mi;")
	public abstract NativeSprite generateEmptySprite(@OriginalArg(0) int width, @OriginalArg(1) int height, @OriginalArg(2) boolean hasAlpha);

	@OriginalMember(owner = "client!ha", name = "Y", descriptor = "()[I")
	public abstract int[] Y();

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Z)V")
	public abstract void setShrinkTextures(@OriginalArg(0) boolean shrink);

	@OriginalMember(owner = "client!ha", name = "E", descriptor = "()I")
	public abstract int E();

	@OriginalMember(owner = "client!ha", name = "I", descriptor = "()I")
	public abstract int I();

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(I)V")
	public abstract void allocateThreads(@OriginalArg(0) int threadCount);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "([I)V")
	public abstract void accumulateViewportDimensions(@OriginalArg(0) int[] cacheInto);

	@OriginalMember(owner = "client!ha", name = "X", descriptor = "(I)V")
	public abstract void X(@OriginalArg(0) int shadowSize);

	@OriginalMember(owner = "client!ha", name = "xa", descriptor = "(F)V")
	public abstract void xa(@OriginalArg(0) float intensity);

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "()V")
	public abstract void finish();

	@OriginalMember(owner = "client!ha", name = "ya", descriptor = "()V")
	public abstract void ya();

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!he;Lclient!vm;)Lclient!is;")
	public abstract OffscreenSurface createOffscreenSurface(@OriginalArg(0) Sprite sprite, @OriginalArg(1) DepthBuffer depthBuffer);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(B)V")
	public final void free() {
		AbstractToolkit.ACTIVE_RENDER_UNITS[this.index] = false;
		this.stop();
	}

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIIILclient!aa;II)V")
	public abstract void drawSurfaceLine(@OriginalArg(0) int startX, @OriginalArg(1) int startY, @OriginalArg(2) int endX, @OriginalArg(3) int endY, @OriginalArg(4) int colour, @OriginalArg(6) ComponentSurface clippingMask, @OriginalArg(7) int maskX, @OriginalArg(8) int maskY);

	@OriginalMember(owner = "client!ha", name = "c", descriptor = "()Lclient!fia;")
	public abstract Renderer renderer();

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!bca;)V")
	public abstract void setCamera(@OriginalArg(0) Matrix matrix);

	@OriginalMember(owner = "client!ha", name = "na", descriptor = "(IIII)[I")
	public abstract int[] na(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height);

	@OriginalMember(owner = "client!ha", name = "r", descriptor = "(IIIIIII)I")
	public abstract int r(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int z1, @OriginalArg(3) int x2, @OriginalArg(4) int y2, @OriginalArg(5) int z2, @OriginalArg(6) int zoom);

	@OriginalMember(owner = "client!ha", name = "DA", descriptor = "(IIII)V")
	public abstract void DA(@OriginalArg(0) int projectionCenterX, @OriginalArg(1) int projectionCenterY, @OriginalArg(2) int projectionScaleX, @OriginalArg(3) int projectionScaleY);

	@OriginalMember(owner = "client!ha", name = "d", descriptor = "()Z")
	public abstract boolean stillwaterEffectLoaded();

	@OriginalMember(owner = "client!ha", name = "e", descriptor = "()Lclient!bca;")
	public abstract Matrix camera();

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(I[Lclient!ab;)V")
	public abstract void copyLights(@OriginalArg(0) int count, @OriginalArg(1) PointLight[] lights);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Ljava/awt/Canvas;II)V")
	public abstract void addCanvas(@OriginalArg(0) Canvas canvas, @OriginalArg(1) int width, @OriginalArg(2) int height);

	@OriginalMember(owner = "client!ha", name = "f", descriptor = "()Z")
	public abstract boolean hasTextures();

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(IIIIII)V")
	public final void drawOpaqueLine(@OriginalArg(0) int y, @OriginalArg(1) int x, @OriginalArg(2) int width, @OriginalArg(4) int colorArgb, @OriginalArg(5) int height) {
		this.line(x, y, width, height, colorArgb, 1);
	}

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(I)V")
	public abstract void tick(@OriginalArg(0) int time);

	/** Turns the native toolkit's own profiling on or off with the profiling overlay; the Java toolkits ignore it. */
	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(Z)V")
	public abstract void method7250(@OriginalArg(0) boolean enabled);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(II[[I[[IIII)Lclient!s;")
	public abstract Ground createGround(@OriginalArg(0) int width, @OriginalArg(1) int length, @OriginalArg(2) int[][] tileHeights, @OriginalArg(3) int[][] nextLevelHeights, @OriginalArg(5) int groundFlags, @OriginalArg(6) int featureFlags);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(BIIII)V")
	public final void horizontalLine(@OriginalArg(1) int y, @OriginalArg(2) int colour, @OriginalArg(3) int x, @OriginalArg(4) int strength) {
		this.U(strength, colour, y, x, 1);
	}

	@OriginalMember(owner = "client!ha", name = "c", descriptor = "(I)V")
	public abstract void initialize();

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!za;)V")
	public abstract void setHeap(@OriginalArg(0) MemoryPool heap);

	@OriginalMember(owner = "client!ha", name = "P", descriptor = "(IIIII)V")
	public abstract void P(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int height, @OriginalArg(3) int colour, @OriginalArg(4) int mode);

	@OriginalMember(owner = "client!ha", name = "h", descriptor = "()V")
	public abstract void stop();

	@OriginalMember(owner = "client!ha", name = "za", descriptor = "(IIIII)V")
	public abstract void za(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int radius, @OriginalArg(3) int colour, @OriginalArg(4) int mode);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!wh;)V")
	public abstract void setSunCubeTexture(@OriginalArg(0) EnvironmentSampler cubeTexture);

	@OriginalMember(owner = "client!ha", name = "j", descriptor = "()Lclient!bca;")
	public abstract Matrix createMatrix();

	@OriginalMember(owner = "client!ha", name = "JA", descriptor = "(IIIIII)I")
	public abstract int JA(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int z1, @OriginalArg(3) int x2, @OriginalArg(4) int y2, @OriginalArg(5) int z2);

	@OriginalMember(owner = "client!ha", name = "i", descriptor = "()I")
	public abstract int i();

	@OriginalMember(owner = "client!ha", name = "k", descriptor = "()Z")
	public abstract boolean supportsFullscreen();

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(BIIII)V")
	public final void fillCircle(@OriginalArg(1) int colour, @OriginalArg(2) int radius, @OriginalArg(3) int x, @OriginalArg(4) int y) {
		this.za(x, y, radius, colour, 1);
	}

	@OriginalMember(owner = "client!ha", name = "L", descriptor = "(III)V")
	public abstract void L(@OriginalArg(0) int colour, @OriginalArg(1) int distance, @OriginalArg(2) int offset);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "([Ljava/awt/Rectangle;II)V")
	public final void flipDirtyRects(@OriginalArg(0) Rectangle[] rectangles, @OriginalArg(1) int count) throws FlipException {
		this.flipDirtyRect(rectangles, count, 0, 0);
	}

	@OriginalMember(owner = "client!ha", name = "f", descriptor = "(II)V")
	public abstract void f(@OriginalArg(0) int zNear, @OriginalArg(1) int zFar);

	@OriginalMember(owner = "client!ha", name = "la", descriptor = "()V")
	public abstract void la();

	/** Prepares post-processing (bloom) for the area of a container component about to be drawn: x, y, width and height on screen. */
	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIII)V")
	public abstract void method7261(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height);

	@OriginalMember(owner = "client!ha", name = "e", descriptor = "(I)V")
	public abstract void linkThreads(@OriginalArg(0) int threadId);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIZ)V")
	public final void verticalLine(@OriginalArg(0) int x, @OriginalArg(1) int colour, @OriginalArg(2) int height, @OriginalArg(3) int y) {
		this.P(x, y, height, colour, 1);
	}

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(II[I[I)Lclient!aa;")
	public abstract ComponentSurface createMask(@OriginalArg(0) int width, @OriginalArg(1) int height, @OriginalArg(2) int[] offsets, @OriginalArg(3) int[] widths);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(I[IBIII)Lclient!mi;")
	public final NativeSprite createNativeSprite(@OriginalArg(0) int width, @OriginalArg(1) int[] pixels, @OriginalArg(4) int rowLength, @OriginalArg(5) int height) {
		return this.createNativeSprite(pixels, rowLength, width, height, true);
	}

	@OriginalMember(owner = "client!ha", name = "l", descriptor = "()V")
	public abstract void cacheReset();

	/** Creates a blank sprite of the given size to render into; only the software toolkit has one, the others return null. */
	@OriginalMember(owner = "client!ha", name = "d", descriptor = "(II)Lclient!he;")
	public abstract Sprite method7268(@OriginalArg(0) int width, @OriginalArg(1) int height);

	@OriginalMember(owner = "client!ha", name = "pa", descriptor = "()V")
	public abstract void pa();

	@OriginalMember(owner = "client!ha", name = "m", descriptor = "()Z")
	public abstract boolean bloom();

	@OriginalMember(owner = "client!ha", name = "da", descriptor = "(III[I)V")
	public abstract void da(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] screenCoords);

	@OriginalMember(owner = "client!ha", name = "e", descriptor = "(II)I")
	public abstract int compareFunctionMasks(@OriginalArg(0) int maskA, @OriginalArg(1) int maskB);

	@OriginalMember(owner = "client!ha", name = "Q", descriptor = "(IIIIII[BII)V")
	public abstract void Q(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int backgroundColour, @OriginalArg(5) int foregroundColour, @OriginalArg(6) byte[] mask, @OriginalArg(7) int maskWidth, @OriginalArg(8) int mode);

	@OriginalMember(owner = "client!ha", name = "c", descriptor = "(IIIIII)V")
	public final void drawOpaqueRectangle(@OriginalArg(0) int width, @OriginalArg(1) int y, @OriginalArg(2) int color, @OriginalArg(3) int x, @OriginalArg(4) int height) {
		this.outlineRect(x, y, width, height, color, 1);
	}

	@OriginalMember(owner = "client!ha", name = "HA", descriptor = "(IIII[I)V")
	public abstract void HA(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int zoom, @OriginalArg(4) int[] screenCoords);

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(Ljava/awt/Canvas;II)V")
	public abstract void resizeCanvas(@OriginalArg(0) Canvas canvas, @OriginalArg(1) int width, @OriginalArg(2) int height);

	@OriginalMember(owner = "client!ha", name = "n", descriptor = "()Z")
	public abstract boolean extendBuildArea();

	@OriginalMember(owner = "client!ha", name = "o", descriptor = "()Z")
	public abstract boolean hasBloom();

	@OriginalMember(owner = "client!ha", name = "p", descriptor = "()V")
	public abstract void restoreSurface();

	@OriginalMember(owner = "client!ha", name = "q", descriptor = "()V")
	public abstract void renderLastFrame();

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIIIIII)V")
	public abstract void drawLine(@OriginalArg(0) int startX, @OriginalArg(1) int startY, @OriginalArg(2) int endX, @OriginalArg(3) int endY, @OriginalArg(4) int colorArgb, @OriginalArg(6) int lineSpacing, @OriginalArg(7) int lineLength, @OriginalArg(8) int offset);

	@OriginalMember(owner = "client!ha", name = "EA", descriptor = "(IIII)V")
	public abstract void EA(@OriginalArg(0) int waterHeight, @OriginalArg(1) int waterColour, @OriginalArg(2) int waterScale, @OriginalArg(3) int waterIntensity);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IBIIII)V")
	public final void fillRect(@OriginalArg(0) int width, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int height, @OriginalArg(5) int colour) {
		this.aa(x, y, width, height, colour, 1);
	}

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIIIIIIIIII)V")
	public abstract void drawTriangle();

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Ljava/awt/Canvas;)V")
	public abstract void setCanvas(@OriginalArg(0) Canvas canvas);

	/** Releases what a render worker thread holds of the toolkit when it finishes its part of the frame (its thread resources or context). */
	@OriginalMember(owner = "client!ha", name = "f", descriptor = "(I)V")
	public abstract void method7281(@OriginalArg(0) int threadId);

	@OriginalMember(owner = "client!ha", name = "g", descriptor = "(II)I")
	public abstract int combineFunctionMasks(@OriginalArg(0) int maskA, @OriginalArg(1) int maskB);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!mi;I)V")
	public final void renderToSprite(@OriginalArg(0) NativeSprite sprite) {
		this.swapSurface(this.createOffscreenSurface(sprite, this.createDepthBuffer(sprite.getWidth(), sprite.getHeight())));
	}

	@OriginalMember(owner = "client!ha", name = "r", descriptor = "()Z")
	public abstract boolean hardShadow();

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(IIIIZ)Lclient!mi;")
	public abstract NativeSprite createNativeSprite(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) boolean hasAlpha);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!mda;)V")
	public abstract void render(@OriginalArg(0) ParticleList particleList);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!mda;I)V")
	public abstract void renderOrtho(@OriginalArg(0) ParticleList particleList, @OriginalArg(1) int zoom);

	@OriginalMember(owner = "client!ha", name = "K", descriptor = "([I)V")
	public abstract void K(@OriginalArg(0) int[] clip);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!oea;[Lclient!rh;Z)Lclient!da;")
	public abstract AbstractFontRenderer createFontRenderer(@OriginalArg(0) FontMetrics metrics, @OriginalArg(1) SpriteType[] images);

	@OriginalMember(owner = "client!ha", name = "H", descriptor = "(III[I)V")
	public abstract void H(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] screenCoords);

	@OriginalMember(owner = "client!ha", name = "T", descriptor = "(IIII)V")
	public abstract void T(@OriginalArg(0) int left, @OriginalArg(1) int top, @OriginalArg(2) int right, @OriginalArg(3) int bottom);

	@OriginalMember(owner = "client!ha", name = "ZA", descriptor = "(IFFFFF)V")
	public abstract void ZA(@OriginalArg(0) int colour, @OriginalArg(1) float sunIntensity, @OriginalArg(2) float antiSunIntensity, @OriginalArg(3) float x, @OriginalArg(4) float y, @OriginalArg(5) float z);

	@OriginalMember(owner = "client!ha", name = "ra", descriptor = "(IIII)V")
	public abstract void ra(@OriginalArg(0) int waterHeight, @OriginalArg(1) int waterColour, @OriginalArg(2) int waterScale, @OriginalArg(3) int waterIntensity);

	@OriginalMember(owner = "client!ha", name = "M", descriptor = "()I")
	public abstract int M();

	@OriginalMember(owner = "client!ha", name = "C", descriptor = "(Z)V")
	public abstract void C(@OriginalArg(0) boolean enabled);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!is;)V")
	public abstract void swapSurface(@OriginalArg(0) OffscreenSurface surface);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIIII)V")
	public abstract void strongLine(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int x2, @OriginalArg(3) int y2, @OriginalArg(4) int colour, @OriginalArg(5) int width);

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(B)V")
	public final void renderFrame() throws FlipException {
		this.flip(0, 0);
	}

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIIF)Lclient!ab;")
	public abstract PointLight createPointLight(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int radius, @OriginalArg(4) int colour, @OriginalArg(5) float intensity);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!rh;Z)Lclient!mi;")
	public abstract NativeSprite generateSprite(@OriginalArg(0) SpriteType spriteType, @OriginalArg(1) boolean paletteSprite);

	@OriginalMember(owner = "client!ha", name = "s", descriptor = "()Z")
	public abstract boolean utilisesNativeHeap();

	@OriginalMember(owner = "client!ha", name = "t", descriptor = "()Z")
	public abstract boolean replaceCanvasOnDisplayUpdate();

	@OriginalMember(owner = "client!ha", name = "u", descriptor = "()Z")
	public abstract boolean forceFixedScreenmode();

	@OriginalMember(owner = "client!ha", name = "h", descriptor = "(I)V")
	public abstract void setTextureSize(@OriginalArg(0) int size);

	@OriginalMember(owner = "client!ha", name = "d", descriptor = "(IIIIII)Lclient!wh;")
	public abstract EnvironmentSampler createCubeTexture(@OriginalArg(0) int materialPositiveX, @OriginalArg(1) int materialNegativeX, @OriginalArg(2) int materialPositiveY, @OriginalArg(3) int materialNegativeY, @OriginalArg(4) int materialPositiveZ, @OriginalArg(5) int materialNegativeZ);

	@OriginalMember(owner = "client!ha", name = "finalize", descriptor = "()V")
	@Override
	public void finalize() {
		this.free();
	}

	@OriginalMember(owner = "client!ha", name = "KA", descriptor = "(IIII)V")
	public abstract void KA(@OriginalArg(0) int left, @OriginalArg(1) int top, @OriginalArg(2) int right, @OriginalArg(3) int bottom);

	@OriginalMember(owner = "client!ha", name = "v", descriptor = "()V")
	public abstract void compositeBloomEffects();

	@OriginalMember(owner = "client!ha", name = "w", descriptor = "()Z")
	public abstract boolean supportsAntiAliasing();

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(FFF)V")
	public abstract void setBloomParams(@OriginalArg(0) float whitePoint, @OriginalArg(1) float bloom, @OriginalArg(2) float brightpass);

	@OriginalMember(owner = "client!ha", name = "A", descriptor = "(ILclient!aa;II)V")
	public abstract void A(@OriginalArg(0) int colour, @OriginalArg(1) ComponentSurface mask, @OriginalArg(2) int x, @OriginalArg(3) int y);

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "(Ljava/awt/Canvas;)V")
	public abstract void releaseSurface(@OriginalArg(0) Canvas canvas);

	@OriginalMember(owner = "client!ha", name = "i", descriptor = "(I)Lclient!za;")
	public abstract MemoryPool createHeap(@OriginalArg(0) int size);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(IIIIIILclient!aa;IIIII)V")
	public abstract void drawTexturedLine(@OriginalArg(0) int startX, @OriginalArg(1) int startY, @OriginalArg(2) int endX, @OriginalArg(3) int endY, @OriginalArg(4) int colour, @OriginalArg(6) ComponentSurface clippingMask, @OriginalArg(7) int maskX, @OriginalArg(8) int maskY, @OriginalArg(9) int lineSpacing, @OriginalArg(10) int lineLength, @OriginalArg(11) int offset);

	@OriginalMember(owner = "client!ha", name = "x", descriptor = "()I")
	public abstract int getMaxLights();

	@OriginalMember(owner = "client!ha", name = "y", descriptor = "()Lclient!bca;")
	public abstract Matrix scratchMatrix();

	@OriginalMember(owner = "client!ha", name = "XA", descriptor = "()I")
	public abstract int XA();

	@OriginalMember(owner = "client!ha", name = "U", descriptor = "(IIIII)V")
	public abstract void U(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int colour, @OriginalArg(4) int mode);

	@OriginalMember(owner = "client!ha", name = "aa", descriptor = "(IIIIII)V")
	public abstract void aa(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int colour, @OriginalArg(5) int mode);

	@OriginalMember(owner = "client!ha", name = "GA", descriptor = "(I)V")
	public abstract void GA(@OriginalArg(0) int colour);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "(Lclient!wh;Lclient!wh;FLclient!wh;)Lclient!wh;")
	public abstract EnvironmentSampler drawQuad(@OriginalArg(0) EnvironmentSampler sourceA, @OriginalArg(1) EnvironmentSampler sourceB, @OriginalArg(2) float alpha, @OriginalArg(3) EnvironmentSampler existing);

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "([Ljava/awt/Rectangle;III)V")
	public abstract void flipDirtyRect(@OriginalArg(0) Rectangle[] rectangles, @OriginalArg(1) int count, @OriginalArg(2) int x, @OriginalArg(3) int y) throws FlipException;

	/** Whether the scene may be drawn by several render worker threads at once; only the software toolkit allows it. */
	@OriginalMember(owner = "client!ha", name = "z", descriptor = "()Z")
	public abstract boolean method7311();

	@OriginalMember(owner = "client!ha", name = "e", descriptor = "(IIIIII)V")
	public abstract void line(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int x2, @OriginalArg(3) int y2, @OriginalArg(4) int colour, @OriginalArg(5) int mode);

	@OriginalMember(owner = "client!ha", name = "A", descriptor = "()V")
	public abstract void stopBloom();

	@OriginalMember(owner = "client!ha", name = "a", descriptor = "([IIIIIZ)Lclient!mi;")
	public abstract NativeSprite createNativeSprite(@OriginalArg(0) int[] pixels, @OriginalArg(2) int rowLength, @OriginalArg(3) int width, @OriginalArg(4) int height, @OriginalArg(5) boolean copyPixels);
}
