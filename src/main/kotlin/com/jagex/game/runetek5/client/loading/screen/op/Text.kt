package com.jagex.game.runetek5.client.loading.screen.op

import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.loginscreen.DrawableItem
import com.jagex.game.runetek5.client.loading.screen.op.instance.TextInstance
import com.jagex.game.runetek5.rendering.MessageBox
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.ClientState

@OriginalClass("client!kd")
class Text @OriginalMember(owner = "client!kd", name = "<init>", descriptor = "(Lclient!in;Lclient!in;Lclient!ot;)V") constructor(@OriginalArg(0) loadingSprites: Js5ResourceProvider?, @OriginalArg(1) fontMetrics: Js5ResourceProvider?, @OriginalArg(2) textInstance: TextInstance?) : DrawableItem {

	@OriginalMember(owner = "client!kd", name = "h", descriptor = "Lclient!da;")
	private var font: AbstractFontRenderer? = null

	@OriginalMember(owner = "client!kd", name = "j", descriptor = "Lclient!in;")
	private val fontMetrics: Js5ResourceProvider?

	@OriginalMember(owner = "client!kd", name = "e", descriptor = "Lclient!ot;")
	private val instance: TextInstance?

	@OriginalMember(owner = "client!kd", name = "i", descriptor = "Lclient!in;")
	private val loadingSprites: Js5ResourceProvider?

	init {
		this.fontMetrics = fontMetrics
		this.instance = textInstance
		this.loadingSprites = loadingSprites
	}

	@OriginalMember(owner = "client!kd", name = "a", descriptor = "(BZ)V")
	override fun execute() {
		@Pc(15) val x: Int = this.instance!!.horizontalAlignment!!.getX(GameShell.GAME_WIDTH, this.instance.verticalSpacing) + this.instance.x
		@Pc(31) val y: Int = this.instance.verticalAlignment!!.getY(MessageBox.paddingTop, this.instance.textVerticalAlignment) + this.instance.textHorizontalAlignment
		this.font!!.drawText(null, this.instance.y, this.instance.width, null, this.instance.height, this.instance.textColour, this.instance.textVerticalAlignment, null, y, 0, 0, this.instance.verticalSpacing, x, this.instance.shadowColour, this.instance.text)
	}

	@OriginalMember(owner = "client!kd", name = "a", descriptor = "(B)Z")
	override fun ready(): Boolean {
		@Pc(5) var ready: Boolean = true
		if (!this.loadingSprites!!.isFileReady(this.instance!!.font)) {
			ready = false
		}
		if (!this.fontMetrics!!.isFileReady(this.instance.font)) {
			ready = false
		}
		return ready
	}

	@OriginalMember(owner = "client!kd", name = "a", descriptor = "(I)V")
	override fun init() {
		@Pc(11) val metrics: FontMetrics? = FontMetrics.list(this.instance!!.font, this.fontMetrics)
		this.font = ClientState.activeToolkit!!.createFontRenderer(metrics, SpriteType.listGroup(this.loadingSprites, this.instance.font))
	}
}
