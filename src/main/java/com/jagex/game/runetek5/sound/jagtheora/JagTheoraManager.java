package com.jagex.game.runetek5.sound.jagtheora;

import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.sound.jagtheora.OggKateDecoder;
import com.jagex.game.runetek5.sound.jagtheora.VorbisPcmStream;
import jagtheora.ogg.OggPacket;
import jagtheora.ogg.OggPage;
import jagtheora.ogg.OggStreamState;
import jagtheora.ogg.OggSyncState;
import java.io.IOException;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.NativeLibraryOwner;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!jfa")
public abstract class JagTheoraManager {

	@OriginalMember(owner = "client!jfa", name = "f", descriptor = "Z")
	private boolean hasBufferedPage;

	@OriginalMember(owner = "client!jfa", name = "n", descriptor = "Z")
	private boolean isEndOfStream;

	@OriginalMember(owner = "client!jfa", name = "v", descriptor = "Lclient!ts;")
	private OggKateDecoder kateDecoder;

	@OriginalMember(owner = "client!jfa", name = "x", descriptor = "Lclient!naa;")
	private JagTheoraTheoraNode theoraDecoder;

	@OriginalMember(owner = "client!jfa", name = "C", descriptor = "Z")
	private boolean isPaused;

	@OriginalMember(owner = "client!jfa", name = "D", descriptor = "Z")
	private boolean isShutdown;

	@OriginalMember(owner = "client!jfa", name = "E", descriptor = "Lclient!jm;")
	private JagTheoraVorbisNode vorbisDecoder;

	@OriginalMember(owner = "client!jfa", name = "F", descriptor = "Ljava/lang/String;")
	private String languageAbbreviation;

	@OriginalMember(owner = "client!jfa", name = "r", descriptor = "[B")
	private final byte[] buffer;

	@OriginalMember(owner = "client!jfa", name = "j", descriptor = "Lclient!jagtheora/ogg/OggSyncState;")
	private final OggSyncState syncState;

	@OriginalMember(owner = "client!jfa", name = "d", descriptor = "Lclient!jagtheora/ogg/OggPage;")
	private final OggPage page;

	@OriginalMember(owner = "client!jfa", name = "p", descriptor = "Lclient!jagtheora/ogg/OggPacket;")
	private final OggPacket packet;

	@OriginalMember(owner = "client!jfa", name = "H", descriptor = "Lclient!eq;")
	private final IterableHashTable cache;

