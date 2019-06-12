package bg.bulsi.eforms.converters;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bg.bulsi.eforms.jsf.util.FacesUtils;
import bg.bulsi.eforms.model.epayment.VwDepartmentAisClients;
import bg.bulsi.eforms.repository.epayment.VwDepartmentAisClientsRepository;

@Component("vwDepartmentAisClientsConverter")
public class VwDepartmentAisClientsConverter implements Converter, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private VwDepartmentAisClientsRepository vwDepartmentAisClientsRepository;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if ((value == null) || value.isEmpty() || FacesUtils.isDummySelectItem(component, value)) {
			return null;
		}
		return vwDepartmentAisClientsRepository.findByClientid(value);
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object value) {
		if (value != null && value instanceof VwDepartmentAisClients) {
			VwDepartmentAisClients vv = (VwDepartmentAisClients) value;
			return vv.getClientid();
		}
		return null;
	}

}
