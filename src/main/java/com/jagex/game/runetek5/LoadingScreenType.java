package com.jagex.game.runetek5;

import com.jagex.game.runetek5.buffer.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.config.gametip.ITipDetails;
import com.jagex.game.runetek5.config.gametip.component.ColorLoadingBarElement;
import com.jagex.game.runetek5.config.gametip.component.OutlinedNewsItem;
import com.jagex.game.runetek5.config.gametip.component.RotatingImageInstance;
import com.jagex.game.runetek5.load.LoadingScreenClearConfig;
import com.jagex.game.runetek5.client.loading.screen.op.instance.TextInstance;
import com.jagex.game.runetek5.config.gametip.component.AnimatedLoadingBarElement;
import com.jagex.game.runetek5.config.gametip.component.MultiSpriteLoadingBarElement;
import com.jagex.game.runetek5.config.gametip.component.SpritePositioning;
import com.jagex.game.runetek5.client.loading.screen.op.instance.BackgroundImageInstance;
import com.jagex.game.runetek5.config.gametip.component.ImageProgressBarInstance;

@OriginalClass("client!vp")
public final class LoadingScreenType {

	@OriginalMember(owner = "client!vp", name = "c", descriptor = "I")
	public int fadeDuration;

	@OriginalMember(owner = "client!vp", name = "d", descriptor = "[Lclient!af;")
	public ITipDetails[] ops;

	@OriginalMember(owner = "client!vp", name = "f", descriptor = "I")
	public int start;

	@OriginalMember(owner = "client!vp", name = "a", descriptor = "(ZLclient!ad;Lclient!cea;)Lclient!af;")
	private ITipDetails decodeOp(@OriginalArg(1) TipRenderDetails opType, @OriginalArg(2) Packet packet) {
		if (TipRenderDetails.aClass6_11 == opType) {
			return LoadingScreenClearConfig.decode(packet);
		} else if (TipRenderDetails.aClass6_12 == opType) {
			return ColorLoadingBarElement.decode(packet);
		} else if (TipRenderDetails.aClass6_4 == opType) {
			return OutlinedNewsItem.decode(packet);
		} else if (opType == TipRenderDetails.aClass6_5) {
			return RotatingImageInstance.decode(packet);
		} else if (opType == TipRenderDetails.aClass6_10) {
			return SpritePositioning.decode(packet);
		} else if (opType == TipRenderDetails.aClass6_2) {
			return ImageProgressBarInstance.decode(packet);
		} else if (TipRenderDetails.OPPLAYER5 == opType) {
			return MultiSpriteLoadingBarElement.decode(packet);
		} else if (opType == TipRenderDetails.aClass6_7) {
			return TextInstance.decode(packet);
		} else if (TipRenderDetails.aClass6_6 == opType) {
			return BackgroundImageInstance.decode(packet);
		} else if (TipRenderDetails.aClass6_9 == opType) {
			return AnimatedLoadingBarElement.decode(packet);
		} else {
			return null;
		}
	}

	@OriginalMember(owner = "client!vp", name = "a", descriptor = "(Lclient!cea;I)V")
	public void decode(@OriginalArg(0) Packet packet) {
		this.start = packet.readUMedium();
		this.fadeDuration = packet.readUShort();
		this.ops = new ITipDetails[packet.readUByte()];
		@Pc(26) TipRenderDetails[] opTypes = TipRenderDetails.values();
		for (@Pc(28) int i = 0; i < this.ops.length; i++) {
			this.ops[i] = this.decodeOp(opTypes[packet.readUByte()], packet);
		}
	}
}
