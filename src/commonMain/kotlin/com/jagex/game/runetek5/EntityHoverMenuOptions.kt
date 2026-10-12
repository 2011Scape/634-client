package com.jagex.game.runetek5

import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.menu.MiniMenuBuilder
import com.jagex.game.runetek5.entity.NPCEntity
import com.jagex.game.runetek5.entity.PathingEntity
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

object EntityHoverMenuOptions {

	@OriginalMember(owner = "client!ph", name = "a", descriptor = "(ILclient!kda;)V")
	@JvmStatic
	fun entityHovered(@OriginalArg(1) pathingEntity: PathingEntity?) {
		if (pathingEntity is NPCEntity) {
			@Pc(28) val npcEntity: NPCEntity = pathingEntity
			if (npcEntity.npcType != null) {
				MiniMenuBuilder.npcHovered(PlayerUpdate.localPlayer!!.level != npcEntity.level, npcEntity)
			}
		} else if (pathingEntity is PlayerEntity) {
			@Pc(12) val playerEntity: PlayerEntity = pathingEntity
			MiniMenu.playerHovered(playerEntity.level != PlayerUpdate.localPlayer!!.level, playerEntity)
		}
	}
}
