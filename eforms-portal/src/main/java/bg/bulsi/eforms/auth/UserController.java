package bg.bulsi.eforms.auth;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;

import bg.bulsi.eforms.model.auth.EauthUserDetails;
import bg.bulsi.eforms.model.auth.USER_TYPE;
import bg.bulsi.eforms.model.epayment.EpaymentUserDetails;

@Controller("userController")
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class UserController implements Serializable {

	private static final long serialVersionUID = 1L;

	public EauthUserDetails getUser() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		if (securityContext.getAuthentication() != null
				&& securityContext.getAuthentication().getPrincipal() instanceof EauthUserDetails) {
			return (EauthUserDetails) securityContext.getAuthentication().getPrincipal();
		}
		return new EauthUserDetails("anonymous", "", "", USER_TYPE.INDIVIDUAL);
	}

	public EpaymentUserDetails getAdmin() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		if (securityContext.getAuthentication() != null
				&& securityContext.getAuthentication().getPrincipal() instanceof EpaymentUserDetails) {
			return (EpaymentUserDetails) securityContext.getAuthentication().getPrincipal();
		}
		return null;
	}
}
