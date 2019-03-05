package rainbow.db.dao.object;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;

import rainbow.core.util.ioc.InitializingBean;
import rainbow.core.util.ioc.Inject;
import rainbow.db.dao.ClassInfo;
import rainbow.db.dao.Dao;
import rainbow.db.dao.NeoBean;
import rainbow.db.dao.ObjectRowMapper;
import rainbow.db.dao.OrderBy;
import rainbow.db.dao.PageData;
import rainbow.db.dao.Select;
import rainbow.db.dao.U;
import rainbow.db.dao.condition.C;
import rainbow.db.dao.model.Entity;
import rainbow.db.internal.ObjectNameRule;
import rainbow.db.model.Column;
import rainbow.db.object.ObjectManager;

/**
 * 封装一个具体对象的数据库操作类
 * 
 * 派生类必须由Context容器管理
 * 
 * @author lijinghui
 * 
 * @param <T>
 */
public class ObjectDao<T> implements InitializingBean {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected Dao dao;

	protected Class<T> clazz;

	protected ClassInfo<T> classInfo;

	protected ObjectRowMapper<T> mapper;

	private CURDHelper<T> curd;

	/**
	 * 默认排序字段，初始设置为按主键排序。如有不同，在派生类的getDefaultOrderBy中返回即可。
	 */
	private List<OrderBy> defaultOrderBy;

	/**
	 * 实体名，默认为类名。如有不同，在派生类构造函数中定义即可。
	 */
	protected String entityName;

	protected Entity entity;

	/**
	 * 对象管理器
	 */
	protected ObjectManager objectManager;

	/**
	 * 名字翻译规则
	 */
	protected List<ObjectNameRule> nameRules;

	protected void setClass(Class<T> clazz) {
		this.clazz = clazz;
		this.classInfo = new ClassInfo<T>(clazz);
	}

	@Inject
	public void setDao(Dao dao) {
		this.dao = dao;
	}

	public Dao getDao() {
		return dao;
	}

	protected List<OrderBy> getDefaultOrderBy() {
		return defaultOrderBy;
	}

	/**
	 * 添加一个增删改查的切面辅助类
	 * 
	 * @param curd
	 */
	public final void addCurd(CURDHelper<T> curd) {
		if (this.curd == null)
			this.curd = curd;
		else
			this.curd = this.curd.add(curd);
	}

	@Inject(obliged = false)
	public void setObjectManager(ObjectManager objectManager) {
		this.objectManager = objectManager;
		if (objectManager != null) {
			nameRules = objectManager.getObjectNameRule(clazz);
			if (nameRules.isEmpty())
				nameRules = null;
		}
	}

	/**
	 * 构造函数，用于手工创建直接使用大场景
	 * 
	 * @param dao
	 * @throws Exception
	 */
	public ObjectDao(Dao dao, Class<T> clazz) {
		this(clazz);
		setDao(dao);
		initMapper();
	}

	/**
	 * 构造函数,需要派生用于作为Bean在容器中生成
	 * 
	 * @param clazz
	 */
	protected ObjectDao(Class<T> clazz) {
		setClass(clazz);
		entityName = clazz.getSimpleName();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initMapper();
	}

	private void initMapper() {
		entity = dao.getEntity(entityName);
		mapper = new ObjectRowMapper<T>(entity, classInfo);
		if (entity.getKeyCount() > 0) {
			ImmutableList.Builder<OrderBy> builder = ImmutableList.builder();
			for (Column column: entity.getKeys()) {
				builder.add(new OrderBy(column.getName(), false));
			}
			defaultOrderBy = builder.build();
		}
	}

	/**
	 * 返回数据库中的全部对象
	 * 
	 * @return
	 */
	public List<T> getAll() {
		return query(null, getDefaultOrderBy());
	}

	/**
	 * 返回数据库中的全部对象
	 * 
	 * @return
	 */
	public List<T> getAll(List<OrderBy> orderBy) {
		return query(null, orderBy);
	}

	/**
	 * 分页地返回数据库中的全部对象
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public PageData<T> getAll(int pageNo, int pageSize) {
		return query(null, getDefaultOrderBy(), pageNo, pageSize);
	}

	/**
	 * 分页地返回数据库中的全部对象
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public PageData<T> getAll(List<OrderBy> orderBy, int pageNo, int pageSize) {
		return query(null, orderBy, pageNo, pageSize);
	}

	/**
	 * 返回指定主键的对象
	 * 
	 * @param keyValues
	 * @return
	 */
	public T fetch(Object... keyValues) {
		NeoBean neo = dao.fetch(entityName, keyValues);
		return afterFetch(neo);
	}

	/**
	 * 返回指定条件的对象
	 * 
	 * @param cnd
	 * @return
	 */
	public T fetch(C cnd) {
		NeoBean neo = dao.fetch(entityName, cnd);
		return afterFetch(neo);
	}

