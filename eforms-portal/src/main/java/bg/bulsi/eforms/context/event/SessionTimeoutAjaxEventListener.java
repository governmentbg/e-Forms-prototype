package bg.bulsi.eforms.context.event;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Redirect to login page in case of session timeout and next action is ajax request
 */
public class SessionTimeoutAjaxEventListener implements PhaseListener {

    private static final long serialVersionUID = 1L;
    private Logger log = LoggerFactory.getLogger(getClass().getName());

	@Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        // do nothing
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        FacesContext facesContext = event.getFacesContext();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        
        boolean isAjaxRequest = facesContext.getPartialViewContext().isAjaxRequest();
        String requestURI = request.getRequestURI();
        String loginURL = request.getContextPath() + "/index.xhtml";
        
        if (isAjaxRequest && requestURI != null && loginURL.equals(requestURI)) {
            try {
                log.info("Session expired! Redirect to login on ajax request.");
            	facesContext.getExternalContext().redirect(loginURL);
            } catch (IOException e) {
                throw new FacesException(e);
            }
        }
    }
}

