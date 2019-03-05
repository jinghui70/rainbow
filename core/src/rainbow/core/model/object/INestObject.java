package rainbow.core.model.object;

/**
 * Nest Model 树形对象接口
 * 
 * @author lijinghui
 *
 */
public interface INestObject<I> extends ITreeObject<I> {

	public int getLeft();

	public void setLeft(int left);

	public int getRight();

	public void setRight(int right);
	
}
