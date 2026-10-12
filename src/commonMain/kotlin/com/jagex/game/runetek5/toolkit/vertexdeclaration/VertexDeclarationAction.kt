package com.jagex.game.runetek5.toolkit.vertexdeclaration

import com.jagex.game.runetek5.DataType
import com.jagex.game.runetek5.VertexAttribute
import com.jagex.game.runetek5.toolkit.opengl.GLFormat
import com.jagex.game.runetek5.utils.Utils
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object VertexDeclarationAction {
	@OriginalMember(owner = "client!daa", name = "m", descriptor = "Lclient!daa;")
	@JvmField
	val TEXCOORD: VertexAttribute = VertexAttribute(4, 2, DataType.aClass26_8)

	@OriginalMember(owner = "client!daa", name = "p", descriptor = "I")
	@JvmField
	val BITS_PER_SLOT: Int = Utils.bitLength(16)

	@OriginalMember(owner = "client!daa", name = "j", descriptor = "Lclient!daa;")
	@JvmField
	val NORMAL_POINTER_ACTION: VertexAttribute = VertexAttribute(1, 3, DataType.aClass26_8)

	@OriginalMember(owner = "client!daa", name = "k", descriptor = "Lclient!daa;")
	@JvmField
	val COLOR: VertexAttribute = VertexAttribute(2, 4, GLFormat.RGBA)

	@OriginalMember(owner = "client!daa", name = "h", descriptor = "Lclient!daa;")
	@JvmField
	val POSITION: VertexAttribute = VertexAttribute(0, 3, DataType.aClass26_8)

	@OriginalMember(owner = "client!daa", name = "l", descriptor = "Lclient!daa;")
	@JvmField
	val aClass69_4: VertexAttribute = VertexAttribute(3, 1, DataType.aClass26_8)

	@OriginalMember(owner = "client!daa", name = "b", descriptor = "(II)Lclient!daa;")
	@JvmStatic
	fun forIndex(@OriginalArg(0) index: Int): VertexAttribute? {
		if (index == 0) {
			return VertexDeclarationAction.POSITION
		} else if (index == 1) {
			return VertexDeclarationAction.NORMAL_POINTER_ACTION
		} else if (index == 2) {
			return VertexDeclarationAction.COLOR
		} else if (index == 3) {
			return aClass69_4
		} else if (index == 4) {
			return VertexDeclarationAction.TEXCOORD
		} else if (index == 5) {
			return aClass69_6
		} else if (index == 6) {
			return aClass69_7
		} else {
			return null
		}
	}

	@OriginalMember(owner = "client!daa", name = "o", descriptor = "Lclient!daa;")
	@JvmField
	val aClass69_7: VertexAttribute = VertexAttribute(6, 4, DataType.aClass26_8)

	@OriginalMember(owner = "client!daa", name = "n", descriptor = "Lclient!daa;")
	@JvmField
	val aClass69_6: VertexAttribute = VertexAttribute(5, 3, DataType.aClass26_8)
}
