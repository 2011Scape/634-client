package com.jagex.game.runetek5

import com.jagex.game.runetek5.core.io.FileCache
import io.File
import io.RandomAccessFile
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

@OriginalClass("client!br")
class CacheFileLocator @OriginalMember(owner = "client!br", name = "<init>", descriptor = "()V") private constructor() {

	companion object {
		@OriginalMember(owner = "client!br", name = "a", descriptor = "(IILjava/lang/String;Ljava/lang/String;)Ljava/io/File;")
		@JvmStatic
		fun getCacheFile(@OriginalArg(1) cacheId: Int, @OriginalArg(2) string: String?, @OriginalArg(3) string2: String?): File {
			if (!FileCache.initialized) {
				throw RuntimeException("")
			}
			@Pc(11) val file: File? = FileCache.foundFiles.get(string) as File?
			if (file != null) {
				return file
			}
			@Pc(50) val strings: Array<String?> = arrayOf("c:/rscache/", "/rscache/", "c:/windows/", "c:/winnt/", "c:/", FileCache.homeDirectory, "/tmp/", "")
			@Pc(73) val strings2: Array<String?> = arrayOf(".jagex_cache_" + cacheId, ".file_store_" + cacheId)
			for (@Pc(75) i in 0 until 2) {
				for (@Pc(78) j in 0 until strings2.size) {
					for (@Pc(81) k in 0 until strings.size) {
						@Pc(112) val string3: String = strings[k] + strings2[j] + "/" + (if (string2 == null) "" else string2 + "/") + string
						@Pc(114) var randomAccessFile: RandomAccessFile? = null
						try {
							@Pc(119) val file2: File = File(string3)
							if (i != 0 || file2.exists()) {
								@Pc(128) val string4: String? = strings[k]
								if (i != 1 || string4!!.length <= 0 || (File(string4)).exists()) {
									(File(strings[k] + strings2[j])).mkdir()
									if (string2 != null) {
										(File(strings[k] + strings2[j] + "/" + string2)).mkdir()
									}
									randomAccessFile = RandomAccessFile(file2, "rw")
									@Pc(191) val probeByte: Int = randomAccessFile.read()
									randomAccessFile.seek(0L)
									randomAccessFile.write(probeByte)
									randomAccessFile.seek(0L)
									randomAccessFile.close()
									FileCache.foundFiles.put(string, file2)
									return file2
								}
							}
						} catch (@Pc(210) ex: Exception) {
							try {
								if (randomAccessFile != null) {
									randomAccessFile.close()
								}
							} catch (@Pc(218) ex2: Exception) {
							}
						}
					}
				}
			}
			throw RuntimeException()
		}

		@OriginalMember(owner = "client!br", name = "a", descriptor = "(BLjava/lang/String;I)V")
		@JvmStatic
		fun initialize(@OriginalArg(1) string: String?, @OriginalArg(2) cacheId: Int) {
			FileCache.cacheId = cacheId
			FileCache.game = string
			try {
				FileCache.homeDirectory = lang.getProperty("user.home")
				if (FileCache.homeDirectory != null) {
					FileCache.homeDirectory = FileCache.homeDirectory + "/"
				}
			} catch (@Pc(24) ex: Exception) {
			}
			FileCache.initialized = true
			if (FileCache.homeDirectory == null) {
				FileCache.homeDirectory = "~/"
			}
		}

		@OriginalMember(owner = "client!br", name = "a", descriptor = "(ILjava/lang/String;)Ljava/io/File;")
		@JvmStatic
		fun loadFromCacheDir(@OriginalArg(1) string: String?): File {
			return getCacheFile(FileCache.cacheId, string, FileCache.game)
		}
	}
}
