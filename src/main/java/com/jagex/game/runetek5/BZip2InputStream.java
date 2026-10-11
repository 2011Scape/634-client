package com.jagex.game.runetek5;

import com.jagex.game.runetek5.core.compress.BzipDecompressor;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.compression.Bzip2Compressor;

public final class BZip2InputStream {

	@OriginalMember(owner = "client!bs", name = "a", descriptor = "(ILclient!ig;)I")
	public static int readBits(@OriginalArg(0) int numBits, @OriginalArg(1) BzipDecompressor entry) {
		while (entry.bsLive < numBits) {
			entry.bsBuff = entry.bsBuff << 8 | entry.input[entry.strmNextInPtr] & 0xFF;
			entry.bsLive += 8;
			entry.strmNextInPtr++;
			entry.strmTotalInLo32++;
		}
		@Pc(17) int value = entry.bsBuff >> entry.bsLive - numBits & (0x1 << numBits) - 1;
		entry.bsLive -= numBits;
		return value;
	}

	@OriginalMember(owner = "client!bs", name = "a", descriptor = "(Lclient!ig;)B")
	public static byte readByte(@OriginalArg(0) BzipDecompressor entry) {
		return (byte) readBits(8, entry);
	}

	@OriginalMember(owner = "client!bs", name = "a", descriptor = "Lclient!ig;")
	public static final BzipDecompressor INSTANCE = new BzipDecompressor();

	@OriginalMember(owner = "client!bs", name = "b", descriptor = "(Lclient!ig;)V")
	public static void decodeBlock(@OriginalArg(0) BzipDecompressor entry) {
		@Pc(2) byte outCh = entry.stateOutCh;
		@Pc(5) int outLen = entry.stateOutLen;
		@Pc(8) int nblockused = entry.nblockused;
		@Pc(11) int k0 = entry.k0;
		@Pc(13) int[] transformTable = Bzip2Compressor.transformTable;
		@Pc(16) int tPos = entry.tPos;
		@Pc(19) byte[] output = entry.output;
		@Pc(22) int strmNextOutPtr = entry.strmNextOutPtr;
		@Pc(25) int availableOut = entry.availableOut;
		@Pc(27) int initialAvailableOut = availableOut;
		@Pc(32) int blockEnd = entry.saveNblock + 1;
		label57: while (true) {
			if (outLen > 0) {
				while (true) {
					if (availableOut == 0) {
						break label57;
					}
					if (outLen == 1) {
						if (availableOut == 0) {
							outLen = 1;
							break label57;
						}
						output[strmNextOutPtr] = outCh;
						strmNextOutPtr++;
						availableOut--;
						break;
					}
					output[strmNextOutPtr] = outCh;
					outLen--;
					strmNextOutPtr++;
					availableOut--;
				}
			}
			while (nblockused != blockEnd) {
				outCh = (byte) k0;
				tPos = transformTable[tPos];
				@Pc(74) byte nextByte = (byte) tPos;
				tPos >>= 0x8;
				nblockused++;
				if (nextByte == k0) {
					if (nblockused != blockEnd) {
						outLen = 2;
						tPos = transformTable[tPos];
						nextByte = (byte) tPos;
						tPos >>= 0x8;
						nblockused++;
						if (nblockused != blockEnd) {
							if (nextByte == k0) {
								outLen = 3;
								tPos = transformTable[tPos];
								nextByte = (byte) tPos;
								tPos >>= 0x8;
								nblockused++;
								if (nblockused != blockEnd) {
									if (nextByte == k0) {
										tPos = transformTable[tPos];
										nextByte = (byte) tPos;
										tPos >>= 0x8;
										nblockused++;
										outLen = (nextByte & 0xFF) + 4;
										tPos = transformTable[tPos];
										k0 = (byte) tPos;
										tPos >>= 0x8;
										nblockused++;
									} else {
										k0 = nextByte;
									}
								}
							} else {
								k0 = nextByte;
							}
						}
						continue label57;
					}
					if (availableOut == 0) {
						outLen = 1;
						break label57;
					}
					output[strmNextOutPtr] = outCh;
					strmNextOutPtr++;
					availableOut--;
				} else {
					k0 = nextByte;
					if (availableOut == 0) {
						outLen = 1;
						break label57;
					}
					output[strmNextOutPtr] = outCh;
					strmNextOutPtr++;
					availableOut--;
				}
			}
			outLen = 0;
			break;
		}
		@Pc(191) int strmTotalOutLo32 = entry.strmTotalOutLo32;
		entry.strmTotalOutLo32 += initialAvailableOut - availableOut;
		entry.stateOutCh = outCh;
		entry.stateOutLen = outLen;
		entry.nblockused = nblockused;
		entry.k0 = k0;
		Bzip2Compressor.transformTable = transformTable;
		entry.tPos = tPos;
		entry.output = output;
		entry.strmNextOutPtr = strmNextOutPtr;
		entry.availableOut = availableOut;
	}

