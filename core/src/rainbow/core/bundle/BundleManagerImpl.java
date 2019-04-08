package rainbow.core.bundle;

import static rainbow.core.util.Preconditions.checkNotNull;
import static rainbow.core.util.Preconditions.checkState;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.management.MBeanServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import rainbow.core.extension.Extension;
import rainbow.core.extension.ExtensionRegistry;
import rainbow.core.platform.BundleAncestor;
import rainbow.core.platform.BundleLoader;
import rainbow.core.util.ioc.Context;
import rainbow.core.util.ioc.DisposableBean;
import rainbow.core.util.ioc.Inject;

public final class BundleManagerImpl implements BundleManager, DisposableBean {

	private static Logger logger = LoggerFactory.getLogger(BundleManagerImpl.class);

	private CopyOnWriteArrayList<Bundle> bundles = new CopyOnWriteArrayList<>();

	private Multimap<Bundle, Bundle> bundleChildren = LinkedListMultimap.create();

	private BundleLoader bundleLoader;

	private MBeanServer mBeanServer;
	
	@Inject
	public void setBundleLoader(BundleLoader bundleLoader) {
		this.bundleLoader = bundleLoader;
	}

	@Inject
	public void setmBeanServer(MBeanServer mBeanServer) {
		this.mBeanServer = mBeanServer;
	}

	@Override
	public synchronized void refresh() {
		List<Bundle> newBundles;
		try {
			newBundles = bundleLoader.loadBundle(bundles);
		} catch (IOException e) {
			logger.error("load bundle failed", e);
			throw new RuntimeException(e);
		}
		logger.info("found {} new bundles", newBundles.size());
		if (!newBundles.isEmpty()) {
			bundles.addAll(newBundles);
			refreshUnactiveBundles();
		}
	}

	/**
	 * 删除掉一个bundle
	 * 
	 * @param id
	 */
	@Override
	public synchronized void uninstallBundle(String id) {
		Bundle bundle = get(id);
		if (bundle == null)
			return;
		if (bundle.getState() == BundleState.FOUND || bundle.getState() == BundleState.READY) {
			bundles.remove(bundle);
			bundle.destroy();
		}
		refreshUnactiveBundles();
	}

	@Override
	public void destroy() throws Exception {
		stopAll();
		for (Bundle bundle : bundles)
			bundle.destroy();
		bundles.clear();
	}

	/**
	 * 返回指定的Bundle
	 * 
	 * @param id
	 * @return
	 */
	public Bundle get(String id) {
		Optional<Bundle> o = bundles.stream().filter(b -> b.getId().equals(id)).findFirst();
		return o.isPresent() ? o.get() : null;
	}

	/**
	 * 返回所有的bundle列表
	 * 
	 * @return
	 */
	public Stream<Bundle> getBundles() {
		return bundles.stream();
	}

	/**
	 * 当发现了新bundle或者删掉了一个bundle，要重新计算bundle的解析状态
	 */
	private void refreshUnactiveBundles() {
		for (Bundle bundle : bundles)
			if (bundle.getState() == BundleState.READY || bundle.getState() == BundleState.ERROR)
				bundle.setState(BundleState.FOUND);
		for (Bundle bundle : bundles)
			resolveBundle(bundle);
	}

	/**
	 * 解析一个包所依赖的所有父包
	 * 
	 * @param bundle
	 * @return
	 */
	private boolean resolveBundle(Bundle bundle) {
		if (bundle.getState() != BundleState.FOUND)
			return false;
		bundle.setState(BundleState.RESOLVING);
		try {
			bundle.setParents(null);
			if (bundle.getParentId() == null || bundle.getParentId().length == 0) {
				bundle.setState(BundleState.READY);
				return true;
			}
			BundleAncestor ancestor = new BundleAncestor();
			for (String id : bundle.getParentId()) {
				Bundle parent = get(id);
				if (parent == null)
					return false;
				if (ancestor.unaware(parent)) {
					if (parent.getState() == BundleState.FOUND) {
						if (!resolveBundle(parent))
							return false;
					}
					ancestor.addParent(parent);
				}
			}
			bundle.setState(BundleState.READY);
			bundle.setParents(ancestor.getParents());
			bundle.setAncestors(ancestor.getAncestors());
			return true;
		} finally {
			if (bundle.getState() != BundleState.READY) {
				bundle.setState(BundleState.FOUND);
				bundle.setParents(null);
			}
		}
	}

	public boolean startBundle(String id) throws BundleException {
		Bundle bundle = checkNotNull(get(id), "bundle not found: {}", id);
		synchronized (this) {
			return startBundle(bundle);
		}
	}

