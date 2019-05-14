package rainbow.db.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import rainbow.db.dao.condition.C;
import rainbow.db.dao.model.Entity;
import rainbow.db.dao.model.Link;

public class SelectBuildContext {

	private Dao dao;

	private Entity entity;

	private List<Link> links = new ArrayList<Link>();

	private List<SelectField> selectFields;

	public SelectBuildContext(Dao dao, Entity entity, String[] select) {
		this.dao = dao;
		this.entity = entity;
		if (select == null || select.length == 0) {
			selectFields = entity.getColumns().stream().map(SelectField::fromColumn).collect(Collectors.toList());
		} else {
			selectFields = Arrays.stream(select).map(this::createSelectField).collect(Collectors.toList());
		}
	}

	public Dao getDao() {
		return dao;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setCnd(C cnd) {
		cnd.initField(this::createField);
	}

	public void setOrderBy(List<OrderBy> orderBy) {
		orderBy.forEach(o -> o.initField(this::createField));
	}

	private SelectField createSelectField(String id) {
		SelectField field = SelectField.parse(id, entity);
		Link link = field.getLink();
		if (link != null && !links.contains(link))
			links.add(link);
		return field;
	}

	private QueryField createField(String id) {
		QueryField field = QueryField.parse(id, this);
		Link link = field.getLink();
		if (link != null && !links.contains(link))
			links.add(link);
		return field;
	}

	public List<SelectField> getSelectFields() {
		return selectFields;
	}

	/**
	 * 查找selectFields里面指定了别名的字段，主要用于翻译order by
	 * 
	 * @param alias
	 * @return
	 */
	public Optional<SelectField> alias2selectField(String alias) {
		return selectFields.parallelStream().filter(field -> alias.equals(field.getAlias())).findAny();
	}

	public List<Link> getLinks() {
		return links;
	}

	public boolean isLinkSql() {
		return links.size() > 0;
	}

	public char getLinkAlias(Link link) {
		int index = links.indexOf(link) + 1;
		return (char) ('A' + index);
	}

}
