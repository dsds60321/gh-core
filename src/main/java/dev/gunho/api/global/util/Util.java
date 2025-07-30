package dev.gunho.api.global.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class Util {

    public static class CommonUtil {

        public static String generateRandomCode(int length) {
            int min = (int) Math.pow(10, length - 1);
            int max = (int) Math.pow(10, length) - 1;
            int code = new SecureRandom().nextInt(max - min + 1) + min;

            return String.valueOf(code);
        }


        public static boolean isEmpty(Object obj) {
            if (obj == null) {
                return true;
            } else if (obj instanceof String) {
                return ((String) obj).isEmpty();
            } else if (obj instanceof Optional) {
                return ((Optional<?>)obj).isEmpty();
            } else if (obj instanceof CharSequence) {
                return ((CharSequence) obj).isEmpty();
            } else if (obj.getClass().isArray()) {
                return Array.getLength(obj) == 0;
            } else if (obj instanceof Collection) {
                return ((Collection<?>)obj).isEmpty();
            } else {
                return obj instanceof Map && ((Map<?, ?>) obj).isEmpty();
            }
        }

        public static boolean isNotEmpty(Object obj) {
            return !CommonUtil.isEmpty(obj);
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

        public static LocalDate parseDate(String dateStr) {
            try {
                if (dateStr.matches("\\d{8}")) {
                    // YYYYMMDD 형식인 경우
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    return LocalDate.parse(dateStr, formatter);
                } else {
                    return LocalDate.parse(dateStr);
                }
            } catch (Exception e) {
                log.warn("날짜 파싱 오류. 기본값을 사용합니다. 입력값: {}, 오류: {}", dateStr, e.getMessage());
                return LocalDate.now();
            }
        }

        public static LocalDate parseMonth(String dateStr) {
            try {
                if (dateStr.matches("\\d{6}")) {
                    // YYYYMM 형식인 경우
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
                    return LocalDate.parse(dateStr, formatter);
                } else {
                    return LocalDate.parse(dateStr);
                }
            } catch (Exception e) {
                log.warn("날짜 파싱 오류. 기본값을 사용합니다. 입력값: {}, 오류: {}", dateStr, e.getMessage());
                return LocalDate.now();
            }
        }
    }
}
