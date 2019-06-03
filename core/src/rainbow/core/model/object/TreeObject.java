package rainbow.core.model.object;

public class TreeObject extends SimpleObject implements ITreeObject {

	private String pid;
	
	public void setPid(String pid) {
		this.pid = pid;
	}

	@Override
	public String getPid() {
		return pid;
	}

}
