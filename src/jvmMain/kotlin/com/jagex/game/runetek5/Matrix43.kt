package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.Matrix
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.sound.jagtheora.OggStreamDecoder

@OriginalClass("client!iaa")
class Matrix43 : Matrix {

	@OriginalMember(owner = "client!iaa", name = "i", descriptor = "F")
	@JvmField
	var xD: Float = 0.0f

	@OriginalMember(owner = "client!iaa", name = "l", descriptor = "F")
	@JvmField
	var xB: Float = 0.0f

	@OriginalMember(owner = "client!iaa", name = "m", descriptor = "F")
	@JvmField
	var zD: Float = 0.0f

	@OriginalMember(owner = "client!iaa", name = "p", descriptor = "F")
	@JvmField
	var zB: Float = 0.0f

	@OriginalMember(owner = "client!iaa", name = "r", descriptor = "F")
	@JvmField
	var zA: Float = 0.0f

	@OriginalMember(owner = "client!iaa", name = "w", descriptor = "F")
	@JvmField
	var yA: Float = 0.0f

	@OriginalMember(owner = "client!iaa", name = "x", descriptor = "F")
	@JvmField
	var yC: Float = 0.0f

	@OriginalMember(owner = "client!iaa", name = "E", descriptor = "F")
	@JvmField
	var yB: Float = 0.0f

	@OriginalMember(owner = "client!iaa", name = "F", descriptor = "F")
	@JvmField
	var xA: Float = 0.0f

	@OriginalMember(owner = "client!iaa", name = "H", descriptor = "F")
	@JvmField
	var yD: Float = 0.0f

	@OriginalMember(owner = "client!iaa", name = "Q", descriptor = "F")
	@JvmField
	var xC: Float = 0.0f

	@OriginalMember(owner = "client!iaa", name = "T", descriptor = "F")
	@JvmField
	var zC: Float = 0.0f

	@OriginalMember(owner = "client!iaa", name = "<init>", descriptor = "()V")
	constructor() : super() {
		this.makeIdentity()
	}

