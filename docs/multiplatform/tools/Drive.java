import java.nio.file.*;
import java.util.*;
import matcher.core.Matcher;
import matcher.core.serdes.MatchesIo;
import matcher.model.InputFile;
import matcher.model.classifier.ClassifierLevel;
import matcher.model.config.Config;
import matcher.model.config.ProjectConfig;
import matcher.model.type.ClassEnvironment;

/**
 * Headless Matcher run: same setup as `matcher-cli automatch`, then keeps auto-matching with
 * stepwise lower absolute thresholds (relative margin kept, so a match must still clearly beat
 * the runner-up) until nothing more matches.
 * args: out.match inputA... -- inputB... -- cpA... -- cpB... -- classPattern absFloor rel memberPattern
 */
public class Drive {
	public static void main(String[] a) throws Exception {
		List<List<Path>> g = new ArrayList<>();
		List<Path> cur = new ArrayList<>();
		int i = 1;
		for (; i < a.length && g.size() < 4; i++) {
			if (a[i].equals("--")) { g.add(cur); cur = new ArrayList<>(); continue; }
			cur.add(Paths.get(a[i]));
		}
		String pattern = a[i];
		double floor = Double.parseDouble(a[i + 1]);
		double rel = Double.parseDouble(a[i + 2]);
		Matcher.init();
		ClassEnvironment env = new ClassEnvironment();
		Matcher m = new Matcher(env);
		ProjectConfig config = new ProjectConfig.Builder(InputFile.fromPaths(g.get(0)), InputFile.fromPaths(g.get(1)))
				.classPathA(InputFile.fromPaths(g.get(2)))
				.classPathB(InputFile.fromPaths(g.get(3))).sharedClassPath(new ArrayList<>()).inputsBeforeClassPath(false)
				.saveUnmappedMatches(true)
				.nonObfuscatedClassPatternA(pattern).nonObfuscatedClassPatternB(pattern)
				.nonObfuscatedMemberPatternA(a[i + 3]).nonObfuscatedMemberPatternB(a[i + 3])
				.build();
		Config.setProjectConfig(config);
		m.init(config, p -> { });
		for (int p = 0; p < 3; p++) m.autoMatchAll(p2 -> { });
		for (double abs = 0.80; abs >= floor - 1e-9; abs -= 0.05) {
			boolean any;
			do {
				any = false;
				for (ClassifierLevel lvl : new ClassifierLevel[] {ClassifierLevel.Full, ClassifierLevel.Extra}) {
					any |= m.autoMatchClasses(lvl, abs, rel, x -> { });
					any |= m.autoMatchMethods(lvl, abs, rel, x -> { });
					any |= m.autoMatchFields(lvl, abs, rel, x -> { });
				}
			} while (any);
			System.out.println("abs " + String.format("%.2f", abs) + " done");
		}
		Path out = Paths.get(a[0]);
		Files.deleteIfExists(out);
		MatchesIo.write(m, out);
		System.exit(0);
	}
}