	@OriginalMember(owner = "client!jfa", name = "<init>", descriptor = "(I)V")
	public JagTheoraManager(@OriginalArg(0) int bufferSize) {
		if (!NativeLibraryOwner.loadLibrary("jagtheora")) {
			throw new RuntimeException("Failed to load jagtheora library");
		}
		this.buffer = new byte[bufferSize];
		this.syncState = new OggSyncState();
		this.page = new OggPage();
		this.packet = new OggPacket();
		this.cache = new IterableHashTable(8);
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(I)V")
	private void tickSubtitles() {
		for (@Pc(17) OggStreamDecoder stream = (OggStreamDecoder) this.cache.first(); stream != null; stream = (OggStreamDecoder) this.cache.next()) {
			if (stream instanceof OggKateDecoder) {
				@Pc(25) OggKateDecoder kateStream = (OggKateDecoder) stream;
				while ((kateStream.packetCount <= 8 || this.getTime() > (double) kateStream.getEndTime()) && kateStream.streamState.packetOut(this.packet) == 1) {
					kateStream.processPacket(this.packet);
				}
			}
		}
		this.setLanguage(this.languageAbbreviation);
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(B)V")
	private void tickVideo() throws IOException {
		while (this.theoraDecoder.streamState.packetOut(this.packet) != 1) {
			@Pc(11) OggStreamDecoder stream = this.readNextPage();
			if (stream == null) {
				if (this.isEndOfStream) {
					this.handle();
				}
				return;
			}
			if (stream == this.kateDecoder) {
				this.tickSubtitles();
			}
		}
		this.theoraDecoder.processPacket(this.packet);
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(Z)Lclient!naa;")
	public final JagTheoraTheoraNode getTheoraDecoder() {
		return this.theoraDecoder;
	}

	@OriginalMember(owner = "client!jfa", name = "b", descriptor = "(Z)Z")
	private boolean isVideoBehind() {
		if (this.vorbisDecoder == null) {
			@Pc(36) double frameRate = (double) this.theoraDecoder.frameRate();
			return frameRate == 0.0D || (double) Utils.currentTimeMillis() >= 1000.0D / frameRate + (double) this.theoraDecoder.getLastDecodeTime();
		} else {
			return !this.theoraDecoder.isSetup() || this.getTime() > this.theoraDecoder.getTime();
		}
	}

	@OriginalMember(owner = "client!jfa", name = "b", descriptor = "(I)V")
	public final void stop() {
		if (this.isShutdown) {
			return;
		}
		for (@Pc(15) OggStreamDecoder stream = (OggStreamDecoder) this.cache.first(); stream != null; stream = (OggStreamDecoder) this.cache.next()) {
			stream.stop();
			stream.streamState.a();
		}
		this.packet.a();
		this.page.a();
		this.syncState.a();
		this.isShutdown = true;
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(ILjava/lang/String;)V")
	public final void setLanguage(@OriginalArg(1) String string) {
		this.languageAbbreviation = string;
		if (this.languageAbbreviation == null) {
			this.kateDecoder = null;
			return;
		}
		if (this.kateDecoder != null && !this.languageAbbreviation.equals(this.kateDecoder.getLanguage())) {
			this.kateDecoder = null;
		}
		if (this.kateDecoder != null) {
			return;
		}
		for (@Pc(43) OggStreamDecoder stream = (OggStreamDecoder) this.cache.first(); stream != null; stream = (OggStreamDecoder) this.cache.next()) {
			if (stream instanceof OggKateDecoder) {
				@Pc(51) OggKateDecoder kateStream = (OggKateDecoder) stream;
				if (this.languageAbbreviation.equals(kateStream.getLanguage())) {
					this.kateDecoder = kateStream;
					return;
				}
			}
		}
	}

	@OriginalMember(owner = "client!jfa", name = "b", descriptor = "(B)Z")
	public final boolean isPlaybackComplete() {
		if (this.isShutdown || this.isEndOfStream) {
			return this.vorbisDecoder == null || this.vorbisDecoder.getBufferedSampleCount() <= 0;
		} else {
			return false;
		}
	}

	@OriginalMember(owner = "client!jfa", name = "c", descriptor = "(I)V")
	private void handle() {
		for (@Pc(11) OggStreamDecoder stream = (OggStreamDecoder) this.cache.first(); stream != null; stream = (OggStreamDecoder) this.cache.next()) {
			if (stream != this.theoraDecoder) {
				while (stream.streamState.packetOut() == 1) {
					stream.processPacket(this.packet);
				}
			}
		}
		if (this.theoraDecoder == null) {
			return;
		}
		for (@Pc(53) int tick = 0; tick < 10; tick++) {
			if (!this.isVideoBehind()) {
				return;
			}
			if (this.theoraDecoder.streamState.packetOut() != 1) {
				this.stop();
				return;
			}
			this.theoraDecoder.processPacket(this.packet);
		}
	}

	@OriginalMember(owner = "client!jfa", name = "d", descriptor = "(I)Lclient!jm;")
	public final JagTheoraVorbisNode getVorbisDecoder() {
		return this.vorbisDecoder;
	}

	@OriginalMember(owner = "client!jfa", name = "e", descriptor = "(I)Lclient!ts;")
	public final OggKateDecoder getKateDecoder() {
		return this.kateDecoder;
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(BZ)V")
	public final void togglePause(@OriginalArg(1) boolean isMuted) {
		if (this.vorbisDecoder != null) {
			@Pc(11) VorbisPcmStream queueBuss = this.vorbisDecoder.getPcmStream();
			if (queueBuss != null) {
				queueBuss.setPaused(isMuted);
			}
		}
		this.isPaused = !this.isPaused;
	}

	@OriginalMember(owner = "client!jfa", name = "a", descriptor = "(B[B)I")
	public abstract int readPage(@OriginalArg(1) byte[] buffer) throws IOException;

	@OriginalMember(owner = "client!jfa", name = "f", descriptor = "(I)V")
	public final void tick() throws IOException {
		if (this.isPaused) {
			return;
		}
		while (!this.isShutdown) {
			@Pc(20) OggStreamDecoder stream;
			if (this.hasBufferedPage) {
				stream = (OggStreamDecoder) this.cache.get((long) this.page.getSerialNumber());
			} else {
				stream = this.readNextPage();
				if (stream == null) {
					if (this.isEndOfStream) {
						this.handle();
					}
					return;
				}
				if (stream == null) {
					throw new IllegalStateException();
				}
				this.hasBufferedPage = true;
			}
			if (this.vorbisDecoder == stream) {
				if (this.vorbisDecoder.getBufferedSampleCount() >= 50) {
					return;
				}
				while (this.vorbisDecoder.streamState.packetOut(this.packet) == 1) {
					this.vorbisDecoder.processPacket(this.packet);
					this.tickSubtitles();
					if (this.theoraDecoder != null) {
						@Pc(80) double time = this.theoraDecoder.getTime();
						for (@Pc(82) int tick = 0; tick < 10 && this.isVideoBehind(); tick++) {
							this.tickVideo();
							if (this.isShutdown) {
								return;
							}
						}
						if (this.theoraDecoder.getTime() > time) {
							return;
						}
					}
					if (this.vorbisDecoder.getBufferedSampleCount() >= 50) {
						return;
					}
				}
			} else if (stream instanceof OggKateDecoder) {
				this.tickSubtitles();
			} else if (stream != this.theoraDecoder) {
				while (stream.streamState.packetOut(this.packet) == 1) {
					if (stream.packetCount == 1 && stream instanceof OggKateDecoder) {
						this.setLanguage(this.languageAbbreviation);
					}
					stream.processPacket(this.packet);
				}
			} else if (this.vorbisDecoder == null && !this.isPaused) {
				for (@Pc(143) int i = 0; i < 10 && this.isVideoBehind(); i++) {
					this.tickVideo();
					if (this.isShutdown) {
						return;
					}
				}
				return;
			}
			this.hasBufferedPage = false;
		}
	}

	@OriginalMember(owner = "client!jfa", name = "g", descriptor = "(I)D")
	public final double getTime() {
		if (this.vorbisDecoder == null) {
			return this.theoraDecoder == null ? 0.0D : this.theoraDecoder.getTime();
		} else {
			return this.vorbisDecoder.getTime();
		}
	}

	@OriginalMember(owner = "client!jfa", name = "h", descriptor = "(I)Lclient!au;")
	private OggStreamDecoder readNextPage() throws IOException {
		if (this.isShutdown) {
			throw new IllegalStateException();
		} else if (this.isEndOfStream) {
			return null;
		} else {
			@Pc(23) int local23;
			while (this.syncState.pageOut(this.page) <= 0) {
				int bytesRead = this.readPage(this.buffer);
				if (bytesRead == -1) {
					this.isEndOfStream = true;
					return null;
				}
				if (bytesRead == 0) {
					return null;
				}
				if (!this.syncState.write(this.buffer, bytesRead)) {
					throw new RuntimeException("");
				}
			}
			int serialNumber = this.page.getSerialNumber();
			if (!this.page.isBOS()) {
				@Pc(79) OggStreamDecoder decoder = (OggStreamDecoder) this.cache.get((long) serialNumber);
				if (!decoder.streamState.pageIn(this.page)) {
					throw new IllegalStateException();
				}
				return decoder;
			}
			@Pc(96) OggStreamState oggStreamState = new OggStreamState(serialNumber);
			if (!oggStreamState.pageIn(this.page)) {
				throw new IllegalStateException();
			} else if (oggStreamState.packetPeek(this.packet) == 1) {
				@Pc(131) OggStreamDecoder newDecoder;
				if (this.theoraDecoder == null && this.packet.isTheora()) {
					this.theoraDecoder = new JagTheoraTheoraNode(oggStreamState);
					newDecoder = this.theoraDecoder;
				} else if (this.vorbisDecoder == null && this.packet.isVorbis()) {
					this.vorbisDecoder = new JagTheoraVorbisNode(oggStreamState);
					newDecoder = this.vorbisDecoder;
				} else {
					@Pc(155) byte[] packetData = this.packet.getData();
					@Pc(159) StringBuffer stringBuffer = new StringBuffer();
					for (@Pc(161) int i = 1; packetData.length > i && Character.isLetterOrDigit((char) packetData[i]); i++) {
						stringBuffer.append((char) packetData[i]);
					}
					@Pc(184) String string = stringBuffer.toString();
					if (string.equals("kate")) {
						newDecoder = new OggKateDecoder(oggStreamState);
					} else {
						newDecoder = new OggOtherStream(oggStreamState);
					}
				}
				this.cache.put((long) serialNumber, newDecoder);
				return newDecoder;
			} else {
				throw new IllegalStateException();
			}
		}
	}
	@OriginalMember(owner = "client!iea", name = "n", descriptor = "Z")
	public static boolean jagtheoraLoaded = false;
}
