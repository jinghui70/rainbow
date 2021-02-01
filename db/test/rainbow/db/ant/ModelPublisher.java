package rainbow.db.ant;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.jfinal.template.source.ClassPathSource;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import rainbow.db.database.DatabaseUtils;
import rainbow.db.model.Model;

@Command(name = "publish")
public class ModelPublisher implements Runnable {

	public static void publish(Model model, boolean drop, Template template, Path outputFile) {
		Map<String, Object> map = Map.of("model", model, "drop", drop);
		template.render(map, outputFile.toFile());
	}

	public static void publish(Model model, boolean drop, Template template, Writer writer) {
		Map<String, Object> map = Map.of("model", model, "drop", drop);
		template.render(map, writer);
	}

	public static Template getTemplate(String databaseType) {
		return Engine.use().getTemplate(new ClassPathSource("rainbow/db/template", databaseType + "tpl"));
	}

	public static void publish(Model model, boolean drop, String databaseType, Path outputFile) {
		Template template = getTemplate(databaseType);
		publish(model, drop, template, outputFile);
	}

	public static void publish(Model model, String databaseType, Path outputFile) {
		publish(model, true, databaseType, outputFile);
	}

	public static void publish(Model model, Path outputFile) {
		publish(model, false, "Document", outputFile);
	}

	@Parameters(description = "model file")
	Path modelFile;

	@Parameters(description = "template file")
	Path templateFile;

	@Parameters(description = "output file")
	Path outputFile;

	@Option(names = { "-d", "--drop" }, defaultValue = "true")
	boolean drop = true;

	@Override
	public void run() {
		Model model = DatabaseUtils.loadModel(modelFile);
		try {
			String tmpstr = Files.readString(templateFile);
			Template template = Engine.use().getTemplateByString(tmpstr, false);
			template.render(Map.of("model", model, "drop", drop), outputFile.toFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new ModelPublisher()).execute(args);
		System.exit(exitCode);
	}
}
