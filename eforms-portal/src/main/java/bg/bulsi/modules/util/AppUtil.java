package bg.bulsi.modules.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.datacontract.schemas._2004._07.edelivery_common.EProfileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.XfaForm;
import com.itextpdf.text.pdf.security.PdfPKCS7;

import bg.bulsi.eforms.model.auth.EauthUserDetails;
import bg.bulsi.eforms.model.auth.USER_TYPE;

public class AppUtil {

	private static Logger log = LoggerFactory.getLogger(AppUtil.class);


	/**
	 * Checks only is the file contains any signature
	 *
	 * @throws IOException
	 */
	public static boolean isFileContainsSignature(byte[] pdf) throws IOException {
		PdfReader reader = new PdfReader(pdf);
		AcroFields acroFields = reader.getAcroFields();
		List<String> signatureNames = acroFields.getSignatureNames();
		return !signatureNames.isEmpty();
	}


	public static boolean isFileContainsSignature(byte[] pdf, String regex, String isSignatureNeeded)
			throws IOException {
		PdfReader reader = new PdfReader(pdf);

		if (isSignatureNeeded.equals("1")) {
			return true;
		}

		AcroFields acroFields = reader.getAcroFields();
		List<String> signatureNames = acroFields.getSignatureNames();
		for (String name : signatureNames) {
			if (name.matches(regex)) {
				return true;
			}
		}
		log.error("Pdf document is not signed by the user!");
		return false;
	}


	public static boolean isFileSignedByDaeu(
			byte[] pdf, String xmlTag, String subjectDn, String issuerDn,
			String serialNum)
			throws IOException, GeneralSecurityException, InvalidKeyException {

		PdfReader reader = new PdfReader(pdf);
		return isFileSignedByDaeu(reader, xmlTag, subjectDn, issuerDn, serialNum);
	}


	public static boolean isFileSignedByDaeu(
			PdfReader reader, String xmlTag, String subjectDn, String issuerDn,
			String serialNum)
			throws IOException, GeneralSecurityException, InvalidKeyException {

		AcroFields acroFields = reader.getAcroFields();
		List<String> signatureNames = acroFields.getSignatureNames();
		for (String name : signatureNames) {
			if (name.toLowerCase().endsWith(xmlTag.toLowerCase())) {
				PdfPKCS7 pkcs7 = acroFields.verifySignature(name);
				if (!pkcs7.verify()) {
					log.error("Pdf signature certificate is not valid");
					return false;
				}

				X509Certificate signingCertificate = pkcs7.getSigningCertificate();

				if (!signingCertificate.getSubjectDN().getName().equals(subjectDn)) {
					log.error("Pdf signature certificate subjectDN not match");
					return false;
				}

				if (!signingCertificate.getIssuerDN().getName().equals(issuerDn)) {
					log.error("Pdf signature certificate IssuerDN not match");
					return false;
				}

				if (!signingCertificate.getSerialNumber().toString().equals(serialNum)) {
					log.error("Pdf signature certificate SerialNumber not match");
					return false;
				}
				return true;
			}
		}
		log.error("Pdf not signed by daeu, not valid xml path found");
		return false;
	}


	/**
	 * extracts pdf application number
	 *
	 * @param pdf
	 * @return
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static String extractFileApplicationNumber(byte[] pdf)
			throws IOException, ParserConfigurationException, SAXException {

		PdfReader reader = new PdfReader(pdf);

		XfaForm xfa = new XfaForm(reader);
		Document doc = xfa.getDomDocument();

		Node node = doc.getElementsByTagName("form").item(0);
		Element el = (Element) node;
		NodeList list = el.getElementsByTagName("field");
		for (int i = 0; i < list.getLength(); i++) {
			Node field = list.item(i);
			Node fieldNode = field.getAttributes().getNamedItem("name");
			if ((fieldNode != null) && fieldNode.getTextContent().equals("formId")) {
				return field.getTextContent();
			}
		}
		return null;
	}


	public static boolean isUserSignatureRequired(byte[] pdf)
			throws IOException, ParserConfigurationException, SAXException {

		boolean required = false;

		PdfReader reader = new PdfReader(pdf);
		XfaForm xfa = new XfaForm(reader);
		Document doc = xfa.getDomDocument();

		NodeList nodeList = doc.getElementsByTagName("txtSignatureYN");
		if (nodeList != null) {
			Node node = nodeList.item(0);
			if ((node != null) && (node.getTextContent() != null)) {
				required = Double.parseDouble(node.getTextContent()) == 1 ? true : false;
			}
		}
		return required;
	}


	public static String getAdministrationEIK(byte[] pdf)
			throws IOException, ParserConfigurationException, SAXException {

		String eik = null;

		PdfReader reader = new PdfReader(pdf);
		XfaForm xfa = new XfaForm(reader);
		Document doc = xfa.getDomDocument();

		NodeList nodeList = doc.getElementsByTagName("txtEDeliveryDestination");
		if (nodeList != null) {
			Node node = nodeList.item(0);
			if ((node != null) && (node.getTextContent() != null)) {
				eik = node.getTextContent();
			}
		}
		return eik;
	}


	public static EProfileType getUserType(EauthUserDetails user) {
		if (user.getUserType() == USER_TYPE.LEGAL) {
			return EProfileType.LEGAL_PERSON;
		}
		return EProfileType.PERSON;
	}


	public static void main(String[] args)
			throws IOException, ParserConfigurationException, SAXException,
			TransformerException, GeneralSecurityException {
		PdfReader reader = new PdfReader("/home/sali/Downloads/124002DKLRv01.pdf");

		XfaForm xfa = new XfaForm(reader);
		Document doc = xfa.getDomDocument();

		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		Writer out = new StringWriter();
		tf.transform(new DOMSource(doc), new StreamResult(out));
		System.out.println(out.toString());

		AcroFields acroFields = reader.getAcroFields();
		List<String> signatureNames = acroFields.getSignatureNames();
		for (String name : signatureNames) {
			System.out.println(name);
		}

		/*
		 * Node node = doc.getElementsByTagName("txtSignatureYN").item(0);
		 * System.out.println(node.getTextContent());
		 */

		System.out.println(isFileSignedByDaeu(reader, ".StampIT[0].SignatureField1[0]",
				"E=mail@e-gov.bg,CN=State e-Government Agency,2.5.4.97=NTRBG-177098809,O=State e-Government Agency,L=Sofia,C=BG",
				"CN=StampIT Global Qualified CA,2.5.4.97=NTRBG-831641791,O=Information Services JSC,L=Sofia,C=BG",
				"8293718808737544016"));

		// System.out.println(getAdministrationEIK(reader));

		String regex = ".*(\\.signature_[0-9]+\\[0\\])";

		/* System.out.println(isFileContainsSignature(reader, regex, "1")); */

	}

}
