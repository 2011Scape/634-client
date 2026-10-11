package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.Matrix;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.preference.GroundDecorOption;

@OriginalClass("client!haa")
public final class NativeMatrix extends Matrix {

	@OriginalMember(owner = "client!haa", name = "i", descriptor = "F")
	public float zB;

	@OriginalMember(owner = "client!haa", name = "r", descriptor = "F")
	public float xB;

	@OriginalMember(owner = "client!haa", name = "v", descriptor = "F")
	public float xC;

	@OriginalMember(owner = "client!haa", name = "z", descriptor = "F")
	public float yA;

	@OriginalMember(owner = "client!haa", name = "B", descriptor = "F")
	public float yD;

	@OriginalMember(owner = "client!haa", name = "C", descriptor = "F")
	public float xA;

	@OriginalMember(owner = "client!haa", name = "H", descriptor = "F")
	public float zC;

	@OriginalMember(owner = "client!haa", name = "N", descriptor = "F")
	public float yB;

	@OriginalMember(owner = "client!haa", name = "P", descriptor = "F")
	public float zD;

	@OriginalMember(owner = "client!haa", name = "U", descriptor = "F")
	public float xD;

	@OriginalMember(owner = "client!haa", name = "X", descriptor = "F")
	public float yC;

	@OriginalMember(owner = "client!haa", name = "bb", descriptor = "F")
	public float zA;

	@OriginalMember(owner = "client!haa", name = "<init>", descriptor = "()V")
	public NativeMatrix() {
		this.makeIdentity();
	}

