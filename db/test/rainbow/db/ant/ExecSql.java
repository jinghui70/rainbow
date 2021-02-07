package rainbow.db.ant;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.sql.DataSource;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import rainbow.core.util.Utils;
import rainbow.db.database.DataSourceConfig;
import rainbow.db.database.DatabaseUtils;
import rainbow.db.jdbc.JdbcTemplate;

@Command(name = "execSql")
public class ExecSql implements Runnable {

	@Option(names = { "-h", "--help" }, usageHelp = true, description = "display this help message")
	boolean usageHelpRequested;

	@Parameters(index = "0..*", description = "init data dir")
	List<Path> sqlFiles;

	@Option(names = { "-j", "--jdbc" }, description = "jdbc url")
	String jdbcUrl;

	@Option(names = { "-u", "--username" })
	String username;

	@Option(names = { "-p", "--password" })
	String password;

	@Override
	public void run() {
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setJdbcUrl(jdbcUrl);
		dsc.setUsername(username);
		dsc.setPassword(password);
		DataSource ds = DatabaseUtils.createDataSource(dsc);
		JdbcTemplate j = new JdbcTemplate(ds);
		for (Path file : sqlFiles) {
			System.out.println("executing file: " + file.getFileName());
			String sql;
			try (InputStream is = Files.newInputStream(file)) {
				sql = Utils.streamToString(is);
				j.execute(sql);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new ExecSql()).execute(args);
		System.exit(exitCode);
	}

}
