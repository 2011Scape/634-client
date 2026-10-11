package com.jagex.game.runetek5.config.material.texture.operation;

import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.EnvironmentLight;
import com.jagex.game.runetek5.config.material.texture.TextureOperation;
import java.util.Random;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.material.texture.TextureRaster;
import com.jagex.game.runetek5.utils.ArrayUtils;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!hn")
public final class TextureOpVoronoiNoise extends TextureOperation {

	@OriginalMember(owner = "client!hn", name = "I", descriptor = "[S")
	private short[] pointPositions = new short[512];

	@OriginalMember(owner = "client!hn", name = "K", descriptor = "I")
	private int distanceMetric = 1;

	@OriginalMember(owner = "client!hn", name = "L", descriptor = "[B")
	private byte[] permutationTable = new byte[512];

	@OriginalMember(owner = "client!hn", name = "R", descriptor = "I")
	private int pointJitter = 2048;

	@OriginalMember(owner = "client!hn", name = "Q", descriptor = "I")
	private int distanceOutputMode = 2;

	@OriginalMember(owner = "client!hn", name = "O", descriptor = "I")
	private int randomSeed = 0;

	@OriginalMember(owner = "client!hn", name = "J", descriptor = "I")
	private int cellCountY = 5;

	@OriginalMember(owner = "client!hn", name = "V", descriptor = "I")
	private int cellCountX = 5;

	@OriginalMember(owner = "client!hn", name = "<init>", descriptor = "()V")
	public TextureOpVoronoiNoise() {
		super(0, true);
	}

	@OriginalMember(owner = "client!hn", name = "b", descriptor = "(Z)V")
	private void initializePointPositions() {
		@Pc(16) Random random = new Random((long) this.randomSeed);
		this.pointPositions = new short[512];
		if (this.pointJitter > 0) {
			for (@Pc(28) int i = 0; i < 512; i++) {
				this.pointPositions[i] = (short) Utils.randomInt(this.pointJitter, random);
			}
		}
	}

	@OriginalMember(owner = "client!hn", name = "e", descriptor = "(I)V")
	@Override
	public void postDecode() {
		this.permutationTable = ArrayUtils.getPermutationTable(this.randomSeed);
		this.initializePointPositions();
	}

