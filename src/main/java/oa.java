import com.jagex.game.runetek5.DepthBuffer;
import com.jagex.game.runetek5.EnvironmentSampler;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.cache.node.JavaSurface;
import com.jagex.game.runetek5.cache.node.SsePointLight;
import com.jagex.game.runetek5.ParticleList;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
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
import com.jagex.game.runetek5.graphics.sw.SoftwareMemoryManager;
import com.jagex.game.runetek5.graphics.sw.SoftwareObject;
import com.jagex.game.runetek5.statics.NativeModelScratch;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.lang.reflect.Method;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.utils.ColorUtil;
import com.jagex.game.runetek5.NativeLibraryOwner;

@OriginalClass("client!oa")
public final class oa extends Toolkit implements SoftwareObject {

	@OriginalMember(owner = "client!oa", name = "x", descriptor = "Lclient!ya;")
	private ya aYa2;

	@OriginalMember(owner = "client!oa", name = "B", descriptor = "Lclient!bca;")
	private Matrix matrix;

	@OriginalMember(owner = "client!oa", name = "N", descriptor = "Lclient!p;")
	private p aP1;

	@OriginalMember(owner = "client!oa", name = "R", descriptor = "I")
	public int anInt7473;

	@OriginalMember(owner = "client!oa", name = "T", descriptor = "[Lclient!a;")
	private a[] anAArray1;

	@OriginalMember(owner = "client!oa", name = "nativeid", descriptor = "J")
	public final long nativeid = 0L;

	@OriginalMember(owner = "client!oa", name = "A", descriptor = "Z")
	private boolean aBoolean541 = false;

	@OriginalMember(owner = "client!oa", name = "z", descriptor = "Lclient!uh;")
	private final Deque aClass341_44 = new Deque();

	@OriginalMember(owner = "client!oa", name = "D", descriptor = "I")
	private int anInt7471 = 4096;

	@OriginalMember(owner = "client!oa", name = "E", descriptor = "I")
	private int anInt7472 = 4096;

	@OriginalMember(owner = "client!oa", name = "C", descriptor = "Lclient!eq;")
	private final IterableHashTable aClass99_52 = new IterableHashTable(4);

	@OriginalMember(owner = "client!oa", name = "S", descriptor = "Z")
	private boolean aBoolean542 = false;

	@OriginalMember(owner = "client!oa", name = "H", descriptor = "Lclient!bca;")
	private Matrix aClass30_11;

	@OriginalMember(owner = "client!oa", name = "<init>", descriptor = "(Ljava/awt/Canvas;Lclient!d;II)V")
	public oa(@OriginalArg(0) Canvas canvas, @OriginalArg(1) MaterialProvider textureSource, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3) {
		super(textureSource);
		try {
			if (!NativeLibraryOwner.loadLibrary("sw3d")) {
				throw new RuntimeException("");
			}
			SoftwareMemoryManager.instanceCreated();
			this.MA(super.materialProvider, 0, 0);
			ColorUtil.initColorTables(false, true);
			this.aBoolean542 = true;
			this.aClass30_11 = new ja();
			this.setCamera(new ja());
			this.allocateThreads(1);
			this.linkThreads(0);
			if (canvas != null) {
				this.addCanvas(canvas, arg2, arg3);
				this.setCanvas(canvas);
			}
		} catch (@Pc(80) Throwable ex) {
			this.free();
			throw new RuntimeException();
		}
	}

