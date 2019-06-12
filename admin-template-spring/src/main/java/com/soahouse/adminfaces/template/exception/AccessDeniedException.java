package com.soahouse.adminfaces.template.exception;


import java.io.Serializable;

//@ApplicationException(rollback = true)
public class AccessDeniedException  extends RuntimeException implements Serializable {

	  public AccessDeniedException() {
    	}

    public AccessDeniedException(Throwable cause) {
        super(cause);
    }

    /**
     *
     * @param msg exception message
     */
    public AccessDeniedException(String msg) {
        super(msg);
    }
    
}
