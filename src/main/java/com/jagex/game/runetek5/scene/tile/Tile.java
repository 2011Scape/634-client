package com.jagex.game.runetek5.scene.tile;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.entity.GroundDecor;
import com.jagex.game.runetek5.entity.InteractiveEntityContainer;
import com.jagex.game.runetek5.entity.Wall;
import com.jagex.game.runetek5.entity.WallDecor;
import com.jagex.game.runetek5.entity.grounditem.AbstractGroundItem;

@OriginalClass("client!es")
public final class Tile {

	@OriginalMember(owner = "client!es", name = "d", descriptor = "Lclient!un;")
	public WallDecor primaryWallDecoration;

	@OriginalMember(owner = "client!es", name = "e", descriptor = "Lclient!oga;")
	public InteractiveEntityContainer interactives;

	@OriginalMember(owner = "client!es", name = "f", descriptor = "S")
	public short cullOffsetZ;

	@OriginalMember(owner = "client!es", name = "g", descriptor = "S")
	public short cullHeightZ;

	@OriginalMember(owner = "client!es", name = "i", descriptor = "Lclient!es;")
	public Tile bridge;

	@OriginalMember(owner = "client!es", name = "j", descriptor = "S")
	public short cullOffsetX;

	@OriginalMember(owner = "client!es", name = "k", descriptor = "Lclient!aba;")
	public GroundDecor groundDecoration;

	@OriginalMember(owner = "client!es", name = "l", descriptor = "Lclient!qfa;")
	public Wall secondaryWall;

	@OriginalMember(owner = "client!es", name = "o", descriptor = "Lclient!qfa;")
	public Wall primaryWall;

	@OriginalMember(owner = "client!es", name = "p", descriptor = "Lclient!un;")
	public WallDecor secondaryWallDecoration;

	@OriginalMember(owner = "client!es", name = "q", descriptor = "Lclient!fha;")
	public AbstractGroundItem groundItem;

	@OriginalMember(owner = "client!es", name = "r", descriptor = "S")
	public short cullHeightX;

	@OriginalMember(owner = "client!es", name = "h", descriptor = "B")
	public byte plane;

	@OriginalMember(owner = "client!es", name = "<init>", descriptor = "(I)V")
	public Tile(@OriginalArg(0) int plane) {
		this.plane = (byte) plane;
	}

	@OriginalMember(owner = "client!es", name = "a", descriptor = "(Z)V")
	public void clearInteractives() {
		while (this.interactives != null) {
			@Pc(7) InteractiveEntityContainer positionEntityNode = this.interactives.child;
			this.interactives.release();
			this.interactives = positionEntityNode;
		}
	}
}
