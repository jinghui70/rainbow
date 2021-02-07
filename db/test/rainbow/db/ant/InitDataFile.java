package rainbow.db.ant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.jfinal.template.source.ClassPathSource;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import rainbow.core.util.Utils;
import rainbow.db.database.DatabaseUtils;
import rainbow.db.model.Model;
import rainbow.db.model.Table;

@Command(name = "initdata")
public class InitDataFile implements Runnable {

	@Parameters(description = "model file")
	String modelFile;

	@Parameters(description = "output path")
	Path output;

	/**
	 * 指定输出的表，不设置输出全部表
	 */
	@Parameters(index = "2..*")
	List<String> entitys;

	@Override
	public void run() {
		Template template = Engine.use().getTemplate(new ClassPathSource("rainbow/db/ant", "InitDataFile.tpl"));

		Path root = Paths.get("..", "rainbow", "conf", "db");
		Model model = DatabaseUtils.loadModel(root.resolve(modelFile));
		if (Files.notExists(output)) {
			try {
				Files.createDirectories(output);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		List<Table> tables = model.getTables();
		if (Utils.hasContent(entitys)) {
			Map<String, Table> map = tables.stream().collect(Collectors.toMap(Table::getName, Function.identity()));
			tables = Utils.transform(entitys, map::get);
		}
		for (Table t : tables) {
			Path file = output.resolve(t.getName() + ".json");
			if (Files.isRegularFile(file))
				throw new RuntimeException(file.getFileName().toString() + " already exist!");
			template.render(ImmutableMap.of("table", t), file.toFile());
		}
	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new InitDataFile()).execute(args);
		System.exit(exitCode);
	}
}
