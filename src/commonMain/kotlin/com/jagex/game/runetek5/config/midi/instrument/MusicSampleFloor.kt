package com.jagex.game.runetek5.config.midi.instrument

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.cache.node.VorbisSound
import com.jagex.game.runetek5.utils.Utils
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!rm")
class MusicSampleFloor {

	@OriginalMember(owner = "client!rm", name = "g", descriptor = "[I")
	private val partitionClassList: IntArray

	@OriginalMember(owner = "client!rm", name = "b", descriptor = "[I")
	private val classDimensions: IntArray

	@OriginalMember(owner = "client!rm", name = "k", descriptor = "[I")
	private val classSubclasses: IntArray

	@OriginalMember(owner = "client!rm", name = "e", descriptor = "[I")
	private val classMasterbooks: IntArray

	@OriginalMember(owner = "client!rm", name = "l", descriptor = "[[I")
	private val subclassBooks: Array<IntArray?>

	@OriginalMember(owner = "client!rm", name = "d", descriptor = "I")
	private val floor1Multiplier: Int

	@OriginalMember(owner = "client!rm", name = "h", descriptor = "[I")
	private val xList: IntArray

	@OriginalMember(owner = "client!rm", name = "<init>", descriptor = "()V")
	constructor() {
		@Pc(4) val floorType: Int = MusicSample.getBits(16)
		if (floorType != 1) {
			throw RuntimeException()
		}
		@Pc(14) val partitions: Int = MusicSample.getBits(5)
		@Pc(16) var maxClass: Int = 0
		this.partitionClassList = IntArray(partitions)
		@Pc(26) var classIndex: Int
		for (@Pc(22) i in 0 until partitions) {
			classIndex = MusicSample.getBits(4)
			this.partitionClassList[i] = classIndex
			if (classIndex >= maxClass) {
				maxClass = classIndex + 1
			}
		}
		this.classDimensions = IntArray(maxClass)
		this.classSubclasses = IntArray(maxClass)
		this.classMasterbooks = IntArray(maxClass)
		this.subclassBooks = arrayOfNulls<IntArray>(maxClass)
		@Pc(77) val local77: Int
		@Pc(99) val local99: Int
		classIndex = 0
		while (classIndex < maxClass) {
			this.classDimensions[classIndex] = MusicSample.getBits(3) + 1
			var subclassCount: Int = MusicSample.getBits(2)
			this.classSubclasses[classIndex] = subclassCount
			if (subclassCount != 0) {
				this.classMasterbooks[classIndex] = MusicSample.getBits(8)
			}
			subclassCount = 0x1 shl subclassCount
			@Pc(92) val books: IntArray = IntArray(subclassCount)
			this.subclassBooks[classIndex] = books
			for (bookIndex in 0 until subclassCount) {
				books[bookIndex] = MusicSample.getBits(8) - 1
			}
			classIndex++
		}
		this.floor1Multiplier = MusicSample.getBits(2) + 1
		val rangeBits: Int = MusicSample.getBits(4)
		@Pc(126) var values: Int = 2
		for (partitionIndex in 0 until partitions) {
			values += this.classDimensions[this.partitionClassList[partitionIndex]]
		}
		this.xList = IntArray(values)
		this.xList[0] = 0
		this.xList[1] = 0x1 shl rangeBits
		values = 2
		for (@Pc(163) j in 0 until partitions) {
			@Pc(169) val partitionClass: Int = this.partitionClassList[j]
			for (@Pc(171) k in 0 until this.classDimensions[partitionClass]) {
				this.xList[values++] = MusicSample.getBits(rangeBits)
			}
		}
		if (MusicSampleFloor.finalX == null || MusicSampleFloor.finalX!!.size < values) {
			MusicSampleFloor.finalX = IntArray(values)
			MusicSampleFloor.finalY = IntArray(values)
			MusicSampleFloor.step2Flag = BooleanArray(values)
		}
	}

