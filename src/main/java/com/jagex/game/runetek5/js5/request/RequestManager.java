package com.jagex.game.runetek5.js5.request;

import com.jagex.game.runetek5.buffer.Packet;
import java.math.BigInteger;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.Decompressor;
import com.jagex.game.runetek5.js5.request.local.LocalRequester;
import com.jagex.game.runetek5.js5.request.remote.Js5NetQueueRequest;
import com.jagex.game.runetek5.js5.request.remote.RemoteFileWorker;
import com.jagex.game.runetek5.js5.Js5WorkerThread;
import com.jagex.game.runetek5.js5.Whirlpool;

@OriginalClass("client!eb")
public final class RequestManager {

	@OriginalMember(owner = "client!eb", name = "d", descriptor = "Lclient!cea;")
	private Packet masterIndexBuffer;

	@OriginalMember(owner = "client!eb", name = "e", descriptor = "[Lclient!bja;")
	private RemoteFileWorker[] remoteFileWorkers;

	@OriginalMember(owner = "client!eb", name = "f", descriptor = "Lclient!aca;")
	private final LocalRequester localRequester;

	@OriginalMember(owner = "client!eb", name = "g", descriptor = "Ljava/math/BigInteger;")
	private final BigInteger updateServerModulus;

	@OriginalMember(owner = "client!eb", name = "b", descriptor = "Ljava/math/BigInteger;")
	private final BigInteger updateServerExponent;

	@OriginalMember(owner = "client!eb", name = "a", descriptor = "Lclient!tha;")
	private final Js5WorkerThread remoteRequester;

	@OriginalMember(owner = "client!eb", name = "i", descriptor = "Lclient!mba;")
	private Js5NetQueueRequest masterIndexRequest;

	@OriginalMember(owner = "client!eb", name = "<init>", descriptor = "(Lclient!tha;Lclient!aca;Ljava/math/BigInteger;Ljava/math/BigInteger;)V")
	public RequestManager(@OriginalArg(0) Js5WorkerThread js5WorkerThread, @OriginalArg(1) LocalRequester cachedResourceWorker, @OriginalArg(2) BigInteger bigInteger, @OriginalArg(3) BigInteger bigInteger2) {
		this.localRequester = cachedResourceWorker;
		this.updateServerModulus = bigInteger2;
		this.updateServerExponent = bigInteger;
		this.remoteRequester = js5WorkerThread;
		if (!this.remoteRequester.isUrgentRequestQueueFull()) {
			this.masterIndexRequest = this.remoteRequester.request((byte) 0, true, 255, 255);
		}
	}

	@OriginalMember(owner = "client!eb", name = "a", descriptor = "(I)V")
	public void pulse() {
		if (this.remoteFileWorkers == null) {
			return;
		}
		for (@Pc(11) int i = 0; i < this.remoteFileWorkers.length; i++) {
			if (this.remoteFileWorkers[i] != null) {
				this.remoteFileWorkers[i].processPrefetchQueue();
			}
		}
		for (@Pc(36) int j = 0; j < this.remoteFileWorkers.length; j++) {
			if (this.remoteFileWorkers[j] != null) {
				this.remoteFileWorkers[j].loop();
			}
		}
	}

	@OriginalMember(owner = "client!eb", name = "a", descriptor = "(Lclient!nw;ILclient!nw;I)Lclient!bja;")
	public RemoteFileWorker getWorker(@OriginalArg(0) Decompressor fileSystem_Client, @OriginalArg(1) int archive, @OriginalArg(2) Decompressor fileSystem_Client2) {
		return this.getRemoteFileWorker(archive, fileSystem_Client, fileSystem_Client2);
	}

