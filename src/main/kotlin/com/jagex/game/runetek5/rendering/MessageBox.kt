package com.jagex.game.runetek5.rendering

import com.jagex.game.runetek5.graphics.FlipException
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer
import com.jagex.game.runetek5.graphics.font.FontMetrics
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.js5.MessageBoxBackgroundSprite
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.InterfaceRectangles
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.graphics.sprite.SpriteType
import com.jagex.game.runetek5.load.LoadingScreenAlignmentX
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.load.LoadingScreenAlignmentY
import com.jagex.game.runetek5.config.Resource
import com.jagex.game.runetek5.graphics.SpriteDataProvider

object MessageBox {

	@OriginalMember(owner = "client!fg", name = "a", descriptor = "(ZLclient!da;Lclient!oea;Ljava/lang/String;ILclient!ha;)V")
	@JvmStatic
	fun draw(@OriginalArg(0) renderFrame: Boolean, @OriginalArg(1) font: AbstractFontRenderer?, @OriginalArg(2) fontMetrics: FontMetrics?, @OriginalArg(3) string: String?, @OriginalArg(5) toolkit: Toolkit?) {
		@Pc(17) val ready: Boolean = !MessageBox.hasCustomStyle || MessageBox.loadAssets()
		if (!ready) {
			return
		}
		@Pc(39) var boxWidth: Int
		@Pc(50) var boxHeight: Int
		if (MessageBox.hasCustomStyle && ready) {
			@Pc(26) val fontMetrics2: FontMetrics? = MessageBox.aClass239_17
			@Pc(32) val font2: AbstractFontRenderer? = toolkit!!.createFontRenderer(fontMetrics2, MessageBox.aClass296Array1)
			boxWidth = fontMetrics2!!.getTextWidthMultiline(string, 250, null)
			boxHeight = fontMetrics2.getTextHeightMultiline(null, fontMetrics2.verticalSpacing, 250, string)
			@Pc(53) val borderWidth: Int = MessageBox.aClass296_3!!.width
			@Pc(57) val textInset: Int = borderWidth + 4
			boxHeight += textInset * 2
			boxWidth += textInset * 2
			if (MessageBox.minHeight > boxHeight) {
				boxHeight = MessageBox.minHeight
			}
			if (boxWidth < MessageBox.minWidth) {
				boxWidth = MessageBox.minWidth
			}
			@Pc(92) var drawX: Int = MessageBox.loadingScreenAlignmentX!!.getX(GameShell.GAME_WIDTH, boxWidth) + MessageBox.offsetX
			@Pc(100) var drawY: Int = MessageBox.loadingScreenAlignmentY!!.getY(MessageBox.paddingTop, boxHeight) + MessageBox.offsetY
			if (OrthoMode.orthoEnabled) {
				drawX += OrthoMode.getOrthoOffsetX()
				drawY += OrthoMode.getOrthoDrawOffsetY()
			}
			toolkit.generateSprite(MessageBoxBackgroundSprite.aClass296_1, false).drawScaledColored(MessageBox.aClass296_2!!.width + drawX, MessageBox.aClass296_2!!.height + drawY, boxWidth - MessageBox.aClass296_2!!.width * 2, -(MessageBox.aClass296_2!!.height * 2) + boxHeight, 1, 0, 0)
			toolkit.generateSprite(MessageBox.aClass296_2, true).render(drawX, drawY)
			MessageBox.aClass296_2!!.flipHorizontal()
			toolkit.generateSprite(MessageBox.aClass296_2, true).render(drawX + boxWidth - borderWidth, drawY)
			MessageBox.aClass296_2!!.flipVertical()
			toolkit.generateSprite(MessageBox.aClass296_2, true).render(drawX + boxWidth - borderWidth, -borderWidth + (drawY - -boxHeight))
			MessageBox.aClass296_2!!.flipHorizontal()
			toolkit.generateSprite(MessageBox.aClass296_2, true).render(drawX, drawY + boxHeight - borderWidth)
			MessageBox.aClass296_2!!.flipVertical()
			toolkit.generateSprite(MessageBox.aClass296_3, true).drawTiled(drawX, MessageBox.aClass296_2!!.height + drawY, borderWidth, boxHeight - MessageBox.aClass296_2!!.height * 2)
			MessageBox.aClass296_3!!.rotate90DegLeft()
			toolkit.generateSprite(MessageBox.aClass296_3, true).drawTiled(MessageBox.aClass296_2!!.width + drawX, drawY, boxWidth - MessageBox.aClass296_2!!.width * 2, borderWidth)
			MessageBox.aClass296_3!!.rotate90DegLeft()
			toolkit.generateSprite(MessageBox.aClass296_3, true).drawTiled(drawX + boxWidth - borderWidth, MessageBox.aClass296_2!!.height + drawY, borderWidth, boxHeight - MessageBox.aClass296_2!!.height * 2)
			MessageBox.aClass296_3!!.rotate90DegLeft()
			toolkit.generateSprite(MessageBox.aClass296_3, true).drawTiled(MessageBox.aClass296_2!!.width + drawX, -borderWidth + drawY - -boxHeight, boxWidth - MessageBox.aClass296_2!!.width * 2, borderWidth)
			MessageBox.aClass296_3!!.rotate90DegLeft()
			font2!!.drawText(null, 0, -1, null, MessageBox.textColour or 0xFF000000.toInt(), 1, boxHeight - textInset * 2, null, drawY + textInset, 0, 0, boxWidth - textInset * 2, drawX + textInset, 1, string)
			InterfaceRectangles.inboundsComponentToTop(drawX, boxWidth, drawY, boxHeight)
		} else {
			boxWidth = fontMetrics!!.getTextWidthMultiline(string, 250, null)
			boxHeight = fontMetrics.getLineCount(null, string, 250) * 13
			toolkit!!.aa(6, 6, boxWidth + 4 + 4, boxHeight + 8, -16777216, 0)
			toolkit.outlineRect(6, 6, boxWidth + 4 + 4, boxHeight + 8, -1, 0)
			font!!.drawText(null, 0, -1, null, -1, 1, boxHeight, null, 10, 0, 0, boxWidth, 10, 1, string)
			InterfaceRectangles.inboundsComponentToTop(6, boxWidth + 4 + 4, 6, boxHeight + 4 + 4)
		}
		if (!renderFrame) {
			return
		}
		try {
			if (OrthoMode.orthoEnabled) {
				OrthoMode.flipOrtho()
			} else {
				toolkit.renderFrame()
			}
		} catch (@Pc(464) ex: FlipException) {
		}
	}
	@OriginalMember(owner = "client!oga", name = "a", descriptor = "I")
	@JvmField
	var queryResultsCursor: Int = 0

