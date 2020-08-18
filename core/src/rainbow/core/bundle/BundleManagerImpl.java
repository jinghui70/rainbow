package rainbow.core.bundle;

import static rainbow.core.util.Preconditions.checkNotNull;
import static rainbow.core.util.Preconditions.checkState;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.MBeanServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import rainbow.core.platform.BundleAncestor;
import rainbow.core.platform.BundleLoader;
import rainbow.core.util.Utils;
import rainbow.core.util.ioc.Context;
import rainbow.core.util.ioc.DisposableBean;
import rainbow.core.util.ioc.Inject;

public final class BundleManagerImpl implements BundleManager, DisposableBean {

	private static Logger logger = LoggerFactory.getLogger(BundleManagerImpl.class);

	private Map<String, Bundle> bundles = new HashMap<String, Bundle>();

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
			Set<String> idSets = new HashSet<String>();
			idSets.addAll(bundles.keySet());
			newBundles = bundleLoader.loadBundle(idSets);
		} catch (IOException e) {
			logger.error("load bundle failed", e);
			throw new RuntimeException(e);
		}
		logger.info("found {} new bundles", newBundles.size());
		if (!newBundles.isEmpty()) {
			newBundles.forEach(b -> bundles.put(b.getId(), b));
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
		Bundle bundle = bundles.get(id);
		if (bundle != null) {
			if (bundle.getState() == BundleState.FOUND || bundle.getState() == BundleState.READY) {
				bundles.remove(id);
				bundle.destroy();
				refreshUnactiveBundles();
			}
		}
	}

	@Override
	public void destroy() throws Exception {
		stopAll();
		bundles.values().forEach(Bundle::destroy);
		bundles.clear();
	}

	@Override
	public Bundle getBundle(String id) {
		return checkNotNull(bundles.get(id), "bundle not found: {}", id);
	}

	/**
	 * 返回所有的bundle列表
	 * 
	 * @return
	 */
	@Override
	public Collection<Bundle> getBundles() {
		return bundles.values();
	}

	/**
	 * 当发现了新bundle或者删掉了一个bundle，要重新计算bundle的解析状态
	 */
	private void refreshUnactiveBundles() {
		Collection<Bundle> bundles = getBundles();
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
			Collection<String> parentIds = bundle.getParentIds();
			if (parentIds.isEmpty()) {
				bundle.setState(BundleState.READY);
				return true;
			}
			List<Bundle> parentBundles = parentIds.stream().map(id -> bundles.get(id)).filter(Predicates.notNull())
					.collect(Collectors.toList());
			if (parentBundles.size() != parentIds.size()) // 有不存在的依赖插件
				return false;
			BundleAncestor ancestor = new BundleAncestor();
			for (Bundle parent : parentBundles) {
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

	@Override
	public boolean startBundle(String id) throws BundleException {
		Bundle bundle = getBundle(id);
		synchronized (this) {
			return startBundle(bundle);
		}
	}

	private boolean startBundle(Bundle bundle) {
		if (bundle.getState() == BundleState.ACTIVE)
			return true;
		if (bundle.getState() != BundleState.READY) {
			logger.info("[{}]-start failed, bundle not ready", bundle);
			return false;
		}
		if (bundle.hasFather()) {
			Bundle father = getBundle(bundle.getData().getFather());
			if (father.getState() != BundleState.ACTIVE)
				return false;
		}
		logger.info("[{}]-preparing to start ...", bundle);
		logger.info("[{}]-starting parents: ", bundle, bundle.getParents());
		for (Bundle parent : bundle.getParents()) {
			if (!startBundle(parent)) {
				logger.debug("start bundle {} failed, parent {} not ready", bundle.getId(), parent.getId());
				return false;
			}
		}
		logger.info("[{}]-starting ...", bundle);
		bundle.setState(BundleState.STARTING);
		try {
			doStartBundle(bundle);
			bundle.setState(BundleState.ACTIVE);
			logger.info("[{}]-started!", bundle);
			fireBundleEvent(bundle, true);
			if (bundle.getActivator() instanceof FatherBundle) {
				logger.info("[{}]-loading sons", bundle);
				getBundles().stream().filter(Bundle::hasFather).filter(b -> b.isFather(bundle.getId()))
						.forEach(this::startBundle);
				logger.info("[{}]-son loaded", bundle);
				((FatherBundle) bundle.getActivator()).afterSonLoaded();
				logger.info("[{}]-excute after son loaded success", bundle);
			}
			return true;
		} catch (Throwable e) {
			logger.error("[{}]- start bundle failed", bundle, e);
			stopBundle(bundle);
			bundle.setState(BundleState.ERROR);
			return false;
		}
	}

	private void doStartBundle(Bundle bundle) throws BundleException {
		BundleActivator activator = createActivator(bundle);
		List<Context> parentContexts = null;
		if (bundle.getParents() != null)
			parentContexts = Utils.transform(bundle.getParents(), p -> p.getActivator().getContext());
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

	@Override
	public void stopBundle(String id) throws BundleException {
		Bundle bundle = getBundle(id);
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
	 */
	@Override
	public void initStart() {
		logger.info("starting all bundles: {}", bundles.keySet().stream().collect(Collectors.joining(",")));
		getBundles().forEach(this::startBundle);
	}

	/**
	 * 停止所有的bundle
	 */
	@Override
	public synchronized void stopAll() {
		getBundles().forEach(this::stopBundle);
	}

	/**
	 * 发送bundle变化的消息
	 * 
	 * @param bundle
	 * @param active
	 */
	private void fireBundleEvent(Bundle bundle, boolean active) {
	}

}
