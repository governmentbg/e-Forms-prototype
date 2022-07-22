package bg.bulsi.eforms.model.autofill;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vwAutoFillFormsForUse")
public class VwAutoFillFormsForUse implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String formId;

	public VwAutoFillFormsForUse() {
		// empty
	}

	public VwAutoFillFormsForUse(String formId) {
		this.formId = formId;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formId == null) ? 0 : formId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VwAutoFillFormsForUse other = (VwAutoFillFormsForUse) obj;
		if (formId == null) {
			if (other.formId != null)
				return false;
		} else if (!formId.equals(other.formId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VwAutoFillFormsForUse [formId=" + formId + "]";
	}

}
