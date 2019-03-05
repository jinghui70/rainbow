package rainbow.core.model.object;

/**
 * 树形对象接口
 * 
 * @author lijinghui
 * 
 */
public interface ITreeObject<I> extends IIdObject<I> {

    /**
     * 获得上级ID
     * 
     * @return
     */
    public I getPid();

}
