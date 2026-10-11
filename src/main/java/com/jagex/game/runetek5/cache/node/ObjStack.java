package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.config.item.ItemType;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!sa")
public final class ObjStack extends Node {

	@OriginalMember(owner = "client!sa", name = "q", descriptor = "Lclient!uh;")
	public final Deque groundItems = new Deque();
	@OriginalMember(owner = "client!np", name = "b", descriptor = "Lclient!eq;")
	public static final IterableHashTable GROUND_ITEMS = new IterableHashTable(64);

	@OriginalMember(owner = "client!el", name = "a", descriptor = "(Lclient!be;IIII)V")
	public static void addGroundItem(@OriginalArg(0) ObjStackEntry objStackEntry, @OriginalArg(1) int level, @OriginalArg(2) int absY, @OriginalArg(3) int absX) {
		@Pc(16) long key = (long) (absY << 14 | level << 28 | absX);
		@Pc(22) ObjStack objStack = (ObjStack) ObjStack.GROUND_ITEMS.get(key);
		if (objStack == null) {
			objStack = new ObjStack();
			ObjStack.GROUND_ITEMS.put(key, objStack);
			objStack.groundItems.addLast(objStackEntry);
			return;
		}
		@Pc(47) ItemType objType = TypeLists.OBJ.list(objStackEntry.id);
		@Pc(50) int value = objType.value;
		if (objType.stackable == 1) {
			value *= objStackEntry.count + 1;
		}
		for (@Pc(71) ObjStackEntry objStackEntry2 = (ObjStackEntry) objStack.groundItems.first(); objStackEntry2 != null; objStackEntry2 = (ObjStackEntry) objStack.groundItems.next()) {
			objType = TypeLists.OBJ.list(objStackEntry2.id);
			@Pc(82) int otherValue = objType.value;
			if (objType.stackable == 1) {
				otherValue *= objStackEntry2.count + 1;
			}
			if (otherValue < value) {
				Node.insertBefore(objStackEntry, objStackEntry2);
				return;
			}
		}
		objStack.groundItems.addLast(objStackEntry);
	}
}
