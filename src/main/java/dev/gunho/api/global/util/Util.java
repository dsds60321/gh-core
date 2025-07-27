package dev.gunho.api.global.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Util {

    public static class CommonUtil {

        public static String generateRandomCode(int length) {
            int min = (int) Math.pow(10, length - 1);
            int max = (int) Math.pow(10, length) - 1;
            int code = new SecureRandom().nextInt(max - min + 1) + min;

            return String.valueOf(code);
        }

    }

    public static class Encrypt {

    }

    public static class MsgUtil {

        /**
         * {} 형태 replace
         */
        public static String getMessage(String originMsg, List<String> replace) {
            if (originMsg == null || replace == null) {
                return originMsg;
            }

            String result = originMsg;
            int replaceIndex = 0;

            while (result.contains("{}") && replaceIndex < replace.size()) {
                String replacement = replace.get(replaceIndex) != null ? replace.get(replaceIndex) : "";
                result = result.replaceFirst("\\{\\}", replacement);
                replaceIndex++;
            }

            return result;
        }

    }

    public static class Date {

        public static String getDay() {
            return getDay("yyyy-MM-dd HH:mm:ss");
        }
        public static String getDay(String format) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
        }
    }
}
