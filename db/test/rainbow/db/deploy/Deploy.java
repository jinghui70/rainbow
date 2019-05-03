package rainbow.db.deploy;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import com.alibaba.fastjson.JSON;

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
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, TransformerException {
		if (args.length < 3) {
			System.out.println("params need: [modelfile] [presetDataDir] [outputDir] [...databaseSet]");
			return;
		}
		Worker worker = new Worker();
		worker.setModelFile(args[0]);
		worker.setPresetDir(args[1]);
		worker.setOutputDir(args[2]);
		worker.outputRdm();

		int index = 3;
		while (index <= args.length - 1) {
			String option = args[index++];
			Work work = JSON.parseObject(option, Work.class);
			worker.doWork(work);
		}
	}

}
