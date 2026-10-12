import com.jagex.game.runetek5.JavaMatrix
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.model.ModelSM
import kotlin.jvm.JvmField
import lang.Runnable

@OriginalClass("client!pga")
class JavaThreadResource @OriginalMember(owner = "client!pga", name = "<init>", descriptor = "(Lclient!ao;)V") constructor(@OriginalArg(0) javaToolkit: JavaToolkit?) {

	@OriginalMember(owner = "client!pga", name = "h", descriptor = "Ljava/lang/Runnable;")
	@JvmField
	var thread: Runnable? = null

	@OriginalMember(owner = "client!pga", name = "k", descriptor = "Z")
	@JvmField
	var fogActive: Boolean = false

	@OriginalMember(owner = "client!pga", name = "y", descriptor = "I")
	@JvmField
	var rasterMinY: Int = 0

	@OriginalMember(owner = "client!pga", name = "E", descriptor = "I")
	@JvmField
	var rasterWidth: Int = 0

	@OriginalMember(owner = "client!pga", name = "M", descriptor = "I")
	@JvmField
	var rasterMinX: Int = 0

	@OriginalMember(owner = "client!pga", name = "e", descriptor = "Z")
	@JvmField
	var water: Boolean = false

	@OriginalMember(owner = "client!pga", name = "b", descriptor = "I")
	@JvmField
	var fogColour: Int = 0

	@OriginalMember(owner = "client!pga", name = "g", descriptor = "I")
	@JvmField
	var waterDepth: Int = 0

	@OriginalMember(owner = "client!pga", name = "o", descriptor = "I")
	@JvmField
	var savedFogColour: Int = 0

	@OriginalMember(owner = "client!pga", name = "l", descriptor = "Z")
	@JvmField
	var zWrite: Boolean = true

	@OriginalMember(owner = "client!pga", name = "u", descriptor = "I")
	@JvmField
	var waterHeight: Int = 0

	@OriginalMember(owner = "client!pga", name = "s", descriptor = "Lclient!gq;")
	@JvmField
	val scratchMatrix: JavaMatrix = JavaMatrix()

	@OriginalMember(owner = "client!pga", name = "w", descriptor = "[I")
	@JvmField
	val vertexScreenZ: IntArray = IntArray(ModelSM.maxModelVertices)

	@OriginalMember(owner = "client!pga", name = "x", descriptor = "[I")
	@JvmField
	val cameraY: IntArray = IntArray(ModelSM.maxModelVertices)

	@OriginalMember(owner = "client!pga", name = "v", descriptor = "[I")
	@JvmField
	val screenX: IntArray = IntArray(64)

	@OriginalMember(owner = "client!pga", name = "J", descriptor = "[F")
	@JvmField
	val texCoordScratch: FloatArray = FloatArray(2)

	@OriginalMember(owner = "client!pga", name = "z", descriptor = "[I")
	@JvmField
	val otherMergeStamps: IntArray = IntArray(10000)

	@OriginalMember(owner = "client!pga", name = "F", descriptor = "[I")
	@JvmField
	val worldY: IntArray = IntArray(ModelSM.maxModelVertices)

	@OriginalMember(owner = "client!pga", name = "P", descriptor = "[I")
	@JvmField
	val fogLevels: IntArray = IntArray(64)

	@OriginalMember(owner = "client!pga", name = "B", descriptor = "[I")
	@JvmField
	val boundsCornerX: IntArray = IntArray(8)

	@OriginalMember(owner = "client!pga", name = "V", descriptor = "[I")
	@JvmField
	val cameraZ: IntArray = IntArray(ModelSM.maxModelVertices)

	@OriginalMember(owner = "client!pga", name = "U", descriptor = "[I")
	@JvmField
	val mergeStamps: IntArray = IntArray(10000)

	@OriginalMember(owner = "client!pga", name = "N", descriptor = "[I")
	@JvmField
	val vertexScreenY: IntArray = IntArray(ModelSM.maxModelVertices)

	@OriginalMember(owner = "client!pga", name = "Y", descriptor = "[I")
	@JvmField
	val clippedColour: IntArray = IntArray(10)

	@OriginalMember(owner = "client!pga", name = "L", descriptor = "[I")
	@JvmField
	val screenY: IntArray = IntArray(64)

	@OriginalMember(owner = "client!pga", name = "W", descriptor = "[I")
	@JvmField
	val clippedX: IntArray = IntArray(10)

	@OriginalMember(owner = "client!pga", name = "Z", descriptor = "[I")
	@JvmField
	val boundsCornerY: IntArray = IntArray(8)

