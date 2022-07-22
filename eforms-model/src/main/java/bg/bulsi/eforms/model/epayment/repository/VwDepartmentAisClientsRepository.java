package bg.bulsi.eforms.model.epayment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bg.bulsi.eforms.model.epayment.VwDepartmentAisClients;

public interface VwDepartmentAisClientsRepository extends JpaRepository<VwDepartmentAisClients, String> {

	VwDepartmentAisClients findByClientid(String clientId);

}
