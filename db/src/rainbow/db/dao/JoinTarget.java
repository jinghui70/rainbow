package rainbow.db.dao;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.LinkedList;
import java.util.List;

import rainbow.core.util.Utils;

public class JoinTarget {

	private JoinType type;

	private String alias;

	private String target;
	
	private List<JoinCnd> cnd;

	public JoinType getType() {
		return type;
	}

	public void setType(JoinType type) {
		this.type = type;
	}

	public String getAlias() {
		return alias;
	}

	public String getTarget() {
		return target;
	}
	
	public List<JoinCnd> getCnd() {
		return cnd;
	}

	public JoinTarget(JoinType type, String target) {
		this.type = type;
		String[] s = Utils.splitTrim(target, ' ');
		checkArgument(s.length == 2, "[%s] need table alias", target);
		this.target = s[0];
		this.alias = s[1];
		this.cnd = new LinkedList<JoinCnd>();
	}
	
	public void on(String left, String right) {
		cnd.add(new JoinCnd(left, right));
	}

}
