package dev.gunho.api.global.exception;

public class EmailSendException extends RuntimeException {


    /**
     * 기본 생성자
     */
    public EmailSendException() {
        super("이메일 발송 중 오류가 발생했습니다.");
    }

    /**
     * 메시지를 포함한 생성자
     * @param message 예외 메시지
     */
    public EmailSendException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인 예외를 포함한 생성자
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 원인 예외만 포함한 생성자
     * @param cause 원인 예외
     */
    public EmailSendException(Throwable cause) {
        super("이메일 발송 중 오류가 발생했습니다.", cause);
    }

}
