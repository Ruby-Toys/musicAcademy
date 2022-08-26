package ruby.api.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static DateTimeFormatter localDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public static DateTimeFormatter localDateFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public static boolean compareToLocalDateTimeString(String before, String after) {
        return LocalDateTime.parse(before, localDateTimeFormatter())
                .isBefore(LocalDateTime.parse(after, localDateTimeFormatter()));
    }

    public static boolean compareToLocalDateString(String before, String after) {
        return !LocalDate.parse(after, localDateFormatter())
                .isBefore(LocalDate.parse(before, localDateFormatter()));
    }

    public static boolean equalsDay(String before, String after) {
        LocalDateTime beforeTime = LocalDateTime.parse(before, localDateTimeFormatter());
        LocalDateTime afterTime = LocalDateTime.parse(after, localDateTimeFormatter());

        return beforeTime.getDayOfWeek().equals(afterTime.getDayOfWeek());
    }
}
