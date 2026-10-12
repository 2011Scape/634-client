package com.jagex.game.runetek5.graphics

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!bca")
abstract class Matrix {

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "([I)V")
	abstract fun project(@OriginalArg(0) destination: IntArray?)

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "(III)V")
	abstract fun translate(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int)

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "(III[I)V")
	abstract fun projectRelative(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) destination: IntArray?)

	@OriginalMember(owner = "client!bca", name = "b", descriptor = "(III)V")
	abstract fun applyTranslation(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int)

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "(I)V")
	abstract fun rotate(@OriginalArg(0) angle: Int)

	@OriginalMember(owner = "client!bca", name = "b", descriptor = "(I)V")
	abstract fun rotateAxisY(@OriginalArg(0) angle: Int)

	/** Transforms the point x, y, z by this matrix into destination[0..2]. */
	@OriginalMember(owner = "client!bca", name = "b", descriptor = "(III[I)V")
	abstract fun method4652(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) destination: IntArray?)

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "(Lclient!bca;)V")
	abstract fun apply(@OriginalArg(0) other: Matrix?)

	@OriginalMember(owner = "client!bca", name = "c", descriptor = "(I)V")
	abstract fun makeRotationX(@OriginalArg(0) angle: Int)

	@OriginalMember(owner = "client!bca", name = "d", descriptor = "(I)V")
	abstract fun rotateAxisX(@OriginalArg(0) angle: Int)

	@OriginalMember(owner = "client!bca", name = "e", descriptor = "(I)V")
	abstract fun makeRotationZ(@OriginalArg(0) angle: Int)

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "(IIIIII)V")
	abstract fun createCamera(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) rotateX: Int, @OriginalArg(4) rotateY: Int, @OriginalArg(5) rotateZ: Int)

	@OriginalMember(owner = "client!bca", name = "c", descriptor = "(III[I)V")
	abstract fun projectDirection(@OriginalArg(0) x: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(3) destination: IntArray?)

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "()Lclient!bca;")
	abstract fun copy(): Matrix?

	@OriginalMember(owner = "client!bca", name = "g", descriptor = "(I)V")
	abstract fun rotateAxisZ(@OriginalArg(0) angle: Int)

	@OriginalMember(owner = "client!bca", name = "b", descriptor = "()V")
	abstract fun makeIdentity()
}
