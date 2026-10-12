import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmStatic

object StaffTeleportCommand {

	@OriginalMember(owner = "client!fe", name = "c", descriptor = "(IIII)V")
	@JvmStatic
	fun executeTeleportCommand(@OriginalArg(0) y: Int, @OriginalArg(2) level: Int, @OriginalArg(3) x: Int) {
		@Pc(35) val string: String = "tele " + level + "," + (x shr 6) + "," + (y shr 6) + "," + (x and 0x3F) + "," + (y and 0x3F)
		println(string)
		ClientCommandProcessor.executeCommand(string, true, false)
	}
}
