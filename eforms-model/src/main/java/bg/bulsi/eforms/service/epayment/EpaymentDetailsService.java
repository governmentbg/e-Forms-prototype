package bg.bulsi.eforms.service.epayment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import bg.bulsi.eforms.model.epayment.EpaymentUserDetails;
import bg.bulsi.eforms.model.epayment.Eserviceadminuser;
import bg.bulsi.eforms.repository.epayment.EserviceadminuserRepository;

@Service
public class EpaymentDetailsService implements UserDetailsService {

	@Autowired
	private EserviceadminuserRepository eserviceadminuserRepository;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Eserviceadminuser user = eserviceadminuserRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return new EpaymentUserDetails(user);
	}

}
