package exception;

import model.http.HttpStatus;

public enum ErrorCode {
    // Infra
    CANNOT_ADAPT(HttpStatus.BAD_REQUEST, "해당 핸들러를 처리할 수 있는 어댑터가 존재하지 않습니다."),
    CLASS_SCAN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "해당 위치에서의 클래스 스캔 오류"),

    // Business
    NOT_AUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증이 필요한 곳에 접근중입니다");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