	@OriginalMember(owner = "client!qo", name = "td", descriptor = "I")
	@JvmField
	var paddingTop: Int = 503
	@OriginalMember(owner = "client!wc", name = "l", descriptor = "I")
	@JvmField
	var backgroundSpriteId: Int = 0

	@OriginalMember(owner = "client!vea", name = "f", descriptor = "Lclient!oea;")
	@JvmField
	var aClass239_17: FontMetrics? = null

	@OriginalMember(owner = "client!lea", name = "c", descriptor = "I")
	@JvmField
	var textColour: Int = 0

	@OriginalMember(owner = "client!vs", name = "i", descriptor = "I")
	@JvmField
	var minWidth: Int = 0

	@OriginalMember(owner = "client!ww", name = "J", descriptor = "[Lclient!rh;")
	@JvmField
	var aClass296Array1: Array<SpriteType?>? = null

	@OriginalMember(owner = "client!hia", name = "b", descriptor = "I")
	@JvmField
	var fontId: Int = 0

	@OriginalMember(owner = "client!maa", name = "d", descriptor = "Lclient!sd;")
	@JvmField
	var loadingScreenAlignmentX: LoadingScreenAlignmentX? = null

	@OriginalMember(owner = "client!wc", name = "m", descriptor = "Z")
	@JvmField
	var hasCustomStyle: Boolean = false
	@OriginalMember(owner = "client!qw", name = "z", descriptor = "I")
	@JvmField
	var edgeSpriteId: Int = 0

	@OriginalMember(owner = "client!sc", name = "g", descriptor = "I")
	@JvmField
	var cornerSpriteId: Int = 0

