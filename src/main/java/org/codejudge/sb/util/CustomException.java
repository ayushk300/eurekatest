package org.codejudge.sb.util;

public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final AppErrorCode appErrorCode;
    private final String reason;

    public CustomException(Exception e) {
        super(e.getMessage(), e);
        this.appErrorCode = AppErrorCode.INTERNAL_SERVER_ERROR;
        this.reason = e.getMessage();
    }

    public CustomException(Exception e, AppErrorCode appErrorCode) {
        super(e.getMessage(), e);
        this.appErrorCode = appErrorCode;
        this.reason = e.getMessage();
    }

    public CustomException(String reason, AppErrorCode appErrorCode) {
        this.appErrorCode = appErrorCode;
        this.reason = reason;
    }

    public CustomException(Exception e, AppErrorCode appErrorCode, String reason) {
        super(e.getMessage(), e);
        this.appErrorCode = appErrorCode;
        this.reason = reason;
    }

    public CustomException(AppErrorCode appErrorCode, String reason) {
        super(reason);
        this.appErrorCode = appErrorCode;
        this.reason = reason;
    }

    public CustomException(String reason) {
        this.reason = reason;
        this.appErrorCode = AppErrorCode.INTERNAL_SERVER_ERROR;
    }

    public AppErrorCode getAppErrorCode() {
        return this.appErrorCode;
    }

    public String getCustomMessage() {
        return this.reason;
    }

    public String toString() {
        return "GenericException [code=" + this.appErrorCode + ", additionalInfo=" + this.reason + "]";
    }
}