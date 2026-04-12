package org.ollama.ui;

final class ExceptionUtil {

    /**
     * Returns the message from the cause, if the cause has no message it will return the class name.
     * @param ex the top exception
     * @return message from the exception
     */
    static String getCauseMessage(Throwable ex) {
        Throwable cause = ExceptionUtil.getCause(ex);
        String msg = cause.getMessage();
        msg = (msg == null || msg.isEmpty()) ? cause.getClass().getName() : msg;
        return msg;
    }

    /**
     * Traverses through the causes and finds the first one.
     * @param ex the top exception
     * @return the root exception
     */
    static Throwable getCause(Throwable ex) {
        Throwable previous = null;
        Throwable cause = ex.getCause();
        while (cause != null) {
            previous = cause;
            cause = cause.getCause();
        }
        return (previous != null) ? previous : ex;
    }
}
