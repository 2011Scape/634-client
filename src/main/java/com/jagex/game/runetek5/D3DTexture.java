package com.jagex.game.runetek5;

import jagdx.IDirect3DBaseTexture;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.toolkit.D3DToolkit;
import com.jagex.game.runetek5.graphics.texture.sampler.SampleMode;

@OriginalClass("client!cg")
public abstract class D3DTexture {

	@OriginalMember(owner = "client!cg", name = "c", descriptor = "Lclient!ria;")
	public FilterMode filter = SampleMode.LINEAR;

	@OriginalMember(owner = "client!cg", name = "d", descriptor = "Lclient!baa;")
	public final DataType type;

	@OriginalMember(owner = "client!cg", name = "b", descriptor = "Lclient!bfa;")
	public final D3DToolkit toolkit;

	@OriginalMember(owner = "client!cg", name = "a", descriptor = "Z")
	public final boolean mipmapped;

	@OriginalMember(owner = "client!cg", name = "e", descriptor = "Lclient!wq;")
	public final PixelFormat format;

	@OriginalMember(owner = "client!cg", name = "<init>", descriptor = "(Lclient!bfa;Lclient!wq;Lclient!baa;ZI)V")
	public D3DTexture(@OriginalArg(0) D3DToolkit d3DToolkit, @OriginalArg(1) PixelFormat format, @OriginalArg(2) DataType type, @OriginalArg(3) boolean mipmapped, @OriginalArg(4) int pixelCount) {
		this.type = type;
		this.toolkit = d3DToolkit;
		this.mipmapped = mipmapped;
		this.format = format;
	}

	@OriginalMember(owner = "client!cg", name = "a", descriptor = "(Lclient!ria;I)V")
	public void setFilterMode(@OriginalArg(0) FilterMode filter) {
		this.filter = filter;
	}

	@OriginalMember(owner = "client!cg", name = "c", descriptor = "(I)Lclient!jagdx/IDirect3DBaseTexture;")
	public abstract IDirect3DBaseTexture getTexture();
}
