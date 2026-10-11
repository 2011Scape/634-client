package com.jagex.game.runetek5.config.midi.instrument;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.VorbisSound;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!rm")
public final class MusicSampleFloor {

	@OriginalMember(owner = "client!rm", name = "g", descriptor = "[I")
	private final int[] partitionClassList;

	@OriginalMember(owner = "client!rm", name = "b", descriptor = "[I")
	private final int[] classDimensions;

	@OriginalMember(owner = "client!rm", name = "k", descriptor = "[I")
	private final int[] classSubclasses;

	@OriginalMember(owner = "client!rm", name = "e", descriptor = "[I")
	private final int[] classMasterbooks;

	@OriginalMember(owner = "client!rm", name = "l", descriptor = "[[I")
	private final int[][] subclassBooks;

	@OriginalMember(owner = "client!rm", name = "d", descriptor = "I")
	private final int floor1Multiplier;

	@OriginalMember(owner = "client!rm", name = "h", descriptor = "[I")
	private final int[] xList;

	@OriginalMember(owner = "client!rm", name = "<init>", descriptor = "()V")
	public MusicSampleFloor() {
		@Pc(4) int floorType = MusicSample.getBits(16);
		if (floorType != 1) {
			throw new RuntimeException();
		}
		@Pc(14) int partitions = MusicSample.getBits(5);
		@Pc(16) int maxClass = 0;
		this.partitionClassList = new int[partitions];
		@Pc(26) int classIndex;
		for (@Pc(22) int i = 0; i < partitions; i++) {
			classIndex = MusicSample.getBits(4);
			this.partitionClassList[i] = classIndex;
			if (classIndex >= maxClass) {
				maxClass = classIndex + 1;
			}
		}
		this.classDimensions = new int[maxClass];
		this.classSubclasses = new int[maxClass];
		this.classMasterbooks = new int[maxClass];
		this.subclassBooks = new int[maxClass][];
		@Pc(77) int local77;
		@Pc(99) int local99;
		for (classIndex = 0; classIndex < maxClass; classIndex++) {
			this.classDimensions[classIndex] = MusicSample.getBits(3) + 1;
			int subclassCount = this.classSubclasses[classIndex] = MusicSample.getBits(2);
			if (subclassCount != 0) {
				this.classMasterbooks[classIndex] = MusicSample.getBits(8);
			}
			subclassCount = 0x1 << subclassCount;
			@Pc(92) int[] books = new int[subclassCount];
			this.subclassBooks[classIndex] = books;
			for (int bookIndex = 0; bookIndex < subclassCount; bookIndex++) {
				books[bookIndex] = MusicSample.getBits(8) - 1;
			}
		}
		this.floor1Multiplier = MusicSample.getBits(2) + 1;
		int rangeBits = MusicSample.getBits(4);
		@Pc(126) int values = 2;
		for (int partitionIndex = 0; partitionIndex < partitions; partitionIndex++) {
			values += this.classDimensions[this.partitionClassList[partitionIndex]];
		}
		this.xList = new int[values];
		this.xList[0] = 0;
		this.xList[1] = 0x1 << rangeBits;
		values = 2;
		for (@Pc(163) int j = 0; j < partitions; j++) {
			@Pc(169) int partitionClass = this.partitionClassList[j];
			for (@Pc(171) int k = 0; k < this.classDimensions[partitionClass]; k++) {
				this.xList[values++] = MusicSample.getBits(rangeBits);
			}
		}
		if (MusicSampleFloor.finalX == null || MusicSampleFloor.finalX.length < values) {
			MusicSampleFloor.finalX = new int[values];
			MusicSampleFloor.finalY = new int[values];
			MusicSampleFloor.step2Flag = new boolean[values];
		}
	}

