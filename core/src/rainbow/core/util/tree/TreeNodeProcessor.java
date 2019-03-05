package rainbow.core.util.tree;


/**
 * 对一个树的节点进行处理的接口
 * 
 * @author kitty
 * 
 * @param <T>
 */
public abstract class TreeNodeProcessor<T> {

    private boolean stop = false;

    /**
     * 是否终止处理
     * 
     * @return
     */
    boolean isStop() {
        return stop;
    }

    /**
     * 停止后续所有的处理
     */
    protected final void stop() {
        stop = true;
    }

    /**
     * 处理一个节点，如果不需要继续处理下级，就返回false。 如果希望中断以后的处理，就调用stop();
     * 
     * @param node
     * @return
     */
    public abstract boolean process(TreeNode<T> node);
    
    /**
     * 处理下级之前的操作
     * 
     * @param node
     */
    public void beforeChildren(TreeNode<T> node){
    }

    /**
     * 处理完下级后的操作
     * 
     * @param node
     */
    public void afterChildren(TreeNode<T> node) {
    }

}
