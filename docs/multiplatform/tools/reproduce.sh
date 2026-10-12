#!/usr/bin/env bash
# Rebuilds docs/multiplatform/void-client-228561a.{match,tiny,members.tsv} from scratch.
# Usage: docs/multiplatform/tools/reproduce.sh <work dir>
# Needs Java 21, git, network (clones FabricMC Matcher and its Gradle deps).
set -euo pipefail

W=$(realpath -m "$1"); mkdir -p "$W"
REPO=$(git -C "$(dirname "$0")" rev-parse --show-toplevel)
VC=${VOID_CLIENT:-/home/pixel/Documents/Projects/void-client}
JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk}
J=$JAVA_HOME/bin
TOOLS=$REPO/docs/multiplatform/tools
ANN=$(find ~/.gradle/caches -name 'openrs2-deob-annotations-*.jar' | head -1)

# 1. Matcher (headless CLI module), pinned to the commit the mapping was made with.
[ -d "$W/Matcher" ] || git clone -q https://github.com/FabricMC/Matcher "$W/Matcher"
git -C "$W/Matcher" checkout -q 73db496b34b5c0f3c134730cae2e50d1fa4bbafa
(cd "$W/Matcher" && JAVA_HOME=$JAVA_HOME ./gradlew -q :matcher-cli:installDist)
LIB=$W/Matcher/matcher-cli/build/install/matcher-cli/lib
ASM=$(ls "$LIB"/asm*.jar | tr '\n' ':')
ALL=$(ls "$LIB"/*.jar | tr '\n' ':')

# 2. void-client 228561a: root-package Java, compiled for Java 8 like the original
#    (StringBuilder string concatenation, Component.getPeer). AuthenticationInfo is a
#    compile-only stub and is not in the jar.
rm -rf "$W/vc" && mkdir -p "$W/vc/classes" "$W/vc/stub/sun/net/www/protocol/http" "$W/vc/stubout"
git -C "$VC" archive 228561a client libs | tar -x -C "$W/vc"
echo 'package sun.net.www.protocol.http; public class AuthenticationInfo {}' \
	> "$W/vc/stub/sun/net/www/protocol/http/AuthenticationInfo.java"
"$J/javac" --release 8 -nowarn -d "$W/vc/stubout" "$W/vc/stub/sun/net/www/protocol/http/AuthenticationInfo.java"
find "$W/vc/client/src" -name '*.java' > "$W/vc/srcs.txt"
"$J/javac" --release 8 -nowarn -encoding UTF-8 -proc:none -cp "$W/vc/libs/clientlibs.jar:$W/vc/stubout" \
	-d "$W/vc/classes" @"$W/vc/srcs.txt"
(cd "$W/vc/classes" && "$J/jar" cf "$W/vc.jar" .)
# void-client ships its native packages in clientlibs.jar; make them inputs so they match ours.
rm -rf "$W/vcnat" && mkdir "$W/vcnat"
(cd "$W/vcnat" && unzip -qo "$W/vc/libs/clientlibs.jar" 'jaclib/*' 'jaggl/*' 'jagdx/*' 'jagtheora/*' 'jagex3/*' \
	&& "$J/jar" cf "$W/vc-natives.jar" .)

# 3. This client at f2649c4, string concatenation inlined so method bodies compare like for like.
rm -rf "$W/ours" && mkdir -p "$W/ours"
git -C "$REPO" archive f2649c4 src lib | tar -x -C "$W/ours"
find "$W/ours/src/main/java" -name '*.java' > "$W/ours/srcs.txt"
"$J/javac" -XDstringConcat=inline -nowarn -encoding UTF-8 -proc:none -cp "$W/ours/lib/stub.jar:$ANN" \
	-d "$W/ours/classes" @"$W/ours/srcs.txt"
rm -rf "$W/ours/classes/com/ms"   # stub.jar's bundled sources, not ours
(cd "$W/ours/classes" && "$J/jar" cf "$W/ours.jar" .)

# 4. Analysis-only copy of ours with each static moved back to its @OriginalMember owner.
"$J/java" -cp "$ASM" "$TOOLS/Reloc.java" "$W/ours.jar" "$W/ours-reloc.jar" "$W/moves.tsv"

# 5. Matcher: default auto-match, then lower absolute thresholds down to 0.50 (relative margin 0.085).
#    Native packages and the 14 root native classes are matched by name: JNI fixes those names.
P='^((jaclib|jaggl|jagdx|jagtheora|jagex3)/.*|a|ba|h|i|j|ja|n|na|oa|p|t|wa|xa|ya)$'
MP='^(jaclib|jaggl|jagdx|jagtheora|jagex3)/.*'
"$J/java" -Xmx12g -cp "$ALL" "$TOOLS/Drive.java" "$W/run.match" "$W/vc.jar" "$W/vc-natives.jar" -- "$W/ours-reloc.jar" -- \
	"$W/vc/libs/clientlibs.jar" -- "$W/ours/lib/stub.jar" "$ANN" -- "$P" 0.50 0.085 "$MP"

# 6. Tiny v2 + member TSV against the real jar, and the coverage numbers.
(cd "$REPO" && ./gradlew -q assemble)
"$J/java" -cp "$ASM" "$TOOLS/Export.java" "$W/run.match" "$W/moves.tsv" "$W/vc.jar" "$W/vc-natives.jar" \
	"$REPO/build/libs/runetek5-634.jar" "$W/void-client-228561a.tiny" "$W/void-client-228561a.members.tsv" "$W/stats.txt"
