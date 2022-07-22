package bg.bulsi.eforms.app;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBElement;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.datacontract.schemas._2004._07.edelivery_common.ArrayOfDcDocument;
import org.datacontract.schemas._2004._07.edelivery_common.ArrayOfDcSubjectShortInfo;
import org.datacontract.schemas._2004._07.edelivery_common.DcDocument;
import org.datacontract.schemas._2004._07.edelivery_common.DcLegalPersonRegistrationInfo;
import org.datacontract.schemas._2004._07.edelivery_common.DcMessageDetails;
import org.datacontract.schemas._2004._07.edelivery_common.DcPersonRegistrationInfo;
import org.datacontract.schemas._2004._07.edelivery_common.DcSubjectShortInfo;
import org.datacontract.schemas._2004._07.edelivery_common.EProfileType;
import org.datacontract.schemas._2004._07.edelivery_common.ObjectFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;
import org.tempuri.IEDeliveryIntegrationService;
import org.xml.sax.SAXException;

import bg.bulsi.eforms.auth.UserController;
import bg.bulsi.eforms.jsf.model.EDeliveryProfile;
import bg.bulsi.eforms.jsf.util.FacesMessageSeverity;
import bg.bulsi.eforms.jsf.util.FacesUtils;
import bg.bulsi.eforms.jsf.util.Util;
import bg.bulsi.eforms.model.auth.EauthUserDetails;
import bg.bulsi.eforms.model.auth.USER_TYPE;
import bg.bulsi.eforms.model.autofill.TblAutoFillForms;
import bg.bulsi.eforms.model.autofill.VwAutoFillFormsForUse;
import bg.bulsi.eforms.model.autofill.repository.TblAutoFillFormsRepository;
import bg.bulsi.eforms.model.autofill.repository.VwAutoFillFormsForUseRepository;
import bg.bulsi.eforms.model.epdaeu.AuditCsvEvent;
import bg.bulsi.eforms.model.epdaeu.AuditCsvEventIdentifier;
import bg.bulsi.eforms.model.epdaeu.FileDownload;
import bg.bulsi.eforms.model.epdaeu.InstitutionServices;
import bg.bulsi.eforms.model.epdaeu.ServiceDocument;
import bg.bulsi.epay.util.DateUtil;
import bg.bulsi.epdaeu.model.ResponseModel;
import bg.bulsi.epdaeu.model.Status;
import bg.bulsi.epdaeu.resteasy.model.xjc.service_addition.ServiceAddition;
import bg.bulsi.epdaeu.resteasy.model.xjc.service_addition.ServiceAddition.Documents;
import bg.bulsi.epdaeu.resteasy.model.xjc.service_addition.ServiceAddition.Documents.Document;
import bg.bulsi.epdaeu.resteasy.model.xjc.service_addition.ServiceAddition.Files;
import bg.bulsi.epdaeu.resteasy.model.xjc.service_addition.ServiceAddition.Files.File;
import bg.bulsi.epdaeu.resteasy.model.xjc.services.Services.Service;
import bg.bulsi.modules.util.AppUtil;
import bg.egov.edelivery.services.integration.exception.ServiceDataException;

