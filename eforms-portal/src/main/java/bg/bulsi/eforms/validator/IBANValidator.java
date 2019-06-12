package bg.bulsi.eforms.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

@FacesValidator(value = "ibanValidator")
public class IBANValidator implements Validator  { 
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String ccNumber = value.toString();
  
		FacesMessage msg = new FacesMessage();

		CheckDigit checkDigit = IBANCheckDigit.IBAN_CHECK_DIGIT;
        boolean b = checkDigit.isValid(ccNumber);
        
		if (!b) {
			msg = new FacesMessage("Невалиден номер", "Невалиден IBAN номер");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		} /*else {
			msg = new FacesMessage("Валиден номер", "Валиден номер");
			msg.setSeverity(FacesMessage.SEVERITY_INFO);
			throw new ValidatorException(msg);
		}*/

    } 
  

} 