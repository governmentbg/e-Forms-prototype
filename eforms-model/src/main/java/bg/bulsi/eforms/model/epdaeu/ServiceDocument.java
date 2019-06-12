package bg.bulsi.eforms.model.epdaeu;

import java.io.Serializable;

import org.primefaces.model.UploadedFile;

public class ServiceDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String description;
	private String url;
	private String mimeType;
	private String fileName;
	private long size;
	private boolean required;

	private String pdfNumber; // app number from the pdf form

	private String id; // hashcode of the url and the name

	private UploadedFile file;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPdfNumber() {
		return pdfNumber;
	}

	public void setPdfNumber(String pdfNumber) {
		this.pdfNumber = pdfNumber;
	}

	public String getId() {
		if (this.id == null) {
			if (this.url != null) {
				this.id = this.url.hashCode() + "";
			}
			if (this.getName() != null) {
				this.id = this.id + this.name.hashCode();
			}
		}
		return id;
	}

}
