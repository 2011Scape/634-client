package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import java.util.Random;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!sl")
public final class TextureOpBricks extends TextureOperation {

	@OriginalMember(owner = "client!sl", name = "K", descriptor = "[[I")
	private int[][] brickBrightness;

	@OriginalMember(owner = "client!sl", name = "L", descriptor = "[[I")
	private int[][] columnBoundaries;

	@OriginalMember(owner = "client!sl", name = "Q", descriptor = "I")
	private int columnWidth;

	@OriginalMember(owner = "client!sl", name = "W", descriptor = "[I")
	private int[] rowBoundaries;

	@OriginalMember(owner = "client!sl", name = "X", descriptor = "I")
	private int halfMortarThickness;

	@OriginalMember(owner = "client!sl", name = "Z", descriptor = "I")
	private int rowHeight;

	@OriginalMember(owner = "client!sl", name = "I", descriptor = "I")
	private int brightnessVariation = 1024;

	@OriginalMember(owner = "client!sl", name = "M", descriptor = "I")
	private int staggerAmount = 1024;

	@OriginalMember(owner = "client!sl", name = "S", descriptor = "I")
	private int verticalOffset = 0;

	@OriginalMember(owner = "client!sl", name = "Y", descriptor = "I")
	private int columnsPerRow = 4;

	@OriginalMember(owner = "client!sl", name = "T", descriptor = "I")
	private int mortarThickness = 81;

	@OriginalMember(owner = "client!sl", name = "bb", descriptor = "I")
	private int columnWidthVariation = 409;

	@OriginalMember(owner = "client!sl", name = "eb", descriptor = "I")
	private int rowHeightVariation = 204;

	@OriginalMember(owner = "client!sl", name = "ab", descriptor = "I")
	private int rowCount = 8;

	@OriginalMember(owner = "client!sl", name = "<init>", descriptor = "()V")
	public TextureOpBricks() {
		super(0, true);
	}

	@OriginalMember(owner = "client!sl", name = "b", descriptor = "(B)V")
	private void generateBrickLayout() {
		@Pc(12) Random random = new Random((long) this.rowCount);
		this.halfMortarThickness = this.mortarThickness / 2;
		this.columnWidth = 4096 / this.columnsPerRow;
		this.rowHeight = 4096 / this.rowCount;
		@Pc(35) int halfColumnWidth = this.columnWidth / 2;
		this.rowBoundaries = new int[this.rowCount + 1];
		@Pc(47) int halfRowHeight = this.rowHeight / 2;
		this.columnBoundaries = new int[this.rowCount][this.columnsPerRow + 1];
		this.brickBrightness = new int[this.rowCount][this.columnsPerRow];
		this.rowBoundaries[0] = 0;
		for (@Pc(76) int i = 0; i < this.rowCount; i++) {
			@Pc(86) int rowHeight;
			@Pc(98) int local98;
			if (i > 0) {
				rowHeight = this.rowHeight;
				int heightVariation = (Utils.randomInt(4096, random) - 2048) * this.rowHeightVariation >> 12;
				@Pc(106) int adjustedRowHeight = rowHeight + (heightVariation * halfRowHeight >> 12);
				this.rowBoundaries[i] = adjustedRowHeight + this.rowBoundaries[i - 1];
			}
			this.columnBoundaries[i][0] = 0;
			for (rowHeight = 0; rowHeight < this.columnsPerRow; rowHeight++) {
				if (rowHeight > 0) {
					int brickWidth = this.columnWidth;
					@Pc(149) int widthVariation = (Utils.randomInt(4096, random) - 2048) * this.columnWidthVariation >> 12;
					brickWidth += halfColumnWidth * widthVariation >> 12;
					this.columnBoundaries[i][rowHeight] = this.columnBoundaries[i][rowHeight - 1] + brickWidth;
				}
				this.brickBrightness[i][rowHeight] = this.brightnessVariation <= 0 ? 4096 : 4096 - Utils.randomInt(this.brightnessVariation, random);
			}
			this.columnBoundaries[i][this.columnsPerRow] = 4096;
		}
		this.rowBoundaries[this.rowCount] = 4096;
	}

	@OriginalMember(owner = "client!sl", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.columnsPerRow = packet.readUByte();
		} else if (opcode == 1) {
			this.rowCount = packet.readUByte();
		} else if (opcode == 2) {
			this.columnWidthVariation = packet.readUShort();
		} else if (opcode == 3) {
			this.rowHeightVariation = packet.readUShort();
		} else if (opcode == 4) {
			this.staggerAmount = packet.readUShort();
		} else if (opcode == 5) {
			this.verticalOffset = packet.readUShort();
		} else if (opcode == 6) {
			this.mortarThickness = packet.readUShort();
		} else if (opcode == 7) {
			this.brightnessVariation = packet.readUShort();
		}
	}

	@OriginalMember(owner = "client!sl", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(11) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(17) int rowIndex = 0;
			@Pc(24) int verticalPos;
			for (verticalPos = this.verticalOffset + TextureRaster.verticalTable[row]; verticalPos < 0; verticalPos += 4096) {
			}
			while (verticalPos > 4096) {
				verticalPos -= 4096;
			}
			while (this.rowCount > rowIndex && verticalPos >= this.rowBoundaries[rowIndex]) {
				rowIndex++;
			}
			@Pc(56) int brickRow = rowIndex - 1;
			@Pc(67) boolean evenRow = (rowIndex & 0x1) == 0;
			@Pc(72) int rowEnd = this.rowBoundaries[rowIndex];
			@Pc(79) int rowStart = this.rowBoundaries[rowIndex - 1];
			if (this.halfMortarThickness + rowStart < verticalPos && rowEnd - this.halfMortarThickness > verticalPos) {
				for (@Pc(97) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
					@Pc(101) int columnIndex = 0;
					@Pc(110) int stagger = evenRow ? this.staggerAmount : -this.staggerAmount;
					@Pc(121) int horizontalPos;
					for (horizontalPos = EnvironmentLight.horizontalTable[i] + (stagger * this.columnWidth >> 12); horizontalPos < 0; horizontalPos += 4096) {
					}
					while (horizontalPos > 4096) {
						horizontalPos -= 4096;
					}
					while (this.columnsPerRow > columnIndex && horizontalPos >= this.columnBoundaries[brickRow][columnIndex]) {
						columnIndex++;
					}
					@Pc(162) int brickColumn = columnIndex - 1;
					@Pc(169) int columnStart = this.columnBoundaries[brickRow][brickColumn];
					@Pc(176) int columnEnd = this.columnBoundaries[brickRow][columnIndex];
					if (horizontalPos > this.halfMortarThickness + columnStart && horizontalPos < columnEnd - this.halfMortarThickness) {
						output[i] = this.brickBrightness[brickRow][brickColumn];
					} else {
						output[i] = 0;
					}
				}
			} else {
				Arrays.setVariables(output, 0, EnvironmentLight.textureOpWidth, 0);
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!sl", name = "e", descriptor = "(I)V")
	@Override
	public void postDecode() {
		this.generateBrickLayout();
	}
}
