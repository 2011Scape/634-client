import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/**
 * Analysis-only: moves every static member of the 634-client jar back to the class its
 * @OriginalMember owner names (the 2010 layout), so Matcher can compare it against a client
 * that kept the original layout. Writes the relocated jar and a TSV of moves:
 * kind newOwner name desc realOwner.
 */
public class Reloc {
	static final String OC = "Lorg/openrs2/deob/annotation/OriginalClass;";
	static final String OM = "Lorg/openrs2/deob/annotation/OriginalMember;";

	static String ann(List<AnnotationNode> l, String d, String key) {
		if (l == null) return null;
		for (AnnotationNode a : l) if (a.desc.equals(d) && a.values != null)
			for (int i = 0; i < a.values.size(); i += 2) if (a.values.get(i).equals(key)) return (String) a.values.get(i + 1);
		return null;
	}

	public static void main(String[] args) throws Exception {
		Map<String, ClassNode> classes = new LinkedHashMap<>();
		Map<String, byte[]> other = new LinkedHashMap<>();
		try (JarFile jf = new JarFile(args[0])) {
			for (JarEntry e : Collections.list(jf.entries())) {
				if (e.isDirectory()) continue;
				byte[] b = jf.getInputStream(e).readAllBytes();
				if (e.getName().endsWith(".class")) {
					ClassNode cn = new ClassNode();
					new ClassReader(b).accept(cn, 0);
					classes.put(cn.name, cn);
				} else other.put(e.getName(), b);
			}
		}
		// original class name -> our class
		Map<String, String> origToOurs = new HashMap<>();
		for (ClassNode cn : classes.values()) {
			String o = ann(cn.invisibleAnnotations, OC, "value");
			if (o != null) origToOurs.put(o, cn.name);
		}
		// decide moves: key owner.name.desc -> new owner
		Map<String, String> moveTo = new HashMap<>();
		Map<String, ClassNode> synth = new LinkedHashMap<>();
		List<String> moves = new ArrayList<>();
		for (ClassNode cn : new ArrayList<>(classes.values())) {
			List<Object[]> todo = new ArrayList<>();
			for (FieldNode f : cn.fields) {
				if ((f.access & Opcodes.ACC_STATIC) == 0) continue;
				String o = ann(f.invisibleAnnotations, OM, "owner");
				todo.add(new Object[] {f, o, f.name, f.desc});
			}
			for (MethodNode m : cn.methods) {
				if ((m.access & Opcodes.ACC_STATIC) == 0 || m.name.equals("<clinit>")) continue;
				String o = ann(m.invisibleAnnotations, OM, "owner");
				todo.add(new Object[] {m, o, m.name, m.desc});
			}
			for (Object[] t : todo) {
				String o = (String) t[1];
				if (o == null) continue;
				String target = origToOurs.get(o);
				if (target == null) {
					target = "_orig/" + o.replace("client!", "");
					if (!synth.containsKey(target)) {
						ClassNode s = new ClassNode();
						s.version = Opcodes.V1_8; s.access = Opcodes.ACC_PUBLIC; s.name = target; s.superName = "java/lang/Object";
						synth.put(target, s);
					}
				}
				if (target.equals(cn.name)) continue;
				moveTo.put(cn.name + "." + t[2] + t[3], target);
			}
		}
		classes.putAll(synth);
		// resolve a member ref to its declaring class by walking supers
		// (ref owner may be a subclass of the declaring class)
		// move members
		for (ClassNode cn : new ArrayList<>(classes.values())) {
			for (Iterator<FieldNode> it = cn.fields.iterator(); it.hasNext();) {
				FieldNode f = it.next();
				String t = moveTo.get(cn.name + "." + f.name + f.desc);
				if (t == null) continue;
				ClassNode dst = classes.get(t);
				String orig = f.name, n = f.name;
				while (hasField(dst, n, f.desc)) n = n + "_";
				if (!n.equals(f.name)) moveTo.put(cn.name + "." + f.name + f.desc + "#rename", n);
				it.remove(); f.access = (f.access & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED | Opcodes.ACC_FINAL)) | Opcodes.ACC_PUBLIC;
				FieldNode nf = f; nf.name = n; dst.fields.add(nf);
				moves.add("f\t" + t + "\t" + n + "\t" + f.desc + "\t" + cn.name + "\t" + orig);
			}
			for (Iterator<MethodNode> it = cn.methods.iterator(); it.hasNext();) {
				MethodNode m = it.next();
				String t = moveTo.get(cn.name + "." + m.name + m.desc);
				if (t == null) continue;
				ClassNode dst = classes.get(t);
				String orig = m.name, n = m.name;
				while (hasMethod(dst, n, m.desc)) n = n + "_";
				if (!n.equals(orig)) moveTo.put(cn.name + "." + orig + m.desc + "#rename", n);
				it.remove(); m.access = (m.access & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
				m.name = n; dst.methods.add(m);
				moves.add("m\t" + t + "\t" + n + "\t" + m.desc + "\t" + cn.name + "\t" + orig);
			}
		}
		// rewrite references
		for (ClassNode cn : classes.values()) for (MethodNode m : cn.methods) {
			if (m.instructions == null) continue;
			for (AbstractInsnNode in : m.instructions) {
				if (in instanceof FieldInsnNode fi) {
					String decl = declaring(classes, fi.owner, fi.name, fi.desc, true, moveTo);
					if (decl == null) continue;
					String t = moveTo.get(decl + "." + fi.name + fi.desc);
					if (t != null) {
						String rn = moveTo.get(decl + "." + fi.name + fi.desc + "#rename");
						fi.owner = t; if (rn != null) fi.name = rn;
					}
				} else if (in instanceof MethodInsnNode mi) {
					String decl = declaring(classes, mi.owner, mi.name, mi.desc, false, moveTo);
					if (decl == null) continue;
					String t = moveTo.get(decl + "." + mi.name + mi.desc);
					if (t != null) {
						String rn = moveTo.get(decl + "." + mi.name + mi.desc + "#rename");
						mi.owner = t; if (rn != null) mi.name = rn;
						mi.itf = (classes.get(t).access & Opcodes.ACC_INTERFACE) != 0;
					}
				}
			}
		}
		try (JarOutputStream out = new JarOutputStream(new FileOutputStream(args[1]))) {
			for (ClassNode cn : classes.values()) {
				ClassWriter cw = new ClassWriter(0);
				cn.accept(cw);
				out.putNextEntry(new JarEntry(cn.name + ".class"));
				out.write(cw.toByteArray());
				out.closeEntry();
			}
		}
		Files.write(Paths.get(args[2]), moves);
		System.out.println("classes " + classes.size() + " synthetic " + synth.size() + " moved members " + moves.size());
	}

	static String keyName(String owner, String desc, Map<String, String> moveTo, String n) {
		// original (pre-rename) field name is recoverable: strip trailing underscores added on collision
		return n.replaceAll("_+$", "");
	}

	static boolean hasField(ClassNode c, String n, String d) {
		for (FieldNode f : c.fields) if (f.name.equals(n) && f.desc.equals(d)) return true;
		return false;
	}

	static boolean hasMethod(ClassNode c, String n, String d) {
		for (MethodNode m : c.methods) if (m.name.equals(n) && m.desc.equals(d)) return true;
		return false;
	}

	// pre-move membership: a member is "declared" in decl if decl.name.desc is a move key or still present
	static String declaring(Map<String, ClassNode> classes, String owner, String n, String d, boolean field, Map<String, String> moveTo) {
		String c = owner;
		while (c != null) {
			if (moveTo.containsKey(c + "." + n + d)) return c;
			ClassNode cn = classes.get(c);
			if (cn == null) return null;
			if (field ? hasField(cn, n, d) : hasMethod(cn, n, d)) return null; // declared here, not moved
			c = cn.superName;
		}
		return null;
	}
}
