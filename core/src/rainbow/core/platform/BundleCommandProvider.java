package rainbow.core.platform;

import java.util.stream.Stream;

import rainbow.core.bundle.Bundle;
import rainbow.core.bundle.BundleManager;
import rainbow.core.bundle.BundleState;
import rainbow.core.console.CommandInterpreter;
import rainbow.core.console.CommandProvider;
import rainbow.core.console.HelpBuilder;
import rainbow.core.util.Utils;
import rainbow.core.util.ioc.Inject;

/**
 * This class provides methods to execute commands from the command line. It
 * registers itself as a CommandProvider so it can be invoked by a
 * CommandInterpreter. The FrameworkCommandProvider registers itself with the
 * highest ranking (Integer.MAXVALUE) so it will always be called first. Other
 * CommandProviders should register with lower rankings.
 * 
 * There is a method for each command which is named '_'+method. The methods are
 * invoked by a CommandInterpreter's execute method.
 * 
 * @author lijinghui
 * 
 */
public class BundleCommandProvider implements CommandProvider {

	private BundleManager bundleManager;

	@Inject
	public void setBundleManager(BundleManager bundleManager) {
		this.bundleManager = bundleManager;
	}

	public String getName() {
		return "bundle";
	}

	@Override
	public String getDescription() {
		return "Bundle 管理";
	}

	@Override
	public void getHelp(StringBuilder sb) {
		HelpBuilder.addCommand("list", "[-s [<bundle state>] [bundle id]", "显示所有Bundle", sb);
		HelpBuilder.addCommand("refresh", "refresh bundles", sb);
		HelpBuilder.addCommand("bundle", "<id>", "显示指定bundle的详细信息", sb);
		HelpBuilder.addCommand("start", "[<id pattern>]", "启动指定的bundle", sb);
		HelpBuilder.addCommand("stop", "[<id pattern>]", "停止指定的bundle", sb);
		HelpBuilder.addCommand("uninstall", "<id>", "卸载指定的bundle", sb);
	}

	/**
	 * Handle the bundle command. Display details for the specified bundle(s).
	 * 
	 * @param ci A Consoleci object containing the command and it's arguments.
	 */
	public void _bundle(CommandInterpreter ci) throws Exception { // NOPMD
		String nextArg = ci.nextArgument();
		if (nextArg == null) {
			ci.println("No bundle specified");
		} else {
			Bundle bundle = bundleManager.get(nextArg);
			if (bundle == null)
				ci.println("bundle %s not found", nextArg);
			ci.println(bundle.getId());
		}
	}

	/**
	 * Handle the start command. Start the specified bundle(s).
	 * 
	 * @param ci A Consoleci object containing the command and it's arguments.
	 */
	public void _start(CommandInterpreter ci) throws Exception { // NOPMD
		String nextArg = ci.nextArgument();
		if (nextArg == null) {
			ci.println("please input bundle id");
			return;
		}
		while (nextArg != null) {
			bundleManager.startBundle(nextArg);
			nextArg = ci.nextArgument();
		}
	}

	/**
	 * Handle the stop command. Stop the specified bundle(s).
	 * 
	 * @param ci A Consoleci object containing the command and it's arguments.
	 */
	public void _stop(CommandInterpreter ci) throws Exception { // NOPMD
		String nextArg = ci.nextArgument();
		if (nextArg == null)
			bundleManager.stopAll();
		while (nextArg != null) {
			bundleManager.stopBundle(nextArg);
			nextArg = ci.nextArgument();
		}
	}

	/**
	 * Handle the uninstall command. Uninstall the specified bundle(s).
	 * 
	 * @param ci A Consoleci object containing the command and it's arguments.
	 */
	public void _uninstall(CommandInterpreter ci) throws Exception { // NOPMD
		String nextArg = ci.nextArgument();
		if (nextArg == null)
			ci.println("please input bundle identify");
		bundleManager.uninstallBundle(nextArg);
	}

	/**
	 * Handle the refresh command. Refresh the packages of the specified bundles.
	 * 
	 * @param ci A CommandInterpreter object containing the command and it's
	 *           arguments.
	 */
	public void _refresh(CommandInterpreter ci) throws Exception { // NOPMD
		bundleManager.refresh();
	}

	/**
	 * Prints the bundle list
	 * 
	 * @param ci A CommandInterpreters object containing the command and it's
	 *           arguments.
	 */
	public void _list(CommandInterpreter ci) throws Exception { // NOPMD
		String filteredName = null;
		BundleState stateFilter = null;
		String option = ci.nextArgument();
		if (option != null) {
			if ("-s".equals(option)) {
				String searchedState = ci.nextArgument();
				try {
					stateFilter = BundleState.valueOf(searchedState.toUpperCase());
				} catch (IllegalArgumentException e) {
					ci.println("Bundle States are: FOUND, RESOLVING, READY, STARTING, STOPPING, ACTIVE");
					ci.println("invalid state[] input: %s", searchedState);
					return;
				}
				filteredName = ci.nextArgument();
			} else {
				filteredName = option;
			}
		}
		Stream<Bundle> bundles = doFilter(stateFilter, filteredName);
		ci.println("State       Bundle                        Desc");
		bundles.forEach(b -> ci.println(String.format("%-12s%-30s%s", b.getState().name(), b.getId(), b.getDesc())));
	}
	
	private Stream<Bundle> doFilter(BundleState stateFilter, String filteredName) {
		Stream<Bundle> bundles = bundleManager.getBundles();
		if (stateFilter != null) {
			bundles = bundles.filter(b -> b.getState() == stateFilter);
		}
		if (!Utils.isNullOrEmpty(filteredName)) {
			bundles = bundles.filter(b -> b.getId().startsWith(filteredName));
		}
		return bundles;
	}

}
