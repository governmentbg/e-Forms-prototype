package com.soahouse.adminfaces.template.util;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.servlet.ServletException;

public final class Exceptions {

    // Constructors ---------------------------------------------------------------------------------------------------

    private Exceptions() {
        // Hide constructor.
    }

    // Utility --------------------------------------------------------------------------------------------------------

    /**
     * Unwrap the nested causes of given exception as long as until it is not an instance of the given type and then
     * return it. If the given exception is already not an instance of the given type, then it will directly be
     * returned. Or if the exception, unwrapped or not, does not have a nested cause anymore, then it will be returned.
     * This is particularly useful if you want to unwrap the real root cause out of a nested hierarchy of
     * {@link ServletException} or {@link FacesException}.
     *
     * @param <T>       The generic throwable type.
     * @param exception The exception to be unwrapped.
     * @param type      The type which needs to be unwrapped.
     * @return The unwrapped root cause.
     */
    public static <T extends Throwable> Throwable unwrap(Throwable exception, Class<T> type) {
        Throwable unwrappedException = exception;

        while (type.isInstance(unwrappedException) && unwrappedException.getCause() != null) {
            unwrappedException = unwrappedException.getCause();
        }

        return unwrappedException;
    }

    /**
     * Unwrap the nested causes of given exception as long as until it is not an instance of {@link FacesException}
     * (Mojarra) or {@link ELException} (MyFaces) and then return it. If the given exception is already not an instance
     * of the mentioned types, then it will directly be returned. Or if the exception, unwrapped or not, does not have
     * a nested cause anymore, then it will be returned.
     *
     * @param exception The exception to be unwrapped from {@link FacesException} and {@link ELException}.
     * @return The unwrapped root cause.
     * @since 1.4
     */
    public static Throwable unwrap(Throwable exception) {
        return unwrap(unwrap(exception, FacesException.class), ELException.class);
    }

    /**
     * Returns <code>true</code> if the given exception or one of its nested causes is an instance of the given type.
     *
     * @param <T>       The generic throwable type.
     * @param exception The exception to be checked.
     * @param type      The type to be compared to.
     * @return <code>true</code> if the given exception or one of its nested causes is an instance of the given type.
     */
    public static <T extends Throwable> boolean is(Throwable exception, Class<T> type) {
        Throwable unwrappedException = exception;

        while (unwrappedException != null) {
            if (type.isInstance(unwrappedException)) {
                return true;
            }

            unwrappedException = unwrappedException.getCause();
        }

        return false;
    }

}