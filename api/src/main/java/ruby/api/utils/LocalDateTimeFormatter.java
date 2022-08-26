package ruby.api.utils;

import java.time.format.DateTimeFormatter;

public class LocalDateTimeFormatter {
    public static DateTimeFormatter formatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
}
