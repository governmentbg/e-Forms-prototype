package bg.bulsi.eforms.auth;

import java.io.Serializable;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;

import bg.bulsi.eforms.jsf.util.FacesUtils;

@Controller("jwtController")
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class JWTController implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger log = LoggerFactory.getLogger(getClass());


	public void redirectToApp() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap();
			String token = params.get("token");
			log.info("TOKEN: " + token);

			FacesUtils.redirect("/app/service.xhtml?token=" + token);
		}
	}
}
