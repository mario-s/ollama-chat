package org.ollama.ui;

final class Utils {

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
