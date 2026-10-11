package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.cache.node.Node;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.core.constants.LocLayer;
import com.jagex.game.runetek5.entity.gameobject.GameObject;
import com.jagex.game.runetek5.scene.Scene;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!mk")
public final class ChangeLocationRequest extends Node {

	@OriginalMember(owner = "client!mk", name = "o", descriptor = "I")
	public int srcRotation;

	@OriginalMember(owner = "client!mk", name = "r", descriptor = "I")
	public int dstShape;

	@OriginalMember(owner = "client!mk", name = "s", descriptor = "I")
	public int y;

	@OriginalMember(owner = "client!mk", name = "v", descriptor = "I")
	public int plane;

	@OriginalMember(owner = "client!mk", name = "w", descriptor = "I")
	public int dstRotation;

	@OriginalMember(owner = "client!mk", name = "y", descriptor = "I")
	public int srcId;

	@OriginalMember(owner = "client!mk", name = "z", descriptor = "I")
	public int srcShape;

	@OriginalMember(owner = "client!mk", name = "A", descriptor = "I")
	public int layer;

	@OriginalMember(owner = "client!mk", name = "B", descriptor = "I")
	public int x;

	@OriginalMember(owner = "client!mk", name = "D", descriptor = "I")
	public int dstId;

	@OriginalMember(owner = "client!mk", name = "p", descriptor = "I")
	public int revertDelay = -1;

	@OriginalMember(owner = "client!mk", name = "q", descriptor = "I")
	public int spawnDelay = 0;
	@OriginalMember(owner = "client!qea", name = "a", descriptor = "(ILclient!mk;)V")
	public static void fillSourceLocation(@OriginalArg(1) ChangeLocationRequest changeLocationRequest) {
		if (Scene.aClass100ArrayArrayArray3 == null) {
			return;
		}
		@Pc(16) GameObject location = null;
		if (changeLocationRequest.layer == LocLayer.WALL) {
			location = (GameObject) Scene.getWall(changeLocationRequest.plane, changeLocationRequest.x, changeLocationRequest.y);
		}
		if (changeLocationRequest.layer == LocLayer.WALLDECOR) {
			location = (GameObject) Scene.getWallDecoration(changeLocationRequest.plane, changeLocationRequest.x, changeLocationRequest.y);
		}
		if (changeLocationRequest.layer == LocLayer.GROUND) {
			location = (GameObject) Scene.getInteractiveOfType(changeLocationRequest.plane, changeLocationRequest.x, changeLocationRequest.y, GameObject.class);
		}
		if (changeLocationRequest.layer == LocLayer.GROUNDDECOR) {
			location = (GameObject) Scene.getGroundDecoration(changeLocationRequest.plane, changeLocationRequest.x, changeLocationRequest.y);
		}
		if (location == null) {
			changeLocationRequest.srcRotation = 0;
			changeLocationRequest.srcId = -1;
			changeLocationRequest.srcShape = 0;
		} else {
			changeLocationRequest.srcId = location.getId();
			changeLocationRequest.srcShape = location.getShape();
			changeLocationRequest.srcRotation = location.getRotation();
		}
	}
}
