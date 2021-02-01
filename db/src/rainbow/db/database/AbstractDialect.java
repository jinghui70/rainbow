package rainbow.db.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.jfinal.template.Engine;
import com.jfinal.template.Template;

import rainbow.core.model.exception.AppException;
import rainbow.core.util.StringBuilderX;
import rainbow.core.util.Utils;
import rainbow.db.dao.Dao;
import rainbow.db.dao.model.PureColumn;
import rainbow.db.model.Field;
import rainbow.db.model.Model;
import rainbow.db.model.Table;

/**
 * 数据库方言接口
 * 
 * @author lijinghui
 * 
 */
public abstract class AbstractDialect implements Dialect {

	protected Template template;

	@Override
	public String getTableListSql() {
		throw new AppException("{} not implement getTableListSql", getClass().getSimpleName());
	}

	@Override
	public List<Field> getColumn(String table, Dao dao) {
		throw new AppException("{} not implement getColumnSql", getClass().getSimpleName());
	}

	@Override
	public String clearTable(String tableName) {
		return "TRUNCATE TABLE " + tableName;
	}

	/**
	 * 生成DDL的模版内容 模版参数:
	 * 
	 * table: 仅输出一张表，
	 * 
	 * model: 输出整个model
	 * 
	 * drop: boolean, 输出model时是否输出drop table
	 * 
	 * @return
	 */
	@Override
	public String toDDL(Model model, boolean drop) {
		return getTemplate().renderToString(Map.of("model", model, "drop", drop));
	}

	@Override
	public String toDDL(Table table) {
		return getTemplate().renderToString(Map.of("table", table));
	}

	@Override
	public Template getTemplate() {
		if (template == null) {
			try {
				template = createTemplate();
			} catch (IOException e) {
				throw new RuntimeException("failed to create DDL template", e);
			}
		}
		return template;
	}

	synchronized Template createTemplate() throws IOException {
		if (template != null)
			return template;
		InputStream is = getTemplateStream();
		if (is == null)
			return DatabaseUtils.dialect("H2").getTemplate();
		String str = Utils.streamToString(is);
		return Engine.use().getTemplateByString(str, false);
	}

	protected InputStream getTemplateStream() {
		return getClass().getClassLoader()
				.getResourceAsStream("rainbow/db/template/" + getClass().getSimpleName() + ".tpl");
	}

	@Override
	public String dropTable(String tableName) {
		return String.format("DROP TABLE IF EXISTS %s CASCADE", tableName);
	}

	@Override
	public String addColumn(String tableName, PureColumn... columns) {
		StringBuilderX sb = new StringBuilderX("ALTER TABLE ").append(tableName).append(" ");
		for (PureColumn column : columns) {
			sb.append("ADD ");
			column2DDL(sb, column);
			sb.appendTempComma();
		}
		sb.clearTemp();
		return sb.toString();
	}

	@Override
	public String dropColumn(String tableName, String... columnNames) {
		StringBuilderX sb = new StringBuilderX("ALTER TABLE ").append(tableName).append(" DROP ");
		for (String name : columnNames) {
			sb.append(name).appendTempComma();
		}
		sb.clearTemp();
		return sb.toString();
	}

	@Override
	public String alterColumn(String tableName, PureColumn... columns) {
		StringBuilderX sb = new StringBuilderX("ALTER TABLE ").append(tableName).append(" ");
		for (PureColumn column : columns) {
			sb.append("MODIFY ");
			column2DDL(sb, column);
			sb.appendTempComma();
		}
		sb.clearTemp();
		return sb.toString();
	}

	protected void column2DDL(StringBuilderX sb, PureColumn column) {
		sb.append(column.getCode()).append(" ").append(column.getType());
		switch (column.getType()) {
		case CHAR:
		case VARCHAR:
			sb.append("(").append(column.getLength()).append(")");
			break;
		case NUMERIC:
			sb.append("(").append(column.getLength()).append(",").append(column.getPrecision()).append(")");
			break;
		default:
			break;
		}
		if (column.isMandatory())
			sb.append(" NOT NULL");
	}
}