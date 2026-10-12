package lang

import JavaDefaultSurface
import awt.Component
import com.jagex.game.runetek5.NanoTimer
import com.jagex.game.runetek5.client.cycle.SystemCycleManager
import com.jagex.game.runetek5.client.input.mouse.MouseManager
import com.jagex.game.runetek5.core.datastruct.ref.SoftReferenceFactory
import com.jagex.game.runetek5.graphics.texture.water.KeyedSoftReferenceFactory
import com.jagex.game.runetek5.sound.player.MonoSterioPlayer
import kotlin.reflect.KClass

/*
 * Not part of the original client: the classes the client creates by name
 * (forName(name).getDeclaredConstructor(...).newInstance(...)) that work in a browser (milestone 4).
 * The original creates them by name so that a JVM without the feature they need fails softly; a
 * name missing here throws ClassNotFoundException, and the client takes the same fallback. Left
 * out on purpose: ByteBufferArrayWrapper and ProxyManager (JVM-only), WindowMode (fullscreen),
 * CursorManager (no mouse warping or custom cursors through it),
 * DirectSoundPcmDevice (Microsoft's JVM).
 */
internal class RegisteredClass(val kClass: KClass<*>, val factory: (Array<out Any?>) -> Any)

internal val classRegistry: Map<String, RegisteredClass> = mapOf(
    "com.jagex.game.runetek5.NanoTimer" to RegisteredClass(NanoTimer::class) { NanoTimer() },
    "com.jagex.game.runetek5.client.cycle.SystemCycleManager" to RegisteredClass(SystemCycleManager::class) { SystemCycleManager() },
    "com.jagex.game.runetek5.client.input.mouse.MouseManager" to RegisteredClass(MouseManager::class) { args -> MouseManager(args[0] as Component?, args[1] as Boolean) },
    "com.jagex.game.runetek5.core.datastruct.ref.SoftReferenceFactory" to RegisteredClass(SoftReferenceFactory::class) { SoftReferenceFactory() },
    "com.jagex.game.runetek5.graphics.texture.water.KeyedSoftReferenceFactory" to RegisteredClass(KeyedSoftReferenceFactory::class) { KeyedSoftReferenceFactory() },
    "com.jagex.game.runetek5.sound.player.MonoSterioPlayer" to RegisteredClass(MonoSterioPlayer::class) { MonoSterioPlayer() },
    "JavaDefaultSurface" to RegisteredClass(JavaDefaultSurface::class) { JavaDefaultSurface() },
)

class NoSuchMethodException(message: String?) : Exception(message)