	@OriginalMember(owner = "client!iaa", name = "e", descriptor = "(I)V")
	override fun makeRotationZ(@OriginalArg(0) angle: Int) {
		this.zC = 1.0f
		this.yB = OggStreamDecoder.COSINE[angle and 0x3FFF]
		this.xA = this.yB
		this.yA = OggStreamDecoder.SINE[angle and 0x3FFF]
		this.zD = 0.0f
		this.zB = this.zD
		this.zA = this.zB
		this.yD = this.zA
		this.yC = this.yD
		this.xD = this.yC
		this.xC = this.xD
		this.xB = -this.yA
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(ZFFFF[F)V")
	fun transformPlane(@OriginalArg(1) normalX: Float, @OriginalArg(2) normalZ: Float, @OriginalArg(3) normalY: Float, @OriginalArg(4) distance: Float, @OriginalArg(5) dest: FloatArray?) {
		@Pc(26) val pointX: Float
		@Pc(42) val pointY: Float
		@Pc(34) val pointZ: Float
		@Pc(18) val axisOffset: Float
		if (normalX > 0.00390625f || normalX < -0.00390625f) {
			axisOffset = -distance / normalX
			pointX = axisOffset * this.xA + this.xD
			pointZ = this.zD + this.zA * axisOffset
			pointY = this.yD + axisOffset * this.yA
		} else if (normalY > 0.00390625f || normalY < -0.00390625f) {
			axisOffset = -distance / normalY
			pointY = this.yB * axisOffset + this.yD
			pointX = this.xB * axisOffset + this.xD
			pointZ = this.zB * axisOffset + this.zD
		} else {
			axisOffset = -distance / normalZ
			pointX = axisOffset * this.xC + this.xD
			pointY = this.yC * axisOffset + this.yD
			pointZ = this.zD + axisOffset * this.zC
		}
		dest!![0] = normalZ * this.xC + normalX * this.xA + normalY * this.xB
		dest[2] = normalZ * this.zC + this.zA * normalX + this.zB * normalY
		dest[1] = this.yB * normalY + this.yA * normalX + this.yC * normalZ
		dest[3] = -(dest[0] * pointX + pointY * dest[1] + pointZ * dest[2])
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(III)V")
	override fun translate(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int) {
		this.zD += z.toFloat()
		this.yD += y.toFloat()
		this.xD += x.toFloat()
	}

	@OriginalMember(owner = "client!iaa", name = "d", descriptor = "(I)V")
	override fun rotateAxisX(@OriginalArg(0) angle: Int) {
		@Pc(9) val cos: Float = OggStreamDecoder.COSINE[angle and 0x3FFF]
		@Pc(15) val sin: Float = OggStreamDecoder.SINE[angle and 0x3FFF]
		@Pc(18) val oldYA: Float = this.yA
		@Pc(21) val oldYB: Float = this.yB
		@Pc(24) val oldYC: Float = this.yC
		this.yA = cos * oldYA - this.zA * sin
		@Pc(37) val oldYD: Float = this.yD
		this.zA = this.zA * cos + sin * oldYA
		this.yB = oldYB * cos - sin * this.zB
		this.yC = cos * oldYC - this.zC * sin
		this.zB = oldYB * sin + cos * this.zB
		this.zC = oldYC * sin + cos * this.zC
		this.yD = cos * oldYD - sin * this.zD
		this.zD = cos * this.zD + oldYD * sin
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(FIFIIIF)V")
	fun setBillboardTransform(@OriginalArg(0) translateZ: Float, @OriginalArg(1) scaleX: Int, @OriginalArg(2) translateX: Float, @OriginalArg(3) scaleY: Int, @OriginalArg(4) angle: Int, @OriginalArg(6) translateY: Float) {
		if (angle == 0) {
			this.yC = 0.0f
			this.xC = this.yC
			this.zB = this.xC
			this.xB = this.zB
			this.zA = this.xB
			this.yA = this.zA
			this.xA = scaleX.toFloat()
			this.zC = 1.0f
			this.yB = scaleY.toFloat()
		} else {
			@Pc(41) val cos: Float = OggStreamDecoder.COSINE[angle and 0x3FFF]
			@Pc(47) val sin: Float = OggStreamDecoder.SINE[angle and 0x3FFF]
			this.yA = scaleX.toFloat() * sin
			this.yB = scaleY.toFloat() * cos
			this.yC = 0.0f
			this.xC = this.yC
			this.zB = this.xC
			this.zA = this.zB
			this.zC = 1.0f
			this.xB = -sin * scaleY.toFloat()
			this.xA = scaleX.toFloat() * cos
		}
		this.xD = translateX
		this.yD = translateY
		this.zD = translateZ
	}

	@OriginalMember(owner = "client!iaa", name = "b", descriptor = "(III[I)V")
	override fun method4652(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) destination: IntArray?) {
		destination!![0] = (z.toFloat() * this.xC + x.toFloat() * this.xA + y.toFloat() * this.xB + this.xD).toInt()
		destination[2] = (y.toFloat() * this.zB + this.zA * x.toFloat() + this.zC * z.toFloat() + this.zD).toInt()
		destination[1] = (this.yD + z.toFloat() * this.yC + x.toFloat() * this.yA + y.toFloat() * this.yB).toInt()
	}

	@OriginalMember(owner = "client!iaa", name = "b", descriptor = "(III)V")
	override fun applyTranslation(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int) {
		this.zD = z.toFloat()
		this.xD = x.toFloat()
		this.yC = 0.0f
		this.xC = this.yC
		this.zB = this.xC
		this.xB = this.zB
		this.zA = this.xB
		this.yA = this.zA
		this.yD = y.toFloat()
		this.zC = 1.0f
		this.yB = this.zC
		this.xA = this.yB
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "([I)V")
	override fun project(@OriginalArg(0) destination: IntArray?) {
		@Pc(11) val dx: Float = destination!![0].toFloat() - this.xD
		@Pc(20) val dy: Float = destination[1].toFloat() - this.yD
		@Pc(29) val dz: Float = destination[2].toFloat() - this.zD
		destination[1] = (this.yB * dy + dx * this.xB + this.zB * dz).toInt()
		destination[2] = (this.zC * dz + dy * this.yC + this.xC * dx).toInt()
		destination[0] = (this.xA * dx + this.yA * dy + dz * this.zA).toInt()
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(IIIIII)V")
	override fun createCamera(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) rotateX: Int, @OriginalArg(4) rotateY: Int, @OriginalArg(5) rotateZ: Int) {
		@Pc(9) val cosX: Float = OggStreamDecoder.COSINE[rotateX and 0x3FFF]
		@Pc(15) val sinX: Float = OggStreamDecoder.SINE[rotateX and 0x3FFF]
		@Pc(21) val cosY: Float = OggStreamDecoder.COSINE[rotateY and 0x3FFF]
		@Pc(27) val sinY: Float = OggStreamDecoder.SINE[rotateY and 0x3FFF]
		@Pc(33) val cosZ: Float = OggStreamDecoder.COSINE[rotateZ and 0x3FFF]
		@Pc(39) val sinZ: Float = OggStreamDecoder.SINE[rotateZ and 0x3FFF]
		@Pc(43) val sinXCosZ: Float = sinX * cosZ
		@Pc(47) val sinXSinZ: Float = sinZ * sinX
		this.yC = sinXCosZ * cosY + sinY * sinZ
		this.zA = cosX * sinY
		this.xA = sinY * sinXSinZ + cosY * cosZ
		this.yB = cosZ * cosX
		this.zB = -sinX
		this.xB = sinZ * cosX
		this.zC = cosX * cosY
		this.xC = cosZ * -sinY + cosY * sinXSinZ
		this.yA = sinXCosZ * sinY + -cosY * sinZ
		this.zD = this.zA * (-x).toFloat() - this.zB * y.toFloat() - z.toFloat() * this.zC
		this.xD = -(this.xB * y.toFloat()) + this.xA * (-x).toFloat() - this.xC * z.toFloat()
		this.yD = -(z.toFloat() * this.yC) + this.yA * (-x).toFloat() - this.yB * y.toFloat()
	}

	@OriginalMember(owner = "client!iaa", name = "g", descriptor = "(I)V")
	override fun rotateAxisZ(@OriginalArg(0) angle: Int) {
		@Pc(9) val cos: Float = OggStreamDecoder.COSINE[angle and 0x3FFF]
		@Pc(15) val sin: Float = OggStreamDecoder.SINE[angle and 0x3FFF]
		@Pc(18) val oldXA: Float = this.xA
		@Pc(21) val oldXB: Float = this.xB
		@Pc(24) val oldXC: Float = this.xC
		this.xA = cos * oldXA - this.yA * sin
		@Pc(37) val oldXD: Float = this.xD
		this.yA = this.yA * cos + oldXA * sin
		this.xB = oldXB * cos - this.yB * sin
		this.xC = oldXC * cos - sin * this.yC
		this.yB = cos * this.yB + sin * oldXB
		this.yC = this.yC * cos + oldXC * sin
		this.xD = cos * oldXD - sin * this.yD
		this.yD = this.yD * cos + sin * oldXD
	}

	@OriginalMember(owner = "client!iaa", name = "j", descriptor = "(I)[F")
	fun getColumnMajor(): FloatArray {
		Matrix43.columnMajorBuffer[0] = this.xA
		Matrix43.columnMajorBuffer[9] = this.yC
		Matrix43.columnMajorBuffer[12] = this.xD
		Matrix43.columnMajorBuffer[1] = this.yA
		Matrix43.columnMajorBuffer[6] = this.zB
		Matrix43.columnMajorBuffer[4] = this.xB
		Matrix43.columnMajorBuffer[10] = this.zC
		Matrix43.columnMajorBuffer[8] = this.xC
		Matrix43.columnMajorBuffer[14] = this.zD
		Matrix43.columnMajorBuffer[13] = this.yD
		Matrix43.columnMajorBuffer[2] = this.zA
		Matrix43.columnMajorBuffer[5] = this.yB
		return Matrix43.columnMajorBuffer
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "()Lclient!bca;")
	override fun copy(): Matrix? {
		@Pc(7) val matrix: Matrix43 = Matrix43()
		matrix.xD = this.xD
		matrix.xB = this.xB
		matrix.yB = this.yB
		matrix.zC = this.zC
		matrix.yD = this.yD
		matrix.yC = this.yC
		matrix.xC = this.xC
		matrix.xA = this.xA
		matrix.zA = this.zA
		matrix.yA = this.yA
		matrix.zD = this.zD
		matrix.zB = this.zB
		return matrix
	}

	@OriginalMember(owner = "client!iaa", name = "k", descriptor = "(I)V")
	fun negateYZ() {
		this.zB = -this.zB
		this.yA = -this.yA
		this.yB = -this.yB
		this.zA = -this.zA
		this.zC = -this.zC
		this.yC = -this.yC
		this.yD = -this.yD
		this.zD = -this.zD
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(III[I)V")
	override fun projectRelative(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) destination: IntArray?) {
		@Pc(6) val dy: Int = (y.toFloat() - this.yD).toInt()
		@Pc(17) val dz: Int = (z.toFloat() - this.zD).toInt()
		@Pc(24) val dx: Int = (x.toFloat() - this.xD).toInt()
		destination!![0] = (this.zA * dz.toFloat() + dx.toFloat() * this.xA + dy.toFloat() * this.yA).toInt()
		destination[2] = (this.xC * dx.toFloat() + dy.toFloat() * this.yC + this.zC * dz.toFloat()).toInt()
		destination[1] = (dz.toFloat() * this.zB + this.xB * dx.toFloat() + dy.toFloat() * this.yB).toInt()
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(Lclient!bca;)V")
	override fun apply(@OriginalArg(0) other: Matrix?) {
		@Pc(6) val source: Matrix43? = other as Matrix43?
		this.zD = source!!.zD
		this.yB = source.yB
		this.xA = source.xA
		this.xC = source.xC
		this.xD = source.xD
		this.zB = source.zB
		this.yA = source.yA
		this.yD = source.yD
		this.zC = source.zC
		this.yC = source.yC
		this.zA = source.zA
		this.xB = source.xB
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(I)V")
	override fun rotate(@OriginalArg(0) angle: Int) {
		this.yB = 1.0f
		this.zC = OggStreamDecoder.COSINE[angle and 0x3FFF]
		this.xA = this.zC
		this.xC = OggStreamDecoder.SINE[angle and 0x3FFF]
		this.zA = -this.xC
		this.zD = 0.0f
		this.zB = this.zD
		this.yD = this.zB
		this.yC = this.yD
		this.yA = this.yC
		this.xD = this.yA
		this.xB = this.xD
	}

	@OriginalMember(owner = "client!iaa", name = "c", descriptor = "(I)V")
	override fun makeRotationX(@OriginalArg(0) angle: Int) {
		this.xA = 1.0f
		this.zC = OggStreamDecoder.COSINE[angle and 0x3FFF]
		this.yB = this.zC
		this.zB = OggStreamDecoder.SINE[angle and 0x3FFF]
		this.yC = -this.zB
		this.zD = 0.0f
		this.zA = this.zD
		this.yD = this.zA
		this.yA = this.yD
		this.xD = this.yA
		this.xC = this.xD
		this.xB = this.xC
	}

	@OriginalMember(owner = "client!iaa", name = "b", descriptor = "()V")
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

	@OriginalMember(owner = "client!iaa", name = "b", descriptor = "(I)V")
	override fun rotateAxisY(@OriginalArg(0) angle: Int) {
		@Pc(9) val cos: Float = OggStreamDecoder.COSINE[angle and 0x3FFF]
		@Pc(15) val sin: Float = OggStreamDecoder.SINE[angle and 0x3FFF]
		@Pc(18) val oldXA: Float = this.xA
		@Pc(21) val oldXB: Float = this.xB
		@Pc(24) val oldXC: Float = this.xC
		this.xA = oldXA * cos + this.zA * sin
		@Pc(37) val oldXD: Float = this.xD
		this.zA = cos * this.zA - sin * oldXA
		this.xB = cos * oldXB + sin * this.zB
		this.zB = this.zB * cos - oldXB * sin
		this.xC = cos * oldXC + this.zC * sin
		this.zC = cos * this.zC - oldXC * sin
		this.xD = this.zD * sin + cos * oldXD
		this.zD = this.zD * cos - sin * oldXD
	}

	@OriginalMember(owner = "client!iaa", name = "b", descriptor = "(Z)[F")
	fun getRotationColumnMajor(): FloatArray {
		Matrix43.columnMajorBuffer[14] = 0.0f
		Matrix43.columnMajorBuffer[0] = this.xA
		Matrix43.columnMajorBuffer[13] = 0.0f
		Matrix43.columnMajorBuffer[1] = this.yA
		Matrix43.columnMajorBuffer[12] = 0.0f
		Matrix43.columnMajorBuffer[10] = this.zC
		Matrix43.columnMajorBuffer[5] = this.yB
		Matrix43.columnMajorBuffer[6] = this.zB
		Matrix43.columnMajorBuffer[9] = this.yC
		Matrix43.columnMajorBuffer[2] = this.zA
		Matrix43.columnMajorBuffer[4] = this.xB
		Matrix43.columnMajorBuffer[8] = this.xC
		return Matrix43.columnMajorBuffer
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(Lclient!bca;I)V")
	fun setInverse(@OriginalArg(0) matrix: Matrix?) {
		@Pc(6) val source: Matrix43? = matrix as Matrix43?
		this.xC = source!!.zA
		this.xB = source.yA
		this.xA = source.xA
		this.zA = source.xC
		this.yC = source.zB
		this.yA = source.xB
		this.yB = source.yB
		this.xD = -(source.yD * this.xB + source.xD * this.xA + source.zD * this.xC)
		this.zB = source.yC
		this.zC = source.zC
		this.yD = -(source.yD * this.yB + source.xD * this.yA + source.zD * this.yC)
		this.zD = -(this.zA * source.xD + this.zB * source.yD + source.zD * this.zC)
	}

	@OriginalMember(owner = "client!iaa", name = "c", descriptor = "(III[I)V")
	override fun projectDirection(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) destination: IntArray?) {
		destination!![1] = (z.toFloat() * this.yC + y.toFloat() * this.yB + this.yA * x.toFloat()).toInt()
		destination[2] = (this.zB * y.toFloat() + this.zA * x.toFloat() + z.toFloat() * this.zC).toInt()
		destination[0] = (this.xB * y.toFloat() + this.xA * x.toFloat() + this.xC * z.toFloat()).toInt()
	}

	companion object {
		@OriginalMember(owner = "client!wg", name = "k", descriptor = "[F")
		@JvmField
		val columnMajorBuffer: FloatArray = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f)
	}
}
