package exeception;

public class CustomException extends RuntimeException {
    ErrorCode code;
    public CustomException(ErrorCode code) {
        this.code = code;
    }
}
