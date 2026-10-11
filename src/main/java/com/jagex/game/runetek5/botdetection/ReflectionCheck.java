package com.jagex.game.runetek5.botdetection;

import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.sign.SignedResource;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!bw")
public final class ReflectionCheck extends Node {

	@OriginalMember(owner = "client!bw", name = "o", descriptor = "I")
	public int operationCount;

	@OriginalMember(owner = "client!bw", name = "p", descriptor = "[Lclient!of;")
	public SignedResource[] methodsToCheck;

	@OriginalMember(owner = "client!bw", name = "q", descriptor = "[[[B")
	public byte[][][] argumentBuffers;

	@OriginalMember(owner = "client!bw", name = "s", descriptor = "I")
	public int id;

	@OriginalMember(owner = "client!bw", name = "t", descriptor = "[I")
	public int[] exceptionsEncountered;

	@OriginalMember(owner = "client!bw", name = "u", descriptor = "[I")
	public int[] operations;

	@OriginalMember(owner = "client!bw", name = "x", descriptor = "[Lclient!of;")
	public SignedResource[] fieldsToCheck;

	@OriginalMember(owner = "client!bw", name = "y", descriptor = "[I")
	public int[] fieldInput;
}