	@OriginalMember(owner = "client!rm", name = "a", descriptor = "([FI)V")
	fun computeFloor(@OriginalArg(0) target: FloatArray?, @OriginalArg(1) halfBlockSize: Int) {
		@Pc(3) val length: Int = this.xList.size
		@Pc(10) val range: Int = MusicSampleFloor.RANGE_LIST[this.floor1Multiplier - 1]
		MusicSampleFloor.step2Flag!![1] = true
		MusicSampleFloor.step2Flag!![0] = true
		@Pc(25) val local25: Int
		@Pc(29) val local29: Int
		@Pc(47) val local47: Int
		@Pc(51) val local51: Int
		@Pc(55) val local55: Int
		for (@Pc(20) targetIndex in 2 until length) {
			val lowIndex: Int = MusicSampleFloor.lowNeighbor(MusicSampleFloor.finalX, targetIndex)
			val highIndex: Int = MusicSampleFloor.highNeighbor(MusicSampleFloor.finalX, targetIndex)
			val predicted: Int = this.predictPoint(MusicSampleFloor.finalX!![lowIndex], MusicSampleFloor.finalY!![lowIndex], MusicSampleFloor.finalX!![highIndex], MusicSampleFloor.finalY!![highIndex], MusicSampleFloor.finalX!![targetIndex])
			val encodedY: Int = MusicSampleFloor.finalY!![targetIndex]
			val highRoom: Int = range - predicted
			@Pc(66) val room: Int = (if (highRoom < predicted) highRoom else predicted) shl 1
			if (encodedY == 0) {
				MusicSampleFloor.step2Flag!![targetIndex] = false
				MusicSampleFloor.finalY!![targetIndex] = predicted
			} else {
				MusicSampleFloor.step2Flag!![highIndex] = true
				MusicSampleFloor.step2Flag!![lowIndex] = true
				MusicSampleFloor.step2Flag!![targetIndex] = true
				if (encodedY >= room) {
					MusicSampleFloor.finalY!![targetIndex] = if (highRoom > predicted) encodedY + predicted - predicted else predicted - encodedY + highRoom - 1
				} else {
					MusicSampleFloor.finalY!![targetIndex] = if ((encodedY and 0x1) == 0) predicted + encodedY / 2 else predicted - (encodedY + 1) / 2
				}
			}
		}
		this.sortByX(0, length - 1)
		var prevX: Int = 0
		var prevY: Int = MusicSampleFloor.finalY!![0] * this.floor1Multiplier
		for (pointIndex in 1 until length) {
			if (MusicSampleFloor.step2Flag!![pointIndex]) {
				val currentX: Int = MusicSampleFloor.finalX!![pointIndex]
				val currentY: Int = MusicSampleFloor.finalY!![pointIndex] * this.floor1Multiplier
				this.drawLine(prevX, prevY, currentX, currentY, target, halfBlockSize)
				if (currentX >= halfBlockSize) {
					return
				}
				prevX = currentX
				prevY = currentY
			}
		}
		@Pc(193) val scale: Float = MusicSampleFloor.INVERSE_DB_TABLE[prevY]
		for (sampleIndex in prevX until halfBlockSize) {
			target!![sampleIndex] *= scale
		}
	}

	@OriginalMember(owner = "client!rm", name = "a", descriptor = "(IIII[FI)V")
	private fun drawLine(@OriginalArg(0) lowX: Int, @OriginalArg(1) highX: Int, @OriginalArg(2) lowY: Int, @OriginalArg(3) highY: Int, @OriginalArg(4) output: FloatArray?, @OriginalArg(5) limit: Int) {
		var lowY: Int = lowY
		@Pc(3) val dy: Int = highY - highX
		@Pc(7) val adx: Int = lowY - lowX
		@Pc(14) val ady: Int = if (dy < 0) -dy else dy
		@Pc(18) val base: Int = dy / adx
		@Pc(20) var y: Int = highX
		@Pc(22) var err: Int = 0
		@Pc(32) val sy: Int = if (dy < 0) base - 1 else base + 1
		@Pc(43) val ady2: Int = ady - (if (base < 0) -base else base) * adx
		output!![lowX] *= MusicSampleFloor.INVERSE_DB_TABLE[highX]
		if (lowY > limit) {
			lowY = limit
		}
		for (@Pc(61) x in lowX + 1 until lowY) {
			err += ady2
			if (err >= adx) {
				err -= adx
				y += sy
			} else {
				y += base
			}
			output[x] *= MusicSampleFloor.INVERSE_DB_TABLE[y]
		}
	}

