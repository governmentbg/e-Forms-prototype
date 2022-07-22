package bg.bulsi.eforms.model.autofill.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import bg.bulsi.eforms.model.autofill.VwAutoFillFormsForUse;

public interface VwAutoFillFormsForUseRepository extends JpaRepository<VwAutoFillFormsForUse, String> {

	Optional<VwAutoFillFormsForUse> findByFormId(String formId);
}
