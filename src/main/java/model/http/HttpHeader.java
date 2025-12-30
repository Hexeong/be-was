package model.http;

import java.util.Map;

public record HttpHeader(
        Map<String, String> headers
) {

}