	@OriginalMember(owner = "client!rm", name = "a", descriptor = "(IIIII)I")
	private fun predictPoint(@OriginalArg(0) x0: Int, @OriginalArg(1) y0: Int, @OriginalArg(2) x1: Int, @OriginalArg(3) y1: Int, @OriginalArg(4) targetX: Int): Int {
		@Pc(3) val deltaY: Int = y1 - y0
		@Pc(7) val deltaX: Int = x1 - x0
		@Pc(14) val absDeltaY: Int = if (deltaY < 0) -deltaY else deltaY
		@Pc(20) val err: Int = absDeltaY * (targetX - x0)
		@Pc(24) val off: Int = err / deltaX
		return if (deltaY < 0) y0 - off else y0 + off
	}

	@OriginalMember(owner = "client!rm", name = "a", descriptor = "()Z")
	fun decode(): Boolean {
		@Pc(5) val nonzero: Boolean = VorbisSound.getBit() != 0
		if (!nonzero) {
			return false
		}
		@Pc(13) val values: Int = this.xList.size
		for (@Pc(15) i in 0 until values) {
			MusicSampleFloor.finalX!![i] = this.xList[i]
		}
		@Pc(34) val range: Int = MusicSampleFloor.RANGE_LIST[this.floor1Multiplier - 1]
		@Pc(40) val rangeBits: Int = Utils.bitLength(range - 1)
		MusicSampleFloor.finalY!![0] = MusicSample.getBits(rangeBits)
		MusicSampleFloor.finalY!![1] = MusicSample.getBits(rangeBits)
		@Pc(52) var off: Int = 2
		for (@Pc(54) k in 0 until this.partitionClassList.size) {
			@Pc(60) val partitionClass: Int = this.partitionClassList[k]
			@Pc(65) val partitionDimensions: Int = this.classDimensions[partitionClass]
			@Pc(70) val partitionSubclass: Int = this.classSubclasses[partitionClass]
			@Pc(76) val mask: Int = (0x1 shl partitionSubclass) - 1
			@Pc(78) var codebook: Int = 0
			if (partitionSubclass > 0) {
				codebook = MusicSample.codebooks!![this.classMasterbooks[partitionClass]]!!.decode()
			}
			for (@Pc(90) j in 0 until partitionDimensions) {
				@Pc(100) val book: Int = this.subclassBooks[partitionClass]!![codebook and mask]
				codebook = codebook ushr partitionSubclass
				MusicSampleFloor.finalY!![off++] = if (book >= 0) MusicSample.codebooks!![book]!!.decode() else 0
			}
		}
		return true
	}

	@OriginalMember(owner = "client!rm", name = "a", descriptor = "(II)V")
	private fun sortByX(@OriginalArg(0) low: Int, @OriginalArg(1) high: Int) {
		if (low >= high) {
			return
		}
		@Pc(5) var pivot: Int = low
		@Pc(9) val pivotX: Int = MusicSampleFloor.finalX!![low]
		@Pc(13) val pivotY: Int = MusicSampleFloor.finalY!![low]
		@Pc(17) val pivotStep: Boolean = MusicSampleFloor.step2Flag!![low]
		for (@Pc(21) i in low + 1..high) {
			@Pc(26) val xValue: Int = MusicSampleFloor.finalX!![i]
			if (xValue < pivotX) {
				MusicSampleFloor.finalX!![pivot] = xValue
				MusicSampleFloor.finalY!![pivot] = MusicSampleFloor.finalY!![i]
				MusicSampleFloor.step2Flag!![pivot] = MusicSampleFloor.step2Flag!![i]
				pivot++
				MusicSampleFloor.finalX!![i] = MusicSampleFloor.finalX!![pivot]
				MusicSampleFloor.finalY!![i] = MusicSampleFloor.finalY!![pivot]
				MusicSampleFloor.step2Flag!![i] = MusicSampleFloor.step2Flag!![pivot]
			}
		}
		MusicSampleFloor.finalX!![pivot] = pivotX
		MusicSampleFloor.finalY!![pivot] = pivotY
		MusicSampleFloor.step2Flag!![pivot] = pivotStep
		this.sortByX(low, pivot - 1)
		this.sortByX(pivot + 1, high)
	}

