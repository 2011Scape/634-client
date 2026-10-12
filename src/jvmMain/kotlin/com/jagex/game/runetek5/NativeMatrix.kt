package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.Matrix
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.preference.GroundDecorOption

@OriginalClass("client!haa")
class NativeMatrix : Matrix {

	@OriginalMember(owner = "client!haa", name = "i", descriptor = "F")
	@JvmField
	var zB: Float = 0.0f

	@OriginalMember(owner = "client!haa", name = "r", descriptor = "F")
	@JvmField
	var xB: Float = 0.0f

	@OriginalMember(owner = "client!haa", name = "v", descriptor = "F")
	@JvmField
	var xC: Float = 0.0f

	@OriginalMember(owner = "client!haa", name = "z", descriptor = "F")
	@JvmField
	var yA: Float = 0.0f

	@OriginalMember(owner = "client!haa", name = "B", descriptor = "F")
	@JvmField
	var yD: Float = 0.0f

	@OriginalMember(owner = "client!haa", name = "C", descriptor = "F")
	@JvmField
	var xA: Float = 0.0f

	@OriginalMember(owner = "client!haa", name = "H", descriptor = "F")
	@JvmField
	var zC: Float = 0.0f

	@OriginalMember(owner = "client!haa", name = "N", descriptor = "F")
	@JvmField
	var yB: Float = 0.0f

	@OriginalMember(owner = "client!haa", name = "P", descriptor = "F")
	@JvmField
	var zD: Float = 0.0f

	@OriginalMember(owner = "client!haa", name = "U", descriptor = "F")
	@JvmField
	var xD: Float = 0.0f

	@OriginalMember(owner = "client!haa", name = "X", descriptor = "F")
	@JvmField
	var yC: Float = 0.0f

	@OriginalMember(owner = "client!haa", name = "bb", descriptor = "F")
	@JvmField
	var zA: Float = 0.0f

	@OriginalMember(owner = "client!haa", name = "<init>", descriptor = "()V")
	constructor() : super() {
		this.makeIdentity()
	}

