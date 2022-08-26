package ruby.api.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static DateTimeFormatter formatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public static boolean compareDateString(String before, String after) {
        return LocalDateTime.parse(before, formatter())
                .isBefore(LocalDateTime.parse(after, formatter()));
    }
}
