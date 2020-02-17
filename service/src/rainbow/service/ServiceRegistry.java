package rainbow.service;

import rainbow.service.exception.InvalidServiceException;

public interface ServiceRegistry {
	
	Service getService(String serviceId) throws InvalidServiceException;

}