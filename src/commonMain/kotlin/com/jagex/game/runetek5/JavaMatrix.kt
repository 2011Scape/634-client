package com.jagex.game.runetek5

import com.jagex.game.runetek5.graphics.Matrix
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.preference.GroundDecorOption
import kotlin.jvm.JvmField

@OriginalClass("client!gq")
class JavaMatrix @OriginalMember(owner = "client!gq", name = "<init>", descriptor = "()V") constructor() : Matrix() {

	@OriginalMember(owner = "client!gq", name = "h", descriptor = "F")
	@JvmField
	var e2_1: Float = 0f

	@OriginalMember(owner = "client!gq", name = "o", descriptor = "F")
	@JvmField
	var e3_1: Float = 0f

	@OriginalMember(owner = "client!gq", name = "q", descriptor = "F")
	@JvmField
	var e3_3: Float = 0f

	@OriginalMember(owner = "client!gq", name = "s", descriptor = "F")
	@JvmField
	var e2_3: Float = 0f

	@OriginalMember(owner = "client!gq", name = "v", descriptor = "F")
	@JvmField
	var e1_3: Float = 0f

	@OriginalMember(owner = "client!gq", name = "y", descriptor = "F")
	@JvmField
	var e1_1: Float = 0f

	@OriginalMember(owner = "client!gq", name = "z", descriptor = "F")
	@JvmField
	var e1_2: Float = 0f

	@OriginalMember(owner = "client!gq", name = "E", descriptor = "F")
	@JvmField
	var e2_2: Float = 0f

	@OriginalMember(owner = "client!gq", name = "G", descriptor = "F")
	@JvmField
	var e3_2: Float = 0f

	@OriginalMember(owner = "client!gq", name = "H", descriptor = "F")
	@JvmField
	var tz: Float = 0f

	@OriginalMember(owner = "client!gq", name = "L", descriptor = "F")
	@JvmField
	var ty: Float = 0f

	@OriginalMember(owner = "client!gq", name = "M", descriptor = "F")
	@JvmField
	var tx: Float = 0f

	init {
		this.makeIdentity()
	}

	@OriginalMember(owner = "client!gq", name = "b", descriptor = "()V")
	override fun makeIdentity() {
		this.e3_3 = 1.0f
		this.e2_2 = this.e3_3
		this.e1_1 = this.e2_2
		this.tz = 0.0f
		this.ty = this.tz
		this.tx = this.ty
		this.e2_3 = this.tx
		this.e1_3 = this.e2_3
		this.e3_2 = this.e1_3
		this.e1_2 = this.e3_2
		this.e3_1 = this.e1_2
		this.e2_1 = this.e3_1
	}

	@OriginalMember(owner = "client!gq", name = "b", descriptor = "(III)V")
	override fun applyTranslation(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int) {
		this.ty = y.toFloat()
		this.e2_3 = 0.0f
		this.e1_3 = this.e2_3
		this.e3_2 = this.e1_3
		this.e1_2 = this.e3_2
		this.e3_1 = this.e1_2
		this.e2_1 = this.e3_1
		this.e3_3 = 1.0f
		this.e2_2 = this.e3_3
		this.e1_1 = this.e2_2
		this.tx = x.toFloat()
		this.tz = z.toFloat()
	}

