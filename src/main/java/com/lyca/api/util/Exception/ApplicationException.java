/**
 * 
 */
package com.lyca.api.util.Exception;

import com.lyca.api.util.Errors;

/**
 * @author Krishna
 *
 */
public class ApplicationException extends Exception {

    /** Serial version id. */
    private static final long serialVersionUID = 1L;

    /**
     * Errors object to get all the errors.
     */
    private Errors errors;

    /**
     * Constructor to display error message for ApplicationException.
     *
     * @param errors to be displayed as exception message.
     */
    public ApplicationException(Errors errors) {
        this.errors = errors;
    }

    /**
     * Get the errors.
     *
     * @return errors object
     */
    public Errors getErrors() {
        return this.errors;
    }

}