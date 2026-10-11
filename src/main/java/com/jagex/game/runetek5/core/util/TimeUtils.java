package com.jagex.game.runetek5.core.util;
import org.openrs2.deob.annotation.OriginalMember;
import java.util.Date;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import java.util.Calendar;
import java.util.TimeZone;
import com.jagex.game.runetek5.client.ClientState;

public final class TimeUtils {

	public static final long MILLISECONDS_PER_MINUTE = 60000L;

	public static final long MILLISECONDS_PER_DAY = 86400000L;

	public static final long MILLISECONDS_PER_THREE_YEARS = 94608000000L;

	public static final int RUNEDAYS_SINCE_UNIX_EPOCH = 11745;

	private TimeUtils() {
		/* empty */
	}
	@OriginalMember(owner = "client!dia", name = "b", descriptor = "[Ljava/lang/String;")
	public static final String[] MONTH_ABBREVIATIONS = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(BJ)V")
	public static void sleep(@OriginalArg(1) long millis) {
		if (millis <= 0L) {
			return;
		}
		if (millis % 10L == 0L) {
			TimeUtils.sleepQuietly(millis - 1L);
			TimeUtils.sleepQuietly(1L);
		} else {
			TimeUtils.sleepQuietly(millis);
		}
	}

	@OriginalMember(owner = "client!mba", name = "a", descriptor = "(JI)Ljava/lang/String;")
	public static String formatCookieDate(@OriginalArg(0) long timeMillis) {
		TimeUtils.ACTIVE_TIMEZONE_CALENDER.setTime(new Date(timeMillis));
		@Pc(13) int dayOfWeek = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(7);
		@Pc(17) int dayOfMonth = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(5);
		@Pc(26) int month = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(2);
		@Pc(30) int year = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(1);
		@Pc(34) int hour = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(11);
		@Pc(38) int minute = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(12);
		@Pc(42) int second = TimeUtils.ACTIVE_TIMEZONE_CALENDER.get(13);
		return TimeUtils.DAY_ABBREVIATIONS[dayOfWeek - 1] + ", " + dayOfMonth / 10 + dayOfMonth % 10 + "-" + TimeUtils.MONTH_ABBREVIATIONS[month] + "-" + year + " " + hour / 10 + hour % 10 + ":" + minute / 10 + minute % 10 + ":" + second / 10 + second % 10 + " GMT";
	}
	@OriginalMember(owner = "client!em", name = "l", descriptor = "Ljava/util/Calendar;")
	public static final Calendar ACTIVE_TIMEZONE_CALENDER = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	@OriginalMember(owner = "client!lt", name = "c", descriptor = "[Ljava/lang/String;")
	public static final String[] DAY_ABBREVIATIONS = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

	@OriginalMember(owner = "client!de", name = "a", descriptor = "(IJ)V")
	public static void sleepQuietly(@OriginalArg(1) long millis) {
		try {
			Thread.sleep(millis);
		} catch (@Pc(15) InterruptedException ex) {
		}
	}
}
