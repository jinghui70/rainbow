package rainbow.db.dao;

public class Delete extends Where<Delete> {

	public Delete(Dao dao, String entityName) {
		super(dao, entityName);
	}

	public int excute() {
		Sql sql = new Sql("DELETE FROM ").append(entity.getCode()).whereCnd(dao, entity, cnd);
		return dao.execSql(sql);
	}

	public int byId(String id) {
		return where("id", id).excute();
	}
	
	public int byKey(Object...objects) {
		Sql sql = new Sql("DELETE FROM ").append(entity.getCode()).whereKey(entity, objects);
		return dao.execSql(sql);
	}
}
