package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.PathingEntity;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.client.menu.MiniMenuBuilder;

public final class EntityHoverMenuOptions {

	@OriginalMember(owner = "client!ph", name = "a", descriptor = "(ILclient!kda;)V")
	public static void entityHovered(@OriginalArg(1) PathingEntity pathingEntity) {
		if (pathingEntity instanceof NPCEntity) {
			@Pc(28) NPCEntity npcEntity = (NPCEntity) pathingEntity;
			if (npcEntity.npcType != null) {
				MiniMenuBuilder.npcHovered(PlayerUpdate.localPlayer.level != npcEntity.level, npcEntity);
			}
		} else if (pathingEntity instanceof PlayerEntity) {
			@Pc(12) PlayerEntity playerEntity = (PlayerEntity) pathingEntity;
			MiniMenu.playerHovered(playerEntity.level != PlayerUpdate.localPlayer.level, playerEntity);
		}
	}
}
