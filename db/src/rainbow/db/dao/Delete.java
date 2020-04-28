package rainbow.db.dao;

public class Delete extends Where<Delete> {

	public Delete(Dao dao, String entityName) {
		super(dao, entityName);
	}

	public int excute() {
		return new Sql("DELETE FROM ").append(entity.getCode()).whereCnd(dao, entity, cnd).execute(dao);
	}

	public int byId(String id) {
		return where("id", id).excute();
	}

	public int byKey(Object... objects) {
		return new Sql("DELETE FROM ").append(entity.getCode()).whereKey(entity, objects).execute(dao);
	}
}
