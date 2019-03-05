package rainbow.core.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import rainbow.core.model.object.INameObject;

public interface UploadHandler extends INameObject {

	Object doWork(ServletContext sc, HttpServletRequest httpRequest) throws Exception;
	
}
