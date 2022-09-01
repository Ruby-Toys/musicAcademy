package ruby.api.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static DateTimeFormatter localDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public static boolean compareToLocalDateTimeString(String before, String after) {
        return LocalDateTime.parse(before, localDateTimeFormatter())
                .isBefore(LocalDateTime.parse(after, localDateTimeFormatter()));
    }

    public static boolean equalsDay(String before, String after) {
        LocalDateTime beforeTime = LocalDateTime.parse(before, localDateTimeFormatter());
        LocalDateTime afterTime = LocalDateTime.parse(after, localDateTimeFormatter());

        return beforeTime.getDayOfWeek().equals(afterTime.getDayOfWeek());
    }
}
