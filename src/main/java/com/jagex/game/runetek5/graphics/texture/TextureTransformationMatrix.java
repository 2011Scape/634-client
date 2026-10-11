package com.jagex.game.runetek5.graphics.texture;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.model.Mesh;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!ew")
public final class TextureTransformationMatrix {

	@OriginalMember(owner = "client!ew", name = "d", descriptor = "[I")
	public final int[] centerZ;

	@OriginalMember(owner = "client!ew", name = "b", descriptor = "[[F")
	public final float[][] matrix;

	@OriginalMember(owner = "client!ew", name = "e", descriptor = "[I")
	public final int[] centerY;

	@OriginalMember(owner = "client!ew", name = "f", descriptor = "[I")
	public final int[] centerX;

	@OriginalMember(owner = "client!ew", name = "<init>", descriptor = "([I[I[I[[F)V")
	public TextureTransformationMatrix(@OriginalArg(0) int[] centerX, @OriginalArg(1) int[] centerY, @OriginalArg(2) int[] centerZ, @OriginalArg(3) float[][] matrix) {
		this.centerZ = centerZ;
		this.matrix = matrix;
		this.centerY = centerY;
		this.centerX = centerX;
	}
	@OriginalMember(owner = "client!dha", name = "a", descriptor = "(IILclient!bh;[I)Lclient!ew;")
	public static TextureTransformationMatrix generateTextureMatrix(@OriginalArg(1) int numFaces, @OriginalArg(2) Mesh mesh, @OriginalArg(3) int[] faces) {
		@Pc(7) int[] centerX = null;
		@Pc(14) int[] centerY = null;
		@Pc(16) int[] centerZ = null;
		@Pc(18) float[][] matrix = null;
		if (mesh.facesTexture != null) {
			@Pc(24) int numTextures = mesh.numTextures;
			@Pc(27) int[] minX = new int[numTextures];
			@Pc(30) int[] maxX = new int[numTextures];
			@Pc(33) int[] minY = new int[numTextures];
			@Pc(36) int[] maxY = new int[numTextures];
			@Pc(39) int[] minZ = new int[numTextures];
			@Pc(42) int[] maxZ = new int[numTextures];
			for (@Pc(44) int i = 0; i < numTextures; i++) {
				minX[i] = Integer.MAX_VALUE;
				maxX[i] = -2147483647;
				minY[i] = Integer.MAX_VALUE;
				maxY[i] = -2147483647;
				minZ[i] = Integer.MAX_VALUE;
				maxZ[i] = -2147483647;
			}
			matrix = new float[numTextures][];
			centerZ = new int[numTextures];
			centerY = new int[numTextures];
			@Pc(91) int local91;
			@Pc(148) int local148;
			for (@Pc(85) int j = 0; j < numFaces; j++) {
				int faceIndex = faces[j];
				if (mesh.facesTexture[faceIndex] != -1) {
					@Pc(105) int textureIndex = mesh.facesTexture[faceIndex] & 0xFF;
					for (@Pc(107) int k = 0; k < 3; k++) {
						@Pc(124) short vertexIndex;
						if (k == 0) {
							vertexIndex = mesh.facesA[faceIndex];
						} else if (k == 1) {
							vertexIndex = mesh.facesB[faceIndex];
						} else {
							vertexIndex = mesh.facesC[faceIndex];
						}
						@Pc(143) int x = mesh.verticesX[vertexIndex];
						int y = mesh.verticesY[vertexIndex];
						@Pc(153) int z = mesh.verticesZ[vertexIndex];
						if (minX[textureIndex] > x) {
							minX[textureIndex] = x;
						}
						if (x > maxX[textureIndex]) {
							maxX[textureIndex] = x;
						}
						if (y < minY[textureIndex]) {
							minY[textureIndex] = y;
						}
						if (y > maxY[textureIndex]) {
							maxY[textureIndex] = y;
						}
						if (z < minZ[textureIndex]) {
							minZ[textureIndex] = z;
						}
						if (maxZ[textureIndex] < z) {
							maxZ[textureIndex] = z;
						}
					}
				}
			}
			centerX = new int[numTextures];
			for (int textureIndex = 0; textureIndex < numTextures; textureIndex++) {
				@Pc(247) byte textureMapping = mesh.texturesMapping[textureIndex];
				if (textureMapping > 0) {
					centerX[textureIndex] = (maxX[textureIndex] + minX[textureIndex]) / 2;
					centerY[textureIndex] = (maxY[textureIndex] + minY[textureIndex]) / 2;
					centerZ[textureIndex] = (minZ[textureIndex] + maxZ[textureIndex]) / 2;
					@Pc(305) float scaleX;
					@Pc(337) float scaleY;
					@Pc(303) float scaleZ;
					if (textureMapping == 1) {
						int textureScaleX = mesh.texturesScaleX[textureIndex];
						if (textureScaleX == 0) {
							scaleZ = 1.0F;
							scaleX = 1.0F;
						} else if (textureScaleX <= 0) {
							scaleZ = 1.0F;
							scaleX = (float) -textureScaleX / 1024.0F;
						} else {
							scaleX = 1.0F;
							scaleZ = (float) textureScaleX / 1024.0F;
						}
						scaleY = 64.0F / (float) mesh.texturesScaleY[textureIndex];
					} else if (textureMapping == 2) {
						scaleY = 64.0F / (float) mesh.texturesScaleY[textureIndex];
						scaleZ = 64.0F / (float) mesh.texturesScaleZ[textureIndex];
						scaleX = 64.0F / (float) mesh.texturesScaleX[textureIndex];
					} else {
						scaleY = (float) mesh.texturesScaleY[textureIndex] / 1024.0F;
						scaleZ = (float) mesh.texturesScaleZ[textureIndex] / 1024.0F;
						scaleX = (float) mesh.texturesScaleX[textureIndex] / 1024.0F;
					}
					matrix[textureIndex] = TextureTransformationMatrix.createTextureMatrix(mesh.textureFaceZ[textureIndex], mesh.textureFaceY[textureIndex], scaleZ, scaleX, scaleY, mesh.textureFaceX[textureIndex], mesh.texturesRotation[textureIndex] & 0xFF);
				}
			}
		}
		return new TextureTransformationMatrix(centerX, centerY, centerZ, matrix);
	}
	@OriginalMember(owner = "client!pq", name = "a", descriptor = "(IIFFIFII)[F")
	public static float[] createTextureMatrix(@OriginalArg(0) int directionZ, @OriginalArg(1) int directionY, @OriginalArg(2) float scaleZ, @OriginalArg(3) float scaleX, @OriginalArg(5) float scaleY, @OriginalArg(6) int directionX, @OriginalArg(7) int rotation) {
		@Pc(6) float[] rotationMatrix = new float[9];
		@Pc(9) float[] matrix = new float[9];
		@Pc(17) float rotationCos = (float) Math.cos((double) ((float) rotation * 0.024543693F));
		@Pc(33) float rotationSin = (float) Math.sin((double) ((float) rotation * 0.024543693F));
		rotationMatrix[6] = -rotationSin;
		rotationMatrix[8] = rotationCos;
		rotationMatrix[3] = 0.0F;
		rotationMatrix[1] = 0.0F;
		rotationMatrix[2] = rotationSin;
		rotationMatrix[4] = 1.0F;
		rotationMatrix[5] = 0.0F;
		rotationMatrix[0] = rotationCos;
		rotationMatrix[7] = 0.0F;
		@Pc(78) float[] alignMatrix = new float[9];
		@Pc(80) float axisX = 1.0F;
		@Pc(85) float cosAngle = (float) directionY / 32767.0F;
		@Pc(87) float axisZ = 0.0F;
		@Pc(92) float oneMinusCos = 1.0F - cosAngle;
		@Pc(102) float sinAngle = -((float) Math.sqrt((double) (1.0F - cosAngle * cosAngle)));
		@Pc(113) float horizontalLength = (float) Math.sqrt((double) (directionX * directionX + directionZ * directionZ));
		if (horizontalLength == 0.0F && cosAngle == 0.0F) {
			matrix = rotationMatrix;
		} else {
			if (horizontalLength != 0.0F) {
				axisX = (float) -directionZ / horizontalLength;
				axisZ = (float) directionX / horizontalLength;
			}
			alignMatrix[5] = axisX * sinAngle;
			alignMatrix[2] = axisX * axisZ * oneMinusCos;
			alignMatrix[8] = cosAngle + oneMinusCos * axisZ * axisZ;
			alignMatrix[4] = cosAngle;
			alignMatrix[0] = oneMinusCos * axisX * axisX + cosAngle;
			alignMatrix[6] = oneMinusCos * axisZ * axisX;
			alignMatrix[3] = sinAngle * -axisZ;
			alignMatrix[1] = sinAngle * axisZ;
			alignMatrix[7] = sinAngle * -axisX;
			matrix[0] = alignMatrix[0] * rotationMatrix[0] + rotationMatrix[1] * alignMatrix[3] + alignMatrix[6] * rotationMatrix[2];
			matrix[1] = alignMatrix[7] * rotationMatrix[2] + rotationMatrix[1] * alignMatrix[4] + rotationMatrix[0] * alignMatrix[1];
			matrix[2] = rotationMatrix[1] * alignMatrix[5] + rotationMatrix[0] * alignMatrix[2] + rotationMatrix[2] * alignMatrix[8];
			matrix[3] = alignMatrix[0] * rotationMatrix[3] + rotationMatrix[4] * alignMatrix[3] + alignMatrix[6] * rotationMatrix[5];
			matrix[4] = rotationMatrix[5] * alignMatrix[7] + rotationMatrix[3] * alignMatrix[1] + rotationMatrix[4] * alignMatrix[4];
			matrix[6] = alignMatrix[0] * rotationMatrix[6] + rotationMatrix[7] * alignMatrix[3] + alignMatrix[6] * rotationMatrix[8];
			matrix[5] = rotationMatrix[4] * alignMatrix[5] + alignMatrix[2] * rotationMatrix[3] + rotationMatrix[5] * alignMatrix[8];
			matrix[7] = alignMatrix[1] * rotationMatrix[6] + alignMatrix[4] * rotationMatrix[7] + rotationMatrix[8] * alignMatrix[7];
			matrix[8] = alignMatrix[5] * rotationMatrix[7] + rotationMatrix[6] * alignMatrix[2] + rotationMatrix[8] * alignMatrix[8];
		}
		matrix[7] *= scaleZ;
		matrix[4] *= scaleY;
		matrix[3] *= scaleY;
		matrix[5] *= scaleY;
		matrix[2] *= scaleX;
		matrix[8] *= scaleZ;
		matrix[6] *= scaleZ;
		matrix[1] *= scaleX;
		matrix[0] *= scaleX;
		return matrix;
	}
}