	@OriginalMember(owner = "client!haa", name = "e", descriptor = "(I)V")
	@Override
	public void makeRotationZ(@OriginalArg(0) int angle) {
		this.zC = 1.0F;
		this.xA = this.yB = GroundDecorOption.COSINE[angle & 0x3FFF];
		this.yA = GroundDecorOption.SINE[angle & 0x3FFF];
		this.xB = -this.yA;
		this.xC = this.xD = this.yC = this.yD = this.zA = this.zB = this.zD = 0.0F;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(FFIF)V")
	public void scale(@OriginalArg(0) float scaleY, @OriginalArg(1) float scaleZ, @OriginalArg(3) float scaleX) {
		this.zD *= scaleZ;
		this.xD *= scaleX;
		this.yC *= scaleY;
		this.yA *= scaleY;
		this.xC *= scaleX;
		this.yB *= scaleY;
		this.yD *= scaleY;
		this.zC *= scaleZ;
		this.zA *= scaleZ;
		this.xA *= scaleX;
		this.zB *= scaleZ;
		this.xB *= scaleX;
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(III)V")
	@Override
	public void applyTranslation(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z) {
		this.zD = z;
		this.xA = this.yB = this.zC = 1.0F;
		this.yD = y;
		this.xD = x;
		this.yA = this.zA = this.xB = this.zB = this.xC = this.yC = 0.0F;
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "()V")
	@Override
	public void makeIdentity() {
		this.xA = this.yB = this.zC = 1.0F;
		this.yA = this.zA = this.xB = this.zB = this.xC = this.yC = this.xD = this.yD = this.zD = 0.0F;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "([FI)[F")
	public float[] copyXYInto(@OriginalArg(0) float[] dest) {
		dest[7] = this.yD;
		dest[5] = this.yB;
		dest[4] = this.yA;
		dest[6] = this.yC;
		dest[1] = this.xB;
		dest[2] = this.xC;
		dest[3] = this.xD;
		dest[0] = this.xA;
		return dest;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(IIIIII)V")
	@Override
	public void createCamera(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int rotateX, @OriginalArg(4) int rotateY, @OriginalArg(5) int rotateZ) {
		@Pc(9) float cosX = GroundDecorOption.COSINE[rotateX & 0x3FFF];
		@Pc(15) float sinX = GroundDecorOption.SINE[rotateX & 0x3FFF];
		@Pc(21) float cosY = GroundDecorOption.COSINE[rotateY & 0x3FFF];
		@Pc(27) float sinY = GroundDecorOption.SINE[rotateY & 0x3FFF];
		@Pc(33) float cosZ = GroundDecorOption.COSINE[rotateZ & 0x3FFF];
		@Pc(39) float sinZ = GroundDecorOption.SINE[rotateZ & 0x3FFF];
		@Pc(43) float sinXCosZ = cosZ * sinX;
		@Pc(47) float sinXSinZ = sinX * sinZ;
		this.xB = sinZ * cosX;
		this.xC = -sinY * cosZ + cosY * sinXSinZ;
		this.zC = cosX * cosY;
		this.yC = sinZ * sinY + sinXCosZ * cosY;
		this.xA = cosY * cosZ + sinXSinZ * sinY;
		this.zA = cosX * sinY;
		this.yB = cosZ * cosX;
		this.zB = -sinX;
		this.yA = sinY * sinXCosZ + sinZ * -cosY;
		this.xD = -(this.xC * (float) z) - (float) y * this.xB + this.xA * (float) -x;
		this.yD = (float) -x * this.yA - this.yB * (float) y - (float) z * this.yC;
		this.zD = -(this.zC * (float) z) + this.zA * (float) -x - this.zB * (float) y;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(I)V")
	@Override
	public void rotate(@OriginalArg(0) int angle) {
		this.yB = 1.0F;
		this.xA = this.zC = GroundDecorOption.COSINE[angle & 0x3FFF];
		this.xC = GroundDecorOption.SINE[angle & 0x3FFF];
		this.zA = -this.xC;
		this.xB = this.xD = this.yA = this.yC = this.yD = this.zB = this.zD = 0.0F;
	}

	@OriginalMember(owner = "client!haa", name = "c", descriptor = "(I)V")
	@Override
	public void makeRotationX(@OriginalArg(0) int angle) {
		this.xA = 1.0F;
		this.yB = this.zC = GroundDecorOption.COSINE[angle & 0x3FFF];
		this.zB = GroundDecorOption.SINE[angle & 0x3FFF];
		this.xB = this.xC = this.xD = this.yA = this.yD = this.zA = this.zD = 0.0F;
		this.yC = -this.zB;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(III[I)V")
	@Override
	public void projectRelative(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] destination) {
		@Pc(10) int dz = (int) ((float) z - this.zD);
		@Pc(17) int dx = (int) ((float) x - this.xD);
		@Pc(24) int dy = (int) ((float) y - this.yD);
		destination[2] = (int) ((float) dz * this.zC + (float) dy * this.yC + (float) dx * this.xC);
		destination[0] = (int) ((float) dx * this.xA + (float) dy * this.yA + this.zA * (float) dz);
		destination[1] = (int) (this.xB * (float) dx + this.yB * (float) dy + this.zB * (float) dz);
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(FZFF)V")
	public void makeTranslation(@OriginalArg(0) float y, @OriginalArg(2) float z, @OriginalArg(3) float x) {
		this.yD = y;
		this.zD = z;
		this.xA = this.yB = this.zC = 1.0F;
		this.xD = x;
		this.yA = this.zA = this.xB = this.zB = this.xC = this.yC = 0.0F;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(III)V")
	@Override
	public void translate(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z) {
		this.xD += x;
		this.yD += y;
		this.zD += z;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(Lclient!bca;B)V")
	public void multiply(@OriginalArg(0) Matrix other) {
		@Pc(6) NativeMatrix matrix = (NativeMatrix) other;
		@Pc(9) float oldXA = this.xA;
		@Pc(12) float oldYA = this.yA;
		@Pc(15) float oldXB = this.xB;
		@Pc(18) float oldYB = this.yB;
		@Pc(21) float oldXC = this.xC;
		@Pc(24) float oldYC = this.yC;
		@Pc(33) float oldXD = this.xD;
		this.yA = oldXA * matrix.yA + matrix.yB * oldYA + this.zA * matrix.yC;
		this.xA = matrix.xC * this.zA + matrix.xA * oldXA + oldYA * matrix.xB;
		@Pc(70) float oldYD = this.yD;
		this.yB = matrix.yC * this.zB + oldXB * matrix.yA + matrix.yB * oldYB;
		this.xB = this.zB * matrix.xC + matrix.xA * oldXB + oldYB * matrix.xB;
		this.zA = oldYA * matrix.zB + matrix.zA * oldXA + this.zA * matrix.zC;
		this.zB = this.zB * matrix.zC + oldYB * matrix.zB + matrix.zA * oldXB;
		this.yC = matrix.yB * oldYC + matrix.yA * oldXC + this.zC * matrix.yC;
		this.xC = this.zC * matrix.xC + matrix.xA * oldXC + oldYC * matrix.xB;
		this.xD = matrix.xD + oldXD * matrix.xA + matrix.xB * oldYD + matrix.xC * this.zD;
		this.yD = matrix.yA * oldXD + oldYD * matrix.yB + this.zD * matrix.yC + matrix.yD;
		this.zC = this.zC * matrix.zC + matrix.zA * oldXC + matrix.zB * oldYC;
		this.zD = matrix.zA * oldXD + matrix.zB * oldYD + this.zD * matrix.zC + matrix.zD;
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "([FI)[F")
	public float[] copyColumnMajorInto(@OriginalArg(0) float[] dest) {
		dest[7] = 0.0F;
		dest[13] = this.yD;
		dest[14] = this.zD;
		dest[12] = this.xD;
		dest[11] = 0.0F;
		dest[9] = this.yC;
		dest[8] = this.xC;
		dest[4] = this.xB;
		dest[0] = this.xA;
		dest[10] = this.zC;
		dest[5] = this.yB;
		dest[15] = 1.0F;
		dest[1] = this.yA;
		dest[3] = 0;
		dest[2] = this.zA;
		dest[6] = this.zB;
		return dest;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(F[FFFFB)V")
	public void transformPlane(@OriginalArg(0) float normalZ, @OriginalArg(1) float[] dest, @OriginalArg(2) float normalY, @OriginalArg(3) float normalX, @OriginalArg(4) float distance) {
		dest[0] = normalY * this.xB + normalX * this.xA + this.xC * normalZ;
		dest[1] = this.yA * normalX + normalY * this.yB + this.yC * normalZ;
		@Pc(74) float pointX;
		@Pc(82) float pointY;
		@Pc(66) float pointZ;
		@Pc(58) float axisOffset;
		if (normalX > 0.00390625F || normalX < -0.00390625F) {
			axisOffset = -distance / normalX;
			pointZ = this.zD + axisOffset * this.zA;
			pointX = this.xD + axisOffset * this.xA;
			pointY = axisOffset * this.yA + this.yD;
		} else if (normalY > 0.00390625F || normalY < -0.00390625F) {
			axisOffset = -distance / normalY;
			pointY = this.yD + axisOffset * this.yB;
			pointZ = this.zD + this.zB * axisOffset;
			pointX = this.xD + this.xB * axisOffset;
		} else {
			axisOffset = -distance / normalZ;
			pointX = this.xC * axisOffset + this.xD;
			pointY = this.yC * axisOffset + this.yD;
			pointZ = this.zD + axisOffset * this.zC;
		}
		dest[2] = normalZ * this.zC + this.zB * normalY + normalX * this.zA;
		dest[3] = -(dest[2] * pointZ + dest[0] * pointX + dest[1] * pointY);
	}

	@OriginalMember(owner = "client!haa", name = "d", descriptor = "(I)V")
	@Override
	public void rotateAxisX(@OriginalArg(0) int angle) {
		@Pc(9) float cos = GroundDecorOption.COSINE[angle & 0x3FFF];
		@Pc(15) float sin = GroundDecorOption.SINE[angle & 0x3FFF];
		@Pc(18) float oldYA = this.yA;
		@Pc(21) float oldYB = this.yB;
		@Pc(24) float oldYC = this.yC;
		this.yA = oldYA * cos - sin * this.zA;
		@Pc(37) float oldYD = this.yD;
		this.yB = oldYB * cos - sin * this.zB;
		this.zA = cos * this.zA + sin * oldYA;
		this.zB = cos * this.zB + sin * oldYB;
		this.yC = oldYC * cos - this.zC * sin;
		this.yD = cos * oldYD - sin * this.zD;
		this.zC = cos * this.zC + oldYC * sin;
		this.zD = sin * oldYD + cos * this.zD;
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(FZFF)V")
	public void translate(@OriginalArg(0) float y, @OriginalArg(2) float z, @OriginalArg(3) float x) {
		this.zD += z;
		this.xD += x;
		this.yD += y;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(BFFF)F")
	public float transformY(@OriginalArg(1) float x, @OriginalArg(2) float z, @OriginalArg(3) float y) {
		return this.yB * y + x * this.yA + z * this.yC + this.yD;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(FFBF[F)V")
	public void transformDirection(@OriginalArg(0) float x, @OriginalArg(1) float y, @OriginalArg(3) float z, @OriginalArg(4) float[] dest) {
		dest[2] = z * this.zC + this.zB * y + this.zA * x;
		dest[1] = this.yC * z + y * this.yB + x * this.yA;
		dest[0] = z * this.xC + this.xA * x + y * this.xB;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(I[F)[F")
	public float[] copyRowMajorInto(@OriginalArg(1) float[] dest) {
		dest[2] = this.xC;
		dest[0] = this.xA;
		dest[6] = this.yC;
		dest[13] = 0.0F;
		dest[3] = this.xD;
		dest[7] = this.yD;
		dest[12] = 0.0F;
		dest[14] = 0.0F;
		dest[10] = this.zC;
		dest[8] = this.zA;
		dest[1] = this.xB;
		dest[5] = this.yB;
		dest[9] = this.zB;
		dest[15] = 1.0F;
		dest[4] = this.yA;
		dest[11] = this.zD;
		return dest;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "()Lclient!bca;")
	@Override
	public Matrix copy() {
		@Pc(7) NativeMatrix matrix = new NativeMatrix();
		matrix.zB = this.zB;
		matrix.zD = this.zD;
		matrix.xB = this.xB;
		matrix.xD = this.xD;
		matrix.zA = this.zA;
		matrix.yD = this.yD;
		matrix.yB = this.yB;
		matrix.xC = this.xC;
		matrix.yA = this.yA;
		matrix.zC = this.zC;
		matrix.xA = this.xA;
		matrix.yC = this.yC;
		return matrix;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(ILclient!bca;)V")
	public void copyRotation(@OriginalArg(1) Matrix source) {
		@Pc(12) NativeMatrix matrix = (NativeMatrix) source;
		this.xA = matrix.xA;
		this.yD = 0.0F;
		this.yB = matrix.yB;
		this.zA = matrix.zA;
		this.zB = matrix.zB;
		this.zC = matrix.zC;
		this.xB = matrix.xB;
		this.yA = matrix.yA;
		this.yC = matrix.yC;
		this.zD = 0.0F;
		this.xD = 0.0F;
		this.xC = matrix.xC;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(Lclient!bca;Lclient!bca;)V")
	public void multiply(@OriginalArg(0) Matrix matrix, @OriginalArg(1) Matrix matrix2) {
		@Pc(6) NativeMatrix left = (NativeMatrix) matrix;
		@Pc(9) NativeMatrix right = (NativeMatrix) matrix2;
		this.xA = left.xC * right.zA + left.xA * right.xA + right.yA * left.xB;
		this.yA = left.yB * right.yA + right.xA * left.yA + left.yC * right.zA;
		this.zA = right.zA * left.zC + left.zA * right.xA + right.yA * left.zB;
		this.xB = right.zB * left.xC + right.yB * left.xB + right.xB * left.xA;
		this.yB = left.yA * right.xB + right.yB * left.yB + left.yC * right.zB;
		this.xC = left.xB * right.yC + left.xA * right.xC + left.xC * right.zC;
		this.zB = left.zC * right.zB + left.zA * right.xB + left.zB * right.yB;
		this.yC = left.yA * right.xC + left.yB * right.yC + right.zC * left.yC;
		this.xD = left.xD + right.zD * left.xC + right.yD * left.xB + left.xA * right.xD;
		this.zC = left.zB * right.yC + left.zA * right.xC + left.zC * right.zC;
		this.yD = left.yD + left.yC * right.zD + right.yD * left.yB + left.yA * right.xD;
		this.zD = left.zB * right.yD + left.zA * right.xD + right.zD * left.zC + left.zD;
	}

	@OriginalMember(owner = "client!haa", name = "c", descriptor = "([FI)[F")
	public float[] copy2DColumnMajorInto(@OriginalArg(0) float[] dest) {
		dest[13] = 0.0F;
		dest[1] = this.yA;
		dest[5] = this.yB;
		dest[6] = 0.0F;
		dest[3] = 0.0F;
		dest[15] = 1.0F;
		dest[4] = this.xB;
		dest[14] = 0.0F;
		dest[10] = this.zD;
		dest[2] = 0.0F;
		dest[0] = this.xA;
		dest[7] = 0.0F;
		dest[11] = 0.0F;
		dest[12] = 0.0F;
		dest[8] = this.xD;
		dest[9] = this.yD;
		return dest;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(FFBF)F")
	public float transformX(@OriginalArg(0) float z, @OriginalArg(1) float y, @OriginalArg(3) float x) {
		return z * this.xC + y * this.xB + x * this.xA + this.xD;
	}

	@OriginalMember(owner = "client!haa", name = "g", descriptor = "(I)V")
	@Override
	public void rotateAxisZ(@OriginalArg(0) int angle) {
		@Pc(9) float cos = GroundDecorOption.COSINE[angle & 0x3FFF];
		@Pc(15) float sin = GroundDecorOption.SINE[angle & 0x3FFF];
		@Pc(18) float oldXA = this.xA;
		@Pc(21) float oldXB = this.xB;
		@Pc(24) float oldXC = this.xC;
		@Pc(27) float oldXD = this.xD;
		this.xA = oldXA * cos - this.yA * sin;
		this.yA = cos * this.yA + oldXA * sin;
		this.xB = oldXB * cos - this.yB * sin;
		this.yB = this.yB * cos + oldXB * sin;
		this.xC = oldXC * cos - sin * this.yC;
		this.xD = cos * oldXD - sin * this.yD;
		this.yC = sin * oldXC + cos * this.yC;
		this.yD = sin * oldXD + this.yD * cos;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "([I)V")
	@Override
	public void project(@OriginalArg(0) int[] destination) {
		@Pc(12) float dx = (float) destination[0] - this.xD;
		@Pc(20) float dy = (float) destination[1] - this.yD;
		@Pc(28) float dz = (float) destination[2] - this.zD;
		destination[2] = (int) (dz * this.zC + this.xC * dx + dy * this.yC);
		destination[1] = (int) (this.zB * dz + this.yB * dy + dx * this.xB);
		destination[0] = (int) (this.yA * dy + this.xA * dx + dz * this.zA);
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(III[I)V")
	@Override
	public void method4652(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] destination) {
		destination[2] = (int) ((float) y * this.zB + this.zA * (float) x + this.zC * (float) z + this.zD);
		destination[1] = (int) (this.yC * (float) z + this.yB * (float) y + (float) x * this.yA + this.yD);
		destination[0] = (int) (this.xD + this.xC * (float) z + (float) x * this.xA + this.xB * (float) y);
	}

	@OriginalMember(owner = "client!haa", name = "c", descriptor = "(III[I)V")
	@Override
	public void projectDirection(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] destination) {
		destination[0] = (int) ((float) x * this.xA + this.xB * (float) y + (float) z * this.xC);
		destination[1] = (int) ((float) y * this.yB + this.yA * (float) x + this.yC * (float) z);
		destination[2] = (int) ((float) x * this.zA + this.zB * (float) y + (float) z * this.zC);
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(I[F)[F")
	public float[] copyRotationRowMajorInto(@OriginalArg(1) float[] dest) {
		dest[7] = 0.0F;
		dest[14] = 0.0F;
		dest[8] = this.zA;
		dest[6] = this.yC;
		dest[4] = this.yA;
		dest[2] = this.xC;
		dest[11] = 0.0F;
		dest[15] = 0.0F;
		dest[9] = this.zB;
		dest[12] = 0.0F;
		dest[13] = 0;
		dest[3] = 0.0F;
		dest[1] = this.xB;
		dest[0] = this.xA;
		dest[10] = this.zC;
		dest[5] = this.yB;
		return dest;
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(ILclient!bca;)V")
	public void setInverse(@OriginalArg(1) Matrix source) {
		@Pc(6) NativeMatrix matrix = (NativeMatrix) source;
		this.xB = matrix.yA;
		this.xC = matrix.zA;
		this.xA = matrix.xA;
		this.yA = matrix.xB;
		this.yB = matrix.yB;
		this.zA = matrix.xC;
		this.yC = matrix.zB;
		this.zB = matrix.yC;
		this.xD = -(this.xC * matrix.zD + matrix.yD * this.xB + matrix.xD * this.xA);
		this.zC = matrix.zC;
		this.yD = -(matrix.zD * this.yC + this.yA * matrix.xD + matrix.yD * this.yB);
		this.zD = -(matrix.yD * this.zB + this.zA * matrix.xD + this.zC * matrix.zD);
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(I)V")
	@Override
	public void rotateAxisY(@OriginalArg(0) int angle) {
		@Pc(9) float cos = GroundDecorOption.COSINE[angle & 0x3FFF];
		@Pc(15) float sin = GroundDecorOption.SINE[angle & 0x3FFF];
		@Pc(18) float oldXA = this.xA;
		@Pc(21) float oldXB = this.xB;
		@Pc(24) float oldXC = this.xC;
		this.xA = cos * oldXA + this.zA * sin;
		@Pc(37) float oldXD = this.xD;
		this.zA = this.zA * cos - sin * oldXA;
		this.xB = cos * oldXB + sin * this.zB;
		this.zB = this.zB * cos - sin * oldXB;
		this.xC = cos * oldXC + this.zC * sin;
		this.zC = this.zC * cos - oldXC * sin;
		this.xD = oldXD * cos + sin * this.zD;
		this.zD = cos * this.zD - oldXD * sin;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(FFFB)V")
	public void setScale(@OriginalArg(0) float scaleZ, @OriginalArg(1) float scaleY, @OriginalArg(2) float scaleX) {
		this.yB = scaleY;
		this.xC = 0.0F;
		this.xB = 0.0F;
		this.xD = 0.0F;
		this.zC = scaleZ;
		this.zD = 0.0F;
		this.yC = 0.0F;
		this.yD = 0.0F;
		this.zA = 0.0F;
		this.yA = 0.0F;
		this.zB = 0.0F;
		this.xA = scaleX;
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(BFFF)F")
	public float transformZ(@OriginalArg(1) float x, @OriginalArg(2) float z, @OriginalArg(3) float y) {
		return this.zD + this.zA * x + y * this.zB + this.zC * z;
	}

	@OriginalMember(owner = "client!haa", name = "b", descriptor = "(B)V")
	public void negateYZ() {
		this.yD = -this.yD;
		this.zB = -this.zB;
		this.zA = -this.zA;
		this.yC = -this.yC;
		this.zD = -this.zD;
		this.zC = -this.zC;
		this.yA = -this.yA;
		this.yB = -this.yB;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(Lclient!bca;)V")
	@Override
	public void apply(@OriginalArg(0) Matrix other) {
		@Pc(6) NativeMatrix matrix = (NativeMatrix) other;
		this.zD = matrix.zD;
		this.zA = matrix.zA;
		this.xD = matrix.xD;
		this.xB = matrix.xB;
		this.zB = matrix.zB;
		this.xA = matrix.xA;
		this.xC = matrix.xC;
		this.yC = matrix.yC;
		this.zC = matrix.zC;
		this.yB = matrix.yB;
		this.yD = matrix.yD;
		this.yA = matrix.yA;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(FFFFFFFFFI)V")
	public void setRotation(@OriginalArg(0) float zc, @OriginalArg(1) float ya, @OriginalArg(2) float xb, @OriginalArg(3) float za, @OriginalArg(4) float zb, @OriginalArg(5) float yb, @OriginalArg(6) float xc, @OriginalArg(7) float yc, @OriginalArg(8) float xa) {
		this.yA = ya;
		this.zC = zc;
		this.xB = xb;
		this.zD = 0.0F;
		this.yC = yc;
		this.xD = 0.0F;
		this.yB = yb;
		this.xC = xc;
		this.yD = 0;
		this.zB = zb;
		this.zA = za;
		this.xA = xa;
	}

	@OriginalMember(owner = "client!haa", name = "a", descriptor = "(IIIFFFI)V")
	public void setTextureCoordTransform(@OriginalArg(0) int rotation, @OriginalArg(2) int scaleV, @OriginalArg(3) float translateX, @OriginalArg(4) float translateZ, @OriginalArg(5) float translateY, @OriginalArg(6) int scaleU) {
		if (rotation == 0) {
			this.yA = this.zA = this.xB = this.zB = this.xC = this.yC = 0.0F;
			this.xD = translateX - (float) scaleU;
			this.zC = 1.0F;
			this.yD = (float) -scaleV + translateY;
			this.xA = scaleU * 2;
			this.yB = scaleV * 2;
			this.zD = translateZ;
			return;
		}
		@Pc(12) float cosAngle = GroundDecorOption.COSINE[rotation & 0x3FFF];
		@Pc(18) float sinAngle = GroundDecorOption.SINE[rotation & 0x3FFF];
		this.xA = cosAngle * 2.0F * (float) scaleU;
		this.yD = (-0.5F * sinAngle - cosAngle * 0.5F) * (float) (scaleV * 2) + translateY;
		this.yB = (float) scaleV * cosAngle * 2.0F;
		this.zC = 1.0F;
		this.xB = -2.0F * sinAngle * (float) scaleV;
		this.zA = this.zB = this.xC = this.yC = 0.0F;
		this.zD = translateZ;
		this.yA = (float) scaleU * sinAngle * 2.0F;
		this.xD = translateX + (float) (scaleU * 2) * (sinAngle * 0.5F - cosAngle * 0.5F);
	}
}
