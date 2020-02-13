package rainbow.service.http;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import rainbow.core.bundle.Bean;
import rainbow.httpserver.RequestHandler;

@Bean(extension = RequestHandler.class)
public class BatchHandler extends ServiceHandler {

	private static Type batchRequest = new TypeReference<List<ClientRequest>>() {
	}.getType();

	@Override
	public String getName() {
		return "batch";
	}

	@Override
	protected Object callService(String entry, String param) throws Throwable {
		List<ClientRequest> clientRequests = JSON.parseObject(param, batchRequest);
		final Map<String, Object> result = new HashMap<String, Object>();
		for (ClientRequest cr : clientRequests) {
			Object value = super.callService(cr.getEntry(), param);
			result.put(cr.getName(), value);
		}
		return result;
	}

}