	@OriginalMember(owner = "client!oa", name = "MA", descriptor = "(Lclient!d;II)V")
	private native void MA(@OriginalArg(0) MaterialProvider arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2);

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIIIII)V")
	@Override
	public void outlineRect(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5) {
		this.U(arg0, arg1, arg2, arg4, arg5);
		this.U(arg0, arg1 + arg3 - 1, arg2, arg4, arg5);
		this.P(arg0, arg1 + 1, arg3 - 1, arg4, arg5);
		this.P(arg0 + arg2 - 1, arg1 + 1, arg3 - 1, arg4, arg5);
	}

	@OriginalMember(owner = "client!oa", name = "ma", descriptor = "(J)V")
	private native void ma(@OriginalArg(0) long arg0);

	@OriginalMember(owner = "client!oa", name = "d", descriptor = "(IIIIII)Lclient!wh;")
	@Override
	public EnvironmentSampler createCubeTexture(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5) {
		return null;
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!za;)V")
	@Override
	public void setHeap(@OriginalArg(0) MemoryPool memoryPool) {
		this.aYa2 = (ya) memoryPool;
		this.va(memoryPool);
	}

	@OriginalMember(owner = "client!oa", name = "w", descriptor = "(Z)V")
	public native void w(@OriginalArg(0) boolean arg0);

	@OriginalMember(owner = "client!oa", name = "A", descriptor = "(ILclient!aa;II)V")
	public native void A(@OriginalArg(0) int arg0, @OriginalArg(1) ComponentSurface arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3);

	@OriginalMember(owner = "client!oa", name = "aa", descriptor = "(IIIIII)V")
	public native void aa(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5);

	@OriginalMember(owner = "client!oa", name = "v", descriptor = "()V")
	@Override
	public void compositeBloomEffects() {
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "([Ljava/awt/Rectangle;III)V")
	@Override
	public void flipDirtyRect(@OriginalArg(0) Rectangle[] rectangles, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3) throws FlipException {
		if (this.aP1 == null) {
			throw new IllegalStateException("off");
		}
		if (ClientFeatures.scaled()) {
			// Not part of the original client: the whole scaled frame is drawn; see flip.
			this.flip(arg2, arg3);
			return;
		}
		this.aP1.method6665(rectangles, arg1, arg2, arg3);
	}

	@OriginalMember(owner = "client!oa", name = "z", descriptor = "()Z")
	@Override
	public boolean method7311() {
		return true;
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!mda;I)V")
	@Override
	public void renderOrtho(@OriginalArg(0) ParticleList particleList, @OriginalArg(1) int arg1) {
		this.method6407(particleList);
		this.method6405().method14(this, NativeModelScratch.particlePositions, NativeModelScratch.particleColours, NativeModelScratch.particleSizes, NativeModelScratch.particleMaterialIds, particleList.particles.size());
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIIIIIIIIIIII)V")
	@Override
	public void drawTriangle() {
	}

	@OriginalMember(owner = "client!oa", name = "l", descriptor = "()V")
	@Override
	public void cacheReset() {
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!rh;Z)Lclient!mi;")
	@Override
	public NativeSprite generateSprite(@OriginalArg(0) SpriteType indexedImage, @OriginalArg(1) boolean arg1) {
		@Pc(17) j local17 = new j(this, indexedImage.pallete, indexedImage.pixels, indexedImage.alpha, 0, indexedImage.width, indexedImage.width, indexedImage.height);
		local17.setOffsets(indexedImage.minX, indexedImage.minY, indexedImage.borderX, indexedImage.borderY);
		return local17;
	}

	@OriginalMember(owner = "client!oa", name = "HA", descriptor = "(IIII[I)V")
	public native void HA(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int[] arg4);

	@OriginalMember(owner = "client!oa", name = "A", descriptor = "()V")
	@Override
	public void stopBloom() {
	}

	@OriginalMember(owner = "client!oa", name = "AA", descriptor = "(SSIBBIZBBBBZZZZZBZZI)V")
	private native void AA(@OriginalArg(0) short arg0, @OriginalArg(1) short arg1, @OriginalArg(2) int arg2, @OriginalArg(3) byte arg3, @OriginalArg(4) byte arg4, @OriginalArg(5) int arg5, @OriginalArg(6) boolean arg6, @OriginalArg(7) byte arg7, @OriginalArg(8) byte arg8, @OriginalArg(9) byte arg9, @OriginalArg(10) byte arg10, @OriginalArg(11) boolean arg11, @OriginalArg(12) boolean arg12, @OriginalArg(13) boolean arg13, @OriginalArg(14) boolean arg14, @OriginalArg(15) boolean arg15, @OriginalArg(16) byte arg16, @OriginalArg(17) boolean arg17, @OriginalArg(18) boolean arg18, @OriginalArg(19) int arg19);

	@OriginalMember(owner = "client!oa", name = "h", descriptor = "(I)V")
	@Override
	public void setTextureSize(@OriginalArg(0) int arg0) {
		throw new IllegalStateException();
	}

	@OriginalMember(owner = "client!oa", name = "H", descriptor = "(III[I)V")
	public native void H(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int[] arg3);

	@OriginalMember(owner = "client!oa", name = "g", descriptor = "()V")
	private void g() {
		System.gc();
		System.runFinalization();
		SoftwareMemoryManager.freePending();
	}

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "(IIIID)V")
	public native void b(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) double arg4);

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(FFF)V")
	@Override
	public void setBloomParams(@OriginalArg(0) float arg0, @OriginalArg(1) float arg1, @OriginalArg(2) float arg2) {
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!oea;[Lclient!rh;Z)Lclient!da;")
	@Override
	public AbstractFontRenderer createFontRenderer(@OriginalArg(0) FontMetrics fontMetrics, @OriginalArg(1) SpriteType[] indexedImages) {
		@Pc(3) int[] local3 = new int[indexedImages.length];
		@Pc(7) int[] local7 = new int[indexedImages.length];
		@Pc(9) boolean local9 = false;
		for (@Pc(11) int i = 0; i < indexedImages.length; i++) {
			local3[i] = indexedImages[i].width;
			local7[i] = indexedImages[i].height;
			if (indexedImages[i].alpha != null) {
				local9 = true;
			}
		}
		if (local9) {
			throw new IllegalArgumentException("Cannot specify alpha with non-mono font unless someone writes it");
		}
		return new h(this, this.aYa2, fontMetrics, indexedImages, null);
	}

	@OriginalMember(owner = "client!oa", name = "wa", descriptor = "(IIIIII)V")
	private native void wa(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5);

	@OriginalMember(owner = "client!oa", name = "f", descriptor = "()Z")
	@Override
	public boolean hasTextures() {
		return true;
	}

	@OriginalMember(owner = "client!oa", name = "B", descriptor = "()Lclient!a;")
	public a method6405() {
		for (@Pc(1) int i = 0; i < this.anInt7473; i++) {
			if (this.anAArray1[i].aRunnable1 == Thread.currentThread()) {
				return this.anAArray1[i];
			}
		}
		return null;
	}

	@OriginalMember(owner = "client!oa", name = "t", descriptor = "(Lclient!p;)V")
	private native void t(@OriginalArg(0) p arg0);

	@OriginalMember(owner = "client!oa", name = "y", descriptor = "()Lclient!bca;")
	@Override
	public Matrix scratchMatrix() {
		return this.aClass30_11;
	}

	@OriginalMember(owner = "client!oa", name = "WA", descriptor = "(S)Z")
	private boolean WA(@OriginalArg(0) short arg0) {
		@Pc(2) MaterialProvider textureSource = super.materialProvider;
		synchronized (super.materialProvider) {
			if (!super.materialProvider.textureAvailable(arg0)) {
				return false;
			}
			@Pc(22) MaterialType textureMetrics = super.materialProvider.getMetrics(arg0);
			if (textureMetrics == null) {
				return false;
			}
			@Pc(44) int[] local44;
			if (textureMetrics.effectCombiner == 2) {
				local44 = super.materialProvider.getPixelsArgb(128, 0.7F, arg0, 128);
			} else {
				local44 = super.materialProvider.getPixels(true, arg0, 0.7F, 128, 128);
			}
			this.CA(arg0, local44, textureMetrics.colorHsl, textureMetrics.effectCombiner, textureMetrics.effectType, textureMetrics.effectParam1, textureMetrics.effectParam2, textureMetrics.isSmall, textureMetrics.alpha, textureMetrics.brightness, textureMetrics.speedU, textureMetrics.speedV, textureMetrics.detailsOnly, textureMetrics.skipTriangles, textureMetrics.unusedFlag, textureMetrics.repeatS, textureMetrics.repeatT, textureMetrics.mipmapping, textureMetrics.hdr, textureMetrics.transposed, textureMetrics.combineMode);
			return true;
		}
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!mda;Z)V")
	private void method6407(@OriginalArg(0) ParticleList particleList) {
		@Pc(1) int local1 = 0;
		@Pc(3) int local3 = 0;
		@Pc(5) int local5 = 0;
		@Pc(7) int local7 = 0;
		for (@Pc(15) PointEntity particle = (PointEntity) particleList.particles.first(); particle != null; particle = (PointEntity) particleList.particles.next()) {
			NativeModelScratch.particlePositions[local1++] = particle.x;
			NativeModelScratch.particlePositions[local1++] = particle.y;
			NativeModelScratch.particlePositions[local1++] = particle.z;
			NativeModelScratch.particleColours[local3++] = particle.colorArgb;
			NativeModelScratch.particleMaterialIds[local7++] = (short) particle.materialId;
			NativeModelScratch.particleSizes[local5++] = particle.size;
		}
	}

	@OriginalMember(owner = "client!oa", name = "e", descriptor = "(I)V")
	@Override
	public void linkThreads(@OriginalArg(0) int arg0) {
		this.anAArray1[arg0].method13();
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "([I)V")
	@Override
	public void accumulateViewportDimensions(@OriginalArg(0) int[] arg0) {
		@Pc(4) Dimension dimension = this.aP1.canvas.getSize();
		arg0[0] = dimension.width;
		arg0[1] = dimension.height;
	}

	@OriginalMember(owner = "client!oa", name = "CA", descriptor = "(S[ISIBBIZBBBBZZZZZBZZI)V")
	private native void CA(@OriginalArg(0) short arg0, @OriginalArg(1) int[] arg1, @OriginalArg(2) short arg2, @OriginalArg(3) int arg3, @OriginalArg(4) byte arg4, @OriginalArg(5) byte arg5, @OriginalArg(6) int arg6, @OriginalArg(7) boolean arg7, @OriginalArg(8) byte arg8, @OriginalArg(9) byte arg9, @OriginalArg(10) byte arg10, @OriginalArg(11) byte arg11, @OriginalArg(12) boolean arg12, @OriginalArg(13) boolean arg13, @OriginalArg(14) boolean arg14, @OriginalArg(15) boolean arg15, @OriginalArg(16) boolean arg16, @OriginalArg(17) byte arg17, @OriginalArg(18) boolean arg18, @OriginalArg(19) boolean arg19, @OriginalArg(20) int arg20);

	@OriginalMember(owner = "client!oa", name = "DA", descriptor = "(IIII)V")
	public native void DA(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3);

	@OriginalMember(owner = "client!oa", name = "i", descriptor = "(I)Lclient!za;")
	@Override
	public MemoryPool createHeap(@OriginalArg(0) int arg0) {
		@Pc(5) ya local5 = new ya(this, arg0);
		this.aClass341_44.addLast(local5);
		return local5;
	}

	@OriginalMember(owner = "client!oa", name = "c", descriptor = "(I)V")
	@Override
	public void initialize() {
		this.anInt7472 = this.anInt7471 = 10000;
		if (this.anInt7473 > 1) {
			throw new IllegalStateException("No MT");
		}
		this.allocateThreads(this.anInt7473);
		this.linkThreads(0);
	}

	@OriginalMember(owner = "client!oa", name = "e", descriptor = "(II)I")
	@Override
	public int compareFunctionMasks(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1) {
		@Pc(3) int local3 = arg0 & 0xFFFFF;
		@Pc(7) int local7 = arg1 & 0xFFFFF;
		return local3 & local7 ^ local7;
	}

	@OriginalMember(owner = "client!oa", name = "xa", descriptor = "(F)V")
	public native void xa(@OriginalArg(0) float arg0);

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "(Ljava/awt/Canvas;)V")
	@Override
	public void releaseSurface(@OriginalArg(0) Canvas canvas) {
		if (this.aP1.canvas == canvas) {
			this.setCanvas(null);
		}
		@Pc(16) p local16 = (p) this.aClass99_52.get((long) canvas.hashCode());
		if (local16 != null) {
			local16.unlink();
			local16.method6667();
		}
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIIIIILclient!aa;II)V")
	@Override
	public void drawSurfaceLine(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(6) ComponentSurface clippingMask, @OriginalArg(7) int arg6, @OriginalArg(8) int arg7) {
		this.Z(arg0, arg1, arg2, arg3, arg4, 1, clippingMask, arg6, arg7);
	}

	@OriginalMember(owner = "client!oa", name = "finalize", descriptor = "()V")
	@Override
	public synchronized void finalize() {
		this.free();
		if (this.nativeid != 0L) {
			SoftwareMemoryManager.free(this);
		}
	}

	@OriginalMember(owner = "client!oa", name = "d", descriptor = "(I)V")
	private native void d(@OriginalArg(0) int arg0);

	@OriginalMember(owner = "client!oa", name = "i", descriptor = "()I")
	public native int i();

	@OriginalMember(owner = "client!oa", name = "n", descriptor = "(JJ)V")
	private native void n(@OriginalArg(0) long arg0, @OriginalArg(1) long arg1);

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!mda;)V")
	@Override
	public void render(@OriginalArg(0) ParticleList particleList) {
		if (particleList.particles.size() != 0) {
			this.method6407(particleList);
			this.method6405().method14(this, NativeModelScratch.particlePositions, NativeModelScratch.particleColours, NativeModelScratch.particleSizes, NativeModelScratch.particleMaterialIds, particleList.particles.size());
		}
	}

	@OriginalMember(owner = "client!oa", name = "x", descriptor = "()I")
	@Override
	public int getMaxLights() {
		return 4;
	}

	@OriginalMember(owner = "client!oa", name = "JA", descriptor = "(IIIIII)I")
	public native int JA(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5);

	@OriginalMember(owner = "client!oa", name = "h", descriptor = "()V")
	@Override
	public void stop() {
		if (this.aBoolean541) {
			return;
		}
		this.anAArray1 = null;
		this.aP1 = null;
		this.aYa2 = null;
		this.aClass30_11 = null;
		this.aClass99_52.clear();
		for (@Pc(25) ya local25 = (ya) this.aClass341_44.first(); local25 != null; local25 = (ya) this.aClass341_44.next()) {
			local25.ga();
		}
		this.aClass341_44.clear();
		this.FA();
		if (this.aBoolean542) {
			ColorUtil.releaseColorTables(true, false);
			this.aBoolean542 = false;
		}
		this.g();
		SoftwareMemoryManager.instanceDestroyed();
		this.aBoolean541 = true;
	}

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "(Ljava/awt/Canvas;II)V")
	@Override
	public void resizeCanvas(@OriginalArg(0) Canvas canvas, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2) {
		@Pc(8) p local8 = (p) this.aClass99_52.get((long) canvas.hashCode());
		local8.method6666(canvas, arg1, arg2);
		if (canvas != null && canvas == this.aP1.canvas) {
			this.setCanvas(canvas);
		}
	}

	@OriginalMember(owner = "client!oa", name = "c", descriptor = "(S)Z")
	private boolean c(@OriginalArg(0) short arg0) {
		synchronized (this) {
			@Pc(9) MaterialType textureMetrics = super.materialProvider.getMetrics(arg0);
			if (textureMetrics == null) {
				return false;
			} else {
				this.AA(arg0, textureMetrics.colorHsl, textureMetrics.effectCombiner, textureMetrics.effectType, textureMetrics.effectParam1, textureMetrics.effectParam2, textureMetrics.isSmall, textureMetrics.alpha, textureMetrics.brightness, textureMetrics.speedU, textureMetrics.speedV, textureMetrics.detailsOnly, textureMetrics.skipTriangles, textureMetrics.unusedFlag, textureMetrics.repeatS, textureMetrics.repeatT, textureMetrics.mipmapping, textureMetrics.hdr, textureMetrics.transposed, textureMetrics.combineMode);
				return true;
			}
		}
	}

	@OriginalMember(owner = "client!oa", name = "m", descriptor = "()Z")
	@Override
	public boolean bloom() {
		return false;
	}

	@OriginalMember(owner = "client!oa", name = "Q", descriptor = "(IIIIII[BII)V")
	public native void Q(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5, @OriginalArg(6) byte[] arg6, @OriginalArg(7) int arg7, @OriginalArg(8) int arg8);

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIZ)Lclient!mi;")
	@Override
	public NativeSprite generateEmptySprite(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) boolean arg2) {
		return new j(this, arg0, arg1);
	}

	@OriginalMember(owner = "client!oa", name = "ya", descriptor = "()V")
	public native void ya();

	@OriginalMember(owner = "client!oa", name = "I", descriptor = "()I")
	public native int I();

	@OriginalMember(owner = "client!oa", name = "f", descriptor = "(II)V")
	public native void f(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1);

	@OriginalMember(owner = "client!oa", name = "KA", descriptor = "(IIII)V")
	public native void KA(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3);

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "(I)V")
	@Override
	public void tick(@OriginalArg(0) int arg0) {
		SoftwareMemoryManager.freePending();
		this.d(arg0);
		for (@Pc(10) ya local10 = (ya) this.aClass341_44.first(); local10 != null; local10 = (ya) this.aClass341_44.next()) {
			local10.r();
		}
	}

	@OriginalMember(owner = "client!oa", name = "g", descriptor = "(II)I")
	@Override
	public int combineFunctionMasks(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1) {
		return arg0 | arg1;
	}

	@OriginalMember(owner = "client!oa", name = "ra", descriptor = "(IIII)V")
	public native void ra(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3);

	@OriginalMember(owner = "client!oa", name = "E", descriptor = "()I")
	public native int E();

	@OriginalMember(owner = "client!oa", name = "pa", descriptor = "()V")
	public native void pa();

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIIIIII)V")
	@Override
	public void strongLine(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5) {
		this.method6405().method9(this, arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@OriginalMember(owner = "client!oa", name = "GA", descriptor = "(I)V")
	public native void GA(@OriginalArg(0) int arg0);

	@OriginalMember(owner = "client!oa", name = "n", descriptor = "()Z")
	@Override
	public boolean extendBuildArea() {
		return true;
	}

	@OriginalMember(owner = "client!oa", name = "X", descriptor = "(I)V")
	public native void X(@OriginalArg(0) int arg0);

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!bh;IIII)Lclient!ka;")
	@Override
	public Model createModel(@OriginalArg(0) Mesh mesh, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4) {
		return new i(this, this.aYa2, mesh, arg1, arg2, arg3, arg4);
	}

	@OriginalMember(owner = "client!oa", name = "c", descriptor = "()Lclient!fia;")
	@Override
	public Renderer renderer() {
		return new Renderer(0, "SSE", 1, "CPU", 0L);
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIIIIIIII)V")
	@Override
	public void drawLine(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(6) int arg5, @OriginalArg(7) int arg6, @OriginalArg(8) int arg7) {
	}

	@OriginalMember(owner = "client!oa", name = "F", descriptor = "(II)V")
	public native void F(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1);

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "(IIIIZ)Lclient!mi;")
	@Override
	public NativeSprite createNativeSprite(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) boolean arg4) {
		return new j(this, arg0, arg1, arg2, arg3, !arg4);
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(I)V")
	@Override
	public void allocateThreads(@OriginalArg(0) int arg0) {
		this.anInt7473 = arg0;
		this.anAArray1 = new a[this.anInt7473];
		for (@Pc(9) int i = 0; i < this.anInt7473; i++) {
			this.anAArray1[i] = new a(this, this.anInt7472, this.anInt7471);
		}
	}

	@OriginalMember(owner = "client!oa", name = "r", descriptor = "(IIIIIII)I")
	public native int r(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5, @OriginalArg(6) int arg6);

	@OriginalMember(owner = "client!oa", name = "va", descriptor = "(Lclient!za;)V")
	private native void va(@OriginalArg(0) MemoryPool arg0);

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Ljava/awt/Canvas;II)V")
	@Override
	public void addCanvas(@OriginalArg(0) Canvas canvas, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2) {
		@Pc(8) p local8 = (p) this.aClass99_52.get((long) canvas.hashCode());
		if (local8 == null) {
			try {
				@Pc(13) Class class2 = Class.forName("java.awt.Canvas");
				@Pc(23) Method method = class2.getMethod("setIgnoreRepaint", Boolean.TYPE);
				method.invoke(canvas, Boolean.TRUE);
			} catch (@Pc(35) Exception ex) {
			}
			local8 = new p(this, canvas, arg1, arg2);
			this.aClass99_52.put((long) canvas.hashCode(), local8);
		} else if (local8.anInt7778 != arg1 || local8.anInt7779 != arg2) {
			local8.method6666(canvas, arg1, arg2);
		}
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!wh;Lclient!wh;FLclient!wh;)Lclient!wh;")
	@Override
	public EnvironmentSampler drawQuad(@OriginalArg(0) EnvironmentSampler environmentSampler, @OriginalArg(1) EnvironmentSampler environmentSampler2, @OriginalArg(2) float arg2, @OriginalArg(3) EnvironmentSampler environmentSampler3) {
		return null;
	}

	@OriginalMember(owner = "client!oa", name = "K", descriptor = "([I)V")
	public native void K(@OriginalArg(0) int[] arg0);

	@OriginalMember(owner = "client!oa", name = "t", descriptor = "()Z")
	@Override
	public boolean replaceCanvasOnDisplayUpdate() {
		return true;
	}

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "(II)V")
	@Override
	public void flip(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1) throws FlipException {
		if (this.aP1 == null) {
			throw new IllegalStateException("off");
		}
		if (ClientFeatures.scaled()) {
			// Not part of the original client: with client.scale the frame is read back from the
			// native renderer and drawn through the canvas's scaled graphics, since the native
			// copy to the window cannot scale.
			this.flipScaled(arg0, arg1);
			return;
		}
		this.aP1.method6664(arg0, arg1);
	}

	// Not part of the original client: the Java image the frame is read back into with client.scale.
	private JavaSurface scaledSurface;

	private void flipScaled(int x, int y) {
		int width = this.aP1.anInt7778;
		int height = this.aP1.anInt7779;
		if (width <= 0 || height <= 0) {
			return;
		}
		if (this.scaledSurface == null || this.scaledSurface.width != width || this.scaledSurface.height != height) {
			this.scaledSurface = JavaSurfaceFactory.createSurface(height, this.aP1.canvas, width);
		}
		int[] pixels = this.na(0, 0, width, height);
		if (pixels == null || pixels.length < width * height) {
			return;
		}
		System.arraycopy(pixels, 0, this.scaledSurface.raster, 0, width * height);
		try {
			Graphics graphics = this.aP1.canvas.getGraphics();
			this.scaledSurface.clip(0, x, height, graphics, 0, width, y);
			graphics.dispose();
		} catch (Exception ex) {
			this.aP1.canvas.repaint();
		}
	}

	@OriginalMember(owner = "client!oa", name = "d", descriptor = "(II)Lclient!he;")
	@Override
	public Sprite method7268(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1) {
		return this.generateEmptySprite(arg0, arg1, false);
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(I[Lclient!ab;)V")
	@Override
	public void copyLights(@OriginalArg(0) int arg0, @OriginalArg(1) PointLight[] pointLights) {
		@Pc(1) int local1 = 0;
		for (@Pc(3) int i = 0; i < arg0; i++) {
			NativeModelScratch.pointLightData[local1++] = pointLights[i].getX();
			NativeModelScratch.pointLightData[local1++] = pointLights[i].getY();
			NativeModelScratch.pointLightData[local1++] = pointLights[i].getZ();
			NativeModelScratch.pointLightData[local1++] = pointLights[i].getRadius();
			NativeModelScratch.pointLightIntensities[i] = pointLights[i].getIntensity();
			NativeModelScratch.pointLightData[local1++] = pointLights[i].getColour();
		}
		this.N(arg0, NativeModelScratch.pointLightData, NativeModelScratch.pointLightIntensities);
	}

	@OriginalMember(owner = "client!oa", name = "za", descriptor = "(IIIII)V")
	public native void za(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4);

	@OriginalMember(owner = "client!oa", name = "na", descriptor = "(IIII)[I")
	public native int[] na(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3);

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "(Z)V")
	@Override
	public void method7250(@OriginalArg(0) boolean arg0) {
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIIIIF)Lclient!ab;")
	@Override
	public PointLight createPointLight(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int range, @OriginalArg(5) float intensity) {
		return new SsePointLight(arg0, arg1, arg2, arg3, range, intensity);
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Ljava/awt/Canvas;)V")
	@Override
	public void setCanvas(@OriginalArg(0) Canvas canvas) {
		if (canvas == null) {
			this.aP1 = null;
			this.t(null);
		} else {
			@Pc(10) p local10 = (p) this.aClass99_52.get((long) canvas.hashCode());
			this.aP1 = local10;
			this.t(local10);
		}
	}

	@OriginalMember(owner = "client!oa", name = "r", descriptor = "()Z")
	@Override
	public boolean hardShadow() {
		return true;
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!wh;)V")
	@Override
	public void setSunCubeTexture(@OriginalArg(0) EnvironmentSampler environmentSampler) {
	}

	@OriginalMember(owner = "client!oa", name = "j", descriptor = "()Lclient!bca;")
	@Override
	public Matrix createMatrix() {
		return new ja();
	}

	@OriginalMember(owner = "client!oa", name = "f", descriptor = "(I)V")
	@Override
	public void method7281(@OriginalArg(0) int arg0) {
		this.anAArray1[arg0].method15();
	}

	@OriginalMember(owner = "client!oa", name = "EA", descriptor = "(IIII)V")
	public native void EA(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3);

	@OriginalMember(owner = "client!oa", name = "b", descriptor = "()V")
	@Override
	public void finish() {
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIII)V")
	@Override
	public void method7261(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3) {
	}

	@OriginalMember(owner = "client!oa", name = "FA", descriptor = "()V")
	private native void FA();

	@OriginalMember(owner = "client!oa", name = "P", descriptor = "(IIIII)V")
	public native void P(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4);

	@OriginalMember(owner = "client!oa", name = "u", descriptor = "()Z")
	@Override
	public boolean forceFixedScreenmode() {
		return true;
	}

	@OriginalMember(owner = "client!oa", name = "U", descriptor = "(IIIII)V")
	public native void U(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4);

	@OriginalMember(owner = "client!oa", name = "Y", descriptor = "()[I")
	public native int[] Y();

	@OriginalMember(owner = "client!oa", name = "ZA", descriptor = "(IFFFFF)V")
	public native void ZA(@OriginalArg(0) int arg0, @OriginalArg(1) float arg1, @OriginalArg(2) float arg2, @OriginalArg(3) float arg3, @OriginalArg(4) float arg4, @OriginalArg(5) float arg5);

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!he;Lclient!vm;)Lclient!is;")
	@Override
	public OffscreenSurface createOffscreenSurface(@OriginalArg(0) Sprite surface, @OriginalArg(1) DepthBuffer depthBuffer) {
		return new wa(this, (j) surface, (xa) depthBuffer);
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!is;)V")
	@Override
	public void swapSurface(@OriginalArg(0) OffscreenSurface offscreenSurface) {
		@Pc(2) wa local2 = (wa) offscreenSurface;
		this.n(local2.aJ1.nativeid, local2.aXa1.nativeid);
	}

	@OriginalMember(owner = "client!oa", name = "da", descriptor = "(III[I)V")
	public native void da(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int[] arg3);

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(II[I[I)Lclient!aa;")
	@Override
	public ComponentSurface createMask(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int[] arg2, @OriginalArg(3) int[] arg3) {
		return new na(this, this.aYa2, arg0, arg1, arg2, arg3);
	}

	@OriginalMember(owner = "client!oa", name = "e", descriptor = "()Lclient!bca;")
	@Override
	public Matrix camera() {
		return this.matrix;
	}

	@OriginalMember(owner = "client!oa", name = "e", descriptor = "(IIIIII)V")
	@Override
	public void line(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5) {
		this.wa(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	@OriginalMember(owner = "client!oa", name = "Z", descriptor = "(IIIIIILclient!aa;II)V")
	private native void Z(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(5) int arg5, @OriginalArg(6) ComponentSurface arg6, @OriginalArg(7) int arg7, @OriginalArg(8) int arg8);

	@OriginalMember(owner = "client!oa", name = "C", descriptor = "(Z)V")
	public native void C(@OriginalArg(0) boolean arg0);

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(II)Lclient!vm;")
	@Override
	public DepthBuffer createDepthBuffer(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1) {
		return new xa(arg0, arg1);
	}

	@OriginalMember(owner = "client!oa", name = "d", descriptor = "()Z")
	@Override
	public boolean stillwaterEffectLoaded() {
		return true;
	}

	@OriginalMember(owner = "client!oa", name = "L", descriptor = "(III)V")
	public native void L(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2);

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "([IIIIIZ)Lclient!mi;")
	@Override
	public NativeSprite createNativeSprite(@OriginalArg(0) int[] arg0, @OriginalArg(2) int arg1, @OriginalArg(3) int arg2, @OriginalArg(4) int arg3, @OriginalArg(5) boolean arg4) {
		return new j(this, arg0, 0, arg1, arg2, arg3, false);
	}

	@OriginalMember(owner = "client!oa", name = "la", descriptor = "()V")
	public native void la();

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Z)V")
	@Override
	public void setShrinkTextures(@OriginalArg(0) boolean arg0) {
	}

	@OriginalMember(owner = "client!oa", name = "M", descriptor = "()I")
	public native int M();

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "()Z")
	@Override
	public boolean supportsBloom() {
		return false;
	}

	@OriginalMember(owner = "client!oa", name = "OA", descriptor = "()Ljava/lang/Object;")
	private Object OA() {
		return new ba(this);
	}

	@OriginalMember(owner = "client!oa", name = "k", descriptor = "()Z")
	@Override
	public boolean supportsFullscreen() {
		return true;
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(IIIIIILclient!aa;IIIII)V")
	@Override
	public void drawTexturedLine(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3, @OriginalArg(4) int arg4, @OriginalArg(6) ComponentSurface clippingMask, @OriginalArg(7) int arg6, @OriginalArg(8) int arg7, @OriginalArg(9) int arg8, @OriginalArg(10) int arg9, @OriginalArg(11) int arg10) {
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(Lclient!bca;)V")
	@Override
	public void setCamera(@OriginalArg(0) Matrix matrix) {
		this.matrix = matrix;
		this.ma(((ja) matrix).nativeid);
	}

	@OriginalMember(owner = "client!oa", name = "p", descriptor = "()V")
	@Override
	public void restoreSurface() {
		this.setCanvas(this.aP1.canvas);
	}

	@OriginalMember(owner = "client!oa", name = "a", descriptor = "(II[[I[[IIII)Lclient!s;")
	@Override
	public Ground createGround(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int[][] tileHeights, @OriginalArg(3) int[][] arg3, @OriginalArg(5) int arg4, @OriginalArg(6) int arg5) {
		return new t(this, this.aYa2, arg0, arg1, tileHeights, arg3, 512, arg4, arg5);
	}

	@OriginalMember(owner = "client!oa", name = "XA", descriptor = "()I")
	public native int XA();

	@OriginalMember(owner = "client!oa", name = "N", descriptor = "(I[I[F)V")
	private native void N(@OriginalArg(0) int arg0, @OriginalArg(1) int[] arg1, @OriginalArg(2) float[] arg2);

	@OriginalMember(owner = "client!oa", name = "T", descriptor = "(IIII)V")
	public native void T(@OriginalArg(0) int arg0, @OriginalArg(1) int arg1, @OriginalArg(2) int arg2, @OriginalArg(3) int arg3);

	@OriginalMember(owner = "client!oa", name = "s", descriptor = "()Z")
	@Override
	public boolean utilisesNativeHeap() {
		return true;
	}

	@OriginalMember(owner = "client!oa", name = "q", descriptor = "()V")
	@Override
	public void renderLastFrame() {
	}

	@OriginalMember(owner = "client!oa", name = "o", descriptor = "()Z")
	@Override
	public boolean hasBloom() {
		return false;
	}

	@OriginalMember(owner = "client!oa", name = "w", descriptor = "()Z")
	@Override
	public boolean supportsAntiAliasing() {
		return false;
	}
}
