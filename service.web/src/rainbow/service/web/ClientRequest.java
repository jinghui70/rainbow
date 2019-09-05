package rainbow.service.web;

import rainbow.core.util.Utils;

public class ClientRequest {
	
	private String entry;
	
	private String alias;
	
	private String params;

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}
	
	public String getName() {
		if (Utils.hasContent(alias)) return alias;
		int index = entry.lastIndexOf('/');
		return (index==-1) ? entry : entry.substring(index + 1);
	}

	@Override
	public String toString() {
		return "[" + entry + "|" + params + "]";
	}

}
