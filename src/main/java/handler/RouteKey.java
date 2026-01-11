package handler;

import model.http.sub.RequestMethod;

public record RouteKey(
        RequestMethod method,
        String urlPath
) {
}
