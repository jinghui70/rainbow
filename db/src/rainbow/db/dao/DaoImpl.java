package rainbow.db.dao;

import static rainbow.core.util.Preconditions.checkArgument;
import static rainbow.core.util.Preconditions.checkNotNull;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

import rainbow.core.model.object.NameObject;
import rainbow.core.util.Utils;
import rainbow.db.dao.condition.C;
import rainbow.db.dao.condition.EmptyCondition;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;
import rainbow.db.database.Dialect;
import rainbow.db.database.DialectManager;
import rainbow.db.jdbc.DataAccessException;
import rainbow.db.jdbc.IncorrectResultSizeDataAccessException;
import rainbow.db.jdbc.JdbcTemplate;
import rainbow.db.jdbc.JdbcUtils;
import rainbow.db.jdbc.ResultSetExtractor;
import rainbow.db.jdbc.RowMapper;

public class DaoImpl extends NameObject implements Dao {

	private static Logger logger = LoggerFactory.getLogger(DaoImpl.class);

	protected Map<String, Entity> entityMap = ImmutableMap.<String, Entity>of();

	private Dialect dialect;

	private JdbcTemplate jdbcTemplate;

	public DaoImpl() {
		this(null, null);
	}

	public DaoImpl(DataSource dataSource) {
		this(dataSource, null);
	}

	public DaoImpl(DataSource dataSource, Map<String, Entity> entityMap) {
		setDataSource(dataSource);
		setEntityMap(entityMap);
	}

	public void setDataSource(DataSource dataSource) {
		if (dataSource == null) {
			this.jdbcTemplate = null;
			this.dialect = null;
		} else {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
			this.dialect = initDatabaseDialect(dataSource);
		}
	}

	public void setEntityMap(Map<String, Entity> entityMap) {
		this.entityMap = (entityMap == null) ? ImmutableMap.<String, Entity>of() : entityMap;
	}

