package com.jagex.game.runetek5;

import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclaration;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationComponent;
import jagdx.IDirect3DVertexDeclaration;
import jagdx.VertexElementCollection;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.toolkit.D3DToolkit;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationAction;

@OriginalClass("client!dxVertexLayout")
public final class dxVertexLayout extends VertexDeclaration {

	@OriginalMember(owner = "client!dxVertexLayout", name = "h", descriptor = "Lclient!jagdx/IDirect3DVertexDeclaration;")
	public final IDirect3DVertexDeclaration vertexDeclaration;

	@OriginalMember(owner = "client!dxVertexLayout", name = "<init>", descriptor = "(Lclient!bfa;[Lclient!jl;)V")
	public dxVertexLayout(@OriginalArg(0) D3DToolkit d3DToolkit, @OriginalArg(1) VertexDeclarationComponent[] vertexDeclarationElements) {
		@Pc(7) VertexElementCollection vertexElementCollection = new VertexElementCollection(d3DToolkit.peerTracker);
		@Pc(9) int usageIndex = 0;
		for (@Pc(11) int i = 0; i < vertexDeclarationElements.length; i++) {
			@Pc(21) int offset = 0;
			@Pc(25) VertexDeclarationComponent vertexDeclarationElement = vertexDeclarationElements[i];
			for (@Pc(27) int j = 0; vertexDeclarationElement.getSize() > j; j++) {
				@Pc(41) VertexAttribute action = vertexDeclarationElement.getAction(j);
				if (VertexDeclarationAction.POSITION == action) {
					vertexElementCollection.addElement(i, 2, 0, 0, 0, offset);
				} else if (action == VertexDeclarationAction.NORMAL_POINTER_ACTION) {
					vertexElementCollection.addElement(i, 2, 0, 3, 0, offset);
				} else if (VertexDeclarationAction.COLOR == action) {
					vertexElementCollection.addElement(i, 4, 0, 10, 0, offset);
				} else if (action == VertexDeclarationAction.aClass69_4) {
					vertexElementCollection.addElement(i, 0, 0, 5, usageIndex++, offset);
				} else if (VertexDeclarationAction.TEXCOORD == action) {
					vertexElementCollection.addElement(i, 1, 0, 5, usageIndex++, offset);
				} else if (VertexDeclarationAction.aClass69_6 == action) {
					vertexElementCollection.addElement(i, 2, 0, 5, usageIndex++, offset);
				} else if (VertexDeclarationAction.aClass69_7 == action) {
					vertexElementCollection.addElement(i, 3, 0, 5, usageIndex++, offset);
				}
				offset += action.pointerOffsetStep;
			}
		}
		vertexElementCollection.finish();
		this.vertexDeclaration = d3DToolkit.iDirect3DDevice.a(vertexElementCollection, null);
	}
}
