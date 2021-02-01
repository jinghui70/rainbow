package rainbow.db.ant;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import rainbow.core.util.StringBuilderX;
import rainbow.core.util.Utils;
import rainbow.core.util.json.JSON;
import rainbow.db.dao.NeoBean;
import rainbow.db.dao.model.Column;
import rainbow.db.dao.model.Entity;
import rainbow.db.database.DatabaseUtils;
import rainbow.db.model.Model;

@Command(name = "dbwork")
public class DeployWork implements Runnable {

	@Option(names = { "-h", "--help" }, usageHelp = true, description = "display this help message")
	boolean usageHelpRequested;

	@Parameters(description = "model file name")
	String modelFile;

	@Parameters(index = "1..*", description = "init data dir")
	List<Path> dataDirs;

	@Option(names = { "-d", "--database" })
	String database;

	@Override
	public void run() {
		if (!modelFile.endsWith(".rdmx"))
			throw new RuntimeException("invalide model file name");
		Path m = Paths.get("..", "rainbow", "conf", "db", modelFile);
		Model model = DatabaseUtils.loadModel(m);

		String name = Utils.substringBefore(modelFile, ".rdmx");
		Path outputDir = Paths.get("..", "dist", "db");
		ModelPublisher.publish(model, outputDir.resolve(String.format("%s.md", name)));
		ModelPublisher.publish(model, database, outputDir.resolve(String.format("%s_%s.sql", name, database)));

		if (Utils.hasContent(dataDirs)) {
			Map<String, Entity> entityMap = DatabaseUtils.resolveModel(model);
			Path dataSqlfile = outputDir.resolve(String.format("%s_data.sql", name));
			System.out.println("generateing preset data sql file: " + dataSqlfile.getFileName());
			generatePreset(dataSqlfile, entityMap);
		}
	}

	private void generatePreset(Path file, Map<String, Entity> entityMap) {
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file))) {
			for (Path p : dataDirs) {
				System.out.println("processing preset data under " + p.getFileName());
				Files.list(p).filter(f -> f.getFileName().toString().endsWith(".json")).sorted().forEach(f -> {
					String entityName = Utils.substringBefore(file.getFileName().toString(), ".json");
					System.out.print("processing ");
					System.out.println(entityName);
					Entity entity = Objects.requireNonNull(entityMap.get(entityName));
					try {
						generatePresetFile(writer, f, entity);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println("Done!");
	}

	private void generatePresetFile(PrintWriter writer, Path file, Entity entity) throws IOException {
		List<String> lines = Files.readAllLines(file);
		List<String> values = new ArrayList<String>();
		for (String line : lines) {
			Map<String, Object> map = JSON.parseObject(line);
			NeoBean neo = new NeoBean(entity, map);
			values.clear();
			StringBuilderX sql = new StringBuilderX("insert into ").append(entity.getCode()).append("(");
			for (Column column : entity.getColumns()) {
				Object v = neo.getObject(column);
				if (v != null) {
					sql.append(column.getCode());
					sql.appendTempComma();
					switch (column.getType()) {
					case DOUBLE:
					case INT:
					case NUMERIC:
					case LONG:
					case SMALLINT:
						values.add(v.toString());
						break;
					case BLOB:
						throw new RuntimeException("not support byte");
					default:
						values.add("'" + v.toString() + "'");
						break;
					}
				}
			}
			sql.clearTemp().append(") values(");
			for (String v : values) {
				sql.append(v).appendTempComma();
			}
			sql.clearTemp().append(");");
			writer.println(sql.toString());
		}
	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new DeployWork()).execute(args);
		System.exit(exitCode);
	}

}
