package rainbow.db.dao.object;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import rainbow.core.model.object.INestObject;
import rainbow.core.model.object.ITreeObject;
import rainbow.core.util.Utils;
import rainbow.core.util.tree.Tree;
import rainbow.core.util.tree.TreeNode;
import rainbow.db.dao.Dao;
import rainbow.db.dao.DaoUtils;
import rainbow.db.dao.NeoBean;
import rainbow.db.dao.U;
import rainbow.db.dao.condition.C;
import rainbow.db.dao.condition.Op;

/**
 * 树形对象的数据库访问工具类
 * 
 * @author lijinghui
 * 
 * @param <T>
 */
public class TreeDao<I, T extends ITreeObject<I>> extends IdDao<I, T> {

	/**
	 * 森林字段,如果一个数据表中存放的是一个森林,这是用来区分树的字段名
	 */
	protected String forestField;

	/**
	 * 给作为Bean的派生类用的
	 * 
	 * @param clazz
	 */
	protected TreeDao(Class<T> clazz) {
		this(clazz, null);
	}

	/**
	 * 给作为Bean的派生类用的
	 * 
	 * @param clazz
	 */
	protected TreeDao(Class<T> clazz, String forestField) {
		super(clazz);
		this.forestField = forestField;
	}

	/**
	 * 构造函数
	 * 
	 * @param dao
	 * @param clazz
	 */
	public TreeDao(Dao dao, Class<T> clazz) {
		this(dao, clazz, null);
	}

	/**
	 * 构造函数
	 * 
	 * @param dao
	 * @param clazz
	 */
	public TreeDao(Dao dao, Class<T> clazz, String forestField) {
		super(dao, clazz);
		this.forestField = forestField;
	}

	/**
	 * 是否是森林
	 * 
	 * @return
	 */
	public boolean isForest() {
		return forestField != null;
	}

	private C getTreeCnd(NeoBean neo) {
		return isForest() ? C.make(forestField, neo.getObject(forestField)) : null;
	}

	/**
	 * 返回树根的父节点id值
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected I rootId() {
		if (Integer.class == keyClazz)
			return (I) Integer.valueOf(0);
		if (Long.class == keyClazz)
			return (I) Long.valueOf(0);
		return null;
	}

	/**
	 * 当数据库保存的不是森林时调用这个函数获得树
	 * 
	 * @return
	 */
	public Tree<I, T> getTree() {
		checkState(isForest() == false);
		List<T> all = getAll();
		if (Utils.isNullOrEmpty(all))
			return null;
		return new Tree<I, T>(all);
	}

	/**
	 * 当数据库保存的是森林，返回指定的树
	 * 
	 * @param forestValue
	 *            树标记值
	 * @return
	 */
	public Tree<I, T> getTree(Object forestValue) {
		checkNotNull(forestValue);
		List<T> all = query(C.make(forestField, forestValue));
		if (Utils.isNullOrEmpty(all))
			return null;
		return new Tree<I, T>(all);
	}

	/**
	 * 返回指定的树枝
	 * 
	 * @param id
	 * @return
	 */
	public TreeNode<T> getBranch(I id) {
		if (INestObject.class.isAssignableFrom(clazz)) {
			NeoBean neo = dao.fetch(entityName, C.make("id", id));
			if (neo == null)
				return null;
			List<T> all = query(C.make("left", Op.GreaterEqual, neo.getObject("left"))
					.and("left", Op.Less, neo.getObject("right")).and(getTreeCnd(neo)));
			Tree<I, T> tree = new Tree<I, T>(all);
			List<TreeNode<T>> firstLevel = tree.getRoots();
			checkState(firstLevel.size() == 1);
			return firstLevel.get(0);
		} else {
			T obj = fetch(id);
			return getBranch(obj);
		}
	}

	public TreeNode<T> getBranch(T obj) {
		TreeNode<T> node = new TreeNode<T>(obj);
		List<T> list = query(C.make("pid", obj.getId()));
		if (!list.isEmpty()) {
			for (T child : list)
				node.addChild(getBranch(child));
		}
		return node;
	}

	/**
	 * 返回某个节点的直接子节点
	 * 
	 * @param pid
	 * @return
	 */
	public List<T> getChildren(I pid) {
		return query(C.make("pid", pid), getDefaultOrderBy());
	}

	/**
	 * 返回某个节点层级
	 * 
	 * @param id
	 * @return
	 */
	public int getLayer(I id) {
		T self = fetch(id);
		checkNotNull(self, "id[%d] not exist", id);
		int level = 1;
		while (!self.getPid().equals(rootId())) {
			self = fetch(self.getPid());
			checkNotNull(self, "(%s) id[%d]'s ancestor [%d] not exist", entityName, id, self.getPid());
			level++;
		}
		return level;
	}

	@Override
	protected void doInsert(T obj, NeoBean neo) {
		if (INestObject.class.isAssignableFrom(clazz)) {
			DaoUtils.calcLeftRight(dao, neo, getTreeCnd(neo), rootId());
		}
		super.doInsert(obj, neo);
	}

	@Override
	protected void doUpdate(NeoBean neo) {
		if (INestObject.class.isAssignableFrom(clazz)) {
			// 确保左右值不被外面污染
			INestObject<I> old = (INestObject<I>) fetch(neo.getObject("id"));
			neo.setValue("left", old.getLeft());
			neo.setValue("right", old.getRight());
		}
	}

	@Override
	protected void doDelete(Object[] keyValues) {
//		TreeNode<T> treeNode = getBranch(id);
//		if (treeNode == null)
//			return;
//		if (treeNode.isLeaf())
//			super.doDelete(id, neo);
//		else {
//			Sql sql = SqlBuilder.delete().from(entityName).where("id", 0).build(dao);
//			List<I> ids = Lists.reverse(TreeUtils.getIds(treeNode));
//			for (I subId : ids) {
//				for (SubEntity sub : subEntities) {
//					dao.execSql(SqlBuilder.delete().from(sub.getName()).where(sub.getProperty(), subId));
//				}
//				dao.execSql(sql.getSql(), subId);
//			}
//		}
	}

	public void move(final I id, final I newPid) {
		dao.transaction(new Runnable() {
			@Override
			public void run() {
				doMove(id, newPid);
			}
		});
	}

	protected void doMove(I id, I newPid) {
		dao.update(entityName, C.make("id", id), U.set("pid", newPid));
		if (INestObject.class.isAssignableFrom(clazz)) {
			// TODO 重新计算所有的左右值
		}
	}

}