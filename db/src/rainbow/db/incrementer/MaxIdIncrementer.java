package rainbow.db.incrementer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Strings;

import rainbow.core.util.ioc.InitializingBean;
import rainbow.core.util.ioc.Inject;
import rainbow.db.dao.Dao;
import rainbow.db.dao.Select;
import rainbow.db.dao.Sql;

public class MaxIdIncrementer extends AbstractIncrementer implements InitializingBean {

	private Dao dao;

	private String tblName;

	private String entityName;

	public MaxIdIncrementer() {
	}
	
	public MaxIdIncrementer(Dao dao, String entityName) {
		this.dao = dao;
		this.entityName = entityName;
	}

	@Inject
	public void setDao(Dao dao) {
		this.dao = dao;
	}

	public void setTblName(String tblName) {
		this.tblName = tblName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		checkNotNull(dao, "Property 'dao' is required");
		checkArgument(!Strings.isNullOrEmpty(tblName) || !Strings.isNullOrEmpty(entityName),
				"either tblName or entityName is required");
	}

	@Override
	protected long getNextKey() {
		if (Strings.isNullOrEmpty(tblName)) {
			Long id = dao.queryForObject(new Select("max(id)").from(entityName), Long.class);
			return id == null ? 1 : id.longValue() + 1;
		} else {
			Long id = dao.queryForObject(new Sql("SELECT max(ID) FROM ").append(tblName), Long.class);
			return id == null ? 1 : id.longValue() + 1;
		}
	}
}
