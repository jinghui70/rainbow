package rainbow.db.dao;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.google.common.base.Objects;

import rainbow.core.model.exception.DuplicateCodeException;
import rainbow.core.model.exception.DuplicateNameException;
import rainbow.core.util.Utils;
import rainbow.core.util.XmlBinder;
import rainbow.db.dao.condition.C;
import rainbow.db.dao.condition.Op;
import rainbow.db.dao.model.Entity;
import rainbow.db.jdbc.JdbcUtils;
import rainbow.db.model.Column;
import rainbow.db.model.Model;

public abstract class DaoUtils {

	private static Transformer transformer;

	static {
		try (InputStream xsltStream = DaoUtils.class.getResourceAsStream("h2.xsl")) {
			TransformerFactory tf = TransformerFactory.newInstance();
			transformer = tf.newTransformer(new StreamSource(xsltStream));
		} catch (TransformerConfigurationException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	synchronized public static String transform(Model model) {
		try {
			String xmltext = new XmlBinder<Model>(Model.class).marshalString(model);
			StringReader reader = new StringReader(xmltext);
			StringWriter writer = new StringWriter();
			transformer.transform(new StreamSource(reader), new StreamResult(writer));
			return writer.toString();
		} catch (JAXBException | TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	public static NeoBean toNeoBean(ResultSet rs, Entity entity) {
		NeoBean bean = new NeoBean(entity);
		for (Column column : entity.getColumns()) {
			try {
				int index = rs.findColumn(column.getDbName());
				bean.setObject(column, JdbcUtils.getResultSetValue(rs, index, column.getType().dataClass()));
			} catch (SQLException e) {
				// 没有对应Column的结果字段，忽略了
			}
		}
		return bean;
	}

	/**
	 * 检查名称是否重复
	 * 
	 * @param obj
	 * @param cnd
	 * @throws DuplicateNameException
	 */
	public static void checkDuplicateName(Dao dao, String entityName, String name, C cnd, String obj)
			throws DuplicateNameException {
		if (dao.count(entityName, C.make("name", name).and(cnd)) > 0) {
			throw new DuplicateNameException(name, obj);
		}
	}

	/**
	 * 检查名称是否重复
	 * 
	 * @param obj
	 * @param cnd
	 * @throws DuplicateNameException
	 */
	public static void checkDuplicateCode(Dao dao, String entityName, String code, C cnd, String obj)
			throws DuplicateNameException {
		if (dao.count(entityName, C.make("code", code).and(cnd)) > 0) {
			throw new DuplicateCodeException(code, obj);
		}
	}

	/**
	 * 增加一个nest对象时，计算左右值
	 * 
	 * @param dao
	 * @param neo
	 * @param cnd
	 * @param rootId
	 */
	public static void calcLeftRight(Dao dao, NeoBean neo, C cnd, Object rootId) {
		String entityName = neo.getEntity().getName();
		int right;
		if (Objects.equal(neo.getObject("pid"), rootId)) {
			right = dao.queryForInt(new Select("max(right)").from(entityName).where(cnd));
			right++;
		} else {
			right = dao.queryForInt(new Select("right").from(entityName).where("id", neo.getObject("pid")));
			if (right <= 0) {
				String err = String.format("id[%s] has a wrong parent id[%s]", neo.getObject("id"),
						neo.getObject("pid"));
				throw new IllegalArgumentException(err);
			}
			dao.update(entityName, cnd.and("left", Op.Greater, right), U.set("left", '+', 2), U.set("right", '+', 2));
			dao.update(entityName, cnd.and("left", Op.Less, right).and("right", Op.GreaterEqual, right),
					U.set("right", '+', 2));
		}
		neo.setValue("left", right);
		neo.setValue("right", right + 1);
	}

	/**
	 * 返回求指定对象所有下级id的sql
	 * 
	 * @param entityName
	 * @param cnd        额外条件，（如果是森林，可以是树标识条件）
	 * @param left
	 * @param right
	 * @param withSelf   是否包含自己
	 * @return
	 */
	public static Select subIdSql(String entityName, C cnd, int left, int right, boolean withSelf) {
		Select sb = new Select("id").from(entityName).where(cnd);
		if (withSelf) {
			return sb.and("left", Op.GreaterEqual, left).and("left", Op.LessEqual, right);
		} else {
			return sb.and("left", Op.Greater, left).and("left", Op.Less, right);
		}
	}

	/**
	 * 返回获得一组节点所有下级id的sql
	 * 
	 * @param entityName
	 * @param ids
	 * @return
	 */
	public static <I> Sql subIdSql(String entityName, List<I> ids) {
		checkArgument(!Utils.isNullOrEmpty(ids));
		Sql sql = new Sql().addParam(Collections.<Object>unmodifiableList(ids));
		sql.append("select A.ID from ").append(entityName).append(" A,").append(entityName).append(" B")
				.append(" where A.LFT>=B.LFT and A.LFT<=B.RGT and B.ID in (");
		Utils.repeat(sql.getStringBuilder(), "?", ',', ids.size());
		sql.append(")");
		return sql;
	}

}
