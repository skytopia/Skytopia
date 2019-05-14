package solar.rpg.skytopia.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Utility {

    /* Static time units. */
    private static final long ONE_SECOND = 1;
    private static final long ONE_MINUTE = (ONE_SECOND * 60);
    private static final long ONE_HOUR = (ONE_MINUTE * 60);
    private static final long ONE_DAY = (ONE_HOUR * 24);
    private static final long ONE_MONTH = (ONE_DAY * 30);

    /**
     * Converts seconds into days, hours, minutes, and seconds.
     *
     * @param seconds Amount of seconds.
     * @return Converted time.
     */
    public static String convertTime(long seconds) {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        int hours = (int) (TimeUnit.SECONDS.toHours(seconds) - ((long) (day * 24)));
        int minute = (int) (TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60));
        int second = (int) (TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60));
        return (day != 0 ? day == 1 ? day + " day " : day + " days " : "") +
                (hours != 0 ? hours == 1 ? hours + " hour " : hours + " hours " : "") +
                (minute != 0 ? minute == 1 ? minute + " minute " : minute + " minutes " : "") +
                (second != 0 ? second == 1 ? second + " second" : second + " seconds" : "");
    }

    /**
     * Converts a time difference into a verbal time.
     *
     * @param then A time from the past.
     * @return The difference between the supplied time and now, prettified.
     */
    public static String prettyTime(long then) {
        StringBuilder res = new StringBuilder();
        long diff = timeToNow(then);
        if (diff < 15) {
            return "a few moments ago";
        }
        if (diff >= ONE_MONTH) {
            return "more than a month ago";
        }
        if (diff >= ONE_DAY) {
            long days = diff / ONE_DAY;
            res.append(days).append(" day").append(days == 1 ? " " : "s ");
            diff -= ONE_DAY * days;
        }
        if (diff >= ONE_HOUR) {
            long hours = diff / ONE_HOUR;
            res.append(hours).append(" hour").append(hours == 1 ? " " : "s ");
            diff -= ONE_HOUR * hours;
        }
        if (diff >= ONE_MINUTE) {
            long minutes = diff / ONE_MINUTE;
            res.append(minutes).append(" minute").append(minutes == 1 ? " " : "s ");
            diff -= ONE_MINUTE * minutes;
        }
        if (diff >= ONE_SECOND) {
            long seconds = diff / ONE_SECOND;
            res.append(seconds).append(" second").append(seconds == 1 ? " " : "s ");
        }
        res.append("ago");
        return res.toString();
    }

    /**
     * @param then The specified time.
     * @return The difference between now and the specified time.
     */
    private static long timeToNow(long then) {
        long diff = (new Date().getTime() / 1000) - (then / 1000);
        if (diff < 0) {
            return 0;
        }
        return diff;
    }
}