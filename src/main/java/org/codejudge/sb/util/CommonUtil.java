package org.codejudge.sb.util;

public class CommonUtil {
    public static String getDetailedExceptionMessage(Exception exception) {

        StringBuilder sb = new StringBuilder();
        sb.append(exception);
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            sb.append("\n at ");
            sb.append(stackTraceElement);
        }
        return String.valueOf(sb);
    }
}
