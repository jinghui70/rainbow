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

	public StreamResult(String name, InputStream inputStream) {
		super(name);
		this.inputStream = inputStream;
	}

	public StreamResult(InputStream inputStream) {
		super("untitled");
		this.inputStream = inputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

}
