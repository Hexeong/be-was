package exception;

public class CustomException extends RuntimeException {
    ErrorCode code;
    String specificMessage;

    public CustomException(ErrorCode code) {
        this.code = code;
        this.specificMessage = "";
    }

    public CustomException(ErrorCode code, String specificMessage) {
        super(specificMessage);
        this.code = code;
        this.specificMessage = specificMessage;
    }

    public ErrorCode getCode() {
        return code;
    }

    public String getSpecificMessage() {
        return specificMessage;
    }
}