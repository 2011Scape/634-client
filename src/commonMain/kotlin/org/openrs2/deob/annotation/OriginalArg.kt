package org.openrs2.deob.annotation

/** The parameter's index in the original method. */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class OriginalArg(val value: Int)
