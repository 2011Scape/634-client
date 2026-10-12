package org.openrs2.deob.annotation

/*
 * OpenRS2's deobfuscator annotations, declared here so that common code can carry them (the
 * openrs2-deob-annotations artifact is JVM-only). Same names, targets and class-file retention as
 * the artifact's, so the compiled classes are annotated as before.
 */

/** The class's name in the original client, as "client!name". */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class OriginalClass(val value: String)