	@OriginalMember(owner = "client!gq", name = "a", descriptor = "(IIIIII)V")
	override fun createCamera(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) rotateX: Int, @OriginalArg(4) rotateY: Int, @OriginalArg(5) rotateZ: Int) {
		@Pc(9) val cosX: Float = GroundDecorOption.COSINE[rotateX and 0x3FFF]
		@Pc(15) val sinX: Float = GroundDecorOption.SINE[rotateX and 0x3FFF]
		@Pc(21) val cosY: Float = GroundDecorOption.COSINE[rotateY and 0x3FFF]
		@Pc(27) val sinY: Float = GroundDecorOption.SINE[rotateY and 0x3FFF]
		@Pc(33) val cosZ: Float = GroundDecorOption.COSINE[rotateZ and 0x3FFF]
		@Pc(39) val sinZ: Float = GroundDecorOption.SINE[rotateZ and 0x3FFF]
		@Pc(43) val cosZSinX: Float = cosZ * sinX
		@Pc(47) val sinXSinZ: Float = sinX * sinZ
		this.e2_3 = sinZ * sinY + cosY * cosZSinX
		this.e1_3 = cosZ * -sinY + sinXSinZ * cosY
		this.e3_2 = -sinX
		this.e2_2 = cosX * cosZ
		this.e3_3 = cosY * cosX
		this.e3_1 = cosX * sinY
		this.e2_1 = cosZSinX * sinY + -cosY * sinZ
		this.e1_2 = cosX * sinZ
		this.e1_1 = sinXSinZ * sinY + cosZ * cosY
		this.tz = -(z.toFloat() * this.e3_3) - y.toFloat() * this.e3_2 + (-x).toFloat() * this.e3_1
		this.tx = this.e1_1 * (-x).toFloat() - y.toFloat() * this.e1_2 - this.e1_3 * z.toFloat()
		this.ty = -(this.e2_3 * z.toFloat()) + this.e2_1 * (-x).toFloat() - y.toFloat() * this.e2_2
	}

	@OriginalMember(owner = "client!gq", name = "a", descriptor = "(Lclient!bca;)V")
	override fun apply(@OriginalArg(0) other: Matrix?) {
		@Pc(6) val javaMatrix: JavaMatrix = other as JavaMatrix
		this.e1_3 = javaMatrix.e1_3
		this.tz = javaMatrix.tz
		this.e2_1 = javaMatrix.e2_1
		this.e3_3 = javaMatrix.e3_3
		this.e2_3 = javaMatrix.e2_3
		this.e2_2 = javaMatrix.e2_2
		this.e3_2 = javaMatrix.e3_2
		this.e1_1 = javaMatrix.e1_1
		this.e3_1 = javaMatrix.e3_1
		this.tx = javaMatrix.tx
		this.e1_2 = javaMatrix.e1_2
		this.ty = javaMatrix.ty
	}

	@OriginalMember(owner = "client!gq", name = "c", descriptor = "(III[I)V")
	override fun projectDirection(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) destination: IntArray?) {
		destination!![0] = (z.toFloat() * this.e1_3 + this.e1_2 * y.toFloat() + this.e1_1 * x.toFloat()).toInt()
		destination[1] = (this.e2_1 * x.toFloat() + y.toFloat() * this.e2_2 + this.e2_3 * z.toFloat()).toInt()
		destination[2] = (this.e3_3 * z.toFloat() + x.toFloat() * this.e3_1 + y.toFloat() * this.e3_2).toInt()
	}

	@OriginalMember(owner = "client!gq", name = "a", descriptor = "(III)V")
	override fun translate(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int) {
		this.ty += y.toFloat()
		this.tz += z.toFloat()
		this.tx += x.toFloat()
	}

	@OriginalMember(owner = "client!gq", name = "g", descriptor = "(I)V")
	override fun rotateAxisZ(@OriginalArg(0) angle: Int) {
		@Pc(9) val cos: Float = GroundDecorOption.COSINE[angle and 0x3FFF]
		@Pc(15) val sin: Float = GroundDecorOption.SINE[angle and 0x3FFF]
		@Pc(18) val e1_1: Float = this.e1_1
		@Pc(21) val e1_2: Float = this.e1_2
		@Pc(24) val e1_3: Float = this.e1_3
		this.e1_1 = cos * e1_1 - sin * this.e2_1
		@Pc(38) val oldTx: Float = this.tx
		this.e1_2 = e1_2 * cos - this.e2_2 * sin
		this.e2_1 = cos * this.e2_1 + e1_1 * sin
		this.e1_3 = cos * e1_3 - this.e2_3 * sin
		this.e2_2 = cos * this.e2_2 + sin * e1_2
		this.e2_3 = sin * e1_3 + cos * this.e2_3
		this.tx = oldTx * cos - sin * this.ty
		this.ty = cos * this.ty + sin * oldTx
	}

	@OriginalMember(owner = "client!gq", name = "d", descriptor = "(I)V")
	override fun rotateAxisX(@OriginalArg(0) angle: Int) {
		@Pc(9) val cos: Float = GroundDecorOption.COSINE[angle and 0x3FFF]
		@Pc(15) val sin: Float = GroundDecorOption.SINE[angle and 0x3FFF]
		@Pc(18) val e2_1: Float = this.e2_1
		@Pc(21) val e2_2: Float = this.e2_2
		@Pc(24) val e2_3: Float = this.e2_3
		this.e2_1 = e2_1 * cos - this.e3_1 * sin
		@Pc(37) val oldTy: Float = this.ty
		this.e3_1 = sin * e2_1 + this.e3_1 * cos
		this.e2_2 = cos * e2_2 - this.e3_2 * sin
		this.e2_3 = cos * e2_3 - sin * this.e3_3
		this.e3_2 = this.e3_2 * cos + sin * e2_2
		this.ty = cos * oldTy - sin * this.tz
		this.e3_3 = cos * this.e3_3 + e2_3 * sin
		this.tz = oldTy * sin + cos * this.tz
	}

	@OriginalMember(owner = "client!gq", name = "a", descriptor = "(III[I)V")
	override fun projectRelative(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) destination: IntArray?) {
		@Pc(6) val dz: Int = (z.toFloat() - this.tz).toInt()
		@Pc(17) val dy: Int = (y.toFloat() - this.ty).toInt()
		@Pc(24) val dx: Int = (x.toFloat() - this.tx).toInt()
		destination!![0] = (this.e3_1 * dz.toFloat() + this.e2_1 * dy.toFloat() + this.e1_1 * dx.toFloat()).toInt()
		destination[1] = (dy.toFloat() * this.e2_2 + this.e1_2 * dx.toFloat() + dz.toFloat() * this.e3_2).toInt()
		destination[2] = (dz.toFloat() * this.e3_3 + dy.toFloat() * this.e2_3 + dx.toFloat() * this.e1_3).toInt()
	}

	@OriginalMember(owner = "client!gq", name = "c", descriptor = "(I)V")
	override fun makeRotationX(@OriginalArg(0) angle: Int) {
		this.e1_1 = 1.0f
		this.e3_3 = GroundDecorOption.COSINE[angle and 0x3FFF]
		this.e2_2 = this.e3_3
		this.e3_2 = GroundDecorOption.SINE[angle and 0x3FFF]
		this.e2_3 = -this.e3_2
		this.tz = 0.0f
		this.e3_1 = this.tz
		this.ty = this.e3_1
		this.e2_1 = this.ty
		this.tx = this.e2_1
		this.e1_3 = this.tx
		this.e1_2 = this.e1_3
	}

	@OriginalMember(owner = "client!gq", name = "e", descriptor = "(I)V")
	override fun makeRotationZ(@OriginalArg(0) angle: Int) {
		this.e3_3 = 1.0f
		this.e2_2 = GroundDecorOption.COSINE[angle and 0x3FFF]
		this.e1_1 = this.e2_2
		this.e2_1 = GroundDecorOption.SINE[angle and 0x3FFF]
		this.tz = 0.0f
		this.e3_2 = this.tz
		this.e3_1 = this.e3_2
		this.ty = this.e3_1
		this.e2_3 = this.ty
		this.tx = this.e2_3
		this.e1_3 = this.tx
		this.e1_2 = -this.e2_1
	}

	@OriginalMember(owner = "client!gq", name = "a", descriptor = "()Lclient!bca;")
	override fun copy(): Matrix? {
		@Pc(7) val javaMatrix: JavaMatrix = JavaMatrix()
		javaMatrix.e3_3 = this.e3_3
		javaMatrix.tz = this.tz
		javaMatrix.e3_1 = this.e3_1
		javaMatrix.e1_3 = this.e1_3
		javaMatrix.e2_1 = this.e2_1
		javaMatrix.e2_3 = this.e2_3
		javaMatrix.tx = this.tx
		javaMatrix.e2_2 = this.e2_2
		javaMatrix.e1_2 = this.e1_2
		javaMatrix.ty = this.ty
		javaMatrix.e1_1 = this.e1_1
		javaMatrix.e3_2 = this.e3_2
		return javaMatrix
	}

	@OriginalMember(owner = "client!gq", name = "a", descriptor = "(I)V")
	override fun rotate(@OriginalArg(0) angle: Int) {
		this.e2_2 = 1.0f
		this.e3_3 = GroundDecorOption.COSINE[angle and 0x3FFF]
		this.e1_1 = this.e3_3
		this.e1_3 = GroundDecorOption.SINE[angle and 0x3FFF]
		this.tz = 0.0f
		this.e3_2 = this.tz
		this.ty = this.e3_2
		this.e2_3 = this.ty
		this.e2_1 = this.e2_3
		this.tx = this.e2_1
		this.e1_2 = this.tx
		this.e3_1 = -this.e1_3
	}

	@OriginalMember(owner = "client!gq", name = "b", descriptor = "(I)V")
	override fun rotateAxisY(@OriginalArg(0) angle: Int) {
		@Pc(9) val cos: Float = GroundDecorOption.COSINE[angle and 0x3FFF]
		@Pc(15) val sin: Float = GroundDecorOption.SINE[angle and 0x3FFF]
		@Pc(18) val e1_1: Float = this.e1_1
		@Pc(21) val e1_2: Float = this.e1_2
		@Pc(24) val e1_3: Float = this.e1_3
		this.e1_1 = e1_1 * cos + sin * this.e3_1
		@Pc(37) val oldTx: Float = this.tx
		this.e1_2 = cos * e1_2 + sin * this.e3_2
		this.e3_1 = this.e3_1 * cos - sin * e1_1
		this.e1_3 = cos * e1_3 + sin * this.e3_3
		this.e3_2 = cos * this.e3_2 - sin * e1_2
		this.e3_3 = cos * this.e3_3 - sin * e1_3
		this.tx = oldTx * cos + sin * this.tz
		this.tz = cos * this.tz - sin * oldTx
	}

	@OriginalMember(owner = "client!gq", name = "a", descriptor = "([I)V")
	override fun project(@OriginalArg(0) destination: IntArray?) {
		@Pc(12) val dx: Float = destination!![0].toFloat() - this.tx
		@Pc(20) val dy: Float = destination[1].toFloat() - this.ty
		@Pc(28) val dz: Float = destination[2].toFloat() - this.tz
		destination[2] = (this.e1_3 * dx + dy * this.e2_3 + this.e3_3 * dz).toInt()
		destination[1] = (this.e1_2 * dx + dy * this.e2_2 + dz * this.e3_2).toInt()
		destination[0] = (this.e3_1 * dz + dx * this.e1_1 + this.e2_1 * dy).toInt()
	}

	@OriginalMember(owner = "client!gq", name = "b", descriptor = "(III[I)V")
	override fun method4652(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) destination: IntArray?) {
		destination!![1] = (x.toFloat() * this.e2_1 + this.e2_2 * y.toFloat() + z.toFloat() * this.e2_3 + this.ty).toInt()
		destination[0] = (z.toFloat() * this.e1_3 + this.e1_2 * y.toFloat() + x.toFloat() * this.e1_1 + this.tx).toInt()
		destination[2] = (this.tz + this.e3_2 * y.toFloat() + x.toFloat() * this.e3_1 + z.toFloat() * this.e3_3).toInt()
	}
}