	@OriginalMember(owner = "client!uu", name = "a", descriptor = "(IILclient!sd;Lclient!ec;IIIBIIII)V")
	@JvmStatic
	fun setupMessageBox(@OriginalArg(0) textColour: Int, @OriginalArg(1) minHeight: Int, @OriginalArg(2) loadingScreenAlignmentX: LoadingScreenAlignmentX?, @OriginalArg(3) loadingScreenAlignmentY: LoadingScreenAlignmentY?, @OriginalArg(4) edgeSpriteId: Int, @OriginalArg(5) cornerSpriteId: Int, @OriginalArg(6) minWidth: Int, @OriginalArg(8) fontId: Int, @OriginalArg(9) offsetX: Int, @OriginalArg(10) offsetY: Int, @OriginalArg(11) backgroundSpriteId: Int) {
		MessageBox.loadingScreenAlignmentX = loadingScreenAlignmentX
		MessageBox.offsetY = offsetY
		MessageBox.loadingScreenAlignmentY = loadingScreenAlignmentY
		MessageBox.textColour = textColour
		MessageBox.aClass296_3 = null
		MessageBox.backgroundSpriteId = backgroundSpriteId
		MessageBox.aClass296_2 = null
		MessageBox.minWidth = minWidth
		MessageBox.fontId = fontId
		MessageBox.minHeight = minHeight
		MessageBox.cornerSpriteId = cornerSpriteId
		MessageBox.offsetX = offsetX
		MessageBoxBackgroundSprite.aClass296_1 = null
		MessageBox.edgeSpriteId = edgeSpriteId
		MessageBox.loadAssets()
		MessageBox.hasCustomStyle = true
	}

	@OriginalMember(owner = "client!gda", name = "q", descriptor = "Lclient!rh;")
	@JvmField
	var aClass296_3: SpriteType? = null

	@OriginalMember(owner = "client!cd", name = "g", descriptor = "I")
	@JvmField
	var offsetX: Int = 0

	@OriginalMember(owner = "client!ur", name = "b", descriptor = "I")
	@JvmField
	var minHeight: Int = 0
	@OriginalMember(owner = "client!fo", name = "p", descriptor = "Lclient!ec;")
	@JvmField
	var loadingScreenAlignmentY: LoadingScreenAlignmentY? = null

	@OriginalMember(owner = "client!aia", name = "O", descriptor = "Lclient!rh;")
	@JvmField
	var aClass296_2: SpriteType? = null

	@OriginalMember(owner = "client!f", name = "a", descriptor = "(Z)Z")
	@JvmStatic
	fun loadAssets(): Boolean {
		@Pc(7) var allLoaded: Boolean = true
		if (MessageBox.aClass296_2 == null) {
			if (Resource.SPRITES!!.isFileReady(MessageBox.cornerSpriteId)) {
				MessageBox.aClass296_2 = SpriteDataProvider.listSingle(Resource.SPRITES, MessageBox.cornerSpriteId)
			} else {
				allLoaded = false
			}
		}
		if (MessageBox.aClass296_3 == null) {
			if (Resource.SPRITES!!.isFileReady(MessageBox.edgeSpriteId)) {
				MessageBox.aClass296_3 = SpriteDataProvider.listSingle(Resource.SPRITES, MessageBox.edgeSpriteId)
			} else {
				allLoaded = false
			}
		}
		if (MessageBoxBackgroundSprite.aClass296_1 == null) {
			if (Resource.SPRITES!!.isFileReady(MessageBox.backgroundSpriteId)) {
				MessageBoxBackgroundSprite.aClass296_1 = SpriteDataProvider.listSingle(Resource.SPRITES, MessageBox.backgroundSpriteId)
			} else {
				allLoaded = false
			}
		}
		if (MessageBox.aClass239_17 == null) {
			if (Resource.FONT_METRICS!!.isFileReady(MessageBox.fontId)) {
				MessageBox.aClass239_17 = FontMetrics.list(MessageBox.fontId, Resource.FONT_METRICS)
			} else {
				allLoaded = false
			}
		}
		if (MessageBox.aClass296Array1 == null) {
			if (Resource.SPRITES!!.isFileReady(MessageBox.fontId)) {
				MessageBox.aClass296Array1 = SpriteType.listGroup(Resource.SPRITES, MessageBox.fontId)
			} else {
				allLoaded = false
			}
		}
		return allLoaded
	}

	@OriginalMember(owner = "client!jaa", name = "B", descriptor = "I")
	@JvmField
	var offsetY: Int = 0
}
