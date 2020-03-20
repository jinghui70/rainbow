package rainbow.service;

import java.io.InputStream;

import rainbow.core.model.object.NameObject;

/**
 * 资源响应对象
 * 
 */
public class StreamResult extends NameObject {

	/**
	 * 资源流对象
	 */
	private InputStream inputStream;

	/**
	 * 下载标记
	 */
	private boolean download;

	/**
	 * 文件类型
	 */
	private String contentType;

	public StreamResult() {
	}

	public StreamResult(String name, InputStream inputStream) {
		super(name);
		this.inputStream = inputStream;
	}

	public boolean isDownload() {
		return download;
	}

	public void setDownload(boolean download) {
		this.download = download;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

}
