package com.jagex.game.runetek5.encoding;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!wda")
public final class Huffman {

	@OriginalMember(owner = "client!wda", name = "b", descriptor = "[I")
	private final int[] codes;

	@OriginalMember(owner = "client!wda", name = "e", descriptor = "[B")
	private final byte[] codeLengths;

	@OriginalMember(owner = "client!wda", name = "j", descriptor = "[I")
	private int[] symbolTree;

	@OriginalMember(owner = "client!wda", name = "<init>", descriptor = "([B)V")
	public Huffman(@OriginalArg(0) byte[] bits) {
		@Pc(6) int length = bits.length;
		this.codes = new int[length];
		this.codeLengths = bits;
		this.symbolTree = new int[8];
		@Pc(20) int[] nextCodes = new int[33];
		@Pc(22) int treeSize = 0;
		for (@Pc(24) int i = 0; i < length; i++) {
			@Pc(30) byte codeLength = bits[i];
			if (codeLength != 0) {
				@Pc(38) int lengthBit = 0x1 << 32 - codeLength;
				@Pc(42) int code = nextCodes[codeLength];
				this.codes[i] = code;
				@Pc(60) int nextCode;
				@Pc(66) int bitLength;
				@Pc(72) int local72;
				@Pc(84) int local84;
				if ((lengthBit & code) == 0) {
					for (bitLength = codeLength - 1; bitLength >= 1; bitLength--) {
						int shorterCode = nextCodes[bitLength];
						if (shorterCode != code) {
							break;
						}
						int shorterLengthBit = 0x1 << 32 - bitLength;
						if ((shorterCode & shorterLengthBit) != 0) {
							nextCodes[bitLength] = nextCodes[bitLength - 1];
							break;
						}
						nextCodes[bitLength] = shorterCode | shorterLengthBit;
					}
					nextCode = lengthBit | code;
				} else {
					nextCode = nextCodes[codeLength - 1];
				}
				nextCodes[codeLength] = nextCode;
				for (bitLength = codeLength + 1; bitLength <= 32; bitLength++) {
					if (nextCodes[bitLength] == code) {
						nextCodes[bitLength] = nextCode;
					}
				}
				int treeIndex = 0;
				for (int bitIndex = 0; bitIndex < codeLength; bitIndex++) {
					@Pc(154) int bitMask = Integer.MIN_VALUE >>> bitIndex;
					if ((code & bitMask) == 0) {
						treeIndex++;
					} else {
						if (this.symbolTree[treeIndex] == 0) {
							this.symbolTree[treeIndex] = treeSize;
						}
						treeIndex = this.symbolTree[treeIndex];
					}
					if (treeIndex >= this.symbolTree.length) {
						@Pc(196) int[] newTree = new int[this.symbolTree.length * 2];
						for (@Pc(198) int j = 0; j < this.symbolTree.length; j++) {
							newTree[j] = this.symbolTree[j];
						}
						this.symbolTree = newTree;
					}
				}
				this.symbolTree[treeIndex] = ~i;
				if (treeSize <= treeIndex) {
					treeSize = treeIndex + 1;
				}
			}
		}
	}

