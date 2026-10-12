package com.jagex.game.runetek5

import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclaration
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationComponent
import jagdx.IDirect3DVertexDeclaration
import jagdx.VertexElementCollection
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.toolkit.D3DToolkit
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationAction

@OriginalClass("client!dxVertexLayout")
class dxVertexLayout @OriginalMember(owner = "client!dxVertexLayout", name = "<init>", descriptor = "(Lclient!bfa;[Lclient!jl;)V") constructor(@OriginalArg(0) d3DToolkit: D3DToolkit?, @OriginalArg(1) vertexDeclarationElements: Array<VertexDeclarationComponent?>?) : VertexDeclaration() {

	@OriginalMember(owner = "client!dxVertexLayout", name = "h", descriptor = "Lclient!jagdx/IDirect3DVertexDeclaration;")
	@JvmField
	val vertexDeclaration: IDirect3DVertexDeclaration?

	init {
		@Pc(7) val vertexElementCollection: VertexElementCollection = VertexElementCollection(d3DToolkit!!.peerTracker)
		@Pc(9) var usageIndex: Int = 0
		@Pc(11) var i: Int = 0
		while (i < vertexDeclarationElements!!.size) {
			@Pc(21) var offset: Int = 0
			@Pc(25) val vertexDeclarationElement: VertexDeclarationComponent? = vertexDeclarationElements[i]
			@Pc(27) var j: Int = 0
			while (vertexDeclarationElement!!.getSize() > j) {
				@Pc(41) val action: VertexAttribute? = vertexDeclarationElement.getAction(j)
				if (VertexDeclarationAction.POSITION === action) {
					vertexElementCollection.addElement(i, 2, 0, 0, 0, offset)
				} else if (action === VertexDeclarationAction.NORMAL_POINTER_ACTION) {
					vertexElementCollection.addElement(i, 2, 0, 3, 0, offset)
				} else if (VertexDeclarationAction.COLOR === action) {
					vertexElementCollection.addElement(i, 4, 0, 10, 0, offset)
				} else if (action === VertexDeclarationAction.aClass69_4) {
					vertexElementCollection.addElement(i, 0, 0, 5, usageIndex++, offset)
				} else if (VertexDeclarationAction.TEXCOORD === action) {
					vertexElementCollection.addElement(i, 1, 0, 5, usageIndex++, offset)
				} else if (VertexDeclarationAction.aClass69_6 === action) {
					vertexElementCollection.addElement(i, 2, 0, 5, usageIndex++, offset)
				} else if (VertexDeclarationAction.aClass69_7 === action) {
					vertexElementCollection.addElement(i, 3, 0, 5, usageIndex++, offset)
				}
				offset += action!!.pointerOffsetStep
				j++
			}
			i++
		}
		vertexElementCollection.finish()
		this.vertexDeclaration = d3DToolkit.iDirect3DDevice!!.a(vertexElementCollection, null)
	}
}
