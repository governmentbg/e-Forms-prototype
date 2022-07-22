package bg.bulsi.eforms.model.epayment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bg.bulsi.eforms.model.epayment.Eserviceadminuser;

public interface EserviceadminuserRepository extends JpaRepository<Eserviceadminuser, Integer> {

	Eserviceadminuser findByUsername(String username);
}