	@OriginalMember(owner = "client!rm", name = "a", descriptor = "([FI)V")
	public void computeFloor(@OriginalArg(0) float[] target, @OriginalArg(1) int halfBlockSize) {
		@Pc(3) int length = this.xList.length;
		@Pc(10) int range = MusicSampleFloor.RANGE_LIST[this.floor1Multiplier - 1];
		MusicSampleFloor.step2Flag[0] = MusicSampleFloor.step2Flag[1] = true;
		@Pc(25) int local25;
		@Pc(29) int local29;
		@Pc(47) int local47;
		@Pc(51) int local51;
		@Pc(55) int local55;
		for (@Pc(20) int targetIndex = 2; targetIndex < length; targetIndex++) {
			int lowIndex = MusicSampleFloor.lowNeighbor(MusicSampleFloor.finalX, targetIndex);
			int highIndex = MusicSampleFloor.highNeighbor(MusicSampleFloor.finalX, targetIndex);
			int predicted = this.predictPoint(MusicSampleFloor.finalX[lowIndex], MusicSampleFloor.finalY[lowIndex], MusicSampleFloor.finalX[highIndex], MusicSampleFloor.finalY[highIndex], MusicSampleFloor.finalX[targetIndex]);
			int encodedY = MusicSampleFloor.finalY[targetIndex];
			int highRoom = range - predicted;
			@Pc(66) int room = (highRoom < predicted ? highRoom : predicted) << 1;
			if (encodedY == 0) {
				MusicSampleFloor.step2Flag[targetIndex] = false;
				MusicSampleFloor.finalY[targetIndex] = predicted;
			} else {
				MusicSampleFloor.step2Flag[lowIndex] = MusicSampleFloor.step2Flag[highIndex] = true;
				MusicSampleFloor.step2Flag[targetIndex] = true;
				if (encodedY >= room) {
					MusicSampleFloor.finalY[targetIndex] = highRoom > predicted ? encodedY + predicted - predicted : predicted - encodedY + highRoom - 1;
				} else {
					MusicSampleFloor.finalY[targetIndex] = (encodedY & 0x1) == 0 ? predicted + encodedY / 2 : predicted - (encodedY + 1) / 2;
				}
			}
		}
		this.sortByX(0, length - 1);
		int prevX = 0;
		int prevY = MusicSampleFloor.finalY[0] * this.floor1Multiplier;
		for (int pointIndex = 1; pointIndex < length; pointIndex++) {
			if (MusicSampleFloor.step2Flag[pointIndex]) {
				int currentX = MusicSampleFloor.finalX[pointIndex];
				int currentY = MusicSampleFloor.finalY[pointIndex] * this.floor1Multiplier;
				this.drawLine(prevX, prevY, currentX, currentY, target, halfBlockSize);
				if (currentX >= halfBlockSize) {
					return;
				}
				prevX = currentX;
				prevY = currentY;
			}
		}
		@Pc(193) float scale = MusicSampleFloor.INVERSE_DB_TABLE[prevY];
		for (int sampleIndex = prevX; sampleIndex < halfBlockSize; sampleIndex++) {
			target[sampleIndex] *= scale;
		}
	}

	@OriginalMember(owner = "client!rm", name = "a", descriptor = "(IIII[FI)V")
	private void drawLine(@OriginalArg(0) int lowX, @OriginalArg(1) int highX, @OriginalArg(2) int lowY, @OriginalArg(3) int highY, @OriginalArg(4) float[] output, @OriginalArg(5) int limit) {
		@Pc(3) int dy = highY - highX;
		@Pc(7) int adx = lowY - lowX;
		@Pc(14) int ady = dy < 0 ? -dy : dy;
		@Pc(18) int base = dy / adx;
		@Pc(20) int y = highX;
		@Pc(22) int err = 0;
		@Pc(32) int sy = dy < 0 ? base - 1 : base + 1;
		@Pc(43) int ady2 = ady - (base < 0 ? -base : base) * adx;
		output[lowX] *= MusicSampleFloor.INVERSE_DB_TABLE[highX];
		if (lowY > limit) {
			lowY = limit;
		}
		for (@Pc(61) int x = lowX + 1; x < lowY; x++) {
			err += ady2;
			if (err >= adx) {
				err -= adx;
				y += sy;
			} else {
				y += base;
			}
			output[x] *= MusicSampleFloor.INVERSE_DB_TABLE[y];
		}
	}

