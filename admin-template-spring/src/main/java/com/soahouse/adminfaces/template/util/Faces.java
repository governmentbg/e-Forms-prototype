package com.soahouse.adminfaces.template.util;

import javax.el.ELContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.servlet.ServletContext;

public final class Faces {

    // Constructors ---------------------------------------------------------------------------------------------------

    private Faces() {
        // Hide constructor.
    }

    public static FacesContext getContext() {
        return FacesContext.getCurrentInstance();
    }

    public static FacesContext getContext(ELContext elContext) {
        return (FacesContext) elContext.getContext(FacesContext.class);
    }

    public static Flash getFlash() {
        return FacesLocal.getFlash(getContext());
    }

    public static boolean hasContext() {
        return getContext() != null;

    }

    /**
     * Returns the servlet context.
     * <p>
     * <i>Note that whenever you absolutely need this method to perform a general task, you might want to consider to
     * submit a feature request to OmniFaces in order to add a new utility method which performs exactly this general
     * task.</i>
     *
     * @return the servlet context.
     * @see ExternalContext#getContext()
     */
    public static ServletContext getServletContext() {
        return FacesLocal.getServletContext(getContext());
    }

}
