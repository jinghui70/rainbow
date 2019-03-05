package rainbow.core.model.object;

/**
 * 树形对象基类
 * 
 * @author lijinghui
 * 
 */
public class TreeObject<I> extends IdObject<I> implements ITreeObject<I> {

    private I pid;

    @Override
    public I getPid() {
        return pid;
    }

    public void setPid(I pid) {
        this.pid = pid;
    }

}
