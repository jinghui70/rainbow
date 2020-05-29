package rainbow.service;

import java.io.InputStream;

import rainbow.core.model.object.NameObject;

/**
 * 资源响应对象，当需要向前端传递流数据的时候，返回该对象实例。
 * 
 * 流数据用 inputStream 或者是 streamWriter表示，二者必有其一。
 * 
 */
public class StreamResult extends NameObject {

	/**
	 * 资源流对象
	 */
	private InputStream inputStream;

	/**
	 * 资源流输出工具
	 */
	private StreamWriter streamWriter;

	/**
	 * 下载标记，配合http的要求，表示这是用于下载的文件
	 */
	private boolean download;

	/**
	 * 文件类型，如果没有提供，系统会自动用name属性来判断
	 */
	private String contentType;

	public StreamResult() {
	}

	public StreamResult(String name, InputStream inputStream) {
		super(name);
		this.inputStream = inputStream;
	}

	public StreamResult(String name, StreamWriter writer) {
		super(name);
		this.streamWriter = writer;
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

	public StreamWriter getStreamWriter() {
		return streamWriter;
	}

	public void setStreamWriter(StreamWriter streamWriter) {
		this.streamWriter = streamWriter;
	}

}
