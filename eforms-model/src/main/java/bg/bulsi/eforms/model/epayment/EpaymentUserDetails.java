package bg.bulsi.eforms.model.epayment;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import bg.bulsi.eforms.model.auth.ROLES;
import bg.bulsi.eforms.util.EpaymentUtil;

public class EpaymentUserDetails extends User {

	private static final long serialVersionUID = 1L;

	private Eserviceadminuser eserviceadminuser;

	public EpaymentUserDetails(Eserviceadminuser eserviceadminuser) {
		super(eserviceadminuser.getName(),
				eserviceadminuser.getPasswordhash() + EpaymentUtil.PASS_SEPARATOR + eserviceadminuser.getPasswordsalt(),
				eserviceadminuser.getIsactive(), true, true, true,
				AuthorityUtils.commaSeparatedStringToAuthorityList(ROLES.ROLE_ADMIN.name()));
		this.eserviceadminuser = eserviceadminuser;
	}

	public Eserviceadminuser getEserviceadminuser() {
		return eserviceadminuser;
	}

	public void setEserviceadminuser(Eserviceadminuser eserviceadminuser) {
		this.eserviceadminuser = eserviceadminuser;
	}

}
