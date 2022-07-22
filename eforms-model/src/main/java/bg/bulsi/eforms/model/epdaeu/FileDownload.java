package bg.bulsi.eforms.model.epdaeu;

import java.io.Serializable;

import org.primefaces.model.StreamedContent;

public class FileDownload implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String fileName;
	private String contentType;
	private long size;
	private boolean save = false;

	private StreamedContent file;

	public FileDownload() {

	}

	public FileDownload(String id, String fileName, String contentType, long size, StreamedContent file, boolean save) {
		this.id = id;
		this.fileName = fileName;
		this.contentType = contentType;
		this.size = size;
		this.file = file;
		this.save = save;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public boolean isSave() {
		return save;
	}

	public void setSave(boolean save) {
		this.save = save;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "FileDownload [id=" + id + ", fileName=" + fileName + ", contentType=" + contentType + ", size=" + size
				+ ", save=" + save + "]";
	}

}
