package bg.bulsi.eforms.epayment;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.datacontract.schemas._2004._07.edelivery_common.DcMessageDetails;
import org.datacontract.schemas._2004._07.edelivery_common.EProfileType;
import org.datacontract.schemas._2004._07.edelivery_common.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;
import org.tempuri.IEDeliveryIntegrationService;

import bg.bulsi.edelivery.client.EdeliveryClient;
import bg.bulsi.eforms.auth.UserController;
import bg.bulsi.eforms.jsf.util.FacesMessageSeverity;
import bg.bulsi.eforms.jsf.util.FacesUtils;
import bg.bulsi.eforms.model.epayment.Epayment;
import bg.bulsi.eforms.model.epayment.VwDepartmentAisClients;
import bg.bulsi.epay.exception.ServiceDataException;
import bg.bulsi.epay.resteasy.resquest.RestRequestImpl;
import bg.bulsi.epay.util.Util;
import bg.bulsi.epay.xjc.services.ApplicantUinTypeId;
import bg.bulsi.epay.xjc.services.payment_json.request.PaymentRequestJson;
import bg.bulsi.epay.xjc.services.payment_json.response.PaymentResponseJson;

@Controller("epaymentController")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class EpaymentController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(EpaymentController.class);

	@Autowired
	private UserController userController;

	private List<VwDepartmentAisClients> departments;
	private VwDepartmentAisClients selectedDepartment;

	private Epayment epayment = new Epayment();
	private PaymentResponseJson response = new PaymentResponseJson();
	private String aisPaymentID = ""; // TODO get

	@PostConstruct
	public void init() {
		departments = userController.getAdmin().getEserviceadminuser().getDepartments();
		if (departments != null && !departments.isEmpty()) {
			selectedDepartment = departments.get(0);
			populatePaymentData();
		}
	}

	public void populatePaymentData() {
		if (selectedDepartment != null) {
			epayment.setIban(selectedDepartment.getServiceprovideriban());
			epayment.setBic(selectedDepartment.getServiceproviderbic());
			epayment.setBankName(selectedDepartment.getServiceproviderbank());
		}
	}

	public void registerPayment() {
		RestRequestImpl ws = null;
		try {
			ws = new RestRequestImpl();
		} catch (ServiceDataException e) {
			String msg = e.getMessage();
			log.error(msg, e);
			FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR, msg);
		}

		PaymentRequestJson rqJson = fillPaymentResponseJson(epayment);

		try {
			response = ws.paymentJSON(rqJson, getSelectedDepartment().getClientid(),
					getSelectedDepartment().getSecretkey());
		} catch (UnsupportedEncodingException | ServiceDataException e) {
			String msg = e.getMessage();
			log.error(msg, e);
			FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR, msg);
		}

		String msg;
		FacesMessageSeverity severity;
		if (response != null) {
			msg = response.toString();
			severity = FacesMessageSeverity.WARN;

			log.debug(msg);

			if (response.getAcceptedReceiptJson() != null) {
				severity = FacesMessageSeverity.INFO;
				String paymentCode = getPaymentCodeWs(ws, response);
				if (paymentCode == null) {
					FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR, "Възникна проблем при извличане на код за плащане");
					return;
				}
				registerMsgInEDelivery(paymentCode);
				epayment = new Epayment();
				populatePaymentData();
			} else if (response.getUnacceptedReceiptJson() != null) {
				severity = FacesMessageSeverity.ERROR;
			}

			FacesUtils.addGlobalMessage(severity, msg);
		} else {
			msg = "Проблем при комуникация с отдалечен сървър";
			severity = FacesMessageSeverity.FATAL;
			FacesUtils.addGlobalMessage(severity, msg);
		}

	}

	private String getPaymentCodeWs(RestRequestImpl ws, PaymentResponseJson response) {
		String code = null;
		try {
			code = ws.paymentCode(response.getAcceptedReceiptJson().getId(), getSelectedDepartment().getClientid(),
					getSelectedDepartment().getSecretkey());
			log.info("Received payment code {} for payment with id {}", code,
					response.getAcceptedReceiptJson().getId());
		} catch (UnsupportedEncodingException | ServiceDataException e) {
			log.error("Error while getting payment code from ws", e);
		}
		return code;
	}

	private void registerMsgInEDelivery(String paymentCode) {
		try {
			String msg = "По заявената от Вас услуга {0} дължите {1} лева. Заявената сума можете да видите в средата на плащане https://pay.egov.bg/. Код за плащане {2}";

			ObjectFactory of = new ObjectFactory();
			IEDeliveryIntegrationService wsEdelivery = EdeliveryClient.initWs();

			DcMessageDetails message = of.createDcMessageDetails();
			message.setTitle(of.createDcMessageTitle("Дължимо плащане"));
			message.setMessageText(of.createDcMessageDetailsMessageText(
					MessageFormat.format(msg, epayment.getAppNumber(), epayment.getSumToPay(), paymentCode)));

			EProfileType receiverType = epayment.getIsIndividual() ? EProfileType.PERSON : EProfileType.LEGAL_PERSON;
			String receiverUniqueIdentifier = epayment.getIsIndividual() ? epayment.getEgn() : epayment.getCompanyEik();

			Integer id = wsEdelivery.sendMessage(message, receiverType, receiverUniqueIdentifier, "", "", "", "");
			if (id == null) {
				FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
						"Възникна грешка при регистриране на уведомление за задълженото лице в еВръчване");
			} else {
				log.info("Registered due payment message in eDelivery with id {} and payment code {}", id, paymentCode);
			}

		} catch (bg.egov.edelivery.services.integration.exception.ServiceDataException e) {
			log.error("Error while registering message for due payment in eDelivery for app with number "
					+ epayment.getAppNumber(), e);
			FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
					"Възникна грешка при регистриране на уведомление за задълженото лице");
		}
	}

	public Epayment getEpayment() {
		return epayment;
	}

	public void setEpayment(Epayment epayment) {
		this.epayment = epayment;
	}

	private PaymentRequestJson fillPaymentResponseJson(Epayment epayment) {

		// XXX Clear or update
		PaymentRequestJson rqJson = new PaymentRequestJson();

		// appNumber;
		rqJson.setPaymentReferenceNumber(epayment.getAppNumber());

		// serviceUri;
		// rqJson.setAdministrativeServiceUri(epayment.getServiceUri());

		rqJson.setAisPaymentId(this.aisPaymentID);

		rqJson.setApplicantName(epayment.buildFullName());
		rqJson.setApplicantUin(epayment.getIdentifier());
		rqJson.setApplicantUinTypeId(String.valueOf(
				epayment.getIsIndividual() ? ApplicantUinTypeId.EGN.getCode() : ApplicantUinTypeId.BULSTAT.getCode()));

		// sumToPay;
		rqJson.setPaymentAmount(epayment.getSumToPay());

		// comments;
		rqJson.setPaymentReason(epayment.getComments());

		// FIXME Fill if exist
		// rqJson.setAdministrativeServiceNotificationURL();
		// rqJson.setAdministrativeServiceSupplierUri();
		// rqJson.setAdditionalInformation(epayment.getComments());

		rqJson.setPaymentTypeCode("paylogin"); // bg.bulsi.module.epay.enums.PaymentType

		rqJson.setCurrency("BGN");

		rqJson.setServiceProviderName(
				getSelectedDepartment().getDepartmentname() + " - " + getSelectedDepartment().getAisname());
		rqJson.setServiceProviderBank(epayment.getBankName());
		rqJson.setServiceProviderBIC(epayment.getBic());
		rqJson.setServiceProviderIBAN(epayment.getIban());

		rqJson.setPaymentReferenceDate(Util.sdf.format(new Date()));
		rqJson.setPaymentReferenceType(epayment.getPaymentReferenceType());
		// rqJson.setExpirationDate("2018-12-30");

		return rqJson;
	}

	public VwDepartmentAisClients getSelectedDepartment() {
		return selectedDepartment;
	}

	public void setSelectedDepartment(VwDepartmentAisClients selectedDepartment) {
		this.selectedDepartment = selectedDepartment;
	}

	public List<VwDepartmentAisClients> getDepartments() {
		return departments;
	}

	public void setDepartments(List<VwDepartmentAisClients> departments) {
		this.departments = departments;
	}

}
