package com.jagex.game.runetek5.core.crypto

import com.jagex.game.runetek5.core.datastruct.ref.key.ShadedMaskTest
import com.jagex.game.runetek5.graphics.model.Model
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

object GpuModelRetainMasks {

	@OriginalMember(owner = "client!ku", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun retainNormals(@OriginalArg(0) flags: Int, @OriginalArg(1) functionMask: Int): Boolean {
		@Pc(25) val bufferMutable: Boolean = if ((flags and 0x37) == 0) GpuModelRetainMasks.requiresColorBufferRebuild(functionMask, flags) else GpuModelRetainMasks.requiresNormalsBufferRebuild(functionMask, flags)
		return (bufferMutable or GpuModelRetainMasks.altersLight(functionMask, flags)) or ((functionMask and 0x10000) != 0)
	}

	@OriginalMember(owner = "client!ku", name = "b", descriptor = "(III)Z")
	@JvmStatic
	fun retainBillboards(@OriginalArg(0) flags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return (functionMask and 0x400) != 0
	}

	@OriginalMember(owner = "client!vf", name = "a", descriptor = "(IBI)Z")
	@JvmStatic
	fun retainFaceAlphas(@OriginalArg(0) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return (((functionMask and 0x40000) != 0) or Model.retainFaceAlpha(preferenceFlags, functionMask)) || GpuModelRetainMasks.requiresColorBufferRebuild(functionMask, preferenceFlags)
	}

	@OriginalMember(owner = "client!uf", name = "d", descriptor = "(III)Z")
	@JvmStatic
	fun copyBillboards(@OriginalArg(0) functionMask: Int, @OriginalArg(2) preferenceFlags: Int): Boolean {
		return (functionMask and 0xC580) != 0
	}

	@OriginalMember(owner = "client!mba", name = "b", descriptor = "(III)Z")
	@JvmStatic
	fun altersFacing(@OriginalArg(0) functionMask: Int, @OriginalArg(1) preferenceFlags: Int): Boolean {
		return (functionMask and 0x10) != 0
	}

	@OriginalMember(owner = "client!va", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun altersLight(@OriginalArg(0) functionMask: Int, @OriginalArg(1) preferenceFlags: Int): Boolean {
		return ((functionMask and 0x18) != 0) or ((functionMask and 0x220) == 544)
	}

	@OriginalMember(owner = "client!ui", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun altersAnyVertices(@OriginalArg(0) functionMask: Int, @OriginalArg(1) preferenceFlags: Int): Boolean {
		return ((GpuModelRetainMasks.altersVerticesX(preferenceFlags, functionMask) or GpuModelRetainMasks.altersVerticesY(functionMask, preferenceFlags)) or GpuModelRetainMasks.altersVerticesZ(preferenceFlags, functionMask)) and GpuModelRetainMasks.hasVertices(functionMask, preferenceFlags)
	}

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(IBI)Z")
	@JvmStatic
	fun hasElementsBuffer(@OriginalArg(0) functionMask: Int, @OriginalArg(2) preferenceFlags: Int): Boolean {
		return (functionMask and 0x800) != 0
	}

	@OriginalMember(owner = "client!pia", name = "a", descriptor = "(IBI)Z")
	@JvmStatic
	fun retainVerticesX(@OriginalArg(0) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return (GpuModelRetainMasks.altersVerticesX(preferenceFlags, functionMask) or ((functionMask and 0x70000) != 0)) || GpuModelRetainMasks.altersAnyVertices(functionMask, preferenceFlags)
	}

	@OriginalMember(owner = "client!vda", name = "b", descriptor = "(III)Z")
	@JvmStatic
	fun altersTextures(@OriginalArg(1) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return (functionMask and 0x8000) != 0
	}

	@OriginalMember(owner = "client!wda", name = "a", descriptor = "(IBI)Z")
	@JvmStatic
	fun hasColors(@OriginalArg(0) functionMask: Int, @OriginalArg(2) preferenceFlags: Int): Boolean {
		return (functionMask and 0x800) != 0
	}

	@OriginalMember(owner = "client!qia", name = "a", descriptor = "(IIB)Z")
	@JvmStatic
	fun altersTextureCoordinates(@OriginalArg(0) functionMask: Int, @OriginalArg(1) preferenceFlags: Int): Boolean {
		return false
	}

	@OriginalMember(owner = "client!tk", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun retainVerticesZ(@OriginalArg(0) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return (((functionMask and 0x70000) != 0) or GpuModelRetainMasks.altersVerticesZ(preferenceFlags, functionMask)) || GpuModelRetainMasks.altersAnyVertices(functionMask, preferenceFlags)
	}

	@OriginalMember(owner = "client!ca", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun retainVerticesY(@OriginalArg(1) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return (GpuModelRetainMasks.altersVerticesY(functionMask, preferenceFlags) or ((functionMask and 0x70000) != 0)) || GpuModelRetainMasks.altersAnyVertices(functionMask, preferenceFlags)
	}

	@OriginalMember(owner = "client!qe", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun requiresTexCoordBufferRebuild(@OriginalArg(1) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return GpuModelRetainMasks.altersTextureCoordinates(functionMask, preferenceFlags) and GpuModelRetainMasks.hasTextureCoordinates(preferenceFlags, functionMask)
	}

	@OriginalMember(owner = "client!qi", name = "a", descriptor = "(BII)Z")
	@JvmStatic
	fun hasNormals(@OriginalArg(1) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return ((functionMask and 0x800) != 0) && ((preferenceFlags and 0x37) != 0)
	}

	@OriginalMember(owner = "client!pf", name = "b", descriptor = "(IIB)Z")
	@JvmStatic
	fun requiresColorBufferRebuild(@OriginalArg(0) functionMask: Int, @OriginalArg(1) preferenceFlags: Int): Boolean {
		if (GpuModelRetainMasks.hasColors(functionMask, preferenceFlags)) {
			return if ((GpuModelRetainMasks.altersColor(functionMask, preferenceFlags) or ((functionMask and 0x9000) != 0)) or Model.retainFaceAlpha(preferenceFlags, functionMask)) true else (((preferenceFlags and 0x37) == 0) and ((((functionMask and 0x2000) != 0) or GpuModelRetainMasks.altersLight(functionMask, preferenceFlags)) or ShadedMaskTest.usesShading(preferenceFlags, functionMask)))
		} else {
			return false
		}
	}

	@OriginalMember(owner = "client!aq", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun retainTextureCoordinates(@OriginalArg(1) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return GpuModelRetainMasks.altersTextureCoordinates(functionMask, preferenceFlags) || GpuModelRetainMasks.requiresTexCoordBufferRebuild(preferenceFlags, functionMask)
	}

	@OriginalMember(owner = "client!pa", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun altersVertices(@OriginalArg(1) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return (functionMask and 0x20) != 0
	}

	@OriginalMember(owner = "client!hf", name = "a", descriptor = "(IBI)Z")
	@JvmStatic
	fun copyElementsBuffer(@OriginalArg(0) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return GpuModelRetainMasks.altersFacing(functionMask, preferenceFlags) and GpuModelRetainMasks.hasElementsBuffer(functionMask, preferenceFlags)
	}

	@OriginalMember(owner = "client!kaa", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun altersFaces(@OriginalArg(0) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return (functionMask and 0x180) != 0
	}

	@OriginalMember(owner = "client!hga", name = "d", descriptor = "(III)Z")
	@JvmStatic
	fun altersVerticesY(@OriginalArg(1) functionMask: Int, @OriginalArg(2) preferenceFlags: Int): Boolean {
		return (functionMask and 0x22) != 0
	}

	@OriginalMember(owner = "client!nia", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun requiresNormalsBufferRebuild(@OriginalArg(0) functionMask: Int, @OriginalArg(1) preferenceFlags: Int): Boolean {
		return GpuModelRetainMasks.hasNormals(preferenceFlags, functionMask) and ((GpuModelRetainMasks.altersLight(functionMask, preferenceFlags) or ((functionMask and 0x2000) != 0)) or ShadedMaskTest.usesShading(preferenceFlags, functionMask))
	}

	@OriginalMember(owner = "client!kfa", name = "a", descriptor = "(IIZ)Z")
	@JvmStatic
	fun retainFaceColors(@OriginalArg(0) preferenceFlags: Int, @OriginalArg(1) functionMask: Int): Boolean {
		return GpuModelRetainMasks.altersColor(functionMask, preferenceFlags) || GpuModelRetainMasks.requiresColorBufferRebuild(functionMask, preferenceFlags)
	}

	@OriginalMember(owner = "client!pc", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun hasVertices(@OriginalArg(0) functionMask: Int, @OriginalArg(2) preferenceFlags: Int): Boolean {
		return (functionMask and 0x800) != 0
	}

	@OriginalMember(owner = "client!fca", name = "a", descriptor = "(BII)Z")
	@JvmStatic
	fun altersColor(@OriginalArg(1) functionMask: Int, @OriginalArg(2) preferenceFlags: Int): Boolean {
		return (functionMask and 0x84080) != 0
	}

	@OriginalMember(owner = "client!kj", name = "a", descriptor = "(IIB)Z")
	@JvmStatic
	fun altersVerticesZ(@OriginalArg(0) preferenceFlags: Int, @OriginalArg(1) functionMask: Int): Boolean {
		return (functionMask and 0x34) != 0
	}

	@OriginalMember(owner = "client!fca", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun hasTextureCoordinates(@OriginalArg(0) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return (functionMask and 0x800) != 0
	}

	@OriginalMember(owner = "client!aj", name = "a", descriptor = "(IIB)Z")
	@JvmStatic
	fun altersShading(@OriginalArg(0) functionMask: Int, @OriginalArg(1) preferenceFlags: Int): Boolean {
		return (functionMask and 0x10000) != 0
	}

	@OriginalMember(owner = "client!wba", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun altersVerticesX(@OriginalArg(0) preferenceFlags: Int, @OriginalArg(1) functionMask: Int): Boolean {
		return (functionMask and 0x21) != 0
	}

	@OriginalMember(owner = "client!nga", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun altersFaceColors(@OriginalArg(1) preferenceFlags: Int, @OriginalArg(2) functionMask: Int): Boolean {
		return (functionMask and 0x84080) != 0
	}

	@OriginalMember(owner = "client!bn", name = "a", descriptor = "(III)Z")
	@JvmStatic
	fun retainFaceIndices(@OriginalArg(1) functionMask: Int, @OriginalArg(2) preferenceFlags: Int): Boolean {
		return (GpuModelRetainMasks.altersFacing(functionMask, preferenceFlags) or ((functionMask and 0x60000) != 0)) || GpuModelRetainMasks.copyElementsBuffer(preferenceFlags, functionMask)
	}
}
