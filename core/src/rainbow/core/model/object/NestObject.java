package rainbow.core.model.object;

/**
 * Nest Model 树形对象
 * 
 * @author lijinghui
 * 
 */
public class NestObject<I> extends TreeObject<I> implements INestObject<I> {

    private int left;

    private int right;

    @Override
    public int getLeft() {
        return left;
    }

    @Override
    public void setLeft(int left) {
        this.left = left;
    }

    @Override
    public int getRight() {
        return right;
    }

    @Override
    public void setRight(int right) {
        this.right = right;
    }

    /**
     * 判断自己是不是另一个对象的子孙
     * 
     * @param other
     * @return
     */
    public boolean isChildOf(NestObject<I> other) {
        return other.left < left && other.right > right;
    }

    /**
     * 判断自己是不是另一个对象的祖先
     * 
     * @param other
     * @return
     */
    public boolean isAncestorOf(NestObject<I> other) {
        return other.left > left && other.right < right;
    }
}
