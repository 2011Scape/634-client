package com.jagex.game.runetek5.encoding

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!wda")
class Huffman {

	@OriginalMember(owner = "client!wda", name = "b", descriptor = "[I")
	private val codes: IntArray

	@OriginalMember(owner = "client!wda", name = "e", descriptor = "[B")
	private val codeLengths: ByteArray

	@OriginalMember(owner = "client!wda", name = "j", descriptor = "[I")
	private var symbolTree: IntArray

	@OriginalMember(owner = "client!wda", name = "<init>", descriptor = "([B)V")
	constructor(@OriginalArg(0) bits: ByteArray?) {
		@Pc(6) val length: Int = bits!!.size
		this.codes = IntArray(length)
		this.codeLengths = bits
		this.symbolTree = IntArray(8)
		@Pc(20) val nextCodes: IntArray = IntArray(33)
		@Pc(22) var treeSize: Int = 0
		@Pc(24) var i: Int = 0
		while (i < length) {
			@Pc(30) val codeLength: Byte = bits[i]
			if (codeLength.toInt() != 0) {
				@Pc(38) val lengthBit: Int = 0x1 shl (32 - codeLength)
				@Pc(42) val code: Int = nextCodes[codeLength.toInt()]
				this.codes[i] = code
				@Pc(60) val nextCode: Int
				@Pc(66) var bitLength: Int
				@Pc(72) var local72: Int
				@Pc(84) var local84: Int
				if ((lengthBit and code) == 0) {
					bitLength = codeLength - 1
					while (bitLength >= 1) {
						val shorterCode: Int = nextCodes[bitLength]
						if (shorterCode != code) {
							break
						}
						val shorterLengthBit: Int = 0x1 shl (32 - bitLength)
						if ((shorterCode and shorterLengthBit) != 0) {
							nextCodes[bitLength] = nextCodes[bitLength - 1]
							break
						}
						nextCodes[bitLength] = shorterCode or shorterLengthBit
						bitLength--
					}
					nextCode = lengthBit or code
				} else {
					nextCode = nextCodes[codeLength - 1]
				}
				nextCodes[codeLength.toInt()] = nextCode
				bitLength = codeLength + 1
				while (bitLength <= 32) {
					if (nextCodes[bitLength] == code) {
						nextCodes[bitLength] = nextCode
					}
					bitLength++
				}
				var treeIndex: Int = 0
				var bitIndex: Int = 0
				while (bitIndex < codeLength) {
					@Pc(154) val bitMask: Int = Int.MIN_VALUE ushr bitIndex
					if ((code and bitMask) == 0) {
						treeIndex++
					} else {
						if (this.symbolTree[treeIndex] == 0) {
							this.symbolTree[treeIndex] = treeSize
						}
						treeIndex = this.symbolTree[treeIndex]
					}
					if (treeIndex >= this.symbolTree.size) {
						@Pc(196) val newTree: IntArray = IntArray(this.symbolTree.size * 2)
						@Pc(198) var j: Int = 0
						while (j < this.symbolTree.size) {
							newTree[j] = this.symbolTree[j]
							j++
						}
						this.symbolTree = newTree
					}
					bitIndex++
				}
				this.symbolTree[treeIndex] = i.inv()
				if (treeSize <= treeIndex) {
					treeSize = treeIndex + 1
				}
			}
			i++
		}
	}

