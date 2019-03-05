package rainbow.core.util.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rainbow.core.util.Utils;

/**
 * 封装一个树节点的类
 * 
 * @author lijinghui
 * 
 * @param <T>
 */
public class TreeNode<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private T obj;

    /**
     * 子节点列表
     */
    private List<TreeNode<T>> children;

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode<T>> children) {
        this.children = children;
    }

    /**
     * 空构造函数，为传输用的
     */
    public TreeNode() {
    }

    /**
     * 构造函数
     * 
     * @param obj
     */
    public TreeNode(T obj) {
        this.obj = obj;
    }

    /**
     * 添加一个下级节点
     * 
     * @param child
     */
    public void addChild(TreeNode<T> child) {
        if (children == null)
            children = new ArrayList<TreeNode<T>>();
        children.add(child);
    }

    /**
     * 添加一个下级节点
     * 
     * @param child
     */
    public void addChild(T child) {
        addChild(new TreeNode<T>(child));
    }

    public boolean isLeaf() {
        return Utils.isNullOrEmpty(children);
    }
}
