import com.jagex.game.runetek5.DepthBuffer;
import com.jagex.game.runetek5.EnvironmentSampler;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.JavaDepthBuffer;
import com.jagex.game.runetek5.JavaMatrix;
import com.jagex.game.runetek5.cache.node.JavaSurface;
import com.jagex.game.runetek5.ParticleList;
import com.jagex.game.runetek5.graphics.model.particle.ParticleNode;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.config.rsinterface.JavaClippingMask;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.FlipException;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.font.FontMetrics;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.cache.node.MemoryPool;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.OffscreenSurface;
import com.jagex.game.runetek5.graphics.model.particle.PointEntity;
import com.jagex.game.runetek5.cache.node.PointLight;
import com.jagex.game.runetek5.graphics.Renderer;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.sprite.Sprite;
import com.jagex.game.runetek5.config.material.MaterialType;
import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.texture.SafeModeTexture2D;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.model.ModelSM;
import com.jagex.game.runetek5.utils.ColorUtil;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!ao")
public final class JavaToolkit extends Toolkit {

	@OriginalMember(owner = "client!ao", name = "x", descriptor = "I")
	private int width;

	@OriginalMember(owner = "client!ao", name = "y", descriptor = "I")
	private int lastTickTime;

	@OriginalMember(owner = "client!ao", name = "z", descriptor = "Lclient!eq;")
	private IterableHashTable surfaces;

	@OriginalMember(owner = "client!ao", name = "A", descriptor = "Ljava/awt/Canvas;")
	private Canvas canvas;

	@OriginalMember(owner = "client!ao", name = "B", descriptor = "Lclient!pi;")
	public JavaSurface canvasSurface;

	@OriginalMember(owner = "client!ao", name = "C", descriptor = "Z")
	private boolean stopped;

	@OriginalMember(owner = "client!ao", name = "D", descriptor = "Z")
	private boolean colourTables;

	@OriginalMember(owner = "client!ao", name = "E", descriptor = "I")
	private int height;

	@OriginalMember(owner = "client!ao", name = "F", descriptor = "I")
	public int sunY;

	@OriginalMember(owner = "client!ao", name = "G", descriptor = "I")
	public int sunIntensity;

	@OriginalMember(owner = "client!ao", name = "H", descriptor = "Lclient!it;")
	private JavaOffscreenSurface offscreenSurface;

	@OriginalMember(owner = "client!ao", name = "I", descriptor = "I")
	public int clipY1;

	@OriginalMember(owner = "client!ao", name = "J", descriptor = "I")
	public int surfaceWidth;

	@OriginalMember(owner = "client!ao", name = "K", descriptor = "I")
	public int reverseSunIntensity;

	@OriginalMember(owner = "client!ao", name = "L", descriptor = "I")
	public int sunZ;

	@OriginalMember(owner = "client!ao", name = "M", descriptor = "[Lclient!pga;")
	private JavaThreadResource[] resources;

	@OriginalMember(owner = "client!ao", name = "N", descriptor = "I")
	private int mainSurfaceWidth;

	@OriginalMember(owner = "client!ao", name = "O", descriptor = "I")
	public int zNear;

	@OriginalMember(owner = "client!ao", name = "P", descriptor = "[I")
	public int[] surfaceRaster;

	@OriginalMember(owner = "client!ao", name = "R", descriptor = "I")
	public int sunX;

	@OriginalMember(owner = "client!ao", name = "S", descriptor = "I")
	public int threadCount;

	@OriginalMember(owner = "client!ao", name = "T", descriptor = "I")
	private int surfaceHeight;

	@OriginalMember(owner = "client!ao", name = "U", descriptor = "I")
	private int count;

	@OriginalMember(owner = "client!ao", name = "V", descriptor = "I")
	private int depthHeight;

	@OriginalMember(owner = "client!ao", name = "W", descriptor = "Z")
	private boolean shrinkTextures;

	@OriginalMember(owner = "client!ao", name = "X", descriptor = "I")
	public int viewY1;

	@OriginalMember(owner = "client!ao", name = "Y", descriptor = "I")
	public int projectionScaleX;

	@OriginalMember(owner = "client!ao", name = "Z", descriptor = "Lclient!gq;")
	public JavaMatrix camera;

	@OriginalMember(owner = "client!ao", name = "ab", descriptor = "I")
	private int mainSurfaceHeight;

	@OriginalMember(owner = "client!ao", name = "bb", descriptor = "I")
	public int zFar;

	@OriginalMember(owner = "client!ao", name = "cb", descriptor = "I")
	private int depthWidth;

	@OriginalMember(owner = "client!ao", name = "db", descriptor = "I")
	public int clipX1;

	@OriginalMember(owner = "client!ao", name = "eb", descriptor = "I")
	public int projectionScaleY;

	@OriginalMember(owner = "client!ao", name = "fb", descriptor = "Lclient!jr;")
	private final LRUCache textureCache;

	@OriginalMember(owner = "client!ao", name = "gb", descriptor = "Lclient!jr;")
	private final LRUCache spriteCache;

	@OriginalMember(owner = "client!ao", name = "hb", descriptor = "I")
	public int ambient;

	@OriginalMember(owner = "client!ao", name = "ib", descriptor = "I")
	public int textureSize;

	@OriginalMember(owner = "client!ao", name = "jb", descriptor = "[F")
	public float[] mainDepthBuffer;

	@OriginalMember(owner = "client!ao", name = "kb", descriptor = "I")
	public int clipY2;

	@OriginalMember(owner = "client!ao", name = "lb", descriptor = "I")
	public int lb;

	@OriginalMember(owner = "client!ao", name = "mb", descriptor = "I")
	private int modelCount;

	@OriginalMember(owner = "client!ao", name = "nb", descriptor = "I")
	public int viewY2;

	@OriginalMember(owner = "client!ao", name = "ob", descriptor = "I")
	public int clipX2;

	@OriginalMember(owner = "client!ao", name = "pb", descriptor = "I")
	public int viewX2;

	@OriginalMember(owner = "client!ao", name = "qb", descriptor = "I")
	public int viewX1;

	@OriginalMember(owner = "client!ao", name = "rb", descriptor = "I")
	public int projectionCenterX;

	@OriginalMember(owner = "client!ao", name = "sb", descriptor = "[F")
	public float[] depthBuffer;

	@OriginalMember(owner = "client!ao", name = "tb", descriptor = "I")
	private int textureId;

	@OriginalMember(owner = "client!ao", name = "ub", descriptor = "Lclient!mi;")
	private NativeSprite cachedBillboardSprite;

	@OriginalMember(owner = "client!ao", name = "<init>", descriptor = "(Ljava/awt/Canvas;Lclient!d;II)V")
	public JavaToolkit(@OriginalArg(0) Canvas canvas, @OriginalArg(1) MaterialProvider textureSource, @OriginalArg(2) int screenWidth, @OriginalArg(3) int screenHeight) {
		this(textureSource);
		try {
			this.addCanvas(canvas, screenWidth, screenHeight);
			this.setCanvas(canvas);
		} catch (@Pc(12) Throwable ex) {
			ex.printStackTrace();
			this.free();
			throw new RuntimeException("");
		}
	}

	@OriginalMember(owner = "client!ao", name = "<init>", descriptor = "(Lclient!d;)V")
	private JavaToolkit(@OriginalArg(0) MaterialProvider textureSource) {
		super(textureSource);
		this.stopped = false;
		this.colourTables = false;
		this.surfaces = new IterableHashTable(4);
		this.sunIntensity = 45823;
		this.shrinkTextures = false;
		this.count = 0;
		this.textureSize = 128;
		this.clipY1 = 0;
		this.zNear = 50;
		this.clipY2 = 0;
		this.clipX1 = 0;
		this.projectionScaleY = 512;
		this.ambient = 75518;
		this.projectionScaleX = 512;
		this.modelCount = 0;
		this.zFar = 3500;
		this.clipX2 = 0;
		this.reverseSunIntensity = 78642;
		this.spriteCache = new LRUCache(16);
		this.textureId = -1;
		try {
			this.textureCache = new LRUCache(256);
			this.camera = new JavaMatrix();
			this.allocateThreads(1);
			this.linkThreads(0);
			ColorUtil.initColorTables(true, true);
			this.colourTables = true;
			this.lastTickTime = (int) Utils.currentTimeMillis();
		} catch (@Pc(99) Throwable ex) {
			ex.printStackTrace();
			this.free();
			throw new RuntimeException("");
		}
	}

	@OriginalMember(owner = "client!ao", name = "u", descriptor = "()Z")
	@Override
	public boolean forceFixedScreenmode() {
		return true;
	}

	@OriginalMember(owner = "client!ao", name = "GA", descriptor = "(I)V")
	@Override
	public void GA(@OriginalArg(0) int colour) {
		this.aa(0, 0, this.surfaceWidth, this.surfaceHeight, colour, 0);
	}

	@OriginalMember(owner = "client!ao", name = "na", descriptor = "(IIII)[I")
	@Override
	public int[] na(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height) {
		@Pc(4) int[] pixels = new int[width * height];
		@Pc(6) int index = 0;
		for (@Pc(8) int i = 0; i < height; i++) {
			@Pc(18) int rowOffset = (y + i) * this.surfaceWidth + x;
			for (@Pc(20) int j = 0; j < width; j++) {
				pixels[index++] = this.surfaceRaster[rowOffset + j];
			}
		}
		return pixels;
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIII)V")
	@Override
	public void outlineRect(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int colour, @OriginalArg(5) int mode) {
		this.U(x, y, width, colour, mode);
		this.U(x, y + height - 1, width, colour, mode);
		this.P(x, y + 1, height - 2, colour, mode);
		this.P(x + width - 1, y + 1, height - 2, colour, mode);
	}

