package org.codejudge.sb.util;

public enum AppErrorCode {
    BAD_REQUEST(400, "failure"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_FOUND(404, "Not found");

    private int statusCode;
    private String status;

    private AppErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.status = message;
    }

    public String getMessage() {
        return this.status;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String toString() {
        return this.status;
    }
}