package bg.bulsi.eforms.model.autofill;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "tblAutoFillForms")
public class TblAutoFillForms implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idAutoFill;

	private String sessionId;

	private String personId;

	private String formId;

	private Date dateReg;

	@Lob
	private byte[] formData;

	// empty string insert for now
	private String filledFormPath;

	// only column not filled by eForms
	private Date dateFormFill;

	private Date sendetToPerson;


	public TblAutoFillForms() {
		// empty
	}


	public TblAutoFillForms(String sessionId, String personId, String formId, Date dateReg, byte[] formData, String filledFormPath) {
		this.sessionId = sessionId;
		this.personId = personId;
		this.formId = formId;
		this.dateReg = dateReg;
		this.formData = formData;
		this.filledFormPath = filledFormPath;
	}


	public Integer getIdAutoFill() {
		return idAutoFill;
	}


	public void setIdAutoFill(Integer idAutoFill) {
		this.idAutoFill = idAutoFill;
	}


	public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	public String getPersonId() {
		return personId;
	}


	public void setPersonId(String personId) {
		this.personId = personId;
	}


	public String getFormId() {
		return formId;
	}


	public void setFormId(String formId) {
		this.formId = formId;
	}


	public Date getDateReg() {
		return dateReg;
	}


	public void setDateReg(Date dateReg) {
		this.dateReg = dateReg;
	}


	public byte[] getFormData() {
		return formData;
	}


	public void setFormData(byte[] formData) {
		this.formData = formData;
	}


	public String getFilledFormPath() {
		return filledFormPath;
	}


	public void setFilledFormPath(String filledFormPath) {
		this.filledFormPath = filledFormPath;
	}


	public Date getDateFormFill() {
		return dateFormFill;
	}


	public void setDateFormFill(Date dateFormFill) {
		this.dateFormFill = dateFormFill;
	}


	public Date getSendetToPerson() {
		return sendetToPerson;
	}


	public void setSendetToPerson(Date sendetToPerson) {
		this.sendetToPerson = sendetToPerson;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((dateReg == null) ? 0 : dateReg.hashCode());
		result = (prime * result) + ((formId == null) ? 0 : formId.hashCode());
		result = (prime * result) + ((idAutoFill == null) ? 0 : idAutoFill.hashCode());
		result = (prime * result) + ((personId == null) ? 0 : personId.hashCode());
		result = (prime * result) + ((sessionId == null) ? 0 : sessionId.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TblAutoFillForms other = (TblAutoFillForms) obj;
		if (dateReg == null) {
			if (other.dateReg != null) {
				return false;
			}
		} else if (!dateReg.equals(other.dateReg)) {
			return false;
		}
		if (formId == null) {
			if (other.formId != null) {
				return false;
			}
		} else if (!formId.equals(other.formId)) {
			return false;
		}
		if (idAutoFill == null) {
			if (other.idAutoFill != null) {
				return false;
			}
		} else if (!idAutoFill.equals(other.idAutoFill)) {
			return false;
		}
		if (personId == null) {
			if (other.personId != null) {
				return false;
			}
		} else if (!personId.equals(other.personId)) {
			return false;
		}
		if (sessionId == null) {
			if (other.sessionId != null) {
				return false;
			}
		} else if (!sessionId.equals(other.sessionId)) {
			return false;
		}
		return true;
	}


	@Override
	public String toString() {
		return "TblAutoFillForms [idAutoFill=" + idAutoFill + ", sessionId=" + sessionId + ", personId=" + personId
				+ ", formId=" + formId + ", dateReg=" + dateReg + "]";
	}

}