	private T afterFetch(NeoBean neo) {
		if (neo == null)
			return null;
		T result = neo.bianShen(clazz);
		if (result != null) {
			decorateItem(result);
		}
		if (curd != null)
			curd.afterFetch(dao, result);
		return result;
	}

	/**
	 * 查询一组对象
	 * 
	 * @param cnd
	 *            查询条件
	 * @return
	 */
	public List<T> query(C cnd) {
		return query(cnd, getDefaultOrderBy());
	}

	/**
	 * 查询一组对象
	 * 
	 * @param cnd
	 *            查询条件
	 * @param orderBy
	 *            排序
	 * @return
	 */
	public List<T> query(C cnd, List<OrderBy> orderBy) {
		Select select = new Select().from(entityName).where(cnd).orderBy(orderBy);
		List<T> result = dao.queryForList(select.build(dao), mapper);
		return afterQuery(result);
	}

	/**
	 * 分页查询一组对象
	 * 
	 * @param cnd
	 *            查询条件
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public PageData<T> query(C cnd, int pageNo, int pageSize) {
		return query(cnd, getDefaultOrderBy(), pageNo, pageSize);
	}

	/**
	 * 分页查询一组对象
	 * 
	 * @param cnd
	 *            查询条件
	 * @param orderBy
	 *            排序
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public PageData<T> query(C cnd, List<OrderBy> orderBy, int pageNo, int pageSize) {
		PageData<T> result = dao
				.pageQuery(new Select().from(entityName).where(cnd).orderBy(orderBy).paging(pageNo, pageSize), mapper);
		result.setRows(afterQuery(result.getRows()));
		return result;
	}

	private List<T> afterQuery(List<T> result) {
		if (result.isEmpty())
			return result;
		if (curd != null)
			curd.afterQuery(dao, result);
		return decorateList(result);
	}

	/**
	 * 返回数据库中的总数
	 * 
	 * @return
	 */
	public int count() {
		return dao.count(entityName);
	}

	/**
	 * 返回符合条件的记录数
	 * 
	 * @param cnd
	 * @return
	 */
	public int count(C cnd) {
		return dao.count(entityName, cnd);
	}

	/**
	 * 插入一个对象
	 * 
	 * @param obj
	 */
	public T insert(final T obj) {
		final NeoBean neo = dao.newNeoBean(entityName);
		neo.init(obj, classInfo);
		dao.transaction(new Runnable() {
			@Override
			public void run() {
				if (curd != null) {
					curd.beforeInsert(dao, obj, neo);
				}
				doInsert(obj, neo);
				if (curd != null)
					curd.afterInsert(dao, obj, neo);
			}
		});
		return obj;
	}

	protected void doInsert(T obj, NeoBean neo) {
		dao.insert(neo);
	}

	/**
	 * 更新一整个对象
	 * 
	 * @param obj
	 */
	public void update(final T obj) {
		final NeoBean neo = dao.newNeoBean(entityName);
		neo.init(obj, classInfo);
		dao.transaction(new Runnable() {
			@Override
			public void run() {
				if (curd != null)
					curd.beforeUpdate(dao, obj, neo);
				doUpdate(neo);
				if (curd != null)
					curd.afterUpdate(dao, obj, neo);
			}
		});
	}

	protected void doUpdate(NeoBean neo) {
		dao.update(neo);
	}

	/**
	 * 按条件更新对象
	 * 
	 * @param cnd
	 * @param update
	 */
	public void update(C cnd, U... update) {
		dao.update(entityName, cnd, update);
	}

	/**
	 * 按主键删除一条记录
	 * 
	 * @param keyValues
	 */
	public void delete(final Object... keyValues) {
		dao.transaction(new Runnable() {
			@Override
			public void run() {
				if (curd != null)
					curd.beforeDelete(dao, keyValues);
				doDelete(keyValues);
				if (curd != null)
					curd.afterDelete(dao, keyValues);
			}
		});
	}

	protected void doDelete(Object[] keyValues) {
		dao.delete(entityName, keyValues);
	}

	/**
	 * 按条件删除对象
	 * 
	 * @param cnd
	 * @return 删除的记录数
	 */
	public int delete(final C cnd) {
		return dao.transaction(new Supplier<Integer>() {
			@Override
			public Integer get() {
				return doDelete(cnd);
			}
		});
	}

	protected int doDelete(C cnd) {
		return dao.delete(entityName, cnd);
	}

	protected void decorateItem(T obj) {
		if (nameRules != null)
			objectManager.setName(obj, nameRules);
	}

	protected List<T> decorateList(List<T> list) {
		for (T obj : list)
			decorateItem(obj);
		return list;
	}
}
