package com.jagex.game.runetek5.cache.node

import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.config.item.ItemType
import com.jagex.game.runetek5.core.datastruct.key.Deque
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!sa")
class ObjStack : Node() {

	@OriginalMember(owner = "client!sa", name = "q", descriptor = "Lclient!uh;")
	@JvmField
	val groundItems: Deque = Deque()

	companion object {
		@OriginalMember(owner = "client!np", name = "b", descriptor = "Lclient!eq;")
		@JvmField
		val GROUND_ITEMS: IterableHashTable = IterableHashTable(64)

		@OriginalMember(owner = "client!el", name = "a", descriptor = "(Lclient!be;IIII)V")
		@JvmStatic
		fun addGroundItem(@OriginalArg(0) objStackEntry: ObjStackEntry?, @OriginalArg(1) level: Int, @OriginalArg(2) absY: Int, @OriginalArg(3) absX: Int) {
			@Pc(16) val key: Long = ((absY shl 14) or (level shl 28) or absX).toLong()
			@Pc(22) var objStack: ObjStack? = ObjStack.GROUND_ITEMS.get(key) as ObjStack?
			if (objStack == null) {
				objStack = ObjStack()
				ObjStack.GROUND_ITEMS.put(key, objStack)
				objStack.groundItems.addLast(objStackEntry)
				return
			}
			@Pc(47) var objType: ItemType? = TypeLists.OBJ.list(objStackEntry!!.id)
			@Pc(50) var value: Int = objType!!.value
			if (objType.stackable == 1) {
				value *= objStackEntry.count + 1
			}
			@Pc(71) var objStackEntry2: ObjStackEntry? = objStack.groundItems.first() as ObjStackEntry?
			while (objStackEntry2 != null) {
				objType = TypeLists.OBJ.list(objStackEntry2.id)
				@Pc(82) var otherValue: Int = objType!!.value
				if (objType.stackable == 1) {
					otherValue *= objStackEntry2.count + 1
				}
				if (otherValue < value) {
					Node.insertBefore(objStackEntry, objStackEntry2)
					return
				}
				objStackEntry2 = objStack.groundItems.next() as ObjStackEntry?
			}
			objStack.groundItems.addLast(objStackEntry)
		}
	}
}
