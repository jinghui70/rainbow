package rainbow.db.dao;

import static com.google.common.base.Preconditions.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import rainbow.core.util.Consumer;
import rainbow.core.util.Utils;
import rainbow.db.dao.model.Entity;
import rainbow.db.model.Column;

public class Join {

	private String alias;

	private String master;
	
	private LinkedList<JoinTarget> targets = Lists.newLinkedList();

	private Join(String master) {
		String[] s = Utils.splitTrim(master, ' ');
		checkArgument(s.length == 2, "[%s] need table alias", master);
		this.master = s[0];
		this.alias = s[1];
	}

	public String getAlias() {
		return alias;
	}

	public String getMaster() {
		return master;
	}

	public List<JoinTarget> getTargets() {
		return targets;
	}

	public Join join(String target) {
		JoinTarget jt = new JoinTarget(JoinType.INNER, target);
		return addTarget(jt);
	}

	public Join leftJoin(String target) {
		JoinTarget jt = new JoinTarget(JoinType.LEFT, target);
		return addTarget(jt);
	}

	public Join rightJoin(String target) {
		JoinTarget jt = new JoinTarget(JoinType.RIGHT, target);
		return addTarget(jt);
	}

	public Join on(String left, String right) {
		checkState(!targets.isEmpty());
		targets.getLast().on(left, right);
		return this;
	}
	
	private Join addTarget(JoinTarget target) {
		targets.add(target);
		return this;
	}

	public void build(Map<String, Entity> entityMap, final Sql sql) {
		final Entity entity = entityMap.get(alias);
		sql.append(" FROM ").append(entity.getDbName()).append(' ').append(alias);
		for (final JoinTarget t : targets) {
			final Entity targetEntity = entityMap.get(t.getAlias());
			checkNotNull(targetEntity, "join target entity [%s] not found", t.getTarget());
			sql.append(' ').append(t.getType().getText()).append(' ').append(targetEntity.getDbName()).append(' ')
					.append(t.getAlias()).append(" ON(");
			
			Utils.join(" AND ", sql, t.getCnd(), new Consumer<JoinCnd>() {
				@Override
				public void consume(JoinCnd cnd) {
					Column left = entity.getColumn(cnd.getLeft());
					checkNotNull(left, "column [%s] not found in entity [%s]", cnd.getLeft(), master);
					Column right = targetEntity.getColumn(cnd.getRight());
					checkNotNull(right, "column [%s] not found in entity [%s]", cnd.getRight(), t.getTarget());
					sql.append(alias).append('.').append(left.getDbName()).append("=").append(t.getAlias()).append('.')
					.append(right.getDbName());
				}
			});
			sql.append(")");
		}
	}

	public static Join make(String master) {
		return new Join(master);
	}

}