	@OriginalMember(owner = "client!hn", name = "c", descriptor = "(II)[I")
	@Override
	public int[] monochromeOutput(@OriginalArg(0) int row) {
		@Pc(11) int[] output = super.monochromeImageCache.get(row);
		if (super.monochromeImageCache.dirty) {
			@Pc(30) int verticalPos = this.cellCountY * TextureRaster.verticalTable[row] + 2048;
			@Pc(34) int verticalCell = verticalPos >> 12;
			@Pc(38) int nextVerticalCell = verticalCell + 1;
			for (@Pc(40) int i = 0; i < EnvironmentLight.textureOpWidth; i++) {
				TextureOpVoronoiNoise.fourthNearestDistance = Integer.MAX_VALUE;
				TextureOpVoronoiNoise.thirdNearestDistance = Integer.MAX_VALUE;
				TextureOpVoronoiNoise.secondNearestDistance = Integer.MAX_VALUE;
				TextureOpVoronoiNoise.nearestDistance = Integer.MAX_VALUE;
				@Pc(60) int horizontalPos = EnvironmentLight.horizontalTable[i] * this.cellCountX + 2048;
				@Pc(64) int horizontalCell = horizontalPos >> 12;
				@Pc(68) int nextHorizontalCell = horizontalCell + 1;
				@Pc(151) int distanceMetric;
				for (@Pc(72) int j = verticalCell - 1; j <= nextVerticalCell; j++) {
					@Pc(93) int rowPermutation = this.permutationTable[(j >= this.cellCountY ? j - this.cellCountY : j) & 0xFF] & 0xFF;
					for (@Pc(97) int k = horizontalCell - 1; k <= nextHorizontalCell; k++) {
						@Pc(122) int pointIndexX = (this.permutationTable[(this.cellCountX > k ? k : k - this.cellCountX) + rowPermutation & 0xFF] & 0xFF) * 2;
						@Pc(126) int negCellX = -(k << 12);
						@Pc(130) int pointIndexY = pointIndexX + 1;
						@Pc(135) int deltaX = negCellX + horizontalPos - this.pointPositions[pointIndexX];
						@Pc(148) int deltaY = verticalPos - (j << 12) - this.pointPositions[pointIndexY];
						distanceMetric = this.distanceMetric;
						@Pc(184) int distance;
						if (distanceMetric == 1) {
							distance = deltaY * deltaY + deltaX * deltaX >> 12;
						} else if (distanceMetric == 3) {
							deltaX = deltaX < 0 ? -deltaX : deltaX;
							deltaY = deltaY < 0 ? -deltaY : deltaY;
							distance = deltaX <= deltaY ? deltaY : deltaX;
						} else if (distanceMetric == 4) {
							deltaX = (int) (Math.sqrt((double) ((float) (deltaX < 0 ? -deltaX : deltaX) / 4096.0F)) * 4096.0D);
							deltaY = (int) (Math.sqrt((double) ((float) (deltaY < 0 ? -deltaY : deltaY) / 4096.0F)) * 4096.0D);
							distance = deltaY + deltaX;
							distance = distance * distance >> 12;
						} else if (distanceMetric == 5) {
							deltaY *= deltaY;
							deltaX *= deltaX;
							distance = (int) (Math.sqrt(Math.sqrt((double) ((float) (deltaX + deltaY) / 1.6777216E7F))) * 4096.0D);
						} else if (distanceMetric == 2) {
							distance = (deltaX < 0 ? -deltaX : deltaX) + (deltaY >= 0 ? deltaY : -deltaY);
						} else {
							distance = (int) (Math.sqrt((double) ((float) (deltaX * deltaX + deltaY * deltaY) / 1.6777216E7F)) * 4096.0D);
						}
						if (TextureOpVoronoiNoise.nearestDistance > distance) {
							TextureOpVoronoiNoise.fourthNearestDistance = TextureOpVoronoiNoise.thirdNearestDistance;
							TextureOpVoronoiNoise.thirdNearestDistance = TextureOpVoronoiNoise.secondNearestDistance;
							TextureOpVoronoiNoise.secondNearestDistance = TextureOpVoronoiNoise.nearestDistance;
							TextureOpVoronoiNoise.nearestDistance = distance;
						} else if (TextureOpVoronoiNoise.secondNearestDistance > distance) {
							TextureOpVoronoiNoise.fourthNearestDistance = TextureOpVoronoiNoise.thirdNearestDistance;
							TextureOpVoronoiNoise.thirdNearestDistance = TextureOpVoronoiNoise.secondNearestDistance;
							TextureOpVoronoiNoise.secondNearestDistance = distance;
						} else if (TextureOpVoronoiNoise.thirdNearestDistance > distance) {
							TextureOpVoronoiNoise.fourthNearestDistance = TextureOpVoronoiNoise.thirdNearestDistance;
							TextureOpVoronoiNoise.thirdNearestDistance = distance;
						} else if (TextureOpVoronoiNoise.fourthNearestDistance > distance) {
							TextureOpVoronoiNoise.fourthNearestDistance = distance;
						}
					}
				}
				distanceMetric = this.distanceOutputMode;
				if (distanceMetric == 0) {
					output[i] = TextureOpVoronoiNoise.nearestDistance;
				} else if (distanceMetric == 1) {
					output[i] = TextureOpVoronoiNoise.secondNearestDistance;
				} else if (distanceMetric == 3) {
					output[i] = TextureOpVoronoiNoise.thirdNearestDistance;
				} else if (distanceMetric == 4) {
					output[i] = TextureOpVoronoiNoise.fourthNearestDistance;
				} else if (distanceMetric == 2) {
					output[i] = TextureOpVoronoiNoise.secondNearestDistance - TextureOpVoronoiNoise.nearestDistance;
				}
			}
		}
		return output;
	}

	@OriginalMember(owner = "client!hn", name = "a", descriptor = "(Lclient!cea;II)V")
	@Override
	public void decode(@OriginalArg(0) Packet packet, @OriginalArg(1) int opcode) {
		if (opcode == 0) {
			this.cellCountX = this.cellCountY = packet.readUByte();
		} else if (opcode == 1) {
			this.randomSeed = packet.readUByte();
		} else if (opcode == 2) {
			this.pointJitter = packet.readUShort();
		} else if (opcode == 3) {
			this.distanceOutputMode = packet.readUByte();
		} else if (opcode == 4) {
			this.distanceMetric = packet.readUByte();
		} else if (opcode == 5) {
			this.cellCountX = packet.readUByte();
		} else if (opcode == 6) {
			this.cellCountY = packet.readUByte();
		}
	}
	@OriginalMember(owner = "client!jt", name = "t", descriptor = "I")
	public static int fourthNearestDistance;
	@OriginalMember(owner = "client!jo", name = "J", descriptor = "I")
	public static int nearestDistance;
	@OriginalMember(owner = "client!og", name = "c", descriptor = "I")
	public static int thirdNearestDistance;
	@OriginalMember(owner = "client!rs", name = "b", descriptor = "I")
	public static int secondNearestDistance;
}
