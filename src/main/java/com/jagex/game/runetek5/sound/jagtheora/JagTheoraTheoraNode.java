package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.Toolkit;
import jagtheora.ogg.OggPacket;
import jagtheora.ogg.OggStreamState;
import jagtheora.theora.DecoderContext;
import jagtheora.theora.Frame;
import jagtheora.theora.GranulePos;
import jagtheora.theora.SetupInfo;
import jagtheora.theora.TheoraComment;
import jagtheora.theora.TheoraInfo;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!naa")
public final class JagTheoraTheoraNode extends OggStreamDecoder {

	@OriginalMember(owner = "client!naa", name = "x", descriptor = "Z")
	private boolean skipUntilKeyFrame;

	@OriginalMember(owner = "client!naa", name = "y", descriptor = "I")
	private int currentPostProcessingLevel;

	@OriginalMember(owner = "client!naa", name = "D", descriptor = "Z")
	private boolean frameDecoded;

	@OriginalMember(owner = "client!naa", name = "E", descriptor = "D")
	private double granuleTime;

	@OriginalMember(owner = "client!naa", name = "F", descriptor = "Z")
	private boolean headerDecoded;

	@OriginalMember(owner = "client!naa", name = "H", descriptor = "J")
	private long packetDecodeTime;

	@OriginalMember(owner = "client!naa", name = "I", descriptor = "Lclient!jagtheora/theora/GranulePos;")
	private GranulePos granulePos;

	@OriginalMember(owner = "client!naa", name = "J", descriptor = "Lclient!mi;")
	private NativeSprite frameSprite;

	@OriginalMember(owner = "client!naa", name = "N", descriptor = "Lclient!jagtheora/theora/Frame;")
	private Frame frame;

	@OriginalMember(owner = "client!naa", name = "Q", descriptor = "I")
	private int maxPostProcessingLevel;

	@OriginalMember(owner = "client!naa", name = "R", descriptor = "Z")
	private boolean keyFramesOnly;

	@OriginalMember(owner = "client!naa", name = "W", descriptor = "Lclient!jagtheora/theora/DecoderContext;")
	private DecoderContext decoderContext;

	@OriginalMember(owner = "client!naa", name = "Z", descriptor = "Lclient!jagtheora/theora/SetupInfo;")
	private final SetupInfo setupInfo = new SetupInfo();

	@OriginalMember(owner = "client!naa", name = "w", descriptor = "Lclient!jagtheora/theora/TheoraInfo;")
	private final TheoraInfo info = new TheoraInfo();

	@OriginalMember(owner = "client!naa", name = "z", descriptor = "Lclient!jagtheora/theora/TheoraComment;")
	private final TheoraComment comment = new TheoraComment();

	@OriginalMember(owner = "client!naa", name = "<init>", descriptor = "(Lclient!jagtheora/ogg/OggStreamState;)V")
	public JagTheoraTheoraNode(@OriginalArg(0) OggStreamState oggStreamState) {
		super(oggStreamState);
	}

	@OriginalMember(owner = "client!naa", name = "a", descriptor = "(Lclient!ha;I)Lclient!mi;")
	public NativeSprite getFrameSprite(@OriginalArg(0) Toolkit toolkit) {
		if (this.frame == null) {
			return null;
		} else if (this.frameDecoded || this.frameSprite == null) {
			this.frameSprite = toolkit.createNativeSprite(this.frame.pixels, this.frame.a, this.frame.a, this.frame.b, false);
			this.frameDecoded = false;
			return this.frameSprite;
		} else {
			return this.frameSprite;
		}
	}

	@OriginalMember(owner = "client!naa", name = "a", descriptor = "(B)V")
	@Override
	public void stop() {
		if (this.frame != null) {
			this.frame.a();
		}
		if (this.decoderContext != null) {
			this.decoderContext.a();
			this.decoderContext = null;
		}
		if (this.granulePos != null) {
			this.granulePos.a();
			this.granulePos = null;
		}
		this.info.a();
		this.comment.a();
		this.setupInfo.a();
	}

	@OriginalMember(owner = "client!naa", name = "d", descriptor = "(I)Z")
	public boolean isSetup() {
		return this.headerDecoded;
	}

	@OriginalMember(owner = "client!naa", name = "e", descriptor = "(I)F")
	public float frameRate() {
		return this.headerDecoded && !this.info.b() ? (float) this.info.fpsNumerator / (float) this.info.fpsDenominator : 0.0F;
	}

	@OriginalMember(owner = "client!naa", name = "a", descriptor = "(IB)V")
	private void setPostProcessingLevel(@OriginalArg(0) int level) {
		this.currentPostProcessingLevel = level;
		if (!this.headerDecoded) {
			return;
		}
		if (this.currentPostProcessingLevel > this.maxPostProcessingLevel) {
			this.currentPostProcessingLevel = this.maxPostProcessingLevel;
		}
		if (this.currentPostProcessingLevel < 0) {
			this.currentPostProcessingLevel = 0;
		}
		this.decoderContext.setPostProcessingLevel(this.currentPostProcessingLevel);
	}

	@OriginalMember(owner = "client!naa", name = "f", descriptor = "(I)J")
	public long getLastDecodeTime() {
		return this.packetDecodeTime;
	}

	@OriginalMember(owner = "client!naa", name = "b", descriptor = "(B)D")
	public double getTime() {
		return this.granuleTime;
	}

	@OriginalMember(owner = "client!naa", name = "a", descriptor = "(BLclient!jagtheora/ogg/OggPacket;)V")
	@Override
	public void decode(@OriginalArg(1) OggPacket oggPacket) {
		@Pc(21) int result;
		if (!this.headerDecoded) {
			result = this.setupInfo.decodeHeader(this.info, this.comment, oggPacket);
			if (result == 0) {
				this.headerDecoded = true;
				if (this.info.frameWidth > 2048 || this.info.frameHeight > 1024) {
					throw new IllegalStateException();
				}
				this.decoderContext = new DecoderContext(this.info, this.setupInfo);
				this.granulePos = new GranulePos();
				this.frame = new Frame(this.info.frameWidth, this.info.frameHeight);
				this.maxPostProcessingLevel = this.decoderContext.getMaxPostProcessingLevel();
				this.setPostProcessingLevel(this.currentPostProcessingLevel);
			} else if (result < 0) {
				throw new IllegalStateException(String.valueOf(result));
			}
			return;
		}
		this.packetDecodeTime = Utils.currentTimeMillis();
		result = this.decoderContext.decodePacketIn(oggPacket, this.granulePos);
		if (result < 0) {
			throw new IllegalStateException(String.valueOf(result));
		}
		this.decoderContext.granuleFrame(this.granulePos);
		this.granuleTime = this.decoderContext.granuleTime(this.granulePos);
		if (this.skipUntilKeyFrame) {
			@Pc(53) boolean keyFrame = oggPacket.isKeyFrame() == 1;
			if (!keyFrame) {
				return;
			}
			this.skipUntilKeyFrame = false;
		}
		if (!this.keyFramesOnly || oggPacket.isKeyFrame() == 1) {
			if (this.decoderContext.decodeFrame(this.frame) != 0) {
				throw new IllegalStateException(String.valueOf(result));
			}
			this.frameDecoded = true;
		}
	}
}
