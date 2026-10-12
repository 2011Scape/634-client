package com.jagex.game.runetek5.js5.dependency

import com.jagex.game.runetek5.LoadingRequirement
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.client.loading.loader.LoadingRequirementType

object CacheableDependencies {
	@OriginalMember(owner = "client!up", name = "n", descriptor = "Lclient!up;")
	@JvmField
	val JAGDX: LoadingRequirement = LoadingRequirement(LoadingRequirementType.NATIVE_LIBRARY)

	@OriginalMember(owner = "client!up", name = "J", descriptor = "Lclient!up;")
	@JvmField
	val FONTS: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "l", descriptor = "Lclient!up;")
	@JvmField
	val JACLIB: LoadingRequirement = LoadingRequirement(LoadingRequirementType.NATIVE_LIBRARY)

	@OriginalMember(owner = "client!up", name = "g", descriptor = "Lclient!up;")
	@JvmField
	val DEFAULTS: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "m", descriptor = "Lclient!up;")
	@JvmField
	val JAGGL: LoadingRequirement = LoadingRequirement(LoadingRequirementType.NATIVE_LIBRARY)

	@OriginalMember(owner = "client!up", name = "s", descriptor = "Lclient!up;")
	@JvmField
	val SHADERS: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "H", descriptor = "Lclient!up;")
	@JvmField
	val INTERFACES: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "x", descriptor = "Lclient!up;")
	@JvmField
	val NPC: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "K", descriptor = "Lclient!up;")
	@JvmField
	val WORLD_MAP: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5_GROUP)

	@OriginalMember(owner = "client!up", name = "q", descriptor = "Lclient!up;")
	@JvmField
	val HW3D: LoadingRequirement = LoadingRequirement(LoadingRequirementType.NATIVE_LIBRARY)

	@OriginalMember(owner = "client!up", name = "w", descriptor = "Lclient!up;")
	@JvmField
	val ENUM: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "d", descriptor = "(I)[Lclient!up;")
	@JvmStatic
	fun getAll(): Array<LoadingRequirement?> {
		return arrayOf<LoadingRequirement?>(CacheableDependencies.DEFAULTS, CacheableDependencies.JACLIB, CacheableDependencies.JAGGL, CacheableDependencies.JAGDX, JAGMISC, SW3D, HW3D, JAGTHEORA, SHADERS, MATERIALS, CONFIG, LOC, ENUM, NPC, OBJ, SEQ, GFX, VARBIT, QC_MSG, QC_MENU, PARTICLES, BILLBOARDS, HUFFMAN, INTERFACES, CS2, CacheableDependencies.FONTS, WORLD_MAP)
	}

	@OriginalMember(owner = "client!up", name = "G", descriptor = "Lclient!up;")
	@JvmField
	val HUFFMAN: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5_FILE)

	@OriginalMember(owner = "client!up", name = "D", descriptor = "Lclient!up;")
	@JvmField
	val QC_MENU: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "u", descriptor = "Lclient!up;")
	@JvmField
	val CONFIG: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "E", descriptor = "Lclient!up;")
	@JvmField
	val PARTICLES: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "v", descriptor = "Lclient!up;")
	@JvmField
	val LOC: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "o", descriptor = "Lclient!up;")
	@JvmField
	val JAGMISC: LoadingRequirement = LoadingRequirement(LoadingRequirementType.NATIVE_LIBRARY)

	@OriginalMember(owner = "client!up", name = "p", descriptor = "Lclient!up;")
	@JvmField
	val SW3D: LoadingRequirement = LoadingRequirement(LoadingRequirementType.NATIVE_LIBRARY)

	@OriginalMember(owner = "client!up", name = "C", descriptor = "Lclient!up;")
	@JvmField
	val QC_MSG: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "z", descriptor = "Lclient!up;")
	@JvmField
	val SEQ: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "r", descriptor = "Lclient!up;")
	@JvmField
	val JAGTHEORA: LoadingRequirement = LoadingRequirement(LoadingRequirementType.NATIVE_LIBRARY)

	@OriginalMember(owner = "client!up", name = "t", descriptor = "Lclient!up;")
	@JvmField
	val MATERIALS: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "B", descriptor = "Lclient!up;")
	@JvmField
	val VARBIT: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "F", descriptor = "Lclient!up;")
	@JvmField
	val BILLBOARDS: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "I", descriptor = "Lclient!up;")
	@JvmField
	val CS2: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "A", descriptor = "Lclient!up;")
	@JvmField
	val GFX: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)

	@OriginalMember(owner = "client!up", name = "y", descriptor = "Lclient!up;")
	@JvmField
	val OBJ: LoadingRequirement = LoadingRequirement(LoadingRequirementType.JS5)
}