	@OriginalMember(owner = "client!eb", name = "c", descriptor = "(I)Z")
	public boolean initialize() {
		if (this.masterIndexBuffer != null) {
			return true;
		}
		if (this.masterIndexRequest == null) {
			if (this.remoteRequester.isUrgentRequestQueueFull()) {
				return false;
			}
			this.masterIndexRequest = this.remoteRequester.request((byte) 0, true, 255, 255);
		}
		if (this.masterIndexRequest.waiting) {
			return false;
		}
		@Pc(46) Packet packet = new Packet(this.masterIndexRequest.getData());
		packet.currentPosition = 5;
		@Pc(55) int archiveCount = packet.readUByte();
		packet.currentPosition += archiveCount * 72;
		@Pc(71) byte[] rsaEncrypted = new byte[packet.buffer.length - packet.currentPosition];
		packet.readBytes(0, rsaEncrypted.length, rsaEncrypted);
		@Pc(105) byte[] rsaDecrypted;
		if (this.updateServerExponent == null || this.updateServerModulus == null) {
			rsaDecrypted = rsaEncrypted;
		} else {
			@Pc(95) BigInteger bigInteger = new BigInteger(rsaEncrypted);
			@Pc(102) BigInteger bigInteger2 = bigInteger.modPow(this.updateServerExponent, this.updateServerModulus);
			rsaDecrypted = bigInteger2.toByteArray();
		}
		if (rsaDecrypted.length != 65) {
			throw new RuntimeException();
		}
		@Pc(132) byte[] digest = Whirlpool.digest(5, packet.buffer, packet.currentPosition - rsaEncrypted.length - 5);
		for (@Pc(134) int i = 0; i < 64; i++) {
			if (rsaDecrypted[i + 1] != digest[i]) {
				throw new RuntimeException();
			}
		}
		this.masterIndexBuffer = packet;
		this.remoteFileWorkers = new RemoteFileWorker[archiveCount];
		return true;
	}

	@OriginalMember(owner = "client!eb", name = "a", descriptor = "(BZILclient!nw;Lclient!nw;)Lclient!bja;")
	private RemoteFileWorker getRemoteFileWorker(@OriginalArg(2) int archive, @OriginalArg(3) Decompressor fileSystem_Client, @OriginalArg(4) Decompressor fileSystem_Client2) {
		if (this.masterIndexBuffer == null) {
			throw new RuntimeException();
		} else if (archive < 0 || archive >= this.remoteFileWorkers.length) {
			throw new RuntimeException();
		} else if (this.remoteFileWorkers[archive] == null) {
			this.masterIndexBuffer.currentPosition = archive * 72 + 6;
			@Pc(47) int tableCrc = this.masterIndexBuffer.readInt();
			@Pc(52) int tableRevision = this.masterIndexBuffer.readInt();
			@Pc(55) byte[] tableDigest = new byte[64];
			this.masterIndexBuffer.readBytes(0, 64, tableDigest);
			@Pc(82) RemoteFileWorker js5ResourceProvider = new RemoteFileWorker(archive, fileSystem_Client, fileSystem_Client2, this.remoteRequester, this.localRequester, tableCrc, tableDigest, tableRevision, true);
			this.remoteFileWorkers[archive] = js5ResourceProvider;
			return js5ResourceProvider;
		} else {
			return this.remoteFileWorkers[archive];
		}
	}
	@OriginalMember(owner = "client!pq", name = "r", descriptor = "Lclient!eb;")
	public static RequestManager requestManager;
	@OriginalMember(owner = "client!et", name = "H", descriptor = "Ljava/math/BigInteger;")
	public static BigInteger js5Modulus = new BigInteger("d6808be939bbfd2ec4e96b1581ce3e1144b526e7643a72e3c64fbb902724fbfcf14ab601da6d6f8dbb57d1c369d080d9fc392abeb7886e0076d07f2aea5810e540d2817fd1967e35b39cc95cf7c9170b5fb55f5bf95524b60e938f0d64614bc365b87d66963a8cc8664e32875366099ef297180d01c7c3842162865e11d92299", 16);
	@OriginalMember(owner = "client!jca", name = "c", descriptor = "Ljava/math/BigInteger;")
	public static BigInteger js5RsaExponent = new BigInteger("10001", 16);
}
