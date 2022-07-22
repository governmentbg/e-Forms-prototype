package bg.bulsi.eforms.model.epayment;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class Epayment implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isIndividual = true;
	private String companyName;
	private String companyEik;
	private String firstName;
	private String surName;
	private String familyName;
	private String egn;
	private String serviceUri;
	private String appNumber;
	private String comments;
	private String sumToPay;
	private String iban;
	private String bic;
	private String bankName;
	private String paymentReferenceType;


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getSurName() {
		return surName;
	}


	public void setSurName(String surName) {
		this.surName = surName;
	}


	public String getFamilyName() {
		return familyName;
	}


	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}


	public String getEgn() {
		return egn;
	}


	public void setEgn(String egn) {
		this.egn = egn;
	}


	public String getServiceUri() {
		return serviceUri;
	}


	public void setServiceUri(String serviceUri) {
		this.serviceUri = serviceUri;
	}


	public String getAppNumber() {
		return appNumber;
	}


	public void setAppNumber(String appNumber) {
		this.appNumber = appNumber;
	}


	public String getComments() {
		return comments;
	}


	public void setComments(String comments) {
		this.comments = comments;
	}


	public String getSumToPay() {
		return sumToPay;
	}


	public void setSumToPay(String sumToPay) {
		this.sumToPay = sumToPay;
	}


	public String getIban() {
		return iban;
	}


	public void setIban(String iban) {
		this.iban = iban;
	}


	public String getBic() {
		return bic;
	}


	public void setBic(String bic) {
		this.bic = bic;
	}


	public String getBankName() {
		return bankName;
	}


	public void setBankName(String bankName) {
		this.bankName = bankName;
	}


	public String getPaymentReferenceType() {
		return paymentReferenceType;
	}


	public void setPaymentReferenceType(String paymentReferenceType) {
		this.paymentReferenceType = paymentReferenceType;
	}


	public boolean getIsIndividual() {
		return isIndividual;
	}


	public void setIsIndividual(boolean isIndividual) {
		this.isIndividual = isIndividual;
	}


	public String getCompanyName() {
		return companyName;
	}


	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}


	public String getCompanyEik() {
		return companyEik;
	}


	public void setCompanyEik(String companyEik) {
		this.companyEik = companyEik;
	}


	public String buildFullName() {
		if (isIndividual) {
			String nameSeparator = " ";

			StringBuilder name = new StringBuilder();
			name.append(this.firstName);
			name.append(nameSeparator);
			if (StringUtils.isNotBlank(this.surName)) {
				name.append(this.surName);
				name.append(nameSeparator);
			}
			name.append(this.familyName);

			return name.toString();
		}
		return companyName;
	}


	public String getIdentifier() {
		if (isIndividual) {
			return egn;
		}
		return companyEik;
	}


	@Override
	public String toString() {
		return "Epayment [isIndividual=" + isIndividual + ", companyName=" + companyName + ", companyEik=" + companyEik
				+ ", firstName=" + firstName + ", surName=" + surName + ", familyName=" + familyName + ", egn=" + egn
				+ ", serviceUri=" + serviceUri + ", appNumber=" + appNumber + ", comments=" + comments + ", sumToPay="
				+ sumToPay + ", iban=" + iban + ", bic=" + bic + ", bankName=" + bankName + ", paymentReferenceType="
				+ paymentReferenceType + "]";
	}

}