	private boolean startBundle(Bundle bundle) {
		if (bundle.getState() == BundleState.ACTIVE) 
			return true;
		if (bundle.getState() != BundleState.READY) {
			logger.info("start bundle {} failed, bundle not ready", bundle.getId());
			return false;
		}
		for (Bundle parent : bundle.getParents()) {
			if (!startBundle(parent)) {
				logger.debug("start bundle {} failed, parent {} not ready", bundle.getId(), parent.getId());
				return false;
			}
		}
		logger.info("starting bundle {}...", bundle.getId());
		bundle.setState(BundleState.STARTING);
		try {
			doStartBundle(bundle);
			bundle.setState(BundleState.ACTIVE);
			logger.info("bundle {} started!", bundle.getId());
			fireBundleEvent(bundle, true);
			return true;
		} catch (Throwable e) {
			logger.error("start bundle {} failed", bundle.getId(), e);
			stopBundle(bundle);
			bundle.setState(BundleState.ERROR);
			return false;
		}
	}

	private void doStartBundle(Bundle bundle) throws BundleException {
		BundleActivator activator = createActivator(bundle);
		List<String> ids = activator.getParentContextId();
		Context[] parentContexts = new Context[ids.size()];
		int i = 0;
		for (String id : ids) {
			Bundle contextBundle = get(id);
			checkNotNull(contextBundle, "can not find parent bundle {}", id);
			checkState(bundle.getAncestors().contains(contextBundle), "bundle {}  isn't in parent list",
					id);
			Context parentContext = contextBundle.activator.getContext();
			checkNotNull(parentContext, "parent bundle {} doesn't have a context", id);
			parentContexts[i++] = parentContext;
		}
		bundle.setActivator(activator);
		activator.start(mBeanServer, parentContexts);
		for (Bundle parent : bundle.getParents()) {
			bundleChildren.put(parent, bundle);
		}
	}

	private BundleActivator createActivator(Bundle bundle) throws BundleException {
		try {
			Class<?> activatorClass = null;
			String className = bundle.getId() + ".Activator";
			try {
				activatorClass = bundle.getClassLoader().loadClass(className);
			} catch (ClassNotFoundException e) {
				className = String.format("rainbow.%s.Activator", bundle.getId());
				activatorClass = bundle.getClassLoader().loadClass(className);
			}
			checkState(BundleActivator.class.isAssignableFrom(activatorClass), "wrong activator class {}",
					activatorClass);
			BundleActivator activator = (BundleActivator) activatorClass.newInstance();
			activator.setBundleId(bundle.getId());
			return activator;
		} catch (Throwable e) {
			throw new BundleException("init activator failed", e);
		}
	}

	public void stopBundle(String id) throws BundleException {
		Bundle bundle = get(id);
		checkNotNull(bundle, "bundle {} not found", id);
		synchronized (this) {
			stopBundle(bundle);
		}
	}

	private void stopBundle(Bundle bundle) {
		if (bundle.getState() != BundleState.ACTIVE)
			return;
		logger.debug("stopping bundle {}...", bundle.getId());
		bundle.setState(BundleState.STOPPING);
		// stopping children first
		for (Bundle child : ImmutableList.copyOf(bundleChildren.get(bundle))) {
			stopBundle(child);
		}
		bundleChildren.removeAll(bundle);

		// stop self
		if (bundle.activator != null) {
			bundle.activator.stop();
			bundle.setActivator(null);
		}
		for (Bundle parent : bundle.getParents())
			bundleChildren.remove(parent, bundle);
		bundle.setState(BundleState.READY);

		logger.info("bundle {} stopped!", bundle.getId());
		fireBundleEvent(bundle, false);
	}

	/**
	 * 初始化启动的bundle
	 * 
	 * @param onList  初始化要启动的bundle列表
	 * @param offList 初始化不要启动的bundle列表
	 */
	public void initStart() {
		logger.info("starting bundles: {}", bundles.stream().map(Bundle::toString).collect(Collectors.joining(",")));
		for (Bundle bundle : bundles)
			startBundle(bundle);
	}

	/**
	 * 停止所有的bundle
	 */
	public synchronized void stopAll() {
		for (Bundle bundle : bundles)
			stopBundle(bundle);
	}

	/**
	 * 发送bundle变化的消息
	 * 
	 * @param bundle
	 * @param active
	 */
	private void fireBundleEvent(Bundle bundle, boolean active) {
		for (Extension extension : ExtensionRegistry.getExtensions(BundleListener.class)) {
			Bundle extensionBundle = null;
			if (extension.getBundle() != null)
				extensionBundle = get(extension.getBundle());
			if (extensionBundle == null
					|| extensionBundle.getState() == BundleState.ACTIVE && extensionBundle != bundle) {
				BundleListener listener = (BundleListener) extension.getObject();
				try {
					if (active)
						listener.bundleStarted(bundle.getId());
					else
						listener.bundleStop(bundle.getId());
				} catch (Throwable e) {
					logger.error("when bundle {} {}, listener [{}:{}] encounter an error",
							bundle.getId(), active ? "start" : "stop", extensionBundle.getId(), listener.getClass(), e);
				}
			}
		}
	}

}
