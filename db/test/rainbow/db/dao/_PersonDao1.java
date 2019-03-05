package rainbow.db.dao;

import rainbow.db.dao.object.IdDao;
import rainbow.db.dao.object._Person;
import rainbow.db.incrementer.Incrementer;

public class _PersonDao1 extends IdDao<Integer, _Person> {

	public _PersonDao1() {
		super(_Person.class);
	}

	@Override
	protected Incrementer createIncrementer() {
		return null;
	}

}
