package com.jagex.game.runetek5.graphics.texture

import com.jagex.game.runetek5.graphics.model.Mesh
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ew")
class TextureTransformationMatrix {

	@OriginalMember(owner = "client!ew", name = "d", descriptor = "[I")
	@JvmField
	val centerZ: IntArray?

	@OriginalMember(owner = "client!ew", name = "b", descriptor = "[[F")
	@JvmField
	val matrix: Array<FloatArray?>?

	@OriginalMember(owner = "client!ew", name = "e", descriptor = "[I")
	@JvmField
	val centerY: IntArray?

	@OriginalMember(owner = "client!ew", name = "f", descriptor = "[I")
	@JvmField
	val centerX: IntArray?

	@OriginalMember(owner = "client!ew", name = "<init>", descriptor = "([I[I[I[[F)V")
	constructor(@OriginalArg(0) centerX: IntArray?, @OriginalArg(1) centerY: IntArray?, @OriginalArg(2) centerZ: IntArray?, @OriginalArg(3) matrix: Array<FloatArray?>?) {
		this.centerZ = centerZ
		this.matrix = matrix
		this.centerY = centerY
		this.centerX = centerX
	}

	companion object {
		@OriginalMember(owner = "client!dha", name = "a", descriptor = "(IILclient!bh;[I)Lclient!ew;")
		@JvmStatic
		fun generateTextureMatrix(@OriginalArg(1) numFaces: Int, @OriginalArg(2) mesh: Mesh?, @OriginalArg(3) faces: IntArray?): TextureTransformationMatrix {
			@Pc(7) var centerX: IntArray? = null
			@Pc(14) var centerY: IntArray? = null
			@Pc(16) var centerZ: IntArray? = null
			@Pc(18) var matrix: Array<FloatArray?>? = null
			if (mesh!!.facesTexture != null) {
				@Pc(24) val numTextures: Int = mesh.numTextures
				@Pc(27) val minX: IntArray = IntArray(numTextures)
				@Pc(30) val maxX: IntArray = IntArray(numTextures)
				@Pc(33) val minY: IntArray = IntArray(numTextures)
				@Pc(36) val maxY: IntArray = IntArray(numTextures)
				@Pc(39) val minZ: IntArray = IntArray(numTextures)
				@Pc(42) val maxZ: IntArray = IntArray(numTextures)
				@Pc(44) var i: Int = 0
				while (i < numTextures) {
					minX[i] = Int.MAX_VALUE
					maxX[i] = -2147483647
					minY[i] = Int.MAX_VALUE
					maxY[i] = -2147483647
					minZ[i] = Int.MAX_VALUE
					maxZ[i] = -2147483647
					i++
				}
				matrix = arrayOfNulls<FloatArray>(numTextures)
				centerZ = IntArray(numTextures)
				centerY = IntArray(numTextures)
				@Pc(91) var local91: Int
				@Pc(148) var local148: Int
				@Pc(85) var j: Int = 0
				while (j < numFaces) {
					val faceIndex: Int = faces!![j]
					if (mesh.facesTexture!![faceIndex].toInt() != -1) {
						@Pc(105) val textureIndex: Int = mesh.facesTexture!![faceIndex].toInt() and 0xFF
						@Pc(107) var k: Int = 0
						while (k < 3) {
							@Pc(124) val vertexIndex: Short
							if (k == 0) {
								vertexIndex = mesh.facesA!![faceIndex]
							} else if (k == 1) {
								vertexIndex = mesh.facesB!![faceIndex]
							} else {
								vertexIndex = mesh.facesC!![faceIndex]
							}
							@Pc(143) val x: Int = mesh.verticesX!![vertexIndex.toInt()]
							val y: Int = mesh.verticesY!![vertexIndex.toInt()]
							@Pc(153) val z: Int = mesh.verticesZ!![vertexIndex.toInt()]
							if (minX[textureIndex] > x) {
								minX[textureIndex] = x
							}
							if (x > maxX[textureIndex]) {
								maxX[textureIndex] = x
							}
							if (y < minY[textureIndex]) {
								minY[textureIndex] = y
							}
							if (y > maxY[textureIndex]) {
								maxY[textureIndex] = y
							}
							if (z < minZ[textureIndex]) {
								minZ[textureIndex] = z
							}
							if (maxZ[textureIndex] < z) {
								maxZ[textureIndex] = z
							}
							k++
						}
					}
					j++
				}
				centerX = IntArray(numTextures)
				var textureIndex: Int = 0
				while (textureIndex < numTextures) {
					@Pc(247) val textureMapping: Byte = mesh.texturesMapping!![textureIndex]
					if (textureMapping > 0) {
						centerX[textureIndex] = (maxX[textureIndex] + minX[textureIndex]) / 2
						centerY[textureIndex] = (maxY[textureIndex] + minY[textureIndex]) / 2
						centerZ[textureIndex] = (minZ[textureIndex] + maxZ[textureIndex]) / 2
						@Pc(305) val scaleX: Float
						@Pc(337) val scaleY: Float
						@Pc(303) val scaleZ: Float
						if (textureMapping.toInt() == 1) {
							val textureScaleX: Int = mesh.texturesScaleX!![textureIndex]
							if (textureScaleX == 0) {
								scaleZ = 1.0f
								scaleX = 1.0f
							} else if (textureScaleX <= 0) {
								scaleZ = 1.0f
								scaleX = (-textureScaleX).toFloat() / 1024.0f
							} else {
								scaleX = 1.0f
								scaleZ = textureScaleX.toFloat() / 1024.0f
							}
							scaleY = 64.0f / mesh.texturesScaleY!![textureIndex].toFloat()
						} else if (textureMapping.toInt() == 2) {
							scaleY = 64.0f / mesh.texturesScaleY!![textureIndex].toFloat()
							scaleZ = 64.0f / mesh.texturesScaleZ!![textureIndex].toFloat()
							scaleX = 64.0f / mesh.texturesScaleX!![textureIndex].toFloat()
						} else {
							scaleY = mesh.texturesScaleY!![textureIndex].toFloat() / 1024.0f
							scaleZ = mesh.texturesScaleZ!![textureIndex].toFloat() / 1024.0f
							scaleX = mesh.texturesScaleX!![textureIndex].toFloat() / 1024.0f
						}
						matrix[textureIndex] = TextureTransformationMatrix.createTextureMatrix(mesh.textureFaceZ!![textureIndex].toInt(), mesh.textureFaceY!![textureIndex].toInt(), scaleZ, scaleX, scaleY, mesh.textureFaceX!![textureIndex].toInt(), mesh.texturesRotation!![textureIndex].toInt() and 0xFF)
					}
					textureIndex++
				}
			}
			return TextureTransformationMatrix(centerX, centerY, centerZ, matrix)
		}

		@OriginalMember(owner = "client!pq", name = "a", descriptor = "(IIFFIFII)[F")
		@JvmStatic
		fun createTextureMatrix(@OriginalArg(0) directionZ: Int, @OriginalArg(1) directionY: Int, @OriginalArg(2) scaleZ: Float, @OriginalArg(3) scaleX: Float, @OriginalArg(5) scaleY: Float, @OriginalArg(6) directionX: Int, @OriginalArg(7) rotation: Int): FloatArray {
			@Pc(6) val rotationMatrix: FloatArray = FloatArray(9)
			@Pc(9) var matrix: FloatArray = FloatArray(9)
			@Pc(17) val rotationCos: Float = java.lang.Math.cos((rotation.toFloat() * 0.024543693f).toDouble()).toFloat()
			@Pc(33) val rotationSin: Float = java.lang.Math.sin((rotation.toFloat() * 0.024543693f).toDouble()).toFloat()
			rotationMatrix[6] = -rotationSin
			rotationMatrix[8] = rotationCos
			rotationMatrix[3] = 0.0f
			rotationMatrix[1] = 0.0f
			rotationMatrix[2] = rotationSin
			rotationMatrix[4] = 1.0f
			rotationMatrix[5] = 0.0f
			rotationMatrix[0] = rotationCos
			rotationMatrix[7] = 0.0f
			@Pc(78) val alignMatrix: FloatArray = FloatArray(9)
			@Pc(80) var axisX: Float = 1.0f
			@Pc(85) val cosAngle: Float = directionY.toFloat() / 32767.0f
			@Pc(87) var axisZ: Float = 0.0f
			@Pc(92) val oneMinusCos: Float = 1.0f - cosAngle
			@Pc(102) val sinAngle: Float = -(java.lang.Math.sqrt((1.0f - cosAngle * cosAngle).toDouble()).toFloat())
			@Pc(113) val horizontalLength: Float = java.lang.Math.sqrt((directionX * directionX + directionZ * directionZ).toDouble()).toFloat()
			if (horizontalLength == 0.0f && cosAngle == 0.0f) {
				matrix = rotationMatrix
			} else {
				if (horizontalLength != 0.0f) {
					axisX = (-directionZ).toFloat() / horizontalLength
					axisZ = directionX.toFloat() / horizontalLength
				}
				alignMatrix[5] = axisX * sinAngle
				alignMatrix[2] = axisX * axisZ * oneMinusCos
				alignMatrix[8] = cosAngle + oneMinusCos * axisZ * axisZ
				alignMatrix[4] = cosAngle
				alignMatrix[0] = oneMinusCos * axisX * axisX + cosAngle
				alignMatrix[6] = oneMinusCos * axisZ * axisX
				alignMatrix[3] = sinAngle * -axisZ
				alignMatrix[1] = sinAngle * axisZ
				alignMatrix[7] = sinAngle * -axisX
				matrix[0] = alignMatrix[0] * rotationMatrix[0] + rotationMatrix[1] * alignMatrix[3] + alignMatrix[6] * rotationMatrix[2]
				matrix[1] = alignMatrix[7] * rotationMatrix[2] + rotationMatrix[1] * alignMatrix[4] + rotationMatrix[0] * alignMatrix[1]
				matrix[2] = rotationMatrix[1] * alignMatrix[5] + rotationMatrix[0] * alignMatrix[2] + rotationMatrix[2] * alignMatrix[8]
				matrix[3] = alignMatrix[0] * rotationMatrix[3] + rotationMatrix[4] * alignMatrix[3] + alignMatrix[6] * rotationMatrix[5]
				matrix[4] = rotationMatrix[5] * alignMatrix[7] + rotationMatrix[3] * alignMatrix[1] + rotationMatrix[4] * alignMatrix[4]
				matrix[6] = alignMatrix[0] * rotationMatrix[6] + rotationMatrix[7] * alignMatrix[3] + alignMatrix[6] * rotationMatrix[8]
				matrix[5] = rotationMatrix[4] * alignMatrix[5] + alignMatrix[2] * rotationMatrix[3] + rotationMatrix[5] * alignMatrix[8]
				matrix[7] = alignMatrix[1] * rotationMatrix[6] + alignMatrix[4] * rotationMatrix[7] + rotationMatrix[8] * alignMatrix[7]
				matrix[8] = alignMatrix[5] * rotationMatrix[7] + rotationMatrix[6] * alignMatrix[2] + rotationMatrix[8] * alignMatrix[8]
			}
			matrix[7] *= scaleZ
			matrix[4] *= scaleY
			matrix[3] *= scaleY
			matrix[5] *= scaleY
			matrix[2] *= scaleX
			matrix[8] *= scaleZ
			matrix[6] *= scaleZ
			matrix[1] *= scaleX
			matrix[0] *= scaleX
			return matrix
		}
	}
}
