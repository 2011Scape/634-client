package com.jagex.game.runetek5.statics;

import org.openrs2.deob.annotation.OriginalMember;

public final class NativeModelScratch {

	@OriginalMember(owner = "client!oa", name = "y", descriptor = "[I")
	private static final int[] sharedIntScratch = new int[Math.max(Math.max(104, 20), 24573)];

	@OriginalMember(owner = "client!oa", name = "F", descriptor = "[F")
	private static final float[] sharedFloatScratch = new float[20];

	@OriginalMember(owner = "client!oa", name = "G", descriptor = "[I")
	public static final int[] pointLightData = sharedIntScratch;

	@OriginalMember(owner = "client!oa", name = "I", descriptor = "[I")
	public static final int[] pickingResult = new int[6];

	@OriginalMember(owner = "client!oa", name = "J", descriptor = "[S")
	public static final short[] particleMaterialIds = new short[8191];

	@OriginalMember(owner = "client!oa", name = "K", descriptor = "[B")
	public static final byte[] aByteArray96 = new byte[8191];

	@OriginalMember(owner = "client!oa", name = "L", descriptor = "[I")
	public static final int[] particleColours = new int[8191];

	@OriginalMember(owner = "client!oa", name = "M", descriptor = "[I")
	public static final int[] particlePositions = sharedIntScratch;

	@OriginalMember(owner = "client!oa", name = "O", descriptor = "[F")
	public static final float[] pointLightIntensities = sharedFloatScratch;

	@OriginalMember(owner = "client!oa", name = "P", descriptor = "[I")
	public static final int[] particleSizes = new int[8191];

	@OriginalMember(owner = "client!oa", name = "Q", descriptor = "[I")
	public static final int[] effectVertices = sharedIntScratch;
}
