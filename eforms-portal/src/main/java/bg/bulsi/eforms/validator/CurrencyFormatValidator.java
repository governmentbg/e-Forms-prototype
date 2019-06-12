package bg.bulsi.eforms.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "curencyFormatValidator")
public class CurrencyFormatValidator implements Validator  { 
	
	public static final String CURENCY_REGEX = "[0-9]+(\\.[0-9]{1,2})?";
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String currNumber = value.toString();
		boolean notValid = true;
  
		FacesMessage msg = new FacesMessage();

		boolean match = currNumber.matches(CURENCY_REGEX);
		boolean restrictContain = currNumber.contains(",");
		
	
		if (restrictContain) {
			notValid = true;
		} else if (match) {
			notValid = false;
		} 
		
		if (notValid) {
			msg = new FacesMessage("Невалидна стойност", "Невалидна стойност. Пример: 9.99");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}

    } 

} 