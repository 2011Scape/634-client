package com.jagex.game.runetek5.config.material.texture.operation

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import java.util.Random
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.config.material.texture.TextureRaster
import com.jagex.game.runetek5.utils.ArrayUtils
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!hn")
class TextureOpVoronoiNoise : TextureOperation {

	@OriginalMember(owner = "client!hn", name = "I", descriptor = "[S")
	private var pointPositions: ShortArray? = ShortArray(512)

	@OriginalMember(owner = "client!hn", name = "K", descriptor = "I")
	private var distanceMetric: Int = 1

	@OriginalMember(owner = "client!hn", name = "L", descriptor = "[B")
	private var permutationTable: ByteArray? = ByteArray(512)

	@OriginalMember(owner = "client!hn", name = "R", descriptor = "I")
	private var pointJitter: Int = 2048

	@OriginalMember(owner = "client!hn", name = "Q", descriptor = "I")
	private var distanceOutputMode: Int = 2

	@OriginalMember(owner = "client!hn", name = "O", descriptor = "I")
	private var randomSeed: Int = 0

	@OriginalMember(owner = "client!hn", name = "J", descriptor = "I")
	private var cellCountY: Int = 5

	@OriginalMember(owner = "client!hn", name = "V", descriptor = "I")
	private var cellCountX: Int = 5

	@OriginalMember(owner = "client!hn", name = "<init>", descriptor = "()V")
	constructor() : super(0, true)

	@OriginalMember(owner = "client!hn", name = "b", descriptor = "(Z)V")
	private fun initializePointPositions() {
		@Pc(16) val random: Random = Random(this.randomSeed.toLong())
		this.pointPositions = ShortArray(512)
		if (this.pointJitter > 0) {
			@Pc(28) var i: Int = 0
			while (i < 512) {
				this.pointPositions!![i] = Utils.randomInt(this.pointJitter, random).toShort()
				i++
			}
		}
	}

	@OriginalMember(owner = "client!hn", name = "e", descriptor = "(I)V")
	override fun postDecode() {
		this.permutationTable = ArrayUtils.getPermutationTable(this.randomSeed)
		this.initializePointPositions()
	}

