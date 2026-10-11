import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class StaffTeleportCommand {

	@OriginalMember(owner = "client!fe", name = "c", descriptor = "(IIII)V")
	public static void executeTeleportCommand(@OriginalArg(0) int y, @OriginalArg(2) int level, @OriginalArg(3) int x) {
		@Pc(35) String string = "tele " + level + "," + (x >> 6) + "," + (y >> 6) + "," + (x & 0x3F) + "," + (y & 0x3F);
		System.out.println(string);
		ClientCommandProcessor.executeCommand(string, true, false);
	}
}
