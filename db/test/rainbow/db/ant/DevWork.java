package rainbow.db.ant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.sql.DataSource;

import com.google.common.collect.ImmutableMap;
import com.jfinal.template.Template;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import rainbow.core.util.Utils;
import rainbow.core.util.encrypt.Cipher;
import rainbow.core.util.encrypt.DefaultCipher;
import rainbow.core.util.json.JSON;
import rainbow.db.dao.Dao;
import rainbow.db.dao.DaoConfig;
import rainbow.db.dao.DaoImpl;
import rainbow.db.dao.NeoBean;
import rainbow.db.database.DatabaseUtils;
import rainbow.db.model.Model;

@Command(name = "devDB")
public class DevWork implements Runnable {

	@Option(names = { "-h", "--help" }, usageHelp = true, description = "display this help message")
	boolean usageHelpRequested;

	@Parameters(description = "datasource name")
	String daoName;

	@Parameters(index = "1..*", description = "init data dir")
	List<Path> dataDirs;

	@Override
	public void run() {
		Path root = Paths.get("..", "rainbow", "conf", "db");
		Path file = root.resolve(daoName + ".json.dev");
		if (!Files.exists(file))
			file = root.resolve(daoName + ".json");
		DaoConfig config = JSON.parseObject(file, DaoConfig.class);
		Path modelFile = root.resolve(config.getModel());
		Model model = DatabaseUtils.loadModel(modelFile);
		Template template = ModelPublisher.getTemplate(config.getType());
		if (config.isEncrypted()) {
			if (Objects.equals(config.getCipher(), "default")) {
				Cipher cipher = new DefaultCipher();
				config.setUsername(cipher.decode(config.getUsername()));
				config.setPassword(cipher.decode(config.getPassword()));
			} else
				throw new RuntimeException("Development database configuration should not be encrypted");
		}
		DataSource dataSource = DatabaseUtils.createDataSource(config);
		DaoImpl dao = new DaoImpl(dataSource, null, // DDL用不到dialect
				DatabaseUtils.resolveModel(model));
		String ddl = template.renderToString(ImmutableMap.of("model", model, "drop", true));
		dao.execSql(ddl);
		if (Utils.hasContent(dataDirs))
			insertPreset(dao);
	}

	private void insertPreset(Dao dao) {
		for (Path dir : dataDirs) {
			if (Files.notExists(dir))
				continue;
			System.out.println("load preset data under " + dir.getFileName());
			try {
				Files.list(dir).filter(f -> f.getFileName().toString().endsWith(".json")).sorted()
						.forEach(f -> insertPresetFile(dao, f));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			System.out.println("Done!");
		}
	}

	private void insertPresetFile(Dao dao, Path file) {
		String entityName = Utils.substringBefore(file.getFileName().toString(), ".json");
		System.out.print("processing ");
		System.out.println(entityName);
		NeoBean neo = dao.newNeoBean(entityName);
		List<String> lines;
		try {
			lines = Files.readAllLines(file);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		for (String line : lines) {
			if (Utils.hasContent(line) && !line.startsWith("//")) {
				Map<String, Object> map = JSON.parseObject(line);
				neo.init(map);
				dao.insert(neo);
			}
		}
	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new DevWork()).execute(args);
		System.exit(exitCode);
	}

}
