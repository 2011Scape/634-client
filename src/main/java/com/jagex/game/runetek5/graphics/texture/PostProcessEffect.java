package com.jagex.game.runetek5.graphics.texture;

import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.graphics.gl.OpenGl2DTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

@OriginalClass("client!fba")
public abstract class PostProcessEffect extends Node {

	@OriginalMember(owner = "client!fba", name = "r", descriptor = "Z")
	public boolean isActive;

	@OriginalMember(owner = "client!fba", name = "o", descriptor = "Lclient!qo;")
	public final GlToolkit activeToolkit;

	@OriginalMember(owner = "client!fba", name = "<init>", descriptor = "(Lclient!qo;)V")
	public PostProcessEffect(@OriginalArg(0) GlToolkit glToolkit) {
		this.activeToolkit = glToolkit;
	}

	@OriginalMember(owner = "client!fba", name = "a", descriptor = "(Lclient!tp;Lclient!tp;IB)V")
	public abstract void draw(@OriginalArg(0) OpenGl2DTexture depthTexture, @OriginalArg(1) OpenGl2DTexture colorTexture, @OriginalArg(2) int pass);

	@OriginalMember(owner = "client!fba", name = "a", descriptor = "(Z)Z")
	public abstract boolean isLoaded();

	@OriginalMember(owner = "client!fba", name = "a", descriptor = "(IBI)V")
	public abstract void resize(@OriginalArg(0) int height, @OriginalArg(2) int width);

	@OriginalMember(owner = "client!fba", name = "a", descriptor = "(BI)V")
	public abstract void endPass(@OriginalArg(1) int pass);

	@OriginalMember(owner = "client!fba", name = "b", descriptor = "(I)Z")
	public abstract boolean createShaders();

	@OriginalMember(owner = "client!fba", name = "d", descriptor = "(I)Z")
	public final boolean requiresDepth() {
		return false;
	}

	@OriginalMember(owner = "client!fba", name = "a", descriptor = "(B)Z")
	public final boolean isActive() {
		return this.isActive;
	}

	@OriginalMember(owner = "client!fba", name = "b", descriptor = "(Z)I")
	public int getFormat() {
		return 0;
	}

	@OriginalMember(owner = "client!fba", name = "b", descriptor = "(B)V")
	public abstract void destroy();

	@OriginalMember(owner = "client!fba", name = "f", descriptor = "(I)I")
	public final int getPassCount() {
		return 1;
	}
}
