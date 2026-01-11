package util.extractor;

import model.http.HttpRequest;

public class CookieExtractor {

    private static final String COOKIE_HEADER_KEY = "cookie";

    private CookieExtractor() {}

    // [AI]
    public static String getValue(HttpRequest req, String cookieName) {
        String cookieHeader = req.headers().get(COOKIE_HEADER_KEY);

        if (cookieHeader == null || cookieHeader.isBlank()) {
            return null;
        }
        String[] cookies = cookieHeader.split(";");

        for (String cookie : cookies) {
            String trimmedCookie = cookie.trim();

            String prefix = cookieName + "=";
            if (trimmedCookie.startsWith(prefix)) {
                return trimmedCookie.substring(prefix.length());
            }
        }

        return null;
    }
}