	private Dialect initDatabaseDialect(DataSource dataSource) {
		try (Connection conn = dataSource.getConnection()) {
			return DialectManager.getDialect(conn.getMetaData());
		} catch (SQLException e) {
			logger.error("failed to init database dialect", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public Dialect getDialect() {
		return dialect;
	}

	@Override
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Override
	public Entity getEntity(String entityName) {
		checkNotNull(entityName);
		return checkNotNull(entityMap.get(entityName), "entity {} not defined", entityName);
	}

	@Override
	public NeoBean newNeoBean(String entityName) {
		Entity entity = getEntity(entityName);
		return new NeoBean(entity);
	}

	@Override
	public NeoBean makeNeoBean(Object obj) {
		return makeNeoBean(obj.getClass().getSimpleName(), obj);
	}

	@Override
	public NeoBean makeNeoBean(String entityName, Object obj) {
		Entity entity = getEntity(entityName);
		return new NeoBean(entity, obj);
	}

	@Override
	public void transaction(int level, Runnable atom) {
		jdbcTemplate.getTransactionManager().transaction(level, atom);
	}

	@Override
	public <T> T transaction(int level, Supplier<T> atom) {
		return jdbcTemplate.getTransactionManager().transaction(level, atom);
	}

	@Override
	public void transaction(Runnable atom) {
		transaction(Connection.TRANSACTION_READ_COMMITTED, atom);
	}

	@Override
	public <T> T transaction(Supplier<T> atom) {
		return transaction(Connection.TRANSACTION_READ_COMMITTED, atom);
	}

	@Override
	public boolean existsOfTable(String tableName) {
		String sql = String.format("SELECT COUNT(1) FROM %s where 1>1", tableName);
		try {
			jdbcTemplate.queryForInt(sql);
		} catch (DataAccessException e) {
			return false;
		}
		return true;
	}

	private NeoBean toNeoBean(Object obj) {
		if (obj instanceof NeoBean)
			return (NeoBean) obj;
		else
			return makeNeoBean(obj);
	}

	@Override
	public int insert(Object obj) {
		return insertNeoBean(toNeoBean(obj));
	}

	private int insertNeoBean(NeoBean neo) {
		Entity entity = neo.getEntity();
		Sql sql = new Sql(entity.getColumns().size()).append("insert into ").append(entity.getDbName()).append("(");

		StringBuilder sb = new StringBuilder();
		entity.getColumns().stream().forEach(column -> {
			Object v = neo.getObject(column);
			if (v == null) {
				checkArgument(!column.isMandatory(), "property {} cannot be null", column.getName());
			} else {
				sql.append(column.getDbName());
				if (NOW.equals(v)) {
					sb.append(dialect.now()).append(',');
				} else {
					sql.addParam(v);
					sb.append("?,");
				}
				sql.appendTempComma();
			}
		});
		sql.clearTemp().append(") values (").append(sb.substring(0, sb.length() - 1)).append(")");
		return execSql(sql);
	}

	@Override
	public <T> void insert(List<T> list) {
		insert(list, 1000, null);
	}

	@Override
	public <T> void insert(List<T> list, int batchSize, ObjectBatchParamSetter<T> setter) {
		if (Utils.isNullOrEmpty(list))
			return;
		Object obj = list.get(0);
		NeoBean neo = toNeoBean(obj);
		Entity entity = neo.getEntity();
		StringBuilder sql = new StringBuilder().append("insert into ").append(entity.getDbName()).append("(");
		int i = 0;
		for (Column column : entity.getColumns()) {
			if (i == 0) {
				i++;
			} else {
				sql.append(",");
			}
			sql.append(column.getDbName());
		}
		sql.append(") values (?");
		for (i = 1; i < entity.getColumns().size(); i++)
			sql.append(",?");
		sql.append(")");

		if (setter == null)
			setter = new ObjectBatchParamSetter<T>();
		setter.init(entity, list);
		jdbcTemplate.batchUpdate(sql.toString(), setter, batchSize);
	}

	@Override
	public void replace(Object obj) {
		NeoBean neo = toNeoBean(obj);
		Entity entity = neo.getEntity();
		Sql sql = new Sql().append("select count(1) from ").append(entity.getDbName()).whereKey(neo);
		int count = queryForInt(sql);
		if (count == 0)
			insert(neo);
		else
			update(neo);
	}

	@Override
	public void clear(String entityName) {
		Entity entity = getEntity(entityName);
		jdbcTemplate.update(getDialect().clearTable(entity.getDbName()));
	}

	@Override
	public int delete(Object obj) {
		NeoBean neo = toNeoBean(obj);
		Entity entity = neo.getEntity();
		Sql sql = new Sql(entity.getKeyCount()).append("delete from ").append(entity.getDbName()).whereKey(neo);
		return execSql(sql);
	}

	@Override
	public int delete(String entityName, C cnd) {
		Entity entity = getEntity(entityName);
		Sql sql = new Sql("delete from ").append(entity.getDbName()).whereCnd(this, entity, cnd);
		return execSql(sql);
	}

	@Override
	public int delete(String entityName, Object... values) {
		Entity entity = getEntity(entityName);
		Sql sql = new Sql("delete from ").append(entity.getDbName()).whereKey(entity, values);
		return execSql(sql);
	}

	@Override
	public int update(Object obj) {
		NeoBean neo = toNeoBean(obj);
		return update(neo);
	}

	@Override
	public int update(String entityName, Object obj) {
		NeoBean neo = makeNeoBean(entityName, obj);
		return update(neo);
	}

	public int update(NeoBean neo) {
		Entity entity = neo.getEntity();
		checkArgument(entity.getKeyCount() > 0, "cann't update 0 key entity {}", entity.getName());
		Sql sql = new Sql().append("update ").append(entity.getDbName()).append(" set ");

		neo.valueColumns().stream().forEach(column -> {
			if (column.isKey())
				return;
			sql.append(column.getDbName());
			Object v = neo.getObject(column);
			if (v == null)
				sql.append("=null");
			else if (NOW.equals(v))
				sql.append("=").append(dialect.now());
			else
				sql.append("=?").addParam(v);
			sql.appendTempComma();
		});
		sql.clearTemp();
		sql.whereKey(neo);
		return execSql(sql);
	}

	@Override
	public int update(String entityName, C cnd, U... items) {
		Entity entity = getEntity(entityName);
		Sql sql = new Sql("UPDATE ").append(entity.getDbName()).append(" SET ");
		for (U item : items) {
			item.toSql(entity, sql);
			sql.appendTempComma();
		}
		sql.clearTemp();
		sql.whereCnd(this, entity, cnd);
		return execSql(sql);
	}

	@Override
	public NeoBean fetch(String entityName, Object... keyValues) {
		Entity entity = getEntity(entityName);
		Sql sql = new Sql().append("select * from ").append(entity.getDbName()).whereKey(entity, keyValues);
		return queryForObject(sql, new NeoBeanMapper(entity));
	}

	@Override
	public NeoBean fetch(String entityName, C cnd) {
		Entity entity = getEntity(entityName);
		Sql sql = new Sql().append("select * from ").append(entity.getDbName()).whereCnd(this, entity, cnd);
		return queryForObject(sql, new NeoBeanMapper(entity));
	}

	@Override
	public <T> T fetch(Class<T> clazz, Object... keyValues) {
		NeoBean neo = fetch(clazz.getSimpleName(), keyValues);
		return neo == null ? null : neo.bianShen(clazz);
	}

	@Override
	public void query(Select select, Consumer<Map<String, Object>> consumer) {
		Sql sql = select.build(this);
		Map<String, Object> map = new HashMap<String, Object>();
		doQuery(sql, rs -> {
			map.clear();
			int index = 1;
			for (Field field : select.getFields()) {
				try {
					Object value = JdbcUtils.getResultSetValue(rs, index++, field.getColumn().dataClass());
					map.put(field.getName(), value);
				} catch (SQLException e) {
					throw new DataAccessException(e);
				}
			}
			consumer.accept(map);
		});
	}

	@Override
	public NeoBean queryForObject(Select select) {
		Sql sql = select.build(this);
		return queryForObject(sql, new NeoBeanMapper(select.getEntity(), select.getFields()));
	}

	@Override
	public List<NeoBean> queryForList(Select select) {
		Sql sql = select.build(this);
		return queryForList(sql, new NeoBeanMapper(select.getEntity(), select.getFields()));
	}

	@Override
	public <T> T queryForObject(Select select, Class<T> clazz) {
		Sql sql = select.build(this);
		if (select.getSelCount() == 1)
			return queryForObject(sql, clazz);
		return queryForObject(sql, new ObjectRowMapper<T>(select, clazz));
	}

	@Override
	public <T> List<T> queryForList(Select select, Class<T> clazz) {
		Sql sql = select.build(this);
		if (select.getSelCount() == 1)
			return queryForList(sql, clazz);
		return queryForList(sql, new ObjectRowMapper<T>(select, clazz));
	}

	@Override
	public Map<String, Object> queryForMap(Select select) {
		Sql sql = select.build(this);
		return queryForObject(sql, new MapRowMapper(select));
	}

	@Override
	public List<Map<String, Object>> queryForMapList(Select select) {
		Sql sql = select.build(this);
		return queryForList(sql, new MapRowMapper(select));
	}

	@Override
	public <T> PageData<T> pageQuery(Select select, Class<T> clazz) {
		checkNotNull(select.getPager());
		int count = count(select);
		if (count == 0) {
			return new PageData<T>();
		} else {
			List<T> list = queryForList(select, clazz);
			return new PageData<T>(count, list);
		}
	}

	@Override
	public <T> PageData<T> pageQuery(Select select, RowMapper<T> mapper) {
		checkNotNull(select.getPager());
		int count = count(select);
		if (count == 0) {
			return new PageData<T>();
		} else {
			Sql sql = select.build(this);
			List<T> list = queryForList(sql, mapper);
			return new PageData<T>(count, list);
		}
	}

	@Override
	public int queryForInt(Select sb) {
		return queryForInt(sb.build(this));
	}

	@Override
	public int count(String entityName, C cnd) {
		Entity entity = getEntity(entityName);
		Sql sql = new Sql().append("select count(1) from ").append(entity.getDbName()).whereCnd(this, entity, cnd);
		return queryForInt(sql);
	}

	@Override
	public int count(String entityName) {
		return count(entityName, EmptyCondition.INSTANCE);
	}

	@Override
	public int count(Select select) {
		return queryForInt(select.buildCount(this));
	}

	@Override
	public <T> T queryForObject(Sql sql, Class<T> requiredType) {
		try {
			if (sql.noParams())
				return jdbcTemplate.queryForObject(sql.getSql(), requiredType);
			else
				return jdbcTemplate.queryForObject(sql.getSql(), sql.getParamArray(), requiredType);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public <T> T queryForObject(Sql sql, RowMapper<T> mapper) {
		try {
			if (sql.noParams())
				return jdbcTemplate.queryForObject(sql.getSql(), mapper);
			else
				return jdbcTemplate.queryForObject(sql.getSql(), sql.getParamArray(), mapper);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public int queryForInt(Sql sql) {
		Integer result = queryForObject(sql, Integer.class);
		return result == null ? 0 : result.intValue();
	}

	@Override
	public <T> List<T> queryForList(Sql sql, Class<T> requiredType) {
		if (sql.noParams())
			return jdbcTemplate.queryForList(sql.getSql(), requiredType);
		else
			return jdbcTemplate.queryForList(sql.getSql(), sql.getParamArray(), requiredType);
	}

	@Override
	public <T> List<T> queryForList(Sql sql, RowMapper<T> mapper) {
		if (sql.noParams())
			return jdbcTemplate.query(sql.getSql(), mapper);
		else
			return jdbcTemplate.query(sql.getSql(), sql.getParamArray(), mapper);
	}

	@Override
	public void doQuery(Sql sql, Consumer<ResultSet> consumer) {
		if (sql.noParams())
			jdbcTemplate.query(sql.getSql(), consumer);
		else
			jdbcTemplate.query(sql.getSql(), sql.getParamArray(), consumer);
	}

	@Override
	public <T> T doQuery(Sql sql, ResultSetExtractor<T> rse) {
		if (sql.noParams())
			return jdbcTemplate.query(sql.getSql(), rse);
		else
			return jdbcTemplate.query(sql.getSql(), sql.getParamArray(), rse);
	}

	@Override
	public int execSql(Sql sql) {
		if (sql.noParams())
			return jdbcTemplate.update(sql.getSql());
		else
			return jdbcTemplate.update(sql.getSql(), sql.getParamArray());
	}

	@Override
	public int execSql(String sql, Object... params) {
		if (params.length == 0)
			return jdbcTemplate.update(sql);
		else
			return jdbcTemplate.update(sql, params);
	}

	public void linkPatch(JSONObject patch) {
		Type type = new TypeReference<List<DaoImplPatchEntity>>() {
		}.getType();
		List<DaoImplPatchEntity> entities = patch.getObject("entity", type);
		if (entities != null) {
			entities.stream().forEach(e -> {
				Entity entity = entityMap.get(e.getName());
				entity.setTags(e.getTags());
				if (e.getColumns() != null) {
					e.getColumns().stream().forEach(c -> {
						Column column = entity.getColumn(c.getName());
						column.setTags(c.getTags());
					});
				}
			});
		}
		type = new TypeReference<List<DaoImplPatchLink>>() {
		}.getType();
		List<DaoImplPatchLink> links = patch.getObject("reference", type);
		if (links != null) {
			links.forEach(linkDef -> {
				DaoImplPatchLinkPart left = linkDef.getLeft();
				Entity leftEntity = entityMap.get(left.getEntity());
				List<Column> leftColumns = left.getFields().stream().map(leftEntity::getColumn)
						.collect(Collectors.toList());
				DaoImplPatchLinkPart right = linkDef.getRight();
				Entity rightEntity = entityMap.get(right.getEntity());
				List<Column> rightColumns = right.getFields().stream().map(rightEntity::getColumn)
						.collect(Collectors.toList());
				checkArgument(leftColumns.size() == rightColumns.size());
				if (Utils.hasContent(left.getName())) {
					Link link = new Link();
					link.setName(left.getName());
					link.setLabel(left.getLabel());
					link.setColumns(leftColumns);
					link.setLinkEntity(rightEntity);
					link.setLinkColumns(rightColumns);
					link.setOne(right.isOne());
					leftEntity.addLink(link);
				}
				if (Utils.hasContent(right.getName())) {
					Link link = new Link();
					link.setName(right.getName());
					link.setLabel(right.getLabel());
					link.setColumns(rightColumns);
					link.setLinkEntity(leftEntity);
					link.setLinkColumns(leftColumns);
					link.setOne(left.isOne());
					rightEntity.addLink(link);
				}
			});
		}
	}

}
