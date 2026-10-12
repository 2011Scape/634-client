package com.jagex.game.runetek5.client.loading.screen.op

import com.jagex.game.runetek5.network.newsitem.NewsItem
import com.jagex.game.runetek5.network.newsitem.NewsItemFetcher
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem
import com.jagex.game.runetek5.config.gametip.component.OutlinedNewsItem
import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.ClientState

@OriginalClass("client!ta")
class News @OriginalMember(owner = "client!ta", name = "<init>", descriptor = "(Lclient!eaa;Lclient!nha;)V") constructor(@OriginalArg(0) newsReader: NewsItemFetcher?, @OriginalArg(1) newsInstance: OutlinedNewsItem?) : DrawableItem {

	@OriginalMember(owner = "client!ta", name = "n", descriptor = "Lclient!nha;")
	private val instance: OutlinedNewsItem?

	@OriginalMember(owner = "client!ta", name = "k", descriptor = "Lclient!eaa;")
	private val reader: NewsItemFetcher?

	init {
		this.instance = newsInstance
		this.reader = newsReader
	}

	@OriginalMember(owner = "client!ta", name = "a", descriptor = "(B)Z")
	override fun ready(): Boolean {
		return this.reader!!.fetch()
	}

	@OriginalMember(owner = "client!ta", name = "a", descriptor = "(I)V")
	override fun init() {
	}

	@OriginalMember(owner = "client!ta", name = "a", descriptor = "(BZ)V")
	override fun execute() {
		@Pc(11) val item: NewsItem? = this.reader!!.get(this.instance!!.item)
		if (item == null) {
			return
		}
		@Pc(28) var x: Int = this.instance.horizontalAlignment!!.getX(GameShell.GAME_WIDTH, this.instance.width) + this.instance.x
		@Pc(45) var y: Int = this.instance.verticalAlignment!!.getY(MessageBox.paddingTop, this.instance.height) + this.instance.y
		if (this.instance.drawOutline) {
			ClientState.activeToolkit!!.outlineRect(x, y, this.instance.width, this.instance.height, this.instance.lineColour, 0)
		}
		y += this.drawWrappedText(item.line3, MiniMenu.minimenuHeader, x, y) * 12
		y += 8
		if (this.instance.drawOutline) {
			ClientState.activeToolkit!!.line(x, y, x + this.instance.width - 1, y, this.instance.lineColour, 0)
		}
		y++
		y += this.drawWrappedText(item.line1, MiniMenu.minimenuHeader, x, y) * 12
		y += 5
		@Pc(127) val endY: Int = y + this.drawWrappedText(item.line2, MiniMenu.minimenuHeader, x, y) * 12
	}

	@OriginalMember(owner = "client!ta", name = "a", descriptor = "(ILjava/lang/String;ZLclient!da;II)I")
	private fun drawWrappedText(@OriginalArg(1) string: String?, @OriginalArg(3) font: AbstractFontRenderer?, @OriginalArg(4) x: Int, @OriginalArg(5) y: Int): Int {
		return font!!.drawText(null, 0, this.instance!!.backgroundColor, null, this.instance.textColor, 0, this.instance.height - 10, null, y + 5, 0, 0, this.instance.width - 10, x + 5, 0, string)
	}
}
