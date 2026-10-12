package com.jagex.game.runetek5

import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import util.Random
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.utils.Utils
import com.jagex.game.runetek5.cache.node.lru.LRUCache
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

@OriginalClass("client!mt")
class LoadingScreenManager @OriginalMember(owner = "client!mt", name = "<init>", descriptor = "(Lclient!sj;ILclient!in;)V") constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) language: Int, @OriginalArg(2) configClient: Js5ResourceProvider?) {

	@OriginalMember(owner = "client!mt", name = "d", descriptor = "Lclient!in;")
	private val configClient: Js5ResourceProvider?

	@OriginalMember(owner = "client!mt", name = "j", descriptor = "[[I")
	private val sequences: Array<IntArray?>

	@OriginalMember(owner = "client!mt", name = "k", descriptor = "[Z")
	private val shuffled: BooleanArray

	@OriginalMember(owner = "client!mt", name = "a", descriptor = "I")
	private val defaultScreen: Int

	init {
		this.configClient = configClient
		this.configClient!!.getGroupCapacity(1)
		@Pc(22) val packet: Packet = Packet(this.configClient.getFile(0, 0))
		@Pc(26) val version: Int = packet.readUByte()
		if (version > 3) {
			this.sequences = arrayOfNulls<IntArray>(0)
			this.shuffled = BooleanArray(0)
			this.defaultScreen = -1
		} else {
			@Pc(47) val opCount: Int = packet.readUByte()
			@Pc(50) val opTypes: Array<TipRenderDetails?>? = TipRenderDetails.values()
			@Pc(52) var compatible: Boolean = true
			@Pc(62) val local62: Int
			@Pc(68) val local68: Int
			if (opCount == opTypes!!.size) {
				var opIndex: Int = 0
				while (opIndex < opTypes.size) {
					val opVersion: Int = packet.readUByte()
					if (opVersion != opTypes[opIndex]!!.version) {
						compatible = false
						break
					}
					opIndex++
				}
			} else {
				compatible = false
			}
			if (compatible) {
				val sequenceCount: Int = packet.readUByte()
				val maxSequenceId: Int = packet.readUByte()
				if (version > 2) {
					this.defaultScreen = packet.readShort()
				} else {
					this.defaultScreen = -1
				}
				this.sequences = arrayOfNulls<IntArray>(maxSequenceId + 1)
				this.shuffled = BooleanArray(maxSequenceId + 1)
				@Pc(146) var sequenceId: Int
				@Pc(140) var i: Int = 0
				while (i < sequenceCount) {
					sequenceId = packet.readUByte()
					this.shuffled[sequenceId] = packet.readUByte() == 1
					@Pc(162) val length: Int = packet.readUShort()
					@Pc(175) var j: Int
					if (this.defaultScreen == -1) {
						this.sequences[sequenceId] = IntArray(length)
						j = 0
						while (j < length) {
							this.sequences[sequenceId]!![j] = packet.readUShort()
							j++
						}
					} else {
						this.sequences[sequenceId] = IntArray(length + 1)
						this.sequences[sequenceId]!![0] = this.defaultScreen
						j = 0
						while (j < length) {
							this.sequences[sequenceId]!![j + 1] = packet.readUShort()
							j++
						}
					}
					i++
				}
				sequenceId = 0
				while (sequenceId < maxSequenceId + 1) {
					if (this.sequences[sequenceId] == null) {
						if (this.defaultScreen == -1) {
							this.sequences[sequenceId] = IntArray(0)
						} else {
							this.sequences[sequenceId] = intArrayOf(this.defaultScreen)
						}
					}
					sequenceId++
				}
			} else {
				this.shuffled = BooleanArray(0)
				this.sequences = arrayOfNulls<IntArray>(0)
				this.defaultScreen = -1
			}
		}
	}

	@OriginalMember(owner = "client!mt", name = "a", descriptor = "(IB)Lclient!vp;")
	fun loadScreenType(@OriginalArg(0) id: Int): LoadingScreenType? {
		@Pc(10) val data: ByteArray? = this.configClient!!.getFile(1, id)
		@Pc(19) val loadingScreenType: LoadingScreenType = LoadingScreenType()
		loadingScreenType.decode(Packet(data))
		return loadingScreenType
	}

	@OriginalMember(owner = "client!mt", name = "a", descriptor = "(I)Z")
	fun hasDefault(): Boolean {
		return this.defaultScreen != -1
	}

	@OriginalMember(owner = "client!mt", name = "a", descriptor = "(II)[I")
	fun getSequence(@OriginalArg(0) sequenceId: Int): IntArray? {
		if (sequenceId < 0 || sequenceId >= this.sequences.size) {
			return if (this.defaultScreen == -1) IntArray(0) else intArrayOf(this.defaultScreen)
		} else if (this.shuffled[sequenceId] && this.sequences[sequenceId]!!.size > 1) {
			@Pc(60) val start: Int = if (this.defaultScreen == -1) 0 else 1
			@Pc(64) val random: Random = Random()
			@Pc(71) val sequence: IntArray = IntArray(this.sequences[sequenceId]!!.size)
			Arrays.copy(this.sequences[sequenceId], 0, sequence, 0, sequence.size)
			@Pc(83) var i: Int = start
			while (i < sequence.size) {
				@Pc(95) val swapIndex: Int = start + Utils.randomInt(sequence.size - start, random)
				@Pc(99) val temp: Int = sequence[i]
				sequence[i] = sequence[swapIndex]
				sequence[swapIndex] = temp
				i++
			}
			return sequence
		} else {
			return this.sequences[sequenceId]
		}
	}

	companion object {
		@OriginalMember(owner = "client!ef", name = "f", descriptor = "Lclient!jr;")
		@JvmField
		val aClass169_57: LRUCache = LRUCache(128, 4)

		@OriginalMember(owner = "client!la", name = "r", descriptor = "Lclient!mt;")
		@JvmField
		var aClass214_1: LoadingScreenManager? = null

		@OriginalMember(owner = "client!wga", name = "v", descriptor = "(I)V")
		@JvmStatic
		fun clearLoadingScreenSpriteCache() {
			LoadingScreenManager.aClass169_57.reset()
		}
	}
}
