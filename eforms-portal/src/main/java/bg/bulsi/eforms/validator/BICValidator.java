package bg.bulsi.eforms.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "bicValidator")
public class BICValidator implements Validator  { 
	
	public static final String BIC_REGEX = "[A-Za-z]{4}[A-Za-z]{2}[A-Za-z0-9]{2}([A-Za-z0-9]{3})?";
//	public static final Pattern BIC_PATTERN = Pattern.compile(BIC_REGEX);
	
	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String bicNumber = value.toString();
  
		FacesMessage msg = new FacesMessage();
		
		boolean b = bicNumber.matches(BIC_REGEX);
		
		if (!b) {
			msg = new FacesMessage("Невалиден номер", "Невалиден BIC номер");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		} /*else {
			msg = new FacesMessage("Валиден номер", "Валиден номер");
			msg.setSeverity(FacesMessage.SEVERITY_INFO);
			throw new ValidatorException(msg);
		}*/

    } 

} 