	@OriginalMember(owner = "client!wda", name = "a", descriptor = "([BI[BIII)I")
	public int decompress(@OriginalArg(0) byte[] output, @OriginalArg(1) int inputOffset, @OriginalArg(2) byte[] input, @OriginalArg(3) int outputLength, @OriginalArg(5) int outputOffset) {
		if (outputLength == 0) {
			return 0;
		}
		@Pc(13) int outputEnd = outputLength;
		@Pc(15) int treeIndex = 0;
		@Pc(23) int inputIndex = inputOffset;
		while (true) {
			@Pc(27) byte flags = input[inputIndex];
			if (flags >= 0) {
				treeIndex++;
			} else {
				treeIndex = this.symbolTree[treeIndex];
			}
			@Pc(46) int entry1;
			if ((entry1 = this.symbolTree[treeIndex]) < 0) {
				output[outputOffset++] = (byte) ~entry1;
				if (outputOffset >= outputEnd) {
					break;
				}
				treeIndex = 0;
			}
			if ((flags & 0x40) == 0) {
				treeIndex++;
			} else {
				treeIndex = this.symbolTree[treeIndex];
			}
			@Pc(88) int entry2;
			if ((entry2 = this.symbolTree[treeIndex]) < 0) {
				output[outputOffset++] = (byte) ~entry2;
				if (outputOffset >= outputEnd) {
					break;
				}
				treeIndex = 0;
			}
			if ((flags & 0x20) == 0) {
				treeIndex++;
			} else {
				treeIndex = this.symbolTree[treeIndex];
			}
			@Pc(126) int entry3;
			if ((entry3 = this.symbolTree[treeIndex]) < 0) {
				output[outputOffset++] = (byte) ~entry3;
				if (outputOffset >= outputEnd) {
					break;
				}
				treeIndex = 0;
			}
			if ((flags & 0x10) == 0) {
				treeIndex++;
			} else {
				treeIndex = this.symbolTree[treeIndex];
			}
			@Pc(161) int entry4;
			if ((entry4 = this.symbolTree[treeIndex]) < 0) {
				output[outputOffset++] = (byte) ~entry4;
				if (outputEnd <= outputOffset) {
					break;
				}
				treeIndex = 0;
			}
			if ((flags & 0x8) == 0) {
				treeIndex++;
			} else {
				treeIndex = this.symbolTree[treeIndex];
			}
			@Pc(203) int entry5;
			if ((entry5 = this.symbolTree[treeIndex]) < 0) {
				output[outputOffset++] = (byte) ~entry5;
				if (outputEnd <= outputOffset) {
					break;
				}
				treeIndex = 0;
			}
			if ((flags & 0x4) == 0) {
				treeIndex++;
			} else {
				treeIndex = this.symbolTree[treeIndex];
			}
			@Pc(238) int entry6;
			if ((entry6 = this.symbolTree[treeIndex]) < 0) {
				output[outputOffset++] = (byte) ~entry6;
				if (outputEnd <= outputOffset) {
					break;
				}
				treeIndex = 0;
			}
			if ((flags & 0x2) == 0) {
				treeIndex++;
			} else {
				treeIndex = this.symbolTree[treeIndex];
			}
			@Pc(270) int entry7;
			if ((entry7 = this.symbolTree[treeIndex]) < 0) {
				output[outputOffset++] = (byte) ~entry7;
				if (outputOffset >= outputEnd) {
					break;
				}
				treeIndex = 0;
			}
			if ((flags & 0x1) == 0) {
				treeIndex++;
			} else {
				treeIndex = this.symbolTree[treeIndex];
			}
			@Pc(306) int entry8;
			if ((entry8 = this.symbolTree[treeIndex]) < 0) {
				output[outputOffset++] = (byte) ~entry8;
				if (outputOffset >= outputEnd) {
					break;
				}
				treeIndex = 0;
			}
			inputIndex++;
		}
		return inputIndex + 1 - inputOffset;
	}

	@OriginalMember(owner = "client!wda", name = "a", descriptor = "(III[BI[B)I")
	public int compress(@OriginalArg(0) int inputEnd, @OriginalArg(1) int inputOffset, @OriginalArg(2) int outputOffset, @OriginalArg(3) byte[] input, @OriginalArg(5) byte[] output) {
		@Pc(7) int bitBuffer = 0;
		@Pc(11) int end = inputEnd;
		@Pc(15) int bitPosition = outputOffset << 3;
		while (inputOffset < end) {
			@Pc(35) int symbol = input[inputOffset] & 0xFF;
			@Pc(40) int code = this.codes[symbol];
			@Pc(45) byte codeLength = this.codeLengths[symbol];
			if (codeLength == 0) {
				throw new RuntimeException("No codeword for data value " + symbol);
			}
			@Pc(62) int byteIndex = bitPosition >> 3;
			@Pc(66) int bitOffset = bitPosition & 0x7;
			bitBuffer &= -bitOffset >> 31;
			@Pc(84) int lastByte = (bitOffset + codeLength - 1 >> 3) + byteIndex;
			bitPosition += codeLength;
			@Pc(89) int shift = bitOffset + 24;
			output[byteIndex] = (byte) (bitBuffer |= code >>> shift);
			if (byteIndex < lastByte) {
				byteIndex++;
				bitOffset = shift - 8;
				output[byteIndex] = (byte) (bitBuffer = code >>> bitOffset);
				if (lastByte > byteIndex) {
					byteIndex++;
					bitOffset -= 8;
					output[byteIndex] = (byte) (bitBuffer = code >>> bitOffset);
					if (lastByte > byteIndex) {
						bitOffset -= 8;
						byteIndex++;
						output[byteIndex] = (byte) (bitBuffer = code >>> bitOffset);
						if (byteIndex < lastByte) {
							bitOffset -= 8;
							byteIndex++;
							output[byteIndex] = (byte) (bitBuffer = code << -bitOffset);
						}
					}
				}
			}
			inputOffset++;
		}
		return (bitPosition + 7 >> 3) - outputOffset;
	}
}
