package rainbow.service.internal.console;

import rainbow.core.console.CommandInterpreter;
import rainbow.core.console.CommandProvider;
import rainbow.core.console.HelpBuilder;
import rainbow.service.internal.Service;
import rainbow.service.internal.ServiceRegistry;
import rainbow.core.util.ioc.Inject;

/**
 * 关于服务的控制台命令提供者
 * 
 * @author lijinghui
 * 
 */
public class ServiceCommandProvider implements CommandProvider {

	private ServiceRegistry serviceRegistry;

	@Inject
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public String getName() {
		return "Service";
	}

	@Override
	public String getDescription() {
		return "服务管理";
	}

	@Override
	public void getHelp(StringBuilder sb) {
		HelpBuilder.addCommand("list", "列出已注册的服务", sb);
	}

	public void _list(CommandInterpreter ci) throws Exception { // NOPMD
		String filter = ci.nextArgument();
		ci.println("Service:");
		for (Service service : serviceRegistry.getServices(filter)) {
			ci.println(String.format("%s", service.getServiceId()));
		}
	}
}
