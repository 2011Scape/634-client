package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.core.io.FileCache;
import java.io.File;
import java.io.RandomAccessFile;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!br")
public final class CacheFileLocator {

	@OriginalMember(owner = "client!br", name = "<init>", descriptor = "()V")
	private CacheFileLocator() {
	}

	@OriginalMember(owner = "client!br", name = "a", descriptor = "(IILjava/lang/String;Ljava/lang/String;)Ljava/io/File;")
	public static File getCacheFile(@OriginalArg(1) int cacheId, @OriginalArg(2) String string, @OriginalArg(3) String string2) {
		if (!FileCache.initialized) {
			throw new RuntimeException("");
		}
		@Pc(11) File file = (File) FileCache.foundFiles.get(string);
		if (file != null) {
			return file;
		}
		@Pc(50) String[] strings = new String[] { "c:/rscache/", "/rscache/", "c:/windows/", "c:/winnt/", "c:/", FileCache.homeDirectory, "/tmp/", "" };
		@Pc(73) String[] strings2 = new String[] { ".jagex_cache_" + cacheId, ".file_store_" + cacheId };
		for (@Pc(75) int i = 0; i < 2; i++) {
			for (@Pc(78) int j = 0; j < strings2.length; j++) {
				for (@Pc(81) int k = 0; k < strings.length; k++) {
					@Pc(112) String string3 = strings[k] + strings2[j] + "/" + (string2 == null ? "" : string2 + "/") + string;
					@Pc(114) RandomAccessFile randomAccessFile = null;
					try {
						@Pc(119) File file2 = new File(string3);
						if (i != 0 || file2.exists()) {
							@Pc(128) String string4 = strings[k];
							if (i != 1 || string4.length() <= 0 || (new File(string4)).exists()) {
								(new File(strings[k] + strings2[j])).mkdir();
								if (string2 != null) {
									(new File(strings[k] + strings2[j] + "/" + string2)).mkdir();
								}
								randomAccessFile = new RandomAccessFile(file2, "rw");
								@Pc(191) int probeByte = randomAccessFile.read();
								randomAccessFile.seek(0L);
								randomAccessFile.write(probeByte);
								randomAccessFile.seek(0L);
								randomAccessFile.close();
								FileCache.foundFiles.put(string, file2);
								return file2;
							}
						}
					} catch (@Pc(210) Exception ex) {
						try {
							if (randomAccessFile != null) {
								randomAccessFile.close();
							}
						} catch (@Pc(218) Exception ex2) {
						}
					}
				}
			}
		}
		throw new RuntimeException();
	}

	@OriginalMember(owner = "client!br", name = "a", descriptor = "(BLjava/lang/String;I)V")
	public static void initialize(@OriginalArg(1) String string, @OriginalArg(2) int cacheId) {
		FileCache.cacheId = cacheId;
		FileCache.game = string;
		try {
			FileCache.homeDirectory = System.getProperty("user.home");
			if (FileCache.homeDirectory != null) {
				FileCache.homeDirectory = FileCache.homeDirectory + "/";
			}
		} catch (@Pc(24) Exception ex) {
		}
		FileCache.initialized = true;
		if (FileCache.homeDirectory == null) {
			FileCache.homeDirectory = "~/";
		}
	}

	@OriginalMember(owner = "client!br", name = "a", descriptor = "(ILjava/lang/String;)Ljava/io/File;")
	public static File loadFromCacheDir(@OriginalArg(1) String string) {
		return getCacheFile(FileCache.cacheId, string, FileCache.game);
	}
}