	@OriginalMember(owner = "client!ao", name = "pa", descriptor = "()V")
	@Override
	public void pa() {
		for (@Pc(1) int i = 0; i < this.resources.length; i++) {
			this.resources[i].fogColour = this.resources[i].savedFogColour;
			this.resources[i].water = false;
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIILclient!aa;IIIII)V")
	@Override
	public void drawTexturedLine(@OriginalArg(0) int startX, @OriginalArg(1) int startY, @OriginalArg(2) int endX, @OriginalArg(3) int endY, @OriginalArg(4) int colour, @OriginalArg(6) ComponentSurface clippingMask, @OriginalArg(7) int surfaceClipLeft, @OriginalArg(8) int surfaceClipTop, @OriginalArg(9) int dashLength, @OriginalArg(10) int gapLength, @OriginalArg(11) int dashOffset) {
		@Pc(2) JavaClippingMask javaClippingMask = (JavaClippingMask) clippingMask;
		@Pc(5) int[] lineWidths = javaClippingMask.lineWidths;
		@Pc(8) int[] lineOffsets = javaClippingMask.lineOffsets;
		@Pc(17) int clipTop = this.clipY1 > surfaceClipTop ? this.clipY1 : surfaceClipTop;
		@Pc(32) int clipBottom = this.clipY2 < surfaceClipTop + lineWidths.length ? this.clipY2 : surfaceClipTop + lineWidths.length;
		@Pc(36) int rawDashOffset = dashOffset << 8;
		@Pc(40) int scaledDash = dashLength << 8;
		@Pc(44) int scaledGap = gapLength << 8;
		@Pc(48) int dashCycle = scaledDash + scaledGap;
		dashOffset = rawDashOffset % dashCycle;
		endX -= startX;
		endY -= startY;
		@Pc(77) int local77;
		@Pc(81) int local81;
		if (endX + endY < 0) {
			int lineLength = (int) (Math.sqrt((double) (endX * endX + endY * endY)) * 256.0D);
			int lengthInCycle = lineLength % dashCycle;
			rawDashOffset = dashCycle + scaledDash - dashOffset - lengthInCycle;
			dashOffset = rawDashOffset % dashCycle;
			if (dashOffset < 0) {
				dashOffset += dashCycle;
			}
			startX += endX;
			endX = -endX;
			startY += endY;
			endY = -endY;
		}
		@Pc(159) int local159;
		@Pc(172) int local172;
		@Pc(176) int local176;
		@Pc(199) int local199;
		@Pc(299) int local299;
		@Pc(316) int local316;
		@Pc(142) int local142;
		@Pc(128) int local128;
		@Pc(263) int premultipliedColour;
		if (endX <= endY) {
			startX <<= 0x10;
			startX += 32768;
			int deltaFixed = endX << 16;
			int step = (int) Math.floor((double) deltaFixed / (double) endY + 0.5D);
			int dashStep = (int) Math.sqrt((double) ((step >> 8) * (step >> 8) + 65536));
			int lastY = endY + startY;
			int alpha = colour >>> 24;
			if (alpha == 255) {
				while (startY <= lastY) {
					int pixelX = startX >> 16;
					int maskRow = startY - surfaceClipTop;
					if (startY >= clipTop && startY < clipBottom && pixelX >= this.clipX1 && pixelX < this.clipX2 && dashOffset < scaledDash && pixelX >= surfaceClipLeft + lineWidths[maskRow] && pixelX < surfaceClipLeft + lineWidths[maskRow] + lineOffsets[maskRow]) {
						this.surfaceRaster[pixelX + startY * this.surfaceWidth] = colour;
					}
					startX += step;
					startY++;
					rawDashOffset = dashOffset + dashStep;
					dashOffset = rawDashOffset % dashCycle;
				}
			} else {
				premultipliedColour = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * alpha >> 8 & 0xFF00) + (alpha << 24);
				int inverseAlpha = 256 - alpha;
				while (startY <= lastY) {
					int pixelX = startX >> 16;
					int maskRow = startY - surfaceClipTop;
					if (startY >= clipTop && startY < clipBottom && pixelX >= this.clipX1 && pixelX < this.clipX2 && dashOffset < scaledDash && pixelX >= surfaceClipLeft + lineWidths[maskRow] && pixelX < surfaceClipLeft + lineWidths[maskRow] + lineOffsets[maskRow]) {
						int pixelIndex = pixelX + startY * this.surfaceWidth;
						int dest = this.surfaceRaster[pixelIndex];
						@Pc(707) int faded = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dest & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
						this.surfaceRaster[pixelX + startY * this.surfaceWidth] = premultipliedColour + faded;
					}
					startX += step;
					startY++;
					rawDashOffset = dashOffset + dashStep;
					dashOffset = rawDashOffset % dashCycle;
				}
			}
			return;
		}
		startY <<= 0x10;
		startY += 32768;
		int deltaFixed = endY << 16;
		int step = (int) Math.floor((double) deltaFixed / (double) endX + 0.5D);
		int lastX = endX + startX;
		int alpha = colour >>> 24;
		int dashStep = (int) Math.sqrt((double) ((step >> 8) * (step >> 8) + 65536));
		if (alpha == 255) {
			while (startX <= lastX) {
				int pixelY = startY >> 16;
				int maskRow = pixelY - surfaceClipTop;
				if (startX >= this.clipX1 && startX < this.clipX2 && pixelY >= clipTop && pixelY < clipBottom && dashOffset < scaledDash) {
					int maskStart = surfaceClipLeft + lineWidths[maskRow];
					if (startX >= maskStart && startX < maskStart + lineOffsets[maskRow]) {
						this.surfaceRaster[startX + pixelY * this.surfaceWidth] = colour;
					}
				}
				startY += step;
				startX++;
				rawDashOffset = dashOffset + dashStep;
				dashOffset = rawDashOffset % dashCycle;
			}
			return;
		}
		premultipliedColour = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * alpha >> 8 & 0xFF00) + (alpha << 24);
		int inverseAlpha = 256 - alpha;
		while (startX <= lastX) {
			int pixelY = startY >> 16;
			int maskRow = pixelY - surfaceClipTop;
			if (startX >= this.clipX1 && startX < this.clipX2 && pixelY >= clipTop && pixelY < clipBottom && dashOffset < scaledDash) {
				int maskStart = surfaceClipLeft + lineWidths[maskRow];
				if (startX >= maskStart && startX < maskStart + lineOffsets[maskRow]) {
					int pixelIndex = startX + pixelY * this.surfaceWidth;
					@Pc(321) int dest = this.surfaceRaster[pixelIndex];
					@Pc(341) int faded = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dest & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
					this.surfaceRaster[pixelIndex] = premultipliedColour + faded;
				}
			}
			startY += step;
			startX++;
			rawDashOffset = dashOffset + dashStep;
			dashOffset = rawDashOffset % dashCycle;
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(II[I[I)Lclient!aa;")
	@Override
	public ComponentSurface createMask(@OriginalArg(0) int width, @OriginalArg(1) int height, @OriginalArg(2) int[] offsets, @OriginalArg(3) int[] widths) {
		return new JavaClippingMask(width, height, offsets, widths);
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!mda;I)V")
	@Override
	public void renderOrtho(@OriginalArg(0) ParticleList particleList, @OriginalArg(1) int zoom) {
		@Pc(3) JavaThreadResource resource = this.threadResource(Thread.currentThread());
		@Pc(7) ParticleNode sentinel = particleList.particles.current;
		for (@Pc(10) ParticleNode node = sentinel.next2; node != sentinel; node = node.next2) {
			@Pc(14) PointEntity particle = (PointEntity) node;
			@Pc(19) int x = particle.x >> 12;
			@Pc(24) int y = particle.y >> 12;
			@Pc(29) int z = particle.z >> 12;
			@Pc(54) float depth = this.camera.tz + this.camera.e3_1 * (float) x + this.camera.e3_2 * (float) y + this.camera.e3_3 * (float) z;
			if (!(depth < (float) this.zNear) && !(depth > (float) resource.fogPlane)) {
				@Pc(103) int px = this.projectionCenterX + (int) ((float) this.projectionScaleX * (this.camera.tx + this.camera.e1_1 * (float) x + this.camera.e1_2 * (float) y + this.camera.e1_3 * (float) z) / (float) zoom);
				@Pc(139) int py = this.lb + (int) ((float) this.projectionScaleY * (this.camera.ty + this.camera.e2_1 * (float) x + this.camera.e2_2 * (float) y + this.camera.e2_3 * (float) z) / (float) zoom);
				if (px >= this.clipX1 && px <= this.clipX2 && py >= this.clipY1 && py <= this.clipY2) {
					if (depth == 0.0F) {
						depth = 1.0F;
					}
					this.drawParticle(particle, px, py, (int) depth, (particle.size * this.projectionScaleX >> 12) / zoom);
				}
			}
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(I[Lclient!ab;)V")
	@Override
	public void copyLights(@OriginalArg(0) int count, @OriginalArg(1) PointLight[] pointLights) {
	}

	@OriginalMember(owner = "client!ao", name = "s", descriptor = "()Z")
	@Override
	public boolean utilisesNativeHeap() {
		return false;
	}

	@OriginalMember(owner = "client!ao", name = "A", descriptor = "()V")
	@Override
	public void stopBloom() {
	}

	@OriginalMember(owner = "client!ao", name = "P", descriptor = "(IIIII)V")
	@Override
	public void P(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int height, @OriginalArg(3) int colour, @OriginalArg(4) int mode) {
		if (x < this.clipX1 || x >= this.clipX2) {
			return;
		}
		if (y < this.clipY1) {
			height -= this.clipY1 - y;
			y = this.clipY1;
		}
		if (y + height > this.clipY2) {
			height = this.clipY2 - y;
		}
		@Pc(40) int pixelIndex = x + y * this.surfaceWidth;
		@Pc(44) int alpha = colour >>> 24;
		@Pc(54) int local54;
		if (mode == 0 || mode == 1 && alpha == 255) {
			for (int row = 0; row < height; row++) {
				this.surfaceRaster[pixelIndex + row * this.surfaceWidth] = colour;
			}
			return;
		}
		@Pc(103) int local103;
		@Pc(111) int local111;
		@Pc(116) int local116;
		if (mode == 1) {
			@Pc(97) int premultipliedColour = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * alpha >> 8 & 0xFF00) + (alpha << 24);
			int inverseAlpha = 256 - alpha;
			for (int row = 0; row < height; row++) {
				int dstIndex = pixelIndex + row * this.surfaceWidth;
				int dstColour = this.surfaceRaster[dstIndex];
				dstColour = ((dstColour & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dstColour & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
				this.surfaceRaster[dstIndex] = premultipliedColour + dstColour;
			}
		} else if (mode == 2) {
			for (int row = 0; row < height; row++) {
				int dstIndex = pixelIndex + row * this.surfaceWidth;
				int dstColour = this.surfaceRaster[dstIndex];
				int sum = colour + dstColour;
				@Pc(178) int redBlueSum = (colour & 0xFF00FF) + (dstColour & 0xFF00FF);
				@Pc(188) int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
				this.surfaceRaster[dstIndex] = sum - overflow | overflow - (overflow >>> 8);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(Z)V")
	@Override
	public void method7250(@OriginalArg(0) boolean enabled) {
	}

	@OriginalMember(owner = "client!ao", name = "xa", descriptor = "(F)V")
	@Override
	public void xa(@OriginalArg(0) float ambient) {
		this.ambient = (int) (ambient * 65535.0F);
	}

	@OriginalMember(owner = "client!ao", name = "ra", descriptor = "(IIII)V")
	@Override
	public void ra(@OriginalArg(0) int waterHeight, @OriginalArg(1) int fogColour, @OriginalArg(2) int waterDepth, @OriginalArg(3) int waterIntensity) {
		for (@Pc(1) int i = 0; i < this.resources.length; i++) {
			this.resources[i].savedFogColour = this.resources[i].fogColour;
			this.resources[i].waterHeight = waterHeight;
			this.resources[i].fogColour = fogColour;
			this.resources[i].waterDepth = waterDepth;
			this.resources[i].water = true;
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!za;)V")
	@Override
	public void setHeap(@OriginalArg(0) MemoryPool memoryPool) {
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!jc;IIII)V")
	private void drawParticle(@OriginalArg(0) PointEntity particle, @OriginalArg(1) int screenX, @OriginalArg(2) int screenY, @OriginalArg(3) int depth, @OriginalArg(4) int size) {
		@Pc(2) int materialId = particle.materialId;
		@Pc(8) int diameter = size << 1;
		if (materialId == -1) {
			this.fillDepthTestedCircle(screenX, screenY, depth, size, particle.colorArgb, 1);
			return;
		}
		if (this.textureId != materialId) {
			@Pc(33) NativeSprite sprite = (NativeSprite) this.spriteCache.get((long) materialId);
			if (sprite == null) {
				@Pc(39) int[] pixels = this.getArgbTexture(materialId);
				if (pixels == null) {
					return;
				}
				@Pc(50) int spriteSize = this.smallTexture(materialId) ? 64 : this.textureSize;
				sprite = this.createNativeSprite(spriteSize, pixels, spriteSize, spriteSize);
				this.spriteCache.put(sprite, (long) materialId);
			}
			this.textureId = materialId;
			this.cachedBillboardSprite = sprite;
		}
		diameter++;
		((SpriteSM) this.cachedBillboardSprite).renderDepthTestedRgb(screenX - size, screenY - size, depth, diameter, diameter, 0, particle.colorArgb, 1);
	}

	@OriginalMember(owner = "client!ao", name = "g", descriptor = "()V")
	private void updateViewport() {
		this.viewX1 = this.clipX1 - this.projectionCenterX;
		this.viewX2 = this.clipX2 - this.projectionCenterX;
		this.viewY1 = this.clipY1 - this.lb;
		this.viewY2 = this.clipY2 - this.lb;
		for (@Pc(29) int i = 0; i < this.threadCount; i++) {
			@Pc(36) Rasterizer rasterizer = this.resources[i].rasterizer;
			rasterizer.minX = this.projectionCenterX - this.clipX1;
			rasterizer.minY = this.lb - this.clipY1;
			rasterizer.width = this.clipX2 - this.clipX1;
			rasterizer.height = this.clipY2 - this.clipY1;
		}
		@Pc(78) int pixel = this.clipY1 * this.surfaceWidth + this.clipX1;
		for (@Pc(81) int y = this.clipY1; y < this.clipY2; y++) {
			for (@Pc(84) int j = 0; j < this.threadCount; j++) {
				this.resources[j].rasterizer.lineOffsets[y - this.clipY1] = pixel;
			}
			pixel += this.surfaceWidth;
		}
	}

	@OriginalMember(owner = "client!ao", name = "i", descriptor = "()I")
	@Override
	public int i() {
		return this.zNear;
	}

	@OriginalMember(owner = "client!ao", name = "y", descriptor = "()Lclient!bca;")
	@Override
	public Matrix scratchMatrix() {
		@Pc(3) JavaThreadResource resource = this.threadResource(Thread.currentThread());
		return resource.scratchMatrix;
	}

	@OriginalMember(owner = "client!ao", name = "d", descriptor = "(I)Z")
	public boolean textureRepeats(@OriginalArg(0) int id) {
		return super.materialProvider.getMetrics(id).repeatS || super.materialProvider.getMetrics(id).repeatT;
	}

	@OriginalMember(owner = "client!ao", name = "Q", descriptor = "(IIIIII[BII)V")
	@Override
	public void Q(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int backgroundColour, @OriginalArg(5) int foregroundColour, @OriginalArg(6) byte[] mask, @OriginalArg(7) int maskWidth, @OriginalArg(8) int mode) {
		if (width <= 0 || height <= 0) {
			return;
		}
		@Pc(6) int maskX = 0;
		@Pc(8) int maskY = 0;
		@Pc(14) int stepX = (maskWidth << 16) / width;
		@Pc(23) int stepY = (mask.length / maskWidth << 16) / height;
		@Pc(30) int pixelIndex = x + y * this.surfaceWidth;
		@Pc(35) int rowSkip = this.surfaceWidth - width;
		if (y + height > this.clipY2) {
			height -= y + height - this.clipY2;
		}
		@Pc(59) int local59;
		if (y < this.clipY1) {
			int clipAmount = this.clipY1 - y;
			height -= clipAmount;
			pixelIndex += clipAmount * this.surfaceWidth;
			maskY += stepY * clipAmount;
		}
		if (x + width > this.clipX2) {
			int clipAmount = x + width - this.clipX2;
			width -= clipAmount;
			rowSkip += clipAmount;
		}
		if (x < this.clipX1) {
			int clipAmount = this.clipX1 - x;
			width -= clipAmount;
			pixelIndex += clipAmount;
			maskX += stepX * clipAmount;
			rowSkip += clipAmount;
		}
		int backgroundAlpha = backgroundColour >>> 24;
		@Pc(132) int foregroundAlpha = foregroundColour >>> 24;
		@Pc(145) int startMaskX;
		@Pc(148) int row;
		@Pc(155) int maskRow;
		@Pc(158) int column;
		if (mode == 0 || mode == 1 && backgroundAlpha == 255 && foregroundAlpha == 255) {
			startMaskX = maskX;
			for (row = -height; row < 0; row++) {
				maskRow = (maskY >> 16) * maskWidth;
				for (column = -width; column < 0; column++) {
					if (mask[(maskX >> 16) + maskRow] == 0) {
						this.surfaceRaster[pixelIndex++] = backgroundColour;
					} else {
						this.surfaceRaster[pixelIndex++] = foregroundColour;
					}
					maskX += stepX;
				}
				maskY += stepY;
				maskX = startMaskX;
				pixelIndex += rowSkip;
			}
			return;
		}
		@Pc(222) int colour;
		@Pc(236) int local236;
		@Pc(240) int local240;
		@Pc(245) int local245;
		if (mode == 1) {
			startMaskX = maskX;
			for (row = -height; row < 0; row++) {
				maskRow = (maskY >> 16) * maskWidth;
				for (column = -width; column < 0; column++) {
					colour = backgroundColour;
					if (mask[(maskX >> 16) + maskRow] != 0) {
						colour = foregroundColour;
					}
					int alpha = colour >>> 24;
					int inverseAlpha = 255 - alpha;
					int dstColour = this.surfaceRaster[pixelIndex];
					this.surfaceRaster[pixelIndex++] = ((colour & 0xFF00FF) * alpha + (dstColour & 0xFF00FF) * inverseAlpha & 0xFF00FF00) + ((colour & 0xFF00) * alpha + (dstColour & 0xFF00) * inverseAlpha & 0xFF0000) >> 8;
					maskX += stepX;
				}
				maskY += stepY;
				maskX = startMaskX;
				pixelIndex += rowSkip;
			}
		} else if (mode == 2) {
			startMaskX = maskX;
			for (row = -height; row < 0; row++) {
				maskRow = (maskY >> 16) * maskWidth;
				for (column = -width; column < 0; column++) {
					colour = backgroundColour;
					if (mask[(maskX >> 16) + maskRow] != 0) {
						colour = foregroundColour;
					}
					if (colour == 0) {
						pixelIndex++;
					} else {
						int dstColour = this.surfaceRaster[pixelIndex];
						int sum = colour + dstColour;
						int redBlueSum = (colour & 0xFF00FF) + (dstColour & 0xFF00FF);
						@Pc(360) int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
						this.surfaceRaster[pixelIndex++] = sum - overflow | overflow - (overflow >>> 8);
					}
					maskX += stepX;
				}
				maskY += stepY;
				maskX = startMaskX;
				pixelIndex += rowSkip;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@OriginalMember(owner = "client!ao", name = "k", descriptor = "()Z")
	@Override
	public boolean supportsFullscreen() {
		return false;
	}

	@OriginalMember(owner = "client!ao", name = "d", descriptor = "(IIIIII)Lclient!wh;")
	@Override
	public EnvironmentSampler createCubeTexture(@OriginalArg(0) int materialPositiveX, @OriginalArg(1) int materialNegativeX, @OriginalArg(2) int materialPositiveY, @OriginalArg(3) int materialNegativeY, @OriginalArg(4) int materialPositiveZ, @OriginalArg(5) int materialNegativeZ) {
		return null;
	}

	@OriginalMember(owner = "client!ao", name = "KA", descriptor = "(IIII)V")
	@Override
	public void KA(@OriginalArg(0) int left, @OriginalArg(1) int top, @OriginalArg(2) int right, @OriginalArg(3) int bottom) {
		if (left < 0) {
			left = 0;
		}
		if (top < 0) {
			top = 0;
		}
		if (right > this.surfaceWidth) {
			right = this.surfaceWidth;
		}
		if (bottom > this.surfaceHeight) {
			bottom = this.surfaceHeight;
		}
		this.clipX1 = left;
		this.clipX2 = right;
		this.clipY1 = top;
		this.clipY2 = bottom;
		this.updateViewport();
	}

	@OriginalMember(owner = "client!ao", name = "v", descriptor = "()V")
	@Override
	public void compositeBloomEffects() {
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!bca;)V")
	@Override
	public void setCamera(@OriginalArg(0) Matrix matrix) {
		this.camera = (JavaMatrix) matrix;
	}

	@OriginalMember(owner = "client!ao", name = "A", descriptor = "(ILclient!aa;II)V")
	@Override
	public void A(@OriginalArg(0) int colour, @OriginalArg(1) ComponentSurface clippingMask, @OriginalArg(2) int x, @OriginalArg(3) int y) {
		@Pc(2) JavaClippingMask javaClippingMask = (JavaClippingMask) clippingMask;
		@Pc(5) int[] lineWidths = javaClippingMask.lineWidths;
		@Pc(8) int[] lineOffsets = javaClippingMask.lineOffsets;
		@Pc(20) int endRow;
		if (this.clipY2 < y + lineWidths.length) {
			endRow = this.clipY2 - y;
		} else {
			endRow = lineWidths.length;
		}
		@Pc(33) int startRow;
		if (this.clipY1 > y) {
			startRow = this.clipY1 - y;
			y = this.clipY1;
		} else {
			startRow = 0;
		}
		if (endRow - startRow <= 0) {
			return;
		}
		@Pc(49) int rowOffset = y * this.surfaceWidth;
		for (@Pc(51) int i = startRow; i < endRow; i++) {
			@Pc(58) int spanOffset = x + lineWidths[i];
			@Pc(62) int length = lineOffsets[i];
			if (this.clipX1 > spanOffset) {
				length -= this.clipX1 - spanOffset;
				spanOffset = this.clipX1;
			}
			if (this.clipX2 < spanOffset + length) {
				length = this.clipX2 - spanOffset;
			}
			spanOffset += rowOffset;
			for (@Pc(94) int j = -length; j < 0; j++) {
				this.surfaceRaster[spanOffset++] = colour;
			}
			rowOffset += this.surfaceWidth;
		}
	}

	@OriginalMember(owner = "client!ao", name = "e", descriptor = "(II)I")
	@Override
	public int compareFunctionMasks(@OriginalArg(0) int maskA, @OriginalArg(1) int maskB) {
		@Pc(3) int combined = maskA | 0x20800;
		return combined & maskB ^ maskB;
	}

	@OriginalMember(owner = "client!ao", name = "f", descriptor = "(II)V")
	@Override
	public void f(@OriginalArg(0) int zNear, @OriginalArg(1) int zFar) {
		@Pc(3) JavaThreadResource javaThreadResource = this.threadResource(Thread.currentThread());
		this.zNear = zNear;
		this.zFar = zFar;
		javaThreadResource.fogPlane = this.zFar - 255;
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIIII)V")
	private void horizontalDashedLine(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int length, @OriginalArg(3) int colour, @OriginalArg(5) int dashLength, @OriginalArg(6) int gapLength, @OriginalArg(7) int phase) {
		if (y < this.clipY1 || y >= this.clipY2) {
			return;
		}
		@Pc(15) int pixel = x + y * this.surfaceWidth;
		@Pc(19) int alpha = colour >>> 24;
		@Pc(23) int period = dashLength + gapLength;
		@Pc(27) int offset = phase % period;
		@Pc(37) int local37;
		if (alpha == 255) {
			int column = 0;
			while (column < length) {
				if (x + column >= this.clipX1 && x + column < this.clipX2 && offset < dashLength) {
					this.surfaceRaster[pixel + column] = colour;
				}
				column++;
				offset++;
				offset %= period;
			}
			return;
		}
		@Pc(97) int blended = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * alpha >> 8 & 0xFF00) + (alpha << 24);
		int inverseAlpha = 256 - alpha;
		@Pc(103) int i = 0;
		while (i < length) {
			if (x + i >= this.clipX1 && x + i < this.clipX2 && offset < dashLength) {
				@Pc(126) int dest = this.surfaceRaster[pixel + i];
				@Pc(146) int faded = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dest & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
				this.surfaceRaster[pixel + i] = blended + faded;
			}
			i++;
			offset++;
			offset %= period;
		}
	}

	@OriginalMember(owner = "client!ao", name = "Y", descriptor = "()[I")
	@Override
	public int[] Y() {
		return new int[] { this.projectionCenterX, this.lb, this.projectionScaleX, this.projectionScaleY };
	}

	@OriginalMember(owner = "client!ao", name = "ya", descriptor = "()V")
	@Override
	public void ya() {
		@Pc(19) int local19;
		@Pc(25) int local25;
		@Pc(27) int local27;
		if (this.clipX1 == 0 && this.clipX2 == this.surfaceWidth && this.clipY1 == 0 && this.clipY2 == this.surfaceHeight) {
			int depthLength = this.depthBuffer.length;
			int unrolledLength = depthLength - (depthLength & 0x7);
			int depthIndex = 0;
			while (depthIndex < unrolledLength) {
				this.depthBuffer[depthIndex++] = 2.14748365E9F;
				this.depthBuffer[depthIndex++] = 2.14748365E9F;
				this.depthBuffer[depthIndex++] = 2.14748365E9F;
				this.depthBuffer[depthIndex++] = 2.14748365E9F;
				this.depthBuffer[depthIndex++] = 2.14748365E9F;
				this.depthBuffer[depthIndex++] = 2.14748365E9F;
				this.depthBuffer[depthIndex++] = 2.14748365E9F;
				this.depthBuffer[depthIndex++] = 2.14748365E9F;
			}
			while (depthIndex < depthLength) {
				this.depthBuffer[depthIndex++] = 2.14748365E9F;
			}
			return;
		}
		int clipWidth = this.clipX2 - this.clipX1;
		int clipHeight = this.clipY2 - this.clipY1;
		int rowSkip = this.surfaceWidth - clipWidth;
		@Pc(116) int startPixel = this.clipX1 + this.clipY1 * this.surfaceWidth;
		@Pc(120) int blockCount = clipWidth >> 3;
		@Pc(124) int remainder = clipWidth & 0x7;
		clipWidth = startPixel - 1;
		for (@Pc(131) int i = -clipHeight; i < 0; i++) {
			@Pc(136) int remaining;
			if (blockCount > 0) {
				remaining = blockCount;
				do {
					clipWidth++;
					this.depthBuffer[clipWidth] = 2.14748365E9F;
					clipWidth++;
					this.depthBuffer[clipWidth] = 2.14748365E9F;
					clipWidth++;
					this.depthBuffer[clipWidth] = 2.14748365E9F;
					clipWidth++;
					this.depthBuffer[clipWidth] = 2.14748365E9F;
					clipWidth++;
					this.depthBuffer[clipWidth] = 2.14748365E9F;
					clipWidth++;
					this.depthBuffer[clipWidth] = 2.14748365E9F;
					clipWidth++;
					this.depthBuffer[clipWidth] = 2.14748365E9F;
					clipWidth++;
					this.depthBuffer[clipWidth] = 2.14748365E9F;
					remaining--;
				} while (remaining > 0);
			}
			if (remainder > 0) {
				remaining = remainder;
				do {
					clipWidth++;
					this.depthBuffer[clipWidth] = 2.14748365E9F;
					remaining--;
				} while (remaining > 0);
			}
			clipWidth += rowSkip;
		}
	}

	@OriginalMember(owner = "client!ao", name = "d", descriptor = "()Z")
	@Override
	public boolean stillwaterEffectLoaded() {
		return true;
	}

	@OriginalMember(owner = "client!ao", name = "U", descriptor = "(IIIII)V")
	@Override
	public void U(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int colour, @OriginalArg(4) int mode) {
		if (y < this.clipY1 || y >= this.clipY2) {
			return;
		}
		if (x < this.clipX1) {
			width -= this.clipX1 - x;
			x = this.clipX1;
		}
		if (x + width > this.clipX2) {
			width = this.clipX2 - x;
		}
		@Pc(40) int pixelIndex = x + y * this.surfaceWidth;
		@Pc(44) int alpha = colour >>> 24;
		@Pc(54) int local54;
		if (mode == 0 || mode == 1 && alpha == 255) {
			for (int column = 0; column < width; column++) {
				this.surfaceRaster[pixelIndex + column] = colour;
			}
			return;
		}
		@Pc(100) int local100;
		@Pc(108) int local108;
		if (mode == 1) {
			@Pc(94) int premultipliedColour = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * alpha >> 8 & 0xFF00) + (alpha << 24);
			int inverseAlpha = 256 - alpha;
			for (int column = 0; column < width; column++) {
				int dstColour = this.surfaceRaster[pixelIndex + column];
				dstColour = ((dstColour & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dstColour & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
				this.surfaceRaster[pixelIndex + column] = premultipliedColour + dstColour;
			}
		} else if (mode == 2) {
			for (int column = 0; column < width; column++) {
				int dstColour = this.surfaceRaster[pixelIndex + column];
				int sum = colour + dstColour;
				@Pc(167) int redBlueSum = (colour & 0xFF00FF) + (dstColour & 0xFF00FF);
				@Pc(177) int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
				this.surfaceRaster[pixelIndex + column] = sum - overflow | overflow - (overflow >>> 8);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@OriginalMember(owner = "client!ao", name = "HA", descriptor = "(IIII[I)V")
	@Override
	public void HA(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int zoom, @OriginalArg(4) int[] screenCoords) {
		@Pc(24) float depth = this.camera.tz + this.camera.e3_1 * (float) x + this.camera.e3_2 * (float) y + this.camera.e3_3 * (float) z;
		if (depth < (float) this.zNear || depth > (float) this.zFar) {
			screenCoords[0] = screenCoords[1] = screenCoords[2] = -1;
			return;
		}
		@Pc(82) int screenX = (int) ((float) this.projectionScaleX * (this.camera.tx + this.camera.e1_1 * (float) x + this.camera.e1_2 * (float) y + this.camera.e1_3 * (float) z) / (float) zoom);
		@Pc(115) int screenY = (int) ((float) this.projectionScaleY * (this.camera.ty + this.camera.e2_1 * (float) x + this.camera.e2_2 * (float) y + this.camera.e2_3 * (float) z) / (float) zoom);
		if (screenX >= this.viewX1 && screenX <= this.viewX2 && screenY >= this.viewY1 && screenY <= this.viewY2) {
			screenCoords[0] = screenX - this.viewX1;
			screenCoords[1] = screenY - this.viewY1;
			screenCoords[2] = (int) depth;
		} else {
			screenCoords[0] = screenCoords[1] = screenCoords[2] = -1;
		}
	}

	@OriginalMember(owner = "client!ao", name = "B", descriptor = "()Z")
	public boolean stopped() {
		return this.stopped;
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIII)V")
	@Override
	public void method7261(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height) {
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!mda;)V")
	@Override
	public void render(@OriginalArg(0) ParticleList particleList) {
		@Pc(3) JavaThreadResource javaThreadResource = this.threadResource(Thread.currentThread());
		@Pc(7) ParticleNode node2 = particleList.particles.current;
		for (@Pc(10) ParticleNode node22 = node2.next2; node22 != node2; node22 = node22.next2) {
			@Pc(14) PointEntity particle = (PointEntity) node22;
			@Pc(19) int x = particle.x >> 12;
			@Pc(24) int y = particle.y >> 12;
			@Pc(29) int z = particle.z >> 12;
			@Pc(54) float depth = this.camera.tz + this.camera.e3_1 * (float) x + this.camera.e3_2 * (float) y + this.camera.e3_3 * (float) z;
			if (!(depth < (float) this.zNear) && !(depth > (float) javaThreadResource.fogPlane)) {
				@Pc(102) int px = this.projectionCenterX + (int) ((float) this.projectionScaleX * (this.camera.tx + this.camera.e1_1 * (float) x + this.camera.e1_2 * (float) y + this.camera.e1_3 * (float) z) / depth);
				@Pc(137) int py = this.lb + (int) ((float) this.projectionScaleY * (this.camera.ty + this.camera.e2_1 * (float) x + this.camera.e2_2 * (float) y + this.camera.e2_3 * (float) z) / depth);
				if (px >= this.clipX1 && px <= this.clipX2 && py >= this.clipY1 && py <= this.clipY2) {
					if (depth == 0.0F) {
						depth = 1.0F;
					}
					this.drawParticle(particle, px, py, (int) depth, (int) ((float) (particle.size * this.projectionScaleX >> 12) / depth));
				}
			}
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(FFF)V")
	@Override
	public void setBloomParams(@OriginalArg(0) float whitePoint, @OriginalArg(1) float bloom, @OriginalArg(2) float brightpass) {
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(IIIIZ)Lclient!mi;")
	@Override
	public NativeSprite createNativeSprite(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) boolean hasAlpha) {
		@Pc(4) int[] pixels = new int[width * height];
		@Pc(11) int srcIndex = y * this.surfaceWidth + x;
		@Pc(16) int srcSkip = this.surfaceWidth - width;
		for (@Pc(18) int i = 0; i < height; i++) {
			@Pc(23) int rowOffset = i * width;
			for (@Pc(25) int j = 0; j < width; j++) {
				pixels[rowOffset + j] = this.surfaceRaster[srcIndex++];
			}
			srcIndex += srcSkip;
		}
		if (hasAlpha) {
			return new TransparentSpriteSM(this, pixels, width, height);
		} else {
			return new OpaqueSpriteSM(this, pixels, width, height);
		}
	}

	@OriginalMember(owner = "client!ao", name = "C", descriptor = "()V")
	private void reset() {
		for (@Pc(1) int i = 0; i < this.threadCount; i++) {
			this.resources[i].resetRasterizer();
		}
		this.la();
	}

	@OriginalMember(owner = "client!ao", name = "r", descriptor = "()Z")
	@Override
	public boolean hardShadow() {
		return false;
	}

	@OriginalMember(owner = "client!ao", name = "n", descriptor = "()Z")
	@Override
	public boolean extendBuildArea() {
		return false;
	}

	@OriginalMember(owner = "client!ao", name = "DA", descriptor = "(IIII)V")
	@Override
	public void DA(@OriginalArg(0) int projectionCenterX, @OriginalArg(1) int projectionCenterY, @OriginalArg(2) int projectionScaleX, @OriginalArg(3) int projectionScaleY) {
		this.projectionCenterX = projectionCenterX;
		this.lb = projectionCenterY;
		this.projectionScaleX = projectionScaleX;
		this.projectionScaleY = projectionScaleY;
		this.updateViewport();
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(Ljava/awt/Canvas;)V")
	@Override
	public void releaseSurface(@OriginalArg(0) Canvas canvas2) {
		if (this.canvas == canvas2) {
			this.setCanvas(null);
		}
		@Pc(15) JavaSurface surface = (JavaSurface) this.surfaces.get((long) canvas2.hashCode());
		if (surface != null) {
			surface.unlink();
		}
	}

	@OriginalMember(owner = "client!ao", name = "L", descriptor = "(III)V")
	@Override
	public void L(@OriginalArg(0) int colour, @OriginalArg(1) int distance, @OriginalArg(2) int offset) {
		for (@Pc(1) int i = 0; i < this.resources.length; i++) {
			@Pc(7) JavaThreadResource javaThreadResource = this.resources[i];
			javaThreadResource.fogColour = colour & 0xFFFFFF;
			@Pc(19) int red = javaThreadResource.fogColour >>> 16 & 0xFF;
			if (red < 2) {
				red = 2;
			}
			@Pc(31) int green = javaThreadResource.fogColour >> 8 & 0xFF;
			if (green < 2) {
				green = 2;
			}
			@Pc(41) int blue = javaThreadResource.fogColour & 0xFF;
			if (blue < 2) {
				blue = 2;
			}
			javaThreadResource.fogColour = red << 16 | green << 8 | blue;
			if (distance < 0) {
				javaThreadResource.fogActive = false;
			} else {
				javaThreadResource.fogActive = true;
			}
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "()Z")
	@Override
	public boolean supportsBloom() {
		return false;
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Ljava/awt/Canvas;II)V")
	@Override
	public void addCanvas(@OriginalArg(0) Canvas canvas, @OriginalArg(1) int width, @OriginalArg(2) int height) {
		@Pc(8) JavaSurface surface = (JavaSurface) this.surfaces.get((long) canvas.hashCode());
		if (surface == null) {
			surface = JavaSurfaceFactory.createSurface(height, canvas, width);
			this.surfaces.put((long) canvas.hashCode(), surface);
		} else if (surface.width != width || surface.height != height) {
			this.resizeCanvas(canvas, width, height);
		}
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(IIIID)V")
	@Override
	public void b(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) double offset) {
		@Pc(4) int rowSkip = this.depthWidth - width;
		@Pc(11) int index = y * this.depthWidth + x;
		@Pc(14) float[] depthBuffer = this.depthBuffer;
		@Pc(16) int row = 0;
		while (row < height) {
			@Pc(19) int column = 0;
			while (column < width) {
				@Pc(24) float depth = depthBuffer[index];
				if (depth != 2.14748365E9F) {
					depthBuffer[index] = (float) ((double) depth + offset);
				}
				column++;
				index++;
			}
			row++;
			index += rowSkip;
		}
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "()V")
	@Override
	public void finish() {
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(I)V")
	@Override
	public void allocateThreads(@OriginalArg(0) int threadCount) {
		this.threadCount = threadCount;
		this.resources = new JavaThreadResource[this.threadCount];
		for (@Pc(9) int i = 0; i < this.threadCount; i++) {
			this.resources[i] = new JavaThreadResource(this);
		}
	}

	@OriginalMember(owner = "client!ao", name = "e", descriptor = "(IIIIII)V")
	@Override
	public void line(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int x2, @OriginalArg(3) int y2, @OriginalArg(4) int colour, @OriginalArg(5) int mode) {
		x2 -= x1;
		y2 -= y1;
		if (y2 == 0) {
			if (x2 >= 0) {
				this.U(x1, y1, x2 + 1, colour, mode);
			} else {
				this.U(x1 + x2, y1, 1 - x2, colour, mode);
			}
		} else if (x2 != 0) {
			if (x2 + y2 < 0) {
				x1 += x2;
				x2 = -x2;
				y1 += y2;
				y2 = -y2;
			}
			@Pc(104) int step;
			@Pc(137) int alpha;
			@Pc(150) int local150;
			@Pc(213) int local213;
			@Pc(228) int local228;
			@Pc(233) int local233;
			@Pc(309) int redBlueSum;
			@Pc(204) int premultipliedColour;
			if (x2 > y2) {
				y1 <<= 0x10;
				y1 += 32768;
				@Pc(94) int deltaFixed = y2 << 16;
				step = (int) Math.floor((double) deltaFixed / (double) x2 + 0.5D);
				x2 += x1;
				if (x1 < this.clipX1) {
					y1 += step * (this.clipX1 - x1);
					x1 = this.clipX1;
				}
				if (x2 >= this.clipX2) {
					x2 = this.clipX2 - 1;
				}
				alpha = colour >>> 24;
				if (mode == 0 || mode == 1 && alpha == 255) {
					while (x1 <= x2) {
						int pixelY = y1 >> 16;
						if (pixelY >= this.clipY1 && pixelY < this.clipY2) {
							this.surfaceRaster[x1 + pixelY * this.surfaceWidth] = colour;
						}
						y1 += step;
						x1++;
					}
				} else if (mode == 1) {
					premultipliedColour = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * alpha >> 8 & 0xFF00) + (alpha << 24);
					int inverseAlpha = 256 - alpha;
					while (x1 <= x2) {
						int pixelY = y1 >> 16;
						if (pixelY >= this.clipY1 && pixelY < this.clipY2) {
							int pixelIndex = x1 + pixelY * this.surfaceWidth;
							int dest = this.surfaceRaster[pixelIndex];
							dest = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dest & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
							this.surfaceRaster[pixelIndex] = premultipliedColour + dest;
						}
						y1 += step;
						x1++;
					}
				} else if (mode == 2) {
					while (x1 <= x2) {
						int pixelY = y1 >> 16;
						if (pixelY >= this.clipY1 && pixelY < this.clipY2) {
							int pixelIndex = x1 + pixelY * this.surfaceWidth;
							int dest = this.surfaceRaster[pixelIndex];
							int sum = colour + dest;
							redBlueSum = (colour & 0xFF00FF) + (dest & 0xFF00FF);
							dest = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							this.surfaceRaster[pixelIndex] = sum - dest | dest - (dest >>> 8);
						}
						y1 += step;
						x1++;
					}
				} else {
					throw new IllegalArgumentException();
				}
			} else {
				x1 <<= 0x10;
				x1 += 32768;
				@Pc(357) int deltaFixed = x2 << 16;
				step = (int) Math.floor((double) deltaFixed / (double) y2 + 0.5D);
				y2 += y1;
				if (y1 < this.clipY1) {
					x1 += step * (this.clipY1 - y1);
					y1 = this.clipY1;
				}
				if (y2 >= this.clipY2) {
					y2 = this.clipY2 - 1;
				}
				alpha = colour >>> 24;
				if (mode == 0 || mode == 1 && alpha == 255) {
					while (y1 <= y2) {
						int pixelX = x1 >> 16;
						if (pixelX >= this.clipX1 && pixelX < this.clipX2) {
							this.surfaceRaster[pixelX + y1 * this.surfaceWidth] = colour;
						}
						x1 += step;
						y1++;
					}
				} else if (mode == 1) {
					premultipliedColour = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * alpha >> 8 & 0xFF00) + (alpha << 24);
					int inverseAlpha = 256 - alpha;
					while (y1 <= y2) {
						int pixelX = x1 >> 16;
						if (pixelX >= this.clipX1 && pixelX < this.clipX2) {
							int pixelIndex = pixelX + y1 * this.surfaceWidth;
							int dest = this.surfaceRaster[pixelIndex];
							dest = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dest & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
							this.surfaceRaster[pixelX + y1 * this.surfaceWidth] = premultipliedColour + dest;
						}
						x1 += step;
						y1++;
					}
				} else if (mode == 2) {
					while (y1 <= y2) {
						int pixelX = x1 >> 16;
						if (pixelX >= this.clipX1 && pixelX < this.clipX2) {
							int pixelIndex = pixelX + y1 * this.surfaceWidth;
							int dest = this.surfaceRaster[pixelIndex];
							int sum = colour + dest;
							redBlueSum = (colour & 0xFF00FF) + (dest & 0xFF00FF);
							@Pc(587) int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
							this.surfaceRaster[pixelIndex] = sum - overflow | overflow - (overflow >>> 8);
						}
						x1 += step;
						y1++;
					}
				} else {
					throw new IllegalArgumentException();
				}
			}
		} else if (y2 >= 0) {
			this.P(x1, y1, y2 + 1, colour, mode);
		} else {
			this.P(x1, y1 + y2, -y2 + 1, colour, mode);
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIIIIIIIII)V")
	@Override
	public void drawTriangle() {
		@Pc(3) JavaThreadResource resource = this.threadResource(Thread.currentThread());
		@Pc(6) Rasterizer rasterizer = resource.rasterizer;
		rasterizer.fastScanline = false;
		@Pc(14) int newX1 = 5 - this.viewX1;
		@Pc(19) int newX2 = 75 - this.viewX1;
		@Pc(24) int newX3 = 15 - this.viewX1;
		@Pc(29) int newY1 = 10 - this.viewY1;
		@Pc(34) int newY2 = 50 - this.viewY1;
		@Pc(39) int newY3 = 90 - this.viewY1;
		rasterizer.clamp = newX1 < 0 || newX1 > rasterizer.width || newX2 < 0 || newX2 > rasterizer.width || newX3 < 0 || newX3 > rasterizer.width;
		rasterizer.alpha = 0;
		rasterizer.halfBlend = false;
		rasterizer.renderTriangleRgb((float) newY1, (float) newY2, (float) newY3, (float) newX1, (float) newX2, (float) newX3, (float) 100, (float) 100, (float) 100, -65536, -65536, -65536);
		rasterizer.fastScanline = true;
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!oea;[Lclient!rh;Z)Lclient!da;")
	@Override
	public AbstractFontRenderer createFontRenderer(@OriginalArg(0) FontMetrics metrics, @OriginalArg(1) SpriteType[] images) {
		@Pc(3) int[] widths = new int[images.length];
		@Pc(7) int[] heights = new int[images.length];
		@Pc(9) boolean alpha = false;
		for (@Pc(11) int i = 0; i < images.length; i++) {
			widths[i] = images[i].width;
			heights[i] = images[i].height;
			if (images[i].alpha != null) {
				alpha = true;
			}
		}
		if (alpha) {
			return new JavaMonoAlphaFont(this, metrics, images, widths, heights);
		} else {
			return new JavaMonoFont(this, metrics, images, widths, heights);
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!he;Lclient!vm;)Lclient!is;")
	@Override
	public OffscreenSurface createOffscreenSurface(@OriginalArg(0) Sprite surface, @OriginalArg(1) DepthBuffer buffer) {
		return new JavaOffscreenSurface(this, (NativeSprite) surface, (JavaDepthBuffer) buffer);
	}

	@OriginalMember(owner = "client!ao", name = "C", descriptor = "(Z)V")
	@Override
	public void C(@OriginalArg(0) boolean zWrite) {
		@Pc(3) JavaThreadResource javaThreadResource = this.threadResource(Thread.currentThread());
		javaThreadResource.zWrite = zWrite;
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIILclient!aa;II)V")
	@Override
	public void drawSurfaceLine(@OriginalArg(0) int startX, @OriginalArg(1) int startY, @OriginalArg(2) int endX, @OriginalArg(3) int endY, @OriginalArg(4) int colour, @OriginalArg(6) ComponentSurface clippingMask, @OriginalArg(7) int surfaceClipLeft, @OriginalArg(8) int surfaceClipTop) {
		@Pc(2) JavaClippingMask javaClippingMask = (JavaClippingMask) clippingMask;
		@Pc(5) int[] lineWidths = javaClippingMask.lineWidths;
		@Pc(8) int[] lineOffsets = javaClippingMask.lineOffsets;
		@Pc(17) int clipTop = this.clipY1 > surfaceClipTop ? this.clipY1 : surfaceClipTop;
		@Pc(32) int clipBottom = this.clipY2 < surfaceClipTop + lineWidths.length ? this.clipY2 : surfaceClipTop + lineWidths.length;
		endX -= startX;
		endY -= startY;
		if (endX + endY < 0) {
			startX += endX;
			endX = -endX;
			startY += endY;
			endY = -endY;
		}
		@Pc(83) int step;
		@Pc(116) int alpha;
		@Pc(129) int local129;
		@Pc(133) int local133;
		@Pc(145) int local145;
		@Pc(226) int spanStart;
		@Pc(243) int index;
		@Pc(248) int dest;
		@Pc(201) int premultipliedColour;
		if (endX <= endY) {
			startX <<= 0x10;
			startX += 32768;
			@Pc(390) int deltaFixed = endX << 16;
			step = (int) Math.floor((double) deltaFixed / (double) endY + 0.5D);
			endY += startY;
			if (startY < clipTop) {
				startX += step * (clipTop - startY);
				startY = clipTop;
			}
			if (endY >= clipBottom) {
				endY = clipBottom - 1;
			}
			alpha = colour >>> 24;
			if (alpha == 255) {
				while (startY <= endY) {
					int pixelX = startX >> 16;
					int maskRow = startY - surfaceClipTop;
					int maskStart = surfaceClipLeft + lineWidths[maskRow];
					if (pixelX >= this.clipX1 && pixelX < this.clipX2 && pixelX >= maskStart && pixelX < maskStart + lineOffsets[maskRow]) {
						this.surfaceRaster[pixelX + startY * this.surfaceWidth] = colour;
					}
					startX += step;
					startY++;
				}
			} else {
				premultipliedColour = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * alpha >> 8 & 0xFF00) + (alpha << 24);
				int inverseAlpha = 256 - alpha;
				while (startY <= endY) {
					int pixelX = startX >> 16;
					int maskRow = startY - surfaceClipTop;
					spanStart = surfaceClipLeft + lineWidths[maskRow];
					if (pixelX >= this.clipX1 && pixelX < this.clipX2 && pixelX >= spanStart && pixelX < spanStart + lineOffsets[maskRow]) {
						index = pixelX + startY * this.surfaceWidth;
						dest = this.surfaceRaster[index];
						@Pc(584) int faded = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dest & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
						this.surfaceRaster[pixelX + startY * this.surfaceWidth] = premultipliedColour + faded;
					}
					startX += step;
					startY++;
				}
			}
			return;
		}
		startY <<= 0x10;
		startY += 32768;
		@Pc(73) int deltaFixed = endY << 16;
		step = (int) Math.floor((double) deltaFixed / (double) endX + 0.5D);
		endX += startX;
		if (startX < this.clipX1) {
			startY += step * (this.clipX1 - startX);
			startX = this.clipX1;
		}
		if (endX >= this.clipX2) {
			endX = this.clipX2 - 1;
		}
		alpha = colour >>> 24;
		if (alpha == 255) {
			while (startX <= endX) {
				int pixelY = startY >> 16;
				int maskRow = pixelY - surfaceClipTop;
				if (pixelY >= clipTop && pixelY < clipBottom) {
					int maskStart = surfaceClipLeft + lineWidths[maskRow];
					if (startX >= maskStart && startX < maskStart + lineOffsets[maskRow]) {
						this.surfaceRaster[startX + pixelY * this.surfaceWidth] = colour;
					}
				}
				startY += step;
				startX++;
			}
			return;
		}
		premultipliedColour = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * alpha >> 8 & 0xFF00) + (alpha << 24);
		int inverseAlpha = 256 - alpha;
		while (startX <= endX) {
			int pixelY = startY >> 16;
			int maskRow = pixelY - surfaceClipTop;
			if (pixelY >= clipTop && pixelY < clipBottom) {
				spanStart = surfaceClipLeft + lineWidths[maskRow];
				if (startX >= spanStart && startX < spanStart + lineOffsets[maskRow]) {
					index = startX + pixelY * this.surfaceWidth;
					dest = this.surfaceRaster[index];
					dest = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dest & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
					this.surfaceRaster[index] = premultipliedColour + dest;
				}
			}
			startY += step;
			startX++;
		}
	}

	@OriginalMember(owner = "client!ao", name = "XA", descriptor = "()I")
	@Override
	public int XA() {
		return this.zFar;
	}

	@OriginalMember(owner = "client!ao", name = "T", descriptor = "(IIII)V")
	@Override
	public void T(@OriginalArg(0) int clipX1, @OriginalArg(1) int clipY1, @OriginalArg(2) int clipX2, @OriginalArg(3) int clipY2) {
		if (this.clipX1 < clipX1) {
			this.clipX1 = clipX1;
		}
		if (this.clipY1 < clipY1) {
			this.clipY1 = clipY1;
		}
		if (this.clipX2 > clipX2) {
			this.clipX2 = clipX2;
		}
		if (this.clipY2 > clipY2) {
			this.clipY2 = clipY2;
		}
		this.updateViewport();
	}

	@OriginalMember(owner = "client!ao", name = "aa", descriptor = "(IIIIII)V")
	@Override
	public void aa(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) int height, @OriginalArg(4) int colour, @OriginalArg(5) int mode) {
		if (x < this.clipX1) {
			width -= this.clipX1 - x;
			x = this.clipX1;
		}
		if (y < this.clipY1) {
			height -= this.clipY1 - y;
			y = this.clipY1;
		}
		if (x + width > this.clipX2) {
			width = this.clipX2 - x;
		}
		if (y + height > this.clipY2) {
			height = this.clipY2 - y;
		}
		if (width <= 0 || height <= 0 || x > this.clipX2 || y > this.clipY2) {
			return;
		}
		@Pc(67) int rowSkip = this.surfaceWidth - width;
		@Pc(74) int pixelIndex = x + y * this.surfaceWidth;
		@Pc(78) int alpha = colour >>> 24;
		@Pc(90) int local90;
		@Pc(94) int local94;
		@Pc(101) int local101;
		if (mode != 0 && (mode != 1 || alpha != 255)) {
			@Pc(217) int local217;
			if (mode == 1) {
				@Pc(201) int premultipliedColour = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + (((colour & 0xFF00FF00) >>> 8) * alpha & 0xFF00FF00);
				int inverseAlpha = 256 - alpha;
				for (int row = 0; row < height; row++) {
					for (int column = -width; column < 0; column++) {
						int dest = this.surfaceRaster[pixelIndex];
						dest = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + (((dest & 0xFF00FF00) >>> 8) * inverseAlpha & 0xFF00FF00);
						this.surfaceRaster[pixelIndex++] = premultipliedColour + dest;
					}
					pixelIndex += rowSkip;
				}
			} else if (mode == 2) {
				for (int row = 0; row < height; row++) {
					for (int column = -width; column < 0; column++) {
						int dest = this.surfaceRaster[pixelIndex];
						int sum = colour + dest;
						@Pc(284) int redBlueSum = (colour & 0xFF00FF) + (dest & 0xFF00FF);
						@Pc(294) int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
						this.surfaceRaster[pixelIndex++] = sum - overflow | overflow - (overflow >>> 8);
					}
					pixelIndex += rowSkip;
				}
			} else {
				throw new IllegalArgumentException();
			}
			return;
		}
		int blockCount = width >> 3;
		int remainder = width & 0x7;
		width = pixelIndex - 1;
		for (int row = -height; row < 0; row++) {
			if (blockCount > 0) {
				x = blockCount;
				do {
					width++;
					this.surfaceRaster[width] = colour;
					width++;
					this.surfaceRaster[width] = colour;
					width++;
					this.surfaceRaster[width] = colour;
					width++;
					this.surfaceRaster[width] = colour;
					width++;
					this.surfaceRaster[width] = colour;
					width++;
					this.surfaceRaster[width] = colour;
					width++;
					this.surfaceRaster[width] = colour;
					width++;
					this.surfaceRaster[width] = colour;
					x--;
				} while (x > 0);
			}
			if (remainder > 0) {
				x = remainder;
				do {
					width++;
					this.surfaceRaster[width] = colour;
					x--;
				} while (x > 0);
			}
			width += rowSkip;
		}
	}

	@OriginalMember(owner = "client!ao", name = "j", descriptor = "(I)[I")
	public int[] getTexels(@OriginalArg(0) int id) {
		@Pc(2) LRUCache cache = this.textureCache;
		@Pc(12) SafeModeTexture2D texture;
		synchronized (this.textureCache) {
			texture = (SafeModeTexture2D) this.textureCache.get((long) id);
			if (texture == null) {
				if (!super.materialProvider.textureAvailable(id)) {
					return null;
				}
				@Pc(32) MaterialType metrics = super.materialProvider.getMetrics(id);
				@Pc(43) int size = metrics.isSmall || this.shrinkTextures ? 64 : this.textureSize;
				texture = new SafeModeTexture2D(id, size, super.materialProvider.getPixels(true, id, 0.7F, size, size), metrics.effectCombiner != 1);
				this.textureCache.put(texture, (long) id);
			}
		}
		texture.isAnimated = true;
		return texture.getArgbColor();
	}

	@OriginalMember(owner = "client!ao", name = "h", descriptor = "()V")
	@Override
	public void stop() {
		if (this.colourTables) {
			ColorUtil.releaseColorTables(false, true);
			this.colourTables = false;
		}
		this.canvasSurface = null;
		this.canvas = null;
		this.width = 0;
		this.height = 0;
		this.surfaces = null;
		this.stopped = true;
	}

	@OriginalMember(owner = "client!ao", name = "e", descriptor = "()Lclient!bca;")
	@Override
	public Matrix camera() {
		return this.camera;
	}

	@OriginalMember(owner = "client!ao", name = "k", descriptor = "(I)[I")
	public int[] getArgbTexture(@OriginalArg(0) int id) {
		@Pc(2) LRUCache cache = this.textureCache;
		@Pc(14) SafeModeTexture2D texture;
		synchronized (this.textureCache) {
			texture = (SafeModeTexture2D) this.textureCache.get((long) id | Long.MIN_VALUE);
			if (texture == null) {
				if (!super.materialProvider.textureAvailable(id)) {
					return null;
				}
				@Pc(34) MaterialType metrics = super.materialProvider.getMetrics(id);
				@Pc(45) int size = metrics.isSmall || this.shrinkTextures ? 64 : this.textureSize;
				texture = new SafeModeTexture2D(id, size, super.materialProvider.getPixelsArgb(size, 0.7F, id, size), metrics.effectCombiner != 1);
				this.textureCache.put(texture, (long) id | Long.MIN_VALUE);
			}
		}
		texture.isAnimated = true;
		return texture.getArgbColor();
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(II[[I[[IIII)Lclient!s;")
	@Override
	public Ground createGround(@OriginalArg(0) int width, @OriginalArg(1) int length, @OriginalArg(2) int[][] tileHeights, @OriginalArg(3) int[][] heights, @OriginalArg(5) int groundFlags, @OriginalArg(6) int featureFlags) {
		return new JavaGround(this, groundFlags, featureFlags, width, length, tileHeights, heights, 512);
	}

	@OriginalMember(owner = "client!ao", name = "H", descriptor = "(III[I)V")
	@Override
	public void H(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] screenCoords) {
		@Pc(24) float depth = this.camera.tz + this.camera.e3_1 * (float) x + this.camera.e3_2 * (float) y + this.camera.e3_3 * (float) z;
		if (depth == 0.0F) {
			screenCoords[0] = screenCoords[1] = screenCoords[2] = -1;
			return;
		}
		@Pc(73) int screenX = (int) ((float) this.projectionScaleX * (this.camera.tx + this.camera.e1_1 * (float) x + this.camera.e1_2 * (float) y + this.camera.e1_3 * (float) z) / depth);
		@Pc(105) int screenY = (int) ((float) this.projectionScaleY * (this.camera.ty + this.camera.e2_1 * (float) x + this.camera.e2_2 * (float) y + this.camera.e2_3 * (float) z) / depth);
		screenCoords[0] = screenX - this.viewX1;
		screenCoords[1] = screenY - this.viewY1;
		screenCoords[2] = (int) depth;
	}

	@OriginalMember(owner = "client!ao", name = "j", descriptor = "()Lclient!bca;")
	@Override
	public Matrix createMatrix() {
		return new JavaMatrix();
	}

	@OriginalMember(owner = "client!ao", name = "la", descriptor = "()V")
	@Override
	public void la() {
		this.clipX1 = 0;
		this.clipY1 = 0;
		this.clipX2 = this.surfaceWidth;
		this.clipY2 = this.surfaceHeight;
		this.updateViewport();
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!bh;IIII)Lclient!ka;")
	@Override
	public Model createModel(@OriginalArg(0) Mesh mesh, @OriginalArg(1) int functionMask, @OriginalArg(2) int featureMask, @OriginalArg(3) int ambient, @OriginalArg(4) int contrast) {
		return new JavaModel(this, mesh, functionMask, ambient, contrast, featureMask);
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(II)Lclient!vm;")
	@Override
	public DepthBuffer createDepthBuffer(@OriginalArg(0) int width, @OriginalArg(1) int height) {
		return new JavaDepthBuffer(width, height);
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "([I)V")
	@Override
	public void accumulateViewportDimensions(@OriginalArg(0) int[] cacheInto) {
		cacheInto[0] = this.surfaceWidth;
		cacheInto[1] = this.surfaceHeight;
	}

	@OriginalMember(owner = "client!ao", name = "F", descriptor = "(II)V")
	@Override
	public void F(@OriginalArg(0) int dx, @OriginalArg(1) int dy) {
		@Pc(6) int rasterOffset = dy * this.surfaceWidth + dx;
		@Pc(13) int depthOffset = dy * this.depthWidth + dx;
		if (rasterOffset == 0 && depthOffset == 0) {
			return;
		}
		@Pc(21) int[] raster = this.surfaceRaster;
		@Pc(24) float[] depthBuffer = this.depthBuffer;
		@Pc(31) int length;
		if (rasterOffset < 0) {
			length = raster.length + rasterOffset;
			Arrays.copy(raster, -rasterOffset, raster, 0, length);
		} else if (rasterOffset > 0) {
			length = raster.length - rasterOffset;
			Arrays.copy(raster, 0, raster, rasterOffset, length);
		}
		if (depthOffset < 0) {
			length = depthBuffer.length + depthOffset;
			Arrays.copy(depthBuffer, -depthOffset, depthBuffer, 0, length);
		} else if (depthOffset > 0) {
			length = depthBuffer.length - depthOffset;
			Arrays.copy(depthBuffer, 0, depthBuffer, depthOffset, length);
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!is;)V")
	@Override
	public void swapSurface(@OriginalArg(0) OffscreenSurface offscreenSurface) {
		@Pc(2) JavaOffscreenSurface javaSurface = (JavaOffscreenSurface) offscreenSurface;
		this.surfaceWidth = javaSurface.width;
		this.surfaceHeight = javaSurface.height;
		this.surfaceRaster = javaSurface.raster;
		this.offscreenSurface = javaSurface;
		this.depthWidth = javaSurface.width;
		this.depthHeight = javaSurface.height;
		this.depthBuffer = javaSurface.depthBuffer;
		this.reset();
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIIIIII)V")
	public void drawBillboardRgb(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int halfWidth, @OriginalArg(4) int halfHeight, @OriginalArg(5) int rotation, @OriginalArg(6) int id, @OriginalArg(7) int colour, @OriginalArg(8) int op, @OriginalArg(9) int mode) {
		if (halfWidth == 0 || halfHeight == 0) {
			return;
		}
		if (id == 65535 || super.materialProvider.getMetrics(id).detailsOnly) {
			this.fillDepthTestedCircle(x, y, z, halfWidth, colour, mode);
			return;
		}
		if (this.textureId != id) {
			@Pc(26) NativeSprite sprite = (NativeSprite) this.spriteCache.get((long) id);
			if (sprite == null) {
				@Pc(32) int[] data = this.getArgbTexture(id);
				if (data == null) {
					return;
				}
				@Pc(44) int size = this.smallTexture(id) ? 64 : this.textureSize;
				sprite = this.createNativeSprite(size, data, size, size);
				this.spriteCache.put(sprite, (long) id);
			}
			this.textureId = id;
			this.cachedBillboardSprite = sprite;
		}
		((SpriteSM) this.cachedBillboardSprite).renderDepthTestedRgb(x - halfWidth, y - halfHeight, z, halfWidth << 1, halfHeight << 1, op, colour, mode);
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!wh;)V")
	@Override
	public void setSunCubeTexture(@OriginalArg(0) EnvironmentSampler environmentSampler) {
	}

	@OriginalMember(owner = "client!ao", name = "EA", descriptor = "(IIII)V")
	@Override
	public void EA(@OriginalArg(0) int waterHeight, @OriginalArg(1) int fogColour, @OriginalArg(2) int waterDepth, @OriginalArg(3) int waterIntensity) {
		@Pc(3) JavaThreadResource javaThreadResource = this.threadResource(Thread.currentThread());
		javaThreadResource.waterHeight = waterHeight;
		javaThreadResource.fogColour = fogColour;
		javaThreadResource.waterDepth = waterDepth;
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(IIIIIIII)V")
	private void verticalDashedLine(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int length, @OriginalArg(3) int colour, @OriginalArg(5) int dashLength, @OriginalArg(6) int gapLength, @OriginalArg(7) int phase) {
		if (x < this.clipX1 || x >= this.clipX2) {
			return;
		}
		@Pc(15) int pixel = x + y * this.surfaceWidth;
		@Pc(19) int alpha = colour >>> 24;
		@Pc(23) int period = dashLength + gapLength;
		@Pc(27) int offset = phase % period;
		@Pc(37) int local37;
		if (alpha == 255) {
			int row = 0;
			while (row < length) {
				if (y + row >= this.clipY1 && y + row < this.clipY2 && offset < dashLength) {
					this.surfaceRaster[pixel + row * this.surfaceWidth] = colour;
				}
				row++;
				offset++;
				offset %= period;
			}
			return;
		}
		@Pc(100) int blended = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * alpha >> 8 & 0xFF00) + (alpha << 24);
		int inverseAlpha = 256 - alpha;
		@Pc(106) int i = 0;
		while (i < length) {
			if (y + i >= this.clipY1 && y + i < this.clipY2 && offset < dashLength) {
				@Pc(129) int index = pixel + i * this.surfaceWidth;
				@Pc(134) int dest = this.surfaceRaster[index];
				@Pc(154) int faded = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dest & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
				this.surfaceRaster[index] = blended + faded;
			}
			i++;
			offset++;
			offset %= period;
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Ljava/awt/Canvas;)V")
	@Override
	public void setCanvas(@OriginalArg(0) Canvas canvas2) {
		if (canvas2 == null) {
			this.canvas = null;
			this.canvasSurface = null;
			if (this.offscreenSurface == null) {
				this.surfaceRaster = null;
				this.surfaceWidth = this.surfaceHeight = 1;
				this.depthWidth = this.depthHeight = 1;
				this.reset();
			}
			return;
		}
		@Pc(10) JavaSurface surface = (JavaSurface) this.surfaces.get((long) canvas2.hashCode());
		if (surface == null) {
			return;
		}
		this.canvas = canvas2;
		@Pc(18) Dimension dimension = canvas2.getSize();
		this.width = dimension.width;
		this.height = dimension.height;
		this.canvasSurface = surface;
		if (this.offscreenSurface != null) {
			return;
		}
		this.surfaceRaster = surface.raster;
		this.surfaceWidth = surface.width;
		this.surfaceHeight = surface.height;
		if (this.surfaceWidth != this.depthWidth || this.surfaceHeight != this.depthHeight) {
			this.mainSurfaceWidth = this.depthWidth = this.surfaceWidth;
			this.mainSurfaceHeight = this.depthHeight = this.surfaceHeight;
			this.mainDepthBuffer = this.depthBuffer = new float[this.depthWidth * this.depthHeight];
		}
		this.reset();
		return;
	}

	@OriginalMember(owner = "client!ao", name = "JA", descriptor = "(IIIIII)I")
	@Override
	public int JA(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int z1, @OriginalArg(3) int x2, @OriginalArg(4) int y2, @OriginalArg(5) int z2) {
		@Pc(1) int outcode = 0;
		@Pc(26) float depth1 = this.camera.e3_1 * (float) x1 + this.camera.e3_2 * (float) y1 + this.camera.e3_3 * (float) z1 + this.camera.tz;
		if (depth1 < 1.0F) {
			depth1 = 1.0F;
		}
		@Pc(57) float depth2 = this.camera.e3_1 * (float) x2 + this.camera.e3_2 * (float) y2 + this.camera.e3_3 * (float) z2 + this.camera.tz;
		if (depth2 < 1.0F) {
			depth2 = 1.0F;
		}
		if (depth1 < (float) this.zNear && depth2 < (float) this.zNear) {
			outcode |= 0x10;
		} else if (depth1 > (float) this.zFar && depth2 > (float) this.zFar) {
			outcode |= 0x20;
		}
		@Pc(128) int screenX1 = (int) ((float) this.projectionScaleX * (this.camera.e1_1 * (float) x1 + this.camera.e1_2 * (float) y1 + this.camera.e1_3 * (float) z1 + this.camera.tx) / depth1);
		@Pc(160) int screenX2 = (int) ((float) this.projectionScaleX * (this.camera.e1_1 * (float) x2 + this.camera.e1_2 * (float) y2 + this.camera.e1_3 * (float) z2 + this.camera.tx) / depth2);
		if (screenX1 < this.viewX1 && screenX2 < this.viewX1) {
			outcode |= 0x1;
		} else if (screenX1 > this.viewX2 && screenX2 > this.viewX2) {
			outcode |= 0x2;
		}
		@Pc(217) int screenY1 = (int) ((float) this.projectionScaleY * (this.camera.e2_1 * (float) x1 + this.camera.e2_2 * (float) y1 + this.camera.e2_3 * (float) z1 + this.camera.ty) / depth1);
		@Pc(249) int screenY2 = (int) ((float) this.projectionScaleY * (this.camera.e2_1 * (float) x2 + this.camera.e2_2 * (float) y2 + this.camera.e2_3 * (float) z2 + this.camera.ty) / depth2);
		if (screenY1 < this.viewY1 && screenY2 < this.viewY1) {
			outcode |= 0x4;
		} else if (screenY1 > this.viewY2 && screenY2 > this.viewY2) {
			outcode |= 0x8;
		}
		return outcode;
	}

	@OriginalMember(owner = "client!ao", name = "f", descriptor = "(I)V")
	@Override
	public void method7281(@OriginalArg(0) int threadId) {
		this.resources[threadId].bindThread(null);
	}

	@OriginalMember(owner = "client!ao", name = "p", descriptor = "()V")
	@Override
	public void restoreSurface() {
		if (this.canvas == null) {
			this.surfaceWidth = 1;
			this.surfaceHeight = 1;
			this.surfaceRaster = null;
			this.depthWidth = 1;
			this.depthHeight = 1;
			this.depthBuffer = null;
		} else {
			this.surfaceRaster = this.canvasSurface.raster;
			this.surfaceWidth = this.canvasSurface.width;
			this.surfaceHeight = this.canvasSurface.height;
			this.depthBuffer = this.mainDepthBuffer;
			this.depthWidth = this.mainSurfaceWidth;
			this.depthHeight = this.mainSurfaceHeight;
		}
		this.offscreenSurface = null;
		this.reset();
	}

	@OriginalMember(owner = "client!ao", name = "l", descriptor = "(I)I")
	public int textureHsl(@OriginalArg(0) int id) {
		return super.materialProvider.getMetrics(id).colorHsl & 0xFFFF;
	}

	@OriginalMember(owner = "client!ao", name = "w", descriptor = "()Z")
	@Override
	public boolean supportsAntiAliasing() {
		return false;
	}

	@OriginalMember(owner = "client!ao", name = "o", descriptor = "()Z")
	@Override
	public boolean hasBloom() {
		return false;
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "([IIIIIZ)Lclient!mi;")
	@Override
	public NativeSprite createNativeSprite(@OriginalArg(0) int[] srcPixels, @OriginalArg(2) int srcStride, @OriginalArg(3) int width, @OriginalArg(4) int height, @OriginalArg(5) boolean copyPixels) {
		@Pc(1) boolean transparent = false;
		@Pc(3) int index = 0;
		for (@Pc(5) int i = 0; i < height; i++) {
			for (@Pc(8) int j = 0; j < width; j++) {
				@Pc(16) int alpha = srcPixels[index++] >>> 24;
				if (alpha != 0 && alpha != 255) {
					transparent = true;
					return transparent ? new TransparentSpriteSM(this, srcPixels, 0, srcStride, width, height, copyPixels) : new OpaqueSpriteSM(this, srcPixels, 0, srcStride, width, height, copyPixels);
				}
			}
		}
		return transparent ? new TransparentSpriteSM(this, srcPixels, 0, srcStride, width, height, copyPixels) : new OpaqueSpriteSM(this, srcPixels, 0, srcStride, width, height, copyPixels);
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!rh;Z)Lclient!mi;")
	@Override
	public NativeSprite generateSprite(@OriginalArg(0) SpriteType indexedImage, @OriginalArg(1) boolean paletteSprite) {
		@Pc(2) int[] pallete = indexedImage.pallete;
		@Pc(5) byte[] pixels = indexedImage.pixels;
		@Pc(8) int width = indexedImage.width;
		@Pc(11) int height = indexedImage.height;
		@Pc(76) SpriteSM javaSprite;
		@Pc(20) int[] local20;
		@Pc(25) byte[] local25;
		@Pc(27) int y;
		@Pc(32) int local32;
		@Pc(34) int x;
		if (paletteSprite && indexedImage.alpha == null) {
			int[] paletteCopy = new int[pallete.length];
			byte[] paletteIndices = new byte[width * height];
			for (y = 0; y < height; y++) {
				int rowOffset = y * width;
				for (x = 0; x < width; x++) {
					paletteIndices[rowOffset + x] = pixels[rowOffset + x];
				}
			}
			for (int paletteIndex = 0; paletteIndex < pallete.length; paletteIndex++) {
				paletteCopy[paletteIndex] = pallete[paletteIndex];
			}
			javaSprite = new PaletteSpriteSM(this, paletteIndices, paletteCopy, width, height);
		} else {
			int[] argbPixels = new int[width * height];
			byte[] alphaChannel = indexedImage.alpha;
			if (alphaChannel == null) {
				for (y = 0; y < height; y++) {
					int rowOffset = y * width;
					for (x = 0; x < width; x++) {
						@Pc(156) int rgb = pallete[pixels[rowOffset + x] & 0xFF];
						argbPixels[rowOffset + x] = rgb == 0 ? 0 : rgb | 0xFF000000;
					}
				}
				javaSprite = new OpaqueSpriteSM(this, argbPixels, width, height);
			} else {
				for (y = 0; y < height; y++) {
					int rowOffset = y * width;
					for (x = 0; x < width; x++) {
						argbPixels[rowOffset + x] = pallete[pixels[rowOffset + x] & 0xFF] | alphaChannel[rowOffset + x] << 24;
					}
				}
				javaSprite = new TransparentSpriteSM(this, argbPixels, width, height);
			}
		}
		javaSprite.setOffsets(indexedImage.minX, indexedImage.minY, indexedImage.borderX, indexedImage.borderY);
		return javaSprite;
	}

	@OriginalMember(owner = "client!ao", name = "f", descriptor = "(IIIIII)V")
	private void fillDepthTestedCircle(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int size, @OriginalArg(4) int colour, @OriginalArg(5) int mode) {
		if (size < 0) {
			size = -size;
		}
		@Pc(8) int y1 = y - size;
		if (y1 < this.clipY1) {
			y1 = this.clipY1;
		}
		@Pc(21) int y2 = y + size + 1;
		if (y2 > this.clipY2) {
			y2 = this.clipY2;
		}
		@Pc(30) int currY = y1;
		@Pc(34) int squareRadius = size * size;
		@Pc(36) int width = 0;
		@Pc(40) int dy = y - y1;
		@Pc(44) int distanceSq = dy * dy;
		@Pc(48) int midpointDistanceSq = distanceSq - dy;
		if (y > y2) {
			y = y2;
		}
		@Pc(57) int alpha = colour >>> 24;
		@Pc(91) int local91;
		@Pc(102) int local102;
		@Pc(116) int local116;
		@Pc(118) int local118;
		if (mode == 0 || mode == 1 && alpha == 255) {
			while (currY < y) {
				while (midpointDistanceSq <= squareRadius || distanceSq <= squareRadius) {
					distanceSq += width + width;
					midpointDistanceSq += width++ + width;
				}
				int startX = x + 1 - width;
				if (startX < this.clipX1) {
					startX = this.clipX1;
				}
				int endX = x + width;
				if (endX > this.clipX2) {
					endX = this.clipX2;
				}
				int pixelIndex = startX + currY * this.surfaceWidth;
				for (int currX = startX; currX < endX; currX++) {
					if ((float) z < this.depthBuffer[pixelIndex]) {
						this.surfaceRaster[pixelIndex] = colour;
					}
					pixelIndex++;
				}
				currY++;
				distanceSq -= dy-- + dy;
				midpointDistanceSq -= dy + dy;
			}
			width = size;
			dy = currY - y;
			midpointDistanceSq = dy * dy + squareRadius;
			distanceSq = midpointDistanceSq - size;
			midpointDistanceSq -= dy;
			while (currY < y2) {
				while (midpointDistanceSq > squareRadius && distanceSq > squareRadius) {
					midpointDistanceSq -= width-- + width;
					distanceSq -= width + width;
				}
				int startX = x - width;
				if (startX < this.clipX1) {
					startX = this.clipX1;
				}
				int endX = x + width;
				if (endX > this.clipX2 - 1) {
					endX = this.clipX2 - 1;
				}
				int pixelIndex = startX + currY * this.surfaceWidth;
				for (int currX = startX; currX <= endX; currX++) {
					if ((float) z < this.depthBuffer[pixelIndex]) {
						this.surfaceRaster[pixelIndex] = colour;
					}
					pixelIndex++;
				}
				currY++;
				midpointDistanceSq += dy + dy;
				distanceSq += dy++ + dy;
			}
			return;
		}
		@Pc(350) int local350;
		@Pc(364) int local364;
		if (mode == 1) {
			@Pc(293) int blended = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * alpha >> 8 & 0xFF00) + (alpha << 24);
			int inverseAlpha = 256 - alpha;
			while (currY < y) {
				while (midpointDistanceSq <= squareRadius || distanceSq <= squareRadius) {
					distanceSq += width + width;
					midpointDistanceSq += width++ + width;
				}
				int startX = x + 1 - width;
				if (startX < this.clipX1) {
					startX = this.clipX1;
				}
				int endX = x + width;
				if (endX > this.clipX2) {
					endX = this.clipX2;
				}
				int pixelIndex = startX + currY * this.surfaceWidth;
				for (int currX = startX; currX < endX; currX++) {
					if ((float) z < this.depthBuffer[pixelIndex]) {
						int dest = this.surfaceRaster[pixelIndex];
						dest = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dest & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
						this.surfaceRaster[pixelIndex] = blended + dest;
					}
					pixelIndex++;
				}
				currY++;
				distanceSq -= dy-- + dy;
				midpointDistanceSq -= dy + dy;
			}
			width = size;
			dy = -dy;
			midpointDistanceSq = dy * dy + squareRadius;
			distanceSq = midpointDistanceSq - size;
			midpointDistanceSq -= dy;
			while (currY < y2) {
				while (midpointDistanceSq > squareRadius && distanceSq > squareRadius) {
					midpointDistanceSq -= width-- + width;
					distanceSq -= width + width;
				}
				int startX = x - width;
				if (startX < this.clipX1) {
					startX = this.clipX1;
				}
				int endX = x + width;
				if (endX > this.clipX2 - 1) {
					endX = this.clipX2 - 1;
				}
				int pixelIndex = startX + currY * this.surfaceWidth;
				for (int currX = startX; currX <= endX; currX++) {
					if ((float) z < this.depthBuffer[pixelIndex]) {
						int dest = this.surfaceRaster[pixelIndex];
						dest = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dest & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
						this.surfaceRaster[pixelIndex] = blended + dest;
					}
					pixelIndex++;
				}
				currY++;
				midpointDistanceSq += dy + dy;
				distanceSq += dy++ + dy;
			}
		} else if (mode == 2) {
			@Pc(633) int redBlueSum;
			while (currY < y) {
				while (midpointDistanceSq <= squareRadius || distanceSq <= squareRadius) {
					distanceSq += width + width;
					midpointDistanceSq += width++ + width;
				}
				int startX = x + 1 - width;
				if (startX < this.clipX1) {
					startX = this.clipX1;
				}
				int endX = x + width;
				if (endX > this.clipX2) {
					endX = this.clipX2;
				}
				int pixelIndex = startX + currY * this.surfaceWidth;
				for (int currX = startX; currX < endX; currX++) {
					if ((float) z < this.depthBuffer[pixelIndex]) {
						int dest = this.surfaceRaster[pixelIndex];
						int sum = colour + dest;
						redBlueSum = (colour & 0xFF00FF) + (dest & 0xFF00FF);
						@Pc(643) int overflow = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
						this.surfaceRaster[pixelIndex] = sum - overflow | overflow - (overflow >>> 8);
					}
					pixelIndex++;
				}
				currY++;
				distanceSq -= dy-- + dy;
				midpointDistanceSq -= dy + dy;
			}
			width = size;
			dy = -dy;
			midpointDistanceSq = dy * dy + squareRadius;
			distanceSq = midpointDistanceSq - size;
			midpointDistanceSq -= dy;
			while (currY < y2) {
				while (midpointDistanceSq > squareRadius && distanceSq > squareRadius) {
					midpointDistanceSq -= width-- + width;
					distanceSq -= width + width;
				}
				int startX = x - width;
				if (startX < this.clipX1) {
					startX = this.clipX1;
				}
				int endX = x + width;
				if (endX > this.clipX2 - 1) {
					endX = this.clipX2 - 1;
				}
				int pixelIndex = startX + currY * this.surfaceWidth;
				for (int currX = startX; currX <= endX; currX++) {
					if ((float) z < this.depthBuffer[pixelIndex]) {
						int dest = this.surfaceRaster[pixelIndex];
						int sum = colour + dest;
						redBlueSum = (colour & 0xFF00FF) + (dest & 0xFF00FF);
						dest = (redBlueSum & 0x1000100) + (sum - redBlueSum & 0x10000);
						this.surfaceRaster[pixelIndex] = sum - dest | dest - (dest >>> 8);
					}
					pixelIndex++;
				}
				currY++;
				midpointDistanceSq += dy + dy;
				distanceSq += dy++ + dy;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIII)V")
	@Override
	public void strongLine(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int x2, @OriginalArg(3) int y2, @OriginalArg(4) int colour, @OriginalArg(5) int strength) {
		@Pc(3) JavaThreadResource resource = this.threadResource(Thread.currentThread());
		@Pc(6) Rasterizer rasterizer = resource.rasterizer;
		@Pc(10) int width = x2 - x1;
		@Pc(14) int height = y2 - y1;
		@Pc(21) int absWidth = width >= 0 ? width : -width;
		@Pc(28) int absHeight = height >= 0 ? height : -height;
		@Pc(30) int minDimension = absWidth;
		if (absWidth < absHeight) {
			minDimension = absHeight;
		}
		if (minDimension == 0) {
			return;
		}
		@Pc(44) int deltaX = (width << 16) / minDimension;
		@Pc(50) int deltaY = (height << 16) / minDimension;
		width += deltaX >> 16;
		height += deltaY >> 16;
		if (deltaY <= deltaX) {
			deltaX = -deltaX;
		} else {
			deltaY = -deltaY;
		}
		@Pc(78) int strongX1 = strength * deltaY >> 17;
		@Pc(86) int strongX2 = strength * deltaY + 1 >> 17;
		@Pc(92) int strongY1 = strength * deltaX >> 17;
		@Pc(100) int strongY2 = strength * deltaX + 1 >> 17;
		@Pc(105) int centerX = x1 - rasterizer.offsetX();
		@Pc(110) int centerY = y1 - rasterizer.offsetY();
		@Pc(114) int lineX1 = centerX + strongX1;
		@Pc(118) int lineX2 = centerX - strongX2;
		@Pc(124) int lineX3 = centerX + width - strongX2;
		@Pc(130) int lineX4 = centerX + width + strongX1;
		@Pc(134) int lineY1 = centerY + strongY1;
		@Pc(138) int lineY2 = centerY - strongY2;
		@Pc(144) int lineY3 = centerY + height - strongY2;
		@Pc(150) int lineY4 = centerY + height + strongY1;
		rasterizer.alpha = 0;
		this.C(false);
		rasterizer.clamp = lineX1 < 0 || lineX1 > rasterizer.width || lineX2 < 0 || lineX2 > rasterizer.width || lineX3 < 0 || lineX3 > rasterizer.width;
		rasterizer.renderFlatTriangleRgb((float) lineY1, (float) lineY2, (float) lineY3, (float) lineX1, (float) lineX2, (float) lineX3, 100.0F, 100.0F, 100.0F, colour);
		rasterizer.clamp = lineX1 < 0 || lineX1 > rasterizer.width || lineX3 < 0 || lineX3 > rasterizer.width || lineX4 < 0 || lineX4 > rasterizer.width;
		rasterizer.renderFlatTriangleRgb((float) lineY1, (float) lineY3, (float) lineY4, (float) lineX1, (float) lineX3, (float) lineX4, 100.0F, 100.0F, 100.0F, colour);
		this.C(true);
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIZ)Lclient!mi;")
	@Override
	public NativeSprite generateEmptySprite(@OriginalArg(0) int width, @OriginalArg(1) int height, @OriginalArg(2) boolean hasAlpha) {
		return hasAlpha ? new TransparentSpriteSM(this, width, height) : new OpaqueSpriteSM(this, width, height);
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Z)V")
	@Override
	public void setShrinkTextures(@OriginalArg(0) boolean shrinkTextures) {
		this.shrinkTextures = shrinkTextures;
		this.textureCache.reset();
	}

	@OriginalMember(owner = "client!ao", name = "c", descriptor = "(I)V")
	@Override
	public void initialize() {
		ModelSM.maxModelVertices = 10000;
		ModelSM.maxModelTriangles = 10000;
		if (this.threadCount > 1) {
			throw new IllegalStateException("No MT");
		}
		this.allocateThreads(this.threadCount);
		this.linkThreads(0);
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIF)Lclient!ab;")
	@Override
	public PointLight createPointLight(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int radius, @OriginalArg(4) int colour, @OriginalArg(5) float intensity) {
		return null;
	}

	@OriginalMember(owner = "client!ao", name = "m", descriptor = "()Z")
	@Override
	public boolean bloom() {
		return false;
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Lclient!wh;Lclient!wh;FLclient!wh;)Lclient!wh;")
	@Override
	public EnvironmentSampler drawQuad(@OriginalArg(0) EnvironmentSampler environmentSampler, @OriginalArg(1) EnvironmentSampler environmentSampler2, @OriginalArg(2) float alpha, @OriginalArg(3) EnvironmentSampler environmentSampler3) {
		return null;
	}

	@OriginalMember(owner = "client!ao", name = "za", descriptor = "(IIIII)V")
	@Override
	public void za(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int radius, @OriginalArg(3) int colour, @OriginalArg(4) int mode) {
		if (radius < 0) {
			radius = -radius;
		}
		@Pc(8) int y1 = y - radius;
		if (y1 < this.clipY1) {
			y1 = this.clipY1;
		}
		@Pc(21) int y2 = y + radius + 1;
		if (y2 > this.clipY2) {
			y2 = this.clipY2;
		}
		@Pc(30) int currY = y1;
		@Pc(34) int squareRadius = radius * radius;
		@Pc(36) int halfWidth = 0;
		@Pc(40) int dy = y - y1;
		@Pc(44) int distanceSq = dy * dy;
		@Pc(48) int midpointDistanceSq = distanceSq - dy;
		if (y > y2) {
			y = y2;
		}
		@Pc(57) int alpha = colour >>> 24;
		@Pc(91) int local91;
		@Pc(102) int local102;
		@Pc(116) int local116;
		@Pc(118) int local118;
		if (alpha == 255) {
			while (currY < y) {
				while (midpointDistanceSq <= squareRadius || distanceSq <= squareRadius) {
					distanceSq += halfWidth + halfWidth;
					midpointDistanceSq += halfWidth++ + halfWidth;
				}
				int startX = x + 1 - halfWidth;
				if (startX < this.clipX1) {
					startX = this.clipX1;
				}
				int endX = x + halfWidth;
				if (endX > this.clipX2) {
					endX = this.clipX2;
				}
				int pixelIndex = startX + currY * this.surfaceWidth;
				for (int currX = startX; currX < endX; currX++) {
					this.surfaceRaster[pixelIndex++] = colour;
				}
				currY++;
				distanceSq -= dy-- + dy;
				midpointDistanceSq -= dy + dy;
			}
			halfWidth = radius;
			dy = currY - y;
			midpointDistanceSq = dy * dy + squareRadius;
			distanceSq = midpointDistanceSq - radius;
			midpointDistanceSq -= dy;
			while (currY < y2) {
				while (midpointDistanceSq > squareRadius && distanceSq > squareRadius) {
					midpointDistanceSq -= halfWidth-- + halfWidth;
					distanceSq -= halfWidth + halfWidth;
				}
				int startX = x - halfWidth;
				if (startX < this.clipX1) {
					startX = this.clipX1;
				}
				int endX = x + halfWidth;
				if (endX > this.clipX2 - 1) {
					endX = this.clipX2 - 1;
				}
				int pixelIndex = startX + currY * this.surfaceWidth;
				for (int currX = startX; currX <= endX; currX++) {
					this.surfaceRaster[pixelIndex++] = colour;
				}
				currY++;
				midpointDistanceSq += dy + dy;
				distanceSq += dy++ + dy;
			}
			return;
		}
		@Pc(277) int premultipliedColour = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * alpha >> 8 & 0xFF00) + (alpha << 24);
		int inverseAlpha = 256 - alpha;
		@Pc(334) int currX;
		@Pc(340) int dest;
		while (currY < y) {
			while (midpointDistanceSq <= squareRadius || distanceSq <= squareRadius) {
				distanceSq += halfWidth + halfWidth;
				midpointDistanceSq += halfWidth++ + halfWidth;
			}
			int startX = x + 1 - halfWidth;
			if (startX < this.clipX1) {
				startX = this.clipX1;
			}
			int endX = x + halfWidth;
			if (endX > this.clipX2) {
				endX = this.clipX2;
			}
			int pixelIndex = startX + currY * this.surfaceWidth;
			for (currX = startX; currX < endX; currX++) {
				dest = this.surfaceRaster[pixelIndex];
				@Pc(360) int faded = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dest & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
				this.surfaceRaster[pixelIndex++] = premultipliedColour + faded;
			}
			currY++;
			distanceSq -= dy-- + dy;
			midpointDistanceSq -= dy + dy;
		}
		halfWidth = radius;
		dy = -dy;
		midpointDistanceSq = dy * dy + squareRadius;
		distanceSq = midpointDistanceSq - radius;
		midpointDistanceSq -= dy;
		while (currY < y2) {
			while (midpointDistanceSq > squareRadius && distanceSq > squareRadius) {
				midpointDistanceSq -= halfWidth-- + halfWidth;
				distanceSq -= halfWidth + halfWidth;
			}
			int startX = x - halfWidth;
			if (startX < this.clipX1) {
				startX = this.clipX1;
			}
			int endX = x + halfWidth;
			if (endX > this.clipX2 - 1) {
				endX = this.clipX2 - 1;
			}
			int pixelIndex = startX + currY * this.surfaceWidth;
			for (currX = startX; currX <= endX; currX++) {
				dest = this.surfaceRaster[pixelIndex];
				dest = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dest & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
				this.surfaceRaster[pixelIndex++] = premultipliedColour + dest;
			}
			currY++;
			midpointDistanceSq += dy + dy;
			distanceSq += dy++ + dy;
		}
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(Ljava/lang/Runnable;)Lclient!pga;")
	public JavaThreadResource threadResource(@OriginalArg(0) Runnable thread) {
		for (@Pc(1) int i = 0; i < this.threadCount; i++) {
			if (this.resources[i].thread == thread) {
				return this.resources[i];
			}
		}
		return null;
	}

	@OriginalMember(owner = "client!ao", name = "E", descriptor = "()I")
	@Override
	public int E() {
		return 0;
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(II)V")
	@Override
	public void flip(@OriginalArg(0) int x, @OriginalArg(1) int y) throws FlipException {
		if (this.canvas == null || this.canvasSurface == null) {
			throw new IllegalStateException("off");
		}
		try {
			@Pc(14) Graphics graphics = this.canvas.getGraphics();
			this.canvasSurface.clip(0, x, this.height, graphics, 0, this.width, y);
		} catch (@Pc(29) Exception ex) {
			this.canvas.repaint();
		}
	}

	@OriginalMember(owner = "client!ao", name = "ZA", descriptor = "(IFFFFF)V")
	@Override
	public void ZA(@OriginalArg(0) int colour, @OriginalArg(1) float sunIntensity, @OriginalArg(2) float antiSunIntensity, @OriginalArg(3) float x, @OriginalArg(4) float y, @OriginalArg(5) float z) {
		this.sunIntensity = (int) (sunIntensity * 65535.0F);
		this.reverseSunIntensity = (int) (antiSunIntensity * 65535.0F);
		@Pc(26) float length = (float) Math.sqrt((double) (x * x + y * y + z * z));
		this.sunX = (int) (x * 65535.0F / length);
		this.sunY = (int) (y * 65535.0F / length);
		this.sunZ = (int) (z * 65535.0F / length);
	}

	@OriginalMember(owner = "client!ao", name = "h", descriptor = "(I)V")
	@Override
	public void setTextureSize(@OriginalArg(0) int size) {
		this.textureSize = size;
		this.textureCache.reset();
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(Ljava/awt/Canvas;II)V")
	@Override
	public void resizeCanvas(@OriginalArg(0) Canvas canvas2, @OriginalArg(1) int width, @OriginalArg(2) int height) {
		@Pc(8) JavaSurface surface = (JavaSurface) this.surfaces.get((long) canvas2.hashCode());
		if (surface == null) {
			return;
		}
		surface.unlink();
		surface = JavaSurfaceFactory.createSurface(height, canvas2, width);
		this.surfaces.put((long) canvas2.hashCode(), surface);
		if (this.canvas != canvas2 || this.offscreenSurface != null) {
			return;
		}
		@Pc(37) Dimension dimension = canvas2.getSize();
		this.width = dimension.width;
		this.height = dimension.height;
		this.canvasSurface = surface;
		this.surfaceRaster = surface.raster;
		this.surfaceWidth = surface.width;
		this.surfaceHeight = surface.height;
		if (this.surfaceWidth != this.depthWidth || this.surfaceHeight != this.depthHeight) {
			this.mainSurfaceWidth = this.depthWidth = this.surfaceWidth;
			this.mainSurfaceHeight = this.depthHeight = this.surfaceHeight;
			this.mainDepthBuffer = this.depthBuffer = new float[this.depthWidth * this.depthHeight];
		}
		this.reset();
	}

	@OriginalMember(owner = "client!ao", name = "r", descriptor = "(IIIIIII)I")
	@Override
	public int r(@OriginalArg(0) int x1, @OriginalArg(1) int y1, @OriginalArg(2) int z1, @OriginalArg(3) int x2, @OriginalArg(4) int y2, @OriginalArg(5) int z2, @OriginalArg(6) int zoom) {
		@Pc(24) float depth1 = this.camera.e3_1 * (float) x1 + this.camera.e3_2 * (float) y1 + this.camera.e3_3 * (float) z1 + this.camera.tz;
		@Pc(49) float depth2 = this.camera.e3_1 * (float) x2 + this.camera.e3_2 * (float) y2 + this.camera.e3_3 * (float) z2 + this.camera.tz;
		@Pc(51) int outcode = 0;
		if (depth1 < (float) this.zNear && depth2 < (float) this.zNear) {
			outcode |= 0x10;
		} else if (depth1 > (float) this.zFar && depth2 > (float) this.zFar) {
			outcode |= 0x20;
		}
		@Pc(117) int screenX1 = (int) ((float) this.projectionScaleX * (this.camera.e1_1 * (float) x1 + this.camera.e1_2 * (float) y1 + this.camera.e1_3 * (float) z1 + this.camera.tx) / (float) zoom);
		@Pc(150) int screenX2 = (int) ((float) this.projectionScaleX * (this.camera.e1_1 * (float) x2 + this.camera.e1_2 * (float) y2 + this.camera.e1_3 * (float) z2 + this.camera.tx) / (float) zoom);
		if (screenX1 < this.viewX1 && screenX2 < this.viewX1) {
			outcode |= 0x1;
		} else if (screenX1 > this.viewX2 && screenX2 > this.viewX2) {
			outcode |= 0x2;
		}
		@Pc(208) int screenY1 = (int) ((float) this.projectionScaleY * (this.camera.e2_1 * (float) x1 + this.camera.e2_2 * (float) y1 + this.camera.e2_3 * (float) z1 + this.camera.ty) / (float) zoom);
		@Pc(241) int screenY2 = (int) ((float) this.projectionScaleY * (this.camera.e2_1 * (float) x2 + this.camera.e2_2 * (float) y2 + this.camera.e2_3 * (float) z2 + this.camera.ty) / (float) zoom);
		if (screenY1 < this.viewY1 && screenY2 < this.viewY1) {
			outcode |= 0x4;
		} else if (screenY1 > this.viewY2 && screenY2 > this.viewY2) {
			outcode |= 0x8;
		}
		return outcode;
	}

	@OriginalMember(owner = "client!ao", name = "m", descriptor = "(I)Z")
	public boolean textureAvailable(@OriginalArg(0) int id) {
		return super.materialProvider.textureAvailable(id);
	}

	@OriginalMember(owner = "client!ao", name = "z", descriptor = "()Z")
	@Override
	public boolean method7311() {
		return true;
	}

	@OriginalMember(owner = "client!ao", name = "K", descriptor = "([I)V")
	@Override
	public void K(@OriginalArg(0) int[] clip) {
		clip[0] = this.clipX1;
		clip[1] = this.clipY1;
		clip[2] = this.clipX2;
		clip[3] = this.clipY2;
	}

	@OriginalMember(owner = "client!ao", name = "i", descriptor = "(I)Lclient!za;")
	@Override
	public MemoryPool createHeap(@OriginalArg(0) int size) {
		return null;
	}

	@OriginalMember(owner = "client!ao", name = "d", descriptor = "(II)Lclient!he;")
	@Override
	public Sprite method7268(@OriginalArg(0) int width, @OriginalArg(1) int height) {
		return this.generateEmptySprite(width, height, false);
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "(IIIIIIIII)V")
	@Override
	public void drawLine(@OriginalArg(0) int startX, @OriginalArg(1) int startY, @OriginalArg(2) int endX, @OriginalArg(3) int endY, @OriginalArg(4) int colour, @OriginalArg(6) int dashLength, @OriginalArg(7) int gapLength, @OriginalArg(8) int dashOffset) {
		endX -= startX;
		endY -= startY;
		@Pc(28) int dashCycle;
		@Pc(32) int rawDashOffset;
		if (endY == 0) {
			if (endX >= 0) {
				this.horizontalDashedLine(startX, startY, endX + 1, colour, dashLength, gapLength, dashOffset);
			} else {
				dashCycle = dashLength + gapLength;
				rawDashOffset = dashOffset % dashCycle;
				rawDashOffset = dashCycle + dashLength - rawDashOffset - (-endX + 1) % dashCycle;
				dashOffset = rawDashOffset % dashCycle;
				if (dashOffset < 0) {
					dashOffset += dashCycle;
				}
				this.horizontalDashedLine(startX + endX, startY, 1 - endX, colour, dashLength, gapLength, dashOffset);
			}
		} else if (endX != 0) {
			rawDashOffset = dashOffset << 8;
			@Pc(143) int scaledDash = dashLength << 8;
			@Pc(147) int scaledGap = gapLength << 8;
			dashCycle = scaledDash + scaledGap;
			dashOffset = rawDashOffset % dashCycle;
			@Pc(172) int local172;
			@Pc(176) int local176;
			if (endX + endY < 0) {
				int lineLength = (int) (Math.sqrt((double) (endX * endX + endY * endY)) * 256.0D);
				int lengthRemainder = lineLength % dashCycle;
				rawDashOffset = dashCycle + scaledDash - dashOffset - lengthRemainder;
				dashOffset = rawDashOffset % dashCycle;
				if (dashOffset < 0) {
					dashOffset += dashCycle;
				}
				startX += endX;
				endX = -endX;
				startY += endY;
				endY = -endY;
			}
			@Pc(254) int phaseStep;
			@Pc(267) int local267;
			@Pc(349) int local349;
			@Pc(375) int index;
			@Pc(380) int dest;
			@Pc(237) int local237;
			@Pc(223) int local223;
			@Pc(340) int premultipliedColour;
			if (endX > endY) {
				startY <<= 0x10;
				startY += 32768;
				int fixedDeltaY = endY << 16;
				int slope = (int) Math.floor((double) fixedDeltaY / (double) endX + 0.5D);
				int lastX = endX + startX;
				int alpha = colour >>> 24;
				phaseStep = (int) Math.sqrt((double) ((slope >> 8) * (slope >> 8) + 65536));
				if (alpha == 255) {
					while (startX <= lastX) {
						int pixelY = startY >> 16;
						if (startX >= this.clipX1 && startX < this.clipX2 && pixelY >= this.clipY1 && pixelY < this.clipY2 && dashOffset < scaledDash) {
							this.surfaceRaster[startX + pixelY * this.surfaceWidth] = colour;
						}
						startY += slope;
						startX++;
						rawDashOffset = dashOffset + phaseStep;
						dashOffset = rawDashOffset % dashCycle;
					}
				} else {
					premultipliedColour = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * alpha >> 8 & 0xFF00) + (alpha << 24);
					int inverseAlpha = 256 - alpha;
					while (startX <= lastX) {
						int pixelY = startY >> 16;
						if (startX >= this.clipX1 && startX < this.clipX2 && pixelY >= this.clipY1 && pixelY < this.clipY2 && dashOffset < scaledDash) {
							index = startX + pixelY * this.surfaceWidth;
							dest = this.surfaceRaster[index];
							dest = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dest & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
							this.surfaceRaster[index] = premultipliedColour + dest;
						}
						startY += slope;
						startX++;
						rawDashOffset = dashOffset + phaseStep;
						dashOffset = rawDashOffset % dashCycle;
					}
				}
			} else {
				startX <<= 0x10;
				startX += 32768;
				int fixedDeltaX = endX << 16;
				int slope = (int) Math.floor((double) fixedDeltaX / (double) endY + 0.5D);
				int lastY = endY + startY;
				int alpha = colour >>> 24;
				phaseStep = (int) Math.sqrt((double) ((slope >> 8) * (slope >> 8) + 65536));
				if (alpha == 255) {
					while (startY <= lastY) {
						int pixelX = startX >> 16;
						if (startY >= this.clipY1 && startY < this.clipY2 && pixelX >= this.clipX1 && pixelX < this.clipX2 && dashOffset < scaledDash) {
							this.surfaceRaster[pixelX + startY * this.surfaceWidth] = colour;
						}
						startX += slope;
						startY++;
						rawDashOffset = dashOffset + phaseStep;
						dashOffset = rawDashOffset % dashCycle;
					}
				} else {
					premultipliedColour = ((colour & 0xFF00FF) * alpha >> 8 & 0xFF00FF) + ((colour & 0xFF00) * alpha >> 8 & 0xFF00) + (alpha << 24);
					int inverseAlpha = 256 - alpha;
					while (startY <= lastY) {
						int pixelX = startX >> 16;
						if (startY >= this.clipY1 && startY < this.clipY2 && pixelX >= this.clipX1 && pixelX < this.clipX2 && dashOffset < scaledDash) {
							index = pixelX + startY * this.surfaceWidth;
							dest = this.surfaceRaster[index];
							@Pc(708) int faded = ((dest & 0xFF00FF) * inverseAlpha >> 8 & 0xFF00FF) + ((dest & 0xFF00) * inverseAlpha >> 8 & 0xFF00);
							this.surfaceRaster[pixelX + startY * this.surfaceWidth] = premultipliedColour + faded;
						}
						startX += slope;
						startY++;
						rawDashOffset = dashOffset + phaseStep;
						dashOffset = rawDashOffset % dashCycle;
					}
				}
			}
		} else if (endY >= 0) {
			this.verticalDashedLine(startX, startY, endY + 1, colour, dashLength, gapLength, dashOffset);
		} else {
			dashCycle = dashLength + gapLength;
			rawDashOffset = dashOffset % dashCycle;
			rawDashOffset = dashCycle + dashLength - rawDashOffset - (-endY + 1) % dashCycle;
			dashOffset = rawDashOffset % dashCycle;
			if (dashOffset < 0) {
				dashOffset += dashCycle;
			}
			this.verticalDashedLine(startX, startY + endY, -endY + 1, colour, dashLength, gapLength, dashOffset);
		}
	}

	@OriginalMember(owner = "client!ao", name = "X", descriptor = "(I)V")
	@Override
	public void X(@OriginalArg(0) int shadowSize) {
	}

	@OriginalMember(owner = "client!ao", name = "c", descriptor = "()Lclient!fia;")
	@Override
	public Renderer renderer() {
		return new Renderer(0, "Pure Java", 1, "CPU", 0L);
	}

	@OriginalMember(owner = "client!ao", name = "e", descriptor = "(I)V")
	@Override
	public void linkThreads(@OriginalArg(0) int index) {
		this.resources[index].bindThread(Thread.currentThread());
	}

	@OriginalMember(owner = "client!ao", name = "a", descriptor = "([Ljava/awt/Rectangle;III)V")
	@Override
	public void flipDirtyRect(@OriginalArg(0) Rectangle[] rectangles, @OriginalArg(1) int count, @OriginalArg(2) int x, @OriginalArg(3) int y) throws FlipException {
		if (this.canvas == null || this.canvasSurface == null) {
			throw new IllegalStateException("off");
		}
		try {
			@Pc(14) Graphics graphics = this.canvas.getGraphics();
			for (@Pc(16) int i = 0; i < count; i++) {
				@Pc(21) Rectangle rect = rectangles[i];
				if (rect.x + x <= this.surfaceWidth && rect.y + y <= this.surfaceHeight && rect.x + x + rect.width > 0 && rect.y + y + rect.height > 0) {
					this.canvasSurface.clip(rect.y, rect.x + x, rect.height, graphics, rect.x, rect.width, rect.y + y);
				}
			}
		} catch (@Pc(79) Exception exception) {
			this.canvas.repaint();
		}
	}

	@OriginalMember(owner = "client!ao", name = "M", descriptor = "()I")
	@Override
	public int M() {
		@Pc(2) int modelCount = this.modelCount;
		this.modelCount = 0;
		return modelCount;
	}

	@OriginalMember(owner = "client!ao", name = "n", descriptor = "(I)I")
	public int textureAlphaBlendMode(@OriginalArg(0) int id) {
		return super.materialProvider.getMetrics(id).effectCombiner;
	}

	@OriginalMember(owner = "client!ao", name = "I", descriptor = "()I")
	@Override
	public int I() {
		@Pc(2) int count = this.count;
		this.count = 0;
		return count;
	}

	@OriginalMember(owner = "client!ao", name = "x", descriptor = "()I")
	@Override
	public int getMaxLights() {
		return 0;
	}

	@OriginalMember(owner = "client!ao", name = "b", descriptor = "(I)V")
	@Override
	public void tick(@OriginalArg(0) int time) {
		@Pc(4) int elapsed = time - this.lastTickTime;
		for (@Pc(9) Object object = this.textureCache.first(); object != null; object = this.textureCache.next()) {
			@Pc(13) SafeModeTexture2D texture = (SafeModeTexture2D) object;
			if (texture.isAnimated) {
				texture.cycle += elapsed;
				@Pc(27) int frames = texture.cycle / 20;
				if (frames > 0) {
					@Pc(36) MaterialType metrics = super.materialProvider.getMetrics(texture.materialId);
					texture.animate(metrics.speedU * elapsed * 50 / 1000, metrics.speedV * elapsed * 50 / 1000);
					texture.cycle -= frames * 20;
				}
				texture.isAnimated = false;
			}
		}
		this.lastTickTime = time;
		this.spriteCache.clean(5);
		this.textureCache.clean(5);
	}

	@OriginalMember(owner = "client!ao", name = "t", descriptor = "()Z")
	@Override
	public boolean replaceCanvasOnDisplayUpdate() {
		return true;
	}

	@OriginalMember(owner = "client!ao", name = "da", descriptor = "(III[I)V")
	@Override
	public void da(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] screenCoords) {
		@Pc(24) float depth = this.camera.tz + this.camera.e3_1 * (float) x + this.camera.e3_2 * (float) y + this.camera.e3_3 * (float) z;
		if (depth < (float) this.zNear || depth > (float) this.zFar) {
			screenCoords[0] = screenCoords[1] = screenCoords[2] = -1;
			return;
		}
		@Pc(81) int screenX = (int) ((float) this.projectionScaleX * (this.camera.tx + this.camera.e1_1 * (float) x + this.camera.e1_2 * (float) y + this.camera.e1_3 * (float) z) / depth);
		@Pc(113) int screenY = (int) ((float) this.projectionScaleY * (this.camera.ty + this.camera.e2_1 * (float) x + this.camera.e2_2 * (float) y + this.camera.e2_3 * (float) z) / depth);
		if (screenX >= this.viewX1 && screenX <= this.viewX2 && screenY >= this.viewY1 && screenY <= this.viewY2) {
			screenCoords[0] = screenX - this.viewX1;
			screenCoords[1] = screenY - this.viewY1;
			screenCoords[2] = (int) depth;
		} else {
			screenCoords[0] = screenCoords[1] = screenCoords[2] = -1;
		}
	}

	@OriginalMember(owner = "client!ao", name = "q", descriptor = "()V")
	@Override
	public void renderLastFrame() {
	}

	@OriginalMember(owner = "client!ao", name = "f", descriptor = "()Z")
	@Override
	public boolean hasTextures() {
		return false;
	}

	@OriginalMember(owner = "client!ao", name = "l", descriptor = "()V")
	@Override
	public void cacheReset() {
		this.textureCache.reset();
		this.spriteCache.reset();
	}

	@OriginalMember(owner = "client!ao", name = "g", descriptor = "(II)I")
	@Override
	public int combineFunctionMasks(@OriginalArg(0) int maskA, @OriginalArg(1) int maskB) {
		return maskA | maskB;
	}

	@OriginalMember(owner = "client!ao", name = "o", descriptor = "(I)Z")
	public boolean smallTexture(@OriginalArg(0) int id) {
		return this.shrinkTextures || super.materialProvider.getMetrics(id).isSmall;
	}
}
