package com.jagex.game.runetek5.toolkit.vertexdeclaration;

import com.jagex.game.runetek5.VertexAttribute;
import com.jagex.game.runetek5.toolkit.opengl.GLFormat;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.DataType;
import org.openrs2.deob.annotation.OriginalArg;
import com.jagex.game.runetek5.utils.Utils;

public final class VertexDeclarationAction {
	@OriginalMember(owner = "client!daa", name = "m", descriptor = "Lclient!daa;")
	public static final VertexAttribute TEXCOORD = new VertexAttribute(4, 2, DataType.aClass26_8);

	@OriginalMember(owner = "client!daa", name = "p", descriptor = "I")
	public static final int BITS_PER_SLOT = Utils.bitLength(16);

	@OriginalMember(owner = "client!daa", name = "j", descriptor = "Lclient!daa;")
	public static final VertexAttribute NORMAL_POINTER_ACTION = new VertexAttribute(1, 3, DataType.aClass26_8);

	@OriginalMember(owner = "client!daa", name = "k", descriptor = "Lclient!daa;")
	public static final VertexAttribute COLOR = new VertexAttribute(2, 4, GLFormat.RGBA);

	@OriginalMember(owner = "client!daa", name = "h", descriptor = "Lclient!daa;")
	public static final VertexAttribute POSITION = new VertexAttribute(0, 3, DataType.aClass26_8);

	private VertexDeclarationAction() {
		/* empty */
	}
	@OriginalMember(owner = "client!daa", name = "l", descriptor = "Lclient!daa;")
	public static final VertexAttribute aClass69_4 = new VertexAttribute(3, 1, DataType.aClass26_8);

	@OriginalMember(owner = "client!daa", name = "b", descriptor = "(II)Lclient!daa;")
	public static VertexAttribute forIndex(@OriginalArg(0) int index) {
		if (index == 0) {
			return VertexDeclarationAction.POSITION;
		} else if (index == 1) {
			return VertexDeclarationAction.NORMAL_POINTER_ACTION;
		} else if (index == 2) {
			return VertexDeclarationAction.COLOR;
		} else if (index == 3) {
			return aClass69_4;
		} else if (index == 4) {
			return VertexDeclarationAction.TEXCOORD;
		} else if (index == 5) {
			return aClass69_6;
		} else if (index == 6) {
			return aClass69_7;
		} else {
			return null;
		}
	}

	@OriginalMember(owner = "client!daa", name = "o", descriptor = "Lclient!daa;")
	public static final VertexAttribute aClass69_7 = new VertexAttribute(6, 4, DataType.aClass26_8);

	@OriginalMember(owner = "client!daa", name = "n", descriptor = "Lclient!daa;")
	public static final VertexAttribute aClass69_6 = new VertexAttribute(5, 3, DataType.aClass26_8);
}
