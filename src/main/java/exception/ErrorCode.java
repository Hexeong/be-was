package exception;

import model.http.HttpStatus;

public enum ErrorCode {
    // Infra
    CANNOT_ADAPT(HttpStatus.BAD_REQUEST, "해당 핸들러를 처리할 수 있는 어댑터가 존재하지 않습니다."),
    CLASS_SCAN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "해당 위치에서의 클래스 스캔 오류"),
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "Not Implemented"),

    // Application
    NOT_AUTHORIZED_ACCESS(HttpStatus.FOUND, "인증이 필요한 곳에 접근중입니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "해당 경로에 대해 요청 메서드를 지원하지 않습니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "해당 페이지를 찾을 수 없습니다."),
    AUTHENTICATION_FAILED(HttpStatus.FOUND, "인증에 실패하였습니다."),
    NO_ARTICLE_DATA(HttpStatus.NOT_FOUND, "Article 데이터가 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "요청 메서드와 URL은 같지만, 잘못된 방식으로 요청하였습니다."),
    REGISTRATION_FIELD_ERROR(HttpStatus.BAD_REQUEST, "회원 가입 중 입력 필드를 잘못 입력하셨습니다.");

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
