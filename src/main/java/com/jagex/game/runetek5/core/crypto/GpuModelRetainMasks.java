package com.jagex.game.runetek5.core.crypto;

import com.jagex.game.runetek5.ClientTrigger;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.core.datastruct.ref.key.ShadedMaskTest;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.client.ClientState;

public final class GpuModelRetainMasks {

	@OriginalMember(owner = "client!ku", name = "a", descriptor = "(III)Z")
	public static boolean retainNormals(@OriginalArg(0) int flags, @OriginalArg(1) int functionMask) {
		@Pc(25) boolean bufferMutable = (flags & 0x37) == 0 ? GpuModelRetainMasks.requiresColorBufferRebuild(functionMask, flags) : GpuModelRetainMasks.requiresNormalsBufferRebuild(functionMask, flags);
		return bufferMutable | GpuModelRetainMasks.altersLight(functionMask, flags) | (functionMask & 0x10000) != 0;
	}

	@OriginalMember(owner = "client!ku", name = "b", descriptor = "(III)Z")
	public static boolean retainBillboards(@OriginalArg(0) int flags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x400) != 0;
	}
	@OriginalMember(owner = "client!vf", name = "a", descriptor = "(IBI)Z")
	public static boolean retainFaceAlphas(@OriginalArg(0) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x40000) != 0 | Model.retainFaceAlpha(preferenceFlags, functionMask) || GpuModelRetainMasks.requiresColorBufferRebuild(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!uf", name = "d", descriptor = "(III)Z")
	public static boolean copyBillboards(@OriginalArg(0) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return (functionMask & 0xC580) != 0;
	}

	@OriginalMember(owner = "client!mba", name = "b", descriptor = "(III)Z")
	public static boolean altersFacing(@OriginalArg(0) int functionMask, @OriginalArg(1) int preferenceFlags) {
		return (functionMask & 0x10) != 0;
	}

	@OriginalMember(owner = "client!va", name = "a", descriptor = "(III)Z")
	public static boolean altersLight(@OriginalArg(0) int functionMask, @OriginalArg(1) int preferenceFlags) {
		return (functionMask & 0x18) != 0 | (functionMask & 0x220) == 544;
	}

	@OriginalMember(owner = "client!ui", name = "a", descriptor = "(III)Z")
	public static boolean altersAnyVertices(@OriginalArg(0) int functionMask, @OriginalArg(1) int preferenceFlags) {
		return (GpuModelRetainMasks.altersVerticesX(preferenceFlags, functionMask) | GpuModelRetainMasks.altersVerticesY(functionMask, preferenceFlags) | GpuModelRetainMasks.altersVerticesZ(preferenceFlags, functionMask)) & GpuModelRetainMasks.hasVertices(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(IBI)Z")
	public static boolean hasElementsBuffer(@OriginalArg(0) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return (functionMask & 0x800) != 0;
	}

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(IBI)Z")
	public static boolean retainVerticesX(@OriginalArg(0) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return GpuModelRetainMasks.altersVerticesX(preferenceFlags, functionMask) | (functionMask & 0x70000) != 0 || GpuModelRetainMasks.altersAnyVertices(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!vda", name = "b", descriptor = "(III)Z")
	public static boolean altersTextures(@OriginalArg(1) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x8000) != 0;
	}

	@OriginalMember(owner = "client!wda", name = "a", descriptor = "(IBI)Z")
	public static boolean hasColors(@OriginalArg(0) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return (functionMask & 0x800) != 0;
	}

	@OriginalMember(owner = "client!qia", name = "a", descriptor = "(IIB)Z")
	public static boolean altersTextureCoordinates(@OriginalArg(0) int functionMask, @OriginalArg(1) int preferenceFlags) {
		return false;
	}

	@OriginalMember(owner = "client!tk", name = "a", descriptor = "(III)Z")
	public static boolean retainVerticesZ(@OriginalArg(0) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x70000) != 0 | GpuModelRetainMasks.altersVerticesZ(preferenceFlags, functionMask) || GpuModelRetainMasks.altersAnyVertices(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!ca", name = "a", descriptor = "(III)Z")
	public static boolean retainVerticesY(@OriginalArg(1) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return GpuModelRetainMasks.altersVerticesY(functionMask, preferenceFlags) | (functionMask & 0x70000) != 0 || GpuModelRetainMasks.altersAnyVertices(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!qe", name = "a", descriptor = "(III)Z")
	public static boolean requiresTexCoordBufferRebuild(@OriginalArg(1) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return GpuModelRetainMasks.altersTextureCoordinates(functionMask, preferenceFlags) & GpuModelRetainMasks.hasTextureCoordinates(preferenceFlags, functionMask);
	}

	@OriginalMember(owner = "client!qi", name = "a", descriptor = "(BII)Z")
	public static boolean hasNormals(@OriginalArg(1) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x800) != 0 && (preferenceFlags & 0x37) != 0;
	}

	@OriginalMember(owner = "client!pf", name = "b", descriptor = "(IIB)Z")
	public static boolean requiresColorBufferRebuild(@OriginalArg(0) int functionMask, @OriginalArg(1) int preferenceFlags) {
		if (GpuModelRetainMasks.hasColors(functionMask, preferenceFlags)) {
			return GpuModelRetainMasks.altersColor(functionMask, preferenceFlags) | (functionMask & 0x9000) != 0 | Model.retainFaceAlpha(preferenceFlags, functionMask) ? true : (preferenceFlags & 0x37) == 0 & ((functionMask & 0x2000) != 0 | GpuModelRetainMasks.altersLight(functionMask, preferenceFlags) | ShadedMaskTest.usesShading(preferenceFlags, functionMask));
		} else {
			return false;
		}
	}
	@OriginalMember(owner = "client!aq", name = "a", descriptor = "(III)Z")
	public static boolean retainTextureCoordinates(@OriginalArg(1) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return GpuModelRetainMasks.altersTextureCoordinates(functionMask, preferenceFlags) || GpuModelRetainMasks.requiresTexCoordBufferRebuild(preferenceFlags, functionMask);
	}

	@OriginalMember(owner = "client!pa", name = "a", descriptor = "(III)Z")
	public static boolean altersVertices(@OriginalArg(1) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x20) != 0;
	}

	@OriginalMember(owner = "client!hf", name = "a", descriptor = "(IBI)Z")
	public static boolean copyElementsBuffer(@OriginalArg(0) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return GpuModelRetainMasks.altersFacing(functionMask, preferenceFlags) & GpuModelRetainMasks.hasElementsBuffer(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!kaa", name = "a", descriptor = "(III)Z")
	public static boolean altersFaces(@OriginalArg(0) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x180) != 0;
	}

	@OriginalMember(owner = "client!hga", name = "d", descriptor = "(III)Z")
	public static boolean altersVerticesY(@OriginalArg(1) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return (functionMask & 0x22) != 0;
	}

	@OriginalMember(owner = "client!nia", name = "a", descriptor = "(III)Z")
	public static boolean requiresNormalsBufferRebuild(@OriginalArg(0) int functionMask, @OriginalArg(1) int preferenceFlags) {
		return GpuModelRetainMasks.hasNormals(preferenceFlags, functionMask) & (GpuModelRetainMasks.altersLight(functionMask, preferenceFlags) | (functionMask & 0x2000) != 0 | ShadedMaskTest.usesShading(preferenceFlags, functionMask));
	}

	@OriginalMember(owner = "client!kfa", name = "a", descriptor = "(IIZ)Z")
	public static boolean retainFaceColors(@OriginalArg(0) int preferenceFlags, @OriginalArg(1) int functionMask) {
		return GpuModelRetainMasks.altersColor(functionMask, preferenceFlags) || GpuModelRetainMasks.requiresColorBufferRebuild(functionMask, preferenceFlags);
	}

	@OriginalMember(owner = "client!pc", name = "a", descriptor = "(III)Z")
	public static boolean hasVertices(@OriginalArg(0) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return (functionMask & 0x800) != 0;
	}

	@OriginalMember(owner = "client!fca", name = "a", descriptor = "(BII)Z")
	public static boolean altersColor(@OriginalArg(1) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return (functionMask & 0x84080) != 0;
	}

	@OriginalMember(owner = "client!kj", name = "a", descriptor = "(IIB)Z")
	public static boolean altersVerticesZ(@OriginalArg(0) int preferenceFlags, @OriginalArg(1) int functionMask) {
		return (functionMask & 0x34) != 0;
	}

	@OriginalMember(owner = "client!fca", name = "a", descriptor = "(III)Z")
	public static boolean hasTextureCoordinates(@OriginalArg(0) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x800) != 0;
	}
	@OriginalMember(owner = "client!aj", name = "a", descriptor = "(IIB)Z")
	public static boolean altersShading(@OriginalArg(0) int functionMask, @OriginalArg(1) int preferenceFlags) {
		return (functionMask & 0x10000) != 0;
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(III)Z")
	public static boolean altersVerticesX(@OriginalArg(0) int preferenceFlags, @OriginalArg(1) int functionMask) {
		return (functionMask & 0x21) != 0;
	}

	@OriginalMember(owner = "client!nga", name = "a", descriptor = "(III)Z")
	public static boolean altersFaceColors(@OriginalArg(1) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x84080) != 0;
	}

	@OriginalMember(owner = "client!bn", name = "a", descriptor = "(III)Z")
	public static boolean retainFaceIndices(@OriginalArg(1) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return GpuModelRetainMasks.altersFacing(functionMask, preferenceFlags) | (functionMask & 0x60000) != 0 || GpuModelRetainMasks.copyElementsBuffer(preferenceFlags, functionMask);
	}
}
