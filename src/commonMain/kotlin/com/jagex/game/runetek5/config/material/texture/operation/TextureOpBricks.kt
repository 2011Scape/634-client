package com.jagex.game.runetek5.config.material.texture.operation

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.graphics.EnvironmentLight
import com.jagex.game.runetek5.config.material.texture.TextureOperation
import util.Random
import com.jagex.game.runetek5.config.material.texture.TextureRaster
import com.jagex.game.runetek5.utils.Utils

@OriginalClass("client!sl")
class TextureOpBricks @OriginalMember(owner = "client!sl", name = "<init>", descriptor = "()V") constructor() : TextureOperation(0, true) {

	@OriginalMember(owner = "client!sl", name = "K", descriptor = "[[I")
	private var brickBrightness: Array<IntArray?>? = null

	@OriginalMember(owner = "client!sl", name = "L", descriptor = "[[I")
	private var columnBoundaries: Array<IntArray?>? = null

	@OriginalMember(owner = "client!sl", name = "Q", descriptor = "I")
	private var columnWidth: Int = 0

	@OriginalMember(owner = "client!sl", name = "W", descriptor = "[I")
	private var rowBoundaries: IntArray? = null

	@OriginalMember(owner = "client!sl", name = "X", descriptor = "I")
	private var halfMortarThickness: Int = 0

	@OriginalMember(owner = "client!sl", name = "Z", descriptor = "I")
	private var rowHeight: Int = 0

	@OriginalMember(owner = "client!sl", name = "I", descriptor = "I")
	private var brightnessVariation: Int = 1024

	@OriginalMember(owner = "client!sl", name = "M", descriptor = "I")
	private var staggerAmount: Int = 1024

	@OriginalMember(owner = "client!sl", name = "S", descriptor = "I")
	private var verticalOffset: Int = 0

	@OriginalMember(owner = "client!sl", name = "Y", descriptor = "I")
	private var columnsPerRow: Int = 4

	@OriginalMember(owner = "client!sl", name = "T", descriptor = "I")
	private var mortarThickness: Int = 81

	@OriginalMember(owner = "client!sl", name = "bb", descriptor = "I")
	private var columnWidthVariation: Int = 409

	@OriginalMember(owner = "client!sl", name = "eb", descriptor = "I")
	private var rowHeightVariation: Int = 204

	@OriginalMember(owner = "client!sl", name = "ab", descriptor = "I")
	private var rowCount: Int = 8

	@OriginalMember(owner = "client!sl", name = "b", descriptor = "(B)V")
	private fun generateBrickLayout() {
		@Pc(12) val random: Random = Random(this.rowCount.toLong())
		this.halfMortarThickness = this.mortarThickness / 2
		this.columnWidth = 4096 / this.columnsPerRow
		this.rowHeight = 4096 / this.rowCount
		@Pc(35) val halfColumnWidth: Int = this.columnWidth / 2
		this.rowBoundaries = IntArray(this.rowCount + 1)
		@Pc(47) val halfRowHeight: Int = this.rowHeight / 2
		this.columnBoundaries = Array<IntArray?>(this.rowCount) { IntArray(this.columnsPerRow + 1) }
		this.brickBrightness = Array<IntArray?>(this.rowCount) { IntArray(this.columnsPerRow) }
		this.rowBoundaries!![0] = 0
		@Pc(76) var i: Int = 0
		while (i < this.rowCount) {
			@Pc(86) var rowHeight: Int
			@Pc(98) val local98: Int
			if (i > 0) {
				rowHeight = this.rowHeight
				val heightVariation: Int = ((Utils.randomInt(4096, random) - 2048) * this.rowHeightVariation) shr 12
				@Pc(106) val adjustedRowHeight: Int = rowHeight + ((heightVariation * halfRowHeight) shr 12)
				this.rowBoundaries!![i] = adjustedRowHeight + this.rowBoundaries!![i - 1]
			}
			this.columnBoundaries!![i]!![0] = 0
			rowHeight = 0
			while (rowHeight < this.columnsPerRow) {
				if (rowHeight > 0) {
					var brickWidth: Int = this.columnWidth
					@Pc(149) val widthVariation: Int = ((Utils.randomInt(4096, random) - 2048) * this.columnWidthVariation) shr 12
					brickWidth += (halfColumnWidth * widthVariation) shr 12
					this.columnBoundaries!![i]!![rowHeight] = this.columnBoundaries!![i]!![rowHeight - 1] + brickWidth
				}
				this.brickBrightness!![i]!![rowHeight] = if (this.brightnessVariation <= 0) 4096 else 4096 - Utils.randomInt(this.brightnessVariation, random)
				rowHeight++
			}
			this.columnBoundaries!![i]!![this.columnsPerRow] = 4096
			i++
		}
		this.rowBoundaries!![this.rowCount] = 4096
	}

