package rainbow.db.dao;

import rainbow.db.dao.object.IdDao;
import rainbow.db.dao.object._Person;

public class _PersonDao extends IdDao<Integer, _Person> {

	public _PersonDao() {
		super(_Person.class);
	}


}
