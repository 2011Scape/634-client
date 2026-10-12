/*
 * Not part of the original client: the native handle of the loaded sw3d.dll for client.getDebugString,
 * found through reflection into the JVM's class loader (milestone 3). Returns `string` with
 * "|16)<handle>" appended when the library is found.
 */
expect fun sw3dLibraryHandleInfo(string: String): String
