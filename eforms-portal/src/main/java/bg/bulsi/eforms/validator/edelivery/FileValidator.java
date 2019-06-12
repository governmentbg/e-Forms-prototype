package bg.bulsi.eforms.validator.edelivery;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.io.FilenameUtils;

/**
 * Validate attached files by some parameters:
 * MAX_ATTACHED_DOCUMENTS
 * MAX_DOCUMENT_SIZE
 * DOCUMENT_EXTENTION_TYPES
 * 
 * @author gdimitrov
 *
 */
public class FileValidator implements Validator {

	private final static int MAX_ATTACHED_DOCUMENTS = 10; // numbers
	private final static int MAX_DOCUMENT_SIZE = 10; // MB
	private final static String DOCUMENT_EXTENTION_TYPES[] = 
		{ "pdf", "doc", "docx", "rtf", "xls", "jpg", "tiff", "gif",	"zip", "gz", "tar", "rar" }; // file extensions
	
	private String newLine = "\n";
	private StringBuffer details = new StringBuffer();
	private StringBuffer summary = new StringBuffer();

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

		boolean errorFlag = false;
		
		@SuppressWarnings("unchecked")
		List<File> files = (List<File>) value;
		FacesMessage msg;

		if (!checkAttached(files)) {
			errorFlag = true;
		}

		boolean hasSummaryFlag = false;
		for (File file : files) {

			if (!checkSize(file, hasSummaryFlag)) {
				errorFlag = true;
				hasSummaryFlag = true;
			}

			if (!checkExtention(file, hasSummaryFlag)) {
				errorFlag = true;
				hasSummaryFlag = true;
			}

		}
		
		if (errorFlag) {			
			msg = new FacesMessage();
			msg.setDetail(details.toString());
			msg.setSummary(summary.toString());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(msg);
		}

	}

	
	/*
	 * Check the attached size with MAX_ATTACHED_DOCUMENTS.
	 * 
	 * @return TRUE if less or equals and FALSE if bigger
	 */
	private boolean checkAttached(List<File> files) {
		boolean result;
		
		int attachetNumbers = files.size();
		
		if (attachetNumbers <= MAX_ATTACHED_DOCUMENTS) {
			result = true;
		} else {
			summary.append("Добавените файлове са повече от допустимите " + MAX_ATTACHED_DOCUMENTS + " броя.").append(newLine);
			result = false;
		}
		
		details.append("Добавени са " + attachetNumbers + " файла.").append(newLine);
		
		return result;
	}
	
	
	/*
	 * Check the file extention in DOCUMENT_EXTENTION array.
	 * 
	 * @return TRUE if contains and FALSE if not contains
	 */
	private boolean checkExtention(File file, boolean hasSummaryFlag) {
		boolean result = false;
		List<String> list = Arrays.asList(DOCUMENT_EXTENTION_TYPES);

		String ext = getFileExtention(file);

		if (list.contains(ext)) {
			details.append("Файл \'" + file.getName() + "\' е от тип \'" + ext + "\'.").append(newLine);
			result = true;
		} 
		
		if (!result && !hasSummaryFlag) {
			summary.append("Файл \'" + file.getName() + "\' е от непозволен тип \'" + ext + "\' .").append(newLine);
			result = false;
		}

		return result;
	}

	
	/*
	 * Check the file size with MAX_DOCUMENT_SIZE in MB.
	 * 
	 * @return TRUE if less or equals and FALSE if bigger
	 */
	private boolean checkSize(File file, boolean hasSummaryFlag) {
		boolean result = false;
		long size = file.getUsableSpace();
		long maxByteSize = MAX_DOCUMENT_SIZE * 1024 * 1024;
		
		if (size <= maxByteSize) {
			details.append("Файл \'" + file.getName() + " е с размер " + size/1024/1024 + "MB.").append(newLine);
			result = true;
		}
		
		if (!result && !hasSummaryFlag) {
			summary.append("Файл " + file.getName() + " с размер " + size/1024/1024 + "MB по голям от допустимия.").append(newLine);
			result = false;
		}
		
		return result;
	}

	
	private String getFileExtention(File file) {
		String s = file.getName();
		String ext = FilenameUtils.getExtension(s);
		return ext;
	}

}
