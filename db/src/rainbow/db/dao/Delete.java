package rainbow.db.dao;

public class Delete extends Where<Delete> {

	public Delete(Dao dao, String entityName) {
		super(dao, entityName);
	}

	public int excute() {
		Sql sql = new Sql("DELETE FROM ").append(entity.getCode()).whereCnd(dao, entity, cnd);
		return dao.execSql(sql);
	}

}
