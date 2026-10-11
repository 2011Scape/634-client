package com.jagex.game.runetek5.js5.dependency;

import com.jagex.game.runetek5.LoadingRequirement;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.loading.loader.LoadingRequirementType;

public final class CacheableDependencies {
	@OriginalMember(owner = "client!up", name = "n", descriptor = "Lclient!up;")
	public static final LoadingRequirement JAGDX = new LoadingRequirement(LoadingRequirementType.NATIVE_LIBRARY);

	@OriginalMember(owner = "client!up", name = "J", descriptor = "Lclient!up;")
	public static final LoadingRequirement FONTS = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "l", descriptor = "Lclient!up;")
	public static final LoadingRequirement JACLIB = new LoadingRequirement(LoadingRequirementType.NATIVE_LIBRARY);

	@OriginalMember(owner = "client!up", name = "g", descriptor = "Lclient!up;")
	public static final LoadingRequirement DEFAULTS = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "m", descriptor = "Lclient!up;")
	public static final LoadingRequirement JAGGL = new LoadingRequirement(LoadingRequirementType.NATIVE_LIBRARY);

	private CacheableDependencies() {
		/* empty */
	}
	@OriginalMember(owner = "client!up", name = "s", descriptor = "Lclient!up;")
	public static final LoadingRequirement SHADERS = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "H", descriptor = "Lclient!up;")
	public static final LoadingRequirement INTERFACES = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "x", descriptor = "Lclient!up;")
	public static final LoadingRequirement NPC = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "K", descriptor = "Lclient!up;")
	public static final LoadingRequirement WORLD_MAP = new LoadingRequirement(LoadingRequirementType.JS5_GROUP);

	@OriginalMember(owner = "client!up", name = "q", descriptor = "Lclient!up;")
	public static final LoadingRequirement HW3D = new LoadingRequirement(LoadingRequirementType.NATIVE_LIBRARY);

	@OriginalMember(owner = "client!up", name = "w", descriptor = "Lclient!up;")
	public static final LoadingRequirement ENUM = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "d", descriptor = "(I)[Lclient!up;")
	public static LoadingRequirement[] getAll() {
		return new LoadingRequirement[] { CacheableDependencies.DEFAULTS, CacheableDependencies.JACLIB, CacheableDependencies.JAGGL, CacheableDependencies.JAGDX, JAGMISC, SW3D, HW3D, JAGTHEORA, SHADERS, MATERIALS, CONFIG, LOC, ENUM, NPC, OBJ, SEQ, GFX, VARBIT, QC_MSG, QC_MENU, PARTICLES, BILLBOARDS, HUFFMAN, INTERFACES, CS2, CacheableDependencies.FONTS, WORLD_MAP };
	}

	@OriginalMember(owner = "client!up", name = "G", descriptor = "Lclient!up;")
	public static final LoadingRequirement HUFFMAN = new LoadingRequirement(LoadingRequirementType.JS5_FILE);

	@OriginalMember(owner = "client!up", name = "D", descriptor = "Lclient!up;")
	public static final LoadingRequirement QC_MENU = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "u", descriptor = "Lclient!up;")
	public static final LoadingRequirement CONFIG = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "E", descriptor = "Lclient!up;")
	public static final LoadingRequirement PARTICLES = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "v", descriptor = "Lclient!up;")
	public static final LoadingRequirement LOC = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "o", descriptor = "Lclient!up;")
	public static final LoadingRequirement JAGMISC = new LoadingRequirement(LoadingRequirementType.NATIVE_LIBRARY);

	@OriginalMember(owner = "client!up", name = "p", descriptor = "Lclient!up;")
	public static final LoadingRequirement SW3D = new LoadingRequirement(LoadingRequirementType.NATIVE_LIBRARY);

	@OriginalMember(owner = "client!up", name = "C", descriptor = "Lclient!up;")
	public static final LoadingRequirement QC_MSG = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "z", descriptor = "Lclient!up;")
	public static final LoadingRequirement SEQ = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "r", descriptor = "Lclient!up;")
	public static final LoadingRequirement JAGTHEORA = new LoadingRequirement(LoadingRequirementType.NATIVE_LIBRARY);

	@OriginalMember(owner = "client!up", name = "t", descriptor = "Lclient!up;")
	public static final LoadingRequirement MATERIALS = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "B", descriptor = "Lclient!up;")
	public static final LoadingRequirement VARBIT = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "F", descriptor = "Lclient!up;")
	public static final LoadingRequirement BILLBOARDS = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "I", descriptor = "Lclient!up;")
	public static final LoadingRequirement CS2 = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "A", descriptor = "Lclient!up;")
	public static final LoadingRequirement GFX = new LoadingRequirement(LoadingRequirementType.JS5);

	@OriginalMember(owner = "client!up", name = "y", descriptor = "Lclient!up;")
	public static final LoadingRequirement OBJ = new LoadingRequirement(LoadingRequirementType.JS5);
}
