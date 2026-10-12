import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/**
 * Turns Matcher's .match (void-client vs the relocated analysis jar) into a Tiny v2 mapping and a
 * member TSV against the real 634-client jar, putting relocated statics back on their real owner,
 * and prints coverage numbers.
 * args: run.match moves.tsv vc.jar vcNatives.jar ours.jar out.tiny out.tsv stats.txt
 */
public class Export {
	record Cls(String name, String sup, int size, Set<String> methods, Set<String> fields, boolean nat, String orig) {}

	static Map<String, Cls> load(String... jars) throws Exception {
		Map<String, Cls> m = new LinkedHashMap<>();
		for (String j : jars) try (JarFile jf = new JarFile(j)) {
			for (JarEntry e : Collections.list(jf.entries())) {
				if (!e.getName().endsWith(".class")) continue;
				byte[] b = jf.getInputStream(e).readAllBytes();
				ClassNode cn = new ClassNode();
				new ClassReader(b).accept(cn, 0);
				Set<String> ms = new LinkedHashSet<>(), fs = new LinkedHashSet<>();
				boolean nat = false;
				for (MethodNode mn : cn.methods) { ms.add(mn.name + mn.desc); nat |= (mn.access & Opcodes.ACC_NATIVE) != 0; }
				for (FieldNode fn : cn.fields) fs.add(fn.name + ";;" + fn.desc);
				String orig = null;
				if (cn.invisibleAnnotations != null) for (AnnotationNode an : cn.invisibleAnnotations)
					if (an.desc.contains("OriginalClass")) orig = (String) an.values.get(1);
				m.put(cn.name, new Cls(cn.name, cn.superName, b.length, ms, fs, nat, orig));
			}
		}
		return m;
	}

	static String strip(String l) { return l.substring(1, l.length() - 1); }

