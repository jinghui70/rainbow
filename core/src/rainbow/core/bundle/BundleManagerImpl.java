package rainbow.core.bundle;

import static rainbow.core.util.Preconditions.checkNotNull;
import static rainbow.core.util.Preconditions.checkState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.management.MBeanServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rainbow.core.platform.BundleLoader;
import rainbow.core.util.dag.Dag;
import rainbow.core.util.dag.DagImpl;
import rainbow.core.util.ioc.Context;
import rainbow.core.util.ioc.DisposableBean;
import rainbow.core.util.ioc.Inject;

public final class BundleManagerImpl implements BundleManager, DisposableBean {

	private static Logger logger = LoggerFactory.getLogger(BundleManagerImpl.class);

	private Map<String, Bundle> bundleMap = new HashMap<String, Bundle>();

	private BundleLoader bundleLoader;

	private MBeanServer mBeanServer;

	private Dag<Bundle> dag;

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
			idSets.addAll(bundleMap.keySet());
			newBundles = bundleLoader.loadBundle(idSets);
		} catch (IOException e) {
			logger.error("load bundle failed", e);
			throw new RuntimeException(e);
		}
		logger.info("found {} new bundles", newBundles.size());
		if (!newBundles.isEmpty()) {
			newBundles.forEach(b -> bundleMap.put(b.getId(), b));
			buildDag();
		}
	}

	private void buildDag() {
		Dag<Bundle> dag = new DagImpl<>();
		bundleMap.values().forEach(bundle -> {
			if (bundle.getState() == BundleState.READY || bundle.getState() == BundleState.ERROR)
				bundle.setState(BundleState.FOUND);
			List<Bundle> parents = new ArrayList<>();
			for (String parentId : bundle.getParentIds()) {
				Bundle parent = bundleMap.get(parentId);
				if (parent == null)
					return;
				parents.add(parent);
			}
			parents.forEach(p -> dag.addEdge(p, bundle));
		});
		dag.dfsList().stream().forEach(b -> {
			if (b.getState() == BundleState.FOUND) {
				Set<Bundle> ancestors = dag.getAncestors(b);
				b.setAncestors(ancestors);
				if (!ancestors.stream().map(Bundle::getState).anyMatch(Predicate.isEqual(BundleState.FOUND))) {
					b.setState(BundleState.READY);
				}
			}
		});
		this.dag = dag;
	}

	/**
	 * 删除掉一个bundle
	 * 
	 * @param id
	 */
	@Override
	public synchronized void uninstallBundle(String id) {
		Bundle bundle = bundleMap.get(id);
		if (bundle != null) {
			if (bundle.getState() == BundleState.FOUND || bundle.getState() == BundleState.READY) {
				bundleMap.remove(id);
				bundle.destroy();
				buildDag();
			}
		}
	}

	@Override
	public void destroy() throws Exception {
		stopAll();
		bundleMap.values().forEach(Bundle::destroy);
		bundleMap.clear();
	}

	@Override
	public Bundle getBundle(String id) {
		return checkNotNull(bundleMap.get(id), "bundle not found: {}", id);
	}

	/**
	 * 返回所有的bundle列表
	 * 
	 * @return
	 */
	@Override
	public Collection<Bundle> getBundles() {
		return bundleMap.values();
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
		for (Bundle parent : dag.getPredecessor(bundle)) {
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
		Collection<Bundle> parents = dag.getPredecessor(bundle);
		if (!parents.isEmpty()) {
			parentContexts = parents.stream().map(p -> p.getActivator().getContext()).collect(Collectors.toList());
		}
		bundle.setActivator(activator);
		activator.start(mBeanServer, parentContexts);
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
			BundleActivator activator = (BundleActivator) activatorClass.getDeclaredConstructor().newInstance();
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
		for (Bundle child : dag.getSuccessor(bundle)) {
			stopBundle(child);
		}
		// stop self
		if (bundle.activator != null) {
			bundle.activator.stop();
			bundle.setActivator(null);
		}
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
		logger.info("starting all bundles: {}", bundleMap.keySet().stream().collect(Collectors.joining(",")));
		dag.dfsList().forEach(this::startBundle);
	}

	/**
	 * 停止所有的bundle
	 */
	@Override
	public synchronized void stopAll() {
		List<Bundle> list = dag.dfsList();
		for (int i = list.size() - 1; i >= 0; i--) {
			stopBundle(list.get(i));
		}
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
