package com.jagex.game.runetek5

import com.jagex.game.runetek5.cache.compression.Bzip2Compressor
import com.jagex.game.runetek5.core.compress.BzipDecompressor
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

class BZip2InputStream private constructor() {

	companion object {

		@OriginalMember(owner = "client!bs", name = "a", descriptor = "(ILclient!ig;)I")
		@JvmStatic
		fun readBits(@OriginalArg(0) numBits: Int, @OriginalArg(1) entry: BzipDecompressor?): Int {
			while (entry!!.bsLive < numBits) {
				entry.bsBuff = (entry.bsBuff shl 8) or (entry.input!![entry.strmNextInPtr].toInt() and 0xFF)
				entry.bsLive += 8
				entry.strmNextInPtr++
				entry.strmTotalInLo32++
			}
			@Pc(17) val value: Int = (entry.bsBuff shr (entry.bsLive - numBits)) and ((0x1 shl numBits) - 1)
			entry.bsLive -= numBits
			return value
		}

		@OriginalMember(owner = "client!bs", name = "a", descriptor = "(Lclient!ig;)B")
		@JvmStatic
		fun readByte(@OriginalArg(0) entry: BzipDecompressor?): Byte {
			return readBits(8, entry).toByte()
		}

		@OriginalMember(owner = "client!bs", name = "a", descriptor = "Lclient!ig;")
		@JvmField
		val INSTANCE: BzipDecompressor = BzipDecompressor()

		@OriginalMember(owner = "client!bs", name = "b", descriptor = "(Lclient!ig;)V")
		@JvmStatic
		fun decodeBlock(@OriginalArg(0) entry: BzipDecompressor?) {
			@Pc(2) var outCh: Byte = entry!!.stateOutCh
			@Pc(5) var outLen: Int = entry.stateOutLen
			@Pc(8) var nblockused: Int = entry.nblockused
			@Pc(11) var k0: Int = entry.k0
			@Pc(13) val transformTable: IntArray? = Bzip2Compressor.transformTable
			@Pc(16) var tPos: Int = entry.tPos
			@Pc(19) val output: ByteArray? = entry.output
			@Pc(22) var strmNextOutPtr: Int = entry.strmNextOutPtr
			@Pc(25) var availableOut: Int = entry.availableOut
			@Pc(27) val initialAvailableOut: Int = availableOut
			@Pc(32) val blockEnd: Int = entry.saveNblock + 1
			label57@ while (true) {
				if (outLen > 0) {
					while (true) {
						if (availableOut == 0) {
							break@label57
						}
						if (outLen == 1) {
							if (availableOut == 0) {
								outLen = 1
								break@label57
							}
							output!![strmNextOutPtr] = outCh
							strmNextOutPtr++
							availableOut--
							break
						}
						output!![strmNextOutPtr] = outCh
						outLen--
						strmNextOutPtr++
						availableOut--
					}
				}
				while (nblockused != blockEnd) {
					outCh = k0.toByte()
					tPos = transformTable!![tPos]
					@Pc(74) var nextByte: Byte = tPos.toByte()
					tPos = tPos shr 0x8
					nblockused++
					if (nextByte.toInt() == k0) {
						if (nblockused != blockEnd) {
							outLen = 2
							tPos = transformTable[tPos]
							nextByte = tPos.toByte()
							tPos = tPos shr 0x8
							nblockused++
							if (nblockused != blockEnd) {
								if (nextByte.toInt() == k0) {
									outLen = 3
									tPos = transformTable[tPos]
									nextByte = tPos.toByte()
									tPos = tPos shr 0x8
									nblockused++
									if (nblockused != blockEnd) {
										if (nextByte.toInt() == k0) {
											tPos = transformTable[tPos]
											nextByte = tPos.toByte()
											tPos = tPos shr 0x8
											nblockused++
											outLen = (nextByte.toInt() and 0xFF) + 4
											tPos = transformTable[tPos]
											k0 = tPos.toByte().toInt()
											tPos = tPos shr 0x8
											nblockused++
										} else {
											k0 = nextByte.toInt()
										}
									}
								} else {
									k0 = nextByte.toInt()
								}
							}
							continue@label57
						}
						if (availableOut == 0) {
							outLen = 1
							break@label57
						}
						output!![strmNextOutPtr] = outCh
						strmNextOutPtr++
						availableOut--
					} else {
						k0 = nextByte.toInt()
						if (availableOut == 0) {
							outLen = 1
							break@label57
						}
						output!![strmNextOutPtr] = outCh
						strmNextOutPtr++
						availableOut--
					}
				}
				outLen = 0
				break
			}
			@Pc(191) val strmTotalOutLo32: Int = entry.strmTotalOutLo32
			entry.strmTotalOutLo32 += initialAvailableOut - availableOut
			entry.stateOutCh = outCh
			entry.stateOutLen = outLen
			entry.nblockused = nblockused
			entry.k0 = k0
			Bzip2Compressor.transformTable = transformTable
			entry.tPos = tPos
			entry.output = output
			entry.strmNextOutPtr = strmNextOutPtr
			entry.availableOut = availableOut
		}

		@OriginalMember(owner = "client!bs", name = "c", descriptor = "(Lclient!ig;)V")
		@JvmStatic
		fun buildSymbolMap(@OriginalArg(0) entry: BzipDecompressor?) {
			entry!!.nInUse = 0
			for (@Pc(4) i in 0 until 256) {
				if (entry.inUse[i]) {
					entry.seqToUnseq[entry.nInUse] = i.toByte()
					entry.nInUse++
				}
			}
		}

		@OriginalMember(owner = "client!bs", name = "d", descriptor = "(Lclient!ig;)V")
		@JvmStatic
		fun decompress(@OriginalArg(0) decompressor: BzipDecompressor?) {
			decompressor!!.blockSize100k = 1
			if (Bzip2Compressor.transformTable == null) {
				Bzip2Compressor.transformTable = IntArray(decompressor.blockSize100k * 100000)
			}
			@Pc(56) var needsNewBlock: Boolean = true
			while (true) {
				while (needsNewBlock) {
					@Pc(60) var uc: Byte = readByte(decompressor)
					if (uc.toInt() == 23) {
						return
					}
					uc = readByte(decompressor)
					uc = readByte(decompressor)
					uc = readByte(decompressor)
					uc = readByte(decompressor)
					uc = readByte(decompressor)
					uc = readByte(decompressor)
					uc = readByte(decompressor)
					uc = readByte(decompressor)
					uc = readByte(decompressor)
					uc = readBit(decompressor)
					decompressor.origPtr = 0
					uc = readByte(decompressor)
					decompressor.origPtr = (decompressor.origPtr shl 8) or (uc.toInt() and 0xFF)
					uc = readByte(decompressor)
					decompressor.origPtr = (decompressor.origPtr shl 8) or (uc.toInt() and 0xFF)
					uc = readByte(decompressor)
					decompressor.origPtr = (decompressor.origPtr shl 8) or (uc.toInt() and 0xFF)
					@Pc(138) var index: Int
					index = 0
					while (index < 16) {
						uc = readBit(decompressor)
						if (uc.toInt() == 1) {
							decompressor.inUse16[index] = true
						} else {
							decompressor.inUse16[index] = false
						}
						index++
					}
					index = 0
					while (index < 256) {
						decompressor.inUse[index] = false
						index++
					}
					@Pc(182) var subIndex: Int
					index = 0
					while (index < 16) {
						if (decompressor.inUse16[index]) {
							subIndex = 0
							while (subIndex < 16) {
								uc = readBit(decompressor)
								if (uc.toInt() == 1) {
									decompressor.inUse[index * 16 + subIndex] = true
								}
								subIndex++
							}
						}
						index++
					}
					buildSymbolMap(decompressor)
					@Pc(213) val alphaSize: Int = decompressor.nInUse + 2
					@Pc(217) val groupCount: Int = readBits(3, decompressor)
					@Pc(221) val selectorCount: Int = readBits(15, decompressor)
					index = 0
					while (index < selectorCount) {
						subIndex = 0
						while (true) {
							uc = readBit(decompressor)
							if (uc.toInt() == 0) {
								decompressor.selectorMtf[index] = subIndex.toByte()
								break
							}
							subIndex++
						}
						index++
					}
					@Pc(246) val mtfPositions: ByteArray = ByteArray(6)
					@Pc(248) var mtfValue: Byte = 0
					while (mtfValue < groupCount) {
						mtfPositions[mtfValue.toInt()] = mtfValue
						mtfValue++
					}
					index = 0
					while (index < selectorCount) {
						mtfValue = decompressor.selectorMtf[index]
						@Pc(273) val selectorValue: Byte = mtfPositions[mtfValue.toInt()]
						while (mtfValue > 0) {
							mtfPositions[mtfValue.toInt()] = mtfPositions[mtfValue - 1]
							mtfValue--
						}
						mtfPositions[0] = selectorValue
						decompressor.selector[index] = selectorValue
						index++
					}
					@Pc(304) var group: Int
					group = 0
					while (group < groupCount) {
						@Pc(309) var currentLength: Int = readBits(5, decompressor)
						index = 0
						while (index < alphaSize) {
							while (true) {
								uc = readBit(decompressor)
								if (uc.toInt() == 0) {
									decompressor.len[group][index] = currentLength.toByte()
									break
								}
								uc = readBit(decompressor)
								if (uc.toInt() == 0) {
									currentLength++
								} else {
									currentLength--
								}
							}
							index++
						}
						group++
					}
					group = 0
					while (group < groupCount) {
						@Pc(347) var minLength: Byte = 32
						@Pc(349) var maxLength: Byte = 0
						index = 0
						while (index < alphaSize) {
							if (decompressor.len[group][index] > maxLength) {
								maxLength = decompressor.len[group][index]
							}
							if (decompressor.len[group][index] < minLength) {
								minLength = decompressor.len[group][index]
							}
							index++
						}
						BzipDecompressor.buildHuffmanTable(decompressor.limit[group], decompressor.base[group], decompressor.perm[group], decompressor.len[group], minLength.toInt(), maxLength.toInt(), alphaSize)
						decompressor.minLens[group] = minLength.toInt()
						group++
					}
					@Pc(420) val endOfBlock: Int = decompressor.nInUse + 1
					@Pc(422) val initialGroup: Byte = -1
					index = 0
					while (index <= 255) {
						decompressor.unzftab[index] = 0
						index++
					}
					@Pc(438) var mtfIndex: Int = 4095
					for (@Pc(440) i in 15 downTo 0) {
						for (@Pc(443) j in 15 downTo 0) {
							decompressor.mtfa[mtfIndex] = (i * 16 + j).toByte()
							mtfIndex--
						}
						decompressor.mtfbase[i] = mtfIndex + 1
					}
					@Pc(470) var saveNblock: Int = 0
					@Pc(473) var groupIndex: Int = initialGroup + 1
					@Pc(475) val groupSize: Byte = 50
					@Pc(480) var groupSelector: Byte = decompressor.selector[0]
					@Pc(485) var groupMinLength: Int = decompressor.minLens[groupSelector.toInt()]
					@Pc(490) var groupLimit: IntArray = decompressor.limit[groupSelector.toInt()]
					@Pc(495) var groupPerm: IntArray = decompressor.perm[groupSelector.toInt()]
					@Pc(500) var groupBase: IntArray = decompressor.base[groupSelector.toInt()]
					@Pc(501) var groupPos: Int = groupSize - 1
					@Pc(503) var codeLength: Int = groupMinLength
					@Pc(507) var codeValue: Int
					@Pc(516) var bit: Byte
					codeValue = readBits(groupMinLength, decompressor)
					while (codeValue > groupLimit[codeLength]) {
						codeLength++
						bit = readBit(decompressor)
						codeValue = (codeValue shl 1) or bit.toInt()
					}
					@Pc(531) var nextSymbol: Int = groupPerm[codeValue - groupBase[codeLength]]
					while (true) {
						while (nextSymbol != endOfBlock) {
							if (nextSymbol == 0 || nextSymbol == 1) {
								@Pc(541) var runLength: Int = -1
								@Pc(543) var runWeight: Int = 1
								do {
									if (nextSymbol == 0) {
										runLength += runWeight
									} else if (nextSymbol == 1) {
										runLength += runWeight * 2
									}
									runWeight *= 2
									if (groupPos == 0) {
										groupIndex++
										groupPos = 50
										groupSelector = decompressor.selector[groupIndex]
										groupMinLength = decompressor.minLens[groupSelector.toInt()]
										groupLimit = decompressor.limit[groupSelector.toInt()]
										groupPerm = decompressor.perm[groupSelector.toInt()]
										groupBase = decompressor.base[groupSelector.toInt()]
									}
									groupPos--
									codeLength = groupMinLength
									codeValue = readBits(groupMinLength, decompressor)
									while (codeValue > groupLimit[codeLength]) {
										codeLength++
										bit = readBit(decompressor)
										codeValue = (codeValue shl 1) or bit.toInt()
									}
									nextSymbol = groupPerm[codeValue - groupBase[codeLength]]
								} while (nextSymbol == 0 || nextSymbol == 1)
								runLength++
								uc = decompressor.seqToUnseq[decompressor.mtfa[decompressor.mtfbase[0]].toInt() and 0xFF]
								decompressor.unzftab[uc.toInt() and 0xFF] += runLength
								while (runLength > 0) {
									Bzip2Compressor.transformTable!![saveNblock] = uc.toInt() and 0xFF
									saveNblock++
									runLength--
								}
							} else {
								@Pc(669) var mtfOffset: Int = nextSymbol - 1
								@Pc(677) var mtfPos: Int
								if (mtfOffset < 16) {
									mtfPos = decompressor.mtfbase[0]
									uc = decompressor.mtfa[mtfPos + mtfOffset]
									while (mtfOffset > 3) {
										@Pc(689) val shiftPos: Int = mtfPos + mtfOffset
										decompressor.mtfa[shiftPos] = decompressor.mtfa[shiftPos - 1]
										decompressor.mtfa[shiftPos - 1] = decompressor.mtfa[shiftPos - 2]
										decompressor.mtfa[shiftPos - 2] = decompressor.mtfa[shiftPos - 3]
										decompressor.mtfa[shiftPos - 3] = decompressor.mtfa[shiftPos - 4]
										mtfOffset -= 4
									}
									while (mtfOffset > 0) {
										decompressor.mtfa[mtfPos + mtfOffset] = decompressor.mtfa[mtfPos + mtfOffset - 1]
										mtfOffset--
									}
									decompressor.mtfa[mtfPos] = uc
								} else {
									@Pc(767) var mtfBlock: Int = mtfOffset / 16
									@Pc(771) val blockOffset: Int = mtfOffset % 16
									mtfPos = decompressor.mtfbase[mtfBlock] + blockOffset
									uc = decompressor.mtfa[mtfPos]
									while (mtfPos > decompressor.mtfbase[mtfBlock]) {
										decompressor.mtfa[mtfPos] = decompressor.mtfa[mtfPos - 1]
										mtfPos--
									}
									@Pc(806) val local806: Int = decompressor.mtfbase[mtfBlock]++
									while (mtfBlock > 0) {
										val previousBase: Int = decompressor.mtfbase[mtfBlock]--
										decompressor.mtfa[decompressor.mtfbase[mtfBlock]] = decompressor.mtfa[decompressor.mtfbase[mtfBlock - 1] + 16 - 1]
										mtfBlock--
									}
									val previousFrontBase: Int = decompressor.mtfbase[0]--
									decompressor.mtfa[decompressor.mtfbase[0]] = uc
									if (decompressor.mtfbase[0] == 0) {
										@Pc(864) var rebuildIndex: Int = 4095
										for (@Pc(866) k in 15 downTo 0) {
											for (@Pc(869) m in 15 downTo 0) {
												decompressor.mtfa[rebuildIndex] = decompressor.mtfa[decompressor.mtfbase[k] + m]
												rebuildIndex--
											}
											decompressor.mtfbase[k] = rebuildIndex + 1
										}
									}
								}
								decompressor.unzftab[decompressor.seqToUnseq[uc.toInt() and 0xFF].toInt() and 0xFF]++
								Bzip2Compressor.transformTable!![saveNblock] = decompressor.seqToUnseq[uc.toInt() and 0xFF].toInt() and 0xFF
								saveNblock++
								if (groupPos == 0) {
									groupIndex++
									groupPos = 50
									groupSelector = decompressor.selector[groupIndex]
									groupMinLength = decompressor.minLens[groupSelector.toInt()]
									groupLimit = decompressor.limit[groupSelector.toInt()]
									groupPerm = decompressor.perm[groupSelector.toInt()]
									groupBase = decompressor.base[groupSelector.toInt()]
								}
								groupPos--
								codeLength = groupMinLength
								codeValue = readBits(groupMinLength, decompressor)
								while (codeValue > groupLimit[codeLength]) {
									codeLength++
									bit = readBit(decompressor)
									codeValue = (codeValue shl 1) or bit.toInt()
								}
								nextSymbol = groupPerm[codeValue - groupBase[codeLength]]
							}
						}
						decompressor.stateOutLen = 0
						decompressor.stateOutCh = 0
						decompressor.cftab[0] = 0
						index = 1
						while (index <= 256) {
							decompressor.cftab[index] = decompressor.unzftab[index - 1]
							index++
						}
						index = 1
						while (index <= 256) {
							decompressor.cftab[index] += decompressor.cftab[index - 1]
							index++
						}
						index = 0
						while (index < saveNblock) {
							uc = (Bzip2Compressor.transformTable!![index] and 0xFF).toByte()
							Bzip2Compressor.transformTable!![decompressor.cftab[uc.toInt() and 0xFF]] = Bzip2Compressor.transformTable!![decompressor.cftab[uc.toInt() and 0xFF]] or (index shl 8)
							decompressor.cftab[uc.toInt() and 0xFF]++
							index++
						}
						decompressor.tPos = Bzip2Compressor.transformTable!![decompressor.origPtr] shr 8
						decompressor.nblockused = 0
						decompressor.tPos = Bzip2Compressor.transformTable!![decompressor.tPos]
						decompressor.k0 = (decompressor.tPos and 0xFF).toByte().toInt()
						decompressor.tPos = decompressor.tPos shr 0x8
						decompressor.nblockused++
						decompressor.saveNblock = saveNblock
						decodeBlock(decompressor)
						if (decompressor.nblockused == decompressor.saveNblock + 1 && decompressor.stateOutLen == 0) {
							needsNewBlock = true
							break
						}
						needsNewBlock = false
						break
					}
				}
				return
			}
		}

		@OriginalMember(owner = "client!bs", name = "e", descriptor = "(Lclient!ig;)B")
		@JvmStatic
		fun readBit(@OriginalArg(0) entry: BzipDecompressor?): Byte {
			return readBits(1, entry).toByte()
		}
	}
}
