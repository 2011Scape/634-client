package com.jagex.game.runetek5.config.material.texture;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.graphics.TextureOpRaster;
import com.jagex.game.runetek5.config.material.texture.operation.TextureShapeRasterizer;
import com.jagex.game.runetek5.graphics.texture.TextureOpShapeRaster;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.operation.BezierCurveRasterizer;
import com.jagex.game.runetek5.config.material.texture.operation.EllipseRasterizer;
import com.jagex.game.runetek5.config.material.texture.operation.LineRasterizer;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!ft")
public final class TextureOpRasterizer extends TextureOperation {

	@OriginalMember(owner = "client!ft", name = "I", descriptor = "[Lclient!iu;")
	private TextureShapeRasterizer[] shapes;

	@OriginalMember(owner = "client!ft", name = "<init>", descriptor = "()V")
	public TextureOpRasterizer() {
		super(0, true);
	}

	@OriginalMember(owner = "client!ft", name = "a", descriptor = "([[IB)V")
	private void render(@OriginalArg(0) int[][] dest) {
		@Pc(12) int width = EnvironmentLight.textureOpWidth;
		@Pc(14) int height = TextureOperation.currentHeight;
		TextureOpRaster.setPixelBuffer(dest);
		TextureOpRasterizer.setClipBounds(TextureRaster.widthMask, TextureOperation.columnEnd);
		if (this.shapes == null) {
			return;
		}
		for (@Pc(30) int index = 0; index < this.shapes.length; index++) {
			@Pc(37) TextureShapeRasterizer shape = this.shapes[index];
			@Pc(40) int fillColour = shape.fillColor;
			@Pc(43) int lineColour = shape.strokeColor;
			if (fillColour < 0) {
				if (lineColour >= 0) {
					shape.drawStroked(height, width);
				}
			} else if (lineColour < 0) {
				shape.drawFilled(width, height);
			} else {
				shape.drawFilledAndStroked(width, height);
			}
		}
	}

	@OriginalMember(owner = "client!ft", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(11) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			this.render(super.monochromeImageCache.getAllPaletteData());
		}
		return output;
	}

	@OriginalMember(owner = "client!ft", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.shapes = new TextureShapeRasterizer[packet.readUByte()];
			for (@Pc(16) int i = 0; i < this.shapes.length; i++) {
				@Pc(22) int shapeType = packet.readUByte();
				if (shapeType == 0) {
					this.shapes[i] = LineRasterizer.decode(packet);
				} else if (shapeType == 1) {
					this.shapes[i] = BezierCurveRasterizer.decode(packet);
				} else if (shapeType == 2) {
					this.shapes[i] = TextureOpShapeRaster.decodeRectangle(packet);
				} else if (shapeType == 3) {
					this.shapes[i] = EllipseRasterizer.decode(packet);
				}
			}
		} else if (opcode == 1) {
			super.monochrome = packet.readUByte() == 1;
		}
	}

	@OriginalMember(owner = "client!ft", name = "d", descriptor = "(II)[[I")
	@Override
	public int[][] getColorOutput(@OriginalArg(0) int count) {
		@Pc(13) int[][] output = super.colorImageCache.get(count);
		if (super.colorImageCache.dirty) {
			@Pc(24) int textureOpWidth = EnvironmentLight.textureOpWidth;
			@Pc(26) int textureOpHeight = TextureOperation.currentHeight;
			@Pc(30) int[][] pixels = new int[textureOpHeight][textureOpWidth];
			@Pc(35) int[][][] rgbData = super.colorImageCache.getAllPalettes();
			this.render(pixels);
			for (@Pc(41) int i = 0; i < TextureOperation.currentHeight; i++) {
				@Pc(47) int[] pixelRow = pixels[i];
				@Pc(51) int[][] rgbRow = rgbData[i];
				@Pc(55) int[] redChannel = rgbRow[0];
				@Pc(59) int[] greenChannel = rgbRow[1];
				@Pc(63) int[] blueChannel = rgbRow[2];
				for (@Pc(65) int j = 0; j < EnvironmentLight.textureOpWidth; j++) {
					@Pc(71) int rgb = pixelRow[j];
					blueChannel[j] = (rgb & 0xFF) << 4;
					greenChannel[j] = rgb >> 4 & 0xFF0;
					redChannel[j] = rgb >> 12 & 0xFF0;
				}
			}
		}
		return output;
	}
	@OriginalMember(owner = "client!mk", name = "a", descriptor = "(IIIII)V")
	public static void setClipBounds(@OriginalArg(0) int maxX, @OriginalArg(2) int maxY) {
		TextureOpRasterizer.textureOpClipMinX = 0;
		TextureOpRasterizer.clipMaxY = maxY;
		TextureOpRasterizer.clipMaxX = maxX;
		TextureOpRasterizer.clipMinY = 0;
	}

	@OriginalMember(owner = "client!bf", name = "g", descriptor = "I")
	public static int clipMaxX = 100;
	@OriginalMember(owner = "client!fn", name = "b", descriptor = "I")
	public static int textureOpClipMinX = 0;

	@OriginalMember(owner = "client!iea", name = "q", descriptor = "I")
	public static int clipMaxY = 100;

	@OriginalMember(owner = "client!nr", name = "f", descriptor = "I")
	public static int clipMinY = 0;
}
