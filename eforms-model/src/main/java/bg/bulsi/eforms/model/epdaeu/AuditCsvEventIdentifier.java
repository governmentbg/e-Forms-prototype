package bg.bulsi.eforms.model.epdaeu;

public enum AuditCsvEventIdentifier {

	APPLICATION_NUMBER("Номер заявление"),
	PAYMENT_CODE("Код за плащане");

	private String identifier;


	private AuditCsvEventIdentifier(String identifier) {
		this.identifier = identifier;
	}


	public String getIdentifier() {
		return identifier;
	}

}