	@OriginalMember(owner = "client!rm", name = "a", descriptor = "(IIIII)I")
	private int predictPoint(@OriginalArg(0) int x0, @OriginalArg(1) int y0, @OriginalArg(2) int x1, @OriginalArg(3) int y1, @OriginalArg(4) int targetX) {
		@Pc(3) int deltaY = y1 - y0;
		@Pc(7) int deltaX = x1 - x0;
		@Pc(14) int absDeltaY = deltaY < 0 ? -deltaY : deltaY;
		@Pc(20) int err = absDeltaY * (targetX - x0);
		@Pc(24) int off = err / deltaX;
		return deltaY < 0 ? y0 - off : y0 + off;
	}

	@OriginalMember(owner = "client!rm", name = "a", descriptor = "()Z")
	public boolean decode() {
		@Pc(5) boolean nonzero = VorbisSound.getBit() != 0;
		if (!nonzero) {
			return false;
		}
		@Pc(13) int values = this.xList.length;
		for (@Pc(15) int i = 0; i < values; i++) {
			MusicSampleFloor.finalX[i] = this.xList[i];
		}
		@Pc(34) int range = MusicSampleFloor.RANGE_LIST[this.floor1Multiplier - 1];
		@Pc(40) int rangeBits = Utils.bitLength(range - 1);
		MusicSampleFloor.finalY[0] = MusicSample.getBits(rangeBits);
		MusicSampleFloor.finalY[1] = MusicSample.getBits(rangeBits);
		@Pc(52) int off = 2;
		for (@Pc(54) int k = 0; k < this.partitionClassList.length; k++) {
			@Pc(60) int partitionClass = this.partitionClassList[k];
			@Pc(65) int partitionDimensions = this.classDimensions[partitionClass];
			@Pc(70) int partitionSubclass = this.classSubclasses[partitionClass];
			@Pc(76) int mask = (0x1 << partitionSubclass) - 1;
			@Pc(78) int codebook = 0;
			if (partitionSubclass > 0) {
				codebook = MusicSample.codebooks[this.classMasterbooks[partitionClass]].decode();
			}
			for (@Pc(90) int j = 0; j < partitionDimensions; j++) {
				@Pc(100) int book = this.subclassBooks[partitionClass][codebook & mask];
				codebook >>>= partitionSubclass;
				MusicSampleFloor.finalY[off++] = book >= 0 ? MusicSample.codebooks[book].decode() : 0;
			}
		}
		return true;
	}

	@OriginalMember(owner = "client!rm", name = "a", descriptor = "(II)V")
	private void sortByX(@OriginalArg(0) int low, @OriginalArg(1) int high) {
		if (low >= high) {
			return;
		}
		@Pc(5) int pivot = low;
		@Pc(9) int pivotX = MusicSampleFloor.finalX[low];
		@Pc(13) int pivotY = MusicSampleFloor.finalY[low];
		@Pc(17) boolean pivotStep = MusicSampleFloor.step2Flag[low];
		for (@Pc(21) int i = low + 1; i <= high; i++) {
			@Pc(26) int xValue = MusicSampleFloor.finalX[i];
			if (xValue < pivotX) {
				MusicSampleFloor.finalX[pivot] = xValue;
				MusicSampleFloor.finalY[pivot] = MusicSampleFloor.finalY[i];
				MusicSampleFloor.step2Flag[pivot] = MusicSampleFloor.step2Flag[i];
				pivot++;
				MusicSampleFloor.finalX[i] = MusicSampleFloor.finalX[pivot];
				MusicSampleFloor.finalY[i] = MusicSampleFloor.finalY[pivot];
				MusicSampleFloor.step2Flag[i] = MusicSampleFloor.step2Flag[pivot];
			}
		}
		MusicSampleFloor.finalX[pivot] = pivotX;
		MusicSampleFloor.finalY[pivot] = pivotY;
		MusicSampleFloor.step2Flag[pivot] = pivotStep;
		this.sortByX(low, pivot - 1);
		this.sortByX(pivot + 1, high);
	}

