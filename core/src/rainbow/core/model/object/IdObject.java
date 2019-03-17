package rainbow.core.model.object;

import java.util.Objects;

/**
 * 有Id 属性的对象基类
 * 
 * @author lijinghui
 * 
 */
public abstract class IdObject<I> implements IIdObject<I> {

    protected I id;

    public I getId() {
        return id;
    }

    public void setId(I id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        return (getClass() == obj.getClass()) && Objects.equals(id, ((IdObject<?>) obj).getId());
    }

}
