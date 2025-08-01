package dev.gunho.api.global.constants;

public class CoreConstants {

    /**
     * HOST 정보
     */
    public static class Host {

        public static final String V1_HOST = "/bingo-us/v1";
        public static final String INVITE_URL = "/bingo-us/v1/couple/?token=%s";

    }

    public static class Network {
        public static final String AUTH_KEY = "Authorization";
    }

    public static class Key {

        public static final String EMAIL_VERIFY = "EMAIL_VERIFY:%s";
        public static final String COUPLE_INVITE = "COUPLE_INVITE:%s";
    }


}
