package rainbow.core.web;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rainbow.core.extension.ExtensionRegistry;

public class RainbowFilter implements Filter {

	private ServletContext sc;

	private Comparator<UrlHandler> handlerComparator = new Comparator<UrlHandler>() {
		@Override
		public int compare(UrlHandler o1, UrlHandler o2) {
			int c = o2.getType().ordinal() - o1.getType().ordinal();
			if (c == 0)
				c = o2.getString().length() - o1.getString().length();
			return c;
		}
	};
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		this.sc = config.getServletContext();
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
		final String path = getPath(httpRequest);
		
		List<UrlHandler> handlers = ExtensionRegistry.getExtensionObjects(UrlHandler.class);
		Iterator<UrlHandler> i = handlers.stream().filter(h->h.match(path)).sorted(handlerComparator).iterator();
		while(i.hasNext()) {
			UrlHandler handler = i.next();
			if (handler.handle(sc, httpRequest, httpResponse, path))
				return;
		}
		chain.doFilter(httpRequest, httpResponse);
	}

	/**
	 * 取请求文件的路径，本来可以request.getServletPath(),但是weblogic有bug不支持此函数
	 * 
	 * @param httpRequest
	 * @return
	 */
	private String getPath(HttpServletRequest httpRequest) {
		String path = httpRequest.getPathInfo();
		if (path == null)
			return httpRequest.getServletPath();
		return path;
	}

}