	@OriginalMember(owner = "client!rm", name = "a", descriptor = "[F")
	public static final float[] INVERSE_DB_TABLE = new float[] { 1.0649863E-7F, 1.1341951E-7F, 1.2079015E-7F, 1.2863978E-7F, 1.369995E-7F, 1.459025E-7F, 1.5538409E-7F, 1.6548181E-7F, 1.7623574E-7F, 1.8768856E-7F, 1.998856E-7F, 2.128753E-7F, 2.2670913E-7F, 2.4144197E-7F, 2.5713223E-7F, 2.7384212E-7F, 2.9163792E-7F, 3.1059022E-7F, 3.307741E-7F, 3.5226967E-7F, 3.7516213E-7F, 3.995423E-7F, 4.255068E-7F, 4.5315863E-7F, 4.8260745E-7F, 5.1397E-7F, 5.4737063E-7F, 5.829419E-7F, 6.208247E-7F, 6.611694E-7F, 7.041359E-7F, 7.4989464E-7F, 7.98627E-7F, 8.505263E-7F, 9.057983E-7F, 9.646621E-7F, 1.0273513E-6F, 1.0941144E-6F, 1.1652161E-6F, 1.2409384E-6F, 1.3215816E-6F, 1.4074654E-6F, 1.4989305E-6F, 1.5963394E-6F, 1.7000785E-6F, 1.8105592E-6F, 1.9282195E-6F, 2.053526E-6F, 2.1869757E-6F, 2.3290977E-6F, 2.4804558E-6F, 2.6416496E-6F, 2.813319E-6F, 2.9961443E-6F, 3.1908505E-6F, 3.39821E-6F, 3.619045E-6F, 3.8542307E-6F, 4.1047006E-6F, 4.371447E-6F, 4.6555283E-6F, 4.958071E-6F, 5.280274E-6F, 5.623416E-6F, 5.988857E-6F, 6.3780467E-6F, 6.7925284E-6F, 7.2339453E-6F, 7.704048E-6F, 8.2047E-6F, 8.737888E-6F, 9.305725E-6F, 9.910464E-6F, 1.0554501E-5F, 1.1240392E-5F, 1.1970856E-5F, 1.2748789E-5F, 1.3577278E-5F, 1.4459606E-5F, 1.5399271E-5F, 1.6400005E-5F, 1.7465769E-5F, 1.8600793E-5F, 1.9809577E-5F, 2.1096914E-5F, 2.2467912E-5F, 2.3928002E-5F, 2.5482977E-5F, 2.7139005E-5F, 2.890265E-5F, 3.078091E-5F, 3.2781227E-5F, 3.4911533E-5F, 3.718028E-5F, 3.9596467E-5F, 4.2169668E-5F, 4.491009E-5F, 4.7828602E-5F, 5.0936775E-5F, 5.424693E-5F, 5.7772202E-5F, 6.152657E-5F, 6.552491E-5F, 6.9783084E-5F, 7.4317984E-5F, 7.914758E-5F, 8.429104E-5F, 8.976875E-5F, 9.560242E-5F, 1.0181521E-4F, 1.0843174E-4F, 1.1547824E-4F, 1.2298267E-4F, 1.3097477E-4F, 1.3948625E-4F, 1.4855085E-4F, 1.5820454E-4F, 1.6848555E-4F, 1.7943469E-4F, 1.9109536E-4F, 2.0351382E-4F, 2.167393E-4F, 2.3082423E-4F, 2.4582449E-4F, 2.6179955E-4F, 2.7881275E-4F, 2.9693157E-4F, 3.1622787E-4F, 3.3677815E-4F, 3.5866388E-4F, 3.8197188E-4F, 4.0679457E-4F, 4.3323037E-4F, 4.613841E-4F, 4.913675E-4F, 5.2329927E-4F, 5.573062E-4F, 5.935231E-4F, 6.320936E-4F, 6.731706E-4F, 7.16917E-4F, 7.635063E-4F, 8.1312325E-4F, 8.6596457E-4F, 9.2223985E-4F, 9.821722E-4F, 0.0010459992F, 0.0011139743F, 0.0011863665F, 0.0012634633F, 0.0013455702F, 0.0014330129F, 0.0015261382F, 0.0016253153F, 0.0017309374F, 0.0018434235F, 0.0019632196F, 0.0020908006F, 0.0022266726F, 0.0023713743F, 0.0025254795F, 0.0026895993F, 0.0028643848F, 0.0030505287F, 0.003248769F, 0.0034598925F, 0.0036847359F, 0.0039241905F, 0.0041792067F, 0.004450795F, 0.004740033F, 0.005048067F, 0.0053761187F, 0.005725489F, 0.0060975635F, 0.0064938175F, 0.0069158226F, 0.0073652514F, 0.007843887F, 0.008353627F, 0.008896492F, 0.009474637F, 0.010090352F, 0.01074608F, 0.011444421F, 0.012188144F, 0.012980198F, 0.013823725F, 0.014722068F, 0.015678791F, 0.016697686F, 0.017782796F, 0.018938422F, 0.020169148F, 0.021479854F, 0.022875736F, 0.02436233F, 0.025945531F, 0.027631618F, 0.029427277F, 0.031339627F, 0.03337625F, 0.035545226F, 0.037855156F, 0.0403152F, 0.042935107F, 0.045725275F, 0.048696756F, 0.05186135F, 0.05523159F, 0.05882085F, 0.062643364F, 0.06671428F, 0.07104975F, 0.075666964F, 0.08058423F, 0.08582105F, 0.09139818F, 0.097337745F, 0.1036633F, 0.11039993F, 0.11757434F, 0.12521498F, 0.13335215F, 0.14201812F, 0.15124726F, 0.16107617F, 0.1715438F, 0.18269168F, 0.19456401F, 0.20720787F, 0.22067343F, 0.23501402F, 0.25028655F, 0.26655158F, 0.28387362F, 0.3023213F, 0.32196787F, 0.34289113F, 0.36517414F, 0.3889052F, 0.41417846F, 0.44109413F, 0.4697589F, 0.50028646F, 0.53279793F, 0.5674221F, 0.6042964F, 0.64356697F, 0.6853896F, 0.72993004F, 0.777365F, 0.8278826F, 0.88168305F, 0.9389798F, 1.0F };

