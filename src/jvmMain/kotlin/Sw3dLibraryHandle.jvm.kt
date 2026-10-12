import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.Vector
import org.openrs2.deob.annotation.Pc

/*
 * Not part of the original client: the part of client.getDebugString (the original's) that reads the
 * native handle of the loaded sw3d.dll through reflection into the JVM's class loader (milestone 3).
 * Returns `string` with "|16)<handle>" appended when it finds the library.
 */
actual fun sw3dLibraryHandleInfo(string: String): String {
	var string: String = string
	@Pc(246) val class2: Class<*> = Class.forName("java.lang.ClassLoader")
	@Pc(250) val field: Field = class2.getDeclaredField("nativeLibraries")
	@Pc(253) val class3: Class<*> = Class.forName("java.lang.reflect.AccessibleObject")
	@Pc(263) val method: Method = class3.getDeclaredMethod("setAccessible", java.lang.Boolean.TYPE)
	method.invoke(field, java.lang.Boolean.TRUE)
	@Pc(286) val vector: Vector<*>? = field.get(client::class.java.classLoader) as Vector<*>?
	@Pc(288) var i: Int = 0
	while (vector!!.size > i) {
		try {
			@Pc(294) val obj: Any? = vector.elementAt(i)
			@Pc(299) val field2: Field = obj!!.javaClass.getDeclaredField("name")
			method.invoke(field2, java.lang.Boolean.TRUE)
			try {
				@Pc(314) val string2: String? = field2.get(obj) as String?
				if (string2 != null && string2.indexOf("sw3d.dll") != -1) {
					@Pc(326) val field3: Field = obj.javaClass.getDeclaredField("handle")
					method.invoke(field3, java.lang.Boolean.TRUE)
					string = string + "|16)" + java.lang.Long.toHexString(field3.getLong(obj))
					method.invoke(field3, java.lang.Boolean.FALSE)
				}
			} catch (@Pc(362) ex: Throwable) {
			}
			method.invoke(field2, java.lang.Boolean.FALSE)
		} catch (@Pc(374) ex2: Throwable) {
		}
		i++
	}
	return string
}