	@OriginalMember(owner = "client!haa", name = "e", descriptor = "(I)V")
	override fun makeRotationZ(@OriginalArg(0) angle: Int) {
		this.zC = 1.0f
		this.yB = GroundDecorOption.COSINE[angle and 0x3FFF]
		this.xA = this.yB
		this.yA = GroundDecorOption.SINE[angle and 0x3FFF]
		this.xB = -this.yA
		this.zD = 0.0f
		this.zB = this.zD
		this.zA = this.zB
		this.yD = this.zA
		this.yC = this.yD
		this.xD = this.yC
		this.xC = this.xD
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(FFIF)V")
	fun scale(@OriginalArg(0) scaleY: Float, @OriginalArg(1) scaleZ: Float, @OriginalArg(3) scaleX: Float) {
		this.zD *= scaleZ
		this.xD *= scaleX
		this.yC *= scaleY
		this.yA *= scaleY
		this.xC *= scaleX
		this.yB *= scaleY
		this.yD *= scaleY
		this.zC *= scaleZ
		this.zA *= scaleZ
		this.xA *= scaleX
		this.zB *= scaleZ
		this.xB *= scaleX
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(III)V")
	override fun applyTranslation(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int) {
		this.zD = z.toFloat()
		this.zC = 1.0f
		this.yB = this.zC
		this.xA = this.yB
		this.yD = y.toFloat()
		this.xD = x.toFloat()
		this.yC = 0.0f
		this.xC = this.yC
		this.zB = this.xC
		this.xB = this.zB
		this.zA = this.xB
		this.yA = this.zA
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "()V")
	override fun makeIdentity() {
		this.zC = 1.0f
		this.yB = this.zC
		this.xA = this.yB
		this.zD = 0.0f
		this.yD = this.zD
		this.xD = this.yD
		this.yC = this.xD
		this.xC = this.yC
		this.zB = this.xC
		this.xB = this.zB
		this.zA = this.xB
		this.yA = this.zA
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "([FI)[F")
	fun copyXYInto(@OriginalArg(0) dest: FloatArray?): FloatArray? {
		dest!![7] = this.yD
		dest[5] = this.yB
		dest[4] = this.yA
		dest[6] = this.yC
		dest[1] = this.xB
		dest[2] = this.xC
		dest[3] = this.xD
		dest[0] = this.xA
		return dest
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(IIIIII)V")
	override fun createCamera(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) rotateX: Int, @OriginalArg(4) rotateY: Int, @OriginalArg(5) rotateZ: Int) {
		@Pc(9) val cosX: Float = GroundDecorOption.COSINE[rotateX and 0x3FFF]
		@Pc(15) val sinX: Float = GroundDecorOption.SINE[rotateX and 0x3FFF]
		@Pc(21) val cosY: Float = GroundDecorOption.COSINE[rotateY and 0x3FFF]
		@Pc(27) val sinY: Float = GroundDecorOption.SINE[rotateY and 0x3FFF]
		@Pc(33) val cosZ: Float = GroundDecorOption.COSINE[rotateZ and 0x3FFF]
		@Pc(39) val sinZ: Float = GroundDecorOption.SINE[rotateZ and 0x3FFF]
		@Pc(43) val sinXCosZ: Float = cosZ * sinX
		@Pc(47) val sinXSinZ: Float = sinX * sinZ
		this.xB = sinZ * cosX
		this.xC = -sinY * cosZ + cosY * sinXSinZ
		this.zC = cosX * cosY
		this.yC = sinZ * sinY + sinXCosZ * cosY
		this.xA = cosY * cosZ + sinXSinZ * sinY
		this.zA = cosX * sinY
		this.yB = cosZ * cosX
		this.zB = -sinX
		this.yA = sinY * sinXCosZ + sinZ * -cosY
		this.xD = -(this.xC * z.toFloat()) - y.toFloat() * this.xB + this.xA * (-x).toFloat()
		this.yD = (-x).toFloat() * this.yA - this.yB * y.toFloat() - z.toFloat() * this.yC
		this.zD = -(this.zC * z.toFloat()) + this.zA * (-x).toFloat() - this.zB * y.toFloat()
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(I)V")
	override fun rotate(@OriginalArg(0) angle: Int) {
		this.yB = 1.0f
		this.zC = GroundDecorOption.COSINE[angle and 0x3FFF]
		this.xA = this.zC
		this.xC = GroundDecorOption.SINE[angle and 0x3FFF]
		this.zA = -this.xC
		this.zD = 0.0f
		this.zB = this.zD
		this.yD = this.zB
		this.yC = this.yD
		this.yA = this.yC
		this.xD = this.yA
		this.xB = this.xD
	}

	@OriginalMember(owner = "client!haa", name = "c", descriptor = "(I)V")
	override fun makeRotationX(@OriginalArg(0) angle: Int) {
		this.xA = 1.0f
		this.zC = GroundDecorOption.COSINE[angle and 0x3FFF]
		this.yB = this.zC
		this.zB = GroundDecorOption.SINE[angle and 0x3FFF]
		this.zD = 0.0f
		this.zA = this.zD
		this.yD = this.zA
		this.yA = this.yD
		this.xD = this.yA
		this.xC = this.xD
		this.xB = this.xC
		this.yC = -this.zB
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(III[I)V")
	override fun projectRelative(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) destination: IntArray?) {
		@Pc(10) val dz: Int = (z.toFloat() - this.zD).toInt()
		@Pc(17) val dx: Int = (x.toFloat() - this.xD).toInt()
		@Pc(24) val dy: Int = (y.toFloat() - this.yD).toInt()
		destination!![2] = (dz.toFloat() * this.zC + dy.toFloat() * this.yC + dx.toFloat() * this.xC).toInt()
		destination[0] = (dx.toFloat() * this.xA + dy.toFloat() * this.yA + this.zA * dz.toFloat()).toInt()
		destination[1] = (this.xB * dx.toFloat() + this.yB * dy.toFloat() + this.zB * dz.toFloat()).toInt()
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(FZFF)V")
	fun makeTranslation(@OriginalArg(0) y: Float, @OriginalArg(2) z: Float, @OriginalArg(3) x: Float) {
		this.yD = y
		this.zD = z
		this.zC = 1.0f
		this.yB = this.zC
		this.xA = this.yB
		this.xD = x
		this.yC = 0.0f
		this.xC = this.yC
		this.zB = this.xC
		this.xB = this.zB
		this.zA = this.xB
		this.yA = this.zA
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(III)V")
	override fun translate(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int) {
		this.xD += x.toFloat()
		this.yD += y.toFloat()
		this.zD += z.toFloat()
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(Lclient!bca;B)V")
	fun multiply(@OriginalArg(0) other: Matrix?) {
		@Pc(6) val matrix: NativeMatrix? = other as NativeMatrix?
		@Pc(9) val oldXA: Float = this.xA
		@Pc(12) val oldYA: Float = this.yA
		@Pc(15) val oldXB: Float = this.xB
		@Pc(18) val oldYB: Float = this.yB
		@Pc(21) val oldXC: Float = this.xC
		@Pc(24) val oldYC: Float = this.yC
		@Pc(33) val oldXD: Float = this.xD
		this.yA = oldXA * matrix!!.yA + matrix.yB * oldYA + this.zA * matrix.yC
		this.xA = matrix.xC * this.zA + matrix.xA * oldXA + oldYA * matrix.xB
		@Pc(70) val oldYD: Float = this.yD
		this.yB = matrix.yC * this.zB + oldXB * matrix.yA + matrix.yB * oldYB
		this.xB = this.zB * matrix.xC + matrix.xA * oldXB + oldYB * matrix.xB
		this.zA = oldYA * matrix.zB + matrix.zA * oldXA + this.zA * matrix.zC
		this.zB = this.zB * matrix.zC + oldYB * matrix.zB + matrix.zA * oldXB
		this.yC = matrix.yB * oldYC + matrix.yA * oldXC + this.zC * matrix.yC
		this.xC = this.zC * matrix.xC + matrix.xA * oldXC + oldYC * matrix.xB
		this.xD = matrix.xD + oldXD * matrix.xA + matrix.xB * oldYD + matrix.xC * this.zD
		this.yD = matrix.yA * oldXD + oldYD * matrix.yB + this.zD * matrix.yC + matrix.yD
		this.zC = this.zC * matrix.zC + matrix.zA * oldXC + matrix.zB * oldYC
		this.zD = matrix.zA * oldXD + matrix.zB * oldYD + this.zD * matrix.zC + matrix.zD
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "([FI)[F")
	fun copyColumnMajorInto(@OriginalArg(0) dest: FloatArray?): FloatArray? {
		dest!![7] = 0.0f
		dest[13] = this.yD
		dest[14] = this.zD
		dest[12] = this.xD
		dest[11] = 0.0f
		dest[9] = this.yC
		dest[8] = this.xC
		dest[4] = this.xB
		dest[0] = this.xA
		dest[10] = this.zC
		dest[5] = this.yB
		dest[15] = 1.0f
		dest[1] = this.yA
		dest[3] = 0.0f
		dest[2] = this.zA
		dest[6] = this.zB
		return dest
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(F[FFFFB)V")
	fun transformPlane(@OriginalArg(0) normalZ: Float, @OriginalArg(1) dest: FloatArray?, @OriginalArg(2) normalY: Float, @OriginalArg(3) normalX: Float, @OriginalArg(4) distance: Float) {
		dest!![0] = normalY * this.xB + normalX * this.xA + this.xC * normalZ
		dest[1] = this.yA * normalX + normalY * this.yB + this.yC * normalZ
		@Pc(74) val pointX: Float
		@Pc(82) val pointY: Float
		@Pc(66) val pointZ: Float
		@Pc(58) val axisOffset: Float
		if (normalX > 0.00390625f || normalX < -0.00390625f) {
			axisOffset = -distance / normalX
			pointZ = this.zD + axisOffset * this.zA
			pointX = this.xD + axisOffset * this.xA
			pointY = axisOffset * this.yA + this.yD
		} else if (normalY > 0.00390625f || normalY < -0.00390625f) {
			axisOffset = -distance / normalY
			pointY = this.yD + axisOffset * this.yB
			pointZ = this.zD + this.zB * axisOffset
			pointX = this.xD + this.xB * axisOffset
		} else {
			axisOffset = -distance / normalZ
			pointX = this.xC * axisOffset + this.xD
			pointY = this.yC * axisOffset + this.yD
			pointZ = this.zD + axisOffset * this.zC
		}
		dest[2] = normalZ * this.zC + this.zB * normalY + normalX * this.zA
		dest[3] = -(dest[2] * pointZ + dest[0] * pointX + dest[1] * pointY)
	}

	@OriginalMember(owner = "client!haa", name = "d", descriptor = "(I)V")
	override fun rotateAxisX(@OriginalArg(0) angle: Int) {
		@Pc(9) val cos: Float = GroundDecorOption.COSINE[angle and 0x3FFF]
		@Pc(15) val sin: Float = GroundDecorOption.SINE[angle and 0x3FFF]
		@Pc(18) val oldYA: Float = this.yA
		@Pc(21) val oldYB: Float = this.yB
		@Pc(24) val oldYC: Float = this.yC
		this.yA = oldYA * cos - sin * this.zA
		@Pc(37) val oldYD: Float = this.yD
		this.yB = oldYB * cos - sin * this.zB
		this.zA = cos * this.zA + sin * oldYA
		this.zB = cos * this.zB + sin * oldYB
		this.yC = oldYC * cos - this.zC * sin
		this.yD = cos * oldYD - sin * this.zD
		this.zC = cos * this.zC + oldYC * sin
		this.zD = sin * oldYD + cos * this.zD
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(FZFF)V")
	fun translate(@OriginalArg(0) y: Float, @OriginalArg(2) z: Float, @OriginalArg(3) x: Float) {
		this.zD += z
		this.xD += x
		this.yD += y
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(BFFF)F")
	fun transformY(@OriginalArg(1) x: Float, @OriginalArg(2) z: Float, @OriginalArg(3) y: Float): Float {
		return this.yB * y + x * this.yA + z * this.yC + this.yD
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(FFBF[F)V")
	fun transformDirection(@OriginalArg(0) x: Float, @OriginalArg(1) y: Float, @OriginalArg(3) z: Float, @OriginalArg(4) dest: FloatArray?) {
		dest!![2] = z * this.zC + this.zB * y + this.zA * x
		dest[1] = this.yC * z + y * this.yB + x * this.yA
		dest[0] = z * this.xC + this.xA * x + y * this.xB
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(I[F)[F")
	fun copyRowMajorInto(@OriginalArg(1) dest: FloatArray?): FloatArray? {
		dest!![2] = this.xC
		dest[0] = this.xA
		dest[6] = this.yC
		dest[13] = 0.0f
		dest[3] = this.xD
		dest[7] = this.yD
		dest[12] = 0.0f
		dest[14] = 0.0f
		dest[10] = this.zC
		dest[8] = this.zA
		dest[1] = this.xB
		dest[5] = this.yB
		dest[9] = this.zB
		dest[15] = 1.0f
		dest[4] = this.yA
		dest[11] = this.zD
		return dest
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "()Lclient!bca;")
	override fun copy(): Matrix? {
		@Pc(7) val matrix: NativeMatrix = NativeMatrix()
		matrix.zB = this.zB
		matrix.zD = this.zD
		matrix.xB = this.xB
		matrix.xD = this.xD
		matrix.zA = this.zA
		matrix.yD = this.yD
		matrix.yB = this.yB
		matrix.xC = this.xC
		matrix.yA = this.yA
		matrix.zC = this.zC
		matrix.xA = this.xA
		matrix.yC = this.yC
		return matrix
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(ILclient!bca;)V")
	fun copyRotation(@OriginalArg(1) source: Matrix?) {
		@Pc(12) val matrix: NativeMatrix? = source as NativeMatrix?
		this.xA = matrix!!.xA
		this.yD = 0.0f
		this.yB = matrix.yB
		this.zA = matrix.zA
		this.zB = matrix.zB
		this.zC = matrix.zC
		this.xB = matrix.xB
		this.yA = matrix.yA
		this.yC = matrix.yC
		this.zD = 0.0f
		this.xD = 0.0f
		this.xC = matrix.xC
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(Lclient!bca;Lclient!bca;)V")
	fun multiply(@OriginalArg(0) matrix: Matrix?, @OriginalArg(1) matrix2: Matrix?) {
		@Pc(6) val left: NativeMatrix? = matrix as NativeMatrix?
		@Pc(9) val right: NativeMatrix? = matrix2 as NativeMatrix?
		this.xA = left!!.xC * right!!.zA + left.xA * right.xA + right.yA * left.xB
		this.yA = left.yB * right.yA + right.xA * left.yA + left.yC * right.zA
		this.zA = right.zA * left.zC + left.zA * right.xA + right.yA * left.zB
		this.xB = right.zB * left.xC + right.yB * left.xB + right.xB * left.xA
		this.yB = left.yA * right.xB + right.yB * left.yB + left.yC * right.zB
		this.xC = left.xB * right.yC + left.xA * right.xC + left.xC * right.zC
		this.zB = left.zC * right.zB + left.zA * right.xB + left.zB * right.yB
		this.yC = left.yA * right.xC + left.yB * right.yC + right.zC * left.yC
		this.xD = left.xD + right.zD * left.xC + right.yD * left.xB + left.xA * right.xD
		this.zC = left.zB * right.yC + left.zA * right.xC + left.zC * right.zC
		this.yD = left.yD + left.yC * right.zD + right.yD * left.yB + left.yA * right.xD
		this.zD = left.zB * right.yD + left.zA * right.xD + right.zD * left.zC + left.zD
	}

	@OriginalMember(owner = "client!haa", name = "c", descriptor = "([FI)[F")
	fun copy2DColumnMajorInto(@OriginalArg(0) dest: FloatArray?): FloatArray? {
		dest!![13] = 0.0f
		dest[1] = this.yA
		dest[5] = this.yB
		dest[6] = 0.0f
		dest[3] = 0.0f
		dest[15] = 1.0f
		dest[4] = this.xB
		dest[14] = 0.0f
		dest[10] = this.zD
		dest[2] = 0.0f
		dest[0] = this.xA
		dest[7] = 0.0f
		dest[11] = 0.0f
		dest[12] = 0.0f
		dest[8] = this.xD
		dest[9] = this.yD
		return dest
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(FFBF)F")
	fun transformX(@OriginalArg(0) z: Float, @OriginalArg(1) y: Float, @OriginalArg(3) x: Float): Float {
		return z * this.xC + y * this.xB + x * this.xA + this.xD
	}

	@OriginalMember(owner = "client!haa", name = "g", descriptor = "(I)V")
	override fun rotateAxisZ(@OriginalArg(0) angle: Int) {
		@Pc(9) val cos: Float = GroundDecorOption.COSINE[angle and 0x3FFF]
		@Pc(15) val sin: Float = GroundDecorOption.SINE[angle and 0x3FFF]
		@Pc(18) val oldXA: Float = this.xA
		@Pc(21) val oldXB: Float = this.xB
		@Pc(24) val oldXC: Float = this.xC
		@Pc(27) val oldXD: Float = this.xD
		this.xA = oldXA * cos - this.yA * sin
		this.yA = cos * this.yA + oldXA * sin
		this.xB = oldXB * cos - this.yB * sin
		this.yB = this.yB * cos + oldXB * sin
		this.xC = oldXC * cos - sin * this.yC
		this.xD = cos * oldXD - sin * this.yD
		this.yC = sin * oldXC + cos * this.yC
		this.yD = sin * oldXD + this.yD * cos
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "([I)V")
	override fun project(@OriginalArg(0) destination: IntArray?) {
		@Pc(12) val dx: Float = destination!![0].toFloat() - this.xD
		@Pc(20) val dy: Float = destination[1].toFloat() - this.yD
		@Pc(28) val dz: Float = destination[2].toFloat() - this.zD
		destination[2] = (dz * this.zC + this.xC * dx + dy * this.yC).toInt()
		destination[1] = (this.zB * dz + this.yB * dy + dx * this.xB).toInt()
		destination[0] = (this.yA * dy + this.xA * dx + dz * this.zA).toInt()
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(III[I)V")
	override fun method4652(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) destination: IntArray?) {
		destination!![2] = (y.toFloat() * this.zB + this.zA * x.toFloat() + this.zC * z.toFloat() + this.zD).toInt()
		destination[1] = (this.yC * z.toFloat() + this.yB * y.toFloat() + x.toFloat() * this.yA + this.yD).toInt()
		destination[0] = (this.xD + this.xC * z.toFloat() + x.toFloat() * this.xA + this.xB * y.toFloat()).toInt()
	}

	@OriginalMember(owner = "client!haa", name = "c", descriptor = "(III[I)V")
	override fun projectDirection(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) destination: IntArray?) {
		destination!![0] = (x.toFloat() * this.xA + this.xB * y.toFloat() + z.toFloat() * this.xC).toInt()
		destination[1] = (y.toFloat() * this.yB + this.yA * x.toFloat() + this.yC * z.toFloat()).toInt()
		destination[2] = (x.toFloat() * this.zA + this.zB * y.toFloat() + z.toFloat() * this.zC).toInt()
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(I[F)[F")
	fun copyRotationRowMajorInto(@OriginalArg(1) dest: FloatArray?): FloatArray? {
		dest!![7] = 0.0f
		dest[14] = 0.0f
		dest[8] = this.zA
		dest[6] = this.yC
		dest[4] = this.yA
		dest[2] = this.xC
		dest[11] = 0.0f
		dest[15] = 0.0f
		dest[9] = this.zB
		dest[12] = 0.0f
		dest[13] = 0.0f
		dest[3] = 0.0f
		dest[1] = this.xB
		dest[0] = this.xA
		dest[10] = this.zC
		dest[5] = this.yB
		return dest
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(ILclient!bca;)V")
	fun setInverse(@OriginalArg(1) source: Matrix?) {
		@Pc(6) val matrix: NativeMatrix? = source as NativeMatrix?
		this.xB = matrix!!.yA
		this.xC = matrix.zA
		this.xA = matrix.xA
		this.yA = matrix.xB
		this.yB = matrix.yB
		this.zA = matrix.xC
		this.yC = matrix.zB
		this.zB = matrix.yC
		this.xD = -(this.xC * matrix.zD + matrix.yD * this.xB + matrix.xD * this.xA)
		this.zC = matrix.zC
		this.yD = -(matrix.zD * this.yC + this.yA * matrix.xD + matrix.yD * this.yB)
		this.zD = -(matrix.yD * this.zB + this.zA * matrix.xD + this.zC * matrix.zD)
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(I)V")
	override fun rotateAxisY(@OriginalArg(0) angle: Int) {
		@Pc(9) val cos: Float = GroundDecorOption.COSINE[angle and 0x3FFF]
		@Pc(15) val sin: Float = GroundDecorOption.SINE[angle and 0x3FFF]
		@Pc(18) val oldXA: Float = this.xA
		@Pc(21) val oldXB: Float = this.xB
		@Pc(24) val oldXC: Float = this.xC
		this.xA = cos * oldXA + this.zA * sin
		@Pc(37) val oldXD: Float = this.xD
		this.zA = this.zA * cos - sin * oldXA
		this.xB = cos * oldXB + sin * this.zB
		this.zB = this.zB * cos - sin * oldXB
		this.xC = cos * oldXC + this.zC * sin
		this.zC = this.zC * cos - oldXC * sin
		this.xD = oldXD * cos + sin * this.zD
		this.zD = cos * this.zD - oldXD * sin
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(FFFB)V")
	fun setScale(@OriginalArg(0) scaleZ: Float, @OriginalArg(1) scaleY: Float, @OriginalArg(2) scaleX: Float) {
		this.yB = scaleY
		this.xC = 0.0f
		this.xB = 0.0f
		this.xD = 0.0f
		this.zC = scaleZ
		this.zD = 0.0f
		this.yC = 0.0f
		this.yD = 0.0f
		this.zA = 0.0f
		this.yA = 0.0f
		this.zB = 0.0f
		this.xA = scaleX
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(BFFF)F")
	fun transformZ(@OriginalArg(1) x: Float, @OriginalArg(2) z: Float, @OriginalArg(3) y: Float): Float {
		return this.zD + this.zA * x + y * this.zB + this.zC * z
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(B)V")
	fun negateYZ() {
		this.yD = -this.yD
		this.zB = -this.zB
		this.zA = -this.zA
		this.yC = -this.yC
		this.zD = -this.zD
		this.zC = -this.zC
		this.yA = -this.yA
		this.yB = -this.yB
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(Lclient!bca;)V")
	override fun apply(@OriginalArg(0) other: Matrix?) {
		@Pc(6) val matrix: NativeMatrix? = other as NativeMatrix?
		this.zD = matrix!!.zD
		this.zA = matrix.zA
		this.xD = matrix.xD
		this.xB = matrix.xB
		this.zB = matrix.zB
		this.xA = matrix.xA
		this.xC = matrix.xC
		this.yC = matrix.yC
		this.zC = matrix.zC
		this.yB = matrix.yB
		this.yD = matrix.yD
		this.yA = matrix.yA
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(FFFFFFFFFI)V")
	fun setRotation(@OriginalArg(0) zc: Float, @OriginalArg(1) ya: Float, @OriginalArg(2) xb: Float, @OriginalArg(3) za: Float, @OriginalArg(4) zb: Float, @OriginalArg(5) yb: Float, @OriginalArg(6) xc: Float, @OriginalArg(7) yc: Float, @OriginalArg(8) xa: Float) {
		this.yA = ya
		this.zC = zc
		this.xB = xb
		this.zD = 0.0f
		this.yC = yc
		this.xD = 0.0f
		this.yB = yb
		this.xC = xc
		this.yD = 0.0f
		this.zB = zb
		this.zA = za
		this.xA = xa
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(IIIFFFI)V")
	fun setTextureCoordTransform(@OriginalArg(0) rotation: Int, @OriginalArg(2) scaleV: Int, @OriginalArg(3) translateX: Float, @OriginalArg(4) translateZ: Float, @OriginalArg(5) translateY: Float, @OriginalArg(6) scaleU: Int) {
		if (rotation == 0) {
			this.yC = 0.0f
			this.xC = this.yC
			this.zB = this.xC
			this.xB = this.zB
			this.zA = this.xB
			this.yA = this.zA
			this.xD = translateX - scaleU.toFloat()
			this.zC = 1.0f
			this.yD = (-scaleV).toFloat() + translateY
			this.xA = (scaleU * 2).toFloat()
			this.yB = (scaleV * 2).toFloat()
			this.zD = translateZ
			return
		}
		@Pc(12) val cosAngle: Float = GroundDecorOption.COSINE[rotation and 0x3FFF]
		@Pc(18) val sinAngle: Float = GroundDecorOption.SINE[rotation and 0x3FFF]
		this.xA = cosAngle * 2.0f * scaleU.toFloat()
		this.yD = (-0.5f * sinAngle - cosAngle * 0.5f) * (scaleV * 2).toFloat() + translateY
		this.yB = scaleV.toFloat() * cosAngle * 2.0f
		this.zC = 1.0f
		this.xB = -2.0f * sinAngle * scaleV.toFloat()
		this.yC = 0.0f
		this.xC = this.yC
		this.zB = this.xC
		this.zA = this.zB
		this.zD = translateZ
		this.yA = scaleU.toFloat() * sinAngle * 2.0f
		this.xD = translateX + (scaleU * 2).toFloat() * (sinAngle * 0.5f - cosAngle * 0.5f)
	}
}
