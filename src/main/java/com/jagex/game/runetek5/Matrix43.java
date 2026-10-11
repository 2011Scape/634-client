package com.jagex.game.runetek5;

import com.jagex.game.runetek5.graphics.Matrix;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.sound.jagtheora.OggStreamDecoder;

@OriginalClass("client!iaa")
public final class Matrix43 extends Matrix {

	@OriginalMember(owner = "client!iaa", name = "i", descriptor = "F")
	public float xD;

	@OriginalMember(owner = "client!iaa", name = "l", descriptor = "F")
	public float xB;

	@OriginalMember(owner = "client!iaa", name = "m", descriptor = "F")
	public float zD;

	@OriginalMember(owner = "client!iaa", name = "p", descriptor = "F")
	public float zB;

	@OriginalMember(owner = "client!iaa", name = "r", descriptor = "F")
	public float zA;

	@OriginalMember(owner = "client!iaa", name = "w", descriptor = "F")
	public float yA;

	@OriginalMember(owner = "client!iaa", name = "x", descriptor = "F")
	public float yC;

	@OriginalMember(owner = "client!iaa", name = "E", descriptor = "F")
	public float yB;

	@OriginalMember(owner = "client!iaa", name = "F", descriptor = "F")
	public float xA;

	@OriginalMember(owner = "client!iaa", name = "H", descriptor = "F")
	public float yD;

	@OriginalMember(owner = "client!iaa", name = "Q", descriptor = "F")
	public float xC;

	@OriginalMember(owner = "client!iaa", name = "T", descriptor = "F")
	public float zC;

	@OriginalMember(owner = "client!iaa", name = "<init>", descriptor = "()V")
	public Matrix43() {
		this.makeIdentity();
	}

