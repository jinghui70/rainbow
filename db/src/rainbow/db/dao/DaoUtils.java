package rainbow.db.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import rainbow.core.util.XmlBinder;
import rainbow.db.dao.model.Column;
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

	public static Object getResultSetValue(ResultSet rs, int index, Column column) throws SQLException {
		Object value = null;
		boolean wasNullCheck = false;
		switch (column.getType()) {
		case SMALLINT:
			value = rs.getShort(index);
			wasNullCheck = true;
			break;
		case INT:
			value = rs.getInt(index);
			wasNullCheck = true;
			break;
		case LONG:
			value = rs.getLong(index);
			wasNullCheck = true;
			break;
		case DOUBLE:
			value = rs.getDouble(index);
			wasNullCheck = true;
			break;
		case NUMERIC:
			value = rs.getBigDecimal(index);
			break;
		case DATE:
			value = rs.getDate(index);
			break;
		case TIME:
			value = rs.getTime(index);
			break;
		case TIMESTAMP:
			value = rs.getTimestamp(index);
			break;
		case CHAR:
		case VARCHAR:
		case CLOB:
			value = rs.getString(index);
			break;
		case BLOB:
			value = rs.getBytes(index);
			break;
		default:
			value = rs.getObject(index);
			break;
		}
		if (wasNullCheck && value != null && rs.wasNull()) {
			value = null;
		}
		return value;
	}

}
