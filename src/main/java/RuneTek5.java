import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.js5.request.RequestManager;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Starts the client against a server of your choosing.
 * <p>
 * The client's own {@code main} takes six positional arguments, always connects to this machine and
 * has the server's public keys built in. This launcher sets the {@code client.*} switches that
 * {@link ClientFeatures} reads, installs the keys given here, and then hands over to that {@code main},
 * so the client still runs in its own frame with its disk cache.
 * <pre>{@code
 * --address <host>            the server to connect to (default play.2011.rs; localhost for a server on
 *                             this machine)
 * --port <port>               the port for both connection attempts (default 43594, Void's)
 * --keys <game.properties>    read security.game.modulus and security.file.modulus from a Void config
 * --login-modulus <hex>       the login RSA modulus
 * --js5-modulus <hex>         the file server RSA modulus
 * --server <void|jagex>       the server's protocol: Void's (default) or the 2010 client's
 * --packets <file>            server packet sizes to override, one <opcode>=<size> per line
 * --world <id>                the world id (default 1)
 * --lang <english|german>     the language (default english)
 * --debug                     ids and coordinates on menus and in the fps overlay
 * --zoom, --no-zoom          a wider mouse-wheel zoom (default on)
 * --render-distance <x>       draw and fog distance times x (default 2)
 * --fps <n>                   frames drawn per second (default 144); the game logic stays at 50 per second
 * --scale <x>                 window pixels per game pixel (default from the screen: 1, 1.5 from 1080 lines
 *                             high, 2 from 2160)
 * --console, --no-console     command history, word editing and autocomplete in the console (default on)
 * --shift-drop, --no-shift-drop  Shift with a left click drops an inventory item (default on; ::shiftdrop
 *                             toggles it)
 * --tile-markers              mark tiles from the right-click menu ("Mark tile"; ::tilemarkers toggles it)
 * --xp-drops                  "+N Skill" drops when experience goes up (::xpdrops toggles it)
 * --npc-labels                NPC names and combat levels above their heads (::npclabels toggles it)
 * --ground-items              the names of items on the ground above their tiles (::grounditems toggles it)
 * --msaa <n>                  OpenGL: draw the 3D view in its own framebuffer with n samples (0, 2, 4, 8)
 * --smaa                      OpenGL: smooth the 3D view's edges with SMAA 1x as well
 * --render-scale <x>          OpenGL: draw the 3D view x times larger and shrink it (1.0, 1.5, 2.0)
 * --no-a2c                    OpenGL: no alpha-to-coverage for cut-out textures
 * --dev                       --debug, --zoom and --console together
 * }</pre>
 * On Linux the window class of a Java application is the simple name of its main class, and desktops
 * match windows to installed applications by it. This class is named after the engine rather than
 * something as common as "Launcher", which other applications claim.
 */
public final class RuneTek5 {

    /** The server the launcher connects to when no --address is given. */
    private static final String DEFAULT_ADDRESS = "play.2011.rs";

    /** The port a Void server listens on. */
    private static final int DEFAULT_PORT = 43594;

    public static void main(String[] args) throws IOException {
        // The client is an applet (GameShell extends java.applet.Applet), so a JDK without the
        // Applet API cannot run it: say so rather than fail on the first class that needs it.
        try {
            Class.forName("java.applet.Applet");
        } catch (ClassNotFoundException ex) {
            System.err.println("This client needs Java 21 to 25. Java " + Runtime.version().feature()
                + " no longer has the Applet API the 2010 client is built on.");
            System.exit(1);
        }
        if (relaunchWithNativeLibraryPath(args)) {
            return;
        }
        var world = "1";
        var lang = "english";
        BigInteger loginModulus = null;
        BigInteger js5Modulus = null;

        for (var i = 0; i < args.length; i++) {
            var arg = args[i];
            switch (arg) {
                case "--address" -> System.setProperty("client.address", value(args, ++i, arg));
                case "--port" -> System.setProperty("client.port", Integer.toString(Integer.parseInt(value(args, ++i, arg))));
                case "--server" -> System.setProperty("client.server", value(args, ++i, arg));
                case "--packets" -> System.setProperty("client.packets", path(value(args, ++i, arg)).toString());
                case "--world" -> world = Integer.toString(Integer.parseInt(value(args, ++i, arg)));
                case "--lang" -> lang = value(args, ++i, arg);
                case "--scale" -> System.setProperty("client.scale", Double.toString(Double.parseDouble(value(args, ++i, arg))));
                case "--fps" -> System.setProperty("client.fps", Integer.toString(Integer.parseInt(value(args, ++i, arg))));
                case "--render-distance" -> System.setProperty("client.renderDistance", Double.toString(Double.parseDouble(value(args, ++i, arg))));
                case "--debug" -> System.setProperty("client.debug", "true");
                case "--zoom" -> System.setProperty("client.zoom", "true");
                case "--no-zoom" -> System.setProperty("client.zoom", "false");
                case "--console" -> System.setProperty("client.console", "true");
                case "--no-console" -> System.setProperty("client.console", "false");
                case "--shift-drop" -> System.setProperty("client.shiftDrop", "true");
                case "--no-shift-drop" -> System.setProperty("client.shiftDrop", "false");
                case "--tile-markers" -> System.setProperty("client.tileMarkers", "true");
                case "--xp-drops" -> System.setProperty("client.xpDrops", "true");
                case "--npc-labels" -> System.setProperty("client.npcLabels", "true");
                case "--ground-items" -> System.setProperty("client.groundItemLabels", "true");
                case "--msaa" -> System.setProperty("client.msaa", Integer.toString(Integer.parseInt(value(args, ++i, arg))));
                case "--smaa" -> System.setProperty("client.smaa", "true");
                case "--render-scale" -> System.setProperty("client.renderScale", Double.toString(Double.parseDouble(value(args, ++i, arg))));
                case "--no-a2c" -> System.setProperty("client.a2c", "false");
                case "--dev" -> {
                    System.setProperty("client.debug", "true");
                    System.setProperty("client.zoom", "true");
                    System.setProperty("client.console", "true");
                }
                case "--login-modulus" -> loginModulus = new BigInteger(value(args, ++i, arg), 16);
                case "--js5-modulus" -> js5Modulus = new BigInteger(value(args, ++i, arg), 16);
                case "--keys" -> {
                    var keys = readKeys(path(value(args, ++i, arg)));
                    loginModulus = keys.get(0);
                    js5Modulus = keys.get(1);
                }
                case "--help", "-h" -> {
                    usage();
                    return;
                }
                default -> {
                    System.err.println("Unknown option " + arg);
                    usage();
                    System.exit(1);
                }
            }
        }

        // Without a port the client would work one out from the world id, which a Void server does not
        // listen on, so the launcher defaults to Void's port.
        if (System.getProperty("client.port") == null) {
            System.setProperty("client.port", Integer.toString(DEFAULT_PORT));
        }
        setDefault("client.address", DEFAULT_ADDRESS);

        // The launcher's defaults, each kept only when no option set it.
        setDefault("client.fps", "144");
        setDefault("client.renderDistance", "2");
        setDefault("client.zoom", "true");
        setDefault("client.console", "true");
        setDefault("client.shiftDrop", "true");
        if (System.getProperty("client.scale") == null) {
            System.setProperty("client.scale", screenScale());
        }

        // ClientFeatures reads the switches when the client first uses it, which is after this point.
        if (loginModulus != null) {
            LoginManager.rsaModulus = loginModulus;
        }
        if (js5Modulus != null) {
            RequestManager.js5Modulus = js5Modulus;
        }

        client.main(new String[] {world, world, "0", "live", lang, "game0"});
    }

    /**
     * On Linux the native OpenGL toolkit (libhw3d.so) links against libjaclib.so by name, and the
     * system's loader finds it only on LD_LIBRARY_PATH, which it reads once when the JVM starts.
     * Without the client's native library directory there, the client falls back to the software
     * toolkit. So when the directory is missing, start this launcher again in a JVM that has it, with
     * the same JVM options and arguments, and exit with its status. Returns false when nothing was
     * needed. -Dclient.nativeDir overrides the directory; -Dclient.relaunch=false turns this off (a
     * debugger then needs LD_LIBRARY_PATH set by hand).
     */
    private static boolean relaunchWithNativeLibraryPath(String[] args) throws IOException {
        if (!System.getProperty("os.name").startsWith("Linux") || "false".equals(System.getProperty("client.relaunch"))
            || System.getenv("RUNETEK5_RELAUNCHED") != null) {
            return false;
        }
        var dir = System.getProperty("client.nativeDir", System.getProperty("user.home") + "/.jagex_cache_32/runescape");
        var current = System.getenv("LD_LIBRARY_PATH");
        if (current != null && Arrays.asList(current.split(":")).contains(dir)) {
            return false;
        }

        var command = new ArrayList<String>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        for (var option : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            // a debugger agent would try to bind the port this JVM already holds
            if (!option.startsWith("-agentlib:jdwp") && !option.startsWith("-Xrunjdwp")) {
                command.add(option);
            }
        }
        // the release jar's manifest opens java.lang, which a JVM started by -cp does not read
        command.add("--add-opens");
        command.add("java.base/java.lang=ALL-UNNAMED");
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(RuneTek5.class.getName());
        command.addAll(Arrays.asList(args));

        var builder = new ProcessBuilder(command).inheritIO();
        builder.environment().put("LD_LIBRARY_PATH", current == null || current.isEmpty() ? dir : dir + ":" + current);
        builder.environment().put("RUNETEK5_RELAUNCHED", "1");
        try {
            System.exit(builder.start().waitFor());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.exit(1);
        }
        return true;
    }

    private static void setDefault(String name, String value) {
        if (System.getProperty(name) == null) {
            System.setProperty(name, value);
        }
    }

    /**
     * The scale for the screen the client opens on: 2 on a screen 2160 lines high (4K) or more, 1.5
     * from 1080 lines (1080p, 1440p), otherwise 1. The height is in the desktop's own units, so on a
     * display the system already enlarges (Windows at 150%, say) it reads smaller and the two scales
     * do not stack.
     */
    private static String screenScale() {
        if (GraphicsEnvironment.isHeadless()) {
            return "1.0";
        }
        var height = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds().height;
        return height >= 2160 ? "2.0" : height >= 1080 ? "1.5" : "1.0";
    }

    private static String value(String[] args, int index, String option) {
        if (index >= args.length) {
            System.err.println(option + " needs a value");
            usage();
            System.exit(1);
        }
        return args[index];
    }

    /**
     * A path from the command line. Gradle passes {@code --args} without a shell, so a leading
     * {@code ~} is expanded here. A relative path is taken from the module's directory, where Gradle
     * runs the client.
     */
    private static Path path(String value) {
        if (value.equals("~") || value.startsWith("~/")) {
            value = System.getProperty("user.home") + value.substring(1);
        }
        return Path.of(value);
    }

    /** The login and file server moduli from a Void game.properties, in that order. */
    private static List<BigInteger> readKeys(Path file) throws IOException {
        var properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file)) {
            properties.load(reader);
        }
        var keys = new ArrayList<BigInteger>();
        for (var name : List.of("security.game.modulus", "security.file.modulus")) {
            var hex = properties.getProperty(name);
            if (hex == null) {
                throw new IOException(file + " has no " + name);
            }
            keys.add(new BigInteger(hex.strip(), 16));
        }
        return keys;
    }

    private static void usage() {
        System.err.println("""
            usage: RuneTek5 [--address host] [--port port] [--keys game.properties]
                            [--login-modulus hex] [--js5-modulus hex] [--server void|jagex] [--packets file]
                            [--world id] [--lang english|german]
                            [--debug] [--[no-]zoom] [--render-distance x] [--fps n] [--scale x] [--msaa n] [--smaa] [--render-scale x] [--no-a2c] [--[no-]console] [--[no-]shift-drop] [--dev]
                            [--tile-markers] [--xp-drops] [--npc-labels] [--ground-items]""");
    }

    private RuneTek5() {
        /* empty */
    }
}
