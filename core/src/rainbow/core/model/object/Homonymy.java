package rainbow.core.model.object;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;

/**
 * 对INameObject对象进行同名判断的Predicate
 * 
 * @author lijinghui
 *
 * @param <T>
 */
public class Homonymy<T extends INameObject> implements Predicate<T> {

    private String name;

    public Homonymy(String name) {
        this.name = name;
    }

    @Override
    public boolean apply(T input) {
        return Objects.equal(name, input.getName());
    }

}
