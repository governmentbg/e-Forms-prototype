package bg.bulsi.eforms.model.autofill.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import bg.bulsi.eforms.model.autofill.TblAutoFillForms;

public interface TblAutoFillFormsRepository extends JpaRepository<TblAutoFillForms, Integer> {

	@Query("SELECT t FROM TblAutoFillForms t WHERE t.sessionId = :sessionId AND t.personId = :personId "
			+ "AND t.formId = :formId AND t.dateFormFill IS NOT NULL AND t.sendetToPerson IS NULL ORDER BY t.dateReg DESC")
	List<TblAutoFillForms> findAutoFilledForms(@Param("sessionId") String sessionId, 
			@Param("personId") String personId,
			@Param("formId") String formId);
}