	@OriginalMember(owner = "client!bs", name = "c", descriptor = "(Lclient!ig;)V")
	public static void buildSymbolMap(@OriginalArg(0) BzipDecompressor entry) {
		entry.nInUse = 0;
		for (@Pc(4) int i = 0; i < 256; i++) {
			if (entry.inUse[i]) {
				entry.seqToUnseq[entry.nInUse] = (byte) i;
				entry.nInUse++;
			}
		}
	}

	@OriginalMember(owner = "client!bs", name = "d", descriptor = "(Lclient!ig;)V")
	public static void decompress(@OriginalArg(0) BzipDecompressor decompressor) {
		decompressor.blockSize100k = 1;
		if (Bzip2Compressor.transformTable == null) {
			Bzip2Compressor.transformTable = new int[decompressor.blockSize100k * 100000];
		}
		@Pc(56) boolean needsNewBlock = true;
		while (true) {
			while (needsNewBlock) {
				@Pc(60) byte uc = readByte(decompressor);
				if (uc == 23) {
					return;
				}
				uc = readByte(decompressor);
				uc = readByte(decompressor);
				uc = readByte(decompressor);
				uc = readByte(decompressor);
				uc = readByte(decompressor);
				uc = readByte(decompressor);
				uc = readByte(decompressor);
				uc = readByte(decompressor);
				uc = readByte(decompressor);
				uc = readBit(decompressor);
				decompressor.origPtr = 0;
				uc = readByte(decompressor);
				decompressor.origPtr = decompressor.origPtr << 8 | uc & 0xFF;
				uc = readByte(decompressor);
				decompressor.origPtr = decompressor.origPtr << 8 | uc & 0xFF;
				uc = readByte(decompressor);
				decompressor.origPtr = decompressor.origPtr << 8 | uc & 0xFF;
				@Pc(138) int index;
				for (index = 0; index < 16; index++) {
					uc = readBit(decompressor);
					if (uc == 1) {
						decompressor.inUse16[index] = true;
					} else {
						decompressor.inUse16[index] = false;
					}
				}
				for (index = 0; index < 256; index++) {
					decompressor.inUse[index] = false;
				}
				@Pc(182) int subIndex;
				for (index = 0; index < 16; index++) {
					if (decompressor.inUse16[index]) {
						for (subIndex = 0; subIndex < 16; subIndex++) {
							uc = readBit(decompressor);
							if (uc == 1) {
								decompressor.inUse[index * 16 + subIndex] = true;
							}
						}
					}
				}
				buildSymbolMap(decompressor);
				@Pc(213) int alphaSize = decompressor.nInUse + 2;
				@Pc(217) int groupCount = readBits(3, decompressor);
				@Pc(221) int selectorCount = readBits(15, decompressor);
				for (index = 0; index < selectorCount; index++) {
					subIndex = 0;
					while (true) {
						uc = readBit(decompressor);
						if (uc == 0) {
							decompressor.selectorMtf[index] = (byte) subIndex;
							break;
						}
						subIndex++;
					}
				}
				@Pc(246) byte[] mtfPositions = new byte[6];
				@Pc(248) byte mtfValue = 0;
				while (mtfValue < groupCount) {
					mtfPositions[mtfValue] = mtfValue++;
				}
				for (index = 0; index < selectorCount; index++) {
					mtfValue = decompressor.selectorMtf[index];
					@Pc(273) byte selectorValue = mtfPositions[mtfValue];
					while (mtfValue > 0) {
						mtfPositions[mtfValue] = mtfPositions[mtfValue - 1];
						mtfValue--;
					}
					mtfPositions[0] = selectorValue;
					decompressor.selector[index] = selectorValue;
				}
				@Pc(304) int group;
				for (group = 0; group < groupCount; group++) {
					@Pc(309) int currentLength = readBits(5, decompressor);
					for (index = 0; index < alphaSize; index++) {
						while (true) {
							uc = readBit(decompressor);
							if (uc == 0) {
								decompressor.len[group][index] = (byte) currentLength;
								break;
							}
							uc = readBit(decompressor);
							if (uc == 0) {
								currentLength++;
							} else {
								currentLength--;
							}
						}
					}
				}
				for (group = 0; group < groupCount; group++) {
					@Pc(347) byte minLength = 32;
					@Pc(349) byte maxLength = 0;
					for (index = 0; index < alphaSize; index++) {
						if (decompressor.len[group][index] > maxLength) {
							maxLength = decompressor.len[group][index];
						}
						if (decompressor.len[group][index] < minLength) {
							minLength = decompressor.len[group][index];
						}
					}
					BzipDecompressor.buildHuffmanTable(decompressor.limit[group], decompressor.base[group], decompressor.perm[group], decompressor.len[group], minLength, maxLength, alphaSize);
					decompressor.minLens[group] = minLength;
				}
				@Pc(420) int endOfBlock = decompressor.nInUse + 1;
				@Pc(422) byte initialGroup = -1;
				for (index = 0; index <= 255; index++) {
					decompressor.unzftab[index] = 0;
				}
				@Pc(438) int mtfIndex = 4095;
				for (@Pc(440) int i = 15; i >= 0; i--) {
					for (@Pc(443) int j = 15; j >= 0; j--) {
						decompressor.mtfa[mtfIndex] = (byte) (i * 16 + j);
						mtfIndex--;
					}
					decompressor.mtfbase[i] = mtfIndex + 1;
				}
				@Pc(470) int saveNblock = 0;
				@Pc(473) int groupIndex = initialGroup + 1;
				@Pc(475) byte groupSize = 50;
				@Pc(480) byte groupSelector = decompressor.selector[0];
				@Pc(485) int groupMinLength = decompressor.minLens[groupSelector];
				@Pc(490) int[] groupLimit = decompressor.limit[groupSelector];
				@Pc(495) int[] groupPerm = decompressor.perm[groupSelector];
				@Pc(500) int[] groupBase = decompressor.base[groupSelector];
				@Pc(501) int groupPos = groupSize - 1;
				@Pc(503) int codeLength = groupMinLength;
				@Pc(507) int codeValue;
				@Pc(516) byte bit;
				for (codeValue = readBits(groupMinLength, decompressor); codeValue > groupLimit[codeLength]; codeValue = codeValue << 1 | bit) {
					codeLength++;
					bit = readBit(decompressor);
				}
				@Pc(531) int nextSymbol = groupPerm[codeValue - groupBase[codeLength]];
				while (true) {
					while (nextSymbol != endOfBlock) {
						if (nextSymbol == 0 || nextSymbol == 1) {
							@Pc(541) int runLength = -1;
							@Pc(543) int runWeight = 1;
							do {
								if (nextSymbol == 0) {
									runLength += runWeight;
								} else if (nextSymbol == 1) {
									runLength += runWeight * 2;
								}
								runWeight *= 2;
								if (groupPos == 0) {
									groupIndex++;
									groupPos = 50;
									groupSelector = decompressor.selector[groupIndex];
									groupMinLength = decompressor.minLens[groupSelector];
									groupLimit = decompressor.limit[groupSelector];
									groupPerm = decompressor.perm[groupSelector];
									groupBase = decompressor.base[groupSelector];
								}
								groupPos--;
								codeLength = groupMinLength;
								for (codeValue = readBits(groupMinLength, decompressor); codeValue > groupLimit[codeLength]; codeValue = codeValue << 1 | bit) {
									codeLength++;
									bit = readBit(decompressor);
								}
								nextSymbol = groupPerm[codeValue - groupBase[codeLength]];
							} while (nextSymbol == 0 || nextSymbol == 1);
							runLength++;
							uc = decompressor.seqToUnseq[decompressor.mtfa[decompressor.mtfbase[0]] & 0xFF];
							decompressor.unzftab[uc & 0xFF] += runLength;
							while (runLength > 0) {
								Bzip2Compressor.transformTable[saveNblock] = uc & 0xFF;
								saveNblock++;
								runLength--;
							}
						} else {
							@Pc(669) int mtfOffset = nextSymbol - 1;
							@Pc(677) int mtfPos;
							if (mtfOffset < 16) {
								mtfPos = decompressor.mtfbase[0];
								uc = decompressor.mtfa[mtfPos + mtfOffset];
								while (mtfOffset > 3) {
									@Pc(689) int shiftPos = mtfPos + mtfOffset;
									decompressor.mtfa[shiftPos] = decompressor.mtfa[shiftPos - 1];
									decompressor.mtfa[shiftPos - 1] = decompressor.mtfa[shiftPos - 2];
									decompressor.mtfa[shiftPos - 2] = decompressor.mtfa[shiftPos - 3];
									decompressor.mtfa[shiftPos - 3] = decompressor.mtfa[shiftPos - 4];
									mtfOffset -= 4;
								}
								while (mtfOffset > 0) {
									decompressor.mtfa[mtfPos + mtfOffset] = decompressor.mtfa[mtfPos + mtfOffset - 1];
									mtfOffset--;
								}
								decompressor.mtfa[mtfPos] = uc;
							} else {
								@Pc(767) int mtfBlock = mtfOffset / 16;
								@Pc(771) int blockOffset = mtfOffset % 16;
								mtfPos = decompressor.mtfbase[mtfBlock] + blockOffset;
								uc = decompressor.mtfa[mtfPos];
								while (mtfPos > decompressor.mtfbase[mtfBlock]) {
									decompressor.mtfa[mtfPos] = decompressor.mtfa[mtfPos - 1];
									mtfPos--;
								}
								@Pc(806) int local806 = decompressor.mtfbase[mtfBlock]++;
								while (mtfBlock > 0) {
									int previousBase = decompressor.mtfbase[mtfBlock]--;
									decompressor.mtfa[decompressor.mtfbase[mtfBlock]] = decompressor.mtfa[decompressor.mtfbase[mtfBlock - 1] + 16 - 1];
									mtfBlock--;
								}
								int previousFrontBase = decompressor.mtfbase[0]--;
								decompressor.mtfa[decompressor.mtfbase[0]] = uc;
								if (decompressor.mtfbase[0] == 0) {
									@Pc(864) int rebuildIndex = 4095;
									for (@Pc(866) int k = 15; k >= 0; k--) {
										for (@Pc(869) int m = 15; m >= 0; m--) {
											decompressor.mtfa[rebuildIndex] = decompressor.mtfa[decompressor.mtfbase[k] + m];
											rebuildIndex--;
										}
										decompressor.mtfbase[k] = rebuildIndex + 1;
									}
								}
							}
							decompressor.unzftab[decompressor.seqToUnseq[uc & 0xFF] & 0xFF]++;
							Bzip2Compressor.transformTable[saveNblock] = decompressor.seqToUnseq[uc & 0xFF] & 0xFF;
							saveNblock++;
							if (groupPos == 0) {
								groupIndex++;
								groupPos = 50;
								groupSelector = decompressor.selector[groupIndex];
								groupMinLength = decompressor.minLens[groupSelector];
								groupLimit = decompressor.limit[groupSelector];
								groupPerm = decompressor.perm[groupSelector];
								groupBase = decompressor.base[groupSelector];
							}
							groupPos--;
							codeLength = groupMinLength;
							for (codeValue = readBits(groupMinLength, decompressor); codeValue > groupLimit[codeLength]; codeValue = codeValue << 1 | bit) {
								codeLength++;
								bit = readBit(decompressor);
							}
							nextSymbol = groupPerm[codeValue - groupBase[codeLength]];
						}
					}
					decompressor.stateOutLen = 0;
					decompressor.stateOutCh = 0;
					decompressor.cftab[0] = 0;
					for (index = 1; index <= 256; index++) {
						decompressor.cftab[index] = decompressor.unzftab[index - 1];
					}
					for (index = 1; index <= 256; index++) {
						decompressor.cftab[index] += decompressor.cftab[index - 1];
					}
					for (index = 0; index < saveNblock; index++) {
						uc = (byte) (Bzip2Compressor.transformTable[index] & 0xFF);
						Bzip2Compressor.transformTable[decompressor.cftab[uc & 0xFF]] |= index << 8;
						decompressor.cftab[uc & 0xFF]++;
					}
					decompressor.tPos = Bzip2Compressor.transformTable[decompressor.origPtr] >> 8;
					decompressor.nblockused = 0;
					decompressor.tPos = Bzip2Compressor.transformTable[decompressor.tPos];
					decompressor.k0 = (byte) (decompressor.tPos & 0xFF);
					decompressor.tPos >>= 0x8;
					decompressor.nblockused++;
					decompressor.saveNblock = saveNblock;
					decodeBlock(decompressor);
					if (decompressor.nblockused == decompressor.saveNblock + 1 && decompressor.stateOutLen == 0) {
						needsNewBlock = true;
						break;
					}
					needsNewBlock = false;
					break;
				}
			}
			return;
		}
	}

	@OriginalMember(owner = "client!bs", name = "e", descriptor = "(Lclient!ig;)B")
	public static byte readBit(@OriginalArg(0) BzipDecompressor entry) {
		return (byte) readBits(1, entry);
	}
}
