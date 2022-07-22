package bg.bulsi.eforms.auth;

import java.io.Serializable;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;

import bg.bulsi.eauth.EAuthSamlGenerator;
import bg.bulsi.eauth.EAuthSamlParser;
import bg.bulsi.eauth.exception.EAuthProcessException;
import bg.bulsi.eauth.model.AuthData;
import bg.bulsi.eauth.saml.authnrequest.dom.AuthRqParams;
import bg.bulsi.eauth.saml.response.AuthenticationUseCase;
import bg.bulsi.eforms.jsf.util.FacesUtils;
import bg.bulsi.eforms.model.auth.EauthUserDetails;
import bg.bulsi.eforms.model.auth.USER_TYPE;

@Controller("eauthController")
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class EauthController implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger log = LoggerFactory.getLogger(getClass());

	private AuthRqParams authRqParams;


	public String gotoEauth() {
		return "eauth/eauthRedirector.xhtml?faces-redirect=true";
	}


	public void generateSaml() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			try {
				authRqParams = EAuthSamlGenerator.generateAuthRqParams();
			} catch (EAuthProcessException e) {
				e.printStackTrace();
			}
		}
	}


	public void processSamlResponse() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap();
			String samlRsBase64 = params.get("SAMLResponse");
			String relayStateBase64 = params.get("RelayState");

			AuthData authData = EAuthSamlParser.extractUserDetailsFromSamlRs(samlRsBase64, relayStateBase64);
			log.info(authData.toString());

			if (authData.getStatus() == AuthenticationUseCase.AUTHENTICATED_SUCCESSFULLY) {
				EauthUserDetails user = new EauthUserDetails(	authData.getUserData().getName(),
																authData.getUserData().getEmail(),
																authData.getUserData().getEgn(), USER_TYPE.INDIVIDUAL);

				HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
						.getSession(true);
				session.setAttribute("EAUTH_USER", user);

				String epdaeuQueryString = (String) session.getAttribute("epdaeuQueryString");
				if (StringUtils.isNotBlank(epdaeuQueryString)) {
					session.removeAttribute("epdaeuQueryString");
					FacesUtils.redirect("/app/epdaeu-service.xhtml?" + epdaeuQueryString);
				} else {
					FacesUtils.redirect("/app/service.xhtml");
				}
			} else {
				SecurityContextHolder.getContext().setAuthentication(null);
			}
		}
	}


	public AuthRqParams getAuthRqParams() {
		return authRqParams;
	}


	public void setAuthRqParams(AuthRqParams authRqParams) {
		this.authRqParams = authRqParams;
	}
}