	@OriginalMember(owner = "client!rm", name = "c", descriptor = "[I")
	public static int[] finalX;

	@OriginalMember(owner = "client!rm", name = "f", descriptor = "[I")
	public static final int[] RANGE_LIST = new int[] { 256, 128, 86, 64 };

	@OriginalMember(owner = "client!rm", name = "i", descriptor = "[Z")
	public static boolean[] step2Flag;

	@OriginalMember(owner = "client!rm", name = "j", descriptor = "[I")
	public static int[] finalY;
	@OriginalMember(owner = "client!rm", name = "a", descriptor = "([II)I")
	public static int highNeighbor(@OriginalArg(0) int[] xValues, @OriginalArg(1) int targetIndex) {
		@Pc(3) int vx = xValues[targetIndex];
		@Pc(5) int high = -1;
		@Pc(7) int value = Integer.MAX_VALUE;
		for (@Pc(9) int i = 0; i < targetIndex; i++) {
			@Pc(14) int vi = xValues[i];
			if (vi > vx && vi < value) {
				high = i;
				value = vi;
			}
		}
		return high;
	}

	@OriginalMember(owner = "client!rm", name = "b", descriptor = "([II)I")
	public static int lowNeighbor(@OriginalArg(0) int[] xValues, @OriginalArg(1) int targetIndex) {
		@Pc(3) int vx = xValues[targetIndex];
		@Pc(5) int low = -1;
		@Pc(7) int value = Integer.MIN_VALUE;
		for (@Pc(9) int i = 0; i < targetIndex; i++) {
			@Pc(14) int vi = xValues[i];
			if (vi < vx && vi > value) {
				low = i;
				value = vi;
			}
		}
		return low;
	}
}
