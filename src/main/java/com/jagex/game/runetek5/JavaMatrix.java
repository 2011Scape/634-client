package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.Matrix;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.preference.GroundDecorOption;

@OriginalClass("client!gq")
public final class JavaMatrix extends Matrix {

	@OriginalMember(owner = "client!gq", name = "h", descriptor = "F")
	public float e2_1;

	@OriginalMember(owner = "client!gq", name = "o", descriptor = "F")
	public float e3_1;

	@OriginalMember(owner = "client!gq", name = "q", descriptor = "F")
	public float e3_3;

	@OriginalMember(owner = "client!gq", name = "s", descriptor = "F")
	public float e2_3;

	@OriginalMember(owner = "client!gq", name = "v", descriptor = "F")
	public float e1_3;

	@OriginalMember(owner = "client!gq", name = "y", descriptor = "F")
	public float e1_1;

	@OriginalMember(owner = "client!gq", name = "z", descriptor = "F")
	public float e1_2;

	@OriginalMember(owner = "client!gq", name = "E", descriptor = "F")
	public float e2_2;

	@OriginalMember(owner = "client!gq", name = "G", descriptor = "F")
	public float e3_2;

	@OriginalMember(owner = "client!gq", name = "H", descriptor = "F")
	public float tz;

	@OriginalMember(owner = "client!gq", name = "L", descriptor = "F")
	public float ty;

	@OriginalMember(owner = "client!gq", name = "M", descriptor = "F")
	public float tx;

	@OriginalMember(owner = "client!gq", name = "<init>", descriptor = "()V")
	public JavaMatrix() {
		this.makeIdentity();
	}

	@OriginalMember(owner = "client!gq", name = "b", descriptor = "()V")
	@Override
	public void makeIdentity() {
		this.e1_1 = this.e2_2 = this.e3_3 = 1.0F;
		this.e2_1 = this.e3_1 = this.e1_2 = this.e3_2 = this.e1_3 = this.e2_3 = this.tx = this.ty = this.tz = 0.0F;
	}

	@OriginalMember(owner = "client!gq", name = "b", descriptor = "(III)V")
	@Override
	public void applyTranslation(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z) {
		this.ty = y;
		this.e2_1 = this.e3_1 = this.e1_2 = this.e3_2 = this.e1_3 = this.e2_3 = 0.0F;
		this.e1_1 = this.e2_2 = this.e3_3 = 1.0F;
		this.tx = x;
		this.tz = z;
	}

	@OriginalMember(owner = "client!gq", name = "a", descriptor = "(IIIIII)V")
	@Override
	public void createCamera(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int rotateX, @OriginalArg(4) int rotateY, @OriginalArg(5) int rotateZ) {
		@Pc(9) float cosX = GroundDecorOption.COSINE[rotateX & 0x3FFF];
		@Pc(15) float sinX = GroundDecorOption.SINE[rotateX & 0x3FFF];
		@Pc(21) float cosY = GroundDecorOption.COSINE[rotateY & 0x3FFF];
		@Pc(27) float sinY = GroundDecorOption.SINE[rotateY & 0x3FFF];
		@Pc(33) float cosZ = GroundDecorOption.COSINE[rotateZ & 0x3FFF];
		@Pc(39) float sinZ = GroundDecorOption.SINE[rotateZ & 0x3FFF];
		@Pc(43) float cosZSinX = cosZ * sinX;
		@Pc(47) float sinXSinZ = sinX * sinZ;
		this.e2_3 = sinZ * sinY + cosY * cosZSinX;
		this.e1_3 = cosZ * -sinY + sinXSinZ * cosY;
		this.e3_2 = -sinX;
		this.e2_2 = cosX * cosZ;
		this.e3_3 = cosY * cosX;
		this.e3_1 = cosX * sinY;
		this.e2_1 = cosZSinX * sinY + -cosY * sinZ;
		this.e1_2 = cosX * sinZ;
		this.e1_1 = sinXSinZ * sinY + cosZ * cosY;
		this.tz = -((float) z * this.e3_3) - (float) y * this.e3_2 + (float) -x * this.e3_1;
		this.tx = this.e1_1 * (float) -x - (float) y * this.e1_2 - this.e1_3 * (float) z;
		this.ty = -(this.e2_3 * (float) z) + this.e2_1 * (float) -x - (float) y * this.e2_2;
	}

