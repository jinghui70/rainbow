package rainbow.db.dao;

import static rainbow.core.util.Preconditions.checkArgument;
import static rainbow.core.util.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

import rainbow.core.util.StringBuilderX;
import rainbow.core.util.Utils;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.PureColumn;
import rainbow.db.database.Dialect;
import rainbow.db.jdbc.ColumnMapRowMapper;
import rainbow.db.jdbc.DataAccessException;
import rainbow.db.jdbc.JdbcTemplate;
import rainbow.db.model.Table;

public class DaoImpl implements Dao {

	private static Logger logger = LoggerFactory.getLogger(DaoImpl.class);

	protected Map<String, Entity> entityMap = ImmutableMap.<String, Entity>of();

	protected Dialect dialect;

	private JdbcTemplate jdbcTemplate;

	public DaoImpl(DataSource dataSource, Dialect dialect, Map<String, Entity> entityMap) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.dialect = dialect;
		setEntityMap(entityMap);
	}

	public DaoImpl(Dao ref, Map<String, Entity> entityMap) {
		this.dialect = ref.getDialect();
		this.jdbcTemplate = ref.getJdbcTemplate();
		setEntityMap(entityMap);
	}

	public void setEntityMap(Map<String, Entity> entityMap) {
		this.entityMap = (entityMap == null) ? ImmutableMap.<String, Entity>of() : entityMap;
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
			jdbcTemplate.queryForObject(sql, ColumnMapRowMapper.instance);
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
		Sql sql = new Sql(entity.getColumns().size()).append("insert into ").append(entity.getCode()).append("(");
		logger.debug("inserting entity:{}", entity.getName());
		StringBuilderX sb = new StringBuilderX();
		entity.getColumns().stream().forEach(column -> {
			Object v = neo.getObject(column);
			if (v == null) {
				checkArgument(!column.isMandatory(), "property {} cannot be null", column.getName());
			} else {
				sql.append(column.getCode());
				if (NOW.equals(v)) {
					sb.append(dialect.now());
				} else {
					sql.addParam(v);
					sb.append("?");
				}
				sb.appendTempComma();
				sql.appendTempComma();
			}
		});
		sql.clearTemp().append(") values (").append(sb.clearTemp()).append(")");
		return sql.execute(this);
	}

	@Override
	public <T> void insert(List<T> list) {
		insert(list, 250, true);
	}

	@Override
	public <T> void insert(List<T> list, int batchSize, boolean transaction) {
		if (Utils.isNullOrEmpty(list))
			return;
		Object obj = list.get(0);
		NeoBean neo = toNeoBean(obj);
		Entity entity = neo.getEntity();
		StringBuilderX sql = new Sql("insert into ").append(entity.getCode()).append("(");
		for (Column column : entity.getColumns()) {
			sql.append(column.getCode());
			sql.appendTempComma();
		}
		sql.clearTemp();
		sql.append(") values (?");
		for (int i = 1; i < entity.getColumns().size(); i++)
			sql.append(",?");
		sql.append(")");

		ObjectBatchParamSetter<T> setter = new ObjectBatchParamSetter<T>();
		setter.init(entity, list);
		jdbcTemplate.batchUpdate(sql.toString(), setter, batchSize, transaction);
	}

	@Override
	public void clear(String entityName) {
		Entity entity = getEntity(entityName);
		jdbcTemplate.update(getDialect().clearTable(entity.getCode()));
	}

	@Override
	public int delete(Object obj) {
		NeoBean neo = toNeoBean(obj);
		Entity entity = neo.getEntity();
		Sql sql = new Sql(entity.getKeyCount()).append("delete from ").append(entity.getCode()).whereKey(neo);
		return sql.execute(this);
	}

	@Override
	public int update(Object obj) {
		NeoBean neo = toNeoBean(obj);
		return update(neo);
	}

	public int update(NeoBean neo) {
		Entity entity = neo.getEntity();
		checkArgument(entity.getKeyCount() > 0, "cann't update 0 key entity {}", entity.getName());
		Sql sql = new Sql().append("update ").append(entity.getCode()).append(" set ");

		neo.valueColumns().stream().forEach(column -> {
			if (column.isKey())
				return;
			sql.append(column.getCode());
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
		return sql.execute(this);
	}

	@Override
	public void replace(Object obj) {
		NeoBean neo = toNeoBean(obj);
		Entity entity = neo.getEntity();
		Sql sql = new Sql("select count(1) from ").append(entity.getCode()).whereKey(neo);
		int count = sql.queryForObject(this, int.class);
		if (count == 0)
			insert(neo);
		else
			update(neo);
	}

	@Override
	public NeoBean fetch(String entityName, Object... keyValues) {
		Entity entity = getEntity(entityName);
		Sql sql = new Sql().append("select ");
		entity.getColumns().forEach(c -> {
			sql.append(c.getCode()).appendTempComma();
		});
		sql.clearTemp().append(" from ").append(entity.getCode()).whereKey(entity, keyValues);
		return sql.queryForObject(this, new NeoBeanMapper(entity));
	}

	@Override
	public <T> T fetch(Class<T> clazz, Object... keyValues) {
		NeoBean neo = fetch(clazz.getSimpleName(), keyValues);
		return neo == null ? null : neo.bianShen(clazz);
	}

	@Override
	public Select select() {
		return new Select(this);
	}

	@Override
	public Select select(String selectStr) {
		return new Select(this, Utils.splitTrim(selectStr, ','));
	}

	@Override
	public Select select(String[] fields) {
		return new Select(this, fields);
	}

	@Override
	public Update update(String entityName) {
		return new Update(this, entityName);
	}

	@Override
	public Delete delete(String entityName) {
		return new Delete(this, entityName);
	}

	@Override
	public int execSql(String sql, Object... params) {
		if (params.length == 0)
			return jdbcTemplate.update(sql);
		else
			return jdbcTemplate.update(sql, params);
	}

	@Override
	public void createTable(Table table) {
		execSql(dialect.toDDL(table));
	}

	@Override
	public void dropTable(String tableName) {
		if (existsOfTable(tableName))
			execSql(dialect.dropTable(tableName));
	}

	@Override
	public void addColumn(String tableName, PureColumn... columns) {
		execSql(dialect.addColumn(tableName, columns));
	}

	@Override
	public void dropColumn(String tableName, String... columnNames) {
		execSql(dialect.dropColumn(tableName, columnNames));

	}

	@Override
	public void alterColumn(String tableName, PureColumn... columns) {
		execSql(dialect.alterColumn(tableName, columns));
	}

	@Override
	public void close() throws IOException {
		getJdbcTemplate().close();
	}

}
