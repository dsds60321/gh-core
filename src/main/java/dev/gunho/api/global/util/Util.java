package dev.gunho.api.global.util;

import java.security.SecureRandom;
import java.util.List;

public class Util {

    public static class CommonUtil {

        public static String generateRandomCode(int length) {
            if (length < 4 || length > 8) {
                throw new IllegalArgumentException("코드 길이는 4-8자리여야 합니다.");
            }

            int min = (int) Math.pow(10, length - 1);
            int max = (int) Math.pow(10, length) - 1;
            int code = new SecureRandom().nextInt(max - min + 1) + min;

            return String.valueOf(code);
        }

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
}