	@OriginalMember(owner = "client!wda", name = "a", descriptor = "([BI[BIII)I")
	fun decompress(@OriginalArg(0) output: ByteArray?, @OriginalArg(1) inputOffset: Int, @OriginalArg(2) input: ByteArray?, @OriginalArg(3) outputLength: Int, @OriginalArg(5) outputOffset: Int): Int {
		var outputOffset: Int = outputOffset
		if (outputLength == 0) {
			return 0
		}
		@Pc(13) val outputEnd: Int = outputLength
		@Pc(15) var treeIndex: Int = 0
		@Pc(23) var inputIndex: Int = inputOffset
		while (true) {
			@Pc(27) val flags: Byte = input!![inputIndex]
			if (flags >= 0) {
				treeIndex++
			} else {
				treeIndex = this.symbolTree[treeIndex]
			}
			@Pc(46) val entry1: Int = this.symbolTree[treeIndex]
			if (entry1 < 0) {
				output!![outputOffset++] = entry1.inv().toByte()
				if (outputOffset >= outputEnd) {
					break
				}
				treeIndex = 0
			}
			if ((flags.toInt() and 0x40) == 0) {
				treeIndex++
			} else {
				treeIndex = this.symbolTree[treeIndex]
			}
			@Pc(88) val entry2: Int = this.symbolTree[treeIndex]
			if (entry2 < 0) {
				output!![outputOffset++] = entry2.inv().toByte()
				if (outputOffset >= outputEnd) {
					break
				}
				treeIndex = 0
			}
			if ((flags.toInt() and 0x20) == 0) {
				treeIndex++
			} else {
				treeIndex = this.symbolTree[treeIndex]
			}
			@Pc(126) val entry3: Int = this.symbolTree[treeIndex]
			if (entry3 < 0) {
				output!![outputOffset++] = entry3.inv().toByte()
				if (outputOffset >= outputEnd) {
					break
				}
				treeIndex = 0
			}
			if ((flags.toInt() and 0x10) == 0) {
				treeIndex++
			} else {
				treeIndex = this.symbolTree[treeIndex]
			}
			@Pc(161) val entry4: Int = this.symbolTree[treeIndex]
			if (entry4 < 0) {
				output!![outputOffset++] = entry4.inv().toByte()
				if (outputEnd <= outputOffset) {
					break
				}
				treeIndex = 0
			}
			if ((flags.toInt() and 0x8) == 0) {
				treeIndex++
			} else {
				treeIndex = this.symbolTree[treeIndex]
			}
			@Pc(203) val entry5: Int = this.symbolTree[treeIndex]
			if (entry5 < 0) {
				output!![outputOffset++] = entry5.inv().toByte()
				if (outputEnd <= outputOffset) {
					break
				}
				treeIndex = 0
			}
			if ((flags.toInt() and 0x4) == 0) {
				treeIndex++
			} else {
				treeIndex = this.symbolTree[treeIndex]
			}
			@Pc(238) val entry6: Int = this.symbolTree[treeIndex]
			if (entry6 < 0) {
				output!![outputOffset++] = entry6.inv().toByte()
				if (outputEnd <= outputOffset) {
					break
				}
				treeIndex = 0
			}
			if ((flags.toInt() and 0x2) == 0) {
				treeIndex++
			} else {
				treeIndex = this.symbolTree[treeIndex]
			}
			@Pc(270) val entry7: Int = this.symbolTree[treeIndex]
			if (entry7 < 0) {
				output!![outputOffset++] = entry7.inv().toByte()
				if (outputOffset >= outputEnd) {
					break
				}
				treeIndex = 0
			}
			if ((flags.toInt() and 0x1) == 0) {
				treeIndex++
			} else {
				treeIndex = this.symbolTree[treeIndex]
			}
			@Pc(306) val entry8: Int = this.symbolTree[treeIndex]
			if (entry8 < 0) {
				output!![outputOffset++] = entry8.inv().toByte()
				if (outputOffset >= outputEnd) {
					break
				}
				treeIndex = 0
			}
			inputIndex++
		}
		return inputIndex + 1 - inputOffset
	}

	@OriginalMember(owner = "client!wda", name = "a", descriptor = "(III[BI[B)I")
	fun compress(@OriginalArg(0) inputEnd: Int, @OriginalArg(1) inputOffset: Int, @OriginalArg(2) outputOffset: Int, @OriginalArg(3) input: ByteArray?, @OriginalArg(5) output: ByteArray?): Int {
		var inputOffset: Int = inputOffset
		@Pc(7) var bitBuffer: Int = 0
		@Pc(11) val end: Int = inputEnd
		@Pc(15) var bitPosition: Int = outputOffset shl 3
		while (inputOffset < end) {
			@Pc(35) val symbol: Int = input!![inputOffset].toInt() and 0xFF
			@Pc(40) val code: Int = this.codes[symbol]
			@Pc(45) val codeLength: Byte = this.codeLengths[symbol]
			if (codeLength.toInt() == 0) {
				throw RuntimeException("No codeword for data value " + symbol)
			}
			@Pc(62) var byteIndex: Int = bitPosition shr 3
			@Pc(66) var bitOffset: Int = bitPosition and 0x7
			bitBuffer = bitBuffer and (-bitOffset shr 31)
			@Pc(84) val lastByte: Int = ((bitOffset + codeLength - 1) shr 3) + byteIndex
			bitPosition += codeLength
			@Pc(89) val shift: Int = bitOffset + 24
			bitBuffer = bitBuffer or (code ushr shift)
			output!![byteIndex] = bitBuffer.toByte()
			if (byteIndex < lastByte) {
				byteIndex++
				bitOffset = shift - 8
				bitBuffer = code ushr bitOffset
				output[byteIndex] = bitBuffer.toByte()
				if (lastByte > byteIndex) {
					byteIndex++
					bitOffset -= 8
					bitBuffer = code ushr bitOffset
					output[byteIndex] = bitBuffer.toByte()
					if (lastByte > byteIndex) {
						bitOffset -= 8
						byteIndex++
						bitBuffer = code ushr bitOffset
						output[byteIndex] = bitBuffer.toByte()
						if (byteIndex < lastByte) {
							bitOffset -= 8
							byteIndex++
							bitBuffer = code shl -bitOffset
							output[byteIndex] = bitBuffer.toByte()
						}
					}
				}
			}
			inputOffset++
		}
		return ((bitPosition + 7) shr 3) - outputOffset
	}
}
