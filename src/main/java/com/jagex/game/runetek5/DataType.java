package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.toolkit.opengl.GLActionFormat;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;

@OriginalClass("client!baa")
public final class DataType {

	@OriginalMember(owner = "client!baa", name = "a", descriptor = "I")
	public final int bytesPerEntry;

	@OriginalMember(owner = "client!baa", name = "<init>", descriptor = "(I)V")
	public DataType(@OriginalArg(0) int bytesPerEntry) {
		this.bytesPerEntry = bytesPerEntry;
	}

	@OriginalMember(owner = "client!baa", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!baa", name = "e", descriptor = "Lclient!baa;")
	public static final DataType aClass26_3 = new DataType(4);

	@OriginalMember(owner = "client!baa", name = "b", descriptor = "Lclient!baa;")
	public static final DataType aClass26_1 = new DataType(1);

	@OriginalMember(owner = "client!baa", name = "i", descriptor = "Lclient!baa;")
	public static final DataType aClass26_7 = new DataType(2);

	@OriginalMember(owner = "client!baa", name = "h", descriptor = "Lclient!baa;")
	public static final DataType aClass26_6 = new DataType(4);

	@OriginalMember(owner = "client!baa", name = "g", descriptor = "Lclient!baa;")
	public static final DataType aClass26_5 = new DataType(2);

	@OriginalMember(owner = "client!baa", name = "j", descriptor = "Lclient!baa;")
	public static final DataType aClass26_8 = new DataType(4);
	@OriginalMember(owner = "client!np", name = "a", descriptor = "(ILclient!baa;)I")
	public static int getGlType(@OriginalArg(1) DataType pixelActionFormat) {
		if (DataType.aClass26_1 == pixelActionFormat) {
			return 5120;
		} else if (pixelActionFormat == GLActionFormat.DEPTH_COMPONENT_24) {
			return 5122;
		} else if (pixelActionFormat == DataType.aClass26_3) {
			return 5124;
		} else if (GLFormat.RGBA == pixelActionFormat) {
			return 5121;
		} else if (pixelActionFormat == DataType.aClass26_5) {
			return 5123;
		} else if (pixelActionFormat == DataType.aClass26_6) {
			return 5125;
		} else if (DataType.aClass26_7 == pixelActionFormat) {
			return 5131;
		} else if (DataType.aClass26_8 == pixelActionFormat) {
			return 5126;
		} else {
			throw new IllegalArgumentException("");
		}
	}
}
