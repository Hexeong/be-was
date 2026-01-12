package exception;

public class CustomException extends RuntimeException {
    ErrorCode code;
    String specificMessage;

    public CustomException(ErrorCode code) {
        this.code = code;
        this.specificMessage = "";
    }

    public CustomException(ErrorCode code, String specificMessage) {
        super(specificMessage); // [수정] super가 가장 먼저 와야 합니다.
        this.code = code;
        this.specificMessage = specificMessage;
    }
}