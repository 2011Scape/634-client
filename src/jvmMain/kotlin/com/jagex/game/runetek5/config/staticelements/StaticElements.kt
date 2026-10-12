package com.jagex.game.runetek5.config.staticelements

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!tu")
class StaticElements {

	@OriginalMember(owner = "client!tu", name = "e", descriptor = "I")
	@JvmField
	var elementCount: Int = 0

	@OriginalMember(owner = "client!tu", name = "c", descriptor = "[I")
	@JvmField
	val mapCategoryIds: IntArray

	@OriginalMember(owner = "client!tu", name = "b", descriptor = "[I")
	@JvmField
	val bitpackedPlacement: IntArray

	@OriginalMember(owner = "client!tu", name = "<init>", descriptor = "(I)V")
	constructor(@OriginalArg(0) count: Int) {
		this.elementCount = count
		this.mapCategoryIds = IntArray(this.elementCount)
		this.bitpackedPlacement = IntArray(this.elementCount)
	}

	companion object {
		@OriginalMember(owner = "client!wt", name = "a", descriptor = "(Lclient!in;Ljava/lang/String;ZB)Lclient!tu;")
		@JvmStatic
		fun load(@OriginalArg(0) archive: Js5ResourceProvider?, @OriginalArg(1) string: String?, @OriginalArg(2) includeMembers: Boolean): StaticElements {
			@Pc(10) val groupId: Int = archive!!.getGroupId(string)
			if (groupId == -1) {
				return StaticElements(0)
			}
			@Pc(31) val fileIds: IntArray? = archive.getFileIds(groupId)
			@Pc(37) val mapElementList: StaticElements = StaticElements(fileIds!!.size)
			@Pc(39) var elementIndex: Int = 0
			@Pc(41) var fileIndex: Int = 0
			while (true) {
				while (elementIndex < mapElementList.elementCount) {
					@Pc(55) val packet: Packet = Packet(archive.getFile(groupId, fileIds[fileIndex++]))
					@Pc(59) val placement: Int = packet.readInt()
					@Pc(65) val categoryId: Int = packet.readUShort()
					@Pc(69) val membersOnly: Int = packet.readUByte()
					if (!includeMembers && membersOnly == 1) {
						mapElementList.elementCount--
					} else {
						mapElementList.bitpackedPlacement[elementIndex] = placement
						mapElementList.mapCategoryIds[elementIndex] = categoryId
						elementIndex++
					}
				}
				return mapElementList
			}
		}

		@OriginalMember(owner = "client!uc", name = "O", descriptor = "Lclient!tu;")
		@JvmField
		var aClass333_3: StaticElements? = null
	}
}
