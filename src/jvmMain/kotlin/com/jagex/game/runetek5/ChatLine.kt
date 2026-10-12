package com.jagex.game.runetek5

import com.jagex.game.runetek5.client.ClientState
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember

@OriginalClass("client!og")
class ChatLine {

	@OriginalMember(owner = "client!og", name = "k", descriptor = "I")
	@JvmField
	var identifier: Int = ChatLine.nextIdentifier()

	@OriginalMember(owner = "client!og", name = "h", descriptor = "Ljava/lang/String;")
	@JvmField
	var message: String? = null

	@OriginalMember(owner = "client!og", name = "l", descriptor = "I")
	@JvmField
	var type: Int = 0

	@OriginalMember(owner = "client!og", name = "m", descriptor = "Ljava/lang/String;")
	@JvmField
	var name: String? = null

	@OriginalMember(owner = "client!og", name = "e", descriptor = "Ljava/lang/String;")
	@JvmField
	var simpleName: String? = null

	@OriginalMember(owner = "client!og", name = "b", descriptor = "Ljava/lang/String;")
	@JvmField
	var clan: String? = null

	@OriginalMember(owner = "client!og", name = "j", descriptor = "I")
	@JvmField
	var time: Int = 0

	@OriginalMember(owner = "client!og", name = "d", descriptor = "Ljava/lang/String;")
	@JvmField
	var crownedName: String? = null

	@OriginalMember(owner = "client!og", name = "g", descriptor = "I")
	@JvmField
	var effectFlags: Int = 0

	@OriginalMember(owner = "client!og", name = "f", descriptor = "I")
	@JvmField
	var quickchatMessageId: Int = 0

	@OriginalMember(owner = "client!og", name = "<init>", descriptor = "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)V")
	constructor(@OriginalArg(0) type: Int, @OriginalArg(1) effectFlags: Int, @OriginalArg(2) crownedName: String?, @OriginalArg(3) name: String?, @OriginalArg(4) simpleName: String?, @OriginalArg(5) clan: String?, @OriginalArg(6) quickchatMessageId: Int, @OriginalArg(7) message: String?) {
		this.message = message
		this.type = type
		this.name = name
		this.simpleName = simpleName
		this.clan = clan
		this.time = ClientState.clientCycle
		this.crownedName = crownedName
		this.effectFlags = effectFlags
		this.quickchatMessageId = quickchatMessageId
	}

	@OriginalMember(owner = "client!og", name = "a", descriptor = "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;IILjava/lang/String;Ljava/lang/String;)V")
	fun set(@OriginalArg(0) type: Int, @OriginalArg(1) effectFlags: Int, @OriginalArg(2) simpleName: String?, @OriginalArg(3) name: String?, @OriginalArg(4) clan: String?, @OriginalArg(6) quickchatMessageId: Int, @OriginalArg(7) crownedName: String?, @OriginalArg(8) message: String?) {
		this.identifier = ChatLine.nextIdentifier()
		this.name = name
		this.crownedName = crownedName
		this.time = ClientState.clientCycle
		this.simpleName = simpleName
		this.type = type
		this.clan = clan
		this.message = message
		this.effectFlags = effectFlags
		this.quickchatMessageId = quickchatMessageId
	}

	companion object {
		@OriginalMember(owner = "client!jb", name = "d", descriptor = "(B)I")
		@JvmStatic
		fun nextIdentifier(): Int {
			return ChatLine.nextChatLineId++
		}

		@OriginalMember(owner = "client!vn", name = "P", descriptor = "I")
		@JvmField
		var nextChatLineId: Int = 0
	}
}
