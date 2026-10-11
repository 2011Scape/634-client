package com.jagex.game.runetek5.client.loading.screen.op;

import com.jagex.game.runetek5.network.newsitem.NewsItem;
import com.jagex.game.runetek5.network.newsitem.NewsItemFetcher;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem;
import com.jagex.game.runetek5.config.gametip.component.OutlinedNewsItem;
import com.jagex.game.runetek5.rendering.MessageBox;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!ta")
public final class News implements DrawableItem {

	@OriginalMember(owner = "client!ta", name = "n", descriptor = "Lclient!nha;")
	private final OutlinedNewsItem instance;

	@OriginalMember(owner = "client!ta", name = "k", descriptor = "Lclient!eaa;")
	private final NewsItemFetcher reader;

	@OriginalMember(owner = "client!ta", name = "<init>", descriptor = "(Lclient!eaa;Lclient!nha;)V")
	public News(@OriginalArg(0) NewsItemFetcher newsReader, @OriginalArg(1) OutlinedNewsItem newsInstance) {
		this.instance = newsInstance;
		this.reader = newsReader;
	}

	@OriginalMember(owner = "client!ta", name = "a", descriptor = "(B)Z")
	@Override
	public boolean ready() {
		return this.reader.fetch();
	}

	@OriginalMember(owner = "client!ta", name = "a", descriptor = "(I)V")
	@Override
	public void init() {
	}

	@OriginalMember(owner = "client!ta", name = "a", descriptor = "(BZ)V")
	@Override
	public void execute() {
		@Pc(11) NewsItem item = this.reader.get(this.instance.item);
		if (item == null) {
			return;
		}
		@Pc(28) int x = this.instance.horizontalAlignment.getX(GameShell.GAME_WIDTH, this.instance.width) + this.instance.x;
		@Pc(45) int y = this.instance.verticalAlignment.getY(MessageBox.paddingTop, this.instance.height) + this.instance.y;
		if (this.instance.drawOutline) {
			ClientState.activeToolkit.outlineRect(x, y, this.instance.width, this.instance.height, this.instance.lineColour, 0);
		}
		y += this.drawWrappedText(item.line3, MiniMenu.minimenuHeader, x, y) * 12;
		y += 8;
		if (this.instance.drawOutline) {
			ClientState.activeToolkit.line(x, y, x + this.instance.width - 1, y, this.instance.lineColour, 0);
		}
		y++;
		y += this.drawWrappedText(item.line1, MiniMenu.minimenuHeader, x, y) * 12;
		y += 5;
		@Pc(127) int endY = y + this.drawWrappedText(item.line2, MiniMenu.minimenuHeader, x, y) * 12;
	}

	@OriginalMember(owner = "client!ta", name = "a", descriptor = "(ILjava/lang/String;ZLclient!da;II)I")
	private int drawWrappedText(@OriginalArg(1) String string, @OriginalArg(3) AbstractFontRenderer font, @OriginalArg(4) int x, @OriginalArg(5) int y) {
		return font.drawText(null, 0, this.instance.backgroundColor, null, this.instance.textColor, 0, this.instance.height - 10, null, y + 5, 0, 0, this.instance.width - 10, x + 5, 0, string);
	}
}