	public static void main(String[] a) throws Exception {
		Map<String, Cls> vc = load(a[2], a[3]);
		Map<String, Cls> ours = load(a[4]);
		// relocated member (newOwner, name, desc) -> real owner + real name
		Map<String, String[]> moved = new HashMap<>();
		for (String l : Files.readAllLines(Paths.get(a[1]))) {
			String[] p = l.split("\t");
			String key = p[1] + "." + p[2] + (p[0].equals("f") ? ";;" : "") + p[3];
			moved.put(key, new String[] {p[4], p[5]});
		}
		Map<String, String> cls = new LinkedHashMap<>();
		List<String[]> mem = new ArrayList<>(); // kind vcOwner vcName vcDesc ourOwner ourName ourDesc
		String curA = null, curB = null;
		for (String l : Files.readAllLines(Paths.get(a[0]))) {
			String[] p = l.split("\t", -1);
			if (l.startsWith("c\t")) {
				curA = strip(p[1]); curB = strip(p[2]);
				cls.put(curA, curB);
			} else if (l.startsWith("\tm\t") || l.startsWith("\tf\t")) {
				boolean f = p[1].equals("f");
				String sa = p[2], sb = p[3];
				int ia = f ? sa.indexOf(";;") : sa.indexOf('('), ib = f ? sb.indexOf(";;") : sb.indexOf('(');
				String an = sa.substring(0, ia), ad = sa.substring(ia + (f ? 2 : 0));
				String bn = sb.substring(0, ib), bd = sb.substring(ib + (f ? 2 : 0));
				String owner = curB, name = bn;
				String[] mv = moved.get(curB + "." + bn + (f ? ";;" : "") + bd);
				if (mv != null) { owner = mv[0]; name = mv[1]; }
				mem.add(new String[] {f ? "f" : "m", curA, an, ad, owner, name, bd});
			}
		}
		// --- tiny v2
		List<String> out = new ArrayList<>();
		out.add("tiny\t2\t0\tvoid\trunetek5");
		Map<String, List<String[]>> byOwner = new LinkedHashMap<>();
		for (String[] m : mem) byOwner.computeIfAbsent(m[1], k -> new ArrayList<>()).add(m);
		for (Map.Entry<String, String> e : cls.entrySet()) {
			String b = e.getValue();
			boolean synth = b.startsWith("_orig/");
			out.add("c\t" + e.getKey() + "\t" + (synth ? "" : b));
			if (synth) out.add("\tc\tOriginal class client!" + b.substring(6) + " has no class in runetek5 (its statics moved to other classes); see member comments.");
			for (String[] m : byOwner.getOrDefault(e.getKey(), List.of())) {
				out.add("\t" + m[0] + "\t" + m[3] + "\t" + m[2] + "\t" + m[5]);
				if (!m[4].equals(b)) out.add("\t\tc\tDeclared in " + m[4] + " in runetek5.");
			}
		}
		Files.write(Paths.get(a[5]), out);
		List<String> tsv = new ArrayList<>();
		tsv.add("kind\tvoid_owner\tvoid_name\tvoid_desc\trunetek5_owner\trunetek5_name\trunetek5_desc");
		for (Map.Entry<String, String> e : cls.entrySet())
			tsv.add("c\t" + e.getKey() + "\t\t\t" + (e.getValue().startsWith("_orig/") ? "" : e.getValue()) + "\t\t");
		for (String[] m : mem) tsv.add(String.join("\t", m));
		Files.write(Paths.get(a[6]), tsv);
		// --- stats
		List<String> st = new ArrayList<>();
		int vcM = 0, vcF = 0, oM = 0, oF = 0;
		for (Cls c : vc.values()) { vcM += c.methods.size(); vcF += c.fields.size(); }
		for (Cls c : ours.values()) { oM += c.methods.size(); oF += c.fields.size(); }
		Set<String> bCls = new HashSet<>();
		for (String b : cls.values()) if (ours.containsKey(b)) bCls.add(b);
		int synth = 0;
		for (String b : cls.values()) if (b.startsWith("_orig/")) synth++;
		long aM = mem.stream().filter(m -> m[0].equals("m") && vc.containsKey(m[1]) && vc.get(m[1]).methods.contains(m[2] + m[3])).count();
		long aF = mem.stream().filter(m -> m[0].equals("f") && vc.containsKey(m[1]) && vc.get(m[1]).fields.contains(m[2] + ";;" + m[3])).count();
		long bM = mem.stream().filter(m -> m[0].equals("m") && ours.containsKey(m[4]) && ours.get(m[4]).methods.contains(m[5] + m[6])).count();
		long bF = mem.stream().filter(m -> m[0].equals("f") && ours.containsKey(m[4]) && ours.get(m[4]).fields.contains(m[5] + ";;" + m[6])).count();
		int withOrig = 0, withOrigMatched = 0;
		for (Cls c : ours.values()) if (c.orig != null && c.orig.startsWith("client!") && !c.orig.contains("/")) { withOrig++; if (bCls.contains(c.name)) withOrigMatched++; }
		int vcCount = 0; for (String n : vc.keySet()) vcCount++;
		int aCls = 0; for (String n : cls.keySet()) if (vc.containsKey(n)) aCls++;
		st.add("VC_CLASSES " + vcCount + " MATCHED " + aCls + " TO_DISSOLVED " + synth);
		st.add("VC_METHODS " + vcM + " MATCHED " + aM);
		st.add("VC_FIELDS " + vcF + " MATCHED " + aF);
		st.add("OURS_CLASSES " + ours.size() + " MATCHED " + bCls.size() + " ORIGINAL " + withOrig + " ORIGINAL_MATCHED " + withOrigMatched);
		st.add("OURS_METHODS " + oM + " MATCHED " + bM);
		st.add("OURS_FIELDS " + oF + " MATCHED " + bF);
		st.add("NATIVE_ROOT");
		for (Cls c : vc.values()) if (c.nat && !c.name.contains("/"))
			st.add(c.name + "\t" + cls.getOrDefault(c.name, "-") + "\t" + (ours.containsKey(c.name) && ours.get(c.name).nat));
		st.add("UNMATCHED_VC");
		vc.values().stream().filter(c -> !cls.containsKey(c.name)).sorted((x, y) -> y.size - x.size).limit(20)
				.forEach(c -> st.add(c.name + "\t" + c.size + "\t" + c.methods.size() + "\t" + c.fields.size() + "\t" + c.sup));
		st.add("UNMATCHED_OURS");
		ours.values().stream().filter(c -> !bCls.contains(c.name)).sorted((x, y) -> y.size - x.size).limit(20)
				.forEach(c -> st.add(c.name + "\t" + c.size + "\t" + c.methods.size() + "\t" + c.fields.size() + "\t" + (c.orig == null ? "-" : c.orig)));
		long unOrig = ours.values().stream().filter(c -> !bCls.contains(c.name) && c.orig != null).count();
		st.add("UNMATCHED_OURS_WITH_ORIGINALCLASS " + unOrig);
		Map<String, Integer> mm = new HashMap<>();
		for (String[] m : mem) if (ours.containsKey(m[4])) mm.merge(m[4], 1, Integer::sum);
		st.add("UNMATCHED_OURS_ORIGINAL_LIST");
		ours.values().stream().filter(c -> !bCls.contains(c.name) && c.orig != null).sorted((x, y) -> y.size - x.size)
				.forEach(c -> st.add(c.name + "\t" + c.size + "\t" + c.orig + "\t" + (c.methods.size() + c.fields.size()) + "\t" + mm.getOrDefault(c.name, 0)));
		int hc = 0, hm = 0, hmm = 0;
		for (Cls c : ours.values()) if (c.orig == null) { hc++; hm += c.methods.size() + c.fields.size(); hmm += mm.getOrDefault(c.name, 0); }
		st.add("OURS_NO_ORIGINALCLASS classes " + hc + " members " + hm + " matched " + hmm);
		st.add("OURS_NO_ORIGINALCLASS_TOP");
		ours.values().stream().filter(c -> c.orig == null).sorted((x, y) -> y.size - x.size).limit(20)
				.forEach(c -> st.add(c.name + "\t" + c.size + "\t" + (c.methods.size() + c.fields.size()) + "\t" + mm.getOrDefault(c.name, 0)));
		Files.write(Paths.get(a[7]), st);
		st.forEach(System.out::println);
	}
}
