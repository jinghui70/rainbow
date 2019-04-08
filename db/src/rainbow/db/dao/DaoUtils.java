package rainbow.db.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import rainbow.core.util.XmlBinder;
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
}
