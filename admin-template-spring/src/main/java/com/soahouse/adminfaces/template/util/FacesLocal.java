package com.soahouse.adminfaces.template.util;

import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.servlet.ServletContext;

public final class FacesLocal {
    // Constructors ---------------------------------------------------------------------------------------------------

    private FacesLocal() {
        // Hide constructor.
    }

    public static Flash getFlash(FacesContext context) {
        return context.getExternalContext().getFlash();
    }

    public static ServletContext getServletContext(FacesContext context) {
        return (ServletContext) context.getExternalContext().getContext();
    }

}