	companion object {

		@OriginalMember(owner = "client!rm", name = "a", descriptor = "[F")
		@JvmField
		val INVERSE_DB_TABLE: FloatArray = floatArrayOf(1.0649863E-7f, 1.1341951E-7f, 1.2079015E-7f, 1.2863978E-7f, 1.369995E-7f, 1.459025E-7f, 1.5538409E-7f, 1.6548181E-7f, 1.7623574E-7f, 1.8768856E-7f, 1.998856E-7f, 2.128753E-7f, 2.2670913E-7f, 2.4144197E-7f, 2.5713223E-7f, 2.7384212E-7f, 2.9163792E-7f, 3.1059022E-7f, 3.307741E-7f, 3.5226967E-7f, 3.7516213E-7f, 3.995423E-7f, 4.255068E-7f, 4.5315863E-7f, 4.8260745E-7f, 5.1397E-7f, 5.4737063E-7f, 5.829419E-7f, 6.208247E-7f, 6.611694E-7f, 7.041359E-7f, 7.4989464E-7f, 7.98627E-7f, 8.505263E-7f, 9.057983E-7f, 9.646621E-7f, 1.0273513E-6f, 1.0941144E-6f, 1.1652161E-6f, 1.2409384E-6f, 1.3215816E-6f, 1.4074654E-6f, 1.4989305E-6f, 1.5963394E-6f, 1.7000785E-6f, 1.8105592E-6f, 1.9282195E-6f, 2.053526E-6f, 2.1869757E-6f, 2.3290977E-6f, 2.4804558E-6f, 2.6416496E-6f, 2.813319E-6f, 2.9961443E-6f, 3.1908505E-6f, 3.39821E-6f, 3.619045E-6f, 3.8542307E-6f, 4.1047006E-6f, 4.371447E-6f, 4.6555283E-6f, 4.958071E-6f, 5.280274E-6f, 5.623416E-6f, 5.988857E-6f, 6.3780467E-6f, 6.7925284E-6f, 7.2339453E-6f, 7.704048E-6f, 8.2047E-6f, 8.737888E-6f, 9.305725E-6f, 9.910464E-6f, 1.0554501E-5f, 1.1240392E-5f, 1.1970856E-5f, 1.2748789E-5f, 1.3577278E-5f, 1.4459606E-5f, 1.5399271E-5f, 1.6400005E-5f, 1.7465769E-5f, 1.8600793E-5f, 1.9809577E-5f, 2.1096914E-5f, 2.2467912E-5f, 2.3928002E-5f, 2.5482977E-5f, 2.7139005E-5f, 2.890265E-5f, 3.078091E-5f, 3.2781227E-5f, 3.4911533E-5f, 3.718028E-5f, 3.9596467E-5f, 4.2169668E-5f, 4.491009E-5f, 4.7828602E-5f, 5.0936775E-5f, 5.424693E-5f, 5.7772202E-5f, 6.152657E-5f, 6.552491E-5f, 6.9783084E-5f, 7.4317984E-5f, 7.914758E-5f, 8.429104E-5f, 8.976875E-5f, 9.560242E-5f, 1.0181521E-4f, 1.0843174E-4f, 1.1547824E-4f, 1.2298267E-4f, 1.3097477E-4f, 1.3948625E-4f, 1.4855085E-4f, 1.5820454E-4f, 1.6848555E-4f, 1.7943469E-4f, 1.9109536E-4f, 2.0351382E-4f, 2.167393E-4f, 2.3082423E-4f, 2.4582449E-4f, 2.6179955E-4f, 2.7881275E-4f, 2.9693157E-4f, 3.1622787E-4f, 3.3677815E-4f, 3.5866388E-4f, 3.8197188E-4f, 4.0679457E-4f, 4.3323037E-4f, 4.613841E-4f, 4.913675E-4f, 5.2329927E-4f, 5.573062E-4f, 5.935231E-4f, 6.320936E-4f, 6.731706E-4f, 7.16917E-4f, 7.635063E-4f, 8.1312325E-4f, 8.6596457E-4f, 9.2223985E-4f, 9.821722E-4f, 0.0010459992f, 0.0011139743f, 0.0011863665f, 0.0012634633f, 0.0013455702f, 0.0014330129f, 0.0015261382f, 0.0016253153f, 0.0017309374f, 0.0018434235f, 0.0019632196f, 0.0020908006f, 0.0022266726f, 0.0023713743f, 0.0025254795f, 0.0026895993f, 0.0028643848f, 0.0030505287f, 0.003248769f, 0.0034598925f, 0.0036847359f, 0.0039241905f, 0.0041792067f, 0.004450795f, 0.004740033f, 0.005048067f, 0.0053761187f, 0.005725489f, 0.0060975635f, 0.0064938175f, 0.0069158226f, 0.0073652514f, 0.007843887f, 0.008353627f, 0.008896492f, 0.009474637f, 0.010090352f, 0.01074608f, 0.011444421f, 0.012188144f, 0.012980198f, 0.013823725f, 0.014722068f, 0.015678791f, 0.016697686f, 0.017782796f, 0.018938422f, 0.020169148f, 0.021479854f, 0.022875736f, 0.02436233f, 0.025945531f, 0.027631618f, 0.029427277f, 0.031339627f, 0.03337625f, 0.035545226f, 0.037855156f, 0.0403152f, 0.042935107f, 0.045725275f, 0.048696756f, 0.05186135f, 0.05523159f, 0.05882085f, 0.062643364f, 0.06671428f, 0.07104975f, 0.075666964f, 0.08058423f, 0.08582105f, 0.09139818f, 0.097337745f, 0.1036633f, 0.11039993f, 0.11757434f, 0.12521498f, 0.13335215f, 0.14201812f, 0.15124726f, 0.16107617f, 0.1715438f, 0.18269168f, 0.19456401f, 0.20720787f, 0.22067343f, 0.23501402f, 0.25028655f, 0.26655158f, 0.28387362f, 0.3023213f, 0.32196787f, 0.34289113f, 0.36517414f, 0.3889052f, 0.41417846f, 0.44109413f, 0.4697589f, 0.50028646f, 0.53279793f, 0.5674221f, 0.6042964f, 0.64356697f, 0.6853896f, 0.72993004f, 0.777365f, 0.8278826f, 0.88168305f, 0.9389798f, 1.0f)

		@OriginalMember(owner = "client!rm", name = "c", descriptor = "[I")
		@JvmField
		var finalX: IntArray? = null

		@OriginalMember(owner = "client!rm", name = "f", descriptor = "[I")
		@JvmField
		val RANGE_LIST: IntArray = intArrayOf(256, 128, 86, 64)

		@OriginalMember(owner = "client!rm", name = "i", descriptor = "[Z")
		@JvmField
		var step2Flag: BooleanArray? = null

		@OriginalMember(owner = "client!rm", name = "j", descriptor = "[I")
		@JvmField
		var finalY: IntArray? = null

		@OriginalMember(owner = "client!rm", name = "a", descriptor = "([II)I")
		@JvmStatic
		fun highNeighbor(@OriginalArg(0) xValues: IntArray?, @OriginalArg(1) targetIndex: Int): Int {
			@Pc(3) val vx: Int = xValues!![targetIndex]
			@Pc(5) var high: Int = -1
			@Pc(7) var value: Int = Int.MAX_VALUE
			for (@Pc(9) i in 0 until targetIndex) {
				@Pc(14) val vi: Int = xValues[i]
				if (vi > vx && vi < value) {
					high = i
					value = vi
				}
			}
			return high
		}

		@OriginalMember(owner = "client!rm", name = "b", descriptor = "([II)I")
		@JvmStatic
		fun lowNeighbor(@OriginalArg(0) xValues: IntArray?, @OriginalArg(1) targetIndex: Int): Int {
			@Pc(3) val vx: Int = xValues!![targetIndex]
			@Pc(5) var low: Int = -1
			@Pc(7) var value: Int = Int.MIN_VALUE
			for (@Pc(9) i in 0 until targetIndex) {
				@Pc(14) val vi: Int = xValues[i]
				if (vi < vx && vi > value) {
					low = i
					value = vi
				}
			}
			return low
		}
	}
}
