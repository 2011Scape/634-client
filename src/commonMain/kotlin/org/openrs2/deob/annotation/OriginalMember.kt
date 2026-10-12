package org.openrs2.deob.annotation

/** The member's owner, name and descriptor in the original client. */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
annotation class OriginalMember(val owner: String, val name: String, val descriptor: String)
