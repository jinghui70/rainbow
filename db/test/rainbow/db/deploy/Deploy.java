package rainbow.db.deploy;

import java.io.IOException;

import javax.xml.transform.TransformerException;

public class Deploy {

	/**
	 * 前三个参数：模型文件、预置数据目录、输出目录，后跟数据库描述
	 * 
	 * @param args
	 * @throws IOException
	 * @throws TransformerException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void main(String[] args)
			throws IOException, InstantiationException, IllegalAccessException, TransformerException {
		if (args.length < 3) {
			System.out.println("params need: [outputDir] [modelfile] [jdbcConfig] [presetDataDir]");
			return;
		}
		Worker worker = new Worker();
		System.out.println("output dir:" + args[0]);
		worker.setOutputDir(args[0]);
		System.out.println("model file:" + args[1]);
		worker.setModelFile(args[1]);
		System.out.println("jdbc config:" + args[2]);
		worker.setJdbcConfig(args[2]);
		if (args.length > 3)
			worker.setPresetDir(args[3]);
		worker.outputRdm();
		worker.doWork();
	}

}