	@OriginalMember(owner = "client!hn", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(11) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(30) val verticalPos: Int = this.cellCountY * TextureRaster.verticalTable!![row] + 2048
			@Pc(34) val verticalCell: Int = verticalPos shr 12
			@Pc(38) val nextVerticalCell: Int = verticalCell + 1
			@Pc(40) var i: Int = 0
			while (i < EnvironmentLight.textureOpWidth) {
				TextureOpVoronoiNoise.fourthNearestDistance = Int.MAX_VALUE
				TextureOpVoronoiNoise.thirdNearestDistance = Int.MAX_VALUE
				TextureOpVoronoiNoise.secondNearestDistance = Int.MAX_VALUE
				TextureOpVoronoiNoise.nearestDistance = Int.MAX_VALUE
				@Pc(60) val horizontalPos: Int = EnvironmentLight.horizontalTable!![i] * this.cellCountX + 2048
				@Pc(64) val horizontalCell: Int = horizontalPos shr 12
				@Pc(68) val nextHorizontalCell: Int = horizontalCell + 1
				@Pc(151) var distanceMetric: Int
				@Pc(72) var j: Int = verticalCell - 1
				while (j <= nextVerticalCell) {
					@Pc(93) val rowPermutation: Int = this.permutationTable!![(if (j >= this.cellCountY) j - this.cellCountY else j) and 0xFF].toInt() and 0xFF
					@Pc(97) var k: Int = horizontalCell - 1
					while (k <= nextHorizontalCell) {
						@Pc(122) val pointIndexX: Int = (this.permutationTable!![((if (this.cellCountX > k) k else k - this.cellCountX) + rowPermutation) and 0xFF].toInt() and 0xFF) * 2
						@Pc(126) val negCellX: Int = -(k shl 12)
						@Pc(130) val pointIndexY: Int = pointIndexX + 1
						@Pc(135) var deltaX: Int = negCellX + horizontalPos - this.pointPositions!![pointIndexX]
						@Pc(148) var deltaY: Int = verticalPos - (j shl 12) - this.pointPositions!![pointIndexY]
						distanceMetric = this.distanceMetric
						@Pc(184) var distance: Int
						if (distanceMetric == 1) {
							distance = (deltaY * deltaY + deltaX * deltaX) shr 12
						} else if (distanceMetric == 3) {
							deltaX = if (deltaX < 0) -deltaX else deltaX
							deltaY = if (deltaY < 0) -deltaY else deltaY
							distance = if (deltaX <= deltaY) deltaY else deltaX
						} else if (distanceMetric == 4) {
							deltaX = (Math.sqrt(((if (deltaX < 0) -deltaX else deltaX).toFloat() / 4096.0f).toDouble()) * 4096.0).toInt()
							deltaY = (Math.sqrt(((if (deltaY < 0) -deltaY else deltaY).toFloat() / 4096.0f).toDouble()) * 4096.0).toInt()
							distance = deltaY + deltaX
							distance = (distance * distance) shr 12
						} else if (distanceMetric == 5) {
							deltaY *= deltaY
							deltaX *= deltaX
							distance = (Math.sqrt(Math.sqrt(((deltaX + deltaY).toFloat() / 1.6777216E7f).toDouble())) * 4096.0).toInt()
						} else if (distanceMetric == 2) {
							distance = (if (deltaX < 0) -deltaX else deltaX) + (if (deltaY >= 0) deltaY else -deltaY)
						} else {
							distance = (Math.sqrt(((deltaX * deltaX + deltaY * deltaY).toFloat() / 1.6777216E7f).toDouble()) * 4096.0).toInt()
						}
						if (TextureOpVoronoiNoise.nearestDistance > distance) {
							TextureOpVoronoiNoise.fourthNearestDistance = TextureOpVoronoiNoise.thirdNearestDistance
							TextureOpVoronoiNoise.thirdNearestDistance = TextureOpVoronoiNoise.secondNearestDistance
							TextureOpVoronoiNoise.secondNearestDistance = TextureOpVoronoiNoise.nearestDistance
							TextureOpVoronoiNoise.nearestDistance = distance
						} else if (TextureOpVoronoiNoise.secondNearestDistance > distance) {
							TextureOpVoronoiNoise.fourthNearestDistance = TextureOpVoronoiNoise.thirdNearestDistance
							TextureOpVoronoiNoise.thirdNearestDistance = TextureOpVoronoiNoise.secondNearestDistance
							TextureOpVoronoiNoise.secondNearestDistance = distance
						} else if (TextureOpVoronoiNoise.thirdNearestDistance > distance) {
							TextureOpVoronoiNoise.fourthNearestDistance = TextureOpVoronoiNoise.thirdNearestDistance
							TextureOpVoronoiNoise.thirdNearestDistance = distance
						} else if (TextureOpVoronoiNoise.fourthNearestDistance > distance) {
							TextureOpVoronoiNoise.fourthNearestDistance = distance
						}
						k++
					}
					j++
				}
				distanceMetric = this.distanceOutputMode
				if (distanceMetric == 0) {
					output[i] = TextureOpVoronoiNoise.nearestDistance
				} else if (distanceMetric == 1) {
					output[i] = TextureOpVoronoiNoise.secondNearestDistance
				} else if (distanceMetric == 3) {
					output[i] = TextureOpVoronoiNoise.thirdNearestDistance
				} else if (distanceMetric == 4) {
					output[i] = TextureOpVoronoiNoise.fourthNearestDistance
				} else if (distanceMetric == 2) {
					output[i] = TextureOpVoronoiNoise.secondNearestDistance - TextureOpVoronoiNoise.nearestDistance
				}
				i++
			}
		}
		return output
	}

	@OriginalMember(owner = "client!hn", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			val count: Int = packet!!.readUByte()
			this.cellCountY = count
			this.cellCountX = count
		} else if (opcode == 1) {
			this.randomSeed = packet!!.readUByte()
		} else if (opcode == 2) {
			this.pointJitter = packet!!.readUShort()
		} else if (opcode == 3) {
			this.distanceOutputMode = packet!!.readUByte()
		} else if (opcode == 4) {
			this.distanceMetric = packet!!.readUByte()
		} else if (opcode == 5) {
			this.cellCountX = packet!!.readUByte()
		} else if (opcode == 6) {
			this.cellCountY = packet!!.readUByte()
		}
	}

	companion object {
		@OriginalMember(owner = "client!jt", name = "t", descriptor = "I")
		@JvmField
		var fourthNearestDistance: Int = 0
		@OriginalMember(owner = "client!jo", name = "J", descriptor = "I")
		@JvmField
		var nearestDistance: Int = 0
		@OriginalMember(owner = "client!og", name = "c", descriptor = "I")
		@JvmField
		var thirdNearestDistance: Int = 0
		@OriginalMember(owner = "client!rs", name = "b", descriptor = "I")
		@JvmField
		var secondNearestDistance: Int = 0
	}
}