	@OriginalMember(owner = "client!gq", name = "a", descriptor = "(Lclient!bca;)V")
	@Override
	public void apply(@OriginalArg(0) Matrix other) {
		@Pc(6) JavaMatrix javaMatrix = (JavaMatrix) other;
		this.e1_3 = javaMatrix.e1_3;
		this.tz = javaMatrix.tz;
		this.e2_1 = javaMatrix.e2_1;
		this.e3_3 = javaMatrix.e3_3;
		this.e2_3 = javaMatrix.e2_3;
		this.e2_2 = javaMatrix.e2_2;
		this.e3_2 = javaMatrix.e3_2;
		this.e1_1 = javaMatrix.e1_1;
		this.e3_1 = javaMatrix.e3_1;
		this.tx = javaMatrix.tx;
		this.e1_2 = javaMatrix.e1_2;
		this.ty = javaMatrix.ty;
	}

	@OriginalMember(owner = "client!gq", name = "c", descriptor = "(III[I)V")
	@Override
	public void projectDirection(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] destination) {
		destination[0] = (int) ((float) z * this.e1_3 + this.e1_2 * (float) y + this.e1_1 * (float) x);
		destination[1] = (int) (this.e2_1 * (float) x + (float) y * this.e2_2 + this.e2_3 * (float) z);
		destination[2] = (int) (this.e3_3 * (float) z + (float) x * this.e3_1 + (float) y * this.e3_2);
	}

	@OriginalMember(owner = "client!gq", name = "a", descriptor = "(III)V")
	@Override
	public void translate(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z) {
		this.ty += y;
		this.tz += z;
		this.tx += x;
	}

	@OriginalMember(owner = "client!gq", name = "g", descriptor = "(I)V")
	@Override
	public void rotateAxisZ(@OriginalArg(0) int angle) {
		@Pc(9) float cos = GroundDecorOption.COSINE[angle & 0x3FFF];
		@Pc(15) float sin = GroundDecorOption.SINE[angle & 0x3FFF];
		@Pc(18) float e1_1 = this.e1_1;
		@Pc(21) float e1_2 = this.e1_2;
		@Pc(24) float e1_3 = this.e1_3;
		this.e1_1 = cos * e1_1 - sin * this.e2_1;
		@Pc(38) float oldTx = this.tx;
		this.e1_2 = e1_2 * cos - this.e2_2 * sin;
		this.e2_1 = cos * this.e2_1 + e1_1 * sin;
		this.e1_3 = cos * e1_3 - this.e2_3 * sin;
		this.e2_2 = cos * this.e2_2 + sin * e1_2;
		this.e2_3 = sin * e1_3 + cos * this.e2_3;
		this.tx = oldTx * cos - sin * this.ty;
		this.ty = cos * this.ty + sin * oldTx;
	}

	@OriginalMember(owner = "client!gq", name = "d", descriptor = "(I)V")
	@Override
	public void rotateAxisX(@OriginalArg(0) int angle) {
		@Pc(9) float cos = GroundDecorOption.COSINE[angle & 0x3FFF];
		@Pc(15) float sin = GroundDecorOption.SINE[angle & 0x3FFF];
		@Pc(18) float e2_1 = this.e2_1;
		@Pc(21) float e2_2 = this.e2_2;
		@Pc(24) float e2_3 = this.e2_3;
		this.e2_1 = e2_1 * cos - this.e3_1 * sin;
		@Pc(37) float oldTy = this.ty;
		this.e3_1 = sin * e2_1 + this.e3_1 * cos;
		this.e2_2 = cos * e2_2 - this.e3_2 * sin;
		this.e2_3 = cos * e2_3 - sin * this.e3_3;
		this.e3_2 = this.e3_2 * cos + sin * e2_2;
		this.ty = cos * oldTy - sin * this.tz;
		this.e3_3 = cos * this.e3_3 + e2_3 * sin;
		this.tz = oldTy * sin + cos * this.tz;
	}

	@OriginalMember(owner = "client!gq", name = "a", descriptor = "(III[I)V")
	@Override
	public void projectRelative(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] destination) {
		@Pc(6) int dz = (int) ((float) z - this.tz);
		@Pc(17) int dy = (int) ((float) y - this.ty);
		@Pc(24) int dx = (int) ((float) x - this.tx);
		destination[0] = (int) (this.e3_1 * (float) dz + this.e2_1 * (float) dy + this.e1_1 * (float) dx);
		destination[1] = (int) ((float) dy * this.e2_2 + this.e1_2 * (float) dx + (float) dz * this.e3_2);
		destination[2] = (int) ((float) dz * this.e3_3 + (float) dy * this.e2_3 + (float) dx * this.e1_3);
	}

	@OriginalMember(owner = "client!gq", name = "c", descriptor = "(I)V")
	@Override
	public void makeRotationX(@OriginalArg(0) int angle) {
		this.e1_1 = 1.0F;
		this.e2_2 = this.e3_3 = GroundDecorOption.COSINE[angle & 0x3FFF];
		this.e3_2 = GroundDecorOption.SINE[angle & 0x3FFF];
		this.e2_3 = -this.e3_2;
		this.e1_2 = this.e1_3 = this.tx = this.e2_1 = this.ty = this.e3_1 = this.tz = 0.0F;
	}

	@OriginalMember(owner = "client!gq", name = "e", descriptor = "(I)V")
	@Override
	public void makeRotationZ(@OriginalArg(0) int angle) {
		this.e3_3 = 1.0F;
		this.e1_1 = this.e2_2 = GroundDecorOption.COSINE[angle & 0x3FFF];
		this.e2_1 = GroundDecorOption.SINE[angle & 0x3FFF];
		this.e1_3 = this.tx = this.e2_3 = this.ty = this.e3_1 = this.e3_2 = this.tz = 0.0F;
		this.e1_2 = -this.e2_1;
	}

	@OriginalMember(owner = "client!gq", name = "a", descriptor = "()Lclient!bca;")
	@Override
	public Matrix copy() {
		@Pc(7) JavaMatrix javaMatrix = new JavaMatrix();
		javaMatrix.e3_3 = this.e3_3;
		javaMatrix.tz = this.tz;
		javaMatrix.e3_1 = this.e3_1;
		javaMatrix.e1_3 = this.e1_3;
		javaMatrix.e2_1 = this.e2_1;
		javaMatrix.e2_3 = this.e2_3;
		javaMatrix.tx = this.tx;
		javaMatrix.e2_2 = this.e2_2;
		javaMatrix.e1_2 = this.e1_2;
		javaMatrix.ty = this.ty;
		javaMatrix.e1_1 = this.e1_1;
		javaMatrix.e3_2 = this.e3_2;
		return javaMatrix;
	}

	@OriginalMember(owner = "client!gq", name = "a", descriptor = "(I)V")
	@Override
	public void rotate(@OriginalArg(0) int angle) {
		this.e2_2 = 1.0F;
		this.e1_1 = this.e3_3 = GroundDecorOption.COSINE[angle & 0x3FFF];
		this.e1_3 = GroundDecorOption.SINE[angle & 0x3FFF];
		this.e1_2 = this.tx = this.e2_1 = this.e2_3 = this.ty = this.e3_2 = this.tz = 0.0F;
		this.e3_1 = -this.e1_3;
	}

	@OriginalMember(owner = "client!gq", name = "b", descriptor = "(I)V")
	@Override
	public void rotateAxisY(@OriginalArg(0) int angle) {
		@Pc(9) float cos = GroundDecorOption.COSINE[angle & 0x3FFF];
		@Pc(15) float sin = GroundDecorOption.SINE[angle & 0x3FFF];
		@Pc(18) float e1_1 = this.e1_1;
		@Pc(21) float e1_2 = this.e1_2;
		@Pc(24) float e1_3 = this.e1_3;
		this.e1_1 = e1_1 * cos + sin * this.e3_1;
		@Pc(37) float oldTx = this.tx;
		this.e1_2 = cos * e1_2 + sin * this.e3_2;
		this.e3_1 = this.e3_1 * cos - sin * e1_1;
		this.e1_3 = cos * e1_3 + sin * this.e3_3;
		this.e3_2 = cos * this.e3_2 - sin * e1_2;
		this.e3_3 = cos * this.e3_3 - sin * e1_3;
		this.tx = oldTx * cos + sin * this.tz;
		this.tz = cos * this.tz - sin * oldTx;
	}

	@OriginalMember(owner = "client!gq", name = "a", descriptor = "([I)V")
	@Override
	public void project(@OriginalArg(0) int[] destination) {
		@Pc(12) float dx = (float) destination[0] - this.tx;
		@Pc(20) float dy = (float) destination[1] - this.ty;
		@Pc(28) float dz = (float) destination[2] - this.tz;
		destination[2] = (int) (this.e1_3 * dx + dy * this.e2_3 + this.e3_3 * dz);
		destination[1] = (int) (this.e1_2 * dx + dy * this.e2_2 + dz * this.e3_2);
		destination[0] = (int) (this.e3_1 * dz + dx * this.e1_1 + this.e2_1 * dy);
	}

	@OriginalMember(owner = "client!gq", name = "b", descriptor = "(III[I)V")
	@Override
	public void method4652(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] destination) {
		destination[1] = (int) ((float) x * this.e2_1 + this.e2_2 * (float) y + (float) z * this.e2_3 + this.ty);
		destination[0] = (int) ((float) z * this.e1_3 + this.e1_2 * (float) y + (float) x * this.e1_1 + this.tx);
		destination[2] = (int) (this.tz + this.e3_2 * (float) y + (float) x * this.e3_1 + (float) z * this.e3_3);
	}
}