	@OriginalMember(owner = "client!sl", name = "a", descriptor = "(Lclient!cea;II)V")
	override fun decode(@OriginalArg(0) packet: Packet?, @OriginalArg(1) opcode: Int) {
		if (opcode == 0) {
			this.columnsPerRow = packet!!.readUByte()
		} else if (opcode == 1) {
			this.rowCount = packet!!.readUByte()
		} else if (opcode == 2) {
			this.columnWidthVariation = packet!!.readUShort()
		} else if (opcode == 3) {
			this.rowHeightVariation = packet!!.readUShort()
		} else if (opcode == 4) {
			this.staggerAmount = packet!!.readUShort()
		} else if (opcode == 5) {
			this.verticalOffset = packet!!.readUShort()
		} else if (opcode == 6) {
			this.mortarThickness = packet!!.readUShort()
		} else if (opcode == 7) {
			this.brightnessVariation = packet!!.readUShort()
		}
	}

	@OriginalMember(owner = "client!sl", name = "c", descriptor = "(II)[I")
	override fun monochromeOutput(@OriginalArg(0) row: Int): IntArray {
		@Pc(11) val output: IntArray = this.monochromeImageCache!!.get(row)!!
		if (this.monochromeImageCache!!.dirty) {
			@Pc(17) var rowIndex: Int = 0
			@Pc(24) var verticalPos: Int = this.verticalOffset + TextureRaster.verticalTable!![row]
			while (verticalPos < 0) {
				verticalPos += 4096
			}
			while (verticalPos > 4096) {
				verticalPos -= 4096
			}
			while (this.rowCount > rowIndex && verticalPos >= this.rowBoundaries!![rowIndex]) {
				rowIndex++
			}
			@Pc(56) val brickRow: Int = rowIndex - 1
			@Pc(67) val evenRow: Boolean = (rowIndex and 0x1) == 0
			@Pc(72) val rowEnd: Int = this.rowBoundaries!![rowIndex]
			@Pc(79) val rowStart: Int = this.rowBoundaries!![rowIndex - 1]
			if (this.halfMortarThickness + rowStart < verticalPos && rowEnd - this.halfMortarThickness > verticalPos) {
				@Pc(97) var i: Int = 0
				while (i < EnvironmentLight.textureOpWidth) {
					@Pc(101) var columnIndex: Int = 0
					@Pc(110) val stagger: Int = if (evenRow) this.staggerAmount else -this.staggerAmount
					@Pc(121) var horizontalPos: Int = EnvironmentLight.horizontalTable!![i] + ((stagger * this.columnWidth) shr 12)
					while (horizontalPos < 0) {
						horizontalPos += 4096
					}
					while (horizontalPos > 4096) {
						horizontalPos -= 4096
					}
					while (this.columnsPerRow > columnIndex && horizontalPos >= this.columnBoundaries!![brickRow]!![columnIndex]) {
						columnIndex++
					}
					@Pc(162) val brickColumn: Int = columnIndex - 1
					@Pc(169) val columnStart: Int = this.columnBoundaries!![brickRow]!![brickColumn]
					@Pc(176) val columnEnd: Int = this.columnBoundaries!![brickRow]!![columnIndex]
					if (horizontalPos > this.halfMortarThickness + columnStart && horizontalPos < columnEnd - this.halfMortarThickness) {
						output[i] = this.brickBrightness!![brickRow]!![brickColumn]
					} else {
						output[i] = 0
					}
					i++
				}
			} else {
				Arrays.setVariables(output, 0, EnvironmentLight.textureOpWidth, 0)
			}
		}
		return output
	}

	@OriginalMember(owner = "client!sl", name = "e", descriptor = "(I)V")
	override fun postDecode() {
		this.generateBrickLayout()
	}
}
