package com.jagex.game.runetek5.graphics;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!bca")
public abstract class Matrix {

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "([I)V")
	public abstract void project(@OriginalArg(0) int[] destination);

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "(III)V")
	public abstract void translate(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z);

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "(III[I)V")
	public abstract void projectRelative(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] destination);

	@OriginalMember(owner = "client!bca", name = "b", descriptor = "(III)V")
	public abstract void applyTranslation(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z);

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "(I)V")
	public abstract void rotate(@OriginalArg(0) int angle);

	@OriginalMember(owner = "client!bca", name = "b", descriptor = "(I)V")
	public abstract void rotateAxisY(@OriginalArg(0) int angle);

	/** Transforms the point x, y, z by this matrix into destination[0..2]. */
	@OriginalMember(owner = "client!bca", name = "b", descriptor = "(III[I)V")
	public abstract void method4652(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] destination);

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "(Lclient!bca;)V")
	public abstract void apply(@OriginalArg(0) Matrix other);

	@OriginalMember(owner = "client!bca", name = "c", descriptor = "(I)V")
	public abstract void makeRotationX(@OriginalArg(0) int angle);

	@OriginalMember(owner = "client!bca", name = "d", descriptor = "(I)V")
	public abstract void rotateAxisX(@OriginalArg(0) int angle);

	@OriginalMember(owner = "client!bca", name = "e", descriptor = "(I)V")
	public abstract void makeRotationZ(@OriginalArg(0) int angle);

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "(IIIIII)V")
	public abstract void createCamera(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int rotateX, @OriginalArg(4) int rotateY, @OriginalArg(5) int rotateZ);

	@OriginalMember(owner = "client!bca", name = "c", descriptor = "(III[I)V")
	public abstract void projectDirection(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] destination);

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "()Lclient!bca;")
	public abstract Matrix copy();

	@OriginalMember(owner = "client!bca", name = "g", descriptor = "(I)V")
	public abstract void rotateAxisZ(@OriginalArg(0) int angle);

	@OriginalMember(owner = "client!bca", name = "b", descriptor = "()V")
	public abstract void makeIdentity();
}
