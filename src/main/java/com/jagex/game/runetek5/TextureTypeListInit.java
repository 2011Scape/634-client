package com.jagex.game.runetek5;

import com.jagex.game.runetek5.config.material.MaterialProvider;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.material.texture.TextureTypeList;
import com.jagex.game.runetek5.client.ClientState;

public final class TextureTypeListInit {

	@OriginalMember(owner = "client!oia", name = "a", descriptor = "(Lclient!in;ZLclient!d;)V")
	public static void initialize(@OriginalArg(0) Js5ResourceProvider modelResource, @OriginalArg(2) MaterialProvider textureSource) {
		TextureTypeList.modelResource = modelResource;
		TextureTypeListInit.textureSource = textureSource;
	}
	@OriginalMember(owner = "client!gq", name = "K", descriptor = "Lclient!d;")
	public static MaterialProvider textureSource;
}