	@OriginalMember(owner = "client!iaa", name = "e", descriptor = "(I)V")
	@Override
	public void makeRotationZ(@OriginalArg(0) int angle) {
		this.zC = 1.0F;
		this.xA = this.yB = OggStreamDecoder.COSINE[angle & 0x3FFF];
		this.yA = OggStreamDecoder.SINE[angle & 0x3FFF];
		this.xC = this.xD = this.yC = this.yD = this.zA = this.zB = this.zD = 0.0F;
		this.xB = -this.yA;
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(ZFFFF[F)V")
	public void transformPlane(@OriginalArg(1) float normalX, @OriginalArg(2) float normalZ, @OriginalArg(3) float normalY, @OriginalArg(4) float distance, @OriginalArg(5) float[] dest) {
		@Pc(26) float pointX;
		@Pc(42) float pointY;
		@Pc(34) float pointZ;
		@Pc(18) float axisOffset;
		if (normalX > 0.00390625F || normalX < -0.00390625F) {
			axisOffset = -distance / normalX;
			pointX = axisOffset * this.xA + this.xD;
			pointZ = this.zD + this.zA * axisOffset;
			pointY = this.yD + axisOffset * this.yA;
		} else if (normalY > 0.00390625F || normalY < -0.00390625F) {
			axisOffset = -distance / normalY;
			pointY = this.yB * axisOffset + this.yD;
			pointX = this.xB * axisOffset + this.xD;
			pointZ = this.zB * axisOffset + this.zD;
		} else {
			axisOffset = -distance / normalZ;
			pointX = axisOffset * this.xC + this.xD;
			pointY = this.yC * axisOffset + this.yD;
			pointZ = this.zD + axisOffset * this.zC;
		}
		dest[0] = normalZ * this.xC + normalX * this.xA + normalY * this.xB;
		dest[2] = normalZ * this.zC + this.zA * normalX + this.zB * normalY;
		dest[1] = this.yB * normalY + this.yA * normalX + this.yC * normalZ;
		dest[3] = -(dest[0] * pointX + pointY * dest[1] + pointZ * dest[2]);
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(III)V")
	@Override
	public void translate(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z) {
		this.zD += z;
		this.yD += y;
		this.xD += x;
	}

	@OriginalMember(owner = "client!iaa", name = "d", descriptor = "(I)V")
	@Override
	public void rotateAxisX(@OriginalArg(0) int angle) {
		@Pc(9) float cos = OggStreamDecoder.COSINE[angle & 0x3FFF];
		@Pc(15) float sin = OggStreamDecoder.SINE[angle & 0x3FFF];
		@Pc(18) float oldYA = this.yA;
		@Pc(21) float oldYB = this.yB;
		@Pc(24) float oldYC = this.yC;
		this.yA = cos * oldYA - this.zA * sin;
		@Pc(37) float oldYD = this.yD;
		this.zA = this.zA * cos + sin * oldYA;
		this.yB = oldYB * cos - sin * this.zB;
		this.yC = cos * oldYC - this.zC * sin;
		this.zB = oldYB * sin + cos * this.zB;
		this.zC = oldYC * sin + cos * this.zC;
		this.yD = cos * oldYD - sin * this.zD;
		this.zD = cos * this.zD + oldYD * sin;
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(FIFIIIF)V")
	public void setBillboardTransform(@OriginalArg(0) float translateZ, @OriginalArg(1) int scaleX, @OriginalArg(2) float translateX, @OriginalArg(3) int scaleY, @OriginalArg(4) int angle, @OriginalArg(6) float translateY) {
		if (angle == 0) {
			this.yA = this.zA = this.xB = this.zB = this.xC = this.yC = 0.0F;
			this.xA = scaleX;
			this.zC = 1.0F;
			this.yB = scaleY;
		} else {
			@Pc(41) float cos = OggStreamDecoder.COSINE[angle & 0x3FFF];
			@Pc(47) float sin = OggStreamDecoder.SINE[angle & 0x3FFF];
			this.yA = (float) scaleX * sin;
			this.yB = (float) scaleY * cos;
			this.zA = this.zB = this.xC = this.yC = 0.0F;
			this.zC = 1.0F;
			this.xB = -sin * (float) scaleY;
			this.xA = (float) scaleX * cos;
		}
		this.xD = translateX;
		this.yD = translateY;
		this.zD = translateZ;
	}

	@OriginalMember(owner = "client!iaa", name = "b", descriptor = "(III[I)V")
	@Override
	public void method4652(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] destination) {
		destination[0] = (int) ((float) z * this.xC + (float) x * this.xA + (float) y * this.xB + this.xD);
		destination[2] = (int) ((float) y * this.zB + this.zA * (float) x + this.zC * (float) z + this.zD);
		destination[1] = (int) (this.yD + (float) z * this.yC + (float) x * this.yA + (float) y * this.yB);
	}

	@OriginalMember(owner = "client!iaa", name = "b", descriptor = "(III)V")
	@Override
	public void applyTranslation(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z) {
		this.zD = z;
		this.xD = x;
		this.yA = this.zA = this.xB = this.zB = this.xC = this.yC = 0.0F;
		this.yD = y;
		this.xA = this.yB = this.zC = 1.0F;
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "([I)V")
	@Override
	public void project(@OriginalArg(0) int[] destination) {
		@Pc(11) float dx = (float) destination[0] - this.xD;
		@Pc(20) float dy = (float) destination[1] - this.yD;
		@Pc(29) float dz = (float) destination[2] - this.zD;
		destination[1] = (int) (this.yB * dy + dx * this.xB + this.zB * dz);
		destination[2] = (int) (this.zC * dz + dy * this.yC + this.xC * dx);
		destination[0] = (int) (this.xA * dx + this.yA * dy + dz * this.zA);
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(IIIIII)V")
	@Override
	public void createCamera(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int rotateX, @OriginalArg(4) int rotateY, @OriginalArg(5) int rotateZ) {
		@Pc(9) float cosX = OggStreamDecoder.COSINE[rotateX & 0x3FFF];
		@Pc(15) float sinX = OggStreamDecoder.SINE[rotateX & 0x3FFF];
		@Pc(21) float cosY = OggStreamDecoder.COSINE[rotateY & 0x3FFF];
		@Pc(27) float sinY = OggStreamDecoder.SINE[rotateY & 0x3FFF];
		@Pc(33) float cosZ = OggStreamDecoder.COSINE[rotateZ & 0x3FFF];
		@Pc(39) float sinZ = OggStreamDecoder.SINE[rotateZ & 0x3FFF];
		@Pc(43) float sinXCosZ = sinX * cosZ;
		@Pc(47) float sinXSinZ = sinZ * sinX;
		this.yC = sinXCosZ * cosY + sinY * sinZ;
		this.zA = cosX * sinY;
		this.xA = sinY * sinXSinZ + cosY * cosZ;
		this.yB = cosZ * cosX;
		this.zB = -sinX;
		this.xB = sinZ * cosX;
		this.zC = cosX * cosY;
		this.xC = cosZ * -sinY + cosY * sinXSinZ;
		this.yA = sinXCosZ * sinY + -cosY * sinZ;
		this.zD = this.zA * (float) -x - this.zB * (float) y - (float) z * this.zC;
		this.xD = -(this.xB * (float) y) + this.xA * (float) -x - this.xC * (float) z;
		this.yD = -((float) z * this.yC) + this.yA * (float) -x - this.yB * (float) y;
	}

	@OriginalMember(owner = "client!iaa", name = "g", descriptor = "(I)V")
	@Override
	public void rotateAxisZ(@OriginalArg(0) int angle) {
		@Pc(9) float cos = OggStreamDecoder.COSINE[angle & 0x3FFF];
		@Pc(15) float sin = OggStreamDecoder.SINE[angle & 0x3FFF];
		@Pc(18) float oldXA = this.xA;
		@Pc(21) float oldXB = this.xB;
		@Pc(24) float oldXC = this.xC;
		this.xA = cos * oldXA - this.yA * sin;
		@Pc(37) float oldXD = this.xD;
		this.yA = this.yA * cos + oldXA * sin;
		this.xB = oldXB * cos - this.yB * sin;
		this.xC = oldXC * cos - sin * this.yC;
		this.yB = cos * this.yB + sin * oldXB;
		this.yC = this.yC * cos + oldXC * sin;
		this.xD = cos * oldXD - sin * this.yD;
		this.yD = this.yD * cos + sin * oldXD;
	}

	@OriginalMember(owner = "client!iaa", name = "j", descriptor = "(I)[F")
	public float[] getColumnMajor() {
		Matrix43.columnMajorBuffer[0] = this.xA;
		Matrix43.columnMajorBuffer[9] = this.yC;
		Matrix43.columnMajorBuffer[12] = this.xD;
		Matrix43.columnMajorBuffer[1] = this.yA;
		Matrix43.columnMajorBuffer[6] = this.zB;
		Matrix43.columnMajorBuffer[4] = this.xB;
		Matrix43.columnMajorBuffer[10] = this.zC;
		Matrix43.columnMajorBuffer[8] = this.xC;
		Matrix43.columnMajorBuffer[14] = this.zD;
		Matrix43.columnMajorBuffer[13] = this.yD;
		Matrix43.columnMajorBuffer[2] = this.zA;
		Matrix43.columnMajorBuffer[5] = this.yB;
		return Matrix43.columnMajorBuffer;
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "()Lclient!bca;")
	@Override
	public Matrix copy() {
		@Pc(7) Matrix43 matrix = new Matrix43();
		matrix.xD = this.xD;
		matrix.xB = this.xB;
		matrix.yB = this.yB;
		matrix.zC = this.zC;
		matrix.yD = this.yD;
		matrix.yC = this.yC;
		matrix.xC = this.xC;
		matrix.xA = this.xA;
		matrix.zA = this.zA;
		matrix.yA = this.yA;
		matrix.zD = this.zD;
		matrix.zB = this.zB;
		return matrix;
	}

	@OriginalMember(owner = "client!iaa", name = "k", descriptor = "(I)V")
	public void negateYZ() {
		this.zB = -this.zB;
		this.yA = -this.yA;
		this.yB = -this.yB;
		this.zA = -this.zA;
		this.zC = -this.zC;
		this.yC = -this.yC;
		this.yD = -this.yD;
		this.zD = -this.zD;
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(III[I)V")
	@Override
	public void projectRelative(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] destination) {
		@Pc(6) int dy = (int) ((float) y - this.yD);
		@Pc(17) int dz = (int) ((float) z - this.zD);
		@Pc(24) int dx = (int) ((float) x - this.xD);
		destination[0] = (int) (this.zA * (float) dz + (float) dx * this.xA + (float) dy * this.yA);
		destination[2] = (int) (this.xC * (float) dx + (float) dy * this.yC + this.zC * (float) dz);
		destination[1] = (int) ((float) dz * this.zB + this.xB * (float) dx + (float) dy * this.yB);
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(Lclient!bca;)V")
	@Override
	public void apply(@OriginalArg(0) Matrix other) {
		@Pc(6) Matrix43 source = (Matrix43) other;
		this.zD = source.zD;
		this.yB = source.yB;
		this.xA = source.xA;
		this.xC = source.xC;
		this.xD = source.xD;
		this.zB = source.zB;
		this.yA = source.yA;
		this.yD = source.yD;
		this.zC = source.zC;
		this.yC = source.yC;
		this.zA = source.zA;
		this.xB = source.xB;
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(I)V")
	@Override
	public void rotate(@OriginalArg(0) int angle) {
		this.yB = 1.0F;
		this.xA = this.zC = OggStreamDecoder.COSINE[angle & 0x3FFF];
		this.xC = OggStreamDecoder.SINE[angle & 0x3FFF];
		this.zA = -this.xC;
		this.xB = this.xD = this.yA = this.yC = this.yD = this.zB = this.zD = 0.0F;
	}

	@OriginalMember(owner = "client!iaa", name = "c", descriptor = "(I)V")
	@Override
	public void makeRotationX(@OriginalArg(0) int angle) {
		this.xA = 1.0F;
		this.yB = this.zC = OggStreamDecoder.COSINE[angle & 0x3FFF];
		this.zB = OggStreamDecoder.SINE[angle & 0x3FFF];
		this.yC = -this.zB;
		this.xB = this.xC = this.xD = this.yA = this.yD = this.zA = this.zD = 0.0F;
	}

	@OriginalMember(owner = "client!iaa", name = "b", descriptor = "()V")
	@Override
	public void makeIdentity() {
		this.xA = this.yB = this.zC = 1.0F;
		this.yA = this.zA = this.xB = this.zB = this.xC = this.yC = this.xD = this.yD = this.zD = 0.0F;
	}

	@OriginalMember(owner = "client!iaa", name = "b", descriptor = "(I)V")
	@Override
	public void rotateAxisY(@OriginalArg(0) int angle) {
		@Pc(9) float cos = OggStreamDecoder.COSINE[angle & 0x3FFF];
		@Pc(15) float sin = OggStreamDecoder.SINE[angle & 0x3FFF];
		@Pc(18) float oldXA = this.xA;
		@Pc(21) float oldXB = this.xB;
		@Pc(24) float oldXC = this.xC;
		this.xA = oldXA * cos + this.zA * sin;
		@Pc(37) float oldXD = this.xD;
		this.zA = cos * this.zA - sin * oldXA;
		this.xB = cos * oldXB + sin * this.zB;
		this.zB = this.zB * cos - oldXB * sin;
		this.xC = cos * oldXC + this.zC * sin;
		this.zC = cos * this.zC - oldXC * sin;
		this.xD = this.zD * sin + cos * oldXD;
		this.zD = this.zD * cos - sin * oldXD;
	}

	@OriginalMember(owner = "client!iaa", name = "b", descriptor = "(Z)[F")
	public float[] getRotationColumnMajor() {
		Matrix43.columnMajorBuffer[14] = 0.0F;
		Matrix43.columnMajorBuffer[0] = this.xA;
		Matrix43.columnMajorBuffer[13] = 0.0F;
		Matrix43.columnMajorBuffer[1] = this.yA;
		Matrix43.columnMajorBuffer[12] = 0.0F;
		Matrix43.columnMajorBuffer[10] = this.zC;
		Matrix43.columnMajorBuffer[5] = this.yB;
		Matrix43.columnMajorBuffer[6] = this.zB;
		Matrix43.columnMajorBuffer[9] = this.yC;
		Matrix43.columnMajorBuffer[2] = this.zA;
		Matrix43.columnMajorBuffer[4] = this.xB;
		Matrix43.columnMajorBuffer[8] = this.xC;
		return Matrix43.columnMajorBuffer;
	}

	@OriginalMember(owner = "client!iaa", name = "a", descriptor = "(Lclient!bca;I)V")
	public void setInverse(@OriginalArg(0) Matrix matrix) {
		@Pc(6) Matrix43 source = (Matrix43) matrix;
		this.xC = source.zA;
		this.xB = source.yA;
		this.xA = source.xA;
		this.zA = source.xC;
		this.yC = source.zB;
		this.yA = source.xB;
		this.yB = source.yB;
		this.xD = -(source.yD * this.xB + source.xD * this.xA + source.zD * this.xC);
		this.zB = source.yC;
		this.zC = source.zC;
		this.yD = -(source.yD * this.yB + source.xD * this.yA + source.zD * this.yC);
		this.zD = -(this.zA * source.xD + this.zB * source.yD + source.zD * this.zC);
	}

	@OriginalMember(owner = "client!iaa", name = "c", descriptor = "(III[I)V")
	@Override
	public void projectDirection(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int[] destination) {
		destination[1] = (int) ((float) z * this.yC + (float) y * this.yB + this.yA * (float) x);
		destination[2] = (int) (this.zB * (float) y + this.zA * (float) x + (float) z * this.zC);
		destination[0] = (int) (this.xB * (float) y + this.xA * (float) x + this.xC * (float) z);
	}
	@OriginalMember(owner = "client!wg", name = "k", descriptor = "[F")
	public static final float[] columnMajorBuffer = new float[] { 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F };
}