@Controller("serviceController")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class ServiceController implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger log = LoggerFactory.getLogger(getClass().getName());

	@Value("${epdaeu.service.test.txt}")
	private String testServiceTxt;

	@Value("${epdaeu.file.size.in.bytes}")
	private String fileSizeInBytes;

	@Value("${epdaeu.file.mime.types}")
	private String fileMimeTypes;

	@Value("${pdf.form.signature.xml.path}")
	private String xmlTag;
	@Value("${pdf.form.signature.cert.subject.dn}")
	private String subjectDn;
	@Value("${pdf.form.signature.cert.issuer.dn}")
	private String issuerDn;
	@Value("${pdf.form.signature.cert.serial.num}")
	private String serialNum;

	@Value("${pdf.form.signature.user.xml.path}")
	private String userXmlTag;

	private FileDownload selectedFileDownload;

	@Autowired
	private InstitutionsServicesLoader institutionsServicesLoader;
	@Autowired
	private UserController userController;

	private List<Service> services;
	private InstitutionServices selectedInstitution;
	private Service selectedService;
	private ServiceAddition selectedServiceAddition;

	private List<ServiceDocument> selectedServiceDocuments;
	private List<ServiceDocument> selectedServiceFiles;
	private ServiceDocument selectedServiceAddDocument;

	private String epdaeuMsg;

	@Autowired
	private IEDeliveryIntegrationService ws;

	private List<EDeliveryProfile> profiles;
	private EDeliveryProfile selectedProfile;
	private String selectedProfileIdentifier;
	private String operatorEGN;

	@Autowired
	private TblAutoFillFormsRepository autofillRepository;

	@Autowired
	private VwAutoFillFormsForUseRepository autofillFormsForUseRepository;

	private static final long TIMEOUT_MILLIS = 60 * 1000; // 1min
	private static final long WAIT_MILLIS = 1000;

	public void selectService() {
		String url = loadServiceData();
		FacesUtils.redirect(url);
	}


	public String loadServiceData() {
		ResponseModel<ServiceAddition> rs = institutionsServicesLoader.getWsClient()
				.getServiceInfoResult(selectedService.getSupplierOID(), selectedService.getArId(), null);
		if (rs.getStatusCode() != Status.STATUS_OK) {
			FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
					"Възникна грешка при извличане на данни за услугата моля опитайте отново!");
			return null;

		}
		selectedServiceAddition = rs.getContent();

		getSelectedServiceDocuments().clear();
		getSelectedServiceFiles().clear();
		loadFiles(selectedServiceAddition.getFiles());
		loadDocuments(selectedServiceAddition.getDocuments());

		loadEdeliveryProfiles();

		return "/app/application.xhtml?faces-redirect=true";
	}


	public void registerApp() throws ServiceDataException {
		String registrationId = null;
		String msg = null;
		EauthUserDetails user = null;
		try {
			if (!validateApplication()) {
				user = userController.getUser();
				registrationId = sendDocs(user);
			}
		} catch (Exception e) {
			msg = e.getMessage();
			log.error("Error while Edelivery document registration", e);
		}

		if (registrationId != null) {
			FacesUtils.addGlobalMessage(FacesMessageSeverity.INFO,
					"Успешно регистрирано заявление с номер: " + registrationId);
			log.debug("audit csv: ", DateUtil.timestamp.format(new Date()),
					AuditCsvEvent.REGISTER_APPLICATION.getEvent(),
					AuditCsvEventIdentifier.APPLICATION_NUMBER.getIdentifier(), registrationId,
					user.getFirstName() + " " + user.getLastName(), user.getIdentity(),
					selectedServiceAddition.getSupplier(), selectedServiceAddition.getShortName());
			FacesUtils.redirect("/app/application_success.xhtml?id=" + registrationId);
		} else if ((registrationId == null) && !FacesUtils.getFacesContext().isValidationFailed()) {
			FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
					MessageFormat.format("Възникна проблем при регистриране на заявление {0}, моля опитайте отново",
							msg));
		}
	}


	private boolean validateApplication() {
		boolean hasAttachedFiles = false;
		for (ServiceDocument doc : selectedServiceDocuments) {
			validateDoc(doc);
			validatePdf(doc, false);
		}

		for (ServiceDocument doc : selectedServiceFiles) {
			validateDoc(doc);
			validatePdf(doc, true);
			if (doc.getFile().getSize() > 0) {
				hasAttachedFiles = true;
			}
		}
		if (!hasAttachedFiles) {
			FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
					"Необходимо е да прикачите поне един иницииращ документ");
		}
		validateDoc(selectedServiceAddDocument);
		validateEdeliveryProfile();
		return FacesUtils.getFacesContext().isValidationFailed();
	}


	private void validateDoc(ServiceDocument doc) {
		if (doc.getFile().getSize() != 0) {

			String docFileName = doc.getFileName() != null ? doc.getFileName() : doc.getFile().getFileName();
			String docFileNameBaseName = docFileName.split("\\.")[0];
			if ((docFileName != null) && !doc.getFile().getFileName().contains(docFileNameBaseName)) {
				FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
						"Файл " + doc.getFile().getFileName() + " е с различен от шаблона който е свален");
			}

			if (!fileMimeTypes.contains(doc.getFile().getContentType())) {
				FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
						"Файл " + doc.getFile().getFileName() + " е в формат който не е допустим");
			}

			if (doc.getFile().getSize() > Long.valueOf(fileSizeInBytes)) {
				FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
						"Файл " + doc.getFile().getFileName() + " е с по-голям размер от допустимото");
			}
		}

		if ((doc.getFile().getSize() == 0) && doc.isRequired()) {
			FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
					"Моля прикачете файл към документ: " + doc.getDescription());
		}
	}


	private String sendDocs(EauthUserDetails user) throws ServiceDataException {

		ObjectFactory of = new ObjectFactory();

		ArrayOfDcDocument arrayOfDocs = of.createArrayOfDcDocument();

		for (ServiceDocument doc : selectedServiceFiles) {
			extractPdfNumber(doc, true);
			addDocumentInApplication(doc, arrayOfDocs, of);
		}
		for (ServiceDocument doc : selectedServiceDocuments) {
			addDocumentInApplication(doc, arrayOfDocs, of);
		}

		addDocumentInApplication(selectedServiceAddDocument, arrayOfDocs, of);

		JAXBElement<ArrayOfDcDocument> jaxbArrayOfDocs = of.createDcMessageDetailsAttachedDocuments(arrayOfDocs);

		DcMessageDetails messageDetails = of.createDcMessageDetails();
		messageDetails.setAttachedDocuments(jaxbArrayOfDocs);
		messageDetails.setTitle(of.createDcMessageTitle(testServiceTxt + selectedServiceAddition.getShortName()));

		// extract administration eik from pdf
		String eik = selectedServiceAddition.getSupplierEIK();
		try {
			String eikPdf = AppUtil.getAdministrationEIK(arrayOfDocs.getDcDocument().get(0).getContent().getValue());
			if (StringUtils.isNotBlank(eikPdf)) {
				eik = eikPdf;
			}
		} catch (IOException | ParserConfigurationException | SAXException ignored) {
			log.error("Error while extracting administration eik from pdf", ignored);
		}

		// case selected profile: type -> AppUtil.getProfileType(selectedProfile.getType())
		Integer registrationId = selectedProfile == null
				? ws.sendMessageOnBehalfOf(messageDetails, AppUtil.getUserType(user), user.getIdentity(),
						null, user.getEmail(), user.getFirstName(), user.getLastName(), EProfileType.INSTITUTION, eik,
						selectedServiceAddition.getServiceOID(), null)
				: ws.sendMessageOnBehalfOf(messageDetails, selectedProfile.getType(), selectedProfile.getIdentifier(),
						null, user.getEmail(), selectedProfile.getName(), null, EProfileType.INSTITUTION, eik,
						selectedServiceAddition.getServiceOID(), user.getUserType() == USER_TYPE.INDIVIDUAL ? operatorEGN : selectedProfile.getIdentifier());

		String pdfAppNumber = arrayOfDocs.getDcDocument().get(0).getDocumentRegistrationNumber().getValue();
		log.info("Registered request in eDelivery with id {} for user {} and pdf file number {}", registrationId,
				user.getUsername(), pdfAppNumber);

		return pdfAppNumber;
	}


	private void addDocumentInApplication(ServiceDocument doc, ArrayOfDcDocument arrayOfDocs, ObjectFactory of) {
		if (doc.getFile().getSize() != 0) {

			DcDocument dcDocument = of.createDcDocument();
			dcDocument.setDocumentName(of.createDcDocumentDocumentName(doc.getFile().getFileName()));
			dcDocument.setContentType(of.createDcDocumentContentType(doc.getFile().getContentType()));
			dcDocument.setContent(of.createDcDocumentContent(doc.getFile().getContents()));
			dcDocument.setId(0);

			if (StringUtils.isNotBlank(doc.getPdfNumber())) {
				dcDocument.setDocumentRegistrationNumber(
						of.createDcDocumentDocumentRegistrationNumber(doc.getPdfNumber()));
			}

			arrayOfDocs.getDcDocument().add(dcDocument);
		}
	}


	private void validatePdf(ServiceDocument doc, boolean isPrimaryDocType) {
		if (doc.getFile().getSize() > 0) {

			boolean isUserSignatureRequired = false;

			try {
				isUserSignatureRequired = AppUtil.isUserSignatureRequired(doc.getFile().getContents());
			} catch (IOException | ParserConfigurationException | SAXException e) {
				FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
						"Проблем при обработка на файл с име " + doc.getFile().getFileName());
				log.error("Error while extracting is user signature reqired", e);
			}

			if (isUserSignatureRequired) {
				//
				try {
					if (/* doc.isRequired() && */ !AppUtil.isFileContainsSignature(doc.getFile().getContents())) {
						FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
								"Файл " + doc.getFile().getFileName() + " не съдържа подпис");
					}
				} catch (IOException e) {
					FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
							"Възникна проблем при проверка за наличие на подпис на файл "
									+ doc.getFile().getFileName());
					log.error("Error while checking is pdf signed", e);
				}
			}

			//
			if (isPrimaryDocType) {
				try {
					if (/* doc.isRequired() && */AppUtil
							.extractFileApplicationNumber(doc.getFile().getContents()) == null) {
						FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
								"Възникна проблем при извличане на номер на заявление за файл: "
										+ doc.getFile().getFileName());
					}
				} catch (IOException | ParserConfigurationException | SAXException e) {
					FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
							"Възникна проблем при извличане на номер на заявление за файл: "
									+ doc.getFile().getFileName());
					log.error("Error while extracting pdf form number", e);
				}
			}

			//
			try {
				if (!AppUtil.isFileSignedByDaeu(doc.getFile().getContents(), xmlTag, subjectDn, issuerDn, serialNum)) {
					FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
							"Файл " + doc.getFile().getFileName() + " не съдържа еПечат на ДАЕУ");
				}
			} catch (IOException | GeneralSecurityException e) {
				FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
						"Възникна грешка при проверка за наличие на еПечат на ДАЕУ за файл "
								+ doc.getFile().getFileName());
				log.error("Error while checking is pdf signed by DAEU ", e);
			}
		}
	}


	private void extractPdfNumber(ServiceDocument doc, boolean isPrimaryDocType) {
		if ((doc.getFile().getSize() > 0) && isPrimaryDocType) {
			try {
				String pdfAppNumber = AppUtil.extractFileApplicationNumber(doc.getFile().getContents());
				doc.setPdfNumber(pdfAppNumber);
			} catch (IOException | ParserConfigurationException | SAXException e) {
				FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR,
						"Възникна проблем при извличане на номер на заявление за файл: " + doc.getFile().getFileName());
				log.error("Error while extracting pdf form application number", e);
			}
		}
	}


	public void loadServiceFromEpdaeu() {
		selectedService = null;
		Map<String, String> rqParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String institutionOID = rqParams.get("institutionOID");
		String arId = rqParams.get("arId");
		log.info("arId: {}", arId);
		log.info("institutionOID: {}", institutionOID);

		search: for (InstitutionServices inst : getInstitutionsServices()) {

			for (Service serv : inst.getServices()) {
				if ((serv != null) && StringUtils.isNotBlank(serv.getSupplierOID())
						&& serv.getSupplierOID().equals(institutionOID) && StringUtils.isNotBlank(serv.getArId())
						&& serv.getArId().equals(arId)) {
					selectedService = serv;
					break search;
				}
			}

		}

		if (selectedService == null) {
			epdaeuMsg = "Услугата не е намерена!"; // не се запазва след
													 // редирект
			FacesUtils.redirect("/app/service.xhtml");
		} else {
			String url = loadServiceData();
			FacesUtils.redirect(url);
		}
	}


	private void loadFiles(Files files) {
		if (files != null) {
			for (File file : files.getFile()) {
				ServiceDocument sd = new ServiceDocument();
				sd.setName(file.getName());
				sd.setDescription(file.getDescription());
				sd.setUrl(file.getLink());
				sd.setRequired(false);
				sd.setMimeType(file.getMimeType());
				sd.setSize(file.getSize());
				sd.setFileName(Util.getFileNameFromUrl(file.getLink()));
				getSelectedServiceFiles().add(sd);
			}
		}
	}


	private void loadDocuments(Documents documents) {
		if (documents != null) {
			for (Document doc : documents.getDocument()) {
				ServiceDocument sd = new ServiceDocument();
				sd.setName(doc.getName());
				sd.setDescription(doc.getDescription());
				sd.setUrl(doc.getLink());
				sd.setRequired(false);
				sd.setFileName(Util.getFileNameFromUrl(doc.getLink()));
				getSelectedServiceDocuments().add(sd);
			}
		}
	}


	private void loadEdeliveryProfiles() {
		profiles = new ArrayList<>();
		selectedProfile = null;
		selectedProfileIdentifier = null;
		operatorEGN = null;

		String identifier = userController.getUser().getIdentity(); // test egn: "8504026412"; test eik: 131423631
		Boolean hasRegistration = null;
		JAXBElement<ArrayOfDcSubjectShortInfo> jaxbProfiles = null;

		USER_TYPE userType = userController.getUser().getUserType();
		if (userType == USER_TYPE.INDIVIDUAL) {
			// Физ. лице
			DcPersonRegistrationInfo personRegInfo = ws.checkPersonHasRegistration(identifier);
			hasRegistration = personRegInfo.isHasRegistration();
			log.info("person with id [{}] has registration in eDelivery: [{}]", identifier, hasRegistration);

			if (hasRegistration) {
				operatorEGN = personRegInfo.getPersonIdentificator().getValue();
				jaxbProfiles = personRegInfo.getAccessibleProfiles();
				// add profile for Person first
				profiles.add(
						new EDeliveryProfile(	EProfileType.PERSON,
												personRegInfo.getName().getValue(),
												personRegInfo.getPersonIdentificator().getValue()
											));
			}
		} else {
			// Юрид. лице: USER_TYPE.LEGAL
			DcLegalPersonRegistrationInfo legalRegInfo = ws.checkLegalPersonHasRegistration(identifier);
			hasRegistration = legalRegInfo.isHasRegistration();
			log.info("legal with id [{}] has registration in eDelivery: [{}]", identifier, hasRegistration);

			if (hasRegistration) {
				jaxbProfiles = legalRegInfo.getProfilesWithAccess();
			}
		}

		if (jaxbProfiles != null) {
			ArrayOfDcSubjectShortInfo arrayOfEdeliveryProfiles = jaxbProfiles.getValue();
			List<DcSubjectShortInfo> edeliveryProfiles = arrayOfEdeliveryProfiles.getDcSubjectShortInfo();
			for (DcSubjectShortInfo profile : edeliveryProfiles) {
				profiles.add(
						new EDeliveryProfile(	profile.getProfileType(), profile.getName().getValue(),
												profile.getProfileType() == EProfileType.PERSON ? profile.getEGN().getValue() : profile.getEIK().getValue()));
			}
		}

		log.info("edeliveryProfiles: {}", Arrays.toString(profiles.toArray()));

	}


	private void validateEdeliveryProfile() {
		if (profiles.size() > 0) {
			if (StringUtils.isNotBlank(selectedProfileIdentifier)) {
				selectedProfile = findProfileByIdentifier(selectedProfileIdentifier);
			}
			if (selectedProfile == null) {
				FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR, "Моля изберете активен профил в еВръчване");
			} else {
				log.info("selectedProfile: {}", selectedProfile);
			}
		}
	}


	public void checkForMessages() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			if (StringUtils.isNoneBlank(epdaeuMsg)) {
				FacesUtils.addGlobalMessage(FacesMessageSeverity.ERROR, epdaeuMsg);
				epdaeuMsg = null;
			}
		}
	}

	@Deprecated
	public void downloadFile(String id) {
		try {
			if (!FacesContext.getCurrentInstance().isPostback()) {
				FacesContext fc = FacesContext.getCurrentInstance();

				Map<String, String> params = fc.getExternalContext().getRequestParameterMap();
				if (id == null) {
					id =  params.get("id");
				}
				log.info("Download file id {}", id);

				ServiceDocument doc = getServiceDocumentById(id);

				if (doc != null) {

					log.info("Download file url {}", doc.getUrl());
					HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

					response.reset();
					response.setContentType(doc.getMimeType());
					response.setCharacterEncoding("UTF-8");
					// response.setHeader("Content-Type", doc.getMimeType()+";
					// charset=UTF-8");
					// response.setContentLength(contentLength);
					response.setHeader("Content-Disposition",
							"attachment; filename*=UTF-8''" + URLEncoder.encode(doc.getFileName(), "UTF-8"));

					// Open response output stream
					OutputStream responseOutputStream = response.getOutputStream();

					// Read PDF contents
					URL url = new URL(doc.getUrl());
					InputStream pdfInputStream = url.openStream();

					// Read PDF contents and write them to the output
					byte[] bytesBuffer = new byte[2048];
					int bytesRead;
					while ((bytesRead = pdfInputStream.read(bytesBuffer)) > 0) {
						responseOutputStream.write(bytesBuffer, 0, bytesRead);
					}

					// Make sure that everything is out
					responseOutputStream.flush();

					// Close both streams
					pdfInputStream.close();
					responseOutputStream.close();

					fc.responseComplete();
				}
			}
		} catch (IOException e) {
			log.error("Error while downloading pdf form", e);
		}
	}

	@Deprecated
	public void downloadFile() {
		downloadFile(null);
	}

	public void saveFileAutofill(String docId, String sessionId, String personId, String formId) {

		byte[] file = downloadFileBytes(docId);
		TblAutoFillForms autofill = new TblAutoFillForms(sessionId, personId, formId, new Date(), file, "");

		autofillRepository.save(autofill);
	}

	private byte[] downloadFileBytes(String id) {
		byte[] file = null;
		try {
			log.info("Download file id {}", id);

			ServiceDocument doc = getServiceDocumentById(id);

			if (doc != null) {
				log.info("Download file url {}", doc.getUrl());

				// Read PDF contents
				URL url = new URL(doc.getUrl());
				try (InputStream pdfInputStream = url.openStream()) {
					// Read PDF contents and write them to the output
					file = IOUtils.toByteArray(pdfInputStream);
				}
			}
		} catch (IOException e) {
			log.error("Error while downloading pdf form", e);
		}

		return file;
	}

	public void prepareFileDownloadSave(boolean save) {
		selectedFileDownload.setSave(save);
	}

	public void prepareFileDownload(String id, String fileName, String contentType) {
		log.info("id: {}; fileName: {}; mimeType: {}", id, fileName, contentType);
		selectedFileDownload = new FileDownload();
		selectedFileDownload.setId(id);
		selectedFileDownload.setFileName(fileName);
		selectedFileDownload.setContentType(contentType);
	}

	public boolean fileAvailableForAutofill(String fileName) {

		Optional<VwAutoFillFormsForUse> result = autofillFormsForUseRepository.findByFormId(fileName);
		boolean present = result.isPresent();
		log.info("file name: [{}] is present: [{}]", fileName, present);

		return present; // false;
	}

	public StreamedContent getFile() throws InterruptedException {

		String id = selectedFileDownload.getId();
		String contentType = selectedFileDownload.getContentType();
		String fileName = selectedFileDownload.getFileName();

		if (StringUtils.isEmpty(contentType)) {
			contentType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(fileName);
			log.info("contentType: [{}]", contentType);
		}

		if (selectedFileDownload.isSave()) {
			FacesContext facesContext = FacesContext.getCurrentInstance();

			HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
			String sessionId = session == null ? null : session.getId();
			log.info("sessionId: [{}]", sessionId);

			String personId = userController.getUser().getIdentity();

			String formId = selectedFileDownload.getFileName(); // params.get("fileName");
			log.info("formId: [{}]", formId);

			saveFileAutofill(id, sessionId, personId, formId);

			// query loop
			int i = 0;
			boolean found = false;
			while ((i < TIMEOUT_MILLIS) || found) {
				Thread.sleep(WAIT_MILLIS); // sleep
				i += WAIT_MILLIS;

				List<TblAutoFillForms> autoFilledDocs = autofillRepository.findAutoFilledForms(sessionId, personId, formId);
				found = !autoFilledDocs.isEmpty();
				log.info("query db...[{}s] found: [{}]", i /1000, found);
				if (found) {
					TblAutoFillForms foundAutoFillDoc = autoFilledDocs.get(0);
					log.info("found doc id: {}" , foundAutoFillDoc.getIdAutoFill());
					foundAutoFillDoc.setSendetToPerson(new Date());
					autofillRepository.save(foundAutoFillDoc); // update entity
					// stream auto filled doc from DB
					return new DefaultStreamedContent(new ByteArrayInputStream(foundAutoFillDoc.getFormData()), contentType, fileName);
				}
			}

			FacesUtils.addGlobalMessage(FacesMessageSeverity.WARN, "Файлът не беше попълнен успешно автоматично в рамките на 1 мин.! \n"
					+ "Моля, опитайте отново.");
			return null; // за да се визуализира предупред. съобщение
		}

		InputStream is = null;
		try {
			log.info("Download file id {}", id);

			ServiceDocument doc = getServiceDocumentById(id);

			if (doc != null) {
				log.info("Download file url {}", doc.getUrl());

				// Read PDF contents
				URL url = new URL(doc.getUrl());
				is = url.openStream();
			}

		} catch (IOException e) {
			log.error("Error while downloading pdf form", e);
		}

		return new DefaultStreamedContent(is, contentType, fileName);
	}


	private ServiceDocument getServiceDocumentById(String id) {
		for (ServiceDocument doc : selectedServiceDocuments) {
			if (doc.getId().equals(id)) {
				return doc;
			}
		}
		for (ServiceDocument doc : selectedServiceFiles) {
			if (doc.getId().equals(id)) {
				return doc;
			}
		}
		return null;
	}


	public EDeliveryProfile findProfileByIdentifier(String ident) {
		for (EDeliveryProfile profile : profiles) {
			if (profile.getIdentifier().equals(ident)) {
				return profile;
			}
		}
		return null;
	}


	public List<InstitutionServices> getInstitutionsServices() {
		return institutionsServicesLoader.getInstitutionsServices();
	}


	public List<Service> getServices() {
		if (selectedInstitution != null) {
			services = selectedInstitution.getServices();
		}
		if (services == null) {
			services = new ArrayList<>();
		}
		return services;
	}


	public ServiceAddition getSelectedServiceAddition() {
		if (selectedServiceAddition == null) {
			selectedServiceAddition = new ServiceAddition();
		}
		return selectedServiceAddition;
	}


	public List<ServiceDocument> getSelectedServiceDocuments() {
		if (selectedServiceDocuments == null) {
			selectedServiceDocuments = new ArrayList<>();
		}
		return selectedServiceDocuments;
	}


	public List<ServiceDocument> getSelectedServiceFiles() {
		if (selectedServiceFiles == null) {
			selectedServiceFiles = new ArrayList<>();
		}
		return selectedServiceFiles;
	}


	public Service getSelectedService() {
		if (selectedService == null) {
			selectedService = new Service();
		}
		return selectedService;
	}


	public void setSelectedService(Service selectedService) {
		this.selectedService = selectedService;
	}


	public ServiceDocument getSelectedServiceAddDocument() {
		if (selectedServiceAddDocument == null) {
			selectedServiceAddDocument = new ServiceDocument();
		}
		return selectedServiceAddDocument;
	}


	public InstitutionServices getSelectedInstitution() {
		return selectedInstitution;
	}


	public void setSelectedInstitution(InstitutionServices selectedInstitution) {
		this.selectedInstitution = selectedInstitution;
	}


	public EDeliveryProfile getSelectedProfile() {
		return selectedProfile;
	}


	public void setSelectedProfile(EDeliveryProfile selectedProfile) {
		this.selectedProfile = selectedProfile;
	}


	public List<EDeliveryProfile> getProfiles() {
		return profiles;
	}


	public void setProfiles(List<EDeliveryProfile> profiles) {
		this.profiles = profiles;
	}


	public String getSelectedProfileIdentifier() {
		return selectedProfileIdentifier;
	}


	public void setSelectedProfileIdentifier(String selectedProfileIdentifier) {
		this.selectedProfileIdentifier = selectedProfileIdentifier;
	}


	public String getOperatorEGN() {
		return operatorEGN;
	}


	public void setOperatorEGN(String operatorEGN) {
		this.operatorEGN = operatorEGN;
	}

}
