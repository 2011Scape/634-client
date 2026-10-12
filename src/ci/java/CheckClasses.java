import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * The checks CI can run without a game server (./gradlew checkClasses, part of check).
 *
 * <ol>
 * <li>Every class of the client loads and links from the class path the task gives it (the release
 * jar): linking runs the JVM's bytecode verifier, so this fails on a class the compiler emitted
 * invalid code for (Kotlin 2.2.20 once did, in a static initialiser the smoke test never reaches)
 * and on anything the release jar is missing, such as the Kotlin standard library.</li>
 * <li>No method's bytecode is over HotSpot's 8000-byte limit for JIT compilation (HugeMethodLimit)
 * unless it is listed in the allowlist file. A method over it runs interpreted only, which made the
 * software renderer four times slower when the Kotlin version of two methods grew past it.</li>
 * </ol>
 *
 * Usage: CheckClasses &lt;client jar&gt; &lt;allowlist&gt;
 */
public final class CheckClasses {

    private static final int HUGE_METHOD_LIMIT = 8000;

    public static void main(String[] args) throws IOException {
        Path jar = Path.of(args[0]);
        Path allowlistFile = Path.of(args[1]);
        Set<String> allowed = new TreeSet<>();
        for (String line : Files.readAllLines(allowlistFile)) {
            line = line.strip();
            if (!line.isEmpty() && !line.startsWith("#")) {
                allowed.add(line);
            }
        }

        List<String> failures = new ArrayList<>();
        Set<String> huge = new TreeSet<>();
        int classes = 0;
        try (JarFile jarFile = new JarFile(jar.toFile())) {
            for (JarEntry entry : Collections.list(jarFile.entries())) {
                String name = entry.getName();
                if (!name.endsWith(".class") || name.startsWith("META-INF/")) {
                    continue;
                }
                classes++;
                String className = name.substring(0, name.length() - ".class".length()).replace('/', '.');
                try {
                    // getDeclaredMethods links the class, and linking verifies it
                    Class<?> type = Class.forName(className, false, CheckClasses.class.getClassLoader());
                    type.getDeclaredMethods();
                    type.getDeclaredFields();
                } catch (Throwable t) {
                    failures.add("does not load: " + className + ": " + t);
                }
                try (InputStream in = jarFile.getInputStream(entry)) {
                    methodSizes(new DataInputStream(in), className, huge);
                }
            }
        }

        for (String method : huge) {
            if (!allowed.contains(method.substring(0, method.lastIndexOf(' ')))) {
                failures.add("over the " + HUGE_METHOD_LIMIT + "-byte JIT limit: " + method
                    + " (split it, or add it to " + allowlistFile.getFileName() + " if the original was as big)");
            }
        }
        for (String method : allowed) {
            if (huge.stream().noneMatch(h -> h.startsWith(method + " "))) {
                failures.add("listed in " + allowlistFile.getFileName() + " but no longer over the limit, remove it: " + method);
            }
        }

        System.out.println("checkClasses: " + classes + " classes, " + huge.size() + " methods over the JIT limit (all allowlisted: " + (failures.stream().noneMatch(f -> f.startsWith("over")) ? "yes" : "no") + ")");
        if (!failures.isEmpty()) {
            failures.forEach(f -> System.out.println("FAIL " + f));
            System.exit(1);
        }
    }

    /** Adds "Class.method(descriptor) size" for each method of the class file whose code is over the limit. */
    private static void methodSizes(DataInputStream in, String className, Set<String> huge) throws IOException {
        in.readInt(); // magic
        in.readUnsignedShort(); // minor
        in.readUnsignedShort(); // major
        int poolCount = in.readUnsignedShort();
        String[] utf8 = new String[poolCount];
        for (int i = 1; i < poolCount; i++) {
            int tag = in.readUnsignedByte();
            switch (tag) {
                case 1 -> utf8[i] = in.readUTF();
                case 3, 4, 9, 10, 11, 12, 17, 18 -> in.skipNBytes(4);
                case 5, 6 -> { in.skipNBytes(8); i++; }
                case 7, 8, 16, 19, 20 -> in.skipNBytes(2);
                case 15 -> in.skipNBytes(3);
                default -> throw new IOException(className + ": constant pool tag " + tag);
            }
        }
        in.skipNBytes(6); // access, this, super
        in.skipNBytes(2L * in.readUnsignedShort()); // interfaces
        int fields = in.readUnsignedShort();
        for (int i = 0; i < fields; i++) {
            in.skipNBytes(6);
            skipAttributes(in);
        }
        int methods = in.readUnsignedShort();
        for (int i = 0; i < methods; i++) {
            in.readUnsignedShort(); // access
            String name = utf8[in.readUnsignedShort()];
            String descriptor = utf8[in.readUnsignedShort()];
            int attributes = in.readUnsignedShort();
            for (int a = 0; a < attributes; a++) {
                String attribute = utf8[in.readUnsignedShort()];
                int length = in.readInt();
                if (attribute.equals("Code")) {
                    in.skipNBytes(4); // max stack, max locals
                    int codeLength = in.readInt();
                    if (codeLength > HUGE_METHOD_LIMIT) {
                        huge.add(className + "." + name + descriptor + " " + codeLength);
                    }
                    in.skipNBytes(length - 8L);
                } else {
                    in.skipNBytes(length);
                }
            }
        }
    }

    private static void skipAttributes(DataInputStream in) throws IOException {
        int attributes = in.readUnsignedShort();
        for (int a = 0; a < attributes; a++) {
            in.skipNBytes(2);
            in.skipNBytes(in.readInt());
        }
    }
}
