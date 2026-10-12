package org.openrs2.deob.annotation

/** The bytecode offset (pc) the deobfuscator recorded for the local variable or parameter. */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.VALUE_PARAMETER)
annotation class Pc(val value: Int)
