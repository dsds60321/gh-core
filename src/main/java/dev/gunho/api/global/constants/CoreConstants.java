package dev.gunho.api.global.constants;

import java.util.List;

public class CoreConstants {

    /**
     * HOST 정보
     */
    public static class Host {

        public static final String V1_HOST = "/bingo-us/v1";
        public static final String V1_ONGI_HOST = "/ongimemo/v1";
        public static final List<String> V1_HOSTS = List.of(
                "/bingo-us/v1",
                "/ongimemo/v1"
        );
        public static final String INVITE_URL = "bingous://signup?token=%s";

    }

    public static class Network {
        public static final String AUTH_KEY = "Authorization";
    }

    public static class Key {

        public static final String EMAIL_VERIFY = "EMAIL_VERIFY:%s";
        public static final String COUPLE_INVITE = "COUPLE_INVITE:%s";
    }


}
