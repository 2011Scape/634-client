import com.jagex.game.runetek5.JavaMatrix;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.model.ModelSM;

@OriginalClass("client!pga")
public final class JavaThreadResource {

	@OriginalMember(owner = "client!pga", name = "h", descriptor = "Ljava/lang/Runnable;")
	public Runnable thread;

	@OriginalMember(owner = "client!pga", name = "k", descriptor = "Z")
	public boolean fogActive;

	@OriginalMember(owner = "client!pga", name = "y", descriptor = "I")
	public int rasterMinY;

	@OriginalMember(owner = "client!pga", name = "E", descriptor = "I")
	public int rasterWidth;

	@OriginalMember(owner = "client!pga", name = "M", descriptor = "I")
	public int rasterMinX;

	@OriginalMember(owner = "client!pga", name = "e", descriptor = "Z")
	public boolean water = false;

	@OriginalMember(owner = "client!pga", name = "b", descriptor = "I")
	public int fogColour = 0;

	@OriginalMember(owner = "client!pga", name = "g", descriptor = "I")
	public int waterDepth = 0;

	@OriginalMember(owner = "client!pga", name = "o", descriptor = "I")
	public int savedFogColour = 0;

	@OriginalMember(owner = "client!pga", name = "l", descriptor = "Z")
	public boolean zWrite = true;

	@OriginalMember(owner = "client!pga", name = "u", descriptor = "I")
	public int waterHeight = 0;

	@OriginalMember(owner = "client!pga", name = "s", descriptor = "Lclient!gq;")
	public final JavaMatrix scratchMatrix = new JavaMatrix();

	@OriginalMember(owner = "client!pga", name = "w", descriptor = "[I")
	public final int[] vertexScreenZ = new int[ModelSM.maxModelVertices];

	@OriginalMember(owner = "client!pga", name = "x", descriptor = "[I")
	public final int[] cameraY = new int[ModelSM.maxModelVertices];

	@OriginalMember(owner = "client!pga", name = "v", descriptor = "[I")
	public final int[] screenX = new int[64];

	@OriginalMember(owner = "client!pga", name = "J", descriptor = "[F")
	public final float[] texCoordScratch = new float[2];

	@OriginalMember(owner = "client!pga", name = "z", descriptor = "[I")
	public final int[] otherMergeStamps = new int[10000];

	@OriginalMember(owner = "client!pga", name = "F", descriptor = "[I")
	public final int[] worldY = new int[ModelSM.maxModelVertices];

	@OriginalMember(owner = "client!pga", name = "P", descriptor = "[I")
	public final int[] fogLevels = new int[64];

	@OriginalMember(owner = "client!pga", name = "B", descriptor = "[I")
	public final int[] boundsCornerX = new int[8];

	@OriginalMember(owner = "client!pga", name = "V", descriptor = "[I")
	public final int[] cameraZ = new int[ModelSM.maxModelVertices];

	@OriginalMember(owner = "client!pga", name = "U", descriptor = "[I")
	public final int[] mergeStamps = new int[10000];

	@OriginalMember(owner = "client!pga", name = "N", descriptor = "[I")
	public final int[] vertexScreenY = new int[ModelSM.maxModelVertices];

	@OriginalMember(owner = "client!pga", name = "Y", descriptor = "[I")
	public final int[] clippedColour = new int[10];

	@OriginalMember(owner = "client!pga", name = "L", descriptor = "[I")
	public final int[] screenY = new int[64];

	@OriginalMember(owner = "client!pga", name = "W", descriptor = "[I")
	public final int[] clippedX = new int[10];

	@OriginalMember(owner = "client!pga", name = "Z", descriptor = "[I")
	public final int[] boundsCornerY = new int[8];

	@OriginalMember(owner = "client!pga", name = "S", descriptor = "[I")
	public final int[] clippedZ = new int[10];

	@OriginalMember(owner = "client!pga", name = "db", descriptor = "[I")
	public final int[] boundsCornerZ = new int[8];

	@OriginalMember(owner = "client!pga", name = "A", descriptor = "[I")
	public final int[] depths = new int[64];