	@OriginalMember(owner = "client!pga", name = "S", descriptor = "[I")
	@JvmField
	val clippedZ: IntArray = IntArray(10)

	@OriginalMember(owner = "client!pga", name = "db", descriptor = "[I")
	@JvmField
	val boundsCornerZ: IntArray = IntArray(8)

	@OriginalMember(owner = "client!pga", name = "A", descriptor = "[I")
	@JvmField
	val depths: IntArray = IntArray(64)

	@OriginalMember(owner = "client!pga", name = "cb", descriptor = "[I")
	@JvmField
	val cameraX: IntArray = IntArray(ModelSM.maxModelVertices)

	@OriginalMember(owner = "client!pga", name = "fb", descriptor = "[I")
	@JvmField
	val clippedY: IntArray = IntArray(10)

	@OriginalMember(owner = "client!pga", name = "R", descriptor = "[I")
	@JvmField
	val vertexScreenX: IntArray = IntArray(ModelSM.maxModelVertices)

	@OriginalMember(owner = "client!pga", name = "a", descriptor = "Lclient!ao;")
	private val toolkit: JavaToolkit

	@OriginalMember(owner = "client!pga", name = "t", descriptor = "I")
	@JvmField
	var fogPlane: Int = 0

	@OriginalMember(owner = "client!pga", name = "D", descriptor = "Lclient!mp;")
	@JvmField
	var rasterizer: Rasterizer? = null

	@OriginalMember(owner = "client!pga", name = "bb", descriptor = "Lclient!jga;")
	@JvmField
	val copyDstModel1: JavaModel

	@OriginalMember(owner = "client!pga", name = "H", descriptor = "Lclient!jga;")
	@JvmField
	val copyDstModel2: JavaModel

	@OriginalMember(owner = "client!pga", name = "C", descriptor = "Lclient!jga;")
	@JvmField
	val copyDstModel3: JavaModel

	@OriginalMember(owner = "client!pga", name = "X", descriptor = "Lclient!jga;")
	@JvmField
	val copyDstModel4: JavaModel

	@OriginalMember(owner = "client!pga", name = "Q", descriptor = "Lclient!jga;")
	@JvmField
	val copyDstModel5: JavaModel

	@OriginalMember(owner = "client!pga", name = "O", descriptor = "Lclient!jga;")
	@JvmField
	val copySrcModel1: JavaModel

	@OriginalMember(owner = "client!pga", name = "G", descriptor = "Lclient!jga;")
	@JvmField
	val copySrcModel2: JavaModel

	@OriginalMember(owner = "client!pga", name = "K", descriptor = "Lclient!jga;")
	@JvmField
	val copySrcModel3: JavaModel

	@OriginalMember(owner = "client!pga", name = "eb", descriptor = "Lclient!jga;")
	@JvmField
	val copySrcModel4: JavaModel

	@OriginalMember(owner = "client!pga", name = "I", descriptor = "Lclient!jga;")
	@JvmField
	val copySrcModel5: JavaModel

	@OriginalMember(owner = "client!pga", name = "ab", descriptor = "[I")
	@JvmField
	val faceBillboard: IntArray

	init {
		this.toolkit = javaToolkit!!
		this.fogPlane = this.toolkit.zFar - 255
		this.rasterizer = Rasterizer(javaToolkit, this)
		this.copyDstModel1 = JavaModel(this.toolkit)
		this.copyDstModel2 = JavaModel(this.toolkit)
		this.copyDstModel3 = JavaModel(this.toolkit)
		this.copyDstModel4 = JavaModel(this.toolkit)
		this.copyDstModel5 = JavaModel(this.toolkit)
		this.copySrcModel1 = JavaModel(this.toolkit)
		this.copySrcModel2 = JavaModel(this.toolkit)
		this.copySrcModel3 = JavaModel(this.toolkit)
		this.copySrcModel4 = JavaModel(this.toolkit)
		this.copySrcModel5 = JavaModel(this.toolkit)
		this.faceBillboard = IntArray(ModelSM.maxModelTriangles)
		@Pc(203) var i: Int = 0
		while (i < ModelSM.maxModelTriangles) {
			this.faceBillboard[i] = -1
			i++
		}
	}

	@OriginalMember(owner = "client!pga", name = "a", descriptor = "(ILjava/lang/Runnable;)V")
	fun bindThread(@OriginalArg(1) runnable: Runnable?) {
		this.thread = runnable
	}

	@OriginalMember(owner = "client!pga", name = "a", descriptor = "(I)V")
	fun resetRasterizer() {
		this.rasterizer = Rasterizer(this.toolkit, this)
	}
}
