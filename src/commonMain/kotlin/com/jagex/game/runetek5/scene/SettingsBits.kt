package com.jagex.game.runetek5.scene

import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.core.constants.TileFlag
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object SettingsBits {
	@OriginalMember(owner = "client!raa", name = "w", descriptor = "[[[B")
	@JvmField
	var tileFunctionMasks: Array<Array<ByteArray?>?>? = null

	@OriginalMember(owner = "client!su", name = "a", descriptor = "(IIII)V")
	@JvmStatic
	fun allocateTileFunctionMasks(@OriginalArg(1) sceneSizeY: Int, @OriginalArg(2) sceneSizeX: Int) {
		SettingsBits.tileFunctionMasks = Array<Array<ByteArray?>?>(4) { Array<ByteArray?>(sceneSizeX) { ByteArray(sceneSizeY) } }
	}

	@OriginalMember(owner = "client!jca", name = "b", descriptor = "(B)V")
	@JvmStatic
	fun clearTileFunctionMasks() {
		@Pc(3) var i: Int = 0
		while (i < SettingsBits.tileFunctionMasks!!.size) {
			@Pc(7) var j: Int = 0
			while (j < SettingsBits.tileFunctionMasks!![0]!!.size) {
				@Pc(11) var k: Int = 0
				while (k < SettingsBits.tileFunctionMasks!![0]!![0]!!.size) {
					SettingsBits.tileFunctionMasks!![i]!![j]!![k] = 0
					k++
				}
				j++
			}
			i++
		}
	}

	@OriginalMember(owner = "client!jl", name = "a", descriptor = "(IZIII)Z")
	@JvmStatic
	fun isVisibleFromLevel(@OriginalArg(0) z: Int, @OriginalArg(2) x: Int, @OriginalArg(3) renderLevel: Int, @OriginalArg(4) level: Int): Boolean {
		if ((SettingsBits.tileFunctionMasks!![0]!![x]!![z].toInt() and TileFlag.BRIDGE) != 0) {
			return true
		} else if ((SettingsBits.tileFunctionMasks!![level]!![x]!![z].toInt() and TileFlag.INVISIBLE) == 0) {
			return renderLevel == SettingsBits.getRenderLevel(level, z, x)
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!bb", name = "a", descriptor = "(IIZ)Z")
	@JvmStatic
	fun isBridge(@OriginalArg(0) y: Int, @OriginalArg(1) x: Int): Boolean {
		if (x >= 0 && y >= 0 && x < SettingsBits.tileFunctionMasks!![1]!!.size && SettingsBits.tileFunctionMasks!![1]!![x]!!.size > y) {
			return (SettingsBits.tileFunctionMasks!![1]!![x]!![y].toInt() and TileFlag.BRIDGE) != 0
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!od", name = "b", descriptor = "(IIII)I")
	@JvmStatic
	fun getRenderLevel(@OriginalArg(1) level: Int, @OriginalArg(2) z: Int, @OriginalArg(3) x: Int): Int {
		if ((SettingsBits.tileFunctionMasks!![level]!![x]!![z].toInt() and 0x8) == 0) {
			return if (level <= 0 || (SettingsBits.tileFunctionMasks!![1]!![x]!![z].toInt() and 0x2) == 0) level else level - 1
		} else {
			return 0
		}
	}
}