	@OriginalMember(owner = "client!pga", name = "cb", descriptor = "[I")
	public final int[] cameraX = new int[ModelSM.maxModelVertices];

	@OriginalMember(owner = "client!pga", name = "fb", descriptor = "[I")
	public final int[] clippedY = new int[10];

	@OriginalMember(owner = "client!pga", name = "R", descriptor = "[I")
	public final int[] vertexScreenX = new int[ModelSM.maxModelVertices];

	@OriginalMember(owner = "client!pga", name = "a", descriptor = "Lclient!ao;")
	private final JavaToolkit toolkit;

	@OriginalMember(owner = "client!pga", name = "t", descriptor = "I")
	public int fogPlane;

	@OriginalMember(owner = "client!pga", name = "D", descriptor = "Lclient!mp;")
	public Rasterizer rasterizer;

	@OriginalMember(owner = "client!pga", name = "bb", descriptor = "Lclient!jga;")
	public final JavaModel copyDstModel1;

	@OriginalMember(owner = "client!pga", name = "H", descriptor = "Lclient!jga;")
	public final JavaModel copyDstModel2;

	@OriginalMember(owner = "client!pga", name = "C", descriptor = "Lclient!jga;")
	public final JavaModel copyDstModel3;

	@OriginalMember(owner = "client!pga", name = "X", descriptor = "Lclient!jga;")
	public final JavaModel copyDstModel4;

	@OriginalMember(owner = "client!pga", name = "Q", descriptor = "Lclient!jga;")
	public final JavaModel copyDstModel5;

	@OriginalMember(owner = "client!pga", name = "O", descriptor = "Lclient!jga;")
	public final JavaModel copySrcModel1;

	@OriginalMember(owner = "client!pga", name = "G", descriptor = "Lclient!jga;")
	public final JavaModel copySrcModel2;

	@OriginalMember(owner = "client!pga", name = "K", descriptor = "Lclient!jga;")
	public final JavaModel copySrcModel3;

	@OriginalMember(owner = "client!pga", name = "eb", descriptor = "Lclient!jga;")
	public final JavaModel copySrcModel4;

	@OriginalMember(owner = "client!pga", name = "I", descriptor = "Lclient!jga;")
	public final JavaModel copySrcModel5;

	@OriginalMember(owner = "client!pga", name = "ab", descriptor = "[I")
	public final int[] faceBillboard;

	@OriginalMember(owner = "client!pga", name = "<init>", descriptor = "(Lclient!ao;)V")
	public JavaThreadResource(@OriginalArg(0) JavaToolkit javaToolkit) {
		this.toolkit = javaToolkit;
		this.fogPlane = this.toolkit.zFar - 255;
		this.rasterizer = new Rasterizer(javaToolkit, this);
		this.copyDstModel1 = new JavaModel(this.toolkit);
		this.copyDstModel2 = new JavaModel(this.toolkit);
		this.copyDstModel3 = new JavaModel(this.toolkit);
		this.copyDstModel4 = new JavaModel(this.toolkit);
		this.copyDstModel5 = new JavaModel(this.toolkit);
		this.copySrcModel1 = new JavaModel(this.toolkit);
		this.copySrcModel2 = new JavaModel(this.toolkit);
		this.copySrcModel3 = new JavaModel(this.toolkit);
		this.copySrcModel4 = new JavaModel(this.toolkit);
		this.copySrcModel5 = new JavaModel(this.toolkit);
		this.faceBillboard = new int[ModelSM.maxModelTriangles];
		for (@Pc(203) int i = 0; i < ModelSM.maxModelTriangles; i++) {
			this.faceBillboard[i] = -1;
		}
	}

	@OriginalMember(owner = "client!pga", name = "a", descriptor = "(ILjava/lang/Runnable;)V")
	public void bindThread(@OriginalArg(1) Runnable runnable) {
		this.thread = runnable;
	}

	@OriginalMember(owner = "client!pga", name = "a", descriptor = "(I)V")
	public void resetRasterizer() {
		this.rasterizer = new Rasterizer(this.toolkit, this);
	}
}
