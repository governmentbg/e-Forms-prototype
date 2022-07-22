package bg.bulsi.eforms.model.epdaeu;

public enum AuditCsvEvent {

	REGISTER_APPLICATION("Подаване на заявление"),
	REGISTER_PAYMENT("Регистриране на плащане");

	private String event;


	private AuditCsvEvent(String event) {
		this.event = event;
	}


	public String getEvent() {
		return event;
	}

}
